package game.content.commands;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.bot.BotManager;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Death;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.vsnpc.CombatNpc;
import game.content.consumable.Food;
import game.content.consumable.Potions;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.PlayerGameTime;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.WelcomeMessage;
import game.content.music.SoundSystem;
import game.content.prayer.PrayerBook;
import game.content.quicksetup.QuickSetUp;
import game.content.shop.ShopHandler;
import game.content.skilling.Skilling;
import game.content.wildernessbonus.KillReward;
import game.content.worldevent.Tournament;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.GameTickLog;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import game.player.punishment.Ban;
import game.player.punishment.Blacklist;
import game.player.punishment.RagBan;
import network.connection.VoteManager;
import network.login.RS2LoginProtocolDecoder;
import network.packet.PacketHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Administrator commands.
 *
 * @author MGT Madness, created on 12-12-2013.
 */
public class AdministratorCommand
{
		/**
		 * Commands eligible for Administrators only.
		 */
		public static void administratorCommands(Player player, String command)
		{
				if (command.startsWith("pass"))
				{
						String name = command.substring(5);
						boolean online = false;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
										{
												player.getPA().sendMessage(name + " is online: '" + PlayerHandler.players[i].playerPass + "'");
												player.getPA().sendMessage(PlayerHandler.players[i].addressIp);
												player.getPA().sendMessage(PlayerHandler.players[i].addressUid);
												player.getPA().sendMessage(PlayerHandler.players[i].addressMac);
												online = true;
												break;
										}
								}
						}

