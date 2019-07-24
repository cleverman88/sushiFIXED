package comp1206.sushi.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;

import comp1206.sushi.common.*;
import comp1206.sushi.server.ServerInterface;

public class Server implements ServerInterface {
//TODO comment code
	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private boolean restockIngredients = true;
	private boolean restockDishes = true;
	Stock s = new Stock(this);
	ServerCommunication comms;
	public DataPersistence per;
	
	public Server() {
		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);
		try {
			comms = new ServerCommunication(this);
			comms.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		per = new DataPersistence(this);
		if(per.ReadObjectFromFile()) {per.WriteObjectToFile(s);}		
	

	}
	
	@Override
	public List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		s.addDishToStockControl(newDish);
		try {
			comms.sendMsg(newDish);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.notifyUpdate();
		per.WriteObjectToFile(newDish);

		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) throws UnableToDeleteException {
			this.dishes.remove(dish);
			s.removeDishFromStockControl(dish);
			per.removeObjectFromFile(dish);
			try {
				comms.sendMsg(dish);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		return s.getDishStock();
	}
	
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		this.restockIngredients = enabled;
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		this.restockDishes = enabled;
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {
		s.setStock(dish, stock);
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		s.setStock(ingredient, stock);
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}
	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount,Number weight) {
		Ingredient mockIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		this.ingredients.add(mockIngredient);
		s.addIngredientToStockControl(mockIngredient);
		this.notifyUpdate();
		per.WriteObjectToFile(mockIngredient);
		return mockIngredient;
	}
	
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount) {
		Ingredient mockIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,1);
		this.ingredients.add(mockIngredient);
		s.addIngredientToStockControl(mockIngredient);
		this.notifyUpdate();
		per.WriteObjectToFile(mockIngredient);
		return mockIngredient;
	}

	@Override
	public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
		for(Dish d: getDishes()) {
			if(d.getRecipe().containsKey(ingredient)) {
				throw new UnableToDeleteException("Dish is using this ingredient");
			}
		}
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		s.removeIngredientFromStockControl(ingredient);
		per.removeObjectFromFile(ingredient);
		this.notifyUpdate();
	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Postcode mockP = new Postcode(postcode.getName(), restaurant);
		Supplier mock = new Supplier(name,mockP);
		this.suppliers.add(mock);
	per.WriteObjectToFile(mock);
		return mock;
	}


	@Override
	public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
		for(Ingredient i : this.getIngredients()) {
			if(i.getSupplier().equals(supplier)) {
				throw new UnableToDeleteException("Ingredient is using this supplier");
			}
		}
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		per.removeObjectFromFile(supplier);
		this.notifyUpdate();
	}

	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed,s,restaurant);
		this.drones.add(mock);
		per.WriteObjectToFile(mock);
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) throws UnableToDeleteException {
		if(!(drone.getStatus().equals("Idle"))) {
			throw new UnableToDeleteException("Drone is still flying");
		}
		int index = this.drones.indexOf(drone);
		drone.kill();
		this.drones.remove(index);
		per.removeDrone(drone);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	@Override
	public Staff addStaff(String name) {
		Staff mock = new Staff(name,s);
		this.staff.add(mock);
		per.WriteObjectToFile(mock);
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) throws UnableToDeleteException {
		if(!(staff.getStatus().contains("Idle"))) {
			throw new UnableToDeleteException("Staff is still working");
		}
		staff.kill();
		this.staff.remove(staff);
		per.removeStaff(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) throws UnableToDeleteException {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		per.removeObjectFromFile(order);
		this.notifyUpdate();
	}
	
	@Override
	public Number getOrderCost(Order order) {
		return order.orderCost(order);
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		return s.getIngredientsStock();
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		Order mock = (Order)order;
		return mock.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
		
		per.updateObject(dish);
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
		per.updateObject(dish);
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code,restaurant);
		this.postcodes.add(mock);
		try {
			comms.sendMsg(mock);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.notifyUpdate();
		per.WriteObjectToFile(mock);
		return mock;
	}

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		for(Supplier supplier : this.getSuppliers()) {
			if(supplier.getPostcode().equals(postcode)) {
				throw new UnableToDeleteException("Supplier is using postcode");
			}
		}
		for(User user : this.getUsers()) {
			if(user.getPostcode().equals(postcode)) {
				throw new UnableToDeleteException("User is using postcode");
			}
		}
		
		this.postcodes.remove(postcode);
		per.removeObjectFromFile(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return this.users;
	}
	
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		per.removeObjectFromFile(user);
		this.notifyUpdate();
	}

	@Override
	public void loadConfiguration(String filename) {
		
		drones.forEach(drone -> drone.kill());
		staff.forEach(staff -> staff.kill());
		
		drones = new ArrayList<Drone>();
		staff = new ArrayList<Staff>();
		dishes = new ArrayList<Dish>();
		ingredients = new ArrayList<Ingredient>();
		orders = new ArrayList<Order>();
		suppliers = new ArrayList<Supplier>();
		users = new ArrayList<User>();
		postcodes = new ArrayList<Postcode>();
		listeners = new ArrayList<UpdateListener>();
		s = new Stock(this);
		per.clearList();
		System.out.println("Loaded configuration: " + filename);
		new Configuration(filename,this);
		
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for(Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish,recipeItem.getKey(),recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return order.getStatus().equals("Completed");
	}

	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		return drone.getStatus();
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		return staff.getStatus();
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
		per.updateObject(dish);
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
		per.updateObject(ingredient);
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
		
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
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
	public Restaurant getRestaurant() {
		
		return restaurant;
	}

	public void addRestaurant(Restaurant restaurant2) {
		restaurant = restaurant2;
		per.WriteObjectToFile(restaurant);
		
	}

	public Dish addDish(Dish newDish) {
		this.dishes.add(newDish);
		s.addDishToStockControl(newDish);
		try {
			comms.sendMsg(newDish);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.notifyUpdate();
		per.WriteObjectToFile(newDish);
		return newDish;
	}

	public void addOrder(Order order) {
		orders.add(order);
		per.WriteObjectToFile(order);
	}
	
	public void addUser(User user) {
		user.getPostcode().setRestaurant(restaurant);
		user.getPostcode().calculateDistance(restaurant);
		per.WriteObjectToFile(user);
		users.add(user);
	}
	
	public void updateOrderStatus(Order order, String status) {
		order.setStatus(status);
		try {
			comms.sendMsg(order);;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean getRestockIngredients() {
		return restockIngredients;
	}
	
	public boolean getRestockDishes() {
		return restockDishes;
	}
	
	
	/*
	 * For persistence
	 */
	
	public void addUser(User user,boolean f) {
		user.getPostcode().setRestaurant(restaurant);
		user.getPostcode().calculateDistance(restaurant);
		users.add(user);
	}
	
	public void addOrder(Order order, boolean b) {
		orders.add(order);
	}

	public void addIngredient(Ingredient obj) {
		ingredients.add(obj);
		this.notifyUpdate();
		
	}

	public void addStaff(Staff obj) {
		staff.add(obj);
		this.notifyUpdate();
	}

	public void addSupplier(Supplier obj) {
		suppliers.add(obj);
		this.notifyUpdate();
		
	}

	public void addPostcode(Postcode obj) {
		this.postcodes.add(obj);
		this.notifyUpdate();
		
	}

	public void addDrone(Drone obj) {
		this.drones.add(obj);
		
	}
	
	public void addRest(Restaurant rest) {
		this.restaurant = rest;
	}
	
	public void addDish(Dish d,boolean b) {
		this.dishes.add(d);
		this.notifyUpdate();
	}
	




}
