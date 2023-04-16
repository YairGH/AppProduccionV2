package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;
import java.util.Date;

public class RmsServida implements Serializable {
    public static final String TABLE_SERVIDA = "servida";
    public static final String COLUMN_SERVIDA_ID = "id";
    public static final String COLUMN_SERVIDA_NAME = "name";
    public static final String COLUMN_SERVIDA_BACHADA = "bachada";
    public static final String COLUMN_SERVIDA_FORMULA = "formula";
    public static final String COLUMN_SERVIDA_IS_PROCESADA = "is_procesada";
    public static final String COLUMN_SERVIDA_ID_MAQUINA = "id_maquina_reparto";
    public static final String COLUMN_SERVIDA_MAQUINA_REPARTO = "maquina_reparto";
    public static final String COLUMN_SERVIDA_TOTAL_REPARTIR = "total_repartir";
    public static final String COLUMN_SERVIDA_TOTAL_PRODUCIDA = "total_producida";
    public static final String COLUMN_SERVIDA_FECHA = "fecha";

    private int id;
    private String name;
    private Date fecha;
    private int bachada;
    private String formula;
    private double cantidad;
    private String maquinaReparto;
    private int idMaquina;
    private boolean isProcesada;
    private double totalRepartir;
    private double totalProducida;
    private String mrpState;
    private String pickRepartoState;

    public RmsServida(int id, String formula, int bachada, String name, int idMaquinaReparto, Double totalRepartir) {
        this.formula = formula;
        this.bachada = bachada;
        this.name = name;
        this.id = id;
        this.idMaquina = idMaquinaReparto;
        this.totalRepartir = totalRepartir;
    }

    public RmsServida(int id, String name, Date fecha, int bachada, String formula, double cantidad, String maquinaReparto, boolean isProcesada, int idMaquinaReparto, String mrpState, String pickRepartoState) {
        this.id = id;
        this.name = name;
        this.fecha = fecha;
        this.bachada = bachada;
        this.formula = formula;
        this.cantidad = cantidad;
        this.maquinaReparto = maquinaReparto;
        this.isProcesada = isProcesada;
        this.idMaquina = idMaquinaReparto;
        this.totalRepartir = totalRepartir;
        this.fecha = fecha;
        this.mrpState = mrpState;
        this.pickRepartoState = pickRepartoState;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getBachada() {
        return bachada;
    }

    public String getFormula() {
        return formula;
    }

    public double getCantidad() {
        return cantidad;
    }

    public String getMaquinaReparto() {
        return maquinaReparto;
    }

    public void setMaquinaReparto(String maquinaReparto) {
        this.maquinaReparto = maquinaReparto;
    }

    public boolean isProcesada() {
        return isProcesada;
    }

    public void setProcesada(boolean procesada) {
        isProcesada = procesada;
    }

    public int getIdMaquina() {
        return idMaquina;
    }

    public double getTotalRepartir() {
        return totalRepartir;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotalProducida() {
        return totalProducida;
    }

    public void setTotalProducida(double totalProducida) {
        this.totalProducida = totalProducida;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String isMrpState() {
        return mrpState;
    }

    public void setMrpState(String mrpState) {
        this.mrpState = mrpState;
    }

    public String isPickRepartoState() {
        return pickRepartoState;
    }

    public void setPickRepartoState(String pickRepartoState) {
        this.pickRepartoState = pickRepartoState;
    }
}
