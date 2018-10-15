package com.example.gleis.sportgoapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;


import com.example.gleis.sportgoapp.Adapter.EventosAdapter;
import com.example.gleis.sportgoapp.Adapter.ParticipantesAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ParticipantesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerViewParticipantes;
    private ParticipantesAdapter adapter;

    private LinearLayout lnSemEvento;
    private LinearLayout lncarregando;
    private DatabaseReference referenciaFirebase;
    private List<Evento> eventos;
    private Evento todosEventos;
    private List<Usuario> participantes;
    private Usuario usuario;
    private TinyDB tinyDB;

    private DatabaseReference referenceFirebase;

    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantes);
        initViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        evento = tinyDB.getObject("evento",Evento.class);
        getSupportActionBar().setTitle(evento.getTituloEvento());
        getSupportActionBar().setSubtitle("Participantes");

        participantes = new ArrayList<>();
        referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").child(this.evento.getIdEvento()).child("participantes").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    usuario = post.getValue(Usuario.class);
                    participantes.add(usuario);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ParticipantesAdapter(participantes,evento,this);
        // adapter
        recyclerViewParticipantes.setAdapter(adapter);
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.tb_participantes);
        recyclerViewParticipantes = (RecyclerView) findViewById(R.id.id_recycler_view_paritcipantes);
        // otimizar recyclerview não mudar o tamanho.
        recyclerViewParticipantes.setHasFixedSize(true);
        //criando layout para o fragment
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(false);
        // setando layout no fragment
        recyclerViewParticipantes.setLayoutManager(llm);

        tinyDB = new TinyDB(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                onBackPressed();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
