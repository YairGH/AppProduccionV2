package com.ygh.produccion.appproduccionv2.pojos;

import java.io.Serializable;

public class Usuario implements Serializable {

    public static final int TIPO_PRODUCCION = 0;
    public static final int TIPO_REPARTO = 1;
    public static final int TIPO_AMBOS = 2;
    public static int ID_MAQUINA_REPARTO = 0;
    public static String NOMBRE_MAQUINA = "";

    public static final String TABLE_USUARIO = "usuario";
    public static final String COLUMN_USUARIO_USERNAME = "username";
    public static final String COLUMN_USUARIO_PASSWORD = "password";
    public static final String COLUMN_USUARIO_ID_MAQUINA = "id_maquina";
    public static final String COLUMN_USUARIO_TIPO_USUARIO = "tipo_usuario";
    public static final String COLUMN_USUARIO_ID_MAQUINA_REPARTO = "id_maquina_reparto";
    public static final String COLUMN_USUARIO_NOMBRE_MAQUINA_REPARTO = "nombre_maquina_reparto";
    public static final String COLUMN_USUARIO_ACTIVO = "usuario_activo";

    private String username;
    private String password;
    private int idMaquina;
    private int idMaquinaReparto;
    private int tipoUsuario;
    private String nombreMaquinaReparto;
    private int usuarioActivo;

    public Usuario(String username, String password, int idMaquina, int tipoUsuario, int idMaquinaReparto, String nombreMaquinaReparto) {
        this.username = username;
        this.password = password;
        this.idMaquina = idMaquina;
        this.tipoUsuario = tipoUsuario;
        this.idMaquinaReparto = idMaquinaReparto;
        this.nombreMaquinaReparto = nombreMaquinaReparto;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getIdMaquina() {
        return idMaquina;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdMaquinaReparto() {
        return idMaquinaReparto;
    }

    public String getNombreMaquinaReparto() {
        return nombreMaquinaReparto;
    }

    public int getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(int usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}
