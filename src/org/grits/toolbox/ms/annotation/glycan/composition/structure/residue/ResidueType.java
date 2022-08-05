package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

import java.util.List;

public abstract class ResidueType implements Comparable<ResidueType> {

	private String m_strName;
	private List<String> m_lSynonyms;
	private String m_strDesc;

	protected String m_strComposition;
	protected double m_dMonoisotopicMass;
	protected double m_dAverageMass;

	protected int m_nMethyl;
	protected int m_nAcetyl;
	protected int m_nLinkages;
	protected boolean m_bIsAcid;

	public ResidueType(String name, List<String> synonyms, String desc) {
		this.m_strName = name;
		this.m_lSynonyms = synonyms;
		this.m_strDesc = desc;

		this.m_strComposition = "?";
		this.m_dAverageMass = 0d;
		this.m_dMonoisotopicMass = 0d;

		this.m_nMethyl = 1;
		this.m_nAcetyl = 1;
		this.m_nLinkages = 1;
		this.m_bIsAcid = false;
	}

	/**
	 * Returns the name of the residue type
	 */
	public String getName() {
		return this.m_strName;
	}

	/**
	 * Returns the set of possible synonyms for this residue type.
	 */
	public List<String> getSynonyms() {
		return this.m_lSynonyms;
	}

	/**
	 * Returns the atomic composition associated with this residue type.
	 */
	public String getComposition() {
		return this.m_strComposition;
	}

	/**
	 * Returns the atomic composition associated with this residue type.
	 */
	public double getAverageMass() {
		return this.m_dAverageMass;
	}

	/**
	 * Returns the atomic composition associated with this residue type.
	 */
	public double getMonoisotopicMass() {
		return this.m_dMonoisotopicMass;
	}

	/**
	 * Return the number of positions available for permethylation.
	 */
	public int getNumMethylations() {
		return this.m_nMethyl;
	}

	/**
	 * Returns the number of positions available for peracetylation.
	 */
	public int getNumAcethylations() {
		return this.m_nAcetyl;
	}

	/**
	 * Returns the maximum number of available linkage position.
	 */
	public int getMaxLinkages() {
		return this.m_nLinkages;
	}

	/**
	 * Returns true if the substituent type is acidic.
	 */
	public boolean isAcid() {
		return this.m_bIsAcid;
	}

	/**
	 * Returns the description of this residue type.
	 */
	public String getDescription() {
		return this.m_strDesc;
	}

	/**
	 * Calculates composition, average mass and exact mass, using Molecule object
	 */
	protected abstract void computeMolecule();

	@Override
	public String toString() {
		return this.m_strName;
	}

	@Override
	public int compareTo(ResidueType o) {
		return this.m_strName.compareTo(o.m_strName);
	}

}
