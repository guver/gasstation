package ar.com.guver.gasstation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import net.bigpoint.assessment.gasstation.GasType;

public class CustomerProducer implements Runnable {

	private final BlockingQueue<GasRequest> customerQueue;
	long duration;
	
	public CustomerProducer (BlockingQueue<GasRequest> sharedQueue, long duration)
	{
		this.customerQueue = sharedQueue; 
		this.duration = duration * 1000;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		//Every certain X time generate a customer gas request and put it into the queue
		while (duration < startTime  - System.currentTimeMillis())
		{
			GasRequest randomRequest = generateRandomRequest();
			try {
				customerQueue.put(randomRequest);
				}
			catch (InterruptedException ex)
			{
				//do nothing since no cleanup needed. Just sleep for a bit and then try again.
			}
			
			//Wait random time for next customer to show up, between 1 to 10 seconds
			try {
				Thread.sleep (ThreadLocalRandom.current().nextInt(1000, 10000));
			} catch (InterruptedException ex) {
				// do nothing
			}
		}
	}

	private GasRequest generateRandomRequest() {
		// Generate a random gas amount, price to pay and type of gas wanted.
		
		//Gastype is calculated in random distribution. Given there are 3 types of gas
		//33% of customer will go into each type.
		GasType type = GasType.values()[ThreadLocalRandom.current().nextInt(0,GasType.values().length)];
		
		//Amount. Maximum gas in a car tank is around 70 liters. 
		//Nobody goes to the gas station for less than 5 liters. Average of gas load is 25-30 liters.
		//We use a pseudo Bell formula then, with min 5 max 70 and average of around 30.
		double amountInLiters = 5.0 + Math.sqrt(ThreadLocalRandom.current().nextDouble(0.0, 900.0)) + ThreadLocalRandom.current().nextDouble(0.0,30.0);
		
		//Maxprice. Customer will use the Market average value as indicator
		// Regular 1.564
		// Super 1.574 
		// Diesel 1.375 
		//Customer will start with a max price of Average value -5% and finish around average price +10%
		//Most customers will fall in the middle line, 
		//only extreme cases will pay less than average or much more than average
		double basePrice = 0;
				if (type == GasType.DIESEL) { basePrice = 1.375; }
				else if (type == GasType.SUPER) { basePrice = 1.574; }
				else if (type == GasType.REGULAR) { basePrice = 1.564; }
				else {
					//Wrong gas type
				}
		double middleLine = basePrice * 1.03;
		double extremes = basePrice * 0.08;
		double maxPricePerLiter = middleLine + ThreadLocalRandom.current().nextDouble((-1.0 * extremes),extremes);
		
		GasRequest request = new GasRequest(type, amountInLiters, maxPricePerLiter);
		
		return request;
	}
	
}
