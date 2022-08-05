package org.grits.toolbox.ms.annotation.glycan.composition.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.Composition;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;

public class CompositionGenerator implements Iterator<Composition> {

	private Composition m_compMin;
	private Composition m_compMax;
	private double m_dMassThreshold;

	private List<ResidueType> m_lRess;

	private Composition m_compCurrent;
	private int m_iCurrent;
	private List<int[]> m_lIndices;

	public CompositionGenerator(Composition compMin, Composition compMax) {
		this.m_compMin = compMin;
		this.m_compMax = compMax;
		this.m_dMassThreshold = Double.MAX_VALUE;
		initialize();
	}

	private void initialize() {
		// Create possible combinations
		this.m_lRess = new ArrayList<>();
		this.m_lRess.addAll(this.m_compMax.getResidueTypes());
		int nIndices = this.m_lRess.size();
		int[] indicesMin = new int[nIndices];
		// Set first indices
		for ( int i=0; i<nIndices; i++ ) {
			indicesMin[i] = 0;
			if ( this.m_compMin == null ||this.m_compMin.isEmpty() )
				continue;
			indicesMin[i] = this.m_compMin.getNumberOfResidue(this.m_lRess.get(i));
			if ( indicesMin[i] < 0 )
				indicesMin[i] = 0;
		}
		List<int[]> lIndices = new ArrayList<>();
		lIndices.add(indicesMin);
		for ( int i=0; i<nIndices; i++ ) {
			int nMax = this.m_compMax.getNumberOfResidue(this.m_lRess.get(i));
			nMax -= indicesMin[i];
			List<int[]> lNewIndices = new ArrayList<>();
			for ( int j=0; j<=nMax; j++ ) {
				for ( int[] oldIndices : lIndices ) {
					// Create a copy of indices
					int[] newIndices = new int[nIndices];
					for ( int k=0; k<nIndices; k++ )
						newIndices[k] = oldIndices[k];
					newIndices[i] += j;
					lNewIndices.add(newIndices);
				}
			}
			lIndices = lNewIndices;
		}
		this.m_lIndices = lIndices;

		this.m_iCurrent = -1;
	}

	public void setMassThreshold(double dMass) {
		this.m_dMassThreshold = dMass;
	}

	public double getMassThreshold() {
		return this.m_dMassThreshold;
	}

	@Override
	public boolean hasNext() {
		int iOld = this.m_iCurrent;
		this.m_iCurrent++;
		updateCurrent();
		boolean result = (this.m_compCurrent != null);
		this.m_iCurrent = iOld;
		return result;
	}

	@Override
	public Composition next() {
		this.m_iCurrent++;
		updateCurrent();
		return this.m_compCurrent;
	}

	private void updateCurrent() {
		this.m_compCurrent = null;
		while (this.m_iCurrent < this.m_lIndices.size()) {
			int[] indicesCurrent = this.m_lIndices.get(this.m_iCurrent);
			Composition composition = new Composition();
			composition.setMassOptions(
					this.m_compMin.isMonoisotopicMass(),
					this.m_compMin.getPerderivatizationType()
				);
			for ( int i=0; i<this.m_lRess.size(); i++ ) {
				int count = indicesCurrent[i];
				if ( count == 0 )
					continue;
				ResidueType res = this.m_lRess.get(i);
				composition.addResidues(res, count);
			}
			if ( composition.computeMass() > this.m_dMassThreshold ) {
				this.m_iCurrent++;
				continue;
			}
			this.m_compCurrent = composition;
			break;
		}
	}

	public Composition getCurrent() {
		return this.m_compCurrent;
	}
}
