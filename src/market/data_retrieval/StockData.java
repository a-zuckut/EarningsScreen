package market.data_retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StockData {

	// GET DATA FROM:
	// http://www.nasdaq.com/symbol/ + STOCK_SYMBOL (get symbols from stock.earnings)
	
//	private static Set<String> stock_list = EarningsFinder.x.keySet();
//	public static Set<Stock> stocks = new HashSet<>(); 
//	
//	// PUT ALL THIS DATA IN A FILE (update when you want to)
//	
//	public static void CollectStockData() {
//		String init = "http://www.nasdaq.com/symbol/";
//		for(String sym : stock_list) {
//			String uri = init + sym;
//			
//		}
//	}
	
	/**
	 * TODO:
	 *  4. P/E Ratio 5. Forward
	 * P/E Ratio 6. EPS 7. Beta 8. PEG ratio 9. Annual EPS estimate 10. Mean
	 * Recommendation (5 - 1) (5 = bearish, 1 = bullish)
	 * @param html
	 */
	private static void RetrieveStockFromHtml(String html) {
		
		int i = html.indexOf("<div class=\"qwidget-symbol\">");
		int leni = "<div class=\"qwidget-symbol\">".length();
		int is = html.indexOf("&nbsp;", i);
		
		String symbol = html.substring(i + leni, is);
		
		int i1 = html.indexOf("<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">");
		int length1 = "<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">".length();
		int i2 = html.indexOf("</div>", i1);	
		String Last_Price = html.substring(i1 + length1 + 1, i2); // + 1 to get rid of $
		
		int i3 = html.indexOf("<b>Industry: </b>");
		int i4 = html.indexOf("screening/companies-by-industry.aspx?industry=", i3);
		int len = "screening/companies-by-industry.aspx?industry=".length();
		int i5 = html.indexOf("\">", i4);
		
		String industry = html.substring(i4 + len, i5);
		
		// P/E Ratio
		
		// Forward P/E ratio
		
		// EPS
		
		// Beta
		
		// PEG
		
		// annual eps estimate
		
		// mean recommendation
		
		System.out.println(symbol + " " + Last_Price + " " + industry);
		
	}
	
	
	public static void main(String[] args) throws Exception {
		File f = new File("test.test");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String x = null, sol = "";
		while((x = reader.readLine()) != null) {
			sol+=x;
		}
		
		RetrieveStockFromHtml(sol);
	}
}
