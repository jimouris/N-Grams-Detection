package NGram_detection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by nikolas on 20/11/16.
 */
public class Main {

    private static String input_file = "input.dat";
    private static String output_file = "ngram_list.dat";

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

        Map<String, Integer> occurrence_map = new HashMap<String, Integer>();
        countOccurrences(occurrence_map);
        printOccurrenceMap(occurrence_map);
        System.out.println("---------------------------------------");
        System.out.println("Occurrence is finished!");
        List<IndexNode> index = create_index(occurrence_map, input_file);
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        for (int i = 0 ; i < index.size() ; i++) {
            IndexNode node = index.get(i);
            System.out.println("index[" + i + "] = Key: " + node.getKey() + " - Value: " + node.getValue());
        }

    }

    // Create Hash (Occurrence Map)
    private static void countOccurrences(Map<String, Integer> map) {
        try {
            Files.lines(Paths.get(input_file))
            .forEach(line -> {
                // Split Line
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (!isNumeric(part)) {
                        if (map.containsKey(part)) { //key exists
                            map.put(part, map.get(part) + 1);
                        } else { //key does not exists
                            map.put(part, 1);
                        }
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

    private static List<IndexNode> create_index(Map<String, Integer> map, final String input_file) {
        List<IndexNode> index = new ArrayList<>();
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
                index.add(indexNode);
                System.out.print("index[" + Integer.toString(index.size()-1) + "] = ");
                System.out.println(index.get(index.size()-1).getValue().toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

}
