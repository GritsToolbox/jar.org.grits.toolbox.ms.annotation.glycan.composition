package org.grits.toolbox.ms.annotation.glycan.composition.structure.modification;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.Molecule;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public enum PerderivatizationType {

	/** A methyl group (CH3). */
	METHYL(  "perMe",  "CH3",   true,  false),
	/** A C^13 methyl group (C^13H3), see http://cell.ccrc.uga.edu/~pierce/alvarez_isotop_paper.pdf */
	HEAVYMETHYL(  "perMe(C^13)",  "C^13H3",   true,  false),
	/** A deutero-methyl group (CD3). */
	DMETHYL( "perDMe", "CD3",   true,  false),
	/** An acetyl group (C2H3O). */
	ACETYL(  "perAc",  "C2H3O", false, true),
	/** A deutero-acetyl group (C2D3O). */
	DACETYL( "perDAc", "C2D3O", false, true);
	private String m_strName;
	private Molecule m_mol;
	private boolean m_bIsMethylation;
	private boolean m_bIsAcetylation;

	private PerderivatizationType(String name, String composition, boolean isMethyaltion, boolean isAcetylation) {
		this.m_strName = name;
		try {
			this.m_mol = Molecule.parse(composition);
		} catch (DictionaryException e) {
		}
		this.m_bIsMethylation = isMethyaltion;
		this.m_bIsAcetylation = isAcetylation;
	}

	public String getName() {
		return this.m_strName;
	}

	public double getMonoisotopicMass() {
		return this.m_mol.getMonoisotopicMass();
	}

	public double getAverageMass() {
		return this.m_mol.getAverageMass();
	}

	public boolean isMethylation() {
		return this.m_bIsMethylation;
	}

	public boolean isAcetylation() {
		return this.m_bIsAcetylation;
	}

	public static PerderivatizationType forName(String name) {
		for ( PerderivatizationType type : PerderivatizationType.values() ) {
			if ( type.m_strName.equals(name) )
				return type;
		}
		return null;
	}
}