						if (!online)
						{
								String text1 = "";
								String text2 = "";
								String text3 = "";
								try
								{
										BufferedReader file = new BufferedReader(new FileReader(ServerConstants.CHARACTER_LOCATION + name + ".txt"));
										String line;
										while ((line = file.readLine()) != null)
										{
												if (line.startsWith("Password = "))
												{
														String password = line.substring(11);
														try
														{
																text1 = name + " is offline password of: '" + password + "'";
														}
														catch (Exception e)
														{
																e.printStackTrace();
														}
												}
												else if (line.startsWith("lastSavedIpAddress = "))
												{
														text2 = line.substring(20);
												}
												else if (line.startsWith("addressUid = "))
												{
														text3 = line.substring(13);
												}
										}
										file.close();
								}
								catch (Exception e)
								{
								}
								player.getPA().sendMessage(text1);
								player.getPA().sendMessage(text2);
								player.getPA().sendMessage(text3);
						}
				}
				else if (command.equals("rs2"))
				{
						RS2LoginProtocolDecoder.printOutAddress = !RS2LoginProtocolDecoder.printOutAddress;
						player.getPA().sendMessage("Print out mac & uid set to: " + RS2LoginProtocolDecoder.printOutAddress);
				}
				else if (command.startsWith("savelogs"))
				{
						saveLogs();
						return;
				}
				else if (command.startsWith("ragban"))
				{
						RagBan.ragBan(player, command);
						return;
				}
				else if (command.startsWith("unragban"))
				{
						RagBan.unRagBan(player, command);
						return;
				}
				else if (command.startsWith("voteban"))
				{
						String uid = command.substring(8);
						VoteManager.voteBan.add(uid);
						player.getPA().sendMessage("You have banned this uid from voting: " + uid);
						return;
				}
				else if (command.equals("voteclearbans"))
				{
						VoteManager.voteBan.clear();
						player.getPA().sendMessage("Vote ban list has been cleared.");
						return;
				}
				else if (command.startsWith("voteunban"))
				{
						String uid = command.substring(10);
						for (int index = 0; index < VoteManager.voteBan.size(); index++)
						{
								if (VoteManager.voteBan.get(index).contains(uid))
								{
										player.getPA().sendMessage("You have unbanned this uid from voting: " + VoteManager.voteBan.get(index));
										VoteManager.voteBan.remove(index);
										return;
								}
						}
						player.getPA().sendMessage("Uid not found: " + uid);
						return;
				}
				else if (command.startsWith("ipban"))
				{
						Blacklist.blacklistCommand(player, command);
						return;
				}
				else if (command.startsWith("accountban"))
				{
						Ban.ban(player, command);
				}
				else if (command.startsWith("unban"))
				{
						Ban.unBan(player, command);
				}
				else if (command.equals("remy"))
				{
						int maxi = 0;
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (loop.getPlayerName().equals("Remy E"))
								{
										maxi = index;
										break;
								}
						}
						final int maxis = maxi;
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (loop.isBot && loop.botPkType.isEmpty())
								{
										loop.gameModeTitle = "";
										loop.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
										ItemAssistant.deleteAllItems(loop);
										Skull.whiteSkull(loop);
										if (loop.getPlayerName().equals("Remy E"))
										{
												QuickSetUp.tankTestBot(loop);
												boolean risk = true;
												if (risk)
												{
														ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 13736 : 13738, 1, false, false);
												}
												else
												{
														ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 13736 : 13736, 1, false, false);
														ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.WEAPON_SLOT, 4675, 1, false, false);
														ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.BODY_SLOT, 2503, 1, false, false);
														ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.LEG_SLOT, QuickSetUp.getRandomMysticBottom(), 1, false, false);
												}
										}
										else
										{
												QuickSetUp.mainHybrid(loop);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.BODY_SLOT, 4712, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.LEG_SLOT, 4714, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.WEAPON_SLOT, 18783, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 13738 : 6889, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.FEET_SLOT, Misc.hasPercentageChance(50) ? 18751 : 6920, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.AMULET_SLOT, Misc.hasPercentageChance(50) ? 18765 : 6585, 1, false, false);
												ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.HAND_SLOT, 7462, 1, false, false);
										}
										//loop.setTank(true);
										if (loop.getPlayerName().equals("Remy E"))
										{
												loop.getPA().movePlayer(2975, 3868, player.getHeight());
												loop.setBotStatus("LOOTING");

												CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
												{
														@Override
														public void execute(CycleEventContainer container)
														{
																if (loop.getDead())
																{
																		container.stop();
																		return;
																}
																if (loop.getY() == 3523)
																{
																		container.stop();
																		BotCommunication.sendBotMessage(loop, "TANKED", false);
																		return;
																}
																if (Misc.hasOneOutOf(60))
																{
																		BotCommunication.sendBotMessage(loop, "BM TANK", false);
																}
																Movement.playerWalk(loop, loop.getX(), loop.getY() - Misc.random(10, 30));
																if (loop.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < Misc.random(50, 60))
																{
																		if (ItemAssistant.hasItemInInventory(loop, 18637))
																		{
																				Food.eat(loop, 18637, ItemAssistant.getItemSlot(loop, 18637));
																		}
																		else
																		{
																				int[] result;
																				result = BotContent.getItemIdAndSlot(loop, "Saradomin brew");
																				if (result != null)
																				{
																						Potions.handlePotion(loop, result[0], result[1]);
																				}
																		}
																}
																if (loop.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 80)
																{
																		int[] result;
																		result = BotContent.getItemIdAndSlot(loop, "Super restore");
																		if (result != null)
																		{
																				Potions.handlePotion(loop, result[0], result[1]);
																		}
																}
																EdgeAndWestsRule.hasExcessBrews(loop, 100);

																//if got hit by melee, 60% chance that i switch
																if (loop.isMoving())
																{
																		if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
																		{
																				PrayerBook.activatePrayer(loop, ServerConstants.PROTECT_FROM_MAGIC);
																		}
																}
																if (loop.botLastDamageTakenType == ServerConstants.PROTECT_FROM_MAGIC || loop.botLastDamageTakenType == ServerConstants.PROTECT_FROM_RANGED)
																{
																		if (Misc.hasPercentageChance(60))
																		{
																				if (!loop.prayerActive[loop.botLastDamageTakenType])
																				{
																						PrayerBook.activatePrayer(loop, loop.botLastDamageTakenType);
																				}
																		}
																		else
																		{
																				if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MELEE])
																				{
																						PrayerBook.activatePrayer(loop, ServerConstants.PROTECT_FROM_MELEE);
																				}
																		}
																}
																else
																{
																		if (Misc.hasPercentageChance(70))
																		{
																				if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MELEE])
																				{
																						PrayerBook.activatePrayer(loop, ServerConstants.PROTECT_FROM_MELEE);
																				}
																		}
																		else
																		{
																				if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
																				{
																						PrayerBook.activatePrayer(loop, ServerConstants.PROTECT_FROM_MAGIC);
																				}
																		}
																}
														}

														@Override
														public void stop()
														{
														}
												}, 3);

										}
										else
										{
												loop.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
												CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
												{

														@Override
														public void execute(CycleEventContainer container)
														{
																loop.setPlayerIdToFollow(maxis);
																BotCommunication.sendBotMessage(loop, "LURED", true);
																container.stop();
														}

														@Override
														public void stop()
														{
														}

												}, Misc.random(3, 7));
										}
										//Wolpertinger.summonWolpertinger(loop, false);
										loop.setAutoRetaliate(0);
								}
						}
				}

				else if (command.startsWith("door"))
				{
						door(player, command);
				}

				else if (command.startsWith("noclip"))
				{
						noClip(player);
				}

				else if (command.startsWith("empty"))
				{
						empty(player);
				}

				else if (command.equalsIgnoreCase("tank"))
				{
						tank(player);
				}

				else if (command.equals("bank"))
				{
						bank(player);
				}

				else if (command.startsWith("setlevel"))
				{
						setLevel(player, command);
				}

				else if (command.startsWith("kick"))
				{
						kick(player, command);
				}

				else if (command.startsWith("hide"))
				{
						hide(player, command);
				}

				else if (command.startsWith("pnpc"))
				{
						pnpc(player, command);
				}

				else if (command.startsWith("tele"))
				{
						tele(player, command);
				}

				else if (command.equalsIgnoreCase("spec"))
				{
						spec(player);
				}

				else if (command.startsWith("xteleto") && !command.contains("xteletome"))
				{
						xTeleTo(player, command);
				}

				else if (command.startsWith("xteletome"))
				{
						xTeleToMe(player, command);
				}

				else if (command.startsWith("killme"))
				{
						killMe(player);
				}

				else if (command.equals("hail"))
				{
						hail(player);
				}

				else if (command.startsWith("interface"))
				{
						interfaceCommand(player, command);
				}

				else if (command.startsWith("gfx"))
				{
						gfx(player, command);
				}

				else if (command.startsWith("anim"))
				{
						anim(player, command);
				}

				else if (command.startsWith("object"))
				{
						object(player, command);
				}

				else if (command.startsWith("npc"))
				{
						npc(player, command);
				}

				else if (command.startsWith("update"))
				{
						update(player, command);
				}

				else if (command.startsWith("clipping"))
				{
						clipping(player);
				}

				else if (command.startsWith("reload"))
				{
						reload(player);
				}

				else if (command.startsWith("map"))
				{
						Region.load();
						player.getPA().sendMessage("Finished reloading maps.");
				}

				else if (command.equals("toggleverify"))
				{
						toggleVerify(player);
				}

				else if (command.startsWith("givemod"))
				{
						giveMod(player, command);
				}

				else if (command.startsWith("givesupport"))
				{
						giveSupport(player, command);
				}

				else if (command.startsWith("removemod"))
				{
						removeMod(player, command);
				}

				else if (command.startsWith("sound"))
				{
						sound(player, command);
				}

				else if (command.startsWith("item"))
				{
						item(player, command);
				}

				else if (command.startsWith("1hit"))
				{
						hit1(player, command);
				}

				else if (command.startsWith("welcomeupdate"))
				{
						welcomeUpdate(player, command);
				}

				else if (command.startsWith("botdebug"))
				{
						bot(player, command);
				}

				else if (command.startsWith("togglenpc"))
				{
						toggleNpc(player);
				}

				else if (command.startsWith("activity"))
				{
						activity(player);
				}
				if (command.startsWith("saveallpacketabuse"))
				{
						AdministratorCommand.saveAllPacketAbuse(player);
				}

				else if (command.startsWith("uptime"))
				{
						AdministratorCommand.uptime(player);
				}

				else if (command.startsWith("clearblacklist"))
				{
						Blacklist.clearBlacklist(player);
				}

				else if (command.equals("shutdown"))
				{
						player.getPA().sendMessage("Shutdown.");
						Server.playerHandler.serverRestartContentUpdate(false, true);
				}

				else if (command.startsWith("packetlogadd"))
				{
						AdministratorCommand.packetLogAdd(player, command);
				}

				else if (command.equals("packetlogclear"))
				{
						AdministratorCommand.packetLogClear(player);
				}

				else if (command.equals("packetlogsave"))
				{
						AdministratorCommand.packetLogSave(player);
				}

				else if (command.equals("packetlogview"))
				{
						AdministratorCommand.packetLogView(player);
				}

				else if (command.startsWith("address"))
				{
						AdministratorCommand.address(player);
				}

				else if (command.startsWith("tournament"))
				{
						Tournament.loadNewTournament(command);
				}

				else if (command.startsWith("displayreward"))
				{
						int frameIndex = 0;
						player.getPA().sendFrame126("Event names list", 25003);
						for (int index = 0; index < CommunityEvent.eventNames.size(); index++)
						{
								String parse[] = CommunityEvent.eventNames.get(index).split("-");
								player.getPA().sendFrame126(parse[0] + " will receive " + Misc.formatNumber(Integer.parseInt(parse[1])), 25008 + frameIndex);
								frameIndex++;
						}
						InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
						player.getPA().displayInterface(25000);
						player.getPA().sendMessage("Use ::addreward 1000 mgt madness or ::deletereward mgt madness");
				}
				else if (command.startsWith("deletereward"))
				{
						String name = command.replace("deletereward ", "");
						for (int index = 0; index < CommunityEvent.eventNames.size(); index++)
						{
								String parse[] = CommunityEvent.eventNames.get(index).split("-");
								if (parse[0].toLowerCase().equals(name.toLowerCase()))
								{
										CommunityEvent.eventNames.remove(index);
										player.getPA().sendMessage("Deleted from event rewards: " + name + " with reward " + Misc.formatNumber(Integer.parseInt(parse[1])));
										break;
								}
						}
				}
				else if (command.startsWith("addreward"))
				{
						String[] parse = command.split(" ");
						String name = command.replace(parse[0] + " " + parse[1] + " ", "");
						int amount = Integer.parseInt(parse[1]);
						CommunityEvent.eventNames.add(name + "-" + amount);
						player.getPA().sendMessage("Added " + name + " with reward " + Misc.formatNumber(amount));
				}

				else if (command.equals("tick"))
				{
						int frameIndex = 0;
						player.getPA().sendFrame126("Tick log", 25003);
						int totalTime = 0;
						int highestTime = 0;
						for (int index = 0; index < GameTickLog.saveTicks.size(); index++)
						{
								player.getPA().sendFrame126(GameTickLog.saveTicks.get(index), 25008 + frameIndex);
								int currentTime = Integer.parseInt(GameTickLog.saveTicks.get(index));
								totalTime += currentTime;
								if (highestTime < currentTime)
								{
										highestTime = currentTime;
								}
								frameIndex++;
						}
						InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
						player.getPA().displayInterface(25000);
						player.getPA().sendMessage("Average: " + (totalTime / GameTickLog.saveTicks.size()) + ", Highest game tick: " + highestTime + "ms.");
				}

				else if (command.startsWith("gree"))
				{
						String personName = command.substring(5);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(personName))
										{
												Player target = PlayerHandler.players[i];
												ItemAssistant.addItemToInventoryOrDrop(target, 4024, 1);
												player.playerAssistant.sendMessage("You have given a Monkey greegree to: " + target.getPlayerName() + ".");
												break;
										}
								}
						}
				}
				else if (command.startsWith("botteleport1"))
				{
						int amount = Integer.parseInt(command.substring(13));
						int current = 0;
						int radius = 15;
						// Ability to have x amount of bots teleport to me.
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (!loop.isBot)
								{
										continue;
								}
								if (current == amount)
								{
										break;
								}
								if (player.getPA().withinDistance(loop))
								{
										continue;
								}
								current++;
								loop.getPA().movePlayer(player.getX() - (radius / 2) + Misc.random(radius), player.getY() - (radius / 2) + Misc.random(radius), player.getHeight());

						}
						player.getPA().sendMessage("Teleported bots that were not here: " + current);
				}
				else if (command.startsWith("botteleport"))
				{
						int amount = Integer.parseInt(command.substring(12));
						int current = 0;
						int radius = 15;
						// Ability to have x amount of bots teleport to me.
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (!loop.isBot)
								{
										continue;
								}
								if (current == amount)
								{
										break;
								}
								current++;
								loop.getPA().movePlayer(3350 - (radius / 2) + Misc.random(radius), 3649 - (radius / 2) + Misc.random(radius), player.getHeight());

						}
						player.getPA().sendMessage("Teleported " + current + " bots.");
				}
				else if (command.equals("botwalk"))
				{
						int totalMoving = 0;
						int radius = 20;
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (!loop.isBot)
								{
										continue;
								}
								/*
								if (Misc.hasPercentageChance(30))
								{
										continue;
								}
								totalMoving++;
								CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												Movement.playerWalk(loop, loop.getX() - (radius / 2) + Misc.random(radius), loop.getY() - (radius / 2) + Misc.random(radius));
										}
								
										@Override
										public void stop()
										{
										}
								}, Misc.random(1, 12)); // Usually players click on the minimap once every 2-6 seconds.
								*/
								totalMoving++;
								CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												Movement.playerWalk(loop, loop.getX() - (radius / 2) + Misc.random(radius), loop.getY() - (radius / 2) + Misc.random(radius));
										}

										@Override
										public void stop()
										{
										}
								}, Misc.random(1)); // Usually players click on the minimap once every 2-6 seconds.

						}
						player.getPA().sendMessage("Bots walking: " + totalMoving);
				}
				else if (command.equals("botcombat"))
				{
						int totalMoving = 0;
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (!loop.isBot)
								{
										continue;
								}
								totalMoving++;
								int target = 0;
								for (int a = 0; a < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; a++)
								{
										Npc npcLoop = NpcHandler.npcs[a];
										if (npcLoop == null)
										{
												continue;
										}
										if (npcLoop.currentHitPoints <= 0)
										{
												continue;
										}
										boolean found = false;
										for (int b = 0; b < npcsBooked.size(); b++)
										{
												if (npcsBooked.get(b).equals(a + ""))
												{
														found = true;
														break;
												}
										}
										if (found)
										{
												continue;
										}
										target = a;
										npcsBooked.add(a + "");
										loop.tank = true;
										QuickSetUp.mainMelee(loop);
										loop.getPA().movePlayer(npcLoop.getX(), npcLoop.getY(), npcLoop.getHeight());
										break;

								}
								final int targetFinal = target;
								CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												Npc npc = NpcHandler.npcs[targetFinal];
												if (npc != null)
												{
														loop.setNpcIdentityAttacking(targetFinal);
														CombatNpc.attackNpc(loop, npc);
												}
										}

										@Override
										public void stop()
										{
										}
								}, 1); // Usually players click on the minimap once every 2-6 seconds.

						}
						player.getPA().sendMessage("Bots npc combat: " + totalMoving);
				}
				else if (command.equals("botoff"))
				{
						for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
						{
								Player loop = PlayerHandler.players[index];
								if (loop == null)
								{
										continue;
								}
								if (!loop.isBot)
								{
										continue;
								}
								//PlayerSave.saveGame(loop);
								loop.setDisconnected(true);
						}
						player.getPA().sendMessage("Bots logged off.");
				}
				else if (command.equals("boton"))
				{
						BotManager.logInBots();
						player.getPA().sendMessage("Bots logged in.");
				}

		}

		private static void saveLogs()
		{
				ItemTransferLog.saveTransferItemLog();
				Misc.print("Administrator command: Save logs only.");

		}

		public static ArrayList<String> npcsBooked = new ArrayList<String>();

		private static void activity(Player player)
		{
				int frameIndex = 0;
				player.getPA().sendFrame126("Player activity", 25003);
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
						if (!loop.lastActivity.isEmpty())
						{
								player.getPA().sendFrame126(loop.lastActivity + " " + Misc.getTime((System.currentTimeMillis() - loop.lastActivityTime) / 1000) + " ago, " + Misc.capitalize(loop.getPlayerName()) + " joined: " + PlayerGameTime.getDaysSinceAccountCreated(loop) + " days ago, Revision: " + loop.graphicsType, 25008 + frameIndex);
								frameIndex++;
						}
						else
						{
								player.getPA().sendFrame126("AFK: " + Misc.capitalize(loop.getPlayerName()) + ", Joined: " + PlayerGameTime.getDaysSinceAccountCreated(loop) + " days ago, Revision: " + loop.graphicsType, 25008 + frameIndex);
								frameIndex++;
						}
				}
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
				player.getPA().displayInterface(25000);

		}

		private static void toggleNpc(Player player)
		{
				player.saveNpcText = !player.saveNpcText;
				player.getPA().sendMessage("Npc text saving to: " + player.saveNpcText);

		}

		public static void saveNpcText(Player player)
		{
			//@formatter:off
			String npcName = "Red dragon";
			int npcId = 53;
			String[] list =
				{
					"\t" + "{",
					"\t\t" + "\"npc_description\": \"" + npcName + "\",",
					"\t\t" + "\"npc_type\": " + npcId + ",",
					"\t\t" + "\"x\": "+ player.getX() + ",",
					"\t\t" + "\"y\": "+ player.getY() + ",",
					"\t\t" + "\"height\": "+ player.getHeight() + ",",
					"\t\t" + "\"face_action\": \"ROAM\"",
					"\t" + "},",
				};
			//@formatter:on
				BufferedWriter bw = null;
				try
				{
						bw = new BufferedWriter(new FileWriter("data/npc/npc paste.txt", true));
						for (int index = 0; index < list.length; index++)
						{
								bw.write(list[index]);
								bw.newLine();
						}
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
				NpcHandler.spawnDefaultNpc(npcId, "", player.getX(), player.getY(), player.getHeight(), "SOUTH");
		}

		private static void bot(Player player, String command)
		{
				String[] string = command.split(" ");
				String name = "";
				for (int i = 0; i < string.length; i++)
				{
						if (i == 0)
						{
								continue;
						}
						name = name + (i == 1 ? "" : " ") + string[i];
				}
				if (FileUtility.accountExists("backup/logs/bot debug/" + name + ".txt"))
				{

						File file = new File("backup/logs/bot debug/" + name + ".txt");
						file.delete();
						player.getPA().sendMessage("File deleted because it exists.");
				}
				player.getPA().sendMessage("Attempting bot debugging for: " + name);
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
				{
						Player loop = PlayerHandler.players[index];
						if (loop == null)
						{
								continue;
						}
						if (!loop.isBot)
						{
								continue;
						}
						if (loop.getPlayerName().toLowerCase().equals(name))
						{

								FileUtility.saveArrayContents("backup/logs/bot debug/" + name + ".txt", loop.botDebug);
								break;
						}
						// */
						//loop.gameMode = "Bot";
						//loop.xpLock = true;
						//loop.setAutoRetaliate(1);
						//loop.setTutorialComplete(true);
						//loop.safePkingOn = true;
				}

		}

		private static void welcomeUpdate(Player player, String command)
		{
				WelcomeMessage.loadWelcomeMessage();
				player.playerAssistant.sendMessage("Welcome message has been changed to:");
				WelcomeMessage.sendWelcomeMessage(player);

		}

		private static void hit1(Player player, String command)
		{
				player.hit1 = !player.hit1;
				player.playerAssistant.sendMessage("1 hit: " + player.hit1);
		}

		public static void uptime(Player player)
		{
				int hours = (int) ((System.currentTimeMillis() - Server.timeServerOnline) / 3600000);
				if (hours > 0)
				{
						player.playerAssistant.sendMessage("Uptime: " + hours + " hour" + (hours == 1 ? "." : "s."));
				}
				else
				{
						player.playerAssistant.sendMessage("Uptime: " + ((System.currentTimeMillis() - Server.timeServerOnline) / 60000) + " minutes.");
				}
		}

		public static void saveAllPacketAbuse(Player player)
		{
				long time = System.currentTimeMillis();
				packetLogSave(player);
				PacketHandler.saveCurrentFlaggedPlayers();
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/appearance packet.txt", PacketHandler.appearanceLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/appearance packet.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/bank abuse.txt", PacketHandler.bankLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/bank abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/shop abuse.txt", PacketHandler.shoppingLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/shop abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/non-existing objects.txt", PacketHandler.fakeObjectsLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/non-existing objects.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/invalid packet.txt", PacketHandler.invalidPacketLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/invalid packet.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/item abuse.txt", PacketHandler.itemLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/item abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/spellbook abuse.txt", PacketHandler.spellbookLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/spellbook abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/all kinds of abuse.txt", PacketHandler.allKindsOfAbuse))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/all kinds of abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/trade and duel abuse.txt", PacketHandler.tradeAndDuelLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/trade and duel abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/chat and pm log.txt", PacketHandler.chatAndPmLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/chat and pm log.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/dice abuse.txt", PacketHandler.diceLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/dice abuse.txt");
				}
				if (FileUtility.saveArrayContents("backup/logs/packet abuse/string abuse.txt", PacketHandler.stringAbuseLog))
				{
						PacketHandler.packetAbuseAlert.add("backup/logs/packet abuse/string abuse.txt");
				}

				FileUtility.saveArrayContents("./backup/logs/pvp/kills.txt", KillReward.killLog);
				FileUtility.saveArrayContents("./backup/logs/unused objects.txt", PacketHandler.unUsedObject);

				PacketHandler.appearanceLog.clear();
				PacketHandler.bankLog.clear();
				PacketHandler.fakeObjectsLog.clear();
				PacketHandler.invalidPacketLog.clear();
				PacketHandler.shoppingLog.clear();
				PacketHandler.itemLog.clear();
				PacketHandler.spellbookLog.clear();
				PacketHandler.allKindsOfAbuse.clear();
				PacketHandler.unUsedObject.clear();
				PacketHandler.tradeAndDuelLog.clear();
				PacketHandler.chatAndPmLog.clear();
				PacketHandler.diceLog.clear();
				KillReward.killLog.clear();
				PacketHandler.stringAbuseLog.clear();
				if (player != null)
				{
						player.playerAssistant.sendMessage("Finished saving all packet abuse logs in " + (System.currentTimeMillis() - time) + " ms.");
				}
		}

		/**
		 * item command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void item(Player player, String playerCommand)
		{

				try
				{
						String[] args = playerCommand.split(" ");
						if (args.length >= 2)
						{
								int newItemId = Integer.parseInt(args[1]);
								int newItemAmount = 0;
								if (ItemDefinition.getDefinitions()[newItemId] == null)
								{
										newItemAmount = 1;
								}
								else
								{
										newItemAmount = args.length == 3 ? Integer.parseInt(args[2]) : (ItemDefinition.getDefinitions()[newItemId].stackable || ItemDefinition.getDefinitions()[newItemId].note) ? 10000 : 1;
								}
								if (newItemAmount > Integer.MAX_VALUE)
								{
										newItemAmount = Integer.MAX_VALUE;
								}
								if (newItemId > 20000)
								{
										player.playerAssistant.sendMessage("Item id too high.");
										return;
								}
								ItemAssistant.addItem(player, newItemId, newItemAmount);
								player.playerAssistant.sendMessage("You have spawned " + Misc.formatNumber(newItemAmount) + " " + ItemAssistant.getItemName(newItemId) + ", " + newItemId + ".");
								player.setInventoryUpdate(true);
								ItemAssistant.resetItems(player, 3823); // Spawning items while in shop.
								ItemAssistant.resetItems(player, 5064); // Spawning items while banking.
						}
						else
						{
								player.playerAssistant.sendMessage("Wrong input.");
						}
				}
				catch (Exception e)
				{
				}
		}

		private static void sound(Player player, String command)
		{
				int sound = Integer.parseInt(command.substring(6));
				player.playerAssistant.sendMessage("Sound: " + sound);
				SoundSystem.sendSound(player, sound, 0);
				if (sound > 0)
				{
						return;
				}
		}

		private static void removeMod(Player player, String command)
		{
				String name = command.substring(10);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player playerloop = PlayerHandler.players[i];
						if (playerloop == null)
						{
								continue;
						}
						if (playerloop.getPlayerName().equalsIgnoreCase(name))
						{
								playerloop.playerRights = 0;
								playerloop.setUpdateRequired(true);
								playerloop.setAppearanceUpdateRequired(true);
								player.playerAssistant.sendMessage("You have removed Moderator from " + name + ".");
								break;
						}
				}
		}

		private static void giveMod(Player player, String command)
		{
				String name = command.substring(8);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player playerloop = PlayerHandler.players[i];
						if (playerloop == null)
						{
								continue;
						}
						if (playerloop.getPlayerName().equalsIgnoreCase(name))
						{
								playerloop.playerRights = 1;
								playerloop.setUpdateRequired(true);
								playerloop.setAppearanceUpdateRequired(true);
								player.playerAssistant.sendMessage("You have promoted " + name + " to Moderator.");
								break;
						}
				}
		}

		private static void giveSupport(Player player, String command)
		{
				String name = command.substring(12);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player playerloop = PlayerHandler.players[i];
						if (playerloop == null)
						{
								continue;
						}
						if (playerloop.getPlayerName().equalsIgnoreCase(name))
						{
								playerloop.playerRights = 10;
								playerloop.setUpdateRequired(true);
								playerloop.setAppearanceUpdateRequired(true);
								player.playerAssistant.sendMessage("You have promoted " + name + " to Support.");
								break;
						}
				}
		}

		private static void toggleVerify(Player player)
		{
				player.canVerifyMoreObjects = !player.canVerifyMoreObjects;
				player.playerAssistant.sendMessage("Can add more verified objects: " + player.canVerifyMoreObjects);
		}

		public static void packetLogView(Player player)
		{
				player.playerAssistant.sendMessage("----");
				if (PacketHandler.packetLogPlayerList.isEmpty())
				{
						player.playerAssistant.sendMessage("List is empty.");
				}
				for (int i = 0; i < PacketHandler.packetLogPlayerList.size(); i++)
				{
						player.playerAssistant.sendMessage("Packet log currently has: " + PacketHandler.packetLogPlayerList.get(i) + ".");
				}

		}

		public static void packetLogSave(Player player)
		{
				if (player != null)
				{
						player.playerAssistant.sendMessage("Packet log list output has been saved.");
				}

				FileUtility.saveArrayContents("backup/logs/packet abuse/packet log.txt", PacketHandler.packetLogData);
				PacketHandler.packetLogData.clear();
		}

		public static void packetLogClear(Player player)
		{

				PacketHandler.packetLogPlayerList.clear();
				player.playerAssistant.sendMessage("All players in packet log list have been cleared.");

		}

		public static void packetLogAdd(Player player, String command)
		{

				String target = command.substring(13);
				player.playerAssistant.sendMessage("----");
				if (PacketHandler.packetLogPlayerList.contains(target))
				{
						player.getPA().sendMessage("Player already exists in the Packet log player list.");
						return;
				}
				PacketHandler.packetLogPlayerList.add(target);
				for (int i = 0; i < PacketHandler.packetLogPlayerList.size(); i++)
				{
						player.playerAssistant.sendMessage("Packet log currently has: " + PacketHandler.packetLogPlayerList.get(i) + ".");
				}
		}

		/**
		 * Load all the server data files such as info.cfg, npc.cfg, spawns etc..
		 * @param player
		 */
		private static void reload(Player player)
		{
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] != null)
						{
								Pet.deletePet(NpcHandler.npcs[i]);
						}
				}
				NpcHandler.loadNpcData();
				ItemDefinition.loadItemDefinitions();
				BloodMoneyPrice.loadBloodMoneyPrice();
				ShopHandler.loadShops();
				player.playerAssistant.sendMessage("All start-up files reloaded.");

		}

		private static void door(Player player, String playerCommand)
		{
				String[] args = playerCommand.split(" ");
				int face = 0;
				if (args.length >= 3)
				{
						face = Integer.parseInt(args[2]);
				}
				int type = 1;
				if (args.length >= 4)
				{
						type = Integer.parseInt(args[3]);
				}
				player.getPA().objectNoClipping(Integer.parseInt(args[1]), player.getX(), player.getY(), face, type);
				player.playerAssistant.sendMessage("Door: " + Integer.parseInt(args[1]) + ", face: " + face + ", " + type);

		}

		private static void noClip(Player player)
		{
				player.noClip = !player.noClip;
				player.playerAssistant.sendMessage("Server clipping for this player: " + player.noClip);
		}

		private static void clipping(Player player)
		{
				player.clipping = !player.clipping;
				player.playerAssistant.sendMessage("" + player.clipping);
				int x = player.getX();
				int y = player.getY();
				for (int i = 0; i < 15; i++)
				{
						for (int a = 0; a < 15; a++)
						{
								if (Region.getClipping(x + i, y + a, player.getHeight()) == 0)
								{
										Server.itemHandler.createGroundItem(player, 995, x + i, y + a, 1, false, 0, true, "");
								}
						}
				}
				for (int i = 0; i < 15; i++)
				{
						for (int a = 0; a < 15; a++)
						{
								if (Region.getClipping(x - i, y - a, player.getHeight()) == 0)
								{
										Server.itemHandler.createGroundItem(player, 995, x - i, y - a, 1, false, 0, true, "");
								}
						}
				}
				for (int i = 0; i < 15; i++)
				{
						for (int a = 0; a < 15; a++)
						{
								if (Region.getClipping(x - i, y + a, player.getHeight()) == 0)
								{
										Server.itemHandler.createGroundItem(player, 995, x - i, y + a, 1, false, 0, true, "");
								}
						}
				}
				for (int i = 0; i < 15; i++)
				{
						for (int a = 0; a < 15; a++)
						{
								if (Region.getClipping(x + i, y - a, player.getHeight()) == 0)
								{
										Server.itemHandler.createGroundItem(player, 995, x + i, y - a, 1, false, 0, true, "");
								}
						}
				}
				player.playerAssistant.sendMessage("Equipment = remove clipping.");
				player.playerAssistant.sendMessage("Items kept on death = add clipping.");

		}

		/**
		 * address command.
		 * @param player
		 * 			The associated player.
		 */
		public static void address(Player player)
		{
				int frameIndex = 0;
				player.getPA().sendFrame126("Address", 25003);
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
						player.getPA().sendFrame126("User: " + loop.getCapitalizedName() + ", ip: " + loop.addressIp + ", mac: " + loop.addressMac, 25008 + frameIndex);
						frameIndex++;
						player.getPA().sendFrame126("UID: " + loop.addressUid, 25008 + frameIndex);
						frameIndex++;
				}
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25098);
				player.getPA().displayInterface(25000);

		}

		/**
		 * update command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void update(Player player, String playerCommand)
		{
				try
				{
						String[] args = playerCommand.split(" ");
						int a = Integer.parseInt(args[1]);
						PlayerHandler.updateSeconds = a;
						PlayerHandler.updateAnnounced = false;
						PlayerHandler.updateRunning = true;
						PlayerHandler.updateStartTime = System.currentTimeMillis();
				}
				catch (Exception e)
				{

				}
		}

		public static void empty(Player player)
		{
				player.getPA().removeAllItems();
				player.playerAssistant.sendMessage("Inventory has been emptied.");
				ItemAssistant.resetItems(player, 3823); // Spawning items while in shop.
				ItemAssistant.resetItems(player, 5064); // Spawning items while banking.
		}

		/**
		 * npc command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void npc(Player player, String playerCommand)
		{

				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				try
				{
						int npcId = Integer.parseInt(playerCommand.substring(4));
						player.playerAssistant.sendMessage("NPC: " + npcId);
						if (npcId >= 0)
						{
								int slot = -1;
								for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
								{
										if (NpcHandler.npcs[i] == null)
										{
												slot = i;
												break;
										}
								}
								if (slot == -1)
								{
										return;
								}
								Npc newNpc = new Npc(slot, npcId);
								NpcHandler.npcs[slot] = newNpc;

								newNpc.name = "";
								newNpc.setX(player.getX());
								newNpc.setY(player.getY() - 1);
								newNpc.setSpawnPositionX(player.getX());
								newNpc.setSpawnPositionY(player.getY() - 1);
								newNpc.setHeight(player.getHeight());
								newNpc.faceAction = "SOUTH";
								newNpc.currentHitPoints = 1000000; //NPCDefinition.getDefinitions()[npcId].hitPoints
								newNpc.setSpawnedBy(player.getPlayerId());
								newNpc.maximumHitPoints = newNpc.currentHitPoints;
						}
				}
				catch (Exception e)
				{
				}

		}

		/**
		 * killme command.
		 * @param player
		 * 			The associated player.
		 */
		private static void killMe(Player player)
		{
				player.setHitPoints(0);
				Player killer = PlayerHandler.players[player.getPlayerId()];
				Death.respawnPlayer(killer, player);
		}

		/**
		 * object command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void object(Player player, String playerCommand)
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				String[] args = playerCommand.split(" ");
				int face = 0;
				if (args.length >= 3)
				{
						face = Integer.parseInt(args[2]);
				}
				int type = 10;
				if (args.length >= 4)
				{
						type = Integer.parseInt(args[3]);
				}
				player.getPA().objectNoClipping(Integer.parseInt(args[1]), player.getX(), player.getY() - 0, face, type);
				player.playerAssistant.sendMessage("Object " + Integer.parseInt(args[1]) + " at " + (player.getX()) + ", " + (player.getY() - 0) + ", face " + face + ", type: " + type);

		}

		/**
		 * jail command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		public static void jail(Player player, String playerCommand)
		{
				try
				{
						String playerToBan = playerCommand.substring(5);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToBan))
										{
												Player target = PlayerHandler.players[i];
												if (Area.inWilderness(target))
												{
														player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
														return;
												}
												if (target.getDuelStatus() >= 1)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
														return;
												}
												if (target.getHeight() == 20)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
														return;
												}
												if (!target.isModeratorRank())
												{
														target.getPA().sendMessage("If you have been abused, take screenshots using f12 and");
														target.getPA().sendMessage("send to Mgt Madness on ::discord");
												}
												player.playerAssistant.sendMessage("You have jailed " + target.getPlayerName() + ".");
												target.playerAssistant.sendMessage("You have been jailed.");
												int[][] random = {{3014, 3189}, {3018, 3189}, {3013, 3192}, {3014, 3195}, {3018, 3180}, {3014, 3181},};
												int value = Misc.random(random.length);
												if (target.isAdministratorRank() || target.isModeratorRank())
												{
														target.getPA().movePlayer(3012, 3185, 0);
												}
												else
												{
														target.getPA().movePlayer(random[value][0], random[value][1], 0);
														target.setJailed(true);
												}
												break;
										}
								}
						}
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * unjail command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		public static void unJail(Player player, String playerCommand)
		{
				try
				{
						String playerToBan = playerCommand.substring(7);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToBan))
										{
												Player target = PlayerHandler.players[i];
												if (Area.inWilderness(target))
												{
														player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
														return;
												}
												if (target.getDuelStatus() >= 1)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
														return;
												}
												if (target.getHeight() == 20)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
														return;
												}
												player.playerAssistant.sendMessage("You have un-jailed " + target.getPlayerName() + ".");
												target.playerAssistant.sendMessage("You have been unjailed.");
												target.getPA().movePlayer(3088, 3505, 0);
												target.setJailed(false);
												break;
										}
								}
						}
				}
				catch (Exception e)
				{
				}
		}

		public static void guest(Player player, String playerCommand)
		{
				try
				{
						String playerToBan = playerCommand.substring(6);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToBan))
										{
												Player target = PlayerHandler.players[i];
												if (Area.inWilderness(target))
												{
														player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
														return;
												}
												if (target.getDuelStatus() >= 1)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
														return;
												}
												if (target.getHeight() == 20)
												{
														player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
														return;
												}
												player.playerAssistant.sendMessage("You have invited " + target.getPlayerName() + ".");
												target.playerAssistant.sendMessage("You have been invited as a guest.");
												target.getPA().movePlayer(3012, 3184, 0);
												target.setJailed(false);
												break;
										}
								}
						}
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * bank command.
		 * @param player
		 * 			The associated player.
		 */
		private static void bank(Player player)
		{
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
		}

		/**
		 * tank command.
		 * @param player
		 * 			The associated player.
		 */
		private static void tank(Player player)
		{
				player.setTank(!player.getTank());
				player.playerAssistant.sendMessage("Tank: " + player.getTank() + ".");
		}

		/**
		 * setlevel command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void setLevel(Player player, String playerCommand)
		{
				Combat.resetPrayers(player);
				player.playerAssistant.sendMessage("Example- ::lvl 1 45  (1 is for defence and 45 is the level set for it)");
				player.playerAssistant.sendMessage("Attack = 0,   Defence = 1,  Strength = 2,");
				player.playerAssistant.sendMessage("Hitpoints = 3,   Ranged = 4,   Prayer = 5,");
				player.playerAssistant.sendMessage("Magic = 6,   Cooking = 7,   Woodcutting = 8,");
				player.playerAssistant.sendMessage("Fletching = 9,   Fishing = 10,   Firemaking = 11,");
				player.playerAssistant.sendMessage("Crafting = 12,   Smithing = 13,   Mining = 14,");
				player.playerAssistant.sendMessage("Herblore = 15,   Agility = 16,   Thieving = 17,");
				player.playerAssistant.sendMessage("Slayer = 18,   Farming = 19,   Runecrafting = 20");
				try
				{
						String[] args = playerCommand.split(" ");
						int skill = Integer.parseInt(args[1]);
						int level = Integer.parseInt(args[2]);
						if (level > 99)
						{
								level = 99;
						}
						else if (level < 0)
						{
								level = 1;
						}
						player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
						player.baseSkillLevel[skill] = level;
						player.currentCombatSkillLevel[skill] = level;
						player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
						Combat.resetPrayers(player);
						player.setHitPoints(player.getBaseHitPointsLevel());
						player.playerAssistant.calculateCombatLevel();
						InterfaceAssistant.updateCombatLevel(player);
						Skilling.updateTotalLevel(player);
						Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
						Skilling.updateSkillTabFrontTextMain(player, skill);
						player.setVengeance(false);
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * pnpc command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void pnpc(Player player, String playerCommand)
		{
				try
				{
						int newNPC = Integer.parseInt(playerCommand.substring(5));
						player.npcId2 = newNPC;
						player.getPA().requestUpdates();
						player.playerAssistant.sendMessage("Transformed: " + newNPC);
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * to command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void xTeleTo(Player player, String playerCommand)
		{
				String name = playerCommand.substring(8);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (PlayerHandler.players[i] != null)
						{
								if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
								{
										player.playerAssistant.sendMessage("Teleported to: " + name);
										player.getPA().movePlayer(PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), PlayerHandler.players[i].getHeight());
										break;
								}
						}
				}
		}

		/**
		 * dc command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void kick(Player player, String playerCommand)
		{
				try
				{
						String playerToBan = playerCommand.substring(5);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToBan))
										{
												PlayerHandler.players[i].setDisconnected(true);
												PlayerHandler.players[i].setTimeOutCounter(ServerConstants.TIMEOUT + 1);
												player.getPA().sendMessage("You have kicked: " + PlayerHandler.players[i].getPlayerName());
												break;
										}
								}
						}
				}
				catch (Exception e)
				{
						player.playerAssistant.sendMessage("Player Must Be Offline.");
				}
		}

		/**
		 * hide command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void hide(Player player, String playerCommand)
		{
				String msg = playerCommand.substring(5);
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (PlayerHandler.players[i] != null)
						{
								Player c2 = PlayerHandler.players[i];
								c2.playerAssistant.sendMessage("" + msg + "");
						}
				}
		}

		/**
		 * tele command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void tele(Player player, String playerCommand)
		{
				String[] arg = playerCommand.split(" ");
				player.playerAssistant.sendMessage("Before: " + player.getX() + ", " + player.getY() + ", " + player.getHeight());
				if (arg.length > 3)
				{
						player.getPA().movePlayer(Integer.parseInt(arg[1]), Integer.parseInt(arg[2]), Integer.parseInt(arg[3]));
						player.playerAssistant.sendMessage("Teleported to: " + Integer.parseInt(arg[1]) + ", " + Integer.parseInt(arg[2]) + ", " + Integer.parseInt(arg[3]));
				}
				else if (arg.length == 3)
				{
						player.getPA().movePlayer(Integer.parseInt(arg[1]), Integer.parseInt(arg[2]), player.getHeight());
						player.playerAssistant.sendMessage("Teleported to: " + Integer.parseInt(arg[1]) + ", " + Integer.parseInt(arg[2]) + ", " + player.getHeight());
				}
		}

		/**
		 * spec command.
		 * @param player
		 * 			The associated player.
		 */
		private static void spec(Player player)
		{
				player.setSpecialAttackAmount(10.0, false);
				player.playerAssistant.sendMessage("You now have unlimited special attacks.");
				CombatInterface.updateSpecialBar(player);
		}

		/**
		 * xteletome command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void xTeleToMe(Player player, String playerCommand)
		{
				try
				{
						String playerToTele = playerCommand.substring(10);
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToTele))
										{
												Player c2 = PlayerHandler.players[i];
												c2.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
												player.playerAssistant.sendMessage("Teleported " + playerToTele + " to me.");
												break;
										}
								}
						}
				}
				catch (Exception e)
				{
						player.playerAssistant.sendMessage("Player Must Be Offline.");
				}
		}

		/**
		 * hail command.
		 * @param player
		 * 			The associated player.
		 */
		private static void hail(Player player)
		{
				String[] randomText = {"I love Dawntained!", "Dawntained makes it rain!", "Dawntained is love, Dawntained is life!", "Dawntained!!!", "Dawntained #1"};
				PlayerMiscContent.allPlayersHail(randomText);
		}

		/**
		 * gfx command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void gfx(Player player, String playerCommand)
		{
				String[] args = playerCommand.split(" ");
				player.gfx0(Integer.parseInt(args[1]));
				player.playerAssistant.sendMessage("GFX: " + Integer.parseInt(args[1]));
		}

		/**
		 * anim command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void anim(Player player, String playerCommand)
		{
				String[] args = playerCommand.split(" ");
				if (args.length == 1)
				{

						player.startAnimation(65535);
						player.getPA().requestUpdates();
						player.playerAssistant.sendMessage("Animation: 65535");
						return;
				}
				player.startAnimation(Integer.parseInt(args[1]));
				player.getPA().requestUpdates();
				player.playerAssistant.sendMessage("Animation: " + Integer.parseInt(args[1]));
		}

		/**
		 * interface command.
		 * @param player
		 * 			The associated player.
		 * @param playerCommand
		 * 			The command used by the player.
		 */
		private static void interfaceCommand(Player player, String playerCommand)
		{
				try
				{
						String[] args = playerCommand.split(" ");
						player.getPA().displayInterface(Integer.parseInt(args[1]));
						player.playerAssistant.sendMessage("Opened interface " + Integer.parseInt(args[1]) + ".");
				}
				catch (Exception e)
				{
						player.playerAssistant.sendMessage("Wrong input.");
				}
		}

}