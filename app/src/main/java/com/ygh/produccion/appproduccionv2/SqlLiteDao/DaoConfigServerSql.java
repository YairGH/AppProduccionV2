package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.ServerConfig;

public class DaoConfigServerSql {
    private final String TAG = this.getClass().getName();

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoConfigServerSql(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    public String saveConfigServer(
            String urlServer,
            String dbServer,
            String adminUserName,
            String adminPassword,
            String mqttServer,
            String mqttPort,
            String ipApiMqtt) {
        try {
            db = dbHelper.getWritableDatabase();

            db.delete(ServerConfig.TABLE_SERVER_CONFIG, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ServerConfig.COLUMN_SERVER_URL, urlServer);
            contentValues.put(ServerConfig.COLUMN_SERVER_DB, dbServer);
            contentValues.put(ServerConfig.COLUMN_SERVER_USERNAME, adminUserName);
            contentValues.put(ServerConfig.COLUMN_SERVER_PASSWORD, adminPassword);
            contentValues.put(ServerConfig.COLUMN_SERVER_MQTT, mqttServer);
            contentValues.put(ServerConfig.COLUMN_PORT_MQTT, mqttPort);
            contentValues.put(ServerConfig.COLUMN_IP_API_MQTT, ipApiMqtt);


            long id = db.insert(ServerConfig.TABLE_SERVER_CONFIG, null, contentValues);
            db.close();
            if (id > 0) {
                Log.i(TAG, "Datos guardado exitosamente");
                return "OK";
            } else {
                return "ERROR";
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error al intentar guardar los datos " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
    }

    public ServerConfig getServerDatos() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    ServerConfig.TABLE_SERVER_CONFIG,
                    new String[] {
                            ServerConfig.COLUMN_SERVER_URL,
                            ServerConfig.COLUMN_SERVER_DB,
                            ServerConfig.COLUMN_SERVER_USERNAME,
                            ServerConfig.COLUMN_SERVER_PASSWORD,
                            ServerConfig.COLUMN_SERVER_MQTT,
                            ServerConfig.COLUMN_PORT_MQTT,
                            ServerConfig.COLUMN_IP_API_MQTT
                    }, null, null, null, null, null
            );
            while (c.moveToNext()) {
                return new ServerConfig(
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_SERVER_URL)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_SERVER_DB)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_SERVER_USERNAME)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_SERVER_PASSWORD)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_SERVER_MQTT)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_PORT_MQTT)),
                        c.getString(c.getColumnIndex(ServerConfig.COLUMN_IP_API_MQTT))
                );
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}