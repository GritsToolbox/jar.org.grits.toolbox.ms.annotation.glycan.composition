package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment;

public interface IFragmentType {
	public ICleavageType getCleavageType();

	public double getMonoisotopicMass();
	public double getAverageMass();

	public int getMaxLinkages();
	public int getNumMethylations();
	public int getNumAcethylations();
}
