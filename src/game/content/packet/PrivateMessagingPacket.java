package game.content.packet;

import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Mute;
import network.packet.PacketType;
import utility.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessagingPacket implements PacketType
{

		public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215, CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74, ADD_IGNORE = 133;

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				switch (packetType)
				{

						case ADD_FRIEND:
								long friendToAdd = player.getInStream().readQWord();
								boolean canAdd = true;

								for (int i1 = 0; i1 < player.friends.length; i1++)
								{
										if (player.friends[i1] != 0 && player.friends[i1] == friendToAdd)
										{
												canAdd = false;
												player.playerAssistant.sendMessage(friendToAdd + " is already on your friends list.");
										}
								}
								if (canAdd == true)
								{
										for (int i1 = 0; i1 < player.friends.length; i1++)
										{
												if (player.friends[i1] == 0)
												{
														player.friends[i1] = friendToAdd;
														for (int i2 = 1; i2 < ServerConstants.MAXIMUM_PLAYERS; i2++)
														{
																if (PlayerHandler.players[i2] != null && PlayerHandler.players[i2].isActive() && Misc.playerNameToInt64(PlayerHandler.players[i2].getPlayerName()) == friendToAdd)
																{
																		Player o = PlayerHandler.players[i2];
																		if (o != null)
																		{
																				if (PlayerHandler.players[i2].privateChat == 0 || (PlayerHandler.players[i2].privateChat == 1 && o.getPA().isInPM(Misc.playerNameToInt64(player.getPlayerName()))))
																				{
																						player.getPA().loadPM(friendToAdd, 1);
																						break;
																				}
																		}
																}
														}
														break;
												}
										}
								}
								ClanChatHandler.updateFriendsList(player, Misc.nameForLong(friendToAdd).replaceAll("_", " "), true);
								break;

						case SEND_PM:
								long sendMessageToFriendId = player.getInStream().readQWord();
								byte pmchatText[] = new byte[100];
								int pmchatTextSize = (byte) (packetSize - 8);
								player.getInStream().readBytes(pmchatText, pmchatTextSize, 0);

								if (Mute.isMuted(player))
								{
										break;
								}

								for (int i1 = 0; i1 < player.friends.length; i1++)
								{
										if (player.friends[i1] == sendMessageToFriendId)
										{
												boolean pmSent = false;

												for (int i2 = 1; i2 < ServerConstants.MAXIMUM_PLAYERS; i2++)
												{
														if (PlayerHandler.players[i2] != null && PlayerHandler.players[i2].isActive() && Misc.playerNameToInt64(PlayerHandler.players[i2].getPlayerName()) == sendMessageToFriendId)
														{
																Player o = PlayerHandler.players[i2];
																if (o != null)
																{
																		if (PlayerHandler.players[i2].privateChat == 0 || (PlayerHandler.players[i2].privateChat == 1 && o.getPA().isInPM(Misc.playerNameToInt64(player.getPlayerName()))))
																		{
																				o.getPA().sendPM(player.getPlayerName(), Misc.playerNameToInt64(player.getPlayerName()), player.playerRights, pmchatText, pmchatTextSize, trackPlayer);
																				pmSent = true;

																		}
																}
																break;
														}
												}
												if (!pmSent)
												{
														player.playerAssistant.sendMessage("That player is currently offline.");
														break;
												}
												break;
										}
								}
								break;

						case REMOVE_FRIEND:
								long friendToRemove = player.getInStream().readQWord();
								for (int i1 = 0; i1 < player.friends.length; i1++)
								{
										if (player.friends[i1] == friendToRemove)
										{
												for (int i2 = 1; i2 < ServerConstants.MAXIMUM_PLAYERS; i2++)
												{
														Player o = PlayerHandler.players[i2];
														if (o != null)
														{
																if (player.friends[i1] == Misc.playerNameToInt64(PlayerHandler.players[i2].getPlayerName()))
																{
																		o.getPA().updatePM(player.getPlayerId(), 0, false);
																		break;
																}
														}
												}
												player.friends[i1] = 0;
												player.getPA().sendFrame126("", i1 + 14101);
												player.getPA().sendFrame126("", i1 + 17551);
												break;
										}
								}

								ClanChatHandler.updateFriendsList(player, Misc.nameForLong(friendToRemove).replaceAll("_", " "), false);
								break;

						case REMOVE_IGNORE:
								long ignore = player.getInStream().readQWord();

								for (int i = 0; i < player.ignores.length; i++)
								{
										if (player.ignores[i] == ignore)
										{
												player.ignores[i] = 0;
												break;
										}
								}
								break;

						case CHANGE_PM_STATUS:
								player.getInStream().readUnsignedByte();
								player.privateChat = player.getInStream().readUnsignedByte();
								player.getInStream().readUnsignedByte();
								for (int i1 = 1; i1 < ServerConstants.MAXIMUM_PLAYERS; i1++)
								{
										if (PlayerHandler.players[i1] != null && PlayerHandler.players[i1].isActive() == true)
										{
												Player o = PlayerHandler.players[i1];
												if (o != null)
												{
														o.getPA().updatePM(player.getPlayerId(), 1, player.privateChat == 2);
												}
										}
								}
								break;

						case ADD_IGNORE:
								long ignoreAdd = player.getInStream().readQWord();
								for (int i = 0; i < player.ignores.length; i++)
								{
										if (player.ignores[i] == 0)
										{
												player.ignores[i] = ignoreAdd;
												break;
										}
								}
								break;

				}

		}
}
