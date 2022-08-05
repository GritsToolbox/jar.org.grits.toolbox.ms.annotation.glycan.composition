package org.grits.toolbox.ms.annotation.glycan.composition.test;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class TestCompositionValidation {

	public static void main(String[] args) throws DictionaryException {
		ResidueDictionary.loadDefaultDictionaries();

//		String str = "Hex#x_1_5:2--Hex#a_1_5";
//		String str = "Hex#x_1_5:1,Sugar#z:1--Sugar#b";
		String str = "Sugar#z:4--Hex#a_1_5";

		Composition comp = CompositionUtils.parse(str);
		System.out.print(comp.isValidStructure());
	}

}
