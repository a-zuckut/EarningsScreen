package market.data_retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import market.stock.functions.EarningsFinder;

public class StockData {

	// GET DATA FROM:
	// http://www.nasdaq.com/symbol/ + STOCK_SYMBOL (get symbols from
	// stock.earnings)

	private static Set<String> stock_list = null; //EarningsFinder.x.keySet();
	public static Set<Stock> stocks = new HashSet<>();

	// PUT ALL THIS DATA IN A FILE (update when you want to)

	public static Stock CollectStockData(String symbol) {
		String init = "https://old.nasdaq.com/symbol/";
		if (symbol != null) {
			String uri = init + symbol;
			System.out.println("Scraping for data: " + uri);
			StringBuilder sb = new StringBuilder();
			try {
				URL stockURL = new URL(uri);
				BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
				String tmp = "";
				while((tmp=in.readLine())!=null){
					sb.append(tmp);
				}
			} catch (Exception e) {
				System.out.println("Broken while reading website");
			}

			return RetrieveStockFromHtml(sb.toString());
		} else {
			for (String sym : stock_list) {
				String uri = init + sym;
				System.out.println(uri);
			}
		}

		return null;
	}

	/**
	 * TODO: 4. P/E Ratio 5. Forward P/E Ratio 6. EPS 7. Beta 8. PEG ratio 9. Annual
	 * EPS estimate 10. Mean Recommendation (5 - 1) (5 = bearish, 1 = bullish)
	 * 
	 * @param html
	 */
	private static Stock RetrieveStockFromHtml(String html) {

		int i = html.indexOf("<div class=\"symbol\">");
		int is = html.indexOf("https://old.nasdaq.com/symbol/", i);
		int tmp = html.indexOf("\">", is);
		int tmp2 = html.indexOf("</a", tmp);

		String symbol = html.substring(tmp + "\">".length(), tmp2);
		System.out.println("Symbol: " + symbol);

		int i1 = html.indexOf("<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">");
		int len = "<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">".length();
		int i2 = html.indexOf("</div>", i1);
		String Last_Price = html.substring(i1 + len + 1, i2); // + 1 to get rid of $
		// System.out.println("Last Price: " + Last_Price);

		int i3 = html.indexOf("<b>Industry: </b>");
		int i4 = html.indexOf("screening/companies-by-industry.aspx?industry=", i3);
		len = "screening/companies-by-industry.aspx?industry=".length();
		int i5 = html.indexOf("\">", i4);

		String industry = html.substring(i4 + len, i5);
		// System.out.println("Industry: " + industry);

		int i6 = html.indexOf("<b>P/E Ratio:");
		int i7 = html.indexOf("<span>", i6);
		int i8 = html.indexOf("</span>", i7);
		len = "<span>".length();

		String PE = html.substring(i7 + len, i8);
		// System.out.println("P/E: " + PE);

		int i9 = html.indexOf("<b>Forward P/E (1y):");
		int j1 = html.indexOf("<span>", i9);
		int j2 = html.indexOf("</span>", j1);
		len = "<span>".length();

		String FORWARD_PE = html.substring(j1 + len, j2);
		// System.out.println("ForwardPE: " + FORWARD_PE);

		int j3 = html.indexOf("Earnings Per Share (EPS)", j2);
		int j4 = html.indexOf("<span>", j3);
		len = "<span>".length();
		int j5 = html.indexOf("</span>", j4);

		String EPS = html.substring(j4 + len, j5).replace("&nbsp;", "").replace("$", "");
		// System.out.println("EPS: " + EPS);

		int j9 = html.indexOf("peg-ratio\">PEG Ratio:");
		int k1 = html.indexOf("<td>", j9);
		len = "<td>".length();
		int k2 = html.indexOf("</td>", k1);

		String PEG = html.substring(k1 + len, k2);
		// System.out.println("PEG: " + PEG);

		int k3 = html.indexOf("eps-forecast\">Annual EPS Est:");
		int k4 = html.indexOf("<td>", k3);
		len = "<td>".length();
		int k5 = html.indexOf("</td>", k4);

		String eps_forcast = html.substring(k4 + len, k5).replace("$", "");
		// System.out.println("EPS Forcast: " + eps_forcast);

		int k6 = html.indexOf("#MeanRec\">Mean Recommendation:");
		int k7 = html.indexOf("<td>", k6);
		len = "<td>".length();
		int k8 = html.indexOf("</td>", k7);

		String RECOMMENDATION = html.substring(k7 + len, k8);
		// System.out.println("Recommendation: " + RECOMMENDATION);

		return new Stock(symbol, Double.parseDouble(Last_Price), industry, Double.parseDouble(PE),
				Double.parseDouble(FORWARD_PE), Double.parseDouble(EPS),
				Double.parseDouble(PEG), Double.parseDouble(eps_forcast), Double.parseDouble(RECOMMENDATION));

	}

	public static void main(String[] args) throws Exception {
		// File f = new File("test.test");
		// BufferedReader br = new BufferedReader(new FileReader(f)); 
  
		// String str="";
		// String tmp;
		// while ((tmp = br.readLine()) != null) {
		// 	str += tmp;
		// }

		// Stock x = RetrieveStockFromHtml(str);
		// System.out.println(x);

		System.out.println(CollectStockData("AAPL"));
	}
}
