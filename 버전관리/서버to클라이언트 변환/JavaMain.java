package raspberrypi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaMain {
	
	public static void main(String[] args){
		//getBeacon();
		//JavaClient.sendNode(7)
		

		ClientServer cs = new ClientServer();
		cs.run();
	}
	
		
/*
	public static void getBeacon()
    {   
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedReader bufferedReader = null;
        PrintStream printStream = null;
		
        try{
        	serverSocket = new ServerSocket(55547);
            System.out.println("I'm waiting here: " 
                + serverSocket.getLocalPort());            
                                
            socket = serverSocket.accept();
            System.out.println("from " + 
                socket.getInetAddress() + ":" + socket.getPort());
            
            InputStreamReader inputStreamReader = 
                new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            
            String line;
            
            while((line=bufferedReader.readLine()) != null){
                JavaCalculate.findNode(line);
            }
        }catch(IOException e){
            System.out.println(e.toString());
        }finally{
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    System.out.print(ex.toString());
                }
            }
            
            if(printStream!=null){
                printStream.close();
            }
            
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.print(ex.toString());
                }
            }
        }
        
    }*/
}
