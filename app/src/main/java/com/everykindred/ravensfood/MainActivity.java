package com.everykindred.ravensfood;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BlogFragment.OnFragmentInteractionListener {


    private TextView booksBadge;
    private TextView blogsBadge;
    private int menuItemId = R.id.nav_blogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        booksBadge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_books));
        blogsBadge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_blogs));
        initializeDrawerCount();

        drawerSelection();

        setInexactAlarm();
    }

    private void setInexactAlarm() {
        //set the pending intent for the broadcast receiver
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //set the alarm
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    private void initializeDrawerCount() {
        //Gravity property aligns the text
//        booksBadge.setGravity(Gravity.CENTER_VERTICAL);
//        booksBadge.setTypeface(null, Typeface.BOLD);
//        booksBadge.setTextColor(Color.RED);
//        booksBadge.setText("1 new");

        blogsBadge.setGravity(Gravity.CENTER_VERTICAL);
        blogsBadge.setTypeface(null, Typeface.BOLD);
        blogsBadge.setTextColor(Color.RED);
        blogsBadge.setText("2 new");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //drawerSelection();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //restore menu id and tab position for media and faq drawer selections
        //this.menuItemId = savedInstanceState.getInt(MENU_ITEM);
        //Log.i("tag", "restore state" + this.menuItemId);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save menu id and tab position for media and faq drawer selections
        //outState.putInt(MENU_ITEM, menuItemId);
        //Log.i("tag", "outstate" + menuItemId);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            getPreferences();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private HashMap<String, String> putData(String title, String subtitle) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("title", title);
        item.put("subtitle", subtitle);
        return item;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        menuItemId = item.getItemId();

        drawerSelection();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void drawerSelection() {
        int id = menuItemId;

        Bundle extras = new Bundle();
        if (id == R.id.nav_home) {
            extras.putString("url", getString(R.string.urlHome));
            addFragments(WebFragment.class, extras);
        } else if (id == R.id.nav_books) {
            extras.putString("url", getString(R.string.urlBooks));
            addFragments(WebFragment.class, extras);
        } else if (id == R.id.nav_media) {
            extras.putString("url", getString(R.string.urlMedia));
            addFragments(WebFragment.class, extras);
        } else if (id == R.id.nav_blogs) {
            //extras.putSerializable("blogs", blogList);

            addFragments(BlogFragment.class, extras);
        } else if (id == R.id.nav_contact) {
            extras.putString("url", getString(R.string.urlContact));
            addFragments(WebFragment.class, extras);
        } else if (id == R.id.nav_doug) {
            //open webpage in browser
            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.urlDoug)));
            startActivity(intent);
        } else if (id == R.id.nav_michael) {
            //open webpage in browser
            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.urlMichael)));
            startActivity(intent);
        } else if (id == R.id.nav_sam) {
            //open webpage in browser
            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.urlSam)));
            startActivity(intent);
        }
    }

    public void addFragments(Class fragmentClass, Bundle extras) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(extras);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FrameLayout fl = (FrameLayout) findViewById(R.id.frameLayout);
        fl.removeAllViews();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    public void loadBlogWebpage(String blogTitle, String blogUrl) {
        Intent intent = new Intent(this, BlogActivity.class);
        intent.putExtra("blogTitle", blogTitle);
        intent.putExtra("blogUrl", blogUrl);
        startActivity(intent);
    }
}
