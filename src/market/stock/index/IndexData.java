package market.stock.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IndexData {
	private static final File nasdaq_hashmap_date_to_price = new File("src/market/stock/index/data/nasdaq.txt");
	private static final File s_p500_hashmap_date_to_price = new File("src/market/stock/index/data/s+p500.txt");
	private static final File dow_jones_hashmap_date_to_price = new File("src/market/stock/index/data/dow_jones.txt");

	/**
	 * Have to check if we already have this data (if it should be updated) Call the
	 * update method when we update the data in Data.java for sectors/subsectors
	 */

	public static Map<Date, Double> nasdaq;
	public static Map<Date, Double> s_and_p500;
	public static Map<Date, Double> dow_jones;
	static {
		nasdaq = new HashMap<>();
		try {
			nasdaq = getMapFromFile(nasdaq_hashmap_date_to_price);
			s_and_p500 = getMapFromFile(s_p500_hashmap_date_to_price);
			dow_jones = getMapFromFile(dow_jones_hashmap_date_to_price);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<Date, Double> getMapFromFile(File file) throws Exception {
		Map<Date, Double> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<Date, Double>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<Date, Double> obtained from file -> indicies");
		return e;
	}

	public static void storeMapFile(File f, Map<Date, Double> m) throws Exception {
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f, false);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(m);
		oos.close();
		fos.close();
		System.out.println("Map<Date, Double> stored -> INDEX" + f);
	}

	public static void storeIndices() {
		try {
			storeMapFile(nasdaq_hashmap_date_to_price, nasdaq);
			storeMapFile(s_p500_hashmap_date_to_price, s_and_p500);
			storeMapFile(dow_jones_hashmap_date_to_price, dow_jones);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateIndices() {
		update(nasdaq_hashmap_date_to_price, "https://stooq.com/q/d/l/?s=^ndq", nasdaq);
		update(s_p500_hashmap_date_to_price, "https://stooq.com/q/d/l/?s=^spx&i=d", s_and_p500);
		update(dow_jones_hashmap_date_to_price, "https://stooq.com/q/d/l/?s=^dji&i=d", dow_jones);
	}

	private static void update(File file, String url, Map<Date, Double> m) {
		// UPDATE FILE WITH URL OF INDEX (STORE WITH <Date, Double> // price correlating with a date
		try {
			URL urlIndex = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(urlIndex.openStream()));
			ArrayList<String[]> data = new ArrayList<>();
			String x = null;
			while ((x = in.readLine()) != null) {
				data.add(x.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
		} catch (Exception e) {
			System.out.println("FAILED LIFE");
		}
	}

}
