package com.flaviomu.devteammngr.web;

import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.service.external.github.GitHubConnectionService;
import com.flaviomu.devteammngr.service.domain.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Api(value = "devteammngr", description = "Operations available on the Dev Team Manager application")
@RestController
@RequestMapping(value = "/users")
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserService userService;
    @Autowired
    private GitHubConnectionService gitHubConnectionService;


    @ApiOperation(value = "Add a user", response = User.class)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @ApiOperation(value = "Retrieve a user given its 'id' provided in the url", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found.")
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@PathVariable(value = "userId") Long userId) {
        return userService.getUser(userId);
    }

    @ApiOperation(value = "Retrieve all the user existing", response = List.class)
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @ApiOperation(value = "Update an existing user given its 'id' provided in the url", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Request and/or BodyRequest not correctly formatted")
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User updateUserWithPut(@PathVariable(value = "userId") Long userId, @RequestBody User user) {
        return userService.updateUserWithPut(userId, user);
    }

    @ApiOperation(value = "Update an existing user given its 'id' provided in the url", response = User.class)
    @ApiResponse(code = 404, message = "User not found")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User updateUserWithPatch(@PathVariable(value = "userId") Long userId, @RequestBody String userJson) {
        return userService.updateUserWithPatch(userId, userJson);
    }

    @ApiOperation(value = "Remove a user given its 'id' provided in the url")
    @ApiResponse(code = 404, message = "User not found")
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        userService.deleteUser(userId);
    }

    @ApiOperation(value = "Retrieve an overview of the GitHub repositories of a user given its 'id' provided in the url", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred")
    })
    @RequestMapping(value = "/{userId}/repositories", method = RequestMethod.GET)
    @ResponseBody
    public List<GHRepositoryOverview> getRepositoriesOverview(@PathVariable(value = "userId") Long userId) {
        log.info(String.valueOf(gitHubConnectionService.getGitHub().hashCode()));

        return userService.getRepositoriesOverview(userId);
    }


}
