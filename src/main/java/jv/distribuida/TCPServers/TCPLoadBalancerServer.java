package jv.distribuida.TCPServers;

import jv.distribuida.handlers.LoadBalancerHandlerManager;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.Message;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

public class TCPLoadBalancerServer {
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