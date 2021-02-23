import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleTokenizer implements Tokenizer{
    public SimpleTokenizer(){
    }

    public Map<String, List<Integer>> generateTokens(Map<String, String> data, String paperId) {
        Map<String, List<Integer>> tokens = new HashMap<>();
        int pos = 0;
        
        for(String item : data.get(paperId).split("\\s+")) {
            item = item.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            if( item.length() > 3 ) {
                
                if( !tokens.containsKey(item) ){
                    tokens.put(item, new ArrayList<>());
                }
                pos +=1;
                
                tokens.get(item).add(pos);
                
            }
        }

        return tokens;
    }
}

