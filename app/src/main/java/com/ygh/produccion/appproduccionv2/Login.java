package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoOrdenesProduccionRpc;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoStockPickingRpc;
import com.ygh.produccion.appproduccionv2.RpcXml.RpcConn;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigServerSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenTraslado;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenesProduccion;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoUsuarioSql;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;
import com.ygh.produccion.appproduccionv2.pojos.StockPicking;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;


public class Login extends AppCompatActivity {

    private static long back_pressed;
    private EditText txtUsuario;
    private EditText txtPassword;
    private LinearLayout mainLayout;
    private LoadingDialog loadingDialog;

        DaoOrdenesProduccion daoOrdenesProduccion = null;
        DaoOrdenTraslado daoOrdenTraslado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_login);

        loadingDialog = new LoadingDialog(Login.this);

        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        txtUsuario = (EditText)findViewById(R.id.txtUsuario);
        txtPassword = (EditText)findViewById(R.id.txtPassword);

        daoOrdenesProduccion = new DaoOrdenesProduccion(this);
        daoOrdenTraslado = new DaoOrdenTraslado(this);

        //txtUsuario.setText("admin");
        //txtPassword.setText("123456789");
        validaUsuarioActivo();

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.finishAffinity();
        else Toast.makeText(getBaseContext(), "Presiona nuevamente para salir", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_login_config_button) {
            String prmUsername = txtUsuario.getText().toString();
            String prmPassword = txtPassword.getText().toString();

            if(prmUsername.equals("") || prmPassword.equals("")) {
                Snackbar.make(mainLayout, "Debe especificar usuario y contraseña del admin que se va a configurar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            } else {
                Intent intent = new Intent(Login.this, ConfigServer.class);
                intent.putExtra(ConfigServer.USERNAME, prmUsername);
                intent.putExtra(ConfigServer.PASSWORD, prmPassword);
                startActivity(intent);
                return true;
            }
        }

        if(id == R.id.menu_login_config_serial) {
            Intent intent = new Intent(Login.this, ConfigSerial.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validaUsuarioActivo() {
        DaoUsuarioSql daoUsuario = new DaoUsuarioSql(this);
        Usuario u = daoUsuario.getUsuarioByActivo();
        if(u != null) {
            txtUsuario.setText(u.getUsername());
            txtPassword.setText(u.getPassword());
           // actionLogin();
        }
    }

    public void actionLogin() {
        //Hide Keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        DaoConfigServerSql daoServer = new DaoConfigServerSql(this);
        ServerConfig serverConfig = daoServer.getServerDatos();
        if(serverConfig != null) {
            String prmUserName = txtUsuario.getText().toString();
            String prmPassword = txtPassword.getText().toString();

            //Utilizo el 5 parametro prmUserName para identificar si la autenticación a Odoo es de admin o de otro usuario
            if (prmUserName.equals("admin")) {
                try {
                    loadingDialog.startLoadingDialog("Conectando...");
                    new LoginTask().execute(serverConfig.getUrl(), serverConfig.getDb(), prmUserName, prmPassword, prmUserName);
                } catch (Exception ex) {
                    Snackbar.make(mainLayout, "Su usuario o contraseña son incorrectas", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } else {

                DaoUsuarioSql daoUsuario = new DaoUsuarioSql(this);
                Usuario u = daoUsuario.getUsuarioByUsername(prmUserName, prmPassword);
                if (u != null) {
                    Usuario.ID_MAQUINA_REPARTO = u.getIdMaquinaReparto();
                    Usuario.NOMBRE_MAQUINA = u.getNombreMaquinaReparto();

                    DaoConfigServerSql daoConfigServerSql = new DaoConfigServerSql(this);
                    ServerConfig c = daoConfigServerSql.getServerDatos();

                    daoUsuario.updateUsuarioActivo(u.getUsername());

                    loadingDialog.startLoadingDialog("Conectando...");
                    new LoginTask().execute(serverConfig.getUrl(), serverConfig.getDb(), c.getUsername(), c.getPassword(), u.getUsername());
                } else {
                    Snackbar.make(mainLayout, "Su usuario o contraseña son incorrectas", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        } else {
            Snackbar.make(mainLayout, "Debe configurar los datos de server para continuar. Contacte a su usuario admin", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void login(View view) {
        actionLogin();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... arg) {
            try {
                RpcConn rpcConn = new RpcConn(arg[0], arg[1], arg[2], arg[3]);
                //Regresa el username del login. El arg[2] es el admin del server
                return arg[4];
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return "";
        }

        protected void onPostExecute(String username) {
            loadingDialog.dismissDialog();
            if(RpcConn.uid != -1) {
                if(username.equals("admin")){
                    Intent intent = new Intent(Login.this, AltaUsuario.class);
                    startActivity(intent);
                    return;
                } else {

                    Intent intent = new Intent(Login.this, MenuProduccionReparto.class);
                    startActivity(intent);

                    return;
                }
            } else {
                Snackbar.make(mainLayout, "Su usuario o contraseña son incorrectas", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    private class TestInfoPicking extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            try {
           //     DaoStockPickingRpc daoP = new DaoStockPickingRpc();
           //     List<StockPicking> lstStockPicking = daoP.getStockPickingFromMaquinaId(Usuario.ID_MAQUINA_REPARTO);
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }
}