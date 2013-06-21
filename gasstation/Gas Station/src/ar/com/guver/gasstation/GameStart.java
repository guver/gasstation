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
	
	public static void main(String args[])
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BlockingQueue<CustomerRequest> customerQueue = new LinkedBlockingQueue<CustomerRequest>(10);
		RealGasStation myGasStation = new RealGasStation(customerQueue);
		boolean inputCorrect = false;
		for (GasType type : GasType.values())
		{
			double amount = 0.0;
			//Capture the size of the pump
			inputCorrect = false;
			while(inputCorrect == false)
			{
				inputCorrect = true;
				System.out.printf("How big would you want your pump of %s to be (please format as number.number):", type.toString());	
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
				System.out.printf("What is the price of your %s (please format as number.number):", type.toString());	
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
		//ExecutorService customerProducer = Executors.newSingleThreadExecutor();
		
		Runnable customerProducerRunnable = new CustomerProducer(customerQueue, duration);
		Thread customerProducer = new Thread(customerProducerRunnable);
		customerProducer.start();
		
		//customerProducer.submit(customerProducerRunnable);
		
		//ExecutorService gasStationConsumer = Executors.newSingleThreadExecutor();
		Thread gasStationConsumer = new Thread(myGasStation);
		gasStationConsumer.start();
		
		//gasStationConsumer.submit(myGasStation);
		
		try {
			customerProducer.join();
		} catch (InterruptedException e) {
			customerProducer.interrupt();
		}
		
		System.out.printf("Total revenue is: %f \n", myGasStation.getRevenue());
		System.out.printf("Total number of sales are: %d \n", myGasStation.getNumberOfSales());
		System.out.printf("Total cancellations because of no gas are: %d \n", myGasStation.getNumberOfCancellationsNoGas());
		System.out.printf("Total number of cancellation because of gas too expensive: %d \n", myGasStation.getNumberOfCancellationsTooExpensive());
		gasStationConsumer.interrupt();
		return;
	}
}
