package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by nikolas on 20/11/16.
 */
public class Main {

    private static String input_file = "input.dat";
    private static String output_file = null;

    public static void main(String[] args) {
        // access arguments
        for (int i = 0; i != args.length; i++) {
            if (args[i].equalsIgnoreCase("-i")) {
                input_file = args[i+1];
            } else if (args[i].equalsIgnoreCase("-o")) {
                output_file = args[i+1];
            } else if (args[i].equalsIgnoreCase("-h")) {
                System.out.println("Program's flags:  -i inputFile -o outputFile");
            }
        }

        if (!(new File(input_file)).exists()){
            System.out.println("Wrong flags! Use -h to see commands");
            return;
        }

        Map<String, Integer> occurrence_map = new HashMap<>();
        countOccurrences(occurrence_map);
        System.out.println("Occurrence map has been created!");
        Map<String, Vector<NGram>> index = create_index(occurrence_map, input_file);
        System.out.println("Indexing has been created!");

        // print to output file the index
//        if (output_file != null) {
//            try {
//                PrintStream printStream = new PrintStream(output_file);
//                printMap(index, printStream, occurrence_map);
//                printStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            printMap(index, System.out, occurrence_map);
//        }
    }

    // Create Hash (Occurrence Map)
    private static void countOccurrences(Map<String, Integer> map) {
        Vector<String> terms = new Vector<>();

        try {
            Files.lines(Paths.get(input_file))
            .forEach(line -> {
                // Split Line
                String[] parts = line.split(" ");
                terms.clear();
                Collections.addAll(terms, parts);
                String last = terms.get(terms.size() - 1);
                terms.remove(last);
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
        }
    }

    private static Map<String, Vector<NGram>> create_index(Map<String, Integer> occurrence_map, final String input_file) {
        Map<String, Vector<NGram>> index = new HashMap<>();
        try {
            Files.lines(Paths.get(input_file)).forEach(line -> {
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
                printWriter.print(node + "\n\t\t\t");
            }
            printWriter.println();
        }
    }

}
