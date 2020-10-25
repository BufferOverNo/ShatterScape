package game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotManager;
import game.content.bank.Bank;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.commands.AdministratorCommand;
import game.content.donator.DonationsNeeded;
import game.content.highscores.Highscores;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.QuestTab;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.log.GameTickLog;
import game.log.NewPlayerIpTracker;
import game.npc.NpcHandler;
import game.player.movement.Movement;
import game.player.punishment.Ban;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import network.connection.InvalidAttempt;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import network.packet.Stream;
import tools.CharacterBackup;
import utility.FileUtility;
import utility.Misc;

public class PlayerHandler
{

		/**
		 * Player slots are occupied from index 1 and above.
		 */
		public static Player players[] = new Player[ServerConstants.MAXIMUM_PLAYERS];

		public static List<Player> list = new ArrayList<Player>();

		public static int playerCount;

		public static boolean updateAnnounced;

		public static boolean updateRunning;

		public static int updateSeconds;

		public static long updateStartTime;

		private boolean kickAllPlayers = false;

		static
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						players[i] = null;
				}
		}

		public boolean newPlayerClient(Client client1)
		{
				int slot = -1;
				for (int i = 1; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
						return false;
				client1.handler = this;
				client1.setPlayerId(slot);
				players[slot] = client1;
				players[slot].setActive(true);
				return true;
		}

		public static int getPlayerCount()
		{
				return playerCount + (ServerConfiguration.ENABLE_BOTS ? BotManager.BOTS_AMOUNT : 0);
		}

		public void updatePlayerNames()
		{
				playerCount = 0;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] != null)
						{
								playerCount++;
						}
				}
		}

		public static boolean isPlayerOn(String playerName)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player loop = PlayerHandler.players[i];
						if (loop == null)
						{
								continue;
						}
						if (playerName.toLowerCase().equals(loop.getPlayerName().toLowerCase()))
						{
								return true;
						}
				}
				return false;
		}

		/**
		* All packets handled every 600ms.
		*/
		public void packetProcessing()
		{
				GameTickLog.packetTickDuration = System.currentTimeMillis();
				try
				{
						for (int j = 0; j < PlayerHandler.players.length; j++)
						{
								if (PlayerHandler.players[j] != null)
								{
										Player player = PlayerHandler.players[j];
										player.canFlush = false;
										//@formatter:off
										while (player.processQueuedPackets());
										//@formatter:on
										if (player.getInventoryUpdate())
										{
												ItemAssistant.resetItems(player, 3214); // Update inventory.
												player.getPA().requestUpdates();
												// Should only updated this if equipment has changed.

												if (ItemAssistant.updateEquipment(player))
												{
														ItemAssistant.calculateEquipmentBonuses(player);
														ItemAssistant.updateEquipmentBonusInterface(player);
														if (player.itemWorn)
														{
																SoundSystem.sendSound(player, 230, 0);
																player.itemWorn = false;
														}
														ItemAssistant.saveEquipment(player);
												}

												if (player.soundToSend > 0)
												{
														SoundSystem.sendSound(player, player.soundToSend, player.soundDelayToSend);
														player.soundToSend = 0;
												}

												for (int index = 0; index < player.skillTabMainToUpdate.size(); index++)
												{
														Skilling.updateSkillTabFrontTextMain(player, player.skillTabMainToUpdate.get(index));
												}
												player.skillTabMainToUpdate.clear();
												player.setInventoryUpdate(false);
										}
										if (player.bankUpdated)
										{
												ItemAssistant.resetItems(player, 5064);
												Bank.resetBank(player, false);
												player.bankUpdated = false;
										}

								}
						}
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}

				GameTickLog.packetTickDuration = System.currentTimeMillis() - GameTickLog.packetTickDuration;
		}

		@SuppressWarnings("unused")
		private static void shuffle()
		{
				list.clear();
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								list.add(PlayerHandler.players[j]);
						}
				}
				Collections.shuffle(list);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						players[i] = null;
				}

				for (int index = 0; index < list.size(); index++)
				{
						players[index + 1] = list.get(index);
				}

				// Also need to change each players getPlayerId and everyone's playerIdAttacking, getLastUnderAttackBy etc..

		}

		public static String currentTime = "";

		public static String currentDate = "";

		public void playerGameTick()
		{
				long time = System.currentTimeMillis();
				updatePlayerNames();
				boolean update1 = QuestTab.updateTimeAndDate();
				if (kickAllPlayers)
				{
						for (int i = 1; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (players[i] != null)
								{
										players[i].setDisconnected(true);
								}
						}
				}

				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null || !players[i].isActive())
						{
								continue;
						}
						try
						{
								if (hasDisconnectRequirements(players[i]))
								{
										LogOutUpdate.main(players, i);
										continue;
								}
								if (update1)
								{
										QuestTab.updateQuestTab(players[i]);
								}
								PlayerContentTick.preMovementContentTick(players[i]);
								Movement.postProcessing(players[i]);
								Movement.getNextPlayerMovement(players[i]);
								PlayerContentTick.afterMovementContentTick(players[i]);
								players[i].preProcessing();
								timedOutHandler(players[i]);
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}

				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null || !players[i].isActive())
						{
								continue;
						}
						try
						{
								if (hasDisconnectRequirements(players[i]))
								{
										LogOutUpdate.main(players, i);
								}
								else
								{

										if (!players[i].initialized)
										{
												int duplicate = 0;
												for (int a = 0; a < ServerConstants.MAXIMUM_PLAYERS; a++)
												{
														Player loop = PlayerHandler.players[a];
														if (loop == null)
														{
																continue;
														}
														if (loop.getPlayerName().equalsIgnoreCase(players[i].getPlayerName()))
														{
																duplicate++;
														}
												}
												if (duplicate > 1)
												{
														players[i] = null;
												}
												else
												{

														LogInUpdate.update(players[i]);
														players[i].initialized = true;
												}
										}
										else
										{
												players[i].update();
										}
								}
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}
				if (updateRunning && !updateAnnounced)
				{
						updateAnnounced = true;
						Server.UpdateServer = true;
				}
				if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000)))
				{
						kickAllPlayers = true;
				}

				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null || !players[i].isActive())
						{
								continue;
						}
						try
						{
								players[i].clearUpdateFlags();
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}
				if (kickAllPlayers)
				{
						if (System.currentTimeMillis() - timeKickedAllPlayers >= 60000)
						{
								timeKickedAllPlayers = System.currentTimeMillis();
								serverRestartContentUpdate(true, true);

						}
				}

				//PlayerHandler.shuffle();
				GameTickLog.playerTickDuration = System.currentTimeMillis() - time;
		}

		public boolean hasDisconnectRequirements(Player player)
		{
				/*
					if (player.isBot && player.isDisconnected())
					{
							return true;
					}
					*/
				if (player.getDead())
				{
						return false;
				}
				if (!player.isBot)
				{
						if (player.headIconPk == 2)
						{
								// If not in combat and has been not sending any packets for 5 minutes.
								if (!Combat.inCombat(player) && player.getTimeOutCounter() > 500)
								{
										return true;
								}
								return false;
						}
				}

				// Client crashes when logging in for the first time after launching client, randomly, so disconnect them.
				if (!Combat.inCombat(player) && !player.isBot && System.currentTimeMillis() - player.timeDied > 650)
				{
						if (player.getTimeOutCounter() >= 20 && Area.inWilderness(player))
						{
								return true;
						}
						if (player.getTimeOutCounter() >= 30)
						{
								return true;
						}
				}
				if (player.getTimeOutCounter() >= ServerConstants.TIMEOUT && !player.isBot && System.currentTimeMillis() - player.timeDied > 650)
				{
						return true;
				}
				if (kickAllPlayers)
				{
						return true;
				}

				if (player.manualLogOut)
				{
						player.getOutStream().createFrame(109);
						player.canFlush = true;
						player.flushOutStream();
						player.canFlush = false;
						return true;
				}
				if (Combat.inCombat(player) && !player.isAdministratorRank() && !player.isBot || System.currentTimeMillis() - player.timeDied <= 650)
				{
						return false;
				}

				if (!player.isAdministratorRank())
				{
						if (System.currentTimeMillis() - player.timeNpcAttackedPlayerLogOutTimer <= 10000)
						{
								return false;
						}
				}

				if (player.doingAnAction() && !player.isBot)
				{
						return false;
				}


				if (!player.isDisconnected())
				{
						return false;
				}
				return true;
		}

		public static boolean canTakeAction;

		/**
		 * True to logOut players, called after backup.zip is created.
		 */
		public static boolean logOut;

		/**
		 * True to restart server, called after backup.zip is created.
		 */
		public static boolean restart;

		public static long timeKickedAllPlayers;

		public void serverRestartContentUpdate(boolean restart, boolean logOut)
		{
				long time = System.currentTimeMillis();
				if (logOut)
				{
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								Player player = players[i];
								if (player == null || !player.isActive())
								{
										continue;
								}
								try
								{
										LogOutUpdate.main(players, i);
								}
								catch (Exception e)
								{
										e.printStackTrace();
								}
						}
				}
				HighscoresDaily.getInstance().saveDailyHighscoresType();
				CoinEconomyTracker.updateCoinEconomyLog();
				GameTickLog.saveLagLogFile();
				Highscores.saveHighscoresFiles();
				ItemTransferLog.saveTransferItemLog();
				InvalidAttempt.saveInvalidAttemptLog();
				FileUtility.saveArrayContents("./backup/logs/vote items.txt", VoteManager.voteRareItems);
				FileUtility.deleteAllLines("./backup/logs/tournament titles.txt");
				FileUtility.saveArrayContents("./backup/logs/tournament titles.txt", Tournament.tournamentTitleWinners);
				FileUtility.deleteAllLines("./backup/logs/ipmute.txt");
				FileUtility.saveArrayContents("./backup/logs/ipmute.txt", IpMute.ipMutedData);
				FileUtility.deleteAllLines("./backup/logs/ban.txt");
				FileUtility.saveArrayContents("./backup/logs/ban.txt", Ban.bannedList);
				FileUtility.deleteAllLines("./backup/logs/ragban.txt");
				FileUtility.saveArrayContents("./backup/logs/ragban.txt", RagBan.ragBanList);
				AdministratorCommand.saveAllPacketAbuse(null);
				VoteManager.updateVoteBans();
				ClanChatHandler.serverRestart(logOut);
				getUpTime();
				Tournament.saveLastEvenType();
				NewPlayerIpTracker.save("SAVE DATA", "");
				HighscoresHallOfFame.save();
				DonationsNeeded.save();
				CommunityEvent.save();
				FileUtility.addLineOnTxt("backup/logs/system log/save log.txt", Misc.getDate() + " Saved. Logout: " + logOut + ". Restart: " + restart);

				// Leave here, this gathers all exploits made and saves it to a file.
				if (PacketHandler.packetAbuseAlert.size() > 0)
				{
						PacketHandler.packetAbuseAlert.add(0, Misc.getDate());
						PacketHandler.packetAbuseAlert.add("------------------------------------------------------------");
				}
				FileUtility.saveArrayContents("./backup/logs/packet abuse/packet abuse alert.txt", PacketHandler.packetAbuseAlert);

				PlayerHandler.logOut = logOut;
				//PlayerHandler.restart = restart;
				// Must be last, because this zips all the logs and character files.
				(new Thread(new CharacterBackup())).start();

				// This saves all Misc.print(string);
				FileUtility.saveArrayContents("./backup/logs/system log/output.txt", Misc.consolePrint);
				Misc.consolePrint.clear();

				if (!logOut && !restart)
				{
						Misc.print("Server content save took: " + (System.currentTimeMillis() - time) + " ms");
				}
		}

		private void getUpTime()
		{
				int hours = (int) ((System.currentTimeMillis() - Server.timeServerOnline) / 3600000);
				if (hours > 0)
				{
						Misc.print("Uptime: " + hours + " hour" + (hours == 1 ? "." : "s."));
				}
				else
				{
						Misc.print("Uptime: " + ((System.currentTimeMillis() - Server.timeServerOnline) / 60000) + " minutes.");
				}

		}

		public void updateNpc(Player plr, Stream str)
		{
				if (plr.isBot)
				{
						return;
				}
				updateBlock.currentOffset = 0;
				if (!plr.isBot)
				{
						str.createFrameVarSizeWord(65);
				}
				str.initBitAccess();

				str.writeBits(8, plr.npcListSize);
				int size = plr.npcListSize;
				plr.npcListSize = 0;
				for (int i = 0; i < size; i++)
				{
						if (plr.rebuildNpcList == false && plr.playerAssistant.withinDistance(plr.npcList[i]) == true)
						{
								plr.npcList[i].updateNpcMovement(str);
								plr.npcList[i].appendNpcUpdateBlock(updateBlock, plr);
								plr.npcList[plr.npcListSize++] = plr.npcList[i];
						}
						else
						{
								int id = plr.npcList[i].npcIndex;
								plr.npcInListBitmap[id >> 3] &= ~(1 << (id & 7));
								str.writeBits(1, 1);
								str.writeBits(2, 3);
						}
				}

				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] != null)
						{
								int id = NpcHandler.npcs[i].npcIndex;
								if (plr.rebuildNpcList == false && (plr.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0)
								{

								}
								else if (plr.playerAssistant.withinDistance(NpcHandler.npcs[i]) == false)
								{

								}
								else
								{
										plr.addNewNpc(NpcHandler.npcs[i], str, updateBlock);
								}
						}
				}

				plr.rebuildNpcList = false;

				if (updateBlock.currentOffset > 0)
				{
						str.writeBits(14, 16383);
						str.finishBitAccess();
						str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
				}
				else
				{
						str.finishBitAccess();
				}
				if (!plr.isBot)
				{
						str.endFrameVarSizeWord();
				}
		}

		public Stream updateBlock = new Stream(new byte[ServerConstants.BUFFER_SIZE]);

		public void updatePlayer(Player plr, Stream str)
		{
				if (plr.isBot)
				{
						return;
				}
				updateBlock.currentOffset = 0;
				if (updateRunning && !updateAnnounced && !plr.isBot)
				{
						str.createFrame(114);
						str.writeWordBigEndian(updateSeconds * 50 / 30);
				}
				plr.updateThisPlayerMovement(str);
				boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
				plr.setChatTextUpdateRequired(false);
				plr.appendPlayerUpdateBlock(updateBlock);
				plr.setChatTextUpdateRequired(saveChatTextUpdate);
				str.writeBits(8, plr.playerListSize);
				int size = plr.playerListSize;
				if (size > 255)
				{
						size = 255;
				}
				plr.playerListSize = 0;
				for (int i = 0; i < size; i++)
				{
						if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.playerAssistant.withinDistance(plr.playerList[i]))
						{
								plr.playerList[i].updatePlayerMovement(str);
								plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
								plr.playerList[plr.playerListSize++] = plr.playerList[i];
						}
						else
						{
								int id = plr.playerList[i].getPlayerId();
								plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
								str.writeBits(1, 1);
								str.writeBits(2, 3);
						}
				}
				/*
				int amountNeededToUpdate = 0;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null || !players[i].isActive() || players[i] == plr)
						{
								continue;
						}
						int id = players[i].getPlayerId();
						if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0)
						{
								continue;
						}
						if (!plr.playerAssistant.withinDistance(players[i]))
						{
								continue;
						}
						amountNeededToUpdate++;
				}
				*/
				int max = 40;
				int currentCount = 0;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (players[i] == null || !players[i].isActive() || players[i] == plr)
						{
								continue;
						}
						int id = players[i].getPlayerId();
						if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0)
						{
								continue;
						}
						if (!plr.playerAssistant.withinDistance(players[i]))
						{
								continue;
						}
						currentCount++;
						plr.addNewPlayer(players[i], str, updateBlock);
						if (currentCount == max)
						{
								break;
						}
				}

				if (updateBlock.currentOffset > 0)
				{
						str.writeBits(11, 2047);
						str.finishBitAccess();
						str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
				}
				else
				{
						str.finishBitAccess();
				}

				if (!plr.isBot)
				{
						str.endFrameVarSizeWord();
				}
		}

		private void timedOutHandler(Player player)
		{
				player.setTimeOutCounter(player.getTimeOutCounter() + 1);
				if (player.getTimeOutCounter() > ServerConstants.TIMEOUT && !player.isBot)
				{
						player.setDisconnected(true);
				}
		}

}