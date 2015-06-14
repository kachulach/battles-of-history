package finki.ukim.mk.battlesofhistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import finki.ukim.mk.battlesofhistory.adapter.ConflictAdapter;
import finki.ukim.mk.battlesofhistory.db.ConflictBattleDao;
import finki.ukim.mk.battlesofhistory.model.Conflict;
import finki.ukim.mk.battlesofhistory.service.SparqlConflictRequestService;
/*
    This activity presents all the conflicts that happened in the chosen period in time.
    It sends a sparql request via outside service (SparqlConflictRequestService) and then all the conflicts are
    fetched back and presented to the user in a list view.
 */
public class ConflictsActivity extends ActionBarActivity {

    //this is the constant string with which a filter is created that is identified for broadcast receiving
    public static final String SPARQL_RESULT_CONFLICT = "mk.ukim.finki.battlesofhistory.SPARQL_REQUEST_CONFLICT_DONE";

    //the history period
    String historyPeriod;

    private ConflictBattleDao dao;
    private List<Conflict> items;
    private ListView conflictList;
    private ConflictAdapter conflictAdapter;
    private OnConflictRefreshReceiver onConflictRefreshReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting the desired view
        setContentView(R.layout.activity_conflicts);

        //creation of new database access object
        dao = new ConflictBattleDao(this);

        //creation of a new broadcast receiver that will receive the results from the service
        onConflictRefreshReceiver = new OnConflictRefreshReceiver();
        filter = new IntentFilter(SPARQL_RESULT_CONFLICT);

        Intent intent = getIntent();

        //here we get the history period, sent from the MainActivity
        historyPeriod = intent.getExtras().
                getString(MainActivity.historyPeriodKey);

        //the list view is filled with all the conflicts fetched
        conflictList = (ListView)findViewById(R.id.conflictList);
        conflictAdapter = new ConflictAdapter(this);
        conflictList.setAdapter(conflictAdapter);
    }

    protected void onStart() {
        super.onStart();

        //we register the receiver with the filter
        registerReceiver(onConflictRefreshReceiver, filter);

        //creation of new intent to the service for the fetching of the list of conflicts
        Intent intent = new Intent(getApplicationContext(), SparqlConflictRequestService.class);
        intent.putExtra(MainActivity.historyPeriodKey, historyPeriod);

        //reseting of the database with the database access object
        dao.openWritable();
        dao.trancateTableConflict();
        items = new ArrayList<>();
        dao.close();

        //checking for internet connection, if yes start the conflict retrival service
        if(isNetworkAvailable()){
            startService(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "There is no internet connection available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //we need to unregister the broadcast receiver when the app is stopped
        unregisterReceiver(onConflictRefreshReceiver);
    }
    /*
        This method is called when the user chooses a certain conflict.
        After it is chosen the BattlesActivity is started where all the battles for the chosen conflict are shown.
    */
    public void openBattlesActivity(View view) {
        /*
            Here we get the tag from the button of the chosen conflict.
            The tag contains the conflict uri, which will be needed for all the battles of that conflict
                to be fetched.
         */
        Button btnOpenBattles = (Button) view;
        String conflictUri = (String)btnOpenBattles.getTag();

        Intent intent = new Intent(getApplicationContext(), BattlesActivity.class);
        intent.putExtra("conflictUri", conflictUri);

        startActivity(intent);

    }
    /*
        This is the broadcast receiver which waits for the service to get the conflicts from the
         sparql endpoint on dbpedia.
        After it is done it refreshes the conflict list through the database access object.
    */
    class OnConflictRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            dao.openWritable();
            items = dao.getAllItemsConflict();
            dao.close();
            conflictAdapter.addConflict(items);
            conflictAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(),
                    "Conflict list loaded.", Toast.LENGTH_LONG).show();
        }
    }
    /*
        A function that checks for internet connection and returns a message as a toast if there is not one
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
