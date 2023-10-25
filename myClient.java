
// A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class myClient {
	// initialize socket and input output streams
	private Socket socket = null;
	private Scanner input = null;

	private DataInputStream serverInputStream;
	private BufferedReader servReader;

	private DataOutputStream out = null;

	// constructor to put ip address and port
	public myClient(String address, int port) {
		// establish a connection
		try {
			socket = new Socket(address, port);

			// Gets input / serverResponses from the socket
			serverInputStream = new DataInputStream(socket.getInputStream());
			servReader = new BufferedReader(new InputStreamReader(serverInputStream));

			String serverConnectResponse = servReader.readLine();
			if(serverConnectResponse.startsWith("Refused")){
				print("Connection has been refused for: " + serverConnectResponse);
				return;
			}else{
				print("Connection established!");
			}

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

		// string to read message from input
		String line = "";
		String[] params;

		while (true) {

			int studentId = -1;
			int commandId;

			line = (input.hasNext() ? input.nextLine() : "");

			

			if (line.startsWith("csi4118")) {

				params = line.split(" ");

				if (params.length < 3) {
					print("Invalid command paramaters. Usage: cs4118 <commandType> <studentNumber> [parameters]");

				} else {

					// if (params[2] == null || params[2] == "") {
					// print("Invalid syntax, please enter a studentId followed by integer between
					// 1-25");
					// } else {

					// }
					try {
						studentId = Integer.parseInt(params[2]);

						if(studentId > 999999 && studentId < 10000000 ){ 
							try {
								switch (params[1]) {
									case "type0":
	
										if (params.length <= 2) {
											print("Invalid command paramaters. Usage: cs4118 type0 <studentNumber> [cypherOffset]");
										} else {
	
											int cipherOffset = -1;
	
											if (params.length <= 3) {
												cipherOffset = 3;
	
												commandId = 0;
												out.writeUTF(commandId + "," + studentId + "," + cipherOffset);
												print(("SERVER> " + servReader.readLine()));
											} else {
												try {
													cipherOffset = Integer.parseInt(params[3]);
	
													if (cipherOffset < 0 || cipherOffset > 25) {
														print("The offset provided is invalid, please chose a number between 1-25");
													} else {
														commandId = 0;
														out.writeUTF(commandId + "," + studentId + "," + cipherOffset);
														print(("Test: " + servReader.readLine()));
													}
												} catch (NumberFormatException e) {
													print("Invalid cipherOffset entered. Must be an integer");
												}
											}
										}
	
										break;
									case "type1":
										commandId = 1;
										out.writeUTF(commandId + "," + studentId);
										print(("SERVER> " + servReader.readLine()));
										break;
									case "type2":
										commandId = 2;
										out.writeUTF(commandId + "," + studentId);
										print(("SERVER> " + servReader.readLine()));
										break;
									case "type3":	
	
										if (params.length > 3) {
											String encodedMessage = params[3].toLowerCase();
											commandId = 3;
											out.writeUTF(commandId + "," + studentId + "," + encodedMessage);
											print(("SERVER> " + servReader.readLine()));
										}else{
											print("Invalid parameters, no encoded message to send.");
										}
										break;
									case "type4":
										commandId = 4;
										out.writeUTF(commandId + "," + studentId);
	
										String response = servReader.readLine();
										print(("SERVER> " + response));
	
										if(response.equals("BYE")){
											input.close();
											out.close();
											socket.close();
											return;
										}
										break;
									default:
									print("Unknown command entered. Please follow [usage]");
								}
								
							} catch (IOException e) {
								e.printStackTrace();
								break;
							}
						}else{
							print("Invalid student id. Please ensure that it is 7 digits");
						}

					} catch (NumberFormatException e) {
						print("Invalid student id entered. Must be an integer");
					}

				}

			} else {
				print("Invalid command entered. Usage: cs4118 <commandType> <studentNumber> [paramaters]");
			}
		}

	}

	private static void print(String message) {
		System.out.println(message);
	}

	public static void main(String args[]) {

		if (args.length < 1) {
			print("Invalid arguments starting myServer. Usage myClient <ip> <port>");
			System.exit(0);
		}

		try {
			//new myServer(Integer.parseInt(args[0]));

			new myClient(args[0], Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			print(" Invalid port number entered. Please try again");
		}
	}
}
