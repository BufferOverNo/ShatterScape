package game.content.combat.vsplayer;

import core.ServerConstants;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.CombatInterface;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.Poison;
import game.content.combat.SpecialAttack;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicAttack;
import game.content.combat.vsplayer.melee.MeleeAttack;
import game.content.combat.vsplayer.range.RangedAttack;
import game.content.combat.vsplayer.range.RangedData;
import game.content.interfaces.AreaInterface;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.SpecialAttackTracker;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Attack player.
 * 
 * @author MGT Madness, created on 27-03-2015.
 */
public class AttackPlayer
{
		/**
		 * Handle the player attack.
		 * 
		 * @param attacker
		 *        The player initiating the attack.
		 * @param victim
		 *        The player receiving the attack.
		 */
		public static void handlePlayerAttack(final Player attacker, final Player victim)
		{
				if (victim == null)
				{
						return;
				}

				if (attacker.doingAnAction())
				{
						return;
				}
				long time = System.currentTimeMillis();

				attacker.faceUpdate(victim.getPlayerId() + 32768);

				// Snowball.
				if (ItemAssistant.hasItemEquippedSlot(attacker, 10501, ServerConstants.WEAPON_SLOT))
				{
						if (attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 8))
						{
								if (time - attacker.getLastSnowBallThrowTime() >= 2400)
								{
										attacker.setLastSnowBallThrowTime(time);
										final int oX = attacker.getX();
										final int oY = attacker.getY();
										final int pX = victim.getX();
										final int pY = victim.getY();
										final int offX = (oY - pY) * -1;
										final int offY = (oX - pX) * -1;
										attacker.turnPlayerTo(victim.getX(), victim.getY());
										attacker.startAnimation(7530);
										attacker.getPA().createPlayersProjectile(oX, oY, offX, offY, 50, attacker.isAdministratorRank() ? 300 : 130, 1281, 21, 21, attacker.isAdministratorRank() ? -victim.getPlayerId() - 1 : 0, 65, Combat.getProjectileSlope(attacker));
								}
								Movement.stopMovement(attacker);
								Combat.resetPlayerAttack(attacker);
								return;
						}
				}

				resetAttackData(attacker);
				MagicAttack.reEngageWithMagic(attacker);
				RangedAttack.reEngageWithRanged(attacker);
				// If using trident of the swamp and autocasting the trident of the swamp spell.
				if (attacker.getWieldedWeapon() == 18769 && attacker.getAutocastId() == 52 && attacker.getAutoCasting() && attacker.getSpellId() == -1)
				{
						AutoCast.resetAutocast(attacker);
						attacker.setPlayerIdAttacking(victim.getPlayerId());
						attacker.setPlayerIdToFollow(victim.getPlayerId());
				}

