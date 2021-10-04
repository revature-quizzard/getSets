package com.revature.post_sets;
import com.revature.documents.Set;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.UUID;

public class SetRepo {

    private final DynamoDbTable<Set> setTable;

    public SetRepo(){
        DynamoDbClient db = DynamoDbClient.builder().httpClient(ApacheHttpClient.create()).build();
        DynamoDbEnhancedClient dbClient = DynamoDbEnhancedClient.builder().dynamoDbClient(db).build();
        setTable = dbClient.table("Sets", TableSchema.fromBean(Set.class));
    }


    public Set addSet(Set newSet){
        UUID uuid = UUID.randomUUID();
        newSet.setId(uuid.toString());
        setTable.putItem(newSet);
        return newSet;
    }


}
