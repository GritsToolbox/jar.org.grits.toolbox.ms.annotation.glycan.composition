package org.grits.toolbox.ms.annotation.glycan.composition.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;

public class CompositionGeneratorSettings {

	private Map<ResidueType, List<Integer>> m_mapResidueToMaxAndMin;
	private double m_dThresholdMass;

	public CompositionGeneratorSettings() {
		this.m_mapResidueToMaxAndMin = new TreeMap<>();
		this.m_dThresholdMass = -1d;
	}

	/**
	 * Sets residue to be added to the compositions and its maximum and minimum counts.
	 * @param res ResidueType to be added to the composition
	 * @param min The number of minimum count for the given residue
	 * @param max The number of maximum count for the given residue
	 */
	public void setResidueCount(ResidueType res, int min, int max) {
		List<Integer> lCount = new ArrayList<>();
		lCount.add(min);
		lCount.add(max);
		this.m_mapResidueToMaxAndMin.put(res, lCount);
	}

	public List<ResidueType> getResidues() {
		List<ResidueType> lRess = new ArrayList<>();
		for ( ResidueType res : this.m_mapResidueToMaxAndMin.keySet() ) {
			lRess.add(res);
		}
		return lRess;
	}

	public int getMinCountForResidue(ResidueType res) {
		return this.m_mapResidueToMaxAndMin.get(res).get(0);
	}

	public int getMaxCountForResidue(ResidueType res) {
		return this.m_mapResidueToMaxAndMin.get(res).get(1);
	}

	public void setThresholdMass(double dMass) {
		this.m_dThresholdMass = dMass;
	}

	public double getThresholdMass() {
		return this.m_dThresholdMass;
	}
}
