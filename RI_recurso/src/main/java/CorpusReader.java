import java.io.*;
import java.nio.file.*;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CorpusReader {
    private String fileName;
    private List<Document> docList;

    
    public CorpusReader(String fileName, List<Document> docList) {
        this.fileName =  "C:\\document_parses\\pdf_json\\" + fileName;
        this.docList = docList;
    }

    public Document processData() throws JsonParseException, JsonMappingException, IOException {
        // read file
        Path filePath = Paths.get(fileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode docNode = mapper.readTree(filePath.toFile());

        Document doc = new Document();
        doc.setPaperId(docNode.get("paper_id").textValue());

        String title = docNode.get("metadata").get("title").textValue();
        String abstractText = "";
        for(int i = 0; i < docNode.get("abstract").size(); i++){
            abstractText = abstractText + docNode.get("abstract").get(i).get("text");
            
        }

        String bodyText = "";
        for(int i = 0; i < docNode.get("body_text").size(); i++){
            bodyText = bodyText + docNode.get("body_text").get(i).get("text");
            
        }
        String content = title + " " + abstractText + " " + bodyText;
        
        doc.setContent(content.replace("\"", "").strip());
        docList.add(doc);
        
    
    return doc;
    }}
        



