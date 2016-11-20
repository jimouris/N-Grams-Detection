package NGram_detection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by nikolas on 20/11/16.
 */
public class Main {

    public static String input_file;
    public static String output_file = "ngram_list.dat";

    public static void main(String[] args) {

        input_file = args[0];
        // access arguments
//        for (int i = 0; i != args.length; ++i) {
//            String arg = args[i];
//            if (arg == "-i") {
//                if (arg != null) input_file = args[i+1];
//            } else if (arg == "-o") {
//                if (arg != null) output_file = args[i+1];
//            } else if (arg == "-h") {
//                System.out.println("Usage: " + args[0] + " -i inputFile -o outputFile");
//            } else {
//                System.out.println("Usage: " + args[0] + " -i inputFile -o outputFile");
//                System.exit(-1);
//            }
//        }

        Map<String, Integer> occurrence_map = new HashMap<String, Integer>();
        countOccurrences(occurrence_map);
    }

    private static void countOccurrences(Map<String,Integer> map){

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(input_file))) {

            stream.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
