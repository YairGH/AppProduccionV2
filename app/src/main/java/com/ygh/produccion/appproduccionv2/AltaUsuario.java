package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoMaquinasRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoUsuarioSql;
import com.ygh.produccion.appproduccionv2.pojos.RmsMaquinas;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.util.ArrayList;

public class AltaUsuario extends AppCompatActivity {

    private Spinner spiMaquinasReparto;
    private ConstraintLayout mainLayout;
    private EditText txtUsername;
    private EditText txtPassword1;
    private EditText txtPassword2;
    ArrayList<RmsMaquinas> lstMaquinasReparto = null;
    private LoadingDialog loadingDialog = null;
    DaoUsuarioSql daoUsuario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_alta_usuario);

        loadingDialog = new LoadingDialog(AltaUsuario.this);

        mainLayout = (ConstraintLayout)findViewById(R.id.mainAltaUsuarioLayout);
        txtUsername = (EditText)findViewById(R.id.txtAltaUsername);
        txtPassword1 = (EditText)findViewById(R.id.txtAltaPassword1);
        txtPassword2 = (EditText)findViewById(R.id.txtAltaPassword2);
        spiMaquinasReparto = (Spinner)findViewById(R.id.spiMaquinas);

        daoUsuario = new DaoUsuarioSql(this);

        loadingDialog.startLoadingDialog("Cargando Máqiunas...");
        new CargaMaquinasTask().execute();

        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                fillDatosUsuario();
            }
        });
    }

    private void fillDatosUsuario() {
        Usuario u = daoUsuario.getUsuarioByUsername(txtUsername.getText().toString());
        if(u != null) {
            txtPassword1.setText(u.getPassword().toString());
            txtPassword2.setText(u.getPassword().toString());
            for(int i = 0; i < spiMaquinasReparto.getCount(); i++) {
                if(spiMaquinasReparto.getItemAtPosition(i).toString().equals(u.getNombreMaquinaReparto())) {
                    spiMaquinasReparto.setSelection(i);
                }
                Log.d("INFO", "MAQUINA REPARTO: " + spiMaquinasReparto.getItemAtPosition(i).toString());
            }
        }
    }

    private void fillMaquinasReparto() {
        if(lstMaquinasReparto.size() > 0) {
            ArrayAdapter<RmsMaquinas> adapter = new ArrayAdapter<>(
                    this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lstMaquinasReparto
            );

            adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            spiMaquinasReparto.setAdapter(adapter);
        } else {
            Snackbar.make(mainLayout, "No existen máquinas de reparto", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void guardaUsuario(View view) {
        String validaRegistro = validaRegistro();
        if(validaRegistro.equals("")) {
            loadingDialog.startLoadingDialog("Guardando...");
            String prmUsuario = txtUsername.getText().toString();
            String prmPassword1 = txtPassword1.getText().toString();
            RmsMaquinas maquina = (RmsMaquinas) spiMaquinasReparto.getSelectedItem();

            daoUsuario = new DaoUsuarioSql(this);
            daoUsuario.saveUsaurio(
                    prmUsuario, prmPassword1, -1, Usuario.TIPO_REPARTO, maquina.getId(), maquina.getName()
            );
            loadingDialog.dismissDialog();

            Snackbar.make(mainLayout, "¡Registro guardado exitosamente!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AltaUsuario.this, Login.class);
                    startActivity(intent);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "ERROR: " + validaRegistro, Snackbar.LENGTH_LONG).setAction("OK", null).show();
        }
        //Snackbar.make(mainLayout, "Guardado Exitoso!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private String validaRegistro() {
        String usuario = txtUsername.getText().toString();
        String password1 = txtPassword1.getText().toString();
        String password2 = txtPassword2.getText().toString();
        int indexMaquina = spiMaquinasReparto.getSelectedItemPosition();

        if(usuario.equals(""))
            return "Debe seleccionar un nombre de usuario";

        if(password1.equals(""))
            return "Debe especificar un password de acceso";

        if(!password1.equals(password2))
            return "El password y su verificación no coincide";

        if(indexMaquina <= 0) {
            return "Debe seleccionar una máquina de reparto";
        }

        return "";
    }

    /**
     * Cargo las máquinas de reparto
     */
    private class CargaMaquinasTask extends AsyncTask<Void, Void, ArrayList<RmsMaquinas>> {
        protected ArrayList<RmsMaquinas> doInBackground(Void... arg) {
            DaoMaquinasRpc dao = new DaoMaquinasRpc();
            return dao.getAllMaquinas();
        }

        protected void onPostExecute(ArrayList<RmsMaquinas> result) {
            lstMaquinasReparto = result;
            loadingDialog.dismissDialog();
            fillMaquinasReparto();
        }
    }
}