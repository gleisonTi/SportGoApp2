package com.example.gleis.sportgoapp.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriarEventoActivity extends AppCompatActivity {

    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    Calendar dateTime = Calendar.getInstance();
    private TextView tvData;
    private TextView tvHora;
    private EditText tituloEvento;
    private EditText descricaoEvento;
    private Spinner esporte;
    private Spinner qtdParticipantes;

    private ImageView imgData;
    private ImageView imgHora;
    private Button btnVoltar;
    private Button btnProximo;
    private TinyDB tinyDB;
    private Evento evento;
    private Evento eventoEdit;
    private  String tipoEsporte;
    private String quantidade;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);
        // assiciar layout nas variveis
        associaVariaveis();


        // criação do evento
        evento = new Evento();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.remove("flagDeEdicao");
                finish();
            }
        });


        final List<String> listaEsporte = new ArrayList<>(Arrays.asList("Futebol","Basquete","Ciclismo","Volêi","Natação","Tênis","Corrida","Skate","Futsal","Outros"));
        esporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoEsporte = listaEsporte.get(position) ;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(CriarEventoActivity.this, android.R.layout.simple_spinner_dropdown_item, listaEsporte);
        esporte.setDropDownHorizontalOffset(100);
        esporte.setAdapter(spin_adapter);


        final List<String> listanumeros = new ArrayList<>();

        int cont = 1;
        for(int i = 1; i <101;i++ ){
            listanumeros.add(String.valueOf(cont++));
        }

        qtdParticipantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantidade = listanumeros.get(position) ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spin_adapter_qtd = new ArrayAdapter<String>(CriarEventoActivity.this, android.R.layout.simple_spinner_dropdown_item, listanumeros);
        qtdParticipantes.setDropDownHorizontalOffset(10);
        qtdParticipantes.setAdapter(spin_adapter_qtd);

        if (tinyDB.getBoolean("flagDeEdicao")) {
            btnProximo.setText("Salvar Alterações");
            btnVoltar.setVisibility(View.GONE);
            eventoEdit = tinyDB.getObject("eventoEdit", Evento.class);
            tituloEvento.setText(eventoEdit.getTituloEvento());
            descricaoEvento.setText(eventoEdit.getDescricaoEvento());
            tvData.setText(eventoEdit.getDataEvento());
            tvHora.setText(eventoEdit.getHoraEvento());

            ArrayAdapter myAdapEsporte = (ArrayAdapter) esporte.getAdapter();
            System.out.println("Tipo :"+eventoEdit.getTipoEvento()+" qtd"+ eventoEdit.getQtdParticipante());
            int positionEsporte  = myAdapEsporte.getPosition(eventoEdit.getTipoEvento());
            esporte.setSelection(positionEsporte);

            ArrayAdapter myAdapQtd = (ArrayAdapter) qtdParticipantes.getAdapter();
            int positionqtd  = myAdapQtd.getPosition(String.valueOf(eventoEdit.getQtdParticipante()));
            qtdParticipantes.setSelection(positionqtd);
        } else {
            btnProximo.setText("Proximo");
            btnVoltar.setVisibility(View.VISIBLE);
        }



        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    if (!tituloEvento.getText().toString().isEmpty() //  testa se algum campo esta vazio
                            && !tipoEsporte.isEmpty()
                            && !quantidade.isEmpty()
                            && !descricaoEvento.getText().toString().isEmpty()
                            && !tvHora.getText().equals("Hora")
                            && !tvData.getText().equals("Data") ) {
                        // Envio de dados para activity LocalMapaActivity
                        Intent it = new Intent(CriarEventoActivity.this, LocalMapaActivity.class);
                        // passando dados para o objeto evento
                        evento.setTituloEvento(tituloEvento.getText().toString());
                        evento.setTipoEvento(tipoEsporte);
                        evento.setQtdParticipante(Integer.parseInt(quantidade));
                        evento.setDescricaoEvento(descricaoEvento.getText().toString());
                        evento.setDataEvento(tvData.getText().toString());
                        evento.setHoraEvento(tvHora.getText().toString());

                        if (tinyDB.getBoolean("flagDeEdicao")) {

                            new AlertDialog.Builder(CriarEventoActivity.this)
                                    .setTitle("Edição de Evento")
                                    .setMessage("deseja editar o evento " + eventoEdit.getTituloEvento() + "?")
                                    .setPositiveButton("sim",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // objeto utilizado atualizar os dados
                                                    Map<String, Object> taskMap = new HashMap<String, Object>();
                                                    // add hash map nesse formato
                                                    taskMap.put("tipoEvento", tipoEsporte);
                                                    taskMap.put("tituloEvento", tituloEvento.getText().toString());
                                                    taskMap.put("qtdParticipante", Integer.parseInt(quantidade));
                                                    taskMap.put("horaEvento", tvHora.getText().toString());
                                                    taskMap.put("descricaoEvento", descricaoEvento.getText().toString());
                                                    taskMap.put("dataEvento", tvData.getText().toString());
                                                    //salva edições do evento
                                                    eventoEdit.atualizaFirebaseEvento(taskMap);
                                                    alert("Foram salvas alterações no evento " + eventoEdit.getTituloEvento());
                                                    tinyDB.remove("flagDeEdicao");
                                                    Intent it = new Intent(CriarEventoActivity.this, MenuActivity.class);
                                                    startActivity(it);
                                                    finish();
                                                }
                                            })
                                    .setNegativeButton("não", null)
                                    .show();

                        } else {
                            // salvando evento na memoria
                            tinyDB.putObject("evento", evento);
                            startActivity(it);
                            alert("Salvando dados...");
                            finish();
                        }

                    }else {
                        alert("Preencha todos os campos");
                    }
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
        Toast.makeText(CriarEventoActivity.this, texto, Toast.LENGTH_LONG).show();
    }

    private void associaVariaveis() {

        tinyDB = new TinyDB(this);
        this.tituloEvento = (EditText) findViewById(R.id.id_titulo_evento);
        this.descricaoEvento = (EditText) findViewById(R.id.id_descricao);
        this.tvData = (TextView) findViewById(R.id.id_tv_data);
        this.tvHora = (TextView) findViewById(R.id.id_tv_hora);

        this.imgData = (ImageView) findViewById(R.id.img_data);
        this.imgHora = (ImageView) findViewById(R.id.img_hora);
        this.btnVoltar = (Button) findViewById(R.id.id_btn_voltar);
        this.btnProximo = (Button) findViewById(R.id.id_btn_proximo);
        this.qtdParticipantes = (Spinner) findViewById(R.id.id_spiner_qtd);
        this.esporte = (Spinner) findViewById(R.id.id_spiner_esporte);

    }

    private void updateDate() {
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime() {
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

    private void updateTextLabelDate() {
        tvData.setText(dataFormat.format(dateTime.getTime()));
    }

    private void updateTextLabelHora() {
        tvHora.setText(timeFormat.format(dateTime.getTime()));
    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (tinyDB.getBoolean("flagDeEdicao")) {
            Intent it = new Intent(CriarEventoActivity.this, MenuActivity.class);
            startActivity(it);
        }
        tinyDB.remove("flagDeEdicao");
        finish();
    }
}
