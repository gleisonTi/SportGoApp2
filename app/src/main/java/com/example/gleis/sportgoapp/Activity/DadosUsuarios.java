package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.example.gleis.sportgoapp.Services.UserDados;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DadosUsuarios extends AppCompatActivity {

    private CircleImageView img_perfil;
    private ImageView imgSexoUser;
    private TextView tv_email;
    private TextView tv_nome;
    private TextView tv_esporte;
    private TextView tv_cidade;
    private TextView tv_idade;
    private FloatingActionButton fb_editar;
    private TinyDB tinyDB;
    private  Usuario usuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuarios);
        associa();

        usuario  = tinyDB.getObject("dadosUsuario",Usuario.class);

        fb_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DadosUsuarios.this,EditarUsuarioActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        });
        carregaDados();

    }

    private void carregaDados() {
        DatabaseReference referenciaFirebase = ConfiguraFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(usuario.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);
                    Picasso.get().load(user.getUrlImagem()).into(img_perfil);
                    tv_email.setText(user.getEmail());
                    tv_esporte.setText(user.getEsporte());
                    tv_cidade.setText(user.getCidade()+" - "+user.getEstado());
                    tv_nome.setText(user.getNome());
                    tv_idade.setText(user.getIdade());
                    tv_idade.setText(user.getIdade());
                    if (user.getSexo().toLowerCase().trim().equals("masculino")) {
                        imgSexoUser.setImageDrawable(ContextCompat.getDrawable(getApplication(),R.drawable.icon_male));
                    }else{
                        imgSexoUser.setImageDrawable(ContextCompat.getDrawable(getApplication(),R.drawable.icon_female));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplication(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MenuActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finish(); //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaDados();

    }

    private void associa() {
        tinyDB = new TinyDB(this);
        img_perfil = (CircleImageView) findViewById(R.id.id_img_user);
        imgSexoUser = (ImageView) findViewById(R.id.id_imgsexo);
        tv_email = (TextView) findViewById(R.id.email_user);
        tv_nome = (TextView) findViewById(R.id.nome_user);
        tv_idade = (TextView) findViewById(R.id.id_idade_user);
        tv_esporte = (TextView) findViewById(R.id.id_esporte_user);
        tv_cidade = (TextView) findViewById(R.id.id_cidade_user);
        fb_editar =(FloatingActionButton) findViewById(R.id.fb_editar);

    }
}
