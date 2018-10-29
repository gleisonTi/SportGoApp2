package com.example.gleis.sportgoapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Activity.ChatActivity;
import com.example.gleis.sportgoapp.Activity.CriarEventoActivity;
import com.example.gleis.sportgoapp.Activity.DadosUsuarios;
import com.example.gleis.sportgoapp.Activity.EventoActivity;
import com.example.gleis.sportgoapp.Activity.ImagemEventoActivity;
import com.example.gleis.sportgoapp.Activity.LocalMapaActivity;
import com.example.gleis.sportgoapp.Activity.ParticipantesActivity;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Interfaces.RecyclerViewOnClickListenerHack;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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


        if(itemEvento != null) {
            referenciaFirebase.child("eventos").orderByChild("idEvento").equalTo(itemEvento.getIdEvento()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventos.clear();
                    participantes.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        todosEventos = postSnapshot.getValue(Evento.class);
                        Picasso.get().load(todosEventos.getImagemEvento()).resize(width, height).centerCrop().into(holder.imgEvento);
                        Picasso.get().load(todosEventos.getUsuarioCriador().getUrlImagem()).resize(width, height).centerCrop().into(holder.imgCriador);
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
            if (itemEvento.getUsuarioCriador().getEmail().equals(tinyDB.getObject("dadosUsuario", Usuario.class).getEmail())) {
                holder.imgadm.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {

        return listaEventos.size();
    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        this.recyclerViewOnClickListenerHack = r;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public CircleImageView imgEvento;
        public TextView tvTitulo;
        public TextView tvDescricao;
        public TextView tvData;
        public TextView tvQtdParticpantes;
        public TextView tvMaxParticpantes;
        public CircleImageView imgCriador;
        public ImageView imgadm;


        public MyViewHolder(View itemView) {
            super(itemView);

            imgEvento = (CircleImageView) itemView.findViewById(R.id.img_evento);
            imgCriador = (CircleImageView) itemView.findViewById(R.id.img_perfil_usuario_criador);
            imgadm = (ImageView) itemView.findViewById(R.id.img_criador);

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
        }

        @Override
        public boolean onLongClick(final View v) {
            // dialogo de escolha de opção
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("Escolha uma opção");
            // se o criador do evento e o que esta logado
            if (listaEventos.get(getAdapterPosition()).getUsuarioCriador().getEmail().equals(tinyDB.getObject("dadosUsuario", Usuario.class).getEmail())) {
                //mostra marcador dos evento
                String[] animals = {"Ver Detalhes", "Ver Participantes", "Editar Evento", "Cancelar Evento",};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                verDetalhes(v);
                                break;
                            case 1:
                                verParticipantes();
                                break;
                            case 2:
                                editarEvento();
                                break;
                            case 3:
                                cancelarEvento();
                                break;
                            default:
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
                                break;
                            case 1:
                                verParticipantes();
                                break;
                            case 2:
                                final String nomeEvento = listaEventos.get(getAdapterPosition()).getTituloEvento();
                                new AlertDialog.Builder(mcontext)
                                        .setTitle(nomeEvento)
                                        .setMessage("Você esta prestes a sair deste evento deseja continuar?")
                                        .setPositiveButton("sim",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        sairDoEvento();
                                                        Toast.makeText(mcontext,"Você saiu do evento "+nomeEvento,Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                        .setNegativeButton("não", null)
                                        .show();
                                break;
                        }
                    }
                });
            }
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        // metodo disparado para sair de um evento
        private void sairDoEvento() {
            // aqui o evento esta sendo removido da lista de eventos do usuario
            FirebaseDatabase.getInstance().getReference().child("usuarios")
                    .child(tinyDB.getObject("dadosUsuario", Usuario.class).getId())
                    .child("eventosAssociados")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                String idEvento = child.getValue(String.class);// recupera id do evento
                                if(listaEventos.get(getAdapterPosition()).getIdEvento().equals(idEvento)){
                                    child.getRef().removeValue();
                                }
                            }

                            listaEventos.remove(getAdapterPosition());
                            Toast.makeText(mcontext,String.valueOf(getAdapterPosition()),Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            // aqui o usuario esta sendo removido da lista de participantes e tambem o evendo esta sendo removido de sua lista
            FirebaseDatabase.getInstance().getReference().child("eventos")
                    .child(listaEventos.get(getAdapterPosition()).getIdEvento())
                    .child("participantes")
                    .orderByChild("email")
                    .equalTo(tinyDB.getObject("dadosUsuario", Usuario.class).getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            child.getRef().removeValue();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(getItemCount() <= 1){
                recyclerViewOnClickListenerHack.onClickListener(null, 0,true);
            }

        }

        private void verParticipantes() {

            tinyDB.putObject("evento",listaEventos.get(getAdapterPosition()));
            Intent it = new Intent(mcontext, ParticipantesActivity.class);
            mcontext.startActivity(it);
            
        }

        private void cancelarEvento() {


            View dialogView = mlayoutInflater.inflate(R.layout.layout_alert_message, null);
            final AlertDialog.Builder editDialog = new AlertDialog.Builder(mcontext);// cria alert com o motivo
            editDialog.setView(dialogView);
            editDialog.setTitle("Motivo de Cancelamento");
            editDialog.setMessage("Para cancelar um evento e necessário uma menssagem para os usuários participantes ");
            editDialog.setPositiveButton("Enviar Menssagem",null);

            editDialog.setNegativeButton("Cancelar",null);

            final EditText editTextMotivo = dialogView.findViewById(R.id.id_menssagem_evento);

            final AlertDialog dialogEdit = editDialog.create();

            dialogEdit.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!editTextMotivo.getText().toString().isEmpty()) {
                                if(!(editTextMotivo.getText().toString().length() < 40)){
                                    setMotivoCancelamento(editTextMotivo.getText().toString());
                                    dialogEdit.dismiss();
                                }else{
                                    Toast.makeText(mcontext,"Digite um Justificativa maior que 40 caracteres",Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(mcontext,"Uma menssagem de feedback para os participantes e obrigatoria para o cancelamento",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("Cancelar Evento")
            .setMessage("Deseja realmente cancelar este evento?")
            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogEdit.show();

                }
            }).setNegativeButton("Não",null).create().show();

        }

        private void editarEvento() {
            // flag necessaria para definir que o evento sera editado
            tinyDB.putBoolean("flagDeEdicao",true);
            // evento que sera editado
            tinyDB.putObject("eventoEdit",listaEventos.get(getAdapterPosition()));
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("Escolha uma opção que deseja editar");
            String[] animals = {"Detalhes do evento", "Local do evento", "Imagem do evento"};
            builder.setItems(animals, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            editDetalhesEvento();
                            break;
                        case 1:
                            editLocalEvento();
                            break;
                        case 2:
                            editImagem();
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void verDetalhes(View v) {
            if (recyclerViewOnClickListenerHack != null) {
                recyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition(),false);
            }
        }
        // cancela e seta motivo no evento
        private void setMotivoCancelamento(String motivoCancelamento) {

            Map<String,Object> taskMap = new HashMap<String,Object>();
            // add hash map nesse formato
            taskMap.put("tipo","Cancelado");
            taskMap.put("motivoDescricao",motivoCancelamento);
            listaEventos.get(getAdapterPosition()).cancelaFirebaseEvento(taskMap);// chama o metodo para cancelar o evento

            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("Evento cancelado")
                    .setMessage("Deseja remover este evento de sua lista de eventos?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sairDoEvento();
                        }
                    }).setNegativeButton("Não",null).create().show();

        }
    }

    // funçoes de edição do evento
    private void editDetalhesEvento() {
        Intent it = new Intent(mcontext, CriarEventoActivity.class);
        mcontext.startActivity(it);
        ((Activity)mcontext).finish();
    }

    private void editLocalEvento() {
        Intent it = new Intent(mcontext, LocalMapaActivity.class);
        mcontext.startActivity(it);
        ((Activity)mcontext).finish();// finaliza activity pelo adapter

    }
    // funções de edição do  evento
    private void editImagem() {
        Intent it = new Intent(mcontext, ImagemEventoActivity.class);
        mcontext.startActivity(it);
        ((Activity)mcontext).finish();
    }



}
