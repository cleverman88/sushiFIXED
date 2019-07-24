package comp1206.sushi.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

public class User extends Model implements java.io.Serializable {
	
	private static final long serialVersionUID = -3646082747382701692L;
	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	private Map<Dish, Number> basket;
	private Order order;
	private List<Order> orders = new ArrayList<Order>();
	
	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		basket = new HashMap<Dish, Number>();
		this.password = password;
		this.address = address;
		this.postcode = postcode;
		order = new Order(this);
	}

	public String getName() {
		return name;
	}
	
	public Map<Dish,Number> getBasket(){
		return basket;
	}
	
	public void addToBasket(Dish d, Number n) {
		basket.put(d, n);
	}
	
	public void removeDishFromBasket(Dish d) {
		basket.remove(d);
	}
	
	public Number BasketCost() {
		int total = 0;
		for(Dish d : basket.keySet()) {
			total += (int)d.getPrice() * (int)basket.get(d);
		}
		return total;
	}
	
	public void clearBasket() {
		basket = new HashMap<Dish, Number>();
	}
	
	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public Number getDistance() {
		return postcode.getDistance();
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}
	
	public void addOrderToList(User user, Order order) {
		user.orders.add(order);
	}
	
	public Order returnOrder() {
		return order;
	}
	
	public void createNewOrder() {
		order = new Order(this);
	}
	
	
	public List<Order> getOrders(){
		return orders;
	}
	
	public void setBasket(Map<Dish, Number> basket) {
		this.basket = basket;
	}

}
