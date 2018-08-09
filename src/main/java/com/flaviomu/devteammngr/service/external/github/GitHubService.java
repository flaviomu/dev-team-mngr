package com.flaviomu.devteammngr.service.external.github;

import com.flaviomu.devteammngr.exception.InternalServerErrorException;
import com.flaviomu.devteammngr.web.dto.GHRepositoryOverview;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Defines the service providing a connection to the GitHub APIs
 *
 */
@Service
@Scope("application")
public class GitHubService {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private GitHub gitHub;

    /**
     * Creates the {@link GitHubService} initialising the connection to GitHub}
     */
    public GitHubService() {
        try {
            gitHub = GitHub.connect();
        } catch (IOException e) {
            log.error("Error while connecting to the GitHub service.");
            log.error(e.getMessage());
            log.warn("Trying anonymous connection.");
            try {
                gitHub = GitHub.connectAnonymously();
            } catch (IOException e1) {
                log.error("Error while trying anonymous connection to the GitHub service.");
                log.error(e.getMessage());
                log.warn("Aborting connection and exiting.");
                System.exit(1);
            }
        }

        log.info("GitHub authenticated connection: " + ! gitHub.isAnonymous());
    }


    public GitHub getGitHub() {
        return gitHub;
    }


    /**
     * Retrieves the public repositories of a GitHub user given his username
     *
     * @param ghUsername the username of the GitHub user
     * @return the list of the @{link {@link GHRepositoryOverview}} of the repositories of the given user
     *
     */
    public List<GHRepositoryOverview> getUserRepositories(String ghUsername) {
        List<GHRepositoryOverview> ghRepositoryOverviews = new ArrayList<>();
        GHUser ghUser;

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

        return ghRepositoryOverviews;
    }
}
