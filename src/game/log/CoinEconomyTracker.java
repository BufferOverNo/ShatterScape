package game.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import utility.FileUtility;
import utility.Misc;

/**
 * In-depth tracking of where coins come and go from.
 * @author MGT Madness, created on 08-08-2015.
 */
public class CoinEconomyTracker
{
		private final static String LOCATION = "./backup/logs/coin income.txt";

		/**
		 * incomeList.add("ACHIEVEMENT-INCOME 90000");
		 */
		public static ArrayList<String> incomeList = new ArrayList<String>();

		/**
		 * Read the coin economy logs.
		 */
		public static void readCoinEconomyLog()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(LOCATION));
						String line;
						while ((line = file.readLine()) != null)
						{
								line = line.replaceAll(",", "");
								incomeList.add(line);
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * Update the coin economy logs.
		 */
		public static void updateCoinEconomyLog()
		{
				// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
				ArrayList<String> finalIncomeList = new ArrayList<String>();
				for (int index = 0; index < incomeList.size(); index++)
				{
						String currentString = incomeList.get(index);
						int lastIndex = currentString.lastIndexOf(" ");
						String matchToFind = currentString.substring(0, lastIndex);
						boolean finalIncomeListHas = false;

						for (int i = 0; i < finalIncomeList.size(); i++)
						{
								if (finalIncomeList.get(i).contains(matchToFind))
								{
										try
										{
												int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
												int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).replaceAll(",", "").substring(lastIndex + 1));
												int finalValueAdded = (finalNumberValue + numberValue);
												finalIncomeList.remove(i);
												finalIncomeList.add(i, matchToFind + " " + Misc.formatNumber(finalValueAdded));
												finalIncomeListHas = true;
										}
										catch (Exception e)
										{
												Misc.print(e + "");
										}
								}
						}

						if (!finalIncomeListHas)
						{
								finalIncomeList.add(currentString);
						}
				}
				FileUtility.deleteAllLines(LOCATION);
				FileUtility.saveArrayContents(LOCATION, finalIncomeList);
		}

}