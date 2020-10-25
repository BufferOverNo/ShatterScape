package network.connection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import core.ServerConstants;
import game.player.Player;
import game.player.punishment.Blacklist;
import utility.FileUtility;
import utility.Misc;

public class InvalidAttempt
{

		public static List<InvalidAttempt> invalidAttempts = new ArrayList<InvalidAttempt>();

		/**
		 * Saved on shutdown.
		 */
		public static ArrayList<String> invalidAttemptsLog = new ArrayList<String>();

		public String addressIp = "";

		public String macAddress = "";

		public String uidAddress = "";

		public String accountAttempted = "";

		public boolean notSamePerson;

		public long time;

		public String passwordAttempted = "";

		/**
		 * 
		 * @param address
		 * @param mac
		 * @param uid
		 * @param accountAttempted
		 * @param notSamePerson
		 * 	True if it is a completely different person who attempted to log in, different ip, uid and mac.
		 */
		public InvalidAttempt(String address, String mac, String uid, String accountAttempted, boolean notSamePerson, String passwordAttempted)
		{
				this.addressIp = address;
				this.macAddress = mac;
				this.uidAddress = uid;
				this.accountAttempted = accountAttempted;
				this.notSamePerson = notSamePerson;
				this.passwordAttempted = passwordAttempted;
				this.time = System.currentTimeMillis();
		}

		public static boolean canConnect(String ip, String mac, String uid, String accountAttempted)
		{
				int count = 0;
				for (int index = 0; index < invalidAttempts.size(); index++)
				{
						if (invalidAttempts.get(index).addressIp.equals(ip) || invalidAttempts.get(index).uidAddress.equals(uid) || invalidAttempts.get(index).macAddress.equals(mac))
						{
								if (System.currentTimeMillis() - invalidAttempts.get(index).time < 60000)
								{
										count++;
								}
						}
						if (count == ServerConstants.MAXIMUM_INVALID_ATTEMPTS)
						{
								return false;
						}
				}
				return true;
		}

		public static void addToLog(String name, String ip, String mac, String uid, String pass, String originalMac, String originalUid)
		{
				name = name.toLowerCase();

				invalidAttemptsLog.add(Misc.getDate() + " [" + uid + "] [" + mac + "] [" + ip + "] " + "attempted to log into: [" + name + "] [" + originalUid + "] [" + originalMac + "] with pass: " + pass);
		}

		public static void saveInvalidAttemptLog()
		{
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				FileUtility.saveArrayContents("backup/logs/bruteforce/" + dateFormat.format(cal.getTime()) + ".txt", invalidAttemptsLog);
				invalidAttemptsLog.clear();
		}

		private final static String[] flaggedNames = {
				"portugalrox",
				"m g t",
				"mgt madness",
				"ronald",
				"exile",
				"owain",
				"i solo",
				"tradeable",
				"tazman1325",
				"armadyl ownz",
				"sticky weed",
				"boom ur dead",
				"big turtle",
				"spottsy89",
				"rush loot",
				"hybrid dm",
				"bog rell",
				"rush break",
				"lookup",
				"chav",
				"nemonax",
				"jamajka1",
				"jasur",
				"itimor",
				"sausage roll",
				"axyy",
				"zyzyqy",
				"panic button",
				"fate lies",
				"alinl1",
				"patjd22",
				"kpe",
				"godog",
				"chaw",
				"thepaintboy",
				"krishuks",
				"istake herbs",
				"brgjm2",
				"crzyallowedd",
				"kuksja",
				"godsword pkz",
				"retard kid",
				"rgncrayon",
				"tripper126",
				"rachetarrow",
				"zodex",
				"briding hard",
				"reihana",
				"s3c0",
				"esanssi",
				"suk4200",
				"tanqe"

		};

		public static boolean autBlacklistUpdated;

		public static ArrayList<String> autoBlacklist = new ArrayList<String>();

		public static ArrayList<String> autoBlacklistReason = new ArrayList<String>();

		/**
		 * Macs that can access flagged accounts.
		 */
		public static ArrayList<String> whitelistedMacs = new ArrayList<String>();

