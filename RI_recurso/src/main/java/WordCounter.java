/**
* @author Cláudio Miguel Costa 85113
* @author Ricardo Magalhães 79923
*/

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class WordCounter {
    public WordCounter(){}
    public int count(String word, File file) throws FileNotFoundException {
        int count = 0;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String nextToken = scanner.next();
            if ( nextToken.split(":")[0].equalsIgnoreCase(word)){
                count++;
            }     
        }
        return count;
    }
}
