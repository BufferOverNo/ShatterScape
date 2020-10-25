package game.content.clanchat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import core.Server;
import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.PlayerRank;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Mute;
import utility.FileUtility;
import utility.Misc;

/**
 * @author Sanity
 */
public class ClanChatHandler
{

		public ClanChatHandler()
		{

		}

		public static Clan[] clans = new Clan[100];

		public static void updateFriendsList(Player player, String newPlayer, boolean added)
		{

				if (!hasPermission(player))
				{
						return;
				}
				if (!clans[player.getClanId()].ownerName.equals(player.getPlayerName()))
				{
						return;
				}
				ArrayList<String> friends = new ArrayList<String>();
				for (int index = 0; index < player.friends.length; index++)
				{
						if (player.friends[index] == 0)
						{
								continue;
						}
						friends.add(Misc.capitalize(Misc.nameForLong(player.friends[index]).replaceAll("_", " ")));
				}
				clans[player.getClanId()].friends = friends;
				clans[player.getClanId()].updated = true;
				if (!clans[player.getClanId()].publicChat)
				{
						if (added)
						{
								player.getPA().sendMessage(Misc.capitalize(newPlayer) + " is now allowed to join this clan chat.");
						}
						else
						{
								player.getPA().sendMessage(Misc.capitalize(newPlayer) + " can no longer join this clan chat.");
								for (int index = 0; index < clans[player.getClanId()].members.length; index++)
								{
										if (clans[player.getClanId()].members[index] <= 0)
										{
												continue;
										}
										Player loop = PlayerHandler.players[clans[player.getClanId()].members[index]];
										if (loop == null)
										{
												continue;
										}
										if (loop.getPlayerName().toLowerCase().equals(newPlayer.toLowerCase()))
										{
												loop.getPA().sendMessage("You are no longer a friend of " + Misc.capitalize(player.getPlayerName()) + ".");
												Server.clanChat.leaveClan(loop.getPlayerId(), loop.getClanId(), true, false, true);
										}
								}
						}
				}
		}

		private static void unBan(Player player, int buttonId, int clanId)
		{
				int buttonIndex = buttonId - 76151;
				if (buttonIndex > clans[clanId].banned.size() - 1)
				{
						if (buttonIndex <= player.clanChatBannedList.size() - 1)
						{
								player.getPA().sendMessage("Another moderator has edited the ban list, please try again.");
								updateBannedText(player);
						}
						return;
				}
				if (!player.clanChatBannedList.get(buttonIndex).equals(clans[clanId].banned.get(buttonIndex)))
				{
						player.getPA().sendMessage("Another moderator has edited the ban list, please try again.");
						updateBannedText(player);
						return;
				}
				clans[clanId].banned.remove(buttonIndex);
				clans[clanId].updated = true;
				updateBannedText(player);

		}

		private static void unMod(Player player, int buttonId, int clanId)
		{
				int buttonIndex = buttonId - 76140;
				if (buttonIndex > clans[clanId].moderators.size() - 1)
				{
						if (buttonIndex <= player.clanChatModeratorList.size() - 1)
						{
								player.getPA().sendMessage("Another moderator has edited the mod list, please try again.");
								updateModeratorText(player);
						}
						return;
				}

				if (!player.clanChatModeratorList.get(buttonIndex).equals(clans[clanId].moderators.get(buttonIndex)))
				{
						player.getPA().sendMessage("Another moderator has edited the mod list, please try again.");
						updateModeratorText(player);
						return;
				}
				clans[clanId].moderators.remove(buttonIndex);
				clans[clanId].updated = true;
				updateModeratorText(player);

		}

		private boolean isBanned(Player player, int clanId)
		{
				for (int index = 0; index < clans[clanId].banned.size(); index++)
				{
						if (player.getPlayerName().equals(clans[clanId].banned.get(index)))
						{
								return true;
						}
				}
				return false;
		}

