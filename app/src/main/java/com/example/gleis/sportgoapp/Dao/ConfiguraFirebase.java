package com.example.gleis.sportgoapp.Dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by gleis on 20/03/2018.
 */

public class ConfiguraFirebase {
    private  static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private  static StorageReference storage;

    public static DatabaseReference getFirebase(){
        if (referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static  FirebaseAuth getAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
    public static StorageReference getStorage(){
        if (storage == null){

            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
