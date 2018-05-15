package com.example.gleis.sportgoapp.Services;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by gleis on 14/05/2018.
 */

public class UserDados {

    private static Usuario usuarioFirebase;
    private static FirebaseUser user;
    private static DatabaseReference usuariodados;
    private static Usuario usuario;

    public UserDados() {
    }

    public static Usuario usuarioFirebase() {

        usuario = new Usuario();
        usuariodados = ConfiguraFirebase.getFirebase();
        user = ConfiguraFirebase.getAutenticacao().getCurrentUser();
        if (user != null) {
            usuariodados.child("usuarios").child(Base64Custom.codificarBase64(user.getEmail())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    usuarioFirebase = dataSnapshot.getValue(Usuario.class);

                    System.out.println("Usuario dados1 --->"+usuarioFirebase.getEmail());

                    usuario.setNome(usuarioFirebase.getNome());
                    usuario.setEmail(usuarioFirebase.getEmail());
                    usuario.setEsporte(usuarioFirebase.getEsporte());
                    usuario.setIdade(usuarioFirebase.getIdade());
                    usuario.setEstado(usuarioFirebase.getEstado());
                    usuario.setCidade(usuarioFirebase.getCidade());
                    usuario.setSexo(usuarioFirebase.getSexo());
                    usuario.setUrlImagem(usuarioFirebase.getUrlImagem());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        // retornando dados do usuario logado
        return usuarioFirebase;
    }
}
