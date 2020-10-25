package game.content.worldevent;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.highscores.HighscoresTournament;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.Teleport;
import game.content.quicksetup.QuickSetUp;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.object.custom.Object;
import game.object.custom.Objects;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Tournament.
 * @author MGT Madness, created on 06-03-2017.
 */
public class Tournament
{

		/**
		 * How many game ticks untill tournament starts once it's announced.
		 */
		public final static int TOURNAMENT_WILL_START_IN = 200; //100 is 1 minute.

		/**
		 * How many seconds untill tournament starts once it's announced.
		 */
		public final static int TOURNAMENT_STARTED_WAIT_TIME = 300; // How many game ticks for the players to gear up once Cowkiller spawns.

		private final static int REWARD = 1500;

		private final static String LAST_EVENT_LOCATION = "backup/logs/tournament event.txt";

		private final static String[] eventLists = {"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows", "Dharok melee", "Max hybrid", "Main Nh"};

		public final static int[] eventShopIds = {78, 79, 76, 77, 80, 81, 82};

		/**
		 * Used for setting combat skills.
		 */
		private final static String[] eventSkillString = {"PURE", "BERSERKER", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN"};

		public static int locationIndex = -1;

		/**
		 * Using tick count instead of time.
		 */
		private static int tournamentTickCount;

		/**
		 * Amount of players lost this round.
		 */
		private static int currentLostAmount;

		/**
		 * Amount of losses needed to start a new round.
		 */
		private static int lossesNeeded;

		/**
		 * Current tournament status.
		 */
		public static String tournamentStatus = "";

		/**
		 * True if the event is active, started by an Admin via a command.
		 */
		public static boolean tournamentActive;

		public static int playersEnteredTournament;


		/**
		 * List of players that entered the lobby.
		 */
		public static ArrayList<Integer> playerListLobby = new ArrayList<Integer>();


		/**
		 * List of players that are participating in the tournament.
		 */
		public static ArrayList<Integer> playerListTournament = new ArrayList<Integer>();


		/**
		 * List of players that have won the titles, so i can remove it from character file when the next winner of the same title is announced.
		 */
		public static ArrayList<String> tournamentTitleWinners = new ArrayList<String>();

		/**
		 * Current event type.
		 */
		public static String eventType = eventLists[eventLists.length - 1];

		public static void tournamenTick()
		{
				if (!tournamentActive)
				{
						return;
				}
				tournamentTickCount++;
				switch (tournamentStatus)
				{
						case "TOURNAMENT ANNOUNCED":
								if (tournamentTickCount == TOURNAMENT_WILL_START_IN)
								{
										tournamentTickCount = 0;
										tournamentStatus = "TOURNAMENT LOBBY WAIT";
										Announcement.announce(eventType + " fighting begins in 3 minutes.", ServerConstants.DARK_BLUE);
										Announcement.announce("Talk to Cow31337Killer at the edge furnace to join!", ServerConstants.DARK_BLUE);
										NpcHandler.spawnNpc(null, 5210, 3108, 3499, 0, false, false);
										locationIndex = 0;
										new Object(29300, 3328, 4753, 20, 0, 10, 0, -1); // Box of health.
										new Object(76, 3327, 4753, 20, 0, 10, 0, -1); // Chest.
										Region r = Region.getRegion(3327, 4745);
										if (r != null)
										{
												r.verifiedObjects.add(new Objects(29300, 3328, 4753, 20, 0, 10, 0));
												r.verifiedObjects.add(new Objects(76, 3327, 4753, 20, 0, 10, 0));
										}
								}
								break;
						case "TOURNAMENT LOBBY WAIT":
								if (tournamentTickCount == TOURNAMENT_STARTED_WAIT_TIME)
								{
										tournamentStart();
								}
								else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 100))
								{
										Announcement.announce(eventType + " fighting begins in 1 minute.", ServerConstants.DARK_BLUE);
										Announcement.announce("Talk to Cow31337Killer at the edge furnace to join!", ServerConstants.DARK_BLUE);
								}
								else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 200))
								{
										Announcement.announce(eventType + " fighting begins in 2 minutes.", ServerConstants.DARK_BLUE);
										Announcement.announce("Talk to Cow31337Killer at the edge furnace to join!", ServerConstants.DARK_BLUE);
								}
								break;

						case "TOURNAMENT NEXT ROUND":
								// 60 seconds passed.
								if (tournamentTickCount == 100)
								{
										currentLostAmount = 0;
										int playersLeft = playerListTournament.size();
										boolean isEvenNumber = playersLeft % 2 == 0;

										if (isEvenNumber)
										{
												lossesNeeded = playersLeft / 2;
										}
										else
										{

												lossesNeeded = (playersLeft - 1) / 2;
										}
										teleportPlayersToArena();
								}
								break;
				}
		}

		/**
		 * When the tournament has started.
		 */
		private static void tournamentStart()
		{

				boolean has8 = playerListLobby.size() >= 2;

				int maximumEntries = 0;
				if (has8)
				{
						maximumEntries = playerListLobby.size() & ~1; // Rounds the given number to the lowest even number.
						lossesNeeded = maximumEntries / 2;
				}
				else
				{
						Announcement.announce("Not enough players to start, cancelled.", ServerConstants.DARK_BLUE);
						cancelTournament();
						return;
				}
				tournamentTickCount = 0;
				tournamentStatus = "TOURNAMENT STARTED";
				for (int index = 0; index < playerListLobby.size(); index++)
				{
						Player loop = PlayerHandler.players[playerListLobby.get(index)];
						if (loop == null)
						{
								continue;
						}
						if (loop.isTeleporting())
						{
								continue;
						}
						playerListTournament.add(playerListLobby.get(index));
				}
				updateText(null);
				playersEnteredTournament = playerListTournament.size();
				Announcement.announce("The " + eventType + " tournament has started. Winner will receive x" + (playersEnteredTournament * REWARD) + " blood money!", ServerConstants.DARK_BLUE);
				teleportPlayersToArena();

		}

		private static void cancelTournament()
		{
				// Delete cow1337killer npc.
				for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++)
				{
						Npc npc = NpcHandler.npcs[index];
						if (npc == null)
						{
								continue;
						}
						if (npc.npcType == 5210)
						{
								Pet.deletePet(npc);
								break;
						}
				}

				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
				{
						Player loop = PlayerHandler.players[index];
						if (loop == null)
						{
								continue;
						}
						if (loop.getHeight() != 20)
						{
								continue;
						}
						Teleport.spellTeleport(loop, 3105 + Misc.random(3), 3498 + Misc.random(4), 0, false);
						//Teleport.spellTeleport(player, 3086 + Misc.random(3), 3508 + Misc.random(4), 0, false);
				}
				tournamentActive = false;
				tournamentTickCount = 0;
				tournamentStatus = "";
				currentLostAmount = 0;
				lossesNeeded = 0;
				playerListLobby.clear();
				playerListTournament.clear();
				locationIndex = -1;
				playersEnteredTournament = 0;
		}

		public final static int TOURNAMENT_ARENA_X = 3328;

		public final static int TOURNAMENT_ARENA_Y = 4767;

		private final static int TELEPORT_START_X = 3328;

		private final static int TELEPORT_START_Y = 4757;

		private static void teleportPlayersToArena()
		{
				Collections.shuffle(playerListTournament);

				int pairs = 0;
				int x = 0;
				int y = 0;
				for (int index = 0; index < playerListTournament.size(); index++)
				{
						Player loop = PlayerHandler.players[playerListTournament.get(index)];
						if (loop == null)
						{
								continue;
						}
						pairs++;
						if (pairs == 1)
						{
								x = TOURNAMENT_ARENA_X - 10 + Misc.random(20);
								y = TOURNAMENT_ARENA_Y - 7 + Misc.random(14);
								// If a partner won't be available because it is an odd number player list, then stop and inform the odd player out.
								if ((index + 1) > playerListTournament.size() - 1)
								{
										// The other finalist logs off.
										if (playerListTournament.size() == 1)
										{
												finalistDisconnection(loop);
												return;
										}
										Skull.goldenSkull(loop);
										loop.getPA().sendMessage(ServerConstants.BLUE_COL + "Could not find a challenger for you, you are still in the tournament.");
										return;
								}
								loop.tournamentTarget = playerListTournament.get(index + 1);
								loop.getPA().movePlayer(x, y, 20);
								Player challenger = PlayerHandler.players[playerListTournament.get(index + 1)];
								loop.getPA().sendMessage(ServerConstants.RED_COL + "Your challenger is " + challenger.getPlayerName() + "!");
								loop.getPA().createPlayerHints(10, challenger.getPlayerId());
								loop.setDuelCount(4);
								Skull.goldenSkull(loop);

								CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												loop.duelForceChatCount--;
												loop.forcedChat("" + loop.duelForceChatCount + "");
												if (loop.duelForceChatCount == 0)
												{
														container.stop();
												}
										}

										@Override
										public void stop()
										{
												loop.forcedChat("FIGHT!");
												loop.duelForceChatCount = 4;
												loop.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
												loop.setDuelCount(0);
										}
								}, 2);

						}
						else if (pairs == 2)
						{
								loop.tournamentTarget = playerListTournament.get(index - 1);
								loop.getPA().movePlayer(x + 1, y, 20);
								Player challenger = PlayerHandler.players[playerListTournament.get(index - 1)];
								loop.getPA().sendMessage(ServerConstants.RED_COL + "Your challenger is " + challenger.getPlayerName() + "!");
								loop.getPA().createPlayerHints(10, challenger.getPlayerId());
								pairs = 0;
								loop.setDuelCount(4);
								Skull.goldenSkull(loop);

								CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
								{

										@Override
										public void execute(CycleEventContainer container)
										{
												loop.duelForceChatCount--;
												loop.forcedChat("" + loop.duelForceChatCount + "");
												if (loop.duelForceChatCount == 0)
												{
														container.stop();
												}
										}

										@Override
										public void stop()
										{
												loop.forcedChat("FIGHT!");
												loop.duelForceChatCount = 4;
												loop.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
												loop.setDuelCount(0);
										}

								}, 2);
						}
				}

		}

		/**
		* Added to log out method and log-in, incase server crashes so it won't register the log out part.
		* @param player
		*/
		public static void logOutUpdate(Player player, boolean logIn)
		{
				if (logIn)
				{
						switch (tournamentStatus)
						{
								case "TOURNAMENT ANNOUNCED":
										player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " tournament has been announced.");
										break;
								case "TOURNAMENT LOBBY WAIT":
										player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " fighting will start soon, talk to Cow31337Killer.");
										break;

								case "TOURNAMENT NEXT ROUND":
										player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " tournament is active!");
										break;
						}
				}
				if (player.getHeight() != 20)
				{
						return;
				}
				if (logIn && player.getHeight() == 20)
				{
						player.setHeight(0);
				}


				ItemAssistant.deleteAllItems(player);
				Combat.updatePlayerStance(player);
				Skull.clearSkull(player);

				if (!logIn)
				{
						player.getPA().createPlayerHints(10, -1);
						if (player.tournamentTarget >= 0)
						{
								Player other = PlayerHandler.players[player.tournamentTarget];
								if (other != null)
								{
										playerDied(other, player);
								}
						}
				}
				player.tournamentTarget = -1;
				removeFromTournamentLobby(player.getPlayerId());
		}

		/**
		 * Other player disconnected while in lobby and we are both in finals.
		 * @param player
		 */
		public static void finalistDisconnection(Player player)
		{
				player.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
				player.getPA().createPlayerHints(10, -1);
				player.tournamentTarget = -1;
				player.getPA().sendMessage(ServerConstants.RED_COL + "The finalist has disonnected, you are given an automatic win!");
				awardWinner(player);
				cancelTournament();
		}

		private static void awardWinner(Player player)
		{
				int amount = (playersEnteredTournament * REWARD);
				String title = Misc.capitalize(eventType);
				title = title.replace("Main Hybrid Welfare", "Main Hybrid");
				title = title.replace("Main Hybrid Barrows", "Main Hybrid");
				title = title.replace("Max Hybrid", "Main Hybrid");
				Announcement.announce(player.getPlayerName() + " has won " + Misc.formatRunescapeStyle(amount) + " blood money and claimed the '#1 " + title + "' title!", ServerConstants.DARK_BLUE);
				CommunityEvent.eventNames.add(player.getPlayerName().toLowerCase() + "-" + amount);
				player.getPA().sendMessage(ServerConstants.GREEN_COL + "::claimevent to claim your tournament prize.");
				removePreviousTitleWinners("#1 " + title);
				PlayerTitle.setTitle(player, "#1 " + title, false);
				tournamentTitleWinners.add(player.getPlayerName().toLowerCase() + "-" + "#1 " + title);
				switch (eventType)
				{
						case "Pure tribrid":
								player.hybridTournamentsWon++;
								break;

						case "Berserker hybrid":
								player.tribridTournamentsWon++;
								break;

						case "Main hybrid welfare":
						case "Main hybrid barrows":
						case "Max hybrid":
						case "Main Nh":
								player.meleeTournamentsWon++;
								break;
				}

				HighscoresTournament.getInstance().sortHighscores(player);

		}


		/**
		 * Using the tournamentTitleWinners arraylist, find the names of the players with the same given titleString and remove it from them if they are online or offline.
		 */
		private static void removePreviousTitleWinners(String titleString)
		{
				for (int index = 0; index < tournamentTitleWinners.size(); index++)
				{
						String parse[] = tournamentTitleWinners.get(index).split("-");
						String name = parse[0];
						String title = parse[1];

						// Match found, remove from online or offline.
						if (titleString.equals(title))
						{
								boolean online = false;
								for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
								{
										if (PlayerHandler.players[i] != null)
										{
												if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
												{
														PlayerTitle.setTitle(PlayerHandler.players[i], "", false);
														online = true;
														break;
												}
										}
								}

								if (!online)
								{
										try
										{
												name = name.toLowerCase();
												BufferedReader file = new BufferedReader(new FileReader(ServerConstants.CHARACTER_LOCATION + name + ".txt"));
												String line;
												String input = "";
												while ((line = file.readLine()) != null)
												{
														if (line.contains("playerTitle ="))
														{
																line = "playerTitle = ";
														}
														input += line + '\n';
												}
												FileOutputStream File = new FileOutputStream(ServerConstants.CHARACTER_LOCATION + name + ".txt");
												File.write(input.getBytes());
												file.close();
												File.close();
										}
										catch (Exception e)
										{
										}
								}
								tournamentTitleWinners.remove(index);
								break;
						}
				}

		}

		private static void announceToLobby(String text)
		{
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
				{
						Player loop = PlayerHandler.players[index];
						if (loop == null)
						{
								continue;
						}
						if (loop.getHeight() != 20)
						{
								continue;
						}
						loop.getPA().sendMessage(text);
				}
		}

		public static void playerDied(Player killer, Player victim)
		{
				if (Tournament.locationIndex == -1)
				{
						return;
				}
				victim.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
				killer.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
				killer.tournamentTarget = -1;
				victim.tournamentTarget = -1;
				Skull.clearSkull(victim);
				killer.getPA().createPlayerHints(10, -1);
				victim.getPA().createPlayerHints(10, -1);
				QuickSetUp.heal(killer);
				QuickSetUp.heal(victim);
				announceToLobby(ServerConstants.RED_COL + killer.getPlayerName() + " has knocked out " + victim.getPlayerName() + "!");
				currentLostAmount++;
				for (int index = 0; index < playerListTournament.size(); index++)
				{
						if (victim.getPlayerId() == playerListTournament.get(index))
						{
								playerListTournament.remove(index);
								updateText(null);
								break;
						}
				}
				// 1 player left, means winner!
				if (playerListTournament.size() == 1)
				{
						awardWinner(killer);
						awardRunnerUp(victim);
						cancelTournament();
						return;
				}
				if (currentLostAmount == lossesNeeded)
				{
						tournamentTickCount = 0;
						tournamentStatus = "TOURNAMENT NEXT ROUND";
						String stage = "finals";
						int playersLeft = playerListTournament.size();
						if (playersLeft == 2)
						{
								stage = "finals";
						}
						else if (playersLeft <= 5)
						{
								stage = "semi-finals";
						}
						else if (playersLeft <= 8)
						{
								stage = "quarter-finals";
						}
						else if (playersLeft <= 16)
						{
								stage = "group stage 1";
						}
						else if (playersLeft <= 32)
						{
								stage = "group stage 2";
						}
						else if (playersLeft <= 64)
						{
								stage = "group stage 3";
						}
						else if (playersLeft <= 128)
						{
								stage = "group stage 4";
						}
						if (stage.equals("finals"))
						{
								Player playerOne = PlayerHandler.players[playerListTournament.get(0)];
								Player playerTwo = PlayerHandler.players[playerListTournament.get(1)];
								Announcement.announce("The " + eventType + " finals is between " + playerOne.getPlayerName() + " and " + playerTwo.getPlayerName() + "!", ServerConstants.DARK_BLUE);
						}
						else
						{
								Announcement.announce("The " + eventType + " has reached the " + stage + "!", ServerConstants.DARK_BLUE);
						}
				}
		}

		private static void awardRunnerUp(Player victim)
		{
				int amount = (playersEnteredTournament * (REWARD / 2));
				CommunityEvent.eventNames.add(victim.getPlayerName().toLowerCase() + "-" + amount);
				Announcement.announce("The runner up " + victim.getPlayerName() + " has won " + Misc.formatRunescapeStyle(amount) + " blood money.", ServerConstants.DARK_BLUE);
				victim.getPA().sendMessage(ServerConstants.GREEN_COL + "::claimevent to claim your tournament prize.");

		}

		public static void loadNewTournament(String command)
		{
				int tournamentId = 0;
				try
				{
						tournamentId = Integer.parseInt(command.substring(11));
				}
				catch (Exception e)
				{

				}
				if (tournamentId > eventLists.length - 1)
				{
						return;
				}
				startNewTournament(tournamentId);
		}

		private static void startNewTournament(int tournamentId)
		{
				cancelTournament();
				tournamentActive = true;
				tournamentStatus = "TOURNAMENT ANNOUNCED";
				eventType = eventLists[tournamentId];
				Announcement.announce("The " + eventType + " tournament will start in 2 minutes!", ServerConstants.DARK_BLUE);
				Announcement.announce("Finalists will receive up to 40k blood money!", ServerConstants.DARK_BLUE);
				Announcement.announce("Head to edge furnace and wait for Cow31337Killer to spawn to receive free items.", ServerConstants.DARK_BLUE);

		}

		/**
		* Load the last event used from the text file and load the location data.
		*/
		public static void tournamentStartUp()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(LAST_EVENT_LOCATION));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										eventType = line;
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}

				try
				{
						BufferedReader file = new BufferedReader(new FileReader("backup/logs/tournament titles.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										tournamentTitleWinners.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * Save the last event used on server shutdown.
		 */
		public static void saveLastEvenType()
		{
				FileUtility.deleteAllLines(LAST_EVENT_LOCATION);
				FileUtility.addLineOnTxt(LAST_EVENT_LOCATION, eventType);
		}

		public static void talkToCowKiller(Player player)
		{
				if (!Tournament.tournamentStatus.equals("TOURNAMENT LOBBY WAIT") && !Tournament.tournamentStatus.equals("TOURNAMENT STARTED") && !Tournament.tournamentStatus.equals("TOURNAMENT NEXT ROUND"))
				{
						return;
				}

				if (!Bank.hasBankingRequirements(player, true))
				{
						return;
				}
				QuickSetUp.bankInventoryAndEquipment(player);
				if (ItemAssistant.hasEquipment(player))
				{
						player.getPA().sendMessage("You cannot enter the tournament with items.");
						return;
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] > 0)
						{
								player.getPA().sendMessage("You cannot enter the tournament with items.");
								return;
						}
				}
				if (player.getPetId() > 0 && player.getPetId() != 6869)
				{
						player.getPA().sendMessage("Pick up your pet before entering tournament!");
						return;
				}
				// 5 would be the player id.
				Teleport.spellTeleport(player, TELEPORT_START_X, TELEPORT_START_Y, 5 * 4, false);
				player.teleBlockEndTime = System.currentTimeMillis() + 20000;
				for (int index = 0; index < eventLists.length; index++)
				{
						if (eventLists[index].equals(eventType))
						{
								final int value = index;
								CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												if (eventType.contains("Dharok"))
												{
														QuickSetUp.mainDharokTournament(player, eventType, eventSkillString[value]);
												}
												else if (eventType.contains("Max hybrid"))
												{
														QuickSetUp.maxBridTournament(player, eventType, eventSkillString[value]);
												}
												else
												{
														QuickSetUp.mainHybridTournament(player, eventType, eventSkillString[value]);
												}
												container.stop();
										}

										@Override
										public void stop()
										{
										}
								}, 10);
								break;
						}
				}
				playerListLobby.add(player.getPlayerId());
				updateText(player);
		}

		public static void removeFromTournamentLobby(int playerId)
		{
				if (!tournamentActive)
				{
						return;
				}
				for (int index = 0; index < playerListLobby.size(); index++)
				{
						if (playerId == playerListLobby.get(index))
						{
								playerListLobby.remove(index);
								break;
						}
				}
				for (int index = 0; index < playerListTournament.size(); index++)
				{
						if (playerId == playerListTournament.get(index))
						{
								playerListTournament.remove(index);
								break;
						}
				}
				updateText(null);
		}

		public static void updateText(Player player)
		{
				if (player != null)
				{
						player.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
						player.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
				}
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
				{
						Player loop = PlayerHandler.players[index];
						if (loop == null)
						{
								continue;
						}
						if (loop.getHeight() != 20)
						{
								continue;
						}

						loop.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
						loop.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
				}

		}

		public static void openShop(Player player)
		{
				int shopId = 0;
				for (int index = 0; index < eventLists.length; index++)
				{
						if (eventLists[index].equals(eventType))
						{
								shopId = eventShopIds[index];
								break;
						}
				}
				if (shopId == 0)
				{
						return;
				}
				player.getShops().openShop(shopId);
		}

		/**
		 * This method is called every minute.
		 */
		public static void currentTime()
		{
				DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				Calendar cal = Calendar.getInstance();
				String time = dateFormat.format(cal.getTime());
				if (time.equals("05:00 PM") || time.equals("01:00 AM") || time.equals("09:00 AM"))
				{
						Tournament.startNewTournament(Misc.random(eventLists.length - 1));
				}

		}



}
