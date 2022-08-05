package org.grits.toolbox.ms.annotation.glycan.composition.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.glycan.composition.utils.TextUtils;

public class TextFileUtils {
	private static final Logger logger = Logger.getLogger(TextFileUtils.class);

	/**
	 * Opens the given file as InputStream from jar file first, and from file next.
	 * @param filename The file name to be checked
	 * @return InputStream of the filename (null if filename is null)
	 * @throws IOException 
	 */
	public static InputStream open(String filename) throws IOException {
		if (filename == null)
			return null;
		String filepath = filename;
		// Try from jar file
		URL url = TextFileUtils.class.getResource(filename);
		if (url != null)
			return url.openStream();
		// Try from file
		File file = new File(filepath);
		if (!file.exists())
			throw new FileNotFoundException("Cannot find "+filename);

		return new FileInputStream(file);
	}

	/**
	 * Reads the file as a text file and returns list of the lines.
	 * A line start with "%" is ignored.
	 * @param filename The file name to be read
	 * @return List of lines in the file
	 */
	public static List<String> getLines(String filename) {
		try {
			List<String> lines = new ArrayList<>();

			InputStream is = open(filename);
			if (is == null)
				return null;

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			// read dictionary
			String line;
			while ((line = br.readLine()) != null) {
				line = TextUtils.trim(line.trim());
				if (line.length() > 0 && !line.startsWith("%"))
					lines.add(line);
			}

			br.close();

			return lines;
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}

}
