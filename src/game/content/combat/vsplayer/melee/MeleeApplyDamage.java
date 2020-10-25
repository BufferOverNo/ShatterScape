package game.content.combat.vsplayer.melee;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.consumable.RegenerateSkill;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.skilling.Skilling;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Create the melee hitsplat and other effects on the player.
 * @author MGT Madness, created on 20-11-2013.
 */
public class MeleeApplyDamage
{

		/**
		 * Apply the melee damage on target.
		 * @param attacker
		 * 			The player applying the damage.
		 * @param victim
		 * 			The player being damaged.
		 * @param firstHitsplat
		 * 			True, to apply the damage of the first hitsplat. False to apply the second hitsplat(Like DDS second hitsplat).
		 */
		public static void applyMeleeHitSplat(final Player attacker, Player victim, boolean firstHitsplat)
		{
				int damage = 0;
				if (firstHitsplat)
				{
						damage = attacker.meleeFirstDamage;
				}
				else
				{
						damage = attacker.meleeSecondDamage;
				}
				/* If the damage is more than the target's hitpoints, then adjust. */
				if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
				{
						damage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}
				victim.setUnderAttackBy(attacker.getPlayerId());
				victim.setLastAttackedBy(attacker.getPlayerId());
				Combat.applySmite(attacker, victim, damage);
				if (firstHitsplat)
				{
						SpecialAttackTracker.saveMaximumDamage(attacker, damage, "FIRST", false);
				}
				else
				{
						SpecialAttackTracker.saveMaximumDamage(attacker, damage, "SECOND", false);
				}
				if (attacker.getDragonClawsSpecialAttack() && firstHitsplat)
				{
						SpecialAttackTracker.storeDragonClawsDamage(attacker, damage, -1, -1, -1);
				}
				else if (attacker.getDragonClawsSpecialAttack() && !firstHitsplat)
				{
						SpecialAttackTracker.storeDragonClawsDamage(attacker, -1, damage, -1, -1);
				}
				applyDragonClawsDamage(attacker, victim);

				Combat.createHitsplatOnPlayer(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON);
				if (firstHitsplat)
				{
						Effects(attacker, victim, damage);
				}
				attacker.setSpecEffect(0);
		}

