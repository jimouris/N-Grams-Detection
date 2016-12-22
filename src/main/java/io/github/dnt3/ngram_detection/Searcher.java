package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Arrays;


public class Searcher implements Runnable {

	private Map<String, ArrayList<NGram>> _index;
	private int _max_n;
	private String _search_file;
	private final PrintStream _printStream;

	Searcher(Map<String, ArrayList<NGram>> index, String search_sub_file, PrintStream printStream, int max_n) {
		this._index = index;
		this._search_file = search_sub_file;
		this._printStream = printStream;
		this._max_n = max_n;
	}

	/* Search in file */
	@Override
	public void run() {
		String search_file = _search_file;
		String searchKey, backupKey;
		ArrayList<String> parts = new ArrayList<>();
		for (int i = 0; i< _max_n-1; i++) parts.add(""); // For cold start
		parts.addAll(Arrays.asList(search_file.split(" ")));
		for (int i = 0; i< _max_n-1; i++) parts.add(""); // For hot finish
		for(int i = _max_n - 1; i < parts.size()-(_max_n-1); i++ ){
			searchKey = parts.get(i);
			/* Search middle term */
			ArrayList<NGram> ngrams = _index.get(searchKey);
			if (ngrams != null) { // If index contains searchKey
				for (NGram ngram : ngrams) {
					int offset = ngram.getOffset();
					boolean areEqual = true;
					for (int j=0 ; j<ngram.getSize() ; j++) {
						backupKey = parts.get(i-offset+j);
						ArrayList<String> terms = ngram.getTerms();
						if (!terms.get(j).equals(backupKey)) {
							areEqual = false;
							break;
						}
					}
					if (areEqual) syncPrint(ngram.toString());
				}
			} /* else Skip it */
		}
	}

	//seemed to be a bottleneck, but after running extensive benchmarks seem to be ok
	private void syncPrint(String s) {
		synchronized (_printStream) {
			_printStream.println(s);
		}
	}

}
