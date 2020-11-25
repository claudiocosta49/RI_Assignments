import java.io.IOException;
import java.util.*;
import java.io.File;
import java.nio.file.*;
public class Main {

    public static void main(String[] args) throws IOException {
        File deleteIndexFile = new File("invertedIndexWithCounts.txt");
        
        try{
            Files.deleteIfExists(deleteIndexFile.toPath());
        }catch(IOException e){}

        long start = System.currentTimeMillis();
        String stopWordsFileName = "";
        String tokenizerType = "";
        

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
                    Indexer indexer = new Indexer();
                    Map<String, List<Integer>> tokens = simpleTokenizer.generateTokens(aux, i);
                    indexer.index(tokens, i);

                }

            case "improved":
                ImprovedTokenizer improvedTokenizer = new ImprovedTokenizer(stopWordsFileName);
                for(int i = 0; i < aux.size(); i++) {
                    Indexer indexer = new Indexer();
                    Map<String, List<Integer>> tokens = improvedTokenizer.generateTokens(aux, i);
                    indexer.index(tokens, i);
        }
            
    }
       
        
        
        
        
        
        
        

        
       
        
       
       
       

       

    }
}




