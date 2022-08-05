package org.grits.toolbox.ms.annotation.glycan.composition.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.generator.CompositionFragmenter;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionFragment;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class TestFragments {

	private static boolean isMonoisotopic = true;
	private static PerderivatizationType perDeriv = PerderivatizationType.METHYL;

	public static void main(String[] args) throws DictionaryException {
		ResidueDictionary.loadDefaultDictionaries();

		System.out.println(perDeriv.getName()+": "+perDeriv.getMonoisotopicMass());

		List<String> lCompositions = new ArrayList<>();
//		String strComp = "Hex:3,HexNAc:2,NeuAc:2--Cer=547.5328";
		lCompositions.add("Hex:2");
		lCompositions.add("Hex:1,HexNAc:1");
		lCompositions.add("Hex:3");
		lCompositions.add("Hex:2,HexNAc:1");
		lCompositions.add("Hex:1,HexNAc:2");
		lCompositions.add("Hex:1,HexNAc:1,NeuAc:1");
		lCompositions.add("Hex:4");
		lCompositions.add("Hex:2,HexNAc:2");
		lCompositions.add("Hex:5");
		lCompositions.add("Hex:3,HexNAc:2");

		for ( String strComp : lCompositions )
			computeFragments(strComp);

	}

	private static void computeFragments(String strComp) throws DictionaryException {
		Composition comp = CompositionUtils.parse(strComp);
		comp.setMassOptions(isMonoisotopic, perDeriv);
		System.out.println("\nTest\n");
		System.out.println(comp+": "+comp.computeMass());

		// Test fragments
		CompositionFragmenter fragmenter = new CompositionFragmenter();
		fragmenter.setAFragments(true);
		fragmenter.setBFragments(false);
		fragmenter.setCFragments(false);
		fragmenter.setXFragments(true);
		fragmenter.setYFragments(true);
		fragmenter.setZFragments(true);
//		fragmenter.setMaxCleavages(2);
//		fragmenter.setMaxCrossRingCleavages(1);

		System.out.println("Fragments of "+comp);
		double start = (double)System.currentTimeMillis();
		List<String> lFragments = fragmenter.computeFragments(comp);
		double end = (double)System.currentTimeMillis();
		System.out.println("Total # of fragments: "+lFragments.size());
		System.out.println(String.format("Calculation time: %.3f", (end - start)/1000));
		start = (double)System.currentTimeMillis();
		Collections.sort(lFragments, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					Composition comp1 = CompositionUtils.parse(o1);
					Composition comp2 = CompositionUtils.parse(o2);
					comp1.setMassOptions(isMonoisotopic, perDeriv);
					comp2.setMassOptions(isMonoisotopic, perDeriv);
					double deff = comp1.computeMass() - comp2.computeMass();
					if ( deff > 0 ) return -1;
					if ( deff < 0 ) return 1;

					return o1.compareTo(o2);
				} catch (DictionaryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 0;
			}
			
		});
		end = (double)System.currentTimeMillis();
		System.out.println(String.format("Sorting time: %.3f", (end - start)/1000));
		int i=1;
		for ( String strFrag : lFragments ) {
			CompositionFragment frag = (CompositionFragment)CompositionUtils.parse(strFrag);
			frag.setMassOptions(isMonoisotopic, perDeriv);
			System.out.println(
				String.format( "\t%d:\t%s\t%s\t%.8f", i++, strFrag, frag.getFragmentType(), frag.computeMass() )
			);
		}

	}
}
