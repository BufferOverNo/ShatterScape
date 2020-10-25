package game.content.miscellaneous;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import core.ServerConstants;
import game.content.profile.RareDropLog;
import game.item.ItemAssistant;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;

/**
 * Rewards for players who have won the Community event.
 * @author MGT Madness, created on 14-02-2015.
 */
public class CommunityEvent
{

		public final static int[] communityEventCasketRare = {1053, 1055, 1057, 1050, 7042, 18725, 18723};

		public final static int[] communityEventCasketNormal = {18735, 18733, 18731, 18729, 18727, 18721, 18715, 18713, 18711, 18709, 18707, 18705, 18703, 18701, 18699, 18697, 18695, 18693, 18691, 18689, 18685, 18683, 18681};

		/**
		 * Add and delete event names to give rewards to.
		 */
		public static ArrayList<String> eventNames = new ArrayList<String>();

		public static void giveReward(Player player)
		{
				if (ItemAssistant.hasItemInInventory(player, 3849))
				{
						ItemAssistant.deleteItemFromInventory(player, 3849, 1);
						if (Misc.hasOneOutOf(40))
						{
								int rareItemId = communityEventCasketRare[Misc.random(communityEventCasketRare.length - 1)];
								ItemAssistant.addItem(player, rareItemId, 1);
								RareDropLog.appendRareDrop(player, "Community event: " + ItemAssistant.getItemName(rareItemId));
								if (!player.profilePrivacyOn)
								{
										Announcement.announce(ServerConstants.GREEN_COL + Misc.capitalize(player.getPlayerName()) + " has received " + ItemAssistant.getItemName(rareItemId) + " from Community event casket.");
								}
								player.getPA().sendScreenshot(ItemAssistant.getItemName(rareItemId), 2);
						}
						else
						{
								ItemAssistant.addItem(player, communityEventCasketNormal[Misc.random(communityEventCasketNormal.length - 1)], 1);
						}
						player.getPA().sendFilterableMessage("You open the casket and receive a reward.");
				}
		}

		/**
		 * Location of the member.txt file.
		 */
		public static String eventFileLocation = "./backup/logs/event.txt";

		public static void checkOnLogIn(Player player)
		{
				for (int index = 0; index < CommunityEvent.eventNames.size(); index++)
				{
						String parse[] = CommunityEvent.eventNames.get(index).split("-");
						if (parse[0].toLowerCase().equals(player.getPlayerName().toLowerCase()))
						{
								int amount = Integer.parseInt(parse[1]);
								player.getPA().sendMessage("<col=005f00>You have x" + amount + " blood money reward ::claimevent");
								return;
						}
				}
		}

		public static void checkForReward(Player player)
		{
				if (player.getHeight() == 20)
				{
						player.getPA().sendMessage("You cannot claim at the tournament area.");
						return;
				}
				for (int index = 0; index < CommunityEvent.eventNames.size(); index++)
				{
						String parse[] = CommunityEvent.eventNames.get(index).split("-");
						if (parse[0].toLowerCase().equals(player.getPlayerName().toLowerCase()))
						{
								int amount = Integer.parseInt(parse[1]);
								if (ItemAssistant.addItem(player, 18644, amount))
								{
										CommunityEvent.eventNames.remove(index);
										player.getPA().sendMessage(ServerConstants.GREEN_COL + "Thank you for participating, your reward is x" + amount + " blood money!");
										return;
								}
								return;
						}
				}
				player.getPA().sendMessage("No reward has been found for you.");
		}

		public static void save()
		{
				FileUtility.deleteAllLines(eventFileLocation);
				FileUtility.saveArrayContents(eventFileLocation, CommunityEvent.eventNames);
		}

		public static void loadFile()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(eventFileLocation));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										CommunityEvent.eventNames.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

}