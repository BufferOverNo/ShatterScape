package game.content.combat;

import core.ServerConstants;
import game.player.Player;

/**
 * Formulas used for multiple combat types.
 * @author MGT Madness, created on 25-03-2015.
 */
public class GlobalCombatFormula
{

		/**
		 * Used for calculated combat advantage.
		 * @param player
		 * 			The associated player.
		 * @param skillLevel
		 * 			The current skill level.
		 * @param highestItemBonus
		 * @param itemBonusMultiplier
		 * @param baseAdvantage
		 * @param otherMultiplier
		 * 			Other multipliers such as prayer and unique item multipliers such as full void.
		 * @param finalMultiplier
		 * @return
		 * 			The invisible advantage.
		 */
		public static int calculateInvisibleAdvantage(Player player, double skillLevel, double highestItemBonus, double itemBonusMultiplier, double baseAdvantage, double otherMultiplier, double finalMultiplier)
		{
				double finalAttackAdvantage = 0;
				double accurateCombatStyleModifier = player.getCombatStyle(ServerConstants.ACCURATE) ? 1.01 : 1.0;
				finalAttackAdvantage += highestItemBonus * itemBonusMultiplier;
				if (!player.isGraniteMaulSpecial)
				{
						otherMultiplier += player.getSpecialAttackAccuracyMultiplier() - 1.0;
				}
				else
				{
						otherMultiplier = 1.0;
				}
				finalAttackAdvantage += baseAdvantage;
				finalAttackAdvantage += skillLevel;
				finalAttackAdvantage *= otherMultiplier;
				finalAttackAdvantage *= accurateCombatStyleModifier;
				finalAttackAdvantage *= levelModifier(player, skillLevel);
				finalAttackAdvantage *= finalMultiplier;
				return (int) finalAttackAdvantage;
		}

		/**
		 * At 118 attack level, it will return 1.0.
		 * <p>
		 * At 72 attack level, it will return 0.80.
		 * <p>
		 * At 1 attack level, it will return 0.50.
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			The number to modify multiple the player's attack advantage.
		 */
		public static double levelModifier(Player player, double level)
		{
				double modifier = 0;
				double addition = 0.50;
				double multipliedBy = addition / 118;
				modifier = level * multipliedBy;
				modifier += addition;
				return modifier;
		}

}