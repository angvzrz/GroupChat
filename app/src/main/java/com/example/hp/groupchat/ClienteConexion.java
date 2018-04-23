package com.example.hp.groupchat;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Diego on 02/04/2017.
 */

public class ClienteConexion extends AsyncTask<String, String, String> {

    private String HOST;
    private int PUERTO;
    private MainActivity main;
    private Socket clientSocket;
    private ClienteRecepcion recepcion;
    private ClienteEnvio envio;

    ClienteConexion(String HOST, int PUERTO, MainActivity main) {
        this.HOST = HOST;
        this.PUERTO = PUERTO;
        this.main = main;

    }

    @Override
    protected String doInBackground(String... params) {
        String conexion;
        try {
            clientSocket = new Socket(HOST, PUERTO);
            DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream is = new DataInputStream(clientSocket.getInputStream());

            os.writeUTF(params[0]);
            conexion = is.readUTF();
            Log.d("Seerrviodr", conexion);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e("Conectando paquetes", "Ready");
        recepcion = new ClienteRecepcion(clientSocket, main);
        recepcion.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ok");
        envio = new ClienteEnvio(clientSocket, main);
        envio.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ok");
    }


    public void onSendFile(Uri selectedImagePath) throws IOException {
        System.out.println("Connecting...");

        File myFile = new File(getRealPathFromURI(selectedImagePath));
        byte[] mybytearray = new byte[(int) myFile.length()];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = clientSocket.getOutputStream();

        System.out.println("Sending...");

        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(KeyWordSystem.File_Transfer+" " + myFile.getName());
        dos.writeLong(mybytearray.length);
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();

        bis.close();


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
