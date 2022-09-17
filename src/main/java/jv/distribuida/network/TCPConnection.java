package jv.distribuida.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection implements Connection {
    protected final Socket socket;
    protected final BufferedReader input;
    protected final PrintWriter output;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream());
    }

    public void send(Message message) throws IOException {
        output.write(message.getText() + "\r\n\r\n");
        output.flush();
    }

    public Message receive() throws IOException {
        return new Message(socket.getInetAddress(), socket.getPort(), input.readLine());
    }

    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void setTimeout(int milli) throws SocketException {
        socket.setSoTimeout(milli);
    }
}
