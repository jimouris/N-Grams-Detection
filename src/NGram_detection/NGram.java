package NGram_detection;

import java.util.Vector;

/**
 * Created by thanos on 20/11/2016.
 */
public class NGram {
    public NGram(int size, int maxDist, Vector<String> terms) {
        this.size = size;
        this.maxDist = maxDist;
        this.terms = terms;
    }

    public int getSize() {
        return size;
    }

    public int getMaxDist() {
        return maxDist;
    }

    public Vector<String> getTerms() {
        return terms;
    }

    private int size;
    private int maxDist;
    private Vector<String> terms;

    public void print(){
        for (String term : this.terms) {
            System.out.print(term+" ");
            System.out.println();
        }
    }

    public boolean getEditDistance(Vector<String> nGram){
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
       for (String term:this.terms) {
           nGram += (term + " ");
       }
       return  nGram;
   }
}
