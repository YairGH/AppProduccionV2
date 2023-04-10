package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.CustomAdapters.AdapterOrdenesRow;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoOrdenesProduccionRpc;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoStockPickingRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigSerialSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenTraslado;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSeriaConn;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSerialAppCompatActivity;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.StockPicking;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class TrasladoPesaje extends UsbSerialAppCompatActivity {

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private int idTraslado;
    private int cantPesada;
    private int cantRestante;
    private double cantProgramada;

    private TextView lblTrasladoPesaje;
    private TextView lblFormulaTrasladoPesaje;
    private TextView lblProgramadoTrasladoPesaje;
    private TextView lblPesoTrasladoReal;

    private LinearLayout layoutPesoProgramadoTraslado;
    private LinearLayout layoutPesoRealTraslado;

    private LoadingDialog loadingDialog;

    private UsbSeriaConn usbSeriaConn;
    private ConstraintLayout layoutRepartoMain;

    private String comandoTara = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traslado_pesaje);
        getSupportActionBar().setTitle("Pesaje Orden de Traslado");

        intFormat.setGroupingUsed(true);

        lblTrasladoPesaje = (TextView)findViewById(R.id.lblTrasladoPesaje);
        lblFormulaTrasladoPesaje = (TextView)findViewById(R.id.lblFormulaTrasladoPesaje);
        lblProgramadoTrasladoPesaje = (TextView)findViewById(R.id.lblProgramadoTrasladoPesaje);
        lblPesoTrasladoReal = (TextView)findViewById(R.id.lblPesoTrasladoReal);

        layoutPesoProgramadoTraslado = (LinearLayout)findViewById(R.id.layoutPesoProgramadoTraslado);
        layoutPesoRealTraslado = (LinearLayout)findViewById(R.id.layoutPesoRealTraslado);

        fillData();
        comienzaLectura();
    }

    @Override
    public void onBackPressed() {

    }

    private void comienzaLectura() {
        try {
            DaoConfigSerialSql daoConfigSerial = new DaoConfigSerialSql(this);
            ConfiguracionSerial configuracionSerial = daoConfigSerial.getConfiguracionSerial();
            if(configuracionSerial != null) {

                UsbSeriaConn.BAUDRATE = configuracionSerial.getBaudrate();
                UsbSeriaConn.BYTESIZE = configuracionSerial.getBytesize();
                UsbSeriaConn.PARITY = configuracionSerial.getParity();
                UsbSeriaConn.STOPBIT = configuracionSerial.getStopbit();
                comandoTara = configuracionSerial.getTara();

            } else {
                Toast.makeText(this, "Puerto serie no configurado", Toast.LENGTH_LONG).show();
            }

            usbSeriaConn = new UsbSeriaConn(
                    (UsbManager) getSystemService(Context.USB_SERVICE),
                    this
            );
            if (usbSeriaConn.creatUsbSerialConn()) {
                Snackbar.make(layoutRepartoMain, "Conexión Serial Exitosa", Snackbar.LENGTH_SHORT).show();
                usbSeriaConn.startUsbSerialListener();
            } else {
                Snackbar.make(layoutRepartoMain, "Cable Serial Desconectado", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(TrasladoPesaje.this, Login.class);
                startActivity(intent);
                return;
            }
            Toast.makeText(this, "ENVÍA TARA", Toast.LENGTH_SHORT).show();
            usbSeriaConn.writeToSerial("\u001a" + comandoTara);
        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "ERROR: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void fillData() {
        DaoOrdenTraslado daoOrdenTraslado = new DaoOrdenTraslado(this);
        OrdenesProduccionInfo ordenTraslado = daoOrdenTraslado.getOrdenTrasladoInfo();

        idTraslado = ordenTraslado.getIdTraslado();
        cantPesada = 0;
        cantRestante = (int)Math.round(ordenTraslado.getCantidad());
        cantProgramada = ordenTraslado.getCantidad();

        lblTrasladoPesaje.setText(ordenTraslado.getOrdenTraslado());
        lblFormulaTrasladoPesaje.setText(ordenTraslado.getFormula());
        lblProgramadoTrasladoPesaje.setText(intFormat.format(cantRestante));
        lblPesoTrasladoReal.setText("0");
    }

    public void procesaCantTraslado(View v) {
        loadingDialog = new LoadingDialog(TrasladoPesaje.this);
        loadingDialog.startLoadingDialog("Cargando Ordenes...");
        new RecuperaOrdenesTask().execute();
    }

    private void addCantidad(int cantRecibida) {
        cantPesada += cantRecibida;
        cantRestante -= cantRecibida;

        lblProgramadoTrasladoPesaje.setText(intFormat.format(cantRestante) + "");
        lblPesoTrasladoReal.setText(intFormat.format(cantPesada) + "");

        updateColors();
    }

    private void updateColors() {
        double porcentaje = 0;

        porcentaje = (cantPesada * 100) / cantProgramada;

        if(porcentaje > 80 &porcentaje <= 105) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

            layoutPesoRealTraslado.setBackgroundColor(ContextCompat.getColor(TrasladoPesaje.this, android.R.color.holo_green_light));
            //layoutPesoProgramadoReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.white));
        } else if(porcentaje > 105) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300);

            layoutPesoRealTraslado.setBackgroundColor(ContextCompat.getColor(TrasladoPesaje.this, android.R.color.holo_red_light));
            layoutPesoProgramadoTraslado.setBackgroundColor(ContextCompat.getColor(TrasladoPesaje.this, android.R.color.holo_red_light));
        }
    }

    @Override
    public void setSerialMessage(String message) {
        try {
            message = message.replaceAll("[^0-9]+", "");
            if(!message.equals("")) {
                try {
                    int cantidadPesada = Integer.parseInt(message);
                    addCantidad(cantidadPesada);
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ex) {}
            }

        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "EX: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class RecuperaOrdenesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            try {
                DaoStockPickingRpc daoStockPickingRpc = new DaoStockPickingRpc();
                String respuestaProcesaTraslado = daoStockPickingRpc.procesaTrasladoAlimento(idTraslado, Double.valueOf(cantPesada + ""));
                System.out.println("PROBANDO " + respuestaProcesaTraslado);
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            loadingDialog.dismissDialog();
        }
    }

    /**
     * Método para prueba
     */

    public void add100(View v) {
        addCantidad(500);
    }
}