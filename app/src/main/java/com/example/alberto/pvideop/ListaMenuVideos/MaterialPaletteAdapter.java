package com.example.alberto.pvideop.ListaMenuVideos;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alberto.pvideop.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.alberto.pvideop.MenuVideos.aux;


/**
 * Created by alberto on 4/07/17.
 */
public class MaterialPaletteAdapter extends RecyclerView.Adapter<MaterialPaletteAdapter.PaletteViewHolder> {
    private ArrayList<Video> data;
    public static int pos;

    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    private int adapterPosition;

    public MaterialPaletteAdapter(@NonNull ArrayList<Video> data,
                                  @NonNull RecyclerViewOnItemClickListener
                                          recyclerViewOnItemClickListener) {
        this.data = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public PaletteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //    View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feo, parent, false);
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new PaletteViewHolder(row);
    }

    public void eliminar (int position){
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(PaletteViewHolder holder, int position) {
        Video video = data.get(position);
        holder.circleView.setImageBitmap(video.getImagen());
        holder.getTitleTextView().setText(video.getName());
        holder.getSubtitleTextView().setText(video.getRuta());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }


    class PaletteViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener, View.OnLongClickListener {
        private ImageView circleView;
        private TextView titleTextView;
        private TextView subtitleTextView;
        private ImageButton opciones;



        public PaletteViewHolder(View itemView) {
            super(itemView);

            circleView = (ImageView)itemView.findViewById(R.id.img);
            titleTextView = (TextView) itemView.findViewById(R.id.txt);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitulo);
           // opciones = (ImageButton) itemView.findViewById(R.id.opcion);
            //     circleView.setImageBitmap();

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }





        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getSubtitleTextView() {
            return subtitleTextView;
        }

        public View getCircleView() {
            return circleView;
        }



        @Override
        public void onClick(View v) {
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }
        public boolean onLongClick(View v) {
            int[] i = recyclerViewOnItemClickListener.onLongClick(v, getAdapterPosition());

            if(i[0]==1) {
                notifyItemRemoved(getAdapterPosition());
                data.remove(getPosition());
                notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                aux[0]=0;
            }
            return true;
        }



        public void i(){
            Log.e("error", "ero");
        }
    }

}
