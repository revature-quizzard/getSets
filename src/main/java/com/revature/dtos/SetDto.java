package com.revature.dtos;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.List;



@Data
@DynamoDBTable(tableName = "Sets")
public class SetDto {

    @DynamoDBAttribute
    private String name;

    @DynamoDBAttribute
    private List<String> tags;

    @DynamoDBAttribute
    private String author;

    @DynamoDBAttribute
    private boolean is_public;

    @DynamoDBAttribute
    private int views = 0;

    @DynamoDBAttribute
    private int plays = 0;

    @DynamoDBAttribute
    private int studies = 0;

    @DynamoDBAttribute
    private int favorites = 0;

}