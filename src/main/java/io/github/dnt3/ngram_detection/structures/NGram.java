package io.github.dnt3.ngram_detection.structures;

import io.github.dnt3.ngram_detection.edit_distance.EditDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class NGram {

    private int size;
    private int maxDist;
    private int offset;
    private ArrayList<String> terms;

    private NGram(int size, int maxDist, ArrayList<String> terms) {
        this.size = size;
        this.maxDist = maxDist;
        this.terms = terms;
        this.offset = 0;
    }

    public int getOffset() { return offset; }

    private void setOffset(int offset) { this.offset = offset; }

    public int getSize() {
        return size;
    }

    public int getMaxDist() {
        return maxDist;
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public boolean getEditDistance(ArrayList<String> nGram){
        int distance = 0;
        for(int i=0; i<nGram.size(); i++){
            distance += EditDistance.editDist(nGram.get(i),this.terms.get(i));
            if(distance > this.maxDist){
                return false;
            }
        }
        return (distance <= maxDist);
    }

   public String toString(){
       String nGram = "";
       for (String term : this.terms) {
           nGram += (term + " ");
       }
       return nGram;
   }

    public static NGram parseLineToNgram(String line) {
       String[] arr = line.split(" ");
       ArrayList<String> terms = new ArrayList<>();
       Collections.addAll(terms, arr);
       String last = terms.get(terms.size() - 1);
       terms.remove(last);
       return new NGram(terms.size(), Integer.parseInt(last), terms);
   }

   public String findLeastUsedWord(Map<String, Integer> occurrence_map) {
       ArrayList<String> tmp = this.getTerms();
       int min_occurrence = occurrence_map.get(tmp.get(0));
       int min_offset = 0, occurrence;
       for (int i = 0 ; i < tmp.size() ; i++) {
           occurrence = occurrence_map.get(tmp.get(i));
           if (occurrence < min_occurrence) {
               min_occurrence = occurrence;
               min_offset = i;
           }
       }
       this.setOffset(min_offset);
       return tmp.get(min_offset);
   }

}
