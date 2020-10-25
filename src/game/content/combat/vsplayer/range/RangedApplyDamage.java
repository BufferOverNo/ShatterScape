package game.content.combat.vsplayer.range;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.miscellaneous.SpecialAttackTracker;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Handle the methods called once the range hitsplat appears for player vs player.
 * 
 * @author MGT Madness, created on 17-11-2013.
 */
public class RangedApplyDamage
{

		/**
		 *
		 * @param attacker
		 *        The player
		 * @param theTarget
		 *        The player being attacked.
		 * @param damageMask
		 *        1 is for single damages and 2 is for double damages.
		 */
		public static void applyRangedHitSplat(Player attacker, Player victim, int damageMask)
		{
				int damage;
				if (damageMask == 1)
				{
						damage = attacker.rangedFirstDamage;
				}
				else
				{
						damage = attacker.rangedSecondDamage;
				}

				if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
				{
						damage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}
				PlayerHandler.players[victim.getPlayerId()].setUnderAttackBy(attacker.getPlayerId());
				PlayerHandler.players[victim.getPlayerId()].setLastAttackedBy(attacker.getPlayerId());
				if (damageMask == 2)
				{
						SpecialAttackTracker.saveMaximumDamage(attacker, damage, "SECOND", false);
				}
				else
				{
						SpecialAttackTracker.saveMaximumDamage(attacker, damage, "FIRST", false);
				}
				if (!attacker.isUsingDarkBowSpecialAttack())
				{
						Combat.createHitsplatOnPlayer(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON);
				}
				else if (attacker.isUsingDarkBowSpecialAttack() && damageMask == 1)
				{
						Combat.createHitsplatOnPlayer(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON);
						darkBowSpecialAttack(attacker, victim);
				}
				victim.setUpdateRequired(true);
				RangedAmmoUsed.dropAmmo(attacker, victim);
				effects(attacker, victim, damage);
				attacker.botUsedSpecialAttack = false;
		}

		private static void effects(Player attacker, Player victim, int damage)
		{


				morrigansJavelinSpecialAttackDamage(attacker, victim, damage);

				if (attacker.showDiamondBoltGFX)
				{
						victim.gfx0(758);
						attacker.showDiamondBoltGFX = false;
				}
				else if (attacker.showDragonBoltGFX)
				{
						victim.gfx0(756);
						attacker.showDragonBoltGFX = false;
						attacker.specialAttackWeaponUsed[29] = 1;
						attacker.setWeaponAmountUsed(29);
				}
				else if (attacker.showRubyBoltGFX)
				{
						victim.gfx0(754);
						attacker.showRubyBoltGFX = false;
						attacker.subtractFromHitPoints(attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 10);
						victim.subtractFromHitPoints(victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 8);
				}
				// Toxic blowpipe
				if (attacker.getWieldedWeapon() == 18779 && Misc.hasOneOutOf(4))
				{
						Poison.appendPoison(attacker, victim, false, 10);
				}

				Combat.applySmite(attacker, victim, damage);

				if (victim.getVengeance())
				{
						Combat.appendVengeance(attacker, victim, damage);
				}

		}

		/**
		* Apply the Dark bow special attack hitsplat.
		* 
		* @param attacker
		*        The player dealing the hitsplat.
		* @param victim
		*        The player receiving the hitsplat.
		*/
		private static void darkBowSpecialAttack(final Player attacker, final Player victim)
		{
				if (!attacker.isUsingDarkBowSpecialAttack())
				{
						return;
				}
				CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								Combat.createHitsplatOnPlayer(attacker, victim, attacker.rangedSecondDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON);
								container.stop();
						}

						@Override
						public void stop()
						{
						}
				}, 1);
		}

		static void handCannonSpecialAttack(final Player attacker, final Player victim)
		{
				if (!attacker.handCannonSpecialAttack)
				{
						return;
				}
				RangedFormula.calculateRangedDamage(attacker, victim, true);
				CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								applyRangedHitSplat(attacker, victim, 2);
								container.stop();
						}

						@Override
						public void stop()
						{
						}
				}, 4);
		}

		/**
		 * Morrigan's javelin special attack effect.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player under attack.
		 * @param damage
		 *        The damage dealt by Morrigan's javelin special attack.
		 */
		public static void morrigansJavelinSpecialAttackDamage(final Player attacker, final Player victim, int damage)
		{
				if (!attacker.morrigansJavelinSpecialAttack)
				{
						return;
				}
				attacker.amountOfDamages = damage / 5;
				if (attacker.amountOfDamages < 1)
				{
						return;
				}
				attacker.morrigansJavelinDamageToDeal = 5;

				CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (!victim.getDead())
								{
										if (attacker.amountOfDamages > 0)
										{
												attacker.amountOfDamages--;
												if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - attacker.morrigansJavelinDamageToDeal < 0)
												{
														attacker.morrigansJavelinDamageToDeal = victim.currentCombatSkillLevel[ServerConstants.HITPOINTS];
												}
												Combat.createHitsplatOnPlayer(attacker, victim, attacker.morrigansJavelinDamageToDeal, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
										}
										else
										{
												container.stop();
										}
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
						}
				}, 2);
		}
}