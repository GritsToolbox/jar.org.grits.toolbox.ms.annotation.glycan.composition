package org.grits.toolbox.ms.annotation.glycan.composition.annotation;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte.CompositionAnalyteFragment;
import org.grits.toolbox.ms.annotation.glycan.composition.generator.CompositionFragmenter;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionFragment;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.CompositionUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.modification.PerderivatizationType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.structure.GlycanAnalyteFragments;
import org.grits.toolbox.ms.annotation.structure.IAnalyteFragment;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.Fragment;

public class CompositionAnalyteFragments extends GlycanAnalyteFragments {

	/**
	 * @param sequence
	 * @param settings
	 * @param fragments
	 * @param maxNumClvg
	 * @param maxNumCr
	 * @param isMonoisotopic
	 * @return
	 */
	@Override
	protected List<IAnalyteFragment> getFragments(
			String sequence, AnalyteSettings settings, List<Fragment> fragments, int maxNumClvg, int maxNumCr, boolean isMonoisotopic ) {
		// allow all types of fragments
		CompositionFragmenter t_fragmenter = new CompositionFragmenter();
		//initialize all of the fragment types false to avoid the default value which is true
		t_fragmenter.setAllFragments(false);

		for(Fragment fragment : fragments){
			if(fragment.getType().equals(Fragment.TYPE_B)){
				t_fragmenter.setBFragments(true);
			}
			if(fragment.getType().equals(Fragment.TYPE_Y)){
				t_fragmenter.setYFragments(true);
			}
			if(fragment.getType().equals(Fragment.TYPE_C)){
				t_fragmenter.setCFragments(true);
			}
			if(fragment.getType().equals(Fragment.TYPE_Z)){
				t_fragmenter.setZFragments(true);
			}
			if(fragment.getType().equals(Fragment.TYPE_A)){
				t_fragmenter.setAFragments(true);
			} 
			if(fragment.getType().equals(Fragment.TYPE_X)){
				t_fragmenter.setXFragments(true);
			}
		}

		// set the number of allowed fragments
		t_fragmenter.setMaxCleavages(maxNumClvg);
		t_fragmenter.setMaxCrossRingCleavages(maxNumCr);
		try {
			Composition composition = CompositionUtils.parse(sequence);
			PerderivatizationType perDeriv = PerderivatizationType.forName(settings.getGlycanSettings().getPerDerivatisationType());
			List<IAnalyteFragment> lFrags = new ArrayList<>();
			for ( String strFrag : t_fragmenter.computeFragments(composition) ) {
				CompositionAnalyteFragment analyteFrag = new CompositionAnalyteFragment();
				CompositionFragment frag = (CompositionFragment)CompositionUtils.parse(strFrag);
				// mass options must be set for each fragment
				frag.setMassOptions(isMonoisotopic, perDeriv);
				analyteFrag.setFragment(frag);
				lFrags.add(analyteFrag);
			}
			return lFrags;
		} catch (DictionaryException e) {
			e.printStackTrace();
			return null;
		}
	}
}
