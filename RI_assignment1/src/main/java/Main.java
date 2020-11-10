import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        CorpusReader reader = new CorpusReader();
        Map<Integer,String> aux= new HashMap<>();
        aux = reader.ReadFile();
        //System.out.println(aux);

    }



}
