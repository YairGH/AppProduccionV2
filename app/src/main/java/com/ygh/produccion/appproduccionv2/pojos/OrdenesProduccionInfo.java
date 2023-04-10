package com.ygh.produccion.appproduccionv2.pojos;

public class OrdenesProduccionInfo {
    public static final String TABLE_ORDEN_TRASLADO = "orden_traslado";
    public static final String COLUMN_TRASLADO_BACHADA = "bachada";
    public static final String COLUMN_TRASLADO_ORDEN = "orden";
    public static final String COLUMN_TRASLADO_CANTIDAD = "cantidad";
    public static final String COLUMN_TRASLADO_FORMULA = "formula";
    public static final String COLUMN_TRASLADO_ID = "id_traslado";

    private int bachada;
    private String ordenFabricacion;
    private String ordenTraslado;
    private double cantidad;
    private String formula;
    private int idTraslado;
    private int idOrdenProduccion;

    public OrdenesProduccionInfo() {}

    public OrdenesProduccionInfo(int bachada, String ordenTraslado, Double cantidad, String formula, int idTraslado) {
        this.bachada = bachada;
        this.ordenTraslado = ordenTraslado;
        this.cantidad = cantidad;
        this.formula = formula;
        this.idTraslado = idTraslado;
    }

    public int getBachada() {
        return bachada;
    }

    public void setBachada(int bachada) {
        this.bachada = bachada;
    }

    public String getOrdenFabricacion() {
        return ordenFabricacion;
    }

    public void setOrdenFabricacion(String ordenFabricacion) {
        this.ordenFabricacion = ordenFabricacion;
    }

    public String getOrdenTraslado() {
        return ordenTraslado;
    }

    public void setOrdenTraslado(String ordenTraslado) {
        this.ordenTraslado = ordenTraslado;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getIdTraslado() {
        return idTraslado;
    }

    public void setIdTraslado(int idTraslado) {
        this.idTraslado = idTraslado;
    }

    public int getIdOrdenProduccion() {
        return idOrdenProduccion;
    }

    public void setIdOrdenProduccion(int idOrdenProduccion) {
        this.idOrdenProduccion = idOrdenProduccion;
    }
}
