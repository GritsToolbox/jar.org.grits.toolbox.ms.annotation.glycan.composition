package org.grits.toolbox.ms.annotation.glycan.composition.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.CustomSubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.MonosaccharideType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.ResidueType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.SubstituentType;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.FragmentDictionary;
import org.grits.toolbox.ms.annotation.glycan.composition.structure.residue.fragment.IFragmentType;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryException;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.DictionaryParserException;

public class CompositionUtils {

	public static Composition parse(String strComposition) throws DictionaryException {
		if (strComposition.contains("#"))
			return parseFragment(strComposition);
		return parseComposition(strComposition);
	}

	private static Composition parseComposition(String strComposition) throws DictionaryException {
		Composition comp = new Composition();

		if (strComposition.contains("--")) {
			String[] tokens = strComposition.split("--");
			if (tokens.length > 2)
				throw new DictionaryParserException("Invalid format for composition.");
			strComposition = tokens[0];
			comp.setReducingEnd(parseReducingEnd(tokens[1]));
		}

		String[] tokens = strComposition.split(",");
		for (String token : tokens) {
			String[] strResCount = token.split(":");
			ResidueType res = parseResidue(strResCount[0]);
			int num = 1;
			try {
				if (strResCount.length > 1)
					num = Integer.valueOf(strResCount[1]);
			} catch (NumberFormatException e) {
				throw new DictionaryParserException("The number of residues must be positive integer.");
			}
			comp.addResidues(res, num);
		}

		return comp;
	}

	private static CompositionFragment parseFragment(String strComposition) throws DictionaryException {
		CompositionFragment frag = new CompositionFragment();

		String strLeaf = "";
		String strCore = "";
		String strRoot = "";
		String[] tokens = strComposition.split("--");
		if (tokens.length > 3)
			throw new DictionaryParserException("Invalid format for composition fragment.");
		if (tokens[0].contains("#")) {
			strLeaf = tokens[0];
			if (tokens.length > 1) {
				if (tokens[1].contains("#"))
					strRoot = tokens[1];
				else {
					strCore = tokens[1];
					if (tokens.length > 2)
						strRoot = tokens[2];
				}
			}
		} else {
			if (tokens.length != 2)
				throw new DictionaryParserException("Invalid format for composition fragment.");
			strCore = tokens[0];
			strRoot = tokens[1];
		}

		if (!strRoot.isEmpty()) {
			if (strRoot.contains("#")) {
				checkCleavedMonosaccharide(strRoot);
				IFragmentType fragRoot = FragmentDictionary.getFragmentType(strRoot);
				frag.addFragment(fragRoot);
			} else {
				SubstituentType type = parseReducingEnd(strRoot);
				frag.setReducingEnd(type);
			}
		}

		if (!strCore.isEmpty()) {
			tokens = strCore.split(",");
			for (String token : tokens) {
				String[] strResCount = token.split(":");
				ResidueType res = parseResidue(strResCount[0]);
				int num = 1;
				try {
					if (strResCount.length > 1)
						num = Integer.valueOf(strResCount[1]);
				} catch (NumberFormatException e) {
					throw new DictionaryParserException("The number of residues must be positive integer.");
				}
				frag.addResidues(res, num);
			}
		}

		if (!strLeaf.isEmpty()) {
			tokens = strLeaf.split(",");
			for (String token : tokens) {
				String[] strResCount = token.split(":");
				checkCleavedMonosaccharide(strResCount[0]);
				IFragmentType res = FragmentDictionary.getFragmentType(strResCount[0]);
				int num = 1;
				try {
					if (strResCount.length > 1)
						num = Integer.valueOf(strResCount[1]);
				} catch (NumberFormatException e) {
					throw new DictionaryParserException("The number of fragments must be positive integer.");
				}
				frag.addLeafFragments(res, num);
			}
		}

		return frag;
	}

	private static void checkCleavedMonosaccharide(String strFrag) throws DictionaryException {
		if (!strFrag.contains("#"))
			throw new DictionaryParserException("Invalid fragment fromat: <" + strFrag + ">");

		if (strFrag.contains("Sugar"))
			return;
		String strMS = strFrag.substring(0, strFrag.indexOf("#"));
		MonosaccharideType msType = ResidueDictionary.getMonosaccharideType(strMS);
		FragmentDictionary.generateAllMonosaccharideFragments(msType);
	}

	private static ResidueType parseResidue(String strRes) throws DictionaryException {
		ResidueType res;
		if (strRes.contains("="))
			res = parseCustomSubstituent(strRes, false);
		else
			res = ResidueDictionary.getResidueType(strRes);
		return res;
	}

	private static SubstituentType parseReducingEnd(String strRes) throws DictionaryException {
		SubstituentType type;
		if (strRes.contains("="))
			type = parseCustomSubstituent(strRes, true);
		else
			type = ResidueDictionary.getReducingEndType(strRes);
		return type;
	}

