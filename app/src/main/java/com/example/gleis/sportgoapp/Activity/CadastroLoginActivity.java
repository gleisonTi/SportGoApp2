package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gleis.sportgoapp.R;

import java.util.regex.Pattern;

public class    CadastroLoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtConfEmail;
    private EditText edtSenha;
    private EditText edtConfSenha;

    private Button btnProximo;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_login);

        //Associar layout nas variaveis
        associaVariaveis();

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(CadastroLoginActivity.this, CadastroActivity.class);

                if(validaEmail(edtEmail.getText().toString())){
                    if(edtEmail.getText().toString().equals(edtConfEmail.getText().toString()) && edtSenha.getText().toString().equals(edtConfSenha.getText().toString())){

                        String email = edtEmail.getText().toString().trim();
                        String senha = edtSenha.getText().toString();

                        Bundle bdlLogin = new Bundle();

                        bdlLogin.putString("email",email);
                        bdlLogin.putString("senha",senha);

                        it.putExtras(bdlLogin);

                        alert("Salvando Dados...");

                        startActivity(it);
                    }
                    else{
                        alert("E-mail ou senha digitados não conferem");
                    }
                }
                else{
                    alert("E-mail digitado não e valido");
                }

            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void alert(String texto) {

        Toast.makeText(this,texto,Toast.LENGTH_LONG).show();
    }

    private void associaVariaveis() {
        this.edtEmail = (EditText) findViewById(R.id.id_cad_email);
        this.edtConfEmail = (EditText) findViewById(R.id.id_cad_confEmail);
        this.edtSenha = (EditText) findViewById(R.id.id_cad_senha);
        this.edtConfSenha = (EditText) findViewById(R.id.id_cad_confSenha);

        this.btnProximo = (Button) findViewById(R.id.id_avancar_login);
        this.btnVoltar = (Button) findViewById(R.id.id_voltar_login);
    }

    public  boolean validaEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
