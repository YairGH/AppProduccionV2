package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.ConfigVariablesBascula;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "produccion_app.db";
    private static final int DATABASE_VERSION = 19;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String DATABASE_CREATE_CONFIG_VARIABLES = "create table "
            + ConfigVariablesBascula.TABLE_CONFIG_VARIABLES + "( "
            + ConfigVariablesBascula.COLUMN_PESO_MINIMO + " REAL, "
            + ConfigVariablesBascula.COLUMN_TIPO_SIG_INSUMO + " TEXT, "
            + ConfigVariablesBascula.COLUMN_TIPO_MEDIDA + " TEXT, "
            + ConfigVariablesBascula.COLUMN_MEDIDA_VALOR + " REAL, "
            + ConfigVariablesBascula.COLUMN_TIEMPO_SIG_INSUMO + " REAL, "
            + ConfigVariablesBascula.COLUMN_BUZZER + " INTEGER, "
            + ConfigVariablesBascula.COLUMN_DONE_HEADER + " TEXT, "
            + ConfigVariablesBascula.COLUMN_PREALARM1 + " REAL, "
            + ConfigVariablesBascula.COLUMN_PREALARM2 + " REAL "
            + ")";

    private static final String DATABASE_CREATE_CONFIG_SERIAL = "create table "
            + ConfiguracionSerial.TABLE_CONFIG_SERIAL + "( "
            + ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BAUDRATE + " INTEGER, "
            + ConfiguracionSerial.COLUMN_CONFIG_SERIAL_STOPBIT + " INTEGER, "
            + ConfiguracionSerial.COLUMN_CONFIG_SERIAL_PARITY + " INTEGER, "
            + ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BYTESIZE + " INTEGER, "
            + ConfiguracionSerial.COLUMN_CONFIG_SERIAL_TARA + " TEXT, "
            + ConfiguracionSerial.COLUMN_BT_DEVICE + " TEXT "
            + ")";

    private static final String DATABASE_CREATE_USUARIO = "create table "
            + Usuario.TABLE_USUARIO + "( "
            + Usuario.COLUMN_USUARIO_USERNAME + " TEXT, "
            + Usuario.COLUMN_USUARIO_PASSWORD + " TEXT, "
            + Usuario.COLUMN_USUARIO_ID_MAQUINA + " INTEGER, "
            + Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO + " INTEGER, "
            + Usuario.COLUMN_USUARIO_TIPO_USUARIO + " INTEGER, "
            + Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO + " TEXT, "
            + Usuario.COLUMN_USUARIO_ACTIVO + " INTEGER "
            + ")";

    private static final String DATABASE_CREATE_SERVER_CONFIG = "create table "
            + ServerConfig.TABLE_SERVER_CONFIG + "( "
            + ServerConfig.COLUMN_SERVER_URL + " TEXT, "
            + ServerConfig.COLUMN_SERVER_DB + " TEXT, "
            + ServerConfig.COLUMN_SERVER_USERNAME + " TEXT, "
            + ServerConfig.COLUMN_SERVER_PASSWORD + " TEXT, "
            + ServerConfig.COLUMN_SERVER_MQTT + " TEXT, "
            + ServerConfig.COLUMN_PORT_MQTT + " TEXT, "
            + ServerConfig.COLUMN_IP_API_MQTT + " TEXT "
            + ")";

    private static final String DATABASE_CREATE_ORDEN_TRASLADO = "create table "
            + OrdenesProduccionInfo.TABLE_ORDEN_TRASLADO + "( "
            + OrdenesProduccionInfo.COLUMN_TRASLADO_ORDEN + " TEXT, "
            + OrdenesProduccionInfo.COLUMN_TRASLADO_ID + " INTEGER, "
            + OrdenesProduccionInfo.COLUMN_TRASLADO_CANTIDAD + " REAL, "
            + OrdenesProduccionInfo.COLUMN_TRASLADO_FORMULA + " TEXT, "
            + OrdenesProduccionInfo.COLUMN_TRASLADO_BACHADA + " INTEGER "
            + ")";

    private static final String DATABASE_CREATE_ORDEN_PRODUCCION = "create table "
            + MrpProduction.TABLE_ORDEN_PRODUCCION + "( "
            + MrpProduction.COLUMN_PRODUCCION_ID + " INTEGER, "
            + MrpProduction.COLUMN_PRODUCCION_NOMBRE + " TEXT, "
            + MrpProduction.COLUMN_PRODUCCION_BACHADA + " INTEGER, "
            + MrpProduction.COLUMN_PRODUCCION_FORMULA + " TEXT, "
            + MrpProduction.COLUMN_PRODUCCION_FECHA_PROGRAMADA + " TEXT "
            + ")";

    private static final String DATABASE_CREATE_STOCK_MOVE = "create table "
            + StockMove.TABLE_STOCK_MOVE + "( "
            + StockMove.COLUMN_STOCK_ID + " INTEGER, "
            + StockMove.COLUMN_STOCK_NAME + " TEXT, "
            + StockMove.COLUMN_STOCK_QTY + " INTEGER, "
            + StockMove.COLUMN_STOCK_QTY_PROGRAMADA + " INTEGER, "
            + StockMove.COLUMN_TIEMPO_MEZCLADO + " DECIMAL, "
            + StockMove.COLUMN_DS_DONE + " TEXT, "
            + StockMove.COLUMN_IS_PREPESADO + " INTEGER "
            + ")";

    private static final String DATABASE_CREATE_SERVIDAS = "create table "
            + RmsServida.TABLE_SERVIDA + "( "
            + RmsServida.COLUMN_SERVIDA_ID + " INTEGER, "
            + RmsServida.COLUMN_SERVIDA_BACHADA + " INTEGER, "
            + RmsServida.COLUMN_SERVIDA_FORMULA + " TEXT, "
            + RmsServida.COLUMN_SERVIDA_NAME + " TEXT, "
            + RmsServida.COLUMN_SERVIDA_IS_PROCESADA + " INTEGER, "
            + RmsServida.COLUMN_SERVIDA_ID_MAQUINA + " INTEGER, "
            + RmsServida.COLUMN_SERVIDA_MAQUINA_REPARTO + " TEXT, "
            + RmsServida.COLUMN_SERVIDA_TOTAL_REPARTIR + " REAL, "
            + RmsServida.COLUMN_SERVIDA_TOTAL_PRODUCIDA + " REAL, "
            + RmsServida.COLUMN_SERVIDA_FECHA + " TEXT "
            + ")";

    private static final String DATABASE_CREATE_SERVIDA_LINES = "create table "
            + RmsServidaLine.TABLE_SERVIDA_LINE + "( "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " INTEGER, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_ID_SERVIDA + " INTEGER, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA + " REAL, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM + " REAL, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO + " TEXT, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_FECHA + " TEXT, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_FH_INICIO_REP + " TEXT, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_FH_FIN_REP + " TEXT, "
            + RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA + " INTEGER "
            + ")";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_USUARIO);
        database.execSQL(DATABASE_CREATE_SERVER_CONFIG);
        database.execSQL(DATABASE_CREATE_ORDEN_TRASLADO);
        database.execSQL(DATABASE_CREATE_ORDEN_PRODUCCION);
        database.execSQL(DATABASE_CREATE_STOCK_MOVE);
        database.execSQL(DATABASE_CREATE_CONFIG_SERIAL);
        database.execSQL(DATABASE_CREATE_CONFIG_VARIABLES);
        database.execSQL(DATABASE_CREATE_SERVIDAS);
        database.execSQL(DATABASE_CREATE_SERVIDA_LINES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ServerConfig.TABLE_SERVER_CONFIG);
        db.execSQL("DROP TABLE IF EXISTS " + Usuario.TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + OrdenesProduccionInfo.TABLE_ORDEN_TRASLADO);
        db.execSQL("DROP TABLE IF EXISTS " + MrpProduction.TABLE_ORDEN_PRODUCCION);
        db.execSQL("DROP TABLE IF EXISTS " + StockMove.TABLE_STOCK_MOVE);
        db.execSQL("DROP TABLE IF EXISTS " + ConfiguracionSerial.TABLE_CONFIG_SERIAL);
        db.execSQL("DROP TABLE IF EXISTS " + ConfigVariablesBascula.TABLE_CONFIG_VARIABLES);
        db.execSQL("DROP TABLE IF EXISTS " + RmsServida.TABLE_SERVIDA);
        db.execSQL("DROP TABLE IF EXISTS " + RmsServidaLine.TABLE_SERVIDA_LINE);

        onCreate(db);
    }
}

