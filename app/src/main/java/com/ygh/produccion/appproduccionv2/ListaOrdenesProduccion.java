package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.accessibilityservice.FingerprintGestureController;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.ygh.produccion.appproduccionv2.CustomAdapters.AdapterOrdenesRow;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoOrdenesProduccionRpc;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoStockPickingRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenTraslado;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoUsuarioSql;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;
import com.ygh.produccion.appproduccionv2.pojos.StockPicking;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListaOrdenesProduccion extends AppCompatActivity {

    private static long back_pressed;
    private ConstraintLayout mainLayout;
    private List<MrpProduction> lstProduccion;
    private List<StockPicking> lstStockPicking;
    private List<OrdenesProduccionInfo> lstOrdenes;
    private ListView lstOrdenesProduccion;
    private TextView txtTest;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ordenes_produccion);
        getSupportActionBar().setTitle("Órdenes de Producción y Traslado");

        mainLayout = (ConstraintLayout)findViewById(R.id.mLayoutListado);
        lstOrdenesProduccion = (ListView)findViewById(R.id.lstOrdenesProduccion);
        txtTest = (TextView)findViewById(R.id.txtTestLista);
        txtTest.setVisibility(View.GONE);

        loadingDialog = new LoadingDialog(ListaOrdenesProduccion.this);
        loadingDialog.startLoadingDialog("Cargando Ordenes...");
        //new RecuperaOrdenesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try {
            new RecuperaOrdenesTask().execute();
        } catch (Exception ex) {

            // new RecuperaOrdenesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        lstOrdenesProduccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtTest.setText("Valor: " + lstOrdenes.get(i).getOrdenFabricacion() + "-" + lstOrdenes.get(i).getBachada());
                if(lstOrdenes.get(i).getBachada() == 0) {
                    siguienteOrdenProduccion(lstOrdenes.get(i).getIdOrdenProduccion());
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            DaoUsuarioSql daoUsuarioSql = new DaoUsuarioSql(this);
            daoUsuarioSql.updateUsuarioLogOut();
            Intent intent = new Intent(ListaOrdenesProduccion.this, Login.class);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Presiona nuevamente para salir", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    /**
     * Decide si va al flujo de orden de traslado o de orden de producción, dependiente cuál sigue
     *
     * @param v
     */
    public void siguienteOrdenProduccion(View v) {
                try {
                   if (!lstOrdenes.get(0).getOrdenTraslado().equals("")) {
                        guardaOrdenTraslado();
                        Intent intent = new Intent(ListaOrdenesProduccion.this, DetalleOrdenTraslado.class);
                        startActivity(intent);
                    } else {
                        siguienteOrdenProduccion(0);
                    }
                } catch (Exception ex) {

                }
    }

    public void siguienteOrdenProduccion(int idOrden) {
        Intent intent = new Intent(ListaOrdenesProduccion.this, DetallesOrdenProduccion.class);
        intent.putExtra("IdOrden", idOrden);
        startActivity(intent);
    }

    /**
     * Guardo la orden de traslado en local para procesar
     */
    private void guardaOrdenTraslado() {
        OrdenesProduccionInfo o = lstOrdenes.get(0);
        DaoOrdenTraslado daoOrdenTraslado = new DaoOrdenTraslado(this);
        daoOrdenTraslado.saveOrdenTraslado(o.getIdTraslado(), o.getOrdenTraslado(), o.getCantidad(), o.getFormula(), o.getBachada());
    }

    /**
     * Recupero los datos de las ordenes de traslado y producción y las uno en el objeto OrdenesProduccionInfo
     */
    private class RecuperaOrdenesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            try {
                DaoOrdenesProduccionRpc daoP = new DaoOrdenesProduccionRpc();
                DaoStockPickingRpc daoS = new DaoStockPickingRpc();
                lstProduccion = daoP.getOrdenesProduccionByMaquina(Usuario.ID_MAQUINA_REPARTO);
                lstStockPicking = daoS.getStockPickingFromMaquinaId(Usuario.ID_MAQUINA_REPARTO);

                lstOrdenes = new ArrayList<>();
                OrdenesProduccionInfo info = null;

                for(MrpProduction p : lstProduccion) {
                    info = new OrdenesProduccionInfo();
                    info.setBachada(p.getBachada());
                    info.setOrdenFabricacion(p.getName());
                    info.setCantidad(p.getProductQty());
                    info.setFormula(p.getProductName());
                    info.setOrdenTraslado("");
                    info.setIdOrdenProduccion(p.getId());
                    lstOrdenes.add(info);
                }

                for(StockPicking sp : lstStockPicking) {
                    info = new OrdenesProduccionInfo();
                    info.setBachada(sp.getBachada());
                    info.setOrdenFabricacion("");
                    info.setOrdenTraslado(sp.getName());
                    info.setCantidad(sp.getCantidad());
                    info.setFormula(sp.getFormula());
                    info.setIdTraslado(sp.getId());

                    lstOrdenes.add(info);
                }

                Collections.sort(lstOrdenes, new Comparator<OrdenesProduccionInfo>() {
                    @Override
                    public int compare(OrdenesProduccionInfo o1, OrdenesProduccionInfo o2) {
                        Integer p1 = o1.getBachada();
                        Integer p2 = o2.getBachada();
                        int pComp = p1.compareTo(p2);
                        if(pComp != 0) {
                            return pComp;
                        } else {
                            Integer b1 = o1.getBachada();
                            Integer b2 = o2.getBachada();
                            return b1.compareTo(b2);
                        }
                    }
                });

            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void v) {

            try {
                AdapterOrdenesRow adapterOrdenesRow = new AdapterOrdenesRow(
                        ListaOrdenesProduccion.this,
                        R.layout.row_ordenes_produccion,
                        lstOrdenes
                );
                lstOrdenesProduccion.setAdapter(adapterOrdenesRow);
                loadingDialog.dismissDialog();

            } catch (Exception ex) {

            }
        }
    }
}