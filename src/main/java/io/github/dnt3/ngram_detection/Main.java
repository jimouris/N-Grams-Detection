package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.IndexNode;
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
//        printOccurrenceMap(occurrence_map);
        System.out.println("Occurrence map has been created!");
        Map<String, Vector<IndexNode>> index = create_index(occurrence_map, input_file);
        System.out.println("Indexing has been created!");

        // print to output file the index
        if (output_file != null) {
            try {
                PrintStream printStream = new PrintStream(output_file);
                printMap(index, printStream);
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            printMap(index, System.out);
        }

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

    private static void printOccurrenceMap(Map<String, Integer> map){
        Iterator iterator = map.keySet().iterator();
        System.out.println("Printing Occurrence Map!");
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Integer value = map.get(key);
            System.out.println("Key: " + key + " - value: " + value);
        }
    }

    private static boolean isNumeric(String str){
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static Map<String, Vector<IndexNode>> create_index(Map<String, Integer> map, final String input_file) {
        Map<String, Vector<IndexNode>> index = new HashMap<>();
        try {
            Files.lines(Paths.get(input_file)).forEach(line -> {
                NGram ngram = NGram.parseLineToNgram(line);
                Vector<String> tmp = ngram.getTerms();
                int min_term = map.get(tmp.get(0));
                int min_offset = 0;

                for (int i = 0; i < tmp.size() ; i++) {
                    int occurrence = map.get(tmp.get(i));
                    if (occurrence < min_term) {
                        min_term = occurrence;
                        min_offset = i;
                    }
                }
                IndexNode indexNode = new IndexNode(tmp.get(min_offset), ngram);
                if (index.containsKey(indexNode.getKey())) {
                    Vector<IndexNode> indx_vec = index.get(indexNode.getKey());
                    indx_vec.add(indexNode);
                } else {
                    Vector<IndexNode> indx_vec = new Vector<>();
                    indx_vec.add(indexNode);
                    index.put(indexNode.getKey(), indx_vec);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

    private static void printMap(Map<String, Vector<IndexNode>> map, PrintStream printWriter) {
        for (Map.Entry<String, Vector<IndexNode>> entry : map.entrySet()) {
            String key = entry.getKey();
            Vector<IndexNode> indx_vec = entry.getValue();
            printWriter.print("Key: " + key + "\nValues: " );
            for (IndexNode node : indx_vec) {
                printWriter.print(node.getValue() + "\n\t\t");
            }
            printWriter.println();
        }
    }

}
