
// A Java program for a Server
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

public class myServer {
	// initialize socket and input stream
	private Socket socket = null;
	private ServerSocket server = null;

	private DataInputStream in = null;

	private OutputStream oStream;
	private PrintWriter printWriter;

	private List<String> logs;

	private final List<Character> ALPHABET = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

	// constructor with port
	public myServer(int port) {

		logs = new ArrayList<>();
		// starts server and waits for a connection
		localPrint("Server started");
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {

			e.printStackTrace();
		}

		clientWait: while (true) {

			try {

				localPrint("Waiting for a client ...");

				socket = server.accept();
				String clientAddress = socket.getInetAddress().getHostAddress();

				localPrint("Client@" + clientAddress + " has been accepted.");

				// takes input from the client socket
				in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

				String line = "";
				String[] params;
				int cipherOffset = 3;
				int studentId;
				String storedMessage = "";

				while (true) {
					line = in.readUTF();

					localPrint("Received: " + line);
					params = line.split(",");

					studentId = Integer.parseInt(params[1]);

					switch (params[0]) {
						case "0":
							cipherOffset = Integer.parseInt(params[2]);
							respondAsServer("OK");

							break;
						case "1":
							respondAsServer("You are connected to the server as: " + clientAddress);

							break;
						case "2":
							respondAsServer("1 2 3 4 5 6 7 8 9 10");

							break;
						case "3":
							String encodedMessage = params[2];
							if (storedMessage == "") {
								storedMessage = decodeMessage(encodedMessage, cipherOffset);
							}else{
								storedMessage += " " + decodeMessage(encodedMessage, cipherOffset);
							}
							respondAsServer("GOT IT");
							break;
						case "4":
							respondAsServer("BYE");
							break;
						default:

							break;
					}

					// respondAsServer("OK BOMBOOCLATT: " + line);

					if (socket.isClosed()) {
						localPrint("Client has been disconnected");
					}
					// if (line.contains("break")) {
					// // close connection
					// localPrint("Connection closed");
					// socket.close();
					// in.close();
					// break;
					// }
				}
			} catch (IOException i) {
				// Most likely the client disconnected from the server
				if (i instanceof EOFException) {
					System.out.println("A client has disconnected ending the stream.");
				}
			}
		}
	}

	private String decodeMessage(String message, int cipherOffset) {
		String decoded = "";
		for (int i = 0; i < message.length(); i++) {

			int index = ALPHABET.indexOf(message.charAt(i));
			int newIndex = index + cipherOffset;
			if (newIndex > 26) {
				newIndex -= 26;
			}
			decoded = decoded + ALPHABET.get(newIndex);
		}

		return decoded;
	}

	private void addLog(String message) {

	}

	private void respondAsServer(String serverMsg) {
		if (socket != null) {
			try {

				if(oStream == null){
					oStream = socket.getOutputStream();
				}
				if(printWriter == null){
					printWriter = new PrintWriter(oStream, true);
				}
				
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		printWriter.println(serverMsg);
	}

	public static void localPrint(String message) {
		System.out.println(message);
	}

	public static void main(String args[]) {

		if (args.length < 1) {
			localPrint("Invalid arguments starting myServer. Usage myServer <port>");
			System.exit(0);
		}

		try {
			new myServer(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
			localPrint(" Invalid port number entered. Please try again");
		}

	}
}
