package game.content.combat.vsplayer.melee;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsplayer.Effects;
import game.player.Player;
import utility.Misc;

/**
 * Melee Formulas.
 * 
 * @author MGT Madness, created on 20-11-2013.
 */
public class MeleeFormula
{

		/**
		 * @param player
		 *        The associated player.
		 * @return The player's invisible attack advantage.
		 */
		public static int getInvisibleMeleeAttackAdvantage(Player player)
		{
				double advantage = 10; // Base
				double equipmentMultiplier = 1.0;
				double currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.ATTACK);
				double attackBonus = player.playerBonus[getHighestMeleeAttackBonus(player)] * equipmentMultiplier;
				double prayerMultiplier = getMeleePrayerAttackMultiplier(player);
				advantage += currentLevel;
				advantage += attackBonus;
				advantage *= prayerMultiplier;
				if (!player.isGraniteMaulSpecial)
				{
						advantage *= player.getSpecialAttackAccuracyMultiplier();
				}
				if (player.getCombatStyle(ServerConstants.ACCURATE))
				{
						advantage *= 1.02;
				}
				if (player.getCombatStyle(ServerConstants.CONTROLLED))
				{
						advantage *= 1.01;
				}
				double finalMultiplier = 1.5;
				advantage *= finalMultiplier;
				advantage *= 2;
				return (int) advantage;

		}

		/**
		 * @param player
		 *        The associated player.
		 * @return The player's invisible defence advantage.
		 */
		public static int getInvisibleMeleeDefenceAdvantage(Player player)
		{
				double advantage = 10; // Base
				double equipmentMultiplier = 1.0;
				double currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE);
				double defenceBonus = player.playerBonus[getHighestMeleeDefenceBonus(player)] * equipmentMultiplier;
				double prayerMultiplier = getMeleePrayerDefenceMultiplier(player);
				double effectiveDefenceBonus = 0.5;
				effectiveDefenceBonus += 0.004237 * currentLevel;
				advantage += (defenceBonus * effectiveDefenceBonus);
				advantage *= prayerMultiplier;
				double finalMultiplier = 1.65;
				advantage *= finalMultiplier;
				advantage += currentLevel;
				if (player.getCombatStyle(ServerConstants.LONG_RANGED) || player.getCombatStyle(ServerConstants.DEFENSIVE))
				{
						advantage *= 1.02;
				}
				if (player.getCombatStyle(ServerConstants.CONTROLLED))
				{
						advantage *= 1.01;
				}
				advantage *= 2;
				return (int) advantage;
		}

		/**
		 * Get the prayer defence multiplier.
		 * 
		 * @param player
		 *        The associated player.
		 * @return The prayer defence multiplier.
		 */
		public static double getMeleePrayerDefenceMultiplier(Player player)
		{
				if (player.prayerActive[ServerConstants.THICK_SKIN])
				{
						return 1.05;
				}
				else if (player.prayerActive[ServerConstants.ROCK_SKIN])
				{
						return 1.1;
				}
				else if (player.prayerActive[ServerConstants.STEEL_SKIN])
				{
						return 1.15;
				}
				else if (player.prayerActive[ServerConstants.CHIVALRY])
				{
						return 1.2;
				}
				else if (player.prayerActive[ServerConstants.PIETY])
				{
						return 1.25;
				}
				else if (player.prayerActive[ServerConstants.RIGOUR])
				{
						return 1.25;
				}
				else if (player.prayerActive[ServerConstants.AUGURY])
				{
						return 1.25;
				}
				return 1.0;
		}

		/**
		 * Get the prayer attack multiplier.
		 * 
		 * @param player
		 *        The associated player.
		 * @return The prayer attack multiplier.
		 */
		private static double getMeleePrayerAttackMultiplier(Player player)
		{
				if (player.prayerActive[ServerConstants.CLARITY_OF_THOUGHT])
				{
						return 1.05;
				}
				else if (player.prayerActive[ServerConstants.IMPROVED_REFLEXES])
				{
						return 1.10;
				}
				else if (player.prayerActive[ServerConstants.INCREDIBLE_REFLEXES])
				{
						return 1.15;
				}
				else if (player.prayerActive[ServerConstants.CHIVALRY])
				{
						return 1.15;
				}
				else if (player.prayerActive[ServerConstants.PIETY])
				{
						return 1.2;
				}
				return 1.0;
		}


		/**
		 * Get the highest attack bonus.
		 * 
		 * @param player
		 *        The assocaited player.
		 * @return The highest attack bonus.
		 */
		private static int getHighestMeleeAttackBonus(Player player)
		{
				if (player.playerBonus[0] > player.playerBonus[1] && player.playerBonus[0] > player.playerBonus[2])
				{
						return 0;
				}
				if (player.playerBonus[1] > player.playerBonus[0] && player.playerBonus[1] > player.playerBonus[2])
				{
						return 1;
				}
				return player.playerBonus[2] <= player.playerBonus[1] || player.playerBonus[2] <= player.playerBonus[0] ? 0 : 2;
		}

		/**
		 * Get the highest defence bonus.
		 * 
		 * @param player
		 *        Player
		 * @return The highest defence bonus.
		 */
		public static int getHighestMeleeDefenceBonus(Player player)
		{
				int index7 = player.playerBonus[7];
				int index6 = player.playerBonus[6];
				int index5 = player.playerBonus[5];

				if (index5 > index6 && index5 > index7)
				{
						return 5;
				}
				if (index6 > index5 && index6 > index7)
				{
						return 6;
				}
				return index7 <= index5 || index7 <= index6 ? 5 : 7;
		}

		/**
		 * Get the maximum melee damage.
		 * 
		 * @param player
		 *        The associated player.
		 * @return The maximum melee damage.
		 */
		public static int getMaximumMeleeDamage(Player player)
		{
				double prayerBoost = 0;
				if (player.prayerActive[1])
				{
						prayerBoost += 0.05;
				}
				else if (player.prayerActive[6])
				{
						prayerBoost += 0.10;
				}
				else if (player.prayerActive[14])
				{
						prayerBoost += 0.15;
				}
				else if (player.prayerActive[24])
				{
						prayerBoost += 0.18;
				}
				else if (player.prayerActive[25])
				{
						prayerBoost += 0.23;
				}
				double visible_strength = player.getCurrentCombatSkillLevel(ServerConstants.STRENGTH);
				double prayer_multiplier = (1.00 + prayerBoost);
				double style_bonus = player.getCombatStyle(ServerConstants.AGGRESSIVE) ? 3 : player.getCombatStyle(ServerConstants.CONTROLLED) ? 1 : 0;
				boolean meleeVoid = Combat.wearingFullVoidMelee(player);

				double effective_level = visible_strength;

				// Apply the prayer bonus.
				effective_level = Math.floor(effective_level * prayer_multiplier);

				// Apply the style bonus.
				effective_level += style_bonus;

				// Add 8 because the guide says to?
				effective_level += 8;

				// Apply Void bonus if applicable.
				if (meleeVoid)
				{
						effective_level = Math.floor(effective_level * 1.10);
				}
				// Effective level completed above.

				double equipment_bonus = player.playerBonus[10];
				double baseMaxHit = Math.floor(0.5 + effective_level * (equipment_bonus + 64) / 640);

				if (!player.isGraniteMaulSpecial)
				{
						if (Combat.wearingFullDharok(player))
						{
								double dharok_hp_max = player.getBaseHitPointsLevel();
								double dharok_hp_current = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								double dharoks_multiplier = 1 + (dharok_hp_max - dharok_hp_current) / 100 * dharok_hp_max / 100;
								double dharoks_max = Math.floor(baseMaxHit * dharoks_multiplier);
								return (int) dharoks_max;
						}
						int TOKTZ_XIL_EK = 6525; // Obsidian dagger.
						int TZHAAR_KET_OM = 6528; // Obby maul.
						int BERSERKER_NECKLACE = 11128;

						if (player.playerEquipment[ServerConstants.AMULET_SLOT] == BERSERKER_NECKLACE)
						{
								if (player.getWieldedWeapon() == TZHAAR_KET_OM)
								{
										return (int) (baseMaxHit * 1.25);
								}
								if (player.getWieldedWeapon() == TOKTZ_XIL_EK)
								{
										return (int) (baseMaxHit * 1.20);
								}
						}
						if (player.specDamage > 1.0)
						{
								return (int) (baseMaxHit * player.specDamage);
						}

				}
				return (int) baseMaxHit;
		}


		/**
		 * Reduce the damage if the victim has melee protection prayer active.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player under attack.
		 * @param damage
		 *        The attacker's damage.
		 */
		public static int getVictimMeleePrayerActive(Player attacker, Player victim, int damage)
		{
				if (attacker.veracEffectActivated && !attacker.isGraniteMaulSpecial)
				{
						return damage;
				}
				if (victim.prayerActive[ServerConstants.PROTECT_FROM_MELEE] && !Combat.wearingFullVerac(attacker))
				{
						damage *= 0.6;
				}
				return damage;
		}

		/**
		 * Calculate the damage that the player will deal.
		 *
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player being attacked.
		 * @param damageType
		 *        1 is for a single hitsplat, 2 is for the second hitsplat and so on..
		 */
		public static void calculateMeleeDamage(Player attacker, Player victim, int damageType)
		{
				int damage = 0;

				// When adding anything here be sure to put a  && !player.isGraniteMaulSpecial.
				MeleeAttack.saveCriticalDamage(attacker);

				if (MeleeFormula.getMeleeDamage0(attacker, victim))
				{
						damage = 0;
				}
				else
				{
						int maximumDamage = getMaximumMeleeDamage(attacker);
						int lowest = 1;
						damage = Misc.random(lowest, maximumDamage);
						damage = MeleeFormula.saradominSwordSpecialAttack(attacker, damage, damageType);
				}
				damage = Effects.victimWearingSpiritShield(victim, damage);
				damage = getVictimMeleePrayerActive(attacker, victim, damage);
				if (attacker.hit1)
				{
						damage = getMaximumMeleeDamage(attacker);
				}
				if (damageType == 1)
				{
						if (attacker.isGraniteMaulSpecial)
						{
								attacker.graniteMaulSpecialDamage = damage;
								attacker.isGraniteMaulSpecial = false;
						}
						else
						{
								attacker.meleeFirstDamage = damage;
						}
				}
				else if (damageType == 2)
				{
						attacker.meleeSecondDamage = damage;
				}
				else if (damageType == 3)
				{
						attacker.meleeThirdDamage = damage;
				}
				else if (damageType == 4)
				{
						attacker.meleeFourthDamage = damage;
				}
		}


		/**
		 * True, if the damage will 0.
		 *
		 * @param c
		 *        Player
		 * @param i
		 *        Other player
		 */
		public static boolean getMeleeDamage0(Player player, Player victim)
		{
				int difference;
				if (player.wearingFullVerac && Misc.hasPercentageChance(25) && !player.isGraniteMaulSpecial)
				{
						difference = 1;
						player.veracEffectActivated = true;
				}
				else
				{
						int attackerMaxRoll = getInvisibleMeleeAttackAdvantage(player);
						int victimMaxRoll = getInvisibleMeleeDefenceAdvantage(victim);
						difference = Misc.random(attackerMaxRoll) - Misc.random(victimMaxRoll);
				}
				if (difference <= 0)
				{
						return true;
				}
				return false;
		}


		/**
		 * Saradomin sword special attack effect.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player under attack.
		 */
		public static int saradominSwordSpecialAttack(Player attacker, int damage, int damageType)
		{
				if (damageType != 2)
				{
						return damage;
				}
				if (!attacker.saradominSwordSpecialAttack)
				{
						return damage;
				}
				if (attacker.isGraniteMaulSpecial)
				{
						return damage;
				}
				damage = Misc.random(damage);
				if (damage > 18)
				{
						damage = 18;
				}
				return damage;
		}


		/**
		 * Calculate the Dragon claws special attack.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player under attack.
		 */
		public static void calculateDragonClawsSpecialAttack(Player attacker, Player victim)
		{
				if (!attacker.getDragonClawsSpecialAttack())
				{
						return;
				}
				calculateMeleeDamage(attacker, victim, 1);
				int damage1 = attacker.meleeFirstDamage;
				int damage2 = 0;
				int damage3 = 0;
				int damage4 = 0;

				/* Start of First result. */
				if (damage1 > 0)
				{
						damage2 = damage1 / 2;
						damage3 = damage2 / 2;
						damage4 = damage3;
				}
				else
				{
						calculateMeleeDamage(attacker, victim, 1);
						damage1 = attacker.meleeFirstDamage;
						damage2 = damage1 / 2;
						damage3 = damage2 / 2;
						damage4 = damage3;
				}
				/* End of First result. */
				if (damage1 == 0)
				{
						calculateMeleeDamage(attacker, victim, 3);
						damage3 = attacker.meleeThirdDamage;
						damage4 = damage3;
				}
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, (damage1 + damage2 + damage3 + damage4));
				attacker.meleeSecondDamage = damage2;
				attacker.meleeThirdDamage = damage3;
				attacker.meleeFourthDamage = damage4;
		}
}