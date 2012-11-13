package com.sfdc.perf;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author psrinivasan
 *         Date: 11/7/12
 *         Time: 2:55 PM
 */
public class EventUtils {
    public static long timeDiff(String initialTime,
                                String finalTime,
                                DateFormat dateFormat) throws ParseException {
        Date d1 = dateFormat.parse(initialTime);
        Date d2 = dateFormat.parse(finalTime);
        return (d2.getTime() - d1.getTime());
    }

    public static ArrayList<Long> timeDiff(String initialTime,
                                           ArrayList<String> finalTimes,
                                           DateFormat dateFormat) throws ParseException {
        ArrayList<Long> result = new ArrayList<Long>(5);
        for (int i = 0; i < finalTimes.size(); i++) {
            result.add(timeDiff(initialTime, finalTimes.get(i), dateFormat));
        }
        return result;
    }
}
