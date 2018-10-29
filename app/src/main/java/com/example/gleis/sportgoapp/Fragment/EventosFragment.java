package com.example.gleis.sportgoapp.Fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gleis.sportgoapp.Activity.EventoActivity;
import com.example.gleis.sportgoapp.Adapter.EventosAdapter;
import com.example.gleis.sportgoapp.Dao.ConfiguraFirebase;
import com.example.gleis.sportgoapp.Entidades.Evento;
import com.example.gleis.sportgoapp.Interfaces.RecyclerViewOnClickListenerHack;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewEventos;
    private LinearLayout lnSemEvento;
    private LinearLayout lncarregando;
    private EventosAdapter adapter;
    private TinyDB tinyDB;
    private List<Evento> listaEvento;
    private DatabaseReference referenceFirebase;
    private Evento todosEventos;
    List<Evento> eventosAtalizados;
    private SwipeRefreshLayout swipRefresh;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public EventosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sem_conexao, container, false);
        if (isOnline()) {
            // inflar o layout para este fragmento
            view = inflater.inflate(R.layout.fragment_eventos, container, false);
            recyclerViewEventos = (RecyclerView) view.findViewById(R.id.id_recycler_view_eventos);
            swipRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipRefresh.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);

            swipRefresh.setOnRefreshListener(this);
            swipRefresh.post(new Runnable() {

                @Override
                public void run() {

                    swipRefresh.setRefreshing(true);

                    // Fetching data from server
                    loadRecyclerViewData();
                }
            });

            lnSemEvento = (LinearLayout) view.findViewById(R.id.id_semEvento);
            lncarregando = (LinearLayout) view.findViewById(R.id.id_carregando);
            tinyDB = new TinyDB(getContext());

            lnSemEvento.setVisibility(View.GONE);

            // otimizar recyclerview não mudar o tamanho.
            recyclerViewEventos.setHasFixedSize(true);
            //criando layout para o fragment
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            llm.setReverseLayout(true);
            llm.setStackFromEnd(true);
            // setando layout no fragment
            recyclerViewEventos.setLayoutManager(llm);

            listaEvento = new ArrayList<>();
            eventosAtalizados = new ArrayList<>();
            referenceFirebase = ConfiguraFirebase.getFirebase();
            carregaDadosEvento();


            return view;
        }

        return view;

    }

    private void carregaDadosEvento() {
        referenceFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("eventos")) {// primeiro testa se a eventos no banco
                    lncarregando.setVisibility(View.GONE);
                    lnSemEvento.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        atulizaDadosEvento();
    }

    private void atulizaDadosEvento() {
        listaEvento.clear();
        referenceFirebase.child("eventos").orderByChild("idEvento").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            lncarregando.setVisibility(View.GONE);
                            todosEventos = snapshot.getValue(Evento.class);
                            if (todosEventos.getStatusEvento().getTipo().equals("Ativo")) {
                                listaEvento.add(todosEventos);
                                lnSemEvento.setVisibility(View.GONE);
                            }
                            if (listaEvento.isEmpty()) {
                                lnSemEvento.setVisibility(View.VISIBLE);
                                lncarregando.setVisibility(View.GONE);
                            }
                        } else {
                            lnSemEvento.setVisibility(View.VISIBLE);
                            lncarregando.setVisibility(View.GONE);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter = new EventosAdapter(listaEvento, getActivity());
        recyclerViewEventos.setAdapter(adapter);

    }

    private void loadRecyclerViewData() {
        swipRefresh.setRefreshing(true);
        atulizaDadosEvento();
        adapter.notifyDataSetChanged();
        swipRefresh.setRefreshing(false);
    }

    @Override
    public void onRefresh() { // metodo de refresf da pagina
        loadRecyclerViewData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        System.out.println("OK 1");

        inflater.inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (search != null) {
            searchView = (SearchView) search.getActionView();
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter != null) {
                        // ver o como melhorar a pesquisa
                        System.out.println("size: " + adapter.getItemCount());
                        if (adapter.getItemCount() == 0) {
                            lnSemEvento.setVisibility(View.VISIBLE);
                            recyclerViewEventos.setVisibility(View.GONE);
                        } else {
                            lnSemEvento.setVisibility(View.GONE);
                            recyclerViewEventos.setVisibility(View.VISIBLE);
                        }
                        adapter.getFilter().filter(newText);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnline()) {
            adapter.notifyDataSetChanged();
        }
    }


    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
