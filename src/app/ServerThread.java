package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket controlSocket;
	private ServerMain serverMain;
	private ServerSocket serverDataSocket;
	
	public ServerThread(Socket controlSocket, ServerMain serverMain, ServerSocket serverDataSocket) {
		this.controlSocket = controlSocket;
		this.serverMain = serverMain;
		this.serverDataSocket = serverDataSocket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader inSocketControl = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
			PrintWriter outSocketControl = new PrintWriter(new PrintWriter(controlSocket.getOutputStream()), true);
			
//			outSocketControl.println("332NeedAccountForLogin");
//			String prazan = inSocketControl.readLine();
//			System.out.println(prazan);
			
			outSocketControl.println("332NeedAccountForLogin");
			String username = "";
			String password;
			
			while(true) {
				String comm = inSocketControl.readLine();
				if(comm == null) {
					break;
				}
				if(comm.startsWith("USER")) {
					username = comm.substring(5);
					outSocketControl.println("331 Username OK");
				} else if(comm.startsWith("PASS")) {
					password = comm.substring(5);
					if(password.equals(serverMain.getUsers().get(username))) {
						outSocketControl.println("231 User Logged In");
					} else {
						outSocketControl.println("530 Invalid Username Or Password");
					}
				} else if(comm.startsWith("SYST")) {
					outSocketControl.println("Bakijev sistem");
				} else if(comm.startsWith("FEAT")) {
					outSocketControl.println("LIST\nRETR\nSTOR\nDELE\nSIZE\nQUIT\nPASV\nTYPE\nSYST\nFEAT\nPWD\nCWD");
				} else if(comm.startsWith("PWD")) {
					outSocketControl.println("257 \"/ServerFiles\" ");
				} else if(comm.startsWith("TYPE")) {
					outSocketControl.println("200 Command OK");
				} else if(comm.startsWith("PASV")) {
					outSocketControl.println("227 127,0,0,1,0,20");
				} else if(comm.startsWith("QUIT")) {
					outSocketControl.println("221 Closing connection");
					break;
				} else if(comm.startsWith("LIST")) {
					Socket dataSocket = serverDataSocket.accept();
					PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
					StringBuilder sb = new StringBuilder();
					for(File f : serverMain.getFiles()) {
						sb.append(f.getName() + "\n");
					}
					outSocketData.println(sb.toString());
					outSocketControl.println("200 Completed");
					dataSocket.close();
				} else if(comm.startsWith("RETR")) {
					Socket dataSocket = serverDataSocket.accept();
					PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
					String fileName = comm.substring(5);
					for(File f : serverMain.getFiles()) {
						if(f.getName().equals(fileName)) {
							FileReader fileReader = new FileReader("./ServerFiles/" + fileName);
							BufferedReader bufferedReader = new BufferedReader(fileReader);
							StringBuilder read = new StringBuilder();
							String line;
							while((line = bufferedReader.readLine()) != null) {
								read.append(line + "\n");
								outSocketData.println(line);
							}
//							outSocketData.println(lineNumber);
//							outSocketData.println(read.toString());
//							System.out.println(read.toString());
							bufferedReader.close();
							outSocketControl.println("250Completed");
							break;
						}
					}
					dataSocket.close();
				} else if(comm.startsWith("STOR")) {
					Socket dataSocket = serverDataSocket.accept();
					BufferedReader inSocketData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
					String fileName = comm.substring(5);
//					int lines = Integer.parseInt(inSocketData.readLine());
					File f = new File("./ServerFiles/" + fileName);
					f.createNewFile();
					PrintWriter writer = new PrintWriter(f);
//					for(int i = 0; i < lines; i++) {
//						writer.println(inSocketData.readLine());
//					}
					String line;
					while((line = inSocketData.readLine()) != null) {
						writer.println(line);
					}
					writer.close();
					serverMain.getFiles().add(f);
					outSocketControl.println("250Completed");
					dataSocket.close();
				} else if(comm.startsWith("DELE")) {
					String fileName = comm.substring(5);
					for(File f : serverMain.getFiles()) {
						System.out.println(f.getName());
						if(f.getName().equals(fileName)) {
							if(!f.delete()) {
								outSocketControl.println("451LocalError");
								System.out.println("Ju");
								break;
							}
							serverMain.getFiles().remove(f);
							System.out.println("Je");
							outSocketControl.println("250Completed");
							break;
						}
					}
				} else if(comm.startsWith("SIZE")) {
					Socket dataSocket = serverDataSocket.accept();
					PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
					String fileName = comm.substring(5);
					for(File f : serverMain.getFiles()) {
						if(f.getName().equals(fileName)) {
							outSocketData.println(f.length());
							outSocketControl.println("250Completed");
							break;
						}
					}
					dataSocket.close();
				} else if(comm.startsWith("CWD")) {
					outSocketControl.println("250 Completed");
				}
			}
			
			controlSocket.close();
			
//			String username = inSocketControl.readLine();
//			if(!username.startsWith("USER")) {
//				int i = 10;
//				while(!username.startsWith("USER") && i > 0) {
//					outSocketControl.println("332NeedAccountForLogin");
//					username = inSocketControl.readLine();
//					i--;
//				}
//				outSocketControl.println("421ServiceNotAvailable");
//				controlSocket.close();
//				return;
//			}
//			
//			username = username.substring(5);
//			outSocketControl.println("331UsernameOK");
//			
//			String password = inSocketControl.readLine();
//			if(!password.startsWith("PASS")) {
//				int i = 10;
//				while(!password.startsWith("USER") && i > 0) {
//					outSocketControl.println("332NeedAccountForLogin");
//					password = inSocketControl.readLine();
//					i--;
//				}
//				outSocketControl.println("421ServiceNotAvailable");
//				controlSocket.close();
//				return;
//			}
//			
//			password = password.substring(5);
//			
//			if(!password.equals(serverMain.getUsers().get(username))) {
//				outSocketControl.println("430InvalidUsernameOrPassword");
//				controlSocket.close();
//				return;
//			}
			
//			outSocketControl.println("230UserLoggedIn");
//			
//			String neki = inSocketControl.readLine();
////			System.out.println(neki);
//			
//			outSocketControl.println("Bakijev sistem");
//			
//			String feat = inSocketControl.readLine();
////			System.out.println(feat);
//			
//			outSocketControl.println("LIST\nRETR\nSTOR\nDELE\nSIZE");
//			
//			String pwd = inSocketControl.readLine();
////			System.out.println(pwd);
//			
//			outSocketControl.println("257 \"/ServerFiles\" ");
//			
//			String type = inSocketControl.readLine();
////			System.out.println(type);
//			
//			outSocketControl.println("200 Command OK");
//			
//			String pasv = inSocketControl.readLine();
////			System.out.println(pasv);
//			
//			outSocketControl.println("227 127,0,0,1,0,20");
//			
//			@SuppressWarnings("resource")
//			ServerSocket serverSocket = new ServerSocket(20);
//
//			String command = inSocketControl.readLine();
//
////			Socket dataSocket = serverSocket.accept();
////			BufferedReader inSocketData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
////			PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
//			
//			while(!command.equals("QUIT")) {
//				System.out.println("e");
//				String start = command.substring(0, 4);
//				
//				Socket dataSocket = serverSocket.accept();
//				BufferedReader inSocketData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
//				PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
//				
//				String fileName = "";
//				boolean flag = true;
//				
//				switch (start) {
//				case "LIST":
//					StringBuilder sb = new StringBuilder();
//					for(File f : serverMain.getFiles()) {
//						sb.append(f.getName() + "\n");
//					}
////					outSocketData.println(serverMain.getFiles().size());
//					outSocketData.println(sb.toString());
//					outSocketControl.println("200 Completed");
//					System.out.println("List");
//					break;
//				
//				case "RETR":
//					fileName = command.substring(5);
//					for(File f : serverMain.getFiles()) {
//						if(f.getName().equals(fileName)) {
//							FileReader fileReader = new FileReader("./ServerFiles/" + fileName);
//							BufferedReader bufferedReader = new BufferedReader(fileReader);
//							StringBuilder read = new StringBuilder();
//							String line;
//							int lineNumber = 0;
//							while((line = bufferedReader.readLine()) != null) {
//								read.append(line + "\n");
//								lineNumber++;
//							}
//							outSocketData.println(lineNumber);
//							outSocketData.println(read.toString());
//							bufferedReader.close();
//							outSocketControl.println("250Completed");
//							flag = false;
//							break;
//						}
//					}
//					if(flag) {
//						outSocketControl.println("550FileUnavailable");
//					}
//					break;
//					
//				case "STOR":
//					fileName = command.substring(5);
//					for(File f : serverMain.getFiles()) {
//						if(f.getName().equals(fileName)) {
//							outSocketControl.println("553FileNameNotAllowed");
//							flag = false;
//							break;
//						}
//					}
//					if(flag) {
//						int lines = Integer.parseInt(inSocketData.readLine());
//						File f = new File("./ServerFiles/" + fileName);
//						f.createNewFile();
//						PrintWriter writer = new PrintWriter(f);
//						for(int i = 0; i < lines; i++) {
//							writer.println(inSocketData.readLine());
//						}
//						writer.close();
//						serverMain.getFiles().add(f);
//						outSocketControl.println("250Completed");
//					}
//					break;
//					
//				case "DELE":
//					fileName = command.substring(5);
//					for(File f : serverMain.getFiles()) {
//						if(f.getName().equals(fileName)) {
//							flag = false;
//							if(!f.delete()) {
//								outSocketControl.println("451LocalError");
//								break;
//							}
//							serverMain.getFiles().remove(f);
//							outSocketControl.println("250Completed");
//						}
//					}
//					if(flag) {
//						outSocketControl.println("550FileUnavailable");
//					}
//					break;
//					
//				case "SIZE":
//					fileName = command.substring(5);
//					for(File f : serverMain.getFiles()) {
//						if(f.getName().equals(fileName)) {
//							outSocketData.println(f.length());
//							outSocketControl.println("250Completed");
//							flag = false;
//							break;
//						}
//					}
//					if(flag) {
//						outSocketControl.println("550FileUnavailable");
//					}
//					break;
//				}
//				dataSocket.close();
//				System.out.println("kraj1");
//				command = inSocketControl.readLine();
//				System.out.println(command);
//			}
//			
//			outSocketControl.println("221ClosingControlConnection");
//			controlSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
