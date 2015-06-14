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
import finki.ukim.mk.battlesofhistory.model.Battle;
/*
    This is basically the adapter for the battle list view.
    What it does is, it has methods for creating and returning a View with Battle objects, in which are
        set some properties that are needed.
    This View then is presented on the screen through a ListView.
    It contains a list of items List<Battle> and has methods for getting info about it:
        - public void addBattle(List<Battle> items) - adding a Battle to the list
        - public int getCount() - returns the number of Battles in the list
        - public Object getItem(int position) - returns a Battle object on the position specified
        - public long getItemId(int position) - returns a Battle object id of the battle on the position specified
*/
public class BattleAdapter extends BaseAdapter {

    //a list of Battle items from the model
    private List<Battle> items;

    //current context
    private Context ctx;

    //an inflater for inflating the view on the layout
    private LayoutInflater inflater;

    //a basic constructor with context to be set
    public BattleAdapter(Context ctx) {
        super();
        this.items = new ArrayList<>();
        this.ctx = ctx;

        this.inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //a constructor with given list of items and context to be set
    public BattleAdapter(List<Battle> items, Context ctx) {
        super();
        this.items = items;
        this.ctx = ctx;
        this.inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*
        This is a holder which will present the Battle object:
            - it has a realative layout
            - contains a textView with the Battle uri on the left side
            - and a button for choosing the desired Battle on the right side
    */
    public class BattleHolder {
        private RelativeLayout layout;
        private TextView tvBattleUri;
        private Button btnOpenLocation;
    }

    //a method for adding a Battle object to the current list from a given Battle list sent as argument
    public void addBattle(List<Battle> items){
        for (Battle item : items) {
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

    //a method for returning a Battle object on the position specified
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    //a method for returning a Battle object id on the position specified
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
        This is the most important method which returns the View that needs to be presented on screen.
        It first sets a holder for containing the Battle object on the UI.
        After that it sets the properties of the holder's UI elements to match the given Battle object's
            properties from the model.
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Battle item = (Battle)this.getItem(position);
        BattleHolder holder = null;

        if(convertView==null){
            //setting of the holder, to present the Battle object's properties
            //and inflating the UI elements
            holder = new BattleHolder();
            holder.layout = (RelativeLayout) inflater.inflate(R.layout.item_battle, null);
            holder.tvBattleUri = (TextView) holder.layout.findViewById(R.id.tvBattleUri);
            holder.btnOpenLocation = (Button) holder.layout.findViewById(R.id.btnOpenLocation);
            convertView = holder.layout;
            convertView.setTag(holder);
        }

        //setting of the properties, from model data.
        holder = (BattleHolder) convertView.getTag();
        holder.tvBattleUri.setText(item.toString());
        holder.btnOpenLocation.setTag(item.getLocation());

        return convertView;
    }
}
