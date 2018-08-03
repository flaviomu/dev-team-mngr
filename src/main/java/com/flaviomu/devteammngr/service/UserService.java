package com.flaviomu.devteammngr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.repository.UserRepository;
import com.flaviomu.devteammngr.exception.BadRequestException;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(user));
        return users;
    }

    public User getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if (user == null)
            throw new UserNotFoundException();

        return user;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserWithPut(Long userId, User user) throws UserNotFoundException, BadRequestException {
        if (! userId.equals(user.getId()))
            throw new BadRequestException();

        if (userRepository.findUserById(userId) == null)
            throw new UserNotFoundException();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonParser jsonParser = new BasicJsonParser();

            String userJson = objectMapper.writeValueAsString(user);
            Map<String, Object> userMap = jsonParser.parseMap(userJson);

            for (String key : userMap.keySet()) {
                if ( (userMap.get(key) == null) || (userMap.get(key).equals("null")) )
                    throw new BadRequestException();
            }
        } catch (IOException e) {
            log.error("Error while updating the User: " + user.getId());
            e.printStackTrace();
            return null;
        }

        return userRepository.save(user);
    }

    public User updateUserWithPatch(Long userId, String patchUserJson) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if (user == null)
            throw new UserNotFoundException();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonParser jsonParser = new BasicJsonParser();

            String userJson = objectMapper.writeValueAsString(user);
            Map<String, Object> userMap = jsonParser.parseMap(userJson);

            Map<String, Object> patchUserMap = jsonParser.parseMap(patchUserJson);

            patchUserMap.keySet().forEach( key -> {
                    userMap.put(key, patchUserMap.get(key));
            });

            userJson = objectMapper.writeValueAsString(userMap);
            user = objectMapper.readValue(userJson, User.class);
        } catch (IOException e) {
            log.error("Error while updating the User: " + userId);
            e.printStackTrace();
            return null;
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (userRepository.findUserById(userId) == null)
            throw new UserNotFoundException();
        
        userRepository.deleteById(userId);
    }

}
