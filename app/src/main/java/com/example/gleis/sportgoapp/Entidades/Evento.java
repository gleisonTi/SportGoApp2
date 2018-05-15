package com.example.gleis.sportgoapp.Entidades;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.Timer;

/**
 * Created by gleis on 09/04/2018.
 */

public class Evento implements Parcelable {

    private String idEvento;
    private String idUsuario;
    private String tituloEvento;
    private String tipoEvento;
    private Integer qtdParticipante;
    private String dataEvento;
    private String horaEvento;
    private String DescricaoEvento;
    private Double enderecolat;
    private Double enderecolng;
    private String endereco;
    private String imagemEvento;


    public Evento() {
    }

    // contrutor para utilizar o parcelable para passar os dados para outra actyvity
    protected Evento(Parcel in) {
        idEvento = in.readString();
        idUsuario = in.readString();
        tituloEvento = in.readString();
        tipoEvento = in.readString();
        if (in.readByte() == 0) {
            qtdParticipante = null;
        } else {
            qtdParticipante = in.readInt();
        }
        dataEvento = in.readString();
        horaEvento = in.readString();
        DescricaoEvento = in.readString();
        if (in.readByte() == 0) {
            enderecolat = null;
        } else {
            enderecolat = in.readDouble();
        }
        if (in.readByte() == 0) {
            enderecolng = null;
        } else {
            enderecolng = in.readDouble();
        }
        endereco = in.readString();
        imagemEvento = in.readString();
    }
 // Foi utilizado na classe a implementação do Parceable para passar dados da classe para outra actyvity
    public static final Creator<Evento> CREATOR = new Creator<Evento>() {
        @Override
        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        @Override
        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    public  void salvarFirebaseEvento(){

        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").child(String.valueOf(getIdEvento())).setValue(this);
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTituloEvento() {
        return tituloEvento;
    }

    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getQtdParticipante() {
        return qtdParticipante;
    }

    public void setQtdParticipante(Integer qtdParticipante) {
        this.qtdParticipante = qtdParticipante;
    }

    public String getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getHoraEvento() {
        return horaEvento;
    }

    public void setHoraEvento(String horaEvento) {
        this.horaEvento = horaEvento;
    }

    public String getDescricaoEvento() {
        return DescricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        DescricaoEvento = descricaoEvento;
    }

    public Double getEnderecolat() {
        return enderecolat;
    }

    public void setEnderecolat(Double enderecolat) {
        this.enderecolat = enderecolat;
    }

    public Double getEnderecolng() {
        return enderecolng;
    }

    public void setEnderecolng(Double enderecolng) {
        this.enderecolng = enderecolng;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getImagemEvento() {
        return imagemEvento;
    }

    public void setImagemEvento(String imagemEvento) {
        this.imagemEvento = imagemEvento;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idEvento);
        dest.writeString(idUsuario);
        dest.writeString(tituloEvento);
        dest.writeString(tipoEvento);
        if (qtdParticipante == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(qtdParticipante);
        }
        dest.writeString(dataEvento);
        dest.writeString(horaEvento);
        dest.writeString(DescricaoEvento);
        if (enderecolat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(enderecolat);
        }
        if (enderecolng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(enderecolng);
        }
        dest.writeString(endereco);
        dest.writeString(imagemEvento);
    }
}
