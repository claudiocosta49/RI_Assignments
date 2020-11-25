import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CorpusReader {
    
    public Map<Integer, String> processData() {
        Map<Integer, String> data = new HashMap<>();


        //read file
        try {
            int count=0;
            Path filePath = Paths.get("all_sources_metadata_2020-03-13.csv");

            BufferedReader readData = Files.newBufferedReader(filePath);
            Iterable<CSVRecord> all = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(readData);
            for (CSVRecord row : all) {
                if(!row.get("abstract").equals("")  && !row.get("abstract").equals(null)){ //abstract can't be empty
                    String text = row.get("title") + " " + row.get("abstract");
                    data.put(count, text);
                    count+=1; //Increase ID
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Cannot access to file properly!");
        }
       
        return data;
    }
}


