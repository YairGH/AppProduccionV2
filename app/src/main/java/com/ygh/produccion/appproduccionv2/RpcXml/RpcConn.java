package com.ygh.produccion.appproduccionv2.RpcXml;

import android.os.StrictMode;
import android.util.Log;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.ObjectStreamException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class RpcConn {
    private final String TAG = "RpcConn";
    public static String url = "";
    public static String db = "";
    public static int uid = -1;
    public static String username = "";
    public static String password = "";

    XmlRpcClient models = null;

    public RpcConn() {
        try {
            Log.i(TAG, "Conectando con Servidor RPC");

            models = new XmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(url + "/xmlrpc/2/object"));
            models.setConfig(config);
            Log.i(TAG, "Conexión Exitosa!");
        } catch (Exception ex) {
            Log.e(TAG, "Error al Conectar: " + ex.getMessage());
        }
    }

    public RpcConn(String url, String db, String username, String password) throws Exception {

        Log.i(TAG, "LogIn");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        login(url, db, username, password);
        RpcConn.username = username;
        RpcConn.password = password;
        RpcConn.url = url;
        RpcConn.db = db;
    }

    private void login(String url, String db, String login, String password) throws XmlRpcException, MalformedURLException {
        XmlRpcClient client = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setEnabledForExtensions(true);

        config.setServerURL(new URL(url+"/xmlrpc/2/common"));
        client.setConfig(config);

        Object[] params = new Object[] {db,login,password}; // Ok & simple
        Object uid = null;
        uid = client.execute("login", params);
        RpcConn.uid = (int)uid;
    }

    public Object[] getIdsSearch(String odooModel, Object[] args) {
        try {
            Log.i(TAG, "Comienza getIdsSearch " + odooModel);

            Object[] obj = (Object[])models.execute(

                    "execute_kw", new Object[] {
                            db, RpcConn.uid, password, odooModel, "search",
                            args
                    }
            );
            System.out.println("RESULTADOS encontrados: " + obj.length);
            return obj;
        } catch (Exception ex) {
            //   Log.e(TAG, "Error getIdsSearch: " + ex.getMessage());
            System.out.println("ERROR YAIR: " + ex.getMessage());
        }
        return null;
    }

    public Object[] getResultRead(String odooModel, Object[] ids, HashMap fieldMap) {
        try {
            Log.i(TAG, "Comienza getResultRead");
            return (Object[])models.execute(
                    "execute_kw", new Object[] {
                            db, RpcConn.uid, password, odooModel, "read",
                            new Object[] {ids},
                            fieldMap
                    }
            );
        } catch (Exception ex) {
            Log.e(TAG, "Error getResultRead: " + ex.getMessage());
        }
        return null;
    }

    public int insert(String odooModel, HashMap fields) {
        try {
            Log.i(TAG, "Comienza insert");
            return (int)models.execute(
                    "execute_kw", new Object[] {
                            db, RpcConn.uid, password, odooModel, "create",
                            new Object[] {fields}
                    }
            );
        } catch (Exception ex) {
            Log.e(TAG, "Error insert: " + ex.getMessage());
        }
        return -1;
    }

    public boolean update(String odooModel, int id, HashMap fields) {
        try {
            models.execute(
                    "execute_kw", new Object[] {
                            db, RpcConn.uid, password, odooModel, "write",
                            new Object[] {
                                    id,
                                    fields
                            }
                    }
            );
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Error update: " + ex.getMessage());
            return false;
        }
    }

    public Object executeMethod(String odooModel, String methodName, Object[] args) {
        try {
            Object result = models.execute(
                    "execute_kw", new Object[]{
                            db, uid, password, odooModel, methodName,
                            args
                    }
            );
            return result;
        } catch (XmlRpcException ex) {
            Log.e(TAG, "Error method: " + ex.getMessage());
            return false;
        } catch (Exception ex2) {
            Log.e(TAG, "Error method: " + ex2.getMessage());
            return false;
        }
    }

    public Object executeMethod(String odooModel, String methodName) {
        try {
            Object result = models.execute(
                    "execute_kw", new Object[]{
                            db, uid, password, odooModel, methodName
                    }
            );
            return result;
        } catch (XmlRpcException ex) {
            Log.e(TAG, "Error method: " + ex.getMessage());
            return false;
        } catch (Exception ex2) {
            Log.e(TAG, "Error method: " + ex2.getMessage());
            return false;
        }
    }
}
