package comp1206.sushi.common;

import java.util.Random;

import comp1206.sushi.common.Drone;

public class Drone extends Model implements Runnable ,java.io.Serializable{


	private static final long serialVersionUID = 605334039430055173L;
	private Number speed;
	private Number progress;
	private Number capacity;
	private Number battery;
	private String status;
	private Postcode source;
	private Postcode destination;
	private Stock stock;
	private boolean alive;
	private Restaurant restaurant;
	
	
	public Drone(Number speed,Stock stock,Restaurant restaurant) {
		this.setSpeed(speed);
		this.setStatus("Idle");
		this.setCapacity(1);
		this.setBattery(100);
		this.stock = stock;
		this.alive = true;
		this.restaurant = restaurant;
		this.setSource(restaurant.getLocation());
		Thread thread = new Thread(this);
		thread.start();
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		return progress;
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	
	public long waitTime(Postcode p) {
		long wait = (long) (((double)p.getDistance()*1000)/ (int)this.getSpeed());
		
		return wait*1000*2;
	}
	
	public void kill() {
		this.alive = false;
	}
	
	@Override
	public void run() {
		while(alive) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Ingredient temp2 = stock.checkStockIngredient(this);
			while(temp2 != null) {
				this.setDestination(temp2.getSupplier().getPostcode());
				System.out.println("Drone "+ this.getName()+" is restocking "+ temp2.getRestockAmount()+" of "+temp2.getName());
				this.setStatus("Flying to supplier");
				try {
					for(int i = 0; i <=100; i++)  {
						Thread.sleep((long) (waitTime(this.getDestination())/100));
						if(i == 50) {
							this.setStatus("Returning to restaraunt");
						}
						this.setProgress(i);
					}
					this.setSource(restaurant.getLocation());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stock.restock(temp2, this);
				System.out.println(this.getName()+ " has finished restocking "+ temp2.getName());
				this.setProgress(null);
				this.setStatus("Idle");
				temp2 = stock.checkStockIngredient(this);
			}
			Order temp = stock.flyOrders(this);
			if(temp != null) {
				try {
					this.setDestination(temp.getUser().getPostcode());
					for(int i = 0; i <=100; i++)  {
						Thread.sleep((long) (waitTime(this.getDestination())/100));
						if(i == 50) {
							this.setStatus("Returning to restaraunt");
						}
						this.setProgress(i);
					}
					this.setSource(restaurant.getLocation());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(this.getName()+ " has finished delivering order name: "+temp.getName()+" to "+ temp.getUser().getName());
				stock.OrderFinished(temp);
				this.setProgress(null);
				this.setDestination(null);
				this.setStatus("Idle");
			}
		}
		}

}
