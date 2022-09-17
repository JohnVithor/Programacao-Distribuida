package jv.distribuida.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class HTTPConnection extends TCPConnection implements Connection {
    public HTTPConnection(Socket socket) throws IOException {
        super(socket);
    }

    public void send(Message message) throws IOException {
        output.write("HTTP/1.0 200 OK\r\n"
                + "Server: LoadBalancer\r\n"
                + "Connection: close\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Lenght: "+message.getText().length()+ "\r\n"
                + "\r\n"
                + message.getText() + "\r\n\r\n");
        output.flush();
    }

    public Message receive() throws IOException {
        String headerLine = input.readLine();
        StringTokenizer tokenizer = new StringTokenizer(headerLine);
        String httpMethod = tokenizer.nextToken();
        String httpQueryString = tokenizer.nextToken();
        String httpVersion = tokenizer.nextToken();
        // headers
        String inputLine;
        HashMap<String, String> headers = new HashMap<>();
        while (!(inputLine = input.readLine()).equals("")) {
            String[] splits = inputLine.split(":");
            if (splits.length != 2) {
                // sendo error
            }
            headers.put(splits[0], splits[1]);
        }
        StringBuilder body = new StringBuilder();
        while (!(inputLine = input.readLine()).equals("")) {
            body.append(inputLine);
        }

        return new HTTPMessage(socket.getInetAddress(), socket.getPort(), body.toString(),
                httpMethod, httpQueryString, httpVersion,
                headers, body.toString());
    }
}
