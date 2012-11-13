package com.sfdc.perf;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author psrinivasan
 *         Date: 11/7/12
 *         Time: 3:10 PM
 */
public class EventUtilsTest extends TestCase {
    public void setUp() throws Exception {

    }

    public void testTimeDiff_1() throws Exception {
        String initialTime = "2012-11-04T22:46:09.000Z";
        String finalTime = "2012-11-04T22:46:09.000Z";
        long latency = EventUtils.timeDiff(initialTime, finalTime, new SimpleDateFormat(EventFileReader.DATE_FORMAT));
        assertEquals(0, latency);
    }

    public void testTimeDiff_2() throws Exception {
        String initialTime = "2012-11-04T22:46:09.000Z";
        ArrayList<String> list = new ArrayList<String>(6);
        list.add("2012-11-04T22:46:09.000Z");
        list.add("2012-11-04T22:46:09.000Z");
        list.add("2012-11-04T22:46:12.000Z");
        list.add("2012-11-04T22:46:12.000Z");
        ArrayList<Long> results = EventUtils.timeDiff(initialTime, list, new SimpleDateFormat(EventFileReader.DATE_FORMAT));
        assertEquals(4, results.size());
        assertEquals(0, results.get(0).longValue());
        assertEquals(0, results.get(1).longValue());
        assertEquals(3000, results.get(2).longValue());
        assertEquals(3000, results.get(3).longValue());
    }
}
