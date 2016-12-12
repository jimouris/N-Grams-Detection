package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
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
        Vector<String> terms = new Vector<>();
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(_ngram_file));
            lines.forEach(line -> {
                // Split Line
                String[] parts = line.split(" ");
                terms.clear();
                Collections.addAll(terms, parts);
                int n = terms.size();
                String last = terms.get(n - 1);
                terms.remove(last);
                if(n-1 > _max_n){
                    _max_n = n-1;
                }
                for (String part : terms) {
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

    Map<String, Vector<NGram>> create_index() {
        Map<String, Vector<NGram>> index = new HashMap<>();
        try {
            Files.lines(Paths.get(_ngram_file)).forEach(line -> {
                NGram ngram = NGram.parseLineToNgram(line);
                String key = ngram.findLeastUsedWord(_occurrence_map);
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

    void printMap(Map<String, Vector<NGram>> index, PrintStream printWriter) {
        printWriter.println("Found " + index.size() + " words.\n\n");
        for (Map.Entry<String, Vector<NGram>> entry : index.entrySet()) {
            String key = entry.getKey();
            Vector<NGram> ngrams_vec = entry.getValue();
            printWriter.print("Key:" + key + " (" + _occurrence_map.get(key) + ")\t\tValues:\t" );
            for (NGram node : ngrams_vec) {
                printWriter.print(node + "\n\t\t\t");
            }
            printWriter.println();
        }
    }

}
