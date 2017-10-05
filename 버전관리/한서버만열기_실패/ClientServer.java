package raspberrypi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientServer extends Thread {

    Socket socket = null;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    

    public ClientServer(Socket socket){
    	this.socket = socket;
    }

	public void run(int node){
		
		String msg = "";

        try {
        	System.out.println("Client Accepted: "+socket.getPort() );
        	msg = Integer.toString(node);
        	msg += "-1";
        	
        	inputStream = new ObjectInputStream(socket.getInputStream());
        	System.out.println((String)inputStream.readObject());
        	inputStream.close();
        	
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(msg);
            outputStream.close();
            
            System.out.println("node send: "+msg);
                
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
