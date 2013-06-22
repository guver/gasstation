package ar.com.guver.gasstation;

public class GameRecord {
	private double recordRevenue;
	private String name;
	
	public GameRecord(double recordRevenue, String name) {
		this.setRecordRevenue(recordRevenue);
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRecordRevenue() {
		return recordRevenue;
	}

	public void setRecordRevenue(double recordRevenue) {
		this.recordRevenue = recordRevenue;
	}
	
	
}
