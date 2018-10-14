package com.example.gleis.sportgoapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ParticipantesAdapter extends RecyclerView.Adapter<ParticipantesAdapter.MyViewHolder>  {

    private LayoutInflater mlayoutInflater;
    private Context mcontext;
    private DatabaseReference referenciaFirebase;
    private List<Usuario> participantes;
    private Usuario usuario;
    private Evento evento;
    private TinyDB tinyDB;

    public ParticipantesAdapter(List<Usuario> participantes,Evento evento, Context c ){

        mlayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.participantes = participantes;
        this.mcontext = c;
        this.tinyDB = new TinyDB(c);
        this.evento = evento;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Este metodo constroi o layout item_evento
        View v = mlayoutInflater.inflate(R.layout.item_participantes, viewGroup,false);
        ParticipantesAdapter.MyViewHolder mvh = new ParticipantesAdapter.MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        usuario = participantes.get(position);
        Picasso.get().load(usuario.getUrlImagem()).into(holder.imgUsuario);
        holder.tvnomeUsuario.setText(usuario.getNome());
        holder.tvDetalhes.setText("idade: "+ usuario.getIdade()+" anos" +
                                  " | "+"Cidade: "+usuario.getCidade()+" - "+usuario.getEstado()+
                                  " | "+"Sexo:"+usuario.getSexo()+
                                  " | "+"Espore Favorito:"+usuario.getEsporte());
        if(usuario.getEmail().equals(evento.getUsuarioCriador().getEmail())){
            holder.imgadm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return participantes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imgUsuario , imgadm;
        public TextView tvnomeUsuario;
        public TextView tvDetalhes;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgadm = (ImageView) itemView.findViewById(R.id.img_criador);
            imgUsuario = (ImageView) itemView.findViewById(R.id.img_User);
            tvnomeUsuario = (TextView) itemView.findViewById(R.id.nome_usuario);
            tvDetalhes = (TextView) itemView.findViewById(R.id.descricao);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

    }
}
