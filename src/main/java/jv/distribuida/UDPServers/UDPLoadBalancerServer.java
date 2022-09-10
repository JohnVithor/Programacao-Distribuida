package jv.distribuida.UDPServers;

import jv.distribuida.handlers.LoadBalancerHandlerManager;
import jv.distribuida.network.*;

import java.io.IOException;

public class UDPLoadBalancerServer {
    public static void main(String[] args) {
        LoadBalancerHandlerManager handler = new LoadBalancerHandlerManager(ConnectionType.UDP);
        try {
            UDPConnection connection = new UDPConnection(Integer.parseInt(args[0]));
            System.err.println("Iniciando Load Balancer na porta " + args[0]);
            while (true) {
                Message message = connection.receive();
                Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            handler.disableHeartbeat();
        }
        System.err.println("Finalizando execução");
    }
}