package com.ygh.produccion.appproduccionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.ygh.produccion.appproduccionv2.BluetoothUtils.BluetoothConnectionService;
import com.ygh.produccion.appproduccionv2.CustomAdapters.AdapterPesajesRow;
import com.ygh.produccion.appproduccionv2.RpcXml.DaoOrdenesProduccionRpc;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigSerialSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigServerSql;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoConfigVariables;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoOrdenesProduccion;
import com.ygh.produccion.appproduccionv2.SqlLiteDao.DaoUsuarioSql;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSeriaConn;
import com.ygh.produccion.appproduccionv2.UsbSerie.UsbSerialAppCompatActivity;
import com.ygh.produccion.appproduccionv2.pojos.ConfigVariablesBascula;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleToIntFunction;

import tw.com.prolific.driver.pl2303g.PL2303GDriver;

public class ProduccionPesaje extends UsbSerialAppCompatActivity {

    MqttAndroidClient mqttClient;
    ConfigServer infoServer;
    IMqttToken token;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    int contadorEnvioMqtt = 0;

    private AdapterPesajesRow adapterLineas;

    //Bandera que indica que se esta inicializando un nuevo insumo
    private boolean isInicializandoInsumo = false;

    //Booleano que controla si esta corriendo el serial
    private boolean isRunningSerial = false;

    //Index auxiliar para la cuenta regresiva de la mezcla
    private int CONTADOR_MEZCLA = 0;

    //Bandera que indica si sigue enviando datos a la alarma
    boolean keepSendingBTData = true;

    int numIntentosDiscovering = 0;
    ConfiguracionSerial configuracionSerial = null;

    //Determina el porcentaje alcanzado para determinar la intensidad del buzzer
    Double PORCENTAJE_ALERTA_1 = 0.0;
    Double PORCENTAJE_ALERTA_2 = 0.0;

    WaitBluetoothResponse waitBluetoothResponse = new WaitBluetoothResponse();
    BuzzerTask buzzerTask = new BuzzerTask();

    Long milisegundosBuzzerPeriodo = 1000L;
    int WAIT_BLUETOOTH_CONNECTION = 5;
    int WATING_BLUETOOTH_SECONDS = 0;
    Boolean deviveNotFound = false;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;

    //Guardo el device al cual me quiero conectar según parametros
    BluetoothDevice mBTDeviceToConnect;

    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final String TAG = "ProduccionPesaje";

    //Peso minimo para saltarse la bascula e ir al siguiente insumo de manera automática
    private Double PESAJE_MINIMO = 0.0;

    //El tiempo de espera para estabilizar peso y pasar al siguiente insumo en caso de que se automático
    private int SEGUNDOS_TO_SIGUIENTE_INSUMO = 0;

    //Indica si el pase al siguiente insumo es automático o con botón de manera manual
    private String TIPO_SIGUIENTE_INSUMO = "";

    //Si es automático si es por porcentaje o cantidad
    private String TIPO_AVANCE_INSUMO = "";

    //La cantidad en porcentaje o cantidad para pasar al siguiente insumo
    private Double CANTIDAD_TOLERANCIA = 0.0;

    private Boolean BUZZER = false;
    private String DONE_HEADER = "";
    private Boolean botonPresionado = false;

    private Boolean enviarBuzzerHeader = false;
    private Boolean isSiguienteSinBascula = false;

    private static String AUTO = "auto";
    private static String MANUAL = "manual";

    private static String PORCENTAJE = "percent";
    private static String VALOR = "value";

    private final static NumberFormat intFormat = NumberFormat.getNumberInstance();
    private DecimalFormat decimalFormat = null;

    private int indexInsumo;

    private MrpProduction mrpProduction;
    private StockMove stockMove;
    private List<StockMove> lstStockMoves;

    private TextView lblPesoReal;
    private TextView lblPesoProgramadoProd;
    private TextView txtTest;
    private TextView txtTiempoV2;
    private ListView lstInsumos;
    private Button btnGuardarProduccion;

    private TextView txtTiempoText;

