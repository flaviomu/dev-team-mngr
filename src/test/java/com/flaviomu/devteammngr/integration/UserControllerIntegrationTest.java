package com.flaviomu.devteammngr.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.misc.Position;
import com.flaviomu.devteammngr.web.controller.UserController;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Defines the integration tests for the @{link {@link UserController}}
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    private static Logger log = LoggerFactory.getLogger(UserControllerIntegrationTest.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void createUser() {
        User user = new User("UserFirstname", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User userCreated = response.getBody();

        assertNotNull(userCreated);

        userCreated.setId(null); // to make it independent from the already existing user
        assertEquals(user, userCreated);
    }


    @Test
    public void getExistingUser() {
        getExistingUser("1");
    }

    private User getExistingUser(String userId) {
        ResponseEntity<User> response = restTemplate.getForEntity("/users/" + userId, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user = response.getBody();

        assertNotNull(user);

        log.debug("User retrieved: " + user.toString());

        return user;
    }

    @Test
    public void getNotExistingUser() {
        getNotExistingUser("-1");
    }

    private void getNotExistingUser(String userId) {
        ResponseEntity<User> response = restTemplate.getForEntity("/users/" + userId, User.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertNotNull(response.getBody());

        assertNull(response.getBody().getId());
        assertNull(response.getBody().getFirstname());
        assertNull(response.getBody().getSurname());
        assertNull(response.getBody().getPosition());
        assertNull(response.getBody().getGitHubUrl());
    }


    @Test
    public void getUsers() {
        ResponseEntity<List> response = restTemplate.getForEntity("/users/", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List users = response.getBody();

        assertNotNull(users);

        log.debug("Users retrieved:");
        for (Object u: users) {
            try {
                String userJson = objectMapper.writeValueAsString(u);
                User user = objectMapper.readValue(userJson, User.class);
                log.debug(user.toString());
            } catch (java.io.IOException e) {
                log.debug("User: null");
                e.printStackTrace();
            }
        }
    }


    @Test
    public void updateUserWithPut() {
        User updatedUser = new User("UserFirstnameUpdatedWithPut", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");
        updatedUser.setId(1L);

        restTemplate.put("/users/1", updatedUser);
        User retrievedUser = getExistingUser("1"); // retrieves again the user with id = 1

        assertNotNull(retrievedUser);
        assertEquals(updatedUser, retrievedUser);
    }


    @Test
    public void updateUserWithPutNotBeingExecutedBecauseOfBadRequest() {
        User updatedUser = new User("UserFirstnameUpdatedWithPut", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");
        updatedUser.setId(1L);
        updatedUser.setGitHubUrl(null); // creates a bad request

        restTemplate.put("/users/1", updatedUser);
        User retrievedUser = getExistingUser("1"); // retrieves again the user with id = 1

        assertNotNull(retrievedUser);
        assertNotEquals(updatedUser, retrievedUser);
    }


    @Test
    public void updateUserWithPatch() {
        User updatedUser = new User("UserFirstnameUpdatedWithPatch", "dev1b", Position.SENIOR_DEV, "https://github.com/dev1b");
        updatedUser.setId(1L);

        //restTemplate.patchForObject("/users/1", objectMapper.writeValueAsString(updatedUser), User.class);
        //
        // RestTemplate does not issue PATCH requests due to a bug in HttpURLConnection:
        //      https://jira.spring.io/browse/SPR-15052
        // Workaround at:
        //      https://rtmccormick.com/2017/07/30/solved-testing-patch-spring-boot-testresttemplate/

        RestTemplate patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        String userPatchJsonOnlyDifferentFields = "{\"firstname\":\"UserFirstnameUpdatedWithPatch\",\"position\":\"SENIOR_DEV\"}";
        ResponseEntity<User> response = patchRestTemplate.exchange("/users/1", HttpMethod.PATCH, getPostRequestHeaders(userPatchJsonOnlyDifferentFields), User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedUser, response.getBody());
    }

    private HttpEntity getPostRequestHeaders(String jsonPostBody) {
        List<MediaType> acceptTypes = new ArrayList<>();
        acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        reqHeaders.setAccept(acceptTypes);

        return new HttpEntity<>(jsonPostBody, reqHeaders);
    }


    @Test
    public void deleteExistingUser() {
        restTemplate.delete("/users/2");

        getNotExistingUser("2");
    }


    @Test
    public void deleteNotExistingUser() {
        restTemplate.delete("/users/-1");
    }


    @Test
    public void getRepositoriesOverview() throws Exception  {
        User user = new User("UserFirstname", "USerSurname", Position.SENIOR_DEV, "https://github.com/dev1b");
        user.setId(1L);

        ResponseEntity<List> response = restTemplate.getForEntity("/users/1/repositories", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List overviews = response.getBody();
        assertNotNull(overviews);

        log.debug("GHRepositoryOverviews retrieved:");
        for (Object o: overviews) {
            try {
                String overviewJson = objectMapper.writeValueAsString(o);
                GHRepositoryOverview overview = objectMapper.readValue(overviewJson, GHRepositoryOverview.class);
                log.debug(overview.toString());
            } catch (java.io.IOException e) {
                log.debug("Overview: null");
                e.printStackTrace();
            }
        }

        GHRepositoryOverview ghRepositoryOverview = new GHRepositoryOverview();
        ghRepositoryOverview.setOwnerName("dev1B");
        ghRepositoryOverview.setName("crate");
        ghRepositoryOverview.setDescription("");
        ghRepositoryOverview.setLanguage("JavaScript");

        String overviewJson = objectMapper.writeValueAsString(overviews.get(0));
        GHRepositoryOverview overview = objectMapper.readValue(overviewJson,GHRepositoryOverview.class);
        assertEquals(ghRepositoryOverview.getName(), overview.getName());
    }
}
