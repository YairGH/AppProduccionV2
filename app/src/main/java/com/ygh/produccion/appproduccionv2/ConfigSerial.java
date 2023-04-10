package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigSerialSql;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;

import org.w3c.dom.Text;

public class ConfigSerial extends AppCompatActivity {

    private Spinner spinnerBaudrate;
    private Spinner spinnerDataBits;
    private Spinner spinnerParity;
    private Spinner spinnerStopBits;
    private EditText txtTara;
    private EditText txtBTDevice;

    private DaoConfigSerialSql daoConfigSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_serial);

        daoConfigSerial = new DaoConfigSerialSql(this);

        spinnerBaudrate = (Spinner)findViewById(R.id.spinnerBaudrate);
        spinnerDataBits = (Spinner)findViewById(R.id.spinnerDataBits);
        spinnerParity = (Spinner)findViewById(R.id.spinnerParity);
        spinnerStopBits = (Spinner)findViewById(R.id.spinnerStopBits);
        txtTara = (EditText) findViewById(R.id.txtTara);
        txtBTDevice = (EditText) findViewById(R.id.txtBTDevice);
    }

    public void guardaConfigSerial(View v) {
        int baudRate = Integer.parseInt(spinnerBaudrate.getSelectedItem().toString());
        int dataBits = Integer.parseInt(spinnerDataBits.getSelectedItem().toString());

        int parity;
        if(spinnerParity.getSelectedItemPosition() == 0) {
            parity = UsbSerialPort.PARITY_EVEN;
        } else if(spinnerParity.getSelectedItemPosition() == 1) {
            parity = UsbSerialPort.PARITY_ODD;
        } else {
            parity = UsbSerialPort.PARITY_NONE;
        }

        int stopBits;
        if(spinnerStopBits.getSelectedItemPosition() == 0) {
            stopBits = UsbSerialPort.STOPBITS_1;
        } else if(spinnerStopBits.getSelectedItemPosition() == 1) {
            stopBits = UsbSerialPort.STOPBITS_1_5;
        } else {
            stopBits = UsbSerialPort.STOPBITS_2;
        }

        daoConfigSerial.saveConfigSerial(
                new ConfiguracionSerial(
                        baudRate, parity, stopBits, dataBits, txtTara.getText().toString(), txtBTDevice.getText().toString()
                )
        );

        Toast.makeText(this, "Configuración Puerto Serie Exitosa", Toast.LENGTH_SHORT)
                .show();

        Intent intent = new Intent(ConfigSerial.this, Login.class);
        startActivity(intent);
    }
}