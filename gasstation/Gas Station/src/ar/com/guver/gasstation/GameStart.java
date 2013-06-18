package ar.com.guver.gasstation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;

public class GameStart {

	
	//Ask for size of queue of cars -> Generate BlockingBoundedQueue, this is not implemented.
	
	//Ask for amount of gas in each pump, and price of each gas type -> Create pumps
	
	public static void main()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BlockingQueue<GasRequest> customerQueue = new LinkedBlockingQueue<GasRequest>();
		RealGasStation myGasStation = new RealGasStation(customerQueue);
		boolean inputCorrect = false;
		for (GasType type : GasType.values())
		{
			double amount = 0.0;
			//Capture the size of the pump
			while(inputCorrect == false)
			{
				inputCorrect = true;
				System.out.format("How big would you want your pump of %s to be (please format as number.number):", type.toString());	
				try {
					String amountString = in.readLine();
					amount = Double.parseDouble(amountString);	
				} catch (NumberFormatException e) {
					System.out.println("The format is wrong");
					inputCorrect = false;
				} catch (IOException e) {
					// Some problem with IO, let's try again
					e.printStackTrace();
					inputCorrect = false;
				}
			}
			GasPump pump = new GasPump(type, amount);
			myGasStation.addGasPump(pump);
			
			double price = 0.0;
			//Capture the price of that type of gas
			System.out.println("Customers will use the Market average value as indicator");
			System.out.println("Regular 1.564");
			System.out.println("Super 1.574");
			System.out.println("Diesel 1.375 ");
			inputCorrect = false;
			while(inputCorrect == false)
			{
				inputCorrect = true;
				System.out.format("What is the price of your %s (please format as number.number):", type.toString());	
				try {
					String priceString = in.readLine();
					price = Double.parseDouble(priceString);	
				} catch (NumberFormatException e) {
					System.out.println("The format is wrong");
					inputCorrect = false;
				} catch (IOException e) {
					// Some problem with IO, let's try again
					e.printStackTrace();
					inputCorrect = false;
				}
			}
			myGasStation.setPrice(type, price);
		}
		
		inputCorrect = false;
		long duration = 0;
		while(inputCorrect == false)
		{
			inputCorrect = true;
			System.out.println("How long whould you want the simulation to run (in minutes):");	
			try {
				String durationString = in.readLine();
				duration = Long.parseLong(durationString);	
			} catch (NumberFormatException e) {
				System.out.println("The format is wrong");
				inputCorrect = false;
			} catch (IOException e) {
				// Some problem with IO, let's try again
				e.printStackTrace();
				inputCorrect = false;
			}
		}
		
		//Generate a thread for gasstation consumer.
		//Generate a thread for customer requests producer.
		ExecutorService customerProducer = Executors.newSingleThreadExecutor();
		Runnable customerProducerRunnable = new CustomerProducer(customerQueue, duration);
		customerProducer.submit(customerProducerRunnable);
		
		ExecutorService gasStationConsumer = Executors.newSingleThreadExecutor();
		gasStationConsumer.submit(myGasStation);
		
		customerProducer.shutdown();
		try {
			customerProducer.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			customerProducer.shutdownNow();
		}
		
		
	}
	
	
	
	
}
