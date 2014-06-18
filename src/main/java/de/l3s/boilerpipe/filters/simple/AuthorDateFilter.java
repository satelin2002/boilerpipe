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
 */
public final class AuthorDateFilter implements BoilerpipeFilter {
	private final String stopFile = "authordatewords.txt";
	public static final AuthorDateFilter INSTANCE = new AuthorDateFilter();

	private AuthorDateFilter() {
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

			// First line which is the author or the date.
			for (TextBlock tb : doc.getTextBlocks()) {
				int count = 0;
				String content = tb.getText();
				
				if (!tb.isContent())
					continue;
				
				
				for (int i = 0; i < content.length(); i++)
					if (content.charAt(i) == '.')
						count++;

				if (count < 2) {
					for (String s : stopWords) {
						if (content.toLowerCase().contains(s.toLowerCase())) {
							tb.setIsContent(false);
							changes = true;
						}
					}
				}
				break;
			}

			return changes;
		} catch (Exception e) {
			return changes;
		}

	}
}
