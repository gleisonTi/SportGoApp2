package com.example.gleis.sportgoapp.Activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DadosUsuarioActivity extends AppCompatActivity {

    private CircleImageView imgUser;
    private TextView nomeUser;
    private ImageView imgSexoUser;
    private TextView emailUser;
    private TextView idadeUser;
    private TextView cidadeUser;
    private TextView esporteUser;
    private TinyDB tinyDB;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuario);
        initViews();
        usuario = tinyDB.getObject("usuario",Usuario.class);

        Picasso.get().load(usuario.getUrlImagem()).into(imgUser);
        nomeUser.setText(usuario.getNome());
        if (usuario.getSexo().toLowerCase().trim().equals("masculino")) {
            imgSexoUser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_male));
        }else{
            imgSexoUser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_female));
        }
        emailUser.setText(usuario.getEmail());
        idadeUser.setText(usuario.getIdade()+" anos");
        cidadeUser.setText(usuario.getCidade()+" - "+usuario.getEstado());
        esporteUser.setText(usuario.getEsporte());

    }

    private void initViews() {
        tinyDB = new TinyDB(this);
        imgUser = (CircleImageView) findViewById(R.id.id_imgUser);
        imgSexoUser = (ImageView) findViewById(R.id.id_imgsexo);
        nomeUser = (TextView) findViewById(R.id.nome_usuario);
        emailUser = (TextView) findViewById(R.id.email_user);
        idadeUser = (TextView) findViewById(R.id.id_idade_user);
        cidadeUser = (TextView) findViewById(R.id.id_cidade_user);
        esporteUser = (TextView) findViewById(R.id.id_esporte_user);
    }
}
