package game.content.combat.vsplayer.magic;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.Poison;
import game.content.skilling.Skilling;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Apply the Magic hitsplat and other effects after it.
 * 
 * @author MGT Madness, created on 21-11-2013.
 */
public class MagicApplyDamage
{

		/**
		 * Apply the magic hitsplat.
		 * 
		 * @param attacker
		 *        The player dealing the hitsplat.
		 * @param victim
		 *        The player receiving the hitsplat.
		 */
		public static void applyMagicHitsplatOnPlayer(final Player attacker, Player victim)
		{

				int damage = attacker.getMagicDamage();

				if (attacker.isMagicSplash())
				{
						damage = 0;
				}
				if (victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
				{
						damage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}
				victim.setUnderAttackBy(attacker.getPlayerId());
				victim.setLastAttackedBy(attacker.getPlayerId());
				if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][6] != 0)
				{
						if (!attacker.isMagicSplash())
						{
								Combat.createHitsplatOnPlayer(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON);
						}
				}
				appendMultiSpellDamage(attacker);
				Effects(attacker, victim, damage);
				victim.setUpdateRequired(true);
				attacker.setUsingMagic(false);
				attacker.setOldSpellId(0);
				if (attacker.getSpellId() == -1 && attacker.getPlayerIdAttacking() == 0)
				{
						attacker.resetFaceUpdate();
						if (attacker.hasLastCastedMagic())
						{
								attacker.resetPlayerIdToFollow();
						}
				}
		}

		public static void appendMultiSpellDamage(Player attacker)
		{
				if (attacker.magicMultiSpell.isEmpty())
				{
						return;
				}
				for (int index = 0; index < attacker.magicMultiSpell.size(); index++)
				{
						String[] parse = attacker.magicMultiSpell.get(index).split(" ");
						int playerId = Integer.parseInt(parse[0]);
						boolean splashed = parse[1].equals("TRUE") ? true : false;
						int damage = Integer.parseInt(parse[2]);
						Player victim = PlayerHandler.players[playerId];
						if (victim == null)
						{
								continue;
						}
						if (victim.getDead())
						{
								continue;
						}
						if (splashed)
						{
								victim.gfx100(85);
								continue;
						}
						int spellGFX = 0;

						if (attacker.getSpellId() >= 0)
						{
								spellGFX = CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][5];
						}
						if (spellGFX == 369 && victim.isOrb())
						{ // ORB
								spellGFX = 1677;
						}
						victim.setOrb(true);
						if (Combat.getEndGfxHeight(attacker) == 100)
						{ // end GFX
								victim.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
						}
						else
						{
								if (spellGFX == 1677)
								{
										victim.gfx0(spellGFX);
								}
								else
								{
										victim.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
								}
						}
						Combat.createHitsplatOnPlayer(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON);
				}

		}

		/**
		 * Effects that are applied after the Magic damage has appeared.
		 *
		 * @param attacker
		 *        The player doing the attack.
		 * @param victim
		 *        The player being attacked.
		 * @param damage
		 *        The damaged dealt by the attacker.
		 */
		public static void Effects(Player attacker, Player victim, int damage)
		{
				// Toxic staff of the dead.
				if (attacker.getWieldedWeapon() == 18783 && Misc.hasPercentageChance(25))
				{
						Poison.appendPoison(attacker, victim, false, 10);
				}
				Combat.applySmite(attacker, victim, damage);

				if (victim.getVengeance())
				{
						Combat.appendVengeance(attacker, victim, damage);
				}

				int endGFX = CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5];

				if (endGFX == 369 && victim.isOrb())
				{
						endGFX = 1677;
				}
				victim.setOrb(true);
				if (Combat.getEndGfxHeight(attacker) == 100 && !attacker.isMagicSplash())
				{
						victim.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
				}
				else if (!attacker.isMagicSplash())
				{
						if (endGFX == 1677)
						{
								victim.gfx(1677, 50);
						}
						else
						{
								victim.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
						}
				}
				else if (attacker.isMagicSplash())
				{
						victim.gfx(85, 120);
				}
				if (!attacker.isMagicSplash())
				{
						if (System.currentTimeMillis() - victim.reduceStat > 35000)
						{
								victim.reduceStat = System.currentTimeMillis();
								switch (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0])
								{
										case 12987:
										case 13011:
										case 12999:
										case 13023:
												victim.currentCombatSkillLevel[0] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 10) / 100);
												break;
								}
						}
						switch (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0])
						{

								case 1153:
										victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 5) / 100);
										victim.playerAssistant.sendMessage("Your attack level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
										break;
								case 1157:
										victim.currentCombatSkillLevel[ServerConstants.STRENGTH] -= ((victim.baseSkillLevel[ServerConstants.STRENGTH] * 5) / 100);
										victim.playerAssistant.sendMessage("Your strength level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.STRENGTH);
										break;
								case 1161:
										victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 5) / 100);
										victim.playerAssistant.sendMessage("Your defence level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
										break;
								case 1542:
										victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 10) / 100);
										victim.playerAssistant.sendMessage("Your defence level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
										break;
								case 1543:
										victim.currentCombatSkillLevel[ServerConstants.STRENGTH] -= ((victim.baseSkillLevel[ServerConstants.STRENGTH] * 10) / 100);
										victim.playerAssistant.sendMessage("Your strength level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.STRENGTH);
										break;
								case 1562:
										victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 10) / 100);
										victim.playerAssistant.sendMessage("Your attack level has been reduced!");
										victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
										Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.ATTACK);
										break;
						}
				}

		}

}