package chatting;

import java.io.BufferedReader;
import java.net.Socket;

// Thread 사용위한 Runnable 인터페이스
public class ReceiveDataThread implements Runnable {
	Socket client;
	BufferedReader input_stream; // ois(ObjectInputStream)
	String received_data;
	
	public ReceiveDataThread(Socket s, BufferedReader input_stream) {
		client = s;
		this.input_stream = input_stream;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while((received_data = input_stream.readLine()) != null) {
				System.out.println(received_data);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input_stream.close();
				client.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}	
	}
}
