package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ygh.produccion.appproduccionv2.RpcXml.DaoOrdenesProduccionRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigVariables;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenesProduccion;
import com.ygh.produccion.appproduccionv2.pojos.ConfigVariablesBascula;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

public class DetallesOrdenProduccion extends AppCompatActivity {

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();

    private ConstraintLayout mainLayout;
    private TextView lblOrdenProduccion = null;
    private TextView lblMaquina = null;
    private TextView lblQty = null;
    private TextView lblFormula = null;
    private TextView lblFhProgramada = null;
    private TextView lblBachada = null;
    private TextView txtTestDetalles = null;

    private LoadingDialog loadingDialog;

    private DecimalFormat decimalFormat = null;

    private MrpProduction mrpProduction = null;
    private StockMove stockMove = null;
    private List<StockMove> lstLineas = null;

    private DaoConfigVariables daoConfigVariables = null;

    //Se utiliza en caso de necesitar procesar bachada 0
    private int idOrdenSeleccionada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            setContentView(R.layout.activity_detalles_orden_produccion);
            txtTestDetalles = findViewById(R.id.txtTestDetalles);
            txtTestDetalles.setVisibility(View.GONE);
            intFormat.setGroupingUsed(true);

            mainLayout = (ConstraintLayout) findViewById(R.id.mainLayoutOrdenesReparto);
            lblOrdenProduccion = findViewById(R.id.lblOrdenProduccion);
            lblMaquina = findViewById(R.id.lblMaquinaReparto);
            lblQty = findViewById(R.id.lblQty);
            lblFormula = findViewById(R.id.lblFormula);
            lblFhProgramada = findViewById(R.id.lblFhProgramada);
            lblBachada = findViewById(R.id.lblBachada);

            daoConfigVariables = new DaoConfigVariables(this);

            idOrdenSeleccionada = getIntent().getIntExtra("IdOrden", 0);

            recuperaDatos();
        } catch (Exception ex) {

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetallesOrdenProduccion.this, ListaOrdenesProduccion.class);
        startActivity(intent);
    }

    private void recuperaDatos() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadingDialog = new LoadingDialog(DetallesOrdenProduccion.this);
                    loadingDialog.startLoadingDialog("Cargando Orden de Reparto...");
                    new ConsultaOrdenTask().execute();
                } catch (Exception ex) {

                    // new ConsultaOrdenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }

    public void iniciarProduccion(View view) {
        Intent intent = new Intent(DetallesOrdenProduccion.this, ProduccionPesaje.class);
        saveInfoProduccion();
        startActivity(intent);
    }

    /**
     * Guardo la información en la BD local. Los insumos NO considero el último insumo porque corresponde a la formula
     * size() - 1
     */
    private void saveInfoProduccion() {
        DaoOrdenesProduccion daoOrdenesProduccion = new DaoOrdenesProduccion(this);
        daoOrdenesProduccion.saveOrdenProduccion(
                mrpProduction.getId(),
                mrpProduction.getName(),
                mrpProduction.getProductName(),
                mrpProduction.getFechaProgramada(),
                mrpProduction.getBachada());

        StockMove s = null;
      //  for(int i = 0; i < lstLineas.size() - 1; i++) {
        for(int i = 0; i < lstLineas.size(); i++) {
            s = lstLineas.get(i);
            daoOrdenesProduccion.saveStockMove(
                    s.getId(), s.getProductId(), s.getProductQty(), s.getTiempoMezclado(), s.getDsDone(), s.getPrePesado()
            );
        }
    }

    private class GuardaVariablesTask extends  AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            try {
                DaoOrdenesProduccionRpc dao = new DaoOrdenesProduccionRpc();
                HashMap<String, Object> configValues = dao.getConfigVariables();
                ConfigVariablesBascula configVariablesBascula = new ConfigVariablesBascula();
                configVariablesBascula.setPesoMinimo((Double)configValues.get("mrp_minimal_weight"));
                configVariablesBascula.setTipoSigInsumo((String)configValues.get("mrp_item_advance"));
                configVariablesBascula.setTipoMedidaSigInsumo((String)configValues.get("mrp_type_advance"));
                configVariablesBascula.setValorToSigInsumo((Double)configValues.get("mrp_type_advance_value"));
                configVariablesBascula.setTiempoSiguienteInsumo((Double)configValues.get("mrp_advance_time"));
                configVariablesBascula.setPrealarm1((Double)configValues.get("mrp_prealarm1"));
                configVariablesBascula.setPrealarm2((Double)configValues.get("mrp_prealarm2"));
                configVariablesBascula.setBuzzer((Boolean)configValues.get("mrp_buzzer"));

                daoConfigVariables.saveConfigVariables(configVariablesBascula);

            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void arg) {

        }
    }

    private class ConsultaOrdenTask extends AsyncTask<Void, Void, MrpProduction> {
        protected MrpProduction doInBackground(Void... arg) {
            try {
                DaoOrdenesProduccionRpc dao = new DaoOrdenesProduccionRpc();
                MrpProduction mrpP = dao.getProductOrdersByMaquinaId(Usuario.ID_MAQUINA_REPARTO, idOrdenSeleccionada);
                return mrpP;
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(MrpProduction mrpP) {
            lblOrdenProduccion.setText(mrpP.getName());
            lblMaquina.setText(mrpP.getNombreMaquinaProduccion());
            lblQty.setText(intFormat.format((int)mrpP.getProductQty()));
            lblFormula.setText(mrpP.getProductName());
            lblFhProgramada.setText(mrpP.getFechaProgramada());
            lblBachada.setText(mrpP.getBachada() + "");

            mrpProduction = mrpP;

            try {
                new ConsultaOrdenDetalles().execute(mrpP.getId() + "");
            } catch (Exception ex) {

            }
        }
    }

    private class ConsultaOrdenDetalles extends AsyncTask<String, Void, List<StockMove>> {
        protected List<StockMove> doInBackground(String... arg) {
            try {
                DaoOrdenesProduccionRpc dao = new DaoOrdenesProduccionRpc();
                lstLineas = dao.getStockByNameProduction(Integer.parseInt(arg[0]));
                return lstLineas;
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(List<StockMove> lstLines) {
            try {
                stockMove = lstLines.get(1);
                new GuardaVariablesTask().execute();
                loadingDialog.dismissDialog();
            } catch (Exception ex) {

            }
        }
    }

    /**
     * Método de prueba para inicializar los datos del pesaje
     */
    /*private class ActualizaDatosTask extends AsyncTask<List<StockMove>, Void, List<StockMove>> {
        protected List<StockMove> doInBackground(List<StockMove>... arg) {
            try {
                DaoOrdenesProduccionRpc dao = new DaoOrdenesProduccionRpc();

                for(StockMove s : arg[0]) {
                    dao.updateProductQtyByIdProduct(s.getId(), 0.0);
                }

                List<StockMove> lstStockMove = dao.getStockByNameProduction(mrpProduction.getName());
                return lstStockMove;
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(List<StockMove> lstLines) {
            for(StockMove s : lstLines) {
                System.out.println("Alim: " + s.getProductId() + " Pesaje? " + s.getEsperaPesaje() + " Peso: " + s.getKilosPesados());
            }

        }
    }*/
}