package com.example.gleis.sportgoapp.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gleis.sportgoapp.Adapter.ChatAdapter;
import com.example.gleis.sportgoapp.Adapter.ChatHolder;
import com.example.gleis.sportgoapp.Adapter.ChatHolderDireito;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.ChatMessage;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.security.auth.callback.CallbackHandler;

public class ChatActivity extends AppCompatActivity {

    private EditText editMessage;
    private TextView statusMessage;
    private LinearLayout statusBalao;
    private FloatingActionButton fbSendMessage;
    // instancia do banco de dados
    private TinyDB tinyDB;
    private Usuario usuariologado;
    private Evento evento;
    private RecyclerView rChat;
    private ChatMessage message;
    private ArrayList<ChatMessage> arraymessages = new ArrayList<>();
    private ChatAdapter adapter;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intiViews();
        setSupportActionBar(toolbar);
        // pega os dados do usuario logado
        usuariologado = tinyDB.getObject("dadosUsuario",Usuario.class);
        evento = tinyDB.getObject("evento",Evento.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(evento.getTituloEvento());
        getSupportActionBar().setSubtitle(evento.getTipoEvento()+" "+evento.getHoraEvento());

        System.out.println(evento.getStatusEvento().toString());

        /*if (evento.getStatusEvento().getTipo().equals("Cancelado")) {
            statusBalao.setVisibility(View.VISIBLE);
            statusMessage.setText("\""+evento.getStatusEvento().getMotivoDescricao()+"\"");
        }*/

        System.out.println("evento :"+ evento.getTituloEvento());

        // recupera lista de mensagens ;
        recuperaDados();
        // posiciona chat no final
        fbSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editMessage.getText().toString().isEmpty()){
                    FirebaseDatabase.getInstance() // aqui esta sendo usada a classe direta do firabase
                            .getReference()
                                .child("eventos")
                                    .child(evento.getIdEvento())
                                        .child("mensagens")
                                        .push()
                                        .setValue(new ChatMessage(usuariologado.getUrlImagem(),usuariologado.getNome(),editMessage.getText().toString()) // cria a mensagem
                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                         rChat.scrollToPosition(adapter.lastPos()-1);
                        }
                    });
                    // apaga mensagem apos enviar
                    rChat.scrollToPosition(adapter.lastPos()-1);
                    editMessage.setText("");
                }
            }
        });

        for (ChatMessage message: arraymessages){
            System.out.println(message.toString());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent it = new Intent(ChatActivity.this,MenuActivity.class);
                startActivity(it);
                finish();//Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void recuperaDados() {

        // cria o tipo de consulta e onde deve consultar .limite de resultado de 50
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("eventos")
                    .child(evento.getIdEvento())
                        .child("mensagens");

        // cria o tipo de ouvinte
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addChildEventListener(childEventListener);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    message = postSnapshot.getValue(ChatMessage.class);
                    arraymessages.add(message);
                    //arraymessages.add(postSnapshot.getValue(ChatMessage.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // configuração do adapater  recebe a que a ser utilizada para a pesquisa
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();

        // cria adapter
        adapter = new ChatAdapter(options,this);
        if(rChat.isComputingLayout()){
            rChat.scrollToPosition(adapter.lastPos()-1);
        }

        adapter.startListening();
        rChat.setAdapter(adapter);

    }

    private void intiViews() {

        // otimizar recyclerview não mudar o tamanho
        rChat = (RecyclerView) findViewById(R.id.list_of_messages);
        rChat.setHasFixedSize(true);
        //criando layout para o fragment
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(false);
        llm.setStackFromEnd(true);// setando layout no fragment
        rChat.setLayoutManager(llm);

        // tolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        editMessage = (EditText) findViewById(R.id.input);
        statusMessage = (TextView) findViewById(R.id.id_menssagem_status);
        statusBalao = (LinearLayout) findViewById(R.id.id_status_balao);
        fbSendMessage = (FloatingActionButton) findViewById(R.id.fabSend);
        tinyDB = new TinyDB(this);
    }
}