    private Double cantProgramada;
    private int cantRestante;
    private Double cantPesada;
    private int cantAnteriorRecibida;
    private int conteoSegundos;

    private Boolean isRunningConteo = false;

    private LinearLayout layoutPesoProgramadoProduccion;
    private LinearLayout layoutPesoRealProduccion;
    private LinearLayout layoutCuentaRegresiva;
    private LinearLayout contentLayout;

    private LoadingDialog loadingDialog;

    DaoOrdenesProduccion daoOrdenesProduccion;
    private DaoConfigVariables daoConfigVariables;

    private UsbSeriaConn usbSeriaConn;
    private ConstraintLayout layoutRepartoMain;

    private String comandoTara = "";

    private ConteoSiguienteInsumoTask conteoSiguienteInsumoTask;

    private Usuario usuarioActivo;

    private DaoConfigServerSql serverDao = null;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     * Si no se detecta el device en los segundos indicados comienza la lectura de peso
     * Si detecta el device comienza el listener del Bluetooth
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");


            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                txtTest.setText("Buscando Dispositivo Alarma...");
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                //Toast.makeText(context, "Dispositivo: " + device.getName() + ": " + device.getAddress(), Toast.LENGTH_SHORT).show();

                if(device.getName() != null && device.getName().contains(configuracionSerial.getBtDevice())) {
                    txtTest.setText("Conectando Alarma...");
                    mBTDeviceToConnect = device;
                    waitBluetoothResponse.cancel(true);
                    pairDevice();
                    startBTConnection(mBTDeviceToConnect, MY_UUID_INSECURE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        buzzerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    else
                        buzzerTask.execute();
                    comienzaLectura();

                    //Toast.makeText(context, "Comenzando LECTURA" + device.getAddress(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        usbSeriaConn.stopUsbSerialListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produccion_pesaje);
        getSupportActionBar().setTitle("Producción");

        intFormat.setGroupingUsed(true);

        lblPesoReal = (TextView)findViewById(R.id.lblPesoReal);
        lblPesoProgramadoProd = (TextView)findViewById(R.id.lblPesoProgramadoProd);
        txtTest = (TextView)findViewById(R.id.txtTest);
        txtTiempoV2 = (TextView)findViewById(R.id.txtTiempoV2);
        txtTiempoText = (TextView)findViewById(R.id.txtTiempoText);
        lstInsumos = (ListView)findViewById(R.id.lstInsumos);
        btnGuardarProduccion = (Button)findViewById((R.id.btnGuardarProduccion));

        layoutPesoProgramadoProduccion = (LinearLayout)findViewById(R.id.layoutPesoProgramadoProduccion);
        layoutPesoRealProduccion = (LinearLayout)findViewById(R.id.layoutPesoRealProduccion);
        layoutRepartoMain = (ConstraintLayout) findViewById(R.id.layoutRepartoMain);

        layoutCuentaRegresiva = (LinearLayout)findViewById(R.id.layoutCuentaRegresiva);
        contentLayout = (LinearLayout)findViewById(R.id.contentLayout);

        daoOrdenesProduccion = new DaoOrdenesProduccion(this);
        daoConfigVariables = new DaoConfigVariables(this);

        DaoUsuarioSql daoUsuario = new DaoUsuarioSql(this);
        usuarioActivo = daoUsuario.getUsuarioByActivo();

        serverDao = new DaoConfigServerSql(this);

        setConfigVariables();

        layoutCuentaRegresiva.setVisibility(View.INVISIBLE);
        contentLayout.setVisibility(View.VISIBLE);
        try {
            inicializaMQTT();
        } catch (Exception ex) {
            Toast.makeText(this, "ERROR MQTT: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        inicializaPesajeInsumo();
    }

    private void inicializaMQTT() throws Exception {
        DaoConfigServerSql serverDao = new DaoConfigServerSql(this);
        ServerConfig infoServer = serverDao.getServerDatos();

        mqttClient = new MqttAndroidClient(
                this.getApplicationContext(),
                "tcp://" + infoServer.getServerMqtt() + ":" + infoServer.getPortMqtt(),
                MqttClient.generateClientId());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        token = mqttClient.connect();
    }

    /**
     * Inicializa variables para inciar pesaje de nuevo insumo
     */
    private void inicializaPesajeInsumo() {
        isInicializandoInsumo = true;
        botonPresionado = false;

        contadorEnvioMqtt = 0;

        layoutCuentaRegresiva.setVisibility(View.INVISIBLE);
        contentLayout.setVisibility(View.VISIBLE);

        txtTiempoV2.setText("00:00");

        milisegundosBuzzerPeriodo = 1000L;

        /****Bluethooth OnCreate***/
        if(BUZZER) {
            numIntentosDiscovering = 0;

            WATING_BLUETOOTH_SECONDS = 0;
            deviveNotFound = false;

            checkPermissions();
            //Broadcasts when bond state changes (ie:pairing)
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver4, filter);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBTDevices = new ArrayList<>();
        }

        //     enableBT();
        /****Bluethooth OnCreate***/
        getInfoProduccion();

        cantAnteriorRecibida = 0;

        conteoSegundos = SEGUNDOS_TO_SIGUIENTE_INSUMO;

        //Bandera para decidir si enviar o no el comando tara al guardar
        //Si el insumo pasa sin bascula no se activo el serial
        isSiguienteSinBascula = false;

        if(fillInitialData()) {
            //Obtengo los segundos de mezcla del producto

            //Primero se realiza el proceso de dispositivo y posterior se inicia la lectura de la báscula.
            //Dentro del método discoverDevices al terminar inicia la lectura
            if (BUZZER) {
                if (mBTDevice != null && !mBTDevice.getName().equals(""))
                    comienzaLectura();
                else
                    discoverDevices();
            } else {
                comienzaLectura();
            }
        }
    }

    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void getInfoProduccion() {
        daoOrdenesProduccion = new DaoOrdenesProduccion(this);
        mrpProduction = daoOrdenesProduccion.getProduccion();
        lstStockMoves = daoOrdenesProduccion.getStockMove();
    }

    private boolean fillInitialData() {
        if(fillListInsumos()) {
            cantRestante = stockMove.getQtyProgramada().intValue();
            cantProgramada = stockMove.getQtyProgramada();
            lblPesoProgramadoProd.setText(intFormat.format(cantRestante));
            lblPesoReal.setText("0");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Llena la información de pesado en cada cambio de insumo.
     * Si ya se pesó el ultimo insumo se guarda la inforamción en odoo
     * @return
     */
    private boolean fillListInsumos() {
        try {
            indexInsumo = -1;
            //Recorre, si el pesaje de insumo = 0 lo setea como insumo actual para que se pinte de verde en el adpater
            //Cuando el indexInusmo = -1 quiere decir que se termino pesaje
            for (int i = 0; i < lstStockMoves.size(); i++) {
                if (lstStockMoves.get(i).getQtyPesada() == 0) {

                    //Si es el primer insumo activo bandera para enviar comando buzzer
                    if (i == 0) {
                        if (BUZZER) {
                            enviarBuzzerHeader = true;
                        }
                    }

                    lstStockMoves.get(i).setInsumoActual(true);
                    stockMove = lstStockMoves.get(i);
                    indexInsumo = i;
                    break;
                }
            }

            if (indexInsumo == -1) {
                loadingDialog = new LoadingDialog(this);

                loadingDialog.startLoadingDialog("Guardando Información...");
                txtTest.setText("Guardando información...");

                try {
                    new updateCantidadesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception ex) {
                    txtTest.setText("Llama update: " + ex.getMessage());
                }
                return false;
            } else {
                if (indexInsumo == lstStockMoves.size() - 1) {
                    btnGuardarProduccion.setText("Terminar Producción");
                }

                adapterLineas = new AdapterPesajesRow(
                        ProduccionPesaje.this,
                        R.layout.row_insumos_produccion,
                        lstStockMoves
                );
                lstInsumos.setAdapter(adapterLineas);
                lstInsumos.setSelection(indexInsumo);
                lstInsumos.setEnabled(false);

                return true;
            }
        } catch (Exception ex) {
//            txtTest.setText(ex.getMessage());
        }
        return false;
//        new ListaInsumosTask().execute(mrpProduction.getName());
    }

    public void siguienteInsumo(View v) {
        botonPresionado = true;
        siguienteInsumoAction();
    }

    private void siguienteInsumoAction() {
        try {
            CONTADOR_MEZCLA = (int)(stockMove.getTiempoMezclado() * 60) * 1000;
            if (!isSiguienteSinBascula) {
                usbSeriaConn.writeToSerial("\u001b" + "Re-99999" + "\u0004");
                usbSeriaConn.writeToSerial("\u001a" + comandoTara);
              //  usbSeriaConn.stopUsbSerialListener();
            }
            milisegundosBuzzerPeriodo = -1L;
            keepSendingBTData = false;

            if(!stockMove.getPrePesado())
                daoOrdenesProduccion.updateStockMove(stockMove.getId(), cantPesada);
            else
                daoOrdenesProduccion.updateStockMove(stockMove.getId(), cantProgramada);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutCuentaRegresiva.setVisibility(View.VISIBLE);
                    contentLayout.setVisibility(View.INVISIBLE);
                    Contador counter = new Contador(CONTADOR_MEZCLA,1000);
                    counter.start();
                }
            });

        } catch (Exception ex) {
            txtTiempoText.setText(ex.getMessage());
        }
    }

    @Override
    public void setSerialMessage(String message) {
        try {
            if(!isInicializandoInsumo) {
                message = message.replaceAll("[^0-9]+", "");
                if (!message.equals("")) {
                    try {

                        int cantidadPesada = Integer.parseInt(message);
                        addCantidad(cantidadPesada);
                        cantAnteriorRecibida = cantidadPesada;

                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception ex) {
                    }
                }
            }

        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "EX: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Para pruebas
     * @param v
     */
    public void add100(View v) {
        addCantidad(100);
    }

    private Boolean isInRangoTolerancia(int cantRecibida) {
        if(TIPO_AVANCE_INSUMO.equals(ProduccionPesaje.PORCENTAJE)) {
            int cantTolerancia = (int)(cantProgramada * (CANTIDAD_TOLERANCIA / 100));
            if(cantRecibida >= (cantProgramada - cantTolerancia)) {
                return true;
            } else {
                return false;
            }
        } else {
            if(cantRecibida >= (cantProgramada - CANTIDAD_TOLERANCIA))
                return true;
            else
                return false;
        }
    }

    /**
     * Si el cambio de insumo es AUTO realiza el conteo de sugundos que no cambie el indicador de peso
     * La información de la bascula se recibe cada segundo.
     * @param cantRecibida
     */
    private void addCantidad(int cantRecibida) {
        try {
            if(!botonPresionado && !isInicializandoInsumo) {
                cantPesada = Integer.valueOf(cantRecibida).doubleValue();

                if (TIPO_SIGUIENTE_INSUMO.equals(ProduccionPesaje.AUTO) && !botonPresionado) {
                    if (isInRangoTolerancia(cantRecibida) && (cantRecibida == cantAnteriorRecibida)) {
                        if (conteoSegundos == 0) {
                            txtTest.setText("Procesando...");
                            botonPresionado = true;
                            siguienteInsumoAction();
                            return;
                        } else if (!botonPresionado) {
                            conteoSegundos--;
                            txtTest.setText("Estabilizando peso. Siguiente insumo en: " + conteoSegundos + " segundos...");
                        } else {
                            return;
                        }
                    } else {
                        conteoSegundos = SEGUNDOS_TO_SIGUIENTE_INSUMO;
                    }
                }

                int qtyRestante = cantRestante - cantRecibida;
                lblPesoReal.setText(intFormat.format(cantRecibida) + " Kg");
                lblPesoProgramadoProd.setText(intFormat.format(qtyRestante) + " Kg");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new EnviaMqttTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cantRecibida + "");
                        } catch (Exception ex) {
                            txtTest.setText(ex.getMessage());
                        }
                    }
                });

                updateColors();
            }
        } catch (Exception ex) {}
    }


    private void updateColors() {
        double porcentaje = 0;

        porcentaje = (cantPesada * 100) / cantProgramada;

        if(porcentaje >= PORCENTAJE_ALERTA_1 && porcentaje < (100 - PORCENTAJE_ALERTA_2)) {
            milisegundosBuzzerPeriodo = 500L;
            keepSendingBTData = true;
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

            layoutPesoRealProduccion.setBackgroundColor(ContextCompat.getColor(ProduccionPesaje.this, android.R.color.holo_green_light));
            //layoutPesoProgramadoReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.white));
        } else if(porcentaje >= (100 - PORCENTAJE_ALERTA_2) && porcentaje < 100) {
            milisegundosBuzzerPeriodo = 333L;
            keepSendingBTData = true;
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

            layoutPesoRealProduccion.setBackgroundColor(ContextCompat.getColor(ProduccionPesaje.this, android.R.color.holo_orange_light));
            //layoutPesoProgramadoReparto.setBackgroundColor(ContextCompat.getColor(RepartoCorral.this, android.R.color.white));
        } else if(porcentaje >= 100) {
            milisegundosBuzzerPeriodo = 0L;
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300);

            layoutPesoRealProduccion.setBackgroundColor(ContextCompat.getColor(ProduccionPesaje.this, android.R.color.holo_red_light));
            layoutPesoProgramadoProduccion.setBackgroundColor(ContextCompat.getColor(ProduccionPesaje.this, android.R.color.holo_red_light));
        }
    }

    private class updateCantidadesTask extends  AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            try {
                DaoOrdenesProduccionRpc daoOrdenesProduccionRpc = new DaoOrdenesProduccionRpc();
                int cantTotalInsumos = 0;
                boolean result = false;
                //Actualiza insumos (líneas)
                for(StockMove s : lstStockMoves) {
                /*    result = daoOrdenesProduccionRpc.updateInsumoOrdenProduccion(
                            s.getId(), (double)s.getQtyPesada()
                    );*/
                    cantTotalInsumos += s.getQtyPesada();
                    System.out.println("RESULT: " + result);
                }
                //Actualiza el header
                System.out.println("ACTUALIZANDO ORDEN PRODUCCION QTY = " + cantTotalInsumos);
                result = daoOrdenesProduccionRpc.updateOrdenProduccionQty(mrpProduction.getId(), cantTotalInsumos, usuarioActivo.getUsername());
                System.out.println("Result = " + result);

                for(StockMove s : lstStockMoves) {
                    result = daoOrdenesProduccionRpc.updateInsumoOrdenProduccion(
                            s.getId(), (double)s.getQtyPesada()
                    );
                    cantTotalInsumos += s.getQtyPesada();
                    System.out.println("RESULT: " + result);
                }

                daoOrdenesProduccionRpc.procesaOrdenProduccion(mrpProduction.getId());

                //Actualiza el status de la produccion
                //daoOrdenesProduccionRpc.procesaOrdenProduccion(mrpProduction.getId());

            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            try {
                loadingDialog.dismissDialog();
                daoOrdenesProduccion.cleanDbOrdenesProduccion();
                //buzzerTask.cancel(true);
                //waitBluetoothResponse.cancel(true);
                usbSeriaConn.stopUsbSerialListener();
                Intent intent = new Intent(ProduccionPesaje.this, ListaOrdenesProduccion.class);
                startActivity(intent);

                //ProcessPhoenix.triggerRebirth(getApplicationContext());
                return;
            } catch (Exception ex) {
                //ProcessPhoenix.triggerRebirth(getApplicationContext());
                return;
            }
        }
    }

   /* private class ListaInsumosTask extends AsyncTask<String, Void, List<StockMove>> {
        protected List<StockMove> doInBackground(String... arg) {
            try {
                DaoOrdenesProduccionRpc dao = new DaoOrdenesProduccionRpc();
                List<StockMove> lstStkMoveLineas = dao.getStockByNameProduction(arg[0]);
                return lstStkMoveLineas;
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(List<StockMove> lstStkMoveLineas) {
            lstStockMoves = lstStkMoveLineas;

            for(int i = 0; i < lstStockMoves.size(); i++) {
                if(lstStockMoves.get(i).getProductNumId() == stockMove.getProductNumId()) {
                    lstStockMoves.get(i).setInsumoActual(true);
                } else {
                    lstStockMoves.get(i).setInsumoActual(false);
                }
            }

            AdapterPesajesRow adapterLineas = new AdapterPesajesRow(
                    ProduccionPesaje.this,
                    R.layout.row_insumos_produccion,
                    lstStockMoves
            );
            lstInsumos.setAdapter(adapterLineas);
        }
    }*/

    /**
     * obtiene datos de configuración inicial de la orden
     */
    private void setConfigVariables() {
        ConfigVariablesBascula configVariablesBascula = daoConfigVariables.getConfigVariables();

        Double tiempoEspera = configVariablesBascula.getTiempoSiguienteInsumo();
        Integer hrs = tiempoEspera.intValue();
        //tiempoEspera = 0.083333;
        SEGUNDOS_TO_SIGUIENTE_INSUMO = (int)(((tiempoEspera) * 60));

        PESAJE_MINIMO = configVariablesBascula.getPesoMinimo();
        TIPO_SIGUIENTE_INSUMO = configVariablesBascula.getTipoSigInsumo();
        TIPO_AVANCE_INSUMO = configVariablesBascula.getTipoMedidaSigInsumo();
        CANTIDAD_TOLERANCIA = configVariablesBascula.getValorToSigInsumo();
        PORCENTAJE_ALERTA_1 = configVariablesBascula.getPrealarm1();
        PORCENTAJE_ALERTA_2 = configVariablesBascula.getPrealarm2();

        //tiempoEspera = 0.00111111;
        //hrs = tiempoEspera.intValue();

        //PESAJE_MINIMO = 0.0;
        //TIPO_SIGUIENTE_INSUMO = ProduccionPesaje.AUTO;
        //TIPO_AVANCE_INSUMO = ProduccionPesaje.PORCENTAJE;
        //CANTIDAD_TOLERANCIA = 10.00;

        //BUZZER = configVariablesBascula.getBuzzer();
        BUZZER = false;
        //DONE_HEADER = configVariablesBascula.getDoneHeader();

        //No se utiliza, para test solamente:
        //BUZZER = true;
        //DONE_HEADER = "N6     U G T B4   L6     R6     P6     A6     I8       C5    F D8       H6     E6     Z M6     W6     m3  t3 |c";

       // TIPO_SIGUIENTE_INSUMO = "manual";
        //Si es auto se esconde el botón y el cambio de insumo se debe realizar en automático
        if(TIPO_SIGUIENTE_INSUMO.equals(ProduccionPesaje.AUTO)) {
            btnGuardarProduccion.setVisibility(View.INVISIBLE);
        } else {
            txtTest.setVisibility(View.INVISIBLE);
        }

        DaoConfigSerialSql daoConfigSerialSql = new DaoConfigSerialSql(this);
        configuracionSerial = daoConfigSerialSql.getConfiguracionSerial();
    }

    public void testBtn(View v) {

    }

    private void comienzaLectura() {
        keepSendingBTData = true;
        txtTest.setText("Lectura...");
        if(!deviveNotFound) {
            Toast.makeText(this, "COMENZANDO LECTURA", Toast.LENGTH_SHORT).show();
        }
        else {
            if(BUZZER)
               Toast.makeText(this, "COMENZANDO LECTURA - Dispositivo alarma no encontrado", Toast.LENGTH_SHORT).show();
        }
        try {

            if(cantProgramada > PESAJE_MINIMO) {
                if(!isRunningSerial) {
                    if (configuracionSerial != null) {

                        UsbSeriaConn.BAUDRATE = configuracionSerial.getBaudrate();
                        UsbSeriaConn.BYTESIZE = configuracionSerial.getBytesize();
                        UsbSeriaConn.PARITY = configuracionSerial.getParity();
                        UsbSeriaConn.STOPBIT = configuracionSerial.getStopbit();
                        comandoTara = configuracionSerial.getTara();

                    } else {
                        Toast.makeText(this, "Puerto serie no configurado", Toast.LENGTH_LONG).show();
                    }
                    usbSeriaConn = new UsbSeriaConn(
                            (UsbManager) getSystemService(Context.USB_SERVICE),
                            this
                    );

                    if (usbSeriaConn.creatUsbSerialConn()) {
                     //   Snackbar.make(layoutRepartoMain, "Conexión Serial Exitosa", Snackbar.LENGTH_SHORT).show();
                        usbSeriaConn.startUsbSerialListener();
                    } else {
                        Toast.makeText(this, "Conexión Serial No Detectada", Toast.LENGTH_SHORT).show();

                        try {
                            mBluetoothConnection.stopClient();
                            unregisterReceiver(mBroadcastReceiver4);
                            unregisterReceiver(mBroadcastReceiver3);
                            unregisterReceiver(mBroadcastReceiver1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        Intent intent = new Intent(ProduccionPesaje.this, ListaOrdenesProduccion.class);
                        startActivity(intent);
                        return;
                    }
                    isRunningSerial = true;
                }

                usbSeriaConn.writeToSerial("\u001a" + comandoTara);
                isInicializandoInsumo = false;

            } else {
                cantPesada = cantProgramada;
                lblPesoReal.setText(cantProgramada + "KG");
                lblPesoProgramadoProd.setText("0 KG");
                isSiguienteSinBascula = true;
                //TimeUnit.SECONDS.sleep(SEGUNDOS_TO_SIGUIENTE_INSUMO);
                siguienteInsumoAction();
            }
        } catch (Exception ex) {
            Snackbar.make(layoutRepartoMain, "ERROR: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class ConteoSiguienteInsumoTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... param) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception ex) {
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean isValidoSiguiente) {

            if(isValidoSiguiente) {
                usbSeriaConn.stopUsbSerialListener();
            }
            isRunningConteo = false;
        }
    }

    /******************Bluetooth Methods*****/

    /**
     * Se activa BT en caso de que este desactivado
     */
    public void enableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    /**
     * Activa el proceso de listar dispositivos para encontrar la ALARMA
     */
    public void discoverDevices() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        //Toast.makeText(this, "Buscando Dispostivos", Toast.LENGTH_SHORT).show();
        txtTest.setText("Buscando dispositivos alarma...");

            try {
                mBluetoothAdapter.isDiscovering();
            } catch (Exception ex) {
                Log.d(TAG, "EX Try: " + ex.getMessage());
            }

            try {
                if (mBluetoothAdapter.isDiscovering()) {
                    Log.d(TAG, "CANELLING1");
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "btnDiscover: Canceling discovery.");

                    //check BT permissions in manifest
                    checkBTPermissions();

                    /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }*/

                    //Comienza el broadcast
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                }
            } catch (Exception ex2) {
                Log.d(TAG, "Ex2: " + ex2.getMessage());
            }

            try {
                if (!mBluetoothAdapter.isDiscovering()) {
                    Log.d(TAG, "Discovering 1");
                    //check BT permissions in manifest
                    checkBTPermissions();
                    Log.d(TAG, "Discovering 2");
                    waitBluetoothResponse.execute();
                    Log.d(TAG, "Discovering 3");
                    mBluetoothAdapter.startDiscovery();
                    Log.d(TAG, "Discovering 4");
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                    Log.d(TAG, "Discovering 5");
                }
            } catch (Exception ex) {
                Log.d(TAG, "EX3: " + ex.getMessage());
                waitBluetoothResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * Parea el device de BT
     */
    private void pairDevice() {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + mBTDeviceToConnect.getName());
            mBTDeviceToConnect.createBond();

            mBTDevice = mBTDeviceToConnect;
            mBluetoothConnection = new BluetoothConnectionService(ProduccionPesaje.this);
        }
    }

    /**
     * Inicia conexión del device encontrado
     * @param device
     * @param uuid
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection = new BluetoothConnectionService(ProduccionPesaje.this);

        mBluetoothConnection.startClient(device, uuid);
    }

    /**
     * milisegundosBuzzerPeriodo > 0 -- Se mantiene enviando el beep con 2
     * milisegundosBuzzerPeriodo = 0 -- Se envío un '1' para dejar el beep contínuo
     * milisegundosBuzzerPeriodo == -1 -- Envío un '2' nuevamente para silenciar al guardar
     * milisegundosBuzzerPeriodo == -2 -- Dejo de enviar datos
     */
    private void enviaBuzzer() {
        String comando = "";

        if(milisegundosBuzzerPeriodo > 0)
            comando = "2";
        else if(milisegundosBuzzerPeriodo == 0) {
            comando = "1";
        }
        else if(milisegundosBuzzerPeriodo == -1L) {
            comando = "2";

        }

        byte[] bytes = comando.getBytes(Charset.defaultCharset());
        try {
            if (keepSendingBTData) {
                mBluetoothConnection.write(bytes);
                if (comando.equals("1"))
                    keepSendingBTData = false;
            } else {
                if (milisegundosBuzzerPeriodo == -1L) {
                    mBluetoothConnection.write(bytes);
                    milisegundosBuzzerPeriodo = 0L;
                }
            }


            /*if(comando.equals("1")) {
                buzzerTask.cancel(true);
            }*/
                Log.d("BUZZER:", comando);

        } catch(Exception ex) {
            ex.printStackTrace();
            Log.d("BUZZER:", ex.getMessage());
        }
    }

    /****************Bluetooth Methods*****/

    private class WaitBluetoothResponse extends  AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... arg) {
            deviveNotFound = false;
            while(!isCancelled()) {
                try {
                    WATING_BLUETOOTH_SECONDS++;
                    TimeUnit.SECONDS.sleep(1);
                    Log.d("Waiting","Waiting... " + WATING_BLUETOOTH_SECONDS);

                    if (WATING_BLUETOOTH_SECONDS == WAIT_BLUETOOTH_CONNECTION) {
                        deviveNotFound = true;
                        return true;
                    }
                } catch(Exception ex) {}
            }
            return deviveNotFound;
        }

        protected void onPostExecute(Boolean deviceNotFound) {
            Log.d(TAG, "Device Not Found");
            if(numIntentosDiscovering > 4) {
                if (deviceNotFound)
                    comienzaLectura();
                else
                    return;
            } else {
                Log.d(TAG,"Trying Again");
                numIntentosDiscovering++;
                WATING_BLUETOOTH_SECONDS = 0;
                tryAgain();
            }
        }
    }

    private void tryAgain() {
        waitBluetoothResponse.cancel(true);
        waitBluetoothResponse = new WaitBluetoothResponse();
        discoverDevices();
    }

    private class BuzzerTask extends  AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... arg) {
            while(!isCancelled()) {
                try {
                    enviaBuzzer();
                    Thread.sleep(milisegundosBuzzerPeriodo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }

    /**
     * Conteo regresivo de meclado
     */
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

    public void seg(long milseg){
        String minutos = String.format("%2s", TimeUnit.MILLISECONDS.toMinutes(milseg)).replace(' ','0');
        String segundos = String.format("%2s", (int)((milseg/1000)%60)).replace(' ', '0');
        txtTiempoV2.setText(minutos + ":" + segundos);
    }

    public void fin(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inicializaPesajeInsumo();
            }
        });
    }

    private class EnviaMqttTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... arg) {
            try {

                ServerConfig configServer = serverDao.getServerDatos();

                URL url = new URL("http://" + configServer.getIpApiMqtt() + ":8082/api/send_mqtt");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("maquina", usuarioActivo.getNombreMaquinaReparto().replaceAll(" ", ""));
                jsonParam.put("producto", stockMove.getProductId());
                jsonParam.put("pesoProgramado", cantProgramada + "");
                jsonParam.put("pesoReal", arg[0].toString());
                jsonParam.put("ipServer", configServer.getServerMqtt());
                jsonParam.put("puertoServer", configServer.getPortMqtt());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                System.out.println(conn.getResponseCode());
                System.out.println(conn.getResponseMessage());

                conn.disconnect();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }
}