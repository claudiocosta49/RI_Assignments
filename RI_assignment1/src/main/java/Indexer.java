import java.io.*;
import java.util.*;


public class Indexer {
    Map<String, Map<Integer, Integer>> invertedIndex;   //{"and": {1: 2}} -> term: {docNo: count}
    
    private List<String> keys;
    private int vocabSize;

    public Indexer() {
        this.invertedIndex = new HashMap<>();
        this.vocabSize = 0;
    }

    public List<String> index(Map<String, List<Integer>> tokens, int docId) throws IOException {
        
        for( Map.Entry<String,List<Integer>> pair: tokens.entrySet() ){
            Map<Integer, Integer> termInfo = new HashMap<>();
            String key = pair.getKey();
            List<Integer> value = pair.getValue();
            termInfo.put(docId, value.size());
            invertedIndex.put(key, termInfo);

        }
        vocabSize = invertedIndex.size();
        BufferedWriter writeData = null;

        try {
            writeData = new BufferedWriter(new FileWriter("invertedIndexWithCounts.txt", true));
        } catch (IOException e) {
            System.err.println("ERROR: Could not create output text file (invertedIndexWithCounts.txt)");
        }

        keys = new ArrayList<>(invertedIndex.keySet());
        Collections.sort(keys);
        
        for (String key : keys) {
            try{
                writeData.write((key + ":" + invertedIndex.get(key) + "\n"));
            } catch (IOException e) {
                System.err.println("ERROR: Access to output text file (invertedIndexWithCounts.txt) was unsuccessful.");
            }
        }

            writeData.close();
            return keys;

        
    }

    public int getVocabSize(){
        return this.vocabSize;
    }
}
