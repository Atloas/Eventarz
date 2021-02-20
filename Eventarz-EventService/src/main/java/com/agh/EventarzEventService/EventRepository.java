package com.agh.EventarzEventService;

import com.agh.EventarzEventService.model.Event;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repository providing access to the Neo4j database for uses regarding Event objects.
 */
@Repository
public interface EventRepository extends Neo4jRepository<Event, Long> {
    /**
     * Searches the database for an Event with the given uuid.
     *
     * @param uuid Identifier of the Event to find.
     * @return An Event object or null.
     */
    Event findByUuid(String uuid);

    /**
     * Searches the database for all Events whose name matches the provided regex.
     *
     * @param regex Regex to use.
     * @return Found Events.
     */
    Set<Event> findByNameRegex(String regex);

    /**
     * Searches the database for all Events related to the given User. Provides extra depth than just the Event itself.
     *
     * @param username User whose Events are to be found.
     * @return Found Events.
     */
    @Query("MATCH (u1:User {username: $0})-[r1]->(e:Event)-[r2]->(g:Group) OPTIONAL MATCH (e)<-[r3]-(u2:User) RETURN u1, u2, r1, r2, r3, e, g")
    Set<Event> findMyEvents(String username);

    /**
     * Searches the database for all Events the given User participates in, with extra depth,
     *
     * @param username User whose participated Events are to be found.
     * @return Found Events.
     */
    @Query("MATCH (u:User {username: $0})-[p:PARTICIPATES_IN]->(e:Event)-[r]-(n) RETURN e, p, r, n")
    Set<Event> findEventsImIn(String username);

    /**
     * Creates a :PARTICIPATES_IN relationship between the provided Event and User.
     * A workaround to what I assume is a bug where simply adding the relationship to an Event object and persisting it wouldn't create the relationship.
     *
     * @param eventUuid Identifier of the Event.
     * @param username Identifier of the User.
     */
    @Query("MATCH (e:Event {uuid: $0}), (u:User {username: $1}) CREATE (u)-[:PARTICIPATES_IN]->(e)")
    void participatesIn(String eventUuid, String username);

    /**
     * Checks if the given User belongs to the group the given Event is posted in.
     *
     * @param username  User to check for.
     * @param eventUuid The identifier of the event to check for.
     * @return Whether the User and the Event belong to the same group.
     */
    @Query("RETURN EXISTS((:User {username: $0})-[:BELONGS_TO]->(:Group)<-[:PUBLISHED_IN]-(:Event {uuid: $1}))")
    boolean checkIfAllowedToJoinEvent(String username, String eventUuid);

    /**
     * Deletes the PARTICIPATES_IN relationship between the given User and Event.
     *
     * @param username  User that left the Event.
     * @param eventUuid Event left by the User.
     */
    @Query("MATCH (:User {username: $0})-[r:PARTICIPATES_IN]->(:Event {uuid: $1}) DELETE r")
    void leftBy(String username, String eventUuid);
}
