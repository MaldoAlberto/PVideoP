package com.example.alberto.pvideop.ListaMenuVideos;


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


/**
 * Created by alberto on 6/05/17.
 */


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alberto.pvideop.R;

import java.io.IOException;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] titulo,subtitulo;
    private final Bitmap[] imageId;
    ViewHolder viewHolder = new ViewHolder();

    //constructor de customList
    public CustomList(Activity context, String[] titulo, Bitmap[] imageId, String[] subtitulo) throws IOException {
        super(context, R.layout.video_item, titulo);
        this.context = context;
        this.titulo = titulo;
        this.imageId = imageId;
        this.subtitulo = subtitulo;
    }

    //Metodo para obtener los item de la listview
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater(); //se obtiene el contexto

        if(view == null) {//se valida el view
            view = inflater.inflate(R.layout.video_item, null); //se obtiene los valores del metodo ViewHolder
            viewHolder.text =(TextView) view.findViewById(R.id.txt);
            viewHolder.imagen = (ImageView) view.findViewById(R.id.img);
            viewHolder.subtitulo = (TextView) view.findViewById(R.id.subtitulo);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        //se mandan los valores del item
        viewHolder.imagen.setImageBitmap(imageId[position]);
        viewHolder.text.setText(titulo[position]);
        viewHolder.subtitulo.setText("Duración : "+subtitulo[position]);
        return view;
    }
}