public class Document {
    private String paperId;
    private String content; //(title + abstract + body)
   

    public Document() {
        this.setPaperId(paperId);
        this.setContent(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPaperId() {
        return paperId;
    }
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }


    @Override
    public String toString() {
        return "{" +
            " paperId='" + getPaperId() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
    

    
}
