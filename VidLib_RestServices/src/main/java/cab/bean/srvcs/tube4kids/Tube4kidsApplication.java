package cab.bean.srvcs.tube4kids;


import static java.util.Collections.singletonMap;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.auth.AccessAuthorizer;
import cab.bean.srvcs.tube4kids.auth.JWTAuthenticator;
import cab.bean.srvcs.tube4kids.cli.RenderCommand;
import cab.bean.srvcs.tube4kids.core.User;
//import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.db.Neo4JGraphDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.RoleDAO;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.filter.DateRequiredFeature;
import cab.bean.srvcs.tube4kids.health.TemplateHealthCheck;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;
import cab.bean.srvcs.tube4kids.resources.AgeGroupResource;
import cab.bean.srvcs.tube4kids.resources.AuthNVerityResource;
import cab.bean.srvcs.tube4kids.resources.ChildResource;
import cab.bean.srvcs.tube4kids.resources.GenreResource;
import cab.bean.srvcs.tube4kids.resources.PlaylistResource;
import cab.bean.srvcs.tube4kids.resources.ProtectedResource;
import cab.bean.srvcs.tube4kids.resources.UserResource;
import cab.bean.srvcs.tube4kids.resources.VideoResource;
import cab.bean.srvcs.tube4kids.resources.YouTubeVideoResource;

import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
//import cab.bean.srvcs.tube4kids.resources.PeopleResource;
//import cab.bean.srvcs.tube4kids.resources.PersonResource;


public class Tube4kidsApplication extends Application<Tube4kidsConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tube4kidsApplication.class);

    private GoogleAPIClientConfiguration googleAPIConf;
    private JWTConfiguration jwtConf;
    
    public final JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();

    private final HibernateBundle<Tube4kidsConfiguration> hibernateBundle = 
	new ScanningHibernateBundle<Tube4kidsConfiguration>("cab.bean.srvcs.tube4kids.core") {
        @Override
        public DataSourceFactory getDataSourceFactory(Tube4kidsConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new Tube4kidsApplication().run(args);
    }
    
    @Override
    public void run(Tube4kidsConfiguration configuration, Environment environment) {
	
	this.jwtConf = configuration.getJwtConfiguration();
	this.googleAPIConf = configuration.getGoogleAPIClientConfiguration();
	
	LOGGER.debug("App cont path: {} ", environment.getApplicationContext().getContextPath() );

	environment.jersey().getResourceConfig().addProperties(
	   singletonMap(ServerProperties.WADL_FEATURE_DISABLE, false)
	);

	environment.healthChecks().register("template", new TemplateHealthCheck(configuration.buildTemplate()));
	environment.jersey().register(DateRequiredFeature.class);

	jsonProvider.setMapper(new JodaMapper());

	environment.jersey().register(jsonProvider);
	// Contain this REST service to a sub-directory (<code>/api/</code>)
	environment.jersey().setUrlPattern(configuration.getAppContextUri());

	buildResources(configuration, environment);

	setupCORS(environment);
    }

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

    private void buildResources(Tube4kidsConfiguration configuration, Environment environment) {

	final JerseyEnvironment jerseyConf = environment.jersey();
	
	final Neo4JGraphDAO neo4JGraphDAO = new Neo4JGraphDAO(configuration.getNeo4jDriver());
	final YouTubeAPIProxy ytProxyClient = new YouTubeAPIProxy(configuration.getProxySearchUrl());

	final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
	final RoleDAO roleDAO = new RoleDAO(hibernateBundle.getSessionFactory());
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
        
        
        jerseyConf.register(new AuthNVerityResource(tokenDAO, userDAO, roleDAO, googleAPIConf, jwtConf));
        
//        jerseyConf.register(new ViewResource());
        jerseyConf.register(new ProtectedResource());
        jerseyConf.register(new AuthDynamicFeature(buildJwtAuthFilter( tokenDAO )));
        jerseyConf.register(RolesAllowedDynamicFeature.class);
        jerseyConf.register(new AuthValueFactoryProvider.Binder<>(User.class));
   }

    private JwtAuthFilter<User> buildJwtAuthFilter(TokenDAO tokenDAO) {

	return
            new JwtAuthFilter.Builder<User>()
            .setCookieName(jwtConf.getCookieName())
            .setJwtConsumer(
        	      new JwtConsumerBuilder()
            	.setExpectedAudience(jwtConf.getAudienceId())
        	        .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
        	        .setRequireExpirationTime() // the JWT must have an expiration time
        	        .setRequireSubject() // the JWT must have a subject claim
        	        .setVerificationKey(jwtConf.getVerificationKey()) // verify the signature with the public key
        	        .setRelaxVerificationKeyValidation() // relaxes key length requirement
        	        .build()
        	    )
            .setRealm(jwtConf.getRealmName())
            .setPrefix(jwtConf.getAuthHeaderPrefix())
            .setAuthenticator(
        	    	new UnitOfWorkAwareProxyFactory(hibernateBundle)
        	    	.create(JWTAuthenticator.class, new Class<?>[]{TokenDAO.class}, new Object[]{tokenDAO})
        	    )
        	    .setAuthorizer(new UnitOfWorkAwareProxyFactory(hibernateBundle).create(AccessAuthorizer.class))
            .buildAuthFilter();
    }

    private void setupCORS(Environment environment) {

	final FilterRegistration.Dynamic cors =  environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters:
        // 	'Access-Control-Allow-Origin: *'
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_TIMING_ORIGINS_PARAM, "54000");

//        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_REQUEST_HEADERS_HEADER, "*");
//        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "*");
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
        
    @Override
    public String getName() {
        return "tube4kids";
    }
}
