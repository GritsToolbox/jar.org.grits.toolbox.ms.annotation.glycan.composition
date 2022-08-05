package org.grits.toolbox.ms.annotation.glycan.composition.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.gelato.AnalyteMatcher;
import org.grits.toolbox.ms.annotation.gelato.GelatoUtils;
import org.grits.toolbox.ms.annotation.gelato.glycan.GlycanStructureAnnotation;
import org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte.CompositionAnalyte;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.CustomSubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.structure.AnalyteStructure;
import org.grits.toolbox.ms.annotation.structure.GelatoAnalyte;
import org.grits.toolbox.ms.annotation.structure.GelatoAnalyteCache;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.annotation.structure.GlycanStructure;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanScansAnnotation;
import org.grits.toolbox.ms.om.data.IonSettings;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.ReducingEnd;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.ms.om.data.ScansAnnotation;
import org.grits.toolbox.ms.om.io.xml.AnnotationReader;
import org.grits.toolbox.ms.om.io.xml.AnnotationWriter;

public class CompositionStructureAnnotation extends GlycanStructureAnnotation {
	private static final Logger logger = Logger.getLogger(CompositionStructureAnnotation.class);

	@Override
	protected AnalyteMatcher getNewAnalyteMatcher(int iCurScan) {
		return new CompositionAnalyteMatcher(iCurScan, this);
	}

	@Override
	protected GelatoAnalyte getNewGelatoAnalyteObject(AnalyteStructure structure) {
		GelatoAnalyte gelatoComposition = null;
		if( GelatoAnalyteCache.hmGelatoAnalytesByStructureId.containsKey(structure.getId()) ) {
			return null;
		} else {
			gelatoComposition = new GelatoAnalyte();
		}
		try {
			String strComposition = structure.getSequence();
			Composition composition = CompositionUtils.parse(strComposition);
			CompositionAnalyte compositionAnalyte = new CompositionAnalyte(structure.getSequence(), composition);
			gelatoComposition.setAnalyte(compositionAnalyte);
			gelatoComposition.setAnalyteStructure(structure);
		} catch (DictionaryException e) {
			logger.error(e);
		}
		//TODO
		return gelatoComposition;
	}

	private double dMassThreshold = Double.MAX_VALUE;

	@Override
	protected void initializeStructures() {
		caluclateMassThreshold(getData().getDataHeader().getMethod());

		super.initializeStructures();
	}

	private void caluclateMassThreshold(Method method) {
		// Get maximum m/z value from scan data
		Double dCutoffVal = method.getIntensityCutoff();
		String sCutoffType = method.getIntensityCutoffType();
		Double dPrecursorCutoffVal = method.getPrecursorIntensityCutoff();
		String sPrecursoCutoffType = method.getPrecursorIntensityCutoffType();

		double dHighestMZ = 0;
		for ( int iCurScanNum : scansToProcess ) {
			HashMap<Integer, Scan> mapIDToScans = dataManager.getScans(iCurScanNum, dCutoffVal, sCutoffType, dPrecursorCutoffVal, sPrecursoCutoffType);
			for ( Scan scan : mapIDToScans.values() ) {
				if ( dHighestMZ < scan.getScanEnd() )
					dHighestMZ = scan.getScanEnd();
			}
		}

		// Calculate maximum charge
		int iMaxCharge=1;
		for ( int iPor=0; iPor<2; iPor++ ) {
			boolean bPolarity = (iPor==0);
			List<List<IonSettings>> lSettingsToAnalyze = new ArrayList<>();
			List<List<Integer>> lSettingsToAnalyzeCounts = new ArrayList<>();
			GelatoUtils.determineIonSettingSets(bPolarity, method.getIons(), method.getMaxIonCount(), lSettingsToAnalyze, lSettingsToAnalyzeCounts);

			if ( lSettingsToAnalyze.isEmpty() )
				continue;

			int iNumAdducts = lSettingsToAnalyze.size();
			for( int i=0; i<iNumAdducts; i++ ) {
				List<IonSettings> lAdducts = null;
				List<Integer> lAdductCounts = null;
				if( lSettingsToAnalyzeCounts.isEmpty() )
					continue;

				lAdducts = lSettingsToAnalyze.get(i);
				lAdductCounts = lSettingsToAnalyzeCounts.get(i);
				if( lAdducts == null || lAdducts.isEmpty() || lAdductCounts == null || lAdductCounts.isEmpty() )
					continue;

				// Count charge
				for ( int j=0; j<lAdducts.size(); j++ ) {
					int iCharge = lAdducts.get(j).getCharge()*lAdductCounts.get(j);
					if ( iMaxCharge < iCharge )
						iMaxCharge = iCharge;
				}
			}
		}

		dMassThreshold = dHighestMZ * iMaxCharge;
	}

