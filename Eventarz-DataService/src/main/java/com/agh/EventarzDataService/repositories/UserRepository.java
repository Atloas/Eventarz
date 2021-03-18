package com.agh.EventarzDataService.repositories;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findByUuid(String uuid);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameRegex(String regex);

    Optional<Long> deleteByUsername(String username);

    @Query("MATCH (u:User {username: $0}) RETURN u.uuid")
    Optional<String> findUuidByUsername(String username);

    @Query("MATCH (user:User {username: $0})<-[:DETAILS_OF]-(details:SECURITY_DETAILS) RETURN details")
    Optional<SecurityDetails> findDetailsFor(String username);
}
