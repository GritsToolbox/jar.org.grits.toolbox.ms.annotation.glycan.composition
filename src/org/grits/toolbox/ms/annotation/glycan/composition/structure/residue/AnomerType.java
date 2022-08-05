package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

public enum AnomerType {
	a('a', "alpha"),
	b('b', "beta"),
	o('o', "open ring"),
	x('?', "unknown");

	private char m_cSymbol;
	private String m_strName;

	private AnomerType(char symbol, String name) {
		this.m_cSymbol = symbol;
		this.m_strName = name;
	}

	public char getSymbol() {
		return this.m_cSymbol;
	}

	public String getName() {
		return this.m_strName;
	}

	public static AnomerType forSymbol(char symbol) {
		for ( AnomerType type : AnomerType.values() ) {
			if ( type.m_cSymbol != symbol )
				continue;
			return type;
		}
		return null;
	}
}