		public static void serverRestart(boolean nullClanChats)
		{
				for (int index = 0; index < clans.length; index++)
				{
						if (clans[index] == null)
						{
								continue;
						}

						if (clans[index].updated)
						{
								if (clans[index].indexOfClanChatsSavedData == -1)
								{
										ClanChatStartUp item = new ClanChatStartUp(clans[index].ownerName, clans[index].clanChatTitle, clans[index].publicChat, clans[index].banned, clans[index].moderators, true, clans[index].friends);
										ClanChatStartUp.clanChats.add(item);
								}
								else
								{
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).banned = clans[index].banned;
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).moderators = clans[index].moderators;
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).clanChatTitle = clans[index].clanChatTitle;
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).publicChat = clans[index].publicChat;
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).updated = clans[index].updated;
										ClanChatStartUp.clanChats.get(clans[index].indexOfClanChatsSavedData).friends = clans[index].friends;
								}
						}
						if (nullClanChats)
						{
								clans[index] = null;
						}
				}

				for (ClanChatStartUp data : ClanChatStartUp.clanChats)
				{
						if (!data.updated)
						{
								continue;
						}
						if (FileUtility.accountExists("backup/logs/clan chat/" + data.ownerName + ".txt"))
						{
								FileUtility.deleteAllLines("backup/logs/clan chat/" + data.ownerName + ".txt");
						}
						BufferedWriter bw = null;
						try
						{
								bw = new BufferedWriter(new FileWriter("backup/logs/clan chat/" + data.ownerName + ".txt", true));
								bw.write("Title:" + data.clanChatTitle);
								bw.newLine();
								bw.write("Public:" + (data.publicChat ? "on" : "off"));
								bw.newLine();
								for (int index = 0; index < data.moderators.size(); index++)
								{
										bw.write("Mod:" + data.moderators.get(index));
										bw.newLine();
								}
								for (int index = 0; index < data.banned.size(); index++)
								{
										bw.write("Ban:" + data.banned.get(index));
										bw.newLine();
								}
								if (data.friends != null)
								{
										for (int index = 0; index < data.friends.size(); index++)
										{
												bw.write("Friend:" + data.friends.get(index));
												bw.newLine();
										}
								}
								bw.flush();
								bw.close();
						}
						catch (IOException ioe)
						{
								ioe.printStackTrace();
						}
				}


		}

		private void grabDataFromSavedClanChats(Player player)
		{
				int index = 0;
				for (ClanChatStartUp data : ClanChatStartUp.clanChats)
				{
						if (data.ownerName.equals(clans[player.getClanId()].ownerName))
						{
								clans[player.getClanId()].indexOfClanChatsSavedData = index;
								clans[player.getClanId()].clanChatTitle = data.clanChatTitle;
								clans[player.getClanId()].banned = data.banned;
								clans[player.getClanId()].moderators = data.moderators;
								clans[player.getClanId()].publicChat = data.publicChat;
								clans[player.getClanId()].friends = data.friends;
								break;
						}
						index++;
				}
		}

		private static boolean hasPermission(Player player)
		{
				if (player.getClanId() == -1)
				{
						return false;
				}
				if (player.getPlayerName().equals(clans[player.getClanId()].ownerName))
				{
						return true;
				}
				for (int index = 0; index < clans[player.getClanId()].moderators.size(); index++)
				{
						if (player.getPlayerName().equals(clans[player.getClanId()].moderators.get(index)))
						{
								return true;
						}
				}
				return false;
		}

		public static void receiveChangeTitlePacket(Player player, String title)
		{
				if (!hasPermission(player))
				{
						return;
				}
				if (title.length() > 22)
				{
						title = title.substring(0, 22);
				}
				clans[player.getClanId()].updated = true;
				clans[player.getClanId()].clanChatTitle = title;


				updateClanChatTitle(player);
				for (int index = 0; index < clans[player.getClanId()].members.length; index++)
				{
						if (clans[player.getClanId()].members[index] <= 0)
						{
								continue;
						}
						Player loop = PlayerHandler.players[clans[player.getClanId()].members[index]];
						if (loop == null)
						{
								continue;
						}
						if (player.getClanId() == loop.getClanId())
						{
								loop.getPA().sendFrame126("Talking in: " + clans[loop.getClanId()].clanChatTitle, 18140);
						}
				}
		}

		public static void receiveBanPacket(Player player, String name)
		{
				if (name.isEmpty())
				{
						return;
				}
				name = Misc.capitalize(name);

				if (name.equals(player.getPlayerName()))
				{
						return;
				}
				if (!hasPermission(player))
				{
						return;
				}
				for (int index = 0; index < clans[player.getClanId()].banned.size(); index++)
				{
						if (name.equals(clans[player.getClanId()].banned.get(index)))
						{
								player.getPA().sendMessage(Misc.capitalize(name) + " is already banned.");
								return;
						}
				}
				clans[player.getClanId()].updated = true;
				clans[player.getClanId()].banned.add(name);
				updateBannedText(player);

				for (int index = 0; index < clans[player.getClanId()].members.length; index++)
				{
						if (clans[player.getClanId()].members[index] <= 0)
						{
								continue;
						}
						Player loop = PlayerHandler.players[clans[player.getClanId()].members[index]];
						if (loop == null)
						{
								continue;
						}
						if (player.getClanId() == loop.getClanId() && loop.getPlayerName().equals(name))
						{
								Server.clanChat.leaveClan(loop.getPlayerId(), loop.getClanId(), true, true, true);
								break;
						}
				}
		}

		public static void receiveModeratorPacket(Player player, String name)
		{
				if (name.isEmpty())
				{
						return;
				}

				name = Misc.capitalize(name);

				if (!hasPermission(player))
				{
						return;
				}
				for (int index = 0; index < clans[player.getClanId()].moderators.size(); index++)
				{
						if (name.equals(clans[player.getClanId()].moderators.get(index)))
						{
								player.getPA().sendMessage(Misc.capitalize(name) + " is already a moderator.");
								return;
						}
				}
				clans[player.getClanId()].updated = true;
				clans[player.getClanId()].moderators.add(name);
				updateModeratorText(player);
		}

		public static boolean isClanChatButton(Player player, int buttonId)
		{
				if (buttonId >= 76151 && buttonId <= 76151 + 49)
				{
						unBan(player, buttonId, player.getClanId());
						return true;
				}
				if (buttonId >= 76140 && buttonId <= 76140 + 9)
				{
						unMod(player, buttonId, player.getClanId());
						return true;
				}
				switch (buttonId)
				{

						// Public/friends.
						case 76127:
								publicFriendsToggle(player);
								return true;
						// Change title.
						case 76201:
								player.getPA().sendMessage(":cctitle:");
								return true;

						// Ban
						case 76137:
								player.getPA().sendMessage(":ccbanplayer:");
								return true;

						// Moderator
						case 76134:
								player.getPA().sendMessage(":ccmodplayer:");
								return true;

						// Join/Leave clanchat.
						case 70209:
								joinLeaveButton(player);
								return true;
						// Clan chat setup.
						case 70212:
								clanChatSetUp(player);
								return true;
				}
				return false;
		}

		private static void publicFriendsToggle(Player player)
		{

				if (!inClanChat(player))
				{
						player.getPA().sendMessage("You need to be in a clan chat to use this.");
						return;
				}

				if (!hasPermission(player))
				{
						return;
				}

				clans[player.getClanId()].publicChat = !clans[player.getClanId()].publicChat;
				clans[player.getClanId()].updated = true;
				updatePublicText(player);
				if (!clans[player.getClanId()].publicChat)
				{
						int clanId = player.getClanId();
						for (int index = 0; index < clans.length; index++)
						{
								if (clans[clanId] == null)
								{
										continue;
								}
								if (clans[clanId].members[index] <= 0)
								{
										continue;
								}
								Player loop = PlayerHandler.players[clans[clanId].members[index]];
								if (loop == null)
								{
										continue;
								}
								loop.getPA().sendMessage("This clan chat has been changed to friends only.");

								boolean isAFriend = false;
								if (loop.getPlayerName().equalsIgnoreCase(clans[clanId].ownerName))
								{
										isAFriend = true;
								}
								else
								{
										if (clans[clanId].friends == null)
										{
												ArrayList<String> friends = new ArrayList<String>();
												for (index = 0; index < player.friends.length; index++)
												{
														if (player.friends[index] == 0)
														{
																continue;
														}
														friends.add(Misc.capitalize(Misc.nameForLong(player.friends[index]).replaceAll("_", " ")));
												}
												clans[clanId].friends = friends;
										}
										for (int a = 0; a < clans[clanId].friends.size(); a++)
										{
												if (loop.getPlayerName().equalsIgnoreCase(clans[clanId].friends.get(a)))
												{
														isAFriend = true;
														break;
												}
										}
								}
								if (!isAFriend)
								{
										Server.clanChat.leaveClan(loop.getPlayerId(), clanId, true, false, true);
								}
						}
				}
				else
				{
						for (int index = 0; index < clans[player.getClanId()].members.length; index++)
						{
								if (clans[player.getClanId()].members[index] <= 0)
								{
										continue;
								}
								Player loop = PlayerHandler.players[clans[player.getClanId()].members[index]];
								if (loop == null)
								{
										continue;
								}
								loop.getPA().sendMessage("This clan chat has been change to public access.");
						}
				}

		}

		private static boolean inClanChat(Player player)
		{
				if (player.getClanId() >= 0)
				{
						return true;
				}
				return false;
		}

		private static void clanChatSetUp(Player player)
		{
				if (!inClanChat(player))
				{
						player.getPA().sendMessage("You need to be in a clan chat to use this.");
						return;
				}

				if (!hasPermission(player))
				{
						player.getPA().sendMessage("You do not have permissions in this clan chat to use this.");
						return;
				}

				updatePublicText(player);
				updateClanChatTitle(player);
				updateModeratorText(player);
				updateBannedText(player);
				player.getPA().displayInterface(19580);
		}

		private static void updateClanChatTitle(Player player)
		{
				player.getPA().sendFrame126(clans[player.getClanId()].clanChatTitle, 19660);

		}

		private static void updateModeratorText(Player player)
		{
				int lastIdUsed = 19595;
				player.clanChatModeratorList.clear();
				for (int index = 0; index < clans[player.getClanId()].moderators.size(); index++)
				{
						player.getPA().sendFrame126(Misc.capitalize(clans[player.getClanId()].moderators.get(index)), 19596 + index);
						player.clanChatModeratorList.add(clans[player.getClanId()].moderators.get(index));
						lastIdUsed++;
				}
				lastIdUsed++;
				InterfaceAssistant.clearFrames(player, lastIdUsed, 19605);

		}

		private static void updateBannedText(Player player)
		{
				player.clanChatBannedList.clear();
				int lastIdUsed = 19606;
				for (int index = 0; index < clans[player.getClanId()].banned.size(); index++)
				{
						player.getPA().sendFrame126(Misc.capitalize(clans[player.getClanId()].banned.get(index)), 19607 + index);
						player.clanChatBannedList.add(clans[player.getClanId()].banned.get(index));
						lastIdUsed++;
				}
				lastIdUsed++;
				InterfaceAssistant.clearFrames(player, lastIdUsed, 19656);

		}

		private static void updatePublicText(Player player)
		{
				if (clans[player.getClanId()].publicChat)
				{
						player.getPA().sendFrame126("Public", 19586);
				}
				else
				{
						player.getPA().sendFrame126("Friends", 19586);
				}

		}

		public static void joinLeaveButton(Player player)
		{

				if (player.getClanId() != -1)
				{
						Server.clanChat.leaveClan(player.getPlayerId(), player.getClanId(), true, false, false);
						return;
				}
				else
				{
						player.getPA().sendMessage(":joincc:");
				}
		}

		/**
		 * True if the player is allowed to talk in clan chat.
		 * @param player
		 * 			The associated player.
		 */
		public boolean canTalkInClanChat(Player player, String message)
		{
				if (player.getClanId() < 0)
				{
						if (player.getClanId() != -1)
						{
								player.setClanId(-1);
						}
						player.playerAssistant.sendMessage("You are not in a clan chat.");
						return false;
				}
				if (Mute.isMuted(player))
				{
						return false;
				}
				if (System.currentTimeMillis() - player.clanChatMessageTime < 1000)
				{
						player.playerAssistant.sendMessage("Your message was not sent because you are sending messages too quickly.");
						return false;
				}
				if (shatterScapeClanChat(player) && player.isJailed())
				{
						player.playerAssistant.sendMessage("You cannot talk in ShatterScape cc while jailed.");
						return false;
				}
				if (shatterScapeClanChat(player) && Misc.checkForOffensive(message))
				{
						player.playerAssistant.sendMessage("Do not use offensive language in the ShatterScape clan chat.");
						return false;
				}
				int left = 15 - (player.secondsBeenOnline / 60);
				if (shatterScapeClanChat(player) && (player.secondsBeenOnline / 60) < 15)
				{
						player.playerAssistant.sendMessage("You have to be online for " + left + " more minutes to use ShatterScape clan chat.");
						return false;
				}
				return true;

		}

		/**
		 * True, if the player is in the Dawntain clan chat.
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if player is in Dawntain clan chat.
		 */
		private boolean shatterScapeClanChat(Player player)
		{
				return clans[player.getClanId()].ownerName.equalsIgnoreCase("ShatterScape");
		}

		/**
		 * Player logs out.
		 * @param player
		 * 			The associated player.
		 */
		public void logOut(Player player)
		{
				if (player.getClanId() >= 0)
				{
						leaveClan(player.getPlayerId(), player.getClanId(), false, false, true);
				}
		}

		/**
		 * Player joins a clan chat.
		 * @param player
		 * 			The associated player.
		 * @param clanChatName
		 * 			The clan chat name.
		 */
		public void joinClanChat(Player player, String clanChatName)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getClanId() != -1)
				{
						player.playerAssistant.sendMessage("You are already in a clan.");
						return;
				}
				for (int j = 0; j < clans.length; j++)
				{
						if (clans[j] != null)
						{
								if (clans[j].ownerName.equalsIgnoreCase(clanChatName))
								{
										addToClan(player.getPlayerId(), j);
										return;
								}
						}
				}
				makeClan(player, clanChatName);
		}


		public void makeClan(Player player, String clanChatName)
		{
				if (openClan() >= 0)
				{
						if (validName(clanChatName))
						{
								if (player.getClanId() != -1)
								{
										return;
								}
								player.setClanId(openClan());
								clans[player.getClanId()] = new Clan(clanChatName);
								clans[player.getClanId()].clanChatTitle = Misc.capitalize(clans[player.getClanId()].ownerName);
								grabDataFromSavedClanChats(player);
								addToClan(player.getPlayerId(), player.getClanId());
								player.getPA().sendFrame126("Leave Chat", 18135);
						}
						else
						{
								player.playerAssistant.sendMessage("A clan with this name already exists.");
						}
				}
				else
				{
						player.playerAssistant.sendMessage("Your clan chat request could not be completed.");
				}
		}

		public void updateClanChat(int clanId)
		{
				boolean hasAMember = false;
				for (int j = 0; j < clans[clanId].members.length; j++)
				{
						if (clans[clanId].members[j] <= 0)
						{
								continue;
						}
						hasAMember = true;
						Player player = PlayerHandler.players[clans[clanId].members[j]];
						if (PlayerHandler.players[clans[clanId].members[j]] != null)
						{
								player.getPA().sendFrame126("Owner: " + Misc.capitalize(clans[clanId].ownerName), 18139);
								player.getPA().sendFrame126("Talking in: " + clans[clanId].clanChatTitle, 18140);
								int slotToFill = 24600;
								for (int i = 0; i < clans[clanId].members.length; i++)
								{
										if (clans[clanId].members[i] > 0)
										{
												if (PlayerHandler.players[clans[clanId].members[i]] != null)
												{
														player.getPA().sendFrame126(getRankString(PlayerHandler.players[clans[clanId].members[i]]) + Misc.capitalize(PlayerHandler.players[clans[clanId].members[i]].getPlayerName()), slotToFill);
														slotToFill++;
												}
										}
								}
								InterfaceAssistant.clearFrames(player, slotToFill, 24699);
						}
				}
				if (!hasAMember)
				{
						if (clans[clanId].updated)
						{
								if (clans[clanId].indexOfClanChatsSavedData == -1)
								{
										ClanChatStartUp data = new ClanChatStartUp(clans[clanId].ownerName, clans[clanId].clanChatTitle, clans[clanId].publicChat, clans[clanId].banned, clans[clanId].moderators, clans[clanId].updated, clans[clanId].friends);
										ClanChatStartUp.clanChats.add(data);
								}
								else
								{
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).banned = clans[clanId].banned;
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).moderators = clans[clanId].moderators;
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).clanChatTitle = clans[clanId].clanChatTitle;
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).publicChat = clans[clanId].publicChat;
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).updated = clans[clanId].updated;
										ClanChatStartUp.clanChats.get(clans[clanId].indexOfClanChatsSavedData).friends = clans[clanId].friends;
								}
						}
						clans[clanId] = null;
				}
		}

		private String getRankString(Player player)
		{
				if (player.getPlayerName().equals(clans[player.getClanId()].ownerName))
				{
						return "<img=15>";
				}
				for (int index = 0; index < clans[player.getClanId()].moderators.size(); index++)
				{
						if (player.getPlayerName().equals(clans[player.getClanId()].moderators.get(index)))
						{
								return "<img=14>";
						}
				}
				if (clans[player.getClanId()].friends != null)
				{
						for (int a = 0; a < clans[player.getClanId()].friends.size(); a++)
						{
								if (player.getPlayerName().equals(clans[player.getClanId()].friends.get(a)))
								{
										return "<img=16>";
								}
						}
				}
				return "";
		}

		public int openClan()
		{
				for (int j = 0; j < clans.length; j++)
				{
						if (clans[j] == null)
						{
								return j;
						}
				}
				return -1;
		}

		public boolean validName(String name)
		{
				for (int j = 0; j < clans.length; j++)
				{
						if (clans[j] != null)
						{
								if (clans[j].ownerName.equalsIgnoreCase(name))
								{
										return false;
								}
						}
				}
				return true;
		}

		public void addToClan(int playerId, int clanId)
		{
				boolean added = false;
				Player player = PlayerHandler.players[playerId];
				if (clans[clanId] != null)
				{
						if (isBanned(player, clanId))
						{
								player.getPA().sendMessage("You are banned from this clan chat.");
								return;
						}
						if (!clans[clanId].publicChat)
						{

								boolean isAFriend = false;
								if (player.getPlayerName().equals(clans[clanId].ownerName))
								{
										isAFriend = true;
								}
								else
								{
										if (clans[clanId].friends != null)
										{
												for (int a = 0; a < clans[clanId].friends.size(); a++)
												{
														if (player.getPlayerName().equals(clans[clanId].friends.get(a)))
														{
																isAFriend = true;
																break;
														}
												}
										}
								}
								if (!isAFriend)
								{
										player.getPA().sendMessage("This clan chat is for friends only.");
										player.getPA().sendFrame126("Join Chat", 18135);
										player.lastClanChatJoined = "";
										clearClanChat(player, true);
										return;
								}
						}
						for (int j = 0; j < clans[clanId].members.length; j++)
						{
								if (clans[clanId].members[j] <= 0)
								{

										if (player.isBot)
										{
												return;
										}
										if (inDiceCc(player, false))
										{
												player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have joined the 'Dice' cc, all the chat in this cc is logged.");
												player.getPA().sendMessage(ServerConstants.BLUE_COL + "Trade the person before you gamble with them to know their wealth.");
												player.getPA().sendMessage(ServerConstants.BLUE_COL + "Scamming will result in an IP ban and account wipe.");
										}
										clans[clanId].members[j] = playerId;
										player.setClanId(clanId);
										playerJoinedClanChatMessage(PlayerRank.getIconText("", player.playerRights, false) + player.getCapitalizedName() + " has joined the channel.", clanId);
										updateClanChat(clanId);
										player.lastClanChatJoined = clans[player.getClanId()].ownerName;
										player.getPA().sendFrame126("Leave Chat", 18135);
										added = true;
										return;
								}
						}
						if (!added)
						{
								player.getPA().sendMessage("Clan is full.");
						}
				}
		}

		public void leaveClan(int playerId, int clanId, boolean manual, boolean banned, boolean noMessage)
		{
				if (clanId < 0)
				{
						Player c = PlayerHandler.players[playerId];
						c.playerAssistant.sendMessage("You are not in a clan.");
						return;
				}
				if (clans[clanId] != null)
				{
						for (int j = 0; j < clans[clanId].members.length; j++)
						{
								if (clans[clanId].members[j] == playerId)
								{
										clans[clanId].members[j] = -1;
								}
						}
						if (PlayerHandler.players[playerId] != null)
						{
								Player player = PlayerHandler.players[playerId];
								PlayerHandler.players[playerId].setClanId(-1);
								if (banned)
								{
										player.getPA().sendMessage("You have been banned from the clan chat.");
								}
								else if (!noMessage)
								{
										player.playerAssistant.sendMessage("You have left the clan.");
								}
								player.getPA().sendFrame126("Join Chat", 18135);
								if (manual)
								{
										player.lastClanChatJoined = "";
								}
								clearClanChat(player, true);
						}
						updateClanChat(clanId);
				}
				else
				{
						Player c = PlayerHandler.players[playerId];
						PlayerHandler.players[playerId].setClanId(-1);
						c.playerAssistant.sendMessage("You are not in a clan.");
				}
		}

		public void playerJoinedClanChatMessage(String message, int clanId)
		{
				if (clanId < 0)
						return;
				for (int j = 0; j < clans[clanId].members.length; j++)
				{
						if (clans[clanId].members[j] < 0)
								continue;
						if (PlayerHandler.players[clans[clanId].members[j]] != null)
						{
								Player c = PlayerHandler.players[clans[clanId].members[j]];
								c.playerAssistant.sendFilterableMessage("" + message);
						}
				}
		}

		public void playerMessageToClan(int playerId, String message, int clanId)
		{
				if (clanId < 0)
				{
						return;
				}
				Player player1 = PlayerHandler.players[playerId];
				player1.clanChatMessageTime = System.currentTimeMillis();
				if (ClanChatHandler.clans[clanId].ownerName.equalsIgnoreCase("dice"))
				{
						diceLog.add("[" + PlayerHandler.players[playerId].getCapitalizedName() + "] sent a message: " + message);
				}
				for (int j = 0; j < clans[clanId].members.length; j++)
				{
						if (clans[clanId].members[j] <= 0)
								continue;
						if (PlayerHandler.players[clans[clanId].members[j]] != null)
						{
								Player player = PlayerHandler.players[clans[clanId].members[j]];
								player.getPA().sendClan(PlayerHandler.players[playerId].getCapitalizedName(), message, Misc.capitalize(clans[clanId].ownerName), PlayerHandler.players[playerId].playerRights);
						}
				}
		}

		public void clearClanChat(Player player, boolean clearInterface)
		{
				player.setClanId(-1);
				if (clearInterface)
				{
						player.getPA().sendFrame126("Owner: ", 18139);
						player.getPA().sendFrame126("Talking in: ", 18140);
						InterfaceAssistant.clearFrames(player, 24600, 24699);
				}
		}

		/**
		 * Receive the clan chat message that the player typed in.
		 * @param player
		 * 			The associated player.
		 */
		public void receiveClanChatMessage(Player player, String message)
		{
				message = message.substring(1);
				if (!canTalkInClanChat(player, message))
				{
						return;
				}

				/* Check for empty message. */
				if (message.trim().length() == 0)
				{
						return;
				}
				/* Convert message to lowercase and capitalize first letter. */
				String convertMessage = message.substring(1);
				convertMessage = convertMessage.toLowerCase();
				message = message.substring(0, 1).toUpperCase() + convertMessage;

				Server.clanChat.playerMessageToClan(player.getPlayerId(), message, player.getClanId());
		}


		public static ArrayList<String> diceLog = new ArrayList<String>();

		public static void sendDiceClanMessage(String name, int diceChannelId, String message)
		{
				diceLog.add("Official: " + name + ": " + message);
				for (int j = 0; j < ClanChatHandler.clans[diceChannelId].members.length; j++)
				{
						if (ClanChatHandler.clans[diceChannelId].members[j] <= 0)
						{
								continue;
						}
						if (PlayerHandler.players[ClanChatHandler.clans[diceChannelId].members[j]] != null)
						{
								Player loop = PlayerHandler.players[ClanChatHandler.clans[diceChannelId].members[j]];
								loop.getPA().sendClan(name, message, Misc.capitalize(ClanChatHandler.clans[diceChannelId].ownerName), 8);
						}
				}

		}

		public static boolean inDiceCc(Player player, boolean message)
		{
				if (player == null)
				{
						return false;
				}
				if (player.getClanId() < 0)
				{
						if (message)
						{
								player.getPA().sendMessage("You need to be in the 'Dice' channel to gamble.");
						}
						return false;
				}
				if (ClanChatHandler.clans[player.getClanId()] == null)
				{
						return false;
				}
				if (!ClanChatHandler.clans[player.getClanId()].ownerName.equalsIgnoreCase("dice"))
				{
						if (message)
						{
								player.getPA().sendMessage("You need to be in the 'Dice' channel to gamble.");
						}
						return false;
				}
				return true;
		}



}