package market.stock.functions.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import market.statistics.BasicFunctions;

public class DatePrices {

	private static final File date_to_price_around_earnings = new File("src/market/stock/sector/data/date_to_price.txt");
	
	public static Map<String, Map<Date[], Double[]>> DATE_TO_PRICE;
	static {
		DATE_TO_PRICE = new HashMap<>();
		try {
			DATE_TO_PRICE = getMapFromFile(date_to_price_around_earnings);
		} catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Map<Date[], Double[]>> getMapFromFile(File file) throws Exception {
		Map<String, Map<Date[], Double[]>> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<String, Map<Date[], Double[]>>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<String,Map<Date[], Double[]> obtained from file");
		return e;
	}
	
	public static void storeMapFile() throws Exception {
		date_to_price_around_earnings.createNewFile();
		FileOutputStream fos = new FileOutputStream(date_to_price_around_earnings, false);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(DATE_TO_PRICE);
		oos.close();
		fos.close();
		System.out.println("Map<String, Date[]> stored -> EARNINGS");
	}

	public static String printPrices() {
		String x = "";
		
		for(Map<Date[], Double[]> m : DATE_TO_PRICE.values()) {
			for(Double[] d : m.values()) {
				if(d != null)
					x += BasicFunctions.toStringDoubleArrayPrices(Stream.of(d).mapToDouble(Double::doubleValue).toArray()) + "\n";
			}
		}
		
		return x;
	}
	
}
