package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

public enum CrossRingCleavageType implements ICleavageType {
	A('a', true),
	X('x', false);

	private char m_cSymbol;
	private boolean m_bIsRootSide;

	private CrossRingCleavageType(char symbol, boolean isRootSide) {
		this.m_cSymbol = symbol;
		this.m_bIsRootSide = isRootSide;
	}

	public char getSymbol() {
		return this.m_cSymbol;
	}

	public boolean isCrossRing() {
		return true;
	}

	public boolean isRootSide() {
		return this.m_bIsRootSide;
	}

	public boolean hasOxygen() {
		return false;
	}

	@Override
	public String toString() {
		return "#"+this.m_cSymbol;
	}

	public static CrossRingCleavageType forString(String strClv) {
		if ( strClv != null && strClv.contains("_") )
			strClv = strClv.substring(0, strClv.indexOf('_'));

		for ( CrossRingCleavageType type : CrossRingCleavageType.values() ) {
			if ( !type.toString().equals(strClv) )
				continue;
			return type;
		}
		return null;
	}
}
