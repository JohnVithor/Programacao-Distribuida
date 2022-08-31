package jv.distribuida.network;

import java.io.IOException;

public class UDPRequestHandler implements Runnable {

    private final UDPConnection connection;
    private final Message message;
    private final RequestHandler handler;
    public UDPRequestHandler(UDPConnection connection, Message message, RequestHandler handler) {
        this.connection = connection;
        this.message = message;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Message response = this.handler.handle(message);
            connection.send(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
//            connection.close(); // DON'T CLOSE!
        }
    }
}
