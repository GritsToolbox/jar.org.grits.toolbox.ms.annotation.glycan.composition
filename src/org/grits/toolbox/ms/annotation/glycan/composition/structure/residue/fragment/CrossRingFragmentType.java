package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.AnomerType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.CoreModificationType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;

/**
 * Class for a fragment type of a cross-ring-cleaved monosaccharide.
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class CrossRingFragmentType extends MonosaccharideType implements IFragmentType {

	private MonosaccharideType m_msTypeParent;
	private CrossRingCleavageType m_cleavageType;
	private int m_iCrossRingCleavageStart;
	private int m_iCrossRingCleavageEnd;

	protected CrossRingFragmentType(MonosaccharideType msTypeParent, CrossRingCleavageType clvType,
			int iClvStart, int iClvEnd) {
		super(msTypeParent.getName()+clvType.toString()+"_"+iClvStart+"_"+iClvEnd,
				getSynonyms(msTypeParent, clvType, iClvStart, iClvEnd),
				iClvStart+","+iClvEnd+" "+
				Character.toUpperCase(clvType.getSymbol())+" fragment of "+msTypeParent.getDescription()
			);
		this.m_msTypeParent = msTypeParent;
		this.m_cleavageType = clvType;
		this.m_iCrossRingCleavageStart = iClvStart;
		this.m_iCrossRingCleavageEnd = iClvEnd;

		initialize();
	}

	private static List<String> getSynonyms(MonosaccharideType msType, CrossRingCleavageType clvType, int iStart, int iEnd) {
		List<String> synonyms = new ArrayList<>();
		for ( String synonym : msType.getSynonyms() ) {
			synonyms.add(synonym+clvType.toString()+"_"+iStart+"_"+iEnd);
		}
		return synonyms;
	}

	/**
	 * Returns cleaved monosaccharide type.
	 * @return MonosaccharideType
	 */
	public MonosaccharideType getCleavedMonosaccharideType() {
		return this.m_msTypeParent;
	}

	@Override
	public CrossRingCleavageType getCleavageType() {
		return this.m_cleavageType;
	}

	public int getStartPositionOfCrossRingCleavage() {
		return this.m_iCrossRingCleavageStart;
	}

	public int getEndPositionOfCrossRingCleavage() {
		return this.m_iCrossRingCleavageEnd;
	}

	@Override
	protected void initialize() {
		// Update monosaccharide info
		this.m_anomer = this.m_msTypeParent.getAnomer();
		this.m_iAnomPos = this.m_msTypeParent.getAnomericCarbon();
		this.m_iRingSize = this.m_msTypeParent.getRingSize();
		this.m_iCarbonLength = this.m_msTypeParent.getCarbonLength();

		// No cross ring cleavage for open chain monosaccharide
		if ( this.m_anomer == AnomerType.o ) {
			this.m_iCrossRingCleavageStart = -1;
			this.m_iCrossRingCleavageEnd = -1;
			// Do not set any info into ms part
			return;
		}

		// Get start and end positions of ring
//		int iRingStartPos = this.m_iAnomPos;
		int iRingEndPos = this.m_iAnomPos+this.m_iRingSize-2;

		// Check the cleavage points
		boolean isAnomerInside = (this.m_iCrossRingCleavageStart != 0);
		boolean isCleavedAtRingEnd = (this.m_iCrossRingCleavageEnd == this.m_iRingSize-1);

		// Identify the start and end positions of cross ring cleavage
		int iClvStartPos = !isAnomerInside? 1 : this.m_iAnomPos+this.m_iCrossRingCleavageStart;
		int iClvEndPos = isCleavedAtRingEnd?
				this.m_iCarbonLength : this.m_iAnomPos+this.m_iCrossRingCleavageEnd-1;

		// Collect remaining carbons after cleavage
		List<Integer> lRemainingCarbons = new ArrayList<>();
		for ( int i=1; i<=this.m_msTypeParent.getCarbonLength(); i++ ) {
			boolean isCarbonInside = ( i >= iClvStartPos && i <= iClvEndPos );
			boolean isAnomerSide = (isCarbonInside == isAnomerInside);
			if ( this.m_cleavageType.isRootSide() == isAnomerSide )
				lRemainingCarbons.add(i);
		}

		// Update carbon length
		this.m_iCarbonLength = lRemainingCarbons.size();

		// Remove modifications and substituents on cleaved carbons
		for ( CoreModification mod : this.m_msTypeParent.getModifications() ) {
			if ( mod.getPosition() > 0 && !lRemainingCarbons.contains(mod.getPosition()) )
				continue;
			// Update positions
			this.m_lMods.add(new CoreModification(mod.getType(), lRemainingCarbons.indexOf(mod.getPosition())+1));
		}
		for ( Substituent subst : this.m_msTypeParent.getSubstituents() ) {
			if ( subst.getPosition() > 0 && !lRemainingCarbons.contains(subst.getPosition()) )
				continue;
			// Update positions
			this.m_lSubsts.add(new Substituent(subst.getType(), lRemainingCarbons.indexOf(subst.getPosition())+1));
		}

		// Add modification depends on cleaved position
		if ( isCleavedAtRingEnd ) {
			if ( lRemainingCarbons.contains(this.m_iAnomPos) )
				this.m_lMods.add(new CoreModification(CoreModificationType.HYDROXY, lRemainingCarbons.indexOf(this.m_iAnomPos)+1));
			else
				this.m_lMods.add(new CoreModification(CoreModificationType.DEOXY, lRemainingCarbons.indexOf(iRingEndPos)+1));
		}

		// Update ring size
		this.m_iAnomPos = ( lRemainingCarbons.contains(this.m_iAnomPos) )?
				lRemainingCarbons.indexOf(this.m_iAnomPos) + 1 : 0;
		this.m_iRingSize = lRemainingCarbons.indexOf(iRingEndPos) - this.m_iAnomPos + 3;

		// Do molecule and linkage calculations
		super.initialize();
	}
}
