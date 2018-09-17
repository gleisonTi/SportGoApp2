package com.example.gleis.sportgoapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Helper.Base64Custom;
import com.example.gleis.sportgoapp.Helper.Preferencias;
import com.example.gleis.sportgoapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroActivity extends AppCompatActivity {

    public CircleImageView imgPerfil;

    public EditText edtNome;
    public EditText edtIdade;
    public EditText edtEsporte;
    public EditText edtEstado;
    public EditText edtCidade;

    public RadioButton rbMasculino;
    public RadioButton rbFeminino;

    public Button btnCadastrar;

    private Usuario usuario = new Usuario();

    private FirebaseAuth autenticacao;

    private StorageReference storageReference;

    private final int GALERIA_IMAGENS = 1;

    private final int PERMISSAO_REQUEST = 2;

    private ProgressDialog prdUpload;

    private Uri salvaimagem;
    private String urlImagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //Desloga Usuario
        FirebaseAuth auth = ConfiguraFirebase.getAutenticacao();
        auth.signOut();
        //associação do layout nas variaveis
        associacaoCadastro();

        storageReference = ConfiguraFirebase.getStorage();
        // teste de permissão da galeria
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_REQUEST);
            }
        }
        //escolher imagem
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALERIA_IMAGENS);
            }
        });
        // cadastrar dados
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // teste se há conexão
                if (isOnline()) {

                    prdUpload.setMessage("Salvando Dados ...");
                    prdUpload.show();
                    Intent it = getIntent();
                    Bundle bundle = it.getExtras();

                    String sexo = "";

                    if (rbFeminino.isChecked()) {
                        sexo = "Feminino";
                    } else {
                        sexo = "Masculino";
                    }

                    String nome = edtNome.getText().toString();
                    String idade = edtIdade.getText().toString();
                    String esporte = edtEsporte.getText().toString();
                    String estado = edtEstado.getText().toString();
                    String cidade = edtCidade.getText().toString();
                    String email = bundle.getString("email");
                    String senha = bundle.getString("senha");


                    usuario.setNome(nome);
                    usuario.setIdade(idade);
                    usuario.setEsporte(esporte);
                    usuario.setEstado(estado);
                    usuario.setCidade(cidade);
                    usuario.setSexo(sexo);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    //salva a imagem e os dados do usuario
                    salvarDados(salvaimagem);

                    alert("Cadastrando dados...");
                } else {
                    alert("Sem conexão no momento :(");
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERIA_IMAGENS && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            //salvando imagem em uma variavel
            this.salvaimagem = selectedImage;

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imgPerfil.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    private void salvarDados(Uri selectedImage) {

        if (selectedImage != null) {

            // pega o nome do arquivo para ser salvo
            String filename = new File(selectedImage.getPath()).getName();

            final StorageReference filepath = storageReference.child("ImagensUsuarios").child(filename);

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
                        //salvando url da imagem na classe usuarios'
                        urlImagem = downloadUri.toString();
                        usuario.setUrlImagem(urlImagem);
                        //Picasso.get().load(downloadUri).fit().centerCrop().into(imgPerfil);
                        //Cria usuario e salva os dados
                        cadastrarUsuario();

                    } else {
                        Toast.makeText(getApplicationContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            prdUpload.dismiss();
            alert("E necessario a escolha de uma imagem para identificação");
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }

    private void cadastrarUsuario() {
        // Criando usuario no firebase
        autenticacao = ConfiguraFirebase.getAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    //FirebaseUser usuarioFirebase = task.getResult().getUser();

                    usuario.setId(identificadorUsuario);

                    System.out.println("Mostrar Usuario: " + usuario);
                    usuario.salvarFirebase();


                    Preferencias preferenciasAndroid = new Preferencias(CadastroActivity.this);

                    preferenciasAndroid.salvarUsuarioPreferencias(identificadorUsuario, usuario.getNome());

                    alert("Usuario Cadastrado com sucesso");
                    prdUpload.dismiss();
                    Intent it = new Intent(CadastroActivity.this, MenuActivity.class);
                    startActivity(it);
                    finish();

                } else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        alert("Digite uma senha mais forte, ontendo no minimo 6 caracteres");

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        alert("O e-mail digitado e inválido, digite um novo e-mail");


                    } catch (FirebaseAuthUserCollisionException e) {
                        alert("Esse email já esta sendo utilizado ");


                    } catch (Exception e) {
                        alert("Erro ao efetuar o Cadastro!");
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void alert(String txt) {
        Toast.makeText(CadastroActivity.this, txt, Toast.LENGTH_SHORT).show();
    }


    private void associacaoCadastro() {
        this.imgPerfil = (CircleImageView) findViewById(R.id.id_imagem_perfil);

        this.edtNome = (EditText) findViewById(R.id.id_cadastro_nome);
        this.edtIdade = (EditText) findViewById(R.id.id_cadastro_idade);
        this.edtEsporte = (EditText) findViewById(R.id.id_cadastro_esporte);
        this.edtEstado = (EditText) findViewById(R.id.id_cadastro_estado);
        this.edtCidade = (EditText) findViewById(R.id.id_cadastro_cidade);

        this.rbFeminino = (RadioButton) findViewById(R.id.id_sexo_feminino);
        this.rbMasculino = (RadioButton) findViewById(R.id.id_sexo_masculino);

        this.btnCadastrar = (Button) findViewById(R.id.id_btn_cadastrar);

        this.prdUpload = new ProgressDialog(this);

    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}

