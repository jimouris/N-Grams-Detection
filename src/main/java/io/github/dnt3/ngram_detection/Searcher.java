package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Searcher implements Runnable {

	private ArrayList<NGram> _nGrams;
	private String _search_file;
	private final PrintStream _printStream;

	public Searcher(ArrayList<NGram> index, String search_sub_file, PrintStream printStream) {
		this._nGrams = index;
		this._search_file = search_sub_file;
		this._printStream = printStream;
	}

	/* Search in file */
	@Override
	public void run() {
		String search_file = _search_file;
		String[] parts = search_file.split(" ");
		for (int i=0; i < _nGrams.size(); i++){
			boolean success = false;
			NGram ngram = _nGrams.get(i);
			int n = ngram.getSize();
			ArrayList<String> terms = ngram.getTerms();
			for (int ctext = 0; ctext < parts.length; ctext++ ) {
				success = false;
				for (int ftext = 0; ftext < n; ftext++) {
					if ( ctext+ftext< parts.length && ftext < n ) {
						if (parts[ctext + ftext].equals(terms.get(ftext))) {
							success = true;
						} else {
							success = false;
							break;
						}
					} else {
						success = false;
						break;
					}
				}
				if (success) syncPrint(_nGrams.get(i).toString());
			}
		}
	}

	//seemed to be a bottleneck, but after running extensive benchmarks seem to be ok
	private void syncPrint(String s) {
		synchronized (_printStream) {
			_printStream.println(s);
		}
	}

}
