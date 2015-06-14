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

import finki.ukim.mk.battlesofhistory.adapter.BattleAdapter;
import finki.ukim.mk.battlesofhistory.db.ConflictBattleDao;
import finki.ukim.mk.battlesofhistory.model.Battle;
import finki.ukim.mk.battlesofhistory.service.SparqlBattleRequestService;
/*
    This activity presents all the battles that in the chosen conflict.
    It sends a sparql request via outside service (SparqlBattleRequestService) and then all the battle are
    fetched back and presented to the user in a list view.
 */
public class BattlesActivity extends ActionBarActivity {

    //this is the constant string with which a filter is created that is identified for broadcast receiving
    public static final String SPARQL_RESULT_BATTLE = "mk.ukim.finki.battlesofhistory.SPARQL_REQUEST_BATTLE_DONE";

    //the uri of the chosen conflict
    String conflictUri;

    private ConflictBattleDao dao;
    private List<Battle> items;
    private ListView battleList;
    private BattleAdapter battleAdapter;
    private OnBattleRefreshReceiver onBattleRefreshReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting the desired view
        setContentView(R.layout.activity_battles);

        //creation of new database access object
        dao = new ConflictBattleDao(this);

        //creation of a new broadcast receiver that will receive the results from the service
        filter = new IntentFilter(SPARQL_RESULT_BATTLE);
        onBattleRefreshReceiver = new OnBattleRefreshReceiver();

        //here we get conflict uri, sent from the ConflictActivity
        Intent intent = getIntent();
        conflictUri = intent.getExtras().getString("conflictUri");

        //the list view is filled with all the conflicts fetched
        battleList = (ListView)findViewById(R.id.battleList);
        battleAdapter = new BattleAdapter(this);
        battleList.setAdapter(battleAdapter);
    }

    protected void onStart() {
        super.onStart();

        //we register the receiver with the filter
        registerReceiver(onBattleRefreshReceiver, filter);

        //creation of new intent to the service for the fetching of the list of conflicts
        Intent intent = new Intent(getApplicationContext(), SparqlBattleRequestService.class);
        intent.putExtra("conflictUri", conflictUri);

        //reseting of the database with the database access object
        dao.openWritable();
        dao.trancateTableBattle();
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
        unregisterReceiver(onBattleRefreshReceiver);
    }
    /*
        This is the broadcast receiver which waits for the service to get the battles from the
         sparql endpoint on dbpedia.
        After it is done it refreshes the conflict list through the database access object.
    */
    class OnBattleRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            dao.openWritable();
            items = dao.getAllItemsBattle();
            dao.close();

            battleAdapter.addBattle(items);
            battleAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(),
                    "Battle list loaded.", Toast.LENGTH_LONG).show();
        }
    }
    /*
        This method is called when the user chooses a certain battle.
        After it is chosen the MapActivity is started where the place where the battle was is pinpointed on a google map.
    */
    public void openMapActivity(View view){
        /*
            Here we get the tag from the button of the chosen battle.
            The tag contains the place uri, which will be needed for geo locating the place on a map.
         */
        Button btnOpenLocation = (Button) view;
        String placeUri = (String)btnOpenLocation.getTag();
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("placeUri", placeUri);
        startActivity(intent);
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
