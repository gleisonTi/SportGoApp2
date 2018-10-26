package com.example.gleis.sportgoapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Activity.EventoActivity;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private TinyDB tinyDB;

    public InfoWindowAdapter(Context ctx){
        context = ctx;
        tinyDB = new TinyDB(ctx);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.info_window_eventos, null);

        CircleImageView imgEvento = view.findViewById(R.id.img_evento);
        final TextView tituloEvento = view.findViewById(R.id.id_titulo_evento);
        TextView distanciaEvento = view.findViewById(R.id.id_km_evento);
        TextView descricaoEvento = view.findViewById(R.id.descricao);

        tituloEvento.setText(marker.getTitle());
        distanciaEvento.setText(marker.getSnippet());

        final Evento infoWindowData = (Evento) marker.getTag();

        Picasso.get().load(infoWindowData.getImagemEvento()).into(imgEvento);

        descricaoEvento.setText(infoWindowData.getDescricaoEvento());


        return view;
    }
}
