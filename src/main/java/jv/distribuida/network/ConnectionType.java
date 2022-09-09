package jv.distribuida.network;

public enum ConnectionType {
    UDP("UDP"),
    TCP("TCP"),
    HTTP("HTTP");
    private final String value;
    ConnectionType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
