package org.grits.toolbox.ms.annotation.glycan.composition.test;

import java.util.Iterator;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.generator.CompositionGenerator;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class TestCompositionGeneration {

	public static void main(String[] args) throws DictionaryException {
		ResidueDictionary.loadDefaultDictionaries();

		Composition maxComposition = CompositionUtils.parse("Hex:7,HexNAc:6,Neu5Ac:4,dHex:1");
		Composition minComposition = CompositionUtils.parse("Hex:3,HexNAc:2");
		maxComposition.setMassOptions(true, PerderivatizationType.METHYL);
		minComposition.setMassOptions(true, PerderivatizationType.METHYL);

//		double dThreshold = Double.MAX_VALUE;
		double dThreshold = 3000.0d;
		List<Composition> lCompositions = CompositionUtils.generateCompositions(minComposition, maxComposition, dThreshold);
		CompositionUtils.sortCompositionsByMass(lCompositions, true);

		System.out.println("Max:"+maxComposition);
		System.out.println("Min:"+minComposition);
		System.out.println("Possible compositions:");
		int i=1;
		for ( Composition comp : lCompositions ) {
			System.out.println(String.format("%d:\t%s\t%.4f", i++, comp, comp.computeMass()));
		}

		i=1;
		CompositionGenerator compGen = new CompositionGenerator(minComposition, maxComposition);
		compGen.setMassThreshold(dThreshold);
		for ( Iterator<Composition> it = compGen; it.hasNext(); ) {
			Composition comp = it.next();
			System.out.println(String.format("%d:\t%s\t%.4f", i++, comp, comp.computeMass()));
		}
	}

}
