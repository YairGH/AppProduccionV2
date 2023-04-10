package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;
import java.util.Date;

public class MrpProduction implements Serializable {
    public static final String PRODUCTION_NAME_FIELD = "productionName";

    public static final String TABLE_ORDEN_PRODUCCION = "orden_produccion";
    public static final String COLUMN_PRODUCCION_ID = "id";
    public static final String COLUMN_PRODUCCION_NOMBRE = "nombre";
    public static final String COLUMN_PRODUCCION_FORMULA = "formula";
    public static final String COLUMN_PRODUCCION_FECHA_PROGRAMADA = "fecha_programada";
    public static final String COLUMN_PRODUCCION_BACHADA = "bachada";

    private int id;
    private String name;
    private String productId;
    private String productName;
    private double productQty;
    private int locationSrcId;
    private int bachada;
    private int idMaquinaReparto;
    private int selectedMaquinaReparto;
    private String nombreMaquinaProduccion;
    private Date fechaTerminacion;
    private String fechaProgramada;
    private int idMaquinaProduccion;
    private int prioridad;
    private String state;

    public MrpProduction(){}

    public MrpProduction(
            int id,
            String name,
            String productId,
            String productName,
            double productQty,
            int locationSrcId,
            int bachada,
            String nombreMaquinaProduccion,
            int idMaquinaProduccion,
            int prioridad,
            String fechaProgramada) {
        this.id = id;
        this.name = name;
        this.productId = productId;
        this.productQty = productQty;
        this.locationSrcId = locationSrcId;
        this.bachada = bachada;
        this.idMaquinaProduccion = idMaquinaProduccion;
        this.prioridad = prioridad;
        this.productName = productName;
        this.fechaProgramada = fechaProgramada;
        this.nombreMaquinaProduccion = nombreMaquinaProduccion;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductId() {
        return productId;
    }

    public double getProductQty() {
        return productQty;
    }

    public int getLocationSrcId() {
        return locationSrcId;
    }

    public int getBachada() {
        return bachada;
    }

    public int getIdMaquinaReparto() {
        return idMaquinaReparto;
    }

    public String getNombreMaquinaProduccion() {
        return nombreMaquinaProduccion;
    }

    public void setIdMaquinaReparto(int idMaquinaReparto) {
        this.idMaquinaReparto = idMaquinaReparto;
    }

    public int getSelectedMaquinaReparto() {
        return selectedMaquinaReparto;
    }

    public void setSelectedMaquinaReparto(int selectedMaquinaReparto) {
        this.selectedMaquinaReparto = selectedMaquinaReparto;
    }

    public Date getFechaTerminacion() {
        return fechaTerminacion;
    }

    public void setFechaTerminacion(Date fechaTerminacion) {
        this.fechaTerminacion = fechaTerminacion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getIdMaquinaProduccion() {
        return idMaquinaProduccion;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(String fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBachada(int bachada) {
        this.bachada = bachada;
    }

    @Override
    public String toString() {
        return this.name;
    }

}

