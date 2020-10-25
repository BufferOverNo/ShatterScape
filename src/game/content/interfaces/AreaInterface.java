package game.content.interfaces;

import game.content.minigame.TargetSystem;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.worldevent.Tournament;
import game.player.Area;
import game.player.Player;

/**
 * Update walkable interfaces that are changed if the player has entered an area.
 * @author MGT Madness 2-11-2013
 */

public class AreaInterface
{
		public static void updateWalkableInterfaces(final Player player)
		{
				TargetSystem.updateArrowHint(player);
				if (player.isInZombiesMinigame())
				{
						player.getPA().walkableInterface(player.waitingForWave ? 20240 : 20230);
						player.getPA().showOption(3, 0, "Null", 1);
				}
				else if (System.currentTimeMillis() - player.timeExitedWilderness < 10000 || System.currentTimeMillis() - player.timeVictimExitedWilderness < 10000)
				{
						player.getPA().walkableInterface(player.useBottomRightWildInterface ? 24395 : 24390);
						player.getPA().showOption(3, 0, "Attack", 1);
				}
				else if (Area.inWilderness(player) || Area.inSafePkFightZone(player))
				{
						PlayerMiscContent.calculateWildernessLevel(player);
						InterfaceAssistant.wildernessInterface(player);
						player.getPA().showOption(3, 0, "Attack", 1);
				}
				else if (player.tournamentTarget >= 0)
				{
						player.getPA().walkableInterface(-1);
						player.getPA().showOption(3, 0, "Attack", 1);
				}
				else if (player.getHeight() == 20 && player.tournamentTarget == -1)
				{
						player.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
						player.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
						player.getPA().walkableInterface(25980);
						player.getPA().showOption(3, 0, "Null", 1);
				}
				else if (Area.inDuelArena(player))
				{
						player.getPA().walkableInterface(201);
						if (player.getDuelStatus() == 5)
						{
								player.getPA().showOption(3, 0, "Attack", 1);
						}
						else
						{
								player.getPA().showOption(3, 0, "Challenge", 1);
						}
				}
				else if (player.getWieldedWeapon() == 14728 || player.getWieldedWeapon() == 4566 || player.getWieldedWeapon() == 7671 || player.getWieldedWeapon() == 7673 || player.getWieldedWeapon() == 10501)
				{
						player.getPA().showOption(3, 0, "Attack", 1);
				}

				else if (Area.inZombieWaitingRoom(player))
				{
						player.getPA().walkableInterface(-1);
						player.getPA().showOption(3, 0, "Duo", 1);
				}

				// Barrows interface.
				else if (player.getHeight() == -1 || Area.isInBarrowsChestArea(player))
				{
						player.getPA().walkableInterface(22045);
				}
				else if (Area.inGodWarsDungeon(player))
				{
						player.getPA().walkableInterface(25957);
				}
				else
				{
						player.getPA().sendFrame99(0);
						player.getPA().walkableInterface(-1);
						player.getPA().showOption(3, 0, "Null", 1);
				}
				if (!player.hasMultiSign && Area.inMulti(player.getX(), player.getY()))
				{
						player.hasMultiSign = true;
						player.getPA().multiWay(1);
				}
				else if (player.hasMultiSign && !Area.inMulti(player.getX(), player.getY()))
				{
						player.hasMultiSign = false;
						player.getPA().multiWay(-1);
				}
		}

}