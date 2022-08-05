package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

public interface ICleavageType {
	public char getSymbol();
	public boolean isCrossRing();
	public boolean isRootSide();
	public boolean hasOxygen();
}
