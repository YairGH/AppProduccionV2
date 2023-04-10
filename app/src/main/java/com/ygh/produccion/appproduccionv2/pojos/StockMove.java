package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;

public class StockMove implements Serializable {

    public static final String TABLE_STOCK_MOVE = "stock_move";
    public static final String COLUMN_STOCK_ID = "id";
    public static final String COLUMN_STOCK_NAME = "name";
    public static final String COLUMN_STOCK_QTY_PROGRAMADA = "qty_programada";
    public static final String COLUMN_STOCK_QTY = "qty";
    public static final String COLUMN_TIEMPO_MEZCLADO = "tiempo_mezclado";
    public static final String COLUMN_DS_DONE = "ds_done";
    public static final String COLUMN_IS_PREPESADO = "is_prepesado";

    private int id;
    private int productNumId;
    private String productId;
    private double productQty;
    private double kilosPesados;
    private Boolean isSelected;
    private Boolean isProcessed;
    private Boolean esperaPesaje;
    private Boolean isInsumoActual;
    private Double qtyProgramada;
    private Double qtyPesada;
    private Double tiempoMezclado;
    private String dsDone;
    private Boolean isPrePesado;

    public StockMove() {}

    public StockMove(String productId, double productQty, int id, int productNumId, double kilosPesados, Boolean esperaPesaje, Double tiempoMezclado, String dsDone, Boolean isPrePesado) {
        this.id = id;
        this.productNumId = productNumId;
        this.productId = productId;
        this.productQty = productQty;
        this.kilosPesados = kilosPesados;
        this.isSelected = false;
        this.isProcessed = false;
        this.esperaPesaje = esperaPesaje;
        this.tiempoMezclado = tiempoMezclado;
        this.dsDone = dsDone;
        this.isPrePesado = isPrePesado;
    }

    public int getProductNumId() {
        return productNumId;
    }

    public int getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public double getProductQty() {
        return productQty;
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public void setProductQty(Double productQty) {
        this.productQty = productQty;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public double getKilosPesados() {
        return kilosPesados;
    }

    public void setKilosPesados(Double kilosPesados) {
        this.kilosPesados = kilosPesados;
    }

    public Boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(Boolean processed) {
        isProcessed = processed;
    }

    public Boolean getEsperaPesaje() {
        return esperaPesaje;
    }

    public Boolean getInsumoActual() {
        return isInsumoActual;
    }

    public void setInsumoActual(Boolean insumoActual) {
        isInsumoActual = insumoActual;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getQtyProgramada() {
        return qtyProgramada;
    }

    public void setQtyProgramada(Double qtyProgramada) {
        this.qtyProgramada = qtyProgramada;
    }

    public Double getQtyPesada() {
        return qtyPesada;
    }

    public void setQtyPesada(Double qtyPesada) {
        this.qtyPesada = qtyPesada;
    }

    public Double getTiempoMezclado() {
        return tiempoMezclado;
    }

    public void setTiempoMezclado(Double tiempoMezclado) {
        this.tiempoMezclado = tiempoMezclado;
    }

    public String getDsDone() {
        return dsDone;
    }

    public void setDsDone(String dsDone) {
        this.dsDone = dsDone;
    }

    public Boolean getPrePesado() {
        return isPrePesado;
    }

    public void setPrePesado(Boolean prePesado) {
        isPrePesado = prePesado;
    }
}
