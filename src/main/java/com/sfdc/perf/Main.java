package com.sfdc.perf;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author psrinivasan
 *         Date: 11/7/12
 *         Time: 3:37 PM
 */
public class Main {
    //TODO:  FIX THE BELOW DECLARATION TO BE IN A CONFIG FILE INSTEAD OF THE CODE!
    private static boolean CHATTER = true;
    private final String eventFileName;
    private final EventFileReader eventFileReader;
    private final String outputFileName;
    private File outputFile;
    private Writer output;
    private boolean outputClosed = false;

    public Main(String eventFileName, String outputFileName) throws FileNotFoundException {
        this.eventFileName = eventFileName;
        this.outputFileName = outputFileName;
        this.eventFileReader = new EventFileReader(this.eventFileName);
        this.outputFile = new File(this.outputFileName);
    }

    public void readAndProcessEvents() throws Exception {
        String eventLine;
        openOutputFile();
        while ((eventLine = eventFileReader.getNextEventLine()) != null) {
            HashMap<String, String> eventData =
                    eventFileReader.getEventTimeStampAndEventData(eventLine);
            computeLatencies(eventData);

        }
        closeOutputFile();
        System.out.println("Events processed: " + eventFileReader.getEventsRead());
        System.out.println("Input lines read: " + eventFileReader.getLinesRead());
    }

    public void computeLatencies(HashMap<String, String> eventData) throws Exception {
        String initialTime = eventData.get(EventFileReader.EVENT_TIME_STAMP);
        ArrayList<String> finalTimes =
                eventFileReader.getOccurrencesOfCreatedDate(eventData.get(EventFileReader.EVENT_DATA));
        ArrayList<Long> latencies = null;
        if (CHATTER) {
            latencies = EventUtils.timeDiff(initialTime,
                    finalTimes,
                    new SimpleDateFormat(EventFileReader.DATE_FORMAT));
        } else {
            //replace the "+0000" in other sfdc date format with a Z :)
            //TODO:  This needs to be generalized?
            ArrayList<String> finalTimes_2 = new ArrayList<String>(finalTimes.size());
            for (String dateString : finalTimes) {
                finalTimes_2.add(dateString.replace("+0000", "Z"));
            }
            latencies = EventUtils.timeDiff(initialTime, finalTimes_2,
                    new SimpleDateFormat(EventFileReader.DATE_FORMAT),
                    new SimpleDateFormat(EventFileReader.DATE_FORMAT));
        }

        for (int i = 0; i < latencies.size(); i++) {
            writeData(latencies.get(i).toString() + "\n");
        }
    }

    public static void main(String[] args) throws Exception {
//        Main main = new Main(
//                System.getProperty("events.log.file", "src/main/resources/test_events.dat"),
//                System.getProperty("output.file", "latencies.out"));
        Main main = new Main(
                System.getProperty("events.log.file", "/Users/psrinivasan/streaming_0_0.log"),
                System.getProperty("output.file", "/Users/psrinivasan/projects/EventLatencyComputer/target/foo"));
        main.readAndProcessEvents();
    }

    public void writeData(String data) throws IOException {
        output.write(data);
    }

    public void openOutputFile() throws IOException {
        this.output = new BufferedWriter(new FileWriter(outputFile));
    }

    public void closeOutputFile() throws IOException {
        if (this.outputClosed == false) {
            output.close();
        }
    }
}
