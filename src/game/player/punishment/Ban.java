package game.player.punishment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;

/**
 * Ban an account.
 * @author MGT Madness, created on 11-04-2017
 */
public class Ban
{

		/**
		 * Store names of accounts that are banned.
		 */
		public static ArrayList<String> bannedList = new ArrayList<String>();

		public static void ban(Player player, String command)
		{
				try
				{
						String name = command.substring(11);
						if (name.isEmpty())
						{
								return;
						}
						name = name.toLowerCase();
						if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
						{
								player.getPA().sendMessage("Account does not exist: " + name);
								return;
						}
						boolean online = false;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								Player loop = PlayerHandler.players[i];
								if (loop == null)
								{
										continue;
								}
								if (loop.getPlayerName().equalsIgnoreCase(name))
								{
										online = true;
										loop.setDisconnected(true);
										loop.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
										break;
								}
						}

						for (int index = 0; index < bannedList.size(); index++)
						{
								if (bannedList.get(index).equals(name))
								{
										player.getPA().sendMessage(name + " is already banned.");
										return;
								}
						}
						bannedList.add(name);
						player.getPA().sendMessage("Banned " + name + ", online: " + online);
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::accountban shatter");
				}
		}

		public static void unBan(Player player, String command)
		{
				try
				{
						String name = command.substring(6);
						if (name.isEmpty())
						{
								return;
						}
						name = name.toLowerCase();
						if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
						{
								player.getPA().sendMessage("Account does not exist: " + name);
								return;
						}

						for (int index = 0; index < bannedList.size(); index++)
						{
								if (bannedList.get(index).equals(name))
								{
										bannedList.remove(index);
										player.getPA().sendMessage("Unbanned " + name);
										return;
								}
						}
						player.getPA().sendMessage(name + " is not banned to begin with.");
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::unban shatter");
				}
		}

		public static void readBanLog()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/ban.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										bannedList.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

}
