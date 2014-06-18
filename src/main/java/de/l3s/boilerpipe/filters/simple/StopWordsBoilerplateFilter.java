package de.l3s.boilerpipe.filters.simple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;

/**
 * Keeps only those content blocks which contain at least <em>k</em> words.
 * 
 * @author Christian Kohlschütter
 */
public final class StopWordsBoilerplateFilter implements BoilerpipeFilter {
	private final String stopFile = "stopwords.txt";
	public static final StopWordsBoilerplateFilter INSTANCE = new StopWordsBoilerplateFilter();

	private StopWordsBoilerplateFilter() {
	}

	public boolean process(final TextDocument doc)
			throws BoilerpipeProcessingException {

		boolean changes = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(stopFile));
			String line;
			List<String> stopWords = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					stopWords.add(line);
				}
			}
			br.close();
			
			for (TextBlock tb : doc.getTextBlocks()) {
				
				if (!tb.isContent()) {
					continue;
				}
				String content = tb.getText();
				for (String s : stopWords) {
					if (content.toLowerCase().contains(s.toLowerCase())) {
						tb.setIsContent(false);
						changes = true;
						continue;
					}
				}
			}

			return changes;
		} catch (Exception e) {
			return changes;
		}

	}
}
