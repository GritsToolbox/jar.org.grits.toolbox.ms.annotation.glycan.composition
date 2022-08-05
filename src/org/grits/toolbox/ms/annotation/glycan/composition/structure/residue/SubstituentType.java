package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.Molecule;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class SubstituentType extends ResidueType {

	private boolean m_bDropMethyl;
	private boolean m_bDropAcetyl;
	private boolean m_bIsRedEnd;

	SubstituentType(String name, List<String> synonyms, String composition, int nMe, boolean bDropMe, int nAc,
			boolean bDropAc, int nLink, boolean bIsAcid, boolean bIsRedEnd, String desc) {
		super(name, synonyms, desc);

		this.m_strComposition = composition;
		this.m_nMethyl = nMe;
		this.m_bDropMethyl = bDropMe;
		this.m_nAcetyl = nAc;
		this.m_bDropAcetyl = bDropAc;
		this.m_nLinkages = nLink;
		this.m_bIsAcid = bIsAcid;
		this.m_bIsRedEnd = bIsRedEnd;

		computeMolecule();
	}

	/**
	 * Returns {@code true} if this substituent type is dropped during
	 * permethylation.
	 */
	public boolean isDroppedWithMethylation() {
		return this.m_bDropMethyl;
	}

	/**
	 * Returns {@code true} if this substituent type is dropped during
	 * peracetylation.
	 */
	public boolean isDroppedWithAcetylation() {
		return this.m_bDropAcetyl;
	}

	/**
	 * Returns {@code true} if this substituent type is reducing end.
	 */
	public boolean isReducingEnd() {
		return this.m_bIsRedEnd;
	}

	@Override
	protected void computeMolecule() {
		if ( this.m_strComposition.equals("?") )
			return;

		try {
			Molecule mol = Molecule.parse(this.m_strComposition);
			this.m_dMonoisotopicMass = mol.getMonoisotopicMass();
			this.m_dAverageMass = mol.getAverageMass();
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
	}

}
