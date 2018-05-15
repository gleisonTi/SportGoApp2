package com.example.gleis.sportgoapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Interfaces.RecyclerViewOnClickListenerHack;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleis on 17/04/2018.
 */

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.MyViewHolder> {

    private List<Evento> listaEventos;
    private LayoutInflater mlayoutInflater;
    public RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack;
    private Context mcontext;
    private DatabaseReference referenciaFirebase;
    private List<Evento> eventos;
    private Evento todosEventos;

    private float scale;
    private int width;
    private int height;

    public EventosAdapter(List<Evento> l, Context c ){

        mcontext = c;
        listaEventos = l;
        mlayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = mcontext.getResources().getDisplayMetrics().density;

        width = mcontext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;
    }

    @Override
    public EventosAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // Este metodo constroi o layout item_evento
        View v = mlayoutInflater.inflate(R.layout.item_evento_card, viewGroup,false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final EventosAdapter.MyViewHolder holder, final int position) {
        final Evento itemEvento =  listaEventos.get(position);

        eventos = new ArrayList<>();

        referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").orderByChild("idEvento").equalTo(itemEvento.getIdEvento()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventos.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todosEventos = postSnapshot.getValue(Evento.class);
                    Picasso.get().load(todosEventos.getImagemEvento()).resize(width,height).centerCrop().into(holder.imgEvento);
                    eventos.add(todosEventos);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.tvTitulo.setText(itemEvento.getTituloEvento());
        holder.tvDescricao.setText(itemEvento.getDescricaoEvento());

    }

    @Override
    public int getItemCount() {

        return listaEventos.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        this.recyclerViewOnClickListenerHack = r;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imgEvento;
        public TextView tvTitulo;
        public TextView tvDescricao;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgEvento = (ImageView) itemView.findViewById(R.id.img_evento);
            tvTitulo = (TextView) itemView.findViewById(R.id.titulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.descricao);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnClickListenerHack != null){
                recyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }
    }
}
