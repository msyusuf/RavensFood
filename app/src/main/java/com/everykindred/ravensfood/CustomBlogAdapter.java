package com.everykindred.ravensfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Brian on 3/4/17.
 */

public class CustomBlogAdapter extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<Map<String, String>> blogList;
    private SharedPreferences preferences;
    private static final String BLOGS_NEW = "blogs_new";

    public CustomBlogAdapter(Context context, ArrayList<Map<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.blogList = data;
        inflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        String blogTitle = blogList.get(position).get("title");

        Set<String> prefSet = preferences.getStringSet(BLOGS_NEW, null);
        List<String> stringSet = new ArrayList<>(prefSet);

        //for all new item matches highlight title font to red and bold
        for (int i = 0; i < stringSet.size(); i++) {
            if (blogTitle.equals(stringSet.get(i))) {
                TextView title = (TextView) view.findViewById(android.R.id.text1);
                title.setTextColor(Color.RED);
                title.setTypeface(null, Typeface.BOLD);
            }
        }

//        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }

}
