package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;
import java.util.Date;

public class RmsServidaLine implements Serializable {
    public static final String TABLE_SERVIDA_LINE = "servida_line";
    public static final String COLUMN_SERVIDA_LINE_ID = "id";
    public static final String COLUMN_SERVIDA_LINE_ID_SERVIDA = "id_servida";
    public static final String COLUMN_SERVIDA_LINE_QTY_PROGRAMADA = "qty_programada";
    public static final String COLUMN_SERVIDA_LINE_QTY_UOM = "qty_uom";
    public static final String COLUMN_SERVIDA_LINE_LOT_RANCHO = "lot_rancho_txt";
    public static final String COLUMN_SERVIDA_LINE_FECHA = "fecha";
    public static final String COLUMN_SERVIDA_LINE_IS_PROCESADA = "is_procesada";
    public static final String COLUMN_SERVIDA_LINE_FH_INICIO_REP = "fecha_inicio_reparto";
    public static final String COLUMN_SERVIDA_LINE_FH_FIN_REP = "fecha_fin_reparto";

    private int id;
    private int idServida;
    private double qtyUom;
    private double qtyProgramada;
    private String lotRanchoTxt;
    private Date fecha;
    private boolean isProcesada;
    private String formula_product_id;
    private String formula_id;
    private Date fechaInicioReparto;
    private Date fechaFinReparto;

    public RmsServidaLine(int id, double qtyProgramada, String lotRanchoTxt) {
        this.id = id;
        this.qtyProgramada = qtyProgramada;
        this.lotRanchoTxt = lotRanchoTxt;
    }

    public RmsServidaLine(int id, int idServida, double qtyUom, double qtyProgramada, String lotRanchoTxt, Date fecha, boolean isProcesada, String formula_product_id, String formula_id) {
        this.id = id;
        this.idServida = idServida;
        this.qtyUom = qtyUom;
        this.qtyProgramada = qtyProgramada;
        this.lotRanchoTxt = lotRanchoTxt;
        this.fecha = fecha;
        this.isProcesada = isProcesada;
        this.formula_product_id = formula_product_id;
        this.formula_id = formula_id;
    }

    public RmsServidaLine(int id, double qtyUom, Date fecha) {
        this.id = id;
        this.qtyUom = qtyUom;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public double getQtyUom() {
        return qtyUom;
    }

    public double getQtyProgramada() {
        return qtyProgramada;
    }

    public String getLotRanchoTxt() {
        return lotRanchoTxt;
    }

    public Date getFecha() {
        return fecha;
    }

    public boolean isProcesada() {
        return isProcesada;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setQtyUom(double qtyUom) {
        this.qtyUom = qtyUom;
    }

    public void setProcesada(boolean procesada) {
        isProcesada = procesada;
    }

    public String getFormula_product_id() {
        return formula_product_id;
    }

    public void setFormula_product_id(String formula_product_id) {
        this.formula_product_id = formula_product_id;
    }

    public String getFormula_id() {
        return formula_id;
    }

    public void setFormula_id(String formula_id) {
        this.formula_id = formula_id;
    }

    public Date getFechaInicioReparto() {
        return fechaInicioReparto;
    }

    public void setFechaInicioReparto(Date fechaInicioReparto) {
        this.fechaInicioReparto = fechaInicioReparto;
    }

    public Date getFechaFinReparto() {
        return fechaFinReparto;
    }

    public void setFechaFinReparto(Date fechaFinReparto) {
        this.fechaFinReparto = fechaFinReparto;
    }

    @Override
    public String toString() {
        return "ID:" + this.id + " Qty: " + this.qtyProgramada + " Lote: " + this.getLotRanchoTxt();
    }
}
