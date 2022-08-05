package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

public enum GlycosidicCleavageType implements ICleavageType {
	B('b', true,  false),
	C('c', true,  true),
	Y('y', false, true),
	Z('z', false, false);

	private char m_cSymbol;
	private boolean m_bIsRootSide;
	private boolean m_bHasOxygen;

	private GlycosidicCleavageType(char symbol, boolean isRootSide, boolean hasOxygen) {
		this.m_cSymbol = symbol;
		this.m_bIsRootSide = isRootSide;
		this.m_bHasOxygen = hasOxygen;
	}

	public char getSymbol() {
		return this.m_cSymbol;
	}

	public boolean isCrossRing() {
		return false;
	}

	public boolean isRootSide() {
		return this.m_bIsRootSide;
	}

	public boolean hasOxygen() {
		return this.m_bHasOxygen;
	}

	@Override
	public String toString() {
		return "#"+this.m_cSymbol;
	}

	public static GlycosidicCleavageType forString(String strClv) {
		for ( GlycosidicCleavageType type : GlycosidicCleavageType.values() ) {
			if ( !type.toString().equals(strClv) )
				continue;
			return type;
		}
		return null;
	}

}
