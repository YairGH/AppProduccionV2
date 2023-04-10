package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DaoOrdenesProduccion {
    private final String TAG = this.getClass().getName();

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoOrdenesProduccion(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    public String cleanDbOrdenesProduccion() {
        try {
            db = dbHelper.getWritableDatabase();

            //db.rawQuery("delete from " + MrpProduction.TABLE_ORDEN_PRODUCCION, new String[] {} );
            db.delete(MrpProduction.TABLE_ORDEN_PRODUCCION, null, null);
            db.delete(StockMove.TABLE_STOCK_MOVE, null, null);

            db.close();
            return "OK";
        } catch (Exception ex) {
            Log.e(TAG, "Error saveOrdenProduccion " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
    }

    public String saveOrdenProduccion(int id, String name, String formula, String fechaProgramada, int bachada) {
        try {
            db = dbHelper.getWritableDatabase();

            //db.rawQuery("delete from " + MrpProduction.TABLE_ORDEN_PRODUCCION, new String[] {} );
          //  db.execSQL("delete from " + MrpProduction.TABLE_ORDEN_PRODUCCION);
           // db.execSQL("delete from " + StockMove.TABLE_STOCK_MOVE);
            db.delete(MrpProduction.TABLE_ORDEN_PRODUCCION, null, null);
            db.delete(StockMove.TABLE_STOCK_MOVE, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MrpProduction.COLUMN_PRODUCCION_ID, id);
            contentValues.put(MrpProduction.COLUMN_PRODUCCION_NOMBRE, name);
            contentValues.put(MrpProduction.COLUMN_PRODUCCION_FORMULA, formula);
            contentValues.put(MrpProduction.COLUMN_PRODUCCION_FECHA_PROGRAMADA, fechaProgramada);
            contentValues.put(MrpProduction.COLUMN_PRODUCCION_BACHADA, bachada);

            long idResponse = db.insert(MrpProduction.TABLE_ORDEN_PRODUCCION, null, contentValues);
            db.close();
            if (idResponse > 0) {
                Log.i(TAG, "Datos guardado exitosamente");
                return "OK";
            } else {
                return "ERROR";
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error saveOrdenProduccion " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
    }

    public String saveStockMove(int id, String name, Double qtyProgramada, Double tiempoMezclado, String dsDone, Boolean isPrePesado) {
        try {
            db = dbHelper.getWritableDatabase();

           // db.rawQuery("delete from " + StockMove.TABLE_STOCK_MOVE, new String[] {} );

            int intPrePesado = isPrePesado == true ? 1 : 0;

            ContentValues contentValues = new ContentValues();
            contentValues.put(StockMove.COLUMN_STOCK_ID, id);
            contentValues.put(StockMove.COLUMN_STOCK_NAME, name);
            contentValues.put(StockMove.COLUMN_STOCK_QTY, 0);
            contentValues.put(StockMove.COLUMN_STOCK_QTY_PROGRAMADA, qtyProgramada);
            contentValues.put(StockMove.COLUMN_TIEMPO_MEZCLADO, tiempoMezclado);
            contentValues.put(StockMove.COLUMN_DS_DONE, dsDone);
            contentValues.put(StockMove.COLUMN_IS_PREPESADO, intPrePesado);

            long idResponse = db.insert(StockMove.TABLE_STOCK_MOVE, null, contentValues);
            db.close();
            if (idResponse > 0) {
                Log.i(TAG, "Datos guardado exitosamente");
                return "OK";
            } else {
                return "ERROR";
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error saveStockMove " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
    }

    public boolean updateStockMove(int id, Double pesoReal) {
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(StockMove.COLUMN_STOCK_QTY, pesoReal);

            db.update(StockMove.TABLE_STOCK_MOVE,
                    contentValues,
                    StockMove.COLUMN_STOCK_ID + " = ?",
                    new String[] { " " + id });
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Guardar la servida line");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public MrpProduction getProduccion() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    MrpProduction.TABLE_ORDEN_PRODUCCION,
                    new String[] {
                            MrpProduction.COLUMN_PRODUCCION_ID,
                            MrpProduction.COLUMN_PRODUCCION_NOMBRE,
                            MrpProduction.COLUMN_PRODUCCION_FORMULA,
                            MrpProduction.COLUMN_PRODUCCION_BACHADA,
                            MrpProduction.COLUMN_PRODUCCION_FECHA_PROGRAMADA,
                    }, null, null, null, null, MrpProduction.COLUMN_PRODUCCION_ID
            );
            MrpProduction p = null;
            while (c.moveToNext()) {
                p = new MrpProduction();
                p.setId(c.getInt(c.getColumnIndex(MrpProduction.COLUMN_PRODUCCION_ID)));
                p.setProductName(c.getString(c.getColumnIndex(MrpProduction.COLUMN_PRODUCCION_NOMBRE)));
                p.setProductName(c.getString(c.getColumnIndex(MrpProduction.COLUMN_PRODUCCION_FORMULA)));
                p.setBachada(c.getInt(c.getColumnIndex(MrpProduction.COLUMN_PRODUCCION_BACHADA)));
                p.setFechaProgramada(c.getString(c.getColumnIndex(MrpProduction.COLUMN_PRODUCCION_FECHA_PROGRAMADA)));
            }

            return p;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<StockMove> getStockMove() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    StockMove.TABLE_STOCK_MOVE,
                    new String[] {
                            StockMove.COLUMN_STOCK_ID,
                            StockMove.COLUMN_STOCK_NAME,
                            StockMove.COLUMN_STOCK_QTY_PROGRAMADA,
                            StockMove.COLUMN_STOCK_QTY,
                            StockMove.COLUMN_TIEMPO_MEZCLADO,
                            StockMove.COLUMN_DS_DONE,
                            StockMove.COLUMN_IS_PREPESADO
                    }, null, null, null, null, null
            );
            StockMove s = null;
            List<StockMove> lstStockMove = new ArrayList<>();
            while (c.moveToNext()) {
                s = new StockMove();
                s.setId(c.getInt(c.getColumnIndex(StockMove.COLUMN_STOCK_ID)));
                s.setProductId(c.getString(c.getColumnIndex(StockMove.COLUMN_STOCK_NAME)));
                s.setQtyProgramada(c.getDouble(c.getColumnIndex(StockMove.COLUMN_STOCK_QTY_PROGRAMADA)));
                s.setQtyPesada(c.getDouble(c.getColumnIndex(StockMove.COLUMN_STOCK_QTY)));
                s.setTiempoMezclado(c.getDouble(c.getColumnIndex(StockMove.COLUMN_TIEMPO_MEZCLADO)));
                s.setDsDone(c.getString(c.getColumnIndex(StockMove.COLUMN_DS_DONE)));

                if(c.getInt(c.getColumnIndex(StockMove.COLUMN_IS_PREPESADO)) == 1)
                    s.setPrePesado(true);
                else
                    s.setPrePesado(false);

                lstStockMove.add(s);
            }

            return lstStockMove;
        } catch (Exception ex) {
            return null;
        }
    }
}
