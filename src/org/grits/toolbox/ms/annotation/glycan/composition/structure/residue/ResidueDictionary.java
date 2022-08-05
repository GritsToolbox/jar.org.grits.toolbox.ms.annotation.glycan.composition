package org.grits.toolbox.ms.annotation.glycan.composition.structure.residue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.glycan.composition.file.FileConstant;
import org.grits.toolbox.ms.annotation.glycan.composition.file.TextFileUtils;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType.CoreModification;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType.Substituent;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.TextUtils;

/**
 * Class for the dictionary of residues loaded from the configuration file. The
 * residues contains MonosaccharideTypes, SubstituentTypes, and
 * ReducingEndTypes. These residue types must be loaded using
 * {@link #loadMonosaccharideTypes(String)},
 * {@link #loadSubstituentTypes(String)}, and
 * {@link #loadReducingEndTypes(String)}, respectively. The default dictionaries
 * can be loaded using {@link #loadDefaultDictionaries()}.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class ResidueDictionary {
	private static final Logger logger = Logger.getLogger(ResidueDictionary.class);

	static {
		initialize();
	}

	private static HashMap<String, MonosaccharideType> mapNameToMSs;
	private static HashMap<String, SubstituentType> mapNameToSubsts;
	private static HashMap<String, SubstituentType> mapNameToRedEnds;

	/**
	 * Initializes all dictionaries
	 */
	public static void initialize() {
		mapNameToMSs = new HashMap<>();
		mapNameToSubsts = new HashMap<>();
		mapNameToRedEnds = new HashMap<>();
	}

	/**
	 * Loads default dictionaries for substituents, monosaccharides, and reducing
	 * ends.
	 * 
	 * @see #loadSubstituentTypes(String)
	 * @see #loadMonosaccharideTypes(String)
	 * @see #loadSubstituentTypes(String)
	 */
	public static void loadDefaultDictionaries() {
		ResidueDictionary.loadSubstituentTypes(FileConstant.SUBSTITUENT_TYPES_FILE);
		ResidueDictionary.loadMonosaccharideTypes(FileConstant.MONOSACCHARIDE_TYPES_FILE);
		ResidueDictionary.loadReducingEndTypes(FileConstant.REDUCING_END_TYPES_FILE);
	}

	/**
	 * Returns the residue type with the given name
	 * 
	 * @see SubstituentType
	 * @throws DictionaryException if there is no residue type with such a name
	 */
	public static ResidueType getResidueType(String strType) throws DictionaryException {
		ResidueType ret = findResidueType(strType);
		if (ret == null)
			throw new DictionaryException("Invalid residue type: <" + strType + ">");
		return ret;
	}

	/**
	 * Returns the residue type with the given name or {@code null} otherwise
	 * 
	 * @see ResidueType
	 */
	public static ResidueType findResidueType(String strType) {
		ResidueType res = findMonosaccharideType(strType);
		if (res != null)
			return res;
		res = findSubstituentType(strType);
		if (res != null)
			return res;
		res = findReducingEndType(strType);
		if (res != null)
			return res;
		return null;
	}

	// ------------------------------
	// For reducing end types

	/**
	 * Returns all reducing ends.
	 */
	public static Collection<SubstituentType> getReducingEnds() {
		Set<SubstituentType> setTypes = new TreeSet<>();
		setTypes.addAll(mapNameToRedEnds.values());
		return setTypes;
	}

	/**
	 * Returns the reducing end type with the given name
	 * 
	 * @see SubstituentType
	 * @throws DictionaryException if there is no reducing end type with such a name
	 */
	public static SubstituentType getReducingEndType(String strType) throws DictionaryException {
		SubstituentType ret = findReducingEndType(strType);
		if (ret == null)
			throw new DictionaryException("Invalid reducing end type: <" + strType + ">");
		return ret;
	}

	/**
	 * Returns the substituent type with the given name or {@code null} otherwise
	 * 
	 * @see SubstituentType
	 */
	public static SubstituentType findReducingEndType(String strType) {
		SubstituentType ret = mapNameToRedEnds.get(strType.toLowerCase());
		return ret;
	}

	/**
	 * Loads reducing end types from the given file into reducing end dictionary.
	 * 
	 * @param filename the file name containing reducing ends
	 */
	public static void loadReducingEndTypes(String filename) {
		try {
			List<SubstituentType> types = loadSubstituentTypeDictionary(filename, true);
			for (SubstituentType type : types) {
				mapNameToRedEnds.put(type.getName().toLowerCase(), type);
				for (String s : type.getSynonyms())
					mapNameToRedEnds.put(s.toLowerCase(), type);
			}
		} catch (DictionaryException e) {
			logger.error(e);
			mapNameToRedEnds.clear();
		}
	}

	// ------------------------------
	// For substituent types

	/**
	 * Returns all substituents.
	 */
	public static Collection<SubstituentType> getSubstituents() {
		Set<SubstituentType> setTypes = new TreeSet<>();
		setTypes.addAll(mapNameToSubsts.values());
		return setTypes;
	}

	/**
	 * Returns the substituent type with the given name
	 * 
	 * @see SubstituentType
	 * @throws DictionaryException if there is no substituent type with such a name
	 */
	public static SubstituentType getSubstituentType(String strType) throws DictionaryException {
		SubstituentType ret = findSubstituentType(strType);
		if (ret == null)
			throw new DictionaryException("Invalid subsituent type: <" + strType + ">");
		return ret;
	}

	/**
	 * Returns the substituent type with the given name or {@code null} otherwise
	 * 
	 * @see SubstituentType
	 */
	public static SubstituentType findSubstituentType(String strType) {
		SubstituentType ret = mapNameToSubsts.get(strType.toLowerCase());
		return ret;
	}

	/**
	 * Loads substituent types from the given file into substituent dictionary.
	 * 
	 * @param filename the file name containing substituents
	 */
	public static void loadSubstituentTypes(String filename) {
		try {
			List<SubstituentType> types = loadSubstituentTypeDictionary(filename, false);
			for (SubstituentType type : types) {
				mapNameToSubsts.put(type.getName().toLowerCase(), type);
				for (String s : type.getSynonyms())
					mapNameToSubsts.put(s.toLowerCase(), type);
			}
		} catch (DictionaryException e) {
			logger.error(e);
			mapNameToSubsts.clear();
		}
	}

	private static List<SubstituentType> loadSubstituentTypeDictionary(String filename, boolean bIsRedEnd)
			throws DictionaryParserException {
		List<SubstituentType> lTypes = new ArrayList<>();

		List<String> lines = TextFileUtils.getLines(filename);
		if (lines == null)
			return lTypes;
		for (String line : lines)
			lTypes.add(parseSubstituentType(line, bIsRedEnd));

		return lTypes;
	}

	private static SubstituentType parseSubstituentType(String line, boolean bIsRedEnd)
			throws DictionaryParserException {
		List<String> tokens = TextUtils.tokenize(line, "\t");

		if (tokens.size() != 10)
			throw new DictionaryParserException("Invalid line for substituent type: " + line);

		String name = tokens.get(0);
		List<String> synonyms = TextUtils.parseStringArray(tokens.get(1));
		String composition = tokens.get(2);
		int nMe = TextUtils.parseInteger(tokens.get(3));
		Boolean bDropMe = TextUtils.parseBoolean(tokens.get(4));
		int nAc = TextUtils.parseInteger(tokens.get(5));
		Boolean bDropAc = TextUtils.parseBoolean(tokens.get(6));
		int nLink = TextUtils.parseInteger(tokens.get(7));
		Boolean bIsAcid = TextUtils.parseBoolean(tokens.get(8));
		String desc = tokens.get(9);

		if (bDropMe == null || bDropAc == null || bIsAcid == null)
			throw new DictionaryParserException("Invalid boolean value: " + line);

		return new SubstituentType(name, synonyms, composition, nMe, bDropMe, nAc, bDropAc, nLink, bIsAcid, bIsRedEnd,
				desc);
	}

	// ------------------------------
	// For monosaccharide types

	/**
	 * Returns all monosaccharides.
	 */
	public static Collection<MonosaccharideType> getMonosaccharides() {
		Set<MonosaccharideType> setTypes = new TreeSet<>();
		setTypes.addAll(mapNameToMSs.values());
		return setTypes;
	}

	/**
	 * Returns the monosaccharide type with the given name
	 * 
	 * @see MonosaccharideType
	 * @throws DictionaryException if there is no monosaccharide type with the given
	 *                             name
	 */
	public static MonosaccharideType getMonosaccharideType(String strType) throws DictionaryException {
		MonosaccharideType ret = findMonosaccharideType(strType);
		if (ret == null)
			throw new DictionaryException("Invalid monosaccharide type: <" + strType + ">");
		return ret;
	}

	/**
	 * Returns the monosaccharide type with the given name or {@code null} otherwise
	 * 
	 * @see MonosaccharideType
	 */
	public static MonosaccharideType findMonosaccharideType(String strType) {
		MonosaccharideType ret = mapNameToMSs.get(strType.toLowerCase());
		return ret;
	}

	/**
	 * Loads MonosaccharideTypes listed in the given file into monosaccharide
	 * dictionary. If monosaccharides in the file have substituents, the
	 * substituents must be loaded in advance from the file which identify them
	 * using {@link #loadSubstituentTypes(String)}.
	 * 
	 * @param filename the file name containing substituents
	 */
	public static void loadMonosaccharideTypes(String filename) {
		try {
			List<MonosaccharideType> mss = loadMonosaccharideTypeDictionary(filename);
			for (MonosaccharideType type : mss) {
				mapNameToMSs.put(type.getName().toLowerCase(), type);
				for (String s : type.getSynonyms())
					mapNameToMSs.put(s.toLowerCase(), type);
			}
		} catch (DictionaryException e) {
			logger.error(e);
			mapNameToSubsts.clear();
		}
	}

	private static List<MonosaccharideType> loadMonosaccharideTypeDictionary(String filename)
			throws DictionaryException {
		List<MonosaccharideType> lTypes = new ArrayList<>();

		List<String> lines = TextFileUtils.getLines(filename);
		if (lines == null)
			return lTypes;
		for (String line : lines) {
			MonosaccharideType type = parseMonosaccharideType(line);
			// TODO: need validation for monosaccharide type
			lTypes.add(type);
		}

		return lTypes;
	}

	private static MonosaccharideType parseMonosaccharideType(String line) throws DictionaryException {
		List<String> tokens = TextUtils.tokenize(line, "\t");

		if (tokens.size() < 8)
			throw new DictionaryParserException("Invalid line for monosaccharide type: " + line);

		String name = tokens.get(0);
		List<String> synonyms = TextUtils.parseStringArray(tokens.get(1));
		int iCLength = TextUtils.parseInteger(tokens.get(2));
		int iAnomPos = TextUtils.parseInteger(tokens.get(3));
		int iRingSize = TextUtils.parseInteger(tokens.get(4));
		AnomerType anomer = (iRingSize > 2) ? AnomerType.x : AnomerType.o;
		List<CoreModification> lMods = parseModifications(tokens.get(5));
		List<Substituent> lSubsts = parseSubstituentsInMonosaccharideType(tokens.get(6));
		String desc = tokens.get(7);

		return new MonosaccharideType(name, synonyms, iCLength, anomer, iAnomPos, iRingSize, lMods, lSubsts, desc);
	}

	private static List<CoreModification> parseModifications(String strMods) throws DictionaryParserException {
		List<CoreModification> lMods = new ArrayList<>();
		if (strMods == null || strMods.equals("-"))
			return lMods;

		String[] tokens = strMods.split(",");
		for (String token : tokens) {
			String[] strMod = token.split(":");
			int pos = TextUtils.parseInteger(strMod[0]);
			CoreModificationType type = CoreModificationType.forName(strMod[1]);
			if (type == null)
				throw new DictionaryParserException("The modification type is not available.");
			lMods.add(new CoreModification(type, pos));
		}

		return lMods;
	}

	private static List<Substituent> parseSubstituentsInMonosaccharideType(String strSubsts)
			throws DictionaryException {
		List<Substituent> lSubsts = new ArrayList<>();
		if (strSubsts == null || strSubsts.equals("-"))
			return lSubsts;

		String[] tokens = strSubsts.split(",");
		for (String token : tokens) {
			String[] strMod = token.split(":");
			int pos = Integer.valueOf(strMod[0]);
			SubstituentType type = getSubstituentType(strMod[1]);
			lSubsts.add(new Substituent(type, pos));
		}

		return lSubsts;
	}
}
