package com.agh.EventarzDataService.repositories;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByUsernameRegex(String regex);

    Long deleteByUsername(String username);

    @Query("MATCH (u:User {username: $0})<-[:DETAILS_OF]-(s:SecurityDetails) SET s.banned=$1")
    void changeBanStatus(String username, boolean banned);

    @Query("MATCH (u:User {username: $0})-[r:PARTICIPATES_IN]->(:Event) DELETE r")
    void removeFromEvents(String username);

    @Query("MATCH (u:User {username: $0})-[r:BELONGS_TO]->(:Group) DELETE r")
    void removeFromGroups(String username);

    @Query("RETURN EXISTS((:User {username: $0})<-[:DETAILS_OF]-(:SecurityDetails))")
    boolean checkIfUserExists(String username);

    @Query("MATCH (user:User {username: $0})<-[:DETAILS_OF]-(details:SecurityDetails) RETURN details")
    SecurityDetails findDetailsFor(String username);
}
