package finki.ukim.mk.battlesofhistory;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
/*
    This activity presents a google map on screen and pinpoints the place where the chosen battle happened.
    It uses Google Map services for presenting the map and Geocoder along with latitude and longitude
        libraries for showing a pin on the place fetched from dbpedia.

    Geocoder is a class with which we can get a map location(Latitude and Longitude) from a given location name.
 */
public class MapActivity extends ActionBarActivity implements OnMapReadyCallback{

    private Geocoder geocoder;
    private String placeUri;
    private double lat;
    private double lon;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setting the view
        setContentView(R.layout.activity_map);

        //getting the uri of the place where the batttle happened
        Intent intent = getIntent();
        placeUri = intent.getExtras().getString("placeUri");

        //here we create a geocoder object
        geocoder = new Geocoder(this, Locale.ENGLISH);

        //here we get the latitude and longitude with a geocoder.
        //it accesses the internet that's why it is surrounded with a try-catch block
        try {
            //the address is returned as a list, from which we get the latitude and the longitude
            List<Address> address = geocoder.getFromLocationName(placeUri, 1);
            lat = address.get(0).getLatitude();
            lon = address.get(0).getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //a simple message as a toast to let the user know the place for which the pin on the map is shown
        Toast.makeText(getApplicationContext(),
                "Fetching location for " + placeUri, Toast.LENGTH_LONG).show();

        //here we get the map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /*
        An overriden method for showing a pin(marker) on a google map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(placeUri));
    }
}
