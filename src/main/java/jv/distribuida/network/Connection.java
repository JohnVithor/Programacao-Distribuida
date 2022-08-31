package jv.distribuida.network;

import java.io.IOException;

public interface Connection {

    void send(Message message) throws IOException;
    Message receive() throws IOException;
    void close() throws IOException;
}
