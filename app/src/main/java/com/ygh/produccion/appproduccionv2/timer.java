package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class timer extends AppCompatActivity {

    public static final String PRM_TIEMPO = "prmTiempo";
    TextView texto;
    Contador counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        int prmTiempo = getIntent().getIntExtra(PRM_TIEMPO, 0);

        texto = (TextView)findViewById(R.id.Tiempo);
        //iniciamos el contador  para iniciar la siguiente ventana
        //counter = new Contador(5000,1000);
        counter = new Contador(prmTiempo,1000);

        counter.start();
    }

    @Override
    public void onBackPressed() {

    }

    public void fin(){
        //NavHostFragment.findNavController(FirstFragment.this)
          //      .navigate(R.id.action_FirstFragment_to_SecondFragment);
        //texto.setText("" + "FIN");
        Intent intent = new Intent(timer.this, ProduccionPesaje.class);

        startActivity(intent);

    }


    public void seg(long milseg){
        String minutos = String.format("%2s", TimeUnit.MILLISECONDS.toMinutes(milseg)).replace(' ','0');
        String segundos = String.format("%2s", (int)((milseg/1000)%60)).replace(' ', '0');
        texto.setText(minutos + ":" + segundos);
    }

    public class Contador extends CountDownTimer {

        public Contador(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            fin();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            seg(millisUntilFinished);
        }

    }
}