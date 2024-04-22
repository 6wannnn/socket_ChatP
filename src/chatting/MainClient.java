package chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {
	static final int PORT_NUM = 8888;
	String ipAddress;
	
	Socket client=null;
	BufferedReader read;
	PrintWriter output_stream;
	BufferedReader input_stream;
	String sendData;
	String receiveData;
	
	String user_id;
	ReceiveDataThread rt;
	boolean endflag = false;
	
	public MainClient(String id, String ip) {
		user_id = id;
		ipAddress = ip;
		try {
			System.out.println("=====  클라이언트 =====");
			client = new Socket(ipAddress, PORT_NUM);
			read = new BufferedReader(new InputStreamReader(System.in));
			input_stream = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output_stream = new PrintWriter(client.getOutputStream());
			output_stream.println(user_id);
			output_stream.flush();
			
			Thread t = new Thread(rt);
			t.start();
			
			while(true) {
				sendData = read.readLine();
				output_stream.println(sendData);
				output_stream.flush();
				
				if(sendData.equals("/quit")) {
					endflag = true;
					break;
				}
			}
			System.out.print("클라이언트 접속을 종료합니다.");
			System.exit(0);
		} catch(Exception e) {
			if(!endflag) {
				e.printStackTrace();
			}
		} finally {
			try {
				input_stream.close();
				output_stream.close();
				client.close();
				
				System.exit(0);
			} catch(IOException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.print("아이디를 입력하세요 : ");
		String id = sc.next();
		new MainClient(id, "172.30.1.23");
	}
}
