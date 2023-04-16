package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DaoServidasSql {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoServidasSql(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public ArrayList<RmsServidaLine> getServidaLinesProcesada() {
        ArrayList<RmsServidaLine> lstServidaLines = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServidaLine.TABLE_SERVIDA_LINE,
                    new String[] {
                            RmsServidaLine.COLUMN_SERVIDA_LINE_ID,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_FECHA
                    }, null, null, null, null, null
            );

            while(c.moveToNext()) {
                lstServidaLines.add(
                        new RmsServidaLine(
                                c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_ID)),
                                c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM)),
                                new Date(Long.parseLong(c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_FECHA))))
                        )
                );
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al buscar en listServidaLines " + ex.getMessage());
        }

        return lstServidaLines;
    }

    public RmsServida getServidaToProcess() {
        RmsServida rmsServida = null;

        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServida.TABLE_SERVIDA,
                    new String[] {
                            RmsServida.COLUMN_SERVIDA_ID,
                            RmsServida.COLUMN_SERVIDA_NAME,
                            RmsServida.COLUMN_SERVIDA_FORMULA,
                            RmsServida.COLUMN_SERVIDA_BACHADA,
                            RmsServida.COLUMN_SERVIDA_ID_MAQUINA,
                            RmsServida.COLUMN_SERVIDA_TOTAL_REPARTIR,
                            RmsServida.COLUMN_SERVIDA_FECHA,
                            RmsServida.COLUMN_SERVIDA_TOTAL_PRODUCIDA,
                            RmsServida.COLUMN_SERVIDA_MAQUINA_REPARTO
                    },
                    null, null, null, null, null
            );
            c.moveToNext();
            rmsServida = new RmsServida(
                    c.getInt(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_ID)),
                    c.getString(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_FORMULA)),
                    c.getInt(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_BACHADA)),
                    c.getString(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_NAME)),
                    c.getInt(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_ID_MAQUINA)),
                    c.getDouble(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_TOTAL_REPARTIR))
            );
            rmsServida.setTotalProducida(c.getDouble(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_TOTAL_PRODUCIDA)));
            rmsServida.setMaquinaReparto(c.getString(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_MAQUINA_REPARTO)));
            rmsServida.setFecha(new Date(c.getLong(c.getColumnIndex(RmsServida.COLUMN_SERVIDA_FECHA))));
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al buscar en getServidaLineToProcess " + ex.getMessage());
        }

        return rmsServida;
    }

    public ArrayList<RmsServidaLine> getAllLineas() {
        ArrayList<RmsServidaLine> lstLineas = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServidaLine.TABLE_SERVIDA_LINE,
                    new String[] {
                            RmsServidaLine.COLUMN_SERVIDA_LINE_ID,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_FH_FIN_REP,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_FH_INICIO_REP
                    },
                    null,
                    null,
                    null, null, RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " ASC"
            );
            RmsServidaLine l = null;
            while(c.moveToNext()) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                l = new RmsServidaLine(
                        c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_ID)),
                        c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA)),
                        c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO))
                );
                l.setQtyUom(c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM)));
                try {
                    l.setFechaFinReparto(df.parse(c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_FH_FIN_REP))));
                } catch (Exception ex) {
                    l.setFechaFinReparto(new Date());
                }

                try {
                    l.setFechaInicioReparto(df.parse(c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_FH_INICIO_REP))));
                } catch (Exception ex) {
                    l.setFechaInicioReparto(new Date());
                }

                if(c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA)) == 0) {
                    l.setProcesada(false);
                } else {
                    l.setProcesada(true);
                }

                lstLineas.add(l);
            }

            return lstLineas;
        } catch (Exception ex) {
            return null;
        }
    }

    public RmsServidaLine getServidaLineToProcess() {
        RmsServidaLine rmsServidaLine = null;

        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServidaLine.TABLE_SERVIDA_LINE,
                    new String[] {
                            RmsServidaLine.COLUMN_SERVIDA_LINE_ID,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA
                    },
                    RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA + " = ? ",
                    new String[] { "0" },
                    null, null, RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " ASC"
            );
            if(c.getCount() > 0) {
                c.moveToNext();
                rmsServidaLine = new RmsServidaLine(
                        c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_ID)),
                        c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA)),
                        c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO))
                );
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al buscar en getServidaLineToProcess " + ex.getMessage());
        }

        return rmsServidaLine;
    }

    public RmsServidaLine getServidaLineToProcessByCorral(String prmCorral) {
        RmsServidaLine rmsServidaLine = null;

        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServidaLine.TABLE_SERVIDA_LINE,
                    new String[] {
                            RmsServidaLine.COLUMN_SERVIDA_LINE_ID,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA
                    },
                    RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA + " = ? and " + RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO + " = ? ",
                    new String[] { "0", prmCorral },
                    null, null, RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " ASC"
            );
            c.moveToNext();
            rmsServidaLine = new RmsServidaLine(
                    c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_ID)),
                    c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA)),
                    c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO))
            );
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al buscar en getServidaLineToProcess " + ex.getMessage());
        }

        return rmsServidaLine;
    }

    public RmsServidaLine getServidaLineByBusquedaCorral(String prmCorral) {
        RmsServidaLine rmsServidaLine = null;

        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    RmsServidaLine.TABLE_SERVIDA_LINE,
                    new String[] {
                            RmsServidaLine.COLUMN_SERVIDA_LINE_ID,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA,
                            RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA
                    },
                    RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO + " like ? ",
                    new String[] { "[" + prmCorral + "]%" },
                    null, null, RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " ASC"
            );
            if(c.getCount() > 0) {
                c.moveToNext();
                rmsServidaLine = new RmsServidaLine(
                        c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_ID)),
                        c.getDouble(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA)),
                        c.getString(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO))
                );
                boolean isProcesada = false;
                if(c.getInt(c.getColumnIndex(RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA)) != 0)
                    isProcesada = true;
                rmsServidaLine.setProcesada(isProcesada);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al buscar en getServidaLineToProcess " + ex.getMessage());
        }

        return rmsServidaLine;
    }

    public boolean saveServida(RmsServida servida) {
        try {
            db = dbHelper.getWritableDatabase();

            db.delete(RmsServida.TABLE_SERVIDA, null, null);
            db.delete(RmsServidaLine.TABLE_SERVIDA_LINE, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(RmsServida.COLUMN_SERVIDA_BACHADA, servida.getBachada());
            contentValues.put(RmsServida.COLUMN_SERVIDA_FORMULA, servida.getFormula());
            contentValues.put(RmsServida.COLUMN_SERVIDA_ID, servida.getId());
            contentValues.put(RmsServida.COLUMN_SERVIDA_NAME, servida.getName());
            contentValues.put(RmsServida.COLUMN_SERVIDA_ID_MAQUINA, servida.getIdMaquina());
            contentValues.put(RmsServida.COLUMN_SERVIDA_TOTAL_REPARTIR, servida.getCantidad());
            contentValues.put(RmsServida.COLUMN_SERVIDA_FECHA, servida.getFecha().getTime());
            contentValues.put(RmsServida.COLUMN_SERVIDA_MAQUINA_REPARTO, servida.getMaquinaReparto());
            contentValues.put(RmsServida.COLUMN_SERVIDA_TOTAL_PRODUCIDA, servida.getTotalProducida());
            long id = db.insert(RmsServida.TABLE_SERVIDA, null, contentValues);
            db.close();
            if(id > 0) {
                Log.i(this.getClass().getName(), "Servida Guardada!");
                return true;
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Guardar la servida");
        }
        return false;
    }

    public int getCorralesPendientesServir() {
        try {
            int cantCorralesPendientes = 0;
            db = dbHelper.getReadableDatabase();
            String qry = "select count(*) as cantCorralesPendientes from " + RmsServidaLine.TABLE_SERVIDA_LINE + " where " +
                    RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA + " = 0";
            Cursor c = db.rawQuery(qry, null);
            if(c.getCount() > 0) {
                c.moveToFirst();
                do {
                    cantCorralesPendientes = c.getInt(c.getColumnIndex("cantCorralesPendientes"));
                } while (c.moveToNext());
                c.close();
            }
            return cantCorralesPendientes;
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al consultar " + ex.getMessage());
        }
        return 0;
    }

    public int getCorralesServidos() {
        try {
            int cantCorralesServidos = 0;
            db = dbHelper.getReadableDatabase();
            String qry = "select count(*) as cantCorralesServidos from " + RmsServidaLine.TABLE_SERVIDA_LINE + " where " +
                    RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA + " = 1";
            Cursor c = db.rawQuery(qry, null);
            if(c.getCount() > 0) {
                c.moveToFirst();
                do {
                    cantCorralesServidos = c.getInt(c.getColumnIndex("cantCorralesServidos"));
                } while (c.moveToNext());
                c.close();
            }
            return cantCorralesServidos;
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al consultar " + ex.getMessage());
        }
        return 0;
    }

    public boolean saveServidaLines(ArrayList<RmsServidaLine> servidaLines) {
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues;
            for(RmsServidaLine sl : servidaLines) {
                contentValues = new ContentValues();
                contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_ID, sl.getId());
                contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_LOT_RANCHO, sl.getLotRanchoTxt());
                contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_PROGRAMADA, sl.getQtyProgramada());
                contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM, sl.getQtyUom());
                contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA, 0);

                long id = db.insert(RmsServidaLine.TABLE_SERVIDA_LINE, null, contentValues);
                if (id <= 0) {
                    return false;
                }
                Log.i(this.getClass().getName(), sl.getLotRanchoTxt() + " Guardado!");
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Guardar la servida line");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean updatePesoRealServida(double pesoReal, int id, Date fhInicio) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();

            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_QTY_UOM, pesoReal);
            contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_IS_PROCESADA, true);
            contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_FH_INICIO_REP, df.format(fhInicio));
            contentValues.put(RmsServidaLine.COLUMN_SERVIDA_LINE_FH_FIN_REP, df.format(currentDate));
            db.update(RmsServidaLine.TABLE_SERVIDA_LINE,
                    contentValues,
                    RmsServidaLine.COLUMN_SERVIDA_LINE_ID + " = ?",
                    new String[] { " " + id });
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Guardar la servida line");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean updateServidaProcesada(int id) {
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(RmsServida.COLUMN_SERVIDA_IS_PROCESADA, true);
            db.update(RmsServida.TABLE_SERVIDA,
                    contentValues,
                    RmsServida.COLUMN_SERVIDA_ID+ " = ?",
                    new String[] { " " + id });
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Guardar la servida");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean cleanServida() {
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(RmsServida.TABLE_SERVIDA, null, null);
            db.delete(RmsServidaLine.TABLE_SERVIDA_LINE, null, null);
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error al Borrar");
            db.close();
            return false;
        }
        return true;
    }
}
