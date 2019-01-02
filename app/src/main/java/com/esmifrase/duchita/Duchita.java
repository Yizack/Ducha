package com.esmifrase.duchita;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
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

public class Duchita extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Chronometer chronometer;
    private UserInfo userInfo;
    private UserSession userSession;
    private boolean isChronometerRunning = false;
    private boolean isActiveShampoo = false;
    private static int Minutos = 0;
    private static int MIntervalo = 5; // Cada cuánto se reproduce la alarma. (EN MINUTOS)
    private long timeRunning = 0;
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

        System.out.println(email+", "+username);
        chronometer = findViewById(R.id.simpleChronometer);
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton media = findViewById(R.id.media);
        FloatingActionButton shampoo = findViewById(R.id.shampoo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChronometerRunning) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer chronometer) {
                        String ss = chronometer.getText().charAt(3) + "" + chronometer.getText().charAt(4);
                        int Segundos = Integer.parseInt(ss);
                        if(Minutos == MIntervalo && Segundos == 0){ // Reproducir Sonido cada MIntervalo minutos
                            MediaPlayer sonido = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
                            sonido.start();
                            sonido.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer sonido) {
                                    sonido.release();
                                }});
                            System.out.println("\n*****\nSonido reproducido en: "+chronometer.getText());
                            Minutos = 0;
                        }
                        if(Segundos == 59){
                            Minutos++;
                        }
                    }});
                    chronometer.start();
                    isChronometerRunning = true;
                    activarAlarma();
                }
                else{
                    chronometer.stop();
                    isChronometerRunning = false;
                    desactivarAlarma();
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
                if(!isActiveShampoo){
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    MIntervalo = MIntervalo*2;
                    isActiveShampoo = true;
                    isChronometerRunning = false;
                    desactivarAlarma();
                    Snackbar.make(view, "Modo Shampoo activado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    MIntervalo = MIntervalo/2;
                    isActiveShampoo = false;
                    isChronometerRunning = false;
                    desactivarAlarma();
                    Snackbar.make(view, "Modo Shampoo desactivado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
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

    @Override
    protected void onPause() {
        timeRunning = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (isChronometerRunning){
            timeRunning = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setBase(SystemClock.elapsedRealtime() - timeRunning);
            chronometer.start();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        desactivarAlarma();
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
                desactivarAlarma();
                userSession.setLoggedin(false);
                userInfo.clearUserInfo();
                startActivity(new Intent(Duchita.this, Login.class));
                finish();
        }
        else if(id == R.id.nav_introduction){
            startActivity(new Intent(Duchita.this, Intro.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void activarAlarma() {
        agregarNotificacion();
    }

    void desactivarAlarma() {
        cancelarNotificacion();
    }

    private void agregarNotificacion() {
        if(!isActiveShampoo) {
            mostrarNotificacion(getString(R.string.alarma), getString(R.string.alarma_normal), getString(R.string.alarma_modo_normal));
        }
        else {
            mostrarNotificacion(getString(R.string.alarma), getString(R.string.alarma_shampoo), getString(R.string.alarma_modo_shampoo));
        }
    }

    private void cancelarNotificacion() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    private void mostrarNotificacion(String titulo, String min, String modo) {
        String CHANNEL_ID = "Canal_1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // SOPORTE PARA API 21 a 27
            CharSequence name = "DuchitaApp";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.duchita)
                    .setContentTitle(titulo)
                    .setContentText(min)
                    .setTicker(modo)
                    .setChannelId(CHANNEL_ID)
                    .setOngoing(true)
                    .build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1, notification);
        }
        else {
            NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID);
            b.setAutoCancel(true)
                    .setSmallIcon(R.drawable.duchita)
                    .setContentTitle(titulo)
                    .setContentText(min)
                    .setContentInfo(modo)
                    .setChannelId(CHANNEL_ID)
                    .setOngoing(true);
            NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, b.build());
        }
    }
}
