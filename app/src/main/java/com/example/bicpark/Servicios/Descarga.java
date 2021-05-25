package com.example.bicpark.Servicios;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class Descarga {

    public void descargdoc(Activity activity){

    //Url del archivo
    String url = "https://firebasestorage.googleapis.com/v0/b/biceuropark-2a103.appspot.com/o/PDF%2Fsolicitud.pdf?alt=media&token=c1c6908c-dc2e-4e25-99c2-8ab22704af40";
    //Creamos una petición de descarga
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
    //Permitimos los tipos de conexión para descargar archivos
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
    //Titulo de la notificación de descarga
        request.setTitle(""+System.currentTimeMillis()+".pdf");
    //Descripción de la notificación de descarga
        request.setDescription("Descargando archivo...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+System.currentTimeMillis()+".pdf");//Guardado del archivo en carpeta descargas y nombre con el tiempo del dispositivo
    //Obtenemos el servicio de descarga y el archivo en cola
    DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
