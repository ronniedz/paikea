package cab.bean.srvcs.tube4kids.core;

import java.util.List;

/**
 * 
 * @author ronalddennison
 */
public interface AVideo {
 
    public String getEtag();
    
    public String getTitle();

    /**
     *  A video's primary key in MySqlDB and Neo4J - a reuse of YouTube's video id.
     *  Mongo generates it's own IDs.
     */
    public String getVideoId();
    
    public String getDescription();
    
    public String getDefaultThumbnail();
    
    public List<VideoGenre> getVideoGenres();

}
