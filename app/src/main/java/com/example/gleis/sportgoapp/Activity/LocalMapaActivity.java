package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocalMapaActivity extends AppCompatActivity {

    private MapView mapa;
    private EditText adress;
    private ImageView btnLocal;
    private Button btnProximo;
    private Button btnVoltar;
    private Double lat;
    private Double lng;
    private String endereco;
    private TinyDB tinyDB;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_mapa);
        associaVariaveis();
        mapa.onCreate(savedInstanceState);

        // pega objeto evento salvo na memoria
        evento = tinyDB.getObject("evento",Evento.class);

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarLatLngEndereco(adress.getText().toString().replace(" ", "+"));
                // new Getcordenadas().execute(adress.getText().toString().replace(" ","+"));
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // passando dados de lat, lng e endereco do evento
                evento.setEnderecolat(lat);
                evento.setEnderecolng(lng);
                evento.setEndereco(endereco);
                //salva novamente os dados na memoria
                tinyDB.putObject("evento",evento);
                // devera ser criado condição para avançar

                Intent it = new Intent(LocalMapaActivity.this, ImagemEventoActivity.class);
                startActivity(it);
                finish();

            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itVoltar = new Intent(LocalMapaActivity.this, CriarEventoActivity.class);
                startActivity(itVoltar);
                finish();
            }
        });


    }

    private void buscarLatLngEndereco(String endereco) {

        Geocoder geocoder = new Geocoder(LocalMapaActivity.this, Locale.getDefault());
        try {
            List<Address> enderecos = geocoder.getFromLocationName(endereco, 1);
            if (enderecos.size() > 0) {

                this.lat = enderecos.get(0).getLatitude();
                this.lng = enderecos.get(0).getLongitude();
                Log.d("teste", "lat :" + lat + " lng " + lng);
                buscarEnderecoLatLng(lat, lng);
            }
        } catch (IOException e) {

        }
    }

    private void buscarEnderecoLatLng(Double lat, Double lng) {

        Geocoder geocoder = new Geocoder(LocalMapaActivity.this, Locale.getDefault());
        try {
            List<Address> enderecos = geocoder.getFromLocation(lat, lng, 1);
            if (enderecos.size() > 0) {

                this.endereco = enderecos.get(0).getAddressLine(0);
                Log.d("teste", "endereco : " + endereco);

                alert(endereco);
                pesquisaLatLong(lat, lng, endereco);

            }
        } catch (IOException e) {

        }
    }

    private void associaVariaveis() {

        tinyDB = new TinyDB(this);
        mapa = (MapView) findViewById(R.id.id_mapa_local);
        adress = (EditText) findViewById(R.id.id_hora_evento);
        btnLocal = (ImageView) findViewById(R.id.id_procurar_loc);
        btnProximo = (Button) findViewById(R.id.id_proximo_loc);
        btnVoltar = (Button) findViewById(R.id.id_voltar_loc);

    }

    private void pesquisaLatLong(final Double lat, final Double lng, final String endereco) {

        mapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                LatLng franca = new LatLng(lat, lng);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


                googleMap.addMarker(new MarkerOptions().position(franca).title(endereco).draggable(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(franca, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        Toast.makeText(LocalMapaActivity.this, "Escolha um local",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng position = marker.getPosition();

                        Double latdrag = position.latitude;
                        Double lngdrag = position.longitude;

                        buscarEnderecoLatLng(latdrag, lngdrag);
                        //Passar lat lng do markador para variavis lat lng;
                    }
                });


            }
        });


    }

    private void alert(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void passaLatLng(Double latdrag, Double lngdrag) {
        lat = latdrag;
        lng = lngdrag;

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

}
