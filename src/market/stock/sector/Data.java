package market.stock.sector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import market.stock.functions.EarningsFinder;
import market.stock.functions.GetHistoricalPrices;
import market.stock.functions.helper.DateObtainer;
import market.stock.functions.helper.DatePrices;
import market.stock.functions.helper.Errors;

public class Data {
	private static final SimpleDateFormat defaultFormat = new SimpleDateFormat("MMM/dd/yy");

	private static final File UPDATE = new File("src/market/stock/sector/data/date.txt");

	private static final File SECTOR_FILE = new File("src/market/stock/sector/data/sector.txt");
	private static final File PREV_CLOSE_FILE = new File("src/market/stock/sector/data/prev_close.txt");
	private static final File SUBSECTOR_FILE = new File("src/market/stock/sector/data/subsector.txt");

	public static final String STOCKS = "http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&render=download";
	// public static final String STOCKS =
	// "http://www.nasdaq.com/screening/companies-by-industry.aspx?render=download";
	// probably only want to do:
	// http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&render=download
	// http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NYSE&render=download

	// Other stock lists: https://stooq.com/db/h/

	// STOCK LIST NASDAQ: https://stooq.com/db/l/?g=27
	// STOCK LIST NYSE: https://stooq.com/db/l/?g=28

	// HISTORICAL DATA: https://stooq.com/q/d/l/?s=on.us&d1=20160323&d2=20160610
	// FORMAT:
	/*
	 * https://stooq.com/q/d/l/?s=${symbol}.us&d1=${yyyyMMdd}&d2=${yyyyMMdd}
	 */

	public static Map<String, ArrayList<String>> SECTORS = new HashMap<>(); // <String, ArrayList<String>> = Sector:
																			// Stocks in sector
	public static Map<String, ArrayList<String>> SUBSECTORS = new HashMap<>(); // <String, ArrayList<String>> =
																				// Subsector: Stocks in subsector
	public static Map<String, Double> PREV_CLOSE = new HashMap<>(); // <String, Double> = Stock: Prev Close

