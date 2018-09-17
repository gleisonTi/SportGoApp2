package com.example.gleis.sportgoapp.Fragment;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gleis.sportgoapp.Activity.LocalMapaActivity;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.example.gleis.sportgoapp.Services.BuscaEnderecoLatLong;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment {

    private MapView mapView;
    private List<Evento> listaEvento;
    private DatabaseReference referenceFirebase;
    private Evento todosEventos;
    private TinyDB tinyDB;
    private Usuario usuario;
    private FirebaseUser user;
    private DatabaseReference usuariodados;

    // Latitude  e Longitude  da região do usuario.
    private LatLng latLngRegiao;


    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        // iniciando Tinydb
        tinyDB =  new TinyDB(getContext());
        //inicializando mapa
        mapView = (MapView) view.findViewById(R.id.mapViewEventos);
        mapView.onCreate(savedInstanceState);
        if(isOnline()){

            listaEventosMapa();

            associaDadosFirebase();
            // Inflate the layout for this fragment
        }

        return view;
    }

    private void listaEventosMapa() {

        listaEvento =  new ArrayList<>();

        referenceFirebase = ConfiguraFirebase.getFirebase();

        referenceFirebase.child("eventos").orderByChild("idEvento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            // Listando os eventos nos mapa a partir do Firebasedatabase
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todosEventos = postSnapshot.getValue(Evento.class);

                    Double lat = todosEventos.getEnderecolat();
                    Double lng = todosEventos.getEnderecolng();
                    String titulo = todosEventos.getTituloEvento();

                    mostrarNoMapa(lat,lng,titulo);

                    listaEvento.add(todosEventos);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // teste de conexão

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    //buscando dados do usuario para recuper latlng//
    private void associaDadosFirebase() {

        // pega localização do dispositivo para iniciar o mapa
        latLngRegiao = tinyDB.getObject("latlngAtual",LatLng.class);

        // pega dados a partir da cidade do usuario
        /*usuariodados = ConfiguraFirebase.getFirebase();
        user = ConfiguraFirebase.getAutenticacao().getCurrentUser();
        if(user != null){
            usuariodados.child("usuarios").child(Base64Custom.codificarBase64(user.getEmail())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // recebendo dados do usuario
                    usuario = dataSnapshot.getValue(Usuario.class);

                    //buscando latlng da cidade do Usuario pra inicio.
                    BuscaEnderecoLatLong buscarcidade = new BuscaEnderecoLatLong();
                    // passando contexto , cidade e estado para busca de latlog
                    buscarcidade.buscaLatLng(getActivity(),usuario.getCidade()+"+"+usuario.getEstado());

                    // passando latLong da região do usuario para variaveis globais

                    latLngRegiao = new LatLng(buscarcidade.getLatitude(),buscarcidade.getLongitude());

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }*/
    }


    private void mostrarNoMapa(final Double lat, final Double lng, final String tituloEvento) {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //latitude e longitude dos eventos
                LatLng latLng = new LatLng(lat, lng);
                // posicionando mapa na região do usuario
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // marcando locais dos eventos
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                marker.title(tituloEvento);
                googleMap.addMarker(marker);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRegiao,12));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12),100,null);

            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


}
