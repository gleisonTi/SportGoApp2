package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Adapter.TabAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.Helper.SlidingTabLayout;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private CircleImageView imgPerfil;
    private TextView nomePerfil;
    private TextView emailPerfil;
    private Usuario usuario;
    private FirebaseUser user;
    private DatabaseReference usuariodados;
    private TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //associa layou a variaveis slidingTabLayout e viewPager
        associa();
        setSupportActionBar(toolbar);

        // Configura Sliding Tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.edit_text_color));


        //Configurar Adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itCadEvento = new Intent(MenuActivity.this,CriarEventoActivity.class);
                startActivity(itCadEvento);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // pegar view lateral
        View hView =  navigationView.getHeaderView(0);
        imgPerfil = (CircleImageView) hView.findViewById(R.id.imagen_perfil_menu);
        nomePerfil = (TextView) hView.findViewById(R.id.id_nome_perfil);
        emailPerfil = (TextView) hView.findViewById(R.id.id_email_perfil);

        associaDadosFirebase();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void associaDadosFirebase() {

        usuariodados = ConfiguraFirebase.getFirebase();
        user = ConfiguraFirebase.getAutenticacao().getCurrentUser();
        if(user != null){
            usuariodados.child("usuarios").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        // adapta o retorno da função a classe Usuario
                        usuario = postSnapshot.getValue(Usuario.class);
                        //encontra o usuario ativo no momento
                        if(user.getEmail().equals(usuario.getEmail())){
                        // salva os dados do usuario para ser usado futuramente
                            tinyDB.putObject("dadosUsuario",usuario);

                        // popula o menu lateral com informaçoes do usuario
                            Picasso.get().load(usuario.getUrlImagem()).into(imgPerfil);
                            nomePerfil.setText(usuario.getNome());
                            emailPerfil.setText(usuario.getEmail());

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    private void associa() {
        tinyDB = new TinyDB(MenuActivity.this);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.id_conta) {

            Intent itconta = new Intent(MenuActivity.this,DadosUsuarios.class);
            startActivity(itconta);

        } else if (id == R.id.id_eventos) {

        } else if (id == R.id.id_eventos_criados) {

        } else if (id == R.id.id_amigos) {

        } else if (id == R.id.id_sair) {

            FirebaseAuth auth = ConfiguraFirebase.getAutenticacao();
            auth.signOut();

            Intent it = new Intent(MenuActivity.this,MainActivity.class);

            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
