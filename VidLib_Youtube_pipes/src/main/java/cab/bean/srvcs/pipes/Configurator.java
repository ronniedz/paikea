package cab.bean.srvcs.pipes;

import java.io.IOException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import cab.bean.srvcs.pipes.Configuration.RestServerConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




public class Configurator {
    
    final Configuration configuration;
    
    public Configurator(String... args) throws Exception {
	super();
	
	this.configuration = new ObjectMapper().readValue(getClass().getResourceAsStream(args[0]), Configuration.class);
	
	System.err.println("Config file: " + configuration);
	if (args.length > 1) {
	    
	}
	
    }

    /**
     * @return the configuration
     */
    public RestServerConfiguration getRestServerConfiguration() {
        return configuration.getRestServerConfiguration();
    }

    
    /**
     * @return the configuration
     */
    public Configuration getConfiguration() {
	return configuration;
    }
    

}
