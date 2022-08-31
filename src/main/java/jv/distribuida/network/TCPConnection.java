package jv.distribuida.network;

import java.io.*;
import java.net.*;

public class TCPConnection implements Connection {

	private final Socket socket;
	private final BufferedReader input;
	private final PrintWriter output;

	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream());
	}

	public void send(Message message) throws IOException {
		output.write(message.getText()+"\r\n");
		output.flush();
	}

	public Message receive() throws IOException {
		return new Message(socket.getInetAddress(), socket.getPort(), input.readLine());
	}
	public void close() throws IOException {
		socket.close();
	}
}
