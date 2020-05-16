package org.ebuy.repository;

import org.ebuy.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Burak Köken on 22.4.2020.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByActivationKey(String activationKey);

    Optional<User> findByResetPasswordKey(String activationKey);

}