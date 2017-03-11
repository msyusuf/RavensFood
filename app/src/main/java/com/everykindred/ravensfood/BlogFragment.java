package com.everykindred.ravensfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogFragment extends ListFragment {
    private static final String ARG_BLOGS = "blogs";
    private static final String MENU_ITEM = "menu_item";
    private static final String url = "jdbc:mysql://ravensfood.db.9732590.hostedresource.com:3306/ravensfood";
    private static final String user = "ravensreadonly";
    private static final String pass = "";
    private static final String BLOGS_NEW = "blogs_new";

    private List<String> node = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private ProgressBar progressBar;
    private String selectStatement = "SELECT nid,title,created FROM node WHERE type = 'article' ORDER BY created";
    private String query;
    private static final String SORT_DESCENDING = "DESC";
    private static final String SORT_ASCENDING = "ASC";
    private String sortType = SORT_DESCENDING;
    private ArrayList<Map<String, String>>  blogs;
    private static String json_string;
    private JSONArray jsonArray;
    private SharedPreferences preferences;
    private ArrayList<String> newItems;

    public BlogFragment() {
        // Required empty public constructor
    }

    public static BlogFragment newInstance(String param1, String param2) {
        BlogFragment fragment = new BlogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BLOGS, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            //get the arraylist as serializable to pass to simple adapter
            //mBlogs = (ArrayList<Map<String, String>>) b.getSerializable(ARG_BLOGS);
        }

    }

    class GetBlogsBackground extends AsyncTask<Object, Object, String> {

        String json_url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             json_url = "http://ravensfood.everykindred.com/app/blogs_newest.php";
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                while ((json_string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(json_string);
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            json_string = result;
            parseJsonBlogs();

            //update the adapter after background thread complete
            updateListAdapter(blogs);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void parseJsonBlogs() {
        blogs = new ArrayList<>();

        try {

            jsonArray = new JSONArray(json_string);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String nid = json.getString("nid");
                String title = json.getString("title");
                String utc = json.getString("created");

                //if the created field is null then return 0
                String strUTC = (utc == null) ? "1": utc;
                String date = getDate(strUTC);
                //add values to hash table
                blogs.add(putData(title, String.valueOf(date)));

                //add the node id to array
                node.add(nid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Set<String> prefSet = preferences.getStringSet(BLOGS_NEW, null);
        newItems = new ArrayList<>();
        if (prefSet != null) {
            newItems = new ArrayList<>(prefSet);
        }

        //show the new item count as badge on blog drawer
        setBadge(newItems.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_blog, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarWeb);

        new GetBlogsBackground().execute();

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_sort);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(blogs);
                updateListAdapter(blogs);
            }
        });

        setRetainInstance(true);

        return rootView;
    }

    private void setBadge(int newItemCount) {
        //if there are new items set the string value
        String newItems;
        if (newItemCount > 0) {
            newItems = String.valueOf(newItemCount) + " new";
        } else {
            newItems = "";
        }

        //get the blog drawer item textview
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        TextView blogsBadge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_blogs));

        blogsBadge.setGravity(Gravity.CENTER_VERTICAL);
        blogsBadge.setTypeface(null, Typeface.BOLD);
        blogsBadge.setTextColor(Color.RED);
        blogsBadge.setText(newItems);

    }

    private void updateListAdapter(ArrayList<Map<String, String>> blogs) {
        //map to the keys
        String[] from = { "title", "subtitle" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        //int[] to = { R.id.blogTitle, R.id.blogSubtitle };

        CustomBlogAdapter simpleAdapter = new CustomBlogAdapter(getActivity(), blogs, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(simpleAdapter);

        //SimpleAdapter adapter = new SimpleAdapter(getActivity(), blogs, android.R.layout.simple_list_item_2, from, to);
        //setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String blogUrl;
        String baseUrl = getString(R.string.urlBlogs);
        String blogTitle = ((TextView) v.findViewById(android.R.id.text1)).getText().toString();

        if (newItems.size() > 0) {
            newItems.remove(blogTitle);
        }

        HashSet<String> stringSet = new HashSet<>();
        stringSet.addAll(newItems);

        //save current length and new blogs to SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(BLOGS_NEW, stringSet);
        editor.apply();

        blogUrl = baseUrl + node.get(position);

        //load the webpage for blog
        if (mListener != null) {
            mListener.loadBlogWebpage(blogTitle, blogUrl);
        }
    }

    private HashMap<String, String> putData(String title, String subtitle) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("title", title);
        item.put("subtitle", subtitle);
        return item;
    }

    private String getDate(String strUnix) throws ParseException {
        //create the formatter for the date and get date from snap
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        //convert the UTC from a string, convert unix time to milliseconds
        long unixSeconds = Long.parseLong(strUnix);
        Date date = new Date(unixSeconds*1000L);

        //String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        String dateString = dateFormat.format(date);

        return dateString;
    }

    private void getBlogs() {
        class ConnectDBasync extends AsyncTask<Object, Object, ArrayList<Map<String, String>>> {

            @Override
            protected void onPostExecute(ArrayList<Map<String, String>> blogs) {
                super.onPostExecute(blogs);

                //update the adapter after background thread complete
                updateListAdapter(blogs);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            protected ArrayList<Map<String, String>> doInBackground(Object... params) {

                query = selectStatement + " " + sortType;

                blogs = new ArrayList<>();

                try {
                    //check if class exists
                    Class.forName("com.mysql.jdbc.Driver");

                    //connect to the db with credentials
                    Connection connect = DriverManager.getConnection(url, user, pass);

                    //use statement to get the three needed columns and match on article which is blog, sort by date
                    Statement statement = connect.createStatement();

                    ResultSet resultSet = statement.executeQuery(query);
                    //ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                    //loop through all rows
                    while (resultSet.next()) {
                        //get the value of the title and created columns for current row
                        String title = resultSet.getString(2);
                        String utc = resultSet.getString(3);

                        //if the created field is null then return 0
                        String strUTC = (utc == null) ? "1": utc;
                        String date = getDate(strUTC);
                        //add values to hash table
                        blogs.add(putData(title, String.valueOf(date)));

                        //add the node id to array
                        node.add(resultSet.getString(1));
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return blogs;
            }
        }

        //execute the background thread
        ConnectDBasync connectDB = new ConnectDBasync();
        connectDB.execute();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void loadBlogWebpage(String blogTitle, String blogUrl);
    }
}
