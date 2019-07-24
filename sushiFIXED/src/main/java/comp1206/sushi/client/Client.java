package comp1206.sushi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateEvent;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;
import comp1206.sushi.server.ServerInterface.UnableToDeleteException;

public class Client implements ClientInterface {
	//Lists that will be used in the class
	public Restaurant restaurant;
	public List<User> Users = new ArrayList<User>();
	public List<Postcode> Postcodes = new ArrayList<Postcode>();
	public List<Dish> Dishes = new ArrayList<Dish>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	
	ClientCommunication comms;
	public User loggedUser;
    private static final Logger logger = LogManager.getLogger("Client");
	
	public Client() {
        logger.info("Starting up client...");
 
        //Setting temp values it gets the information from the server.
        Postcodes.add(new Postcode("UB5 6PB"));
		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);
		
		//Starts the communication between the server and the client
		comms = new ClientCommunication(this);
	    comms.start();
	}
	
	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}
	
	/**
	 * Adds a dish to the client
	 * @param Dish which you wish to add to the client
	 * @returns Dish that has been added
	 */
	
	public Dish addDish(Dish d) {
		this.Dishes.add(d);
		this.notifyUpdate();
		return d;
	}
	
	@Override
	public String getRestaurantName() {
		
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		
		return restaurant.getLocation();
	}
	
	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		//If the postcode is null it just sets it to mine
		if(postcode== null) {
			postcode = new Postcode("UB5 6PB", restaurant);
		}
		User mock = new User(username, password, address, postcode);
		loggedUser = mock;
		Users.add(mock);
		//Sends the information to the client
		try {
			comms.sendMsg(mock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mock;
	}

	@Override
	public User login(String username, String password) {
		for(User s : Users) {
			if(s.getName().equals(username) && s.getPassword().equals(password)) {
				loggedUser = s;
				return s;
			}
		}
		return null;
	}
	
	@Override
	public List<Postcode> getPostcodes() {
		
		return Postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		
		return Dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		
		return dish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		
		return dish.getPrice();
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
	
		return user.getBasket();
	}

	@Override
	public Number getBasketCost(User user) {
		
		return user.BasketCost();
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		user.addToBasket(dish, quantity);
		this.notifyUpdate();
	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		user.removeDishFromBasket(dish);
		user.addToBasket(dish, quantity);
		this.notifyUpdate();
	}

	@Override
	public Order checkoutBasket(User user) {

		user.returnOrder().setBasketToOrder(user.getBasket());
		Order temp = user.returnOrder();		
		user.createNewOrder();
		this.notifyUpdate();
		
		//Sends the information to the server
		try {
			comms.sendMsg(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user.returnOrder();
	}

	@Override
	public void clearBasket(User user) {
		user.clearBasket();
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders(User user) {
		
		return user.getOrders();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return order.getStatus().equals("Complete");
	}

	@Override
	public String getOrderStatus(Order order) {
		
		return order.getStatus();
	}

	@Override
	public Number getOrderCost(Order order) {

		return order.orderCost(order);
	}

	@Override
	public void cancelOrder(Order order) {
		//Sends the cancel order request to the server
		if(order.getStatus().equals("Pending")) {
		try {
			comms.sendMsg(order);
		} catch (IOException e) {
			e.printStackTrace();
		}
		order.cancelOrder(order);
		this.notifyUpdate();
		}
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void notifyUpdate() {
		try {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
		}
		catch(NullPointerException e) {
			System.out.println("CAUGHT");
		}
	}
	
	/**
	 * Removes a dish from the client
	 * @param Dish which you wish to add to the client
	 */
	
	public void removeDish(Dish d) {
		this.Dishes.remove(d);
		this.notifyUpdate();
	}

}
