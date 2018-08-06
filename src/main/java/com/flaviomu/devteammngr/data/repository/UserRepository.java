package com.flaviomu.devteammngr.data.repository;

import com.flaviomu.devteammngr.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Defines a @{link User} repository to manage the persistence of the users
 *
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserById(Long id);

    void deleteById(Long l);

}
