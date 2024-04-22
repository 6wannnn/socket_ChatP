package chatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class ChatServerThread implements Runnable {
	Socket child;
	BufferedReader input_stream; // input_stream
	PrintWriter output_stream; // output_stream(ObjectOutputStream)
	
	String user_id;
	HashMap<String, PrintWriter> hm;
	
	InetAddress ip;
	String msg;
	
	public ChatServerThread(Socket s, HashMap<String, PrintWriter> h) {
		child = s;
		hm = h;
		
		try {
			input_stream = new BufferedReader(new InputStreamReader(child.getInputStream()));
			output_stream = new PrintWriter(child.getOutputStream());
			user_id = input_stream.readLine();
			ip = child.getInetAddress();
			System.out.println(ip + "로부터 " + user_id + "님이 접속하였습니다.");
			broadcast(user_id + "님이 접속하셨습니다.");
			synchronized(hm) {
				hm.put(user_id, output_stream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String received_data;
		
		try {
			while((received_data = input_stream.readLine()) != null) {
				if(received_data.equals("/quit")) {
					synchronized(hm) {
						hm.remove(user_id);
					}
					break;
				} else if(received_data.indexOf("/to")>=0) {
					sendMsg(received_data);
				} else {
					System.out.println(user_id + " >> " + received_data);
					broadcast(user_id + " >> " + received_data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized(hm) {
				hm.remove(user_id);
			}
			broadcast(user_id + "님이 퇴장했습니다.");
			System.out.println(user_id + "님이 퇴장했습니다.");
			
			try {
				if(child != null) {
					input_stream.close();
					output_stream.close();
					child.close();
				}
			} catch (Exception e) {}
		}
	}
	
	public void broadcast(String message) {
		synchronized(hm) {
			try {
				for(PrintWriter output_stream : hm.values()) {
					output_stream.println(message);
					output_stream.flush();
				}
			} catch(Exception e) {}
		}
	}
	
	public void sendMsg(String message) {
		int begin = message.indexOf(" ") + 1;
		int end = message.indexOf(" ", begin);
		
		if(end != -1) {
			String id = message.substring(begin, end);
			String msg = message.substring(end+1);
			PrintWriter output_stream = hm.get(id);
			
			try {
				if(output_stream != null) {
					output_stream.println(user_id + "님이 다음과 같은 귓속말을 보내셨습니다. : " + msg);
					output_stream.flush();
				}
			} catch(Exception e) {}
		}
	}
}
