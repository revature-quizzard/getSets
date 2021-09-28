package com.revature.post_sets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revature.documents.*;

import java.util.ArrayList;
import java.util.List;

public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final TagRepo tagRepo;
    private final SetRepo setRepo;
    private final UserRepo userRepo;

    private static final Gson mapper = new GsonBuilder().setPrettyPrinting().create();

    public PostHandler() {
        this.tagRepo = new TagRepo();
        this.setRepo = new SetRepo();
        this.userRepo = new UserRepo();
    }

    public PostHandler(TagRepo tagRepo, SetRepo setRepo, UserRepo userRepo) {
        this.tagRepo = tagRepo;
        this.setRepo = setRepo;
        this.userRepo = userRepo;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Set toSave = new Set();
        LambdaLogger logger = context.getLogger();
        logger.log("RECEIVED EVENT: " + requestEvent);

        SetDto responseSet = null;

        try{
            //getting data from request body
            responseSet = mapper.fromJson(requestEvent.getBody() , SetDto.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        //Get the author user, if the author doesn't exist, throw an exception
        User author = userRepo.getUser(responseSet.getAuthor());

        // generating Set with Dto fields
        toSave.setName(responseSet.getName());
        toSave.setAuthor(responseSet.getAuthor());
        toSave.set_public(responseSet.is_public());

        /*
            since our api holds Tags as Strings for the SetDto,
            we convert the Data by getting all Tags by name ,
            and return them in a list.
        */

        List<Tag> trans_Tags = tagRepo.findTags(responseSet.getTags());
        logger.log("TAGS: " + trans_Tags + "\n");

        toSave.setTags(trans_Tags);
        toSave.setCards(new ArrayList<Card>());

        // setting response body to newly generated data
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();

        //Save the set to the setRepo and get the id back
        toSave = setRepo.addSet(toSave);

        //Save the set to the userRepo with the correct id
        userRepo.addSet(toSave, author);

        //All data is saved properly, return the set in the response body
        responseEvent.setBody(mapper.toJson(toSave));
        return responseEvent;
    }
}
