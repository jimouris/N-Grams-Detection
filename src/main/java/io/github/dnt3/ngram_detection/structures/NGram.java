package io.github.dnt3.ngram_detection.structures;

import io.github.dnt3.ngram_detection.edit_distance.EditDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class NGram {

    private int _size;
    private int _maxDist;
    private int _offset;
    private ArrayList<String> _terms;

    private NGram(int _size, int maxDist, ArrayList<String> terms) {
        this._size = _size;
        this._maxDist = maxDist;
        this._terms = terms;
        this._offset = 0;
    }

    public int getOffset() { return _offset; }

    public int getSize() {
        return _size;
    }

    public ArrayList<String> getTerms() {
        return _terms;
    }

    public boolean getEditDistance(ArrayList<String> nGram){
        int distance = 0;
        for(int i=0; i<nGram.size(); i++){
            distance += EditDistance.editDist(nGram.get(i), _terms.get(i));
            if(distance > _maxDist){
                return false;
            }
        }
        return (distance <= _maxDist);
    }

   public String toString(){
       String nGram = "";
       for(int i = 0; i < _size-1; i++){
           nGram += (_terms.get(i) + " ");
       }
       nGram += _terms.get(_size-1);
       return nGram;
   }

    public static NGram parseLineToNgram(String line) {
       String[] arr = line.split(" ");
       ArrayList<String> terms = new ArrayList<>();
       Collections.addAll(terms, arr);
       String last = terms.get(terms.size() - 1);
       terms.remove(terms.size()-1);
       return new NGram(terms.size(), Integer.parseInt(last), terms);
   }

   public String findLeastUsedWord(Map<String, Integer> occurrence_map) {
       int min_occurrence = occurrence_map.get(_terms.get(0));
       int min_offset = 0, occurrence;
       for (int i = 1 ; i < _terms.size() ; i++) {
           occurrence = occurrence_map.get(_terms.get(i));
           if (occurrence < min_occurrence) {
               min_occurrence = occurrence;
               min_offset = i;
           }
       }
       _offset = min_offset;
       return _terms.get(min_offset);
   }

}
