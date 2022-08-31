package jv.distribuida.service;

import jv.distribuida.network.Message;
import jv.distribuida.network.UDPConnection;

import java.io.IOException;
import java.lang.reflect.Member;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        UDPConnection connection = new UDPConnection(8080);
        while(true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message));
        }
    }
}
