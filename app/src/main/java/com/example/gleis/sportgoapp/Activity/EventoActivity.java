package com.example.gleis.sportgoapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.gleis.sportgoapp.Adapter.ParticipantesAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.ChatMessage;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventoActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;
    private ImageView imgEvento;
    private CircleImageView imgCriador;
    private TextView nomeCriador;
    private TextView local;
    private TextView data;
    private TextView hora;
    private TextView subtitle;
    private TextView qtdparticipantes;
    private TextView descricao;
    private FloatingActionButton btnParticipar;
    private MapView mapa;

    private RecyclerView recyclerViewParticipantes;
    private ParticipantesAdapter adapter;
    private DatabaseReference referenciaFirebase;
    private List<Usuario> participantes;

    private Toolbar mToolbar;
    private Evento evento;

    private Context mcontext;
    private TinyDB tinyDB;
    private Usuario usuario;
    private ArrayList<Usuario> listaUsuario = new ArrayList<>();
    private float scale;
    private int width;
    private int height;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_evento);
        associaVariaveis();

        mapa.onCreate(savedInstanceState);
        nestedScrollView.scrollTo(0,0);
        setSupportActionBar(mToolbar);
        // pega usuario logado
        usuario = tinyDB.getObject("dadosUsuario", Usuario.class);
        evento = tinyDB.getObject("evento",Evento.class);

        System.out.println(evento);
        // pega lista de usuario participantes
        listaParticipantes();
        //pegar tamanho da tela para width e height
        tamanhoTela();
        // colocando titulo do evento no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(evento.getTituloEvento());
        getSupportActionBar().setSubtitle("teste de per");

        Picasso.get().load(evento.getImagemEvento()).resize(width,height).centerCrop().into(imgEvento);
        Picasso.get().load(evento.getUsuarioCriador().getUrlImagem()).resize(width,height).centerCrop().into(imgCriador);

        nomeCriador.setText(evento.getUsuarioCriador().getNome());
        local.setText(evento.getEndereco());
        data.setText(evento.getDataEvento());
        hora.setText(evento.getHoraEvento());
        subtitle.setText(evento.getTipoEvento());
        // O 03 abaixo referencia a quanridade de participantes no evento !!! mudar depois
        qtdparticipantes.setText("03/"+evento.getQtdParticipante().toString());
        descricao.setText(evento.getDescricaoEvento());

        // mostra evento no mapa
        mapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latlngEvento = new LatLng(evento.getEnderecolat(),evento.getEnderecolng());
                googleMap.addMarker(new MarkerOptions().position(latlngEvento).title(evento.getEndereco()).draggable(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngEvento, 15));
            }
        });

        btnParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!jaParticipa()){
                    new AlertDialog.Builder(EventoActivity.this)
                            .setTitle("Entrar em Evento")
                            .setMessage("Você esta prestes a participar do evento "+evento.getTituloEvento()+" deseja continuar?")
                            .setPositiveButton("sim",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            participar();
                                            Intent it = new Intent(EventoActivity.this, ChatActivity.class);
                                            tinyDB.putObject("evento",evento);
                                            startActivity(it);
                                            finish();
                                        }
                                    })
                            .setNegativeButton("não", null)
                            .show();
                }else{
                    Intent it = new Intent(EventoActivity.this, ChatActivity.class);
                    tinyDB.putObject("evento",evento);
                    startActivity(it);
                }
            }
        });

    }

    // função responsavel por listar os participantes
    private void quantidadeParticipantes(Evento itemEvento) {

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapa.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapa.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapa.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapa.onPause();
    }


    // testa se o usuario ja participa do evento
    private boolean jaParticipa() {

        for (Usuario user : listaUsuario){
            System.out.println(" quant usuarios :" + user.getNome());
            if(user.getEmail().equals(usuario.getEmail())){
                return true;
            }
        }
        return false;
    }

    private void listaParticipantes() {

        FirebaseDatabase.getInstance() // aqui esta sendo usada a classe direta do firabase
                .getReference()
                .child("eventos")
                .child(evento.getIdEvento()).child("participantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Usuario user = postSnapshot.getValue(Usuario.class);
                    listaUsuario.add(user);
                    qtdparticipantes.setText(listaUsuario.size()+" / "+evento.getQtdParticipante());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        adapter = new ParticipantesAdapter(listaUsuario,evento,this);
        // adapter
        recyclerViewParticipantes.setAdapter(adapter);
    }
    private ArrayList<Usuario> retornaListaUsuario() {
        return listaUsuario;
    }

    // incluindo participante em evento
    private void participar() {

        FirebaseDatabase.getInstance() // aqui esta sendo usada a classe direta do firabase
                .getReference()
                .child("eventos")
                .child(evento.getIdEvento()).child("participantes").push().setValue(tinyDB.getObject("dadosUsuario", Usuario.class));

        // associando evento ao usuario
        FirebaseDatabase.getInstance() // aqui esta sendo usada a classe direta do firabase
                .getReference()
                .child("usuarios")
                .child(usuario.getId()).child("eventosAssociados").push().setValue(evento.getIdEvento());
    }

    private void tamanhoTela() {

        scale = mcontext.getResources().getDisplayMetrics().density;
        width = mcontext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;
    }


    private void alert(String s) {
        Toast.makeText(EventoActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    private void associaVariaveis() {
        this.nestedScrollView = (NestedScrollView) findViewById(R.id.nested);
        this.mapa = (MapView) findViewById(R.id.id_map_view_evento);
        this.mcontext = this;
        this.imgEvento = (ImageView) findViewById(R.id.img_evento);
        this.imgCriador = (CircleImageView) findViewById(R.id.img_usuarioCriador);

        this.nomeCriador = (TextView) findViewById(R.id.nome_criador);
        this.local = (TextView) findViewById(R.id.id_loc_evento);
        this.data = (TextView) findViewById(R.id.id_data);
        this.hora = (TextView) findViewById(R.id.id_hora);
        this.subtitle = (TextView) findViewById(R.id.id_subtitle);
        this.qtdparticipantes = (TextView ) findViewById(R.id.id_qtd_participantes);
        this.descricao = (TextView) findViewById(R.id.id_descricao);
        this.btnParticipar = (FloatingActionButton) findViewById(R.id.fb_participar);
        this.mToolbar = (Toolbar) findViewById(R.id.tb_evento);
        this.tinyDB = new TinyDB(EventoActivity.this);

        // lista de ususarios

        recyclerViewParticipantes = (RecyclerView) findViewById(R.id.id_recycler_view_paritcipantes);
        // otimizar recyclerview não mudar o tamanho.
        recyclerViewParticipantes.setHasFixedSize(true);
        //criando layout para o fragment
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(false);
        // setando layout no fragment
        recyclerViewParticipantes.setLayoutManager(llm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MenuActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finish();; //Método para matar a activity e não deixa-lá indexada na pilhagem
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("evento",evento);
    }
}
