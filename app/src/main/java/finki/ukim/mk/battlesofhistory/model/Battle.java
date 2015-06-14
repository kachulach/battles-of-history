package finki.ukim.mk.battlesofhistory.model;
/*
    This is a class which creates a Battle object, which represents the Battle model of the app.
    Every Battle object has the properties:
        - id - String - an unique identifier
        - Uri - String - a unique identifier that points to a resource on dbpedia
        - Place - String - a unique identifier that points to a resource on dbpedia of the location where the batle happened

    Also has the getters and setters of this properties.
    There are two other methods too:
        - public String getLocation () - which returns a String of a cleaned, parsed Uri of a Place property,
            which will be used to be shown on the UI (e.g. if the Uri of place is "http://dbpedia.org/resource/Berovo%E2%80%93Macedonia"
            this method will return "Berovo-Macedonia")

        - public String toString () - similar to getLocation, only it returns the Uri of the Battle
 */
public class Battle {

    private Long id;

    private String Uri;

    private String Place;

    public Battle() {
    }

    public Battle(String uri, String place) {
        super();
        Uri = uri;
        Place = place;
    }

    public String getUri() {
        return Uri;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getPlace() { return Place; }

    public void setPlace(String place) {
        this.Place = place;
    }

    public String getLocation (){
        String location;
        location = Place;
        location = location.replace("http://dbpedia.org/resource/", "");
        location = location.replace("_", " ");
        location = location.replace("%E2%80%93", "-");

        return location;
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
