
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.io.*;

public class myServer {

	private Socket socket = null;
	private ServerSocket server = null;

	private DataInputStream in = null;

	private OutputStream oStream;
	private PrintWriter printWriter;

	private List<String> logs;

	// Alphabet for easy indexing
	private final List<Character> ALPHABET = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

	// constructor with port
	public myServer(int port, boolean refuseLocal) {
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

				// Checking for local conections and allowing or disallowing the connection if that is the case
				if (refuseLocal ? !clientAddress.equals("127.0.0.1") : true) {

					localPrint("Client@" + clientAddress + " has been accepted.");
					respondAsServer("Accepted! Connection established!");

					// takes input from the client socket
					in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

					// We parse the commands sent from the client in a condensed format so that we can easily and quickly decipher what is to be done and how to interpret the data
					String line = "";
					String[] params;
					int cipherOffset = 3;
					int studentId;
					String storedMessage = "";

					// Ensures that the server is always listening for messages from the client
					while (true) {
						line = in.readUTF();

						localPrint("Received: " + line);
						params = line.split(",");

						// Getting the studentID of the request
						studentId = Integer.parseInt(params[1]);

						// Depending on the message sent by the client, the server decides what process to execute, makes a log if it and assigns the appropriate parameters to the command. Also makes any necessary changes to the localServer's code 
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

					// DONE: Save all storedMessages to logfiles in the server's folder using the currnet timestamp of the disconnected client
					File logFile = new File(new Date().getTime() + ".txt");
					
					if (!logFile.exists()) {
						try {
							logFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					// Iterates through the logFiles in the list and writes it to our logFile, once done it is purged from the array
					try {
						FileWriter logWriter = new FileWriter(logFile, (logFile.exists()));

						for (Iterator<String> it = logs.iterator(); it.hasNext();) {
							String s = it.next();
							logWriter.write(s + "\n");
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

	// Method for decoding the messages for storage in the logFiles, by using thecurrent cipherOffset to get the actual message that is being sent/stored
	private String decodeMessage(String message, int cipherOffset) {
		String decoded = "";
		for (int i = 0; i < message.length(); i++) {

			int index = ALPHABET.indexOf(message.charAt(i));
			int newIndex = index + cipherOffset;
			if (newIndex > 25) {
				newIndex -= 25;
				//localPrint("Alpha is greater than 26, spill over, input: %s | output: %s".formatted(message.charAt(i), "N/A"));
			}else{
				//localPrint("Alpha is not greater than 26 input: %s".formatted(message.charAt(i)));
			}
			decoded = decoded + ALPHABET.get(newIndex);
		}

		return decoded;
	}

	private void addLog(String message) {
		logs.add(message);
	}

	// Simple utility function to respondTo the client as the Server using the preconfigured streams
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

	// Ensuring proper arguments are provided when the java file is run in the  compiler ensuring that it has everything needed to properly function
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
