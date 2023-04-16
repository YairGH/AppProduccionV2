package com.ygh.produccion.appproduccionv2;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigSerialSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoServidasSql;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSeriaConn;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSerialAppCompatActivity;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RepartoCorral extends UsbSerialAppCompatActivity {

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();

    public static final String PRM_CORRAL = "prmCorral";
    public static final String PRM_FH_INICIO = "prmFhInicio";
    public static final String PRM_PESO_INICIAL = "pesoInicial";
    private String prmCorral = "";
    private Date fhInicioReparto = null;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    //PesoInicial es la lectura con la que se comienza el reparto
    private double pesoInicial = 0;

    //PesoProgramado es el peso programado de la línea
    private double pesoProgramado = 0;

    //PesoReal es el peso que se le ha descargado a la línea
    private double pesoReal = 0;

    //PesoLectura es el peso actual de la bascula, es variable
    private double pesoLectura = 0;

    private TextView lblServidosPendientes;
    private TextView lblRepartoLote;
    private TextView lblRepartoQty;
    private TextView lblPesoProgramadoReparto;
    private TextView lblPesoRepartoReal;
    //private TextView lblLecturaBascula;

    /**TEMPORALES PARA PRUEBA**/
    private EditText txtSetPeso;
    private EditText txtPeso10;
    /**TEMPORALES PARA PRUEBA**/

    private LinearLayout layoutPesoProgramadoReparto;
    private LinearLayout layoutPesoRealReparto;
    private ConstraintLayout layoutRepartoMain;

    private DaoServidasSql daoServidas = null;
    private RmsServida currentServida = null;
    private RmsServidaLine line = null;

    private Button btnGuardar = null;

    private UsbSeriaConn usbSeriaConn;

    private int ocupaTara;
    private String comandoTara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto_corral);
        getSupportActionBar().setTitle("Reparto");

        intFormat.setGroupingUsed(true);

        lblPesoProgramadoReparto = (TextView)findViewById(R.id.lblPesoProgramadoReparto_reparto);
        lblRepartoLote = (TextView)findViewById(R.id.lblRepartoLote_reparto);
        lblRepartoQty = (TextView)findViewById(R.id.lblMaquina_reparto);
        lblPesoProgramadoReparto = (TextView)findViewById(R.id.lblPesoProgramadoReparto_reparto);
        lblPesoRepartoReal = (TextView)findViewById(R.id.lblPesoRepartoReal_reparto);
        lblServidosPendientes = (TextView)findViewById(R.id.lblServidosPendientes_reparto);

        layoutPesoProgramadoReparto = (LinearLayout)findViewById(R.id.layoutPesoProgramadoReparto_reparto);
        layoutPesoRealReparto = (LinearLayout)findViewById(R.id.layoutPesoRealReparto_reparto);
        layoutRepartoMain = (ConstraintLayout) findViewById(R.id.layoutRepartoMain);

        btnGuardar = (Button)findViewById(R.id.btnGuardarReparto_reparto);

        prmCorral = getIntent().getStringExtra(RepartoCorral.PRM_CORRAL);

        pesoInicial = getIntent().getDoubleExtra(RepartoCorral.PRM_PESO_INICIAL, 0);

        try {
            fhInicioReparto = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(getIntent().getStringExtra(RepartoCorral.PRM_FH_INICIO));
        } catch (Exception ex) {
            fhInicioReparto = new Date();
        }

        daoServidas = new DaoServidasSql(this);
        fillData();
        comienzaLectura();
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

    }

    /**
     * Se llenan los campos con la Servida y Línea de servida Actual
     * La inicia la variable de peso programado (inicio)
     */
    private void fillData() {
        int cantCorralesPendientes = daoServidas.getCorralesPendientesServir();
        int cantCorralesServidos = daoServidas.getCorralesServidos();
        lblServidosPendientes.setText(cantCorralesServidos + "/" + cantCorralesPendientes);

        currentServida = daoServidas.getServidaToProcess();
        line = daoServidas.getServidaLineToProcessByCorral(prmCorral);

        lblPesoProgramadoReparto.setText(currentServida.getTotalRepartir() + "");
        lblRepartoLote.setText(line.getLotRanchoTxt());
        lblRepartoQty.setText("" + df.format(line.getQtyProgramada()));
        lblPesoProgramadoReparto.setText("" + df.format(line.getQtyProgramada()));

        pesoProgramado = Double.parseDouble(lblPesoProgramadoReparto.getText().toString());
    }

    public void guardarReparto(View v) {
        try {

            usbSeriaConn.stopUsbSerialListener();
        } catch (Exception ex) {
            Toast.makeText(this, "Intentado stopListener " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        String strPesoRealReparto = lblPesoRepartoReal.getText().toString();
        strPesoRealReparto = strPesoRealReparto.replaceAll("[^0-9.]+", "");

        daoServidas.updateServidaProcesada(currentServida.getId());
        daoServidas.updatePesoRealServida(Double.parseDouble(strPesoRealReparto), line.getId(), fhInicioReparto);

        Intent intent = new Intent(RepartoCorral.this, IniciarCorral.class);
        startActivity(intent);
    }

    public void iniciaReparto(View v) {
        btnGuardar.setEnabled(true);
        pesoLectura = pesoInicial;
    }

    public void add10(View v) {
        double pesoProgramadoAux = 0;
        double pesoRealAux = 0;
        pesoLectura -= 10;
        pesoProgramadoAux = pesoProgramado - (pesoInicial - pesoLectura);
        pesoRealAux = pesoReal + (pesoInicial - pesoLectura);

        lblPesoProgramadoReparto.setText(intFormat.format(pesoProgramadoAux));
        lblPesoRepartoReal.setText(intFormat.format(pesoRealAux));

        updateColors();
    }

    private void updateColors() {
        String strPesoRepartoReal = lblPesoRepartoReal.getText().toString();
        strPesoRepartoReal = strPesoRepartoReal.replaceAll("[^0-9.]+", "");
        double porcentaje = 0;
        double pesoRepartido = Double.parseDouble(strPesoRepartoReal);

        porcentaje = (pesoRepartido * 100) / pesoProgramado;

        if(porcentaje > 80 &porcentaje <= 105) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

            layoutPesoRealReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.holo_green_light));
            //layoutPesoProgramadoReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.white));
        } else if(porcentaje > 105) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300);

            layoutPesoRealReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.holo_red_light));
            layoutPesoProgramadoReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.holo_red_light));
        }
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
                ocupaTara = 0;//configuracionSerial.getOcupaTara();

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
                Intent intent = new Intent(RepartoCorral.this, Login.class);
                startActivity(intent);
                return;
            }


            usbSeriaConn.writeToSerial("\u001A" + comandoTara);

        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "ERROR: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setSerialMessage(String message) {
        try {
            message = message.replaceAll("[^0-9]+", "");
            if(!message.equals("")) {
                int pesoLecturaBascula = Integer.parseInt(message);

                double pesoProgramadoAux = 0;
                double pesoRealAux = 0;
                pesoLectura = pesoLecturaBascula;
                // pesoProgramadoAux = pesoProgramado - (pesoInicial - pesoLectura);
                pesoProgramadoAux = pesoProgramado - pesoLectura;
                pesoRealAux = pesoReal + pesoLectura;

                lblPesoProgramadoReparto.setText(intFormat.format(pesoProgramadoAux) + " Kg");
                lblPesoRepartoReal.setText(intFormat.format(pesoRealAux) + " Kg");

                TimeUnit.SECONDS.sleep(1);

                updateColors();
            }
        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "ERROR: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}