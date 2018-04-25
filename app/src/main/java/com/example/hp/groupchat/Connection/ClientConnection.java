package com.example.hp.groupchat.Connection;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.hp.groupchat.MainActivity;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends AsyncTask<PackData, PackData, PackData> {
    private String HOST;
    private int PUERTO;
    private MainActivity main;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ClientReceiver clientReceiver;
    private ClientSender clientSender;
    private boolean statusConnection;

    public ClientConnection(String HOST, int PUERTO, MainActivity main) {
        this.HOST = HOST;
        this.PUERTO = PUERTO;
        this.main = main;

    }

    @Override
    protected void onProgressUpdate(PackData... values) {

        PackData mensaje = values[0];
        mensaje.setPos('R');
        main.addNewMsg(mensaje);

    }

    @Override
    protected PackData doInBackground(PackData... params) {
        PackData conexion = null;
        try {
            socket = new Socket(HOST, PUERTO);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
             inputStream= new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject(params[0]);
            conexion = (PackData) inputStream.readObject();
            publishProgress(conexion);
            statusConnection = true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conexion;
    }

    @Override
    protected void onPostExecute(PackData s) {
        Log.e("Conectando paquetes", "Ready");
        clientReceiver = new ClientReceiver(inputStream, main);
        clientReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new PackData(main.getNombre(), KeyWordSystem.Only_Text,"Ok"));

        clientSender = new ClientSender(outputStream, main);
        clientSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new PackData(main.getNombre(),KeyWordSystem.Only_Text,"Ok"));


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
