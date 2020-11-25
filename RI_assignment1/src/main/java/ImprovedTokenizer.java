import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ImprovedTokenizer implements Tokenizer{
    private Set<String> stopWords;
    private SnowballStemmer stemmer = new porterStemmer();
    private Pattern token_cleanse = Pattern.compile("^[a-z0-9]+(['\\@]?([a-z]){3,})+");
    private Pattern repeated = Pattern.compile("((\\w+)]{2,})\\1$");

    public ImprovedTokenizer(String stop_words_filename) throws IOException {
        this.stopWords = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(stop_words_filename));

        String line;
        while(br.readLine() != null){
            line = br.readLine().split(" ")[0];
            if(line.length()>0)
                this.stopWords.add(line);
        }
        br.close();
   
    }

    public Map<String, List<Integer>> generateTokens(Map<Integer, String> data, int docId){

        Map<String, List<Integer>> tokens = new HashMap<String, List<Integer>>();

        int position = 0;
        for(String item : this.repeated.matcher(data.get(docId)).replaceAll("").split("[.,!|/()?\\-\\s]+")){

            if(!this.token_cleanse.matcher(item).matches() || stopWords.contains(item))
                continue;

            this.stemmer.setCurrent(item);
            this.stemmer.stem();
            item = this.stemmer.getCurrent();

            if(item.length()>3) {
                if (!tokens.containsKey(item)) {
                    tokens.put(item, new ArrayList<>());
                }
                tokens.get(item).add(position++);
            }
        }

        return tokens;
    }
}