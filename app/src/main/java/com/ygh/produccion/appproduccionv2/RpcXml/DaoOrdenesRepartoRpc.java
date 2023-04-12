package com.ygh.produccion.appproduccionv2.RpcXml;

import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class DaoOrdenesRepartoRpc {

    private RpcConn rpcConn;

    public DaoOrdenesRepartoRpc() {
        this.rpcConn = new RpcConn();
    }

    public ArrayList<MrpProduction> getProductOrdersToAsignarMaquina() {
        try {
            ArrayList<MrpProduction> lstOrderProducts = new ArrayList<>();

            Vector queryVector = new Vector();
            queryVector.addElement(new Object[]{"state", "=", "done"});
            queryVector.addElement(new Object[]{"maquina_reparto_id", "=", false});

            Object[] mrpProductionIds = rpcConn.getIdsSearch(
                    "mrp.production",
                    new Object[]{
                            queryVector
                    });

            if (mrpProductionIds == null || mrpProductionIds.length == 0)
                return lstOrderProducts;

            HashMap hashMapFields = new HashMap();
            hashMapFields.put("fields", new Object[]{"id", "name", "product_id", "product_qty", "location_src_id", "bachada", "maquina_reparto_id", "maquina_id", "date_finished", "prioridad_formula"});

            Object[] mrpProduction = rpcConn.getResultRead(
                    "mrp.production",
                    mrpProductionIds,
                    hashMapFields
            );

            for (Object mp : mrpProduction) {

                lstOrderProducts.add(
                        new MrpProduction(
                                ((HashMap<String, Integer>) mp).get("id"),
                                ((HashMap<String, String>) mp).get("name"),
                                ((HashMap<String, Object[]>) mp).get("product_id")[1].toString(),
                                (((HashMap<String, Double>) mp).get("product_qty")),
                                Integer.parseInt((((HashMap<String, Object[]>) mp).get("location_src_id")[0]).toString()),
                                ((HashMap<String, Integer>) mp).get("bachada"),
                                ((HashMap<String, Object[]>) mp).get("maquina_id")[1].toString(),
                                Integer.parseInt(((HashMap<String, Object[]>) mp).get("maquina_id")[0].toString()),
                                ((HashMap<String, Integer>) mp).get("prioridad_formula")
                        )
                );
            }

            Collections.sort(lstOrderProducts, new Comparator<MrpProduction>() {
                @Override
                public int compare(MrpProduction o1, MrpProduction o2) {
                    return o2.getId() - o1.getId();
                }
            });

            return lstOrderProducts;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public MrpProduction getNextProductOrderByMaquinaToAsignarReparto(int idMaquina) {
        ArrayList<MrpProduction> lstOrderProducts = new ArrayList<>();

        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] {"state", "=", "done"});
        queryVector.addElement(new Object[] {"maquina_id", "=", idMaquina});
        queryVector.addElement(new Object[] {"maquina_reparto_id", "=", false});
        //queryVector.addElement(new Object[] {"date_finished", "=", true});

        Object[] mrpProductionIDs = rpcConn.getIdsSearch(
                "mrp.production",
                new Object[] {
                        queryVector
                });

        if(mrpProductionIDs == null || mrpProductionIDs.length == 0)
            return null;

        HashMap hashMapFields = new HashMap();
        hashMapFields.put("fields", new Object[] {"id", "name", "product_id", "product_qty", "location_src_id", "bachada", "maquina_reparto_id", "maquina_id", "date_finished", "prioridad_formula"});

        Object[] mrpProduction = rpcConn.getResultRead(
                "mrp.production",
                mrpProductionIDs,
                hashMapFields
        );

        MrpProduction mrpP = null;

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateFinished;

        for(Object mp : mrpProduction) {
            try {
                dateFinished = parser.parse(((HashMap<String, String>) mp).get("date_finished").toString());
            } catch (Exception ex) {
                dateFinished = new Date();
            }

            mrpP = new MrpProduction(
                    ((HashMap<String, Integer>)mp).get("id"),
                    ((HashMap<String, String>)mp).get("name"),
                    ((HashMap<String, Object[]>)mp).get("product_id")[1].toString(),
                    (((HashMap<String, Double>)mp).get("product_qty")),
                    Integer.parseInt((((HashMap<String, Object[]>)mp).get("location_src_id")[0]).toString()),
                    ((HashMap<String, Integer>)mp).get("bachada"),
                    ((HashMap<String, Object[]>)mp).get("maquina_id")[1].toString(),
                    Integer.parseInt(((HashMap<String, Object[]>)mp).get("maquina_id")[0].toString()),
                    ((HashMap<String, Integer>)mp).get("prioridad_formula")
            );

            mrpP.setFechaTerminacion(dateFinished);

            lstOrderProducts.add(
                    mrpP
            );
        }

        Collections.sort(lstOrderProducts, new Comparator<MrpProduction>() {
            @Override
            public int compare(MrpProduction o1, MrpProduction o2) {
                return o1.getFechaTerminacion().compareTo(o2.getFechaTerminacion());
            }
        });

        return lstOrderProducts.get(0);
    }
}
