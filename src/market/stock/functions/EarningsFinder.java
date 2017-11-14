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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import market.stock.sector.Data;

public class EarningsFinder {
	private static boolean debug = false;

	private static final File earnings = new File("src/market/stock/sector/data/earning_dates.txt");
	private static final String URL_PART = "https://finance.yahoo.com/calendar/earnings?symbol=";

	// POTENTIALLY USE
	// https://www.zacks.com/stock/research/AA/earnings-announcements (for stock AA)

	private URL url = null;
	public final String STOCK;

	private String html;
	public String sector;
	public String subsector;
	public Double prev_close;

	public EarningsFinder(String stock) {
		html = null;
		STOCK = stock;

		prev_close = Data.PREV_CLOSE.get(STOCK);
		sector = getSector();
		subsector = getSubSector();

		try {
			url = new URL(URL_PART + stock);
			getHtml();
		} catch (MalformedURLException e) {
			System.out.println("URL was malformed.");
		}
	}

	public static Map<String, Date[]> x;
	static {
		x = new HashMap<>();
		try {
			x = getMapFromFile(earnings);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Date[]> getMapFromFile(File file) throws Exception {
		Map<String, Date[]> e;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		e = (Map<String, Date[]>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("Map<String,ArrayList<String>> obtained from file");
		return e;
	}

	public static void storeMapFile() throws Exception {
		earnings.createNewFile();
		FileOutputStream fos = new FileOutputStream(earnings, false);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(x);
		oos.close();
		fos.close();
		System.out.println("Map<String, Date[]> stored -> EARNINGS");
	}
	
	/**
	 * DEFAULT LOOK AT FILES (lowest order notation (potentially) )
	 * @return
	 */
	public Date[] getEarningsDates() {
		return getEarningsDates(true);
	}

	public Date[] getEarningsDates(boolean LOOK_AT_FILE) {
		if (html == null) {
			if (!getHtml()) {
				if (x.containsKey(STOCK)) {
					System.out.println("Got " + STOCK + " data from file");
					return x.get(STOCK); // returns stored data
				}
				System.out.println("Error: could not obtain dates for " + STOCK);
				return new Date[0];
			}
		}
		
		if(LOOK_AT_FILE) {
			if(x.containsKey(STOCK)) {
				System.out.println("Got " + STOCK + " data from file");
				return x.get(STOCK); // returns stored data
			}
		}

		if (debug)
			System.out.println("Getting Earnings Dates");

		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern
				.compile("((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) [0-3][0-9], 20[0-1][0-9], [0-9] (AM|PM))")
				.matcher(html);
		while (m.find()) {
			allMatches.add(clean(m.group()));
		}
		
		// REGEX FOR ZACKS THAT COULD THEORETICALLY WORK: (1?[0-9]\/[0-9][0-9]\/20[0-1][5-8])

		Date[] ret = new Date[allMatches.size()];
		if(ret.length < 1) {
			System.out.println("Error: could not obtain dates for " + STOCK);
			return ret;
		}

		SimpleDateFormat format = new SimpleDateFormat("MMM dd/ yyyy/ hh aa");
		int i = 0;
		for (String s : allMatches) {
			try {
				ret[i++] = format.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		x.put(STOCK, ret);
		return ret;
	}

	// returns false if couldn't getHtml
	public boolean getHtml() {
		if (url == null)
			return false;

		if (debug)
			System.out.println("Getting html data for " + STOCK);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String i;
			while ((i = br.readLine()) != null) {
				html += i;
			}
			br.close();
			return true;
		} catch (IOException e) {
			return false;
		}

	}

	// PRIVATE METHODS

	private String clean(String group) {
		String ret = group.trim();
		ret = ret.replace(",", "/");
		return ret;
	}

	private String getSubSector() {
		for (String s : Data.SUBSECTORS.keySet()) {
			for (String stock : Data.SUBSECTORS.get(s)) {
				if (stock.trim().equals(STOCK))
					return s;
			}
		}
		return null;
	}

	private String getSector() {
		for (String s : Data.SECTORS.keySet()) {
			for (String stock : Data.SECTORS.get(s)) {
				if (stock.trim().equals(STOCK))
					return s;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Symbol: " + STOCK + ", in Sector: " + sector + ", in Subsector: " + subsector + ", Current Price: "
				+ prev_close + "\n";
	}

}
