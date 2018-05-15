package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class ImagemEventoActivity extends AppCompatActivity {

    public ImageView imgEvento;
    public Button btnCriarEvento;
    public Button btnVoltarImg;
    private  final  int GALERIA_IMAGENS = 1;
    private Uri salvaimagem;
    private StorageReference storageReference;
    private String urlImagem;
    private ProgressDialog prdUpload;
    private Evento evento = new Evento();
    private  ProgressDialog progressDialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem_evento);
        associaVariaveis();

        storageReference = ConfiguraFirebase.getStorage();
        progressDialog = new ProgressDialog(ImagemEventoActivity.this);

        imgEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,GALERIA_IMAGENS);
            }
        });

        btnVoltarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itVoltar = new Intent(ImagemEventoActivity.this,LocalMapaActivity.class);
                startActivity(itVoltar);
                finish();
            }
        });
        btnCriarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prdUpload.setMessage("Salvando dados");
                prdUpload.show();
                //recebendo todos os dados para cadastro do eventos
                Intent itendereco = getIntent();
                Bundle bundle = itendereco.getExtras();

                // recebendo dados da activity CriarEventoActivity;
                // salvar imagem
                String titulo = bundle.getString("titulo");
                String tipo = bundle.getString("tipo");
                String qtdEvento =  bundle.getString("qtdEvento");
                String data = bundle.getString("data");
                String hora = bundle.getString("hora");
                String descricao = bundle.getString("descricao");
                Double latitude = bundle.getDouble("latitude");
                Double longitude = bundle.getDouble("longitude");
                String endereco = bundle.getString("endereco");

                // ! criar validações com if;

                evento.setTituloEvento(titulo);
                evento.setTipoEvento(tipo);
                evento.setQtdParticipante(Integer.parseInt(qtdEvento));
                evento.setDataEvento(data);
                evento.setHoraEvento(hora);
                evento.setDescricaoEvento(descricao);
                evento.setEnderecolat(latitude);
                evento.setEnderecolng(longitude);
                evento.setEndereco(endereco);

                criarEvento();




            }
        });

    }

    private void criarEvento() {

        user = ConfiguraFirebase.getAutenticacao().getCurrentUser();
        if(user != null){
            //pegar data
            Calendar datac = Calendar.getInstance();
            int dia = datac.get(Calendar.DAY_OF_MONTH);
            int mes = datac.get(Calendar.MONTH);
            int ano = datac.get(Calendar.YEAR);
            int hora = datac.get(Calendar.HOUR_OF_DAY);
            int min = datac.get(Calendar.MINUTE);
            int seg = datac.get(Calendar.SECOND);

            String dataAtual = dia +"/"+ mes +"/"+ ano +","+hora +":"+min+":"+seg;
            Log.d("dataat","data "+ dataAtual);
            // pegar id do uduario criador do evento
            String dataAtual64 = Base64Custom.codificarBase64(dataAtual);

            Log.d("dataat64","data "+ dataAtual64);

            String idUsuario = Base64Custom.codificarBase64(user.getEmail());

            evento.setIdEvento(idUsuario+dataAtual64);
            evento.setIdUsuario(idUsuario);

            salvarDados(salvaimagem);


        }else{
        alert("Usuario não logado");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERIA_IMAGENS && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            //salvando imagem em uma variavel
            this.salvaimagem = selectedImage;

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imgEvento.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgEvento.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    private void salvarDados(Uri selectedImage) {

        StorageReference filepath = this.storageReference.child("ImagensEventos").child(selectedImage.getLastPathSegment());

        filepath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Uri downloadUri = taskSnapshot.getDownloadUrl();

                //salvando url da imagem na classe usuarios'
                urlImagem = downloadUri.toString();
                evento.setImagemEvento(urlImagem);

                //Picasso.get().load(downloadUri).fit().centerCrop().into(imgPerfil);

                //Cria Evento e salva os dados
                evento.salvarFirebaseEvento();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                prdUpload.setMessage("Criando evento...");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                prdUpload.dismiss();
                alert("Evento criado com sucesso");
                prdUpload.dismiss();

                Intent it = new Intent(ImagemEventoActivity.this, MenuActivity.class);
                startActivity(it);
                finish();

            }
        });
    }

    private void alert(String s) {
        Toast.makeText(ImagemEventoActivity.this,s,Toast.LENGTH_LONG).show();
    }

    private void associaVariaveis() {
        imgEvento = (ImageView) findViewById(R.id.id_imagem_evento);
        btnCriarEvento = (Button) findViewById(R.id.id_btn_criar_evento);
        btnVoltarImg = (Button) findViewById(R.id.id_voltar_img);

        this.prdUpload = new ProgressDialog(this);

    }
}
