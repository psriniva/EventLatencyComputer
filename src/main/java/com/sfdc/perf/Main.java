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
        while ((eventLine = eventFileReader.getNextEventLine()) != null) {
            HashMap<String, String> eventData =
                    eventFileReader.getEventTimeStampAndEventData(eventLine);
            computeLatencies(eventData);

        }
    }

    public void computeLatencies(HashMap<String, String> eventData) throws Exception {
        String initialTime = eventData.get(EventFileReader.EVENT_TIME_STAMP);
        ArrayList<String> finalTimes =
                eventFileReader.getOccurrencesOfCreatedDate(eventData.get(EventFileReader.EVENT_DATA));
        ArrayList<Long> latencies =
                EventUtils.timeDiff(initialTime, finalTimes, new SimpleDateFormat(EventFileReader.DATE_FORMAT));
        openOutputFile();
        for (int i = 0; i < latencies.size(); i++) {
            writeData(latencies.get(i).toString() + "\n");
        }
        closeOutputFile();
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main(
                System.getProperty("events.log.file", "src/main/resources/test_events.dat"),
                System.getProperty("output.file", "latencies.out"));
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