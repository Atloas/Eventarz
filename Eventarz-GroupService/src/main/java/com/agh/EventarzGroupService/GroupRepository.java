package com.agh.EventarzGroupService;

import com.agh.EventarzGroupService.model.Group;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends Neo4jRepository<Group, Long> {

    Group findByUuid(String uuid);

    Group findByUuid(String uuid, @Depth int depth);

    List<Group> findByNameRegex(String regex);

    @Query("MATCH (u1:User {username: $0})-[r1]->(g:Group) OPTIONAL MATCH (g)<-[r2]-(u2:User) RETURN u1, u2, r1, r2, g")
    List<Group> findMyGroups(String username);

    @Query("MATCH (u:User {username: $0})-[r:BELONGS_TO]->(g:Group) RETURN u, r, g")
    List<Group> findMyGroupNames(String username);

    @Query("MATCH (g:Group {uuid: $0}), (u:User {username: $1}) CREATE (u)-[:BELONGS_TO]->(g)")
    void belongsTo(String groupUuid, String username);

    @Query("MATCH (u:User {username: $0})-[r1:BELONGS_TO]->(g:Group {uuid: $1}) OPTIONAL MATCH (g)<-[:PUBLISHED_IN]-(:Event)<-[r2:PARTICIPATES_IN]-(u) DELETE r1, r2")
    void leftBy(String username, String groupUuid);

    @Query("MATCH (g:Group {uuid: $0}) OPTIONAL MATCH (g)<-[*]-(e:Event) DETACH DELETE g, e")
    Long deleteByUuid(String uuid);
}
