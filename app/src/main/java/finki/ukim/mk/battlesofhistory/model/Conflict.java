package finki.ukim.mk.battlesofhistory.model;
/*
    This is a class which creates a Conflict object, which represents the Conflict model of the app.
    Every Battle object has the properties:
        - id - String - an unique identifier
        - Uri - String - a unique identifier that points to a resource on dbpedia

    Also has the getters and setters of this properties.
    There are two other methods too:
        - public String getDbpediaObject() - which returns a String that formats the Uri of the Conflict in a
            dbpedia object notation (e.g. if the Conflict Uri is "http://dbpedia.org/resource/Skopje"
            this method will return "<Skopje>") - this will be needed when a SPARQL query is constructed in the service

        - public String toString () - which returns a String of a cleaned, parsed Uri of the name of the Conflict,
            which will be used to be shown on the UI (e.g. if the Uri of place is "http://dbpedia.org/resource/Krusevska%E2%80%93Republika"
            this method will return "Krusevska-Republika")
 */
public class Conflict {

    private Long id;

    private String Uri;

    public Conflict() {
    }

    public Conflict(String uri) {
        super();
        Uri = uri;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDbpediaObject (){
        String dbpediaObject;

        dbpediaObject = Uri;
        dbpediaObject = "<" + dbpediaObject + ">";
        dbpediaObject = dbpediaObject.replace("%E2%80%93", "-");
        return dbpediaObject;
    }

    @Override
    public String toString() {

        String returnUri;
        returnUri = Uri;
        returnUri = returnUri.replace("http://dbpedia.org/resource/", "");
        returnUri = returnUri.replace("_", " ");
        returnUri = returnUri.replace("%E2%80%93", "-");

        return returnUri;
    }
}