				if (!hasAttackRequirements(attacker, victim))
				{
						if (!attacker.ignorePlayerTurn)
						{
								attacker.turnPlayerTo(victim.getX(), victim.getY());
						}
						return;
				}
				if (Area.inZombieWaitingRoom(victim))
				{

						Combat.resetPlayerAttack(attacker);
						if (victim.isInTrade() || victim.isUsingBankInterface() || victim.getDuelStatus() != 0)
						{
								attacker.turnPlayerTo(victim.getX(), victim.getY());
								attacker.getPA().sendMessage(victim.getPlayerName() + " is busy.");
								return;
						}
						Zombie.requestDuo(attacker, victim);
						return;
				}
				attacker.setAttackTimer(Combat.getAttackTimerCount(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.timeInCombat = time;
				victim.timeInCombat = System.currentTimeMillis();
				attacker.setTimeAttackedAnotherPlayer(time);
				attacker.timeInPlayerCombat = time;
				attacker.killedPlayerImmuneTime = 0;
				TargetSystem.doingWildActivity(attacker);
				victim.setTimeUnderAttackByAnotherPlayer(time);
				victim.timeInPlayerCombat = time;
				if (!victim.isCombatBot() && !attacker.isCombatBot())
				{
						GameTimeSpent.increaseGameTime(attacker, GameTimeSpent.PKING);
						GameTimeSpent.increaseGameTime(victim, GameTimeSpent.PKING);
				}
				if (victim.isCombatBot())
				{
						attacker.lastActivity = "PK BOT";
						attacker.lastActivityTime = time;

						// To count pking time spent.
						if (time - attacker.lastTimeSpentUsed > 5000)
						{
								attacker.lastTimeSpentUsed = time;
								attacker.timeSpent[0]++;
						}
				}
				AreaInterface.updateWalkableInterfaces(attacker);
				AreaInterface.updateWalkableInterfaces(victim);
				Skull.combatSkull(attacker, victim);
				victim.setUnderAttackBy(attacker.getPlayerId());
				victim.setLastAttackedBy(attacker.getPlayerId());
				Combat.resetSpecialAttackData(attacker);
				attacker.againstPlayer = true;
				landNormalAttack(attacker, victim);
				BotContent.damaged(attacker);
				BotContent.goodLuckMessage(attacker, victim);
				attacker.botTimeInCombat = time;
				victim.botTimeInCombat = time;

		}

		/**
		 * True, if the player has all the requirements to land an attack.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @param victim
		 *        The player receiving the attack.
		 * @return True, if the player has all the requirements to land an attack.
		 */
		public static boolean hasAttackRequirements(Player attacker, Player victim)
		{
				if (!hasSubAttackRequirements(attacker, victim))
				{
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						return false;
				}
				if (!attacker.hasLastCastedMagic() && !attacker.getUsingRanged())
				{
						if (attacker.getAutoRetaliate() == 1)
						{
								attacker.setMeleeFollow(true);
						}
						if (!MeleeAttack.hasMeleeRequirements(attacker, victim))
						{
								return false;
						}
				}

				else if (attacker.hasLastCastedMagic())
				{
						if (!MagicAttack.hasMagicRequirements(attacker, victim))
						{
								return false;
						}
						if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.MAGIC_FOLLOW_DISTANCE))
						{
								return false;
						}

				}

				else if (attacker.getUsingRanged())
				{
						if (!RangedAttack.hasRangedRequirements(attacker, victim))
						{
								return false;
						}
				}

				if (attacker.getAttackTimer() > 0)
				{
						return false;
				}

