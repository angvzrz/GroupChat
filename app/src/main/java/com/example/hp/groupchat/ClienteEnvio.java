package com.example.hp.groupchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Diego on 02/04/2017.
 */

public class ClienteEnvio extends AsyncTask<String, String, String> {

    Socket clientSocket;
    MainActivity main;
    DataOutputStream os;

    public ClienteEnvio(Socket clientSocket, MainActivity main) {
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
            Log.d("PreEnvio", "data esperarando");
            os = new DataOutputStream(clientSocket.getOutputStream());
            Log.e("PreEnvio", "paso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        Log.e("doInBackground", params[0]);
        Log.e("Mensaje", "Inicio");
        while (true) {
            try {
                String message = main.bq.take();
                //main.bq.peek();
                if (!message.contains(KeyWordSystem.File_Transfer)) {
                    os.writeUTF(message);
                }else{
                    Log.e(KeyWordSystem.File_Transfer,"Prepared");
                    onSendFile(message);
                    Log.e(KeyWordSystem.File_Transfer,message);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void onSendFile(String msg) throws IOException, URISyntaxException {
        System.out.println("Connecting...");
        Log.e(KeyWordSystem.File_Transfer,msg);
        int pos=Integer.parseInt(msg.split(" ")[1]);
        MainActivity.Mensaje mensaje=main.men.get(pos);
        Bitmap bitmap=mensaje.getBitmap();
        Log.e("myFile","on course");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        sendBytes(main.nombre+"_img_"+System.currentTimeMillis()+".png",byteArray);
        stream.close();
      /*  byte[] mybytearray = new byte[ myFile.length()];




        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(KeyWordSystem.File_Transfer + " " + myFile.getName());
        dos.writeLong(myFile.length());
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();

        bis.close();*/
       /* byte[] buffer = new byte[8192]; // or 4096, or more
        FileInputStream fis = new FileInputStream(bitmap.getf);

        BufferedInputStream bis = new BufferedInputStream(fis);

        OutputStream os = clientSocket.getOutputStream();
        int count;
        DataOutputStream outputStream=new DataOutputStream(os);
        outputStream.writeUTF(msg);
        outputStream.writeLong(myFile.length());

        while ((count = bis.read(buffer)) > 0)
        {
            outputStream.write(buffer, 0, count);
        }
        bis.close();*/


    }
    public void sendBytes(String name,byte[] myByteArray) throws IOException {
        sendBytes( name,myByteArray, 0, myByteArray.length);
    }

    public void sendBytes(String name,byte[] myByteArray, int start, int len) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = clientSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeUTF(KeyWordSystem.File_Transfer+" "+name);
        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }
    }
}
