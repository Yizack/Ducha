package com.esmifrase.duchita;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import prefs.UserInfo;
import prefs.UserSession;

public class Duchita extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView cronometro;
    private UserInfo userInfo;
    private UserSession userSession;
    private boolean isChronometerRunning = false;
    private boolean isActiveShampoo = false;
    private static int Intervalo = 5; // Cada cuánto se reproduce la alarma. (MINUTOS)
    private static int sIntervalo = 0; // Cada cuánto se reproduce la alarma. (SEGUNDOS)
    private Context context = this;
    private Handler timerhandler = new Handler();
    private int Minutos, min = 0;
    private int Segundos, seg = 0;

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
        cronometro = findViewById(R.id.Cronometro);
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton media = findViewById(R.id.media);
        FloatingActionButton shampoo = findViewById(R.id.shampoo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChronometerRunning) {
                    startCronometro();
                    isChronometerRunning = true;
                    agregarNotificacion();
                }
                else{
                    stopCronometro();
                    isChronometerRunning = false;
                    cancelarNotificacion();
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
                isChronometerRunning = false;
                stopCronometro();
                cancelarNotificacion();
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        cancelarNotificacion();
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
                cancelarNotificacion();
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
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "Canal_1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // SOPORTE PARA API 24+
            CharSequence name = "DuchitaApp";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.duchita)
                    .setContentTitle(titulo)
                    .setContentText(min)
                    .setTicker(modo)
                    .setChannelId(CHANNEL_ID)
                    .setUsesChronometer(true)
                    .setContentIntent(pIntent)
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
                    .setUsesChronometer(true)
                    .setContentIntent(pIntent)
                    .setOngoing(true);
            NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, b.build());
        }
    }

    public void startCronometro(){
        Minutos = 0;
        min = 0;
        Segundos = 0;
        seg = 0;
        timerhandler.postDelayed(timerRunnable,0);
    }

    public void stopCronometro(){
        timerhandler.removeCallbacks(timerRunnable);
    }

    final Runnable timerRunnable = new Runnable(){
        @Override
        public void run() {
            min = seg/60;
            if(min > 99)
                cronometro.setText(min+":"+String.format("%02d",seg%60));
            else
                cronometro.setText(String.format("%02d",min)+":"+String.format("%02d",seg%60));
            if (Segundos == 59){
                Minutos++;min++;
                if(Minutos != Intervalo)
                    Minutos = 0;
            }
            if(Minutos == Intervalo && Segundos == sIntervalo){
                reproducirSonido();
                System.out.println("Se reprodució el sonido en: " + String.format("%02d",min)+":"+String.format("%02d",seg%60));
                Segundos = 0;
            }
            Segundos++;seg++;
            timerhandler.postDelayed(timerRunnable,1000);
        }
    };

    public void reproducirSonido(){
        MediaPlayer sonido = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        sonido.start();
        sonido.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer sonido) {
                sonido.release();
            }});
    }
}