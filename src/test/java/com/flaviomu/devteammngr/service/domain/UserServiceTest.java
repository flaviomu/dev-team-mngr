package com.flaviomu.devteammngr.service.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.misc.Position;
import com.flaviomu.devteammngr.data.repository.UserRepository;
import com.flaviomu.devteammngr.exception.BadRequestException;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import com.flaviomu.devteammngr.service.external.github.GitHubService;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Defines the tests for the @{link {@link UserService}}
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;
    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private UserService userService;


    private User user1;
    private User user2;
    private List<User> initialUsers;


    @Before
    public void setUp() {
        user1 = new User("User1fn", "User1sn", Position.JUNIOR_DEV, "https://github.com/user1");
        user1.setId(1L);
        user2 = new User("User2fn", "User2sn", Position.INTERMEDIATE_DEV, "https://github.com/user2");
        user2.setId(2L);
        initialUsers = new ArrayList<>();
        initialUsers.add(user1);
        initialUsers.add(user2);
    }


    @Test
    public void getUsers() {
        when(userRepository.findAll()).thenReturn(initialUsers);

        List<User> users = new ArrayList<>(userService.getUsers());

        assertEquals(initialUsers.size(), users.size());
    }


    @Test
    public void getExistingUser() {
        when(userRepository.findUserById(1L)).thenReturn(user1);
        when(userRepository.findUserById(2L)).thenReturn(user2);

        assertEquals(userRepository.findUserById(1L).getFirstname(), user1.getFirstname());
        assertEquals(userRepository.findUserById(2L).getSurname(), user2.getSurname());
    }


    @Test(expected = UserNotFoundException.class)
    public void getNotExistingUser() {
        when(userRepository.findUserById(-1L)).thenThrow(UserNotFoundException.class);

        userRepository.findUserById(-1L);
    }


    @Test
    public void createUser() {
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(userRepository.save(user1).getId(), user1.getId());
    }


    @Test
    public void updateUserWithPut() {
        user1.setFirstname("User1fnUpdated");

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(userService.updateUserWithPut(user1.getId(), user1).getFirstname(), user1.getFirstname());
    }


    @Test(expected = BadRequestException.class)
    public void updateUserWithPutAndThrowBadRequestException() {
        user1.setFirstname("User1fnUpdated");
        user1.setGitHubUrl("null");

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);

        assertEquals(userService.updateUserWithPut(user1.getId(), user1).getFirstname(), user1.getFirstname());
    }


    @Test
    public void updateUserWithPatch() throws Exception {
        String user1PatchJson = "{\"firstname\":\"User1fnUpdated\",\"position\":\"INTERMEDIATE_DEV\"}";

        user1.setFirstname("User1fnUpdated");           // update the user for the test comparison
        user1.setPosition(Position.INTERMEDIATE_DEV);   // update the user for the test comparison

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(userService.updateUserWithPatch(user1.getId(), user1PatchJson).getFirstname(), user1.getFirstname());
        assertEquals(userService.updateUserWithPatch(user1.getId(), user1PatchJson).getPosition(), user1.getPosition());
    }


    @Test
    public void deleteUser() {
        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        doNothing().when(userRepository).deleteById(user1.getId());

        userService.deleteUser(user1.getId());

        verify(userRepository, times(1)).deleteById(user1.getId());
    }


    @Test
    public void getGitHubRepositoriesOverview() {
        GHRepositoryOverview ghRepositoryOverview = new GHRepositoryOverview();
        ghRepositoryOverview.setOwnerName("user1");
        ghRepositoryOverview.setName("user1-repository");
        ghRepositoryOverview.setDescription("My first repository");
        ghRepositoryOverview.setLanguage("Java");

        List<GHRepositoryOverview> ghRepositoryOverviews = new ArrayList<>();
        ghRepositoryOverviews.add(ghRepositoryOverview);

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        when(gitHubService.getUserRepositories(ghRepositoryOverview.getOwnerName())).thenReturn(ghRepositoryOverviews);

        List<GHRepositoryOverview> ghRepositoryOverviewsReceived = userService.getRepositoriesOverview(user1.getId());
        assertEquals(ghRepositoryOverviewsReceived.get(0).getName(), ghRepositoryOverview.getName());
    }
}