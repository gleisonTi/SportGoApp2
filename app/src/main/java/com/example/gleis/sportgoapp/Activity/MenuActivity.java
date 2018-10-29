package com.example.gleis.sportgoapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.example.gleis.sportgoapp.Adapter.TabAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.Helper.SlidingTabLayout;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private CircleImageView imgPerfil;
    private TextView nomePerfil;
    private TextView emailPerfil;
    private Usuario usuario;
    private FirebaseUser user;
    private DatabaseReference usuariodados;
    private FloatingActionButton fab;
    private TinyDB tinyDB;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Guarda a ultima posição do smartphone.
     */
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //associa layou a variaveis slidingTabLayout e viewPager
        associaDadosFirebase();
        associa();
        setSupportActionBar(toolbar);
        //pega dados do usuario logado no sistema
        //permissão de gps
        checkLocationPermission();
        if(tinyDB.getBoolean("flagDeEdicao")){
            tinyDB.remove("flagDeEdicao");
        }

        // Vamos instanciar o GoogleApiClient, caso seja nulo
        // necessario para pegar localização do dispositivo
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this) // Interface ConnectionCallbacks
                    .addOnConnectionFailedListener(this) //Interface OnConnectionFailedListener
                    .addApi(LocationServices.API) // Vamos a API do LocationServices
                    .build();
        }

        // Configura Sliding Tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.edit_text_color));


        //Configurar Adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(1,true);
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    fab.setVisibility(View.GONE);
                }else if(position == 1){
                    fab.setVisibility(View.VISIBLE);
                }if(position == 0){
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itCadEvento = new Intent(MenuActivity.this, CriarEventoActivity.class);
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
        View hView = navigationView.getHeaderView(0);
        imgPerfil = (CircleImageView) hView.findViewById(R.id.imagen_perfil_menu);
        nomePerfil = (TextView) hView.findViewById(R.id.id_nome_perfil);
        emailPerfil = (TextView) hView.findViewById(R.id.id_email_perfil);

        navigationView.setNavigationItemSelectedListener(this);

        LatLng latLng = new LatLng(-20.557311, -47.413900);
        tinyDB.putObject("latlngAtual",latLng);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void associaDadosFirebase() {

        usuariodados = ConfiguraFirebase.getFirebase();
        user = ConfiguraFirebase.getAutenticacao().getCurrentUser();
        if (user != null) {
            usuariodados.child("usuarios").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // adapta o retorno da função a classe Usuario
                        usuario = postSnapshot.getValue(Usuario.class);
                        //encontra o usuario ativo no momento
                        if (user.getEmail().equals(usuario.getEmail())) {
                            // salva os dados do usuario para ser usado futuramente
                            tinyDB.putObject("dadosUsuario", usuario);
                            System.out.println("Usuario: "+usuario.getEmail());
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
        fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
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

            Intent itconta = new Intent(MenuActivity.this, DadosUsuarios.class);
            itconta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(itconta);

        } else if (id == R.id.id_eventos) {

        } else if (id == R.id.id_eventos_criados) {

        } else if (id == R.id.id_amigos) {
            Intent itchat = new Intent(MenuActivity.this, ChatActivity.class);
            startActivity(itchat);

        } else if (id == R.id.id_sair) {

            FirebaseAuth.getInstance().getCurrentUser().reload();
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            }else{
                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
           /* user.reload();
            FirebaseAuth.getInstance().signOut();
            Intent it = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(it);
            finish();*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // pegamos a ultima localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            // Criamos o LatLng através do Location
            final LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            tinyDB.putObject("latlngAtual",latLng);
        }else{
            final LatLng latLng = new LatLng(-20.557311, -47.413900);
            tinyDB.putObject("latlngAtual",latLng);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // func resposavel pela permição

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permissão de Localização")
                        .setMessage("O aplicativo necessita de permissão de acesso ao gps, permitir?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MenuActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
