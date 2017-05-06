package cab.bean.srvcs.tube4kids.filter;


import io.dropwizard.hibernate.UnitOfWork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.api.x.Collection;

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.User;

/*     
 * A {@link DynamicFeature} that registers an authorization filter for resource methods
 * annotated with the {@link AdminOrOwner} annotations.
 */
public class AdminOrOwnerDynamicFeature implements DynamicFeature {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminOrOwnerDynamicFeature.class);
    
    public AdminOrOwnerDynamicFeature() {
	super();
    }
    
    private static class OwnerTest {

	public static enum DataType
	{
	    ENTITY, PATH_PARAM, ARG_INDEX
	}

	public static final  String PROP_SEP = ".";
	public static final  String FUNC_SEP = ":";

	public static final  String pathParamMarker = "\\$(.+)";
	public static final String numericStr = "^(\\d+)$";
	public static final String numericArgStr= "^arg(\\d+)$";
	public static final Pattern numPattern = Pattern.compile(numericStr);
	public static final Pattern argPattern = Pattern.compile(numericArgStr);
	public static final Pattern classPattern = Pattern.compile(pathParamMarker);

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
//	    if ((walker = classPattern.matcher(subject)).matches()) {
//		this.subject = subject.substring(1);
//		this.dSrc = DataType.ENTITY;
//		try {
//		    this.bindClass = Class.forName(subject);
//		    
//		} catch (ClassNotFoundException e) {
//		    e.printStackTrace();
//		}
//		this.argN = -1;
//	    } else 
		if ((walker = numPattern.matcher(subject)).matches()
		    || (walker = argPattern.matcher(subject)).matches()) {
		this.dSrc = DataType.ARG_INDEX;
		this.argN = Integer.parseInt(walker.group(1));
	    } else {
		this.argN = -1;
		this.dSrc = DataType.PATH_PARAM;
	    }
	}

        /**
	 * @return the subject
	 */
	public String getSubject() {
	    return subject;
	}


	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
	    this.subject = subject;
	}

    }

    
