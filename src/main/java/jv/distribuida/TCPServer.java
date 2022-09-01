package jv.distribuida;

import jv.distribuida.database.DatabaseClient;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.TCPRequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.service.AbstractHandler;
import jv.distribuida.service.BoardHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        TCPConnection dbconnection = new TCPConnection(new Socket("localhost", 9000));
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new BoardHandler(databaseClient);
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while(true) {
                Thread.ofVirtual().start(new TCPRequestHandler(new TCPConnection(serverSocket.accept()), handler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
