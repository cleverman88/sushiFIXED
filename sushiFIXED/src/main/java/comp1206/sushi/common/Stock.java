package comp1206.sushi.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import comp1206.sushi.server.Server;



public class Stock implements java.io.Serializable {
	private static final long serialVersionUID = 5661864626241358088L;
	Map<Ingredient, Number> stockIngredient = new ConcurrentHashMap<Ingredient, Number>();
	Map<Dish, Number> stockDish = new ConcurrentHashMap<Dish, Number>();
	List<Dish> dishesInProgress;
	List<Ingredient> ingredientsInProgress;
	public transient Server server;
	public Stock(Server server) {
		dishesInProgress = new ArrayList<Dish>();
		ingredientsInProgress = new ArrayList<Ingredient>();
		this.server = server;
	}
	
	public void resetLists() {
		dishesInProgress = new ArrayList<Dish>();
		ingredientsInProgress = new ArrayList<Ingredient>();
	}
	
	public void addDishToStockControl(Dish d) {
		stockDish.put(d, 0);
	}
	
	public void addIngredientToStockControl(Ingredient i) {
		stockIngredient.put(i, 0);
	}
	
	public void removeIngredientFromStockControl(Ingredient i) {
		stockIngredient.remove(i);
	}
	
	public void removeDishFromStockControl(Dish d) {
		stockDish.remove(d);
	}
	
	
	public synchronized Map<Ingredient, Number> getIngredientsStock() {
		return stockIngredient;
	}
	
	public synchronized Map<Dish, Number> getDishStock() {
		return stockDish;
	}
	
	public synchronized Ingredient checkStockIngredient(Drone d) {
		if(server.getRestockIngredients()) {
			for(Ingredient i : getIngredientsStock().keySet()) {
				if((int)(getIngredientsStock().get(i)) < (int)(i.getRestockThreshold()) && !(ingredientsInProgress.contains(i))){
					ingredientsInProgress.add(i);
					return i;
				}
			}
		}
		return null;
	}

	
	public boolean restock(Ingredient i,Drone d) {
		setStock(i, (int)getIngredientsStock().get(i)+(int)i.getRestockAmount());
		ingredientsInProgress.remove(i);
		return true;
	}
	
	public synchronized Dish checkStockDish(Staff s) {
		if(server.getRestockDishes()) {
			for(Dish i : stockDish.keySet()) {
				if((int)(stockDish.get(i)) + (getInstancesOf(i)*(int)i.getRestockAmount())< (int)(i.getRestockThreshold())){
				if(canMakeDish(i,s)) {return i;}
				}
			}
		}
		return null;
	}
	
	public synchronized boolean canMakeDish(Dish dish, Staff s) {
		try {
		Dish d = getDishFromName(dish.getName());
		for(Ingredient i : d.getRecipe().keySet()) {
			if((int)d.getRecipe().get(i)*(int)d.getRestockAmount() <= (int)getIngredientsStock().get(i)) {
			}
			else {
				return false;
			}
		}
		dishesInProgress.add(d);
		for(Ingredient i : d.getRecipe().keySet()) {
			if((int)d.getRecipe().get(i) <= (int)getIngredientsStock().get(i)) {
				setStock(i,(int)getIngredientsStock().get(i) - (int)d.getRecipe().get(i)*(int)d.getRestockAmount());
			}
		}
		return true;
		}
		catch(NullPointerException e) {
			return false;	
		}
	}
	
	public void makeDish(Dish dish) {
		Dish d = getDishFromName(dish.getName());
			setStock(d, (int)stockDish.get(d) + (int)d.getRestockAmount());
			dishesInProgress.remove(d);
		}
	
	
	
	public synchronized void setStock(Ingredient i, Number n) {
		stockIngredient.remove(i);
		stockIngredient.put(i, n);
		server.per.updateObject(this);
	}
	
	public synchronized void setStock(Dish d, Number n) {
		stockDish.remove(d);
		stockDish.put(d, n);
		server.per.updateObject(this);
		
	}
	
	public synchronized Order flyOrders(Drone drone) {
		try {
		for(Order order: server.getOrders()) {
			if(order.getStatus().equals("Pending")) {
				for(Dish d: order.getBasketOrder().keySet()) {
							if(!server.getDishes().contains(getDishFromName(d.getName()))) {
								return null;
							}
							if((int)order.getBasketOrder().get(d) > (int)stockDish.get(getDishFromName(d.getName()))) {
								order.getBasketOrder().replace(d, (int)order.getBasketOrder().get(d) - (int)stockDish.get(getDishFromName(d.getName())));
								setStock(getDishFromName(d.getName()), 0);
								return null;
							}
							
							if((int)order.getBasketOrder().get(d) <= (int)stockDish.get(getDishFromName(d.getName()))) {
								setStock(getDishFromName(d.getName()), (int)stockDish.get(getDishFromName(d.getName())) -(int)order.getBasketOrder().get(d));
								order.getBasketOrder().replace(d, 0);
							}
						}
			for(Dish d: order.getBasketOrder().keySet()) {
				setStock(getDishFromName(d.getName()), (int)stockDish.get(getDishFromName(d.getName())) - (int)order.getBasketOrder().get(d));
			}
			server.updateOrderStatus(order, "In Progress");
			drone.setStatus("Flying order");
			drone.setDestination(order.getUser().getPostcode());
				return order;
				}
			}
		}
		catch(NullPointerException e) {
			return null;
		}
		
		return null;
	}
	
	public void OrderFinished(Order order) {
		server.updateOrderStatus(order, "Completed");
	}
	
	public Dish getDishFromName(String s) {
		for(Dish d :server.getDishes()){
			if(d.getName().equals(s)){
				return d;
			}
		}
		return null;
	}
	
	
	public int getInstancesOf(Object o) {
		int count = 0;
		if(o instanceof Dish) {
			for(Dish d : dishesInProgress) {
				if(d.getName().equals(((Dish) o).getName())) {
					count++;
				}
			}
		}
		return count;
		
	}
	
	
	

}
