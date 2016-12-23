package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;


public class Searcher implements Runnable {

	private Map<String, ArrayList<NGram>> _index;
	private int _max_n;
	private File _search_file;
	private final PrintStream _printStream;
	private Set<String> _out_set;

	Searcher(Map<String, ArrayList<NGram>> index, File search_sub_file, PrintStream printStream, int max_n) {
		this._index = index;
		this._search_file = search_sub_file;
		this._printStream = printStream;
		this._max_n = max_n;
		this._out_set = new HashSet<>();
	}

	/* Search in file */
	@Override
	public void run() {
		int i, j, offset;
		boolean areEqual;
		ArrayList<NGram> ngrams;
		ArrayList<String> terms;
		ArrayList<String> parts = new ArrayList<>();
		Scanner s = null;
		try{
			s = new Scanner(_search_file).useDelimiter("\\s+");
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
		}
		for (i = 0; i < _max_n-1; i++) parts.add(""); // For cold start
		while(s.hasNext()){
			parts.addAll(Arrays.asList(s.next().split("\\s+")));
		}
		for (i = 0; i < _max_n-1; i++) parts.add(""); // For hot finish
		s.close();
		// for (String part: parts){
		// 	System.out.println("part: |"+part+"|");
		// }
		for (i = _max_n - 1; i < parts.size()-(_max_n-1); i++) {
			ngrams = _index.get(parts.get(i));
			/* If index contains searchKey */
			if (ngrams != null) {
				for (NGram ngram : ngrams) {
					offset = ngram.getOffset();
					areEqual = true;
					terms = ngram.getTerms();
					for (j=0 ; j<ngram.getSize() ; j++) {
						if (! terms.get(j).equals(parts.get(i-offset+j)) ) {
							areEqual = false;
							break;
						}
					}
					if (areEqual) _out_set.add(ngram.toString());
				}
			} /* else Skip it */
		}
		for (String ngram : _out_set) _printStream.println(ngram);
	}
}
