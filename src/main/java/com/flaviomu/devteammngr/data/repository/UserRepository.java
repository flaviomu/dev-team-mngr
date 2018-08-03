package com.flaviomu.devteammngr.data.repository;

import com.flaviomu.devteammngr.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserById(Long id);

    void deleteById(Long l);

}
