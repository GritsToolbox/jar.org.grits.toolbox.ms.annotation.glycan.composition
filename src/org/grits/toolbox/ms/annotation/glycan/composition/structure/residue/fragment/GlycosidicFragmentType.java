package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

import java.util.ArrayList;

import org.grits.toolbox.ms.annotation.glycan.composition.molecule.Molecule;
import org.grits.toolbox.ms.annotation.glycan.composition.molecule.MoleculeUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;

public class GlycosidicFragmentType extends ResidueType implements IFragmentType {

	private GlycosidicCleavageType m_cleavageType;

	public GlycosidicFragmentType(GlycosidicCleavageType clvType) {
		super("Sugar"+clvType.toString(), new ArrayList<>(),
				Character.toUpperCase(clvType.getSymbol())+" cleavage of a monosaccharide" );
		this.m_cleavageType = clvType;
		computeMolecule();
	}

	@Override
	public GlycosidicCleavageType getCleavageType() {
		return this.m_cleavageType;
	}

	@Override
	protected void computeMolecule() {
		// For no cross ring fragments
		if ( this.m_cleavageType.isCrossRing() )
			return;
		Molecule mol = ( this.m_cleavageType.hasOxygen() )? MoleculeUtils.water : MoleculeUtils.hydrogen_mol;
		this.m_strComposition = mol.toString();
		this.m_dMonoisotopicMass = mol.getMonoisotopicMass();
		this.m_dAverageMass = mol.getAverageMass();
	}


}
