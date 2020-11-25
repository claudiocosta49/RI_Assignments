import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public class Indexer {
    Map<String, Map<Integer, Integer>> invertedIndex;   //{"and": {1: 2}} -> term: {docNo: count}
    
    private List<String> keys;

    public Indexer() {
        this.invertedIndex = new HashMap<>();
    }

    public void index(Map<String, List<Integer>> tokens, int docId) throws IOException {
        
        for( Map.Entry<String,List<Integer>> pair: tokens.entrySet() ){
            Map<Integer, Integer> termInfo = new HashMap<>();
            String key = pair.getKey();
            List<Integer> value = pair.getValue();
            termInfo.put(docId, value.size());
            invertedIndex.put(key, termInfo);

        }

        BufferedWriter writeData = null;

        try {
            writeData = new BufferedWriter(new FileWriter("invertedIndexWithCounts.txt", true));
        } catch (IOException e) {
            System.err.println("Error: Cannot access to file properly!");
        }

        keys = new ArrayList<>(invertedIndex.keySet());
        Collections.sort(keys);
        
        for (String key : keys) {
            try{
                writeData.write(key + ":" + invertedIndex.get(key) + "\n");
            } catch (IOException e) {
                System.err.println("Error: Cannot access to file properly!");
            }
        }

            writeData.close();

        
    }
}
