package com.flaviomu.devteammngr.data.entity;

import com.flaviomu.devteammngr.data.misc.Position;

import javax.persistence.*;
import java.util.Objects;


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

    public User() {}

    public User(String firstname, String surname, Position position, String gitHubUrl) {
        this.firstname = firstname;
        this.surname = surname;
        this.position = position;
        this.gitHubUrl = gitHubUrl;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(surname, user.surname) &&
                position == user.position &&
                Objects.equals(gitHubUrl, user.gitHubUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, surname, position, gitHubUrl);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", position=" + position +
                ", gitHubUrl='" + gitHubUrl + '\'' +
                '}';
    }
}
