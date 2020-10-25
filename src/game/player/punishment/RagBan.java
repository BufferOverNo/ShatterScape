package game.player.punishment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Ban an account.
 * @author MGT Madness, created on 11-04-2017
 */
public class RagBan
{

		/**
		 * Store names of accounts that are banned.
		 */
		public static ArrayList<String> ragBanList = new ArrayList<String>();

		public static void ragBan(Player player, String command)
		{
				try
				{
						String parse[] = command.split(" ");
						int hours = Integer.parseInt(parse[1]);
						String name = command.replace(parse[0] + " " + parse[1] + " ", "");
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


						for (int index = 0; index < ragBanList.size(); index++)
						{
								String parse1[] = ragBanList.get(index).split("-");
								if (parse1[0].equals(name))
								{
										player.getPA().sendMessage(name + " is already rag banned.");
										return;
								}
						}
						boolean online = false;
						String ip = "";
						String uid = "";
						String mac = "";
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
										ip = loop.addressIp;
										uid = loop.addressUid;
										mac = loop.addressMac;
										loop.getPA().movePlayer(3089, 3502, 0);
										break;
								}
						}
						if (!online)
						{

								ip = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
								mac = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressMac", 3);
								uid = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressUid", 3);
						}
						if (!Blacklist.useAbleData(mac))
						{
								mac = "invalid00";
						}
						if (!Blacklist.useAbleData(uid))
						{
								uid = "invalid00";
						}

						ragBanList.add(name + "-" + ip + " " + hours + " " + System.currentTimeMillis() + " " + mac + " " + uid);
						player.getPA().sendMessage(name + " has been rag banned, online: " + online + ", for: " + hours + " hours.");
						FileUtility.addLineOnTxt("backup/logs/rag_ban_history.txt", Misc.getDate() + " " + player.getPlayerName() + " banned: " + name + "-" + ip + " " + mac + " " + uid);
						Announcement.announce(Misc.capitalize(name) + " has been banned from using the wild because of ragging others.");
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::ragban 5 shatter");
				}
		}

		public static void unRagBan(Player player, String command)
		{
				try
				{
						String name = command.substring(9);
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

						for (int index = 0; index < ragBanList.size(); index++)
						{
								String parse1[] = ragBanList.get(index).split("-");
								if (parse1[0].equals(name))
								{
										ragBanList.remove(index);
										player.getPA().sendMessage(name + " has been unbanned.");
										FileUtility.addLineOnTxt("backup/logs/rag_ban_history.txt", Misc.getDate() + " " + player.getPlayerName() + " unbanned: " + name);
										return;
								}
						}
						player.getPA().sendMessage(name + " is not banned to begin with.");
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::unragban shatter");
				}
		}

		public static void readBanLog()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/ragban.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										ragBanList.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public static boolean multiLogAllowed = true;

		public static boolean isRagBanned(Player player)
		{
				if (player.isBot)
				{
						return false;
				}

				if (!multiLogAllowed)
				{
						int count = 0;
						for (int index = 0; index < ipsInWilderness.size(); index++)
						{
								if (player.addressIp.equals(ipsInWilderness.get(index)))
								{
										if (count == 1)
										{
												player.getPA().sendMessage("You cannot multi-log into the wilderness.");
												return true;
										}
										count++;
								}
						}
				}

				for (int index = 0; index < ragBanList.size(); index++)
				{
						String parse1[] = ragBanList.get(index).split("-");
						String parse2[] = ragBanList.get(index).replace(parse1[0] + "-", "").split(" ");
						int hours = Integer.parseInt(parse2[1]);
						long timeBanned = Long.parseLong(parse2[2]);
						if (parse1[0].equals(player.getPlayerName().toLowerCase()) || player.addressIp.equals(parse2[0]) || player.addressMac.equals(parse2[3]) || player.addressUid.equals(parse2[4]))
						{
								if (System.currentTimeMillis() > (timeBanned + hours * 3600000))
								{
										return false;
								}
								long left = (timeBanned + hours * 3600000) - System.currentTimeMillis();
								left /= 60000;
								player.getPA().sendMessage("You are banned for " + left + " more minutes from the wilderness.");
								return true;
						}
				}
				return false;
		}

		/**
		 * List of ips in the wilderness.
		 */
		public static ArrayList<String> ipsInWilderness = new ArrayList<String>();

		public static void addToWilderness(String addressIp)
		{
				ipsInWilderness.add(addressIp);
		}

		public static void removeFromWilderness(String addressIp)
		{
				for (int index = 0; index < ipsInWilderness.size(); index++)
				{
						if (addressIp.equals(ipsInWilderness.get(index)))
						{
								ipsInWilderness.remove(index);
								break;
						}
				}
		}

		public static void loggedIn(Player player)
		{
				if (Area.inWilderness(player))
				{
						RagBan.addToWilderness(player.addressIp);
				}

		}

}
