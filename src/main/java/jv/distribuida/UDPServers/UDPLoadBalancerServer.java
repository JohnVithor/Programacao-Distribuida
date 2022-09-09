package jv.distribuida.UDPServers;

import jv.distribuida.handlers.LoadBalancerHandlerManager;
import jv.distribuida.network.*;

import java.io.IOException;

public class UDPLoadBalancerServer {
    public static void main(String[] args) throws IOException {
        RequestHandler handler = new LoadBalancerHandlerManager(ConnectionType.UDP);
        UDPConnection connection = new UDPConnection(9005);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}