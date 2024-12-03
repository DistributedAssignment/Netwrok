package distributed.assessment.qub.ac.uk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Repository {
	private static DatagramSocket socket;
	private static int port= 999;
	private static int ini_port= 789;
	public static void Main(String args[]) throws IOException {
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte[] receive;
		while (true) {
			receive = new byte[1028];
			//Waits to receive a connection request from a client
			System.err.println("Repository is waiting...");
			DatagramPacket packet = new DatagramPacket(receive, receive.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Received packet");
    		String[] data;	
    		String temp = new String(receive);
    		data = temp.split(" ");
			if (data.length == 2) {
				ini_port = Integer.parseInt(data[0].trim());
				byte[] info = temp.getBytes();
				packet = new DatagramPacket(info, info.length, InetAddress.getByName(data[1].trim()), Integer.parseInt(data[0].trim()));
				socket.send(packet);
			} else {
				//Sends the information
				temp = Integer.toString(ini_port);
				byte[] info = temp.getBytes();
				packet = new DatagramPacket(info, info.length, InetAddress.getByName(data[1].trim()), Integer.parseInt(data[0].trim()));
				socket.send(packet);
			}
			}	
		}
	
	private static String getLocalAddress() {
	    try (DatagramSocket skt = new DatagramSocket()) {
	        // Use default gateway and arbitrary port
	        skt.connect(InetAddress.getByName("192.168.1.1"), 12345);
	        return skt.getLocalAddress().getHostAddress();
	    } catch (Exception e) {
	        return null;
	    }
	}
}

