package game.content.donator;

import java.io.BufferedReader;
import java.io.FileReader;

import core.ServerConstants;
import game.player.Player;
import utility.FileUtility;


/**
 * Refill Hp, Prayer, Special and cure poison.
 * @author MGT Madness, created on 16-09-2016, in the 12 hour flight from Malaysia to Egypt.
 */
public class DonationsNeeded
{

		public static int currentDonationAmount;

		public final static int DONATIONS_NEEDED = 450;

		public static void getDonatorMessage(Player player)
		{
				player.getPA().sendMessage(ServerConstants.DONATOR_ICON + "This is for Donators, help fund the server at ::donate");
		}

		public static void getLegendaryDonatorMessage(Player player)
		{
				player.getPA().sendMessage("<img=6>This is for Legendary Donators, help fund the server at ::donate");
		}

		public static void kingArthurNpcChat(Player player)
		{
				player.getDH().sendNpcChat3("Donations are needed to purchase advertisments on RS", "related communities, so the playerbase increases ", "Thank you for playing ShatterScape.", 9850);
		}

		public static void save()
		{
				FileUtility.deleteAllLines("backup/logs/donations/monthly donation amount.txt");
				FileUtility.addLineOnTxt("backup/logs/donations/monthly donation amount.txt", currentDonationAmount + "");
		}

		public static void loadFile()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/donations/monthly donation amount.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										currentDonationAmount = Integer.parseInt(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

}
