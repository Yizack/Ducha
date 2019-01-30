package com.esmifrase.duchita;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import prefs.UserInfo;
import prefs.UserSession;

public class Duchita extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView cronometro;
    private UserInfo userInfo;
    private UserSession userSession;
    public boolean isChronometerRunning = false;
    public static boolean isActiveShampoo = false;
    public static int Intervalo = 5; // Cada cuánto se reproduce la alarma. (MINUTOS)
    public static int sIntervalo = 0; // Cada cuánto se reproduce la alarma. (SEGUNDOS) de 0 a 59
    private Intent intent_service;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userInfo      = new UserInfo(this);
        userSession   = new UserSession(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!userSession.isUserLoggedin()){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        String username = userInfo.getKeyUsername();
        String email    = userInfo.getKeyEmail();
        userInfo.setAntEmail(email);
        userInfo.setAntUsername(username);
        Log.i("Login: ",email+", "+username);
        cronometro = findViewById(R.id.Cronometro);
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton media = findViewById(R.id.media);
        FloatingActionButton shampoo = findViewById(R.id.shampoo);
        intent_service = new Intent(getApplicationContext(), Cronometro.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChronometerRunning) {
                    isChronometerRunning = true;
                    servicio(Cronometro.START);
                }
                else{
                    isChronometerRunning = false;
                    servicio(Cronometro.STOP);
                }
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Esta función aún no está implementada", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        shampoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChronometerRunning)
                    servicio(Cronometro.STOP);
                isChronometerRunning = false;
                if(!isActiveShampoo){
                    Intervalo = Intervalo*2;
                    sIntervalo = sIntervalo*2;
                    isActiveShampoo = true;
                    Snackbar.make(view, "Modo Shampoo activado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    Intervalo = Intervalo/2;
                    sIntervalo = sIntervalo/2;
                    isActiveShampoo = false;
                    Snackbar.make(view, "Modo Shampoo desactivado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView tvUsername = header.findViewById(R.id.key_username);
        TextView tvEmail = header.findViewById(R.id.key_email);

        tvUsername.setText(username);
        tvEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String texto_cronometro = intent.getStringExtra("tiempo");
                cronometro.setText(texto_cronometro);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        context.registerReceiver(br, new IntentFilter(Cronometro.receiver));
        Log.i("Duchita", "Broadcast receiver registrado.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(br);
        Log.i("Duchita", "Broadcast receiver no registrado.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isChronometerRunning)
            servicio(Cronometro.STOP);
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
        else if (id == R.id.nav_salir) {
                userSession.setLoggedin(false);
                userInfo.clearUserInfo();
                if(isChronometerRunning)
                    servicio(Cronometro.STOP);
                isChronometerRunning = false;
                startActivity(new Intent(Duchita.this, Login.class));
                finish();
        }
        else if(id == R.id.nav_introduccion){
            startActivity(new Intent(Duchita.this, Intro.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void servicio(String accion){
        intent_service.setAction(accion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent_service);
        else
            context.startService(intent_service);
    }
}
