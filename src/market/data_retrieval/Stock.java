package market.data_retrieval;

public class Stock {

	public String symbol;
	public String current_price;
	public String industry;
	public String pe;
	public String forward_pe;
	public String eps;
	public String beta;
	public String peg;
	public String annual_eps_estimate;
	public String mean_recommendation;

	/**
	 * Data Points 1. Symbol 2. Current price 3. Industry 4. P/E Ratio 5. Forward
	 * P/E Ratio 6. EPS 7. Beta 8. PEG ratio 9. Annual EPS estimate 10. Mean
	 * Recommendation (5 - 1) (5 = bearish, 1 = bullish)
	 */
	public Stock(String symbol, String current_price, String industry, String pe, String forward_pe, String eps,
			String peg, String annual_eps_estimate, String mean_recommendation) {
		this.symbol = symbol;
		this.current_price = current_price;
		this.industry = industry;
		this.pe = pe;
		this.forward_pe = forward_pe;
		this.eps = eps;
		this.peg = peg;
		this.annual_eps_estimate = annual_eps_estimate;
		this.mean_recommendation = mean_recommendation;
	}
	
	@Override
	public String toString() {
		return    "---------------------------------------------------------------------\n"
		 		+ "STOCK: " + symbol + " in industry: " + industry + "\n"
				+ "Price: " + current_price + " with recommendation of " + mean_recommendation + "\n"
				+ "Data: PE: " + pe + " FORWARD PE: " + forward_pe + " EPS: " + eps + " PEG: " + peg + "\n"
				+ "Annual EPS: " + annual_eps_estimate + "\n"
				+ "---------------------------------------------------------------------"; 
	}
	
	public static String getCsvHeaderLine() {
		return "SYMBOL, CURRENT PRICE, INDUSTRY, PE, FORWARD PE, EPS, PEG, EPS ESTIMATE, RECOMMENDATION";
	}
	
	public String getCsvLine() {
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
				symbol, current_price, industry, pe, forward_pe, eps, peg, annual_eps_estimate, mean_recommendation);
	}

}
