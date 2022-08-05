package org.grits.toolbox.ms.annotation.glycan.composition.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.CrossRingFragmentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.FragmentDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.IFragmentType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;

public class TestResidueDictionary {

	public static void main(String[] args) {
		ResidueDictionary.loadDefaultDictionaries();

		for ( SubstituentType type : ResidueDictionary.getSubstituents() ) {
			System.out.println(type.getName()+": "+type.getComposition()+","+type.getMonoisotopicMass());
			System.out.println("\t"+type.getNumMethylations()+" "+type.isDroppedWithMethylation()
			+" "+type.getNumAcethylations()+" "+type.isDroppedWithAcetylation());
		}
		System.out.println();

		for ( MonosaccharideType ms : ResidueDictionary.getMonosaccharides() ) {
			System.out.println(ms.getName()+": "+ms.getComposition()+","+String.format("%.8f", ms.getMonoisotopicMass()));
			System.out.println("\t"+ms.getNumMethylations()+" "+ms.getNumAcethylations()+" "+ms.getMaxLinkages());
			FragmentDictionary.generateAllMonosaccharideFragments(ms);
			try {
				List<CrossRingFragmentType> lFragments = new ArrayList<>();
				for ( IFragmentType frag : FragmentDictionary.getFragments(ms) ) {
					if ( !frag.getCleavageType().isCrossRing() )
						continue;
					lFragments.add((CrossRingFragmentType)frag);
				}
				Collections.sort(lFragments, new Comparator<CrossRingFragmentType>() {
					@Override
					public int compare(CrossRingFragmentType o1, CrossRingFragmentType o2) {
						int iComp = 0;
						iComp = o1.getStartPositionOfCrossRingCleavage() - o2.getStartPositionOfCrossRingCleavage();
						if ( iComp != 0 )
							return iComp;
						iComp = o1.getEndPositionOfCrossRingCleavage() - o2.getEndPositionOfCrossRingCleavage();
						if ( iComp != 0 )
							return iComp;
						iComp = ( o1.getStartPositionOfCrossRingCleavage() == 0 )? 1 : -1;
						if ( !o1.getCleavageType().isRootSide() &&  o2.getCleavageType().isRootSide() )
							return -iComp;
						if (  o1.getCleavageType().isRootSide() && !o2.getCleavageType().isRootSide() )
							return iComp;
						return 0;
					}
				});
				for ( CrossRingFragmentType frag : lFragments ) {
					System.out.println("\t"+frag.getName()+": "+
							frag.getStartPositionOfCrossRingCleavage()+" "+frag.getEndPositionOfCrossRingCleavage()+" "+
							frag.getNumMethylations()+" "+frag.getNumAcethylations()+" "+frag.getMaxLinkages()+" "+
							frag.getComposition()+" "+String.format("%.8f", frag.getMonoisotopicMass()));
				}
			} catch (DictionaryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
