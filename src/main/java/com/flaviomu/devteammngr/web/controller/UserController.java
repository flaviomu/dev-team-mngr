package com.flaviomu.devteammngr.web.controller;

import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.service.external.github.GitHubService;
import com.flaviomu.devteammngr.service.domain.UserService;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * Defines the controller exposing the API for the DEV TEAM MANAGER Application
 */
@Api(value = "devteammngr", description = "Operations available on the Dev Team Manager application")
@RestController
@RequestMapping(value = "/users")
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserService userService;
    @Autowired
    private GitHubService gitHubService;


    /**
     * Creates a new user
     *
     * @param user the @{link User} to be created
     * @return the @{link User} created
     */
    @ApiOperation(value = "Add a user",
                  response = User.class)
    @RequestMapping(method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }


    /**
     * Retrieves a @{link User} given its id
     *
     * @param userId the @{link User} id of the user to be retrieved
     * @return the @{link User} specified by the given id
     */
    @ApiOperation(value = "Retrieve a user given its 'id' provided in the url",
                  response = User.class)
    @ApiResponse(code = 404,
                 message = "User not found.")
    @RequestMapping(value = "/{userId}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUser(@PathVariable(value = "userId") Long userId) {
        return userService.getUser(userId);
    }


    /**
     * Retrieves all the users
     *
     * @return the list of the @{link User}s existing
     */
    @ApiOperation(value = "Retrieve all the user existing",
                  response = List.class)
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getUsers() {
        return userService.getUsers();
    }


    /**
     * Updates a @{link User} overwriting completely the associated info with the ones given
     *
     * @param userId the @{link User} id of the user to be updated
     * @param user the @{link User} info to be saved
     * @return the @{link User} updated
     */
    @ApiOperation(value = "Update an existing user given its 'id' provided in the url",
                  response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404,
                         message = "User not found"),
            @ApiResponse(code = 400,
                         message = "Request and/or BodyRequest not correctly formatted")
    })
    @RequestMapping(value = "/{userId}",
                    method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User updateUserWithPut(@PathVariable(value = "userId") Long userId, @RequestBody User user) {
        return userService.updateUserWithPut(userId, user);
    }


    /**
     * Updates a @{link User} overwriting only the fields given
     *
     * @param userId the @{link User} id of the user to be updated
     * @param userJson the @{link User} fields to be saved
     * @return the @{link User} updated
     */
    @ApiOperation(value = "Update an existing user given its 'id' provided in the url",
                  response = User.class)
    @ApiResponse(code = 404,
                 message = "User not found")
    @RequestMapping(value = "/{userId}",
                    method = RequestMethod.PATCH,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public User updateUserWithPatch(@PathVariable(value = "userId") Long userId, @RequestBody String userJson) {
        return userService.updateUserWithPatch(userId, userJson);
    }


    /**
     * Deletes a @{link User} given its id
     *
     * @param userId the @{link User} id of the user to be deleted
     */
    @ApiOperation(value = "Remove a user given its 'id' provided in the url")
    @ApiResponse(code = 404,
                 message = "User not found")
    @RequestMapping(value = "/{userId}",
                    method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        userService.deleteUser(userId);
    }


    /**
     * Retrieves the GitHub repositories associated to the @{link User} specified by the given id
     *
     * @param userId the @{link User} id
     * @return the list of @{link {@link GHRepositoryOverview}} owned by the specified user
     */
    @ApiOperation(value = "Retrieve an overview of the GitHub repositories of a user given its 'id' provided in the url", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred")
    })
    @RequestMapping(value = "/{userId}/repositories", method = RequestMethod.GET)
    public List<GHRepositoryOverview> getRepositoriesOverview(@PathVariable(value = "userId") Long userId) {
        return userService.getRepositoriesOverview(userId);
    }

}
