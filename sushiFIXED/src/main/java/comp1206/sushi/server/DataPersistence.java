package comp1206.sushi.server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.Staff;
import comp1206.sushi.common.Stock;
import comp1206.sushi.common.Supplier;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

public class DataPersistence {
	Server server;
	String filename="Persistance.txt";
	CopyOnWriteArrayList<Object> listOfEverything;
    public DataPersistence(Server server) {
    	this.server = server;
    	listOfEverything = new CopyOnWriteArrayList<Object>();
    }
 
    public void WriteObjectToFile(Object serObj) {
        try {
        	listOfEverything.add(serObj);
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(listOfEverything);
            objectOut.close();
 
        } catch (FileNotFoundException ex) {
        	ex.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void removeObjectFromFile(Object serObj) {
        try {
         	for(Object o : listOfEverything) {
         		if(o.equals(serObj)) {
         			listOfEverything.remove(serObj);
         		}
         	}
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(listOfEverything);
            objectOut.close();
 
        } catch (FileNotFoundException ex) {
        	ex.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void removeStaff(Staff f) {
    	try {
    	for(Object o : listOfEverything) {
    		if(o instanceof Staff) {
    			if(((Staff) o).getName().equals(f.getName())) {
    				listOfEverything.remove(o);
    			}
    		}
    	}
    	FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(listOfEverything);
        objectOut.close();

    } catch (FileNotFoundException ex) {
    	ex.printStackTrace();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	
    }
    public void removeDrone(Drone d) {
    	try {
    	for(Object o : listOfEverything) {
    		if(o instanceof Drone) {
    			if(((Drone) o).getName().equals(d.getName())) {
    				listOfEverything.remove(o);
    			}
    		}
    	}
    	FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(listOfEverything);
        objectOut.close();

    } catch (FileNotFoundException ex) {
    	ex.printStackTrace();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	
    }
    
    public boolean ReadObjectFromFile() {
        try {
        	ArrayList<Drone> d = new ArrayList<Drone>();
        	ArrayList<Staff> f = new ArrayList<Staff>();
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            CopyOnWriteArrayList object = (CopyOnWriteArrayList) objectIn.readObject();
            	listOfEverything = new CopyOnWriteArrayList<Object> (object);
            	for(Object obj : listOfEverything) {
            		System.out.println(obj);
                	if(obj instanceof Restaurant) {
                		server.addRest((Restaurant) obj);
                	}
                	else if(obj instanceof Dish) {
                		boolean add = true;
                		for(Dish dish : server.getDishes()) {
                			if(dish.getName().equals(((Dish) obj).getName())) {
                				add= false;
                			}
                		}
                		if(add) {server.addDish((Dish) obj, false);}
                	}
                	else if(obj instanceof Drone) {
                		d.add((Drone)obj);
                	}
                	else if(obj instanceof Ingredient) {
                		server.addIngredient((Ingredient)obj);
                	}
                	else if(obj instanceof Order) {
                		server.addOrder((Order) obj, false);
                	}
                	else if(obj instanceof Staff) {
                		f.add((Staff)obj);
                	}
                	else if(obj instanceof Supplier) {
                		server.addSupplier((Supplier)obj);
                	}
                	else if(obj instanceof User) {
                		server.addUser((User)obj,false);
                	}
                	else if(obj instanceof Postcode) {
                		server.addPostcode((Postcode)obj);
                	}
                	else if(obj instanceof Stock) {
                		server.s = (Stock) obj;
                		server.s.server = server;
                		server.s.resetLists();
                	}
                	  

            }
                objectIn.close();
                for(Drone obj: d) {
                	server.addDrone((Drone) new Drone (((Drone) obj).getSpeed(), server.s, server.restaurant));
                }
                for(Staff obj: f) {
                	server.addStaff((Staff) new Staff(((Staff) obj).getName(), server.s));
                }

 
        } catch (FileNotFoundException ex) {
           System.out.println("No file to read from");
           File file = new File("Persistance.txt");
           try {
			file.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
           
        } catch (IOException e) 
        {e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    public void clearList() {
    	listOfEverything = new CopyOnWriteArrayList<Object>();
    	try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(listOfEverything);
            objectOut.close();
 
        } catch (FileNotFoundException ex) {
        	ex.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void updateObject(Object obj) {
    	
    	 try {
         	for(Object o : listOfEverything) {
         		if(o.equals(obj)) {
         			listOfEverything.remove(obj);
         		}
         	}
         	listOfEverything.add(obj);
             FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
             objectOut.writeObject(listOfEverything);
             objectOut.close();
  
         } catch (FileNotFoundException ex) {
         	ex.printStackTrace();
         } catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    }
}


