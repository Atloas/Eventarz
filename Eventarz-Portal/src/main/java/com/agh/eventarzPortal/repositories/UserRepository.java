package com.agh.eventarzPortal.repositories;

import com.agh.eventarzPortal.model.User;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repository providing access to the Neo4j database for uses regarding User objects.
 */
@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
    /**
     * Searches the database for a User with the given uuid.
     *
     * @param uuid Identifier of the User to find.
     * @return A User object or null.
     */
    User findByUuid(String uuid);

    /**
     * Searches the database for a User with the given uuid, with adjustable depth.
     *
     * @param uuid  Identifier of the User to find.
     * @param depth Search depth to use.
     * @return A User object or null.
     */
    User findByUuid(String uuid, @Depth int depth);

    /**
     * Searches the database for a User with the given username.
     *
     * @param username Name of the User to find.
     * @return A User object or null.
     */
    User findByUsername(String username);

    /**
     * Searches the database for all Users whose username matches the provided regex.
     *
     * @param regex Regex to use.
     * @return Found Users.
     */
    Set<User> findByUsernameRegex(String regex);

    /**
     * Searches the database for a User with teh given username and returns their id.
     *
     * @param username Username to find.
     * @return The found ID number or null.
     */
    @Query("MATCH (u:User {username: $0}) RETURN u.id")
    Long findIdByUsername(String username);
}
