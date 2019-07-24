package comp1206.sushi.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import comp1206.sushi.server.Server;

public class Configuration {
	private String name;
	private Server server;
	public Configuration(String name, Server server) {
		this.name = name;
		this.server = server;
		readFile();
	}
	
	//Loads the file, and given the name at the start adds that respective item to the server
	public void readFile() {
	        String line = null;
	        
	        try {
	            FileReader fileReader = 
	                new FileReader(name);

	            BufferedReader bufferedReader = 
	                new BufferedReader(fileReader);
	            while((line = bufferedReader.readLine()) != null) {
	            	switch(line.split(":")[0]) {
	            	case "POSTCODE":
	            		server.addPostcode(line.split(":")[1]);
	            		break;
	            	case "RESTAURANT":
	            		Postcode postcode = new Postcode(line.split(":")[2]);
	            		server.addRestaurant(new Restaurant(line.split(":")[1],postcode));
	            		break;
	            	case "SUPPLIER":
	            		Postcode p = new Postcode(line.split(":")[2]);
	            		server.addSupplier(line.split(":")[1],p);
	            		break;
	            	case "INGREDIENT":
	            		Supplier stemp = null;
	            		for(Supplier x : server.getSuppliers()) {
	            			if(x.getName().equals(line.split(":")[3])){
	            				stemp = x;
	            				break;
	            			}
	            		}
	            		server.addIngredient(line.split(":")[1],line.split(":")[2],stemp,Integer.parseInt(line.split(":")[4]),Integer.parseInt(line.split(":")[5]),Integer.parseInt(line.split(":")[6]));
	            		break;
	            	case "DISH":
	            		Dish d = new Dish(line.split(":")[1],line.split(":")[2],Integer.parseInt(line.split(":")[3]),Integer.parseInt(line.split(":")[4]),Integer.parseInt(line.split(":")[5]));
	            		for(int i = 0; i <= line.split(",").length-1; i++) {
	            			Ingredient mock = null;
	            			for(Ingredient ing : server.getIngredients()) {
	            				if(ing.getName().equals(line.split(":")[6].split(",")[i].split("\\*")[1].replaceAll(" ", ""))) {
	            					mock = ing;
	            					}
	            			}
            				server.addIngredientToDish(d,mock,Integer.parseInt(line.split(":")[6].split(",")[i].split("\\*")[0].replaceAll(" ", "")));
	            		}
	            		server.addDish(d);
	            		break;
	            	case "USER":
	            		server.addUser(new User(line.split(":")[1],line.split(":")[2],line.split(":")[3],new Postcode(line.split(":")[4])));
	            		break;
	            	case "ORDER":
	            		User mock = null;
	            		for(User o : server.getUsers()) {
	            			if(o.getName().equals(line.split(":")[1])) {
	            				mock = o;
	            			}
	            		}
	            		for(int i = 0; i <= line.split(":")[2].split(",").length-1; i++) {
	            			Dish mock2 = null;
	            			for(Dish ing : server.getDishes()) {
	            				if(ing.getName().replaceAll(" ", "").equals(line.split(":")[2].split(",")[i].split("\\*")[1].replaceAll(" ", ""))) {
	            					mock2 = ing;
	            				}
	            			}
            				mock.addToBasket(mock2,Integer.parseInt(line.split(":")[2].split(",")[i].split("\\*")[0].replaceAll(" ", "")));
	            		}
	            		mock.returnOrder().setBasketToOrder(mock.getBasket());
	            		//mock.addOrderToList(mock, mock.returnOrder());
	            		
	            		server.addOrder(mock.returnOrder());
	            		mock.createNewOrder();
	            		mock.clearBasket();
	            		break;
	            	case "STOCK":
	            		for(Dish s : server.getDishes()) {
	            			if(s.getName().equals(line.split(":")[1])) {
	            				server.setStock(s, Integer.parseInt(line.split(":")[2]));
	            			}
	            		}
	            		for(Ingredient s : server.getIngredients()) {
	            			if(s.getName().equals(line.split(":")[1])) {
	            				server.setStock(s, Integer.parseInt(line.split(":")[2]));
	            			}
	            		}
	            		break;
	            	case "STAFF":
	            		server.addStaff(line.split(":")[1]);
	            		break;
	            	case "DRONE":
	            		server.addDrone(Integer.parseInt(line.split(":")[1]));
	            		break;
	            	}
	                
	                
	            }   

	            bufferedReader.close();    
	            fileReader.close();
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                name + "'");                
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + name + "'");                  
	        }
	}
}
