package com.example.alberto.pvideop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class MainActivity extends AppCompatActivity {

    //   private Button videos;
    private EditText clave, confirmacion,ip;
    private TextView verVideos;
    private Button validar,cambiarClave,olvidarClave;
    private FloatingActionButton menu;
  //  private Button menu;

    private File f,f1;
    static private String FILENAME = "clave.txt";
    private FileInputStream fis;
    private FileOutputStream fos;
    private InputStreamReader isr;
    private BufferedReader bufferedReader;
    private StringBuilder sb;
    private String[] parts;
    private String line,part1, part2;
    final Context c = this;

    String clave_actual,clave_nueva;

    private boolean permisoR=false;
    private boolean permisoW=false;
    private boolean dialogoInfo = false;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.caratula);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                setContentView(R.layout.activity_main);
            //    setContentView(R.layout.main_feo);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                //Componentes del main Activity
                clave = (EditText) findViewById(R.id.contrasena);
                confirmacion = (EditText) findViewById(R.id.contrasenaRepetida);
                ip =(EditText) findViewById(R.id.ip);
                validar = (Button) findViewById(R.id.valida);
                cambiarClave = (Button) findViewById(R.id.cambiarClave);
                olvidarClave = (Button) findViewById(R.id.olvidaClave);
                menu = (FloatingActionButton) findViewById(R.id.menuVideo);
               // menu = (Button) findViewById(R.id.menuVideo);
                verVideos = (TextView) findViewById(R.id.textView3);
                menu.setEnabled(false);
                validar.setEnabled(true);
                menu.setVisibility(View.INVISIBLE);

                //orientación
              //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
               // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //se crea un archivo
                f = getFileStreamPath(FILENAME); //existe el archivp
                if (f.length() != 0) { //contiene la clave
                    sb = Read(f, FILENAME);
                    //se obtiene la contraseña del formato ***,***
                    parts = sb.toString().split(",");
                    part1 = parts[0]; // contraseña
                    part2 = parts[1]; // confirmación
                    //se desabilita los TexEdit y se anexa la onfirmación
                    cambiarClave.setVisibility(View.VISIBLE);
                    olvidarClave.setVisibility(View.VISIBLE);
                    clave.setText(part1);
                    confirmacion.setText(part2);
                    clave.setEnabled(false);
                    clave.setVisibility(View.INVISIBLE);
                    confirmacion.setVisibility(View.INVISIBLE);
                    confirmacion.setEnabled(false);
                    menu.setEnabled(true);
                    validar.setEnabled(false);
                    validar.setVisibility(View.GONE);
                    menu.setVisibility(View.VISIBLE);
                    verVideos.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i=0; i<menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            String title = mi.getTitle().toString();
            Spannable newTitle = new SpannableString(title);
            newTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#222f75")), 0, newTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(newTitle);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);


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
            dialogoSegridad();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialogoSegridad(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle("Solicitar Clave para acceder");
        builder.setIcon(R.drawable.icono);

        View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_inapropiado, null);
        final EditText respuesta = (EditText) viewInflated.findViewById(R.id.respuesta);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  resp = respuesta.getText().toString();
                //  toast(clave.getText().toString()+" "+resp);

                if(resp.equals(clave.getText().toString()))
                {
                    dialogoGeneros();

                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }



    void dialogoGeneros(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Géneros");
        SharedPreferences settings = getSharedPreferences("mysettings", 0);
        final SharedPreferences.Editor editor = settings.edit();

        View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_generos, null);
        final CheckBox caricatura = (CheckBox) viewInflated.findViewById(R.id.Caricatura);
        final CheckBox documental = (CheckBox) viewInflated.findViewById(R.id.Documental);
        final CheckBox futbol = (CheckBox) viewInflated.findViewById(R.id.Futbol);
        final CheckBox guerra = (CheckBox) viewInflated.findViewById(R.id.Guerra);
        final CheckBox noticia = (CheckBox) viewInflated.findViewById(R.id.Noticia);
        final CheckBox novela = (CheckBox) viewInflated.findViewById(R.id.Novela);

        caricatura.setChecked(settings.getBoolean("caricatura",false));
        documental.setChecked(settings.getBoolean("documental",false));
        futbol.setChecked(settings.getBoolean("futbol",false));
        guerra.setChecked(settings.getBoolean("guerra",false));
        noticia.setChecked(settings.getBoolean("noticia",false));
        novela.setChecked(settings.getBoolean("novela",false));

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i("res",caricatura.isChecked()+" "+documental.isChecked()+" "+futbol.isChecked()+" "+guerra.isChecked()+" "+noticia.isChecked()+" "+novela.isChecked());
                if(caricatura.isChecked()) {
                    editor.putBoolean("caricatura", true);
                    editor.commit();
                    caricatura.setChecked(true);
                }
                else {
                    editor.putBoolean("caricatura", false);
                    editor.commit();
                    caricatura.setChecked(false);

                }
                if(documental.isChecked()) {
                    editor.putBoolean("documental", true);
                    editor.commit();
                    documental.setChecked(true);
                }
                else {
                    editor.putBoolean("documental", false);
                    editor.commit();
                    documental.setChecked(false);
                }
                if(futbol.isChecked()) {
                    editor.putBoolean("futbol", true);
                    editor.commit();
                    futbol.setChecked(true);
                }
                else {
                    editor.putBoolean("futbol", false);
                    editor.commit();
                    futbol.setChecked(false);
                }
                if(guerra.isChecked()) {
                    editor.putBoolean("guerra", true);
                    editor.commit();
                    guerra.setChecked(true);
                }
                else {
                    editor.putBoolean("guerra", false);
                    editor.commit();
                    guerra.setChecked(false);
                }
                if(noticia.isChecked()) {
                    editor.putBoolean("noticia", true);
                    editor.commit();
                    noticia.setChecked(true);
                }
                else {
                    editor.putBoolean("noticia", false);
                    editor.commit();
                    noticia.setChecked(false);
                }
                if(novela.isChecked()) {
                    editor.putBoolean("novela", true);
                    editor.commit();
                    novela.setChecked(true);
                }
                else {
                    editor.putBoolean("novela", false);
                    editor.commit();
                    novela.setChecked(false);
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //metodo para mandar la inforación al activity
    public void Valida(View view) throws IOException {

        requestStoragePermission();
        String text = clave.getText().toString();
        String text2 = confirmacion.getText().toString();

        if (((text.length() < 4) || (text.length() > 4)) &&( (text2.length() < 4) || (text2.length() > 4))) { //validación
            clave.setError("La contraseña es demasiado corta.");
            confirmacion.setError("La contraseña es demasiado corta.");
        } else {
            Log.e("reusltado ",text +"  "+text2);
            if (text2.equals(text)) { //se cumple validación
                //se guarda contraseña en un archivo.
                f1 = getFileStreamPath(FILENAME);
                if (f1.length() == 0) { // existe go en el archivo
                    Write(FILENAME,text,text2);
                } else {
                    sb =Read(f1,FILENAME);
                    parts = sb.toString().split(",");
                    part1 = parts[0];
                    part2 = parts[1];
                    clave.setText(part1);
                    clave.setText(part2);
                }

                confirmacion.setEnabled(false);
                confirmacion.setBackgroundColor(0);
                clave.setEnabled(false);
                clave.setBackgroundColor(0);
                menu.setEnabled(true);
                validar.setEnabled(false);
                validar.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                verVideos.setVisibility(View.VISIBLE);
            } else {
                clave.setError("Las contraseñas deben ser iguales.");
                confirmacion.setError("Las contraseñas deben ser iguales.");
                toast("Las contraseñas deben ser iguales.","#D85F3E");
            }
        }

}


    public void irMenu(View view){
        requestStoragePermission();
      //  if(permisoR) {
            //llama Menu,java
            SharedPreferences settings = getSharedPreferences("ip", 0);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString("direccion",ip.getText().toString());
            editor.commit();
            sb =Read(f1, FILENAME);
            parts = sb.toString().split(",");
            part1 = parts[0];
            part2 = parts[1];
             SharedPreferences clave =  getSharedPreferences("clave", 0);
            final SharedPreferences.Editor id = clave.edit();
            id.putString("clave",part1.toString());
            id.commit();
            Intent intent = new Intent(MainActivity.this, MenuVideos.class);
            intent.putExtra("clave", part1);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, view, "simple_activity_transition");
            startActivity(intent, options.toBundle());
        //}
    }

    public StringBuilder Read(File file,String fileName) {
        try {
            fis = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //se obtiene la información
        isr = new InputStreamReader(fis);
        bufferedReader = new BufferedReader(isr);
        sb = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line); // se le qu tiee el archivo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    //metodo que escribe en el archivo
    public void Write(String fileName, String text, String text2) throws IOException {
        String claves = text + "," + text2;
        fos = openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(claves.getBytes());
        fos.close();

    }

    public void cambiar(View view){
        if(!dialogoInfo) {
            dialogoInformacion();
            dialogoInfo = true;
        }
        else
            dialogoCambioClave();
        //  toast("cambiar clave");
    }

    public void olvidar(View view){
            dialogoOlvidarClave();
    }

    private void dialogoInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setIcon(R.drawable.icono);

        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogo_cambiar_clave, null));


        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(R.string.dialogo_aceptar_cambiar_clave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogoCambioClave();
            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void dialogoOlvidarClave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setIcon(R.drawable.icono);
        View viewInflated = getLayoutInflater().inflate(R.layout.dilogo_olvidar_clave, null);
        final TextView claveRecuperar = (TextView) viewInflated.findViewById(R.id.claveReuperar);
        builder.setView(viewInflated);
        sb =Read(f1, FILENAME);
        parts = sb.toString().split(",");
        part1 = parts[0];
        part2 = parts[1];
        claveRecuperar.setText(claveRecuperar.getText()+part1.substring(0,1));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(R.string.dialogo_aceptar_cambiar_clave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    void dialogoCambioClave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Cambiar Clave");
        builder.setIcon(R.drawable.icono);

        View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_opciones, null);
        final EditText claveActual = (EditText) viewInflated.findViewById(R.id.claveActual);
        final EditText claveNueva = (EditText) viewInflated.findViewById(R.id.claveNueval);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clave_actual = claveActual.getText().toString();
                clave_nueva = claveNueva.getText().toString();
                Log.i("totos",clave_actual+" "+clave_nueva);
                if(clave_actual.equals(part1))
                {
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    writer.print("");
                    writer.close();
                    f = getFileStreamPath(FILENAME);
                    try {
                        Write(FILENAME,clave_nueva,clave_nueva);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    toast("Clave nueva: "+" " +Read(f,FILENAME).toString().substring(0,4),"#222f75");

                }
                else{
                    toast("Clave actual incorrecta","#D85F3E");
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //alertas

    public void toast(String  text,String color){
        Toast toast =  Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        int video = android.graphics.Color.parseColor(color);
        toast.getView().setBackgroundColor(video);
        toast.show();
    }
    public void toast(String  text){
        Toast toast =  Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    //indicar los permisos necesarios y en caso de pedir su habilitación

    public void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permisoW = true;
                }else{
                    permisoW = false;
                }
                return;
            } case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permisoR = true;

                }else{
                    permisoR = false;
                }
                Log.i("repsuessta", "" + permisoR);
                return;
            }
        }
    }
}