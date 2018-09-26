package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.example.gleis.sportgoapp.Services.UserDados;
import com.squareup.picasso.Picasso;

public class DadosUsuarios extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageView img_perfil;
    private TextView tv_email;
    private TextView tv_esporte;
    private TextView tv_cidade;
    private TextView tv_estado;
    private FloatingActionButton fb_editar;
    private TinyDB tinyDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuarios);
        associa();

        Object dadosUsuario = tinyDB.getObject("dadosUsuario",Usuario.class);

        Usuario usuario = (Usuario) dadosUsuario;

        Picasso.get().load(usuario.getUrlImagem()).into(img_perfil);
        tv_email.setText(usuario.getEmail());
        tv_esporte.setText(usuario.getEsporte());

        fb_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DadosUsuarios.this,EditarUsuarioActivity.class);
                startActivity(it);
            }
        });
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(usuario.getNome()+", "+usuario.getIdade()+" anos");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MenuActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finish();; //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
    private void associa() {
        tinyDB = new TinyDB(this);
        mtoolbar = (Toolbar) findViewById(R.id.tbDetalhes);
        img_perfil = (ImageView) findViewById(R.id.img_perfil_usuario);
        tv_email = (TextView) findViewById(R.id.tv_email_usuario);
        tv_esporte = (TextView) findViewById(R.id.tv_esporte_usuario);
        fb_editar =(FloatingActionButton) findViewById(R.id.fb_editar);

    }
}
