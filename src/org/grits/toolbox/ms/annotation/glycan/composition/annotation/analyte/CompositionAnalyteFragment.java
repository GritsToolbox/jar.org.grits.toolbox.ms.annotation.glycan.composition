package org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionFragment;
import org.grits.toolbox.ms.annotation.structure.IAnalyteFragment;

public class CompositionAnalyteFragment implements IAnalyteFragment {

	private CompositionFragment m_fragment;

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public String getType() {
		return m_fragment.getFragmentType();
	}

	public void setFragment(CompositionFragment fragment) {
		this.m_fragment = fragment;
	}

	public CompositionFragment getFragment() {
		return this.m_fragment;
	}
}
