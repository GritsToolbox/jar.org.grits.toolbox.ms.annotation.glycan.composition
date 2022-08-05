package org.grits.toolbox.ms.annotation.glycan.composition.annotation;

import org.grits.toolbox.ms.annotation.gelato.AnalyteStructureAnnotation;
import org.grits.toolbox.ms.annotation.gelato.AnnotateFragments;
import org.grits.toolbox.ms.annotation.gelato.glycan.GlycanAnalyteMatcher;

public class CompositionAnalyteMatcher extends GlycanAnalyteMatcher {

	public CompositionAnalyteMatcher(int iCurScan, AnalyteStructureAnnotation parent) {
		super(iCurScan, parent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiate a CompositionAnnotateFragments object when AnnotateFragments is needed
	 **/
	@Override
	public AnnotateFragments getNewAnnotateFragmentsObject() {
		return new CompositionAnnotateFragments(this);
	}
}
