package jv.distribuida.network;

import java.io.Serializable;
import java.net.InetAddress;

public class Message implements Serializable {
    private InetAddress address;
    private int port;
    private String text;

    public Message(InetAddress address, int port, String text) {
        this.address = address;
        this.port = port;
        this.text = text;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
