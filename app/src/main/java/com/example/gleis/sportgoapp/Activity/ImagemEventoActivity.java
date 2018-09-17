package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImagemEventoActivity extends AppCompatActivity {

    public CircleImageView imgEvento;
    public Button btnCriarEvento;
    public Button btnVoltarImg;
    private  final  int GALERIA_IMAGENS = 1;
    private  final int PERMISSAO_REQUEST = 2;
    private Uri salvaimagem;
    private StorageReference storageReference;
    private String urlImagem;
    private ProgressDialog prdUpload;
    private Evento evento = new Evento();
    private Usuario usuario;
    private  ProgressDialog progressDialog;
    private TinyDB tinyDB;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem_evento);
        associaVariaveis();
        // teste de permissão da galeria
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else{
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSAO_REQUEST);
            }
        }

        evento = tinyDB.getObject("evento",Evento.class);
        usuario = tinyDB.getObject("dadosUsuario",Usuario.class);


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
            // pegar id do usuario criador do evento
            String dataAtual64 = Base64Custom.codificarBase64(dataAtual);

            Log.d("dataat64","data "+ dataAtual64);

            String idUsuario = Base64Custom.codificarBase64(user.getEmail());

            evento.setIdEvento(idUsuario+dataAtual64);
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

            String filename = new File(selectedImage.getPath()).getName();

            System.out.println( "lastaps---> "+filename);
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
        // teste para imagem nula se não houver imagem
        // devera ser aplicado o Tinidb para caregamento de dados dos usuarios
        if(selectedImage != null) {

            // pega o nome do arquivo para ser salvo
            String filename = new File(selectedImage.getPath()).getName();

            final StorageReference filepath = this.storageReference.child("ImagensEventos").child(filename);

            filepath.putFile(selectedImage).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = 100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    System.out.println("Upload is " + progress + "% done");
                    int currentprogress = (int) progress;
                    prdUpload.setProgress(currentprogress);
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        System.out.println("url Image:"+downloadUri.toString());

                        evento.setImagemEvento(downloadUri.toString());
                        evento.setUsuarioCriador(usuario);
                        evento.salvarFirebaseEvento();

                        System.out.println(evento.toString());

                    } else {
                        Toast.makeText(getApplicationContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    alert("Evento criado com sucesso");
                    prdUpload.dismiss();
                    Intent it = new Intent(ImagemEventoActivity.this, MenuActivity.class);
                    startActivity(it);
                    finish();
                }
            });


        }else{
            prdUpload.dismiss();
            alert("E necessario a escolha de uma imagem para o evento");
        }
    }

    private void alert(String s) {
        Toast.makeText(ImagemEventoActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if(requestCode== PERMISSAO_REQUEST) {
            if(grantResults.length> 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else{
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }return;
        }
    }

    private void associaVariaveis() {
        // inicia shared preferences

        tinyDB =  new TinyDB(this);

        imgEvento = (CircleImageView) findViewById(R.id.id_imagem_evento);
        btnCriarEvento = (Button) findViewById(R.id.id_btn_criar_evento);
        btnVoltarImg = (Button) findViewById(R.id.id_voltar_img);

        this.prdUpload = new ProgressDialog(this);

    }
}
