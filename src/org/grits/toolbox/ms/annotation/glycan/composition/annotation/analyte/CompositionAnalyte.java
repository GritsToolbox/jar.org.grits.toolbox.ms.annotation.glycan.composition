package org.grits.toolbox.ms.annotation.glycan.composition.annotation.analyte;

import org.grits.toolbox.ms.annotation.gelato.Analyte;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;

public class CompositionAnalyte extends Analyte {

	private Composition m_composition;

	public CompositionAnalyte(String stringRepresentation, Composition composition) {
		super(stringRepresentation);
		this.m_composition = composition;
	}

	@Override
	public double computeMass() {
		return this.m_composition.computeMass();
	}

	public Composition getComposition() {
		return this.m_composition;
	}

	
}
