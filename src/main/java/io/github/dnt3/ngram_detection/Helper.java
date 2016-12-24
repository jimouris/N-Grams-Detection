package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static io.github.dnt3.ngram_detection.Main._max_n;


class Helper {

    private final String _ngram_file;
    private Map<String, Integer> _occurrence_map;

    Helper(String ngram_file) {
        this._ngram_file = ngram_file;
        this._occurrence_map = new HashMap<>();
    }

    Map<String, Integer> countOccurrences() {
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(_ngram_file));
            lines.forEach(line -> {
                String part;
                String[] parts = line.split(" ");
                int len = parts.length;
                if (len-1 > _max_n) {
                    _max_n = len - 1;
                }
                for (int i = 0 ; i < len-1 ; i++) {
                    part = parts[i];
                    if (_occurrence_map.containsKey(part)) { //key exists
                        _occurrence_map.put(part, _occurrence_map.get(part) + 1);
                    } else { //key does not exists
                        _occurrence_map.put(part, 1);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lines != null) lines.close();
        }
        return _occurrence_map;
    }

    Map<String, ArrayList<NGram>> create_index() {
        Map<String, ArrayList<NGram>> index = new HashMap<>();
        try {
            Files.lines(Paths.get(_ngram_file)).forEach(line -> {
                NGram ngram = NGram.parseLineToNgram(line);
                String key = ngram.findLeastUsedWord(_occurrence_map);
				/* Insert to hash */
                ArrayList<NGram> ngrams_lst;
                if (index.containsKey(key)) {
                    ngrams_lst = index.get(key);
                    ngrams_lst.add(ngram);
                } else {
                    ngrams_lst = new ArrayList<>();
                    ngrams_lst.add(ngram);
                    index.put(key, ngrams_lst);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

    void printMap(Map<String, ArrayList<NGram>> index, PrintStream printWriter) {
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
