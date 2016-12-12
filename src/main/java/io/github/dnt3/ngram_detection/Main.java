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
    static int _max_n = 0;
    private static String _ngram_file = "input.dat";
    private static String _search_file = "text_stream.dat";
    private static PrintStream _printStream = System.out;
    private static final int _cores = Runtime.getRuntime().availableProcessors();

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

        Helper helper = new Helper(_ngram_file);
		helper.countOccurrences();
		Map<String, Vector<NGram>> index = helper.create_index();
//		 helper.printMap(index, _printStream);
		_printStream.println("\nF\n");

        System.out.println(_cores + " cores available\n");
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

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

}