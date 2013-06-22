package ar.com.guver.gasstation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;

public class GameStart {

	private final static short MAXIMUMRECORDSTOSTORE = 5;
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
			if (type == GasType.REGULAR) {System.out.println("Regular 1.564");}
			if (type == GasType.SUPER) {System.out.println("Super 1.574");}
			if (type == GasType.DIESEL) {System.out.println("Diesel 1.375 ");}
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
		System.out.printf("Total revenue per minute is: %f \n", (myGasStation.getRevenue() / duration));
		System.out.printf("Total number of sales are: %d \n", myGasStation.getNumberOfSales());
		System.out.printf("Total cancellations because of no gas are: %d \n", myGasStation.getNumberOfCancellationsNoGas());
		System.out.printf("Total number of cancellation because of gas too expensive: %d \n", myGasStation.getNumberOfCancellationsTooExpensive());
		checkForRecord(myGasStation.getRevenue() / duration);
		gasStationConsumer.interrupt();
		return;
	}

	private static void checkForRecord(double possibleRecordRevenue) {
		// Checks CSV file for record, if the revenue per minute is better than current record then ask for name and save it.
		
		BufferedReader br = null;
		String line = "";
		String comma = ",";
		ArrayList<GameRecord> recordArray = new ArrayList<GameRecord>(MAXIMUMRECORDSTOSTORE); //Decreasing array with records.
		File csvFile = new File("./records.csv");
		try {
			csvFile.createNewFile();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] recordRow = line.split(comma);
				System.out.println("Name " + recordRow[0] + ", revenue=" + recordRow[1]);
				recordArray.add(new GameRecord(Double.parseDouble(recordRow[0]), recordRow[1])); //put all records in descending order
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		boolean rewriteFile = false;
		for (int i=0; i < recordArray.size() || recordArray.size() == 0; i++)
		{
			if (recordArray.size() == 0 || recordArray.get(i).getRecordRevenue() < possibleRecordRevenue) //We got a record! continue...
			{
				try {
					System.out.print("You got a new record! Please input your name: ");
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
					String nameString = in.readLine();
					if (recordArray.size()==MAXIMUMRECORDSTOSTORE) //We have all records occupied, remove the last one.
					{
						recordArray.remove(MAXIMUMRECORDSTOSTORE);
					}
					recordArray.add(i, new GameRecord(possibleRecordRevenue, nameString));
					rewriteFile = true; //Mark rewrite needed
					in.close();
					break; //Stop loopíng
				} catch (IOException e) {
					// Some problem with IO
					e.printStackTrace();
				}
			}
		}
		if (rewriteFile)
		{
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
				for (int i=0; i < recordArray.size(); i++)
				{
					bw.write(Double.toString(recordArray.get(i).getRecordRevenue()) + comma + recordArray.get(i).getName() + "\n");
					System.out.println(Integer.toString(i+1) + ": " + recordArray.get(i).getName() + " with a revenue of " + recordArray.get(i).getRecordRevenue());
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
