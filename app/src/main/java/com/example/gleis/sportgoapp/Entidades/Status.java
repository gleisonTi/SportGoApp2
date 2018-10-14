package com.example.gleis.sportgoapp.Entidades;

public class Status {

    private String tipo;
    private String motivoDescricao;

    public Status(String tipo, String motivoDescricao) {
        this.tipo = tipo;
        this.motivoDescricao = motivoDescricao;
    }

    public Status() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivoDescricao() {
        return motivoDescricao;
    }

    public void setMotivoDescricao(String motivoDescricao) {
        this.motivoDescricao = motivoDescricao;
    }

    @Override
    public String toString() {
        return "Status{" +
                "tipo='" + tipo + '\'' +
                ", motivoDescricao='" + motivoDescricao + '\'' +
                '}';
    }
}
