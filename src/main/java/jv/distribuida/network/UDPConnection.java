package jv.distribuida.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPConnection implements Connection {
    private final DatagramSocket socket;

    public UDPConnection(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void send(Message message) throws IOException {
        byte[] replymsg = message.getText().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(replymsg, replymsg.length, message.getAddress(), message.getPort());
        socket.send(sendPacket);
    }

    public Message receive() throws IOException {
        byte[] receivemessage = new byte[1024];
        DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
        socket.receive(receivepacket);
        String text = new String(receivepacket.getData(), 0, receivepacket.getLength());
        return new Message(receivepacket.getAddress(), receivepacket.getPort(), text);
    }

    @Override
    public void close() {
        socket.close();
    }
}