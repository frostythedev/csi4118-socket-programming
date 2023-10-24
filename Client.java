
// A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	// initialize socket and input output streams
	private Socket socket = null;
	private Scanner input = null;

	private DataInputStream serverInputStream;
	private BufferedReader servReader;

	private DataOutputStream out = null;

	// constructor to put ip address and port
	public Client(String address, int port) {
		// establish a connection
		try {
			socket = new Socket(address, port);

			if (!socket.isConnected()) {
				print("Could not establish connection with server, please try again later.");
				System.exit(0);
			}

			// Gets input / serverResponses from the socket
			serverInputStream = new DataInputStream(socket.getInputStream());
			servReader = new BufferedReader(new InputStreamReader(serverInputStream));

			System.out.println("Connected");

			// takes input from terminal
			input = new Scanner(System.in);

			// sends output to the socket
			out = new DataOutputStream(socket.getOutputStream());

		} catch (UnknownHostException u) {
			System.out.println(u);
			return;
		} catch (IOException i) {
			System.out.println(i);
			return;
		}

		// Thread responseThread = new Thread(() -> {
		// 	try {
		// 		String serverResponse = servReader.readLine();

		// 		// if(serverInputStream.available() > 0){
		// 		// 	serverResponse = servReader.readLine();
		// 		// }
		// 		if (serverResponse != null) {
		// 			System.out.println("Response from server: " + serverResponse);
		// 		}
		// 	} catch (IOException e) {
		// 		e.printStackTrace();
		// 	}
		// });
		// responseThread.start();

		// string to read message from input
		String line = "";
		String[] params;

		while (true) {

			int studentId = -1;
			int commandId;

			line = (input.hasNext() ? input.nextLine() : "");

			if (line.startsWith("csi4118")) {
				params = line.split(" ");

				if (params.length < 2) {
					print("Invalid command paramaters. Usage: cs4118 <commandType> <studentNumber> [parameters]");

				} else {

					try {
						switch (params[1]) {
							case "type0":

								if (params.length <= 2) {
									print("Invalid command paramaters. Usage: cs4118 type0 <studentNumber> [cypherOffset]");
								} else {

									int cipherOffset = -1;
									if (params[2] == null || params[2] == "") {
										print("Invalid syntax, please enter a studentId followed by integer between 1-25");
									} else {
										try {
											studentId = Integer.parseInt(params[2]);

										} catch (NumberFormatException e) {
											print("Invalid student id entered. Must be an integer");
										}

										if (params.length <= 3 || params[3] == null || params[3] == "") {
											cipherOffset = 3;
										} else {
											try {
												cipherOffset = Integer.parseInt(params[3]);
											} catch (NumberFormatException e) {
												print("Invalid cipherOffset entered. Must be an integer");
											}
										}

									}

									commandId = 0;
									out.writeUTF(commandId + "," + studentId + "," + cipherOffset);
									print(("Test: " + servReader.readLine()));
									
									// Thread responseThread = new Thread(() -> {
									// 	try {
									// 		String serverResponse = servReader.readLine();
									// 		if (serverResponse != null) {
									// 			System.out.println("Response from server: " + serverResponse);
									// 		}
									// 		return;
									// 	} catch (IOException e) {
									// 		e.printStackTrace();
									// 	}
									// });
									// responseThread.start();
									// try {
									// 	responseThread.join();
									// } catch (InterruptedException e) {
									// 	// TODO Auto-generated catch block
									// 	e.printStackTrace();
									// }
									
								}

								break;
							case "type1":

								break;
							case "type2":

								break;
							case "type3":

								break;
							case "type4":
								out.writeUTF("type4 Bye");

								input.close();
								out.close();
								socket.close();
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}

			} else {
				print("Invalid command entered. Usage: cs4118 <commandType> <studentNumber> [paramaters]");
			}
		}

		// keep reading until "Over" is input
		// while (!line.equals("Over")) {
		// try {
		// line = input.nextLine();
		// out.writeUTF(line);
		// } catch (IOException i) {
		// System.out.println(i);
		// }
		// }

		// close the connection
	}

	public void print(String message) {
		System.out.println(message);
	}

	public static void main(String args[]) {
		Client client = new Client("127.0.0.1", 5000);
	}
}
