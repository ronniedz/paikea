package cab.bean.srvcs.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.Configuration.RestServerConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




public class Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configurator.class);

    final Configuration configuration;
    
    public Configurator(String args, ObjectMapper objectMapper) throws Exception {
	super();
	System.err.println("Config file: " + args);
	LOGGER.debug("Loading Configuration from: " + args);

	this.configuration = objectMapper.readValue(getClass().getResourceAsStream(args), Configuration.class);
	
    }

    /**
     * @return the configuration
     */
    public RestServerConfiguration getRestServerConfiguration() {
        return configuration.getRestServerConfiguration();
    }

    
    public YoutubeResourceConfiguration getYoutubeResourceConfiguration() {
	return configuration.getYoutubeResourceConfiguration();
    }
    
    
    /**
     * @return the configuration
     */
    public Configuration getConfiguration() {
	return configuration;
    }
    

}
