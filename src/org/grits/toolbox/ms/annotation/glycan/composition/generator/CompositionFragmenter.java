package org.grits.toolbox.ms.annotation.glycan.composition.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionFragment;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.CrossRingCleavageType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.FragmentDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.GlycosidicCleavageType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.IFragmentType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class CompositionFragmenter {

	private static final Map<String, List<String>> mapCompositionToFragments;
	private static final Map<String, List<String>> mapCompositionToSubstructures;

	static {
		mapCompositionToFragments = new HashMap<>();
		mapCompositionToSubstructures = new HashMap<>();
	}

	private boolean m_bAFragments;
	private boolean m_bBFragments;
	private boolean m_bCFragments;
	private boolean m_bXFragments;
	private boolean m_bYFragments;
	private boolean m_bZFragments;

	private int m_nMaxCleavages;
	private int m_nMaxCrossRingCleavages;

	public CompositionFragmenter() {

		this.m_bAFragments = false;
		this.m_bBFragments = false;
		this.m_bCFragments = false;
		this.m_bXFragments = false;
		this.m_bYFragments = false;
		this.m_bZFragments = false;

		this.m_nMaxCleavages = -1;
		this.m_nMaxCrossRingCleavages = -1;
	}

	/**
	 * Returns the flag for A-type fragments.
	 * 
	 * @return {@code true} if A-type fragments are computed
	 */
	public boolean computeAFragments() {
		return this.m_bAFragments;
	}

	/**
	 * Set the flag for A-type fragments.
	 * 
	 * @param bAFragments The flag for A-type fragments
	 */
	public void setAFragments(boolean bAFragments) {
		this.m_bAFragments = bAFragments;
	}

	/**
	 * Returns the flag for B-type fragments.
	 * 
	 * @return {@code true} if B-type fragments are computed
	 */
	public boolean computeBFragments() {
		return this.m_bBFragments;
	}

	/**
	 * Set the flag for B-type fragments.
	 * 
	 * @param bBFragments The flag for B-type fragments
	 */
	public void setBFragments(boolean bBFragments) {
		this.m_bBFragments = bBFragments;
	}

	/**
	 * Returns the flag for C-type fragments.
	 * 
	 * @return {@code true} if C-type fragments are computed
	 */
	public boolean computeCFragments() {
		return this.m_bCFragments;
	}

	/**
	 * Set the flag for C-type fragments.
	 * 
	 * @param bCFragments The flag for C-type fragments
	 */
	public void setCFragments(boolean bCFragments) {
		this.m_bCFragments = bCFragments;
	}

	/**
	 * Returns the flag for X-type fragments.
	 * 
	 * @return {@code true} if X-type fragments are computed
	 */
	public boolean computeXFragments() {
		return this.m_bXFragments;
	}

	/**
	 * Set the flag for X-type fragments.
	 * 
	 * @param bXFragments The flag for X-type fragments
	 */
	public void setXFragments(boolean bXFragments) {
		this.m_bXFragments = bXFragments;
	}

	/**
	 * Returns the flag for Y-type fragments.
	 * 
	 * @return {@code true} if Y-type fragments are computed
	 */
	public boolean computeYFragments() {
		return this.m_bYFragments;
	}

	/**
	 * Set the flag for Y-type fragments.
	 * 
	 * @param bYFragments The flag for Y-type fragments
	 */
	public void setYFragments(boolean bYFragments) {
		this.m_bYFragments = bYFragments;
	}

	/**
	 * Returns the flag for Z-type fragments.
	 * 
	 * @return {@code true} if Z-type fragments are computed
	 */
	public boolean computeZFragments() {
		return this.m_bZFragments;
	}

	/**
	 * Set the flag for Z-type fragments.
	 * 
	 * @param bZFragments The flag for Z-type fragments
	 */
	public void setZFragments(boolean bZFragments) {
		this.m_bZFragments = bZFragments;
	}

	/**
	 * Set the flag for all fragments.
	 * @param bFragments The flag for all fragments
	 */
	public void setAllFragments(boolean bFragments) {
		this.m_bAFragments = bFragments;
		this.m_bBFragments = bFragments;
		this.m_bCFragments = bFragments;
		this.m_bXFragments = bFragments;
		this.m_bYFragments = bFragments;
		this.m_bZFragments = bFragments;
	}

	/**
	 * Returns the maximum number of cleavages on glycosidic linkages (B,C,Y,Z).
	 * 
	 * @return The maximum number of cleavages on glycosidic linkages
	 */
	public int getMaxCleavages() {
		return this.m_nMaxCleavages;
	}

	/**
	 * Set the maximum number of cleavages on glycosidic linkages (B,C,Y,Z).
	 * 
	 * @param nMaxCleavages The maximum number of cleavages on glycosidic linkages
	 */
	public void setMaxCleavages(int nMaxCleavages) {
		this.m_nMaxCleavages = nMaxCleavages;
	}

	/**
	 * Returns the maximum number of cross-ring cleavages (A,X)
	 * 
	 * @return The maximum number of cross-ring cleavages
	 */
	public int getMaxCrossRingCleavages() {
		return this.m_nMaxCrossRingCleavages;
	}

	/**
	 * Set the maximum number of cross-ring cleavages (A,X)
	 * 
	 * @param nMaxCrossRingCleavages The maximum number of cross-ring cleavages
	 */
	public void setMaxCrossRingCleavages(int nMaxCrossRingCleavages) {
		this.m_nMaxCrossRingCleavages = nMaxCrossRingCleavages;
	}

	public String getCurrentFragmentOptions() {
		String strOption = "";
		if ( this.m_bAFragments )
			strOption += 'A';
		if ( this.m_bBFragments )
			strOption += 'B';
		if ( this.m_bCFragments )
			strOption += 'C';
		if ( this.m_bXFragments )
			strOption += 'X';
		if ( this.m_bYFragments )
			strOption += 'Y';
		if ( this.m_bZFragments )
			strOption += 'Z';
		strOption += ( this.m_nMaxCleavages < 0 )? '?' : this.m_nMaxCleavages;
		strOption += ( this.m_nMaxCrossRingCleavages < 0 )? '?' : this.m_nMaxCrossRingCleavages;
		return strOption;
	}

	/**
	 * Computes all possible fragments from the given composition.
	 * 
	 * @param composition a Composition
	 * @return List of String format of all possible composition fragments
	 * @throws DictionaryException
	 */
	public List<String> computeFragments(Composition composition) throws DictionaryException {
		String strKey = getCurrentFragmentOptions();

		String strComp = composition.toString();
		if ( mapCompositionToFragments.containsKey(strComp+strKey) )
			return mapCompositionToFragments.get(strComp+strKey);

		List<Composition> lSubstructures = new ArrayList<>();

		// Consider substructures
		Set<String> lIsSubsumed = new HashSet<>();
		mapCompositionToSubstructures.put(strComp+strKey, new ArrayList<>());
		for ( Composition sub : CompositionUtils.generateSubstructures(composition) ) {
			mapCompositionToSubstructures.get(strComp+strKey).add(sub.toString()+strKey);
			lSubstructures.add(sub);
			if ( mapCompositionToSubstructures.containsKey(sub.toString()+strKey) )
				lIsSubsumed.addAll(mapCompositionToSubstructures.get(sub.toString()+strKey));
		}
		// Add itself
		lSubstructures.add(composition);
		// Sort substructures
		CompositionUtils.sortCompositionsByMass(lSubstructures, true);

		int nDuplication1 = 0;
		int nDuplication2 = 0;

		List<String> lFragments = new ArrayList<>();
		for (Composition comp : lSubstructures) {
			String strSubKey = comp.toString()+strKey;
			if ( lIsSubsumed.contains(strSubKey) )
				continue;
			List<String> lSubFragments = new ArrayList<>();
			if ( mapCompositionToFragments.containsKey(strSubKey) ) {
				lSubFragments = mapCompositionToFragments.get(strSubKey);
			} else {
				// Compute fragments for substructures
				nDuplication1 += computeFragments0(lSubFragments, comp);
				mapCompositionToFragments.put(strSubKey, lSubFragments);
			}

			for ( String strFragment : lSubFragments )
				if ( !lFragments.contains(strFragment) ) {
					lFragments.add(strFragment);
				} else
					nDuplication2++;
		}
//		System.out.println( String.format("Duplication: %d,%d", nDuplication1, nDuplication2) );

		mapCompositionToFragments.put(strComp+strKey, lFragments);
		return lFragments;
	}

	private int computeFragments0(List<String> lFragments, Composition composition) throws DictionaryException {
		if ( composition.getNumberOfMonosaccharides() == 1 && composition.getReducingEnd() == null)
			return 0;

		int nDup = 0;
		for (ResidueType res : composition.getResidueTypes()) {
			// Cleaves only monosaccharides
			if (!(res instanceof MonosaccharideType))
				continue;
			MonosaccharideType msType = (MonosaccharideType) res;
			int nRes = composition.getNumberOfResidue(res);

			List<String> lFragmentsCurrent = lFragments;
			if ( lFragmentsCurrent.isEmpty() )
				lFragmentsCurrent.add(composition.toString());
			for ( int i=0; i<nRes; i++ ) {
				
				List<String> lFragmentsNext = new ArrayList<>();
				for ( String strFrag : lFragmentsCurrent ) {
					Composition terget = CompositionUtils.parse( strFrag );

					for (IFragmentType fragType : this.getFragments(msType)) {
						// Generate fragments
						CompositionFragment frag = new CompositionFragment(terget);
						if ( fragType.getCleavageType().isRootSide() && frag.getRootFragment() != null )
							continue;
						// Remove current residue
						frag.addResidues(res, -1);
						// Add a fragment instead of removed residue
						if (!frag.addFragment(fragType))
							continue;
						if (!frag.isValidStructure())
							continue;
						if ( lFragmentsNext.contains(frag.toString()) ) {
							nDup++;
							continue;
						}
						if (this.m_nMaxCrossRingCleavages >= 0
								&& frag.getNumberOfCrossRingFragments() > this.m_nMaxCrossRingCleavages)
							continue;
						if (this.m_nMaxCleavages >= 0
								&& frag.getNumberOfNonCrossRingFragments() > this.m_nMaxCleavages)
							continue;
						lFragmentsNext.add(frag.toString());
					}
				}

//				System.out.println(String.format("Substructures(%s:%d): %d",res, i+1, lFragmentsNext.size()));
				lFragmentsCurrent = lFragmentsNext;
				lFragments.addAll(lFragmentsCurrent);
			}
		}
		lFragments.remove(composition.toString());

		return nDup;
	}

/*
	private int computeFragments(List<String> lFragments, Composition composition) throws DictionaryException {
		if (lFragments.contains(composition.toString()))
			return 1;

		// Filter out fragments exceeding the max number of cleavages
		if (composition instanceof CompositionFragment) {
			CompositionFragment fragment = (CompositionFragment) composition;
			if (this.m_nMaxCrossRingCleavages >= 0
					&& fragment.getNumberOfCrossRingFragments() > this.m_nMaxCrossRingCleavages)
				return 0;
			if (this.m_nMaxCleavages >= 0
					&& fragment.getNumberOfNonCrossRingFragments() > this.m_nMaxCleavages)
				return 0;
			// Add fragment
			lFragments.add(composition.toString());
		}

		int nDuplication = 0;
		for (ResidueType res : composition.getResidueTypes()) {
			// Cleaves only monosaccharides
			if (!(res instanceof MonosaccharideType))
				continue;
			MonosaccharideType msType = (MonosaccharideType) res;

			for (IFragmentType fragType : this.getFragments(msType)) {
				// Generate fragments
				CompositionFragment frag = new CompositionFragment(composition);
				if ( fragType.getCleavageType().isRootSide() && frag.getRootFragment() != null )
					continue;
				// Remove current residue
				frag.addResidues(res, -1);
				// Add a fragment instead of removed residue
				if (!frag.addFragment(fragType))
					continue;
				if (!frag.isValidStructure())
					continue;
				// If already contains the fragment, all the other fragments must be contained
				if (lFragments.contains(frag.toString())) {
					System.out.println(lFragments.size()+": "+composition.toString()+" -> "+frag.toString());
//					nDuplication++;
//					break;
				}
				nDuplication += computeFragments(lFragments, frag);
			}

		}
		return nDuplication;
	}
*/
	private static Map<String, List<IFragmentType>> mapMSToFragments = new HashMap<>();

	private List<IFragmentType> getFragments(MonosaccharideType msType) throws DictionaryException {
		String strKey = msType.toString()+getCurrentFragmentOptions();
		if ( mapMSToFragments.containsKey(strKey) )
			return mapMSToFragments.get(strKey);

		// Generate fragments for the monosaccharide type
		FragmentDictionary.generateAllMonosaccharideFragments(msType);
		List<IFragmentType> lFragments = new ArrayList<>();
		if ( this.m_bAFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, CrossRingCleavageType.A) );
		if ( this.m_bBFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, GlycosidicCleavageType.B) );
		if ( this.m_bCFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, GlycosidicCleavageType.C) );
		if ( this.m_bXFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, CrossRingCleavageType.X) );
		if ( this.m_bYFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, GlycosidicCleavageType.Y) );
		if ( this.m_bZFragments )
			lFragments.addAll( FragmentDictionary.getFragments(msType, GlycosidicCleavageType.Z) );
		mapMSToFragments.put(strKey, lFragments);
		return lFragments;
	}
}
