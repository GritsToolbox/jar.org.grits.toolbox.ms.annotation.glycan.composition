package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.Molecule;
import org.grits.toolbox.ms.annotation.glycan.composition.molecule.MoleculeUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class MonosaccharideType extends ResidueType {

	protected int m_iCarbonLength;

	protected AnomerType m_anomer;
	protected int m_iAnomPos;
	protected int m_iRingSize;

	protected List<CoreModification> m_lMods;
	protected List<Substituent> m_lSubsts;

	/**
	 * An inner class for core modifications.
	 */
	protected static class CoreModification {
		private CoreModificationType m_type;
		private int m_iPosition;

		/**
		 * @param type CoreModificationType
		 * @param pos The number of modification position. A negative value for unknown position.
		 */
		public CoreModification(CoreModificationType type, int pos) {
			this.m_type = type;
			this.m_iPosition = pos;
		}

		public int getPosition() {
			return this.m_iPosition;
		}

		public CoreModificationType getType() {
			return this.m_type;
		}

		public String toString() {
			return this.m_iPosition + ":" + this.m_type.getName();
		}
	}

	/**
	 * An inner class for core substituents.
	 */
	protected static class Substituent {
		private SubstituentType m_type;
		private int m_iPosition;

		/**
		 * @param type SubstituentType
		 * @param pos The number of modification position. A negative value for unknown position.
		 */
		public Substituent(SubstituentType type, int pos) {
			this.m_type = type;
			this.m_iPosition = pos;
		}

		public int getPosition() {
			return this.m_iPosition;
		}

		public SubstituentType getType() {
			return this.m_type;
		}

		public String toString() {
			return this.m_iPosition + ":" + this.m_type.getName();
		}
	}

	protected MonosaccharideType(String name, List<String> synonyms, String desc) {
		super(name, synonyms, desc);
		this.m_iCarbonLength = 0;
		this.m_anomer = AnomerType.o;
		this.m_iAnomPos = 0;
		this.m_iRingSize = 0;
		this.m_lMods = new ArrayList<>();
		this.m_lSubsts = new ArrayList<>();
	}

	protected MonosaccharideType(String name, List<String> synonyms, int nCarbon, AnomerType anomType, int iAnomPos,
			int iRingSize, List<CoreModification> lMods, List<Substituent> lSubsts, String desc) {
		super(name, synonyms, desc);
		this.m_iCarbonLength = nCarbon;
		this.m_anomer = anomType;
		this.m_iAnomPos = iAnomPos;
		this.m_iRingSize = iRingSize;
		this.m_lMods = lMods;
		this.m_lSubsts = lSubsts;

		initialize();
	}

	/**
	 * Returns the number of carbon length for this monosaccharide type.
	 */
	public int getCarbonLength() {
		return this.m_iCarbonLength;
	}

	/**
	 * Returns the default anomer type for this monosaccharide type.
	 * 
	 * @see AnomerType
	 */
	public AnomerType getAnomer() {
		return this.m_anomer;
	}

	/**
	 * Returns the anomeric carbon for this monosaccharide type.
	 */
	public int getAnomericCarbon() {
		return this.m_iAnomPos;
	}

	/**
	 * Returns the default ring size for this monosaccharide type.
	 */
	public int getRingSize() {
		return this.m_iRingSize;
	}

	/**
	 * Returns the list of core modifications for this monosaccharide type.
	 */
	public List<CoreModification> getModifications() {
		return this.m_lMods;
	}

	/**
	 * Returns the list of core modifications connecting on the given position.
	 * All modifications will be returned if the given number is zero or negative.
	 * The modification with unknown position is always returned.
	 * @param pos The position number which core modifications connect to
	 */
	public List<CoreModificationType> getCoreModifications(int iPos) {
		List<CoreModificationType> lMods = new ArrayList<>();
		for ( CoreModification mod : this.m_lMods ) {
			if ( iPos > 0 && mod.m_iPosition > 0 && mod.m_iPosition != iPos )
				continue;
			lMods.add(mod.m_type);
		}
		return lMods;
	}

	/**
	 * Returns the list of substituents for this monosaccharide type.
	 */
	public List<Substituent> getSubstituents() {
		return this.m_lSubsts;
	}

	/**
	 * Returns the list of core substituents connecting on the given position.
	 * All modifications will be returned if the given number is negative.
	 * The modification with unknown position is always returned.
	 * @param pos The position number which substituents connect to
	 */
	public List<SubstituentType> getSubstituents(int iPos) {
		List<SubstituentType> lSubsts = new ArrayList<>();
		for ( Substituent subst : this.m_lSubsts ) {
			if ( iPos < 0 && subst.m_iPosition < 0 && subst.m_iPosition != iPos )
				continue;
			lSubsts.add(subst.m_type);
		}
		return lSubsts;
	}

	protected void initialize() {
		checkAcid();
		computeAvailablePositions();
		computeMolecule();
	}

	private void checkAcid() {
		this.m_bIsAcid = false;
		for (CoreModification mod : this.m_lMods) {
			if (mod.m_type != CoreModificationType.ACID)
				continue;
			this.m_bIsAcid = true;
			return;
		}
		for (Substituent subst : this.m_lSubsts) {
			if (!subst.m_type.m_bIsAcid)
				continue;
			this.m_bIsAcid = true;
			return;
		}
	}

	private void computeAvailablePositions() {
		int nPos = getNumAvailableCorePositions();

		// Count number of methylation and acetylation
		int nMe = nPos;
		int nAc = nPos;
		for (CoreModification mod : this.m_lMods) {
			if ( mod.m_type == CoreModificationType.ACID ) {
				nMe++;
				nAc++;
			}
		}
		for (Substituent subst : this.m_lSubsts) {
			nPos += subst.getType().getMaxLinkages() - 2;
			if (!subst.m_type.isDroppedWithMethylation())
				nMe += subst.getType().getNumMethylations() - 2;
			if (!subst.m_type.isDroppedWithAcetylation())
				nAc += subst.getType().getNumAcethylations() - 2;
		}
		this.m_nLinkages = nPos;
		this.m_nMethyl = nMe;
		this.m_nAcetyl = nAc;
	}

	private int getNumAvailableCorePositions() {
		int nPos = getAvailableCorePositions().size();
		for (CoreModification mod : this.m_lMods) {
			if (mod.m_type == CoreModificationType.ANHYDRO)
				nPos -= 2;
			if (mod.m_type == CoreModificationType.LACTON)
				nPos--;
		}
		return nPos;
	}

	private List<Integer> getAvailableCorePositions() {
		if (this.m_iCarbonLength < 1)
			return new ArrayList<>();

		// Check occupied positions
		Set<Integer> lOccupiedPoss = new HashSet<>();

		// Check modifications
		for (CoreModification mod : this.m_lMods) {
			if (mod.m_iPosition < 1)
				continue;
			if (mod.m_type == CoreModificationType.DEOXY
			 || mod.m_type == CoreModificationType.ACID
			 || mod.m_type == CoreModificationType.DOUBLEBOND)
				lOccupiedPoss.add(mod.m_iPosition);
		}

		// Check anomer and ring
		if (this.m_anomer == AnomerType.o)
			lOccupiedPoss.add(this.m_iAnomPos);
		else if (this.m_iRingSize > 0)
			lOccupiedPoss.add(this.m_iAnomPos + this.m_iRingSize - 2);

		List<Integer> lLinkPoss = new ArrayList<>();
		for (int i = 1; i <= this.m_iCarbonLength; i++) {
			if (lOccupiedPoss.contains(i))
				continue;
			lLinkPoss.add(i);
		}
		return lLinkPoss;
	}

	@Override
	protected void computeMolecule() {

		if (this.m_iCarbonLength < 1)
			return;

		// Calculate composition of core structure
		int nC = this.m_iCarbonLength;
		int nO = nC;
		int nH = nC*2;

		for (CoreModification mod : this.m_lMods) {
			nO += mod.m_type.getNumberOfOxygen();
			nH += mod.m_type.getNumberOfHydrogen();
		}

		try {
			Molecule mol = new Molecule();
			mol.add(MoleculeUtils.getAtom("C"), nC);
			mol.add(MoleculeUtils.getAtom("O"), nO);
			mol.add(MoleculeUtils.getAtom("H"), nH);
			for (Substituent subst : this.m_lSubsts) {
				Molecule molSubst = Molecule.parse(subst.getType().getComposition());
				mol.add(molSubst);
			}
			mol.remove(MoleculeUtils.water, this.m_lSubsts.size());

			this.m_strComposition = mol.toString();
			this.m_dMonoisotopicMass = mol.getMonoisotopicMass();
			this.m_dAverageMass = mol.getAverageMass();
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
	}

	public List<String> validateType() {
		List<String> lErrors = new ArrayList<>();
		// Check positions
		for (CoreModification mod : this.m_lMods ) {
			if (mod.m_iPosition > this.m_iCarbonLength)
				lErrors.add("The position number must be less than carbon length.");
		}

		return lErrors;
	}
}
