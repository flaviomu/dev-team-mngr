package com.flaviomu.devteammngr.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.misc.Position;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import com.flaviomu.devteammngr.service.domain.UserService;
import com.flaviomu.devteammngr.service.external.github.GitHubService;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Defines the tests for the @{link {@link UserController}}
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static Logger log = LoggerFactory.getLogger(UserControllerTest.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;
    @MockBean
    private GitHubService gitHubService;


    @Test
    public void createUser() throws Exception {
        User user = new User("UserFirstname", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");
        user.setId(1L);

        when(userService.createUser(user)).thenReturn(user);

        String userJson = objectMapper.writeValueAsString(user);
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/users")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .content(userJson))
                                                //.andDo(print())
                                                .andReturn()
                                                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());
        assertEquals(userJson, response.getContentAsString());
    }


    @Test
    public void getUser() throws Exception {
        User user = new User("UserFirstname", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");
        user.setId(1L);

        when(userService.getUser(1L)).thenReturn(user);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/users/1")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                                .andReturn()
                                                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());
    }


    @Test
    public void getUsers() throws Exception {
        User user1 = new User("User1fn", "User1sn", Position.JUNIOR_DEV, "https://github.com/user1");
        user1.setId(1L);
        User user2 = new User("User2fn", "User2sn", Position.INTERMEDIATE_DEV, "https://github.com/user2");
        user2.setId(2L);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userService.getUsers()).thenReturn(users);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/users")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                                .andReturn()
                                                .getResponse();


        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List usersReceived = objectMapper.readValue(response.getContentAsString(), List.class);
        String user1receivedJson = objectMapper.writeValueAsString(usersReceived.get(0));
        User user1Received = objectMapper.readValue(user1receivedJson, User.class);
        String user2receivedJson = objectMapper.writeValueAsString(usersReceived.get(1));
        User user2Received = objectMapper.readValue(user2receivedJson, User.class);

        assertEquals(1L, user1Received.getId().longValue());
        assertEquals(user2.getFirstname(), user2Received.getFirstname());
    }


    @Test
    public void updateUserWithPut() throws Exception {
        User user = new User("UserName", "UserSurname", Position.SENIOR_DEV, "https://github.com/user");
        user.setId(1L);
        user.setFirstname("UpdatedUsernName");

        when(userService.updateUserWithPut(user.getId(), user)).thenReturn(user);

        String userJson = objectMapper.writeValueAsString(user);
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.put("/users/1")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .content(userJson))
                                                //.andDo(print())
                                                .andReturn()
                                                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());
    }


    @Test
    public void updateUserWithPatch() throws Exception {
        User user = new User("UserName", "UserSurname", Position.JUNIOR_DEV, "https://github.com/user");
        user.setId(1L);
        user.setFirstname("UserNameUpdated");   // update the user for the test comparison
        user.setPosition(Position.SENIOR_DEV);  // update the user for the test comparison

        User userPatch = new User("UserNameUpdated", "UserSurname", null, "null");

        String userPatchJsonOnlyDifferentFields = "{\"firstname\":\"UserNameUpdated\",\"position\":\"SENIOR_DEV\"}";

        when(userService.updateUserWithPatch(user.getId(), userPatchJsonOnlyDifferentFields)).thenReturn(user);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.patch("/users/1")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                                                                .content(userPatchJsonOnlyDifferentFields))
                                                //.andDo(print())
                                                .andReturn()
                                                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());
    }


    @Test
    public void deleteExistingUser() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    @Test
    public void deleteNotExistingUser() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).deleteUser(-1L);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/users/-1"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    public void getRepositoriesOverview() throws Exception {
        User user = new User("UserFirstname", "USerSurname", Position.SENIOR_DEV, "https://github.com/user");
        user.setId(1L);

        GHRepositoryOverview ghRepositoryOverview = new GHRepositoryOverview();
        ghRepositoryOverview.setOwnerName("user");
        ghRepositoryOverview.setName("user-repository");
        ghRepositoryOverview.setDescription("My first repository");
        ghRepositoryOverview.setLanguage("Java");

        List<GHRepositoryOverview> ghRepositoryOverviews = new ArrayList<>();
        ghRepositoryOverviews.add(ghRepositoryOverview);

        when(userService.getRepositoriesOverview(1L)).thenReturn(ghRepositoryOverviews);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/users/1/repositories")
                                                                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                                //.andDo(print())
                                                .andReturn()
                                                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List ghRepositoryOverviewsReceived = objectMapper.readValue(response.getContentAsString(), List.class);
        String ghRepositoryOverviewReceivedJson = objectMapper.writeValueAsString(ghRepositoryOverviewsReceived.get(0));
        GHRepositoryOverview ghRepositoryOverviewReceived = objectMapper.readValue(ghRepositoryOverviewReceivedJson, GHRepositoryOverview.class);

        assertEquals(ghRepositoryOverviewReceived.getName(), ghRepositoryOverview.getName());
    }
}