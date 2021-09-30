package com.revature.documents;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class Tag {

    private String tagName;
    private String tagColor;

    @DynamoDbPartitionKey
    public String getName() {
        return tagName;
    }

    public void setName(String name) {
        this.tagName = name;
    }

    @DynamoDbAttribute("tagColor")
    public String getColor() {
        return tagColor;
    }

    public void setColor(String color) {
        this.tagColor = color;
    }

    public Tag(String name){
        this.tagName = name;
    }

    public Tag() {
        super();
    }

}
