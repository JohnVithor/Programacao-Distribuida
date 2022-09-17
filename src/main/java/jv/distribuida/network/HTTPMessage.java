package jv.distribuida.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;

public class HTTPMessage extends Message implements Serializable {

    private final String method;
    private final String query;
    private final String version;
    private final HashMap<String, String> headers;
    private final String body;

    public HTTPMessage(InetAddress address, int port, String text,
                       String method, String query, String version,
                       HashMap<String, String> headers, String body) {
        super(address, port, text);
        this.method = method;
        this.query = query;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
