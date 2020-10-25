package game.content.combat.vsplayer.range;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.player.Player;
import utility.Misc;

/**
 * Player Range formulas.
 * 
 * @author MGT Madness, created on 17-11-2013.
 */

public class RangedFormula
{

		/**
		 * Generate the range damage for normal attacks that only deal 1 hitsplat at a time.
		 * <p>
		 * This method is called during the weapon animation and the damage is used when the hitsplat appears. Remember to update calculateSecondDamage same as this but change the rangeSecondDamage. For calculateSecondDamage, remove the Bolts Effects part.
		 * 
		 * @param attacker
		 *        The player that is attacking.
		 * @param theTarget
		 *        The player being attacked.
		 */
		public static void calculateRangedDamage(Player attacker, Player victim, boolean secondDamage)
		{
				attacker.maximumDamageRanged = getRangedMaximumDamage(attacker);

				int damage;
				int arrow = attacker.playerEquipment[ServerConstants.ARROW_SLOT];
				boolean damageWillNotBe0 = false;

				/* Player activate Diamond bolts (e) special attack */
				if (arrow == 9243 && Misc.hasPercentageChance(7)) // Diamond bolt (e)
				{
						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										damageWillNotBe0 = true;
										attacker.showDiamondBoltGFX = true;
								}
						}
				}
				if (isRangedDamage0(attacker, victim) && !damageWillNotBe0)
				{
						damage = 0;
				}
				else
				{
						damage = Misc.random(1, attacker.maximumDamageRanged);
				}
				damage = Effects.victimWearingSpiritShield(victim, damage);
				if (victim.prayerActive[ServerConstants.PROTECT_FROM_RANGED])
				{
						damage *= 0.6;
				}

				/* Player Dark bow special attack is active. */
				if (attacker.isUsingDarkBowSpecialAttack() && damage < 8)
				{
						damage = 8;
				}

				/* Player activate Dragon bolts (e) special attack */
				if (arrow == 9244 & Combat.antiFire(victim, true, false) <= 0 && Misc.hasPercentageChance(7))
				{
						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										attacker.showDragonBoltGFX = true;
										damage *= 1.45;
								}
						}
				}

				/* Player activate the Ruby bolts (e) special attack */
				if (arrow == 9242 && damage > 0 && Misc.hasPercentageChance(7))
				{

						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										attacker.showRubyBoltGFX = true;
								}
						}
				}

				if (attacker.blowpipeSpecialAttack)
				{
						attacker.addToHitPoints(damage / 2);
						attacker.blowpipeSpecialAttack = false;
				}
				if (secondDamage)
				{
						attacker.rangedSecondDamage = damage;
				}
				else
				{
						attacker.rangedFirstDamage = damage;
				}
				Combat.addCombatExperience(attacker, ServerConstants.RANGED_ICON, damage);
				victim.setUnderAttackBy(attacker.getPlayerId());
		}

		/**
		 * @param player
		 *        The player
		 * @return The attack multiplier.
		 */
		public static double getRangedAttackMultiplier(Player player)
		{
				double multiplier = getPrayerRangedMultiplier(player);
				if (RangedData.wearingFullVoidRanged(player))
				{
						multiplier += 0.20;//Apparently void ranged attack multiplier is added twice on Osrs, so it has to be 0.20 then.
				}
				return multiplier;
		}

		private static double getPrayerRangedMultiplier(Player player)
		{
				if (player.prayerActive[ServerConstants.SHARP_EYE])
				{
						return 1.05;
				}
				else if (player.prayerActive[ServerConstants.HAWK_EYE])
				{
						return 1.10;
				}
				else if (player.prayerActive[ServerConstants.EAGLE_EYE])
				{
						return 1.15;
				}
				else if (player.prayerActive[ServerConstants.RIGOUR])
				{
						return 1.20;
				}
				return 1.0;
		}

		/**
		 * @param player
		 *        The player.
		 * @return The defence multiplier.
		 */
		public static double getRangedDefenceMultiplier(Player player)
		{
				double multiplier = MeleeFormula.getMeleePrayerDefenceMultiplier(player);
				return multiplier;
		}

		/**
		 * @param player
		 *        The associated player.
		 * @return The player's invisible ranged attack advantage.
		 */
		public static int getInvisibleRangedAttackAdvantage(Player player)
		{
				double skillLevel = player.getCurrentCombatSkillLevel(ServerConstants.RANGED);
				double highestItemBonus = player.playerBonus[ServerConstants.RANGED_ATTACK_BONUS];
				double itemBonusMultiplier = 1.0;
				double baseAdvantage = 10;
				double otherMultiplier = getRangedAttackMultiplier(player);
				double finalMultiplier = 1.65;
				if (player.getWieldedWeapon() == 18779)
				{
						highestItemBonus += player.blowpipeDartItemId == 11230 ? 18 : player.blowpipeDartItemId == 811 ? 15 : 0;
				}
				double finalAttackAdvantage = 0;
				double accurateCombatStyleModifier = player.getCombatStyle(ServerConstants.ACCURATE) ? 1.01 : 1.0;
				finalAttackAdvantage += highestItemBonus * itemBonusMultiplier;
				finalAttackAdvantage += baseAdvantage;
				finalAttackAdvantage += skillLevel;
				finalAttackAdvantage *= otherMultiplier;
				finalAttackAdvantage *= accurateCombatStyleModifier;
				finalAttackAdvantage *= finalMultiplier;
				finalAttackAdvantage *= 2.27;
				return (int) finalAttackAdvantage;
		}

		//143 attack bonus, 92 ranged level vs 224 defence bonus, 93 defence level
		//143 attack bonus, 104 ranged level vs 224 defence bonus, 76 defence level
		//143 attack bonus, 92 ranged level vs 132 defence bonus, 76 defence level
		//143 attack bonus, 92 ranged level vs 0 defence bonus, 76 defence level
		//143 attack bonus, 92 ranged level vs 159 defence bonus, 40 defence level
		public static int getInvisibleRangedDefenceAdvantage(Player player)
		{
				double skillLevel = player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE);
				double highestItemBonus = player.playerBonus[ServerConstants.RANGED_DEFENCE_BONUS];
				double itemBonusMultiplier = 1;
				double baseAdvantage = 10;
				double otherMultiplier = getRangedDefenceMultiplier(player);
				double finalMultiplier = 1.73;
				double advantage = 0;
				double accurateCombatStyleModifier = player.getCombatStyle(ServerConstants.DEFENSIVE) ? 1.01 : player.getCombatStyle(ServerConstants.LONG_RANGED) ? 1.01 : 1.0;
				double effectiveDefenceBonus = 0.5;
				effectiveDefenceBonus += 0.004237 * skillLevel;
				highestItemBonus *= effectiveDefenceBonus;
				advantage += highestItemBonus * itemBonusMultiplier;
				advantage += baseAdvantage;
				advantage *= otherMultiplier;
				advantage *= accurateCombatStyleModifier;
				advantage += skillLevel;
				advantage *= finalMultiplier;
				advantage *= 2.27;
				return (int) advantage;
		}

		/**
		 * Calculate weather the 0 should appear.
		 *
		 * @param attacker
		 * @param victim
		 */
		public static boolean isRangedDamage0(Player attacker, Player victim)
		{
				int Difference;
				Difference = Misc.random(getInvisibleRangedAttackAdvantage(attacker)) - Misc.random(getInvisibleRangedDefenceAdvantage(victim));
				if (Difference <= 0)
				{
						return true;
				}
				return false;
		}

		/**
		 * Calculate the maximum range damage depending on weapon etc..
		 * 
		 * @param player
		 *        The player
		 * @return The max damage of range.
		 */
		public static int getRangedMaximumDamage(Player player)
		{
				int rangeLevel = player.getCurrentCombatSkillLevel(ServerConstants.RANGED);
				double modifier = 1.0;
				double specialAttackDamageMultiplier = player.specDamage;
				if (player.prayerActive[ServerConstants.SHARP_EYE])
				{
						modifier += 0.05;
				}
				else if (player.prayerActive[ServerConstants.HAWK_EYE])
				{
						modifier += 0.10;
				}
				else if (player.prayerActive[ServerConstants.EAGLE_EYE])
				{
						modifier += 0.15;
				}
				else if (player.prayerActive[ServerConstants.RIGOUR])
				{
						modifier += 0.23;
				}
				if (RangedData.wearingFullVoidRanged(player))
				{
						modifier += 0.20;
				}

				double c = modifier * rangeLevel;
				int rangedStrength = getRangedStrength(player);
				double maxDamage = (c + 8) * (rangedStrength + 70) / 640;
				maxDamage *= specialAttackDamageMultiplier;
				if (maxDamage < 1)
				{
						maxDamage = 1;
				}
				if (maxDamage > 48 && player.isUsingDarkBowSpecialAttack())
				{
						maxDamage = 48;
				}
				return (int) maxDamage;
		}

		public static int getRangedStrength(Player player)
		{
				int arrow = 0;
				arrow = player.playerEquipment[ServerConstants.ARROW_SLOT];
				int strength = 0;

				// Amulet of anguish
				if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18815)
				{
						strength += 5;
				}

				// Wielded items come first before arrows strength bonuses, or else it will stack with arrows and that is a bug and is overpowered.
				switch (player.getWieldedWeapon())
				{
						// Toxic blowpipe
						case 18779:
								return strength += 40 + (player.blowpipeDartItemId == 11230 ? 20 : player.blowpipeDartItemId == 811 ? 14 : 0);

						// knifes
						case 864:
								return strength += 3;

						case 863:
								return strength += 4;

						case 865:
								return strength += 7;

						case 866:
								return strength += 10;

						case 867:
								return strength += 14;

						case 868:
								return strength += 24;



						case 806: // Bronze dart.
								return strength += 1;

						case 807: // Iron dart.
								return strength += 3;

						case 808: // Steel dart.
								return strength += 4;

						case 809: // Mithril dart.
								return strength += 7;

						case 810: // Adamant dart.
								return strength += 10;

						case 811: // Rune dart.
								return strength += 14;

						case 11230: // Dragon dart.
								return strength += 20;

						// Morrigan's javelin.
						case 13879:
								return strength += 100;
						// Morrigan's throwing axe.
						case 13883:
								return strength += 110;
						//Toktz-xil-ul.	
						case 6522:
								return strength += 49;

						// Heavy ballista, this is only exception other than twisted bow that uses break instead of return, as it uses Dragon javelins as ammo.
						case 18807:
								strength += 15;
								break;

						// Twisted bow, the weapon itself has a strength bonus that stacks ontop of the arrow.
						case 18830:
								strength += 20;
								break;

				}
				switch (arrow)
				{
						// bronze to rune bolts
						case 877:
								strength += 10;
								break;
						case 9140:
								strength += 46;
								break;
						case 9141:
								strength += 64;
								break;
						case 18819:
								strength += 150;
								break;

						// Enchanted bolts.
						case 9142:
						case 9241:
						case 9240:
						case 9338:
						case 9337:
								strength += 82;
								break;
						case 9143:
						case 9243:
						case 9242:
						case 9339:
						case 9340: // Diamond bolt
								strength += 100;
								break;
						case 9144: // Runite bolt
						case 9244: // Dragon bolt e
						case 9245: // Onxy bolt e
						case 9341: // Dragon bolt
						case 9342: // Onyx bolt
								strength += 115;
								break;

						case 15243:
								// Hand cannon shot.
								strength += 140;
								break;

						// bronze to dragon arrows
						case 882:
								strength += 7;
								break;
						case 884:
								strength += 10;
								break;
						case 886:
								strength += 16;
								break;
						case 888:
								strength += 22;
								break;
						case 890:
								strength += 31;
								break;
						case 10159:
								strength += 38;
								break;
						case 892:
								strength += 49;
								break;
						case 4740:
								strength += 59;
								break;
						case 11212:
								strength += 60;
								break;

				}
				return strength;

		}


}