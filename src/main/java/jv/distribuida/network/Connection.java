package jv.distribuida.network;

import java.io.IOException;
import java.net.SocketException;

public interface Connection {
    void send(Message message) throws IOException;

    Message receive() throws IOException;

    void close() throws IOException;

    void setTimeout(int milli) throws SocketException;
}
