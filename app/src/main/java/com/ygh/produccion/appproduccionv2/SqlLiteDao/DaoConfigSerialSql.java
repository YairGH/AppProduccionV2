package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;

public class DaoConfigSerialSql {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoConfigSerialSql(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    public void saveConfigSerial(ConfiguracionSerial configuracionSerial) {
        try {
            db = dbHelper.getWritableDatabase();

            db.delete(ConfiguracionSerial.TABLE_CONFIG_SERIAL, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BAUDRATE, configuracionSerial.getBaudrate());
            contentValues.put(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BYTESIZE, configuracionSerial.getBytesize());
            contentValues.put(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_PARITY, configuracionSerial.getParity());
            contentValues.put(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_STOPBIT, configuracionSerial.getStopbit());
            contentValues.put(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_TARA, configuracionSerial.getTara());
            contentValues.put(ConfiguracionSerial.COLUMN_BT_DEVICE, configuracionSerial.getBtDevice());
            db.insert(ConfiguracionSerial.TABLE_CONFIG_SERIAL, null, contentValues);
            db.close();
        } catch (Exception ex) {

        }
    }

    public ConfiguracionSerial getConfiguracionSerial() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    ConfiguracionSerial.TABLE_CONFIG_SERIAL,
                    new String[] {
                            ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BAUDRATE,
                            ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BYTESIZE,
                            ConfiguracionSerial.COLUMN_CONFIG_SERIAL_PARITY,
                            ConfiguracionSerial.COLUMN_CONFIG_SERIAL_STOPBIT,
                            ConfiguracionSerial.COLUMN_CONFIG_SERIAL_TARA,
                            ConfiguracionSerial.COLUMN_BT_DEVICE
                    }, null, null, null, null, null
            );
            while (c.moveToNext()) {
                return new ConfiguracionSerial(
                        c.getInt(c.getColumnIndex(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BAUDRATE)),
                        c.getInt(c.getColumnIndex(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_PARITY)),
                        c.getInt(c.getColumnIndex(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_STOPBIT)),
                        c.getInt(c.getColumnIndex(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_BYTESIZE)),
                        c.getString(c.getColumnIndex(ConfiguracionSerial.COLUMN_CONFIG_SERIAL_TARA)),
                        c.getString(c.getColumnIndex(ConfiguracionSerial.COLUMN_BT_DEVICE))
                );
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }
}
