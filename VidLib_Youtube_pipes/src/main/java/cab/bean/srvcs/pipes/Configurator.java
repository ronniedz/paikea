package cab.bean.srvcs.pipes;

import cab.bean.srvcs.tube4kids.config.RestServerConfiguration;
import cab.bean.srvcs.tube4kids.config.YoutubeResourceConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Configurator {

    final Configuration configuration;

    public Configurator(String arg, ObjectMapper objectMapper) throws Exception {
	super();
	System.err.println("Loading Configuration from: " + arg);
	this.configuration = objectMapper.readValue(getClass().getResourceAsStream(arg), Configuration.class);
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

