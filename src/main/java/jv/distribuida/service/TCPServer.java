package jv.distribuida.service;

import jv.distribuida.network.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while(true) {
                Thread.ofVirtual().start(new TCPRequestHandler(new TCPConnection(serverSocket.accept())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
