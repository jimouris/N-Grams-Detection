package NGram_detection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nikolas on 20/11/16.
 */
public class Main {

    public static String input_file = "input.dat";
    public static String output_file = "ngram_list.dat";

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

    }

    private static void countOccurrences(Map<String, Integer> map) {
        // Create Hash (Occurrence Map)
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

}
