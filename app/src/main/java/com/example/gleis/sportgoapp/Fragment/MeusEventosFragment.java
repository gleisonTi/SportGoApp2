package com.example.gleis.sportgoapp.Fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gleis.sportgoapp.Activity.EventoActivity;
import com.example.gleis.sportgoapp.Adapter.EventosAdapter;
import com.example.gleis.sportgoapp.Adapter.MeusEventosAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Interfaces.RecyclerViewOnClickListenerHack;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeusEventosFragment extends Fragment implements RecyclerViewOnClickListenerHack {

    private RecyclerView recyclerViewEventos;

    private MeusEventosAdapter adapter;

    private List<Evento> listaEvento;
    private TinyDB tinyDB;
    private DatabaseReference referenceFirebase;
    private Evento todosEventos;


    public MeusEventosFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sem_conexao, container, false);
        if(isOnline()){
            // pegar dados do Usuario logado
            tinyDB = new TinyDB(getContext());
            // inflar o layout para este fragmento
            view = inflater.inflate(R.layout.fragment_participando, container, false);
            // criar um view quando o usuario não estiver participando de nehum evento
            recyclerViewEventos = (RecyclerView) view.findViewById(R.id.id_recycler_view_participando);
            //metodo para carregar dados no final da lista !!! metodo antigo
            recyclerViewEventos.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            // otimizar recyclerview não mudar o tamanho.
            recyclerViewEventos.setHasFixedSize(true);
            //criando layout para o fragment
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            llm.setReverseLayout(false);
            // setando layout no fragment
            recyclerViewEventos.setLayoutManager(llm);

            listaEvento =  new ArrayList<>();

            referenceFirebase = ConfiguraFirebase.getFirebase();

            // pegar referencia do usuario depois dos eventos que ele paricipa
            referenceFirebase.child("usuarios")
                    .child(tinyDB.getObject("dadosUsuario", Usuario.class).getId()) // id do Usuario no firebase
                    .child("eventosAssociados").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // no com os ids dos eventos em que o usuario participa
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        // pega ids dos eventos  em que o cliente participa
                        String idEvento = postSnapshot.getValue(String.class);
                        System.out.println("idEvento: "+idEvento);
                        // recupera eventos dos usuarios
                        referenceFirebase.child("eventos").child(idEvento).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    todosEventos = dataSnapshot.getValue(Evento.class);

                                    listaEvento.add(todosEventos);

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /*referenceFirebase.child("eventos").orderByChild("idEvento").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                        todosEventos = postSnapshot.getValue(Evento.class);

                        listaEvento.add(todosEventos);

                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
            adapter = new MeusEventosAdapter(listaEvento,getActivity());
            adapter.setRecyclerViewOnClickListenerHack(this);
            // adapter
            recyclerViewEventos.setAdapter(adapter);
            return  view;
        }

        return view;
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent it = new Intent(getActivity(), EventoActivity.class);
        it.putExtra("evento",listaEvento.get(position));
        startActivity(it);
    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
