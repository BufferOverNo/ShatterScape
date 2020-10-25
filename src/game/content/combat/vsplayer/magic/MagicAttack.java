package game.content.combat.vsplayer.magic;

import core.ServerConstants;
import game.content.achievement.AchievementStatistics;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.Effects;
import game.item.ItemAssistant;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Handle the magic attack.
 * 
 * @author MGT Madness, created on 28-03-2015.
 */
public class MagicAttack
{

		/**
		 * Initiate the magic attack.
		 * 
		 * @param attacker
		 *        The player initiating the attack.
		 * @param victim
		 *        The player receiving the attack.
		 */
		public static void landMagicAttack(Player attacker, Player victim)
		{
				if (!attacker.hasLastCastedMagic())
				{
						return;
				}
				if (attacker.getSpellId() == -1)
				{
						return;
				}
				if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12891)
				{
						attacker.timeUsedBarrage = System.currentTimeMillis();
				}
				AchievementStatistics.startNewFight(attacker, victim, "MAGIC");
				attacker.setLastUsedMagic(System.currentTimeMillis());
				attacker.setUsingMagic(true);
				attacker.stakeAttacks++;
				MagicData.requiredRunes(attacker, attacker.getSpellId(), "DELETE RUNES");
				attacker.startAnimation(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][2]);
				Combat.addBarragesCasted(attacker);
				attacker.setLastCastedMagic(true);
				attacker.setMagicSplash(MagicFormula.isSplash(attacker, victim));
				if (attacker.isMagicSplash())
				{
						victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MAGIC;
				}
				MagicFormula.calculateMagicDamage(attacker, victim);
				//MagicAttack.createMagicProjectile(attacker, victim);
				victim.setUnderAttackBy(attacker.getPlayerId());
				attacker.setPlayerIdAttacking(victim.getPlayerId());
				castMultiCombatSpell(attacker, victim);

				switch (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0])
				{

						// Teleblock spell.
						case 12445:
								if (!attacker.isMagicSplash())
								{
										if ((System.currentTimeMillis() > victim.teleBlockEndTime + 30000))
										{
												if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
												{
														victim.teleBlockEndTime = System.currentTimeMillis() + 150000;
														victim.getPA().sendMessage(ServerConstants.PURPLE_COL + "A teleblock spell has been cast on you. It will expire in 2 minutes and a half.");
														attacker.setMagicDamage(2);
												}
												else
												{
														victim.teleBlockEndTime = System.currentTimeMillis() + 300000;
														victim.getPA().sendMessage(ServerConstants.PURPLE_COL + "A teleblock spell has been cast on you. It will expire in 5 minutes.");
														attacker.setMagicDamage(5);
												}
										}
								}
								else
								{
										attacker.setMagicDamage(0);
								}
								break;
						//Blood spells.
						case 12901:
						case 12919:
						case 12911:
						case 12929:
								int heal = attacker.getMagicDamage() / 4;
								attacker.addToHitPoints(heal);
								break;
				}
				Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, attacker.getMagicDamage());

				int pX = attacker.getX();
				int pY = attacker.getY();
				int nX = victim.getX();
				int nY = victim.getY();
				int offX = (pY - nY) * -1;
				int offY = (pX - nX) * -1;
				attacker.setProjectileStage(2);
				if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3] > 0)
				{
						if (Combat.getStartGfxHeight(attacker) == 100)
						{
								attacker.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
						}
						else
						{
								attacker.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
						}
				}
				if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4] > 0)
				{
						attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78, CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4], Combat.getStartHeight(attacker), Combat.getEndHeight(attacker), -victim.getPlayerId() - 1, Combat.getStartDelay(attacker), Combat.getProjectileSlope(attacker));
				}
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setOldPlayerIndex(victim.getPlayerId());
				attacker.setOldSpellId(attacker.getSpellId());
				if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0] == 12891 && victim.isMoving())
				{
						attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, -victim.getPlayerId() - 1, Combat.getStartDelay(attacker), Combat.getProjectileSlope(attacker));
				}
				long freezeDelay = Combat.getFreezeTime(attacker); // freeze time
				if (freezeDelay > 0 && victim.canBeFrozen() && !attacker.isMagicSplash())
				{
						if (!victim.isBot || victim.getPlayerName().equals("Remy E"))
						{
								Movement.stopMovement(victim);
								victim.setFrozenLength(freezeDelay);
								victim.frozenBy = attacker.getPlayerId();
								victim.playerAssistant.sendMessage("You have been frozen!");
						}
						victim.setOrb(victim.isBot ? true : false);
				}
				if (attacker.lastUsedManualSpell) // Spell id is > 0 when
				{
						attacker.resetPlayerIdAttacking();
				}
				attacker.setSpellId(-1);
		}

		private static void castMultiCombatSpell(Player attacker, Player victim)
		{
				if (attacker.isMagicSplash())
				{
						return;
				}
				if (!Combat.usingMultiSpell(attacker))
				{
						return;
				}
				if (!Area.inMulti(attacker.getX(), attacker.getY()))
				{
						return;
				}

				// Used to cap the amount of players tagged to 9.
				int multiHits = 0;
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (multiHits == 9)
						{
								break;
						}
						Player loop = PlayerHandler.players[j];
						if (loop == null)
						{
								continue;
						}
						if (victim.getPlayerId() == loop.getPlayerId())
						{
								continue;
						}
						if (loop.getPlayerId() == attacker.getPlayerId())
						{
								continue;
						}
						if (!Area.inMulti(loop.getX(), loop.getY()))
						{
								continue;
						}
						int combatDif1 = Combat.getCombatDifference(attacker.getCombatLevel(), loop.getCombatLevel());
						if (combatDif1 > attacker.wildernessLevel || combatDif1 > loop.wildernessLevel)
						{
								continue;
						}
						// if loop is not closed to the player i am initially attacking, which is the victim.
						if (!loop.playerAssistant.withInDistance(loop.getX(), loop.getY(), victim.getX(), victim.getY(), 1))
						{
								continue;
						}
						multiHits++;
						if (MagicFormula.isSplash(attacker, loop))
						{
								attacker.magicMultiSpell.add(loop.getPlayerId() + " TRUE 0");
								continue;
						}
						int damage = Misc.random(1, MagicFormula.getMagicMaximumDamage(attacker)); // The damage to the target.
						damage = Effects.victimWearingSpiritShield(loop, damage);
						if (loop.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
						{
								damage *= 0.6;
						}
						attacker.magicMultiSpell.add(loop.getPlayerId() + " false " + damage);

						Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damage);
						Combat.multiSpellEffect(attacker, loop, damage);
				}
		}


		/**
		* Handle the content of magic spell attack on player packet.
		* 
		* @param attacker
		*        The player using the spell.
		* @param victimID
		*        The player receiving the spell.
		* @param castingSpellId
		*        The spell used by the attacker.
		*/
		public static void magicOnPlayerPacket(Player attacker, int victimID, int castingSpellId)
		{
				attacker.setUsingRanged(false);
				attacker.setMeleeFollow(false);
				attacker.playerAssistant.stopAllActions();
				Player victim = PlayerHandler.players[victimID];

				if (victim == null)
				{
						return;
				}

				MagicData.setCombatSpell(attacker, castingSpellId);
				if (Combat.spellbookPacketAbuse(attacker, attacker.getSpellId()))
				{
						Combat.resetPlayerAttack(attacker);
						return;
				}
				attacker.faceUpdate(victim.getPlayerId() + 32768);
				attacker.setPlayerIdAttacking(victim.getPlayerId());
				attacker.setLastCastedMagic(true);
				Combat.stopMovement(attacker, victim, false);

				// Has to be kept here so the player doesn't run 2 extra tiles before realising he cannot even attack.
				if (!AttackPlayer.hasSubAttackRequirements(attacker, victim))
				{
						attacker.turnPlayerTo(victim.getX(), victim.getY());
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
				}
		}

		/**
		 * Requirements to attack with magic.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player receiving the attack.
		 * @return True, if the player has the magic requirements.
		 */
		public static boolean hasMagicRequirements(Player attacker, Player victim)
		{
				int castedSpell = attacker.getSpellId();
				if (castedSpell == -1)
				{
						return false;
				}
				if (attacker.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < CombatConstants.MAGIC_SPELLS[castedSpell][1])
				{
						attacker.playerAssistant.sendMessage("You need to have a magic level of " + CombatConstants.MAGIC_SPELLS[castedSpell][1] + " to cast this spell.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}

				if (attacker.getSpellId() == 31)
				{
						if (EdgeAndWestsRule.isEdgeOrWestRule(attacker, victim, "TELEBLOCK"))
						{
								Combat.resetPlayerAttack(attacker);
								return false;
						}
				}


				if (!MagicData.requiredRunes(attacker, castedSpell, "CHECK REQUIREMENT"))
				{
						attacker.playerAssistant.sendMessage("You don't have the required runes to cast this spell.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}

				int staffRequired = MagicData.getStaffNeeded(attacker, castedSpell);
				if (staffRequired > 0)
				{
						if (attacker.getWieldedWeapon() != staffRequired)
						{
								attacker.playerAssistant.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
								Combat.resetPlayerAttack(attacker);
								Movement.stopMovement(attacker);
								return false;
						}
				}

				if (attacker.getDuelStatus() == ServerConstants.DUELING)
				{
						if (attacker.duelRule[4])
						{
								attacker.playerAssistant.sendMessage("Magic has been disabled in this duel!");
								Combat.resetPlayerAttack(attacker);
								Movement.stopMovement(attacker);
								return false;
						}
				}

				for (int value = 0; value < CombatConstants.REDUCE_SPELLS.length; value++)
				{
						if (CombatConstants.REDUCE_SPELLS[value] == CombatConstants.MAGIC_SPELLS[castedSpell][0])
						{
								if ((System.currentTimeMillis() - victim.reduceSpellDelay[value]) < CombatConstants.REDUCE_SPELL_TIME[value])
								{
										attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
										Combat.resetPlayerAttack(attacker);
										Movement.stopMovement(attacker);
										return false;
								}
								break;
						}
				}

				if (System.currentTimeMillis() < victim.teleBlockEndTime && CombatConstants.MAGIC_SPELLS[castedSpell][0] == CombatConstants.TELE_BLOCK)
				{
						attacker.playerAssistant.sendMessage("That player is already affected by this spell.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}

				if (System.currentTimeMillis() < (victim.teleBlockEndTime + 30000) && CombatConstants.MAGIC_SPELLS[castedSpell][0] == CombatConstants.TELE_BLOCK)
				{
						attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}

				for (int r = 0; r < CombatConstants.REDUCE_SPELLS.length; r++)
				{
						if (CombatConstants.REDUCE_SPELLS[r] == CombatConstants.MAGIC_SPELLS[castedSpell][0])
						{
								attacker.reduceSpellId = r;
								if ((System.currentTimeMillis() - victim.reduceSpellDelay[attacker.reduceSpellId]) > CombatConstants.REDUCE_SPELL_TIME[attacker.reduceSpellId])
								{
										victim.canUseReducingSpell[attacker.reduceSpellId] = true;
								}
								else
								{
										victim.canUseReducingSpell[attacker.reduceSpellId] = false;
								}
								break;
						}
				}
				if (!victim.canUseReducingSpell[attacker.reduceSpellId])
				{
						attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}
				if (castedSpell == 25)
				{
						for (int npc : ServerConstants.UNDEAD_NPCS)
						{
								if (NpcHandler.npcs[attacker.getNpcIdAttacking()].npcType != npc)
								{
										attacker.playerAssistant.sendMessage("You can only attack undead monsters with this spell.");
										Combat.resetPlayerAttack(attacker);
										Movement.stopMovement(attacker);
										return false;
								}
						}
				}
				if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), attacker.getHeight(), 1, 1, true))
				{
						if (attacker.isFrozen())
						{
								Combat.resetPlayerAttack(attacker);
						}
						return false;
				}
				return true;
		}

		/**
		 * Re-engage the player with magic or start autocasting.
		 * 
		 * @param attacker
		 *        The player attacking.
		 */
		public static void reEngageWithMagic(Player attacker)
		{

				/*
				 * This will activate when the player uses the magic packet before the hitsplat lands. Because when the hitsplat lands, it will setUsingMagic(false);
				 */
				if (attacker.getSpellId() > 0 && !attacker.getAutoCasting())
				{
						attacker.setLastCastedMagic(true);
						attacker.lastUsedManualSpell = true;
				}

				// If player is using melee/ranged packet and is autocasting.
				if (attacker.getAutoCasting() && attacker.getSpellId() == -1) // Spell id is 0 when autocasting and changes when the player has manually casted a spell.
				{
						if (Combat.spellbookPacketAbuse(attacker, attacker.getAutocastId()))
						{
								Combat.resetPlayerAttack(attacker);
								return;
						}
						attacker.lastUsedManualSpell = false;
						attacker.setSpellId(attacker.getAutocastId());
						attacker.setLastCastedMagic(true);
				}
		}

		/**
		 * Send the barrage/blitz projectile.
		 * 
		 * @param attacker
		 *        The player sending the projectile.
		 * @param victim
		 *        The player receiving the projectile.
		 */
		public static void createMagicProjectile(Player attacker, Player victim)
		{
				if (attacker.getSpellId() != 47 && attacker.getSpellId() != 43)
				{
						return;
				}
				int pX = attacker.getX();
				int pY = attacker.getY();
				int oX = victim.getX();
				int oY = victim.getY();
				int offX = (pY - oY) * -1;
				int offY = (pX - oX) * -1;
				int differenceY = attacker.getY() - victim.getY();
				int differenceX = attacker.getX() - victim.getX();
				pY -= differenceY / 1.5;
				pX -= differenceX / 1.5;
				int distance = 0;
				if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 4))
				{
						distance++;
				}
				if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 6))
				{
						distance++;
				}
				attacker.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 90 + (distance * 25), MagicAttack.PROJECTILE_ID, 60, 40, -victim.getPlayerId() - 1, 53, 0);
		}

		final static int PROJECTILE_ID = 368;

}