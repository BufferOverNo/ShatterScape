package tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import core.ServerConfiguration;
import utility.FileUtility;
import utility.Logger;

public class ErrorManager
{

		private static int number;

		private static String historyLocation = "./backup/logs/system log/error/history.txt";

		public static String currentErrorLocation = "";

		public static void loadErrorFile() throws FileNotFoundException
		{
				if (ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				getLatestErrorFile();
				createNewErrorFile();
				updateHistory();
				FileOutputStream file = new FileOutputStream(ErrorManager.currentErrorLocation);
				Logger logger = new Logger(file, System.out);
				System.setErr(logger);
		}

		private static void getLatestErrorFile()
		{
				String text;
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(historyLocation));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (line.contains("latest"))
								{
										text = line.substring(7);
										number = Integer.parseInt(text);
										currentErrorLocation = "./backup/logs/system log/error/error" + (number + 1) + ".txt";
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
				FileUtility.deleteAllLines(historyLocation);
		}

		private static void createNewErrorFile()
		{
				try
				{
						BufferedReader characterfile = new BufferedReader(new FileReader(currentErrorLocation));
						characterfile.close();
				}
				catch (Exception e)
				{
				}
		}

		private static void updateHistory()
		{
				FileUtility.addLineOnTxt(historyLocation, "latest " + Integer.toString(number + 1));
		}

}
