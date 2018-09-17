package com.example.gleis.sportgoapp.Adapter;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Interfaces.RecyclerViewOnClickListenerHack;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.maps.model.LatLng;
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
    private List<Usuario> participantes;
    private Usuario usuario;
    private TinyDB tinyDB;

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
        tinyDB = new TinyDB(c);
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
        participantes = new ArrayList<>();

        referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").orderByChild("idEvento").equalTo(itemEvento.getIdEvento()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventos.clear();
                participantes.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todosEventos = postSnapshot.getValue(Evento.class);
                    Picasso.get().load(todosEventos.getImagemEvento()).resize(width,height).centerCrop().into(holder.imgEvento);
                    Picasso.get().load(todosEventos.getUsuarioCriador().getUrlImagem()).resize(width,height).centerCrop().into(holder.imgUsuario);
                    eventos.add(todosEventos);

                    // pega quantidade de participantes
                    for(DataSnapshot post : postSnapshot.child("participantes").getChildren()){
                        usuario = post.getValue(Usuario.class);
                        participantes.add(usuario);
                    }
                }

                holder.tvQtdParticpantes.setText(participantes.size()+" ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.tvTitulo.setText(itemEvento.getTituloEvento());
        holder.tvDescricao.setText(itemEvento.getDescricaoEvento());
        holder.tvData.setText(itemEvento.getDataEvento());
        holder.tvMaxParticpantes.setText("/ "+String.valueOf(itemEvento.getQtdParticipante()));
        holder.tvDistancia.setText( getdistancia(new LatLng(itemEvento.getEnderecolat(),itemEvento.getEnderecolng())));
        holder.tvnomeUsuario.setText(itemEvento.getUsuarioCriador().getNome());

    }

    private List<Usuario> quantidadeParticipantes(Evento itemEvento) {

        participantes = new ArrayList<>();
        referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").child(itemEvento.getIdEvento()).child("participantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    usuario = postSnapshot.getValue(Usuario.class);
                    participantes.add(usuario);
                }

                System.out.println("quant Usuarios 1: "+participantes.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        System.out.println("quant Usuarios 2: "+participantes.size());
        return participantes;
    }

    private String getdistancia(LatLng latLng) {
        // loc do usuario local
        LatLng posicaoInicial = tinyDB.getObject("latlngAtual",LatLng.class);
        // loc final
        LatLng posicaiFinal = latLng;
    // posição inicial
        final Location start = new Location("start point");
        start.setLatitude(posicaoInicial.latitude);
        start.setLongitude(posicaoInicial.longitude);
     // posição final
        final Location finish = new Location("Finish Point");
        finish.setLatitude(posicaiFinal.latitude);
        finish.setLongitude(posicaiFinal.longitude);

        final float distance = start.distanceTo(finish);

        Log.i("LOG","A Distancia é = "+formatNumber(distance));
        // retorna o km editado
        return formatNumber(distance);
    }

    private String formatNumber(float distance) {
        String unit = "m";
        if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.1f%s", distance, unit);
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
        public ImageView imgUsuario;
        public TextView tvnomeUsuario;
        public TextView tvTitulo;
        public TextView tvDescricao;
        public TextView tvData;
        public TextView tvQtdParticpantes;
        public TextView tvMaxParticpantes;
        public TextView tvDistancia;
        public Button btnDetalhes;
        public Button btnParticipar;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgEvento = (ImageView) itemView.findViewById(R.id.img_evento);
            imgUsuario = (ImageView) itemView.findViewById(R.id.img_perfil_usuario);
            tvnomeUsuario = (TextView) itemView.findViewById(R.id.id_nomeUsuario);
            tvTitulo = (TextView) itemView.findViewById(R.id.titulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.descricao);
            tvData = (TextView) itemView.findViewById(R.id.id_data);
            tvQtdParticpantes = (TextView) itemView.findViewById(R.id.id_qtd_participantes);
            tvMaxParticpantes = (TextView) itemView.findViewById(R.id.id_max_participantes);
            tvDistancia = (TextView) itemView.findViewById(R.id.id_distancia);
            btnDetalhes = (Button) itemView.findViewById(R.id.id_detalhes);
            btnParticipar = (Button) itemView.findViewById(R.id.id_participar);

            itemView.setOnClickListener(this);
            btnDetalhes.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnClickListenerHack != null){
                recyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition());
            }
        }


    }
}