		/**
		 * 
		 * @param player
		 * 	The player being logged into.
		 * @param load
		 *  if its 3, then incorrect, if it is 1 then correct password
		 * @return
		 */
		public static boolean isFlaggedAccount(Player player, String accountAttemptedName, String currentIp, String currentMac, String currentUid, String passAttempted, int load)
		{
				// If incorrect password, then the char file won't be read.
				String originalUid = "";
				String originalMac = "";
				String originalIp = "";

				accountAttemptedName = accountAttemptedName.toLowerCase();
				for (int index = 0; index < flaggedNames.length; index++)
				{
						if (accountAttemptedName.equals(flaggedNames[index]))
						{
								for (int i = 0; i < whitelistedMacs.size(); i++)
								{
										if (currentMac.equalsIgnoreCase(whitelistedMacs.get(i)))
										{
												return false;
										}
								}
								// Incorrect password.
								if (load == 3)
								{

										originalUid = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + accountAttemptedName + ".txt", "addressUid", 3);
										originalMac = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + accountAttemptedName + ".txt", "addressMac", 3);
										originalIp = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + accountAttemptedName + ".txt", "lastSavedIpAddress", 3);
								}

								// Correct password.
								else if (load == 1)
								{
										originalUid = player.addressUid;
										originalMac = player.addressMac;
										originalIp = player.lastSavedIpAddress;
								}

								if (currentIp.equals(originalIp) || currentMac.equalsIgnoreCase(originalMac) || currentUid.equalsIgnoreCase(originalUid))
								{
										return false;
								}
								String string = "correct";
								if (load == 3)
								{
										string = "incorrect";
								}
								autoBlacklistReason.add("[" + currentIp + "] [" + currentMac + "] [" + currentUid + "] autoblacklisted for logging into [" + accountAttemptedName + "] with " + string + " password of: " + passAttempted);
								autoBlacklist.add(currentIp);
								if (Blacklist.useAbleData(currentMac))
								{
										autoBlacklist.add(currentMac);
								}
								if (Blacklist.useAbleData(currentUid))
								{
										autoBlacklist.add(currentUid);
								}
								autBlacklistUpdated = true;
								return true;
						}
				}
				return false;
		}

		public static boolean autoBlackListed(String currentIp, String currentMac, String currentUid)
		{
				for (int i = 0; i < whitelistedMacs.size(); i++)
				{
						if (currentMac.equalsIgnoreCase(whitelistedMacs.get(i)))
						{
								return false;
						}
				}
				for (int index = 0; index < autoBlacklist.size(); index++)
				{
						if (currentIp.equals(autoBlacklist.get(index)))
						{
								return true;
						}
						if (currentMac.equalsIgnoreCase(autoBlacklist.get(index)))
						{
								return true;
						}
						if (currentUid.equalsIgnoreCase(autoBlacklist.get(index)))
						{
								return true;
						}
				}
				return false;
		}

		public static void loadAutoBlacklist()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/bruteforce/autoblacklisted.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										InvalidAttempt.autoBlacklist.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/bruteforce/whitelisted.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										InvalidAttempt.whitelistedMacs.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}

		}

		public static boolean isBruteforceHacker(String ip, String mac, String uid, String attemptedName)
		{
				String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + attemptedName + ".txt", "addressUid", 3);
				String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + attemptedName + ".txt", "addressMac", 3);
				String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + attemptedName + ".txt", "lastSavedIpAddress", 3);
				if (ip.equals(originalIp) || mac.equalsIgnoreCase(originalMac) || uid.equalsIgnoreCase(originalUid))
				{
						return false;
				}

				int flaggedCount = 0;
				String lastNameAttempted = "";
				String lastPassAttempted = "";
				for (int index = 0; index < invalidAttempts.size(); index++)
				{
						if (invalidAttempts.get(index).addressIp.equals(ip) || invalidAttempts.get(index).uidAddress.equals(uid) && Blacklist.useAbleData(uid) || invalidAttempts.get(index).macAddress.equals(mac) && Blacklist.useAbleData(mac))
						{
								if (invalidAttempts.get(index).notSamePerson && !lastNameAttempted.equals(invalidAttempts.get(index).accountAttempted) && !lastPassAttempted.equals(invalidAttempts.get(index).passwordAttempted))
								{
										flaggedCount++;
										// If i want it to be 3, then use arraylist to store names i tried to hack, String will not be accurate at all.
										if (flaggedCount == 2)
										{
												autoBlacklistReason.add("[" + ip + "] [" + mac + "] [" + uid + "] auto ip banned for trying to hack 2 accounts [" + lastNameAttempted + "] & [" + attemptedName + "]");
												autoBlacklist.add(ip);
												if (Blacklist.useAbleData(mac))
												{
														autoBlacklist.add(mac);
												}
												if (Blacklist.useAbleData(uid))
												{
														autoBlacklist.add(uid);
												}
												autBlacklistUpdated = true;
												return true;
										}
										lastNameAttempted = invalidAttempts.get(index).accountAttempted;
										lastPassAttempted = invalidAttempts.get(index).passwordAttempted;
								}
						}
				}
				return false;
		}
}