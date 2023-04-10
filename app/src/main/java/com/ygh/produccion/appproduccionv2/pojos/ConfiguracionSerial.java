package com.ygh.produccion.appproduccionv2.pojos;

public class ConfiguracionSerial {
    public static String TABLE_CONFIG_SERIAL = "configSerial";
    public static String COLUMN_CONFIG_SERIAL_BAUDRATE = "baudrate";
    public static String COLUMN_CONFIG_SERIAL_PARITY = "parity";
    public static String COLUMN_CONFIG_SERIAL_STOPBIT = "stopbit";
    public static String COLUMN_CONFIG_SERIAL_BYTESIZE = "bytesize";
    public static String COLUMN_CONFIG_SERIAL_TARA = "tara";
    public static String COLUMN_BT_DEVICE = "btDevice";

    private int baudrate;
    private int parity;
    private int stopbit;
    private int bytesize;
    private String tara;
    private String btDevice;

    /**
     *
     * @param baudrate
     * @param parity
     * @param stopbit
     * @param bytesize
     */
    public ConfiguracionSerial(int baudrate, int parity, int stopbit, int bytesize, String tara, String btDevice) {
        this.baudrate = baudrate;
        this.parity = parity;
        this.stopbit = stopbit;
        this.bytesize = bytesize;
        this.tara = tara;
        this.btDevice = btDevice;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public int getParity() {
        return parity;
    }

    public int getStopbit() {
        return stopbit;
    }

    public int getBytesize() {
        return bytesize;
    }

    public String getTara() {
        return tara;
    }

    public String getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(String btDevice) {
        this.btDevice = btDevice;
    }
}
