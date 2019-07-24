package comp1206.sushi.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.portable.InputStream;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.User;

public class ClientCommunication extends Thread {

	ObjectOutputStream os;
	Client clientRest;
	ObjectInputStream is;

	public ClientCommunication(Client client) {
		this.clientRest = client;
	}

	public void run() {
		//Sets up connection with the server on localhost
		String serverName = "localhost";
		int port = Integer.parseInt("6066");
		try {
			System.out.println("Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(serverName, port);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			//Instantiates the output and input stream
			os = new ObjectOutputStream(client.getOutputStream());
			is = new ObjectInputStream(client.getInputStream());

			while(true) {
				//Constantly listens for messages from the server
				recieveMsg();
			}
		}
		catch (Exception e) {

		}

	}

	public void recieveMsg() throws ClassNotFoundException, IOException {
		//Reads the message from the server
		Object d = is.readObject();
		//If it is a dish it checks if it exists, if it exists then it's a remove request, else it adds it
		if(d instanceof Dish) {
			boolean carry = true;
			for (Dish check : clientRest.getDishes()) {
				if (check.getName().equals(((Dish) d).getName())) {
					carry = false;
					clientRest.removeDish(check);
					break;
				}
			}
			if (carry) {
				clientRest.addDish((Dish)d);
			}
		}
		if(d instanceof User) {((User)d).clearBasket();((User)d).createNewOrder();clientRest.Users.add((User)d);
		}
		//Adds it if it is a postcode
		if(d instanceof Postcode) {clientRest.Postcodes.add((Postcode) d);}
		//Sets the restaurant from the server
		if(d instanceof Restaurant) {clientRest.restaurant =((Restaurant)d);}
		//If it is an order it checks if it exists if it does then it is a remove request, else it adds it
		if(d instanceof Order) {
			if(((Order) d).getUser().getName().equals(clientRest.loggedUser.getName())) {
				for(Order o: clientRest.getOrders(clientRest.loggedUser)){
					if(o.getName().equals(((Order) d).getName())){
						o.setStatus(((Order) d).getStatus());
					}
				}
			}
		}	
	}

	/**
	 * Sends an order to the server
	 * @param Order which you want to send
	 * @throws IOException
	 */
	public void sendMsg(Order k) throws IOException {
		os.reset();
		os.writeObject(k);
		os.reset();
	}

	/**
	 * Sends a User to the server
	 * @param User which you want to send
	 * @throws IOException
	 */
	public void sendMsg(User k) throws IOException{
		os.reset();
		os.writeObject(k);
		os.reset();
	}
}