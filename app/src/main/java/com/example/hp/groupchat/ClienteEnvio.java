package com.example.hp.groupchat;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Diego on 02/04/2017.
 */

public class ClienteEnvio extends AsyncTask<String,String,String> {

    Socket clientSocket;
    MainActivity main;
    DataOutputStream os;
    public ClienteEnvio(Socket clientSocket,MainActivity main){
        this.clientSocket = clientSocket;
        this.main = main;

    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.d("PreEnvio","data esperarando");
            os = new DataOutputStream(clientSocket.getOutputStream());
            Log.e("PreEnvio","paso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {

            Log.e("doInBackground",params[0]);
        Log.e("Mensaje","Inicio");
        while(true){
            try {
                System.out.println("ESPERANDO MENSAJE");
                String mensaje = main.bq.take();
                //main.bq.peek();
                os.writeUTF(mensaje);
                System.out.println("MENSAJE ENVIADO");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