	static {
		SECTOR_FILE.setReadable(true);
		PREV_CLOSE_FILE.setReadable(true);
		SUBSECTOR_FILE.setReadable(true);

		try {
			if (!update())
				throw new Exception("Don't reinitialize");
			URL stockURL = new URL(STOCKS);
			BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
			ArrayList<String[]> data = new ArrayList<>();
			String x = null;
			while ((x = in.readLine()) != null) {
				data.add(replace("\"", x.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")));
			}

			/*
			 * data: 0: Symbol 1: Name 2: LastSale 3: MarketCap 4: ADR TSO 5: IPOyear 6:
			 * Sector 7: Industry 8: Summary Quote
			 */

			// log(data.get(0));

			// FIRST ADD SECTORS
			for (int i = 1; i < data.size(); i++) {
				String stock = data.get(i)[0].trim();
				String sector = data.get(i)[6].trim();
				ArrayList<String> init = new ArrayList<>();
				if (SECTORS.containsKey(sector)) {
					SECTORS.get(sector).add(stock);
				} else {
					init.add(stock);
					SECTORS.put(sector, init);
				}
			}
			// DONE

			// FIRST ADD SECTORS
			for (int i = 1; i < data.size(); i++) {
				String stock = data.get(i)[0].trim();
				String subsector = data.get(i)[7].trim();
				ArrayList<String> init = new ArrayList<>();
				if (SUBSECTORS.containsKey(subsector)) {
					SUBSECTORS.get(subsector).add(stock);
				} else {
					init.add(stock);
					SUBSECTORS.put(subsector, init);
				}
			}
			// DONE

			// SECOND ADD PREV_CLOSE
			for (int i = 1; i < data.size(); i++) {
				if (SECTORS.containsKey(data.get(i)[6])) {
					PREV_CLOSE.put(data.get(i)[0].trim(),
							data.get(i)[2].equals("n/a") ? -1 : Double.parseDouble(data.get(i)[2]));
				}
			}
			// DONE
		} catch (Exception e) {
			// Obtained cached data.
			try {
				SECTORS = getMapFromFile(SECTOR_FILE);
				SUBSECTORS = getMapFromFile(SUBSECTOR_FILE);
				PREV_CLOSE = getMapFromFileDouble(PREV_CLOSE_FILE);
			} catch (Exception e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}

		System.out.print("\n\n");
	}

	public static void storeDate(File update) {
		try {
			update.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(update));
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			String curr_date = format.format(new Date());
			System.out.println("Storing new date.");
			bw.write(curr_date);
			bw.close();
		} catch (IOException e) {
			System.out.println("Failed to update date file.");
			e.printStackTrace();
		}
	}

	private static boolean update() {
		if (UPDATE.exists()) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(UPDATE)));
				String file_date = br.readLine();
				System.out.println("Currently updated for " + file_date);
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				String curr_date = format.format(Calendar.getInstance().getTime());
				if (file_date.equals(curr_date)) {
					System.out.println("Not Updating");
					br.close();
					return false;
				}
				br.close();
			} catch (Exception e) {
				System.out.println("Updating");
				return true;
			}
		}
		System.out.println("Updating");
		return true;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Double> getMapFromFileDouble(File file) throws Exception {
		Map<String, Double> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<String, Double>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<String,Double> obtained from file");
		return e;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, ArrayList<String>> getMapFromFile(File file) throws Exception {
		Map<String, ArrayList<String>> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<String, ArrayList<String>>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<String,ArrayList<String>> obtained from file");
		return e;
	}

	public static void storeMapFile(Map<String, ArrayList<String>> map, File file) throws Exception {
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file, false);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(map);
		oos.close();
		fos.close();
		System.out.println("Map<String, String> stored");
	}

	public static void storeMapFileDouble(Map<String, Double> mapStringDouble, File prevCloseFile) throws Exception {
		prevCloseFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(prevCloseFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(mapStringDouble);
		oos.close();
		fos.close();
		System.out.println("Map<String, Double> stored");
	}

	private static String[] replace(String string, String[] split) {
		String[] ret = new String[split.length];
		for (int i = 0; i < split.length; i++) {
			ret[i] = split[i].replace("\"", "");
		}
		return ret;
	}

	public static void log(Object[] strings) {
		for (int i = 0; i < strings.length; i++) {
			System.out.print((strings[i] != null ? strings[i] : "null") + ",");
		}
		System.out.println();
	}

	@SuppressWarnings("all")
	public static void main(String[] args) {
		System.out.println("SECTORS:: " + Data.SECTORS.keySet());
		System.out.println("SUBSECTORS:: " + Data.SUBSECTORS.keySet());

		System.out.print("\n");

		Scanner scanner = new Scanner(System.in);
		String input;
		System.out.println("1: Sector, 2: Subsector, 3: Individual Stock, RANDOM: store, exit: exit");
		while (!(input = scanner.nextLine()).equals("exit")) {
			switch (input.trim()) {
			case "all":
				for (String sector : SECTORS.keySet()) {
					ArrayList<String> data = SECTORS.get(sector);
					System.out.println("STARTED SECTOR " + sector);
					getData(data);
					System.out.println("FINISHED SECTOR " + sector);
					STORE_ALL();
				}
				break;
			case "1":
				System.out.println("Input a sector: ");
				String sector = scanner.nextLine().trim();
				getData(Data.SECTORS.get(sector));
				System.out.println("FINISHED SUBSECTOR");
				break;
			case "2":
				System.out.println("Input a subsector: ");
				String subsector = scanner.nextLine();
				getData(Data.SUBSECTORS.get(subsector));
				System.out.println("FINISHED SUBSECTOR");
				break;
			case "3":
				getIndividualStock(scanner);
				break;
			case "0":
				obtain_date_correlating_price_data();
				break;
			case "s":
			case "sector":
			case "slist":
			case "list sector":
			case "list s":
			case "lists":
				for(String s:SECTORS.keySet()) {
					System.out.println("Sector: " + s + " " + SECTORS.get(s).size());
				}
				break;
			case "histp":
				System.out.println(DatePrices.DATE_TO_PRICE.size());
				print_to_file(DatePrices.DATE_TO_PRICE.toString(), "src/market/stock/sector/print/HistoricalPrices.txt");
				break;
			}
			STORE_ALL();
			System.out.println("DONE\n\n" + "1: Sector, 2: Subsector, 3: Individual Stock, exit: exit");
		}
		scanner.close();
	}

	/**
	 * Prints the first input to the File given by the second input
	 * @param string String that is to printed to given file name
	 * @param string2 File
	 */
	private static void print_to_file(String string, String string2) {
		try {
			PrintWriter out = new PrintWriter(string2);
			out.println(string);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void getData(ArrayList<String> stocks) {
		if (stocks == null)
			return;
		for (String s : stocks) {
			System.out.println("NEXT STOCK " + s);
			EarningsFinder ef = new EarningsFinder(s);
			Date[] dates = ef.getEarningsDates();
			for (Date date : dates) {
				DateObtainer dateObt = new DateObtainer(date);
				Date[] obt = dateObt.getDates();
				if (obt == null)
					continue;

				Double[] x = GetHistoricalPrices.getPricesAccordingToDates(ef.STOCK, obt);
				if (x == null)
					continue;
				System.out.println("Earnings: " + defaultFormat.format(date));

				for (Date d : obt) {
					System.out.print(defaultFormat.format(d) + "\t");
				}
				System.out.print("\n");
				for (Double d : x) {
					System.out.print(d + "\t\t");
				}
				System.out.println("\n");
			}
		}
	}

	private static void getIndividualStock(Scanner scanner) {
		System.out.println("Input a stock: ");
		String stock = scanner.nextLine();
		EarningsFinder ef = new EarningsFinder(stock);
		Date[] dates = ef.getEarningsDates();
		for (Date date : dates) {
			DateObtainer dateObt = new DateObtainer(date);
			Date[] obt = dateObt.getDates();
			if (obt == null)
				continue;
			Double[] x = GetHistoricalPrices.getPricesAccordingToDates(ef.STOCK, obt);
			if (x == null)
				continue;

			System.out.println("Earnings: " + defaultFormat.format(date));
			for (Date d : obt) {
				System.out.print(defaultFormat.format(d) + "\t");
			}
			System.out.print("\n");
			for (Double d : x) {
				System.out.print(d + "\t\t");
			}
			System.out.println("\n");
		}
	}

	/*
	 * So gets the hashmap from EarningsFinder For all stocks that have data (w/
	 * Date[].length > 0) obtain the dates -> Date[] and then GetHistoricalPrices ->
	 * Double[]
	 * 
	 * return the hashmap -> <Symbol, Map<Date[], Double[]> >
	 */
	public static void obtain_date_correlating_price_data() {
		Map<String, Date[]> earningsDates = EarningsFinder.x;
		Map<String, Map<Date[], Double[]>> ret = new HashMap<>();
		for (String sym : earningsDates.keySet()) {
			ret.put(sym, new HashMap<>());
			Date[] p1 = earningsDates.get(sym);
			for (Date date : p1) {
				DateObtainer dateObt = new DateObtainer(date);
				Date[] obt = dateObt.getDates();
				if (obt == null)
					continue;
				String uri = GetHistoricalPrices.construct_uri(sym, obt[0], obt[obt.length - 1]);
				Map<String, Double> data = GetHistoricalPrices.data.get(uri);
				Double[] x = GetHistoricalPrices.getPricesAccordingToDates(data, sym, obt);
				ret.get(sym).put(obt, x);
			}
		}
		
		DatePrices.DATE_TO_PRICE = ret;
		
	}

	public static void STORE_ALL() {
		try {
			storeMapFile(SECTORS, SECTOR_FILE);
			storeMapFile(SUBSECTORS, SUBSECTOR_FILE);
			storeMapFileDouble(PREV_CLOSE, PREV_CLOSE_FILE);
			DatePrices.storeMapFile();
			storeDate(UPDATE);
			GetHistoricalPrices.storeMapFile();
			EarningsFinder.storeMapFile();
			Errors.PRINT_ERRORS();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * TODO: ONLY GO THROUGH DATA THAT IS OBTAINED (not things that have not
	 * "obtained data") - create a HashMap for <Symbol, Date[]>. have that - go
	 * through that HashMap and online print those
	 * 
	 * TODO: Turn price data into weekly return and cumulative returns TODO: Turn
	 * index data into weekly return and cumulative returns TODO: Subtract index
	 * returns to individual price returns
	 * 
	 * cumulative returns = (PREVIOUS_CUMULATIVE_RETURN*CURRENT_RETURN) (at end -1)
	 */

}
