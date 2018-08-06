package com.flaviomu.devteammngr.service.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.repository.UserRepository;
import com.flaviomu.devteammngr.exception.BadRequestException;
import com.flaviomu.devteammngr.exception.InternalServerErrorException;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import com.flaviomu.devteammngr.service.external.github.GitHubConnectionService;
import com.flaviomu.devteammngr.web.GHRepositoryOverview;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
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


/**
 * Defines the service providing the functionality to manage a @{@link User}
 *
 */
@Service
public class UserService {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GitHubConnectionService gitHubConnectionService;


    /**
     * Retrieves all the users
     *
     * @return the list of the @{link User}s existing
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(user));
        return users;
    }


    /**
     * Retrieves the user given its id
     *
     * @param userId the @{link User} id of the user to be retrieved
     * @return the @{link User} specified by the given id
     * @throws UserNotFoundException
     */
    public User getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if (user == null)
            throw new UserNotFoundException();

        return user;
    }


    /**
     * Creates a new @{link User}
     *
     * @param user the info correlated to the @{link User} to be created
     * @return the @{link User} created
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }


    /**
     * Updates a @{link User} overwriting completely the associated info with the ones given
     *
     * @param userId the @{link User} id of the user to be updated
     * @param user the @{link User} info to be saved
     * @return the @{link User} updated
     * @throws UserNotFoundException
     * @throws BadRequestException
     */
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


    /**
     * Updates a @{link User} overwriting only the fields given
     *
     * @param userId the @{link User} id of the user to be updated
     * @param patchUserJson the @{link User} fields to be saved
     * @return the @{link User} updated
     * @throws UserNotFoundException
     */
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


    /**
     * Deletes a @{link User} given its id
     *
     * @param userId the @{link User} id of the user to be deleted
     */
    public void deleteUser(Long userId) {
        if (userRepository.findUserById(userId) == null)
            throw new UserNotFoundException();

        userRepository.deleteById(userId);
    }


    /**
     * Retrieves the GitHub repositories associated to the @{link User} specified by the given id
     *
     * @param userId the @{link User} id
     * @return the list of @{link {@link GHRepositoryOverview}} owned by the specified user
     */
    public List<GHRepositoryOverview> getRepositoriesOverview(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null)
            throw new UserNotFoundException();

        GitHub gitHub = gitHubConnectionService.getGitHub();
        GHUser ghUser;
        List<GHRepositoryOverview> ghRepositoryOverviews = new ArrayList<>();

        String ghUsername = user.getGitHubUrl().split("/")[user.getGitHubUrl().split("/").length - 1];
        log.info("Getting Repositories Overviews for GitHub user: " + ghUsername);

        try {
            ghUser = gitHub.getUser(ghUsername);
        } catch (IOException e) {
            log.error("Error while retrieving GitHub user: " + ghUsername);
            e.printStackTrace();
            throw new InternalServerErrorException();
        }

        ghUser.listRepositories().asList().forEach(ghRepository -> {
            GHRepositoryOverview overview = new GHRepositoryOverview();
            overview.setOwnerName(ghRepository.getOwnerName());
            overview.setName(ghRepository.getName());
            overview.setDescription(ghRepository.getDescription());
            overview.setLanguage(ghRepository.getLanguage());
            ghRepositoryOverviews.add(overview);
        });

        log.debug("GitHub user: " + ghUsername + " - Repositories number: " + ghRepositoryOverviews.size());

        return ghRepositoryOverviews;
    }
}
