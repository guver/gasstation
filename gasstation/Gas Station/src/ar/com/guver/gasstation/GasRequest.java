package ar.com.guver.gasstation;

import java.util.concurrent.Callable;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasRequest implements Callable<Double>{
		
	//Amount
	//Maximum price to pay
	//Type of Gas
	private double amount;
	private double maxPrice;
	private GasType type;
	private GasPump pump;
	
	public GasRequest(GasType type, double amountInLiters,
			double maxPricePerLiter){
		this.amount = amountInLiters;
		this.type = type;
		this.maxPrice = maxPricePerLiter;
	}

	@Override
	public Double call() throws Exception {
		// Here we should talk to the gasPump to get the gas loaded
		synchronized (pump)
		{
			if (pump.getRemainingAmount() < amount) {
				throw new NotEnoughGasException();
			}
			else {
				pump.pumpGas(amount);
			}
		}
		return amount;
	}

	public GasType getType() {
		return type;
	}

	public void setPump(GasPump pump) {
		this.pump = pump;
	}
	
	public GasPump getPump()
	{
		return this.pump;
	}
	
	public double getMaxPrice() {
		return this.maxPrice;
	}
}
