package com.revature.post_sets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revature.documents.*;
import com.revature.documents.Set;
import com.revature.exceptions.ResourceNotFoundException;

import java.util.*;

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
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        responseEvent.setHeaders(headers);

        User caller = null;
        //Does this give a Username or Id?
        try{
            Object item = requestEvent.getRequestContext().getAuthorizer().get("claims");
            LinkedHashMap casted = (LinkedHashMap) item;
            String caller_id = (String) casted.get("sub");
            caller = userRepo.getUserById(caller_id);
        } catch(Exception e){
            responseEvent.setStatusCode(401);
            return responseEvent;
        }

        try{
            //getting data from request body
            responseSet = mapper.fromJson(requestEvent.getBody() , SetDto.class);

            //Get the author user, if the author doesn't exist, throw an exception
            User author = userRepo.getUser(responseSet.getAuthor());
            if(!author.equals(caller.getUsername())) {
                responseEvent.setStatusCode(403);
                return responseEvent;
            }

            // generating Set with Dto fields
            toSave.setSetName(responseSet.getSetName());
            toSave.setAuthor(responseSet.getAuthor());
            toSave.setPublic(responseSet.isPublic());

            /*
                since our api holds Tags as Strings for the SetDto,
                we convert the Data by getting all Tags by name ,
                and return them in a list.
            */

            List<Tag> trans_Tags = tagRepo.findTags(responseSet.getTags());
            logger.log("TAGS: " + trans_Tags + "\n");

            toSave.setTags(trans_Tags);
            toSave.setCards(new ArrayList<Card>());


            //Save the set to the setRepo and get the id back
            toSave = setRepo.addSet(toSave);

            //Save the set to the userRepo with the correct id
            userRepo.addSet(toSave, author);

            //All data is saved properly, return the set in the response body
            responseEvent.setBody(mapper.toJson(toSave));
            return responseEvent;
        }catch (ResourceNotFoundException rnfe){
            //Client Error
            responseEvent.setStatusCode(400);
            return responseEvent;
        } catch (Exception e) {
            //Unexpected Server Error
            System.out.println(e);
            responseEvent.setStatusCode(500);
            return responseEvent;
        }
    }
}
