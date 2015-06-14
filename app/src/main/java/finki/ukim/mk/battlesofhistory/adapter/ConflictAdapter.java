package finki.ukim.mk.battlesofhistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import finki.ukim.mk.battlesofhistory.R;
import finki.ukim.mk.battlesofhistory.model.Conflict;
/*
    This is basically the adapter for the Conflict list view.
    What it does is, it has methods for creating and returning a View with Conflict objects, in which are
        set some properties that are needed.
    This View then is presented on the screen through a ListView.
    It contains a list of items List<Conflict> and has methods for getting info about it:
        - public void addConflict(List<Conflict> items) - adding a Conflict to the list
        - public int getCount() - returns the number of Conflicts in the list
        - public Object getItem(int position) - returns a Conflict object on the position specified
        - public long getItemId(int position) - returns a Conflict object id of the Conflict on the position specified
*/
public class ConflictAdapter extends BaseAdapter {

    //a list of Conflict items from the model
    private List<Conflict> items;

    //current context
    private Context ctx;

    //an inflater for inflating the view on the layout
    private LayoutInflater inflater;

    //a basic constructor with context to be set
    public ConflictAdapter(Context ctx) {
        super();
        this.items = new ArrayList<>();
        this.ctx = ctx;
        this.inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //a constructor with given list of items and context to be set
    public ConflictAdapter(List<Conflict> items, Context ctx) {
        super();
        this.items = items;
        this.ctx = ctx;
        this.inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /*
        This is a holder which will present the Conflict object:
            - it has a realative layout
            - contains a textView with the Conflict uri on the left side
            - and a button for choosing the desired Conflict on the right side
    */
    public class ConflictHolder {
        private RelativeLayout layout;
        private TextView tvConflictUri;
        private Button btnOpenBattles;
    }

    //a method for adding a Conflict object to the current list from a given Conflict list sent as argument
    public void addConflict(List<Conflict> items){
        for (Conflict item : items) {
            this.items.add(item);
        }

        //this method says that the view needs to be refreshed(i.e. the dataset has changed)
        this.notifyDataSetChanged();
    }

    //a method for returning the number of items in the list
    @Override
    public int getCount() {
        return items.size();
    }

    //a method for returning a Conflict object on the position specified
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    //a method for returning a Conflict object id on the position specified
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
        This is the most important method which returns the View that needs to be presented on screen.
        It first sets a holder for containing the Conflict object on the UI.
        After that it sets the properties of the holder's UI elements to match the given Conflict object's
            properties from the model.
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Conflict item = (Conflict)this.getItem(position);
        ConflictHolder holder = null;

        if(convertView==null){
            //setting of the holder, to present the Conflict object's properties
            //and inflating the UI elements
            holder = new ConflictHolder();
            holder.layout = (RelativeLayout) inflater.inflate(R.layout.item_conflict, null);
            holder.tvConflictUri = (TextView) holder.layout.findViewById(R.id.tvConfclitUri);
            holder.btnOpenBattles = (Button) holder.layout.findViewById(R.id.btnOpenBattles);
            convertView = holder.layout;
            convertView.setTag(holder);
        }

        //setting of the properties, from model data.
        holder = (ConflictHolder) convertView.getTag();
        holder.btnOpenBattles.setTag(item.getDbpediaObject());
        holder.tvConflictUri.setText(item.toString());

        return convertView;
    }
}
