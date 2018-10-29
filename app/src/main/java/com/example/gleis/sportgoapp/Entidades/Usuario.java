package com.example.gleis.sportgoapp.Entidades;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    public void removeFirebase(){
        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(String.valueOf(getId())).removeValue();
    }
    public void atualizaFirebaseUsuario(Map<String, Object> taskMap, final Context context) {
        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(getId()).updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(context,"Dados atualizados com sucesso",Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}





