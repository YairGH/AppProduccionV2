package com.ygh.produccion.appproduccionv2.RpcXml;

import android.util.Log;

import com.ygh.produccion.appproduccionv2.pojos.RmsServida;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

public class DaoServidasRpc {
    private RpcConn rpcConn;

    public DaoServidasRpc() {
        rpcConn = new RpcConn();
    }

    public RmsServida getServidaToProcess(int idMaquinaReparto) {

        ArrayList<RmsServida> lstServidas = new ArrayList<>();
        Vector queryVectorServidas = new Vector();
        queryVectorServidas.addElement(new Object[] {"state", "=", "draft"});
        queryVectorServidas.addElement(new Object[] {"maquina_reparto_id", "=", idMaquinaReparto});

        //Falta condición de que la orden de producción relacionado <> a null y que el estado de la orden de producción = done
        //queryVectorServidas.addElement(new Object[] {"mrp_state", "=", "done"});

        //Condición de warehouse

        Object[] rmsServidasIds = rpcConn.getIdsSearch(
                "catt.servida",
                new Object[] {
                        queryVectorServidas
                }
        );


        System.out.println("IDS Encontrados: " + rmsServidasIds.length);

        HashMap hashMapFieldsServidas = new HashMap();
        hashMapFieldsServidas.put(
                "fields", new Object[] {
                        "id", "name", "fecha", "bachada", "mrp_product_uom_qty", "maquina_reparto_id", "mrp_state", "pick_reparto_state"
                }
        );

        Object[] rmsServidas = rpcConn.getResultRead(
                "catt.servida",
                rmsServidasIds,
                hashMapFieldsServidas
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date servidaFecha;

        RmsServida serv = null;
        for(Object rmsServida : rmsServidas) {

            try {
                servidaFecha = sdf.parse(((HashMap<String, String>)rmsServida).get("fecha"));
            } catch (Exception ex) {
                servidaFecha = new Date();
            }
            String pickRepartState = "";
            String mrpState = "";
            try {
                Boolean pickState = (Boolean) ((HashMap<String, Object>) rmsServida).get("pick_reparto_state");
                pickRepartState = pickState.toString();
            } catch (Exception ex) {
                pickRepartState = ((HashMap<String, String>)rmsServida).get("pick_reparto_state");
            }

            try {
                Boolean bMrpState = (Boolean) ((HashMap<String, Object>) rmsServida).get("mrp_state");
                mrpState = bMrpState.toString();
            } catch (Exception ex) {
                mrpState = ((HashMap<String, String>)rmsServida).get("mrp_state");
            }

            serv = new RmsServida(
                    ((HashMap<String, Integer>)rmsServida).get("id"),
                    ((HashMap<String, String>)rmsServida).get("name"),
                    servidaFecha,
                    ((HashMap<String, Integer>)rmsServida).get("bachada"),
                    "1",
                    Double.parseDouble(((HashMap<String, Object>)rmsServida).get("mrp_product_uom_qty").toString()),
                    ((HashMap<String, Object[]>)rmsServida).get("maquina_reparto_id")[1].toString(),
                    false,
                    0,
                    mrpState,
                    pickRepartState
            );

            lstServidas.add(serv);
        }
        lstServidas.sort(Comparator.comparing(RmsServida::getBachada));
        return lstServidas.get(0);
    }

    /*
    public RmsServida getServidaByProductionOrder(MrpProduction mrp) {
        ArrayList<RmsServida> lstServidas = new ArrayList<>();
        Vector queryVectorServidas = new Vector();
        //queryVectorServidas.addElement(new Object[] {"x_update", "=", false});
        queryVectorServidas.addElement(new Object[] {"mrp_id", "=", mrp.getId()});
        //status = "Draft"


        Object[] rmsServidasIds = rpcConn.getIdsSearch(
                "rms.servida",
                new Object[] {
                        queryVectorServidas
                }
        );

        HashMap hashMapFieldsServidas = new HashMap();
        hashMapFieldsServidas.put(
                "fields", new Object[] {
                        "id", "name", "fecha", "bachada", "mrp_id"
                }
        );

        Object[] rmsServidas = rpcConn.getResultRead(
                "rms.servida",
                rmsServidasIds,
                hashMapFieldsServidas
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date servidaFecha;

        for(Object rmsServida : rmsServidas) {

            try {
                servidaFecha = sdf.parse(((HashMap<String, String>)rmsServida).get("fecha"));
            } catch (Exception ex) {
                servidaFecha = new Date();
            }

            lstServidas.add(
                    new RmsServida(
                            ((HashMap<String, Integer>)rmsServida).get("id"),
                            ((HashMap<String, String>)rmsServida).get("name"),
                            servidaFecha,
                            ((HashMap<String, Integer>)rmsServida).get("bachada"),
                            mrp.getProductId(),
                            mrp.getProductQty(),
                            mrp.getIdMaquinaReparto() + "",
                            false,
                            mrp.getIdMaquinaReparto()
                    )
            );
        }
        return lstServidas.get(0);
    }*/

    public ArrayList<RmsServidaLine> getServidasLineByServida(int idServida) {
        ArrayList<RmsServidaLine> lstServidaLines = new ArrayList<>();

        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] { "servida_id", "=", idServida });

