package org.grits.toolbox.ms.annotation.glycan.composition.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.MoleculeUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;

/**
 * Class for the monosaccharide composition containing residues and reducing
 * end. Each residue is stored with its number.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class Composition {

	protected Map<ResidueType, Integer> m_mapResidueToCount;
	protected SubstituentType m_redEndType;

	protected boolean m_bIsMonoisotopic;
	protected PerderivatizationType m_perderivType;

	public Composition() {
		this.m_mapResidueToCount = new TreeMap<>();
		this.m_redEndType = null;
		this.m_bIsMonoisotopic = true;
		this.m_perderivType = null;
	}

	public Composition(Composition comp) {
		this();
		for (ResidueType res : comp.m_mapResidueToCount.keySet())
			this.m_mapResidueToCount.put(res, comp.m_mapResidueToCount.get(res));
		this.m_redEndType = comp.m_redEndType;
		this.m_bIsMonoisotopic = comp.m_bIsMonoisotopic;
		this.m_perderivType = comp.m_perderivType;
	}

	public boolean isEmpty() {
		if ( !this.m_mapResidueToCount.isEmpty() )
			return false;
		if ( this.m_redEndType != null )
			return false;
		return true;
	}

	/**
	 * Sets the given substituent type as a reducing end.
	 * 
	 * @param redEnd Substituent type, {@null} for free end
	 * @return {@code false} if the given SubstituentType is not reducing end
	 */
	public boolean setReducingEnd(SubstituentType redEnd) {
		if (redEnd != null && !redEnd.isReducingEnd())
			return false;
		this.m_redEndType = redEnd;
		return true;
	}

	/**
	 * Returns reducing end of this structure
	 */
	public SubstituentType getReducingEnd() {
		return this.m_redEndType;
	}

	/**
	 * Adds the given residue type with the given count number. The number can be
	 * negative value but total count number after the addition must be positive
	 * value. The given residue type is removed from this composition if the total
	 * count number is zero.
	 * 
	 * @param res   ResidueType
	 * @param count The count number of the res
	 * @return {@code false} if total count number becomes negative value, otherwise
	 *         {@true}
	 */
	public boolean addResidues(ResidueType res, int count) {
		if (!this.m_mapResidueToCount.containsKey(res))
			this.m_mapResidueToCount.put(res, 0);
		int n = this.m_mapResidueToCount.get(res) + count;
		// The count number must be positive value
		if (n < 0)
			return false;
		// Removes the residue if the count number is zero
		if (n == 0) {
			this.m_mapResidueToCount.remove(res);
			return true;
		}
		this.m_mapResidueToCount.put(res, n);
		return true;
	}

	/**
	 * Adds the given residue type.
	 * 
	 * @param res ResidueType
	 * @return {@code true} if the addition is succeeded.
	 * @see #addResidues(ResidueType, int)
	 */
	public boolean addResidue(ResidueType res) {
		return this.addResidues(res, 1);
	}

	/**
	 * Returns set of containing residue types
	 */
	public Set<ResidueType> getResidueTypes() {
		Set<ResidueType> lRess = new TreeSet<>();
		for (ResidueType type : this.m_mapResidueToCount.keySet())
			lRess.add(type);
		return lRess;
	}

	/**
	 * Returns the number of the given residue or {@code -1} if no residue in this
	 * structure.
	 * 
	 * @param res ResidueType
	 */
	public int getNumberOfResidue(ResidueType res) {
		if (!this.m_mapResidueToCount.containsKey(res))
			return -1;
		return this.m_mapResidueToCount.get(res);
	}

	public int getNumberOfResidues() {
		int num = 0;
		for (ResidueType res : this.m_mapResidueToCount.keySet())
			num += this.m_mapResidueToCount.get(res);
		return num;
	}

	public int getNumberOfMonosaccharides() {
		int num = 0;
		for (ResidueType res : this.m_mapResidueToCount.keySet())
			if (res instanceof MonosaccharideType)
				num += this.m_mapResidueToCount.get(res);
		return num;
	}

	public int getNumberOfSubstituents() {
		int num = 0;
		for (ResidueType res : this.m_mapResidueToCount.keySet())
			if (res instanceof SubstituentType)
				num += this.m_mapResidueToCount.get(res);
		return num;
	}

	public int getMaxLinkages() {
		int nLinks = 0;
		int nRess = 0;
		for (ResidueType res : this.m_mapResidueToCount.keySet()) {
			int n = this.m_mapResidueToCount.get(res);
			nRess += n;
			nLinks += n * res.getMaxLinkages();
		}
		if (this.m_redEndType != null) {
			nRess++;
			nLinks += this.m_redEndType.getMaxLinkages();
		}
		if (nRess > 1)
			nLinks -= nRess - 1;
		return nLinks;
	}

	/**
	 * Set options for mass calculations.
	 * 
	 * @param isMonoisotopic {@code true} for monoisotopic mass, {@code false} for
	 *                       average mass
	 * @param perDeriv       PerderivatizationType for perderivatization
	 *                       ({@code null} if no derivatization type is specified)
	 */
	public void setMassOptions(boolean isMonoisotopic, PerderivatizationType perDeriv) {
		this.m_bIsMonoisotopic = isMonoisotopic;
		this.m_perderivType = perDeriv;
	}

	public boolean isMonoisotopicMass() {
		return this.m_bIsMonoisotopic;
	}

	public PerderivatizationType getPerderivatizationType() {
		return this.m_perderivType;
	}

	/**
	 * Calculates monoisotopic or average mass of the composition. The mass can also
	 * be perderivatized with the given PerderivatizationType.
	 * 
	 */
	public double computeMass() {
		double dMass = 0d;

		// Collect residues which will not be dropped with perderivatization
		List<ResidueType> lResidues = new ArrayList<>();
		for (ResidueType type : this.m_mapResidueToCount.keySet()) {
			if (this.m_perderivType != null && type instanceof SubstituentType) {
				SubstituentType subst = (SubstituentType) type;
				if (this.m_perderivType.isMethylation() && subst.isDroppedWithMethylation())
					continue;
				if (this.m_perderivType.isAcetylation() && subst.isDroppedWithAcetylation())
					continue;
			}
			lResidues.add(type);
		}

		int nResidues = 0;
		for (ResidueType type : lResidues) {
			int n = this.m_mapResidueToCount.get(type);
			dMass += n * ((this.m_bIsMonoisotopic) ? type.getMonoisotopicMass() : type.getAverageMass());
			nResidues += n;
		}
		if (this.m_redEndType != null) {
			dMass += (this.m_bIsMonoisotopic)
					? this.m_redEndType.getMonoisotopicMass() + MoleculeUtils.hydrogen_mol.getMonoisotopicMass()
					: this.m_redEndType.getAverageMass() + MoleculeUtils.hydrogen_mol.getAverageMass();
			nResidues++;
		}

		// Reduce water masses for glycosidic linkages
		if (nResidues > 1)
			dMass -= (nResidues - 1) * ((this.m_bIsMonoisotopic) ? MoleculeUtils.water.getMonoisotopicMass()
					: MoleculeUtils.water.getAverageMass());

		// Perderivatization
		if (this.m_perderivType != null) {
			int nPerDeriv = 0;
			for (ResidueType type : lResidues) {
				int n = this.m_mapResidueToCount.get(type);
				nPerDeriv += n * ((this.m_perderivType.isMethylation()) ? type.getNumMethylations()
						: (this.m_perderivType.isAcetylation()) ? type.getNumAcethylations() : 0);
			}
			if (this.m_redEndType != null) {
				nPerDeriv += (this.m_perderivType.isMethylation()) ? this.m_redEndType.getNumMethylations()
						: (this.m_perderivType.isAcetylation()) ? this.m_redEndType.getNumAcethylations() : 0;
			}
			if (nResidues > 1)
				nPerDeriv -= (nResidues - 1) * 2;

			// Add perderivationzation masses and reduce water masses
			dMass += nPerDeriv * ((this.m_bIsMonoisotopic)
					? this.m_perderivType.getMonoisotopicMass() - MoleculeUtils.hydrogen.getMonoisotopicMass()
					: this.m_perderivType.getAverageMass() - MoleculeUtils.hydrogen.getAverageMass());
		}

		return dMass;
	}

	public Composition copy() {
		return new Composition(this);
	}

	public boolean isValidStructure() {
		if (this.getNumberOfMonosaccharides() == 0)
			return false;

		int nLinks = this.getMaxLinkages();
		if (nLinks < 0)
			return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Composition))
			return false;
		Composition comp = (Composition) obj;
		if (this.m_redEndType != comp.m_redEndType)
			return false;
		if (this.m_mapResidueToCount.size() != comp.m_mapResidueToCount.size())
			return false;
		for (ResidueType res : this.m_mapResidueToCount.keySet()) {
			if (!comp.m_mapResidueToCount.containsKey(res))
				return false;
			if (comp.m_mapResidueToCount.get(res) != this.m_mapResidueToCount.get(res))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		// Group residue types into monosaccharide and substituent
		List<MonosaccharideType> lMSTypes = new ArrayList<>();
		List<SubstituentType> lSubstTypes = new ArrayList<>();
		for (ResidueType type : this.m_mapResidueToCount.keySet()) {
			if (type instanceof MonosaccharideType)
				lMSTypes.add((MonosaccharideType) type);
			if (type instanceof SubstituentType)
				lSubstTypes.add((SubstituentType) type);
		}
		StringBuffer sb = new StringBuffer();
		for (MonosaccharideType type : lMSTypes) {
			if ( sb.length() != 0 )
				sb.append(",");
			int n = this.m_mapResidueToCount.get(type);
			sb.append(type.toString());
			sb.append(":");
			sb.append(n);
		}
		for (SubstituentType type : lSubstTypes) {
			if ( sb.length() != 0 )
				sb.append(",");
			int n = this.m_mapResidueToCount.get(type);
			sb.append(type.toString());
			sb.append(":");
			sb.append(n);
		}
		if (this.m_redEndType != null) {
			if ( sb.length() != 0 )
				sb.append("--");
			sb.append(this.m_redEndType.toString());
		}
		return sb.toString();
	}

}
