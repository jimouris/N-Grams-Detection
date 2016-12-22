package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class Helper {

    private final String _ngram_file;
    private Map<String, Integer> _occurrence_map;

    public Helper(String ngram_file) {
        this._ngram_file = ngram_file;
        this._occurrence_map = new HashMap<>();
    }

    public ArrayList<NGram> geNGramTerms() {
        ArrayList<NGram> ngramList = new ArrayList<>();
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(_ngram_file));
            lines.forEach(line -> {
                NGram ngram = NGram.parseLineToNgram(line);
                ngramList.add(ngram);

            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lines != null) lines.close();
        }
        return ngramList;
    }

    public void printMap(Map<String, ArrayList<NGram>> index, PrintStream printWriter) {
        printWriter.println("Found " + index.size() + " words.\n\n");
        for (Map.Entry<String, ArrayList<NGram>> entry : index.entrySet()) {
            String key = entry.getKey();
            ArrayList<NGram> ngrams_vec = entry.getValue();
            printWriter.print("Key:" + key + " (" + _occurrence_map.get(key) + ")\t\tValues:\t" );
            for (NGram node : ngrams_vec) {
                printWriter.print(node + "\n\t\t\t");
            }
            printWriter.println();
        }
    }

}
