package com.flaviomu.devteammngr.web;

import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import com.flaviomu.devteammngr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

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

}
