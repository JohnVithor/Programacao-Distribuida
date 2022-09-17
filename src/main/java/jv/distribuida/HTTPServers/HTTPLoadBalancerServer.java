package jv.distribuida.HTTPServers;

import jv.distribuida.handlers.LoadBalancerHandlerManager;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.HTTPConnection;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.TCPRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class HTTPLoadBalancerServer {
    public static void main(String[] args) {
        try{
            LoadBalancerHandlerManager handler = new LoadBalancerHandlerManager(ConnectionType.HTTP);
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
                System.err.println("Iniciando Load Balancer na porta " + args[0]);
                while (true) {
                    Thread.ofVirtual().start(new TCPRequestHandler(new HTTPConnection(serverSocket.accept()), handler));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Finalizando execução");
    }
}