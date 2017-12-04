package com.esmifrase.duchita;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import prefs.UserInfo;
import prefs.UserSession;

public class Duchita extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvUsername, tvEmail;
    private Chronometer chronometer;
    private UserInfo userInfo;
    private UserSession userSession;
    private boolean isChronometerRunning = false;
    private boolean isActiveShampoo = false;
    private Handler mHandler;
    private static int minutes = 5;
    private static int seconds = 60;
    private int mInterval = (seconds*minutes)*1000;
    private long timeRunning = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userInfo      = new UserInfo(this);
        userSession   = new UserSession(this);
        mHandler = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!userSession.isUserLoggedin()){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        String username = userInfo.getKeyUsername();
        String email    = userInfo.getKeyEmail();

        userInfo.setAntEmail(email);
        userInfo.setAntUsername(username);

        System.out.println(email+", "+username);
        chronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton media = (FloatingActionButton) findViewById(R.id.media);
        FloatingActionButton shampoo = (FloatingActionButton) findViewById(R.id.shampoo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChronometerRunning == false) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    isChronometerRunning = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRepeatingTask();
                        }
                    }, mInterval);
                }
                else{
                    chronometer.stop();
                    isChronometerRunning = false;
                    stopRepeatingTask();
                }
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Esta función aún no está implementada", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        shampoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isActiveShampoo == false){
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    mInterval = mInterval*2;
                    isActiveShampoo = true;
                    isChronometerRunning = false;
                    stopRepeatingTask();
                    Snackbar.make(view, "Modo Shampoo activado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    mInterval = (seconds*minutes)*1000;
                    isActiveShampoo = false;
                    isChronometerRunning = false;
                    stopRepeatingTask();
                    Snackbar.make(view, "Modo Shampoo desactivado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        tvUsername      = (TextView)header.findViewById(R.id.key_username);
        tvEmail         = (TextView)header.findViewById(R.id.key_email);

        tvUsername.setText(username);
        tvEmail.setText(email);

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
    protected void onPause() {
        timeRunning = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (isChronometerRunning == true){
            timeRunning = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setBase(SystemClock.elapsedRealtime() - timeRunning);
            chronometer.start();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        stopRepeatingTask();
        super.onDestroy();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            startActivity(new Intent(Duchita.this, Perfil.class));
        }
        else if (id == R.id.nav_manage) {
                stopRepeatingTask();
                userSession.setLoggedin(false);
                userInfo.clearUserInfo();
                startActivity(new Intent(Duchita.this, Login.class));
                finish();
        }
        else if(id == R.id.nav_introduction){
            startActivity(new Intent(Duchita.this, Intro.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            }
            finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
