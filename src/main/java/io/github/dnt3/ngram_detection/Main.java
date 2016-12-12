package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class Main {

    /**
     * _search file is created by merging sub-files (by using the ... script). Every line in the _search_file corresponds to a sub-file.
     * (This convention is for multithreaded purposes.)
     **/
    private static int _max_n = 0;
    private static String _ngram_file = "input.dat";
    private static String _search_file = "text_stream.dat";
    private static PrintStream _printStream = System.out;
    private static int _cores = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {

		// access arguments
		for (int i = 0; i != args.length; i++) {
			if (args[i].equalsIgnoreCase("-i")) {
				_ngram_file = args[i+1];
			} else if (args[i].equalsIgnoreCase("-o")) {
				String output_file = args[i+1];
				try {
					_printStream = new PrintStream(output_file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (args[i].equalsIgnoreCase("-f")) {
                _search_file = args[i+1];
            } else if (args[i].equalsIgnoreCase("-h")) {
				System.out.println("Program's flags:  -i inputFile -o outputFile");
			}
		}

		if (!(new File(_ngram_file)).exists()){
			System.out.println("Wrong flags! N-gram file does not exist. \nUse -h to see commands");
			System.exit(-1);
		} else if (!(new File(_search_file)).exists()) {
            System.out.println("Wrong flags! Search file does not exist. \nUse -h to see commands");
            System.exit(-1);
        }

		Map<String, Integer> occurrence_map = new HashMap<>();
		countOccurrences(occurrence_map);
		Map<String, Vector<NGram>> index = create_index(occurrence_map, _ngram_file);
		// printMap(index, _printStream, occurrence_map);
		_printStream.println("\nF\n");


        System.out.println(_cores + " available");
        ExecutorService executor = new ThreadPoolExecutor(1, _cores, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        Stream<String> all_files = null;
        try {
            // for each sub-file (newline) create a new searcher
            all_files = Files.lines(Paths.get(_search_file));
            all_files.forEach(line -> {

                Runnable runnable = new Searcher(index, line, _printStream, _max_n);
                executor.execute(runnable);

			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (all_files != null) all_files.close();
		}

	}

	// Create Hash (Occurrence Map)
	private static void countOccurrences(Map<String, Integer> map) {
		Vector<String> terms = new Vector<>();
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(_ngram_file));
			lines.forEach(line -> {
				// Split Line
				String[] parts = line.split(" ");
				terms.clear();
				Collections.addAll(terms, parts);
				int n = terms.size();
				String last = terms.get(n - 1);
				terms.remove(last);
				if(n-1 > _max_n){
					_max_n = n-1;
				}
				for (String part : terms) {
					if (map.containsKey(part)) { //key exists
						map.put(part, map.get(part) + 1);
					} else { //key does not exists
						map.put(part, 1);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lines != null)
				lines.close();
		}
	}

	private static Map<String, Vector<NGram>> create_index(Map<String, Integer> occurrence_map, final String _ngram_file) {
		Map<String, Vector<NGram>> index = new HashMap<>();
		try {
			Files.lines(Paths.get(_ngram_file)).forEach(line -> {
				NGram ngram = NGram.parseLineToNgram(line);
				String key = ngram.findLeastUsedWord(occurrence_map);
				/* Insert to hash */
				Vector<NGram> ngrams_vec;
				if (index.containsKey(key)) {
					ngrams_vec = index.get(key);
					ngrams_vec.add(ngram);
				} else {
					ngrams_vec = new Vector<>();
					ngrams_vec.add(ngram);
					index.put(key, ngrams_vec);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;
	}

	private static void printMap(Map<String, Vector<NGram>> map, PrintStream printWriter, Map<String, Integer> occurrence_map) {
		printWriter.println("Found " + map.size() + " words.\n\n");
		for (Map.Entry<String, Vector<NGram>> entry : map.entrySet()) {
			String key = entry.getKey();
			Vector<NGram> ngrams_vec = entry.getValue();
			printWriter.print("Key:" + key + " ("+occurrence_map.get(key)+")\t\tValues:\t" );
			for (NGram node : ngrams_vec) {
				printWriter.print(node + "\n\t\t\t");
			}
			printWriter.println();
		}
	}

}
