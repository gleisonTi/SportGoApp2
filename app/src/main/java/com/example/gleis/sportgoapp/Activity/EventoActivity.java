package com.example.gleis.sportgoapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.gleis.sportgoapp.Entidades.ChatMessage;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventoActivity extends AppCompatActivity {

    private ImageView imgEvento;
    private TextView titulo;
    private TextView local;
    private TextView data;
    private TextView hora;
    private TextView qtdparticipantes;
    private TextView descricao;
    private Button btnParticipar;
    private Button btnVoltar;

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
        setContentView(R.layout.activity_evento);
        associaVariaveis();
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
        getSupportActionBar().setTitle(evento.getTituloEvento());//Ativar o botão

        Picasso.get().load(evento.getImagemEvento()).resize(width,height).centerCrop().into(imgEvento);
        titulo.setText(evento.getTituloEvento());
        local.setText(evento.getEndereco());
        data.setText(evento.getDataEvento());
        hora.setText(evento.getHoraEvento());
        // O 03 abaixo referencia a quanridade de participantes no evento !!! mudar depois
        qtdparticipantes.setText("03/"+evento.getQtdParticipante().toString());
        descricao.setText(evento.getDescricaoEvento());

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

    // busca lista de participantes
    private void listaParticipantes() {

        final ArrayList<Usuario> listaUsuarioaux = new ArrayList<>();
        FirebaseDatabase.getInstance() // aqui esta sendo usada a classe direta do firabase
                .getReference()
                .child("eventos")
                .child(evento.getIdEvento()).child("participantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Usuario user = postSnapshot.getValue(Usuario.class);
                    listaUsuario.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
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
        this.mcontext = this;
        this.imgEvento = (ImageView) findViewById(R.id.img_evento);
        this.titulo = (TextView) findViewById(R.id.id_titulo_evento);
        this.local = (TextView) findViewById(R.id.id_loc_evento);
        this.data = (TextView) findViewById(R.id.id_data);
        this.hora = (TextView) findViewById(R.id.id_hora);
        this.qtdparticipantes = (TextView ) findViewById(R.id.id_qtd_participantes);
        this.descricao = (TextView) findViewById(R.id.id_descricao);
        this.btnParticipar = (Button) findViewById(R.id.btn_participar);
        this.btnVoltar = (Button) findViewById(R.id.btn_voltar);
        this.mToolbar = (Toolbar) findViewById(R.id.tb_evento);
        this.tinyDB = new TinyDB(EventoActivity.this);
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
        Intent it = new Intent(EventoActivity.this,MenuActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("evento",evento);
    }
}
