package com.ygh.produccion.appproduccionv2.pojos;

public class ConfigVariablesBascula {
    public static String TABLE_CONFIG_VARIABLES = "configVariables";
    public static String COLUMN_PESO_MINIMO = "pesoMinimo";
    public static String COLUMN_TIPO_SIG_INSUMO = "tipoSiguienteInsumo";
    public static String COLUMN_TIPO_MEDIDA = "tipoMedida";
    public static String COLUMN_MEDIDA_VALOR = "medidaValor";
    public static String COLUMN_TIEMPO_SIG_INSUMO = "tiempoSigInsumo";
    public static String COLUMN_BUZZER = "buzzer";
    public static String COLUMN_DONE_HEADER = "doneHeader";
    public static String COLUMN_PREALARM1 = "mrp_prealarm1";
    public static String COLUMN_PREALARM2 = "mrp_prealarm2";

    public ConfigVariablesBascula() {}

    public ConfigVariablesBascula(Double pesoMinimo, String tipoSigInsumo, String tipoMedidaSigInsumo, Double valorToSigInsumo, Double tiempoSiguienteInsumo, Boolean buzzer, String doneHeader, Double prealarm1, Double prealarm2) {
        this.pesoMinimo = pesoMinimo;
        this.tipoSigInsumo = tipoSigInsumo;
        this.tipoMedidaSigInsumo = tipoMedidaSigInsumo;
        this.valorToSigInsumo = valorToSigInsumo;
        this.tiempoSiguienteInsumo = tiempoSiguienteInsumo;
        this.buzzer = buzzer;
        this.doneHeader = doneHeader;
        this.prealarm1 = prealarm1;
        this.prealarm2 = prealarm2;
    }

    private Double prealarm1;
    private Double prealarm2;

    //Indica si se va a activar el buzzer
    private Boolean buzzer;

    //String del comando inicial para el buzzer
    private String doneHeader;

    //Peso minimo el cual será ignorado por la báscula
    private Double pesoMinimo;

    //Consideración para ver si para pasar al siguiente insumo es manual o automático
    private String tipoSigInsumo;

    //Si el siguiente insumo es automático puede ser por porcentaje o por cantidad
    private String tipoMedidaSigInsumo;

    //Valor con el cual comparar si pasa al siguiente insumo
    private Double valorToSigInsumo;

    //Tiempo de espera para pasar al siguiente insumo
    private Double tiempoSiguienteInsumo;

    public Double getPesoMinimo() {
        return pesoMinimo;
    }

    public void setPesoMinimo(Double pesoMinimo) {
        this.pesoMinimo = pesoMinimo;
    }

    public String getTipoSigInsumo() {
        return tipoSigInsumo;
    }

    public void setTipoSigInsumo(String tipoSigInsumo) {
        this.tipoSigInsumo = tipoSigInsumo;
    }

    public String getTipoMedidaSigInsumo() {
        return tipoMedidaSigInsumo;
    }

    public void setTipoMedidaSigInsumo(String tipoMedidaSigInsumo) {
        this.tipoMedidaSigInsumo = tipoMedidaSigInsumo;
    }

    public Double getValorToSigInsumo() {
        return valorToSigInsumo;
    }

    public void setValorToSigInsumo(Double valorToSigInsumo) {
        this.valorToSigInsumo = valorToSigInsumo;
    }

    public Double getTiempoSiguienteInsumo() {
        return tiempoSiguienteInsumo;
    }

    public void setTiempoSiguienteInsumo(Double tiempoSiguienteInsumo) {
        this.tiempoSiguienteInsumo = tiempoSiguienteInsumo;
    }

    public Boolean getBuzzer() {
        return buzzer;
    }

    public void setBuzzer(Boolean buzzer) {
        this.buzzer = buzzer;
    }

    public String getDoneHeader() {
        return doneHeader;
    }

    public void setDoneHeader(String doneHeader) {
        this.doneHeader = doneHeader;
    }

    public Double getPrealarm1() {
        return prealarm1;
    }

    public void setPrealarm1(Double prealarm1) {
        this.prealarm1 = prealarm1;
    }

    public Double getPrealarm2() {
        return prealarm2;
    }

    public void setPrealarm2(Double prealarm2) {
        this.prealarm2 = prealarm2;
    }
}
