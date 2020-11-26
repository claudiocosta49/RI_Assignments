import java.io.IOException;
import java.util.*;
import java.io.File;
import java.nio.file.*;
public class Main {

    public static void main(String[] args) throws IOException {
        File deleteIndexFile = new File("invertedIndexWithCounts.txt");
        Long start = null;
        int vocabSize = 0;
        List<String> keysOrdered = new ArrayList<String>();
        Map<String, List<Integer>> tokens = new HashMap<String, List<Integer>>();

        try{
            Files.deleteIfExists(deleteIndexFile.toPath());
        }catch(IOException e){}

        
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


        CorpusReader reader = new CorpusReader();
        Map<Integer,String> aux = reader.processData();
        

        switch(tokenizerType){
            case "simple":
                SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
                for(int i = 0; i < aux.size(); i++) {
                    if(i == 0){
                        start = System.currentTimeMillis();
                    }
                    tokens = simpleTokenizer.generateTokens(aux, i);
                    Indexer indexer = new Indexer();
                    keysOrdered.addAll(indexer.index(tokens, i));         
                    vocabSize += indexer.getVocabSize();         
                }

            case "improved":
                ImprovedTokenizer improvedTokenizer = new ImprovedTokenizer(stopWordsFileName);
                for(int i = 0; i < aux.size(); i++) {
                    if(i == 0){
                        start = System.currentTimeMillis();
                    }
                    tokens = improvedTokenizer.generateTokens(aux, i);
                    Indexer indexer = new Indexer();               
                    keysOrdered.addAll(indexer.index(tokens, i));
                    vocabSize += indexer.getVocabSize();

                    
        }
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long stop = System.currentTimeMillis();
        long total = stop - start;
        long usedRAM = runtime.totalMemory() - runtime.freeMemory();
        
        
        System.out.println("+-------------------EXERCISE 4-------------------\n");
        System.out.println("*****4.1/4.2*****\n");
        String leftAlignFormat = "| %-20s | %-15s |%n";
        System.out.format("+---------------------+-------------------+%n");
        System.out.format("|            "+ tokenizerType + "Tokenizer          |%n");
        System.out.format("+---------------------+-------------------+%n");
        
        System.out.format(leftAlignFormat, "Total Indexing Time", String.valueOf(total/1000) + " seconds");
        System.out.format(leftAlignFormat, "Total Memory Used", String.valueOf(usedRAM/MEGABYTE) + "MB");
        System.out.format(leftAlignFormat, "Vocabulary Size", String.valueOf(vocabSize) + " entries");
        System.out.format("+--------------------+-------------------+%n");

        

        /*
        File indexFile = new File("invertedIndexWithCounts.txt");
        int c = 0;
        System.out.println("*****4.3*****\n");
        for(String key: keysOrdered ){
            WordCounter wordCounter = new WordCounter();

            if ( wordCounter.count(key, indexFile) == 1 && c < 10 ) {
                System.out.println(key);
                c++;
            }
        }*/

        




    }
       
        
        
        
        
        
        
        

        
       
        
       
       
       

       

    }
}




