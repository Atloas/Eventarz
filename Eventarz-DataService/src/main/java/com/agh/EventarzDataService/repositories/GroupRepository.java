package com.agh.EventarzDataService.repositories;

import com.agh.EventarzDataService.model.Group;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends Neo4jRepository<Group, Long> {

    @Query("MATCH (group:Group {uuid: $0})<-[founded:FOUNDED]-(founder:User) " +
            "OPTIONAL MATCH (group)<-[belongsTo:BELONGS_TO]-(member:User) " +
            "OPTIONAL MATCH (group)<-[publishedIn:PUBLISHED_IN]-(event:Event) " +
            "OPTIONAL MATCH (event)<-[participatesIn:PARTICIPATES_IN]-(participant:User) " +
            "RETURN group, founded, founder, belongsTo, member, publishedIn, event, participatesIn")
    Group findByUuid(String uuid);

    List<Group> findByNameRegex(String regex);

    @Query("MATCH (u1:User {username: $0})-[r1]->(g:Group) OPTIONAL MATCH (g)<-[r2]-(u2:User) OPTIONAL MATCH (g)<-[r3]-(e:Event) RETURN u1, u2, r1, r2, r3, g, e")
    List<Group> findMyGroups(String username);

    @Query("MATCH (u:User {username: $0})-[r:BELONGS_TO]->(g:Group) RETURN u, r, g")
    List<Group> findMyGroupNames(String username);

    @Query("MATCH (g:Group {uuid: $0}), (u:User {username: $1}) CREATE (u)-[:BELONGS_TO]->(g)")
    void join(String groupUuid, String username);

    @Query("MATCH (u:User {username: $0})-[r1:BELONGS_TO]->(g:Group {uuid: $1}) OPTIONAL MATCH (g)<-[:PUBLISHED_IN]-(:Event)<-[r2:PARTICIPATES_IN]-(u) DELETE r1, r2")
    void leave(String username, String groupUuid);

    @Query("MATCH (g:Group {uuid: $0}) OPTIONAL MATCH (g)<-[*]-(e:Event) DETACH DELETE g, e")
    Long deleteByUuid(String uuid);

    @Query("RETURN EXISTS((:Group {uuid: $0})<-[:FOUNDED]-(:User {username: $1}))")
    boolean isFounder(String uuid, String username);

    @Query("RETURN EXISTS((:User {username: $1})-[:BELONGS_TO]->(:Group {uuid: $0}))")
    boolean checkIfAllowedToPublishEvent(String groupUuid, String username);
}
