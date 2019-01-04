package com.esmifrase.duchita;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

public class Cronometro extends Service {
    private int Minutos = 0, contador = 0;
    private long TiempoInicial = 0L;
    private boolean reproducido = false;
    private Handler timerhandler = new Handler();
    private Intent intent;
    private Context context = this;
    public static String receiver = "recibir.accion";
    public MediaPlayer sonido;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(receiver);
        startCronometro(); // Empezar cronómetro
    }

    public void startCronometro() {
        Minutos = 0; // Inicializar minuto verificador de intervalo a 0
        contador = 0; // Inicializar contador a 0
        reproducido = false; // No se ha reproducido el sonido
        TiempoInicial = SystemClock.elapsedRealtime(); // Capturar tiempo del sistema
        timerhandler.postDelayed(timerRunnable,0); // Correr cronómetro en milisegundos.
    }

    public void stopCronometro(){
        timerhandler.removeCallbacks(timerRunnable); // Parar runnable del cronómetro.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCronometro(); // Parar cronómetro.
        Log.e("Servicio","Se detuvo el servicio");
    }

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
        Minutos = 0; // Se reinicia el minuto verificador de intervalos.
        Log.i("Se reprodució en", texto_cronometro + ":" +  String.format("%03d", miliseg%1000));
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
            if (seg == 59) { // Si los segundos llegan a 59.
                if (contador == 0) // Evita que se sumen varios minutos cuando seg es 59 en varios milisegundos.
                    Minutos++; // Sumar un minuto.
                contador++; // Capturar las veces que seg es igual a 59 en los milisegundos para evitar sumar muchos minutos.
            }
            else // Si no es 59, no sumas ningún minuto.
                contador = 0;
            if (Duchita.sIntervalo != 0 && Duchita.Intervalo == 0) { // Si el intervalo es menor a 00:59 y no es 00:00
                if (seg % Duchita.sIntervalo == 0 && !reproducido) { // Si los segundos son múltiplos del intervalo en segundos y no se ha reproducido el sonido.
                    if (min != 0 || seg != 0) // evita que se reproduzca el sonido cuando el cronómetro es 00:00.
                        reproducirSonido(texto_cronometro, miliseg); // Reproducir el sonido.
                }
                else if (seg % Duchita.sIntervalo != 0) // Si el segundo no es múltiplo del intervalo en segundos.
                    reproducido = false; // No se ha reproducido el sonido.
            }
            else {  // Si el intervalo es mayor a 1:00
                if (Minutos == Duchita.Intervalo && seg == Duchita.sIntervalo && !reproducido) // Si es el intervalo y no se ha reproducido.
                    reproducirSonido(texto_cronometro, miliseg); // Reproducir sonido.
                else if (seg != Duchita.sIntervalo) // Si los segundos no son igual a los segundos del intervalo.
                    reproducido = false; // no se ha reproducido
            }
            timerhandler.postDelayed(timerRunnable, 0);
        }
    };
}
