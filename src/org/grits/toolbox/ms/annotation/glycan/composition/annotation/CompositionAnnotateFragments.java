package org.grits.toolbox.ms.annotation.glycan.composition.annotation;

import org.grits.toolbox.ms.annotation.gelato.AnalyteMatcher;
import org.grits.toolbox.ms.annotation.gelato.glycan.GlycanAnnotateFragments;
import org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte.CompositionAnalyte;
import org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte.CompositionAnalyteFragment;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionFragment;
import org.grits.toolbox.ms.annotation.structure.GelatoAnalyte;
import org.grits.toolbox.ms.annotation.structure.GlycanStructure;
import org.grits.toolbox.ms.annotation.structure.IAnalyteFragment;
import org.grits.toolbox.ms.annotation.structure.IAnalyteFragments;

public class CompositionAnnotateFragments extends GlycanAnnotateFragments {

	public CompositionAnnotateFragments(AnalyteMatcher parentAnalyteMatcher) {
		super(parentAnalyteMatcher);
	}

	@Override
	protected IAnalyteFragments getNewAnalyteFragmentObject() {
		return new CompositionAnalyteFragments();
	}

	@Override
	protected GelatoAnalyte getNewFragmentGelatoAnalyteObject(IAnalyteFragment fragment, String sId) {
		CompositionAnalyteFragment analyteFrag = (CompositionAnalyteFragment) fragment;
		CompositionFragment copy = analyteFrag.getFragment().copy();
		GlycanStructure fragStructure = new GlycanStructure();
		fragStructure.setSequence(analyteFrag.getFragment().toString());
		fragStructure.setId(sId);	

		CompositionAnalyte flagAnalyte = new CompositionAnalyte(fragStructure.getSequence(), copy);
		GelatoAnalyte fragGelatoAnalyte = new GelatoAnalyte(flagAnalyte, fragStructure);
		return fragGelatoAnalyte;
	}

}
