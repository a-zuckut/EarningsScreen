package market.stock.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import market.stock.functions.helper.Errors;

public class GetHistoricalPrices {

	private static final File prices = new File("src/market/stock/sector/data/historic_prices.txt");
	private static final String INIT_STRING_GOOGLE = "http://finance.google.com/finance/historical?q=";
	// https://stooq.com/q/d/l/?s=on.us&d1=20160323&d2=20160610
//	private static final String INIT_STRING_STOOQ = "https://stooq.com/q/d/l/?s=";

	public static Map<String, Map<String, Double>> data;
	static {
		data = new HashMap<>();
		try {
			data = getMapFromFile(prices);
		} catch (Exception e) {
		}
	}
	
	public static String construct_uri(String stock, Date d1, Date d2) {
		SimpleDateFormat format_google = new SimpleDateFormat("MMM+dd+yyyy");
		String i = format_google.format(adjust(d1, -7));
		String f = format_google.format(adjust(d2, 7));
		return INIT_STRING_GOOGLE + stock + "&startdate=" + i + "&enddate="
				+ f + "&output=csv";
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Map<String, Double>> getMapFromFile(File file) throws Exception {
		Map<String, Map<String, Double>> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<String, Map<String, Double>>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<String,Double> obtained from file");
		return e;
	}

	public static void storeMapFile() throws Exception {
		prices.createNewFile();
		FileOutputStream fos = new FileOutputStream(prices, false);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(data);
		oos.close();
		fos.close();
		System.out.println("Map<String, Map<String,Double> stored -> PRICES");
	}

	// increase data points a bit + 7 in both directions
	public static Map<String, Double> getPricesAccordingToDates(String stock, Date d1, Date d2) {
//		SimpleDateFormat format_stooq = new SimpleDateFormat("yyyyMMdd");
		
//		String i = format_stooq.format(adjust(d1, -7));
//		String f = format_stooq.format(adjust(d2, 7));
//		String uri = INIT_STRING_STOOQ + stock + ".us&d1=" + i + "&d2=" + f;
		String uri = construct_uri(stock, d1, d2); // google
		try {
			if (data.containsKey(uri)) {
				System.out.println("Got " + stock + " data from file");
				return data.get(uri);
			}

			URL url = new URL(uri);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			ArrayList<String> in = new ArrayList<>();
			String XXX = br.readLine();
			while ((XXX = br.readLine()) != null) {
				in.add(XXX);
			}

			Map<String, Double> ret = new HashMap<>();
			for (String d : in) {
				String[] split = d.split(",");
				ret.put(split[0], Double.parseDouble(split[split.length - 2]));
			}
			data.put(uri, ret);
			return ret;
		} catch (MalformedURLException e) {
			System.out.println(uri);
			Errors.errors.add(e.toString());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println(uri);
			Errors.errors.add(e.toString());
			e.printStackTrace();
			return null; // error return null
		}
	}

	private static Date adjust(Date d1, int w) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d1);
		calendar.add(Calendar.DATE, w);
		return calendar.getTime();
	}
	
	public static Double[] getPricesAccordingToDates(Map<String, Double> data, String STOCK, Date[] obt) {
		if (data == null)
			return null;
		if (data.size() < obt.length)
			return null;

		// KEYSET IN TERMS OF: 1-Oct-17 // SO dd-MMM-yy
		SimpleDateFormat format = new SimpleDateFormat("d-MMM-yy");
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		Double[] ret = new Double[obt.length];
		for (int i = 0; i < obt.length; i++) {
			Date date = obt[i];
			ret[i] = data.get(format.format(date));
			int incr = -1;
			while (ret[i] == null) {
				// GOOD IMPROVEMENT - FIND THE NEAREST DATE FROM THE data MAP THAT EXISTS
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, incr);
				date = c.getTime();
				obt[i] = date;
				ret[i] = data.get(format.format(date));
			}
		}

		return ret;
	}

	public static Double[] getPricesAccordingToDates(String STOCK, Date[] obt) {
		Map<String, Double> data = getPricesAccordingToDates(STOCK, obt[0], obt[obt.length - 1]);
		return getPricesAccordingToDates(data, STOCK, obt);

	}

}
