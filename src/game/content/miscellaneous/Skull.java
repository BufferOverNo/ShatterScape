package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.ItemsKeptOnDeath;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * White and red skull.
 * @author MGT Madness, created on 29-01-2014.
 */
public class Skull
{

		/**
		* Start the cycle event for how long the player killing skull will last for.
		*/
		public static void startSkullTimerEvent(final Player player)
		{

				if (player.isUsingSkullTimerEvent || player.skullTimer == 0)
				{
						return;
				}
				if (player.isCombatBot())
				{
						return;
				}
				player.isUsingSkullTimerEvent = true;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								player.skullTimer--;
								if (player.skullTimer <= 1)
								{
										Skull.clearSkull(player);
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.isUsingSkullTimerEvent = false;
						}
				}, 1);

		}

		/**
		 * Activate white skull.
		 * @param player
		 * 			The associated player.
		 */
		public static void whiteSkull(Player player)
		{
				player.timeScannedForWildernessRisk = 0;
				player.setWhiteSkull(true);
				player.setRedSkull(false);
				player.skullTimer = 1200;
				startSkullTimerEvent(player);
				player.headIconPk = 0;
				player.getPA().requestUpdates();
		}

		/**
		 * Skull the attacker, this method is called during combat.
		 * @param attacker
		 * 			The player attacking.
		 * @param victim
		 * 			The player receiving the attack.
		 */
		public static void combatSkull(Player attacker, Player victim)
		{
				if (Area.inWilderness(attacker))
				{
						if (!attacker.attackedPlayers.contains(attacker.getPlayerIdAttacking()) && !victim.attackedPlayers.contains(attacker.getPlayerId()))
						{
								attacker.attackedPlayers.add(attacker.getPlayerIdAttacking());
								if (!attacker.getRedSkull())
								{
										Skull.whiteSkull(attacker);
								}
						}
				}
		}

		/**
		 * Activate red skull.
		 * @param player
		 * 			The associated player.
		 */
		public static void redSkull(Player player)
		{
				player.setPrayerActive(ServerConstants.PROTECT_ITEM, false);
				player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[10], 0);
				player.setWhiteSkull(false);
				player.setRedSkull(true);
				player.skullTimer = 1200;
				player.timeScannedForWildernessRisk = 0;
				startSkullTimerEvent(player);
				player.headIconPk = 1;
				player.getPA().requestUpdates();
		}

		/**
		 * Activate red skull.
		 * @param player
		 * 			The associated player.
		 */
		public static void goldenSkull(Player player)
		{
				player.setPrayerActive(ServerConstants.PROTECT_ITEM, false);
				player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[10], 0);
				player.setWhiteSkull(false);
				player.setRedSkull(true);
				player.skullTimer = 1200;
				player.timeScannedForWildernessRisk = 0;
				startSkullTimerEvent(player);
				player.headIconPk = 2;
				player.getPA().requestUpdates();
		}

		/**
		 * Turn off the skull.
		 * @param player
		 * 			The associated player.
		 */
		public static void clearSkull(Player player)
		{
				if (player.isCombatBot())
				{
						return;
				}
				player.setWhiteSkull(false);
				player.setRedSkull(false);
				player.headIconPk = -1;
				player.attackedPlayers.clear();
				player.getPA().requestUpdates();
				ItemsKeptOnDeath.updateInterface(player);
		}


}
