package com.revature.documents;
import java.util.List;


//This class represents the fields of a Set that we expect to receive in a POST call
public class SetDto {

    private String set_name;
    private List<String> tags;
    private String author;
    private boolean is_public;

    public String getName() {
        return set_name;
    }

    public void setName(String name) {
        this.set_name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean is_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
}