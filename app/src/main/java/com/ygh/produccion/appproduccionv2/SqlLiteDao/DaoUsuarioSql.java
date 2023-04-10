package com.ygh.produccion.appproduccionv2.SqlLiteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.ConfiguracionSerial;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.util.ArrayList;

public class DaoUsuarioSql {
    private final String TAG = this.getClass().getName();

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public DaoUsuarioSql(Context ctx) {
        dbHelper = new SQLiteHelper(ctx);
    }

    public String updateUsuarioActivo(String username) {
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Usuario.COLUMN_USUARIO_ACTIVO, 0);
            db.update(Usuario.TABLE_USUARIO, contentValues, null, null);

            contentValues = new ContentValues();
            contentValues.put(Usuario.COLUMN_USUARIO_ACTIVO, 1);
            db.update(Usuario.TABLE_USUARIO,
                    contentValues,
                    Usuario.COLUMN_USUARIO_USERNAME + "= ?",
                    new String[] { username });
            db.close();
            return "OK";
        } catch (Exception ex) {
            return "NOK";
        }
    }

    public String updateUsuarioLogOut() {
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Usuario.COLUMN_USUARIO_ACTIVO, 0);
            db.update(Usuario.TABLE_USUARIO, contentValues, null, null);
            db.close();
            return "OK";
        } catch (Exception ex) {
            return "NOK";
        }
    }

    public String saveUsaurio(String username, String password, int idMaquina, int tipoUsuario, int idMaquinaReparto, String nombreMaquinaReparto) {

        try {
            db = dbHelper.getWritableDatabase();

            db.delete(Usuario.TABLE_USUARIO, Usuario.COLUMN_USUARIO_USERNAME + " = ? ", new String[] { username });

            ContentValues contentValues = new ContentValues();
            contentValues.put(Usuario.COLUMN_USUARIO_USERNAME, username);
            contentValues.put(Usuario.COLUMN_USUARIO_PASSWORD, password);
            contentValues.put(Usuario.COLUMN_USUARIO_ID_MAQUINA, idMaquina);
            contentValues.put(Usuario.COLUMN_USUARIO_TIPO_USUARIO, tipoUsuario);
            contentValues.put(Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO, idMaquinaReparto);
            contentValues.put(Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO, nombreMaquinaReparto);
            contentValues.put(Usuario.COLUMN_USUARIO_ACTIVO, 0);
            long id = db.insert(Usuario.TABLE_USUARIO, null, contentValues);
            db.close();
            if(id > 0) {
                Log.i(TAG, "Usuario guardado exitosamente");
                return "OK";
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error al intentar guardar los datos " + ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
        return "ERROR No identificado";
    }

    public ArrayList<String> getAllUsernames() {
        try {
            ArrayList<String> lstUsernames = new ArrayList<>();
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    Usuario.TABLE_USUARIO,
                    new String[] { Usuario.COLUMN_USUARIO_USERNAME },
                    null, null, null, null, null);

            lstUsernames.add("Selecciona...");
            while(c.moveToNext()) {
                lstUsernames.add(c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_USERNAME)));
            }

            c.close();
            db.close();

            return lstUsernames;
        } catch (Exception ex) {

        }
        return null;
    }

    public Usuario getUsuarioByActivo() {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    Usuario.TABLE_USUARIO,
                    new String[] {
                            Usuario.COLUMN_USUARIO_USERNAME,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA,
                            Usuario.COLUMN_USUARIO_PASSWORD,
                            Usuario.COLUMN_USUARIO_TIPO_USUARIO,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO,
                            Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO
                    },
                    Usuario.COLUMN_USUARIO_ACTIVO + " = ? ",
                    new String[] { "1" },
                    null, null, null);

            c.moveToFirst();
            Usuario usuario = new Usuario(
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_USERNAME)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_PASSWORD)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_TIPO_USUARIO)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO))
            );

            c.close();
            db.close();

            return usuario;

        } catch (Exception ex) {
            Log.e(TAG, "Usuario no encontrado " + ex.getMessage());
            return null;
        }
    }

    public Usuario getUsuarioByUsername(String username) {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    Usuario.TABLE_USUARIO,
                    new String[] {
                            Usuario.COLUMN_USUARIO_USERNAME,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA,
                            Usuario.COLUMN_USUARIO_PASSWORD,
                            Usuario.COLUMN_USUARIO_TIPO_USUARIO,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO,
                            Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO
                    },
                    Usuario.COLUMN_USUARIO_USERNAME + " = ? ",
                    new String[] { username },
                    null, null, null);

            c.moveToFirst();
            Usuario usuario = new Usuario(
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_USERNAME)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_PASSWORD)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_TIPO_USUARIO)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO))
            );

            c.close();
            db.close();

            return usuario;

        } catch (Exception ex) {
            Log.e(TAG, "Usuario no encontrado " + ex.getMessage());
            return null;
        }
    }

    public Usuario getUsuarioByUsername(String username, String password) {
        try {
            db = dbHelper.getReadableDatabase();
            Cursor c = db.query(
                    Usuario.TABLE_USUARIO,
                    new String[] {
                            Usuario.COLUMN_USUARIO_USERNAME,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA,
                            Usuario.COLUMN_USUARIO_PASSWORD,
                            Usuario.COLUMN_USUARIO_TIPO_USUARIO,
                            Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO,
                            Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO
                    },
                    Usuario.COLUMN_USUARIO_USERNAME + " = ? " + "and " + Usuario.COLUMN_USUARIO_PASSWORD + " = ?",
                    new String[] { username, password },
                    null, null, null);

            c.moveToFirst();
            Usuario usuario = new Usuario(
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_USERNAME)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_PASSWORD)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_TIPO_USUARIO)),
                    c.getInt(c.getColumnIndex(Usuario.COLUMN_USUARIO_ID_MAQUINA_REPARTO)),
                    c.getString(c.getColumnIndex(Usuario.COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO))
            );

            c.close();
            db.close();

            return usuario;

        } catch (Exception ex) {
            Log.e(TAG, "Usuario no encontrado " + ex.getMessage());
            return null;
        }
    }
}