				return true;
		}

		/**
		 * Initiate a normal attack.
		 * 
		 * @param attacker
		 *        The player initiating the normal attack.
		 * @param victim
		 *        The player receiving the normal attack.
		 */
		private static void landNormalAttack(Player attacker, Player victim)
		{
				if (RangedAttack.landRangedAttack(attacker, victim))
				{
						return;
				}
				if (MeleeAttack.normalMeleeAttack(attacker, victim))
				{
						return;
				}
				MagicAttack.landMagicAttack(attacker, victim);
				attackEfect(attacker, victim);
		}

		private static void attackEfect(Player attacker, Player victim)
		{
				if (Combat.hasSerpentineHelm(victim) && Misc.hasOneOutOf(6))
				{
						Poison.appendPoison(victim, attacker, false, 10);
				}

		}

		/**
		* Initiate the special attack.
		* 
		* @param attacker
		*        The player initiating the special attack.
		* @param victim
		*        The player receiving the special attack.
		*/
		public static boolean landSpecialAttack(Player attacker, Player victim)
		{
				if (!attacker.isUsingSpecial())
				{
						return false;
				}

				if (attacker.duelRule[10] && attacker.getDuelStatus() == 5)
				{
						attacker.playerAssistant.sendMessage("Special attacks have been disabled during this duel!");
						attacker.setUsingSpecial(false);
						CombatInterface.updateSpecialBar(attacker);
						Combat.resetPlayerAttack(attacker);
						return true;
				}

				// Cases where i switch to Granite maul and click on special bar too quick that it pressed on my previous weapon special bar (msb) instead of granite maul.
				// So when this happens, just launch the granite maul special straight away.
				if (attacker.getWieldedWeapon() == 4153)
				{
						return true;
				}

				if (Combat.checkSpecAmount(attacker, attacker.getWieldedWeapon()))
				{
						attacker.stakeAttacks++;
						attacker.stakeSpecialAttacks++;
						SpecialAttack.activateSpecial(attacker, attacker.getWieldedWeapon(), victim.getPlayerId());
						if (attacker.getWieldedWeapon() == 1249)
						{
								return true;
						}
						if (attacker.getUsingRanged())
						{
								RangedAttack.rangedSpecialAttack(attacker, victim);
						}
						else
						{
								MeleeAttack.meleeSpecialAttack(attacker, victim);
						}
						return true;
				}
				else
				{
						attacker.playerAssistant.sendMessage("You don't have the required special energy to use this attack.");
						attacker.setUsingSpecial(false);
						attacker.botUsedSpecialAttack = false;
						CombatInterface.updateSpecialBar(attacker);
						Combat.resetPlayerAttack(attacker);
						return true;
				}
		}

		/**
		 * Stop the player from attacking, if this returns true.
		 * 
		 * @param attacker
		 *        The player attacking.
		 * @return True, if the player has the requirements to attack the victim.
		 */
		public static boolean hasSubAttackRequirements(Player attacker, Player victim)
		{
				if (attacker.getDead())
				{
						return false;
				}
				if (attacker.getPlayerIdAttacking() == attacker.getPlayerId())
				{
						return false;
				}
				if (attacker.getTransformed() != 0)
				{
						return false;
				}
				if (attacker.isTeleporting())
				{
						return false;
				}
				if (attacker.dragonSpearEvent)
				{
						return false;
				}
				if (!attacker.isTutorialComplete() || !victim.isTutorialComplete())
				{
						return false;
				}
				if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.OUT_OF_VIEW_DISTANCE))
				{
						attacker.ignorePlayerTurn = true;
						return false;
				}
				if (attacker.getHeight() != victim.getHeight())
				{
						return false;
				}

				if (attacker.playerAssistant.isOnTopOfTarget(victim) && attacker.isFrozen())
				{
						return false;
				}

				if (victim.getDead())
				{
						return false;
				}
				if (victim.isTeleporting())
				{
						return false;
				}
				if (victim.getDoingAgility())
				{
						return false;
				}
				long time = System.currentTimeMillis();
				if (Area.inWildernessAgilityCourse(victim) && time - victim.wildernessAgilityCourseImmunity <= 1800000)
				{
						attacker.playerAssistant.sendMessage("This player is protected by the wizards!");
						return false;
				}
				if (Area.inDuelArena(victim) && attacker.getDuelStatus() != 5 && !attacker.hasLastCastedMagic())
				{
						if (Area.inDuelArenaRing(attacker) || attacker.getDuelStatus() == 5)
						{
								attacker.playerAssistant.sendMessage("You can't challenge inside the arena!");
								return false;
						}
						return true;
				}
				if (attacker.getDuelStatus() == 5 && victim.getDuelStatus() == 5)
				{
						// Aby whip and dds only.
						if (attacker.duelRule[9])
						{
								if (attacker.getWieldedWeapon() != 4151 && attacker.getWieldedWeapon() != 1215 && attacker.getWieldedWeapon() != 5698)
								{
										attacker.playerAssistant.sendMessage("You can only use Abyssal whip and Dragon daggers in this duel!");
										return false;
								}
						}
						if (victim.getDuelingWith() == attacker.getPlayerId() && victim.lastDueledWithName.equals(attacker.getPlayerName()))
						{
								if (attacker.getDuelCount() > 0)
								{
										attacker.playerAssistant.sendMessage("The duel hasn't started yet!");
										return false;
								}

								// Must be added here or there will be no following in duel arena combat.
								attacker.setPlayerIdToFollow(attacker.getPlayerIdAttacking());
								return true;
						}
						else
						{
								attacker.playerAssistant.sendMessage("This isn't your opponent!");
								return false;
						}
				}
				if (Area.inZombieWaitingRoom(victim))
				{
						return true;
				}

				if (!Area.inPVPArea(victim) || !Area.inPVPArea(attacker) || attacker.getHeight() == 20)
				{
						if (attacker.isCombatBot())
						{
								BotContent.cannotAttackPlayer(attacker);
								return false;
						}
						if (attacker.playerIdAttackingMeInSafe == victim.getPlayerId() && victim.playerIdCanAttackInSafe == attacker.getPlayerId() && time - attacker.timeExitedWilderness < 10000
						//
						|| victim.playerIdAttackingMeInSafe == attacker.getPlayerId() && attacker.playerIdCanAttackInSafe == victim.getPlayerId() && time - victim.timeExitedWilderness < 10000)
						{
						}
						else
						{
								if (attacker.tournamentTarget >= 0)
								{
										if (victim.tournamentTarget != attacker.getPlayerId() || attacker.tournamentTarget != victim.getPlayerId())
										{

												attacker.playerAssistant.sendMessage("This is not your challenger!");
												return false;
										}

										if (attacker.getDuelCount() > 0)
										{
												attacker.playerAssistant.sendMessage("The tournament hasn't started yet!");
												return false;
										}
								}
								else
								{
										if (!Area.inPVPArea(victim))
										{
												attacker.playerAssistant.sendMessage("That player is not in the Wilderness.");
										}
										else
										{
												attacker.playerAssistant.sendMessage("You are not in the Wilderness.");
										}
										return false;
								}
						}
				}

				if (Combat.wasAttackedByNpc(victim) && !Area.inMulti(victim.getX(), victim.getY()))
				{
						BotContent.cannotAttackPlayer(attacker);
						attacker.playerAssistant.sendMessage("That player is already in combat.");
						return false;
				}
				if (Combat.wasAttackedByNpc(attacker) && !Area.inMulti(victim.getX(), victim.getY()))
				{
						attacker.playerAssistant.sendMessage("You are already in combat.");
						return false;
				}

				if (!Area.inSafePkFightZone(attacker) && attacker.getHeight() != 20)
				{
						int combatDif1 = Combat.getCombatDifference(attacker.getCombatLevel(), victim.getCombatLevel());
						if (combatDif1 > attacker.wildernessLevel || combatDif1 > victim.wildernessLevel)
						{
								attacker.playerAssistant.sendMessage("Your combat level difference is too great to attack that player here.");
								return false;
						}
				}
				if (!Area.inMulti(victim.getX(), victim.getY()))
				{

						// These two numbers 9000 and 9000 to be the same as each other.
						if (victim.getUnderAttackBy() > 0)
						{
								if (victim.getUnderAttackBy() != attacker.getPlayerId() && Combat.wasUnderAttackByAnotherPlayer(victim, 4000))
								{
										attacker.playerAssistant.sendMessage("That player is already in combat.");
										return false;
								}
						}
						if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(victim))
						{
								Player third = PlayerHandler.players[victim.getPlayerIdAttacking()];
								boolean thirdPersonNotAttackingBackAtAll = false;
								if (third != null)
								{
										if (!third.lastPlayerAttackedName.equals(victim.getPlayerName()))
										{
												thirdPersonNotAttackingBackAtAll = true;
										}
								}
								if (victim.getPlayerIdAttacking() > 0 && victim.getPlayerIdAttacking() != attacker.getPlayerId() && !thirdPersonNotAttackingBackAtAll)
								{
										attacker.getPA().sendMessage(victim.getPlayerName() + " is under Edgeville protection.");
										attacker.getPA().sendMessage("You cannot Pj this fight.");
										return false;
								}
								if (victim.getUnderAttackBy() > 0)
								{
										if (victim.getUnderAttackBy() != attacker.getPlayerId() && Combat.wasUnderAttackByAnotherPlayer(victim, 30000))
										{
												attacker.getPA().sendMessage(victim.getPlayerName() + " is under Edgeville protection.");
												attacker.getPA().sendMessage("You cannot Pj this fight.");
												return false;
										}
								}
								if (time - victim.killedPlayerImmuneTime <= (EdgeAndWestsRule.TIME_IMMUNE_FROM_BEING_ATTACKED * 1000))
								{
										if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim))
										{
												attacker.getPA().sendMessage(victim.getPlayerName() + " is under Edgeville protection.");
												attacker.getPA().sendMessage("This player just dropped another player, give him a chance to teleport.");
												attacker.playerTriedToAttack = victim.getPlayerName();
												attacker.timeTriedToAttackPlayer = time;
												return false;
										}
								}
						}
						if (attacker.getUnderAttackBy() > 0)
						{
								if (victim.getPlayerId() != attacker.getUnderAttackBy())
								{
										if (time - attacker.timeInPlayerCombat <= 4000)
										{
												attacker.playerAssistant.sendMessage("You are already in combat.");
												return false;
										}
										else
										{
												if (victim.getPlayerId() != attacker.getUnderAttackBy())
												{
														attacker.setUnderAttackBy(0);
												}
										}
								}
						}
				}

				if (attacker.isInZombiesMinigame() || victim.isInZombiesMinigame())
				{
						return false;
				}

				if (EdgeAndWestsRule.isEdgeOrWestRule(attacker, victim, "NORMAL ATTACK"))
				{
						return false;
				}


				attacker.setPlayerIdToFollow(attacker.getPlayerIdAttacking());
				return true;
		}

		/**
		 * Apply the normal attack packet, the attack could have ranged or melee.
		 * 
		 * @param attacker
		 *        The player sending the packet.
		 * @param victimID
		 *        The player id being clicked on by the attacker.
		 */
		public static void normalAttackPacket(Player attacker, int victimID)
		{
				attacker.setUsingRanged(false);
				attacker.setMeleeFollow(false);
				attacker.setLastCastedMagic(false);
				AttackPlayer.resetAttackData(attacker);
				Player victim = PlayerHandler.players[victimID];

				if (victim == null)
				{
						return;
				}

				attacker.faceUpdate(victim.getPlayerId() + 32768);


				if (Area.inDuelArena(victim) && attacker.getDuelStatus() != 5 && !attacker.hasLastCastedMagic())
				{
						attacker.setMeleeFollow(true);
						if (victim.isInTrade() || victim.isUsingBankInterface() || victim.getDuelStatus() != 0)
						{
								attacker.turnPlayerTo(victim.getX(), victim.getY());
								attacker.getPA().sendMessage(victim.getPlayerName() + " is busy.");
								Combat.resetPlayerAttack(attacker);
								return;
						}
						if (!Area.inDuelArena(attacker))
						{
								return;
						}

						if (Area.inDuelArenaRing(attacker))
						{
								return;
						}

						if (attacker.findOtherPlayerId > 0)
						{
								return;
						}
						attacker.setPlayerIdToFollow(victim.getPlayerId());
						attacker.findOtherPlayerId = 20;
						CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										if (attacker.findOtherPlayerId > 0)
										{
												attacker.findOtherPlayerId--;
												if (attacker.getPA().withinDistanceOfTargetPlayer(victim, 1))
												{
														attacker.getTradeAndDuel().requestDuel(victim.getPlayerId());
														container.stop();
												}
										}
										else
										{
												container.stop();
										}
								}

								@Override
								public void stop()
								{
										attacker.findOtherPlayerId = 0;
								}
						}, 1);
						return;
				}

				if (attacker.getAutoCasting())
				{
						attacker.setLastCastedMagic(true);
				}
				else
				{
						if (RangedData.isWieldingMediumRangeRangedWeapon(attacker) || RangedData.isWieldingShortRangeRangedWeapon(attacker))
						{
								attacker.setUsingRanged(true);
						}
						else
						{
								attacker.setMeleeFollow(true);
						}
				}

				attacker.setPlayerIdAttacking(victimID);
				Combat.stopMovement(attacker, victim, false);

				// Has to be kept here so the player doesn't run 2 extra tiles before realising he cannot even attack.
				if (!hasSubAttackRequirements(attacker, victim))
				{
						attacker.turnPlayerTo(victim.getX(), victim.getY());
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
				}
		}

		/**
		 * Reset specific player attack fields because we are starting a new attack round.
		 * 
		 * @param attacker
		 *        The player attacking
		 */
		public static void resetAttackData(Player attacker)
		{
				attacker.armadylCrossBowSpecial = false;
				attacker.veracEffectActivated = false;
				attacker.setUsingMediumRangeRangedWeapon(false);
				attacker.againstPlayer = false;
				attacker.setUsingShortRangeRangedWeapon(false);
				SpecialAttackTracker.resetSpecialAttackWeaponUsed(attacker);
				attacker.setDroppedRangedItemUsed(0);
				attacker.setAmmoDropped(false);
				attacker.ignorePlayerTurn = false;
		}
}