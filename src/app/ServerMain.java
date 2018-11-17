package app;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerMain {

	private HashMap<String, String> users;
	private ArrayList<File> files;
	
	public ServerMain() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(21);
		ServerSocket serverDataSocket = new ServerSocket(20);
		
		files = new ArrayList<>();
		users = new HashMap<>();
		File dir = new File("./ServerFiles");
		File[] matchingFiles = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("txt");
			}
		});
		
		for(int i = 0; i < matchingFiles.length; i++) {
			files.add(matchingFiles[i]);
		}
		
		users.put("baki", "bakibaki");
		users.put("maki", "makimaki");
		
		while(true) {
			Socket socket = serverSocket.accept();
			ServerThread serverThread = new ServerThread(socket, this, serverDataSocket);
			Thread thread = new Thread(serverThread);
			thread.start();
		}
	}
	
	public static void main(String[] args) {
		try {
			new ServerMain();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, String> users) {
		this.users = users;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}
	
}
