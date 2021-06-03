package com.agh.EventarzAuthenticationService.repositories;

import com.agh.EventarzAuthenticationService.model.SecurityDetails;
import com.agh.EventarzAuthenticationService.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthDataRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH (user:User {username: $0})<-[:DETAILS_OF]-(details:SecurityDetails) RETURN details")
    SecurityDetails findDetailsForUsername(String username);
}
