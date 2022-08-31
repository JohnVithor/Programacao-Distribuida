package jv.distribuida.network;

import java.io.*;
import java.net.*;

public class TCPConnection {

	private final Socket socket;
	private final BufferedReader input;
	private final PrintWriter output;

	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream());
	}

	public void send(String message) throws IOException {
		output.write(message+"\r\n");
		output.flush();
	}

	public String receive() throws IOException {
		return input.readLine();
	}
	public void close() throws IOException {
		socket.close();
	}
}
