package com.revature.post_sets;
import com.revature.documents.Set;
import com.revature.documents.Tag;
import com.revature.documents.User;
import com.revature.exceptions.ResourceNotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {

    private final DynamoDbTable<User> userTable;

    public UserRepo(){
        DynamoDbClient db = DynamoDbClient.builder().httpClient(ApacheHttpClient.create()).build();
        DynamoDbEnhancedClient dbClient = DynamoDbEnhancedClient.builder().dynamoDbClient(db).build();
        userTable = dbClient.table("Users", TableSchema.fromBean(User.class));
    }
    public User getUser(String name) {
        AttributeValue val = AttributeValue.builder().s(name).build();
        Expression filter = Expression.builder().expression("#a = :b") .putExpressionName("#a", "username") .putExpressionValue(":b", val).build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder().filterExpression(filter).build();

        User user = userTable.scan(request).stream().findFirst().orElseThrow(ResourceNotFoundException::new).items().get(0);
        System.out.println("USER WITH ID: " + user);
        return user;
    }


    public User addSet(Set newSet, User user){
        System.out.println(" ");
        System.out.println(newSet);
        System.out.println(user);
        //Create a UserSetDoc with correct Set data
        User.UserSetDoc doc = new User.UserSetDoc(newSet);

        if(user.getCreated_sets() == null) {
            user.setCreated_sets(new ArrayList<User.UserSetDoc>());
        }

        //Create a copy of created_sets
        List<User.UserSetDoc> temp = user.getCreated_sets();
        //Modify copy
        temp.add(doc);
        //Save copy
        user.setCreated_sets(temp);

        //Save user with updated fields to db
        user = userTable.updateItem(user);

        System.out.println(user);
        return user;
    }
}
