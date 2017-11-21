package market.data_retrieval;

public class Stock {

	public String symbol;
	public double current_price;
	public String industry;
	public double pe;
	public double forward_pe;
	public double eps;
	public double beta;
	public double peg;
	public double annual_eps_estimate;
	public double mean_recommendation;

	/**
	 * Data Points 1. Symbol 2. Current price 3. Industry 4. P/E Ratio 5. Forward
	 * P/E Ratio 6. EPS 7. Beta 8. PEG ratio 9. Annual EPS estimate 10. Mean
	 * Recommendation (5 - 1) (5 = bearish, 1 = bullish)
	 */
	public Stock(String symbol, double current_price, String industry, double pe, double forward_pe, double eps,
			double beta, double peg, double annual_eps_estimate, double mean_recommendation) {
		this.symbol = symbol;
		this.current_price = current_price;
		this.industry = industry;
		this.pe = pe;
		this.forward_pe = forward_pe;
		this.eps = eps;
		this.beta = beta; 
		this.peg = peg;
		this.annual_eps_estimate = annual_eps_estimate;
		this.mean_recommendation = mean_recommendation;
	}
	
	@Override
	public String toString() {
		return "STOCK: " + symbol + " in industry: " + industry + "\n"
				+ "Price: " + current_price + " with recommendation of " + mean_recommendation + "\n"
				+ "Data: PE: " + pe + " FORWARD PE: " + forward_pe + " EPS: " + eps + " PEG: " + peg + "\n"
				+ "Beta: " + beta + " Annual EPS: " + annual_eps_estimate + "\n"; 
	}

}
