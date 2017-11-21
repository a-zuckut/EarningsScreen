package market.data_retrieval;

import java.util.HashSet;
import java.util.Set;

import market.stock.functions.EarningsFinder;

public class StockData {

	// GET DATA FROM:
	// http://www.nasdaq.com/symbol/ + STOCK_SYMBOL (get symbols from
	// stock.earnings)

	private static Set<String> stock_list = EarningsFinder.x.keySet();
	public static Set<Stock> stocks = new HashSet<>();

	// PUT ALL THIS DATA IN A FILE (update when you want to)

	public static void CollectStockData() {
		String init = "http://www.nasdaq.com/symbol/";
		for (String sym : stock_list) {
			String uri = init + sym;
			System.out.println(uri);
		}
	}

	/**
	 * TODO: 4. P/E Ratio 5. Forward P/E Ratio 6. EPS 7. Beta 8. PEG ratio 9. Annual
	 * EPS estimate 10. Mean Recommendation (5 - 1) (5 = bearish, 1 = bullish)
	 * 
	 * @param html
	 */
	private static Stock RetrieveStockFromHtml(String html) {

		int i = html.indexOf("<div class=\"qwidget-symbol\">");
		int leni = "<div class=\"qwidget-symbol\">".length();
		int is = html.indexOf("&nbsp;", i);

		String symbol = html.substring(i + leni, is);

		int i1 = html.indexOf("<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">");
		int len = "<div id=\"qwidget_lastsale\" class=\"qwidget-dollar\">".length();
		int i2 = html.indexOf("</div>", i1);
		String Last_Price = html.substring(i1 + len + 1, i2); // + 1 to get rid of $

		int i3 = html.indexOf("<b>Industry: </b>");
		int i4 = html.indexOf("screening/companies-by-industry.aspx?industry=", i3);
		len = "screening/companies-by-industry.aspx?industry=".length();
		int i5 = html.indexOf("\">", i4);

		String industry = html.substring(i4 + len, i5);

		int i6 = html.indexOf("P/E Ratio");
		len = "<td align=\"right\" nowrap>".length();
		int i7 = html.indexOf("<td align=\"right\" nowrap>", i6);
		int i8 = html.indexOf("</td>", i7);

		String PE = html.substring(i7 + len, i8);

		int i9 = html.indexOf("Forward P/E");
		int j1 = html.indexOf("<td align=\"right\" nowrap>", i9);
		len = "<td align=\"right\" nowrap>".length();
		int j2 = html.indexOf("</td>", j1);

		String FORWARD_PE = html.substring(j1 + len, j2);

		int j3 = html.indexOf("Earnings Per Share (EPS)");
		int j4 = html.indexOf("<td align=\"right\" nowrap>", j3);
		len = "<td align=\"right\" nowrap>".length();
		int j5 = html.indexOf("</td", j4);

		String EPS = html.substring(j4 + len, j5).replace("&nbsp;", "").replace("$", "");

		int j6 = html.indexOf(
				"\"Beta\" is a volatility measurement of a stock mutual fund or ETF versus a comparable benchmark like the S&P 500 stock index");
		int j7 = html.indexOf("<td align=\"right\" nowrap>", j6);
		len = "<td align=\"right\" nowrap>".length();
		int j8 = html.indexOf("</td>", j7);

		String Beta = html.substring(j7 + len, j8);

		int j9 = html.indexOf("peg-ratio\">PEG Ratio:");
		int k1 = html.indexOf("<td>", j9);
		len = "<td>".length();
		int k2 = html.indexOf("</td>", k1);

		String PEG = html.substring(k1 + len, k2);

		int k3 = html.indexOf("eps-forecast\">Annual EPS Est:");
		int k4 = html.indexOf("<td>", k3);
		len = "<td>".length();
		int k5 = html.indexOf("</td>", k4);

		String eps_forcast = html.substring(k4 + len, k5).replace("$", "");

		int k6 = html.indexOf("#MeanRec\">Mean Recommendation:");
		int k7 = html.indexOf("<td>", k6);
		len = "<td>".length();
		int k8 = html.indexOf("</td>", k7);

		String RECOMMENDATION = html.substring(k7 + len, k8);

		return new Stock(symbol, Double.parseDouble(Last_Price), industry, Double.parseDouble(PE),
				Double.parseDouble(FORWARD_PE), Double.parseDouble(EPS), Double.parseDouble(Beta),
				Double.parseDouble(PEG), Double.parseDouble(eps_forcast), Double.parseDouble(RECOMMENDATION));

	}

	public static void main(String[] args) throws Exception {
		CollectStockData();
	}
}
