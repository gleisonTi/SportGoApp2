package com.example.gleis.sportgoapp.Entidades;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by gleis on 20/03/2018.
 */

public class Usuario {

        private String id;
        private String nome;
        private String idade;
        private String esporte;
        private String estado;
        private String cidade;
        private String email;
        private String senha;
        private String sexo;
        private String UrlImagem;

    public Usuario() {

    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getEsporte() {
        return esporte;
    }

    public void setEsporte(String esporte) {
        this.esporte = esporte;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUrlImagem() {
        return UrlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        UrlImagem = urlImagem;
    }

    public  void salvarFirebase(){

        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(String.valueOf(getId())).setValue(this);
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> hashMapUsuario = new HashMap<>();

        hashMapUsuario.put("imagem",getUrlImagem());
        hashMapUsuario.put("id",getId());
        hashMapUsuario.put("nome",getNome());
        hashMapUsuario.put("idade",getIdade());
        hashMapUsuario.put("esporte",getEsporte());
        hashMapUsuario.put("estado",getEstado());
        hashMapUsuario.put("cidade",getCidade());
        hashMapUsuario.put("email",getEmail());
        hashMapUsuario.put("senha",getSenha());
        hashMapUsuario.put("sexo",getSexo());

        return hashMapUsuario;
    }
}





