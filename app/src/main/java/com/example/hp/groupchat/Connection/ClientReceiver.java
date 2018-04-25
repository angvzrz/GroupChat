package com.example.hp.groupchat.Connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.MainActivity;
import com.example.hp.groupchat.shared.PackData;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientReceiver extends AsyncTask<PackData, PackData, Void> {

    private MainActivity main;
    private ObjectInputStream inputStream;
    private boolean statusConnection;
    private byte[] bitmapdata;

    ClientReceiver(ObjectInputStream inputStream, MainActivity main) {
        this.inputStream = inputStream;
        this.main = main;
        this.statusConnection=true;

    }

    @Override
    protected void onProgressUpdate(PackData... values) {

        //MainActivity.Mensaje mensaje = new MainActivity.Mensaje( values[0], 'R');
        PackData msg=values[0];
        msg.setPos('R');
        main.addNewMsg(msg);
    }

    /*@Override
    protected void onPreExecute() {
        try {
            inputStream = new ObjectInputStream(clienteSocket.getInputStream());
            //inputStream.defaultReadObject();
            Log.e("Recepcion", "paso el pre");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected Void doInBackground(PackData... params) {
        Log.e("Recepcion", "doInBackGround");
        PackData packData;
        while (statusConnection) {
            try {
                Log.e("Recepcion", "esperando mensaje de entrada" + inputStream.available());
                packData = (PackData) inputStream.readObject();
                Log.e("MSG", packData.toString());
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                   /* bitmapdata = readBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                    saveImageToGallery(bitmap, string.split(" ")[1]);
                    publishProgress(string);*/
                } else {
                    publishProgress(packData);
                }
            } catch (IOException e) {
                e.printStackTrace();
                statusConnection=false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void receiverFile(String string) throws IOException {
        Log.d(KeyWordSystem.File_Transfer, "Reading: " + System.currentTimeMillis());
        String nameFile = string.split(" ")[1];
        File file = File.createTempFile(nameFile, null, main.getCacheDir());
        file.deleteOnExit();
        OutputStream output = new FileOutputStream(file);
        long size = inputStream.readLong();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (size > 0 && (bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }

        output.close();
        Log.d(KeyWordSystem.File_Transfer, "Ok");
        //saveImageToGallery(file);
       // publishProgress(nameFile);
    }

   /* private byte[] readBytes() throws IOException {
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

    private void saveImageToGallery(Bitmap b, String name) {


        String description = "Image from server.";
        Date currentDate = new Date(System.currentTimeMillis() + 25L * 24L * 60L * 60L * 1000L);
        MediaStore.Images.Media.insertImage(main.getContentResolver(), b, name + "-" + currentDate.toString(), description);
        Log.e("SaveImageInGallery", "Done!");
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        try {
            clienteSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void setStatusConnection(boolean statusConnection) {
        this.statusConnection = statusConnection;
    }

}