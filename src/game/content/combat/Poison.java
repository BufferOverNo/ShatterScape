package game.content.combat;

import core.ServerConstants;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Combat posioning.
 * @author MGT Madness, created on 09-04-2014.
 */
public class Poison
{
		public static void removePoison(Player player)
		{
				player.poisonDamage = 0;
				informClientOfPoisonOff(player);
		}

		/**
		 * Inform the client to turn on the poison orb.
		 * @param player
		 * 			The associated player.
		 */
		public static void informClientOfPoisonOn(Player player)
		{
				player.playerAssistant.sendMessage(":poisonon:");
		}

		/**
		 * Inform the client to turn off the poison orb.
		 * @param player
		 * 			The associated player.
		 */
		public static void informClientOfPoisonOff(Player player)
		{
				player.playerAssistant.sendMessage(":poisonoff:");
		}

		/**
		 * Poison the player.
		 */
		public static void appendPoison(final Player attacker, final Player player, boolean logInUpdate, int poisonDamage)
		{
				if (poisonDamage > player.poisonDamage)
				{
						player.poisonDamage = poisonDamage;
						player.poisonHitsplatsLeft = 4;
				}
				if (player.poisonEvent)
				{
						return;
				}
				if (player.poisonDamage == 0 && logInUpdate)
				{
						return;
				}
				if (!logInUpdate)
				{
						// Has anti poison effect.
						if (System.currentTimeMillis() - player.lastPoisonSip < player.poisonImmune)
						{
								return;
						}
						if (Combat.hasSerpentineHelm(player))
						{
								return;
						}

						player.playerAssistant.sendMessage("You have been poisoned!");
						player.poisonDamage = poisonDamage;
						player.poisonHitsplatsLeft = 3;
						player.poisonTicksUntillDamage = 100;
						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										container.stop();
								}

								@Override
								public void stop()
								{
										if (attacker == null)
										{
												Combat.appendHitFromNpcOrVengEtc(player, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
										}
										else
										{
												attacker.ignoreInCombat = true;
												Combat.createHitsplatOnPlayer(attacker, player, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
												attacker.ignoreInCombat = false;
										}
								}
						}, 1);
				}
				player.poisonEvent = true;
				Poison.informClientOfPoisonOn(player);

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.getDead())
								{
										container.stop();
										return;
								}
								if (System.currentTimeMillis() - player.lastPoisonSip < player.poisonImmune)
								{
										container.stop();
										return;
								}
								if (player.poisonDamage == 0)
								{
										container.stop();
										return;
								}
								int damage = player.poisonDamage;

								player.poisonTicksUntillDamage--;
								if (player.poisonTicksUntillDamage == 0)
								{
										if (attacker == null)
										{
												Combat.appendHitFromNpcOrVengEtc(player, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
										}
										else
										{
												attacker.ignoreInCombat = true;
												Combat.createHitsplatOnPlayer(attacker, player, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
												attacker.ignoreInCombat = false;
										}
										player.poisonHitsplatsLeft--;
										player.poisonTicksUntillDamage = 100;

										if (player.poisonHitsplatsLeft == 0)
										{
												if (player.poisonDamage == 1)
												{
														player.playerAssistant.sendMessage("The poison has worn off.");
														container.stop();
														return;
												}
												else
												{

														player.poisonDamage--;
														player.poisonHitsplatsLeft = 4;
												}
										}
								}



						}

						@Override
						public void stop()
						{
								player.poisonEvent = false;
								Poison.removePoison(player);
						}
				}, 1);
		}

}
