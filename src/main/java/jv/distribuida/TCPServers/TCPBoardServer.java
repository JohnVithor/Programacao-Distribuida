package jv.distribuida.TCPServers;

import jv.distribuida.client.DatabaseClient;
import jv.distribuida.handlers.BoardHandlerManager;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.TCPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPBoardServer {
    public static void main(String[] args) throws IOException {
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, ConnectionType.TCP);
        RequestHandler handler = new BoardHandlerManager(databaseClient);
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                Thread.ofVirtual().start(new TCPRequestHandler(new TCPConnection(serverSocket.accept()), handler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}