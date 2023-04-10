package com.ygh.produccion.appproduccionv2.pojos;

public class ServerConfig {
    public static final String TABLE_SERVER_CONFIG = "server_config";
    public static final String COLUMN_SERVER_URL = "server_url";
    public static final String COLUMN_SERVER_DB = "server_db";
    public static final String COLUMN_SERVER_USERNAME = "server_username";
    public static final String COLUMN_SERVER_PASSWORD = "server_password";
    public static final String COLUMN_SERVER_MQTT = "server_mqtt";
    public static final String COLUMN_PORT_MQTT = "port_mqtt";
    public static final String COLUMN_IP_API_MQTT = "ip_api_mqtt";

    private String url;
    private String db;
    private String username;
    private String password;
    private String serverMqtt;
    private String portMqtt;
    private String ipApiMqtt;

    public ServerConfig(String url, String db, String username, String password, String serverMqtt, String portMqtt, String ipApiMqtt) {
        this.url = url;
        this.db = db;
        this.username = username;
        this.password = password;
        this.serverMqtt = serverMqtt;
        this.portMqtt = portMqtt;
        this.ipApiMqtt = ipApiMqtt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerMqtt() {
        return serverMqtt;
    }

    public void setServerMqtt(String serverMqtt) {
        this.serverMqtt = serverMqtt;
    }

    public String getPortMqtt() {
        return portMqtt;
    }

    public void setPortMqtt(String portMqtt) {
        this.portMqtt = portMqtt;
    }

    public String getIpApiMqtt() {
        return ipApiMqtt;
    }

    public void setIpApiMqtt(String ipApiMqtt) {
        this.ipApiMqtt = ipApiMqtt;
    }
}
