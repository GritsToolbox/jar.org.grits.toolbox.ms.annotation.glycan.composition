package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class CustomSubstituentType extends SubstituentType {

	private int m_nScale = 4;

	public CustomSubstituentType(String name, double mass, boolean isRedEnd) {
		super(name, new ArrayList<>(),
			"?", 1, false, 1, false, 1, false, isRedEnd,
			"Custom reducing end type: "+name);

		this.m_dMonoisotopicMass = mass;
		this.m_dAverageMass = mass;
	}

	public String toString() {
		BigDecimal bdMass = new BigDecimal(this.m_dMonoisotopicMass);
		return this.getName()+"="+bdMass.setScale(this.m_nScale, RoundingMode.HALF_UP);
	}
}
