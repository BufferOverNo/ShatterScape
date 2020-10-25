package game.content.combat.vsplayer.magic;

import core.ServerConstants;
import game.content.combat.CombatConstants;
import game.content.combat.vsplayer.Effects;
import game.player.Player;
import utility.Misc;

/**
 * Player vs player Magic formulas
 * @author MGT Madness, created on 21-11-2013.
 */
public class MagicFormula
{

		/**
		 * calculate the int magicDamage.
		 * <p>
		 * This is called when the animation starts.
		 *
		 * @param attacker The player who is attacking.
		 * @param theTarget The player being attacked.
		 */
		public static int calculateMagicDamage(Player attacker, Player victim)
		{
				attacker.setMaximumDamageMagic(getMagicMaximumDamage(attacker));
				int damage = Misc.random2(attacker.getMaximumDamageMagic()); // The damage to the target.
				damage = Effects.victimWearingSpiritShield(victim, damage);
				if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
				{
						damage *= 0.6;
				}
				return attacker.setMagicDamage(damage);
		}

		/**
		 * The maximum damage with magic.
		 * @param player The player.
		 * @return The maximum damage
		 */
		public static int getMagicMaximumDamage(Player player)
		{
				double damage = CombatConstants.MAGIC_SPELLS[player.getSpellId()][6];
				double bonusDamageMultiplier = 1;

				bonusDamageMultiplier += getMagicPercentageDamageBonus(player);
				damage *= bonusDamageMultiplier;
				int roundedDamage = (int) Math.round(damage);
				return roundedDamage;
		}

		public static double getMagicPercentageDamageBonus(Player player)
		{
				double bonusDamageMultiplier = 0.0;
				switch (player.getWieldedWeapon())
				{


						case 15486: // Staff of Light.
						case 18781: // Staff of the dead.
						case 18783: // Toxic staff of the dead.
								bonusDamageMultiplier += 0.15;
								break;
				}

				if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18765) // Arcane Stream necklace.
				{
						bonusDamageMultiplier += 0.10;
				}

				if (player.playerEquipment[ServerConstants.RING_SLOT] == 18817) // Arcane Stream necklace.
				{
						bonusDamageMultiplier += 0.05;
				}
				return bonusDamageMultiplier;
		}

		/**
		* True, if the random calculated difference between the player's magic attack and opponenet's magic defence is 0 or less.
		* <p>
		* Used to calculate weather a magic casted spell will be a splash.
		*
		* @param player The player that is attacking.
		* @param theTarget The player being attacked.
		* @return the splash result.
		*/
		public static boolean isSplash(Player player, Player target)
		{
				int Difference = Misc.random(getMagicAttackAdvantage(player)) - Misc.random(getMagicDefenceAdvantage(target));
				if (Difference <= 0)
				{
						return true;
				}
				return false;
		}

		/**
		 * Calculate the Magic attack advantage.
		 * @param player The player.
		 * @return Magic attack advantage.
		 */
		public static int getMagicAttackAdvantage(Player player)
		{
				int baseAttack = 30;
				int magicAttack = baseAttack;
				double equipmentBonusMultiplier = 1; // Was 9.0 when players complained about splashing is a bit more often.
				double equipmentBonus = (player.playerBonus[ServerConstants.MAGIC_ATTACK_BONUS] * equipmentBonusMultiplier);
				double magicMultiplier = 1.0;
				double levelMultiplier = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) / 100.0;
				magicAttack += equipmentBonus;
				if (MagicData.wearingFullVoidMagic(player))
				{
						magicMultiplier += 0.3;
				}
				magicMultiplier += getMagicPrayerBoost(player);
				magicAttack *= magicMultiplier;
				magicAttack *= levelMultiplier;
				double finalMultiplier = 1.11;
				magicAttack *= finalMultiplier;
				if (player.playerBonus[ServerConstants.MAGIC_ATTACK_BONUS] < 0)
				{
						magicAttack = 0;
				}
				magicAttack *= 6.4;
				return magicAttack;
		}

		//When it is 60 magic attack vs 42 magic defence, it should splash way more.

		public static int getMagicDefenceAdvantage(Player player)
		{
				double equipmentBonusMultiplier = 1;
				int magicDefence = (int) (player.playerBonus[ServerConstants.MAGIC_DEFENCE_BONUS] * equipmentBonusMultiplier);
				double magicMultiplier = 1.0 + getMagicPrayerBoost(player);
				int baseDefence = 22; // So splashing is more often when mage boxing.
				magicDefence += baseDefence;
				int levelsBelow = player.getBaseMagicLevel() - player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
				if (levelsBelow > 0)
				{
						levelsBelow *= 0.6;
						double value = 0.0;
						value = levelsBelow / 100.0;
						value = 1.0 - value;
						magicDefence *= value;
				}
				magicDefence *= magicMultiplier;
				double finalMultiplier = 1.0;
				magicDefence *= finalMultiplier;
				magicDefence *= 6.4;
				return magicDefence;
		}

		private static double getMagicPrayerBoost(Player player)
		{
				if (player.prayerActive[ServerConstants.MYSTIC_WILL])
				{
						return 0.05;
				}
				else if (player.prayerActive[ServerConstants.MYSTIC_LORE])
				{
						return 0.10;
				}
				else if (player.prayerActive[ServerConstants.MYSTIC_MIGHT])
				{
						return 0.15;
				}
				else if (player.prayerActive[ServerConstants.AUGURY])
				{
						return 0.25;
				}
				return 0;
		}

}