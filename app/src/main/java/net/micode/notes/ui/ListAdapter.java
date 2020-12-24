package net.micode.notes.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.micode.notes.R;

public class ListAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private String[] data = {"lai","hui","ying"};

    //构造有参函数
    public ListAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item,null);

        TextView id = view.findViewById(R.id.list_item);

        //设置属性
        id.setText(data[position]);
        return view;
    }

    /*@Override
    public boolean hasStableIds(){
        return true;
    }*/
}
