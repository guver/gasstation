package ar.com.guver.gasstation;

import net.bigpoint.assessment.gasstation.GasType;

public class CustomerRequest {
		//Amount
		//Maximum price to pay
		//Type of Gas
		private double amount;
		private double maxPrice;
		private GasType type;
		public CustomerRequest(GasType type, double amountInLiters,
				double maxPricePerLiter) {
			this.type = type;
			amount = amountInLiters;
			maxPrice = maxPricePerLiter;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public double getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(double maxPrice) {
			this.maxPrice = maxPrice;
		}
		public GasType getType() {
			return type;
		}
		public void setType(GasType type) {
			this.type = type;
		}
}
