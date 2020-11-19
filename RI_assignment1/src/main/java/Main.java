import java.io.File;
import java.io.IOException;
import java.util.*;
import java.nio.file.*;

public class Main {

    public static void main(String[] args) throws IOException {
        CorpusReader reader = new CorpusReader();
        Map<Integer,String> aux= new HashMap<>();
        SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
        ImprovedTokenizer improvedTokenizer = new ImprovedTokenizer("snowball_stopwords_EN.txt");

        aux = reader.processData();

        Map<String, List<Integer>> tokens = simpleTokenizer.generateTokens(aux, 0);
        System.out.println("\n---------------TOKEN STRUCTURE - SIMPLE TOKENIZER---------------\n");
        System.out.print(tokens);
        System.out.println("\n----------------------------------------------------------------");
        tokens.clear();
        tokens = improvedTokenizer.generateTokens(aux, 0);

        System.out.println("\n---------------TOKEN STRUCTURE - IMPROVED TOKENIZER---------------\n");
        System.out.print(tokens);
        System.out.println("\n----------------------------------------------------------------");
       
       

    }



}
