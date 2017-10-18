package com.example.alberto.pvideop.ImagenVideo;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class MiIntentService extends IntentService {
    public static final String ACTION_MyIntentService = "com.example.alberto.vides.RESPONSE";
    public static final String GENERO = "genero",TIME = "tiempo";
    private int respuesta, tiempo; // falso representa  que no es  inapropiado, verdadore es inapropiado
    private boolean existe = false;
    private String datos = "", parts[];

    public MiIntentService() {
        super("MiIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
      //  tareaLarga();
        SocketCliente sc = new SocketCliente();
        //respuesta = sc.ex();
        SharedPreferences settings = getSharedPreferences("ip", 0);
        final SharedPreferences.Editor editor = settings.edit();
        String aux =settings.getString("direccion","ip");
        Log.e("error",aux);
        datos = sc.ejecutaCliente(aux);
        parts = datos.split("-");
        respuesta = Integer.valueOf(parts[0]);
        tiempo = Integer.valueOf(parts[1]);
         tareaLarga(tiempo);



      //  tareaLarga();
    /*
        if (!respuesta) { // si no es inapropiado   se obtenga una imagen
            existe = true;
            i++;
        }
        else {// si es inapropiado que se incremnete u otados
            existe = false;
           // i++;
        }*/
        Log.e("advertencia", ""+respuesta);

        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(GENERO, respuesta);
        intentResponse.putExtra(TIME, tiempo);
/*
        if (i == 5) {
            Log.e("advertencia", "video inapropiado");
        }*/
        sendBroadcast(intentResponse);
      //  Log.d("estado", "" + respuesta + " numero de inapropiados " + i);
//        tareaLarga();


    }

    private void tareaLarga(int time) {
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
        }
    }
}