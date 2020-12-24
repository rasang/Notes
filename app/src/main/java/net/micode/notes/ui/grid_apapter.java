package net.micode.notes.ui;



import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

//import androidx.cursoradapter.widget.CursorAdapter;

import java.util.Random;

class MyAdapter extends BaseAdapter {
    private Context context;

    public MyAdapter(Context context){
        this.context = context;
    }

    private static final int[] COLOR = new int[] {
            0xff87CEFA, 0xffFFC0CB, 0xff7FFFAA, 0xffF0E68C, 0xffC0C0C0
    };

    @Override
    public int getCount() {
        return DATA.length;
    }

    @Override
    public Object getItem(int position) {
        return DATA[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(lp);
        }
        TextView view = (TextView) convertView;
        view.setText(DATA[position]);
        view.setBackgroundColor(COLOR[position % 5]);
        view.setGravity(Gravity.BOTTOM);
        view.setTextColor(Color.WHITE);
        AbsListView.LayoutParams lp = (AbsListView.LayoutParams) view.getLayoutParams();
        lp.height = (int) (getPositionRatio(position) * 200);
        view.setLayoutParams(lp);
        return view;
    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }


    public void bindView(View view, Context context, Cursor cursor) {

    }

    private final Random mRandom = new Random();
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
//        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
        return 2.5;
    }


    private static final String[] DATA = new String[]{
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
            "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
            "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
            "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
            "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
            "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
            "Baylough", "Beaufort", "Beauvoorde"
    };

}