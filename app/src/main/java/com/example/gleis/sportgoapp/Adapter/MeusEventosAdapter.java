package com.example.gleis.sportgoapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Activity.ChatActivity;
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

import static java.security.AccessController.getContext;

/**
 * Created by gleis on 17/04/2018.
 */

public class MeusEventosAdapter extends RecyclerView.Adapter<MeusEventosAdapter.MyViewHolder> {

    private List<Evento> listaEventos;
    private LayoutInflater mlayoutInflater;
    public RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack;
    private Context mcontext;
    private DatabaseReference referenciaFirebase;
    private List<Evento> eventos;
    private Evento todosEventos;
    private TinyDB tinyDB;
    private List<Usuario> participantes;
    private Usuario usuario;
    private Evento itemEvento;

    private float scale;
    private int width;
    private int height;

    public MeusEventosAdapter(List<Evento> l, Context c) {

        mcontext = c;
        listaEventos = l;
        mlayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = mcontext.getResources().getDisplayMetrics().density;

        width = mcontext.getResources().getDisplayMetrics().widthPixels - (int) (14 * scale + 0.5f);
        height = (width / 16) * 9;
        tinyDB = new TinyDB(c);
    }

    @Override
    public MeusEventosAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // Este metodo constroi o layout item_evento
        View v = mlayoutInflater.inflate(R.layout.item_eventoparticipando_card, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MeusEventosAdapter.MyViewHolder holder, final int position) {
        itemEvento = listaEventos.get(position);

        eventos = new ArrayList<>();
        participantes = new ArrayList<>();
        referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").orderByChild("idEvento").equalTo(itemEvento.getIdEvento()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventos.clear();
                participantes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    todosEventos = postSnapshot.getValue(Evento.class);
                    Picasso.get().load(todosEventos.getImagemEvento()).resize(width, height).centerCrop().into(holder.imgEvento);
                    eventos.add(todosEventos);
                    // pega quantidade de participantes
                    for (DataSnapshot post : postSnapshot.child("participantes").getChildren()) {
                        usuario = post.getValue(Usuario.class);
                        participantes.add(usuario);
                    }
                }

                holder.tvQtdParticpantes.setText(participantes.size() + " ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.tvTitulo.setText(itemEvento.getTituloEvento());
        holder.tvDescricao.setText(itemEvento.getDescricaoEvento());
        holder.tvData.setText(itemEvento.getDataEvento());
        holder.tvMaxParticpantes.setText("/ " + String.valueOf(itemEvento.getQtdParticipante()));

    }

    @Override
    public int getItemCount() {

        return listaEventos.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        this.recyclerViewOnClickListenerHack = r;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imgEvento;
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
            tvTitulo = (TextView) itemView.findViewById(R.id.titulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.descricao);
            tvData = (TextView) itemView.findViewById(R.id.id_data);
            tvQtdParticpantes = (TextView) itemView.findViewById(R.id.id_qtd_participantes);
            tvMaxParticpantes = (TextView) itemView.findViewById(R.id.id_max_participantes);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tinyDB.putObject("evento", listaEventos.get(getAdapterPosition()));
            Intent it = new Intent(mcontext, ChatActivity.class);
            mcontext.startActivity(it);
            ((Activity) mcontext).finish();
        }

        @Override
        public boolean onLongClick(final View v) {
            // dialogo de escolha de opção
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("Escolha uma opção");
            // se o criador do evento e o que esta logado
            if (listaEventos.get(getAdapterPosition()).getUsuarioCriador().getEmail().equals(tinyDB.getObject("dadosUsuario", Usuario.class).getEmail())) {
                String[] animals = {"Ver Detalhes", "Ver Participantes", "Editar Evento", "Cancelar Evento",};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                verDetalhes(v);
                            case 1:
                                verParticipantes();
                            case 2:
                                editarEvento();
                            case 3:
                                cancelarEvento();
                        }
                    }
                });
            } else {
                String[] animals = {"Ver Detalhes", "Ver Participantes", "Sair do Evento"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                verDetalhes(v);
                            case 1:
                                verParticipantes();
                            case 2:
                                sairDoEvento();
                        }
                    }
                });
            }
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        private void sairDoEvento() {
            Toast.makeText(mcontext,"Saiu do Evento",Toast.LENGTH_LONG).show();
        }

        private void verParticipantes() {
            Toast.makeText(mcontext,"Abrir Participantes",Toast.LENGTH_LONG).show();

        }

        private void cancelarEvento() {
            Toast.makeText(mcontext,"Abrir Motivo",Toast.LENGTH_LONG).show();

        }

        private void editarEvento() {
            Toast.makeText(mcontext,"Abrir Edição do Evento",Toast.LENGTH_LONG).show();
        }

        private void verDetalhes(View v) {
            if (recyclerViewOnClickListenerHack != null) {
                recyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition());
            }
        }
    }


}
