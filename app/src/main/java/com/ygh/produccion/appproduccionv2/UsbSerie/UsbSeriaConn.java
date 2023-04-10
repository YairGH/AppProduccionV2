package com.ygh.produccion.appproduccionv2.UsbSerie;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by yairg on 21/09/17.
 */

public class UsbSeriaConn {
    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbSerialPort port;
    private UsbSerialDriver driver;
    private UsbDeviceConnection connection;
    private ListenerTask listenerTask;
    private UsbManager usbManager;
    private UsbSerialAppCompatActivity usbSerialActivity;

    private List<UsbSerialDriver> availableDrivers;

    public static int BAUDRATE;
    public static int PARITY;
    public static int STOPBIT;
    public static int BYTESIZE;

    private boolean isConnected;

    public UsbSeriaConn(UsbManager usbManager, Context ctx) {
        this.usbManager = usbManager;
        usbSerialActivity = (UsbSerialAppCompatActivity)ctx;
    }

    public boolean creatUsbSerialConn() {
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        if(availableDrivers.isEmpty()) {
            Toast.makeText(usbSerialActivity.getApplicationContext(), "Driver no detectado", Toast.LENGTH_LONG).show();
            return false;
        }

        for(int i = 0; i < availableDrivers.size(); i++) {
            driver = availableDrivers.get(i);
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(usbSerialActivity, 0, new Intent(ACTION_USB_PERMISSION), flags);

            usbManager.requestPermission(driver.getDevice(), mPermissionIntent);

            connection = usbManager.openDevice(driver.getDevice());

            if(connection != null) {
                return true;
            }
        }
        Toast.makeText(usbSerialActivity.getApplicationContext(), "Fallo en conexión", Toast.LENGTH_LONG).show();
        return false;
    }

    public void startUsbSerialListener() {
        port = driver.getPorts().get(0);
        try {
            //Toast.makeText(usbSerialActivity.getApplicationContext(), "Abriendo Serial", Toast.LENGTH_SHORT).show();
            port.open(connection);
            port.setParameters(
                    UsbSeriaConn.BAUDRATE,
                    UsbSeriaConn.BYTESIZE,
                    UsbSeriaConn.STOPBIT,
                    UsbSeriaConn.PARITY
            );
            //Toast.makeText(usbSerialActivity.getApplicationContext(), "Cable Serial Conectado", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(usbSerialActivity.getApplicationContext(), "Error Cable Serial " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        listenerTask = new ListenerTask();
        listenerTask.execute();
    }

    public void stopUsbSerialListener() {
        try {
            listenerTask.cancel(true);
        } catch (Exception ex) {

        }
    }

    public void writeToSerial(String message) {
        try {
            byte[] data = message.getBytes();
            port.write(data, 0);
        } catch (Exception ex) {
            Toast.makeText(usbSerialActivity.getApplicationContext(), "ERROR Write" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ListenerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        /**
         * Configurado con el formato de la terminal de bascula
         * Si el input no cumple con el formato del modelo No envía MSG
         * @param param
         * @return
         */
        protected Void doInBackground(Void... param) {
            usbSerialActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(usbSerialActivity.getApplicationContext(), "LEYENDO PESO", Toast.LENGTH_SHORT).show();
                }
            });

            final StringBuffer sb = new StringBuffer();
            String result;
            boolean inicio = false;
            int numBytesRecibidos = 0;

            //While productivo bascula
            while(!isCancelled()) {

                try {

                    byte buffer[] = new byte[1024];
                    int numBytesRead = port.read(buffer, 0);
                    Charset charset = Charset.forName("US-ASCII");
                    result = new String(buffer, 0, numBytesRead, charset);

                    if(result.equals("\u0002") && !inicio) {
                        inicio = true;
                        sb.delete(0, sb.length());
                    } else if(result.equals("\r")) {
                        if(sb.toString().getBytes(StandardCharsets.US_ASCII).length == 6)
                            usbSerialActivity.setSerialMessage(sb.toString());
                        sb.delete(0, sb.length());
                        inicio = false;
                    } else {
                        if(inicio)
                            sb.append(result);
                    }

                } catch (Exception ex) {
                    Toast.makeText(usbSerialActivity.getApplicationContext(), "Error Reading " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            //While para pruebas con putty
            /*while(!isCancelled()) {
                try {
                    byte buffer[] = new byte[1024];
                    final int numBytesRead = port.read(buffer, 1000);
                    final String message = new String(buffer, "UTF-8");

                    if(numBytesRead > 0) {
                        sb.append(new String(buffer, "UTF-8"));
                    } else {
                        usbSerialActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!sb.toString().equals("")) {
                                    //  Toast.makeText(usbSerialActivity.getApplicationContext(), "MESSAGE READ: " + sb.toString(), Toast.LENGTH_SHORT).show();
                                    usbSerialActivity.setSerialMessage(sb.toString());
                                    sb.delete(0, sb.length());
                                }
                            }
                        });
                    }

                } catch (Exception ex) {
                    Toast.makeText(usbSerialActivity.getApplicationContext(), "Error Reading " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }*/

            return null;
        }

        protected void onPostExecute(Void param) {
            try {
                port.close();
                connection.close();
            } catch (Exception ex) {}
        }
    }
}
