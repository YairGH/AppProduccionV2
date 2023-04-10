package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;

public class DaoOrdenTraslado {
    private final String TAG = this.getClass().getName();

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoOrdenTraslado(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    /**
     *
     * @param idTraslado
     * @param nameTraslado
     * @param qtyTraslado
     * @param formula
     * @param bachada
     * @return
     */
    public String saveOrdenTraslado(int idTraslado, String nameTraslado, double qtyTraslado, String formula, int bachada) {
        try {
            db = dbHelper.getWritableDatabase();

            db.rawQuery("delete from " + OrdenesProduccionInfo.TABLE_ORDEN_TRASLADO, new String[] {} );

            db.close();

            OrdenesProduccionInfo o = getOrdenTrasladoInfo();

            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(OrdenesProduccionInfo.COLUMN_TRASLADO_ID, idTraslado);
            contentValues.put(OrdenesProduccionInfo.COLUMN_TRASLADO_ORDEN, nameTraslado);
            contentValues.put(OrdenesProduccionInfo.COLUMN_TRASLADO_BACHADA, bachada);
            contentValues.put(OrdenesProduccionInfo.COLUMN_TRASLADO_FORMULA, formula);
            contentValues.put(OrdenesProduccionInfo.COLUMN_TRASLADO_CANTIDAD, qtyTraslado);

            long id = db.insert(OrdenesProduccionInfo.TABLE_ORDEN_TRASLADO, null, contentValues);
            db.close();
            if (id > 0) {
                Log.i(TAG, "Datos guardado exitosamente");
                return "OK";
            } else {
                return "ERROR";
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error saveOrdenTraslado " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
    }

    public OrdenesProduccionInfo getOrdenTrasladoInfo() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    OrdenesProduccionInfo.TABLE_ORDEN_TRASLADO,
                    new String[] {
                            OrdenesProduccionInfo.COLUMN_TRASLADO_ID,
                            OrdenesProduccionInfo.COLUMN_TRASLADO_ORDEN,
                            OrdenesProduccionInfo.COLUMN_TRASLADO_CANTIDAD,
                            OrdenesProduccionInfo.COLUMN_TRASLADO_BACHADA,
                            OrdenesProduccionInfo.COLUMN_TRASLADO_FORMULA,
                    }, null, null, null, null, null
            );
            while (c.moveToNext()) {
                return new OrdenesProduccionInfo (
                        c.getInt(c.getColumnIndex(OrdenesProduccionInfo.COLUMN_TRASLADO_BACHADA)),
                        c.getString(c.getColumnIndex(OrdenesProduccionInfo.COLUMN_TRASLADO_ORDEN)),
                        c.getDouble(c.getColumnIndex(OrdenesProduccionInfo.COLUMN_TRASLADO_CANTIDAD)),
                        c.getString(c.getColumnIndex(OrdenesProduccionInfo.COLUMN_TRASLADO_FORMULA)),
                        c.getInt(c.getColumnIndex(OrdenesProduccionInfo.COLUMN_TRASLADO_ID))
                );
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
