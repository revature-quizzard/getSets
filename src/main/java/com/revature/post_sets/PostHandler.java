package com.revature.post_sets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revature.dtos.SetDto;

import java.util.ArrayList;
import java.util.List;

public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final TagRepo tagRepo =  new TagRepo();
    private static final SetRepo setRepo =  new SetRepo();
    private static final Gson mapper = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Set toSave = new Set();
        LambdaLogger logger = context.getLogger();
        logger.log("RECEIVED EVENT: " + requestEvent);

        try{
            //getting data from request body

            SetDto responeSet = mapper.fromJson(requestEvent.getBody() , SetDto.class);
            // generating Set with Dto fields

            toSave.setName(responeSet.getName());
            toSave.setAuthor(responeSet.getAuthor());
            toSave.set_public(responeSet.is_public());
        /*
           since our api holds Tags as Strings for the SetDto,
           we convert the Data by getting all Tags by name ,
           and return them in a list.
         */

            List<Tag> trans_Tags = tagRepo.findTags(responeSet.getTags());
            logger.log("TAGS: " + trans_Tags);

            toSave.setTags(trans_Tags);
            toSave.setCards(new ArrayList<Card>());
            System.out.println( "Set From Request in Post Handler " + responeSet + " And Tag List " + trans_Tags);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        // setting response body to newly generated data
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(mapper.toJson(setRepo.addSet(toSave)));
        return responseEvent;
    }
}
