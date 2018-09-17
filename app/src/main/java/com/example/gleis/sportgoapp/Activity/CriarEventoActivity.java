package com.example.gleis.sportgoapp.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CriarEventoActivity extends AppCompatActivity {

    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    Calendar dateTime = Calendar.getInstance();
    private TextView tvData;
    private TextView tvHora;
    private EditText tituloEvento;
    private EditText tipoEvento;
    private EditText qtdParticipante;
    private EditText descricaoEvento;

    private ImageView imgData;
    private ImageView imgHora;
    private Button btnVoltar;
    private Button btnProximo;
    private TinyDB tinyDB;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);
        // assiciar layout nas variveis
        associaVariaveis();

        // criação do evento
        evento = new Evento();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnline()){
                    // Envio de dados para activity LocalMapaActivity
                    Intent it = new Intent(CriarEventoActivity.this, LocalMapaActivity.class);
                    Bundle bundle = new Bundle();

                    // passando dados para o objeto evento
                    evento.setTituloEvento(tituloEvento.getText().toString());
                    evento.setTipoEvento(tipoEvento.getText().toString());
                    evento.setQtdParticipante(Integer.parseInt(qtdParticipante.getText().toString()));
                    evento.setDescricaoEvento(descricaoEvento.getText().toString());
                    evento.setDataEvento(tvData.getText().toString());
                    evento.setHoraEvento(tvHora.getText().toString());

                    // salvando evento na memoria
                    tinyDB.putObject("evento",evento);

                    startActivity(it);

                    alert("Salvando dados...");

                    finish();
                }

            }
        });

        imgData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });
        imgHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTime();

            }
        });

    }

    private void alert(String texto) {
        Toast.makeText(CriarEventoActivity.this,texto,Toast.LENGTH_LONG).show();
    }

    private void associaVariaveis() {

        tinyDB = new TinyDB(this);
        this.tituloEvento = (EditText) findViewById(R.id.id_titulo_evento);
        this.tipoEvento = (EditText) findViewById(R.id.id_tipo_evento);
        this.qtdParticipante = (EditText) findViewById(R.id.id_qtd_evento);
        this.descricaoEvento = (EditText) findViewById(R.id.id_descricao);
        this.tvData = (TextView) findViewById(R.id.id_tv_data);
        this.tvHora = (TextView) findViewById(R.id.id_tv_hora);

        this.imgData =(ImageView) findViewById(R.id.img_data);
        this.imgHora =(ImageView) findViewById(R.id.img_hora);
        this.btnVoltar = (Button) findViewById(R.id.id_btn_voltar);
        this.btnProximo = (Button) findViewById(R.id.id_btn_proximo);

    }

    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime(){
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabelDate();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            updateTextLabelHora();
        }
    };

    private void updateTextLabelDate(){
        tvData.setText(dataFormat.format(dateTime.getTime()));
    }
    private void updateTextLabelHora(){
        tvHora.setText(timeFormat.format(dateTime.getTime()));
    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
