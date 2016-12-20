package io.github.dnt3.ngram_detection.naive_approach;

import io.github.dnt3.ngram_detection.Helper;
import io.github.dnt3.ngram_detection.Searcher;
import io.github.dnt3.ngram_detection.structures.NGram;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by nikolas on 20/12/2016.
 */
public class NaiveMain {


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
            System.err.println("Wrong flags! N-gram file does not exist. \nUse -h to see commands");
            System.exit(-1);
        }

        if (!(new File(_search_file)).exists()) {
            System.err.println("Wrong flags! Search file does not exist. \nUse -h to see commands");
            System.exit(-1);
        }

        long tStart = System.currentTimeMillis();
        Helper helper = new Helper(_ngram_file);
        ArrayList<NGram> nGrams = helper.geNGramTerms();

        long tEnd = System.currentTimeMillis();
        System.out.println("Building index: " + (tEnd - tStart)/1000.0 + " sec.");
        System.out.println("\nF\n");

        tStart = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(_cores);
        Stream<String> all_files = null;

        // for each sub-file (newline) create a new searcher
        try {
            all_files = Files.lines(Paths.get(_search_file));
            all_files.forEach(line -> {

                Runnable runnable = new Searcher(nGrams, line, _printStream, 0, true);
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
