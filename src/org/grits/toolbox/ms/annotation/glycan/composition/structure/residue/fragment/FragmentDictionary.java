package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.AnomerType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.CoreModificationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class FragmentDictionary {

	private static Map<MonosaccharideType, Map<ICleavageType, List<IFragmentType>>> mapMSTypeToClvTypeToFragments;
	private static Map<String, IFragmentType> mapNameToFragment;
	private static Map<GlycosidicCleavageType, GlycosidicFragmentType> mapGlycosidicClvTypeToFragment;

	static {
		mapMSTypeToClvTypeToFragments = new HashMap<>();
		mapNameToFragment = new HashMap<>();
		mapGlycosidicClvTypeToFragment = new HashMap<>();
		for ( GlycosidicCleavageType clvType : GlycosidicCleavageType.values() ) {
			GlycosidicFragmentType fragType = new GlycosidicFragmentType(clvType);
			mapGlycosidicClvTypeToFragment.put(clvType, fragType);

			mapNameToFragment.put(fragType.getName(), fragType);
			for ( String synonym : fragType.getSynonyms() )
				mapNameToFragment.put(synonym.toLowerCase(), fragType);
		}
	}

	public static void generateAllMonosaccharideFragments(MonosaccharideType msType) {
		if ( mapMSTypeToClvTypeToFragments.containsKey(msType) )
			return;

		mapMSTypeToClvTypeToFragments.put(msType, new HashMap<>());
		// For glycosidic cleavage
		for ( GlycosidicCleavageType clvType : mapGlycosidicClvTypeToFragment.keySet() ) {
			mapMSTypeToClvTypeToFragments.get(msType).put(clvType, new ArrayList<>());

			mapMSTypeToClvTypeToFragments.get(msType).get(clvType)
				.add(mapGlycosidicClvTypeToFragment.get(clvType));
		}

		// For cross ring fragments
		for ( CrossRingCleavageType clvType : CrossRingCleavageType.values() ) {
			int iRingEnd = msType.getRingSize() - 1;
			for ( int iClvStart=0; iClvStart<=iRingEnd-2; iClvStart++ ) {
				for ( int iClvEnd=iClvStart+2; iClvEnd<=iRingEnd; iClvEnd++ ) {
					if ( iClvStart == 0 && iClvEnd == iRingEnd )
						continue;
					if ( !canCleaveCrossRing(msType, iClvStart, iClvEnd) )
						continue;

					if ( !mapMSTypeToClvTypeToFragments.get(msType).containsKey(clvType) )
						mapMSTypeToClvTypeToFragments.get(msType).put(clvType, new ArrayList<>());
					CrossRingFragmentType fragType = new CrossRingFragmentType(msType, clvType, iClvStart, iClvEnd);
					mapMSTypeToClvTypeToFragments.get(msType).get(clvType).add(fragType);

					mapNameToFragment.put(fragType.getName(), fragType);
					for ( String synonym : fragType.getSynonyms() )
						mapNameToFragment.put(synonym.toLowerCase(), fragType);
				}
			}
		}

		return;
	}

	public static IFragmentType findFragmentType(String strType) {
		IFragmentType ret = mapNameToFragment.get(strType);
		return ret;
	}

	public static IFragmentType getFragmentType(String strType) throws DictionaryException {
		IFragmentType ret = findFragmentType(strType);
		if ( ret == null )
			throw new DictionaryException("Invalid fragment type: <"+strType+">");
		return ret;
		
	}

	public static List<IFragmentType> getFragments(MonosaccharideType msType) throws DictionaryException {
		if ( !mapMSTypeToClvTypeToFragments.containsKey(msType) )
			throw new DictionaryException("Invalid monosaccharide type: <"+msType.getName()+">");

		List<IFragmentType> lFragments = new ArrayList<>();
		for ( ICleavageType clvType : mapMSTypeToClvTypeToFragments.get(msType).keySet() ) {
			lFragments.addAll( mapMSTypeToClvTypeToFragments.get(msType).get(clvType) );
		}

		return lFragments;
	}


	public static List<IFragmentType> getFragments(MonosaccharideType msType, ICleavageType clvType) throws DictionaryException {
		if ( !mapMSTypeToClvTypeToFragments.containsKey(msType) )
			throw new DictionaryException("Invalid monosaccharide type: <"+msType.getName()+">");
		if ( !mapMSTypeToClvTypeToFragments.get(msType).containsKey(clvType) )
			throw new DictionaryException("Invalid cleavage type for monosaccharide type: <"+msType.getName()+">");
		return new ArrayList<>( mapMSTypeToClvTypeToFragments.get(msType).get(clvType) );
	}

	private static boolean canCleaveCrossRing(MonosaccharideType msType, int iClvStart, int iClvEnd) {

		// Open chain monosaccharide can not be fragmented as a cross ring fragment
		if ( msType.getAnomer() == AnomerType.o )
			return false;

		// Monosaccharide with two or more rings can not be fragmented as a cross ring fragment
		if ( msType.getCoreModifications(-1).contains(CoreModificationType.ANHYDRO)
		  || msType.getCoreModifications(-1).contains(CoreModificationType.LACTON) )
			return false;

		iClvStart = iClvStart + msType.getAnomericCarbon() - 1;
		iClvEnd = iClvEnd + msType.getAnomericCarbon() - 1;

		// Too short to cleave the monosaccharide
		if ( msType.getCarbonLength() < iClvEnd )
			return false;

		// Double bond can not be cleaved
		if ( (iClvStart > 0 && msType.getCoreModifications(iClvStart).contains(CoreModificationType.DOUBLEBOND) )
		  || (iClvEnd > 0 && msType.getCoreModifications(iClvEnd).contains(CoreModificationType.DOUBLEBOND) ))
			return false;

		return true;
	}


}