//    Parameter p = methodArgs[ Integer.parseInt( parts[1]) ];
//    p.getType();

    @Override
    @UnitOfWork
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
	
	final Method method = resourceInfo.getResourceMethod();
        final AnnotatedMethod am = new AnnotatedMethod(method);
        final AdminOrOwner aclAnnotation = am.getAnnotation(AdminOrOwner.class);

        if (aclAnnotation != null) {
            final ImmutableList.Builder<OwnerTest> listBuilder = ImmutableList.builder();

            Parameter[] methodArgs = method.getParameters();
            
            OwnerTest ot = null;
            for (String testString : aclAnnotation.provoPropria() ) {
        		ot = new OwnerTest(testString);

        		switch (ot.dSrc) {
        			case ARG_INDEX : {
        			    Parameter p = methodArgs[ot.argN ];
        			    ot.bindClass = p.getType();
        			    listBuilder.add(ot);
        			    break;
        			}
        			case PATH_PARAM : {
        		            for ( Parameter mp : methodArgs ) {
            		        	PathParam param = mp.getAnnotation(PathParam.class);
		                if ( param != null  && param.value().equals(ot.subject) ) {
		                    ot.bindClass = mp.getType();
		                    listBuilder.add(ot);
		                    break;
		        		}
        		            }
        			    break;
        			}
//        			case ENTITY : {
//        			    try {
//        				getTrueTypedVal(ot.subject , Class.forName(ot.subject));
//        				Class t = Class.forName(ot.subject);
//        				t.newInstance()
//				    } catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				    }
//        			    Parameter p = methodArgs[ot.argN ];
//        			    ot.bindClass = p.getType();
//        			    break;
//        			}
//        			default: 
        		}
            }
            
//            Arrays.asList(aclAnnotation.provoPropria()).forEach(x -> {
//        	
//        		builder.put(parts[1], new OwnerTest(parts[1], parts[0]));
//
//        		String[] parts = x.split(":");
//        		if (parts[1].matches("^\\d+$") ) {
//        		    Parameter p = methodArgs[ Integer.parseInt( parts[1]) ];
//        		    p.getType();
//        		}
//        		else if (parts[1].startsWith("$") ) {
//        		    
//        		    builder.put(parts[1], new OwnerTest(parts[1], parts[0]));
//            
//        		}
//        		builder.put(parts[1], new OwnerTest(parts[1], parts[0]));
//            });
            
//            final Map<String, OwnerTest> conditionsMap = builder.build();
//            OwnerTest temp = null;
//            int countdown = aclAnnotation.provoPropria().length;
//            listBuilder.build()
//            for ( Parameter mp : method.getParameters() ) {
//                PathParam param = mp.getAnnotation(PathParam.class);
//                if ( param != null  && (temp = conditionsMap.get(param.value())) != null ) {
//                    temp.bindClass = mp.getType();
//                    countdown--;
//        		}
//                else {
//                    
//                }
//            }
            final List<OwnerTest> conditionsList = listBuilder.build();

            if (conditionsList.size() == aclAnnotation.provoPropria().length) {
            	configuration.register(new AdminOrOwnerRequestFilter( aclAnnotation.adminRoles(), conditionsList));
            	return;
            }
        }
    }
	private static boolean isSimpleType(Class<?> type) {
	    return (type == Double.class || type == Float.class || type == Long.class ||
		    type == Integer.class || type == Short.class || type == Character.class ||
		    type == Byte.class || type == Boolean.class || type == String.class );
	}

    @Priority(Priorities.AUTHORIZATION)
    private static class AdminOrOwnerRequestFilter implements ContainerRequestFilter {

        private final String[] adminRoles;
	private final List<OwnerTest> conditionsList;

	protected static <T> T getTrueTypedVal(final String val, final Class<T> clazz) {
		LOGGER.debug("val: " + val);
		LOGGER.debug("clazz: " + clazz);

	    try {
		final Constructor<?> ctor = clazz.getConstructor(val.getClass());
		if ( ctor != null) {
		    if (Collection.class.isAssignableFrom(clazz)) {
			
		    }
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

	AdminOrOwnerRequestFilter(final String[] adminRoles, final List<OwnerTest> conditionsList ) {
            this.adminRoles = adminRoles;
            this.conditionsList = conditionsList;
        }

	@Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            User user = (User) requestContext.getSecurityContext().getUserPrincipal();

	    if (user != null) {

		if (! user.hasAnyRole(this.adminRoles)) {

		    Boolean resp = true;
		    final MultivaluedMap<String, String> map = requestContext.getUriInfo().getPathParameters();

		    LOGGER.debug("! user.hasAnyRole(this.adminRoles)");

		    try {
            		    for (OwnerTest criteria : conditionsList ) {
        				Method meth = user.getClass().getMethod(criteria.methodName, criteria.bindClass);
        				LOGGER.debug("criteria.subject: " + criteria.subject);
        				LOGGER.debug("criteria.bindClass: " + criteria.bindClass);
        				if ( isSimpleType(criteria.bindClass) ) {
        				    LOGGER.debug("isSimpleType");
        				    resp = resp && (Boolean) meth.invoke(user, AdminOrOwnerRequestFilter.getTrueTypedVal(map.getFirst(criteria.subject), criteria.bindClass));
        				}
        				else {
        				    LOGGER.debug("NOT SimpleType");
        				    LOGGER.debug("criteria.bindClass: " + criteria.bindClass);
        				    resp = resp &&  (Boolean) meth.invoke(user, getEntity(requestContext, criteria.bindClass));
        				}
            		    }
		    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		    }

//		    final Function<AdminOrOwnerDynamicFeature.OwnerTest, Boolean> mapFunc =  (criteria) -> {
//			    try {
//				Method meth = user.getClass().getMethod(criteria.methodName, criteria.bindClass);
//				if ( isSimpleType(criteria.bindClass) ) {
//				    LOGGER.debug("criteria.subject: " + criteria.subject);
//				    LOGGER.debug("criteria.bindClass: " + criteria.bindClass);
//				    return (Boolean) meth.invoke(user, AdminOrOwnerRequestFilter.getTrueTypedVal(map.getFirst(criteria.subject), criteria.bindClass));
//				}
//				else {
//				   return  (Boolean) meth.invoke(user, getEntity(requestContext, criteria.bindClass));
//				}
//			    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
//				return false;
//			    }
//		    };
//
//		    if (  !  conditionsList.stream().map(mapFunc).reduce(true, (a, b) -> a && b) ) {
//			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
//		    }
		    
		    if (  !  resp ) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		    }
		}
	    }
        }

	private final <T> T getEntity(ContainerRequestContext requestContext, Class<T> type) {
	    final InputStream in = requestContext.getEntityStream();
	    final ByteArrayOutputStream out = new ByteArrayOutputStream();
	        try {
	            if (in.available() > 0) {
	                ReaderWriter.writeTo(in, out);

	                byte[] requestEntity = out.toByteArray();

			requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));
	                
			LOGGER.debug("getting Entity  ");
//			LOGGER.debug("requestEntity: " + requestEntity.length);
			Object o = new ObjectMapper().readValue(requestEntity, type);
			LOGGER.debug("GOT Entity  " + ((Child)o).getId());
			return type.cast(o);
			
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	            throw new ContainerException(ex);
	        }
		return null;
	}
    }

}


