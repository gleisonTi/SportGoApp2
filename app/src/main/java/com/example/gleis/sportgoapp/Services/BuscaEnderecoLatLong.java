package com.example.gleis.sportgoapp.Services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by gleis on 04/05/2018.
 */

public class BuscaEnderecoLatLong {

    private Double latitude;
    private Double longitude;
    private String endereco;
    private Context mcontext;

    public BuscaEnderecoLatLong() {

    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Context getMcontext() {
        return mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public void buscaLatLng(Context c,String endereco){
        Geocoder geocoder = new Geocoder(c);
        try {
            List<Address> enderecos = geocoder.getFromLocationName(endereco, 1);
            if (enderecos.size() > 0) {

                setLatitude(enderecos.get(0).getLatitude());
                setLongitude(enderecos.get(0).getLongitude());
            }
        }catch (IOException e)

        {

        }
    }
}
