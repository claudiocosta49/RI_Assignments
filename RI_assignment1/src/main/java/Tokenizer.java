import java.util.List;
import java.util.Map;

public interface Tokenizer {
    Map<String, List<Integer>> generateTokens(Map<Integer, String> data, int docId);
}

