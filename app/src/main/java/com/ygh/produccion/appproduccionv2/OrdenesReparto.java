package com.ygh.produccion.appproduccionv2;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.CustomAdapters.AdapterServidasLine;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoServidasRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoServidasSql;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSerialAppCompatActivity;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import tw.com.prolific.driver.pl2303g.PL2303GDriver;

public class OrdenesReparto extends UsbSerialAppCompatActivity {

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();

    private ConstraintLayout mainLayout;
    private TextView lblServida = null;
    private TextView lblMaquina = null;
    private TextView lblCantidad = null;
    private TextView lblQtyReparto = null;
    private TextView lblFormula = null;
    private TextView lblBachada = null;
    private TextView lblCantLotes = null;
    private TextView lblQtyServida = null;
    private TextView lblFechaReparto = null;
    private TextView lblTitulo = null;
    private ArrayList<MrpProduction> lstOrdenesReparto = new ArrayList<>();
    private ArrayList<RmsServidaLine> lstServidaLines = new ArrayList<>();
    private AdapterServidasLine adapterServidasLine;
    private RmsServida rmsServida;
    private DaoServidasSql daoServidasSql;
    private LoadingDialog loadingDialog;

    private DecimalFormat decimalFormat = null;

    private boolean isLocal = false;

    private static final String ACTION_USB_PERMISSION = "com.prolific.PL2303Gsimpletest.USB_PERMISSION";

