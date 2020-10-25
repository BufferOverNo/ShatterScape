package game.content.miscellaneous;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import game.player.Player;

/**
 * Welcome message.
 * @author MGT Madness, created on 17-02-2016.
 */
public class WelcomeMessage
{

		/**
		 * Store the welcome messages.
		 */
		public static ArrayList<String> welcomeMessageList = new ArrayList<String>();

		/**
		 * welcome message.txt location.
		 */
		public final static String LOCATION = "data/welcome message.txt";

		/**
		 * Load the welcome message from the text file.
		 */
		public static void loadWelcomeMessage()
		{
				welcomeMessageList.clear();
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(LOCATION));
						String line;
						while ((line = file.readLine()) != null)
						{
								welcomeMessageList.add(line);
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * Send the welcome message.
		 * @param player
		 * 			The associated player.
		 */
		public static void sendWelcomeMessage(Player player)
		{
				for (int index = 0; index < welcomeMessageList.size(); index++)
				{
						player.playerAssistant.sendMessage(welcomeMessageList.get(index));
				}
		}

}
