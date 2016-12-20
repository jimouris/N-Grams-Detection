package io.github.dnt3.ngram_detection;

import io.github.dnt3.ngram_detection.structures.NGram;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class Searcher implements Runnable {

    private Map<String, ArrayList<NGram>> _index;
    private ArrayList<NGram> _nGrams;
    private int _max_n;
    private String _search_file;
    private final PrintStream _printStream;
    private boolean _native_flag = false;

    public Searcher(Map<String, ArrayList<NGram>> index, String search_sub_file, PrintStream printStream, int max_n) {
        this._index = index;
        this._nGrams = new ArrayList<>();
        this._search_file = search_sub_file;
        this._printStream = printStream;
        this._max_n = max_n;
    }

    public Searcher(ArrayList<NGram> nGrams, String search_sub_file, PrintStream printStream,
                    int max_n, boolean native_flag) {
        this._nGrams = nGrams;
        this._index = new HashMap<>();
        this._search_file = search_sub_file;
        this._printStream = printStream;
        this._max_n = max_n;
        this._native_flag = native_flag;
    }

    /* Search in file */
    @Override
    public void run() {


        if (_native_flag){
            String search_file = _search_file;
            for (int i=0; i < _nGrams.size(); i++){
                String[] parts = search_file.split(" ");
                boolean success = false;
                for (int ctext = 0; ctext < parts.length-1; ctext++ ) {
                    success = false;
                    if (parts[ctext].equals(_nGrams.get(i).getTerms().get(0))) {
                        for (int ftetst = 1; ftetst < _nGrams.get(i).getTerms().size(); ftetst++) {
                            if ( ctext+ftetst< parts.length && ftetst < _nGrams.get(i).getTerms().size() ) {
                                if (parts[ctext + ftetst].equals(_nGrams.get(i).getTerms().get(ftetst))) {
                                    success = true;
                                } else {
                                    success = false;
                                    break;
                                }
                            }
                            else{
                                success = false;
                                break;
                            }
                        }
                        if (success)
                        System.out.println("I found perfect match, ngram: " + _nGrams.get(i).toString());
                    }
                }
            }
        }

        else {
            ArrayList<String> searchTerms = new ArrayList<>();
            for (int i = 0; i < _max_n - 1; i++) searchTerms.add("");
            String search_file = _search_file;
            String searchKey, backupKey;
            String[] parts = search_file.split(" ");
            for (String part : parts) {
                if (searchTerms.size() < 2 * _max_n - 1) {
                    searchTerms.add(part);
                }
                if (searchTerms.size() >= 2 * _max_n - 1) {
                    int search_offset = _max_n - 1;
                    int backup_offset = _max_n - 1;
                    searchKey = searchTerms.get(search_offset);
                    /* Search middle term */
                    if (_index.containsKey(searchKey)) {
                        ArrayList<NGram> ngrams = _index.get(searchKey);
                        for (NGram ngram : ngrams) {
                            int offset = ngram.getOffset();
                            boolean areEqual = true;
                            for (int i = 0; i < ngram.getSize(); i++) {
                                backupKey = searchTerms.get(backup_offset - offset + i);
                                ArrayList<String> terms = ngram.getTerms();
                                if (!terms.get(i).equals(backupKey)) {
                                    areEqual = false;
                                    break;
                                }
                            }
                            search_offset++;
                            if (areEqual) syncPrint(ngram.toString());
                        }
                    } /* else Skip it */
                    searchTerms.remove(0); /* remove first */
                }
            }
            // TODO: check-fix hot finish!
        }
    }

    //seemed to be a bottleneck, but after running extensive benchmarks seem to be ok
    private void syncPrint(String s) {
        synchronized (_printStream) {
            _printStream.println(s);
        }
    }

}
