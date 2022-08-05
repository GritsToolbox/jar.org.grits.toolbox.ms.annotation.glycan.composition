package org.grits.toolbox.ms.annotation.glycan.composition.molecule;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;

/**
 * This object represent a molecule as a collection of atoms and charges.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class Molecule {

	private static Pattern mol_pattern;
	static {
		mol_pattern = Pattern.compile("([A-Z][a-z]?)([0-9]*)(\\^[0-9]+)?");
	}

	private TreeMap<Atom, Integer> atoms;
	private double main_mass;
	private double avg_mass;
	private int charges;

	/**
	 * Create an empty molecule.
	 */
	public Molecule() {
		atoms = new TreeMap<Atom, Integer>();
		main_mass = 0.;
		avg_mass = 0.;
		charges = 0;
	}

	/**
	 * Returns a molecule parsed from its chemical formula.
	 * 
	 * @param init the chemical formula
	 * @throws DictionaryException 
	 */
	public static Molecule parse(String init) throws DictionaryException {
		Molecule mol = new Molecule();

		if (init.equals("0"))
			init = "";

		int mul = 1;
		if (init.startsWith("-")) {
			mul = -1;
			init = init.substring(1);
		}

		int cur = 0;
		Matcher m = mol_pattern.matcher(init);
		while (m.find()) {
			String atom_name = m.group(1);
			String atom_number_str = m.group(2);
			String isotope_number_str = m.group(3);
			if(isotope_number_str != null){
				isotope_number_str = isotope_number_str.substring(1, isotope_number_str.length()); 
			}
			
			int num = mul;
			if (atom_number_str != null && atom_number_str.length() > 0) {
				try {
					num *= Integer.valueOf(atom_number_str);
				} catch (NumberFormatException e) { // This never happens
				}
			}
			if(isotope_number_str!=null){
				try {
					mol.addIsotope(atom_name, num, Integer.valueOf(isotope_number_str));
				} catch (NumberFormatException e) { // This never happens
				}
			}else{
				mol.add(atom_name,num);
			}
			cur = m.end();
		}

		int i = cur;
		for (; i < init.length(); i++) {
			if (init.charAt(i) == '-')
				mol.charges--;
			else if (init.charAt(i) == '+')
				mol.charges++;
			else
				break;
		}
		if (i != init.length())
			throw new DictionaryParserException("Invalid format: " + init);

		if (mol.charges > 0) {
			mol.main_mass -= mol.charges * MoleculeUtils.electron.getMonoisotopicMass();
			mol.avg_mass -= mol.charges * MoleculeUtils.electron.getAverageMass();
		}

		return mol;
	}

	/**
	 * Return <code>true</code> if the two molecules have the same chemical formula.
	 */

	public boolean equals(Object other) {
		if (other == null || !(other instanceof Molecule))
			return false;
		return this.toString().equals(other.toString());
	}

	/**
	 * Return an hash code associated with this molecule.
	 */

	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * Create a new object which is a copy of the current one.
	 */

	public Molecule clone() {
		Molecule ret = new Molecule();
		ret.atoms = (TreeMap<Atom, Integer>) this.atoms.clone();
		ret.main_mass = this.main_mass;
		ret.avg_mass = this.avg_mass;
		ret.charges = this.charges;
		return ret;
	}

	/**
	 * Return a collection of entries representing the atoms in this molecule and
	 * their quantities.
	 */

	public Collection<Map.Entry<Atom, Integer>> getAtoms() {
		return atoms.entrySet();
	}

	/**
	 * Return the mass of the molecule in the current settings.
	 */

	public double getMass() {
		return main_mass;
	}

	/**
	 * Return the mono-isotopic mass of the molecule.
	 */

	public double getMonoisotopicMass() {
		return main_mass;
	}

	/**
	 * Return the average mass of the molecule.
	 */
	public double getAverageMass() {
		return avg_mass;
	}

	/**
	 * Return the mass-to-charge ratio of the molecule.
	 */
	public double getMZ() {
		if (charges == 0)
			return getMass();
		return getMass() / Math.abs(charges);
	}

	/**
	 * Return the number of positive charges associated to this molecule.
	 */
	public int getNoCharges() {
		return charges;
	}

	/**
	 * Add <code>num</code> positive charges to the molecule.
	 */
	public void addPositiveCharges(int num) {
		charges += num;
		main_mass -= num * MoleculeUtils.electron.getMonoisotopicMass();
		avg_mass -= num * MoleculeUtils.electron.getAverageMass();
	}

	/**
	 * Remove <code>num</code> positive charges from the molecule.
	 */
	public void removePositiveCharges(int num) {
		addNegativeCharges(num);
	}

	/**
	 * Add <code>num</code> negative charges to the molecule.
	 */
	public void addNegativeCharges(int num) {
		charges -= num;
		main_mass += num * MoleculeUtils.electron.getMonoisotopicMass();
		avg_mass += num * MoleculeUtils.electron.getAverageMass();
	}

	/**
	 * Remove <code>num</code> negative charges to the molecule.
	 */
	public void removeNegativeCharges(int num) {
		addPositiveCharges(num);
	}

	/**
	 * Clone the molecule and add <code>num</code> atoms of type <code>atom</code>
	 * to it.
	 */
	public Molecule and(String atom, int num) throws DictionaryException {
		Molecule ret = this.clone();
		ret.add(atom, num);
		return ret;
	}

	/**
	 * Clone the molecule and add the atom <code>a</code> to it.
	 */
	public Molecule and(Atom a) {
		return this.and(a, 1);
	}

	/**
	 * Clone the molecule and add <code>num</code> instances of the atom
	 * <code>a</code> to it.
	 */
	public Molecule and(Atom a, int num) {
		Molecule ret = this.clone();
		ret.add(a, num);
		return ret;
	}

	/**
	 * Clone the molecule and add the content of molecule <code>m</code> to it.
	 */
	public Molecule and(Molecule m) {
		return this.and(m, 1);
	}

	/**
	 * Clone the molecule and add <code>num</code> times the content of molecule
	 * <code>m</code> to it.
	 */
	public Molecule and(Molecule m, int num) {
		Molecule ret = this.clone();
		ret.add(m, num);
		return ret;
	}

	/**
	 * Add one atom of type <code>atom</code> to the molecule.
	 */
	public void add(String atom) throws DictionaryException {
		add(MoleculeUtils.getAtom(atom), 1);
	}

	/**
	 * Add <code>num</code> atoms of type <code>atom</code> to the molecule.
	 */
	public void add(String atom, int num) throws DictionaryException {
		add(MoleculeUtils.getAtom(atom), num);
	}

	public void addIsotope(String atom, int num, int isotope) throws DictionaryException {
		add(MoleculeUtils.getAtom(atom+"^"+isotope),num);
	}

	/**
	 * Add one instance of atom <code>a</code> to the molecule.
	 */
	public void add(Atom a) {
		add(a, 1);
	}

	/**
	 * Add <code>num</code> instances of atom <code>a</code> to the molecule.
	 */
	public void add(Atom a, int num) {
		Integer cur_num = atoms.get(a);
		if (cur_num == null)
			atoms.put(a, num);
		else
			atoms.put(a, cur_num + num);

		main_mass += num * a.getMonoisotopicMass();
		avg_mass += num * a.getAverageMass();

		if ( atoms.get(a) == 0 )
			atoms.remove(a);
	}

	/**
	 * Add the content of molecule <code>m</code> to the molecule.
	 */
	public void add(Molecule m) {
		add(m, 1);
	}

	/**
	 * Add <code>num</code> times the content of molecule <code>m</code> to the
	 * molecule.
	 */
	public void add(Molecule m, int num) {
		if (m != null) {
			for (Map.Entry<Atom, Integer> a : m.atoms.entrySet())
				this.add(a.getKey(), num * a.getValue());
			this.addPositiveCharges(num * m.charges);
		}
	}

	/**
	 * Remove <code>num</code> atoms of type <code>atom</code> from the molecule.
	 */
	public void remove(String atom, int num) throws DictionaryException {
		add(atom, -num);
	}

	/**
	 * Remove one instance of the atom <code>a</code> from the molecule.
	 */
	public void remove(Atom a) {
		add(a, -1);
	}

	/**
	 * Remove <code>num</code> instances of atom <code>a</code> from the molecule.
	 */
	public void remove(Atom a, int num) {
		add(a, -num);
	}

	/**
	 * Remove the content of molecule <code>m</code> from the molecule.
	 */
	public void remove(Molecule m) {
		add(m, -1);
	}

	/**
	 * Remove <code>num</code> times the content of molecule <code>m</code> from the
	 * molecule.
	 */
	public void remove(Molecule m, int num) {
		add(m, -num);
	}

	/**
	 * Return the chemical formula of the molecule.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Atom, Integer> a : atoms.entrySet()) {
			sb.append(a.getKey().getSymbol());
			sb.append(a.getValue().toString());
		}
		for (int i = 0; i < Math.abs(charges); i++) {
			if (charges > 0)
				sb.append('+');
			else
				sb.append('-');
		}
		return sb.toString();
	}

}