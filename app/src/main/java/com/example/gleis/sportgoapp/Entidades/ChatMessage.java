package com.example.gleis.sportgoapp.Entidades;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {

    private String urlImageUser;
    private String userName;
    private String message;
    private String hourMessage;
    private String email;

    public ChatMessage(String urlImageUser, String userName,String email, String message) {
        this.urlImageUser = urlImageUser;
        this.userName = userName;
        this.message = message;
        this.email = email;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date hora = Calendar.getInstance().getTime();
        this.hourMessage = sdf.format(hora);
    }

    public ChatMessage() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlImageUser() {
        return urlImageUser;
    }

    public void setUrlImageUser(String urlImageUser) {
        this.urlImageUser = urlImageUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHourMessage() {
        return hourMessage;
    }

    public void setHourMessage(String hourmessage) {
        this.hourMessage = hourmessage;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "urlImageUser='" + urlImageUser + '\'' +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", hourMessage='" + hourMessage + '\'' +
                '}';
    }
}
