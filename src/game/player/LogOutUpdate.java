package game.player;

import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.donator.DonatorFeatures;
import game.content.highscores.Highscores;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.PlayerGameTime;
import game.content.worldevent.Tournament;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import game.player.punishment.RagBan;
import network.connection.HostList;
import network.packet.PacketHandler;

/**
 * Logging out update.
 * @author MGT Madness, created on 02-03-2015.
 */
public class LogOutUpdate
{

		/**
		* Logout button.
		*/
		public static void manualLogOut(Player player)
		{
				if (Combat.inCombatAlert(player))
				{
						return;
				}
				if (player.getDead())
				{
						return;
				}
				if (player.headIconPk == 2)
				{
						player.getPA().sendMessage(ServerConstants.RED_COL + "You cannot log off with a golden skull!");
						return;
				}
				if (!player.isAdministratorRank())
				{
						if (System.currentTimeMillis() - player.timeNpcAttackedPlayerLogOutTimer <= 10000)
						{
								player.getPA().sendMessage("Please wait a few more seconds after being out of combat to log out.");
								return;
						}
				}
				if (player.playerIsFiremaking || player.doingAnAction() || player.getDoingAgility() || player.isTeleporting() || player.isAnEgg || player.usingPreachingEvent)
				{
						return;
				}
				player.manualLogOut = true;
		}

		/**
		 * Called when player is disconnected.
		 * @param player
		 * 			The associated player.
		 */
		public static void logOutContent(Player player)
		{
				for (int i = 0; i < PacketHandler.packetLogPlayerList.size(); i++)
				{
						if (player.getPlayerName().toLowerCase().equals(PacketHandler.packetLogPlayerList.get(i).toLowerCase()))
						{
								PacketHandler.saveData(player.getPlayerName(), "Has logged off.");
								break;
						}
				}
				player.getTradeAndDuel().claimStakedItems();
				if (player.isInTrade())
				{
						Player o = PlayerHandler.players[player.getTradeWith()];
						if (o != null)
						{
								o.getTradeAndDuel().declineTrade1(true);
						}
				}
				else if (player.getDuelStatus() >= 1 && player.getDuelStatus() <= 5)
				{
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								o.getTradeAndDuel().declineDuel(false);
						}
						player.getTradeAndDuel().declineDuel(false);
				}
				if (player.privateChat != 2)
				{
						for (int i = 1; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] == null || !PlayerHandler.players[i].isActive())
								{
										continue;
								}
								Player o = PlayerHandler.players[i];
								if (o != null)
								{
										o.getPA().updatePM(player.getPlayerId(), 0, true);
								}
						}
				}
				RagBan.removeFromWilderness(player.addressIp);
				Tournament.logOutUpdate(player, false);
				TargetSystem.logOut(player);
				Zombie.logOutUpdate(player);
				PlayerGameTime.loggedOffTime(player);
				Poison.informClientOfPoisonOff(player);
				Server.clanChat.logOut(player);
				player.lastSavedIpAddress = player.addressIp;
				Highscores.sortHighscoresOnLogOut(player);
				DonatorFeatures.resetAfk(player, true);
				CycleEventHandler.getSingleton().stopEvents(player);
		}

		/**
		 * Default changes/method calls that have nothing to do with player content.
		 * @param player
		 * 			The associated player.
		 */
		private static void logOutUpdate(Player player)
		{
				player.logOutSaveTime = System.currentTimeMillis();
				PlayerSave.saveGame(player);
				if (!player.isBot)
				{
						HostList.getHostList().remove(player.session);
				}
				player.setDisconnected(true);
				if (!player.isBot)
				{
						player.session.close();
				}
				if (!player.hasTooManyConnections)
				{
						for (int i = 0; i < HostList.connections.size(); i++)
						{
								if (HostList.connections.get(i).equals(player.addressIp))
								{
										HostList.connections.remove(i);
										break;
								}
						}
				}
				player.session = null;
				player.inStream = null;
				player.setOutStream(null);
				player.setActive(false);
				player.buffer = null;
				player.playerListSize = 0;
				for (int i = 0; i < Player.maxPlayerListSize; i++)
				{
						player.playerList[i] = null;
				}
				player.setX(player.setY(-1));
				player.mapRegionX = player.mapRegionY = -1;
				player.currentX = player.currentY = 0;
				Movement.resetWalkingQueue(player);//d
				PlayerHandler.players[player.getPlayerId()] = null;
		}

		public static void main(Player[] players, int index)
		{
				LogOutUpdate.logOutContent(players[index]);
				LogOutUpdate.logOutUpdate(players[index]);

		}

}
