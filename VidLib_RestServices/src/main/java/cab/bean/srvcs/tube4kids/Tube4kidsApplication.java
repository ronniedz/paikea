package cab.bean.srvcs.tube4kids;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.container.ContainerRequestContext;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.HmacKey;
import org.neo4j.driver.v1.Driver;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;

import cab.bean.srvcs.tube4kids.auth.ExampleAuthenticator;
import cab.bean.srvcs.tube4kids.auth.ExampleAuthorizer;
import cab.bean.srvcs.tube4kids.auth.JWTAuthenticator;
import cab.bean.srvcs.tube4kids.cli.RenderCommand;
import cab.bean.srvcs.tube4kids.filter.DateRequiredFeature;
import cab.bean.srvcs.tube4kids.health.TemplateHealthCheck;
import cab.bean.srvcs.tube4kids.core.User;

//import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.db.Neo4JGraphDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;
import cab.bean.srvcs.tube4kids.resources.AgeGroupResource;
import cab.bean.srvcs.tube4kids.resources.ChildResource;
import cab.bean.srvcs.tube4kids.resources.GenreResource;
import cab.bean.srvcs.tube4kids.resources.AuthNVerityResource;
import cab.bean.srvcs.tube4kids.resources.PlaylistResource;
import cab.bean.srvcs.tube4kids.resources.UserResource;
import cab.bean.srvcs.tube4kids.resources.VideoResource;
import cab.bean.srvcs.tube4kids.resources.YouTubeVideoResource;
import cab.bean.srvcs.tube4kids.resources.ProtectedResource;
import cab.bean.srvcs.tube4kids.resources.ViewResource;
import cab.bean.srvcs.tube4kids.resources.FilteredResource;
//import cab.bean.srvcs.tube4kids.resources.PeopleResource;
//import cab.bean.srvcs.tube4kids.resources.PersonResource;





import org.glassfish.jersey.server.ServerProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;


public class Tube4kidsApplication extends Application<Tube4kidsConfiguration> {

    public static void main(String[] args) throws Exception {
        new Tube4kidsApplication().run(args);
    }

    private final HibernateBundle<Tube4kidsConfiguration> hibernateBundle = 
	new ScanningHibernateBundle<Tube4kidsConfiguration>("cab.bean.srvcs.tube4kids.core") {
        @Override
        public DataSourceFactory getDataSourceFactory(Tube4kidsConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "tube4kids";
    }

    public final JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();

    @Override
    public void initialize(Bootstrap<Tube4kidsConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new MigrationsBundle<Tube4kidsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(Tube4kidsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<Tube4kidsConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(Tube4kidsConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    private void buildResources(Tube4kidsConfiguration configuration, final JerseyEnvironment jerseyConf) {

	final Neo4JGraphDAO neo4JGraphDAO = new Neo4JGraphDAO(configuration.getNeo4jDriver());
	final YouTubeAPIProxy ytProxyClient = new YouTubeAPIProxy(configuration.getProxySearchUrl());

	final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        final TokenDAO tokenDAO = new TokenDAO(hibernateBundle.getSessionFactory());

        final VideoDAO videoDAO = new VideoDAO(hibernateBundle.getSessionFactory());
        final GenreDAO genreDAO = new GenreDAO(hibernateBundle.getSessionFactory());
        final PlaylistDAO playlistDAO = new PlaylistDAO(hibernateBundle.getSessionFactory());
        final AgeGroupDAO ageGroupDAO = new AgeGroupDAO(hibernateBundle.getSessionFactory());
        final ChildDAO childDAO = new ChildDAO(hibernateBundle.getSessionFactory());
        

        jerseyConf.register(new YouTubeVideoResource(ytProxyClient));

        jerseyConf.register(new GenreResource(genreDAO));
        jerseyConf.register(new UserResource(userDAO));
        jerseyConf.register(new AgeGroupResource(ageGroupDAO));
        jerseyConf.register(new ChildResource(childDAO, videoDAO, playlistDAO));
        jerseyConf.register(new PlaylistResource(playlistDAO, videoDAO));
        jerseyConf.register(new VideoResource(videoDAO, genreDAO, userDAO, neo4JGraphDAO, ytProxyClient));

        jerseyConf.register(new AuthNVerityResource(tokenDAO, userDAO , configuration.getJwtTokenSecret(), configuration.getClientId()));
        
        jerseyConf.register(new ViewResource());
        jerseyConf.register(new ProtectedResource());
        


	    String jwtSecret = configuration.getJwtTokenSecret();

        JWTAuthenticator authenticator = new UnitOfWorkAwareProxyFactory(hibernateBundle)
        		.create(JWTAuthenticator.class, new Class<?>[]{TokenDAO.class}, new Object[]{tokenDAO});
  
        JwtAuthFilter<User> authFilter = buildJwtAuthFilter(jwtSecret, configuration.getClientId(), authenticator );
        
        jerseyConf.register(new AuthDynamicFeature(authFilter));
        jerseyConf.register(RolesAllowedDynamicFeature.class);
        jerseyConf.register(new AuthValueFactoryProvider.Binder<>(User.class));
   }

    private JwtAuthFilter<User> buildJwtAuthFilter(String jwtSecret, String clientId, JWTAuthenticator authenticator) {
        
	HmacKey key = null;
	
	try {
	    key = new HmacKey(jwtSecret.getBytes("UTF-8") );
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
        
	final JwtConsumer consumer = new JwtConsumerBuilder()
	.setExpectedAudience(clientId)
        .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
        .setRequireExpirationTime() // the JWT must have an expiration time
        .setRequireSubject() // the JWT must have a subject claim
        .setVerificationKey(key) // verify the signature with the public key
        .setRelaxVerificationKeyValidation() // relaxes key length requirement
        .build(); // create the JwtConsumer instance 
	return
            new JwtAuthFilter.Builder<User>()
            .setCookieName("beancab")
            .setJwtConsumer(consumer)
            .setRealm("Video Library")
            .setPrefix("Bearer")
            .setAuthenticator(authenticator)
            .buildAuthFilter();
    }

    @Override
    public void run(Tube4kidsConfiguration configuration, Environment environment) {

        // ADD WADL
        Map<String, Object> properties = new HashMap<>();
        properties.put(ServerProperties.WADL_FEATURE_DISABLE, false);
        environment.jersey().getResourceConfig().addProperties(properties);


        environment.healthChecks().register("template", new TemplateHealthCheck( configuration.buildTemplate() ));
        environment.jersey().register(DateRequiredFeature.class);

        	
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JodaModule());

        
	this.jsonProvider.setMapper(new JodaMapper());
	environment.jersey().register(this.jsonProvider);
	environment.jersey().setUrlPattern("/api/*");

        buildResources(configuration, environment.jersey());


        environment.jersey().register(new FilteredResource());
        
        setupCORS(environment);
    }
    private void setupCORS(Environment environment) {
        final FilterRegistration.Dynamic cors =  environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters:
        // 	'Access-Control-Allow-Origin: *'
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_TIMING_ORIGINS_PARAM, "54000");

        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_REQUEST_HEADERS_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "*");
//       cors.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

//      cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_METHODS_HEADER, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_REQUEST_METHOD_HEADER, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");

        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        // Add URL mapping
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, "54000");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


	
//	FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
//	    filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
//	    filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
//	    filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
//	    filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
//	    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	  }

    
        
}
