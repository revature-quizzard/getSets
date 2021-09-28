package com.revature.post_sets;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.revature.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {

    private final DynamoDBMapper dbReader;

    public UserRepo(){
        dbReader = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    public User getUser(String name) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(name));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("username = :val1").withExpressionAttributeValues(eav);

        List<User> author = dbReader.scan(User.class, scanExpression);
        if(author == null) {
            throw new ResourceNotFoundException();
        }
        return author.get(0);
    }

    public User addSet(Set newSet, User user){
        return user;
    }
}
