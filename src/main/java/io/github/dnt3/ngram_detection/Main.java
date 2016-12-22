package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class Main {

    /**
     * _search file is created by merging sub-files (by using the "./converter" script). Every line in the _search_file corresponds to a sub-file.
     * (This convention is for multithreaded purposes.)
     **/
    static int _max_n = 0;
    private static String _ngram_file = "input.dat";
    private static String _search_file = "text_stream.dat";
    private static String _output_file = "output_file.dat";
    private static final int _cores = Runtime.getRuntime().availableProcessors();
	private static List<PrintStream> _printStreamList = new ArrayList<>();

	public static void main(String[] args) {
		// access arguments
		for (int i = 0; i != args.length; i++) {
			if (args[i].equalsIgnoreCase("-i")) {
				_ngram_file = args[i+1];
			} else if (args[i].equalsIgnoreCase("-o")) {
				_output_file = args[i+1];
			} else if (args[i].equalsIgnoreCase("-f")) {
                _search_file = args[i+1];
            } else if (args[i].equalsIgnoreCase("-h")) {
				System.out.println("Program's flags:  -i inputFile -o outputFile");
			}
		}

		if (!(new File(_ngram_file)).exists()){
			System.err.println("Wrong flags! N-gram file does not exist. \nUse -h to see commands");
			System.exit(-1);
		} else if (!(new File(_search_file)).exists()) {
			System.err.println("Wrong flags! Search file does not exist. \nUse -h to see commands");
			System.exit(-1);
		}
		try {
			_output_file = _output_file.substring(0, _output_file.lastIndexOf('.'));
			for (int core = 0 ; core < _cores ; core++) {
				_printStreamList.add(new PrintStream(_output_file + "_" + core + ".dat"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        long tStart = System.currentTimeMillis();
        Helper helper = new Helper(_ngram_file);
		helper.countOccurrences();
		Map<String, ArrayList<NGram>> index = helper.create_index();
//		helper.printMap(index, System.err);
        long tEnd = System.currentTimeMillis();
        System.out.println("Building index: " + (tEnd - tStart)/1000.0 + " sec.");
		System.out.println("\nF");

        tStart = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(_cores);
        Stream<String> all_files = null;
        // for each sub-file (newline) create a new searcher
		try {
			final int[] i = {0};
			all_files = Files.lines(Paths.get(_search_file));
            all_files.forEach(line -> {

                Runnable runnable = new Searcher(index, line, _printStreamList.get(i[0]++ % _cores), _max_n);
                pool.execute(runnable);

			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (all_files != null) all_files.close();
		}
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
        tEnd = System.currentTimeMillis();
        System.out.println("\nSearching time: " + (tEnd - tStart)/1000.0 + " sec.");
	}

}