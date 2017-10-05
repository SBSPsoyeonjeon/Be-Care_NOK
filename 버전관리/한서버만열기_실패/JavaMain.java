package raspberrypi;

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class JavaMain {
	
    static final int port = 13150;
	
	public static void main(String[] args){
		
	    ServerSocket socket = null;
	    Socket s = null;
		Scanner scan = null;
		int data = 0;
		
		try {
			socket = new ServerSocket(port);
			scan = new Scanner(System.in);
			
			while(true){
				System.out.println("next node? = ");
				data = scan.nextInt();
				if(data == -1){
					System.out.println("stop!");
					break;
				}
				try{
		        	s = socket.accept();
					if(!s.isClosed()){
						ClientServer client = new ClientServer(s);
						client.run(data);
						s.close();
					} else{
						System.out.println("socket closed");
					}
				} catch(IOException e){
					System.out.println("no client");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			scan.close();
		}
	}
}