		/**
		 * Melee effects of the damage. This is called after the Melee hitsplat appears.
		 */
		public static void Effects(final Player attacker, final Player victim, int damage)
		{
				// Dragon dagger p++ and Abyssal dagger p++.
				if ((attacker.getWieldedWeapon() == 5698 || attacker.getWieldedWeapon() == 18785) && Misc.hasPercentageChance(30))
				{
						Poison.appendPoison(attacker, victim, false, 6);
				}

				// Abyssal tentacle.
				if (attacker.getWieldedWeapon() == 18767 && Misc.hasPercentageChance(25))
				{
						Poison.appendPoison(attacker, victim, false, 4);
				}
				if (damage > 0)
				{
						if (attacker.wearingFullGuthan && Misc.random(3) == 1)
						{
								attacker.addToHitPoints(damage);
								victim.gfx0(398);
						}
						if (victim.getVengeance())
						{
								Combat.appendVengeance(attacker, victim, damage);
						}
				}
				final int DRAGON_SCIMITAR = 1;
				final int ZAMORAK_GODSWORD = 2;
				final int BANDOS_GODSWORD = 3;
				final int SARADOMIN_GODSWORD = 4;
				switch (attacker.getSpecEffect())
				{

						// Barrelchest anchor
						case 5:
								int reduction = damage / 10;
								if (reduction > 0)
								{
										int index = ServerConstants.ATTACK;
										if (Misc.hasPercentageChance(30))
										{
												index = ServerConstants.DEFENCE;
										}
										else if (Misc.hasPercentageChance(30))
										{
												index = ServerConstants.RANGED;
										}
										else if (Misc.hasPercentageChance(30))
										{
												index = ServerConstants.MAGIC;
										}
										int amount = victim.getCurrentCombatSkillLevel(index) - reduction;
										if (amount < 1)
										{
												amount = 1;
										}
										victim.currentCombatSkillLevel[index] = amount;
										Skilling.updateSkillTabFrontTextMain(victim, index);
										RegenerateSkill.storeBoostedTime(victim, index);
								}
								break;

						// Dragon warhammer
						case 6:
								if (damage > 0)
								{
										int defence = (int) (victim.currentCombatSkillLevel[ServerConstants.DEFENCE] * 0.7);
										if (defence < 1)
										{
												defence = 1;
										}
										victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = defence;
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
										RegenerateSkill.storeBoostedTime(victim, ServerConstants.DEFENCE);
								}
								break;

						case DRAGON_SCIMITAR:
								if (damage > 0)
								{
										if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC] || victim.prayerActive[ServerConstants.PROTECT_FROM_RANGED] || victim.prayerActive[ServerConstants.PROTECT_FROM_MELEE])
										{
												victim.headIcon = -1;
												victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[16], 0);
												victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[17], 0);
												victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[18], 0);
										}
										victim.playerAssistant.sendMessage("You have been injured!");
										victim.stopPrayerDelay = System.currentTimeMillis();
										victim.setPrayerActive(16, false);
										victim.setPrayerActive(17, false);
										victim.setPrayerActive(18, false);
										victim.getPA().requestUpdates();
								}
								break;

						case ZAMORAK_GODSWORD:
								if (damage > 0 && !victim.isCombatBot() && victim.canBeFrozen())
								{
										victim.setFrozenLength(20000);
										victim.frozenBy = attacker.getPlayerId();
										victim.gfx0(369);
										victim.playerAssistant.sendMessage("You have been frozen.");
										attacker.playerAssistant.sendMessage("You have frozen your target.");
										if (!victim.isBot)
										{
												Movement.stopMovement(victim);
										}
										Combat.resetPlayerAttack(victim);
								}
								break;

						case BANDOS_GODSWORD:
								if (damage > 0)
								{
										int defence = victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= damage;
										victim.playerAssistant.sendMessage("You feel weak.");
										if (defence < 1)
										{
												defence = 1;
										}
										victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = defence;
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
										RegenerateSkill.storeBoostedTime(victim, ServerConstants.DEFENCE);
								}
								break;

						case SARADOMIN_GODSWORD:
								if (damage > 20)
								{
										attacker.addToHitPoints(damage / 2);
										attacker.currentCombatSkillLevel[ServerConstants.PRAYER] += damage / 4;
										Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.PRAYER);
								}
								break;
				}
		}

		/**
		 * Apply the third and fourth hitsplat of the Dragon claws special attack.
		 * @param attacker
		 * 			The player attacking.
		 * @param victim
		 * 			The player under attack.
		 */
		public static void applyDragonClawsDamage(final Player attacker, final Player victim)
		{
				if (!attacker.getDragonClawsSpecialAttack())
				{
						return;
				}
				if (attacker.getUsingDragonClawsSpecialAttackEvent())
				{
						return;
				}
				attacker.setUsingDragonClawsSpecialAttackEvent(true);
				CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - attacker.meleeThirdDamage < 0)
								{
										attacker.meleeThirdDamage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}

								Combat.createHitsplatOnPlayer(attacker, victim, attacker.meleeThirdDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON);

								if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - attacker.meleeFourthDamage < 0)
								{
										attacker.meleeFourthDamage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}
								Combat.createHitsplatOnPlayer(attacker, victim, attacker.meleeFourthDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON);

								SpecialAttackTracker.storeDragonClawsDamage(attacker, -1, -1, attacker.meleeThirdDamage, attacker.meleeFourthDamage);
								SpecialAttackTracker.saveDragonClawsMaximumDamage(attacker, false);
								attacker.setUsingDragonClawsSpecialAttackEvent(false);
						}
				}, 1);
		}
}