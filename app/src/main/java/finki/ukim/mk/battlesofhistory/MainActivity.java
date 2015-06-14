package finki.ukim.mk.battlesofhistory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
/*
    This is the main activity where a list of historical periods is presented.
    The user chooses certain period, after which an intent is created.
    The intent contains the period as a key named historyPeriodKey.
    The value of the key is a string historyPeriod.
 */
public class MainActivity extends ActionBarActivity {

    //key the history period chosen, needed in intents
    public static final String historyPeriodKey = "historyPeriodKey";

    //the chosen history period
    String historyPeriod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting the UI
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    /*
        A placeholder fragment with a simple view
    */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    /*
        The next five methods are onClick methods which send the user to the ConflictsActivity
        where the historyPeriod is parsed for getting info of the selected historyPeriod.

        Each one sets the historyPeriod to the one own.
    */
    //btnAncientHistory onClick method
    public void ancientHistoryClick(View view){
        historyPeriod = "ancientHistory";
        Intent intent = new Intent(this, ConflictsActivity.class);
        intent.putExtra(historyPeriodKey, historyPeriod);

        startActivity(intent);
    }
    //btnPostclassicalEra onClick method
    public void postclassicalEraClick(View view){
        historyPeriod = "postclassicalEra";
        Intent intent = new Intent(this, ConflictsActivity.class);
        intent.putExtra(historyPeriodKey, historyPeriod);

        startActivity(intent);
    }
    //btnEarlyMperiod onClick method
    public void earlyMperiodClick(View view){
        historyPeriod = "earlyMperiod";
        Intent intent = new Intent(this, ConflictsActivity.class);
        intent.putExtra(historyPeriodKey, historyPeriod);

        startActivity(intent);
    }
    //btnMidMperiod onClick method
    public void midMperiodClick(View view){
        historyPeriod = "midMperiod";
        Intent intent = new Intent(this, ConflictsActivity.class);
        intent.putExtra(historyPeriodKey, historyPeriod);

        startActivity(intent);
    }
    //btnContemPeriod onClick method
    public void contemPeriodClick(View view){
        historyPeriod = "contemPeriod";
        Intent intent = new Intent(this, ConflictsActivity.class);
        intent.putExtra(historyPeriodKey, historyPeriod);

        startActivity(intent);
    }
}
