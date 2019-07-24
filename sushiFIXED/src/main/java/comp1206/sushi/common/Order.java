package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model  implements java.io.Serializable {

	private static final long serialVersionUID = -6702715152925430163L;
	private String status;
	private Map <Dish,Number> ordersBasket;
	public Map <Dish, Number> finalOrder;
	private User user;
	private String temp;
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
	LocalDateTime now = LocalDateTime.now();  
	public Order(User user) {
		this.user = user;
		this.status = "Pending";
		this.name = dtf.format(now);
		this.temp = name.toString();
		ordersBasket = new HashMap<Dish,Number>();
	}

	public Number getDistance() {
		return user.getDistance();
	}
	
	@Override 
	public String getName() {
		return this.temp;
	}
	
	public void setTime() {
		this.name = dtf.format(now);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	
	public Map<Dish, Number> setBasketToOrder(Map <Dish, Number> b){
		ordersBasket = new HashMap<Dish,Number> ();
		for(Dish d : b.keySet()) {
			ordersBasket.put(d, b.get(d));
		}
		finalOrder = new HashMap<Dish,Number>();
		for(Dish d : b.keySet()) {
			finalOrder.put(d, b.get(d));
		}
		
		user.addOrderToList(user, this);
		return ordersBasket;
		
	}
	
	public Map<Dish, Number> getBasketOrder(){
		return ordersBasket;
	}

	
	public User getUser() {
		return user;
	}
	
	public Number orderCost(Order order) {
		int total = 0;
		for(Dish d : order.finalOrder.keySet()) {
			total += (int)d.getPrice() * (int)order.finalOrder.get(d);
		}
		
		return total;
	}
		
	public void cancelOrder(Order order) {
		order.getUser().getOrders().remove(order);
	}

}
