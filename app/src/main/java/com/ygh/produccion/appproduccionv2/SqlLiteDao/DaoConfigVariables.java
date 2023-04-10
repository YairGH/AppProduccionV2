package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ygh.produccion.appproduccionv2.pojos.ConfigVariablesBascula;
import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;

/**
 * Dao para las variables de siguiente insumo automático o manual
 */
public class DaoConfigVariables {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoConfigVariables(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    public void saveConfigVariables(ConfigVariablesBascula configVariablesBascula) {
        try {
            db = dbHelper.getWritableDatabase();

            db.delete(ConfigVariablesBascula.TABLE_CONFIG_VARIABLES, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ConfigVariablesBascula.COLUMN_PESO_MINIMO, configVariablesBascula.getPesoMinimo());
            contentValues.put(ConfigVariablesBascula.COLUMN_TIPO_SIG_INSUMO, configVariablesBascula.getTipoSigInsumo());
            contentValues.put(ConfigVariablesBascula.COLUMN_TIPO_MEDIDA, configVariablesBascula.getTipoMedidaSigInsumo());
            contentValues.put(ConfigVariablesBascula.COLUMN_MEDIDA_VALOR, configVariablesBascula.getValorToSigInsumo());
            contentValues.put(ConfigVariablesBascula.COLUMN_TIEMPO_SIG_INSUMO, configVariablesBascula.getTiempoSiguienteInsumo());
            contentValues.put(ConfigVariablesBascula.COLUMN_BUZZER, configVariablesBascula.getBuzzer());
            contentValues.put(ConfigVariablesBascula.COLUMN_DONE_HEADER, configVariablesBascula.getDoneHeader());
            contentValues.put(ConfigVariablesBascula.COLUMN_PREALARM1, configVariablesBascula.getPrealarm1());
            contentValues.put(ConfigVariablesBascula.COLUMN_PREALARM2, configVariablesBascula.getPrealarm2());
            db.insert(ConfigVariablesBascula.TABLE_CONFIG_VARIABLES, null, contentValues);
            db.close();
        } catch (Exception ex){

        }
    }

    public ConfigVariablesBascula getConfigVariables() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    ConfigVariablesBascula.TABLE_CONFIG_VARIABLES,
                    new String[] {
                            ConfigVariablesBascula.COLUMN_PESO_MINIMO,
                            ConfigVariablesBascula.COLUMN_TIPO_SIG_INSUMO,
                            ConfigVariablesBascula.COLUMN_TIPO_MEDIDA,
                            ConfigVariablesBascula.COLUMN_MEDIDA_VALOR,
                            ConfigVariablesBascula.COLUMN_TIEMPO_SIG_INSUMO,
                            ConfigVariablesBascula.COLUMN_BUZZER,
                            ConfigVariablesBascula.COLUMN_DONE_HEADER,
                            ConfigVariablesBascula.COLUMN_PREALARM1,
                            ConfigVariablesBascula.COLUMN_PREALARM2
                    }, null, null, null, null, null
            );
            while (c.moveToNext()) {
                Integer intBuzzer = c.getInt(c.getColumnIndex(ConfigVariablesBascula.COLUMN_BUZZER));
                Boolean buzzer = intBuzzer == 0 ? false : true;

                return new ConfigVariablesBascula(
                        c.getDouble(c.getColumnIndex(ConfigVariablesBascula.COLUMN_PESO_MINIMO)),
                        c.getString(c.getColumnIndex(ConfigVariablesBascula.COLUMN_TIPO_SIG_INSUMO)),
                        c.getString(c.getColumnIndex(ConfigVariablesBascula.COLUMN_TIPO_MEDIDA)),
                        c.getDouble(c.getColumnIndex(ConfigVariablesBascula.COLUMN_MEDIDA_VALOR)),
                        c.getDouble(c.getColumnIndex(ConfigVariablesBascula.COLUMN_TIEMPO_SIG_INSUMO)),
                        buzzer,
                        c.getString(c.getColumnIndex(ConfigVariablesBascula.COLUMN_DONE_HEADER)),
                        c.getDouble(c.getColumnIndex(ConfigVariablesBascula.COLUMN_PREALARM1)),
                        c.getDouble(c.getColumnIndex(ConfigVariablesBascula.COLUMN_PREALARM2))
                );
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }
}
