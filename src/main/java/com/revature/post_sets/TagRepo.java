package com.revature.post_sets;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.stream.Collectors;

public class TagRepo {
    private final DynamoDBMapper dbReader;

    public TagRepo() {
        dbReader = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    public List<Tag> findTags(List<String> tag_names) {

        List<Tag> tagsList = dbReader.batchLoad(tag_names).values().stream().map(e -> (Tag) e).collect(Collectors.toList());
        System.out.println("Tags List From Tag Repo : " + tagsList);
        return tagsList;

    }
}