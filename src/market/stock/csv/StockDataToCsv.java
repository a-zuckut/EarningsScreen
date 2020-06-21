package market.stock.csv;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import market.data_retrieval.Stock;
import market.data_retrieval.StockData;
import market.stock.sector.Data;

// Purpose of this file will be to get stock data and store it in CSV format
public class StockDataToCsv {
	// Need to get data from Data.java
	
	private static final String DATA_CSV_FILE = "src/market/stock/csv/data/stockDataWithoutNoSector.csv";
	private static Map<String, ArrayList<String>> sectors = Data.SECTORS;
	
	private static ArrayList<Stock> getStockData(boolean updateCsvConcurrently) throws UnsupportedEncodingException, FileNotFoundException {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		BufferedWriter bw = null;
		if (updateCsvConcurrently) {
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DATA_CSV_FILE), "UTF-8"));
				bw.write(Stock.getCsvHeaderLine());
				bw.newLine();
				bw.flush();
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (String sector: sectors.keySet()) {
			if (sector.isEmpty() || sector.isBlank() || sector.toLowerCase().equals("n/a")) {
				System.out.println("Skipping Empty Sector '" + sector + "'");
				continue;
			}
			System.out.println("Sector: " + sector + " (len = " + sectors.get(sector).size() + ")");
			for (String stock: sectors.get(sector)) {
				System.out.println("Symbol: " + stock);
				try {
					Stock s = StockData.CollectStockData(stock);
					stocks.add(s);
					if (updateCsvConcurrently && bw != null) {
						bw.write(s.getCsvLine());
						bw.newLine();
						bw.flush();
					}
				} catch (Exception e) {
					System.out.println("Couldn't figure out " + stock);
				}
			}
		}
		
		return stocks;
	}
	
	private static void writeToCSV(ArrayList<Stock> stockList)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DATA_CSV_FILE), "UTF-8"));
            bw.write(Stock.getCsvHeaderLine());
            bw.newLine();
            for (Stock stock: stockList)
            {
                bw.write(stock.getCsvLine());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
        }
        catch (FileNotFoundException e){
        	e.printStackTrace();
        }
        catch (IOException e){
        	e.printStackTrace();
        }
    }

	// This file will be a runnable file that gets all current stock data and saves it to a csv file
	public static void main(String[] args) {
		sectors = Data.SECTORS;
		ArrayList<Stock> stocks = null;
		boolean writeConcurrently = true;
		try {
			stocks = getStockData(writeConcurrently);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (!writeConcurrently && stocks != null)
			writeToCSV(stocks);
	}
}
