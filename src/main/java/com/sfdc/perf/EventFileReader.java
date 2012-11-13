package com.sfdc.perf;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author psrinivasan
 *         Date: 10/30/12
 *         Time: 3:40 PM
 */
public class EventFileReader {
    private final String file_name;
    private BufferedReader reader;
    private static final String EVENT_PREFIX = "INFO: ReceivedEvent~";
    private static final String TOKEN = "~";
    public static final String EVENT_TIME_STAMP = "EventTimeStamp";
    public static final String EVENT_DATA = "EventData";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public EventFileReader(String file_name) throws FileNotFoundException {
        this.file_name = file_name;
        this.reader = new BufferedReader(new FileReader(file_name));

    }

    public String getNextLine() throws IOException {
        String nextLine = reader.readLine();
        if (nextLine == null) {
            return null;
        }
        //System.out.println(nextLine);
        return nextLine;
    }

    public String getNextEventLine() throws IOException {
        String line = getNextLine();
        while (line != null) {
            if (line.startsWith(EVENT_PREFIX)) {
                return line;
            } else {
                line = getNextLine();
            }
        }
        return line;
    }

    public HashMap<String, String> getEventTimeStampAndEventData(String eventLine) throws Exception {
        StringTokenizer strtok = new StringTokenizer(eventLine, TOKEN);
        if (strtok.countTokens() < 2) {
            throw new Exception("Event Line must have at least two tokens");
        }
        HashMap h = new HashMap(2);
        strtok.nextToken(); //ignore the first element.
        h.put(EVENT_TIME_STAMP, strtok.nextToken());
        h.put(EVENT_DATA, strtok.nextToken());
        return h;
    }

    public Date convertStringToDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.parse(dateString);

    }

    public ArrayList<String> getOccurrencesOfCreatedDate(String body) throws Exception {
        try {
            if ((body == null) || (body == "")) {
                //LOGGER.error("Received Empty Response Body!");
                throw new Exception("Received Empty Body");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> resultsList = new ArrayList<String>(10);
        recursiveTokenSearch(rootNode, resultsList, "createdDate");
        return resultsList;
    }

    public void recursiveTokenSearch(JsonNode rootNode, ArrayList<String> resultsList, String searchString) throws Exception {
        if (rootNode.isArray()) {
            //we're an array
            // so just call the recursive search on the elements.  nothing fancy here.
            //System.out.println("Size of JSON Array is " + rootNode.size());
            for (int i = 0; i < rootNode.size(); i++) {
                //System.out.println("name of array element " + i + " is " + rootNode.get(i).asText());
                recursiveTokenSearch(rootNode.get(i), resultsList, searchString);
            }

        } else if (rootNode.isObject()) {
            // we're in an object .
            // Iterate over all child nodes.
            // check if we have any value nodes.  if we do, then check to see if any nodes matching searchstring are available.
            // if not recursively search the non child nodes.
            Iterator<String> itr = rootNode.getFieldNames();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                if (rootNode.path(fieldName).isValueNode()) {
                    //We're a value node - good time to end the recursion one way or the other.
                    if (searchString.equalsIgnoreCase(fieldName)) {
                        //awesome! we have a hit!
                        resultsList.add(rootNode.path(fieldName).asText());
                    }
                } else {
                    // ok, we're not a value node, so continue searching recursively
                    recursiveTokenSearch(rootNode.path(fieldName), resultsList, searchString);
                }
            }
        } else if (rootNode.isValueNode()) {
            //we're value node.
            //shouldn't come here since we should have checked for value nodes in the object node code itself.
            //throw an exception since this indicates a bug in the object node search code
            throw new Exception("Hmmm ... didn't expect to find myself here.  Not sure what to do now");

        } else {
            throw new Exception("Dont know what type of node this is " + rootNode.toString());
        }
    }
}
