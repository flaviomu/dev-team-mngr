package com.flaviomu.devteammngr.service.external.github;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Scope("application")
public class GitHubConnection {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private GitHub gitHub;

    public GitHubConnection() {
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
}
