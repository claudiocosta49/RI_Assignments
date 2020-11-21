import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Indexer {
    Map<String, Map<Integer, Integer>> occurrenceMap;   //{and: {1: 2}} -> term: {docNo: count}
    Map<Integer, Integer> termInfo = new HashMap<>();

    public Indexer() {
        this.occurrenceMap = new HashMap<>();
    }
    public void index(Map<String, List<Integer>> terms, int docId) throws IOException {
        int countOcurrences = 0;
        for(String term : terms.keySet()){
            if(!this.occurrenceMap.containsKey(term)){
                termInfo.put(docId, countOcurrences++);
                this.occurrenceMap.put(term, termInfo);
            }else{
                this.occurrenceMap.get(term).put(docId, countOcurrences++);
            }
        }

        System.out.println(occurrenceMap);

    } 
}
