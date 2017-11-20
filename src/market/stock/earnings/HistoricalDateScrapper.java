package market.stock.earnings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import market.stock.sector.Data;

public class HistoricalDateScrapper {
	volatile static boolean STOP = false;

	private static final File UPDATE = new File("src/market/stock/earnings/data/date.txt");
	private static final File EARNINGS_DATA_FILE = new File("src/market/stock/earnings/data/earnings_data_nasdaq.txt");

	public static Map<String, ArrayList<Date>> datesOfEarnings = new HashMap<>();
	private static Date startDate;

	private static String url_start = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=";
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");

	static {
		Date updatedLast = last_update();
		startDate = updatedLast;
		System.out.println("Init start date to: " + startDate);

		RetrieveEarningsData();
		
		Calendar cal = Calendar.getInstance();
		Date rnd = new Date();
		cal.setTime(rnd);
		cal.add(Calendar.DATE, -1);

		if (startDate != null)
			if (rnd.before(startDate)) {
				System.out.println("Already updated...");
			} else {
				GetDatesTilPresent();
				StoreEarningsData();
			}

	}

	public static void GetDatesTilPresent() {

		String value = "";

		Date curr = startDate;
		Calendar c = Calendar.getInstance();
		c.setTime(curr);
		Date max = new Date();
		while (curr.before(max)) {
			try {
				URL uri = new URL(url_start + format.format(curr));
				BufferedReader br = new BufferedReader(new InputStreamReader(uri.openStream()));
				String input = null;
				String html = "";
				while ((input = br.readLine()) != null) {
					html += input + "\n";
				}

				Date page_date = getDate(html);
				value += page_date + "\n";
				assert (page_date == curr);
				ArrayList<Earning> output = find_stocks(html);
				if (output == null) {
					curr = next(curr);
					continue;
				}
				value += output + "\n";

				for (Earning e : output) {
					if (datesOfEarnings.containsKey(e.symbol)) {
						datesOfEarnings.get(e.symbol).add(e.reported_date);
					} else {
						System.out.println("Added " + e.symbol);
						ArrayList<Date> n = new ArrayList<>();
						n.add(curr);
						datesOfEarnings.put(e.symbol, n);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			curr = next(curr);

		}
		startDate = curr;
	}

	private static void RetrieveEarningsData() {
		try {
			SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy");
			BufferedReader bw = new BufferedReader(new FileReader(EARNINGS_DATA_FILE));
			ArrayList<String> data = new ArrayList<>();
			String x = null;
			while ((x = bw.readLine()) != null) {
				data.add(x);
			}

			for (String s : data) {
				if (s.equals(""))
					return;
				String[] first = s.split(":");
				datesOfEarnings.put(first[0], new ArrayList<Date>());
				String[] second = first[1].trim().split(",");

				for (int i = 0; i < second.length - 1; i++) {
					datesOfEarnings.get(first[0]).add(f.parse(second[i]));
				}
			}

		} catch (IOException e) {
			System.out.println("Couldn't retrieve data");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void StoreEarningsData() {
		Data.print_to_file(printEarningsMap(), EARNINGS_DATA_FILE.toString());
		Data.print_to_file(format.format(startDate), UPDATE.getPath());
	}

	private static Date last_update() {
		Calendar c = Calendar.getInstance();
		c.set(2012, Calendar.JANUARY, 3);
		try {
			if (!UPDATE.exists()) {
				UPDATE.createNewFile();
				return c.getTime();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(UPDATE)));
			String file_date = br.readLine();
			System.out.println("Currently updated for " + file_date);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
			br.close();
			return format.parse(file_date);
		} catch (Exception e) {
			return c.getTime();
		}
	}

	private static Date next(Date curr) {
		Calendar c = Calendar.getInstance();
		c.setTime(curr);
		c.add(Calendar.DATE, 1);
		System.out.println("next date..." + format.format(c.getTime()));
		return c.getTime();
	}

	private static ArrayList<Earning> find_stocks(String html) {
		ArrayList<Earning> ret = new ArrayList<>();

		int i1 = html.indexOf("<div id=\"two_column_main_content_pnlInsider\">");
		if (i1 == -1)
			return null;
		int i2 = html.indexOf("% Suprise", i1);
		if (i2 == -1)
			return null;
		int end = html.indexOf("</table>", i2);

		int count = 0;

		int s = html.indexOf("<tr>", i2);
		int f = html.indexOf("</tr>", s);

		while (f < end) {

			count++;

			try {
				ret.add(parseEarningHtml(html.substring(s + "<tr>".length(), f)));
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("Error in parsing on " + getDate(html) + " number: " + count);
			}

			s = html.indexOf("<tr>", f);
			f = html.indexOf("</tr>", s);
		}

		return ret;
	}

	public static Earning parseEarningHtml(String html) throws ParseException {
		String cleaner = html.replaceAll("</?td.*>", "");
		cleaner = cleaner.replaceAll("\n", "");
		cleaner = cleaner.replaceAll("\t+", "");
		cleaner = cleaner.replaceAll(" +", " ");
		cleaner = cleaner.replaceAll("<a id=\".*\">", "");
		cleaner = cleaner.replaceAll("<span.*span>", "");
		cleaner = cleaner.replaceAll("<br/.*>", "").trim();

		int i1 = cleaner.lastIndexOf("(");
		int i2 = cleaner.lastIndexOf(")");
		String name = cleaner.substring(0, i1).trim();
		String symbol = cleaner.substring(i1 + 1, i2).trim();
		cleaner = cleaner.substring(i2 + 1).trim();
		int i3 = cleaner.indexOf(" ");
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date d = format.parse(cleaner.substring(0, i3));
		cleaner = cleaner.substring(i3 + 1).trim();
		int i4 = cleaner.indexOf(" ");
		int i5 = cleaner.indexOf(" ", i4 + 1);
		String q = cleaner.substring(0, i5);
		cleaner = cleaner.substring(i5).trim();

		String[] split = cleaner.replace("$", "").trim().split(" ");
		
		if (split.length < 2)
			System.out.println(cleaner);

		double estimate = 0;
		if (!split[0].replace("$", "").trim().equals("n/a")) {
			estimate = Double.parseDouble(split[0].replace("$", "").trim());
		}
		int num = Integer.valueOf(split[1].trim());
		double actual = 0;
		if (!split[3].replace("$", "").trim().equals("n/a")) {
			actual = Double.parseDouble(split[3].replace("$", "").trim());
		}

		Earning ret = new Earning(name, d, q, estimate, actual, num);
		ret.symbol = symbol;
		return ret;
	}

	private static Date getDate(String html) {
		SimpleDateFormat f1 = new SimpleDateFormat("MMMM dd, yyyy");
		int index1 = html.indexOf("Earnings Announcements for ");
		index1 += "Earnings Announcements for ".length();
		int index2 = html.indexOf("</h2>", index1);
		try {
			return f1.parse(html.substring(index1, index2));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String printEarningsMap() {

		String x = "";
		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy");

		for (String sym : datesOfEarnings.keySet()) {
			x += sym + ": ";
			for (Date dates : datesOfEarnings.get(sym)) {
				x += f.format(dates) + ", ";
			}
			x += "\n";
		}
		return x.substring(0, x.length());
	}

	public static void main(String[] args) {
	}

}
