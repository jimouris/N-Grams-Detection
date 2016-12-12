package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;


public class Searcher implements Runnable {

    private Map<String, Vector<NGram>> _index;
    private int _max_n;
    private String _search_file;
    private final PrintStream _printStream;

    Searcher(Map<String, Vector<NGram>> index, String search_sub_file, PrintStream printStream, int max_n) {
        this._index = index;
        this._search_file = search_sub_file;
        this._printStream = printStream;
        this._max_n = max_n;
    }

    /* Search in file */
    @Override
    public void run() {
        LinkedList<String> searchTerms = new LinkedList<>();
        for (int i = 0; i< _max_n-1; i++) searchTerms.add("");
        String search_file = _search_file;
        String searchKey, backupKey;
        String[] parts = search_file.split(" ");
        for (String part : parts) {
            if (searchTerms.size() < 2*_max_n-1){
                searchTerms.add(part);
            }
            if (searchTerms.size() >= 2*_max_n-1) {
                int search_offset = _max_n-1;
                int backup_offset = _max_n-1;
                searchKey = searchTerms.get(search_offset);
                /* Search middle term */
                if (_index.containsKey(searchKey)) {
                    Vector<NGram> ngrams = _index.get(searchKey);
                    for (NGram ngram : ngrams) {
                        int offset = ngram.getOffset();
                        boolean areEqual = true;
                        for (int i=0 ; i<ngram.getSize() ; i++) {
                            backupKey = searchTerms.get(backup_offset-offset+i);
                            Vector<String> terms = ngram.getTerms();
                            if (!terms.get(i).equals(backupKey)) {
                                areEqual = false;
                                break;
                            }
                        }
                        search_offset++;
                        if (areEqual) syncPrint(ngram.toString());
                    }
                } /* else Skip it */
                searchTerms.remove(); /* remove first */
            }
        }
        // TODO: check-fix hot finish!
    }

    // TODO: maybe this is a bottleneck
    private void syncPrint(String s) {
        synchronized (_printStream) {
            _printStream.println(s);
        }
    }

}
