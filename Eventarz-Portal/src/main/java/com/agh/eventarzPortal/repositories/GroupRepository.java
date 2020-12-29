package com.agh.eventarzPortal.repositories;

import com.agh.eventarzPortal.model.Group;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repository providing access to the Neo4j database for uses regarding Group objects.
 */
@Repository
public interface GroupRepository extends Neo4jRepository<Group, Long> {
    /**
     * Searches the database for a Group with the given uuid.
     *
     * @param uuid Identifier of the Group to find.
     * @return A Group object or null.
     */
    Group findByUuid(String uuid);

    /**
     * Searches the database for a Group with the given uuid, with adjustable depth.
     *
     * @param uuid  Identifier of the Group to find.
     * @param depth Search depth to use.
     * @return A Group object or null.
     */
    Group findByUuid(String uuid, @Depth int depth);

    /**
     * Searches the database for all Groups whose name matches the provided regex.
     *
     * @param regex Regex to use.
     * @return Found Groups.
     */
    Set<Group> findByNameRegex(String regex);

    /**
     * Searches the database for all Groups related to the given User. Provides extra depth than just the Group itself.
     *
     * @param username User whose Groups are to be found.
     * @return Found Groups.
     */
    @Query("MATCH (u1:User {username: $0})-[r1]->(g:Group) OPTIONAL MATCH (g)<-[r2]-(u2:User) RETURN u1, u2, r1, r2, g")
    Set<Group> findMyGroups(String username);

    /**
     * Searches the database for all Groups related to the given User, like {@link #findMyGroups}, but without the extra depth.
     *
     * @param username User whose Groups are to be found.
     * @return Found Groups.
     */
    @Query("MATCH (u:User {username: $0})-[r:BELONGS_TO]->(g:Group) RETURN u, r, g")
    Set<Group> findMyGroupNames(String username);

    /**
     * Creates a :BELONGS_TO relationship between the provided Event and User.
     * A workaround to what I assume is a bug where simply adding the relationship to a Group object and persisting it wouldn't create the relationship.
     *
     * @param groupUuid Identifier of the Group.
     * @param username Identifier of the User.
     */
    @Query("MATCH (g:Group {uuid: $0}), (u:User {username: $1}) CREATE (u)-[:BELONGS_TO]->(g)")
    void belongsTo(String groupUuid, String username);

    /**
     * Deletes the BELONGS_TO relationship between the given User and Group.
     *
     * @param username  User that left the Group.
     * @param groupUuid Group left by the User.
     */
    @Query("MATCH (u:User {username: $0})-[r1:BELONGS_TO]->(g:Group {uuid: $1}) OPTIONAL MATCH (g)<-[:PUBLISHED_IN]-(:Event)<-[r2:PARTICIPATES_IN]-(u) DELETE r1, r2")
    void leftBy(String username, String groupUuid);

    /**
     * Deletes the given Group with all its Events.
     *
     * @param uuid Group to delete.
     */
    @Query("MATCH (g:Group {uuid: $0}) OPTIONAL MATCH (g)<-[*]-(e:Event) DETACH DELETE g, e")
    void deleteWithEvents(String uuid);
}
