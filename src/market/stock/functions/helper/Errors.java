package market.stock.functions.helper;

import java.util.ArrayList;

public class Errors {

	public static ArrayList<String> errors = new ArrayList<>();
	
	public static void PRINT_ERRORS() {
		System.out.println("Printing Errors: ");
		if(errors.size() > 0) {
			for(String x : errors) {
				System.out.println(x);
			}
		}
		System.out.println("FINISHED PRINTING ERRORS");
	}
	
	
}
