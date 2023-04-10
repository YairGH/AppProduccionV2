package com.ygh.produccion.appproduccionv2.RpcXml;

import com.ygh.produccion.appproduccionv2.pojos.MrpProduction;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;
import com.ygh.produccion.appproduccionv2.pojos.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class DaoOrdenesProduccionRpc {
    private RpcConn rpcConn;

    public DaoOrdenesProduccionRpc() {
        this.rpcConn = new RpcConn();
    }

    /**
     * Obtengo la orden de produccion que se va a procesar
     * @param idMaquina
     * @return
     */
    public MrpProduction getProductOrdersByMaquinaId(int idMaquina, int idOrden) {

        ArrayList<MrpProduction> lstOrderProducts = new ArrayList<>();

        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] {"state", "=", "confirmed"});
        //queryVector.addElement(new Object[] {"state", "=", "to_close"});
        if(idMaquina != 0) {
            queryVector.addElement(new Object[]{"maquina_id", "=", idMaquina});
        }

        Object[] mrpProductionIds = rpcConn.getIdsSearch(
                "mrp.production",
                new Object[] {
                        queryVector
                });

        if(mrpProductionIds == null || mrpProductionIds.length == 0)
            return null;

        HashMap hashMapFields = new HashMap();
        hashMapFields.put("fields", new Object[] {"id", "name", "product_id", "product_qty", "location_src_id", "bachada", "maquina_id", "date_finished", "prioridad_formula", "state", "date_planned_start"});

        Object[] mrpProduction = rpcConn.getResultRead(
                "mrp.production",
                mrpProductionIds,
                hashMapFields
        );

        for(Object mp : mrpProduction) {
            MrpProduction p = new MrpProduction(
                    ((HashMap<String, Integer>)mp).get("id"),
                    ((HashMap<String, String>)mp).get("name"),
                    ((HashMap<String, Object[]>)mp).get("product_id")[0].toString(),
                    ((HashMap<String, Object[]>)mp).get("product_id")[1].toString(),
                    (((HashMap<String, Double>)mp).get("product_qty")),
                    Integer.parseInt((((HashMap<String, Object[]>)mp).get("location_src_id")[0]).toString()),
                    ((HashMap<String, Integer>)mp).get("bachada"),
                    ((HashMap<String, Object[]>)mp).get("maquina_id")[1].toString(),
                    Integer.parseInt(((HashMap<String, Object[]>)mp).get("maquina_id")[0].toString()),
                    ((HashMap<String, Integer>)mp).get("prioridad_formula"),
                    ((HashMap<String, String>)mp).get("date_planned_start")
            );
            p.setState(((HashMap<String, String>)mp).get("state"));
            lstOrderProducts.add(
                    p
            );
        }



        Collections.sort(lstOrderProducts, new Comparator<MrpProduction>() {
            @Override
            public int compare(MrpProduction o1, MrpProduction o2) {
                Integer p1 = o1.getPrioridad();
                Integer p2 = o2.getPrioridad();
                int pComp = p1.compareTo(p2);
                if(pComp != 0) {
                    return pComp;
                } else {
                    Integer b1 = o1.getBachada();
                    Integer b2 = o2.getBachada();
                    return b1.compareTo(b2);
                }
            }
        });

        if(idOrden == 0) {
            for (int i = 0; i < lstOrderProducts.size(); i++) {
                if (lstOrderProducts.get(i).getBachada() > 0) {
                    return lstOrderProducts.get(i);
                }
            }
        } else {
            for (int i = 0; i < lstOrderProducts.size(); i++) {
                if (lstOrderProducts.get(i).getId() == idOrden) {
                    return lstOrderProducts.get(i);
                }
            }
        }
        return null;
        //return lstOrderProducts.get(lstOrderProducts.size() - 1);
    }

    /**
     * Obtiene los detalles de insumos de la orden de producción. Se obtiene por el nombre de la orden de producción
     * @param name
     * @return
     */

    public List<StockMove> getStockByNameProduction(int idProduction) {
        List<StockMove> lstStockMoves = new ArrayList<>();
        Object[] searchQueryName = new Object[] {"raw_material_production_id", "=", idProduction};
     //   Object[] searchQueryName = new Object[] {"created_production_id", "=", idMrpProduction};

        Vector queryVector = new Vector();
        queryVector.addElement(searchQueryName);

        Object[] stockMovesIds = rpcConn.getIdsSearch(
                "stock.move",
                new Object[] {
                        queryVector
                });

        Arrays.sort(stockMovesIds);

        HashMap hashMapFields = new HashMap();
        hashMapFields.put("fields", new Object[] {"id", "name", "product_id", "product_uom_qty", "qty_programada", "catt_espera_pesaje", "tiempo_mezclado", "mrp_ds_done_line", "pre_pesado"});

        Object[] stockMoves = rpcConn.getResultRead(
                "stock.move",
                stockMovesIds,
                hashMapFields
        );
        for(Object stockMove : stockMoves) {

            String dsDone = "";
            try {
                dsDone = ((HashMap<String, String>) stockMove).get("mrp_ds_done_line").toString();
            } catch (Exception ex) {
                dsDone = "";
            }

            lstStockMoves.add(
                    new StockMove(
                              ((HashMap<String, Object[]>) stockMove).get("product_id")[1].toString(),
                            //(((HashMap<String, Double>) stockMove).get("qty_programada")),
                            (((HashMap<String, Double>) stockMove).get("product_uom_qty")),
                            (((HashMap<String, Integer>) stockMove).get("id")),
                            Integer.parseInt(((HashMap<String, Object[]>) stockMove).get("product_id")[0].toString()),
                            (((HashMap<String, Double>) stockMove).get("product_uom_qty")),
                            (((HashMap<String, Boolean>) stockMove).get("catt_espera_pesaje")),
                            (((HashMap<String, Double>) stockMove).get("tiempo_mezclado")),
                            dsDone,
                            (((HashMap<String, Boolean>) stockMove).get("pre_pesado"))
                    )
            );
        }

       /*Collections.sort(lstStockMoves, new Comparator<StockMove>() {
            @Override
            public int compare(StockMove o1, StockMove o2) {
                return o2.getId() - o1.getId();
            }
        });*/

        lstStockMoves.sort(Comparator.comparing(StockMove::getId));

        //Elimino el último elemeno que es la formula, no es válido el valor
       // lstStockMoves.remove(lstStockMoves.size() - 1); //Ya no aplica, se modifico modelo de datos

        return lstStockMoves;
    }

    /**
     * Obtengo las ordenes de producción por máquina de producción y ordeno por bachada y prioridad
     * @param idMaquina
     * @return
     */
    public List<MrpProduction> getOrdenesProduccionByMaquina(int idMaquina) {

        ArrayList<MrpProduction> lstOrderProducts = new ArrayList<>();

        Vector queryVector = new Vector();
        queryVector.addElement(new Object[] {"state", "=", "confirmed"});
        //queryVector.addElement(new Object[] {"state", "=", "to_close"});
        if(idMaquina != 0) {
                 queryVector.addElement(new Object[]{"maquina_id", "=", idMaquina});
        }

        Object[] mrpProductionIds = rpcConn.getIdsSearch(
                "mrp.production",
                new Object[] {
                        queryVector
                });

        if(mrpProductionIds == null || mrpProductionIds.length == 0)
            return null;

        HashMap hashMapFields = new HashMap();
        hashMapFields.put("fields", new Object[] {"id", "name", "product_id", "product_qty", "location_src_id", "bachada", "maquina_id", "date_finished", "prioridad_formula", "state", "date_planned_start"});

        Object[] mrpProduction = rpcConn.getResultRead(
                "mrp.production",
                mrpProductionIds,
                hashMapFields
        );

        for(Object mp : mrpProduction) {
            MrpProduction p = new MrpProduction(
                    ((HashMap<String, Integer>)mp).get("id"),
                    ((HashMap<String, String>)mp).get("name"),
                    ((HashMap<String, Object[]>)mp).get("product_id")[0].toString(),
                    ((HashMap<String, Object[]>)mp).get("product_id")[1].toString(),
                    (((HashMap<String, Double>)mp).get("product_qty")),
                    Integer.parseInt((((HashMap<String, Object[]>)mp).get("location_src_id")[0]).toString()),
                    ((HashMap<String, Integer>)mp).get("bachada"),
                    ((HashMap<String, Object[]>)mp).get("maquina_id")[1].toString(),
                    Integer.parseInt(((HashMap<String, Object[]>)mp).get("maquina_id")[0].toString()),
                    ((HashMap<String, Integer>)mp).get("prioridad_formula"),
                    ((HashMap<String, String>)mp).get("date_planned_start")
            );
            p.setState(((HashMap<String, String>)mp).get("state"));
            lstOrderProducts.add(
                    p
            );
        }



        Collections.sort(lstOrderProducts, new Comparator<MrpProduction>() {
            @Override
            public int compare(MrpProduction o1, MrpProduction o2) {
                Integer p1 = o1.getPrioridad();
                Integer p2 = o2.getPrioridad();
                int pComp = p1.compareTo(p2);
                if(pComp != 0) {
                    return pComp;
                } else {
                    Integer b1 = o1.getBachada();
                    Integer b2 = o2.getBachada();
                    return b1.compareTo(b2);
                }
            }
        });

        return lstOrderProducts;
    }

    public boolean  updateInsumoOrdenProduccion(int id, Double dblQty) {
        HashMap hashMapToUpdate = new HashMap();
        hashMapToUpdate.put("should_consume_qty", dblQty);
        hashMapToUpdate.put("quantity_done", dblQty);
        boolean isUpdated = rpcConn.update(
                "stock.move",
                id,
                hashMapToUpdate
        );
        return isUpdated;
    }

    public boolean updateOrdenProduccionQty(int idOrderProduction, double qty, String operador) {
        HashMap hashMapToUpdate = new HashMap();
        hashMapToUpdate.put("qty_producing", qty);
        hashMapToUpdate.put("operador", operador);
        boolean isUpdated = rpcConn.update(
                "mrp.production",
                idOrderProduction,
                hashMapToUpdate
        );
        return isUpdated;
    }

    /**
     * Método para cambiar el status de la orden de produccion
     * @param idOrden
     * @return
     */
    public String procesaOrdenProduccion(int idOrden) {
        try {

            Object[] args = new Object[]{
                    new Object[]{idOrden}
            };

            Object response = rpcConn.executeMethod(
                    "mrp.production", "procesar_produccion_alimentacion", args);

            System.out.println("--------------RESPUESTA PROCESA PRODUCCION---------");
            System.out.println(response.toString());

            return response.toString();
        } catch (Exception ex) {
            System.out.println("--------------RESPUESTA PROCESA TRASLADO ERROR---------");
            System.out.println(ex.getMessage());
            return "ERROR " + ex.getMessage();
        }
    }

    public HashMap<String, Object> getConfigVariables() {
        try {
            Object[] args = new Object[]{
                    //new Object[]{ }
            };

            Object response = rpcConn.executeMethod(
                    "res.config.settings", "get_mrp_process_values", args);

            return (HashMap<String, Object>) response;
        } catch (Exception ex) {
            return null;
        }
    }
}
