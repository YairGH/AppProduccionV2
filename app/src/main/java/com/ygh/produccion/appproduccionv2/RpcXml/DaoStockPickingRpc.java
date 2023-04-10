package com.ygh.produccion.appproduccionv2.RpcXml;

import com.ygh.produccion.appproduccionv2.pojos.StockPicking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class DaoStockPickingRpc {
    private RpcConn rpcConn;

    public DaoStockPickingRpc() {
        this.rpcConn = new RpcConn();
    }

    /**
     * Obtengo los stock picking de las máquinas
     * @param idMaquina
     */
    public List<StockPicking> getStockPickingFromMaquinaId(int idMaquina) {
        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] {"state", "=", "assigned"});
        if(idMaquina != 0) {
            queryVector.addElement(new Object[]{"maquina_reparto_id", "=", idMaquina});
        }

        Object[] stockPickingIDs = rpcConn.getIdsSearch(
                "stock.picking",
                new Object[] {
                        queryVector
                });

        List<StockPicking> lstStockPicking = new ArrayList<>();
        StockPicking sp = null;
        if(stockPickingIDs != null && stockPickingIDs.length > 0) {
            HashMap hashMapFields = new HashMap();
            hashMapFields.put("fields", new Object[] {"id", "name", "bachada", "maquina_reparto_id", "state"});

            Object[] stockPickingList = rpcConn.getResultRead(
                    "stock.picking",
                    stockPickingIDs,
                    hashMapFields
            );

            int id = 0;
            String name = "";
            int bachada = 0;
            String state = "";
            String maquina = "";
            String formula = "";
            double cantidad;

            for(Object spResult : stockPickingList) {
                id = ((HashMap<String, Integer>)spResult).get("id");
                name = ((HashMap<String, String>)spResult).get("name");
                bachada = ((HashMap<String, Integer>)spResult).get("bachada");
                state = ((HashMap<String, String>)spResult).get("state");
                maquina = ((HashMap<String, Object[]>)spResult).get("maquina_reparto_id")[1].toString();

                sp = new StockPicking(
                        id, name, bachada
                );

                Object[] args = new Object[] {
                        new Object[] {id}
                };

                /**Obtengo la cantidad y la formula desde el método get_cantidad_programada_almentacion **/
                Object infoPicking = rpcConn.executeMethod(
                        "stock.picking", "get_cantidad_programada_almentacion", args);

                cantidad = 0.0;
                try {
                    JSONObject jobj = new JSONObject(infoPicking.toString());
                    cantidad = jobj.getDouble("cantidad_programada");

                    String objFormula = jobj.getString("product_id");
                    StringTokenizer st = new StringTokenizer(objFormula, "\"");
                    st.nextToken();
                    formula = st.nextToken();

                } catch (Exception ex) {
                    cantidad = 0.0;
                    System.out.println("EXCEPTION: --- " + ex.getMessage());
                }

                sp.setCantidad(cantidad);
                sp.setFormula(formula);
                lstStockPicking.add(sp);
            }
        }
        return lstStockPicking;
    }

    /**
     * Ejecuta método en Odoo para registrar la pesada de la orden de traslado
     * @param idTraslado
     * @param cantidad
     * @return
     */
    public String procesaTrasladoAlimento(int idTraslado, Double cantidad) {
        try {
            Object[] args = new Object[]{
                    new Object[]{idTraslado}, cantidad
            };

            Object response = rpcConn.executeMethod(
                    "stock.picking", "procesar_traslado_alimentacion", args);

            System.out.println("--------------RESPUESTA PROCESA TRASLADO---------");
            System.out.println(response.toString());

            return response.toString();
        } catch (Exception ex) {
            System.out.println("--------------RESPUESTA PROCESA TRASLADO ERROR---------");
            System.out.println(ex.getMessage());
            return "ERROR " + ex.getMessage();
        }
    }
}
