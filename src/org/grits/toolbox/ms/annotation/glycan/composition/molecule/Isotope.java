package org.grits.toolbox.ms.annotation.glycan.composition.molecule;

import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.TextUtils;

/**
 * Contains all information about an isotope.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class Isotope {

	private String symbol;
	private String atom_symbol;
	private boolean is_stable;
	private int atomic_number;
	private double mass;
	private double abundance;

	/**
	 * Create a new object by parsing the information from a initialization string.
	 * 
	 * @param init initialization string composed of 6 tab separated values:
	 *             chemical symbol of the isotope, chemical symbol of the atom,
	 *             atomic number, <code>true</code> if the isotope is stable, mass,
	 *             relative abundance.
	 * @throws Exception if the initialization string is not the correct format
	 */

	public Isotope(String init) throws DictionaryParserException {
		List<String> tokens = TextUtils.tokenize(init, "\t");
		if (tokens.size() != 6)
			throw new DictionaryParserException("Invalid line: " + init);

		symbol = tokens.get(0);
		atom_symbol = tokens.get(1);
		atomic_number = Integer.valueOf(tokens.get(2));
		is_stable = tokens.get(3).equals("1") || tokens.get(3).equals("true") || tokens.get(3).equals("yes");
		mass = Double.valueOf(tokens.get(4));
		abundance = Double.valueOf(tokens.get(5));
	}

	/**
	 * Return <code>true</code> if the two objects represent the same isotope.
	 */
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Isotope))
			return false;
		return this.symbol.equals(((Isotope) o).symbol);
	}

	/**
	 * Lexicographic comparison of the chemical symbols of the two isotopes.
	 */
	public int compareTo(Isotope a) {
		if (a == null)
			return 1;
		return this.symbol.compareTo(a.symbol);
	}

	/**
	 * Return the chemical symbol of this isotope.
	 */

	public String getSymbol() {
		return symbol;
	}

	/**
	 * Return the chemical symbol of the atom represented by this isotope.
	 */

	public String getAtomSymbol() {
		return atom_symbol;
	}

	/**
	 * Return the atomic number.
	 */
	public int getAtomicNumber() {
		return atomic_number;
	}

	/**
	 * Return <code>true</code> if the isotope is stable.
	 */
	public boolean isStable() {
		return is_stable;
	}

	/**
	 * Return the mass of the isotope.
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * Return the relative abundance of this isotope.
	 */

	public double getAbundance() {
		return abundance;
	}
}