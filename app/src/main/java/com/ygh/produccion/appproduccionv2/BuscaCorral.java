package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoServidasSql;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuscaCorral extends AppCompatActivity {

    private ConstraintLayout mainLayoutBusqueda;
    private EditText txtCorralBusqueda;
    private TextView lblCorralResult;
    private TextView lblQtyProgResult;
    private Button btnIniciarServida;
    private RmsServidaLine servidaLineResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_corral);

        mainLayoutBusqueda = (ConstraintLayout) findViewById(R.id.mainLayoutBusqueda);
        txtCorralBusqueda = (EditText)findViewById(R.id.txtCorralBusqueda_reparto);
        lblCorralResult = (TextView)findViewById(R.id.lblCorralResult_reparto);
        lblQtyProgResult = (TextView)findViewById(R.id.lblQtyProgResult_reparto);
        btnIniciarServida = (Button)findViewById(R.id.btnIniciarServida);

        btnIniciarServida.setEnabled(false);
    }

    public void buscaInfoCorral(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayoutBusqueda.getWindowToken(), 0);
        btnIniciarServida.setEnabled(false);
        DaoServidasSql dao = new DaoServidasSql(this);
        servidaLineResult = dao.getServidaLineByBusquedaCorral(txtCorralBusqueda.getText().toString());
        if(servidaLineResult != null) {
            if(!servidaLineResult.isProcesada()) {
                lblCorralResult.setText(servidaLineResult.getLotRanchoTxt());
                lblQtyProgResult.setText(servidaLineResult.getQtyProgramada() + "");
                btnIniciarServida.setEnabled(true);
            } else {
                Snackbar.make(mainLayoutBusqueda, "El corral especificado ya ha sido procesado", Snackbar.LENGTH_LONG).setAction("Ok", null).show();
            }
        } else {
            Snackbar.make(mainLayoutBusqueda, "No se ha encontrado el corral especificado", Snackbar.LENGTH_LONG).setAction("Ok", null).show();
        }
    }

    public void iniciaRepartoBusqueda(View v) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String prmCorral = lblCorralResult.getText().toString();
        Intent intent = new Intent(BuscaCorral.this, RepartoCorral.class);
        intent.putExtra(RepartoCorral.PRM_CORRAL, prmCorral);
        intent.putExtra(RepartoCorral.PRM_FH_INICIO, formatter.format(new Date()));
        startActivity(intent);
        return;
    }
}