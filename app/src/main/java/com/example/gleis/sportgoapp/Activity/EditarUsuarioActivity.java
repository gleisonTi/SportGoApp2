package com.example.gleis.sportgoapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarUsuarioActivity extends AppCompatActivity {

    public CircleImageView imgPerfil;

    public EditText edtNome;
    public EditText edtIdade;
    public EditText edtEsporte;
    public EditText edtEstado;
    public EditText edtCidade;

    public RadioButton rbMasculino;
    public RadioButton rbFeminino;

    public Button btnSalvar;
    public Button btnCancelar;

    public TinyDB tinyDB;
    public  Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);
        initVariaveis();

        Object dadosUsuario = tinyDB.getObject("dadosUsuario", Usuario.class);
        usuario = (Usuario) dadosUsuario;

        edtNome.setText(usuario.getNome());
        edtIdade.setText(usuario.getIdade());
        edtEsporte.setText(usuario.getEsporte());
        edtEstado.setText(usuario.getEstado());
        edtCidade.setText(usuario.getCidade());

        if(usuario.getSexo().contains("Masculino")){
            rbMasculino.setChecked(true);
        }else{
            rbFeminino.setChecked(true);
        }
    }

    private void initVariaveis() {

        tinyDB = new TinyDB(this);

        imgPerfil = (CircleImageView) findViewById(R.id.id_editar_imagem_perfil);

        edtNome = (EditText) findViewById(R.id.id_editar_nome);
        edtIdade = (EditText) findViewById(R.id.id_editar_idade);
        edtEsporte = (EditText) findViewById(R.id.id_editar_esporte);
        edtEstado = (EditText) findViewById(R.id.id_editar_estado);
        edtCidade = (EditText) findViewById(R.id.id_editar_cidade);

        rbFeminino = (RadioButton) findViewById(R.id.id_editar_sexo_feminino);
        rbMasculino = (RadioButton) findViewById(R.id.id_editar_sexo_masculino);

        btnSalvar = (Button) findViewById(R.id.id_btn_salvar);

        btnCancelar = (Button) findViewById(R.id.id_btn_cancelar);
    }
}
