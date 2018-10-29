package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarUsuarioActivity extends AppCompatActivity {

    public CircleImageView imgPerfil;

    public EditText edtNome;
    public EditText edtIdade;
    public EditText edtEsporte;
    public EditText edtEstado;
    public EditText edtCidade;
    public FloatingActionButton deletarConta;

    public RadioButton rbMasculino;
    public RadioButton rbFeminino;

    public Button btnSalvar;
    public Button btnCancelar;

    public TinyDB tinyDB;
    public  Usuario usuario;
    public Map<String, Object> taskMap = new HashMap<String, Object>();

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);
        initVariaveis();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Atualizando dados");


        usuario = tinyDB.getObject("dadosUsuario", Usuario.class);

        recuperaDados();

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtNome.getText().toString().isEmpty()
                        && !edtIdade.getText().toString().isEmpty()
                        && !edtEsporte.getText().toString().isEmpty()
                        && !edtEstado.getText().toString().isEmpty()
                        && !edtCidade.getText().toString().isEmpty()){

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditarUsuarioActivity.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Deseja atualizar seus dados");
                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            editarUsuario();
                        }
                    });
                    alertDialog.setNegativeButton("Não",null);
                    alertDialog.create().show();

                }else{
                    Toast.makeText(getApplication(),"A campo vazios",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deletarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditarUsuarioActivity.this);
                alert.setMessage("Deseja realmente excluir sua conta");
                alert.setTitle("Excluir conta");
                alert.setCancelable(true);
                alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Excluido Conta");
                        progressDialog.show();
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    usuario.removeFirebase();
                                    Toast.makeText(getApplicationContext(),"Conta excluida com sucesso",Toast.LENGTH_LONG).show();
                                    Intent it = new Intent(getApplicationContext(),MainActivity.class);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    it.putExtra("EXIT", true);
                                    startActivity(it);
                                    finish();
                                }
                            }
                        });
                    }
                });
                alert.setNegativeButton("Não",null);
                alert.create().show();
            }
        });
    }

    private void recuperaDados() {
        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(usuario.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);
                    Picasso.get().load(user.getUrlImagem()).into(imgPerfil);
                    edtNome.setText(user.getNome());
                    edtIdade.setText(user.getIdade());
                    edtEsporte.setText(user.getEsporte());
                    edtEstado.setText(user.getEstado());
                    edtCidade.setText(user.getCidade());
                    if(user.getSexo().contains("Masculino")){
                        rbMasculino.setChecked(true);
                    }else{
                        rbFeminino.setChecked(true);
                    }
                }
           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplication(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editarUsuario() {

        // add hash map nesse formato
        taskMap.put("nome", edtNome.getText().toString());
        taskMap.put("idade", edtIdade.getText().toString());
        taskMap.put("esporte", edtEsporte.getText().toString());
        taskMap.put("estado",edtEstado.getText().toString());
        taskMap.put("cidade", edtCidade.getText().toString());
        if (rbMasculino.isChecked()) {
            taskMap.put("sexo", "Masculino");

        }else{
            taskMap.put("sexo", "Feminino");
        }
        //salva edições do Usuario
        usuario.atualizaFirebaseUsuario(taskMap,this);

        atualizaEventos();
    }

    private void atualizaEventos() {
        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("eventos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapEvento : dataSnapshot.getChildren()){
                    if (snapEvento.child("participantes").exists()) {
                        for(DataSnapshot snapUsuario : snapEvento.child("participantes").getChildren()){
                            Usuario user = snapUsuario.getValue(Usuario.class);
                            if(usuario.getEmail().equals(user.getEmail())){
                                snapUsuario.getRef().updateChildren(taskMap);
                            }
                        }
                    }

                    if (snapEvento.child("usuarioCriador").getValue(Usuario.class).getEmail().equals(usuario.getEmail())) {
                        snapEvento.child("usuarioCriador").getRef().updateChildren(taskMap);
                    }
                }
                progressDialog.dismiss();
                taskMap.clear();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initVariaveis() {

        tinyDB = new TinyDB(this);

        imgPerfil = (CircleImageView) findViewById(R.id.id_editar_imagem_perfil);



        edtNome = (EditText) findViewById(R.id.id_editar_nome);
        edtIdade = (EditText) findViewById(R.id.id_editar_idade);
        edtEsporte = (EditText) findViewById(R.id.id_editar_esporte);
        edtEstado = (EditText) findViewById(R.id.id_editar_estado);
        edtCidade = (EditText) findViewById(R.id.id_editar_cidade);
        deletarConta = (FloatingActionButton) findViewById(R.id.id_deletar_conta);

        rbFeminino = (RadioButton) findViewById(R.id.id_editar_sexo_feminino);
        rbMasculino = (RadioButton) findViewById(R.id.id_editar_sexo_masculino);

        btnSalvar = (Button) findViewById(R.id.id_btn_salvar);

        btnCancelar = (Button) findViewById(R.id.id_btn_cancelar);
    }
}
