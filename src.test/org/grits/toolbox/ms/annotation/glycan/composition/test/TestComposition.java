package org.grits.toolbox.ms.annotation.glycan.composition.test;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;

public class TestComposition {

	public static void main(String[] args) throws Exception {
		ResidueDictionary.loadDefaultDictionaries();

		boolean isMonoisotopic = true;
		PerderivatizationType perDeriv = PerderivatizationType.METHYL;
		if ( perDeriv != null )
			System.out.println(perDeriv.getName()+": "+perDeriv.getMonoisotopicMass());

		MonosaccharideType msType = null;

		// N-Glycan Core
		Composition comp = new Composition();
		msType = ResidueDictionary.getMonosaccharideType("Hex");
		comp.addResidues(msType, 3);
		msType = ResidueDictionary.getMonosaccharideType("HexNAc");
		comp.addResidues(msType, 2);
		SubstituentType redEnd = ResidueDictionary.getReducingEndType("redEnd");
		comp.setReducingEnd(redEnd);
		comp.setMassOptions(isMonoisotopic, perDeriv);
		System.out.println("N-Glycan Core (reduced reducing end)");
		System.out.println(comp+": "+comp.computeMass());
		System.out.println("Substructures:");
		for ( Composition sub : CompositionUtils.generateSubstructures(comp) ) {
			System.out.println("\t"+sub);
		}

		Composition comp1 = comp.copy();
		msType = ResidueDictionary.getMonosaccharideType("dHex");
		comp1.addResidues(msType, 1);
		// N-Glycan Core + Fuc
		System.out.println("N-Glycan Core + Fuc");
		System.out.println(comp1+": "+comp1.computeMass());

		Composition comp2 = comp.copy();
		msType = ResidueDictionary.getMonosaccharideType("HexNAc");
		comp2.addResidues(msType, 1);
		// N-Glycan Core + Fuc
		System.out.println("N-Glycan Bisected");
		System.out.println(comp2+": "+comp2.computeMass());

		System.out.println();
	}

}
