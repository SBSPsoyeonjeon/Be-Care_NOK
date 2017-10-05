package raspberrypi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServer extends Thread {
	
    ServerSocket socket = null;
    ObjectOutputStream outputStream;
    
    static final int port = 13150;

	public void run(int node){
		
		String msg = "";
        
        try {
        		socket = new ServerSocket(port);
        		Socket s = socket.accept();
                System.out.println("Client Accepted: "+s.getPort() );
                msg = Integer.toString(node);
                msg += "-1";
                
                outputStream = new ObjectOutputStream(s.getOutputStream());
                outputStream.writeObject(msg);
                outputStream.close();
                System.out.println("node send: "+msg);

                if(socket != null){
                    try {
                        socket.close();
                        System.out.println("socket closed");
                    } catch (IOException ex) {
                        System.out.println(ex.toString());
                    }
                }
                
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
