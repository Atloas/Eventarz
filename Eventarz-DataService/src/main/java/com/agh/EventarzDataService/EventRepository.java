package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.Event;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends Neo4jRepository<Event, Long> {

    Event findByUuid(String uuid);

    List<Event> findByNameRegex(String regex);

    Long deleteByUuid(String uuid);

    @Query("MATCH (u1:User {username: $0})-[r1]->(e:Event)-[r2]->(g:Group) OPTIONAL MATCH (e)<-[r3]-(u2:User) RETURN u1, u2, r1, r2, r3, e, g")
    List<Event> findMyEvents(String username);

    @Query("MATCH (u:User {username: $0})-[p:PARTICIPATES_IN]->(e:Event)-[r]-(n) RETURN e, p, r, n")
    List<Event> findEventsImIn(String username);

    @Query("MATCH (e:Event {uuid: $0}), (u:User {username: $1}) CREATE (u)-[:PARTICIPATES_IN]->(e)")
    void participatesIn(String eventUuid, String username);

    @Query("RETURN EXISTS((:User {username: $1})-[:BELONGS_TO]->(:Group)<-[:PUBLISHED_IN]-(:Event {uuid: $0}))")
    boolean checkIfAllowedToJoinEvent(String eventUuid, String username);

    @Query("MATCH (:User {username: $0})-[r:PARTICIPATES_IN]->(:Event {uuid: $1}) DELETE r")
    void leftBy(String username, String eventUuid);
}
