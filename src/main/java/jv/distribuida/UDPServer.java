package jv.distribuida;

import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;
import jv.distribuida.service.AbstractHandler;
import jv.distribuida.service.BoardHandler;

import java.io.IOException;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        RequestHandler handler = new BoardHandler();
        UDPConnection connection = new UDPConnection(8080);
        while(true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}
