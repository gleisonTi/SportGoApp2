package com.example.gleis.sportgoapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.R;
import com.squareup.picasso.Picasso;

public class EventoActivity extends AppCompatActivity {

    private ImageView imgEvento;
    private TextView titulo;
    private TextView local;
    private TextView data;
    private TextView hora;
    private TextView qtdparticipantes;
    private TextView descricao;
    private Button btnParticipar;
    private Button btnVoltar;

    private Toolbar mToolbar;
    private Evento evento;

    private Context mcontext;

    private float scale;
    private int width;
    private int height;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        associaVariaveis();
        setSupportActionBar(mToolbar);

        if(savedInstanceState != null){
            evento = savedInstanceState.getParcelable("evento");
        }
        else{
            if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("evento") != null){
                evento = getIntent().getExtras().getParcelable("evento");
            }
            else{
                alert("Fallhou !!");
            }
        }

        //pegar tamanho da tela para width e height
        tamanhoTela();
        // colocando titulo do evento no toolbar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(evento.getTituloEvento());//Ativar o botão

        Picasso.get().load(evento.getImagemEvento()).resize(width,height).centerCrop().into(imgEvento);
        titulo.setText(evento.getTituloEvento());
        local.setText(evento.getEndereco());
        data.setText(evento.getDataEvento());
        hora.setText(evento.getHoraEvento());
        // O 03 abaixo referencia a quanridade de participantes no evento !!! mudar depois
        qtdparticipantes.setText("03/"+evento.getQtdParticipante().toString());
        descricao.setText(evento.getDescricaoEvento());

    }

    private void tamanhoTela() {

        scale = mcontext.getResources().getDisplayMetrics().density;

        width = mcontext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;
    }


    private void alert(String s) {
        Toast.makeText(EventoActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    private void associaVariaveis() {

        this.mcontext = this;
        this.imgEvento = (ImageView) findViewById(R.id.img_evento);
        this.titulo = (TextView) findViewById(R.id.id_titulo_evento);
        this.local = (TextView) findViewById(R.id.id_loc_evento);
        this.data = (TextView) findViewById(R.id.id_data);
        this.hora = (TextView) findViewById(R.id.id_hora);
        this.qtdparticipantes = (TextView ) findViewById(R.id.id_qtd_participantes);
        this.descricao = (TextView) findViewById(R.id.id_descricao);
        this.btnParticipar = (Button) findViewById(R.id.btn_participar);
        this.btnVoltar = (Button) findViewById(R.id.btn_voltar);
        this.mToolbar = (Toolbar) findViewById(R.id.tb_evento);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("evento",evento);
    }
}
