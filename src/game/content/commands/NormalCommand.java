package game.content.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.donator.DonationsNeeded;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.HackLog;
import game.content.miscellaneous.PvpBlacklist;
import game.content.miscellaneous.Skull;
import game.content.wildernessbonus.WildernessRisk;
import game.player.Area;
import game.player.LogOutUpdate;
import game.player.Player;
import game.player.PlayerHandler;
import network.connection.DonationManager;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import utility.Misc;

/**
 * Normal player commands.
 * @author MGT Madness, created on 12-12-2013.
 */
public class NormalCommand
{

		/**
		 * Commands eligible for normal players only.
		 * @param player
		 * 			The player using the command.
		 */
		public static void normalCommands(Player player, String playerCommand)
		{
				if (playerCommand.equals("test1"))
				{
						test1(player, playerCommand);
				}

				else if (playerCommand.contains("test2"))
				{
						test2(player, playerCommand);
				}

				else if (playerCommand.equals("test3"))
				{
						test3(player, playerCommand);
				}
				else if (playerCommand.equals("empty"))
				{
						if (player.isAdministratorRank())
						{
								return;
						}
						if (Combat.inCombatAlert(player))
						{
								return;
						}
						player.getDH().sendDialogues(286);
				}
				else if (playerCommand.startsWith("changepassword"))
				{
						try
						{
								player.getPA().closeInterfaces();
								String newestPassword = playerCommand.substring(15);
								if (newestPassword.trim().length() == 0)
								{
										player.getDH().sendStatement("Cancelled. your password is: @blu@" + player.playerPass);
										return;
								}
								if (!newestPassword.matches("[A-Za-z0-9 ]+"))
								{
										PacketHandler.stringAbuseLog.add(player.getPlayerName() + " at " + Misc.getDate());
										PacketHandler.stringAbuseLog.add("Change password abuse:");
										if (newestPassword.contains("\r") || newestPassword.contains("\n"))
										{
												PacketHandler.stringAbuseLog.add("Contains backwards slash r or n");
										}
										else
										{
												PacketHandler.stringAbuseLog.add("Contains: " + newestPassword);
										}
										return;
								}
								if (newestPassword.length() > 0 && newestPassword.length() < 21)
								{
										HackLog.hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDate());
										HackLog.hackLog.add("Old pass: " + player.playerPass);
										HackLog.hackLog.add("New pass: " + newestPassword);
										HackLog.hackLog.add("------");
										player.playerPass = newestPassword;
										player.getPA().sendMessage("Your new password is: " + ServerConstants.BLUE_COL + "'" + player.playerPass + "'");
								}
								else
								{
										player.playerAssistant.sendMessage("Wrong entry. your password is: " + ServerConstants.BLUE_COL + player.playerPass);
										player.playerAssistant.sendMessage("Maximium of 20 characters allowed for your password.");
								}
						}
						catch (Exception e)
						{

						}
				}
				else if (playerCommand.startsWith("thread"))
				{
						try
						{
								int threadId = Integer.parseInt(playerCommand.substring(7));
								player.getPA().sendMessage(":packet:website www.shatterscape.com/forums/topic/" + threadId + "-shatterscape");
						}
						catch (Exception e)
						{
								player.getPA().sendMessage("Wrong input, use ::thread 8 for example.");
						}
				}
				else if (playerCommand.startsWith("board"))
				{
						try
						{
								int threadId = Integer.parseInt(playerCommand.substring(6));
								player.getPA().sendMessage(":packet:website www.shatterscape.com/forums/forum/" + threadId + "-shatterscape");
						}
						catch (Exception e)
						{
								player.getPA().sendMessage("Wrong input, use ::thread 8 for example.");
						}
				}
				else if (playerCommand.startsWith("changepass"))
				{
						try
						{
								player.getPA().closeInterfaces();
								String newestPassword = playerCommand.substring(11);
								if (newestPassword.trim().length() == 0)
								{
										player.getDH().sendStatement("Cancelled. your password is: @blu@" + player.playerPass);
										return;
								}
								if (!newestPassword.matches("[A-Za-z0-9 ]+"))
								{
										PacketHandler.stringAbuseLog.add(player.getPlayerName() + " at " + Misc.getDate());
										PacketHandler.stringAbuseLog.add("Change password abuse:");
										if (newestPassword.contains("\r") || newestPassword.contains("\n"))
										{
												PacketHandler.stringAbuseLog.add("Contains backwards slash r or n");
										}
										else
										{
												PacketHandler.stringAbuseLog.add("Contains: " + newestPassword);
										}
										return;
								}
								if (newestPassword.length() > 0 && newestPassword.length() < 21)
								{
										HackLog.hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDate());
										HackLog.hackLog.add("Old pass: " + player.playerPass);
										HackLog.hackLog.add("New pass: " + newestPassword);
										HackLog.hackLog.add("------");
										player.playerPass = newestPassword;
										player.getPA().sendMessage("Your new password is: " + ServerConstants.BLUE_COL + "'" + player.playerPass + "'");
								}
								else
								{
										player.playerAssistant.sendMessage("Wrong entry. your password is: " + ServerConstants.BLUE_COL + player.playerPass);
										player.playerAssistant.sendMessage("Maximium of 20 characters allowed for your password.");
								}
						}
						catch (Exception e)
						{

						}
				}
				else if (playerCommand.startsWith("admin") && player.getPlayerName().equals("Mgt Madness") && ServerConfiguration.DEBUG_MODE)
				{
						String name = playerCommand.replace("admin ", "").toLowerCase();
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (loop.getPlayerName().toLowerCase().equals(name))
								{
										loop.playerRights = 2;
										player.getPA().sendMessage(name + " promoted to Administrator.");
										loop.setUpdateRequired(true);
										loop.setAppearanceUpdateRequired(true);
										break;
								}

						}
				}

				else if (playerCommand.startsWith("player"))
				{

						int frameIndex = 0;
						player.getPA().sendFrame126("Players online: " + PlayerHandler.getPlayerCount(), 25003);
						player.getPA().sendFrame126("Join the 'ShatterScape' cc and check your Quest tab.", 25008 + frameIndex);
						frameIndex++;
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (loop.isBot)
								{
										continue;
								}
								player.getPA().sendFrame126(loop.getPlayerName(), 25008 + frameIndex);
								frameIndex++;
						}
						InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
						player.getPA().displayInterface(25000);
				}

				else if (playerCommand.startsWith("/"))
				{
						slash(player, playerCommand);
				}
				else if (playerCommand.startsWith("skull"))
				{
						skull(player, playerCommand);
				}
				else if (playerCommand.startsWith("risk"))
				{
						player.timeUsedRiskCommand = System.currentTimeMillis();
						WildernessRisk.carriedWealth(player, true);
						player.diceResultSaved = "<img=8> I am currently risking " + Misc.formatNumber(player.riskedWealth) + " blood money.";
						player.playerAssistant.sendMessage(":quickchat " + player.diceResultSaved);
				}
				else if (playerCommand.startsWith("redskull"))
				{
						if (player.headIconPk == 2)
						{
								return;
						}
						Skull.redSkull(player);
						ItemsKeptOnDeath.updateInterface(player);
						player.getPA().sendMessage(ServerConstants.RED_COL + "You have been red skulled! You cannot protect item.");
				}

				else if (playerCommand.startsWith("tokens"))
				{
						player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have used a total of " + player.donatorTokensRankUsed + " Donator tokens.");
				}

				else if (playerCommand.startsWith("forum"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/forums");
				}
				else if (playerCommand.startsWith("donate"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/donate");
						player.getPA().sendMessage("Type in ::claimdonation when you have donated.");
				}
				else if (playerCommand.equals("vote"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/vote");
						player.getPA().sendMessage("Type in ::claimvote when you have voted.");
				}
				else if (playerCommand.startsWith("discord"))
				{
						player.getPA().sendMessage(":packet:website www.discord.gg/7vY4Hae");
				}
				else if (playerCommand.startsWith("guide") || playerCommand.startsWith("help"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/forum/index.php?board=9.0");
				}
				else if (playerCommand.startsWith("updates"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/forums/forum/10-shatterscape-updates/");
				}
				else if (playerCommand.startsWith("bots"))
				{
						player.getPA().toggleBots(false);
				}

				else if (playerCommand.equals("claim") || playerCommand.equals("claimvote"))
				{

						if (player.getDuelStatus() >= 1 || player.isInTrade() || player.getHeight() == 20)
						{
								player.getPA().sendMessage("You cannot vote right now.");
								return;
						}
						if (System.currentTimeMillis() - player.timeClaimedEvent <= 5000)
						{
								return;
						}
						/*
						for (int index = 0; index < VoteManager.voteBan.size(); index++)
						{
								if (player.addressUid.toLowerCase().contains(VoteManager.voteBan.get(index).toLowerCase()))
								{
										player.getPA().sendMessage("You have been limited from voting for a while.");
										return;
								}
						}
						*/
						player.timeClaimedEvent = System.currentTimeMillis();

						if (VoteManager.voteLimitReached(player))
						{
								return;
						}

						(new Thread(new VoteManager(player))).start();
				}


				else if (playerCommand.equals("claimevent"))
				{
						CommunityEvent.checkForReward(player);
				}


				else if (playerCommand.equals("claimdonation"))
				{
						if (System.currentTimeMillis() - player.timeClaimedDonation <= 5000)
						{
								return;
						}
						(new Thread(new DonationManager(player))).start();
						player.timeClaimedDonation = System.currentTimeMillis();
				}

				else if (playerCommand.startsWith("rule"))
				{
						int frameIndex = 0;
						player.getPA().sendFrame126("Rules list", 25003);
						player.getPA().sendFrame126("Please be friendly to everyone to lighten up the community.", 25008 + frameIndex);
						frameIndex++;
						player.getPA().sendFrame126("@red@Action will be taken if you constantly break rules.", 25008 + frameIndex);
						frameIndex++;
						player.getPA().sendFrame126("@red@Can result in mute/IP ban depending on severity.", 25008 + frameIndex);
						frameIndex++;
						for (int index = 0; index < rules.length; index++)
						{
								player.getPA().sendFrame126(rules[index], 25008 + frameIndex);
								frameIndex++;
						}
						double amount = rules.length * 22.5;
						InterfaceAssistant.setFixedScrollMax(player, 25007, (int) amount);
						InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
						player.getPA().displayInterface(25000);
				}
				else if (playerCommand.startsWith("command"))
				{
						int frameIndex = 0;
						player.getPA().sendFrame126("Commands list", 25003);
						for (int index = 0; index < commands.length; index++)
						{
								player.getPA().sendFrame126(commands[index], 25008 + frameIndex);
								frameIndex++;
						}
						double amount = commands.length * 22.5;
						InterfaceAssistant.setFixedScrollMax(player, 25007, (int) amount);
						InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
						player.getPA().displayInterface(25000);
				}

				else if (playerCommand.startsWith("yell") && !player.isDonator() && !player.isModeratorRank() && !player.isSupport())
				{
						DonationsNeeded.getDonatorMessage(player);
				}


				else if (playerCommand.startsWith("clan"))
				{
						player.getPA().sendMessage(":packet:website www.shatterscape.com/forum/index.php?board=17.0");
				}

				else if (playerCommand.startsWith("forcelogout"))
				{
						LogOutUpdate.manualLogOut(player);
				}
				else if (playerCommand.contains("ancient") || playerCommand.contains("modern") || playerCommand.contains("lunar"))
				{
						if (player.isAdministratorRank())
						{
								return;
						}
						player.getPA().sendMessage("Use the Occult altar inside the East Edgeville building.");
				}
				else if (playerCommand.contains("veng") || playerCommand.contains("barrage") || playerCommand.contains("tb"))
				{
						if (player.isAdministratorRank())
						{
								return;
						}
						player.getPA().sendMessage("Use the Quick set up feature on the quest tab.");
				}
				else if (playerCommand.contains("level") || playerCommand.contains("max") || playerCommand.contains("master"))
				{
						if (player.isAdministratorRank())
						{
								return;
						}
						player.getPA().sendMessage("Click on the skill tab icon to change your levels.");
				}
				else if (playerCommand.contains("item") || playerCommand.contains("pickup"))
				{
						if (player.isAdministratorRank())
						{
								return;
						}
						player.getPA().sendMessage("Use the shops at home to buy items or check your bank.");
				}
				else if (playerCommand.startsWith("blacklist"))
				{
						Player playerAttackedMe = PlayerHandler.players[player.getLastAttackedBy()];
						if (playerAttackedMe != null)
						{
								PvpBlacklist.addPvpBlacklist(player, "addpvpblacklist" + playerAttackedMe.getPlayerName(), false);
						}
						PvpBlacklist.displayPvpBlacklistInterface(player);
				}

		}

		private final static String[] rules = {"No RWT", "No Racism", "No Offensive language", "No Spamming staff", "No Bug abusing", "No Advertising", "No Encouraging rule breaking", "If Bj tie, Host wins.", "55x2, 55 is host win"};

		private final static String[] commands = {
				"rules",
				"bots",
				"guide",
				"forum",
				"vote",
				"donate",
				"yt",
				"clan",
				"skull",
				"redskull",
				"empty",
				"discord",
				"updates",
				"thread",
				"board",
				"changepassword",
				"claimvote",
				"claimdonation",
				"claimevent",
				"tokens",
				"maximizable",
				"angle",
				"fps",
				"[Donator commands below]",
				"yell",
				"[Legendary Donator commands below]",
				"settitle",
				"titleaftername",
				"titlebeforename"};

		private static void skull(Player player, String playerCommand)
		{
				if (player.getRedSkull() && (Area.inWilderness(player) || Combat.inCombat(player)))
				{
						player.playerAssistant.sendMessage("You cannot do this right now.");
						return;
				}
				Skull.whiteSkull(player);
				ItemsKeptOnDeath.updateInterface(player);
				player.getPA().sendMessage("You have been skulled.");
		}

		/*
		CustomBot bot = new CustomBot("speaking bot", "password");
		bot.attatchEvent(new CustomBotEvent(bot.getClient()));
		bot.startEvent();
		BotUtils.addBot(bot);
		*/
		/*// Update prices.txt
		try
				{
						BufferedWriter bw = null;
						bw = new BufferedWriter(new FileWriter("prices.txt", true));
						for (int index = 0; index < ItemDefinition.getDefinitions().length; index++)
						{
								if (ItemDefinition.getDefinitions()[index] == null)
								{
										continue;
								}
								int price = ItemDefinition.getDefinitions()[index].price;
								if (BloodMoneyPrice.getBloodMoneyPrice(index) > 0)
								{
										price = 200000000 + BloodMoneyPrice.getBloodMoneyPrice(index);
								}
								bw.write(index + " " + price);
								bw.newLine();
		
						}
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
		
		URL tmp;
				try
				{
						// I cannot send more than 150 requests a minute or i get ip banned.
						String ip = "70.72.54.82";
						tmp = new URL("http://ip-api.com/line/" + ip);
						BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
						br.readLine();
						Misc.print(br.readLine());
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
				}
				catch (MalformedURLException e)
				{
						e.printStackTrace();
				}
				catch (IOException e)
				{
						e.printStackTrace();
				}
				
				
				// Find what section of game tick lags the most.
				double npc = 0;
				double player1 = 0;
				double event = 0;
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("lag spike.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										if (line.contains("Cycle event"))
										{
												event += Integer.parseInt(line.substring(13));
										}
										else if (line.contains("Player"))
										{
												player1 += Integer.parseInt(line.substring(8));
										}
										else if (line.contains("NPC"))
										{
												npc += Integer.parseInt(line.substring(5));
										}
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
				double total = event + player1 + npc;
				Misc.print("Event: " + (event / total) * 100);
				Misc.print("Player: " + (player1 / total) * 100);
				Misc.print("Npc: " + (npc / total) * 100);
				
				
				final File folder = new File("players");
				int amount = 0;
				for (final File fileEntry : folder.listFiles())
				{
						// If the location is not a folder directory, then it has to be a file.
						if (!fileEntry.isDirectory())
						{
								String result = "";
								String name = "";
								String ip = "";
								try
								{
										BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
										String line;
										while ((line = file.readLine()) != null)
										{
												if (line.contains("Username ="))
												{
														name = line.substring(line.indexOf("=") + 2);
												}
												else if (line.contains("172.58.12.62"))
												{
														Misc.print(name + ", " + line);
												}
										}
										file.close();
								}
								catch (Exception e)
								{
								}
						}
				}
				
				
				final File folder = new File("players");
				int amount = 0;
				for (final File fileEntry : folder.listFiles())
				{
						// If the location is not a folder directory, then it has to be a file.
						if (!fileEntry.isDirectory())
						{
								String result = "";
								String name = "";
								String ip = "";
								try
								{
										BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
										String line;
										while ((line = file.readLine()) != null)
										{
												if (line.contains("Username ="))
												{
														name = line.substring(line.indexOf("=") + 2);
												}
												else if (line.contains("lastSavedIpAddress"))
												{
														String string = line.substring(line.lastIndexOf("=") + 2);
														if (string.isEmpty())
														{
																continue;
														}
														ip = string;
												}
												else if (line.contains("accountDateCreated"))
												{
														String string = line.substring(line.lastIndexOf("=") + 2);
														if (string.isEmpty())
														{
																continue;
														}
														if (string.contains("29/10/2016"))
														{
																Misc.print(ip);
																amount++;
														}
												}
										}
										file.close();
								}
								catch (Exception e)
								{
								}
						}
				}
				Misc.print("Amount: " + amount);
				
				
				try
				{
						BufferedWriter bw = null;
						bw = new BufferedWriter(new FileWriter("items.txt", true));
						for (int index = 0; index < ItemDefinition.getDefinitions().length; index++)
						{
								if (ItemDefinition.getDefinitions()[index] == null)
								{
										continue;
								}
								if (ItemDefinition.getDefinitions()[index].name.equals("Null"))
								{
										continue;
								}
								bw.write("Id: " + ItemDefinition.getDefinitions()[index].itemId);
								bw.newLine();
								bw.write("Name: " + ItemDefinition.getDefinitions()[index].name);
								bw.newLine();
								bw.write("Price: " + ItemDefinition.getDefinitions()[index].price);
								bw.newLine();
								bw.write("Noted: " + ItemDefinition.getDefinitions()[index].note);
								bw.newLine();
								bw.write("Stackable: " + ItemDefinition.getDefinitions()[index].stackable);
								bw.newLine();
								bw.write("F2p: " + ItemDefinition.getDefinitions()[index].f2p);
								bw.newLine();
								bw.write("Random: " + ItemDefinition.getDefinitions()[index].random);
								bw.newLine();
								bw.write("Stab attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[0]);
								bw.newLine();
								bw.write("Slash attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[1]);
								bw.newLine();
								bw.write("Crush attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[2]);
								bw.newLine();
								bw.write("Magic attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[3]);
								bw.newLine();
								bw.write("Ranged attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[4]);
								bw.newLine();
								bw.write("Stab defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[5]);
								bw.newLine();
								bw.write("Slash defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[6]);
								bw.newLine();
								bw.write("Crush defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[7]);
								bw.newLine();
								bw.write("Magic defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[8]);
								bw.newLine();
								bw.write("Ranged defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[9]);
								bw.newLine();
								bw.write("Strength bonus: " + ItemDefinition.getDefinitions()[index].bonuses[10]);
								bw.newLine();
								bw.write("Prayer bonus: " + ItemDefinition.getDefinitions()[index].bonuses[11]);
								bw.newLine();
								bw.newLine();
						}
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
				
				
				
				// Npc drop simulator per hour.
				NpcHandler.loadNpcData();
				int npcToKill = 4040;
				int killsPerHour = 51;
		
				int totalLoot = 0;
				int lootAmount = 100000;
		
				for (int index = 0; index < lootAmount; index++)
				{
						int npcIndex = -1;
						int chance = 0;
						int itemId = 0;
						int itemAmount = 0;
						for (int i = 0; i < NpcDrops.npcRareDropsList.size(); i++)
						{
								if (NpcDrops.npcRareDropsList.get(i) == npcToKill)
								{
										npcIndex = i;
										break;
								}
						}
						if (npcIndex == -1)
						{
								return;
						}
						String[] drops = NpcDrops.npcRareDropsData.get(npcIndex).split("-");
						int dropsLength = drops.length;
						for (int a = 0; a < dropsLength; a++)
						{
								String[] currentLoot = drops[a].split(" ");
		
								chance = Integer.parseInt(currentLoot[0]);
								if (currentLoot[2].contains(","))
								{
										String[] split = currentLoot[2].split(",");
										itemAmount = Misc.random(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
								}
								else
								{
										itemAmount = Integer.parseInt(currentLoot[2]);
								}
								if (Misc.hasOneOutOf(GameMode.getDropRate(player, chance)))
								{
										itemId = Integer.parseInt(currentLoot[1]);
										break;
								}
						}
						if (itemId == 0)
						{
								continue;
						}
						int loot = BloodMoneyPrice.getBloodMoneyPrice(itemId) * itemAmount;
						if (BloodMoneyPrice.getBloodMoneyPrice(itemId) != 1)
						{
								loot = ShopAssistant.getSellToShopPrice(itemId);
						}
						totalLoot += loot;
				}
				Misc.printDontSave((totalLoot / lootAmount) * killsPerHour + "");
				*/

		/*
		for (int index = 0; index < player.playerItems.length; index++)
		{
				Misc.printDontSave("{" + (player.playerItems[index] - 1) + ", 1}, // " + ItemAssistant.getItemName(player.playerItems[index] - 1));
		}
		
		for (int index = 0; index < player.playerEquipment.length; index++)
		{
				Misc.printDontSave("{" + (player.playerEquipment[index]) + ", 1}, // " + ItemAssistant.getItemName(player.playerEquipment[index]));
		}
		*/


		/*
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<String> kills = new ArrayList<String>();
		try
		{
				BufferedReader file = new BufferedReader(new FileReader("kills.txt"));
				String line;
				while ((line = file.readLine()) != null)
				{
						if (!line.isEmpty() && line.contains("Reward: [") && line.contains("="))
						{
								String name = line.substring(line.indexOf("]") + 2);
								name = name.substring(0, name.indexOf(" killed "));
								String amountString = line.substring(line.indexOf(" with ") + 6, line.indexOf(" damage at "));
								int damage = Integer.parseInt(amountString);
								data.add(name + "-" + damage);
								kills.add(name + "-" + 1);
		
						}
				}
				file.close();
		}
		catch (Exception e)
		{
		}
		
		kills = sort(kills, "-");
		Misc.printDontSave(kills + "");
		data = sort(data, "-");
		Misc.printDontSave(data + "");
		Misc.printDontSave(kills.size() + ", " + data.size());
		
		for (int index = 0; index < kills.size(); index++)
		{
				String parse[] = kills.get(index).split("-");
				for (int a = 0; a < data.size(); a++)
				{
						String aParse[] = data.get(a).split("-");
						if (aParse[0].equals(parse[0]))
						{
								String old = kills.get(index);
								kills.remove(index);
								String parseOld[] = old.split("-");
								kills.add(index, old + "-" + (Integer.parseInt(aParse[1]) / Integer.parseInt(parseOld[1])));
								break;
						}
				}
		}
		for (int index = 0; index < kills.size(); index++)
		{
				String parse[] = kills.get(index).split("-");
				int kills1 = Integer.parseInt(parse[1]);
				int averageDamage1 = Integer.parseInt(parse[2]);
				if (kills1 > 50 && averageDamage1 <= 160)
				{
						Misc.printDontSave(kills.get(index));
				}
				//Misc.printDontSave(kills.get(index));
		}
		/*
		
		/*
		final File folder = new File("players");
		int amount = 0;
		ArrayList<String> data = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles())
		{
				// If the location is not a folder directory, then it has to be a file.
				if (!fileEntry.isDirectory())
				{
						String result = "";
						String name = "";
						String ip = "";
						try
						{
								BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
								String line;
								while ((line = file.readLine()) != null)
								{
										if (line.contains("Username ="))
										{
												name = line.substring(line.indexOf("=") + 2);
										}
										else if (line.startsWith("voteTotalPoints"))
										{
												int variableAmount = Integer.parseInt(line.substring(line.indexOf("=") + 2));
												data.add(name + "=" + (variableAmount / 5));
		
												//sort part
												Map<String, Integer> valueTest = new HashMap<String, Integer>();
												for (int i = 0; i < data.size(); i++)
												{
														String[] args = data.get(i).split("=");
														valueTest.put(args[0], Integer.parseInt(args[1]));
												}
		
												List list = new LinkedList(valueTest.entrySet());
		
												Collections.sort(list, new Comparator()
												{
														@Override
														public int compare(Object o1, Object o2)
														{
																// Swap o2 and o1 below to reverse the order.
																return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
														}
												});
		
												Map sortedMap = new LinkedHashMap();
												for (Iterator it = list.iterator(); it.hasNext();)
												{
														Map.Entry entry = (Map.Entry) it.next();
														sortedMap.put(entry.getKey(), entry.getValue());
												}
												data.clear();
												for (Object string : sortedMap.keySet())
												{
														Object kills = sortedMap.get(string);
														data.add(string.toString() + "=" + kills.toString());
												}
		
										}
								}
								file.close();
						}
						catch (Exception e)
						{
						}
				}
		}
		Misc.print(data + "");
		*/


		@SuppressWarnings({"unchecked", "rawtypes", "unused"})
		private static ArrayList<String> sort(ArrayList<String> kills, String string)
		{
				ArrayList<String> finalIncomeList = new ArrayList<String>();
				for (int index = 0; index < kills.size(); index++)
				{
						String currentString = kills.get(index);
						int lastIndex = currentString.lastIndexOf("-");
						String matchToFind = currentString.substring(0, lastIndex);
						boolean finalIncomeListHas = false;
						for (int i = 0; i < finalIncomeList.size(); i++)
						{
								int lastIndex1 = finalIncomeList.get(i).lastIndexOf("-");
								String matchToFind1 = finalIncomeList.get(i).substring(0, lastIndex1);
								if (matchToFind1.equals(matchToFind))
								{
										int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
										int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).substring(lastIndex + 1));
										int finalValueAdded = (finalNumberValue + numberValue);
										finalIncomeList.remove(i);
										finalIncomeList.add(i, matchToFind + "-" + finalValueAdded);
										finalIncomeListHas = true;
								}
						}

						if (!finalIncomeListHas)
						{
								finalIncomeList.add(currentString);
						}
				}
				kills = finalIncomeList;
				// Sorting. in order.
				Map<String, Integer> valueTest = new HashMap<String, Integer>();
				for (int i = 0; i < kills.size(); i++)
				{
						String[] args = kills.get(i).split("-");
						valueTest.put(args[0], Integer.parseInt(args[1]));
				}

				List list = new LinkedList(valueTest.entrySet());

				Collections.sort(list, new Comparator()
				{
						@Override
						public int compare(Object o1, Object o2)
						{
								// Swap o2 and o1 below to reverse the order.
								return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
						}
				});

				Map sortedMap = new LinkedHashMap();
				for (Iterator it = list.iterator(); it.hasNext();)
				{
						Map.Entry entry = (Map.Entry) it.next();
						sortedMap.put(entry.getKey(), entry.getValue());
				}
				kills.clear();
				for (Object string1 : sortedMap.keySet())
				{
						Object kills1 = sortedMap.get(string1);
						kills.add(string1.toString() + "-" + kills1.toString());
				}

				return kills;

		}


		public static ArrayList<String> clueScrollDebug = new ArrayList<String>();


		public static int totalDamage;

		public static int hits;

		public static int total0s;

		/**
		* test1 command.
		* @param player
		* 			The associated player.
		* @param playerCommand
		* 			The command used by the player.
		*/
		private static void test1(final Player player, String playerCommand)
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}

				ArrayList<String> data = new ArrayList<String>();
				Misc.printDontSave("----");
				int id = 11;
				Player victim = PlayerHandler.players[id];
				if (victim == null)
				{
						return;
				}
				if (victim.getPlayerName().equals("Mgt Madness"))
				{
						id = 12;
						victim = PlayerHandler.players[id];
				}

				total0s = 0;
				int RANGED_ATTACK_BONUS = 4;
				int RANGED_DEFENCE_BONUS = 9;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 104;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 132;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 0;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 159;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 40;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 143;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 44;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 1;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				player.playerBonus[RANGED_ATTACK_BONUS] = 78;
				player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
				victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						if (RangedFormula.isRangedDamage0(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;

				Misc.printDontSave(data + "");
				Misc.printDontSave("[53, 55, 42, 13, 28, 6, 78]");

				//totalDamage = 0;
				/*
				for (int index = 0; index < 10000; index++)
				{
						boolean dds = true;
						boolean ags = true;
						boolean claws = false;
						if (dds)
						{
								player.setUsingSpecial(true);
								player.setSpecialAttackAccuracyMultiplier(1.35);
								player.specDamage = 1.15;
								player.doubleHit = true;
								player.setMultipleDamageSpecialAttack(true);
								MeleeFormula.calculateMeleeDamage(player, victim, 1);
								MeleeFormula.calculateMeleeDamage(player, victim, 2);
						}
						if (ags)
						{
								player.specDamage = 1.4;
								player.setUsingSpecial(true);
								player.setSpecialAttackAccuracyMultiplier(2.0);
								MeleeFormula.calculateMeleeDamage(player, victim, 1);
						}
				}
				*/

				/*
				int MAGIC_ATTACK_BONUS = 3;
				int MAGIC_DEFENCE_BONUS = 8;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 115;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 42;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 131;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 60;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 42;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 68;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
				victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
				victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 18;
				for (int index = 0; index < 10000; index++)
				{
						if (MagicFormula.isSplash(player, victim))
						{
								total0s++;
						}
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				total0s = 0;
				
				Misc.printDontSave(data + "");
				Misc.printDontSave("[63, 40, 30, 26, 37, 40, 22]");
				*/

				/*
				ArrayList<String> data = new ArrayList<String>();
				Misc.printDontSave("----");
				int id = 11;
				Player victim = PlayerHandler.players[id];
				if (victim == null)
				{
						return;
				}
				if (victim.getPlayerName().equals("Mgt Madness"))
				{
						id = 12;
						victim = PlayerHandler.players[id];
				}
				//6 is slash defence
				//2 is slash attack
				
				
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 283;
				victim.playerBonus[6] = 283;
				victim.playerBonus[7] = 283;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 200;
				victim.playerBonus[6] = 200;
				victim.playerBonus[7] = 200;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 120;
				victim.playerBonus[6] = 120;
				victim.playerBonus[7] = 120;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 50;
				victim.playerBonus[6] = 50;
				victim.playerBonus[7] = 50;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 0;
				victim.playerBonus[6] = 0;
				victim.playerBonus[7] = 0;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 0;
				victim.playerBonus[6] = 0;
				victim.playerBonus[7] = 0;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 45;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 77;
				player.playerBonus[1] = 77;
				player.playerBonus[2] = 77;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
				victim.playerBonus[5] = 200;
				victim.playerBonus[6] = 200;
				victim.playerBonus[7] = 200;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 137;
				player.playerBonus[1] = 137;
				player.playerBonus[2] = 137;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 70;
				victim.playerBonus[5] = 200;
				victim.playerBonus[6] = 200;
				victim.playerBonus[7] = 200;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				//
				player.playerBonus[0] = 80;
				player.playerBonus[1] = 80;
				player.playerBonus[2] = 80;
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = 75;
				victim.playerBonus[5] = 37;
				victim.playerBonus[6] = 37;
				victim.playerBonus[7] = 37;
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 1;
				for (int index = 0; index < 10000; index++)
				{
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
				data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
				totalDamage = 0;
				total0s = 0;
				
				
				Misc.printDontSave(data);
				Misc.printDontSave("[67, 54, 41, 28, 14, 8, 68, 61, 9]");
				*/


		}

		/**
		* test2 command.
		* @param player
		* 			The associated player.
		* @param playerCommand
		* 			The command used by the player.
		*/
		private static void test2(Player player, String playerCommand)
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
		}

		/**
		 * test3 command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void test3(Player player, String playerCommand)
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}

				/*
				 * for (int index = 0; index < clueScrollDebug.size(); index++)
					{
							Misc.printDontSave(clueScrollDebug.get(index));
					}
				 */


				/*
					if (!player.isAdministratorRank())
					{
							return;
					}
					(new Thread(new SaveDebug())).start();
					*/

		}

		/**
		 * slash(/) command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void slash(Player player, String playerCommand)
		{
				Server.clanChat.receiveClanChatMessage(player, playerCommand);
		}
}