        Object[] rmsServidaLineIds = rpcConn.getIdsSearch(
                "catt.servida.line",
                new Object[] {
                        queryVector
                }
        );

        HashMap hashMapFields = new HashMap();
        hashMapFields.put(
                "fields", new Object[] {
                        "id", "qty_uom", "qty_programada", "lot_rancho_id", "formula_product_id"
                }
        );

        Object[] rmsServidaLines = rpcConn.getResultRead(
                "catt.servida.line",
                rmsServidaLineIds,
                hashMapFields
        );

        for(Object sl : rmsServidaLines) {
            lstServidaLines.add(
                    new RmsServidaLine(
                            ((HashMap<String, Integer>)sl).get("id"),
                            idServida,
                            ((HashMap<String, Double>)sl).get("qty_uom"),
                            ((HashMap<String, Double>)sl).get("qty_programada"),
                            ((HashMap<String, Object[]>)sl).get("lot_rancho_id")[1].toString(),
                            null,
                            false,
                            ((HashMap<String, Object[]>)sl).get("formula_product_id")[1].toString(),
                            ""
                    )
            );
        }

        lstServidaLines.sort(Comparator.comparing(RmsServidaLine::getId));
        return lstServidaLines;
    }

    public boolean updateServidaLines(ArrayList<RmsServidaLine> lstServidaLines) {
        boolean isUpdated = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap hashMapToUpdate;

        for(RmsServidaLine sl : lstServidaLines) {
            //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //calendar.setTime(sl.getFecha());
            //calendar.add(Calendar.HOUR, 6);
            String fechaFinReparto = df.format(sl.getFechaFinReparto());
            String fechaInicioReparto = df.format(sl.getFechaInicioReparto());

            hashMapToUpdate = new HashMap();
            hashMapToUpdate.put("qty_uom", sl.getQtyUom());
            hashMapToUpdate.put("fecha", fechaFinReparto);
            hashMapToUpdate.put("fecha_inicio", fechaInicioReparto);
            isUpdated = rpcConn.update(
                    "catt.servida.line",
                    sl.getId(),
                    hashMapToUpdate
            );
            if(!isUpdated) {
                return false;
            }
        }

        Log.i(this.getClass().getName(), "UPDATED!!");

        return isUpdated;
    }

    public String cierraServida(int idServida) {
        try {
            Object[] args = new Object[]{
                    new Object[]{idServida}
            };

            Object response = rpcConn.executeMethod(
                    "catt.servida", "button_aplicar", args);

            System.out.println("--------------RESPUESTA PROCESA SERVIDA---------");
            System.out.println(response.toString());

            return response.toString();
        } catch (Exception ex) {
            System.out.println("--------------RESPUESTA PROCESA SERVIDA---------");
            System.out.println(ex.getMessage());
            return "ERROR " + ex.getMessage();
        }
    }
}
