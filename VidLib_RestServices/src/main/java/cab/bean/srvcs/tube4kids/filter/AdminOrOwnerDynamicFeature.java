package cab.bean.srvcs.tube4kids.filter;


import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.core.User;

public class AdminOrOwnerDynamicFeature implements DynamicFeature {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminOrOwnerDynamicFeature.class);
    
    public AdminOrOwnerDynamicFeature() {
	super();
    }

    @Override
    @UnitOfWork
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
	
	final Method method = resourceInfo.getResourceMethod();
        final AnnotatedMethod am = new AnnotatedMethod(method);
        AdminOrOwner aclAnnotation = am.getAnnotation(AdminOrOwner.class);

        if (aclAnnotation != null) {

            final ImmutableMap.Builder<String, ConditionsCheck> builder = ImmutableMap.builder();

            Arrays.asList(aclAnnotation.provoPropria()).forEach(x -> {
        		String[] parts = x.split(":");
        		builder.put(parts[1], new ConditionsCheck(parts[1], parts[0]));
            });
            
            final Map<String, ConditionsCheck> conditionsMap = builder.build();
            ConditionsCheck temp = null;
            int countdown = aclAnnotation.provoPropria().length;
            
            for ( Parameter mp : method.getParameters() ) {
                PathParam param = mp.getAnnotation(PathParam.class);
                if ( param != null  && (temp = conditionsMap.get(param.value())) != null ) {
                    temp.bindClass = mp.getType();
                    countdown--;
        		}
            }
    
            if (countdown == 0) {
            	configuration.register(new AdminOrOwnerRequestFilter( aclAnnotation.adminRoles(), conditionsMap));
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

    @Priority(Priorities.AUTHORIZATION)
    private static class AdminOrOwnerRequestFilter implements ContainerRequestFilter {

        private final String[] adminRoles;
	private final Map<String, ConditionsCheck> conditionsMap;

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
	
        AdminOrOwnerRequestFilter(final String[] adminRoles, final Map<String, ConditionsCheck> conditionsMap ) {
            this.adminRoles = adminRoles;
            this.conditionsMap = conditionsMap;
        }

	@Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {

            User user = (User) requestContext.getSecurityContext().getUserPrincipal();

	    if (user != null) {

		if (! user.hasAnyRole(this.adminRoles)) {

		    final MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();

		    final Function<Map.Entry<String,AdminOrOwnerDynamicFeature.ConditionsCheck>, Boolean> mapFunc =  (entry) -> {
			    try {
				Method meth = user.getClass().getMethod(entry.getValue().methodName, entry.getValue().bindClass);
				return (Boolean) meth.invoke(user, this.getTrueTypedVal(map.getFirst(entry.getKey()), entry.getValue().bindClass));
			    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			    }
			    return false;
		    };

		    if (  !  conditionsMap.entrySet().stream().map(mapFunc).reduce(true, (a, b) -> a && b) ) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		    }
		}
	    }
        }

    }

}


