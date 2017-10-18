package com.example.alberto.pvideop.ListaMenuVideos;

import android.view.View;

/**
 * Created by alberto on 4/07/17.
 */
public interface  RecyclerViewOnItemClickListener {

    void onClick(View v, int position);
    int[] onLongClick(View v, int position);

}