package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Long> {

    public User findByUsername(String username);

}
