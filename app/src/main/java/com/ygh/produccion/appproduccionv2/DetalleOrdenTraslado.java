package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenTraslado;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

public class DetalleOrdenTraslado extends AppCompatActivity {

    private OrdenesProduccionInfo ordenTraslado = null;

    private TextView lblOrdenTraslado;
    private TextView lblMaquinaTraslado;
    private TextView lblBachadaTraslado;
    private TextView lblFormulaTraslado;
    private TextView lblQtyTraslado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detalle_orden_traslado);

        lblOrdenTraslado = findViewById(R.id.lblOrdenTraslado);
        lblMaquinaTraslado = findViewById(R.id.lblMaquinaTraslado);
        lblBachadaTraslado = findViewById(R.id.lblBachadaTraslado);
        lblFormulaTraslado = findViewById(R.id.lblFormulaTraslado);
        lblQtyTraslado = findViewById(R.id.lblQtyTraslado);

        cargaOrdenTraslado();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetalleOrdenTraslado.this, ListaOrdenesProduccion.class);
        startActivity(intent);
    }

    private void cargaOrdenTraslado() {
        DaoOrdenTraslado daoOrdenTraslado = new DaoOrdenTraslado(this);
        ordenTraslado = daoOrdenTraslado.getOrdenTrasladoInfo();
        lblOrdenTraslado.setText(ordenTraslado.getOrdenTraslado());
        lblBachadaTraslado.setText(ordenTraslado.getBachada() + "");
        lblFormulaTraslado.setText(ordenTraslado.getFormula());
        lblQtyTraslado.setText(ordenTraslado.getCantidad() + "");
        lblMaquinaTraslado.setText(Usuario.NOMBRE_MAQUINA);
    }

    public void btnIniciarPesajeTraslado(View v) {
        Intent intent = new Intent(DetalleOrdenTraslado.this, TrasladoPesaje.class);
        startActivity(intent);
    }
}