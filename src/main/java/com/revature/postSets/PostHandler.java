package com.revature.postSets;

import com.amazonaws.services.lambda.runtime.Context;
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

        //getting data from request body
        SetDto responeSet = mapper.fromJson(requestEvent.getBody() , SetDto.class);
        // generating Set with Dto fields
        Set toSave = new Set();
        toSave.setName(responeSet.getName());
        toSave.setAuthor(responeSet.getAuthor());
        toSave.set_public(responeSet.is_public());
        List<Tag> trans_Tags = tagRepo.findTags(responeSet.getTags());
        toSave.setTags(trans_Tags);
        toSave.setCards(new ArrayList<Card>());
        System.out.println( "Set From Request in Post Handler " + responeSet + " And Tag List " + trans_Tags);
        // setting response body to newly generated data
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(mapper.toJson(setRepo.addSet(toSave)));
        return responseEvent;
    }
}
