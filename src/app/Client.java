package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public Client() throws UnknownHostException, IOException {
		Socket controlSocket = new Socket("localhost", 21);
		
		BufferedReader inSocketControl = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
		PrintWriter outSocketControl = new PrintWriter(new PrintWriter(controlSocket.getOutputStream()), true);
		
		String prva = inSocketControl.readLine();
		System.out.println(prva);
		
		outSocketControl.println("USER baki");
		
		String druga = inSocketControl.readLine();
		System.out.println(druga);
		
		outSocketControl.println("PASS bakibaki");
		
		String treca = inSocketControl.readLine();
		if(treca.startsWith("430")) {
			controlSocket.close();
			return;
		}
		
		System.out.println(treca);
		
		Scanner sc = new Scanner(System.in);
		
		String poruka = sc.nextLine();
		while(!poruka.equals("QUIT")) {
			Socket dataSocket = new Socket("localhost", 20);
			
			BufferedReader inSocketData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
			PrintWriter outSocketData = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
			
			outSocketControl.println(poruka);
			
			String start = poruka.substring(0, 4);
			
			String fileName = "";
//			boolean flag = true;
			
			switch (start) {
			case "LIST":
				int br = Integer.parseInt(inSocketData.readLine());
				for(int i = 0; i < br; i++) {
					String lista = inSocketData.readLine();
					System.out.println(lista);
				}
				String mess = inSocketControl.readLine();
				System.out.println(mess);
				break;

			case "RETR":
				fileName = poruka.substring(5);
				File fajl = new File("./MyComputer/" + fileName);
				fajl.createNewFile();
				int broj = Integer.parseInt(inSocketData.readLine());
				PrintWriter writer = new PrintWriter(fajl);
				for(int i = 0; i < broj; i++) {
					writer.println(inSocketData.readLine());
				}
				String asd = inSocketControl.readLine();
				System.out.println(asd);
				writer.close();
				break;
				
			case "STOR":
				fileName = poruka.substring(5);
				FileReader fileReader = new FileReader("./MyComputer/" + fileName);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				StringBuilder read = new StringBuilder();
				
				String line;
				int lineNumber = 0;
				while((line = bufferedReader.readLine()) != null) {
					read.append(line + "\n");
					lineNumber++;
				}
				
				outSocketData.println(lineNumber);
				outSocketData.println(read.toString());
				bufferedReader.close();
				
				String ruka = inSocketControl.readLine();
				System.out.println(ruka);
				break;
				
			case "DELE":
				String por = inSocketControl.readLine();
				System.out.println(por);
				break;
				
			case "SIZE":
				String size = inSocketData.readLine();
				String mes = inSocketControl.readLine();
				System.out.println(size);
				System.out.println(mes);
				break;
			}
			dataSocket.close();
			poruka = sc.nextLine();
		}
		outSocketControl.println(poruka);
		String poslednja = inSocketControl.readLine();
		System.out.println(poslednja);
		controlSocket.close();
	}
	
	public static void main(String[] args) {
		try {
			new Client();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
