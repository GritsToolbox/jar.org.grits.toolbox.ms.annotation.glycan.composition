package org.grits.toolbox.ms.annotation.glycan.composition.molecule;

import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.TextUtils;

/**
 * Contains all information about a component of a {@link Molecule} object.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class Atom implements Comparable<Atom> {

	private String symbol;
	private String name;
	private double main_mass;
	private double avg_mass;

	/**
	 * Create a new object by parsing the information from a initialization string.
	 * 
	 * @param init initialization string composed of 4 tab separated values:
	 *             chemical symbol, name, mono-isotopic mass, average mass
	 * @throws Exception if the initialization string is not the correct format
	 */
	public Atom(String init) throws DictionaryParserException {
		List<String> tokens = TextUtils.tokenize(init, "\t");
		if (tokens.size() != 4)
			throw new DictionaryParserException("Invalid line: " + init);

		symbol = tokens.get(0);
		name = tokens.get(1);
		main_mass = Double.valueOf(tokens.get(2));
		avg_mass = Double.valueOf(tokens.get(3));
	}

	/**
	 * Return <code>true</code> if the two objects represent the same atom.
	 */

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Atom))
			return false;
		return this.symbol.equals(((Atom) o).symbol);
	}

	/**
	 * Lexicographic comparison of the chemical symbols of the two atoms.
	 */

	public int compareTo(Atom a) {
		if (a == null)
			return 1;
		return this.symbol.compareTo(a.symbol);
	}

	/**
	 * Return the chemical symbol of this atom.
	 */

	public String getSymbol() {
		return symbol;
	}

	/**
	 * Return the name of this atom.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the mass of this atom given the current isotopic settings.
	 */

	public double getMass() {
		return main_mass;
	}

	/**
	 * Return the mono-isotopic mass of this atom.
	 */

	public double getMonoisotopicMass() {
		return main_mass;
	}

	/**
	 * Return the average mass of this atom.
	 */

	public double getAverageMass() {
		return avg_mass;
	}
}