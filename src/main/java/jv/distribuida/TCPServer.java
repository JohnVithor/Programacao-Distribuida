package jv.distribuida;

import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.TCPRequestHandler;
import jv.distribuida.service.AbstractHandler;
import jv.distribuida.service.BoardHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServer {
    public static void main(String[] args) {
        RequestHandler handler = new BoardHandler();
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while(true) {
                Thread.ofVirtual().start(new TCPRequestHandler(new TCPConnection(serverSocket.accept()), handler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
