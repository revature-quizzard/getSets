package com.revature.postSets;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SetRepo {

    private final DynamoDBMapper dbReader;

    public SetRepo(){
        dbReader = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }


    public void addSet(Set newSet){
        dbReader.save(newSet);
    }


}
