package finki.ukim.mk.battlesofhistory.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import finki.ukim.mk.battlesofhistory.MainActivity;
import finki.ukim.mk.battlesofhistory.db.ConflictBattleDao;
import finki.ukim.mk.battlesofhistory.model.Conflict;
/*
    This class is a Service which will access the internet, gather the required data with a SPARQL request to the
        SPARQL dbpedia endpoint, formated as XML, parse the XML file and update the database and the view with the newly
        parsed data which will be ready for presentation.

    Every Android Service class has a method that need to be overridden:
        public int onStartCommand(Intent intent, int flags, int startId) - here we set some things that need to be set on the start of
            the service

    In this class there are also some constant strings defined at the beginning that will help with the query creation.

    There is also an AsyncTask object in which all the internet requiring tasks and parsing will be put. This will be run on a background thread
        so it won't interfere with the user experience (e.g. too much waiting)

*/
public class SparqlConflictRequestService extends Service {

    public static final String ANCIENT_HISTORY = "?date >= \"0001-01-01\"^^xsd:date && ?date <= \"0500-12-31\"^^xsd:date";
    public static final String POSTCLASSICAL_ERA = "?date > \"500-01-01\"^^xsd:date && ?date <= \"1500-12-31\"^^xsd:date";
    public static final String EARLY_MODERN_PERIOD = "?date > \"1500-01-01\"^^xsd:date && ?date <= \"1750-12-31\"^^xsd:date";
    public static final String MIDDLE_MODERN_PERIOD = "?date > \"1750-01-01\"^^xsd:date && ?date <= \"1914-12-31\"^^xsd:date";
    public static final String CONTEMPORARY_AGE = "?date > \"1914-01-01\"^^xsd:date && ?date <= \"2015-01-01\"^^xsd:date";

    public static final String DBPEDIA_URL = "http://dbpedia.org/sparql";

    public static final String SPARQL_RESULT_CONFLICT = "mk.ukim.finki.battlesofhistory.SPARQL_REQUEST_CONFLICT_DONE";

    private SparqlRequestTask sparqlRequest;
    private String historyPeriod;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    /*
        With this method we get the historyPeriod and we initiate and start the SparqlRequestTask for
        data retrieval and parsing.
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Here we get the history period string passed through the intent from the Main Activity
        historyPeriod = intent.getExtras().getString(MainActivity.historyPeriodKey);
        sparqlRequest = new SparqlRequestTask();
        sparqlRequest.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    /*
        This is is the main class which will fetch the data from dbpedia and parse it in model's format.
        It is a subclass of AsyncTask, which means it will run asychroniously on a background thread.

        It has two methods that need to be overridden:
            - protected Boolean doInBackground(String... params) - main task, what needs to be done
            - protected void onPostExecute(Boolean result) - what needs to be done after the execution of the main task is done

        There also is a method here called:
            - public boolean parseXml(InputStream in) - which gets an InputStream as argument(in this case an XML file) and
                every node is parsed as a Conflict object and added to the database.

        Here we use the androjena library for creating and executing queries on the dbpedia endpoint
     */
    private class SparqlRequestTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            //First, we create the SPARQL query that needs to be executed.
            //For that, a new Query object is created with the makeQuery function, where the history period string is passed.
            Query query = makeQuery(historyPeriod);

            //after that we use a QueryExecutrionFactory to execute the query
            QueryExecution quexec = QueryExecutionFactory.sparqlService(DBPEDIA_URL, query);

            //results are then contained in a ResultSet
            ResultSet results = quexec.execSelect();

            //and then are output as an XML in a ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsXML(baos, results);

            //this ByteArrayOutputStream is used to create a ByteArrayInputStream, so we can parse it
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            boolean parsingOk = false;

            //here the parsing is done using parseXml method
            if(bais!=null){
                try {
                    parsingOk = parseXml(bais);
                } catch (ParserConfigurationException | IOException
                        | SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //and we return true if the parsing succeeded
            return parsingOk;
        }
        /*
            After the AsyncTask is done we send a broadcast with the intent id SPARQL_RESULT_CONFLICT
            which the broadcast receiver gets back in the Main Activity
        */
        @Override
        protected void onPostExecute(Boolean result) {

            Intent intent = new Intent(SPARQL_RESULT_CONFLICT);
            sendBroadcast(intent);

            stopSelf();
        }
        /*
            This is the function that parses the XML file passed to it as input stream.
            It first gets all the nodes from the XML file, and then filters those with a tag "uri".
            All these nodes are manipulated and from each one a Conflict object is extracted.
            Each Conflict object extracted is inserted in the database, and when this job is done a true value is returned.
         */
        public boolean parseXml(InputStream in) throws SAXException,
                IOException, ParserConfigurationException {

            //first we create a Document object with a DocumentBuilderFactory
            //this Document object represents the entire XML tree
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document dom = db.parse(in);
            in.close();

            //here we get the root element of the document
            Element docEle = dom.getDocumentElement();

            //and we create a NodeList with all child elements with tag "uri"
            NodeList nl = docEle.getElementsByTagName("uri");
            if (nl != null && nl.getLength() > 0) {
                ConflictBattleDao dao = new ConflictBattleDao(getApplicationContext());
                dao.openWritable();
                for (int i = 0; i < nl.getLength(); i++) {

                    Element entry = (Element) nl.item(i);
                    String uri = null;
                    if(entry.hasChildNodes()){
                        uri = entry.getFirstChild().getNodeValue();
                    }

                    Conflict item = new Conflict(uri);

                    dao.insertConflict(item);
                }
                dao.close();
            }
            return true;
        }
    }

    /*
        This is a help function that returns a period string based on the historyPeriod that has been sent
        by the Main Activity(the actual that is chosen by the user)
     */
    public String getHistoryPeriodInterval(String historyPeriod){
        switch (historyPeriod){
            case "ancientHistory":
                return ANCIENT_HISTORY;
            case "postclassicalEra":
                return POSTCLASSICAL_ERA;
            case "earlyMperiod":
                return EARLY_MODERN_PERIOD;
            case "midMperiod":
                return MIDDLE_MODERN_PERIOD;
            case "contemPeriod":
                return CONTEMPORARY_AGE;
        }
        return null;
    }

    /*
        A history period interval is passed to this function(one of the constants declared earlier).
        It then creates a SPARQL query using a QueryFactory from the androjena library and the query is returned.
     */
    public Query makeQuery(String historyPeriodInterval){
        String conflictQuery =  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n" +
                                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                "\n" +
                                "select distinct ?conflict where\n" +
                                "{\n" +
                                "?conflict rdf:type dbpedia-owl:MilitaryConflict.\n" +
                                "?battle dbpedia-owl:isPartOfMilitaryConflict ?conflict;\n" +
                                "        dbpedia-owl:date ?date.\n" +
                                "FILTER("+ getHistoryPeriodInterval(historyPeriodInterval) +")\n" +
                                "}\n" +
                                "ORDER BY ?date\n" +
                                "LIMIT 100";
        return QueryFactory.create(conflictQuery);
    }
}
