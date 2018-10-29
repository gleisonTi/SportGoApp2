package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private TextView txtCadastrar;
    private TextView recuperarSenha;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        associacao();
        usuarioLogado();


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // testa se tem conexão
                if(isOnline()){

                    progressDialog.setMessage("Buscando Usuario...");
                    progressDialog.show();
                    if(!edtEmail.getText().toString().equals("")  && !edtSenha.getText().toString().equals("")){
                        if(validateEmailFormat(edtEmail.getText().toString())){
                            String email = edtEmail.getText().toString();
                            String senha = edtSenha.getText().toString();

                            validacao(email,senha);
                        }else{
                            alert("Email digitado não e valido");
                            progressDialog.dismiss();
                        }

                    }else{
                        alert("Preencha os campos de email e senha");
                        progressDialog.dismiss();
                    }
                }
                else {
                    alert("Não há conexão com a internet");
                    progressDialog.dismiss();
                }


            }
        });

        txtCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,  CadastroLoginActivity.class);
                startActivity(it);
            }
        });
        recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, RecuperaSenha.class);
                startActivity(it);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean validateEmailFormat(final String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    private void usuarioLogado() {
        // testa se o usuario esta logado
        autenticacao = ConfiguraFirebase.getAutenticacao();
        if( autenticacao.getCurrentUser() != null) {

            Intent it = new Intent(MainActivity.this,MenuActivity.class);
            startActivity(it);
            finish();

        }
    }


    private void alert(String txt) {
        Toast.makeText(MainActivity.this, txt, Toast.LENGTH_SHORT).show();
    }

    private void validacao(String email, String senha) {

        autenticacao = ConfiguraFirebase.getAutenticacao();

        autenticacao.signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    alert("Login efetuado com sucesso");
                    abrirTelaMenu();

                }else{

                    alert("Email ou senha incorretos");
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG);
                progressDialog.dismiss();

            }
        });

    }


    private void abrirTelaMenu() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Intent it = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(it);
        finish();
    }

    private void associacao() {

        this.edtEmail = (EditText) findViewById(R.id.id_email);
        this.edtSenha = (EditText) findViewById(R.id.id_senha);
        this.btnEntrar = (Button) findViewById(R.id.id_entrar);
        this.txtCadastrar = (TextView) findViewById(R.id.id_cadastro);
        this.recuperarSenha = (TextView) findViewById(R.id.id_recuperarSenha);
    }

    // Testa a conexão com a internet e retorna verdadeiro ou falso
    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}
