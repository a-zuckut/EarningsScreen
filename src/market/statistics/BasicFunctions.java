package market.statistics;

import java.util.ArrayList;
import java.util.Date;

import market.stock.index.IndexData;

public class BasicFunctions {

	/**
	 * Given dates d and prices p for those dates, return the returns per date -> date
	 * @param d list of dates
	 * @param p list of prices correlating with dates
	 * @return Array of percentages from d_1-d_0 to d_n-d_n-1, so size n-1, null if error
	 */
	public static double[] determinePerDateReturns(Date[] d, Double[] p) {
		if(p == null) return null;
		double[] ret = new double[p.length - 1];
		
		for(int i = 1; i < p.length; i++) {
			ret[i-1] = (p[i]/p[i-1] - 1) - 1;
		}
		
		return ret;
	}
	
	public static double[] determinePerDateReturnsMinusIndexReturns(Date[] d, Double[] p) {
		if(p == null) return null;
		double[] ret = new double[p.length - 1];
		
		for(int i = 1; i < p.length; i++) {
			ret[i-1] = (p[i]/p[i-1] - 1) - (indices(d[i])/indices(d[i-1]) - 1);
		}
		
		return ret;
	}
	
	public static double indices(Date date) {
		// Gets data from IndexData.class
//		double price = IndexData.dow_jones.get(date);
//		double price3 = IndexData.nasdaq.get(date);
		double price2 = IndexData.s_and_p500.get(date);
		
		return price2;
	}

	/**
	 * d, p MUST BE LENGTH >= 1
	 */
	public static double[] determineCumulativeReturns(Date[] d, Double[] p) {
		if(p == null) return null;
		double[] ret = new double[p.length - 1];
		
		for(int i = 1; i < p.length; i++) {
			ret[i-1] = p[i]/p[0] - 1;
		}
		
		return ret;
	}

	public static double[] printReturnsPerTimePeriod(Double[] d, Date[] dates) {
		double[] returns = determinePerDateReturns(dates, d);
		return returns;
	}
	
	public static double[] printReturnsCumulativePeriod(Double[] d, Date[] dates) {
		double[] returns = determineCumulativeReturns(dates, d);
		return returns;
	}
	
	public static String toStringDoubleArrayReturns(double[] returns) {
		String x = "";
		for(double i : returns) {
			x += String.format("%8.2f\t", i*100);
		}
		return x;
	}
	
	public static String toStringDoubleArrayPrices(double[] prices) {
		String x = "";
		for(double i : prices) {
			x += String.format("%8.2f\t", i);
		}
		return x;
	}

	/**
	 * @requires returnsAL.length >= 1
	 * @param returnsAL
	 * @return
	 */
	public static double[] averageArrayListOfDoubles(ArrayList<double[]> returnsAL) {
		
		int size = returnsAL.size();
		double[] average = new double[returnsAL.get(0).length];
		
		for(double[] x : returnsAL) {
			int index = 0;
			for(double d:x) {
				average[index++] += d;
			}
		}
		
		for(int i = 0; i < average.length; i++) {
			average[i] = (average[i]/(double) size);
		}
		
		return average;
	}
	
}
