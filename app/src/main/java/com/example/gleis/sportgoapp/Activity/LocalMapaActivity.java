package com.example.gleis.sportgoapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private Evento eventoEdit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_mapa);
        associaVariaveis();

        mapa.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(LocalMapaActivity.this);
        progressDialog.setMessage("Buscando Local");
        progressDialog.setCanceledOnTouchOutside(false);
        // pega objeto evento salvo na memoria
        evento = tinyDB.getObject("evento", Evento.class);

        if (tinyDB.getBoolean("flagDeEdicao")) {
            btnProximo.setText("Salvar Alterações");
            btnVoltar.setVisibility(View.GONE);
            eventoEdit = tinyDB.getObject("eventoEdit", Evento.class);

            lat = eventoEdit.getEnderecolat();
            lng = eventoEdit.getEnderecolng();
            endereco = eventoEdit.getEndereco();

            pesquisaLatLong(lat,lng,null);

        } else {
            btnProximo.setText("Proximo");
            btnVoltar.setVisibility(View.VISIBLE);
        }

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                buscarLatLngEndereco(adress.getText().toString().replace(" ", "+"));
                // new Getcordenadas().execute(adress.getText().toString().replace(" ","+"));
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!lat.toString().isEmpty() && !lng.toString().isEmpty()) {
                    // passando dados de lat, lng e endereco do evento
                    evento.setEnderecolat(lat);
                    evento.setEnderecolng(lng);
                    evento.setEndereco(endereco);

                    if (tinyDB.getBoolean("flagDeEdicao")) {

                        new AlertDialog.Builder(LocalMapaActivity.this)
                                .setTitle("Edição de Evento")
                                .setMessage("deseja editar o evento " + eventoEdit.getTituloEvento() + "?")
                                .setPositiveButton("sim",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                               /* eventoEdit.setTituloEvento(tituloEvento.getText().toString());
                                                eventoEdit.setTipoEvento(tipoEvento.getText().toString());
                                                eventoEdit.setQtdParticipante(Integer.parseInt(qtdParticipante.getText().toString()));
                                                eventoEdit.setDescricaoEvento(descricaoEvento.getText().toString());
                                                eventoEdit.setDataEvento(tvData.getText().toString());
                                                eventoEdit.setHoraEvento(tvHora.getText().toString());*/

                                                // objeto utilizado atualizar os dados
                                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                                // add hash map nesse formato

                                                //salva ediçoes do lat lng
                                                taskMap.put("enderecolat", lat);
                                                taskMap.put("enderecolng", lng);
                                                taskMap.put("endereco", endereco);

                                                eventoEdit.atualizaFirebaseEvento(taskMap);
                                                alert("Foram salvas alterações no evento " + eventoEdit.getTituloEvento());
                                                tinyDB.remove("flagDeEdicao");
                                                Intent it = new Intent(LocalMapaActivity.this, MenuActivity.class);
                                                startActivity(it);

                                                finish();
                                            }
                                        })
                                .setNegativeButton("não", null)
                                .show();

                    } else {
                        //salva novamente os dados na memoria
                        tinyDB.putObject("evento", evento);
                        // devera ser criado condição para avançar

                        Intent it = new Intent(LocalMapaActivity.this, ImagemEventoActivity.class);
                        startActivity(it);
                        finish();

                    }

                }else{
                    alert("Escolha um Endereço");
                }
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.remove("flagDeEdicao");
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

                lat = enderecos.get(0).getLatitude();
                lng = enderecos.get(0).getLongitude();
                Log.d("teste", "lat :" + lat + " lng " + lng);
                buscarEnderecoLatLng(lat, lng);
            } else {

                progressDialog.dismiss();
                alert("Local não encotrado");

            }
        } catch (IOException e) {
            progressDialog.dismiss();
        }
    }

    private void buscarEnderecoLatLng(Double lat, Double lng) {

        Geocoder geocoder = new Geocoder(LocalMapaActivity.this, Locale.getDefault());
        try {
            List<Address> enderecos = geocoder.getFromLocation(lat, lng, 1);
            if (enderecos.size() > 0) {

                endereco = enderecos.get(0).getAddressLine(0);
                Log.d("teste", "endereco : " + endereco);

                alert(endereco);
                this.lat = lat;
                this.lng = lng;
                pesquisaLatLong(lat, lng, endereco);

            } else {
                progressDialog.dismiss();
                alert("Local não localizado");
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
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (tinyDB.getBoolean("flagDeEdicao")) {
            Intent it = new Intent(LocalMapaActivity.this, MenuActivity.class);
            startActivity(it);
        }
        tinyDB.remove("flagDeEdicao");
    }
}