	@Override
	protected List<AnalyteStructure> loadAnalyteSettingsFromDB(AnalyteSettings analyteSettings) {
		String strPerDeriv = analyteSettings.getGlycanSettings().getPerDerivatisationType();
		PerderivatizationType perDeriv = PerderivatizationType.forName(strPerDeriv);
		boolean isMono = getData().getDataHeader().getMethod().getMonoisotopic();
		SubstituentType redEnd = this.convertReducingEnd(analyteSettings.getGlycanSettings().getReducingEnd());

		// Load max and min compositions
		// In this part, the name of database is use as the composition name(s).
		// I don't want to change anything in existing object models!
		String strDB = analyteSettings.getGlycanSettings().getFilter().getDatabase();
		String strMinComposition = null;
		String strMaxComposition = strDB;
		if ( strDB.contains("~|~") ) {
			String[] parsed = strDB.split("~|~");
			strMinComposition = parsed[0];
			strMaxComposition = parsed[1];
		}
		try {
			// Generate compositions
			Composition compMin = null;
			if ( strMinComposition != null ) {
				compMin = CompositionUtils.parse(strMinComposition);
				compMin.setReducingEnd(redEnd);
				compMin.setMassOptions(isMono, perDeriv);
			}
			Composition compMax = CompositionUtils.parse(strMaxComposition);
			compMax.setReducingEnd(redEnd);
			compMax.setMassOptions(isMono, perDeriv);

			List<AnalyteStructure> lAnalyteStructures = new ArrayList<>();
			List<Composition> lComps = CompositionUtils.generateCompositions(compMin, compMax, dMassThreshold);
			for ( Composition comp : lComps ) {
				AnalyteStructure as = new AnalyteStructure();
				String strComp = comp.toString();
				as.setId(String.valueOf(strComp.hashCode()));
				as.setSequence(strComp);
				as.setSequenceFormat("Composition");
				lAnalyteStructures.add(as);
			}
			return lAnalyteStructures;
		} catch (DictionaryException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	protected boolean passesFilters(AnalyteStructure analyteStructure, AnalyteSettings analyteSettings) {
		// no filter
		return true;
	}

	@Override
	protected boolean processStructure( AnalyteSettings analyteSettings, GelatoAnalyte gelatoAnalyte) {
		Annotation annotation = null;
		ScansAnnotation glycanScansAnnotation = null;
		//		double plainMass = 0.0;
		try {
			Method method = getData().getDataHeader().getMethod();
			GlycanStructure glycanStructure = (GlycanStructure) gelatoAnalyte.getAnalyteStructure();

			// Get new GlycanAnnotation without GWS sequence
			annotation = getNewGlycanAnnotation(
					glycanStructure.getId(),
					glycanStructure.getSequence(),
					glycanStructure.getSequenceFormat(),
					null, // no GWB sequence at here
//					glycanStructure.getGWBSequence().substring(0,glycanStructure.getGWBSequence().indexOf("$")),
					analyteSettings.getGlycanSettings().getPerDerivatisationType(),
					analyteSettings.getGlycanSettings().getReducingEnd());

			glycanScansAnnotation = new GlycanScansAnnotation();
			glycanScansAnnotation.setAnnotationId(GlycanStructureAnnotation.iAnnotationIDCount);
			glycanScansAnnotation.setStringAnnotationId(glycanStructure.getId());
			annotation.setStringId(glycanStructure.getId());
//			analyteIDs.add(glycanStructure.getId());
			annotation.setId(GlycanStructureAnnotation.iAnnotationIDCount);
			GlycanStructureAnnotation.iAnnotationIDCount++;

			// Update composition with settings
			Composition composition = ((CompositionAnalyte)gelatoAnalyte.getAnalyte()).getComposition();
			// Set mass options
			PerderivatizationType perDeriv = PerderivatizationType.forName(analyteSettings.getGlycanSettings().getPerDerivatisationType());
			composition.setMassOptions(method.getMonoisotopic(), perDeriv);
			// populate reducing end
			SubstituentType redEnd = convertReducingEnd(analyteSettings.getGlycanSettings().getReducingEnd());
			// Set reducing end
			composition.setReducingEnd(redEnd);
			// Update string representation
			gelatoAnalyte.getAnalyte().setAnalyteStringRepresentation(composition.toString());

			boolean bRes = processAllStructureOptions(
					this.lPosModeSettingsToAnalyze, this.lPosModeSettingsToAnalyzeCounts,
					this.lNegModeSettingsToAnalyze, this.lNegModeSettingsToAnalyzeCounts, 
					this.lExchangesoAnalyze, this.lExchangesToAnalyzeCounts, 
					this.lNeutralLossesToAnalyze, this.lNeutralLossesToAnalyzeCounts, 
					glycanScansAnnotation,
					analyteSettings, gelatoAnalyte, 
					annotation);

			if( bRes ) {
				//				if(currentFeatureIndex != data.getFeatureIndex()) {//means there is new annotations added using the given glycan structure
				//					addAnnotation(annotation);
				//				}
				if( ! glycanScansAnnotation.getScanAnnotations().keySet().isEmpty() ) {
					AnnotationWriter writer = new AnnotationWriter();
					writer.writeAnnotationsPerAnalyte(glycanScansAnnotation,this.m_tempFilePath);
				}
				return true;
			}

		} catch( Exception e ) {
			logger.error("Error in processScans", e);
		}
		return false;
	}

	@Override
	protected ScansAnnotation readScansAnnotation(AnnotationReader arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	private SubstituentType convertReducingEnd(ReducingEnd redEndOM) {
		SubstituentType redEnd = null;
		if( !redEndOM.getType().equals(GlycanPreDefinedOptions.OTHER) )
			redEnd = ResidueDictionary.findReducingEndType(redEndOM.getType());
		if ( redEnd == null ) {
			redEnd = new CustomSubstituentType(redEndOM.getLabel(), redEndOM.getMass(), true);
			// Reset reducing end type to other if not
			if( !redEndOM.getType().equals(GlycanPreDefinedOptions.OTHER) ) {
				logger.warn("Unable to look-up reducing end \"" + redEndOM.getLabel() + "\"");
				redEndOM.setType(GlycanPreDefinedOptions.OTHER);
			}
		}
		return redEnd;
	}
}
