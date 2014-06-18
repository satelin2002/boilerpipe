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
 * Removes blocks with first section has h1 or h2.
 * 
 * @author Christian Kohlschütter
 */
public final class TagNameBoilerplateFilter implements BoilerpipeFilter {
	public static final TagNameBoilerplateFilter INSTANCE = new TagNameBoilerplateFilter();

	private TagNameBoilerplateFilter() {
	}

	public boolean process(final TextDocument doc)
			throws BoilerpipeProcessingException {

		boolean changes = false;
		
		int count = 0;
		try {
			for (TextBlock tb : doc.getTextBlocks()) {
				if (count > 0) return changes;
				if (!tb.isContent()) {
					continue;
				}
				
//				System.out.println("TC: " + tb.getText());
//				System.out.println("TT: " + tb.getTagName());
				
				if (tb.getTagName().equalsIgnoreCase("h1") || tb.getTagName().equalsIgnoreCase("h2")) {
					tb.setIsContent(false);
					changes = true;
					count = count + 1;
					continue;
				}
				count = count + 1;
			}

			return changes;
		} catch (Exception e) {
			return changes;
		}

	}
}
