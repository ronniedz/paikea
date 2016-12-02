package cab.bean.srvcs.tube4kids.db;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import cab.bean.srvcs.tube4kids.core.Video;

public class Neo4JGraphDAO {

    private Driver neo4jDriver;
    
    public Neo4JGraphDAO(Driver neo4jDriver) {
	this.neo4jDriver = neo4jDriver;

    }


    public Map<String, ?> insert(Video video) {
	ObjectMapper objectMapper = new ObjectMapper();
	@SuppressWarnings("unchecked")
	Map<String, Object> videoAsMap = objectMapper.convertValue(video, Map.class);	

//	Session session = neo4jDriver.session();
	
// #################################	
//	Statement statement = new Statement( "MATCH (n) WHERE n.name={myNameParam} RETURN n.age" );
//	 StatementResult cursor = session.run( statement.withParameters( Values.parameters( "myNameParam", "Bob" )  ) );

	
	String videoInsertTemplate = 
"CREATE (invid: Video {title: {title}, userId: TOINT({userId}), videoId: {videoId}, created: {publishedAt} }) RETURN invid";

//	String j =
//"CREATE ( invid: Video {title: 'Mod!', userId: TOINT('1'), videoId: 'KrJQimAC91w', created: '2016aac' }) RETURN invid";

	
	String query = String.format(
		"CREATE ( invid: Video {"
		+ "title: '%s', "
		+ "userId: TOINT('%s'), "
		+ "videoId: '%s', "
		+ "created: '%s' "
		+ "}) RETURN invid;",
		video.getTitle(),
		video.getUserId(),
		video.getVideoId(),
		video.getPublishedAt()
	);

	System.out.println("qry: " + query);
		
	

	 
	 List<Map<String, Object>> list = 
		neo4jDriver.session().run( query ).list(r -> unwrap(r, "v"));
//	 	neo4jDriver.session().run( videoInsertTemplate, videoAsMap ).list(r -> unwrap(r, "v"));
	 
	 Map<String, Object> outp = list.get(0);
	 System.out.print("outp: " + outp);
//	 Map<String, ?> res = cursor.single().asMap();
//	 session.close();
	 
	 return outp;
    }
 /*
		List<Map<String, Object>> list = session.run(query).list(r -> unwrap(r, "v"));

// #################################	
//
//	StatementResult cursor = session.run( "MATCH (n) WHERE n.name = {myNameParam} RETURN (n)",
//                Values.parameters( "myNameParam", "Bob" ) );
//
//	 session.r
// #################################	
	

	
//	MATCH (video:Video {videoId: row.video_id})
//	MATCH (user:User {id: TOINT(row.user_id)})
//	MATCH (genre:Genre {id: TOINT(row.genre_id)})
//	MATCH (genre2:Genre {id: TOINT(row.genre2_id)})
//	MERGE (user)-[:ASSIGNS]->(genre)
//	MERGE (user)-[:ASSIGNS]->(genre2)
//	MERGE (video)-[:WITH]->(genre)
//	MERGE (video)-[:WITH]->(genre2);

	;

  */
    private Map<String, Object> unwrap(Record r, String unkey) {
	return r.asMap(x -> x.asMap()).get(unkey);
    }


    public List<Map<String, Object>> listAllVideo() {
	Session session = neo4jDriver.session();
	String query = "MATCH (v:Video)-[:WITH]-(genre:Genre) return v;";
	
	// StatementResult result = session.run(query);

	List<Map<String, Object>> list = session.run(query).list(r -> unwrap(r, "v"));
	session.close();

//	list.forEach(d -> System.out.println(d));


	// while (result.hasNext()) {
	// Record record = result.next();
	// org.neo4j.driver.v1.Value nv = record.get("v");
	// Video vid = (Video) nv.asObject();
	// System.out.println(vid.toString());
	// }
	return list;
    }

}
