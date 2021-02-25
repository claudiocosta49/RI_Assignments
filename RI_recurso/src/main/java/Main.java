import java.io.IOException;
import java.util.*;
import java.io.File;
public class Main {

    public static void main(String[] args) throws IOException {
        Long start = null;
        int vocabSize = 0;
        List<String> keysOrdered = new ArrayList<String>();
        List<Document> docList = new ArrayList<Document>();
        Map<String, List<Integer>> tokens = new HashMap<String, List<Integer>>();

        
        File folder = new File(".");

        for (File file : folder.listFiles()) {                 //prevent file accumulation
            if (file.getName().startsWith("block")) {
                file.delete();
         }
        }
        
        String stopWordsFileName = "";
        String tokenizerType = "";
        final long MEGABYTE = 1024L * 1024L;

        if(args.length == 4){
            if(args[0].equalsIgnoreCase("-f") && args[2].equalsIgnoreCase("-t")){
                if(args[3].equalsIgnoreCase("simple") || args[3].equalsIgnoreCase("improved")){
                    stopWordsFileName = args[1];
                    tokenizerType = args[3].toLowerCase();
                }else{
                    System.err.println("ERROR: Tokenizer parameter usage: -t \"simple\"/\"improved\"");
                    return;
                }
            }else{
                System.err.println("ERROR: Usage: mvn exec:java -D exec.mainClass=\"Main\" -D exec.args=\"-f stopWordsFileName -t tokenizerType\"");
                return;
            }
        }else{
            System.err.println("ERROR: Usage: mvn exec:java -D exec.mainClass=\"Main\" -D exec.args=\"-f stopWordsFileName -t tokenizerType\"");
            return;
        }

        
        File f = new File("C:\\document_parses\\pdf_json\\");
        File[] files = f.listFiles();

        Map<String, String> aux = new HashMap<String, String>();
        for(int i = 0; i < 100; i++){                                 //testing for 100 docs
            CorpusReader reader = new CorpusReader(files[i].getName(), docList);
            Document doc = reader.processData();
            aux.put(doc.getPaperId(), doc.getContent());
        }
        int N = docList.size();


        switch(tokenizerType){
            case "simple":
                
                SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
                start = System.currentTimeMillis();
                int blockNumber = 0;
                
                for(String key: aux.keySet()){
                    tokens = simpleTokenizer.generateTokens(aux, key);

                    //Iterator<Document> documentStream = docList.iterator();
                    
                    Indexer indexer = new Indexer(2000, 650000, blockNumber, docList, N);
                    indexer.index(tokens, key);
                    blockNumber = indexer.getBlockNumber();    
                    vocabSize += indexer.getVocabSize();
                }
                
               

            case "improved":
                ImprovedTokenizer improvedTokenizer = new ImprovedTokenizer(stopWordsFileName);
                start = System.currentTimeMillis();
                blockNumber = 0;
                for(String key: aux.keySet()){
                    tokens = improvedTokenizer.generateTokens(aux, key);
                    Indexer indexer = new Indexer(2000, 650000, blockNumber, docList, N);
                    indexer.index(tokens, key);
                    blockNumber = indexer.getBlockNumber();
                
                    vocabSize += indexer.getVocabSize();
                }
                    
        }
        //System.out.println(tokens);
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long stop = System.currentTimeMillis();
        long total = stop - start;
        long usedRAM = runtime.totalMemory() - runtime.freeMemory();
        
        
        
        String leftAlignFormat = "| %-20s | %-15s |%n";
        System.out.format("+---------------------+-------------------+%n");
        System.out.format("|            "+ tokenizerType + "Tokenizer          |%n");
        System.out.format("+---------------------+-------------------+%n");
        
        System.out.format(leftAlignFormat, "Total Indexing Time", String.valueOf(total/1000) + " seconds");
        System.out.format(leftAlignFormat, "Total Memory Used", String.valueOf(usedRAM/MEGABYTE) + "MB");
        System.out.format(leftAlignFormat, "Vocabulary Size", String.valueOf(vocabSize) + " entries");
        System.out.format("+--------------------+-------------------+%n");



    }
       
  
    }





