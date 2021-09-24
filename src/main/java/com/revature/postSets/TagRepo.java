package com.revature.postSets;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;

public class TagRepo {
    private final DynamoDBMapper dbReader;

    public TagRepo() {
        dbReader = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    public List<Tag> findTags(List<String> tag_names) {


        dbReader.save(newSet);

    }
}