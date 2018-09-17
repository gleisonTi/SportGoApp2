package com.example.gleis.sportgoapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gleis.sportgoapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHolder extends RecyclerView.ViewHolder {

    public CircleImageView imgUser;
    public TextView tvUserName;
    public TextView tvHourmessage;
    public TextView tvMessage;
    public LinearLayout layoutDirecao;
    public LinearLayout layoutBalao;

    public ChatHolder(View itemView) {
        super(itemView);

        imgUser = (CircleImageView) itemView.findViewById(R.id.id_imagem_chat);
        tvUserName = (TextView) itemView.findViewById(R.id.id_nome_chat);
        tvHourmessage = (TextView) itemView.findViewById(R.id.data_hora_message);
        tvMessage = (TextView) itemView.findViewById(R.id.id_mensagem);
        layoutBalao = (LinearLayout) itemView.findViewById(R.id.id_balao);
        layoutDirecao = (LinearLayout) itemView.findViewById(R.id.id_direcao);
    }
}
