/**
* @author Cláudio Miguel Costa 85113
* @author Ricardo Magalhães 79923
*/

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

public class Indexer {
    private int vocabSize;
	private int memorySize;
	private int blockNumber;
	private List<Document> docList;
	private Map<String, Map<String, List<Integer>>> dict;
    private List<Integer> blockPostingsList;
	private HashMap<String, Double> termTf;
	private HashMap<String, Double> termIdf;
    private int blockSize;
	private int N;

    public Indexer(int blockSize, int memorySize, int blockNumber, List<Document> docList, int N, HashMap<String, Double> termTf) {
        this.vocabSize = 0;
        this.blockSize = blockSize;
        this.blockNumber = blockNumber;
		this.memorySize = memorySize;
        this.docList = docList;
        this.dict = new LinkedHashMap<>();
		this.termIdf = new LinkedHashMap<>();
		this.termTf = termTf;
		this.N = N;
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
				DecimalFormat df = new DecimalFormat("#.####");
				if(!termTf.containsKey(term)){
					this.termTf.put(term, 1.0);
				}
				double tf = (double) 1 + Math.log10(termPositions.size());    
				this.termTf.put(term, Double.parseDouble(df.format(tf)));    
				
                if (dict.get(term) == null) {
                    postingsList = this.addToDict(dict, term, termPositions, paperId);
                    
                }else {
                    postingsList = dict.get(term).get(paperId);	
                }				
            }             
        }
        if(!dict.isEmpty()){
			blockNumber++;
            sortAndWriteBlock(dict, paperId);
			calculateNormalizedWeights(termTf);
            vocabSize = dict.size();
            Map<String,Map<String,List<Integer>>> mergedBlocks = mergeAllBlocks();
			writeMergedDict(mergedBlocks, termTf);
        }   
        
    }

    public List<Integer> addToDict(Map<String, Map<String,List<Integer>>> dict, String term,List<Integer> termPositions, String paperId) {
		//adds terms and postings list to index
        Map<String,List<Integer>> aux = new HashMap<>();
        List<Integer> postingsList = new ArrayList<Integer>();
        postingsList.addAll(termPositions);
        aux.put(paperId, postingsList);
        dict.put(term, aux);    
        return postingsList;
    }

    public int getVocabSize(){
        return this.vocabSize;
    }
	public int getBlockNumber(){
		return this.blockNumber;
	}
    private void sortAndWriteBlock(Map<String, Map<String, List<Integer>>> dict, String paperId) { //writes block to output block file
        Path file = Paths.get("positionIndexes/block" + this.blockNumber + ".txt");
        List<String> keys = new ArrayList<String>(dict.keySet());
        Collections.sort(keys);
        
        List<String> lines = new ArrayList<String>();
        for (String key : keys) {
            Collections.sort(dict.get(key).get(paperId));
            String index = key + " : " + dict.get(key).toString();
			lines.add(index);

		}
		try {
			Files.write(file, lines);
           
		} catch (IOException e) {
			e.printStackTrace();
		}

        
	}

    public Map<String,Map<String,List<Integer>>> mergeAllBlocks(){	//merges all block files into memory
		Map<String,Map<String,List<Integer>>> mergedBlocks = new LinkedHashMap<String,Map<String,List<Integer>>>();

        for( int i = 1; i<this.blockNumber; i++){
            Map<String,Map<String,List<Integer>>> blockDict = this.blockToDict("positionIndexes/block"+i+".txt");
        
            //Merging the terms of the two hashmaps together to scan block files sequentially
            List<String> mergedSortedTerms = mergeBlocks(new HashMap<>(),blockDict);
			for(String term : mergedSortedTerms){
				//If we have both terms, merge the two
				String paperId = blockDict.get(term).toString().substring(1,41);
				if(!mergedBlocks.containsKey(term)){
					mergedBlocks.put(term, new HashMap<String, List<Integer>>());
				}
				mergedBlocks.get(term).put(paperId, blockDict.get(term).get(paperId));
				}
        }
		return mergedBlocks;
    }

    public static List<String> mergeBlocks(Map<String,Map<String,List<Integer>>> map1,Map<String,Map<String,List<Integer>>> map2){ //merges two given maps
		
		Set<String> terms1 = map1.keySet();
		Set<String> terms2 = map2.keySet();		
		
	    Iterator<String> iter1 = terms1.iterator();
	    Iterator<String> iter2 = terms2.iterator();

	    List<String> merged = new ArrayList<String>();

	    String value1 = (iter1.hasNext() ? iter1.next() : null);
	    String value2 = (iter2.hasNext() ? iter2.next() : null);
	    
		// Loop while values remain in either list
	    while (value1 != null || value2 != null) {
	    if(value2 == null){
	    	merged.add(value1);
            value1 = (iter1.hasNext() ? iter1.next() : null);
	    }else if(value1 == null){
	    	merged.add(value2);
            value2 = (iter2.hasNext() ? iter2.next() : null);
	    }
	    else if(value1.equals(value2)){
            merged.add(value1);
            value1 = (iter1.hasNext() ? iter1.next() : null);
            value2 = (iter2.hasNext() ? iter2.next() : null);

	    } else if (value1 != null && value1.compareTo(value2) <= 0) {

	            merged.add(value1);
	            value1 = (iter1.hasNext() ? iter1.next() : null);

	        } else {
	            merged.add(value2);
	            value2 = (iter2.hasNext() ? iter2.next() : null);
	        }
	    }
	    // Return merged result
	    return merged;			
	}
    

    public Map<String,Map<String,List<Integer>>> blockToDict(String blockFileName){
		Map<String, Map<String, List<Integer>>> blockDict = new LinkedHashMap<String, Map<String,List<Integer>>>();	
		
		try (Stream<String> stream = Files.lines(Paths.get(blockFileName))) { //save partial positional index to memory
			this.blockPostingsList = new ArrayList<Integer>();
			stream.forEach(line -> {
				//filling up the dict
				String term = line.split(" :")[0];
                String paperId = line.split(" :")[1].substring(2,42);
			    
				this.blockPostingsList = getLinePostings(line);
				Map<String, List<Integer>> aux = new HashMap<String, List<Integer>>();
                aux.put(paperId, blockPostingsList);
				blockDict.put(term, aux);
				
			} );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return blockDict;
	}
	private void writeMergedDict(Map<String,Map<String, List<Integer>>> dict, Map<String, Double> termTf){ //writes output file
			Path file = Paths.get("positionMergedIndex.txt");
			List<String> keys = new ArrayList<String>(dict.keySet());
			List<String> lines = new ArrayList<String>();

			for(String key : keys){
				double df = dict.get(key).size();
				DecimalFormat decFormat = new DecimalFormat("#.####");
				double idf = Math.log10(docList.size()/df);
				String index = key + ":" + decFormat.format(idf) + ";";
				double tf = termTf.get(key);
				String weight = decFormat.format(tf * idf);

				for(Map.Entry<String, List<Integer>> entry : dict.get(key).entrySet()){
					String docId = entry.getKey();

					List<Integer> positions = entry.getValue();
					index += docId+ ":" + weight + ":" + positions.toString().replaceAll("\\[", "").replaceAll("\\]", "") + ";";
				}	
				lines.add(index);
				Collections.sort(lines);
			}	
			try {
				Files.write(file, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

    private static List<Integer> getLinePostings(String line){
		List<Integer> postingsList = new ArrayList<Integer>();
		line = line.replaceAll("]", ""); 	
		 String[] lineComponents = line.split("\\["); //delimited with ','
		 String[] postings = lineComponents[1].replaceAll("}", "").split(",");
		 
		 //converting from array to postingsList
		 
		 for(String s : postings){
			 s = s.trim();
			 postingsList.add(Integer.valueOf(s));
		 }
		return postingsList;
	}
	
	private void calculateNormalizedWeights(HashMap<String, Double> termTf) {
        double sumDocLen = 0;
        for (String term : termTf.keySet()) {
            sumDocLen += Math.pow((termTf.get(term)),2);
        }
        double squaredDocLen = Math.sqrt(sumDocLen);
		DecimalFormat df = new DecimalFormat("#.####");
        for (String term:termTf.keySet()) {
            double normalizedWeight = termTf.get(term)/squaredDocLen;

            termTf.put(term, Double.parseDouble(df.format(normalizedWeight)));
        }
    }

    
}
