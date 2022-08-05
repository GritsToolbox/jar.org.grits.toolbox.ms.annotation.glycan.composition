package org.grits.toolbox.ms.annotation.glycan.composition.structure;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.MoleculeUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.CrossRingFragmentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.IFragmentType;

/**
 * Class for monosaccharide composition containing its fragment.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class CompositionFragment extends Composition {

	private Map<IFragmentType, Integer> m_mapLeafFragmentToCount;
	private IFragmentType m_fragmentRoot;

	public CompositionFragment() {
		super();
		this.m_mapLeafFragmentToCount = new TreeMap<>();
		this.m_fragmentRoot = null;
	}

	public CompositionFragment(Composition comp) {
		super(comp);
		this.m_mapLeafFragmentToCount = new TreeMap<>();
		this.m_fragmentRoot = null;
		if (!(comp instanceof CompositionFragment))
			return;
		CompositionFragment compFrag = (CompositionFragment) comp;
		for (IFragmentType frag : compFrag.m_mapLeafFragmentToCount.keySet())
			this.m_mapLeafFragmentToCount.put(frag, compFrag.m_mapLeafFragmentToCount.get(frag));
		this.m_fragmentRoot = compFrag.m_fragmentRoot;
	}

	@Override
	public boolean isEmpty() {
		if ( !super.isEmpty() )
			return false;
		if ( !this.m_mapLeafFragmentToCount.isEmpty() )
			return false;
		if ( this.m_fragmentRoot != null )
			return false;
		return true;
	}

	public boolean addFragment(IFragmentType type) {
		if (setRootFragment(type))
			return true;
		return addLeafFragment(type);
	}

	/**
	 * Sets the given fragment type as a root side fragment.
	 * 
	 * @param type Fragment type, {@null} for no root side fragment
	 * @return {@code false} if the given FragmentType is not root side fragment
	 */
	private boolean setRootFragment(IFragmentType type) {
		if (type != null && !type.getCleavageType().isRootSide())
			return false;
		this.m_fragmentRoot = type;
		// Reducing end and root fragment can not be set at the same time
		if (type != null && this.m_redEndType != null)
			this.m_redEndType = null;
		return true;
	}

	/**
	 * Returns root side fragment of this structure.
	 */
	public IFragmentType getRootFragment() {
		return this.m_fragmentRoot;
	}

	@Override
	public boolean setReducingEnd(SubstituentType redEnd) {
		if (!super.setReducingEnd(redEnd))
			return false;
		// Reducing end and root fragment can not be set at the same time
		if (redEnd != null && this.m_fragmentRoot != null)
			this.m_fragmentRoot = null;
		return true;
	}

	/**
	 * Adds the given fragment type with the given count number as leaf side
	 * fragments. The number can be negative value but total count number after the
	 * addition must be positive value. The given residue type is removed from this
	 * composition if the total count number is zero.
	 * 
	 * @param type  FragmentType
	 * @param count The count number of the res
	 * @return {@code false} if total count number becomes negative value, otherwise
	 *         {@true}
	 */
	public boolean addLeafFragments(IFragmentType type, int count) {
		// A fragment for root side can not be added
		if (type.getCleavageType().isRootSide())
			return false;
		if (!this.m_mapLeafFragmentToCount.containsKey(type))
			this.m_mapLeafFragmentToCount.put(type, 0);
		int n = this.m_mapLeafFragmentToCount.get(type) + count;
		// The count number must be positive value
		if (n < 0)
			return false;
		// Removes the residue if the count number is zero
		if (n == 0) {
			this.m_mapLeafFragmentToCount.remove(type);
			return true;
		}
		this.m_mapLeafFragmentToCount.put(type, n);
		return true;
	}

	/**
	 * Adds the given fragment type as a leaf side fragment.
	 * 
	 * @param type FragmentType
	 * @return {@code true} if the addition is succeeded.
	 * @see #addLeafFragments(CrossRingFragmentType, int)
	 */
	private boolean addLeafFragment(IFragmentType type) {
		return addLeafFragments(type, 1);
	}

	/**
	 * Returns set of containing fragment types.
	 */
	public Set<IFragmentType> getFragmentTypes() {
		Set<IFragmentType> lRess = new TreeSet<>();
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet())
			lRess.add(type);
		if (this.m_fragmentRoot != null)
			lRess.add(this.m_fragmentRoot);
		return lRess;
	}

	public int getNumberOfAllFragments() {
		int num = 0;
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet())
			num += this.m_mapLeafFragmentToCount.get(type);
		if (this.m_fragmentRoot != null)
			num++;
		return num;
	}

	public int getNumberOfCrossRingFragments() {
		int num = 0;
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet())
			if (type.getCleavageType().isCrossRing())
				num += this.m_mapLeafFragmentToCount.get(type);
		if (this.m_fragmentRoot != null && this.m_fragmentRoot.getCleavageType().isCrossRing())
			num++;
		return num;
	}

	public int getNumberOfNonCrossRingFragments() {
		int num = 0;
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet())
			if (!type.getCleavageType().isCrossRing())
				num += this.m_mapLeafFragmentToCount.get(type);
		if (this.m_fragmentRoot != null && !this.m_fragmentRoot.getCleavageType().isCrossRing())
			num++;
		return num;
	}

	@Override
	public int getMaxLinkages() {
		int nLinks = super.getMaxLinkages();
		int nRess = (nLinks > 0) ? 1 : 0;
		for (IFragmentType frag : this.m_mapLeafFragmentToCount.keySet()) {
			int n = this.m_mapLeafFragmentToCount.get(frag);
			nRess += n;
			nLinks += n * frag.getMaxLinkages();
		}
		if (this.m_fragmentRoot != null) {
			nRess++;
			nLinks += this.m_fragmentRoot.getMaxLinkages();
		}
		if (nRess > 1)
			nLinks -= (nRess - 1) * 2;
		return nLinks;
	}

	@Override
	public double computeMass() {
		double dMass = super.computeMass();

		int nResidues = 0;
		// Add fragment masses
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet()) {
			int n = this.m_mapLeafFragmentToCount.get(type);
			dMass += n * ((this.m_bIsMonoisotopic) ? type.getMonoisotopicMass() : type.getAverageMass());
			nResidues += n;
		}
		if (this.m_fragmentRoot != null) {
			dMass += (this.m_bIsMonoisotopic) ? this.m_fragmentRoot.getMonoisotopicMass()
					: this.m_fragmentRoot.getAverageMass();
			nResidues++;
		}

		if (!this.m_mapResidueToCount.isEmpty() || this.m_redEndType != null)
			nResidues++;
		// Reduce water masses for glycosidic linkages
		if (nResidues > 1)
			dMass -= (nResidues - 1) * ((this.m_bIsMonoisotopic) ? MoleculeUtils.water.getMonoisotopicMass()
					: MoleculeUtils.water.getAverageMass());

		//
		if (this.m_perderivType != null) {
			int nPerDeriv = 0;
			for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet()) {
				int n = this.m_mapLeafFragmentToCount.get(type);
				nPerDeriv += n * ((this.m_perderivType.isMethylation()) ? type.getNumMethylations()
						: (this.m_perderivType.isAcetylation()) ? type.getNumAcethylations() : 0);
			}
			if (this.m_fragmentRoot != null) {
				nPerDeriv += (this.m_perderivType.isMethylation()) ? this.m_fragmentRoot.getNumMethylations()
						: (this.m_perderivType.isAcetylation()) ? this.m_fragmentRoot.getNumAcethylations() : 0;
			}
			if (nResidues > 1)
				nPerDeriv -= (nResidues - 1) * 2;

			// Add perderivationzation masses and reduce water masses
			dMass += nPerDeriv * ((this.m_bIsMonoisotopic)
					? this.m_perderivType.getMonoisotopicMass() - MoleculeUtils.water.getMonoisotopicMass()
					: this.m_perderivType.getAverageMass() - MoleculeUtils.water.getAverageMass());
		}

		return dMass;
	}

	@Override
	public CompositionFragment copy() {
		return new CompositionFragment(this);
	}

	@Override
	public boolean isValidStructure() {
		if (this.getNumberOfMonosaccharides() == 0) {
			if (this.m_redEndType == null && this.m_fragmentRoot == null)
				return false;
			if (this.m_mapLeafFragmentToCount.isEmpty())
				return false;
			// Check number of linkages on fragments
			int nRootLinkages = 0;
			if (this.m_redEndType != null)
				nRootLinkages += this.m_redEndType.getMaxLinkages();
			if (this.m_fragmentRoot != null)
				nRootLinkages += this.m_fragmentRoot.getMaxLinkages();
			int nLeafFragments = 0;
			int nLeafLinkages = 0;
			for (IFragmentType frag : this.m_mapLeafFragmentToCount.keySet()) {
				int n = this.m_mapLeafFragmentToCount.get(frag);
				nLeafFragments += n;
				nLeafLinkages += n * frag.getMaxLinkages();
			}
			nLeafLinkages -= (nLeafFragments - 1) * 2;
			if (nLeafLinkages < 1 && 2 - nLeafLinkages > nRootLinkages)
				return false;
			if (this.getNumberOfCrossRingFragments() == 0)
				return false;
		}
		int nLinks = this.getMaxLinkages();
		if (nLinks < 0)
			return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CompositionFragment))
			return false;
		CompositionFragment frag = (CompositionFragment) obj;
		if (this.m_fragmentRoot != frag.m_fragmentRoot)
			return false;
		if (this.m_mapLeafFragmentToCount.size() != frag.m_mapLeafFragmentToCount.size())
			return false;
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet()) {
			if (!frag.m_mapLeafFragmentToCount.containsKey(type))
				return false;
			if (frag.m_mapLeafFragmentToCount.get(type) != this.m_mapLeafFragmentToCount.get(type))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String str = super.toString();

		StringBuffer sb = new StringBuffer();
		// Add leaf fragment part
		for (IFragmentType type : this.m_mapLeafFragmentToCount.keySet()) {
			if (sb.length() != 0)
				sb.append(",");
			int count = this.m_mapLeafFragmentToCount.get(type);
			sb.append(type.toString());
			sb.append(":");
			sb.append(count);
		}
		if ( sb.length() != 0 && !str.isEmpty() )
			sb.append("--");
		sb.append(str);

		// Add root fragment part
		if (this.m_fragmentRoot != null) {
			sb.append("--");
			sb.append(this.m_fragmentRoot.toString());
		}
		return sb.toString();
	}

	/**
	 * Returns String representation of fragment type.
	 * @return String representation of fragment type
	 */
	public String getFragmentType() {
		String strFragmentType = "";
		for ( IFragmentType type : getFragmentTypes() ) {
			String symbol = ""+Character.toUpperCase(type.getCleavageType().getSymbol());
			if ( type.getCleavageType().isCrossRing() ) {
				CrossRingFragmentType ringType = (CrossRingFragmentType)type;
				symbol += "_"+ringType.getStartPositionOfCrossRingCleavage()
						+"_"+ringType.getEndPositionOfCrossRingCleavage()
						+"_{"+ringType.getCleavedMonosaccharideType().getName()+"}";
			}
			if ( this.m_mapLeafFragmentToCount.containsKey(type) ) {
				int n = this.m_mapLeafFragmentToCount.get(type);
				for ( int i=0; i<n; i++ )
					strFragmentType += symbol;
			} else if ( this.m_fragmentRoot == type ) {
				strFragmentType += symbol;
			}
		}
		return strFragmentType;
	}
}
