package com.flaviomu.devteammngr.service.domain;

import com.flaviomu.devteammngr.data.entity.User;
import com.flaviomu.devteammngr.data.misc.Position;
import com.flaviomu.devteammngr.data.repository.UserRepository;
import com.flaviomu.devteammngr.exception.BadRequestException;
import com.flaviomu.devteammngr.exception.UserNotFoundException;
import com.flaviomu.devteammngr.service.external.github.GitHubService;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/**
 * Defines the tests for the @{link {@link UserService}}
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private UserService userService;


    private User user1;
    private User user2;
    private List<User> initialUsers;


    @BeforeEach
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
        lenient().when(userRepository.findUserById(1L)).thenReturn(user1);
        lenient().when(userRepository.findUserById(2L)).thenReturn(user2);

        assertEquals(user1.getFirstname(), userRepository.findUserById(1L).getFirstname());
        assertEquals(user2.getSurname(), userRepository.findUserById(2L).getSurname());
    }


    @Test
    public void getNotExistingUser() {
        when(userRepository.findUserById(-1L)).thenThrow(UserNotFoundException.class);
        Executable codeUnderTest = () -> userRepository.findUserById(-1L);

        assertThrows(UserNotFoundException.class, codeUnderTest);
    }


    @Test
    public void createUser() {
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(user1.getId(), userRepository.save(user1).getId());
    }


    @Test
    public void updateUserWithPut() {
        user1.setFirstname("User1fnUpdated");

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(user1.getFirstname(), userService.updateUserWithPut(user1.getId(), user1).getFirstname());
    }


    @Test
    public void updateUserWithPutAndThrowBadRequestException() {
        user1.setFirstname("User1fnUpdated");
        user1.setGitHubUrl("null"); // creates a bad request

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        Executable codeUnderTest = () -> userService.updateUserWithPut(user1.getId(), user1);

        assertThrows(BadRequestException.class, codeUnderTest);
    }


    @Test
    public void updateUserWithPatch() {
        String user1PatchJson = "{\"firstname\":\"User1fnUpdated\",\"position\":\"INTERMEDIATE_DEV\"}";

        user1.setFirstname("User1fnUpdated");           // update the user for the test comparison
        user1.setPosition(Position.INTERMEDIATE_DEV);   // update the user for the test comparison

        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);

        assertEquals(user1.getFirstname(), userService.updateUserWithPatch(user1.getId(), user1PatchJson).getFirstname());
        assertEquals(user1.getPosition(), userService.updateUserWithPatch(user1.getId(), user1PatchJson).getPosition());
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
        assertEquals(ghRepositoryOverview.getName(), ghRepositoryOverviewsReceived.get(0).getName());
    }
}