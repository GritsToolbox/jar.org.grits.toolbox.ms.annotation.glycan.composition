package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

public enum CoreModificationType {

	DEOXY("d", -1, 0),
	ACID("a", 1, -2),
	HYDROXY("h", 1, 0),
	DOUBLEBOND("u", -1, -2),
	LACTON("lac", -1, -2),
	ANHYDRO("an", -1, -2);

	private String m_strName;
	private int m_nOxygen;
	private int m_nHydrogen;

	private CoreModificationType(String name, int nO, int nH) {
		this.m_strName = name;
		this.m_nOxygen = nO;
		this.m_nHydrogen = nH;
	}

	/**
	 * Returns the name of the modification type
	 */
	public String getName() {
		return this.m_strName;
	}

	/**
	 * Returns the number of oxygens the modification gains/losses
	 */
	public int getNumberOfOxygen() {
		return this.m_nOxygen;
	}

	/**
	 * Returns the number of hydrogens the modification gains/losses
	 */
	public int getNumberOfHydrogen() {
		return this.m_nHydrogen;
	}

	/**
	 * Returns CoreModificationType for name
	 * @param name String of the modification
	 * @return CoreModificationType with the given name or {@code null} if nothing have the name
	 */
	public static CoreModificationType forName(String name) {
		for ( CoreModificationType mod : CoreModificationType.values() ) {
			if ( !mod.m_strName.equals(name) )
				continue;
			return mod;
		}
		return null;
	}
}
