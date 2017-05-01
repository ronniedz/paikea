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
import java.util.Optional;

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
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
	
	final Method method = resourceInfo.getResourceMethod();

        final AnnotatedMethod am = new AnnotatedMethod(method);

        AdminOrOwner aclAnnotation = am.getAnnotation(AdminOrOwner.class);

        final String[] propId = aclAnnotation.member().split("\\.");

        if (aclAnnotation != null) {

            LOGGER.debug("method.getName() {} " , method.getName());

            for ( Parameter mp : method.getParameters() ) {

                PathParam param = mp.getAnnotation(PathParam.class);

                if ( param != null  && param.value().equals(aclAnnotation.bindParam())) {

        		    LOGGER.debug("mp.getName {} " , mp.getName());
        		    LOGGER.debug("mp.getType {} " , mp.getType());
        		    LOGGER.debug("param {} " , param.value());
        		    LOGGER.debug("annotationType {} " , param.annotationType());
        		    LOGGER.debug("getClass {} " , param.getClass());
        		    LOGGER.debug("true {} " , param.value().equals(aclAnnotation.bindParam()));
        		    configuration.register(new AdminOrOwnerRequestFilter());
        		    return;
        		}
            }
    
           	    
        }
    }
    class Struct {
	final String oProp;
	final String oValParam;
	final String member;
	final Class<?> oClass;
	
	public Struct(String oProp, String oValParam, Class<?> oClass, String member) {
	    this.oProp = oProp;
	    this.oValParam = oValParam;
	    this.oClass = oClass;
	    this.member = member;
	}
    }

    @Priority(Priorities.AUTHORIZATION) // authorization filter - should go after any authentication filters
    private static class AdminOrOwnerRequestFilter implements ContainerRequestFilter {

        private final String[] adminRoles;
        private final Class userObject;
	private final String propId;
	private final String propName;
	private final String member;
	private final UserDAO userDAO;
	private final Class<?> clazz;
	private final Struct dat;

        AdminOrOwnerRequestFilter() {
            this.adminRoles = null;
            this.userObject = null;
            this.propId = null;
            this.propName = null;
            this.userDAO = null;
            this.clazz = null;
            this.member = null;
            this.dat = null;
        }

        public AdminOrOwnerRequestFilter(final String[] adminRoles, final Struct dat, final UserDAO userDAO, Class<?> class1) {
            this.adminRoles = (adminRoles != null) ? adminRoles : new String[] {};
            this.userDAO = userDAO;
            this.clazz = class1;
            this.dat = dat;
            this.userObject = dat.oClass;
            this.propId = dat.oProp;
            this.propName = dat.oValParam;
            this.member = dat.member;
        }

        //@UnitOfWork
	@Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {

            User user = (User) requestContext.getSecurityContext().getUserPrincipal();
            if (user != null) {
        		MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();
		LOGGER.debug("map: " + map);
		LOGGER.debug("map.propName: " + map.getFirst(propName));

		String val = map.getFirst(propName);

		try {

		    
		    Object valorReal = getTrueTypedVal(val);
		    Object o = PropertyUtils.getProperty(user, this.member);

		    if (isSameType(o, userObject)) {
			
		    }
		    
		} catch (NoSuchMethodException | SecurityException
		 | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}


//		for (Field field : user.getClass().getDeclaredFields()) {
//		    LOGGER.debug("field.getName(): " + field.getName());
//
//		    if (field.getName().equalsIgnoreCase("children")) {
//			
//		    }
//		    field.setAccessible(true); // You might want to set modifier to public first.
		LOGGER.debug("adminRoles status {}" , user.hasAnyRole(adminRoles));
		
		boolean goon = user.isMyChild(Long.parseLong(map.getFirst(propName)));
		
		LOGGER.debug("status {}" , goon);
		
		if (! goon) {
	            throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}
		
//            userDAO.create(user);
            
//	    if ( isAuthenticated(requestContext)) {
//        		
////        		return;
////                for (final String role : adminRoles) {
////                    if (requestContext.getSecurityContext().isUserInRole(role)) {
////                		MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();
////                		LOGGER.debug("map.propName: " + map.getFirst(propName));
////                        return;
////                    }
////                }
//                
////                for (final String prop : userProps) {
////                    if ()
////                   final String[] inner_dot_prop = prop.split(".");
////                   
////                }
//                
//            }
//            else { 
//                throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
//            }
//
            }
            else
            throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
        }

	private Object getTrueTypedVal(final String val) {
	    Constructor<?> ctor = null;
	    try {
		return val != null ? (val.matches("^-?\\d+$") ? ((ctor = clazz
		    .getConstructor(String.class)) != null) ? ctor
		    .newInstance(val) : null : val) : null;
	    } catch (NoSuchMethodException | SecurityException
		    | InstantiationException | IllegalAccessException
		    | IllegalArgumentException | InvocationTargetException e) {
		e.printStackTrace();
		return null;
	    }
	}

	private boolean isSameType(Object o, Class userObject2) {
	    // TODO Auto-generated method stub
	    return false;
	}

    }
}
