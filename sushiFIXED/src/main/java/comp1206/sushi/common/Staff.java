package comp1206.sushi.common;

import java.util.Random;

import comp1206.sushi.common.Staff;

public class Staff extends Model implements Runnable,java.io.Serializable {

	private static final long serialVersionUID = -8154439140351231158L;
	private String name;
	private String status;
	private Number fatigue;
	private Stock stock;
	private boolean alive;
	public Staff(String name, Stock stock) {
		this.stock = stock;
		this.setName(name);
		this.setFatigue(0);
		this.setStatus("Idle");
		alive = true;
		Thread thread = new Thread(this);
		thread.start();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
	}

	public String getStatus() {
		return status;
	}	

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
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
		
		Dish d = stock.checkStockDish(this);
		if(d != null) {
			this.setStatus("Cooking");
			System.out.println(this.getName()+" is making dish "+ d.getName());
			try {
				Random random = new Random();
				int low = 20000;
				int high = 60000;
				int result = random.nextInt(high-low) + low;
				Thread.sleep(result);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stock.makeDish(d);
			System.out.println(this.getName()+ " has finished making "+ d.getName());
			this.setStatus("Idle");
		}
	}
	}

}
