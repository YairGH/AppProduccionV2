package com.ygh.produccion.appproduccionv2.pojos;

public class StockPicking {
    private int id;
    private String name;
    private int bachada;
    private String formula;
    private double cantidad;

    public StockPicking(int id, String name, int bachada) {
        this.id = id;
        this.name = name;
        this.bachada = bachada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBachada() {
        return bachada;
    }

    public void setBachada(int bachada) {
        this.bachada = bachada;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
