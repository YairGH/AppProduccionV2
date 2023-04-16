package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenTraslado;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenesProduccion;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoServidasSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoUsuarioSql;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.RmsServida;

public class MenuProduccionReparto extends AppCompatActivity {

    private static long back_pressed;
    DaoOrdenesProduccion daoOrdenesProduccion = null;
    DaoOrdenTraslado daoOrdenTraslado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_produccion_reparto);

        daoOrdenesProduccion = new DaoOrdenesProduccion(this);
        daoOrdenTraslado = new DaoOrdenTraslado(this);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            DaoUsuarioSql daoUsuarioSql = new DaoUsuarioSql(this);
            daoUsuarioSql.updateUsuarioLogOut();
            Intent intent = new Intent(MenuProduccionReparto.this, Login.class);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Presiona nuevamente para salir", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    public void btnAppProduccion(View v) {
        goToAppProduccion();
    }

    public void btnAppReparto(View v) {
        goToAppReparto();
    }

    private void goToAppProduccion() {

        OrdenesProduccionInfo ordenTraslado = daoOrdenTraslado.getOrdenTrasladoInfo();
        MrpProduction mrpProduction = daoOrdenesProduccion.getProduccion();

        if(ordenTraslado != null) {
            Intent intent = new Intent(MenuProduccionReparto.this, TrasladoPesaje.class);
            startActivity(intent);
        } else if(mrpProduction != null) {
            Intent intent = new Intent(MenuProduccionReparto.this, ProduccionPesaje.class);
            startActivity(intent);
        }

        Intent intent = new Intent(MenuProduccionReparto.this, ListaOrdenesProduccion.class);
        startActivity(intent);
    }

    private void goToAppReparto() {
        if(checkRepartoAbierto())
            return;

        Intent intent = new Intent(MenuProduccionReparto.this, OrdenesReparto.class);
        startActivity(intent);
    }

    private boolean checkRepartoAbierto() {
        DaoServidasSql daoServidasSql = new DaoServidasSql(this);
        RmsServida currentServida = daoServidasSql.getServidaToProcess();
        if(currentServida != null) {
            /*Snackbar.make(mainLayout, "La servida " + currentServida.getName() + " esta en proceso. ¿Desea continuar?", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuProduccionReparto.this, IniciarCorral.class);
                    startActivity(intent);
                }
            }).show();*/
            Intent intent = new Intent(MenuProduccionReparto.this, IniciarCorral.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}