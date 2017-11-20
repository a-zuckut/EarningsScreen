package market.stock.earnings;

import java.util.Date;

public class Earning {

	public String company_name;
	public Date reported_date;
	public Quarter FiscalQuarterEnding;
	public int number_of_estimates;
	public double EPS_ESTIMATE;
	public double EPS_ACTUAL;
	public double surprise;
	
	public String symbol;
	
	public Earning() {
		company_name = "";
		reported_date = new Date();
		FiscalQuarterEnding = new Quarter();
		EPS_ESTIMATE = 0D;
		EPS_ACTUAL = 0D;
		surprise = 0D;
		number_of_estimates = 0;
	}
	
	public Earning(String name, Date date, String quarter, double estimate, double actual, int num) {
		company_name = name;
		reported_date = date;
		FiscalQuarterEnding = parseQuarter(quarter);
		EPS_ESTIMATE = estimate;
		EPS_ACTUAL = actual;
		number_of_estimates = num;
		if(estimate == 0) surprise = 0;
		else surprise = ((actual - estimate) / estimate) * 100;
	}

	private Quarter parseQuarter(String quarter) {
		String[] split = quarter.split(" ");
		Quarter q = new Quarter();
		q.month = split[0];
		q.year = Integer.valueOf(split[1]);
		return q;
	}
	
	@Override
	public String toString() {
		return symbol + " had earnings of " + EPS_ACTUAL + " which had surprise: " + String.format("%.2f", surprise);
	}
	
}
