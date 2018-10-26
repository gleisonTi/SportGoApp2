package com.example.gleis.sportgoapp.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Activity.EventoActivity;
import com.example.gleis.sportgoapp.Activity.LocalMapaActivity;
import com.example.gleis.sportgoapp.Adapter.InfoWindowAdapter;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private MarkerOptions marker; // vc parou aqui

    // Latitude  e Longitude  da região do usuario.
    private LatLng latLngRegiao;
    public static final int REQUEST_FINE_LOCATION = 99;


    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        // iniciando Tinydb
        tinyDB = new TinyDB(getContext());
        //inicializando mapa
        mapView = (MapView) view.findViewById(R.id.mapViewEventos);
        mapView.onCreate(savedInstanceState);
        if (isOnline()) {

            latLngRegiao = tinyDB.getObject("latlngAtual", LatLng.class);
            System.out.println("lat do usuario" + latLngRegiao.latitude + latLngRegiao.longitude);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRegiao, 12));
                    if (checkPermissions()) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);

                    }
                }
            });

            listaEventosMapa();
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
                    if (dataSnapshot.exists()) {
                        todosEventos = postSnapshot.getValue(Evento.class);
                        if (todosEventos.getStatusEvento().getTipo().equals("Ativo")) {
                            Double lat = todosEventos.getEnderecolat();
                            Double lng = todosEventos.getEnderecolng();
                            String titulo = todosEventos.getTituloEvento();


                            mostrarNoMapa(todosEventos);
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
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


    private void mostrarNoMapa(final Evento evento) {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //latitude e longitude dos eventos
                LatLng latLng = new LatLng(evento.getEnderecolat(), evento.getEnderecolng());
                // posicionando mapa na região do usuario
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // marcando locais dos eventos

                marker = new MarkerOptions();
                marker.position(latLng)
                        .title(evento.getTituloEvento())
                        .snippet(getdistancia(latLng))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter(getActivity());
                googleMap.setInfoWindowAdapter(infoWindowAdapter);
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getContext(),evento.getTituloEvento(),Toast.LENGTH_SHORT).show();
                        tinyDB.putObject("evento",evento);
                        Intent intent = new Intent(getContext(), EventoActivity.class);
                        getContext().startActivity(intent);
                    }
                });

                Marker m = googleMap.addMarker(marker);
                m.setTag(evento);

                m.showInfoWindow();

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRegiao,12));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12),100,null);

            }

        });


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
