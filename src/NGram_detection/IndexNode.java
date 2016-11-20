package NGram_detection;

/**
 * Created by jimouris on 11/20/16.
 */
public class IndexNode {

    private String key;
    private NGram value;

    public IndexNode(String key, NGram value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public NGram getValue() {
        return this.value;
    }

}
