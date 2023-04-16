package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.RpcXml.RpcConn;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigServerSql;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;

public class ConfigServer extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private ConstraintLayout mainLayout;

    private TextView txtUrl = null;
    private TextView txtDb = null;
    private TextView txtServerMQTT = null;
    private TextView txtPortMQTT = null;
    private TextView txtIpApiMqtt = null;

    private String prmUsername = "";
    private String prmPassword = "";
    private String prmServerMQTT = "";
    private String prmPortMQTT = "";


    private DaoConfigServerSql dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_server);

        mainLayout = (ConstraintLayout)findViewById(R.id.mainLayout_configServer);
        txtUrl = (TextView)findViewById(R.id.txtUrlServer);
        txtDb = (TextView)findViewById(R.id.txtServerBD);
        txtServerMQTT = (TextView)findViewById(R.id.txtServerMQTT);
        txtPortMQTT = (TextView)findViewById(R.id.txtPortMQTT);
        txtIpApiMqtt = (TextView)findViewById(R.id.txtIpApiMqtt);

        prmUsername = getIntent().getStringExtra(ConfigServer.USERNAME);
        prmPassword = getIntent().getStringExtra(ConfigServer.PASSWORD);

        dao = new DaoConfigServerSql(this);

     /*   txtUrl.setText("https://engorda.koon.app/");
        txtDb.setText("15eengordaC1");
        txtServerMQTT.setText("108.175.3.161");
        txtPortMQTT.setText("1883");*/

        getInfoServer();
    }

    public void guardaDatosServer(View v) {
        String prmUrl = txtUrl.getText().toString();
        String prmDb = txtDb.getText().toString();
        connectAndSave(prmUrl, prmDb, prmUsername, prmPassword);
    }

    private void connectAndSave(String url, String db, String prmUsername, String prmPassword) {
        new LoginTask().execute(url, db, prmUsername, prmPassword);
    }

    private void getInfoServer() {
        ServerConfig s = dao.getServerDatos();
        if(s != null) {
            txtUrl.setText(s.getUrl());
            txtDb.setText(s.getDb());
            txtServerMQTT.setText(s.getServerMqtt());
            txtPortMQTT.setText(s.getPortMqtt());
            txtIpApiMqtt.setText(s.getIpApiMqtt());
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... arg) {
            try {
                RpcConn rpcConn = new RpcConn(arg[0], arg[1], arg[2], arg[3]);
            } catch (Exception ex) {
                RpcConn.uid = -1;
                System.out.println("ERROR: " + ex.getMessage());
            }
                return null;
        }

        protected void onPostExecute(Void arg) {
            if(RpcConn.uid != -1) {
                String prmUrl = txtUrl.getText().toString();
                String prmDb = txtDb.getText().toString();
                String mqttServer = txtServerMQTT.getText().toString();
                String mqttPort = txtPortMQTT.getText().toString();
                String ipApiMqtt = txtIpApiMqtt.getText().toString();

                String respuesta = dao.saveConfigServer(prmUrl, prmDb, prmUsername, prmPassword, mqttServer, mqttPort, ipApiMqtt);
                if (!respuesta.equals("OK")) {
                    Snackbar.make(mainLayout, "" + respuesta, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(mainLayout, "Configuración guardada exitosamente", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Intent intent = new Intent(ConfigServer.this, Login.class);
                    startActivity(intent);
                }
            } else {
                Snackbar.make(mainLayout, "Favor de revisar su configuración, usuario y contraseña", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }
}