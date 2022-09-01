package jv.distribuida.network;

import java.io.IOException;

public interface RequestHandler {
    Message handle(Message message) throws IOException;
}