	private static CustomSubstituentType parseCustomSubstituent(String strRes, boolean isRedEnd)
			throws DictionaryParserException {
		String[] custom = strRes.split("=");
		if (custom.length != 2)
			throw new DictionaryParserException("Invalid custom substituent type: <" + strRes + ">");
		try {
			return new CustomSubstituentType(custom[0], Double.parseDouble(custom[1]), isRedEnd);
		} catch (NumberFormatException e) {
			throw new DictionaryParserException("The mass value of custom substituent must be positive double.");
		}
	}

	/**
	 * Generates compositions which are substructures of the {@code maxComposition}
	 * and superstructures of {@code minComposition}. (For the correct calculation,
	 * the {@code minComposition} must be a substructure of {@code maxComposition}.)
	 * The resulting compositions are filtered with the given mass threshold value.
	 * The mass options of all generating compositions will be reset with the ones
	 * of minComposition.
	 * 
	 * @param minComposition Composition with a minimum residue set. This will be
	 *                       the smallest composition in the result. ({@code null}
	 *                       if no minimum)
	 * @param maxComposition Composition with a maximum residue set. This will be
	 *                       the biggest composition in the result.
	 * @param dMassThreshold double value of mass threshold
	 * @return List of Compositions based on the given maximum and minimum residue
	 *         set
	 */
	public static List<Composition> generateCompositions(Composition minComposition, Composition maxComposition,
			double dMassThreshold) {
		if (maxComposition == null || maxComposition.isEmpty())
			return new ArrayList<>();

		double minMass = 0;
		if (minComposition != null && !minComposition.isEmpty())
			minMass = minComposition.computeMass();

		// No result if the mass of minimum composition exceeds the threshold
		if (minMass > dMassThreshold)
			return new ArrayList<>();

		Composition compositionMax = maxComposition.copy();
		if (minComposition != null && !minComposition.isEmpty()) {
			compositionMax.setMassOptions(minComposition.isMonoisotopicMass(),
					minComposition.getPerderivatizationType());
			// Reduce residues in min composition from max composition.
			for (ResidueType res : compositionMax.getResidueTypes()) {
				int count = minComposition.getNumberOfResidue(res);
				if (count == -1)
					continue;
				compositionMax.addResidues(res, -count);
			}
		}

		// Generate substructures
		List<Composition> lCompositions = generateSubstructures(compositionMax);
		lCompositions.add(compositionMax);

		// Add residues in min composition to each generated substructure
		if (minComposition != null && !minComposition.isEmpty()) {
			for (ResidueType res : minComposition.getResidueTypes()) {
				int count = minComposition.getNumberOfResidue(res);
				for (Composition sub : lCompositions)
					sub.addResidues(res, count);
			}
			lCompositions.add(minComposition.copy());
		}

		// Filter out with mass
		List<Composition> lFilteredCompositions = new ArrayList<>();
		for (Composition composition : lCompositions) {
			if (composition.computeMass() > dMassThreshold)
				continue;
			lFilteredCompositions.add(composition);
		}

		return lFilteredCompositions;
	}

	public static List<Composition> generateSubstructuresForReducingEnd(Composition composition) {
		List<Composition> lSubstructures = new ArrayList<>();
		lSubstructures.addAll(generateSubstructures(composition));

		// Reducing end
		if (composition.getReducingEnd() == null)
			return lSubstructures;

		Composition substructure = composition.copy();
		substructure.setReducingEnd(null);
		if (!substructure.isValidStructure())
			return lSubstructures;

		lSubstructures.add(substructure);
		for (Composition subsub : generateSubstructures(substructure)) {
			if (lSubstructures.contains(subsub))
				continue;
			lSubstructures.add(subsub);
		}
		return lSubstructures;
	}

	/**
	 * Generates possible substructures of the given Composition. The substructures
	 * exceeding the given mass are discarded.<br>
	 * Note: The given composition must contain the desired mass options
	 * (monoisotopic or average, perderivatization type) before the calculation
	 * because the mass values depend on the options.
	 * 
	 * @param composition    Composition to be subsumed
	 * @param dMassThreshold double value of mass threshold
	 * @return List of substructures
	 */
	public static List<Composition> generateSubstructures(Composition composition) {
		List<Composition> lSubstructures = new ArrayList<>();

		if (!composition.isValidStructure())
			return lSubstructures;

		// Residues
		for (ResidueType res : composition.getResidueTypes()) {
			Composition substructure = composition.copy();
			substructure.addResidues(res, -1);
			if (!substructure.isValidStructure())
				continue;
			// Only allow to add substructure with smaller mass than threshold
			lSubstructures.add(substructure);
			for (Composition subsub : generateSubstructures(substructure)) {
				if (lSubstructures.contains(subsub))
					continue;
				lSubstructures.add(subsub);
			}
		}
		return lSubstructures;
	}

	public static void sortCompositionsByMass(List<Composition> lCompositions, final boolean isAscending) {
		Collections.sort(lCompositions, new Comparator<Composition>() {
			@Override
			public int compare(Composition o1, Composition o2) {
				double def = o1.computeMass() - o2.computeMass();
				if (def < 0)
					return (isAscending) ? -1 : 1;
				if (def > 0)
					return (isAscending) ? 1 : -1;
				return 0;
			}
		});
	}
}
