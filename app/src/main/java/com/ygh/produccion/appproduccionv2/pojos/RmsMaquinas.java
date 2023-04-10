package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;

public class RmsMaquinas implements Serializable {
    public static final String TABLE_MAQUINAS = "maquina";
    public static final String COLUMN_MAQUINA_ID_EN_USO = "maquina_en_uso";

    public static String ESTADO_EN_USO = "en_uso";
    private int id;
    private String name;

    public RmsMaquinas(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}