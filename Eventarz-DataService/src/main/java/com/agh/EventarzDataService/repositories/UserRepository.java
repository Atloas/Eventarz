package com.agh.EventarzDataService.repositories;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
    User findByUuid(String uuid);

    User findByUsername(String username);

    List<User> findByUsernameRegex(String regex);

    Long deleteByUsername(String username);

    @Query("MATCH (u:User {username: $0}) RETURN u.uuid")
    String findUuidByUsername(String username);

    @Query("MATCH (user:User {username: $0})<-[:DETAILS_OF]-(details:SECURITY_DETAILS) RETURN details")
    SecurityDetails findDetailsFor(String username);
}
