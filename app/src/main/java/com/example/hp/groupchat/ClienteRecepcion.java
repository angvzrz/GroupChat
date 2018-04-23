package com.example.hp.groupchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;

public class ClienteRecepcion extends AsyncTask<String, String, String> {

    private Socket clienteSocket;
    private MainActivity main;
    private DataInputStream is;
    private byte[] bitmapdata;

    ClienteRecepcion(Socket clienteSocket, MainActivity main) {
        this.clienteSocket = clienteSocket;
        this.main = main;

    }

    @Override
    protected void onProgressUpdate(String... values) {

        MainActivity.Mensaje mensaje=new MainActivity.Mensaje("Nombre", values[0], 'R');
        if (values[0].contains(KeyWordSystem.File_Transfer)){
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            mensaje.setBitmap(bitmap);
        }
        main.men.add(mensaje);
        main.adaptador1.notifyDataSetChanged();
    }

    @Override
    protected void onPreExecute() {
        try {
            is = new DataInputStream(clienteSocket.getInputStream());
            Log.e("Recepcion", "paso el pre");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e("Recepcion", "doInBackGround");
        String string;
        while (true) {
            try {
                Log.e("Recepcion", "esperando mensaje de entrada" + is.available());
                string = is.readUTF();
                Log.e("MSG", string);
                if (string.contains(KeyWordSystem.File_Transfer)) {
                    bitmapdata = readBytes();

                    publishProgress(string);
                } else {
                    publishProgress(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiverFile(String string) throws IOException {
        Log.d(KeyWordSystem.File_Transfer, "Reading: " + System.currentTimeMillis());
        String nameFile = string.split(" ")[1];
        File file = File.createTempFile(nameFile, null, main.getCacheDir());
        file.deleteOnExit();
        OutputStream output = new FileOutputStream(file);
        long size = is.readLong();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (size > 0 && (bytesRead = is.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }

        output.close();
        Log.d(KeyWordSystem.File_Transfer, "Ok");
        saveImageToGallery(file);
        publishProgress(nameFile);
    }
    private byte[] readBytes() throws IOException {
        // Again, probably better to store these objects references in the support class
        InputStream in = clienteSocket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }

    private void saveImageToGallery(File file) {

        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
        String baseName = StringUtils.getBaseName(file.getName());

        String description = "Image from server.";
        Date currentDate = new Date(System.currentTimeMillis() + 25L * 24L * 60L * 60L * 1000L);
        MediaStore.Images.Media.insertImage(main.getContentResolver(), b, baseName + "-" + currentDate.toString(), description);
        Log.e("SaveImageInGallery", "Done!");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
