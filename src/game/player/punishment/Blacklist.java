package game.player.punishment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Blacklist system.
 * @author MGT Madness, created on 01-02-2016.
 */
public class Blacklist
{

		public static ArrayList<String> blacklistedIp = new ArrayList<String>();

		public static ArrayList<String> blacklistedMac = new ArrayList<String>();

		public static ArrayList<String> blacklistedUid = new ArrayList<String>();

		public static ArrayList<String> blacklistedAccounts = new ArrayList<String>();

		/**
		 * Must be above 65k so the Client does not try to read it as a model.
		 */
		public static int clientFileNumber = 65001;

		public static void checkIfClientHasBlacklistFile(Player player)
		{
				player.playerAssistant.sendMessage(":rottenpotatov4" + clientFileNumber);
		}

		public static void tellClientToCreateBlacklistFile(Player player)
		{
				player.playerAssistant.sendMessage(":rottenpotatov2" + clientFileNumber);
		}

		public static void blacklistPlayer(Player player, String name)
		{
				boolean online = false;
				String ip = "";
				String mac = "";
				String uid = "";
				ip = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
				mac = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressMac", 3);
				uid = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressUid", 3);
				mac = mac.toLowerCase();
				uid = uid.toLowerCase();
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						final Player playerLoop = PlayerHandler.players[i];
						if (playerLoop == null)
						{
								continue;
						}
						if (playerLoop.getPlayerName().equalsIgnoreCase(name) || ip.equals(playerLoop.addressIp) || mac.equalsIgnoreCase(playerLoop.addressMac) && !mac.contains("invalid") || uid.equalsIgnoreCase(playerLoop.addressUid) && !uid.contains("invalid"))
						{
								online = true;
								ip = playerLoop.addressIp;
								mac = playerLoop.addressMac;
								uid = playerLoop.addressUid;
								tellClientToCreateBlacklistFile(playerLoop);
								CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												container.stop();
										}

										@Override
										public void stop()
										{

												playerLoop.setDisconnected(true);
										}
								}, 3);
						}
				}
				if (!online)
				{
						ip = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
						mac = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressMac", 3);
						uid = readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressUid", 3);
				}

				mac = mac.toLowerCase();
				uid = uid.toLowerCase();

				if (!ip.isEmpty() && !arrayListContains(blacklistedIp, ip + " (" + name + ")"))
				{
						addNewBlacklistData(name, "IP ADDRESS: ", ip);
				}

				if (useAbleData(mac))
				{
						if (!arrayListContains(blacklistedMac, mac + " (" + name + ")"))
						{
								addNewBlacklistData(name, "MAC ADDRESS: ", mac);
						}
				}
				if (useAbleData(uid))
				{
						if (!arrayListContains(blacklistedUid, uid + " (" + name + ")"))
						{
								addNewBlacklistData(name, "UID ADDRESS: ", uid);
						}
				}

				if (!arrayListContains(blacklistedAccounts, name))
				{
						addNewBlacklistData(name, "NAME: ", name);
				}
		}

		private static void loadPermanentBlacklist()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/blacklisted/permanent.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.startsWith("//") && !line.isEmpty())
								{
										line = line.toLowerCase();
										if (line.startsWith("ip: "))
										{
												blacklistedIp.add(line.toLowerCase().substring(4) + " (permanent.txt)");
										}
										else if (line.startsWith("mac: "))
										{
												blacklistedMac.add(line.toLowerCase().substring(5) + " (permanent.txt)");
										}
										else if (line.startsWith("uid"))
										{
												blacklistedUid.add(line.toLowerCase().substring(5) + " (permanent.txt)");
										}
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}

		}

		private static void addNewBlacklistData(String name, String blacklistType, String data)
		{
				if (data.isEmpty())
				{
						return;
				}
				if (!blacklistType.equals("NAME"))
				{
						if (!useAbleData(data))
						{
								return;
						}
				}
				switch (blacklistType)
				{
						case "IP ADDRESS: ":
								blacklistedIp.add(data + " (" + name + ")");
								break;
						case "MAC ADDRESS: ":
								blacklistedMac.add(data + " (" + name + ")");
								break;
						case "UID ADDRESS: ":
								blacklistedUid.add(data + " (" + name + ")");
								break;
						case "NAME: ":
								blacklistedAccounts.add(data);
								break;
				}
				writeToBlacklistOriginFile(name, blacklistType, data);
		}

		public static boolean useAbleData(String data)
		{
				if (data.toLowerCase().contains("invalid") || data.isEmpty())
				{
						return false;
				}
				return true;
		}

		public static boolean isBlacklisted(String name, String ip, String mac, String password, String uid)
		{

				// Banned only accounts.
				for (int index = 0; index < Ban.bannedList.size(); index++)
				{
						if (Ban.bannedList.get(index).equalsIgnoreCase(name))
						{
								return true;
						}
				}

				boolean blacklisted = false;
				boolean ipMatch = false;
				boolean macMatch = false;
				boolean uidMatch = false;
				name = name.toLowerCase();
				mac = mac.toLowerCase();
				uid = uid.toLowerCase();
				boolean permanentMatch = false;
				String originalBlacklistedName = "";

				for (int i = 0; i < blacklistedIp.size(); i++)
				{
						if (ip.equals(blacklistedIp.get(i).substring(0, blacklistedIp.get(i).indexOf("(") - 1)))
						{
								blacklisted = true;
								ipMatch = true;
								originalBlacklistedName = blacklistedIp.get(i).substring(blacklistedIp.get(i).indexOf("("));
								if (blacklistedIp.get(i).contains("permanent.txt"))
								{
										permanentMatch = true;
								}
								break;
						}
				}
				if (useAbleData(mac))
				{
						for (int i = 0; i < blacklistedMac.size(); i++)
						{
								if (mac.equalsIgnoreCase(blacklistedMac.get(i).substring(0, blacklistedMac.get(i).indexOf("(") - 1)))
								{
										blacklisted = true;
										macMatch = true;
										originalBlacklistedName = blacklistedMac.get(i).substring(blacklistedMac.get(i).indexOf("("));
										if (blacklistedMac.get(i).contains("permanent.txt"))
										{
												permanentMatch = true;
										}
										break;
								}
						}
				}

				if (useAbleData(uid))
				{
						for (int i = 0; i < blacklistedUid.size(); i++)
						{
								if (uid.equalsIgnoreCase(blacklistedUid.get(i).substring(0, blacklistedUid.get(i).indexOf("(") - 1)))
								{
										blacklisted = true;
										uidMatch = true;
										originalBlacklistedName = blacklistedUid.get(i).substring(blacklistedUid.get(i).indexOf("("));
										if (blacklistedUid.get(i).contains("permanent.txt"))
										{
												permanentMatch = true;
										}
										break;
								}
						}
				}

				for (int i = 0; i < blacklistedAccounts.size(); i++)
				{
						if (name.equalsIgnoreCase(blacklistedAccounts.get(i)))
						{
								return true;
						}
				}
				if (permanentMatch)
				{
						return true;
				}
				if (blacklisted)
				{
						if (!originalBlacklistedName.isEmpty())
						{
								originalBlacklistedName = originalBlacklistedName.replace("(", "");
								originalBlacklistedName = originalBlacklistedName.replace(")", "");
						}
						if (!ipMatch)
						{
								addNewBlacklistData(originalBlacklistedName, "IP ADDRESS: ", ip);
						}
						if (!macMatch)
						{
								addNewBlacklistData(originalBlacklistedName, "MAC ADDRESS: ", mac);
						}
						if (!uidMatch)
						{
								addNewBlacklistData(originalBlacklistedName, "UID ADDRESS: ", uid);
						}
				}

				return blacklisted;
		}


		public static void blacklistCommand(Player player, String command)
		{
				String name = command.substring(6);
				if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
				{
						player.playerAssistant.sendMessage(name + " character file does not exist.");
						return;
				}
				blacklistPlayer(player, name);
				player.playerAssistant.sendMessage("You have blacklisted: " + name);
		}

		private static boolean arrayListContains(ArrayList<String> arraylist, String match)
		{
				for (int i = 0; i < arraylist.size(); i++)
				{
						if (arraylist.get(i).equalsIgnoreCase(match))
						{
								return true;
						}
				}
				return false;
		}

		private static void writeToBlacklistOriginFile(String name, String blacklistType, String data)
		{
				String location = "backup/logs/blacklisted/names/" + name + ".txt";
				File file = new File(location);
				if (!file.exists())
				{
						try
						{
								file.createNewFile();
						}
						catch (IOException e)
						{
								e.printStackTrace();
						}
				}
				FileUtility.addLineOnTxt(location, blacklistType + data + " (" + Misc.getDate() + ")");
		}

		public static String readBlacklistedData(String location)
		{
				String name = location.substring(24);
				name = name.replace(".txt", "");
				name = name.replace("names/", "");
				String result = "";
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(location));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (line.startsWith("IP ADDRESS"))
								{
										result = line.substring(12);
										result = result.substring(0, result.indexOf("(") - 1);
										blacklistedIp.add(result + " (" + name + ")");
								}
								else if (line.startsWith("MAC ADDRESS"))
								{
										result = line.substring(13);
										result = result.substring(0, result.indexOf("(") - 1);
										blacklistedMac.add(result + " (" + name + ")");
								}
								else if (line.startsWith("UID ADDRESS"))
								{
										result = line.substring(13);
										result = result.substring(0, result.indexOf("(") - 1);
										blacklistedUid.add(result + " (" + name + ")");
								}
								else if (line.startsWith("name"))
								{
										result = line.substring(6);
										result = result.substring(0, result.indexOf("(") - 1);
										blacklistedAccounts.add(result);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
				return result;
		}

		public static String readOfflinePlayerData(String location, String variable, int space)
		{
				String result = "";
				try
				{
						@SuppressWarnings("resource")
						BufferedReader file = new BufferedReader(new FileReader(location));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (line.contains(variable))
								{
										result = line.substring(variable.length() + space);
										return result;
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
				return result;
		}

		public static void loadStartUpBlacklistedData()
		{
				Blacklist.loadBlacklistedData(false);
				clientFileNumber = Integer.parseInt(readOfflinePlayerData("backup/logs/blacklisted/client file number.txt", "number", 2));
		}

		public static void loadBlacklistedData(boolean delete)
		{
				final File folder = new File("backup/logs/blacklisted/names");
				String accountName = "";
				for (final File fileEntry : folder.listFiles())
				{
						// If the location is not a folder directory, then it has to be a file.
						if (!fileEntry.isDirectory())
						{
								if (delete)
								{
										File file = new File(folder.toString() + "/" + fileEntry.getName());
										file.delete();
								}
								else
								{
										readBlacklistedData("backup/logs/blacklisted/names/" + fileEntry.getName());
										accountName = fileEntry.getName().replace(".txt", "");
										blacklistedAccounts.add(accountName);
								}
						}
				}
				loadPermanentBlacklist();
		}

		public static void clearBlacklist(Player player)
		{
				player.playerAssistant.sendMessage("Old client file number: " + clientFileNumber);
				clientFileNumber++;
				String location = "backup/logs/blacklisted/client file number.txt";
				FileUtility.deleteAllLines(location);
				FileUtility.addLineOnTxt(location, "number: " + clientFileNumber);
				blacklistedIp.clear();
				blacklistedMac.clear();
				blacklistedUid.clear();
				blacklistedAccounts.clear();
				loadBlacklistedData(true);
				player.playerAssistant.sendMessage("Black list cleared and new client file number set to: " + clientFileNumber);
		}

}