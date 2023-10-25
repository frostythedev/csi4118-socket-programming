
// A Java program for a Server
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
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
	private boolean refuseLocal;

	private final List<Character> ALPHABET = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

	// constructor with port
	public myServer(int port, boolean refuseLocal) {

		this.refuseLocal = refuseLocal;
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

				if (refuseLocal ? !clientAddress.equals("127.0.0.1") : true) {

					localPrint("Client@" + clientAddress + " has been accepted.");
					respondAsServer("Accepted! Connection established!");

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
								addLog("OPP_CODE: %d | IP: %s | STUDENT_ID: %d".formatted(0, clientAddress, studentId));

								break;
							case "1":
								respondAsServer("You are connected to the server as: " + clientAddress);
								addLog("OPP_CODE: %d | IP: %s | STUDENT_ID: %d".formatted(1, clientAddress, studentId));

								break;
							case "2":
								respondAsServer("1 2 3 4 5 6 7 8 9 10");
								addLog("OPP_CODE: %d | IP: %s | STUDENT_ID: %d".formatted(2, clientAddress, studentId));

								break;
							case "3":
								String encodedMessage = params[2];
								if (storedMessage == "") {
									storedMessage = decodeMessage(encodedMessage, cipherOffset);
								} else {
									storedMessage += " " + decodeMessage(encodedMessage, cipherOffset);
								}

								respondAsServer("GOT IT");

								addLog("OPP_CODE: %d | IP: %s | STUDENT_ID: %d".formatted(3, clientAddress, studentId));
								break;
							case "4":
								respondAsServer("BYE");
								addLog("OPP_CODE: %d | IP: %s | STUDENT_ID: %d".formatted(4, clientAddress, studentId));
								break;
							default:

								break;
						}
					}
				}else{
					
					respondAsServer("Refused! Cannot connect to server for the same computer.");
					socket.close();
				}

			} catch (IOException i) {
				// Most likely the client disconnected from the server
				if (i instanceof EOFException) {

					// TODO: Save all storedMessages to logfiles for archive

					File logFile = new File("logs/" + new Date().getTime() + ".txt");
					if (!logFile.exists()) {
						try {
							logFile.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						FileWriter logWriter = new FileWriter(logFile, (logFile.exists()));

						for (Iterator<String> it = logs.iterator(); it.hasNext();) {
							String s = it.next();
							logWriter.write(s);
							it.remove();

						}

						logWriter.close();
						localPrint("Successfully saved logs.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("A client has disconnected ending the stream.");
				}
			}
		}
	}

	private String decodeMessage(String message, int cipherOffset) {
		String decoded = "";
		for (int i = 0; i < message.length(); i++) {

			int index = ALPHABET.indexOf(message.charAt(i)) + 1;
			int newIndex = index + cipherOffset;
			if (newIndex > 26) {
				newIndex -= 26;
			}
			decoded = decoded + ALPHABET.get(newIndex);
		}

		return decoded;
	}

	private void addLog(String message) {
		logs.add(message);
	}

	private void respondAsServer(String serverMsg) {
		if (socket != null) {
			try {

				if (oStream == null) {
					oStream = socket.getOutputStream();
				}
				if (printWriter == null) {
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

		if (args.length < 2) {
			localPrint("Invalid arguments starting myServer. Usage myServer <port> <refuseLocal>");
			System.exit(0);
		}

		try {
			new myServer(Integer.parseInt(args[0]), (args[1] != null ? Boolean.parseBoolean(args[1]) : false));
		} catch (NumberFormatException e) {
			localPrint(" Invalid port number entered. Please try again");
		}

	}
}
