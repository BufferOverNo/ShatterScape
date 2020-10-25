package game.player.punishment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;

/**
 * Ip mute system, with unipmute by entering name.
 * @author MGT Madness, created on 06-04-2017.
 */
public class IpMute
{

		/**
		 * Store ip-name
		 */
		public static ArrayList<String> ipMutedData = new ArrayList<String>();

		public static void unIpMute(Player player, String command)
		{
				try
				{
						String name = command.substring(9);
						if (name.isEmpty())
						{
								return;
						}
						if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
						{
								player.getPA().sendMessage("Account does not exist: " + name);
								return;
						}
						String ipTarget = "";
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
										ipTarget = loop.addressIp;
										loop.ipMuted = false;
										break;
								}
						}

						if (!online)
						{
								if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
								{
										player.getPA().sendMessage("Account does not exist: " + name);
										return;
								}
								ipTarget = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
						}
						boolean complete = false;
						for (int index = 0; index < ipMutedData.size(); index++)
						{
								String[] parse = ipMutedData.get(index).split("-");
								String ip = parse[0];
								String storedName = parse[1];
								if (storedName.equals(name) || ip.equals(ipTarget))
								{
										ipMutedData.remove(index);
										index--;
										complete = true;
										// Do not add break, because there can be more than 1 occurence of an ip muted account of they switched ips.
								}
						}
						player.getPA().sendMessage("Un-ip muted " + name + ": " + complete + ", online: " + online);
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::unipmute shatter");
				}
		}

		public static void ipMute(Player player, String command)
		{
				try
				{
						String name = command.substring(7);
						if (name.isEmpty())
						{
								return;
						}

						boolean online = false;
						String ip = "";
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								Player loop = PlayerHandler.players[i];
								if (loop == null)
								{
										continue;
								}
								if (loop.getPlayerName().toLowerCase().equalsIgnoreCase(name))
								{
										online = true;
										ip = loop.addressIp;
										loop.ipMuted = true;
										break;
								}
						}

						if (!online)
						{
								if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
								{
										player.getPA().sendMessage("Account does not exist: " + name);
										return;
								}
								ip = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
						}
						ipMutedData.add(ip + "-" + name.toLowerCase());
						player.getPA().sendMessage(name + " has been ip muted, online: " + online);
				}
				catch (Exception e)
				{
						player.getPA().sendMessage("Wrong usage, ::ipmute shatter");
				}
		}

		public static void readIpMuteLog()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/ipmute.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										ipMutedData.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public static void ipMuteLogInUpdate(Player player)
		{
				for (int index = 0; index < ipMutedData.size(); index++)
				{
						String[] parse = ipMutedData.get(index).split("-");
						String ip = parse[0];
						String storedName = parse[1];
						if (ip.equals(player.addressIp))
						{
								player.ipMuted = true;
								if (!player.getPlayerName().toLowerCase().equalsIgnoreCase(storedName))
								{
										ipMutedData.add(player.addressIp + "-" + player.getPlayerName().toLowerCase());
								}
								break;
						}
						else if (player.getPlayerName().toLowerCase().equalsIgnoreCase(storedName))
						{
								player.ipMuted = true;
								if (!player.addressIp.equals(ip))
								{
										ipMutedData.add(player.addressIp + "-" + player.getPlayerName().toLowerCase());
								}
								break;
						}
				}
		}
}
