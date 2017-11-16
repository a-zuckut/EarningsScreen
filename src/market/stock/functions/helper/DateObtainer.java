package market.stock.functions.helper;

import java.util.Calendar;
import java.util.Date;

public class DateObtainer {

	private Date start;

	public DateObtainer(Date d) {
		start = d;
	}

	/**
	 * Want to find prices on dates around start.
	 * 
	 * @return dates at (start - 1 month), (start - 3 weeks), (start - 2 weeks),
	 *         (start - 1 week), (start - 2 days), (start - 1 day), (start + 1 day),
	 *         (start + 2 day), (start + 1 week)
	 */
	public Date[] getDates() {
		Date curr = new Date();
		// if start > curr date return null
		if (start.after(curr)) {
			return null;
		}

		// if start + 7 >= curr date return Date[6] (assert start < curr date)
		Calendar c = Calendar.getInstance();
		c.setTime(start);
		c.add(Calendar.DATE, 28);
		if (c.getTime().after(curr)) {
			return null; // CURRENTLY I DON"T WANT TO DEAL WITH DIFFERENT DATA POINTS
			// Return 7 weeks, 6 weeks, 5 weeks, 4 weeks, 3 weeks, 2 weeks, 1 week, Day of,
			// BEFORE
			// Date[] ret = new Date[8];
			// int index = ret.length -1;
			// c.setTime(start);
			// ret[index--] = c.getTime(); // Curr Day
			// c.add(Calendar.DATE, -7); // 1 week
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 2 weeks
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 3 weeks
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 4 weeks
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 5 weeks
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 6 weeks
			// ret[index--] = c.getTime();
			// c.add(Calendar.DATE, -7); // 7 week
			// ret[index--] = c.getTime();
			// return ret;
		}

		// if start + 7 < curr date return Date[9]
		if (c.getTime().before(curr)) {
			// Return 7 weeks, 6 weeks, 5 weeks, 4 weeks, 3 weeks, 2 weeks, 1 week, day of,
			// BEFORE ; AND ; 1 week, 2 week, 3 week, 4 week AFTER
			Date[] ret = new Date[12];
			int index = ret.length - 1;
			c.setTime(start);
			c.add(Calendar.DATE, 28);
			ret[index--] = c.getTime(); // +4 weeks
			c.add(Calendar.DATE, -7); 	// +3 week
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// +2 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// +1 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// 0 weeks
			ret[index--] = c.getTime(); // THIS ONE IS THING
			c.add(Calendar.DATE, -7); 	// -1 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -2 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -3 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -4 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -5 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -6 weeks
			ret[index--] = c.getTime();
			c.add(Calendar.DATE, -7); 	// -7 weeks
			ret[index--] = c.getTime();
			return ret;

		}

		return null;
	}

	// private boolean isWeekday(Date d) {
	// Calendar c = Calendar.getInstance();
	// c.setTime(d);
	//
	// if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
	// c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) return true;
	// return false;
	// }

}
