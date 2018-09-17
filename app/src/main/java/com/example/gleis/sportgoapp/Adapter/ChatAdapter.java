package com.example.gleis.sportgoapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.gleis.sportgoapp.Entidades.ChatMessage;
import com.example.gleis.sportgoapp.Entidades.Usuario;
import com.example.gleis.sportgoapp.Preferencias.TinyDB;
import com.example.gleis.sportgoapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends FirebaseRecyclerAdapter<ChatMessage,ChatHolder> {

    private Context context;
    private ArrayList<ChatMessage> arrayListmessage;
    private int contmessages;
    private TinyDB tinyDB;

    public ChatAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatMessage model) {

        if (model.getUserName().equals(tinyDB.getObject("dadosUsuario",Usuario.class).getNome())) {
            holder.layoutDirecao.setGravity(Gravity.RIGHT);
            holder.layoutDirecao.setPadding(10, 5, LinearLayout.LayoutParams.WRAP_CONTENT, 5);
            holder.layoutBalao.setBackgroundResource(R.drawable.drawable_rigth_layout);
            holder.imgUser.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.GONE);
            holder.tvMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.tvHourmessage.setText(model.getHourMessage());
            holder.tvMessage.setText(model.getMessage());
        } else {
            holder.imgUser.setVisibility(View.VISIBLE);
            holder.tvUserName.setVisibility(View.VISIBLE);
            holder.tvMessage.setTextColor(Color.parseColor("#FF484949"));
            holder.tvUserName.setText(model.getUserName()+"  ");
            Picasso.get().load(model.getUrlImageUser()).resize(100,100).centerCrop().into(holder.imgUser);
            holder.layoutDirecao.setGravity(Gravity.LEFT);
            holder.layoutDirecao.setPadding(10, 5, 10, 5);
            holder.layoutBalao.setBackgroundResource(R.drawable.drawable_left_layout);
            holder.tvHourmessage.setText(model.getHourMessage());
            holder.tvMessage.setText(model.getMessage());
        }

    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.balao_esquerdo, parent, false);
        return new ChatHolder(view);

    }

    public Integer lastPos(){
        return getItemCount();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
