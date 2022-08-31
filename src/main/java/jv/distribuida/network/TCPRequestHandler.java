package jv.distribuida.network;

import java.io.IOException;

public class TCPRequestHandler implements Runnable {

    private final TCPConnection connection;
    private final RequestHandler handler;

    public TCPRequestHandler(TCPConnection connection, RequestHandler handler) {
        this.connection = connection;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Message message = connection.receive();
            message = this.handler.handle(message);
            connection.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
