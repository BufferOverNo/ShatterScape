package game.content.combat.vsplayer.melee;

import core.ServerConstants;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.content.achievement.AchievementStatistics;
import game.content.combat.Combat;
import game.content.combat.HolidayItem;
import game.content.combat.vsplayer.AttackPlayer;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import game.player.movement.Movement;

/**
 * General melee related.
 * 
 * @author MGT Madness, created on 08-02-2014.
 */
public class MeleeAttack
{

		/**
		 * A normal single hitsplat damage melee attack.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param theVictim
		 *        The player being attacked.
		 * @param specialAttack
		 *        True, if the attacker is using a special attack.
		 */
		public static boolean normalMeleeAttack(Player attacker, Player victim)
		{
				if (attacker.hasLastCastedMagic() || attacker.getUsingRanged())
				{
						return false;
				}
				if (HolidayItem.isHolidayItem(attacker, victim))
				{
						return true;
				}

				if (AttackPlayer.landSpecialAttack(attacker, victim))
				{
						return true;
				}
				attacker.stakeAttacks++;
				attacker.setMeleeFollow(true);
				AchievementStatistics.startNewFight(attacker, victim, "MELEE");
				attacker.startAnimation(MeleeData.getWeaponAnimation(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				MeleeFormula.calculateMeleeDamage(attacker, victim, 1);
				MeleeAttack.earlyEffects(attacker, victim, attacker.meleeFirstDamage, false);
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, attacker.meleeFirstDamage);
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setProjectileStage(0);
				attacker.setOldPlayerIndex(victim.getPlayerId());
				return true;
		}

		public static boolean hasVestaLongSwordRequirement(Player attacker, Player victim, Npc npc)
		{
				// Vesta's longsword
				if (attacker.getWieldedWeapon() == 13899)
				{
						if (System.currentTimeMillis() - attacker.vestaLongSwordTime > 30000)
						{
								if (System.currentTimeMillis() - attacker.vestaLongSwordAttackTime > 30000 && attacker.vestaLongSwordUses == 1)
								{
										attacker.vestaLongSwordUses = 0;
								}
								attacker.vestaLongSwordUses++;
								attacker.vestaLongSwordAttackTime = System.currentTimeMillis();
						}
						else
						{
								attacker.playerAssistant.sendMessage("The Vesta's longsword is worn out.");
								Combat.resetPlayerAttack(attacker);
								Movement.stopMovement(attacker);
								if (victim != null)
								{
										attacker.turnPlayerTo(victim.getX(), victim.getY());
								}
								else if (npc != null)
								{
										attacker.turnPlayerTo(npc.getX(), npc.getY());
								}
								return false;
						}
						if (attacker.vestaLongSwordUses == 2)
						{
								attacker.vestaLongSwordUses = 0;
								attacker.vestaLongSwordTime = System.currentTimeMillis();
						}
				}
				return true;
		}

		/**
		* Melee special attack.
		* 
		* @param attacker
		*        The player attacking.
		* @param victim
		*        The player under attack.
		*/
		public static void meleeSpecialAttack(Player attacker, Player victim)
		{
				singleHitSplatSpecialAttack(attacker, victim);
				multipleHitSplatSpecialAttack(attacker, victim);
				AchievementStatistics.startNewFight(attacker, victim, "MELEE");
		}

		/**
		 * Special attacks that cause 1 hitsplat.
		 * 
		 * @param attacker
		 *        The player who is using the special attack.
		 * @param victim
		 *        The player being attacked.
		 */
		public static void singleHitSplatSpecialAttack(Player attacker, Player victim)
		{
				if (attacker.getMultipleDamageSpecialAttack())
				{
						return;
				}
				MeleeFormula.calculateMeleeDamage(attacker, victim, 1);
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, attacker.meleeFirstDamage);
		}

		/**
		 * Special attacks that cause multiple hitsplats.
		 * 
		 * @param attacker
		 *        The player who is using the special attack.
		 * @param victim
		 *        The player being attacked.
		 */
		public static void multipleHitSplatSpecialAttack(Player attacker, Player victim)
		{
				if (attacker.getDragonClawsSpecialAttack())
				{
						MeleeFormula.calculateDragonClawsSpecialAttack(attacker, victim);
						return;
				}
				if (!attacker.getMultipleDamageSpecialAttack())
				{
						return;
				}
				MeleeFormula.calculateMeleeDamage(attacker, victim, 1);
				MeleeFormula.calculateMeleeDamage(attacker, victim, 2);
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, (attacker.meleeSecondDamage + attacker.meleeFirstDamage));
		}

		/**
		 * Grab the maximum melee damage of the attacker, to use with the 634 hitsplats criticals.
		 * 
		 * @param attacker
		 *        The player attacking.
		 */
		public static void saveCriticalDamage(Player attacker)
		{
				if (attacker.isGraniteMaulSpecial)
				{
						attacker.graniteMaulSpecialCriticalDamage = MeleeFormula.getMaximumMeleeDamage(attacker);
				}
				else
				{
						attacker.maximumDamageMelee = MeleeFormula.getMaximumMeleeDamage(attacker);
				}
		}

		public static boolean hasMeleeRequirements(Player attacker, Player victim)
		{
				if (attacker.duelRule[3] && attacker.getDuelStatus() == ServerConstants.DUELING)
				{
						attacker.playerAssistant.sendMessage("Melee has been disabled in this duel!");
						return false;
				}

				if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, Combat.getRequiredDistance(attacker)))
				{
						if (attacker.isFrozen())
						{
								Combat.resetPlayerAttack(attacker);
						}
						else
						{
								attacker.ignorePlayerTurn = true;
						}
						return false;
				}

				if (attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, Combat.getRequiredDistance(attacker)))
				{
						if (attacker.playerAssistant.isDiagonalFromTarget(victim) && !victim.isMoving())
						{
								if (attacker.isFrozen())
								{
										Combat.resetPlayerAttack(attacker);
								}
								else
								{
										attacker.ignorePlayerTurn = true;
								}

								// They kite the bot around a tree where the bot is diagonal from the player, so the bot just stays there forever.
								if (attacker.isBot)
								{
										if (attacker.botDiagonalTicks >= 6)
										{
												if (!attacker.prayerActive[attacker.botLastDamageTakenType])
												{
														BotContent.togglePrayer(attacker, attacker.botLastDamageTakenType, true);
												}
												attacker.setBotStatus("LOOTING");
												BotContent.retreatToBank(attacker, false);
												BotCommunication.sendBotMessage(attacker, "?", false);
												BotContent.walkToBankArea(attacker);
												attacker.botDiagonalTicks = 0;
										}
										else
										{
												attacker.botDiagonalTicks++;
										}
								}
								return false;
						}
				}

				return true;

		}

		/**
		 * Effects that are applied when the weapon animation starts.
		 * 
		 * @param attacker
		 *        The player attacking the victim.
		 * @param theVictim
		 *        The player being attacked.
		 * @param damage
		 *        The damage of the attacker.
		 * @param special
		 *        True, if a special attack is being used.
		 */
		public static void earlyEffects(Player attacker, Player victim, int damage, boolean special)
		{
				if (special)
				{
				}
				else
				{
						attacker.wearingFullGuthan = false;
						attacker.wearingFullVerac = false;
						if (Combat.wearingFullVerac(attacker))
						{
								attacker.wearingFullVerac = true;
						}
						if (Combat.wearingFullGuthan(attacker))
						{
								attacker.wearingFullGuthan = true;
						}
				}
		}
}