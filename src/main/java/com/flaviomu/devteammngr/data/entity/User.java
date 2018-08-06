package com.flaviomu.devteammngr.data.entity;

import com.flaviomu.devteammngr.data.misc.Position;

import javax.persistence.*;


/**
 * Defines a User
 *
 */
@Entity
@Table(name="USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="FIRSTNAME")
    private String firstname;

    @Column(name="SURNAME")
    private String surname;

    @Column(name="POSITION")
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(name="GITHUB_URL")
    private String gitHubUrl;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getGitHubUrl() {
        return gitHubUrl;
    }

    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }
}
