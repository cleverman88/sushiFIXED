package comp1206.sushi.server;

import java.net.*;
import java.util.ArrayList;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;
import comp1206.sushi.server.ServerInterface.UnableToDeleteException;

import java.io.*;

public class ServerCommunication extends Thread {
	private ServerSocket serverSocket;
	Server serverRest;
	ObjectOutputStream os;
	ArrayList<ObjectOutputStream> sockets;
	ArrayList<ObjectInputStream> socketsListen;

	public ServerCommunication(Server server) throws IOException {
		serverSocket = new ServerSocket(6066);
		this.serverRest = server;
		sockets = new ArrayList<ObjectOutputStream>();
		// serverSocket.setSoTimeout(10000);
	}

	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				sockets.add(out);
				new Listen((new ObjectInputStream(server.getInputStream())));
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				out.writeObject(serverRest.getRestaurant());
				for(Dish d : serverRest.getDishes()) {
					out.reset();
					out.writeObject(d);
					out.reset();
				}
				for(Postcode p : serverRest.getPostcodes()) {
					out.reset();
					out.writeObject(p);
					out.reset();
				}
				
				for(User u: serverRest.getUsers()) {
					out.reset();
					out.writeObject(u);
					out.reset();
				}
				

			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void sendMsg(Dish d) throws IOException {
		ArrayList<ObjectOutputStream> temp = new ArrayList<ObjectOutputStream>(sockets);
		for (ObjectOutputStream s : temp) {
				try {
					s.reset();
					s.writeObject(d);
					s.reset();
				}
				catch (SocketException e) {
					sockets.remove(s);
			}

		}
	}
	
	public void sendMsg(Postcode p) throws IOException{
		ArrayList<ObjectOutputStream> temp = new ArrayList<ObjectOutputStream>(sockets);
		for (ObjectOutputStream s : temp) {
				try {
					s.reset();
					s.writeObject(p);
					s.reset();
				}
				catch (SocketException e) {
					sockets.remove(s);
				}

			}
		}
	
	public void sendMsg(Order o) throws IOException{
		ArrayList<ObjectOutputStream> temp = new ArrayList<ObjectOutputStream>(sockets);
		for (ObjectOutputStream s : temp) {
				try {
					s.reset();
					s.writeObject(o);
					s.reset();
				}
				catch (SocketException e) {
					sockets.remove(s);
				}

			}
		}
	

	public class Listen extends Thread {
		ObjectInputStream is;

		public Listen(ObjectInputStream is) {
			this.is = is;
			start();
		}

		public void run() {
			try {
				while (true) {
					recieveMsg();
				} 
			}
			catch (Exception e) {

			}
		}
		
		public void recieveMsg() throws ClassNotFoundException, IOException, UnableToDeleteException {
			Object o = is.readObject(); 
			if(o instanceof Order) {
				boolean carry = true;
				for (Order check : serverRest.getOrders()) {
					if (check.getName().equals(((Order) o).getName())) {
						carry = false;
						serverRest.removeOrder(check);
						break;
					}
				}
				if (carry) {
					((Order) o).getUser().getPostcode().calculateDistance(serverRest.restaurant);
					for(User u : serverRest.getUsers()) {
						if(((Order) o).getUser().getName().equals(u.getName())) {
							u.addOrderToList(u, (Order)o);
							u.setBasket(((Order) o).getBasketOrder());
							break;
						}
							
					}
					serverRest.addOrder((Order) o);
				}
			}
			
			if(o instanceof User) {
				serverRest.addUser((User) o);
			}
		}
	}


	

}
