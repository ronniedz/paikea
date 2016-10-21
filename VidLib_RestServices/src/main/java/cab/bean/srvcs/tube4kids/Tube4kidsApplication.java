package cab.bean.srvcs.tube4kids;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

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

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;

import cab.bean.srvcs.tube4kids.auth.ExampleAuthenticator;
import cab.bean.srvcs.tube4kids.auth.ExampleAuthorizer;
import cab.bean.srvcs.tube4kids.cli.RenderCommand;
import cab.bean.srvcs.tube4kids.core.Person;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.db.PersonDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.filter.DateRequiredFeature;
import cab.bean.srvcs.tube4kids.health.TemplateHealthCheck;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;
import cab.bean.srvcs.tube4kids.resources.AgeGroupResource;
import cab.bean.srvcs.tube4kids.resources.ChildResource;
import cab.bean.srvcs.tube4kids.resources.FilteredResource;
import cab.bean.srvcs.tube4kids.resources.GenreResource;
import cab.bean.srvcs.tube4kids.resources.PeopleResource;
import cab.bean.srvcs.tube4kids.resources.PersonResource;
import cab.bean.srvcs.tube4kids.resources.PlaylistResource;
import cab.bean.srvcs.tube4kids.resources.ProtectedResource;
import cab.bean.srvcs.tube4kids.resources.VideoResource;
import cab.bean.srvcs.tube4kids.resources.ViewResource;
import cab.bean.srvcs.tube4kids.resources.YouTubeVideoResource;

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
	new ScanningHibernateBundle<Tube4kidsConfiguration>(
		"cab.bean.srvcs.tube4kids.core") {
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
        bootstrap.addBundle(new AssetsBundle());
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

        jerseyConf.register(new YouTubeVideoResource(new YouTubeAPIProxy(configuration.getProxyUrl())));

        final PersonDAO personDAO = new PersonDAO(hibernateBundle.getSessionFactory());
        final VideoDAO videoDAO = new VideoDAO(hibernateBundle.getSessionFactory());
        final GenreDAO genreDAO = new GenreDAO(hibernateBundle.getSessionFactory());
        final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        final PlaylistDAO playlistDAO = new PlaylistDAO(hibernateBundle.getSessionFactory());
        final AgeGroupDAO ageGroupDAO = new AgeGroupDAO(hibernateBundle.getSessionFactory());
        final ChildDAO childDAO = new ChildDAO(hibernateBundle.getSessionFactory());

        
        jerseyConf.register(new GenreResource(genreDAO));
        jerseyConf.register(new AgeGroupResource(ageGroupDAO));
        
        jerseyConf.register(new ChildResource(childDAO, videoDAO, playlistDAO));
        
        jerseyConf.register(new PlaylistResource(playlistDAO, videoDAO));
        
        jerseyConf.register(new VideoResource(videoDAO, genreDAO, userDAO, configuration.getNeo4jSession()));
        
        jerseyConf.register(new ViewResource());
        jerseyConf.register(new ProtectedResource());
        jerseyConf.register(new PeopleResource(personDAO));
        jerseyConf.register(new PersonResource(personDAO));
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

//        final byte[] key = configuration.getJwtTokenSecret();
//
//        final JwtConsumer consumer = new JwtConsumerBuilder()
//            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
//            .setRequireExpirationTime() // the JWT must have an expiration time
//            .setRequireSubject() // the JWT must have a subject claim
//            .setVerificationKey(new HmacKey(key)) // verify the signature with the public key
//            .setRelaxVerificationKeyValidation() // relaxes key length requirement
//            .build(); // create the JwtConsumer instance
//
//        environment.jersey().register(new AuthDynamicFeature(
//            new JwtAuthFilter.Builder<User>()
//                .setJwtConsumer(consumer)
//                .setRealm("BeanCab")
//                .setPrefix("Bearer")
//                .setAuthenticator(new AnotherAuthenticator())
//                .buildAuthFilter()));
//
//        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
////        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
//        environment.jersey().register(RolesAllowedDynamicFeature.class);
//        environment.jersey().register(new SecuredResource(configuration.getJwtTokenSecret()));
        
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ExampleAuthenticator())
                .setAuthorizer(new ExampleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        buildResources(configuration, environment.jersey());
        environment.jersey().register(new FilteredResource());
        
        final FilterRegistration.Dynamic cors =  environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters:
        // 	'Access-Control-Allow-Origin: *'
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    
    }

    private static class AnotherAuthenticator implements Authenticator<JwtContext, User> {
	
	@Override
	public Optional<User> authenticate(JwtContext context) {
	    // Provide your own implementation to lookup users based on the principal attribute in the
	    // JWT Token. E.g.: lookup users from a database etc.
	    // This method will be called once the token's signature has been verified
	    
	    // In case you want to verify different parts of the token you can do that here.
	    // E.g.: Verifying that the provided token has not expired.
	    
	    // All JsonWebTokenExceptions will result in a 401 Unauthorized response.
	    
	    try {
		final String subject = context.getJwtClaims().getSubject();
		if ("good-guy".equals(subject)) {
		    return Optional.of(new User("good-guy"));
		}
		return Optional.empty();
	    }
	    catch (MalformedClaimException e) { return Optional.empty(); }
	}
    }
    
        
}
