package com.example.alberto.pvideop.ImagenVideo;


import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by alberto on 21/05/17.
 */
public class SocketCliente {

    public boolean ex(){
        int i=0;
        if(i%2==0) {

            return true;
        }
        else
            return false;
    }



    public String ejecutaCliente(String ip){
        try{
            Log.e("error",ip);
           // tareaLarga();
            File f= new File("/storage/emulated/0/DCIM/profile.png");
            String path=f.getAbsolutePath();
            long tam=f.length();
            Log.d("tam archivo"," "+tam);
            String nombre=f.getName();
            Log.i("progreso","Archivo Enviado");
            DataInputStream dis= new DataInputStream(new FileInputStream(path));
            DatagramSocket s = new DatagramSocket();
            byte [] b = nombre.getBytes();
            InetAddress dst = InetAddress.getByName(ip);
            DatagramPacket p = new DatagramPacket(b,b.length,dst,9001);
            s.send(p);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DataOutputStream dos =new DataOutputStream(baos);
            dos.writeLong(tam);
            dos.flush();
            b = baos.toByteArray();
            p = new DatagramPacket(b, b.length, dst,9001);
            s.send(p);
            byte[] buf= new byte[4000];

            int n=0;
            long enviados=0;
         //   while(enviados<tam){
                n=dis.read(buf);
                dos.write(buf,0,n);
                enviados=enviados+n;
                p = new DatagramPacket(buf, buf.length, dst,9001);
                s.send(p);
                int porcentaje=(int)((enviados*100)/tam);
                Log.i("porcentaje","Se ha enviado el  "+porcentaje+"%");

       //     }

            p = new DatagramPacket(b,b.length,dst,9001);
            s.receive(p);
            String respuesta = new String(p.getData(),0,p.getLength());
            Log.i("respuesta", respuesta);

         //   dis.close();
         //   dos.close();
            return respuesta;


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    } // end main


}
