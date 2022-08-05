package org.grits.toolbox.ms.annotation.glycan.composition.molecule;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.glycan.composition.file.FileConstant;
import org.grits.toolbox.ms.annotation.glycan.composition.file.TextFileUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.TextUtils;

/**
 * Utility class holding the information about atoms and isotopes.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class MoleculeUtils {
	private static final Logger logger = Logger.getLogger(MoleculeUtils.class);

	// ---- static variables

	private static HashMap<String, Atom> atoms;
	private static HashMap<Atom, Isotope> atom_main_isotope;
	private static HashMap<Atom, List<Isotope>> atom_other_isotopes;
	private static HashMap<Atom, List<Isotope>> atom_all_isotopes;

	/** An atom of hydrogen (H). */
	public static Atom hydrogen;

	/** An electon (e). */
	public static Atom electron;

	/** A molecule of water (H2O). */
	public static Molecule water;

	/** A molecule of hydrogen (H2). */
	public static Molecule hydrogen_mol;

	/** A proton (H+). */
	public static Molecule h_ion;

	/** A lithium ion (Li+). */
	public static Molecule li_ion;

	/** A sodium ion (Na+). */
	public static Molecule na_ion;

	/** A potassiom ion (K+). */
	public static Molecule k_ion;

	static {
		atoms = new HashMap<String, Atom>();
		atom_main_isotope = new HashMap<Atom, Isotope>();
		atom_other_isotopes = new HashMap<Atom, List<Isotope>>();
		atom_all_isotopes = new HashMap<Atom, List<Isotope>>();

		loadAtoms(FileConstant.ATOMS_FILE);
		loadIsotopes(FileConstant.ISOTOPES_FILE);

		try {
			water = Molecule.parse("H2O");
			hydrogen_mol = Molecule.parse("H2");
			hydrogen = getAtom("H");
			electron = getAtom("e");

			h_ion = Molecule.parse("H+");
			li_ion = Molecule.parse("Li+");
			na_ion = Molecule.parse("Na+");
			k_ion = Molecule.parse("K+");
		} catch (DictionaryException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private MoleculeUtils() {
	}

	/**
	 * Load all information about atoms from a configuration file.
	 */
	private static void loadAtoms(String filename) {
		try {
			List<String> lines = TextFileUtils.getLines(filename);
			if ( lines == null )
				return;
			
			for ( String line : lines ) {
				line = TextUtils.trim(line);
				Atom atom = new Atom(line);
				atoms.put(atom.getSymbol(), atom);
			}
		} catch (DictionaryParserException e) {
			logger.error(e);
			atoms.clear();
		}
	}

	/**
	 * Load all information about isotopes from a configuration file.
	 */
	private static void loadIsotopes(String filename) {
		try {
			List<String> lines = TextFileUtils.getLines(filename);
			if ( lines == null )
				return;
			
			for ( String line : lines ) {
				line = TextUtils.trim(line);
				Isotope isotope = new Isotope(line);

				if (atoms.get(isotope.getAtomSymbol()) == null)
					throw new DictionaryException("Invalid atom name: " + isotope.getAtomSymbol());

				if (isotope.isStable()) {
					Atom atom = atoms.get(isotope.getAtomSymbol());
					if (atom_all_isotopes.get(atom) == null)
						atom_all_isotopes.put(atom, new LinkedList<Isotope>());
					atom_all_isotopes.get(atom).add(isotope);

					// Add as atom
					Atom isotopeAtomObj=new Atom(atom.getSymbol()+"^"+isotope.getAtomicNumber()+"\t"+atom.getName()+"("+isotope.getAtomicNumber()+")"+"\t"+isotope.getMass()+"\t"+isotope.getMass());
					atoms.put(isotopeAtomObj.getSymbol(), isotopeAtomObj);
				}
			}

			// classify isotopes
			for (Map.Entry<Atom, List<Isotope>> e : atom_all_isotopes.entrySet()) {
				Atom atom = e.getKey();
				List<Isotope> isotopes = e.getValue();

				// find main isotope
				Isotope main_isotope = isotopes.get(0);
				for (Isotope isotope : isotopes) {
					if (isotope.getAbundance() > main_isotope.getAbundance())
						main_isotope = isotope;
				}
				atom_main_isotope.put(atom, main_isotope);

				// collect other isotopes
				LinkedList<Isotope> other_isotopes = new LinkedList<Isotope>();
				for (Isotope isotope : isotopes) {
					if (isotope != main_isotope)
						other_isotopes.add(isotope);
				}
				atom_other_isotopes.put(atom, other_isotopes);

				// put main isotope in first position
				LinkedList<Isotope> all_isotopes = (LinkedList<Isotope>) atom_all_isotopes.get(atom);
				all_isotopes.remove(main_isotope);
				all_isotopes.addFirst(main_isotope);
			}
		} catch (DictionaryException e) {
			logger.error(e);
			atoms.clear();
		}
	}

	// --- methods

	/**
	 * Return the atom of type <code>atom_name</code>.
	 * 
	 * @throws DictionaryException if the atom do not exists
	 */
	public static Atom getAtom(String atom_name) throws DictionaryException {
		Atom ret = atoms.get(atom_name);
		if (ret == null)
			throw new DictionaryException("Invalid atom " + atom_name);
		return ret;
	}

	/**
	 * Returns 
	 * @param isotope_name
	 * @return
	 * @throws DictionaryException
	 */
	public static Isotope getIsotope(String isotope_name) throws DictionaryException {
		if ( !isotope_name.contains("^") )
			return getMainIsotope(getAtom(isotope_name));
		String[] _isotope = isotope_name.split("^");
		Atom atom = getAtom(_isotope[0]);
		try {
			int num = Integer.parseInt(_isotope[1]);
			for ( Isotope isotope : getAllIsotopes(atom) ) {
				if ( isotope.getAtomicNumber() == num )
					return isotope;
			}
		} catch (NumberFormatException e) {
		}
		throw new DictionaryParserException("Invalid isotope number "+_isotope[1]);
	}

	/**
	 * Return the main isotope for the atom <code>atom</code>.
	 */
	public static Isotope getMainIsotope(Atom atom) {
		if (atom == null)
			return null;
		return atom_main_isotope.get(atom);
	}

	/**
	 * Return all possible isotopes for the atom <code>atom</code>.
	 */
	public static List<Isotope> getAllIsotopes(Atom atom) {
		if (atom == null)
			return null;
		return atom_all_isotopes.get(atom);
	}

}
