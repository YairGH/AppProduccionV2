package com.ygh.produccion.appproduccionv2;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoServidasRpc;
import com.ygh.produccion.appproduccionv2.RpcXml.RpcConn;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigSerialSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigServerSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoServidasSql;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSeriaConn;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSerialAppCompatActivity;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class IniciarCorral extends UsbSerialAppCompatActivity {

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();

    private LoadingDialog loadingDialog;

    private TextView txtCorralIniciarCorral = null;
    private TextView txtQtyProgramadaIniciarCorral = null;
    private TextView lblPesoInicialCorral = null;
    private TextView txtLog = null;
    private ConstraintLayout mainLayoutIniciaCorral = null;

    private Button button = null;

    private RmsServidaLine currentLineServida = null;

    private DaoServidasSql dao = null;
    private DaoConfigServerSql daoServer = null;

    private RmsServidaLine servidaLineResult;

    private UsbSeriaConn usbSeriaConn;

    private Integer pesoInicial = 0;

    private static final String ACTION_USB_PERMISSION = "com.prolific.PL2303Gsimpletest.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_corral);
        getSupportActionBar().setTitle("Corral Reparto");

        button = (Button) findViewById(R.id.button_reparto);

        intFormat.setGroupingUsed(true);

        mainLayoutIniciaCorral = (ConstraintLayout)findViewById(R.id.mainLayoutIniciaCorral);
        txtCorralIniciarCorral = (TextView)findViewById(R.id.txtCorralIniciarCorral_reparto);
        txtQtyProgramadaIniciarCorral = (TextView)findViewById(R.id.txtQtyProgramadaIniciarCorral_reparto);
        lblPesoInicialCorral = (TextView)findViewById(R.id.lblPesoInicialCorral_reparto);
        txtLog = (TextView)findViewById(R.id.txtLog_reparto);

        dao = new DaoServidasSql(this);
        daoServer = new DaoConfigServerSql(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getNextCorralInfo();

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        //try {
        //   usbSeriaConn.stopUsbSerialListener();
        //} catch (Exception ex) {
        //   Toast.makeText(this, "Intentado stopListener " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        //}
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(IniciarCorral.this, OrdenesReparto.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.busqueda_menu, menu);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        };
        menu.findItem(R.id.mBuscarCorral).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.mBuscarCorral).getActionView();
        searchView.setQueryHint("Corral...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                servidaLineResult = dao.getServidaLineByBusquedaCorral(query);
                if(servidaLineResult != null) {
                    if(!servidaLineResult.isProcesada()) {
                        txtCorralIniciarCorral.setText(servidaLineResult.getLotRanchoTxt());
                        txtQtyProgramadaIniciarCorral.setText(intFormat.format((int)servidaLineResult.getQtyProgramada()));
                    } else {
                        Snackbar.make(mainLayoutIniciaCorral, "El corral especificado ya ha sido procesado", Snackbar.LENGTH_LONG).setAction("Ok", null).show();
                    }
                } else {
                    Snackbar.make(mainLayoutIniciaCorral, "No se ha encontrado el corral especificado", Snackbar.LENGTH_LONG).setAction("Ok", null).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //  Toast.makeText(IniciarCorral.this, "Typing", Toast.LENGTH_LONG).show();;
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            Intent intent = new Intent(IniciarCorral.this, OrdenesReparto.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNextCorralInfo() {
        currentLineServida = dao.getServidaLineToProcess();
        if(currentLineServida != null) {
            txtCorralIniciarCorral.setText(currentLineServida.getLotRanchoTxt());
            txtQtyProgramadaIniciarCorral.setText(intFormat.format((int) currentLineServida.getQtyProgramada()));
            comienzaLectura();
        } else {
            button.setText("TERMINAR SERVIDA");
            txtCorralIniciarCorral.setText("N/D");
            txtQtyProgramadaIniciarCorral.setText("N/D");
        }
    }

    public void comienzaServida(View v) {
        if(!button.getText().toString().contains("TERMINAR")) {
            if (pesoInicial > 0) {

                try {
                    usbSeriaConn.stopUsbSerialListener();
                } catch (Exception ex) {
                    Toast.makeText(this, "Intentado stopListener " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Peso Inicial " + pesoInicial, Toast.LENGTH_SHORT).show();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                String prmCorral = txtCorralIniciarCorral.getText().toString();
                Intent intent = new Intent(IniciarCorral.this, RepartoCorral.class);
                intent.putExtra(RepartoCorral.PRM_PESO_INICIAL, pesoInicial.doubleValue());
                intent.putExtra(RepartoCorral.PRM_CORRAL, prmCorral);
                intent.putExtra(RepartoCorral.PRM_FH_INICIO, formatter.format(new Date()));
                startActivity(intent);
                return;
            } else {
                Toast.makeText(this, "No se ha detectado peso", Toast.LENGTH_SHORT).show();
            }
        } else {
            new SincronizaTask().execute();
            loadingDialog = new LoadingDialog(IniciarCorral.this);
            loadingDialog.startLoadingDialog("SINCRONIZANDO");
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

            } else {
                Toast.makeText(this, "Puerto serie no configurado", Toast.LENGTH_LONG).show();;
            }

            usbSeriaConn = new UsbSeriaConn(
                    (UsbManager) getSystemService(Context.USB_SERVICE),
                    this
            );
            if (usbSeriaConn.creatUsbSerialConn()) {
                Snackbar.make(mainLayoutIniciaCorral, "Conexión Serial Exitosa", Snackbar.LENGTH_SHORT).show();
                usbSeriaConn.startUsbSerialListener();
                //  Toast.makeText(this, "LEYENDO...", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainLayoutIniciaCorral, "Cable Serial Desconectado", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(IniciarCorral.this, Login.class);
                startActivity(intent);
                return;
            }

        } catch (Exception ex) {
            Snackbar.make(mainLayoutIniciaCorral, "ERROR: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setSerialMessage(String message) {
        try {
            message = message.replaceAll("[^0-9]+", "");

            if(!message.equals("")) {
                try {
                    int cantidadPesada = Integer.parseInt(message);
                    lblPesoInicialCorral.setText("Lectura Bascula: " + cantidadPesada);
                    TimeUnit.SECONDS.sleep(1);
                    pesoInicial = cantidadPesada;
                } catch (Exception ex) {}
            }
        } catch (Exception ex) {
            Toast.makeText(this, "ERROR: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            //Snackbar.make(mainLayoutIniciaCorral, "EX: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class SincronizaTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... arg) {
            try {
                ServerConfig serverInfo = daoServer.getServerDatos();
                RpcConn conn = new RpcConn(serverInfo.getUrl(), serverInfo.getDb(), serverInfo.getUsername(), serverInfo.getPassword());

                ArrayList<RmsServidaLine> lstLineas = dao.getAllLineas();
                RmsServida servida = dao.getServidaToProcess();
                DaoServidasRpc daoServidasRpc = new DaoServidasRpc();
                Boolean response = daoServidasRpc.updateServidaLines(lstLineas);
                if(!daoServidasRpc.cierraServida(servida.getId()).contains("ERROR") && response)
                    return true;
                else
                    return false;

            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if(result) {
                dao.cleanServida();
                loadingDialog.dismissDialog();
                Intent intent = new Intent(IniciarCorral.this, MenuProduccionReparto.class);
                startActivity(intent);
            } else {
                loadingDialog.dismissDialog();
            }
        }
    }
}