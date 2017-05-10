/**
 * Copyright (c) 2016-2018 Bean.cab USA
 * All rights reserved.
 */
package cab.bean.srvcs.tube4kids.filter;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import jersey.repackaged.com.google.common.collect.ImmutableList;

import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.core.User;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link DynamicFeature} that registers an authorization filter for resource methods
 * annotated with the {@link &#64;AdminOrOwner} annotation.
 * The general authorization rules are:
 * <ul>
 * <li>If principal is not found, a 401 response is returned.
 * <li>If the user is not in one of the declared <i>admin</i> roles, a 403 response is returned. 
 * <li>If the <i>ownership</i> tests from the annotation (Eg: <code> ownerTests= { "isMyChild:arg0"} ) </code>)  return false, a 403 response is returned. 
 * </ul>
 * <p/>
 * @author Ronald B. Dennison (ronniedz@gmail.com)
 */
public class AdminOrOwnerDynamicFeature implements DynamicFeature {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminOrOwnerDynamicFeature.class);
    
    public AdminOrOwnerDynamicFeature() { super(); }
    
    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
	final Method method = resourceInfo.getResourceMethod();
	final AdminOrOwner aclAnnotation = method.getAnnotation(AdminOrOwner.class);

	if (aclAnnotation != null) {
	    final ImmutableList.Builder<OwnerTest> listBuilder = ImmutableList .builder();
	    final Parameter[] methodArgs = method.getParameters();

	    for (String testString : aclAnnotation.ownerTests()) {
		listBuilder.add(createOwnershipTest(testString, methodArgs));
	    }
	    configuration.register(new AdminOrOwnerRequestFilter(aclAnnotation.adminRoles(), listBuilder.build()));
	}
    }

    private OwnerTest createOwnershipTest(String testString, final Parameter[] methodArgs) {
	final OwnerTest ot = new OwnerTest(testString);

	switch (ot.dSrc) {
	    case ARG_INDEX: {
		Parameter p = methodArgs[ot.argN];
		ot.bindClass = p.getType();
		break;
	    }
	    case PATH_PARAM: {
		for (Parameter mp : methodArgs) {
		    PathParam param = mp.getAnnotation(PathParam.class);
		    if (param != null && param.value().equals(ot.subject)) {
			ot.bindClass = mp.getType();
			break;
		    }
		}
		break;
	    }
	}
	return ot;
    }

    private static boolean isSimpleType(Class<?> type) {
	return (type == String.class || type == Long.class
		|| type == Integer.class || type == Boolean.class
		|| type == Float.class || type == Double.class
		|| type == Short.class || type == Byte.class
		|| type == Character.class);
    }

    /**
     * RequestFilter. Throws ForbiddenException if user is not an administrator or if the <i>test methods</i> 
     * (from the AdminOrOwner annotation) return false;
     */
    @Priority(Priorities.AUTHORIZATION)
    private static class AdminOrOwnerRequestFilter implements ContainerRequestFilter {
        private final String[] adminRoles;
	private final List<OwnerTest> conditionsList;

	AdminOrOwnerRequestFilter(final String[] adminRoles, final List<OwnerTest> conditionsList ) {
            this.adminRoles = adminRoles;
            this.conditionsList = conditionsList;
        }
	
	@Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            final User user = (User) requestContext.getSecurityContext().getUserPrincipal();

	    if (user == null) {
		throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
	    }
	    
	    if (! user.hasAnyRole(this.adminRoles)) {
		final MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();
		
		/*
		 * Execute the Ownership tests.
		 */
		final Function<AdminOrOwnerDynamicFeature.OwnerTest, Boolean> mapFunc =  (criteria) -> {
		    try {
			return ( isSimpleType(criteria.bindClass) )
				? (Boolean) user.getClass().getMethod(criteria.methodName, criteria.bindClass)
					.invoke(user, AdminOrOwnerRequestFilter.getTrueTypedVal(map.getFirst(criteria.subject), criteria.bindClass))
					
				: (Boolean) user.getClass().getMethod(criteria.methodName, criteria.bindClass)
					.invoke(user, getEntity(requestContext, criteria.bindClass));
					
		    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		    }
		};
		
		if (  !  conditionsList.stream().map(mapFunc).reduce(true, (a, b) -> a && b) ) {
		    throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}
	    }
        }

	/**
	 * Return  <code>val</code> as an object of type <code>clazz</code>.
	 *
	 * @param val
	 * @param clazz
	 * @return
	 */
	static <T> T getTrueTypedVal(final String val, final Class<T> clazz) {
	    try {
		final Constructor<?> ctor = clazz.getConstructor(val.getClass());
		if ( ctor != null) {
		    return clazz.cast(ctor.newInstance(val));
		} else {
		    return clazz.cast(val);
		}
	    } catch (NoSuchMethodException | SecurityException
		    | InstantiationException | IllegalAccessException
		    | IllegalArgumentException | InvocationTargetException e) {
		e.printStackTrace();
	    }
	    return null;
	}

	/**
	 * Read <code>type</code> from the Request Body.
	 *
	 * TODO Replace with registered JsonProvider.getEntity [or similar]
	 *
	 * @param requestContext
	 * @param type of the object in the request body 
	 * @return the Object read from the request body.
	 */
	private final <T> T getEntity(ContainerRequestContext requestContext, Class<T> type) {
	    final InputStream in = requestContext.getEntityStream();
	    final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
		if (in.available() > 0) {
		    ReaderWriter.writeTo(in, out);
		    byte[] requestEntity = out.toByteArray();
		    requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));
		    return new ObjectMapper().readValue(requestEntity, type);
		}
	    } catch (IOException ex) {
		ex.printStackTrace();
		throw new ContainerException(ex);
	    }
	    return null;
	}
    }
    
    private static class OwnerTest {

	public static enum DataType
	{
	    PATH_PARAM, ARG_INDEX
	}

	private static final  String PROP_SEP = ".";
	private static final  String FUNC_SEP = ":";
	private static final String numericStr = "^(\\d+)$";
	private static final String numericArgStr= "^arg(\\d+)$";
	private static final Pattern numPattern = Pattern.compile(numericStr);
	private static final Pattern argPattern = Pattern.compile(numericArgStr);

	private final String methodName;
	private final DataType dSrc;
	private final int argN;
	private final String subjectProperty;

	private String subject;
        Class<?> bindClass;

	public OwnerTest(String testString) {

	    String[] parts = testString.split(FUNC_SEP);

	    if (parts.length != 2) {
		throw new ContainerException("Misconfig");
	    }

	    this.methodName = parts[0];

	    if (parts[1].contains(PROP_SEP)) {
		final String[] inner = parts[1].split(PROP_SEP);
		this.subject = inner[0];
		this.subjectProperty = inner[1];
	    } else {
		this.subject = parts[1];
		this.subjectProperty = null;
	    }
	    Matcher walker = null;
	    if ((walker = numPattern.matcher(subject)).matches() || (walker = argPattern.matcher(subject)).matches()) {
		this.dSrc = DataType.ARG_INDEX;
		this.argN = Integer.parseInt(walker.group(1));
	    } else {
		this.argN = -1;
		this.dSrc = DataType.PATH_PARAM;
	    }
	}
    }

}


