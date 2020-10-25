package game.content.combat.vsplayer;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Special combat effects that affect all combat types such as Ring of recoil.
 * @author MGT Madness, created on 31-01-2015.
 */
public class Effects
{

		/**
		 * Phoenix necklace effect.
		 * @param player
		 * 			The associated player.
		 */
		public static void phoenixNecklace(Player player, int damage)
		{
				if (damage == 0)
				{
						return;
				}
				int PHOENIX_NECKLACE = 11090;
				if (!ItemAssistant.hasItemEquipped(player, PHOENIX_NECKLACE))
				{
						return;
				}
				// If hp les than 20$
				if (((double) player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / (double) player.getBaseHitPointsLevel()) <= 0.20)
				{
						// Add 30% of base hp to my hitpoints.
						player.addToHitPoints((int) (player.getBaseHitPointsLevel() / 10.0) * 3);
						ItemAssistant.deleteEquipment(player, PHOENIX_NECKLACE, ServerConstants.AMULET_SLOT);
						player.playerAssistant.sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
				}
		}

		/**
		 * Ring of recoil effect.
		 * @param attacker
		 * 			The player who caused the damage.
		 * @param victim
		 * 			The player being damaged.
		 * @param damage
		 * 			The damage dealt by the attacker.
		 */
		public static void recoilEffect(Player attacker, Player victim, int damage)
		{
				//Attacker = the one attacking
				// Victim the one being damaged
				// When it comes to recoil, the attacker gets damaged.
				if (damage > 0 && victim.getRecoilCharges() > 0 && (ItemAssistant.hasItemEquipped(victim, 2550) || ItemAssistant.hasItemEquipped(victim, 18814)))
				{
						if (victim.getDead() && victim.getHeight() == 20)
						{
								return;
						}
						int recoilDamage = damage / 10;
						if (recoilDamage < 1)
						{
								recoilDamage = 1;
						}
						victim.setRecoilCharges(victim.getRecoilCharges() - recoilDamage);
						if (victim.getRecoilCharges() < 0)
						{
								recoilDamage += victim.getRecoilCharges();
						}
						if (recoilDamage > 0)
						{
								if (recoilDamage > victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS))
								{
										recoilDamage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}
								attacker.damageTaken[victim.getPlayerId()] += damage;
								attacker.totalDamage[4] += recoilDamage;
								attacker.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
								createHitsplatOnPlayerRecoil(victim, attacker, recoilDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						}
						if (victim.getRecoilCharges() <= 0)
						{
								victim.setRecoilCharges(40);
								ItemAssistant.deleteEquipment(victim, 2550, ServerConstants.RING_SLOT);
								victim.playerAssistant.sendMessage("Your ring of recoil turns to dust.");
						}
				}
		}

		/**
		 * Apply the retribution or wrath effect.
		 */
		public static void appendRetributionOrWrath(Player victim, Player attacker)
		{
				if (!victim.getDead())
				{
						return;
				}
				if (victim.redemptionOrWrathActivated)
				{
						return;
				}
				if (!victim.prayerActive[ServerConstants.RETRIBUTION])
				{
						return;
				}
				victim.redemptionOrWrathActivated = true;
				int revengeDamage = (int) (victim.baseSkillLevel[ServerConstants.PRAYER] * 0.25);
				revengeDamage = Misc.random2(revengeDamage);
				createHitsplatOnPlayerRecoil(victim, attacker, revengeDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				victim.gfx0(437);
		}

		public static int victimWearingSpiritShield(Player victim, int damage)
		{
				// Elysian spirit shield.
				if (victim.playerEquipment[ServerConstants.SHIELD_SLOT] == 13742 && Misc.hasPercentageChance(70))
				{
						return damage *= 0.75;
				}

				// Divine spirit shield.
				if (victim.playerEquipment[ServerConstants.SHIELD_SLOT] == 13740 && victim.getCurrentCombatSkillLevel(ServerConstants.PRAYER) >= 2 && !victim.getTank() && Misc.hasPercentageChance(75))
				{
						int damageAbsorbed = (int) (damage * 0.20);
						// For every 14 damage before damage reduction, the player loses 1 prayer point.
						int prayerDrainAmount = (int) (damageAbsorbed / 2);
						victim.currentCombatSkillLevel[ServerConstants.PRAYER] -= prayerDrainAmount;
						Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.PRAYER);
						return damage -= damageAbsorbed;
				}
				return damage;
		}

		/**
		* Same method as Combat.createHitsplatOnPlayer, except, this one does not have recoil feature.
		*/
		public static void createHitsplatOnPlayerRecoil(Player attacker, Player victim, int damage, int hitSplatColour, int icon)
		{
				boolean maxHit = false;
				if (damage == 0 && victim.getDead())
				{
						return;
				}
				if (damage > victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)) // Leave for Granite maul overkill.
				{
						damage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
						if (damage == 0)
						{
								return;
						}
				}
				victim.damageTaken[attacker.getPlayerId()] += damage;
				if (victim.dragonSpearEvent)
				{
						victim.dragonSpearEffectStack.add("Damage:" + damage + " " + hitSplatColour + " " + icon + " " + maxHit);
						return;
				}
				victim.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
				victim.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
				victim.dealDamage(damage);
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.HITPOINTS);
		}

}
