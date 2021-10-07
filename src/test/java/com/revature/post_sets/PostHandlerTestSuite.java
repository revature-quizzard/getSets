package com.revature.post_sets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revature.documents.Set;
import com.revature.documents.SetDto;
import com.revature.documents.Tag;
import com.revature.documents.User;
import com.revature.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked"})
public class PostHandlerTestSuite {
    static final Gson mapper = new GsonBuilder().setPrettyPrinting().create();

    PostHandler sut;
    Context mockContext;
    TagRepo mockTagRepo;
    UserRepo mockUserRepo;
    SetRepo mockSetRepo;


    @BeforeEach
    public void caseSetUp() {
        mockTagRepo = mock(TagRepo.class);
        mockUserRepo = mock(UserRepo.class);
        mockSetRepo = mock(SetRepo.class);
        sut = new PostHandler(mockTagRepo, mockSetRepo, mockUserRepo);

        mockContext = mock(Context.class);
    }

    @AfterEach
    public void caseTearDown() {
        sut = null;
        reset(mockContext);
    }

    @Test
    public void given_validRequestandValidAuthUser_handlerPosts() {

        // Arrange
        APIGatewayProxyRequestEvent mockRequestEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        //Mock the authed user
        Map<String, Object> authorizer = new HashMap<>();
        LinkedHashMap<String, String> claims = new LinkedHashMap();
        claims.put("sub", "1234");
        authorizer.put("claims", claims);
        requestContext.setAuthorizer(authorizer);
        mockRequestEvent.setRequestContext(requestContext);

        //Mock the requestEvent
        mockRequestEvent.withPath("/sets");
        mockRequestEvent.withHttpMethod("POST");
        SetDto mockInput = new SetDto();
        mockInput.setAuthor("Jack");
        mockInput.setPublic(true);
        mockInput.setSetName("Test");
        List<String> stags = new ArrayList<>();
        stags.add("java");
        mockInput.setTags(stags);
        mockRequestEvent.withBody(mapper.toJson(mockInput));
        mockRequestEvent.withQueryStringParameters(null);

        //Mock user
        User user = new User();
        user.setUsername("Jack");

        //Mock tags
        List<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setTagName("java");
        t.setTagColor("blue");
        tags.add(t);

        //Mock posted set
        Set set = new Set();
        set.setPublic(mockInput.isPublic());
        set.setId("123");
        set.setSetName(mockInput.getSetName());
        set.setAuthor(mockInput.getAuthor());
        set.setTags(tags);
        set.setCards(new ArrayList<>());

        when(mockUserRepo.getUserById(any())).thenReturn(user);
        when(mockUserRepo.getUser(any())).thenReturn(user);
        when(mockTagRepo.findTags(Mockito.any())).thenReturn(tags);
        when(mockSetRepo.addSet(Mockito.any())).thenReturn(set);

        //Mock expectedResponse
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        expectedResponse.setHeaders(headers);
        expectedResponse.setStatusCode(201);
        expectedResponse.setBody(mapper.toJson(set));

        // Act
        APIGatewayProxyResponseEvent actualResponse = sut.handleRequest(mockRequestEvent, mockContext);

        // Assert
        verify(mockUserRepo, times(1)).getUserById(any());
        verify(mockUserRepo, times(1)).getUser(any());
        verify(mockUserRepo, times(1)).addSet(any(), any());
        verify(mockTagRepo, times(1)).findTags(any());
        verify(mockSetRepo, times(1)).addSet(any());
        System.out.println(expectedResponse);
        System.out.println(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void given_validRequestandInvalidAuthUser_handlerThrows401() {

        // Arrange
        APIGatewayProxyRequestEvent mockRequestEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        mockRequestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext());

        //Mock the requestEvent
        mockRequestEvent.withPath("/sets");
        mockRequestEvent.withHttpMethod("POST");
        SetDto mockInput = new SetDto();
        mockInput.setAuthor("Jack");
        mockInput.setPublic(true);
        mockInput.setSetName("Test");
        List<String> stags = new ArrayList<>();
        stags.add("java");
        mockInput.setTags(stags);
        mockRequestEvent.withBody(mapper.toJson(mockInput));
        mockRequestEvent.withQueryStringParameters(null);

        //Mock user
        User user = new User();
        user.setUsername("Jack");

        //Mock tags
        List<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setTagName("java");
        t.setTagColor("blue");
        tags.add(t);

        //Mock posted set
        Set set = new Set();
        set.setPublic(mockInput.isPublic());
        set.setId("123");
        set.setSetName(mockInput.getSetName());
        set.setAuthor(mockInput.getAuthor());
        set.setTags(tags);
        set.setCards(new ArrayList<>());

        when(mockUserRepo.getUserById(any())).thenReturn(user);
        when(mockUserRepo.getUser(any())).thenReturn(user);
        when(mockTagRepo.findTags(Mockito.any())).thenReturn(tags);
        when(mockSetRepo.addSet(Mockito.any())).thenReturn(set);

        //Mock expectedResponse
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        expectedResponse.setHeaders(headers);
        expectedResponse.setStatusCode(401);

        // Act
        APIGatewayProxyResponseEvent actualResponse = sut.handleRequest(mockRequestEvent, mockContext);

        // Assert
        verify(mockUserRepo, times(0)).getUserById(any());
        verify(mockUserRepo, times(0)).getUser(any());
        verify(mockUserRepo, times(0)).addSet(any(), any());
        verify(mockTagRepo, times(0)).findTags(any());
        verify(mockSetRepo, times(0)).addSet(any());
        System.out.println(expectedResponse);
        System.out.println(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void given_validRequestandInvalidAuthor_handlerThrows400() {

        // Arrange
        APIGatewayProxyRequestEvent mockRequestEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        //Mock the authed user
        Map<String, Object> authorizer = new HashMap<>();
        LinkedHashMap<String, String> claims = new LinkedHashMap();
        claims.put("sub", "1234");
        authorizer.put("claims", claims);
        requestContext.setAuthorizer(authorizer);
        mockRequestEvent.setRequestContext(requestContext);

        //Mock the requestEvent
        mockRequestEvent.withPath("/sets");
        mockRequestEvent.withHttpMethod("POST");
        SetDto mockInput = new SetDto();
        mockInput.setAuthor("Jack");
        mockInput.setPublic(true);
        mockInput.setSetName("Test");
        List<String> stags = new ArrayList<>();
        stags.add("java");
        mockInput.setTags(stags);
        mockRequestEvent.withBody(mapper.toJson(mockInput));
        mockRequestEvent.withQueryStringParameters(null);

        //Mock user
        User user = new User();
        user.setUsername("Jack");

        //Mock tags
        List<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setTagName("java");
        t.setTagColor("blue");
        tags.add(t);

        //Mock posted set
        Set set = new Set();
        set.setPublic(mockInput.isPublic());
        set.setId("123");
        set.setSetName(mockInput.getSetName());
        set.setAuthor(mockInput.getAuthor());
        set.setTags(tags);
        set.setCards(new ArrayList<>());

        when(mockUserRepo.getUserById(any())).thenReturn(user);
        when(mockUserRepo.getUser(any())).thenThrow(new ResourceNotFoundException());
        when(mockTagRepo.findTags(Mockito.any())).thenReturn(tags);
        when(mockSetRepo.addSet(Mockito.any())).thenReturn(set);

        //Mock expectedResponse
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        expectedResponse.setHeaders(headers);
        expectedResponse.setStatusCode(400);

        // Act
        APIGatewayProxyResponseEvent actualResponse = sut.handleRequest(mockRequestEvent, mockContext);

        // Assert
        verify(mockUserRepo, times(1)).getUserById(any());
        verify(mockUserRepo, times(1)).getUser(any());
        System.out.println(expectedResponse);
        System.out.println(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void given_validRequestandUnexpectedError_handlerThrows500() {

        // Arrange
        APIGatewayProxyRequestEvent mockRequestEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        //Mock the authed user
        Map<String, Object> authorizer = new HashMap<>();
        LinkedHashMap<String, String> claims = new LinkedHashMap();
        claims.put("sub", "1234");
        authorizer.put("claims", claims);
        requestContext.setAuthorizer(authorizer);
        mockRequestEvent.setRequestContext(requestContext);

        //Mock the requestEvent
        mockRequestEvent.withPath("/sets");
        mockRequestEvent.withHttpMethod("POST");
        SetDto mockInput = new SetDto();
        mockInput.setAuthor("Jack");
        mockInput.setPublic(true);
        mockInput.setSetName("Test");
        List<String> stags = new ArrayList<>();
        stags.add("java");
        mockInput.setTags(stags);
        mockRequestEvent.withBody(mapper.toJson(mockInput));
        mockRequestEvent.withQueryStringParameters(null);

        //Mock user
        User user = new User();
        user.setUsername("Jack");

        //Mock tags
        List<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setTagName("java");
        t.setTagColor("blue");
        tags.add(t);

        //Mock posted set
        Set set = new Set();
        set.setPublic(mockInput.isPublic());
        set.setId("123");
        set.setSetName(mockInput.getSetName());
        set.setAuthor(mockInput.getAuthor());
        set.setTags(tags);
        set.setCards(new ArrayList<>());

        when(mockUserRepo.getUserById(any())).thenReturn(user);
        when(mockUserRepo.getUser(any())).thenThrow(new NullPointerException());
        when(mockTagRepo.findTags(Mockito.any())).thenReturn(tags);
        when(mockSetRepo.addSet(Mockito.any())).thenReturn(set);

        //Mock expectedResponse
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        expectedResponse.setHeaders(headers);
        expectedResponse.setStatusCode(500);

        // Act
        APIGatewayProxyResponseEvent actualResponse = sut.handleRequest(mockRequestEvent, mockContext);

        // Assert
        verify(mockUserRepo, times(1)).getUserById(any());
        verify(mockUserRepo, times(1)).getUser(any());
        System.out.println(expectedResponse);
        System.out.println(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void given_validRequestandAuthorNotEqualsCaller_handlerThrows403() {

        // Arrange
        APIGatewayProxyRequestEvent mockRequestEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        //Mock the authed user
        Map<String, Object> authorizer = new HashMap<>();
        LinkedHashMap<String, String> claims = new LinkedHashMap();
        claims.put("sub", "1234");
        authorizer.put("claims", claims);
        requestContext.setAuthorizer(authorizer);
        mockRequestEvent.setRequestContext(requestContext);

        //Mock the requestEvent
        mockRequestEvent.withPath("/sets");
        mockRequestEvent.withHttpMethod("POST");
        SetDto mockInput = new SetDto();
        mockInput.setAuthor("Jack");
        mockInput.setPublic(true);
        mockInput.setSetName("Test");
        List<String> stags = new ArrayList<>();
        stags.add("java");
        mockInput.setTags(stags);
        mockRequestEvent.withBody(mapper.toJson(mockInput));
        mockRequestEvent.withQueryStringParameters(null);

        //Mock users
        User user = new User();
        user.setUsername("Jack");

        User user2 = new User();
        user2.setUsername("Not Jack");

        //Mock tags
        List<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setTagName("java");
        t.setTagColor("blue");
        tags.add(t);

        //Mock posted set
        Set set = new Set();
        set.setPublic(mockInput.isPublic());
        set.setId("123");
        set.setSetName(mockInput.getSetName());
        set.setAuthor(mockInput.getAuthor());
        set.setTags(tags);
        set.setCards(new ArrayList<>());

        when(mockUserRepo.getUserById(any())).thenReturn(user);
        when(mockUserRepo.getUser(any())).thenReturn(user2);
        when(mockTagRepo.findTags(Mockito.any())).thenReturn(tags);
        when(mockSetRepo.addSet(Mockito.any())).thenReturn(set);

        //Mock expectedResponse
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization");
        headers.put("Access-Control-Allow-Origin", "*");
        expectedResponse.setHeaders(headers);
        expectedResponse.setStatusCode(403);

        // Act
        APIGatewayProxyResponseEvent actualResponse = sut.handleRequest(mockRequestEvent, mockContext);

        // Assert
        verify(mockUserRepo, times(1)).getUserById(any());
        verify(mockUserRepo, times(1)).getUser(any());
        System.out.println(expectedResponse);
        System.out.println(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

}
