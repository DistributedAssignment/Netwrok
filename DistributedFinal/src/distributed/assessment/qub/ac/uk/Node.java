package distributed.assessment.qub.ac.uk;
import java.net.URL;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/*
 * DO NOT FORGET STUFF ABOUT THE CLOCK SO THAT TASKS ARE DONE IN ORDER
 * 
 */
public class Node implements Runnable{
	private String ip;
	private InetAddress IP;
	private int port;
	private int initial_port;
	private String initial_ip;
	private Queue<String> accounts;	
	private Queue<byte[]> messages;
	private InetAddress initial_IP;
	private DatagramSocket socket;
	private Account[] account_list;
	private int[] port_list;
	private InetAddress[] IP_list;
	private int[] l_port_list;
	private String[] node_list;
	private String name;
	private int index;
	public Node() {
		this.accounts = new LinkedList<String>();
		this.messages = new LinkedList<byte[]>();
		this.account_list = new Account[2048];
		this.ip = null;
		this.IP = null;
		this.port = 1;
		this.socket = null;
		this.initial_port = 1;
		this.initial_ip = null;
		this.initial_IP = null;
		this.port_list = null;
		this.IP_list = null;
		this.node_list = null;
		this.name = null;
		this.index = 0;
	}
	
	public void run() {
		/**TO DO**/
		/* TUESDAY
		 * SET UP REPOSITORY CLASS
		 * FINISH 3
		 * FINISH 1
		 * TEST BY GRADUALLY ADDING THE STUFF CREATED START WITH CREATING AN INITIAL NODE AND TEST BY ADDING MORE STRUCTURE
		 * 
		 * WENSDAY
		 * FINISH 4
		 * REFER TO ASSIGNMENT SHEET AND CHECK OF STUFF
		 * 
		 * THURSDAY
		 * ADD ANY ADDITIONAL STUFF
		 * TEST
		 * WRITE REPORT 
		 * 
		 */
		//This sets the node up for being apart of a network
		initialise();

		//Now the node has been created we can now run the manager
		try {
			manager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void manager() throws Exception {
		//This will be the interface that the client deals with when managing there accounts
		final String[] MENU_LIST = {"create","manage","disconnect"};
    	Scanner myObj = new Scanner(System.in);
		boolean finished = false;
		int number = 0;
		String account = null;
		byte[] data = new byte[1028];
		while (!finished) {
			try {
		    	//Menu
				System.out.println("Please press: ");
		    	for (int i=0; i<MENU_LIST.length; i++) {
		    		int n = i+1;
		    		System.out.println(n+": "+MENU_LIST[i]);
		    		
		    	}
		    	System.out.print("Input: ");
		    	account = myObj.nextLine();
		    	account.trim();
		    	number = Integer.parseInt(account);
				}
				catch(Exception NumberFormatException) {
				  number = 0;
				}
			
			switch(number) {
			  case 1:
				  createAccount();
			    break;
			  case 2:
				try {
		    	System.out.print("Account number: ");
		    	account = myObj.nextLine();
		    	account.trim();  
				} catch (Exception e) {
					account = "0";
				}
			    if (retrieveData(account)) {
			    	while (accountManagement(account));
			    }		    
			    break;
			  case 3:
				  
			  default:
	    		  System.out.println("Invalid request");
	    		  break;
			}	
		}
	}

	 private void createAccount() throws Exception {
		  Scanner myObj = new Scanner(System.in);
			boolean create = false;
			int overdraft = 0;
			while (!create) {
				String overdraft_str = null;
		    	System.out.print("Please input your desired overdraft limit (0-1500): ");
				try {
			    	overdraft_str = myObj.nextLine();
			    	overdraft_str.trim();
			    	overdraft = Integer.parseInt(overdraft_str);
		
					if (overdraft >= 0 && overdraft <= 1500) {
						create = true;
					}
					}
					catch(Exception NumberFormatException) {
					System.out.println("Not a number"); 
					}
			}	
			int number = 0;
			//Finds an account with free space
			for (int i = 0; i < account_list.length; i++) {
				if (account_list[i] == null) {
					break;
				}
				number += 1;
			}
			/**NEED TO VERIFY THE ACCOUNT NUMBER IS CORRECT BY CONSULTING WITH THE INITIAL NODE**/
			
			Account a = new Account(number,0,overdraft);
		    while (accountManagement(Integer.toString(a.getNumber())));	
	}

	private boolean accountManagement(String account) throws Exception{
			final String[] REQUEST_LIST = {"retreive","withdraw","deposit","close","exit"};
	    	Scanner myObj = new Scanner(System.in);
	    	int number = 0;
	    	String choice;
	    	System.out.println("Account "+account);
	    	System.out.println("Please press: ");
	    	for (int i=0; i<REQUEST_LIST.length; i++) {
	    		int n = i+1;
	    		System.out.println(n+": "+REQUEST_LIST[i]);
	    		
	    	}
			try {
		    	System.out.print("Input: ");
		    	choice = myObj.nextLine();
		    	choice.trim();
		    	number = Integer.parseInt(choice);
				}
				catch(Exception NumberFormatException) {
				  number = 0;
				}
	    	
	    	switch(number) {
	    	  case 1:
	    		boolean b = retrieveData(account);
	    		return b;
	    	  case 2:
	    		 withdraw(account);
	    		 accounts.add(account);
	    		return true;
	    	  case 3:
	    		deposit(account);
	    		accounts.add(account);
	    		return true;
	    	  case 4:
	      		closeData(account);
	      		accounts.add(account);
	    		return false;
	    	  case 5:
	    		return false;
	    	  default:
	    		  System.out.println("Invalid request");
	    		  return true;
	    	}
	    }
	
	 private void closeData(String account) {
		int num = Integer.parseInt(account);
		account_list[num] = null;	
	}

	private void deposit(String account) {
		Scanner myObj = new Scanner(System.in);
	 	boolean deposit =  false;
	 	int money = 0;
	 	String money_str = null;
	 	while (!(deposit)) {
	 	
	 	//Here the user is asked to input there desired withdraw
		try {
    	System.out.print("How much would you like to deposit: ");
    	money_str = myObj.nextLine();
    	money_str.trim();
    	money= Integer.parseInt(account);
		}
		catch(Exception NumberFormatException) {
		 money = -1;
		}
		
		
		int num = Integer.parseInt(account);
		Account a = account_list[num];
		System.out.println("");
		System.out.println("--------------------------");
		if (a==null) {
			System.out.println("Account no longer exists");
			System.out.println("--------------------------");
			System.out.println("");
			
		} 
		
		int d = a.deposit(money);
		
		if (d==-1) {
			System.out.println("Invalid Input");
			System.out.println("--------------------------");
			System.out.println("");
		}else {
			System.out.println("Account balanca: " + d);
			System.out.println("--------------------------");
			System.out.println("");
			deposit = true;
		}
	 	}
	}

	private void withdraw(String account) {
		Scanner myObj = new Scanner(System.in);
	 	boolean withdraw = false;
	 	int money = 0;
	 	String money_str = null;
	 	while (!(withdraw)) {
	 	
	 	//Here the user is asked to input there desired withdraw
		try {
    	System.out.print("How much would you like to withdraw: ");
    	money_str = myObj.nextLine();
    	money_str.trim();
    	money= Integer.parseInt(account);
		}
		catch(Exception NumberFormatException) {
		 money = -1;
		 money_str = "-1";
		}
		int num = Integer.parseInt(account);
		Account a = account_list[num];
		System.out.println("");
		System.out.println("--------------------------");
		if (a==null) {
			System.out.println("Account no longer exists");
			System.out.println("--------------------------");
			System.out.println("");
			break;
		}
		
		int w = a.withdraw(money);
		if (w == -1) {
			System.out.println("Insuffcient funds");
			System.out.println("--------------------------");
			System.out.println("");
			withdraw = true;
			
		} else if (w == -2) {
			System.out.println("Inavlid input");
			System.out.println("--------------------------");
			System.out.println("");
			
		}else {
			System.out.println("Account balance: " + w);
			System.out.println("--------------------------");
			System.out.println("");
			withdraw = true;
		}
	 	}
		
	}

	private boolean retrieveData(String account) {
		try {
			int num = Integer.parseInt(account);
			Account a = account_list[num];
			if (account == null) {
				System.out.println("This account does not exist");
				return false;
			} else {
				System.out.println();
				System.out.println("-----------------------------");
				System.out.println("Account Number: "+a.getNumber());
				System.out.println("Balance: "+a.getMoney());
				System.out.println("Overdraft: "+a.getOverdraft());
				System.out.println("-----------------------------");
				System.out.println();
				return true;
			}
		} catch (Exception e) {
			System.out.println("This account does not exist");
			return false;
		}
	}

	private void initialise() {
		//Deal with ip
		try {
			ip = getLocalAddress();
			IP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//Create a socket this is done by trying to create one until a port not in use is found
		boolean found = false;
		while (!found) {
			try {
				socket = new DatagramSocket(port,IP);
				found = true;
			} catch (Exception e) {
				found = false;
				port += 1;
			}
		}
		
		//Now the node needs to be connected to the network
		/*There was a previous network who's data is still in the repository or there is an election being held for a new initial node,
		 * This node will listen for the initial node to see if it is still there,
		 * If an election is being held this will be stated in the data and then the node will wait for the election to be finished to join the network
		 */
		
		/***HERE GIT REPOSITORY IS BEING ACCESSED**/
		
		
		/**THE END OF THAT BIT**/
		
		//Now the node listen's too see if this node still exists
		Listener search = new Listener("Connect",index);
		boolean exists = false;
		try {
			search.run();
			exists = true;
		} catch (NumberFormatException e) {
			exists = false;
		}
		
		//If this is the initial node then you will also need to declare the NodeUpdater and NodeManager
		if (!exists) {
			
		}

	}
	
	
	//This returns the IP that the computer is operating on
	private static String getLocalAddress() {
	    try (DatagramSocket skt = new DatagramSocket()) {
	        // Use default gateway and arbitrary port
	        skt.connect(InetAddress.getByName("192.168.1.1"), 12345);
	        return skt.getLocalAddress().getHostAddress();
	    } catch (Exception e) {
	        return null;
	    }
		}
	
	//The main method simply creates the node and starts running it
	public static void main(String args[]) {
		Node node = new Node();
		node.run();
	}
	
	/**Below are the classes which will be used by the network**/
	
	//This listens for a message from a node to see if it is still on the network
	//It can also be used to test if the previous network which existed is still in use
	private class MessageHandler implements Runnable{
		public MessageHandler() {
			
		}
		
		
		public void run() {
			String[][] updates = new String[20][5];
			String[][] nodes = new String[20][5];
			int[] times = new int[20];
			int n = 0;
			int k = 0;
			for (int i = 0; i<20; i++) {
				try {
				String[] m = (new String(messages.remove())).split(" ");
				//Preps the updates to handled separately 
				if (m[1].equals("Update")) {
					updates[n] = m;
					n+=1;
				} else if (m[1].equals("New Node")){
					nodes[k] = m;
					n+=1;
				} else if (m[1].equals("Initial Fail")){
					/***ADD LATER***/
				}  else if (m[1].equals("Initial New Node")){
					/***ADD LATER***/
				} else if (m[1].equals("New Account")){
					/***ADD LATER***/
				}
				} catch (Exception e) {
					//If all messages have been put in the block
					break;
				}
			}
			//So the update management is started
			(new Thread (new UpdateHandler(updates, times))).start();
			try {
			int j =0;
			for (int i = 0; i<nodes.length;i++) {
					j = Integer.parseInt(nodes[i][4]);
					port_list[j] = Integer.parseInt(nodes[i][1]);
					IP_list[j] = InetAddress.getByName(nodes[i][2]);
					Listener l = new Listener("Node",j);
					(new Thread(l)).start();
			}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class UpdateHandler implements Runnable{
		String[][] updates;
		public UpdateHandler(String[][] updates, int[] times ) {
			this.updates = updates;
		}
		
		
		public void run() {
			try {
			int j =0;
			for (int i = 0; i<updates.length;i++) {
				j = Integer.parseInt(updates[i][4]);
				port_list[j] = Integer.parseInt(updates[i][1]);
				IP_list[j] = InetAddress.getByName(updates[i][2]);
				Listener l = new Listener("Node",j);
				(new Thread(l)).start();
		
		}
		}
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class Listener implements Runnable{
		private String type;
		private int index;
		private DatagramSocket l_socket;
		private Thread t;
		private int l_port;
		public Listener(String t, int i) {
			this.type = t;
			this.l_socket = null;
			this.l_port = 1;
			this.t = null;
			this.index = i;
		}
		
		public void destroy() throws Exception {
			l_socket.close();
		}
		
		public void run() {
			initialise();
			l_port_list[index] = l_port;
			while(true) {
				//As this is listening for one node one time it just starts the times and waits
				byte[] receive = new byte[1028];
				t = (new Thread (new Timer()));
				t.start();
				try {
					DatagramPacket packet = new DatagramPacket(receive, receive.length);
					
					l_socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//If a package is received the timer is stopped and the run() method terminates
				t.interrupt();

				
				//As in this case you are waiting for a connection only once
				if (type.equals("Connect")) {
					break;
					//So the initial node has been found to exists and run() can terminate and no exception is thrown
				}
			}

		}
		
		private void initialise() {
			//Create a socket this is done by trying to create one until a port not in use is found
			boolean found = false;
			while (!found) {
				try {
					l_socket = new DatagramSocket(l_port,IP);
					found = true;
				} catch (Exception e) {
					found = false;
					l_port += 1;
				}
			}
		}
			
		private class Timer implements Runnable{
			long start_time;
			public Timer() {
				this.start_time = System.currentTimeMillis();
			}
			public void run(){			
			while(true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long end_time = System.currentTimeMillis();
				long time =(end_time - start_time)/1000;
				if (time >= 25.0) {
					//Begins the process of removing the node from the list
					//Unless it is of type Initial or Connection
					if (type.equals("Connect")) {
						//This will throw an exception back to the initialise method
						Integer.parseInt("NOT FOUND");
					} else if (type.equals("Initial")) {
						//Process for creating new initial node
					} else {
						//Process of removing node from list
					}
				}
			}
		}
		}
	}
	
	private class Receiver implements Runnable{
		private DatagramSocket socket_r;
		int r_port;
		public Receiver() {
			this.socket_r = null;
			this.r_port = 1;
		}
		
		public void run() {
			initialise();
			byte[] receive;
			while (true) {
				receive = new byte[1028];
				//Waits to receive a connection request from a client
				DatagramPacket packet = new DatagramPacket(receive, receive.length);
				try {
					socket_r.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				messages.add(receive);
				}	
			}
		
		public byte[] getMessage() {
			return messages.remove();
		}
		
		private int initialise() {
		    boolean setup= false;
			while (setup == false) {
	    	try {
	    		setup = true;
	    		socket = new DatagramSocket(port,IP);
			} catch (SocketException e) {
				setup = false;
				port +=1;
			}
			}
			return port;
		}
		
		public void destroy(){
			socket_r.close();
			messages = null;
			port = 0;
			}
	}

	private class Updater implements Runnable{
		//When ever an account is changed the node adds the account number to the accounts queue
		private DatagramSocket u_socket;
		private int u_port;
		public Updater(){
			this.u_socket = null;
			this.u_port = 1;
			
		}
		
		public void run() {
			//The socket is created 
			initialise();
			
			while (true) {
			try {
			//Tries to retrieve an account
			String temp_data;
			byte[] data;
			String account = accounts.remove();	
			/**If this an account that has been removed**/
			if (account_list[Integer.parseInt(account)] == null) {
				//Now this will be sent to every node in the network
				for (int i = 0; i < port_list.length ;i++) {
				temp_data = "Update "+account+" null "+Integer.toString(port) +" "+ip+" "+i;
				data = temp_data.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[i],port_list[i]);
				u_socket.send(packet);
				packet = null;
				}
				break;
			}
			
			/**If this is an account that still exists or has been created**/
			String account_data = account_list[Integer.parseInt(account)].getStringFormat();
			
			//Now this will be sent to every node in the network
			for (int i = 0; i < port_list.length ;i++) {
			temp_data = "Update "+account_data+" "+Integer.toString(port) +" "+ip+" "+i;
			data = temp_data.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[i],port_list[i]);
			u_socket.send(packet);
			packet = null;
			}
			System.err.println(name + " Updater; Sending update of account");
			} catch (NoSuchElementException | IOException e) {
				System.err.println(name + " Updater; No accounts to update");
			}
			}
		}
		
		private void initialise() {
			//Create a socket this is done by trying to create one until a port not in use is found
			boolean found = false;
			while (!found) {
				try {
					u_socket = new DatagramSocket(u_port,IP);
					found = true;
				} catch (Exception e) {
					found = false;
					u_port += 1;
				}
			}
		}
		
	}	

	private class Ping implements Runnable{
		int p_port;
		DatagramSocket p_socket;
		public Ping(String ip) {
			//Initialise the information about the socket
			this.p_port =  1;
			this.p_socket = null;
		}
		
		private void initialise() {
			
		    boolean setup= false;
			while (setup == false) {
		    	try {
		    		setup = true;
		    		p_socket = new DatagramSocket(p_port,IP);
				} catch (SocketException e) {
					setup = false;
					p_port +=1;
				}
		    }
		}
		
		public void run() {
			initialise();
			//So the ping reads from the node arrays and sends a ping to the listener 
			int i = 0;
			while (true) {
				while (i<IP_list.length) {
					if (!(IP_list[i]==null)) {
						String data_str = Integer.toString(p_port);
						byte[] data = data_str.getBytes();
						DatagramPacket packet;
						try {
							packet = new DatagramPacket(data, (data).length,IP_list[i], l_port_list[i]);
							socket.send(packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i += 1;
					}
				}
				i = 0;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class NodeUpdater implements Runnable{
		public NodeUpdater(byte[] data){
		}
		
		public void run() {

		}
	}

	private class NodeManager implements Runnable{
		public NodeManager(){
			
		}
		
		public void run() {
			
		}
	}
}
	
	


