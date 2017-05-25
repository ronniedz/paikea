package cab.bean.srvcs.tube4kids.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


public class DateConverter {

    public static final DateTimeFormatter parser = ISODateTimeFormat.dateTime();


    public static DateTime parseString(String isodate) {
	return parser.parseDateTime(isodate);
    }
}
