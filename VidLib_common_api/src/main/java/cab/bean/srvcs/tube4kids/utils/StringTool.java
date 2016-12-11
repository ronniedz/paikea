package cab.bean.srvcs.tube4kids.utils;

import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class StringTool {
    
    public static String doubleQuote(String str) {
	
//	return "\"" + str.replaceAll("(?<!\\)\"", "\\\"") + "\"";
	return "\"" + Pattern.quote(str) + "\"";
    }
    
    public static String singleQuote(String str) {
	return "'" + str.replaceAll("(?<!\\)'", "\\\'") + "'";
    }
    
    public static String join(Collection<String> list, String glue) {
	return StringUtils.join(list, glue);
    }
    
    public static String join(Collection<String> list, String glue, boolean elseBlank) {
	return list == null ? (elseBlank ? "" : null) : StringUtils.join(list, glue);
    }
    
    public static <T> String joinMap(Collection<T> list, String glue, Function<T, String> fn) {
	return list == null ? "" : list.stream().map( s -> fn.apply(s) ).collect(Collectors.joining(glue));
    }

}
