package com.esmifrase.duchita;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Cronometro extends Service {
    private long TiempoInicial = 0L;
    private boolean reproducido = false;
    private Handler timerhandler = new Handler();
    private Context context = this;
    public static final String receiver = "recibir.accion";
    public static final String START = "START";
    public static final String STOP = "STOP";
    public MediaPlayer sonido;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String accion = intent.getAction();
            switch (accion) {
                case START:
                    iniciarForegroundService(); // Iniciar servicio Foreground
                    break;
                case STOP:
                    detenerForegroundService(); // Detener servicio Foreground
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void startCronometro() {
        reproducido = false; // No se ha reproducido el sonido
        TiempoInicial = SystemClock.elapsedRealtime(); // Capturar tiempo del sistema
        timerhandler.postDelayed(timerRunnable,0); // Correr cronómetro en milisegundos.
    }

    public void stopCronometro(){
        timerhandler.removeCallbacks(timerRunnable); // Parar runnable del cronómetro.
    }

    final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long miliseg = SystemClock.elapsedRealtime() - TiempoInicial;
            long Buff = 0L; // Sin buff.
            long actualizarTiempo = Buff + miliseg;
            int seg = (int) (actualizarTiempo / 1000); // Milisegundos a segundos.
            int min = seg / 60; // Segundos a  minutos.
            seg %= 60; // Segundos en base 60.
            String texto_cronometro = String.format("%02d", min) + ":" + String.format("%02d", seg);
            enviarTexto(texto_cronometro); // Enviar valor del cronómetro al TextView cronometro en Duchita.java.
            if (Duchita.sIntervalo != 0 && Duchita.Intervalo == 0) { // Si el intervalo es menor a 00:59 y no es 00:00
                if (seg % Duchita.sIntervalo == 0 && !reproducido) { // Si los segundos son múltiplos del intervalo en segundos y no se ha reproducido el sonido.
                    if (min != 0 || seg != 0) // evita que se reproduzca el sonido cuando el cronómetro es 00:00.
                        reproducirSonido(texto_cronometro, miliseg); // Reproducir el sonido.
                }
                else if (seg % Duchita.sIntervalo != 0) // Si el segundo no es múltiplo del intervalo en segundos.
                    reproducido = false; // No se ha reproducido el sonido.
            }
            else {  // Si el intervalo es mayor a 1:00
                if (min % Duchita.Intervalo == 0 && seg == Duchita.sIntervalo && !reproducido){ // Si min es múltiplo de Intervalo (minutos) y seg es sIntervalo (segundos)
                    if (min != 0 || seg != 0)
                        reproducirSonido(texto_cronometro, miliseg); // Reproducir sonido.
                }
                else if (seg != Duchita.sIntervalo) // Si los segundos no son igual a los segundos del intervalo.
                    reproducido = false; // no se ha reproducido
            }
            timerhandler.postDelayed(timerRunnable, 0);
        }
    };

    private void enviarTexto(String texto_cronometro){
        intent.putExtra("tiempo", texto_cronometro); // Enviar tiempo del cronómetro a la actividad Duchita.java
        sendBroadcast(intent);
    }

    public void reproducirSonido(String texto_cronometro, long miliseg){
        sonido = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI); // Crear sonido de notificación.
        sonido.start(); // Reproducir sonido.
        sonido.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // Si se reproduce el sonido.
            public void onCompletion(MediaPlayer sonido) {
                sonido.release(); // Liberar sonido.
            }});
        reproducido = true; // Se ha reproducido.
        Log.i("Se reprodució en", texto_cronometro + ":" +  String.format("%03d", miliseg%1000));
    }

    private void iniciarForegroundService() {
        Log.i("Servicio","Se inició el servicio");
        startCronometro(); // Empezar cronómetro
        if(!Duchita.isActiveShampoo)
            mostrarNotificacion(getString(R.string.alarma), getString(R.string.alarma_normal), getString(R.string.alarma_modo_normal));
        else
            mostrarNotificacion(getString(R.string.alarma), getString(R.string.alarma_shampoo), getString(R.string.alarma_modo_shampoo));
    }

    private void detenerForegroundService(){
        Log.e("Servicio","Se detuvo el servicio");
        stopCronometro();
        cancelarNotificacion();
        stopForeground(true);
        stopSelf();
    }

    private void mostrarNotificacion(String titulo, String min, String modo) {
        Intent intent = new Intent(this, Duchita.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        String CHANNEL_ID = "com.esmifrase.duchita.cronometro";
        String channelName = "Cronometro";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // SOPORTE DE NOTIFICACIONES PARA API 21+
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.duchita_blanco)
                    .setContentTitle(titulo)
                    .setContentText(min)
                    .setTicker(modo)
                    .setChannelId(CHANNEL_ID)
                    .setUsesChronometer(true)
                    .setContentIntent(pIntent)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .build();
            startForeground(1, notification);
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
                    .setAutoCancel(false)
                    .setOngoing(true);
            Notification notification = b.build();
            startForeground(1, notification);
        }
    }

    private void cancelarNotificacion() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}
