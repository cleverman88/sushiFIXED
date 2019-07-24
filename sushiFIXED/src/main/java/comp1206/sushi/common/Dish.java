package comp1206.sushi.common;

import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;

public class Dish extends Model implements java.io.Serializable {

	private String name;
	private String description;
	private Number price;
	private Map <Ingredient,Number> recipe;
	private Number restockThreshold;
	private Number restockAmount;

	public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.restockThreshold = restockThreshold;
		this.restockAmount = restockAmount;
		this.recipe = new HashMap<Ingredient,Number>();
	}
	/**
	 * Gets the dishes name
	 * @return The dishes name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of the dish
	 * @param The name that you wish to give the dish
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Gets the description of the dish
	 * @return The dishes description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Sets the description of the dish
	 * @param The description of the dish
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Gets the price of the dish
	 * @return Gets the price of the dish
	 */
	public Number getPrice() {
		return price;
	}
	/**
	 * Sets the price of the dish
	 * @param The price of the dish
	 */
	public void setPrice(Number price) {
		this.price = price;
	}
	/**
	 * Gets the recipe for the dish
	 * @return the recipe for that dish
	 */
	public Map <Ingredient,Number> getRecipe() {
		return recipe;
	}
	/**
	 * Sets the recipe for the dish
	 * @param The recipe map
	 */
	public void setRecipe(Map <Ingredient,Number> recipe) {
		this.recipe = recipe;
	}
	/**
	 * Sets the restockThreshold for the dish
	 * @param restockThreshold
	 */
	public void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}
	/**
	 * Sets the restockAmount for the dish
	 * @param restockAmount
	 */
	public void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}
	/**
	 * Gets the restockThreshold for a dish
	 * @return The restockThreshold
	 */
	public Number getRestockThreshold() {
		return this.restockThreshold;
	}
	/**
	 * Gets the restockAmount for a dish
	 * @return The restockAmount
	 */
	public Number getRestockAmount() {
		return this.restockAmount;
	}

}
