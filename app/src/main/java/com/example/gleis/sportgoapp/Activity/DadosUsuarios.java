package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.gleis.sportgoapp.Entidades.Usuario;
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
    private Usuario usuario = UserDados.usuarioFirebase();

    FloatingActionButton btnEditar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuarios);
        associa();

        Picasso.get().load(usuario.getUrlImagem()).into(img_perfil);
        tv_email.setText(usuario.getEmail());
        tv_esporte.setText(usuario.getEsporte());
        tv_cidade.setText(usuario.getCidade());
        tv_estado.setText(usuario.getEstado());


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
        mtoolbar = (Toolbar) findViewById(R.id.tbDetalhes);
        img_perfil = (ImageView) findViewById(R.id.img_perfil_usuario);
        tv_email = (TextView) findViewById(R.id.tv_email_usuario);
        tv_esporte = (TextView) findViewById(R.id.tv_esporte_usuario);
        tv_cidade = (TextView) findViewById(R.id.tv_cidade_usuario);
        tv_estado = (TextView) findViewById(R.id.tv_estado_usuario);

    }
}
