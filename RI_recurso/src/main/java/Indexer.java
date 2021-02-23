import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.plaf.DesktopIconUI;


public class Indexer {
    
    private int vocabSize;
	private int memorySize;
	private int blockNumber;
	private Iterator<Document> documentStream;
	private Map<String, Map<String, List<Integer>>> dict;
    private List<Integer> blockPostingsList;

    private int blockSize;

    public Indexer(int blockSize, int memorySize, int blockNumber, Iterator<Document> documentStream) {
        this.vocabSize = 0;
        this.blockSize = blockSize;
        this.blockNumber = blockNumber;
		this.memorySize = memorySize;
        this.documentStream = documentStream;
        this.dict = new LinkedHashMap<String, Map<String,List<Integer>>>();
    }

    public void index(Map<String, List<Integer>> tokens, String paperId) throws IOException {    //index with positional indexing
        int initMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
		int usedMemory = 0;
        
        while(usedMemory<this.memorySize){
            int curMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
            usedMemory = initMemory - curMemory;
            String[] terms = tokens.keySet().toArray(new String[tokens.size()]);
            
            for (int i = 0; i < terms.length; i++) {
                List<Integer> postingsList;
                String term = terms[i];
                List<Integer> termPositions = tokens.get(term);
                if (dict.get(term) == null) {
                    postingsList = this.addToDict(dict, term, termPositions, paperId);
                    
                } else {
                    postingsList = dict.get(term).get(paperId);
                }
                
                
            } 
            break;
            
        }
        //System.out.println(dict);
        sortAndWrite(dict, paperId);
        vocabSize = dict.size();

        
    }

    public List<Integer> addToDict(Map<String, Map<String,List<Integer>>> dict2, String term,List<Integer> termPositions, String paperId) {
        Map<String,List<Integer>> aux = new HashMap<>();
        List<Integer> postingsList = new ArrayList<Integer>();
        

        postingsList.addAll(termPositions);
        
        aux.put(paperId, postingsList);
        

        dict2.put(term, aux);
        
        return postingsList;
    }

    public int getVocabSize(){
        return this.vocabSize;
    }
    private void sortAndWrite(Map<String, Map<String, List<Integer>>> dict2, String paperId) {


        Path file = Paths.get("block" + this.blockNumber + ".txt");

        List<String> keys = new ArrayList<String>(dict2.keySet());
        
        Collections.sort(keys);
        
        List<String> lines = new ArrayList<String>();
        for (String key : keys) {
            Collections.sort(dict2.get(key).get(paperId)); // sort the postings list
            String index = key + " : " + dict2.get(key).toString();
			lines.add(index);

		}
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

    
}
