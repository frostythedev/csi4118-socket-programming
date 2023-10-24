
// A Java program for a Server
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Server {
	// initialize socket and input stream
	private Socket socket = null;
	private ServerSocket server = null;

	private DataInputStream in = null;

	private OutputStream oStream;
	private PrintWriter printWriter;

	private List<String> logs;

	// constructor with port
	public Server(int port) {

		logs = new ArrayList<>();
		// starts server and waits for a connection
		try {
			server = new ServerSocket(port);
			localPrint("Server started");

			localPrint("Waiting for a client ...");

			socket = server.accept();
			localPrint("Client@" + socket.getInetAddress().getHostAddress() + " accepted");

			// takes input from the client socket
			in = new DataInputStream(
					new BufferedInputStream(socket.getInputStream()));

			String line = "";

			while (true) {
				line = in.readUTF();
				localPrint("Received: " + line);
				respondAsServer("OK BOMBOOCLATT: " + line);

				if (line.contains("break")) {
					// close connection
					localPrint("Connection closed");
					socket.close();
					in.close();
					break;
				}
			}
		} catch (IOException i) {
			System.out.println(i);
		}

		// reads message from client until "Over" is sent
		// while (!line.equals("Over")) {
		// try {
		// line = in.readUTF();
		// System.out.println(line);

		// } catch (IOException i) {
		// System.out.println(i);
		// }
		// }
		// System.out.println("Closing connection");

		// } catch (IOException i) {
		// System.out.println(i);
		// }
	}

	private void addLog(String message) {

	}

	private void respondAsServer(String serverMsg){
		if(socket != null && (oStream == null || printWriter == null)){
			try {
				oStream = socket.getOutputStream();
				printWriter = new PrintWriter(oStream, true);
			} catch (IOException e) {
		
				e.printStackTrace();
			}
		}

		printWriter.println(serverMsg);
	}

	public void localPrint(String message) {
		System.out.println(message);
	}

	public static void main(String args[]) {
		Server server = new Server(5000);
	}
}
