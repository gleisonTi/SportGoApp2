package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaSenha extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnEnviar;
    private TextView alertError;
    private TextView alertSucess;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnEnviar = (Button) findViewById(R.id.btn_Rsenha);
        alertError = (TextView) findViewById(R.id.id_alert_erro);
        alertSucess = (TextView) findViewById(R.id.id_alert_sucess);
        alertSucess.setVisibility(View.GONE);
        alertError.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando");


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtEmail.getText().toString().isEmpty()) {
                    progressDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(edtEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        alertSucess.setVisibility(View.VISIBLE);
                                        alertError.setVisibility(View.GONE);
                                    } else {
                                        progressDialog.dismiss();
                                        alertError.setVisibility(View.VISIBLE);
                                        alertSucess.setVisibility(View.GONE);

                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplication(),"Digite um email para Envio",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
