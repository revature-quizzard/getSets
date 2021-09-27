package com.revature.post_sets;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.revature.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class TagRepo {
    private final DynamoDBMapper dbReader;

    public TagRepo() {
        dbReader = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    public List<Tag> findTags(List<String> tag_names) {

        List<Tag> tagQuery = tag_names.stream().map(e -> (new Tag(e))).collect(Collectors.toList());

        List<Tag> result = dbReader.batchLoad(tagQuery)
                .values()
                .stream()
                .findFirst()
                .get()
                .stream()
                .map(e -> (Tag)e)
                .collect(Collectors.toList());

        return result;

    }
}