package com.example.hp.groupchat.Connection;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.hp.groupchat.ClienteEnvio;
import com.example.hp.groupchat.ClienteRecepcion;
import com.example.hp.groupchat.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends AsyncTask<String, String, String> {
    private String HOST;
    private int PUERTO;
    private MainActivity main;
    private Socket clientSocket;
    private ClientReceiver clientReceiver;
    private ClientSender clientSender;
    private boolean statusConnection;

    public ClientConnection(String HOST, int PUERTO, MainActivity main) {
        this.HOST = HOST;
        this.PUERTO = PUERTO;
        this.main = main;

    }

    @Override
    protected void onProgressUpdate(String... values) {

        MainActivity.Mensaje mensaje = new MainActivity.Mensaje( values[0], 'R');
        main.addNewMsg(mensaje);

    }

    @Override
    protected String doInBackground(String... params) {
        String conexion = "";
        try {
            clientSocket = new Socket(HOST, PUERTO);
            DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream is = new DataInputStream(clientSocket.getInputStream());

            os.writeUTF(params[0]);
            conexion = is.readUTF();
            publishProgress(conexion);
            statusConnection = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conexion;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e("Conectando paquetes", "Ready");
        clientReceiver = new ClientReceiver(clientSocket, main);
        clientReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ok");
        clientSender = new ClientSender(clientSocket, main);
        clientSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Ok");

    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = main.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
