package com.example.alberto.pvideop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alberto.pvideop.ListaMenuVideos.DividerItemDecoration;
import com.example.alberto.pvideop.ListaMenuVideos.MaterialPaletteAdapter;
import com.example.alberto.pvideop.ListaMenuVideos.RecyclerViewOnItemClickListener;
import com.example.alberto.pvideop.ListaMenuVideos.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by alberto on 6/05/17.
 */
public class MenuVideos extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final static String _ID = MediaStore.Video.Media._ID;
    ListView lv;
    int count;
    private ArrayList<Video> videos;
    private Intent clve;
    String clave;
    RecyclerView recyclerView;

    // public final static String PATH = "/data/data/com.android.fileexplorerdemo.Reproductor/cache/";
//    public final static String PATH = "/storage/emulated/0/DCIM/.thumbnails/";
    public final static String PATH = "/storage/emulated/0/DCIM/";

    String[] titulo,ruta,tipo,fecha,tamano,duracion;
    Bitmap[] imagen;
    public final static int[] aux = {0};
    private final CharSequence[] opcionVideos ={"Información del vídeo", "Eliminar vídeo"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_videos);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lv = (ListView) findViewById(R.id.listView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        clve = getIntent();
        clave = clve.getStringExtra("clave");
        //toast(clave);
        /*
            CustomList adapter = null;
            try {
                adapter = new CustomList(MenuVideos.this, titulo, imagen,duracion);
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }

            lv = (ListView) findViewById(R.id.listView);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(MenuVideos.this);
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    // TODO Auto-generated method stub
                    dialogoOpcionesVideo(pos);

                   // toast("pos: " + pos);


                    return true;
                }
            });

    }*/

        datos();
        initVideos();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final Intent intent = new Intent(this, Reproductor.class);
        recyclerView.setAdapter(new MaterialPaletteAdapter(videos, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {

                //  Toast toast = Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT);
                //    int video = android.graphics.Color.parseColor(videos.get(position).getHex());
                //    toast.getView().setBackgroundColor(1);
                //    toast.show();
          //      pos = position;
                intent.putExtra(PATH, ruta[position]);
                intent.putExtra("clave",clave);
                startActivity(intent);
            }

            @Override
            public int[] onLongClick(View v, int position) {
               dialogoOpciones(v,position);
                return aux;
            }






        }));



        //VERTICAL
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //HORIZONTAL
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));



    }


    private void datos(){

        String[] projection = {
                MediaStore.Files.FileColumns._ID,//0
                MediaStore.Files.FileColumns.DATA,//1
                MediaStore.Files.FileColumns.DATE_ADDED,//2
                MediaStore.Files.FileColumns.MEDIA_TYPE,//3
                MediaStore.Files.FileColumns.MIME_TYPE,//4
                MediaStore.Files.FileColumns.TITLE,//5
                MediaStore.Video.VideoColumns.DATE_TAKEN, //6
                MediaStore.Files.FileColumns.SIZE,//7
                MediaStore.Video.VideoColumns.DURATION, //8


        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        Cursor cursor;
        cursor = managedQuery(queryUri,
                projection, // Which columns to return
                selection, //  MEDIA_DATA + " like ? ",       // WHERE clause; which rows to return (all rows)
                null, //  new String[] {"%sdcard%"},       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        count = cursor.getCount();

        int k = 0;
        titulo = new String[count];
        ruta = new String[count];
        tipo = new String[count];
        imagen = new Bitmap[count];
        fecha = new String[count];
        tamano = new String[count];
        duracion = new String[count];

        OutputStream outStream = null;
        String nombre;

        if (cursor.moveToFirst()) {
            do {
                titulo[k] = cursor.getString(5);
                nombre = titulo[k] + ".png";
                tipo[k] = cursor.getString(4);
                ruta[k] = cursor.getString(1);
                titulo[k] = titulo[k] + "." + tipo[k].substring(6);
                fecha[k] = cursor.getString(6);
                tamano[k] = cursor.getString(7);
                tamano[k] = size(Integer.parseInt(tamano[k]));
                duracion[k] = cursor.getString(8);
                duracion[k] = setCorrectDuration(duracion[k]);

                File file = new File(PATH, nombre);

                if (file.exists()) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imagen[k] = BitmapFactory.decodeStream(fis);
                } else {
                    try {
                        outStream = new FileOutputStream(file);
                        imagen[k] = ThumbnailUtils.createVideoThumbnail(ruta[k], MediaStore.Video.Thumbnails.MICRO_KIND);
                        imagen[k].compress(Bitmap.CompressFormat.PNG, 90, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (FileNotFoundException e) {
                        Log.d("", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d("", "Error accessing file: " + e.getMessage());
                    }
                }
                k++;
            } while (cursor.moveToNext());
        }
    }


    private void initVideos() {
        videos = new ArrayList<Video>();
        for(int i = 0; i<count;i++)
            videos.add(new Video(titulo[i],imagen[i],duracion[i]));
//            toast(titulo[i]);}

    }


    public void dialogoOpciones(View v, final int pos) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuVideos.this,R.style.MyDialogTheme);
        builder.setIcon(R.drawable.icono);


        //   LayoutInflater inflater = getLayoutInflater();
        //   builder.setView(inflater.inflate(R.layout.dialogo_opciones, null));
        builder.setTitle(getString(R.string.dilogo_titulo_video));
        builder.setItems(opcionVideos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item==0) {
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuVideos.this,R.style.MyDialogTheme);
                    builder.setIcon(R.drawable.icono);

                    //  LayoutInflater inflater = getLayoutInflater();
                    //  builder.setView(inflater.inflate(R.layout.dialogo_informacion, null));

                    builder.setTitle(getString(R.string.dialogo_titulo_información));

                    long  diaMesAño =  Long.parseLong(fecha[pos]);
                    Date date = new Date(diaMesAño);
                    SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
                    String d = dateFormat.format(date);

                   /* TextView  titles,loca,tam,fech,dura;
                    titles = (TextView) findViewById(R.id.titulo);
                    loca = (TextView) findViewById(R.id.localizacion);
                    tam = (TextView) findViewById(R.id.tam);
                    fech = (TextView) findViewById(R.id.fecha);
                    dura = (TextView) findViewById(R.id.duracion);
                    titles.setText("titulo");
                    loca.setText(""+ruta[pos]);
                    tam.setText(""+tamano[pos]);
                    fech.setText(d);
                    dura.setText(""+duracion[pos]);*/

                    builder.setMessage("Título : "+titulo[pos]+ "\nLocalización : "+ruta[pos]+"\nTamaño : "+tamano[pos]
                            +"\nFecha : "+d+ "\nDuración : "+duracion[pos]);

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(R.string.dialogo_aceptar_cambiar_clave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // positive button logic
                            // startActivity(intent);
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    // display dialog
                    dialog2.show();

                }
                if(item==1){
             //       toast(ruta[2]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuVideos.this,R.style.MyDialogTheme);
                    builder.setIcon(R.drawable.icono);
                    builder.setTitle(getString(R.string.dialogo_titulo_EliminarVideo));
                    builder.setMessage(getString(R.string.dialogo_mensaje_EliminarVideo));
                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(R.string.dialogo_aceptar_cambiar_clave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // positive button logic
                            //videos.remove(new Video(titulo[pos],imagen[pos],duracion[pos]));


                            //removeItem(new Video(titulo[pos],imagen[pos],duracion[pos]));
                            //Delete file in Internal Storage
                          //  deleteFile(ruta[pos]);

                       aux[0] = 1;


                        }
                    });

                    String negativeText = "Cancelar";
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // negative button logic
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog3 = builder.create();
                    // display dialog
                    dialog3.show();
                }

            }
        });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

    }


    public void removeItem(Video item){
        this.videos.remove(item);
    }

    private String setCorrectDuration(String songs_duration) {

        if(Integer.valueOf(songs_duration) != null){
            int time = Integer.valueOf(songs_duration);

            int seconds = time/1000;
            int minutes = seconds/60;
            seconds = seconds % 60;

            if(seconds<10){
                songs_duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            }else{

                songs_duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
            return songs_duration;
        }
        return null;


    }

    public String size(int size){
        String hrSize = "";
        int k = size;
        double m = size/1024;
        double g = size/1048576;
        double t = size/1073741824;

        DecimalFormat dec = new DecimalFormat("0");

        if (k>0)
        {

            hrSize = dec.format(k).concat("KB");

        }
        if (m>0)
        {

            hrSize = dec.format(m).concat("KB");
        }
        if (g>0)
        {

            hrSize = dec.format(g).concat("MB");
        }
        if (t>0)
        {

            hrSize = dec.format(t).concat("GB");
        }

        return hrSize;
    }



    public void toast(String  text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(MenuVideos.this, Reproductor.class);
        intent.putExtra(PATH, ruta[position]);
        intent.putExtra("clave",clave);
        ///  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MenuVideos.this,view,"simple_activity_transition");
        //startActivity(intent, options.toBundle());
        startActivity(intent);

    }
}