    PL2303GDriver mSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ordenes_reparto);

        intFormat.setGroupingUsed(true);

        decimalFormat = new DecimalFormat("0.00");

        mainLayout = (ConstraintLayout)findViewById(R.id.mainLayoutOrdenesReparto);
        lblServida = findViewById(R.id.lblServida_reparto);
        lblMaquina = findViewById(R.id.lblMaquinaReparto_reparto);
        lblCantidad = findViewById(R.id.lblQtyProducida_reparto);
        lblQtyReparto = findViewById(R.id.lblQtyRepartir_reparto);
        lblFormula = findViewById(R.id.lblFormula_reparto);
        lblBachada = findViewById(R.id.lblBachada_reparto);
        lblCantLotes = findViewById(R.id.lblCantLotes_reparto);
        lblQtyServida = findViewById(R.id.lblQtyServida_reparto);
        lblFechaReparto = findViewById(R.id.lblFechaReparto_reparto);
        lblTitulo = findViewById(R.id.lblTituloOR_reparto);
        daoServidasSql = new DaoServidasSql(this);

        recuperaDatos();
    }

    @Override
    public void onBackPressed()
    {

    }

    /**
     * Si existe datos en la BD local llena los campos desde SqlLite, de lo contrario carga de Odoo
     */
    private void recuperaDatos() {
        RmsServida s = daoServidasSql.getServidaToProcess();
        if(s != null) {
            isLocal = true;
            fillLocalInfo(s, daoServidasSql.getAllLineas());
        } else {
            loadingDialog = new LoadingDialog(OrdenesReparto.this);
            loadingDialog.startLoadingDialog("Cargando Orden de Reparto...");
            new CargaServidaSiguienteTask().execute();
        }
    }

    private void fillLocalInfo(RmsServida s, ArrayList<RmsServidaLine> lstLineas) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        lblFechaReparto.setText(df.format(s.getFecha()));
        lblServida.setText(s.getName());
        lblMaquina.setText(s.getMaquinaReparto());
        lblCantidad.setText(intFormat.format((int)s.getTotalProducida()));
        lblFormula.setText(s.getFormula());
        lblBachada.setText(s.getBachada() + "");

        int qtyServir = 0;
        int qtyServida = 0;

        for(RmsServidaLine l : lstLineas) {
            if(!l.isProcesada())
                qtyServir += (int)l.getQtyProgramada();
            else
                qtyServida += (int)l.getQtyUom();
        }

        lblQtyReparto.setText(intFormat.format((int)qtyServir));
        lblQtyServida.setText(intFormat.format((int)qtyServida));
        lblCantLotes.setText(intFormat.format(lstLineas.size()));
    }

    /**
     * Lleno el listview con la información de las líneas de servida
     */
    private void fillDetallesServida() {
        lblFormula.setText(lstServidaLines.get(0).getFormula_product_id());
        rmsServida.setFormula(lstServidaLines.get(0).getFormula_product_id());
        lblCantLotes.setText("" + lstServidaLines.size());
        double qtyRepartir = 0;
        for(RmsServidaLine l : lstServidaLines) {
            qtyRepartir += l.getQtyProgramada();
        }
        lblQtyReparto.setText(intFormat.format(qtyRepartir));
        lblQtyServida.setText("0");
    }

    @Override
    public void setSerialMessage(String message) {
        lblTitulo.setText(message);
    }

    /**
     * Cargo la servida relacionada con la máquina que no ha sido repartida
     */
    private class CargaServidaSiguienteTask extends AsyncTask<Void, Void, RmsServida> {
        protected RmsServida doInBackground(Void... arg) {
            try {
                DaoServidasRpc daoServidasRpc = new DaoServidasRpc();
                RmsServida rmsServida = daoServidasRpc.getServidaToProcess(Usuario.ID_MAQUINA_REPARTO);
                return rmsServida;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }

        protected void onPostExecute(RmsServida result) {
            if(result != null) {
                rmsServida = result;
                lblServida.setText(result.getName());
                lblCantidad.setText("" + intFormat.format((int)result.getCantidad()));
                rmsServida.setTotalProducida(result.getCantidad());
                lblBachada.setText("" + result.getBachada());
                lblFormula.setText("");
                lblMaquina.setText(result.getMaquinaReparto());
                rmsServida.setMaquinaReparto(result.getMaquinaReparto());

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                lblFechaReparto.setText(df.format(result.getFecha()));

                Integer[] idServida = {result.getId()};
                new CargaLineasServidaTask().execute(idServida);
            } else {
                loadingDialog.dismissDialog();
                Snackbar.make(mainLayout, "No existen ordenes de reparto para la máquina asignada al usuario", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrdenesReparto.this, Login.class);
                        startActivity(intent);
                    }
                }).show();
            }
        }
    }

    /**
     * Descargo la líneas de la servida
     */
    private class CargaLineasServidaTask extends AsyncTask<Integer, Void, Void> {
        protected Void doInBackground(Integer... arg) {
            DaoServidasRpc daoServidasRpc = new DaoServidasRpc();
            lstServidaLines = daoServidasRpc.getServidasLineByServida(arg[0]);
            return null;
        }

        protected void onPostExecute(Void arg) {
            fillDetallesServida();
            loadingDialog.dismissDialog();
        }
    }

    public void iniciarReparto(View v) {
        System.out.println("MrpState: " + rmsServida.isMrpState() + " PickState: " + rmsServida.isPickRepartoState());

        String msgValido = validoToRepartir();
        if(msgValido.equals("")) {
            new SaveServidaTask().execute(rmsServida, lstServidaLines);
        } else {
            Snackbar.make(mainLayout, msgValido, Snackbar.LENGTH_LONG).show();
        }

        /*try {
            UsbSeriaConn.BAUDRATE = 19200;
            UsbSeriaConn.BYTESIZE = 8;
            UsbSeriaConn.PARITY = 0;
            UsbSeriaConn.STOPBIT = 1;

            UsbSeriaConn usbSeriaConn = new UsbSeriaConn(
                    (UsbManager) getSystemService(Context.USB_SERVICE),
                    this
            );
            if (usbSeriaConn.creatUsbSerialConn()) {
                Snackbar.make(mainLayout, "CREADO", Snackbar.LENGTH_LONG).show();
                usbSeriaConn.startUsbSerialListener();
            } else {
                Snackbar.make(mainLayout, "FALLO!", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            lblTitulo.setText(ex.getMessage());
        }*/
    }

    private void conectaSerial() {
        mSerial = new PL2303GDriver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, ACTION_USB_PERMISSION);

        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported()) {

            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT)
                    .show();


            mSerial = null;

        }

        if( !mSerial.enumerate() ) {
            Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida si la orden de producción o el inventario estan en DONE antes de comenzar reparto
     * @return
     */
    private String validoToRepartir() {
        String msg = "";
   /*     if(rmsServida.isMrpState() != null && (!rmsServida.isMrpState().toUpperCase().equals("DONE"))) {
            if(!rmsServida.isMrpState().toUpperCase().equals("CONFIRMED")) {
                msg = "La orden de producción no esta lista para el reparto";
                return msg;
            }
        }

        if(rmsServida.isPickRepartoState() != null && !rmsServida.isPickRepartoState().toUpperCase(Locale.ROOT).equals("DONE")) {
            if(!rmsServida.isPickRepartoState().toUpperCase().equals("CONFIRMED")) {
                msg = "El albaran no esta listo para el reparto";
                return msg;
            }
        }*/

        return "";
    }

    private class SaveServidaTask extends AsyncTask<Object, Void, Void> {
        protected Void doInBackground(Object... arg) {
            if(!isLocal) {
                (OrdenesReparto.this).daoServidasSql.saveServida((RmsServida) arg[0]);
                (OrdenesReparto.this).daoServidasSql.saveServidaLines((ArrayList<RmsServidaLine>) arg[1]);
            }
            return null;
        }

        protected void onProgressUpdate(Void... arg) {
            return;
        }

        protected void onPostExecute(Void arg) {
            Intent intent = new Intent(OrdenesReparto.this, IniciarCorral.class);
            startActivity(intent);
            return;
        }
    }
}