package org.grits.toolbox.ms.annotation.glycan.composition.test;

import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.generator.CompositionFragmenter;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class TestParser {

	public static void main(String[] args) throws DictionaryException {
		ResidueDictionary.loadDefaultDictionaries();

		String strComposition = "Hex:2,HexNAc:2--Cer=547.5328";
		Composition comp = CompositionUtils.parse(strComposition);
		System.out.println( strComposition +": "+strComposition.equals(comp.toString()) );

		CompositionFragmenter fragmenter = new CompositionFragmenter();
		fragmenter.setAFragments(true);
		fragmenter.setXFragments(true);
		fragmenter.setBFragments(true);
		fragmenter.setYFragments(true);
		fragmenter.setCFragments(true);
		fragmenter.setZFragments(true);
		fragmenter.setMaxCleavages(2);
		fragmenter.setMaxCrossRingCleavages(2);

		List<String> lFragments = fragmenter.computeFragments(comp);
		
		int i=1;
		for ( String strFrag : lFragments ) {
			Composition persed = CompositionUtils.parse(strFrag);
			System.out.println("\t"+i+++"\t"+strFrag+": "+strFrag.equals(persed.toString()));
		}
	}

}
