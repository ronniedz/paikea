package cab.bean.srvcs.tube4kids.filter;


import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.UserDAO;


public class AdminOrOwnerDynamicFeature implements DynamicFeature {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminOrOwnerDynamicFeature.class);
    final UserDAO userDAO;
    
    public AdminOrOwnerDynamicFeature(UserDAO userDAO) {
	this.userDAO = userDAO;
    }

    @Override
    @UnitOfWork
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
	
	final Method method = resourceInfo.getResourceMethod();

        final AnnotatedMethod am = new AnnotatedMethod(method);

        AdminOrOwner aclAnnotation = am.getAnnotation(AdminOrOwner.class);

        if (aclAnnotation != null) {

            LOGGER.debug("method.getName() {} " , method.getName());
            
            final ImmutableMap.Builder<String, ConditionsCheck> builder = ImmutableMap.builder();
//            for (Map.Entry<String, Map<String, String>> entry : viewRendererConfiguration.entrySet()) {
//                builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
//            }
            
            Arrays.asList(aclAnnotation.grants()).forEach(x -> {
        		String[] parts = x.split(":");
        		builder.put(parts[1], new ConditionsCheck(parts[1], parts[0]));
            });
            
            final Map<String, ConditionsCheck> conditionsMap = builder.build();
            ConditionsCheck temp = null;
            int countdown = aclAnnotation.grants().length;
            
            for ( Parameter mp : method.getParameters() ) {

                PathParam param = mp.getAnnotation(PathParam.class);
                
                if ( param != null  && (temp = conditionsMap.get(param.value())) != null ) {
                    temp.bindClass = mp.getType();
                    countdown--;
        		    LOGGER.debug("mp.getType {} " , mp.getType());
        		    LOGGER.debug("param {} " , param.value());
        		}
            }
    
            if (countdown == 0) {
            	configuration.register(new AdminOrOwnerRequestFilter( aclAnnotation.admins(), conditionsMap, userDAO));
            	return;
            }
           	    
        }
    }
    
    private static class ConditionsCheck {
	final String methodName;
	final String bindParam;
        Class<?> bindClass;
        
        public ConditionsCheck(String bindParam, String methodName) {
            this.methodName = methodName;
            this.bindParam = bindParam;
        }
    }

//    class Struct {
//	final String oProp;
//	final String oValParam;
//	final String member;
//	final Class<?> oClass;
//	
//	public Struct(String oProp, String oValParam, Class<?> oClass, String member) {
//	    this.oProp = oProp;
//	    this.oValParam = oValParam;
//	    this.oClass = oClass;
//	    this.member = member;
//	}
//    }

    @Priority(Priorities.AUTHORIZATION) // authorization filter - should go after any authentication filters
    private static class AdminOrOwnerRequestFilter implements ContainerRequestFilter {

        private final String[] adminRoles;
	private final Map<String, ConditionsCheck> conditionsMap;
	private final UserDAO userDAO;

//	@UnitOfWork
//	private boolean evalCondition(User user, String val, String methodName, Class<?> bindClass) {
//	    boolean resp = false;
//	    Method meth;
//	    try {
//		meth = user.getClass().getMethod(methodName, bindClass);
//		resp = (Boolean) meth.invoke(user, getTrueTypedVal(val, bindClass));
//	    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//		e.printStackTrace();
//	    }
//	    return resp;
//	}


	private <T> T getTrueTypedVal(final String val, final Class<T> clazz) {
	    try {
		if (clazz.equals(String.class)) {
		    return clazz.cast(val);
		}
		final Constructor<?> ctor = clazz.getConstructor(val.getClass());
		if ( ctor != null) {
		    return clazz.cast(ctor.newInstance(val));
		}
	    } catch (NoSuchMethodException | SecurityException
		    | InstantiationException | IllegalAccessException
		    | IllegalArgumentException | InvocationTargetException e) {
		e.printStackTrace();
	    }
	    return null;
	}
	
        AdminOrOwnerRequestFilter(final String[] adminRoles, final Map<String, ConditionsCheck> conditionsMap, UserDAO userDAO) {
            this.adminRoles = adminRoles;
            this.conditionsMap = conditionsMap;
            this.userDAO = userDAO;
        }

        @UnitOfWork
	@Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {

            User user = (User) requestContext.getSecurityContext().getUserPrincipal();
	    if (user != null) {
//		userDAO.loadAssets(user, new String[] {"roles", "children", "playlists" } );
//		userDAO.loadRoles(user);
//		userDAO.loadChildren(user);
//		userDAO.loadPlaylists(user);
//		user.getRoles();
//		user.getPlaylists();
//		user.getChildren();
		
		if (!user.hasAnyRole(this.adminRoles)) {
		    MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();
		    LOGGER.debug("map: " + map);
		    Function<Map.Entry<String,AdminOrOwnerDynamicFeature.ConditionsCheck>, Boolean> mapFunc =  (entry) -> {
//			return evalCondition( user, map.getFirst(entry.getKey()), entry.getValue().methodName , entry.getValue().bindClass);
			    boolean resp = false;
			    Method meth;
			    try {
				meth = user.getClass().getMethod(entry.getValue().methodName, entry.getValue().bindClass);
				resp = (Boolean) meth.invoke(user, getTrueTypedVal(map.getFirst(entry.getKey()), entry.getValue().bindClass));
			    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			    }
			    return resp;
		    };
		    if (!
			    conditionsMap.entrySet().stream().map(mapFunc).reduce(true, (a, b) -> a && b)
		    ) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		    }
		}
	    }
        }

    }
}
