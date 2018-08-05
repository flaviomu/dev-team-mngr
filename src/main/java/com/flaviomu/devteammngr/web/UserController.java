package com.flaviomu.devteammngr.web;

import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.service.external.github.GitHubConnectionService;
import com.flaviomu.devteammngr.service.domain.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserService userService;

    @Autowired
    private GitHubConnectionService gitHubConnectionService;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@PathVariable(value = "userId") Long userId) {
        return userService.getUser(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User updateUserWithPut(@PathVariable(value = "userId") Long userId, @RequestBody User user) {
        return userService.updateUserWithPut(userId, user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User updateUserWithPatch(@PathVariable(value = "userId") Long userId, @RequestBody String userJson) {
        return userService.updateUserWithPatch(userId, userJson);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        userService.deleteUser(userId);
    }

    @RequestMapping(value = "/{userId}/repositories", method = RequestMethod.GET)
    @ResponseBody
    public List<GHRepositoryOverview> getRepositoriesOverview(@PathVariable(value = "userId") Long userId) {
        log.info(String.valueOf(gitHubConnectionService.getGitHub().hashCode()));

        return userService.getRepositoriesOverview(userId);
    }


}
