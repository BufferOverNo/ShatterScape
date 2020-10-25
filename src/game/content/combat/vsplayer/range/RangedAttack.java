package game.content.combat.vsplayer.range;

import core.ServerConstants;
import game.content.achievement.AchievementStatistics;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.vsplayer.AttackPlayer;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.player.Player;
import game.player.movement.Movement;

/**
 * Handle the ranged attack.
 * 
 * @author MGT Madness, created on 28-03-2015.
 */
public class RangedAttack
{

		/**
		 * Start the Ranged special attack.
		 * @param attacker
		 * 			The player using the special attack.
		 * @param victim
		 * 			The player being attacked.
		 */
		public static void rangedSpecialAttack(Player attacker, Player victim)
		{
				RangedFormula.calculateRangedDamage(attacker, victim, false);
				RangedApplyDamage.handCannonSpecialAttack(attacker, victim);
				if (attacker.isMagicBowSpecialAttack() || attacker.isUsingDarkBowSpecialAttack())
				{
						RangedFormula.calculateRangedDamage(attacker, victim, true);
				}
				AchievementStatistics.startNewFight(attacker, victim, "RANGED");
		}

		/**
		 * Initiate the ranged attack.
		 * 
		 * @param attacker
		 *        The player initiating the attack.
		 * @param victim
		 *        The player receiving the attack.
		 */
		public static boolean landRangedAttack(Player attacker, Player victim)
		{

				if (!attacker.getUsingRanged())
				{
						return false;
				}

				if (AttackPlayer.landSpecialAttack(attacker, victim))
				{
						return true;
				}
				attacker.stakeAttacks++;
				AchievementStatistics.startNewFight(attacker, victim, "RANGED");
				attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
				attacker.setLastRangedWeaponUsed(attacker.getWieldedWeapon());
				RangedFormula.calculateRangedDamage(attacker, victim, false);
				if (CombatConstants.isDarkBow(attacker.getWieldedWeapon()))
				{
						attacker.setUsingDarkBowNormalAttack(true);
						RangedFormula.calculateRangedDamage(attacker, victim, true);
				}
				if (Combat.getRangeStartGFX(attacker) > 0)
				{
						attacker.gfx100(Combat.getRangeStartGFX(attacker));
				}
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setProjectileStage(1);
				attacker.setOldPlayerIndex(victim.getPlayerId());
				Combat.fireProjectilePlayer(attacker);
				RangedAmmoUsed.deleteAmmo(attacker);
				if (attacker.getWieldedWeapon() == 11235)
				{
						RangedAmmoUsed.deleteAmmo(attacker);
				}
				return true;
		}

		/**
		 * @param attacker
		 * @param victim
		 * @return
		 * 			True, if the attacker has the Ranged requirements to attack the victim.
		 */
		public static boolean hasRangedRequirements(Player attacker, Player victim)
		{
				boolean usingMediumRangeRangedWeapon = RangedData.isWieldingMediumRangeRangedWeapon(attacker);
				boolean usingShortRangeRangedWeapon = RangedData.isWieldingShortRangeRangedWeapon(attacker);
				boolean hasArrowEquipped = attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= 0 ? false : true;

				if (attacker.getDuelStatus() == ServerConstants.DUELING && attacker.duelRule[2])
				{
						attacker.playerAssistant.sendMessage("Range has been disabled in this duel!");
						Movement.stopMovement(attacker);
						Combat.resetPlayerAttack(attacker);
						return false;
				}

				if (usingMediumRangeRangedWeapon || usingShortRangeRangedWeapon)
				{
						if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.getRangedWeaponDistance(attacker)))
						{
								return false;
						}
				}

				if (!RangedData.hasRequiredAmmo(attacker, hasArrowEquipped))
				{
						Movement.stopMovement(attacker);
						Combat.resetPlayerAttack(attacker);
						return false;
				}
				if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), victim.getHeight(), 1, 1, true))
				{
						return false;
				}


				return true;
		}

		public static void reEngageWithRanged(Player attacker)
		{
				if (!attacker.hasLastCastedMagic())
				{
						if (RangedData.isWieldingMediumRangeRangedWeapon(attacker) || RangedData.isWieldingShortRangeRangedWeapon(attacker))
						{
								attacker.setUsingRanged(true);
						}
				}

		}

}