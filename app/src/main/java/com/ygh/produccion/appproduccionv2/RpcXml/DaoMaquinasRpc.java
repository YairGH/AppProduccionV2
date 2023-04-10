package com.ygh.produccion.appproduccionv2.RpcXml;

import android.content.Context;

import com.ygh.produccion.appproduccionv2.pojos.RmsMaquinas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class DaoMaquinasRpc {
    private RpcConn rpcConn;
    private Context ctx;

    public DaoMaquinasRpc() {
        this.rpcConn = new RpcConn();
    }

    public DaoMaquinasRpc(RpcConn rpcConn, Context ctx) {
        this.rpcConn = rpcConn;
        this.ctx = ctx;
    }

    public ArrayList<RmsMaquinas> getAllMaquinas() {
        ArrayList<RmsMaquinas> lstMaquinas = new ArrayList<>();

        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] { "state", "=", "en_uso" });

        System.out.println("Comienza busqueda maquinas...");

        Object[] rmsMaquinasIds = rpcConn.getIdsSearch(
                "catt.maquina",
                new Object[] {
                        queryVector
                }
        );

        System.out.println("MaquinasIds" + rmsMaquinasIds.toString());
        System.out.println("Size: " + rmsMaquinasIds.length);


        HashMap hashMapFields = new HashMap();
        hashMapFields.put("fields", new Object[] {"id", "name"});

        Object[] rmsMaquinas = rpcConn.getResultRead(
                "catt.maquina",
                rmsMaquinasIds,
                hashMapFields
        );

        lstMaquinas.add(
                new RmsMaquinas(
                        0,
                        "Asignar..."
                )
        );

        for(Object m : rmsMaquinas) {
            lstMaquinas.add(
                    new RmsMaquinas(
                            ((HashMap<String, Integer>)m).get("id"),
                            ((HashMap<String, String>)m).get("name")
                    )
            );
        }

        return lstMaquinas;
    }
}
