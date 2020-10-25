package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * General file related methods.
 * @author MGT Madness, created on 25-01-2015.
 */
public class FileUtility
{

		/**
		 * Delete all lines in a file.
		 * @param fileLocation
		 * 			The location of the file.
		 */
		public static void deleteAllLines(String fileLocation)
		{
				if (!FileUtility.accountExists(fileLocation))
				{
						return;
				}
				try
				{
						FileOutputStream writer = new FileOutputStream(fileLocation);
						writer.write((new String()).getBytes());
						writer.close();

				}
				catch (IOException ex)
				{
						ex.printStackTrace();
				}
		}

		/**
		 * Check if the file exists.
		 * @param location
		 * 			The location of the file.
		 * @return
		 * 			True, if the file exists.
		 */
		public static boolean accountExists(String location)
		{
				File firstFolder = new File(location);
				if (firstFolder.exists())
				{
						return true;
				}
				return false;
		}

		/**
		 * @param location
		 * 			Location of the .txt file.
		 * @param text
		 * 			The String to search for in the .txt file.
		 * @return
		 * 			True, if a line in this .txt file matches the given text.
		 */
		public static boolean txtEquals(String location, String text)
		{
				text = text.toLowerCase();
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(location));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (line.equals(text))
								{
										file.close();
										return true;
								}
						}
						file.close();
				}
				catch (Exception e)
				{
						Misc.print("Problem reading file: " + location + ", " + text);
				}
				return false;
		}


		/**
		 * Add a line of txt in a .txt file.
		 * @param location
		 * 			Location of the .txt file.
		 * @param line
		 * 			The line to add in the .txt file.
		 */
		public static void addLineOnTxt(String location, String line)
		{
				BufferedWriter bw = null;
				try
				{
						bw = new BufferedWriter(new FileWriter(location, true));
						bw.write(line);
						bw.newLine();
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
		}

		public static void saveArrayContentsSilent(String location, ArrayList<?> arraylist)
		{
				BufferedWriter bw = null;
				try
				{
						bw = new BufferedWriter(new FileWriter(location, true));

						for (int index = 0; index < arraylist.size(); index++)
						{
								bw.write("" + arraylist.get(index));
								bw.newLine();
						}

						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
		}

		public static boolean saveArrayContents(String location, ArrayList<?> arraylist)
		{
				boolean wrote = false;
				BufferedWriter bw = null;
				try
				{
						bw = new BufferedWriter(new FileWriter(location, true));

						for (int index = 0; index < arraylist.size(); index++)
						{
								bw.write("" + arraylist.get(index));
								bw.newLine();
								wrote = true;
						}

						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
				if (wrote)
				{
						Misc.printDontSave("Written to file: " + location);
				}
				return wrote;
		}
}