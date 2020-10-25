package game.content.combat;


import core.ServerConstants;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.content.achievement.Achievements;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicApplyDamage;
import game.content.combat.vsplayer.magic.MagicData;
import game.content.combat.vsplayer.melee.MeleeApplyDamage;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedApplyDamage;
import game.content.combat.vsplayer.range.RangedData;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.music.SoundSystem;
import game.content.prayer.PrayerBook;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import utility.Misc;

public class Combat
{
		public static boolean hasSerpentineHelm(Player player)
		{
				// Serpentine helms and the coloured versions.
				if (ItemAssistant.hasItemEquippedSlot(player, 18773, ServerConstants.HEAD_SLOT) || ItemAssistant.hasItemEquippedSlot(player, 18775, ServerConstants.HEAD_SLOT) || ItemAssistant.hasItemEquippedSlot(player, 18777, ServerConstants.HEAD_SLOT))
				{
						return true;
				}
				return false;
		}

		public static void lowerBoosterCombatLevels(Player player)
		{
				if (player.currentCombatSkillLevel[ServerConstants.ATTACK] > 118)
				{
						player.currentCombatSkillLevel[ServerConstants.ATTACK] = 118;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.ATTACK);
				}
				if (player.currentCombatSkillLevel[ServerConstants.DEFENCE] > 118)
				{
						player.currentCombatSkillLevel[ServerConstants.DEFENCE] = 118;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.DEFENCE);
				}
				if (player.currentCombatSkillLevel[ServerConstants.STRENGTH] > 118)
				{
						player.currentCombatSkillLevel[ServerConstants.STRENGTH] = 118;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.STRENGTH);
				}
				if (player.currentCombatSkillLevel[ServerConstants.RANGED] > 118)
				{
						player.currentCombatSkillLevel[ServerConstants.RANGED] = 118;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.RANGED);
				}
				if (player.currentCombatSkillLevel[ServerConstants.MAGIC] > 105)
				{
						player.currentCombatSkillLevel[ServerConstants.MAGIC] = 105;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
				}
		}

		public static boolean spellbookPacketAbuse(Player player, int spellTarget)
		{
				if (player.spellBook.equals("MODERN") && spellTarget >= 32 && spellTarget <= 48)
				{
						PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", tried to use spell: " + spellTarget);
						return true;
				}
				if (player.spellBook.equals("ANCIENT") && (spellTarget <= 31 || spellTarget >= 49))
				{
						PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", tried to use spell: " + spellTarget);
						return true;
				}
				return false;
		}

		public static int antiFire(Player player, boolean dragonBolt, boolean npc)
		{
				int resistance = 0;
				if (player.antiFirePotion)
				{
						resistance++;
				}
				if (player.playerEquipment[ServerConstants.SHIELD_SLOT] == 1540 || player.playerEquipment[ServerConstants.SHIELD_SLOT] == 11284 || !dragonBolt && player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
				{
						resistance++;
				}
				if (npc)
				{
						Combat.addCharge(player);
				}
				return resistance;
		}

		public static boolean wearingFullVerac(Player player)
		{
				return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4753 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4757 && player.playerEquipment[ServerConstants.LEG_SLOT] == 4759 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4755;
		}

		public static boolean wearingFullDharok(Player player)
		{
				return player.getWieldedWeapon() == 4718 && player.playerEquipment[ServerConstants.HEAD_SLOT] == 4716 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4720 && player.playerEquipment[ServerConstants.LEG_SLOT] == 4722;
		}

		public static boolean wearingFullGuthan(Player player)
		{
				return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4724 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4728 && player.playerEquipment[ServerConstants.LEG_SLOT] == 4730 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4726;
		}

		public static void castVengeance(Player player)
		{

				if (player.getDead())
				{
						return;
				}
				if (player.duelRule[4])
				{
						player.playerAssistant.sendMessage("Magic has been disabled for this duel!");
						return;
				}
				if (player.getVengeance())
				{
						player.getPA().sendMessage("You have already casted vengance.");
						return;
				}
				if (player.baseSkillLevel[ServerConstants.DEFENCE] < 40)
				{
						player.getPA().sendMessage("You need 40 defence to cast vengeance.");
						return;
				}
				if (System.currentTimeMillis() - player.lastVeng > 30000)
				{
						boolean hasRunePouch = ItemAssistant.hasItemInInventory(player, 18820);
						boolean earth = ItemAssistant.hasItemAmountInInventory(player, 557, 10);
						boolean astral = ItemAssistant.hasItemAmountInInventory(player, 9075, 4);
						boolean death = ItemAssistant.hasItemAmountInInventory(player, 560, 2);
						boolean earthInPouch = false;
						boolean astralInPouch = false;
						boolean deathInPouch = false;
						if (hasRunePouch)
						{
								if (!earth)
								{
										earth = RunePouch.specificRuneInsideRunePouch(player, "CHECK", 557, 10);
										if (earth)
										{
												earthInPouch = true;
										}
								}
								if (!astral)
								{
										astral = RunePouch.specificRuneInsideRunePouch(player, "CHECK", 9075, 4);
										if (astral)
										{
												astralInPouch = true;
										}
								}
								if (!death)
								{
										death = RunePouch.specificRuneInsideRunePouch(player, "CHECK", 560, 2);
										if (death)
										{
												deathInPouch = true;
										}
								}
						}
						if (earth && astral && death)
						{
								InterfaceAssistant.vengeanceTimer(player, true);
								player.setVengeance(true);
								player.lastVeng = System.currentTimeMillis();
								player.startAnimation(4410);
								Skilling.addSkillExperience(player, 112, ServerConstants.MAGIC);
								player.gfx100(726);
								if (earthInPouch)
								{
										RunePouch.specificRuneInsideRunePouch(player, "DELETE", 557, 10);
								}
								else
								{
										ItemAssistant.deleteItemFromInventory(player, 557, ItemAssistant.getItemSlot(player, 557), 10);
								}
								if (deathInPouch)
								{
										RunePouch.specificRuneInsideRunePouch(player, "DELETE", 560, 2);
								}
								else
								{
										ItemAssistant.deleteItemFromInventory(player, 560, ItemAssistant.getItemSlot(player, 560), 2);
								}
								if (astralInPouch)
								{
										RunePouch.specificRuneInsideRunePouch(player, "DELETE", 9075, 4);
								}
								else
								{
										ItemAssistant.deleteItemFromInventory(player, 9075, ItemAssistant.getItemSlot(player, 9075), 4);
								}
								player.getPA().sendMessage(":packet:vengeancerunes:");
								Movement.stopMovement(player);
						}
						else
						{
								player.playerAssistant.sendMessage("You need more earth runes, astral runes and death runes to cast this spell.");
						}
				}
				else
				{
						long time = 30000 - (System.currentTimeMillis() - player.lastVeng);
						time /= 1000;
						if (time == 0)
						{
								time = 1;
						}
						String second = time > 1 ? "seconds" : "second";
						player.playerAssistant.sendMessage("You must wait " + time + " " + second + " before casting this again.");
				}
		}

		/**
		 * Refresh combat skills.
		 * 
		 * @param player
		 *        The associated player.
		 */
		public static void refreshCombatSkills(Player player)
		{
				for (int i = 0; i < 7; i++)
				{
						Skilling.updateSkillTabFrontTextMain(player, i);
				}
		}

		/**
		 * @return True, if the player is wearing full void melee.
		 */
		public static boolean wearingFullVoidMelee(Player player)
		{
				return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11665 && player.playerEquipment[ServerConstants.LEG_SLOT] == 8840 && player.playerEquipment[ServerConstants.BODY_SLOT] == 8839 && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
		}

		/**
		 * Apply the redemption prayer effect.
		 * 
		 * @param player
		 *        The associated player.
		 */
		public static void appendRedemption(Player player, int damage)
		{
				if (damage <= 0)
				{
						return;
				}
				if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > (player.getBaseHitPointsLevel() / 10))
				{
						return;
				}
				if (player.prayerActive[ServerConstants.REDEMPTION])
				{
						player.addToHitPoints((int) (player.getBaseHitPointsLevel() * .25));
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
						Skilling.updateSkillTabFrontTextMain(player, 3);
						Skilling.updateSkillTabFrontTextMain(player, 5);
						player.gfx0(436);
						Combat.resetPrayers(player);
				}
		}

		/**
		 * Reset combat variables.
		 */
		public static void resetSpecialAttackData(Player player)
		{
				player.magicMultiSpell.clear();
				player.setSpecialAttackAccuracyMultiplier(1.0);
				player.specDamage = 1.0;
				player.setDragonClawsSpecialAttack(false);
				player.setMultipleDamageSpecialAttack(false);
				player.saradominSwordSpecialAttack = false;
				player.morrigansJavelinSpecialAttack = false;
				player.handCannonSpecialAttack = false;
				player.blowpipeSpecialAttack = false;
				player.showDragonBoltGFX = false;
				player.showRubyBoltGFX = false;
				player.showDiamondBoltGFX = false;
				player.setMagicBowSpecialAttack(false);
				player.setUsingDarkBowSpecialAttack(false);
				player.setUsingDarkBowNormalAttack(false);
				player.armadylCrossBowSpecial = false;
		}

		/**
		 * Restore the special attack by 5% every 30 seconds.
		 */
		public static void restoreSpecialAttackEvent(final Player player)
		{
				if (player.specialAttackEvent)
				{
						return;
				}
				if (player.getSpecialAttackAmount() >= 10)
				{
						return;
				}
				player.specialAttackEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.getSpecialAttackAmount() < 10)
								{
										player.specialAttackRestoreTimer++;
										if (player.specialAttackRestoreTimer >= 33)
										{
												player.setSpecialAttackAmount(player.getSpecialAttackAmount() + 0.5, false);
												CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
												player.specialAttackRestoreTimer = 0;
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
								player.specialAttackEvent = false;
								player.specialAttackRestoreTimer = 0;
						}
				}, 1);
		}

		/**
		 * Use to check if the player is in a player vs player area or in combat and also send alert messages to the player.
		 * 
		 * @return True, if the player is in PVP area or in combat.
		 */
		public static boolean inPVPAreaOrCombat(Player player)
		{
				if (Area.inPVPArea(player))
				{
						player.playerAssistant.sendMessage("You cannot do this in a player vs player area.");
						return true;
				}
				if (inCombatAlert(player))
				{
						return true;
				}
				return false;
		}

		/**
		 * Use to check if the player is in combat and also send alert message to the player.
		 * 
		 * @return True, if player is in combat.
		 */
		public static boolean inCombatAlert(Player player)
		{
				if ((inCombat(player) || System.currentTimeMillis() - player.timeNpcAttackedPlayer < 9600) && !player.isAdministratorRank())
				{
						int timer = secondsUntillOutOfCombat(player);
						String word = timer > 1 ? "seconds" : "second";
						if (timer <= 0)
						{
								return false;
						}
						player.playerAssistant.sendMessage("You need to wait " + timer + " " + word + " from being out of combat to use this.");
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 *        The associated player.
		 * @return Amount of seconds untill completely out of combat, starting from 10.
		 */
		public static int secondsUntillOutOfCombat(Player player)
		{
				long timer = player.getTimeAttackedAnotherPlayer();
				if (timer < player.getTimeUnderAttackByAnotherPlayer())
				{
						timer = player.getTimeUnderAttackByAnotherPlayer();
				}
				else if (timer < player.getTimeNpcAttackedPlayer())
				{
						timer = player.getTimeNpcAttackedPlayer();
				}
				else if (timer < player.timeNpcAttackedPlayer)
				{
						timer = player.timeNpcAttackedPlayer;
				}

				if ((System.currentTimeMillis() - timer) <= 650)
				{
						timer = 10;
				}
				else
				{
						timer = (System.currentTimeMillis() - timer) / 1000;
				}
				timer = 10 - timer;
				if (timer == 0)
				{
						timer = 10;
				}
				return (int) timer;
		}

		/**
		 * @param player
		 *        The associated player.
		 * @return True, if the player has attacked another player within the given time.
		 */
		public static boolean wasAttackingAnotherPlayer(Player player, long time)
		{
				if (System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= time)
				{
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 *        The associated player.
		 * @return True, if the player has been under attack by another player within the given time.
		 */
		public static boolean wasUnderAttackByAnotherPlayer(Player player, long time)
		{
				if (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= time)
				{
						return true;
				}
				return false;
		}

		/**
		 * @return True, if the player has been in combat with an NPC in the last 5 seconds.
		 */
		public static boolean wasAttackedByNpc(Player player)
		{
				if (System.currentTimeMillis() - player.timeNpcAttackedPlayer < 4000)
				{
						return true;
				}
				return false;
		}

		/**
		 * @return True, if the player has been in combat in the last 10 seconds.
		 */
		public static boolean inCombat(Player player)
		{
				if (wasUnderAttackByAnotherPlayer(player, 9600) || wasAttackingAnotherPlayer(player, 9600) || wasAttackedByNpc(player))
				{
						return true;
				}
				if (System.currentTimeMillis() - player.timeExitedWilderness < 10000)
				{
						return true;
				}
				return false;
		}

		public static boolean getUsingCrossBow(Player player)
		{
				if (player.getWieldedWeapon() == 9185 || player.getWieldedWeapon() == 18642 || player.getWieldedWeapon() == 18836)
				{
						return true;
				}
				return false;
		}

		public static boolean usingSpear(Player player)
		{
				boolean spear = false;
				if (player.getWieldedWeapon() == 1249 || player.getWieldedWeapon() == 11716 || player.getWieldedWeapon() == 13905 || player.getWieldedWeapon() == 13907 || player.getWieldedWeapon() == 4726)
				{
						spear = true;
				}
				return spear;
		}

		public static boolean usingCrystalBow(Player player)
		{
				return player.getWieldedWeapon() >= 4212 && player.getWieldedWeapon() <= 4223;
		}

		public static void appendHitFromNpcOrVengEtc(Player player, int damage, int hitSplatColour, int icon)
		{
				if (damage > player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS))
				{
						damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
						if (damage == 0)
						{
								return;
						}
				}
				if (damage == 0 && player.getDead())
				{
						return;
				}
				boolean maxHit = false;
				if (player.dragonSpearEvent)
				{
						player.dragonSpearEffectStack.add("Damage:" + damage + " " + hitSplatColour + " " + icon + " " + maxHit);
						return;
				}
				player.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
				player.dealDamage(damage);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
		}

		/**
		 *
		 * @param attacker
		 *        The player dealing the hitsplat.
		 * @param theVictim
		 *        The player receiving the hitsplat.
		 * @param damage
		 *        The damage dealt.
		 * @param hitSplatColour
		 * @param icon
		 *        The icon type. -1 for no icon, 0 is Melee, 1 is Ranged, 2 is Magic, 3 for deflect, 4 for Dwarf cannon.
		 */
		public static void createHitsplatOnPlayer(Player attacker, Player victim, int damage, int hitSplatColour, int icon)
		{
				if (attacker.getDead() && attacker.getHeight() == 22)
				{
						return;
				}
				if (damage == 0 && victim.getDead())
				{
						return;
				}
				if (attacker.getDead() && attacker.getHeight() == 20)
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
				if (!attacker.ignoreInCombat)
				{
						victim.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
						attacker.setTimeAttackedAnotherPlayer(System.currentTimeMillis());
				}
				victim.damageTaken[attacker.getPlayerId()] += damage;

				boolean maxHit = false;
				if (attacker.maximumDamageMelee < 4)
				{
						attacker.maximumDamageMelee = 4;
				}
				if (attacker.maximumDamageRanged < 4)
				{
						attacker.maximumDamageRanged = 4;
				}
				if (attacker.getMaximumDamageMagic() < 4)
				{
						attacker.setMaximumDamageMagic(4);
				}

				switch (icon)
				{
						case 0:
								int damageMelee = attacker.maximumDamageMelee;
								if (attacker.isGraniteMaulSpecial)
								{
										damageMelee = attacker.graniteMaulSpecialCriticalDamage;
								}
								maxHit = damage >= damageMelee * 0.96;
								break;
						case 1:
								maxHit = damage >= attacker.maximumDamageRanged * 0.96;
								break;
						case 2:
								maxHit = damage >= attacker.getMaximumDamageMagic() * 0.96;
								break;
				}
				if (icon >= 0)
				{
						attacker.totalDamage[icon] += damage;
				}

				if (victim.isCombatBot())
				{
						if (icon == ServerConstants.MAGIC_ICON)
						{
								victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MAGIC;
						}
						else if (icon == ServerConstants.RANGED_ICON)
						{
								victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_RANGED;
						}
						else if (icon == ServerConstants.MELEE_ICON)
						{
								victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MELEE;
						}
				}
				if (victim.dragonSpearEvent)
				{
						victim.dragonSpearEffectStack.add("Damage:" + damage + " " + hitSplatColour + " " + icon + " " + maxHit);
						return;
				}
				victim.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
				victim.dealDamage(damage);
				Effects.recoilEffect(attacker, victim, damage);
				Effects.appendRetributionOrWrath(victim, attacker);
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.HITPOINTS);

		}

		public static void appendVengeance(Player victimOfVengeance, Player vengeancePlayer, int damage)
		{
				if (damage <= 0)
				{
						return;
				}

				if (vengeancePlayer.getDead() && victimOfVengeance.getHeight() == 20)
				{
						return;
				}
				victimOfVengeance.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
				vengeancePlayer.forcedText = "Taste vengeance!";
				vengeancePlayer.forcedChatUpdateRequired = true;
				vengeancePlayer.setUpdateRequired(true);
				vengeancePlayer.setVengeance(false);
				damage = (int) (damage * 0.75);
				if (damage > victimOfVengeance.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS))
				{
						damage = victimOfVengeance.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}

				vengeancePlayer.specialAttackWeaponUsed[31] = 1;
				vengeancePlayer.againstPlayer = true;
				vengeancePlayer.setWeaponAmountUsed(31);
				SpecialAttackTracker.saveMaximumDamage(vengeancePlayer, damage, "VENGEANCE", false);
				vengeancePlayer.totalDamage[3] += damage;
				victimOfVengeance.damageTaken[vengeancePlayer.getPlayerId()] += damage;
				appendHitFromNpcOrVengEtc(victimOfVengeance, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				Skilling.updateSkillTabFrontTextMain(victimOfVengeance, 3);
				victimOfVengeance.setUpdateRequired(true);
				if (victimOfVengeance.getDead())
				{
						Effects.appendRetributionOrWrath(victimOfVengeance, vengeancePlayer);
				}
		}

		public static void applyHitSplatOnPlayer(Player attacker, int theTarget)
		{
				Player victim = PlayerHandler.players[theTarget];
				if (victim == null)
				{
						return;
				}
				if (victim.isTeleporting())
				{
						return;
				}
				if (victim.getDead())
				{
						attacker.setHitDelay(0);
						attacker.resetPlayerIdAttacking();
						return;
				}

				victim.doNotClosePmInterface = true;
				victim.getPA().closeInterfaces();

				if (victim.getPlayerIdAttacking() <= 0 && victim.getNpcIdAttacking() <= 0)
				{
						if (victim.getAutoRetaliate() == 1 && !victim.isMoving() && !victim.doingAnAction())
						{
								if (!victim.isCombatBot() && !victim.getBotStatus().equals("LOOTING"))
								{
										victim.setPlayerIdAttacking(attacker.getPlayerId());
										victim.resetNpcIdToFollow();
								}
								else if (victim.isCombatBot() && victim.getBotStatus().equals("LOOTING"))
								{
										if (victim.botEarlyRetreat)
										{
												return;
										}
										victim.botEarlyRetreat = true;
										BotContent.walkToBankArea(victim);
										BotContent.regearStage1Event(victim);
										BotContent.switchOffPrayerEventWhenOutsideWild(victim);
										if (System.currentTimeMillis() - victim.timeAttackedAnotherPlayer >= 4000)
										{
												BotCommunication.sendBotMessage(victim, "?", false);
										}
								}
								if (victim.getBotStatus().equals("LOOTING"))
								{
										BotContent.addBotDebug(victim, "Is looting, cannot auto retaliate.");
								}
						}
				}
				if ((victim.getAttackTimer() <= 3 || victim.getAttackTimer() == 0 && victim.getPlayerIdAttacking() == 0 && !attacker.getDoingAgility()) && System.currentTimeMillis() - attacker.lastAttackAnimationTimer >= 1800)
				{ // block animation
						victim.startAnimation(getBlockAnimation(victim));
						if (!victim.soundSent)
						{
								SoundSystem.sendSound(victim, attacker, 511, 450);
						}
				}

				/* Player apply melee hitsplat */
				if (attacker.getProjectileStage() == 0)
				{
						MeleeApplyDamage.applyMeleeHitSplat(attacker, victim, true);
						if (attacker.getMultipleDamageSpecialAttack())
						{
								MeleeApplyDamage.applyMeleeHitSplat(attacker, victim, false);
						}
				}

				/* Player apply range hitsplat */
				else if (!attacker.isUsingMagic() && attacker.getProjectileStage() > 0)
				{
						RangedApplyDamage.applyRangedHitSplat(attacker, victim, 1);
						if (attacker.isMagicBowSpecialAttack() || attacker.isUsingDarkBowSpecialAttack() || attacker.isUsingDarkBowNormalAttack())
						{
								RangedApplyDamage.applyRangedHitSplat(attacker, victim, 2);
						}
				}

				/* Player apply magic hitsplat */
				else if (attacker.getProjectileStage() > 0)
				{
						MagicApplyDamage.applyMagicHitsplatOnPlayer(attacker, victim);
				}
				attacker.getPA().requestUpdates();
				attacker.setOldPlayerIndex(0);
				attacker.setProjectileStage(0);
				attacker.setLastRangedWeaponUsed(0);
				attacker.bowSpecShot = 0;
		}

		public static boolean usingMultiSpell(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 12891:
						case 12881:
						case 13011:
						case 13023:
						case 12919:
								// blood spells
						case 12929:
						case 12963:
						case 12975:
								return true;
				}
				return false;
		}


		public static void multiSpellEffect(Player player, Player victim, int damage)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 13011:
						case 13023:
								if (System.currentTimeMillis() - victim.reduceStat > 35000)
								{
										victim.reduceStat = System.currentTimeMillis();
										victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((victim.getBaseAttackLevel() * 10) / 100);
								}
								break;
						case 12919:
								// blood spells
						case 12929:
								int heal = damage / 4;
								player.addToHitPoints(heal);
								break;
						case 12891:
						case 12881:
								if (victim.canBeFrozen())
								{
										Movement.stopMovement(victim);
										Combat.resetPlayerAttack(victim);
										// Ice burst or Ice barrage.
										victim.setFrozenLength(CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12881 ? 10000 : 20000);
										victim.frozenBy = player.getPlayerId();
										victim.playerAssistant.sendMessage("You have been frozen!");
								}
								break;
				}
		}

		public static void applySmite(Player player, Player victim, int damage)
		{
				if (victim == null)
				{
						return;
				}
				if (!player.prayerActive[ServerConstants.SMITE])
				{
						return;
				}
				if (player.getTank())
				{
						return;
				}
				if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) < 0)
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
				}
				if (damage <= 0)
				{
						return;
				}
				if (victim.dragonSpearEvent)
				{
						victim.dragonSpearEffectStack.add("Prayer:" + damage);
						return;
				}
				victim.currentCombatSkillLevel[ServerConstants.PRAYER] -= damage / 4;
				if (victim.getCurrentCombatSkillLevel(ServerConstants.PRAYER) <= 0)
				{
						victim.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
						resetPrayers(victim);
				}
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.PRAYER);
		}

		public static void fireProjectilePlayer(Player player)
		{
				if (player.getOldPlayerIndex() > 0)
				{
						if (PlayerHandler.players[player.getOldPlayerIndex()] != null)
						{
								player.setProjectileStage(2);
								final int pX = player.getX();
								final int pY = player.getY();
								int oX = PlayerHandler.players[player.getOldPlayerIndex()].getX();
								int oY = PlayerHandler.players[player.getOldPlayerIndex()].getY();
								final int offX = (pY - oY) * -1;
								final int offY = (pX - oX) * -1;
								if (player.getUsingRanged())
								{
										player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, getProjectileSpeed(player), getRangeProjectileGFX(player), getProjectileStartHeight(player), getProjectileEndHeight(player), -player.getOldPlayerIndex() - 1, getStartDelay(player), getProjectileSlope(player));
										if (player.isMagicBowSpecialAttack())
										{
												player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 95, getRangeProjectileGFX(player), getProjectileStartHeight(player), getProjectileEndHeight(player), -player.getOldPlayerIndex() - 1, 65, 10);
										}
										else if (usingDbow(player))
										{
												player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, player.isUsingDarkBowSpecialAttack() ? 120 : 80, getRangeProjectileGFX(player), 36, 31, -player.getOldPlayerIndex() - 1, player.isUsingDarkBowNormalAttack() ? 54 : 51, player.isUsingDarkBowSpecialAttack() ? 30 : getProjectileSlope(player));
										}
										else if (player.handCannonSpecialAttack)
										{
												player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 95, getRangeProjectileGFX(player), getProjectileStartHeight(player), getProjectileEndHeight(player), -player.getOldPlayerIndex() - 1, 65, getProjectileSlope(player));
										}
								}
						}
				}
		}

		public static int getProjectileSlope(Player player)
		{
				if (RangedData.hasBowEquipped(player))
				{
						return 16;
				}
				else
				{
						return 0;
				}
		}

		/**
		 * @return The beginning height of the projectile.
		 */
		public static int getProjectileStartHeight(Player player)
		{
				if (player.armadylCrossBowSpecial)
				{
						return 36;
				}
				switch (player.getWieldedWeapon())
				{
						case 15241:
								// Hand cannon.
								return 20;
				}
				return 38;
		}

		/**
		 * @return The final height of the project as it ends.
		 */
		public static int getProjectileEndHeight(Player player)
		{
				if (player.armadylCrossBowSpecial)
				{
						return 36;
				}
				switch (player.getWieldedWeapon())
				{
						case 15241:
								// Hand cannon.
								return 20;
				}
				return 34;
		}


		public static boolean usingDbow(Player player)
		{
				return CombatConstants.isDarkBow(player.getWieldedWeapon());
		} /* Prayer */

		public static boolean checkSpecAmount(Player player, int weapon)
		{
				switch (weapon)
				{

						case 18660: // Sara's blessed sword.
									// Heavy ballista
						case 18807:
								if (player.getSpecialAttackAmount() >= 6.5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 6.5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;

						// Dragon halberd.
						case 3204:
								if (player.getSpecialAttackAmount() >= 3)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 3, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						// Hand cannon.
						case 15241:
						case 18785: // Abyssal dagger.
								if (player.getSpecialAttackAmount() >= 5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						case 1249:
								// Dragon spear
						case 1215:
						case 1231:
						case 5680:
						case 5698:
						case 13901:
						case 13899:
						case 1305:
						case 1434:
								if (player.getSpecialAttackAmount() >= 2.5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 2.5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						case 4151:
						case 18767: // Abyssal tentacle.
						case 15445:
						case 15444:
						case 15443:
						case 15442:
						case 15441:
						case 11694:
						case 11698:
						case 4153: // Granite maul.
						case 18662: // Granite maul (or).
						case 14484:
						case 13883:
						case 10887:
						case 18659: // Magic shortbow (i).
									// morrigan throwing axe
						case 13879:
								// Morrigan Javeline
						case 18779: // Toxic blowpipe.
								if (player.getSpecialAttackAmount() >= 5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						case 18771:
								if (player.getSpecialAttackAmount() >= 5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						case 1377:
						case 11696:
						case 11730:
						case 13905:
						case 13907:
						case 15486:
								if (player.getSpecialAttackAmount() >= 10)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 10, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
						case 4587:
						case 859:
						case 861: // Magic shortbow.
						case 11235:
						case 15701:
						case 15702:
						case 15703:
						case 15704:
						case 11700:
								if (player.getSpecialAttackAmount() >= 5.5)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.5, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;

						case 18642: // Armadyl crossbow.
								if (player.getSpecialAttackAmount() >= 4)
								{
										player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 4.0, true);
										CombatInterface.addSpecialBar(player, weapon);
										return true;
								}
								return false;
				}
				return false;
		}

		/**
		 * Stop the player from attacking.
		 */
		public static void resetPlayerAttack(Player player)
		{
				player.setUsingRanged(false);
				player.setMeleeFollow(false);
				player.resetNpcIdentityAttacking();
				player.resetFaceUpdate();
				player.resetPlayerIdAttacking();
				Follow.resetFollow(player);
				player.setLastCastedMagic(false);
				player.setSpellId(-1);
		}

		public static int getCombatDifference(int combat1, int combat2)
		{
				if (combat1 > combat2)
				{
						return (combat1 - combat2);
				}
				if (combat2 > combat1)
				{
						return (combat2 - combat1);
				}
				return 0;
		}


		public final static double[] prayerDrainData = {
				0.05, // Thick Skin.
				0.05, // Burst of Strength.
				0.05, // Clarity of Thought.
				0.05, // Sharp Eye.
				0.05, // Mystic Will.
				0.10, // Rock Skin.
				0.10, // SuperHuman Strength.
				0.10, // Improved Reflexes.
				0.016, // Rapid restore.
				0.03, // Rapid Heal.
				0.03, // Protect Items.
				0.10, // Hawk eye.
				0.10, // Mystic Lore.
				0.20, // Steel Skin.
				0.20, // Ultimate Strength.
				0.20, // Incredible Reflexes.
				0.20, // Protect from Magic.
				0.20, // Protect from Missiles.
				0.20, // Protect from Melee.
				0.20, // Eagle Eye.
				0.20, // Mystic Might.
				0.05, // Retribution.
				0.10, // Redemption.
				0.3, // Smite.
				0.4, // Chivalry.
				0.4, // Piety.
				0.05, // Preserve.
				0.4, // Rigour.
				0.4, // Augury.
		};



		public static void reducePrayerLevel(Player player)
		{
				if (player.getTank())
				{
						return;
				}
				if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) - 1 > 0)
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] -= 1;
				}
				else
				{
						player.playerAssistant.sendMessage("You have run out of prayer points!");
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
						SoundSystem.sendSound(player, 437, 0);
						resetPrayers(player);
				}
				Skilling.updateSkillTabFrontTextMain(player, 5);
		}

		public static void resetPrayers(Player player)
		{
				PrayerBook.resetAllPrayerGlows(player);
				player.headIcon = -1;
				player.getPA().requestUpdates();
				InterfaceAssistant.quickPrayersOff(player);
				player.quickPray = false;
		}

		/**
		 *
		 * @param attackType
		 *        The combat type, 0 is Melee, 1 is Range, 2 is Magic.
		 * @param damage
		 *        The damage dealt to the target.
		 */
		public static void addCombatExperience(Player player, int attackType, int damage)
		{
				if (player.isMagicSplash() && player.isUsingMagic())
				{
						damage = 0;
						// Do not return because magic gives base experience.
				}

				switch (attackType)
				{

						case 0:
								// Melee
								switch (player.getCombatStyle())
								{

										case 0:
												// Accurate
												Skilling.addSkillExperience(player, 4 * damage, ServerConstants.ATTACK);
												break;
										case 1:
												// Aggressive
												Skilling.addSkillExperience(player, 4 * damage, ServerConstants.STRENGTH);
												break;
										case 2:
												// Block
												Skilling.addSkillExperience(player, 4 * damage, ServerConstants.DEFENCE);
												break;
										case 3:
												// Controlled
												for (int i = 0; i < 3; i++)
												{
														Skilling.addSkillExperience(player, (4 * damage) / 3, i);
												}
												break;

								}

								break;

						case 1:
								// Ranged
								switch (player.getCombatStyle())
								{

										case 0:
												// Accurate
										case 1:
												// Rapid
												Skilling.addSkillExperience(player, 4 * damage, ServerConstants.RANGED);
												break;
										case 3:
												// Block
												Skilling.addSkillExperience(player, 2 * damage, ServerConstants.RANGED);
												Skilling.addSkillExperience(player, 2 * damage, ServerConstants.DEFENCE);
												break;

								}

								break;

						case 2:
								// Magic
								int magicXP = damage * 2 + CombatConstants.MAGIC_SPELLS[player.getSpellId()][7];

								// Teleblock.
								if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12445)
								{
										// Magic damage is set to 2 if it is a half tb, 5 if full tb.
										magicXP = 80 + player.getMagicDamage();
								}
								Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
								if (player.getSpellId() == 47)
								{
										SoundSystem.sendSound(player, victim, 1125, 300);
								}
								else if (player.getSpellId() == 43)
								{
										SoundSystem.sendSound(player, victim, 1110, 300);
								}
								Skilling.addSkillExperience(player, magicXP, ServerConstants.MAGIC);
								break;

				}

				// Teleblock spell
				if (player.getSpellId() >= 0)
				{
						if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12445 && player.isUsingMagic())
						{
						}
						else
						{
								if (damage > 0)
								{
										Skilling.addSkillExperience(player, (int) ((damage * 1.3)), ServerConstants.HITPOINTS); // Hitpoints experience.
								}
						}
				}
				else
				{
						Skilling.addSkillExperience(player, (int) ((damage * 1.3)), ServerConstants.HITPOINTS); // Hitpoints experience.
				}
		}

		/**
		 * Weapon stand, walk, run, etc emotes
		 **/
		public static void updatePlayerStance(Player player)
		{
				String weapon = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();

				/* Normal animations for without weapons. */
				player.playerStandIndex = 0x328;
				player.playerTurnIndex = 0x337;
				player.playerWalkIndex = 0x333;
				player.playerTurn180Index = 0x334;
				player.playerTurn90CWIndex = 0x335;
				player.playerTurn90CCWIndex = 0x336;
				player.playerRunIndex = 0x338;

				if (weapon.contains("ahrim") || weapon.contains("sceptre") || weapon.contains("toktz-mej-tal") || weapon.contains("trident"))
				{
						weaponRoamAnimations(player, 809, 1146, 1210);
						return;
				}
				if (weapon.equals("elder maul"))
				{
						weaponRoamAnimations(player, 13733, 13734, 13731);
						return;
				}
				if (weapon.contains("boxing"))
				{
						weaponRoamAnimations(player, 3677, 3680, 3680);
						return;
				}
				if (weapon.contains("sled"))
				{
						weaponRoamAnimations(player, 1461, 1468, 1467);
						player.playerTurnIndex = 1461; // standTurnAnimIndex
						player.playerTurn180Index = 1461; // turn180AnimIndex
						player.playerTurn90CWIndex = 1461; // turn90CWAnimIndex
						player.playerTurn90CCWIndex = 1461; // turn90CWAnimIndex
						player.setTransformed(20);
						return;
				}
				if (weapon.contains("toy kite"))
				{
						weaponRoamAnimations(player, 8981, 8982, 8986);
						player.playerTurnIndex = 8982; // standTurnAnimIndex
						player.playerTurn180Index = 8982; // turn180AnimIndex
						player.playerTurn90CWIndex = 8982; // turn90CWAnimIndex
						player.playerTurn90CCWIndex = 8982; // turn90CWAnimIndex
				}
				if (weapon.contains("basket of eggs"))
				{
						weaponRoamAnimations(player, 1836, 1836, 1836);
						player.playerTurnIndex = 1836; // standTurnAnimIndex
						player.playerTurn180Index = 1836; // turn180AnimIndex
						player.playerTurn90CWIndex = 1836; // turn90CWAnimIndex
						player.playerTurn90CCWIndex = 1836; // turn90CWAnimIndex
				}
				if (weapon.contains("easter carrot"))
				{
						weaponRoamAnimations(player, 11543, 11545, 11544);
						return;
				}
				if (weapon.contains("staff") || weapon.contains("halberd") || weapon.contains("guthan") || weapon.contains("rapier") || weapon.contains("wand"))
				{
						weaponRoamAnimations(player, 12010, 1146, 1210);
						player.playerTurnIndex = 1205;
						player.playerTurn180Index = 1206;
						player.playerTurn90CWIndex = 1207;
						player.playerTurn90CCWIndex = 1208;
						return;
				}
				if (weapon.contains("dharok"))
				{
						player.playerStandIndex = 0x811;
						player.playerWalkIndex = 0x67F;
						player.playerRunIndex = 12001;
						return;
				}
				if (weapon.contains("verac"))
				{
						weaponRoamAnimations(player, 1832, 1830, 1831);
						return;
				}
				if (weapon.contains("karil"))
				{
						player.playerStandIndex = 2074;
						player.playerWalkIndex = 2076;
						player.playerRunIndex = 2077;
						return;
				}
				if (weapon.contains("2h sword") || weapon.contains("godsword") || weapon.contains("blessed sword") || weapon.contains("saradomin sword") || weapon.contains("blessed sword"))
				{
						weaponRoamAnimations(player, 7047, 7046, 7039);
						player.playerTurnIndex = 7040;
						player.playerTurn180Index = 7045;
						player.playerTurn90CWIndex = 7043;
						player.playerTurn90CCWIndex = 7044;
						return;
				}
				if (weapon.contains("sword") || weapon.contains("scimitar"))
				{
						weaponRoamAnimations(player, 0x328, 0x333, 1210);
						player.playerTurnIndex = 0x337;
						player.playerTurn180Index = 0x334;
						player.playerTurn90CWIndex = 0x335;
						player.playerTurn90CCWIndex = 0x336;
						return;
				}
				if (weapon.contains("crossbow"))
				{
						weaponRoamAnimations(player, 4591, 4226, 4228);
						return;
				}
				if (weapon.contains("bow"))
				{
						weaponRoamAnimations(player, 808, 819, 824);
						return;
				}
				switch (player.getWieldedWeapon())
				{
						// Abyssal Whips.
						case 4151:
						case 18767: // Abyssal tentacle.
						case 15445:
						case 15444:
						case 15443:
						case 15442:
						case 15441:
								weaponRoamAnimations(player, player.getWieldedWeapon() == 18767 ? 1832 : 11973, 11975, player.getWieldedWeapon() == 18767 ? 1661 : 11976);
								break;

						case 18807: // Heavy ballista.
								weaponRoamAnimations(player, 13722, 13723, 13724);
								break;

						case 10887:
								// Barrelchest Anchor
								player.playerStandIndex = 5869;
								player.playerWalkIndex = 5867;
								player.playerRunIndex = 5868;
								break;

						case 15241:
								// Hand cannon
								player.playerStandIndex = 12155;
								player.playerWalkIndex = 12154;
								player.playerRunIndex = 12154;
								break;
						case 6528:
								// Tzhaar-ket-om
								player.playerStandIndex = 0x811;
								player.playerWalkIndex = 1663;
								player.playerRunIndex = 1664;
								break;
						case 4153: // Granite maul.
						case 18662: // Granite maul (or).
								player.playerStandIndex = 1662;
								player.playerWalkIndex = 1663;
								player.playerRunIndex = 1664;
								break;
						case 1305:
								// Dragon longsword
								player.playerStandIndex = 809;
								break;
						case 19784:
								player.playerStandIndex = 809;
								break;

				}
		}

		/**
		 * The animations of the player when wielding a weapon.
		 * 
		 * @param stand
		 *        Animation when standing.
		 * @param walk
		 *        Animation when walking.
		 * @param run
		 *        Animation when running.
		 */
		public static void weaponRoamAnimations(Player player, int stand, int walk, int run)
		{
				player.playerStandIndex = stand;
				player.playerWalkIndex = walk;
				player.playerRunIndex = run;
		}

		/**
		 * Block animations
		 */
		public static int getBlockAnimation(Player player)
		{
				if (player.getDoingAgility() || player.getTransformed() != 0 || player.isTeleporting())
				{
						return -1;
				}
				player.soundSent = false;
				String shield = ItemAssistant.getItemName(player.playerEquipment[ServerConstants.SHIELD_SLOT]).toLowerCase();
				String weapon = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();
				Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
				if (shield.contains("defender"))
				{
						SoundSystem.sendSound(player, victim, 791, 0);
						player.soundSent = true;
						return 4177;
				}
				if (shield.contains("book") && (weapon.contains("wand")))
				{
						return 404;
				}
				if (shield.contains("shield"))
				{
						SoundSystem.sendSound(player, victim, 791, 0);
						player.soundSent = true;
						return 1156;
				}
				if (weapon.contains("scimitar"))
				{
						return 12030;
				}
				if (weapon.equals("staff of light"))
				{
						return 13046;
				}
				if (weapon.contains("staff") || weapon.contains("trident"))
				{
						return 420;
				}
				if (weapon.contains("boxing gloves"))
				{
						return 3679;
				}
				switch (player.getWieldedWeapon())
				{


						case 18826: // Elder maul
								return 13732;

						// Heavy ballista.
						case 18807:
								return 13725;
						case 1291:
						case 1293:
						case 1295:
						case 1297:
						case 1299:
						case 1301:
						case 1303:
						case 1305:
						case 6607:
						case 13474:
						case 13899:
						case 13901:
						case 13923:
						case 13925:
						case 13982:
						case 13984:
						case 16024:
						case 16025:
						case 16026:
						case 16027:
						case 16028:
						case 16029:
						case 16030:
						case 16031:
						case 16032:
						case 16033:
						case 16034:
						case 16383:
						case 16385:
						case 16387:
						case 16389:
						case 16391:
						case 16393:
						case 16395:
						case 16397:
						case 16399:
						case 16401:
						case 16403:
						case 16961:
						case 16963:
						case 18351:
						case 18352:
						case 18367:
						case 18368:
						case 1321:
						case 1323:
						case 1325:
						case 1327:
						case 1329:
						case 1331:
						case 1333:
						case 4587:
						case 6611:
						case 13979:
						case 13981:
						case 14097:
						case 14287:
						case 14289:
						case 14291:
						case 14293:
						case 14295:
						case 746:
						case 747:
						case 1203:
						case 1205:
						case 1207:
						case 1209:
						case 1211:
						case 1213:
						case 1215:
						case 1217:
						case 1219:
						case 1221:
						case 1223:
						case 1225:
						case 1227:
						case 1229:
						case 1231:
						case 1233:
						case 1235:
						case 1813:
						case 5668:
						case 5670:
						case 5672:
						case 5674:
						case 5676:
						case 5678:
						case 5680:
						case 5682:
						case 5684:
						case 5686:
						case 5688:
						case 5690:
						case 5692:
						case 5694:
						case 5696:
						case 5698:
						case 18785: // Abyssal dagger.
						case 5700:
						case 5702:
						case 6591:
						case 6593:
						case 6595:
						case 6597:
						case 8872:
						case 8873:
						case 8875:
						case 8877:
						case 8879:
						case 13976:
						case 13978:
						case 14297:
						case 14299:
						case 14301:
						case 14303:
						case 14305:
						case 15826:
						case 15848:
						case 15849:
						case 15850:
						case 15851:
						case 15853:
						case 15854:
						case 15855:
						case 15856:
						case 15857:
						case 15858:
						case 15859:
						case 15860:
						case 15861:
						case 15862:
						case 15863:
						case 15864:
						case 15865:
						case 15866:
						case 15867:
						case 15868:
						case 15869:
						case 15870:
						case 15871:
						case 15872:
						case 15873:
						case 15874:
						case 15875:
						case 15876:
						case 15877:
						case 15879:
						case 15880:
						case 15881:
						case 15882:
						case 15883:
						case 15884:
						case 15885:
						case 15886:
						case 15887:
						case 15888:
						case 15889:
						case 15890:
						case 15891:
						case 16757:
						case 16759:
						case 16761:
						case 16763:
						case 16765:
						case 16767:
						case 16769:
						case 16771:
						case 16773:
						case 16775:
						case 16777:
						case 16779:
						case 16781:
						case 16783:
						case 16785:
						case 16787:
						case 16789:
						case 16791:
						case 16793:
						case 16795:
						case 16797:
						case 16799:
						case 16801:
						case 16803:
						case 16805:
						case 16807:
						case 16809:
						case 16811:
						case 16813:
						case 16815:
						case 16817:
						case 16819:
						case 16821:
						case 16823:
						case 16825:
						case 16827:
						case 16829:
						case 16831:
						case 16833:
						case 16835:
						case 16837:
						case 16839:
						case 16841:
						case 16843:
						case 17275:
						case 17277:
						case 667:
						case 1277:
						case 1279:
						case 1281:
						case 1283:
						case 1285:
						case 1287:
						case 1289:
						case 19780:
						case 16035:
						case 16036:
						case 16037:
						case 16038:
						case 16039:
						case 16040:
						case 16041:
						case 16042:
						case 16043:
						case 16044:
						case 16045:
						case 16935:
						case 16937:
						case 16939:
						case 16941:
						case 16943:
						case 16945:
						case 16947:
						case 16949:
						case 16951:
						case 16953:
						case 16955:
						case 16957:
						case 16959:
						case 18349:
						case 18350:
						case 18365:
						case 18366:
								return 12030;

						case 1171:
						case 1173:
						case 1175:
						case 1177:
						case 1179:
						case 1181:
						case 1183:
						case 1185:
						case 1187:
						case 1189:
						case 1191:
						case 1193:
						case 1195:
						case 1197:
						case 1199:
						case 1201:
						case 1540:
						case 2589:
						case 2597:
						case 2603:
						case 2611:
						case 2621:
						case 2629:
						case 2659:
						case 2675:
						case 2890:
						case 3122:
						case 3488:
						case 3758:
						case 4156:
						case 4224:
						case 4226:
						case 4227:
						case 4228:
						case 4229:
						case 4230:
						case 4231:
						case 4232:
						case 4233:
						case 4234:
						case 4235:
						case 4507:
						case 4512:
						case 6215:
						case 6217:
						case 6219:
						case 6221:
						case 6223:
						case 6225:
						case 6227:
						case 6229:
						case 6231:
						case 6233:
						case 6235:
						case 6237:
						case 6239:
						case 6241:
						case 6243:
						case 6245:
						case 6247:
						case 6249:
						case 6251:
						case 6253:
						case 6255:
						case 6257:
						case 6259:
						case 6261:
						case 6263:
						case 6265:
						case 6267:
						case 6269:
						case 6271:
						case 6273:
						case 6275:
						case 6277:
						case 6279:
						case 6631:
						case 6633:
						case 6894:
						case 7332:
						case 7334:
						case 7336:
						case 7338:
						case 7340:
						case 7342:
						case 7344:
						case 7346:
						case 7348:
						case 7350:
						case 7352:
						case 7354:
						case 7356:
						case 7358:
						case 7360:
						case 7676:
						case 9731:
						case 10352:
						case 10665:
						case 10667:
						case 10669:
						case 10671:
						case 10673:
						case 10675:
						case 10677:
						case 10679:
						case 10827:
						case 11284:
						case 12908:
						case 12910:
						case 12912:
						case 12914:
						case 12916:
						case 12918:
						case 12920:
						case 12922:
						case 12924:
						case 12926:
						case 12928:
						case 12930:
						case 12932:
						case 12934:
						case 13506:
						case 13734:
						case 13736:
						case 13738:
						case 13740:
						case 13742:
						case 13744:
						case 13964:
						case 13966:
						case 14578:
						case 14579:
						case 15808:
						case 15809:
						case 15810:
						case 15811:
						case 15812:
						case 15813:
						case 15814:
						case 15815:
						case 15816:
						case 15817:
						case 15818:
						case 16079:
						case 16933:
						case 16934:
						case 16971:
						case 16972:
						case 17341:
						case 17342:
						case 17343:
						case 17344:
						case 17345:
						case 17346:
						case 17347:
						case 17348:
						case 17349:
						case 17351:
						case 17353:
						case 17355:
						case 17357:
						case 17359:
						case 17361:
						case 17405:
						case 18359:
						case 18360:
						case 18361:
						case 18362:
						case 18363:
						case 18364:
						case 18582:
						case 18584:
						case 18691:
						case 18709:
						case 18747:
						case 19340:
						case 19345:
						case 19352:
						case 19410:
						case 19426:
						case 19427:
						case 19440:
						case 19441:
						case 19442:
						case 19749:
								SoundSystem.sendSound(player, victim, 791, 0);
								player.soundSent = true;
								return 1156;

						case 4151:
						case 18767: // Abyssal tentacle.
						case 13444:
						case 14661:
						case 15441:
						case 15442:
						case 15443:
						case 15444:
						case 21369:
						case 21371:
						case 21372:
						case 21373:
						case 21374:
						case 21375:
						case 23691:
								SoundSystem.sendSound(player, victim, 791, 0);
								player.soundSent = true;
								return 11974;

						case 8844:
						case 8845:
						case 8846:
						case 8847:
						case 8848:
						case 8849:
						case 8850:
						case 15455:
						case 15456:
						case 15457:
						case 15458:
						case 15459:
						case 15825:
						case 17273:
						case 20072:
								SoundSystem.sendSound(player, victim, 791, 0);
								player.soundSent = true;
								return 4177;

						case 3095:
						case 3096:
						case 3097:
						case 3098:
						case 3099:
						case 3100:
						case 3101:
						case 6587:
						case 14484:
								return 397;

						case 1379:
						case 1381:
						case 1383:
						case 1385:
						case 1387:
						case 1389:
						case 1391:
						case 1393:
						case 1395:
						case 1397:
						case 1399:
						case 1401:
						case 1403:
						case 1405:
						case 1407:
						case 1409:
						case 2415:
						case 2416:
						case 2417:
						case 3053:
						case 3054:
						case 3055:
						case 3056:
						case 4170:
						case 4675:
						case 4710:
						case 4862:
						case 4863:
						case 4864:
						case 4865:
						case 4866:
						case 4867:
						case 6562:
						case 6603:
						case 6727:
						case 9084:
						case 9091:
						case 9092:
						case 9093:
						case 11736:
						case 11738:
						case 11739:
						case 11953:
						case 13406:
						case 13629:
						case 13630:
						case 13631:
						case 13632:
						case 13633:
						case 13634:
						case 13635:
						case 13636:
						case 13637:
						case 13638:
						case 13639:
						case 13640:
						case 13641:
						case 13642:
						case 6908:
						case 6910:
						case 6912:
						case 6914:
								return 415;
						// Boxing gloves
						case 7671:
						case 7673:
								return 3679;
						case 4153:
						case 6528:
								return 1666;
						case 1307:
						case 1309:
						case 1311:
						case 1313:
						case 1315:
						case 1317:
						case 1319:
						case 6609:
						case 7158:
						case 7407:
						case 16127:
						case 16128:
						case 16129:
						case 16130:
						case 16131:
						case 16132:
						case 16133:
						case 16134:
						case 16135:
						case 16136:
						case 16137:
						case 16889:
						case 16891:
						case 16893:
						case 16895:
						case 16897:
						case 16899:
						case 16901:
						case 16903:
						case 16905:
						case 16907:
						case 16909:
						case 16973:
						case 18369:
						case 20874:
						case 11694:
						case 11696:
						case 11698:
						case 11700:
						case 11730:
						case 18660: // Sara's blessed sword.
								return 13051;

						case 3190:
						case 3192:
						case 3194:
						case 3196:
						case 3198:
						case 3200:
						case 3202:
						case 3204:
						case 6599:
								return 12806;

						case 18353:
						case 18354:
								return 13054;

						case 15241:
								// Hand cannon.
								return 12156;

						case 4718:
								return 12004;

						case 10887:
								return 5866;

						case 4755:
								return 2063;
						case 11716:
								return 12008;
						case 15445:
								return 11974;

						default:
								SoundSystem.sendSound(player, victim, 816, 0);
								player.soundSent = true;
								return 424;
				}
		}

		/**
		 * Attack speed.
		 **/
		public static int getAttackTimerCount(Player player, String weaponName)
		{
				if (player.hasLastCastedMagic() && player.getSpellId() >= 0)
				{
						if (player.getSpellId() == 52)
						{
								return 4;
						}
						switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
						{
								case 12871:
										// Ice blitz
								case 13023:
										// Shadow barrage
								case 12891:
										// Ice barrage
										return 5;
						}
						return 5;
				}
				else if (weaponName.contains("hunters' crossbow"))
				{
						if (player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								return 5;
						}
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 4;
						}
						if (player.getCombatStyle(ServerConstants.AGGRESSIVE))
						{
								return 3;
						}
				}
				else if (weaponName.contains("twisted bow"))
				{
						if (player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								return 6;
						}
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 6;
						}
						if (player.getCombatStyle(ServerConstants.RAPID))
						{
								return 5;
						}
				}
				else if ((weaponName.contains("crossbow") || weaponName.contains("c'bow")) && !weaponName.contains("karil's crossbow"))
				{
						if (player.getCombatStyle(ServerConstants.LONG_RANGED))
						{
								return 6;
						}
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 6;
						}
						if (player.getCombatStyle(ServerConstants.RAPID))
						{
								return 5;
						}
				}
				else if (weaponName.contains("heavy ballista"))
				{
						if (player.getCombatStyle(ServerConstants.LONG_RANGED))
						{
								return 7;
						}
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 7;
						}
						if (player.getCombatStyle(ServerConstants.RAPID))
						{
								return 6;
						}
				}
				else if (weaponName.contains("bow") && !weaponName.contains("dark bow"))
				{
						if (player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								return 4;
						}
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 4;
						}
						if (player.getCombatStyle(ServerConstants.RAPID))
						{
								return 3;
						}
				}

				if (WeaponSpeed.matching(WeaponSpeed.SPEED_2_TICKS, weaponName))
				{
						if (player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 3;
						}
						else if (player.getCombatStyle(ServerConstants.LONG_RANGED))
						{
								return 3;
						}
						else
						{
								return 2;
						}
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_3_TICKS, weaponName))
				{
						int speed = 3;
						if (player.getCombatStyle(ServerConstants.ACCURATE) || player.getCombatStyle(ServerConstants.LONG_RANGED))
						{
								speed = 4;
						}
						if (player.getWieldedWeapon() == 18779)
						{
								if (player.getPlayerIdAttacking() > 0)
								{
								}
								else if (player.getNpcIdAttacking() > 0)
								{
										speed--;
								}
						}
						return speed;
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_7_TICKS, weaponName))
				{
						return 7;
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_5_TICKS, weaponName))
				{
						return 5;
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_6_TICKS, weaponName))
				{
						return 6;
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_4_TICKS, weaponName))
				{
						return 4;
				}
				if (WeaponSpeed.matching(WeaponSpeed.SPEED_8_TICKS, weaponName))
				{
						return 8;
				}
				return 5;
		}

		/**
		 * How many game ticks it takes for the hitsplat to be applied after animation is applied.
		 **/
		public static int getHitDelay(Player player, String weaponName)
		{
				weaponName = weaponName.toLowerCase();
				if (player.hasLastCastedMagic() && player.getSpellId() >= 0)
				{
						int distance = 0;
						Player target = PlayerHandler.players[player.getPlayerIdAttacking()];
						if (target != null)
						{
								int distanceFromTarget = player.getPA().distanceToPoint(target.getX(), target.getY());
								if (distanceFromTarget >= 3)
								{
										distance++;
								}
								if (distanceFromTarget >= 5)
								{
										distance++;
								}
								if (distanceFromTarget >= 8)
								{
										distance++;
								}
						}
						else
						{
								Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];

								if (npc != null)
								{
										// Trident of the swamp.
										if (player.getWieldedWeapon() == 18769)
										{
												return 4;
										}

										int distanceFromTarget = player.getPA().distanceToPoint(npc.getX(), npc.getY());
										if (distanceFromTarget <= 4)
										{
												distance++;
										}
										if (distanceFromTarget <= 7)
										{
												distance++;
										}
										if (distanceFromTarget >= 8)
										{
												distance++;
										}
								}
						}
						distance += 3;

						// Blitz.
						if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12871)
						{
								distance++;
						}
						return distance;
				}
				else
				{
						if (RangedData.isRangedItem(player, player.getWieldedWeapon()))
						{
								int distance = 0;
								// Heavy ballista
								if (player.getWieldedWeapon() == 18807)
								{
										return 5;
								}
								if (weaponName.equals("dark bow"))
								{
										return 5;
								}
								Player target = PlayerHandler.players[player.getPlayerIdAttacking()];
								if (target != null)
								{
										int distanceFromTarget = player.getPA().distanceToPoint(target.getX(), target.getY());
										if (distanceFromTarget >= 3)
										{
												distance++;
										}

								}
								else
								{
										Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
										if (npc != null)
										{
												int distanceFromTarget = player.getPA().distanceToPoint(npc.getX(), npc.getY());
												if (distanceFromTarget >= 3)
												{
														distance++;
												}
										}
								}
								return distance + 3;
						}
						else
						{
								return 1;
						}
				}
		}

		public static int getRequiredDistance(Player player)
		{
				return 1 + (MeleeData.usingHalberd(player) ? 1 : 0);
		}

		public static boolean usingBolts(Player player)
		{
				return player.playerEquipment[ServerConstants.ARROW_SLOT] >= 9130 && player.playerEquipment[ServerConstants.ARROW_SLOT] <= 9145 || player.playerEquipment[ServerConstants.ARROW_SLOT] >= 9230 && player.playerEquipment[ServerConstants.ARROW_SLOT] <= 9245;
		}

		public static boolean properBolts(Player player)
		{
				return player.playerEquipment[ServerConstants.ARROW_SLOT] >= 9140 && player.playerEquipment[ServerConstants.ARROW_SLOT] <= 9144 || player.playerEquipment[ServerConstants.ARROW_SLOT] >= 9240 && player.playerEquipment[ServerConstants.ARROW_SLOT] <= 9244 || player.playerEquipment[ServerConstants.ARROW_SLOT] >= 9337 && player.playerEquipment[ServerConstants.ARROW_SLOT] <= 9342;
		}

		public static int correctBowAndArrows(Player player)
		{
				if (usingBolts(player))
						return -1;
				if (player.getWieldedWeapon() == 839 || player.getWieldedWeapon() == 841)
				{
						return 884;
				}
				switch (player.getWieldedWeapon())
				{
						case 839:
						case 841:
								return 882;
						case 843:
						case 845:
								return 884;
						case 847:
						case 849:
								return 886;
						case 851:
						case 853:
								return 890;
						case 855:
						case 857:
								return 890;
						case 859:
						case 861:
						case 18659: // Magic shortbow (i).
								return 892;

						case 15241:
								return 15243;

						case 18807:
								return 18819;
						case 4734:
								// Karil's crossbow.
								return 4740;
						case 11235:
						case 15701:
						case 15702:
						case 15703:
						case 15704:
								return 11212;
				}
				return -1;
		}

		public static int getRangeStartGFX(Player player)
		{
				switch (player.getDroppedRangedWeaponUsed())
				{
						case 863:
								return 220;
						case 864:
								return 219;
						case 865:
								return 221;
						case 866:
								// knives
								return 223;
						case 867:
								return 224;
						case 868:
								return 225;
						case 869:
								return 222;
						case 806:
								return 232;
						case 807:
								return 233;
						case 808:
								return 234;
						case 809:
								// darts
								return 235;

						case 11230:
								return 237;
						case 810:
								return 236;
						case 811:
								return 237;
						case 825:
								return 206;
						case 826:
								return 207;
						case 827:
								// javelin
								return 208;
						case 828:
								return 209;
						case 829:
								return 210;
						case 830:
								return 211;
						case 800:
								return 42;
						case 801:
								return 43;
						case 802:
								return 44; // axes
						case 803:
								return 45;
						case 804:
								return 46;
						case 805:
								return 48;
						case 882:
								return 19;
						case 884:
								return 18;
						case 886:
								return 20;
						case 888:
								return 21;
						case 890:
								return 22;
						case 892:
								return 24;
						case 11212:
								return 26;
						case 4212:
						case 4214:
						case 4215:
						case 4216:
						case 4217:
						case 4218:
						case 4219:
						case 4220:
						case 4221:
						case 4222:
						case 4223:
								return 250;
				}
				return -1;
		}

		public static int getRangeProjectileGFX(Player player)
		{
				if (player.isUsingDarkBowSpecialAttack())
				{
						return 1099;
				}
				if (player.bowSpecShot > 0)
				{
						switch (player.getDroppedRangedWeaponUsed())
						{
								default:
										return 249;
						}
				}

				// Armadyl crossbow.
				if (player.getWieldedWeapon() == 18642 && player.armadylCrossBowSpecial)
				{
						return 2619;
				}
				if (Combat.getUsingCrossBow(player))
				{
						return 27;
				}
				if (player.getWieldedWeapon() == 13879)
				{
						return 1837;
				}
				if (player.getWieldedWeapon() == 13883)
				{
						return 1839;
				}
				switch (player.getDroppedRangedWeaponUsed())
				{
						case 15241:
								// Hand cannon.
						case 15243:
								// Hand cannon shot.
								return 2143;
						case 18819:// Heavy ballista.
								return 2622;
						case 863:
								return 213;
						case 864:
								return 212;
						case 865:
								return 214;
						case 866:
								// knives
								return 216;
						case 867:
								return 217;
						case 868:
								return 218;
						case 869:
								return 215;
						case 806:
								return 226;
						case 807:
								return 227;
						case 808:
								return 228;
						case 809:
								// darts
								return 229;

						case 11230:
						case 18779:
								return 231;
						case 810:
								return 230;
						case 811:
								return 231;
						case 825:
								return 200;
						case 826:
								return 201;
						case 827:
								// javelin
								return 202;
						case 828:
								return 203;
						case 829:
								return 204;
						case 830:
								return 205;
						case 6522:
								// Toktz-xil-ul
								return 442;
						case 800:
								return 36;
						case 801:
								return 35;
						case 802:
								return 37; // axes
						case 803:
								return 38;
						case 804:
								return 39;
						case 805:
								return 40;
						case 882:
								return 10;
						case 884:
								return 9;
						case 886:
								return 11;
						case 888:
								return 12;
						case 890:
								return 13;
						case 892:
								return 15;
						case 11212:
								return 17;
						case 4740:
								// bolt rack
								return 27;
				}
				return -1;
		}

		public static int getProjectileSpeed(Player player)
		{

				if (player.armadylCrossBowSpecial)
				{
						return 95;
				}
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("knife"))
				{
						return 55;
				}
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("dart"))
				{
						return 55;
				}
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("blowpipe"))
				{
						return 60;
				}
				if (player.isUsingDarkBowSpecialAttack())
				{
						return 100;
				}
				else if (player.isMagicBowSpecialAttack())
				{
						return 90;
				}
				else if (player.getWieldedWeapon() == 15241) // Hand cannon.
				{
						return 75;
				}
				else if (player.getWieldedWeapon() == 13883 || player.getWieldedWeapon() == 13879) // Morrigan's throwing axe and javelin.
				{
						return 70;
				}
				else if (Combat.getUsingCrossBow(player))
				{
						return 75;
				}
				else if (CombatConstants.isDarkBow(player.getWieldedWeapon()) && player.isUsingDarkBowNormalAttack()) // Dragon arrow.
				{
						return 75;
				}
				else if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("bow"))
				{
						return 70;
				}
				return 70;
		}

		public static boolean wearingStaff(Player player, int runeId)
		{
				int wep = player.getWieldedWeapon();
				switch (runeId)
				{
						case 554:
								if (wep == 1387)
										return true;
								break;
						case 555:
								if (wep == 1383)
										return true;
								break;
						case 556:
								if (wep == 1381)
										return true;
								break;
						case 557:
								if (wep == 1385)
										return true;
								break;
				}
				return false;
		}

		public static void addBarragesCasted(Player player)
		{
				if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12891)
				{
						player.barragesCasted++;
						Achievements.checkCompletionMultiple(player, "1076");
				}

				// Fire strike.
				else if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1158)
				{
						Achievements.checkCompletionSingle(player, 1019);
				}
		}

		public static boolean checkMagicRequirementsForNpcCombatAndMagicOnFloorItemPacket(Player player, int spell)
		{
				if (player.hasLastCastedMagic() && spell >= 0)
				{ // check for runes
						if (!MagicData.requiredRunes(player, spell, "CHECK REQUIREMENT"))
						{
								player.playerAssistant.sendMessage("You don't have the required runes to cast this spell.");
								return false;
						}
				}
				if (player.hasLastCastedMagic() && player.getPlayerIdAttacking() > 0)
				{
						if (PlayerHandler.players[player.getPlayerIdAttacking()] != null)
						{
								for (int r = 0; r < CombatConstants.REDUCE_SPELLS.length; r++)
								{ // reducing spells, confuse etc
										if (CombatConstants.REDUCE_SPELLS[r] == CombatConstants.MAGIC_SPELLS[spell][0])
										{
												player.reduceSpellId = r;
												if ((System.currentTimeMillis() - PlayerHandler.players[player.getPlayerIdAttacking()].reduceSpellDelay[player.reduceSpellId]) > CombatConstants.REDUCE_SPELL_TIME[player.reduceSpellId])
												{
														PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId] = true;
												}
												else
												{
														PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId] = false;
												}
												break;
										}
								}
								if (!PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId])
								{
										player.playerAssistant.sendMessage("That player is currently immune to this spell.");
										player.setUsingMagic(false);
										Movement.stopMovement(player);
										resetPlayerAttack(player);
										return false;
								}
						}
				}
				int staffRequired = getStaffNeeded(player);
				if (player.hasLastCastedMagic() && staffRequired > 0)
				{ // staff required
						if (player.getWieldedWeapon() != staffRequired)
						{
								player.playerAssistant.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
								return false;
						}
				}
				if (spell >= 0)
				{
						if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < CombatConstants.MAGIC_SPELLS[spell][1])
						{
								player.playerAssistant.sendMessage("You need to have a magic level of " + CombatConstants.MAGIC_SPELLS[spell][1] + " to cast this spell.");
								return false;
						}
				}
				if (player.hasLastCastedMagic())
				{
						if (spell == 25)
						{ // crumble undead
								for (int npc : ServerConstants.UNDEAD_NPCS)
								{
										if (NpcHandler.npcs[player.getNpcIdAttacking()].npcType != npc)
										{
												player.playerAssistant.sendMessage("You can only attack undead monsters with this spell.");
												Movement.stopMovement(player);
												AutoCast.resetAutocast(player);
												return false;
										}
								}
						}
				}


				return true;
		}

		public static long getFreezeTime(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getOldSpellId()][0])
				{
						case 1572:
						case 12861:
								// ice rush
								return 5000;
						case 1582:
						case 12881:
								// ice burst
								return 10000;
						case 1592:
						case 12871:
								// ice blitz
								return 15000;
						case 12891:
								// ice barrage
								return 20000;
						default:
								return 0;
				}
		}

		public static int getStartHeight(Player player)
		{

				if (player.getSpellId() == 52)
				{
						return 10;
				}
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 1562:
								// stun
								return 25;
						case 12939:
								// smoke rush
								return 35;
						case 12987:
								// shadow rush
								return 38;
						case 12861:
								// ice rush
								return 15;
						case 12951:
								// smoke blitz
								return 38;
						case 12999:
								// shadow blitz
								return 25;
						case 12911:
								// blood blitz
								return 25;
						default:
								return 43;
				}
		}

		public static int getEndHeight(Player player)
		{


				if (player.getSpellId() == 52)
				{
						return 7;
				}
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 1562:
								// stun
								return 10;
						case 12939:
								// smoke rush
								return 20;
						case 12987:
								// shadow rush
								return 28;
						case 12861:
								// ice rush
								return 10;
						case 12951:
								// smoke blitz
								return 28;
						case 12999:
								// shadow blitz
								return 15;
						case 12911:
								return 10; // blood blitz
						default:
								return 31;
				}
		}

		public static int getStartDelay(Player player)
		{
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("knife"))
				{
						return 40;
				}
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("blowpipe"))
				{
						return 40;
				}
				if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("dart"))
				{
						return 40;
				}
				if (player.isMagicBowSpecialAttack())
				{
						return 60;
				}
				else if (player.isUsingDarkBowNormalAttack() && CombatConstants.isDarkBow(player.getWieldedWeapon()))
				{
						return 49;
				}
				if (player.getSpellId() >= 0)
				{
						switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
						{
								case 1539:
										return 60;
								default:
										return 53;
						}
				}
				return 53;
		}

		public static int getStaffNeeded(Player player)
		{
				if (player.getSpellId() == -1)
				{
						return 0;
				}
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 1539:
								return 1409;
						case 12037:
								return 4170;
						case 1190:
								return 2415;
						case 1191:
								return 2416;
						case 1192:
								return 2417;
						default:
								return 0;
				}
		}

		public static boolean godSpells(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 1190:
								return true;
						case 1191:
								return true;
						case 1192:
								return true;
						default:
								return false;
				}
		}

		public static int getEndGfxHeight(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getOldSpellId()][0])
				{
						case 12987:
						case 12901:
						case 12861:
						case 12445:
						case 1192:
						case 13011:
						case 12919:
						case 12881:
						case 12999:
						case 12911:
						case 12871:
						case 13023:
						case 12929:
						case 12891:
								return 0;
						default:
								return 100;
				}
		}

		public static int getStartGfxHeight(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0])
				{
						case 12871:
						case 12891:
						case 12445:
								return 0;
						default:
								return 100;
				}
		}

		public static void handleDfs(final Player attacker)
		{
				final Player victim = PlayerHandler.players[attacker.getPlayerIdAttacking()];
				if (victim == null)
				{
						return;
				}
				if (victim.getDead())
				{
						return;
				}
				if (attacker.dragonFireShieldCharges == 0)
				{
						attacker.playerAssistant.sendMessage("Your shield has no charges left.");
						return;
				}
				attacker.setProjectileStage(2);
				if (System.currentTimeMillis() - attacker.dfsDelay > 120000)
				{
						int damage = Misc.random(25);
						int anti = Combat.antiFire(victim, false, false);
						if (anti >= 1)
						{
								damage = Misc.random(7);
						}
						final int damage1 = damage;
						attacker.startAnimation(6696);
						attacker.gfx0(1165);
						attacker.dragonFireShieldCharges -= 1;

						final int pX = attacker.getX();
						final int pY = attacker.getY();
						int oX = victim.getX();
						int oY = victim.getY();
						final int offX = (pY - oY) * -1;
						final int offY = (pX - oX) * -1;
						attacker.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 75, 1166, 37, 37, -victim.getPlayerId() - 1, 53, 16);
						attacker.dfsDelay = System.currentTimeMillis();
						ItemAssistant.calculateEquipmentBonuses(attacker);
						ItemAssistant.updateEquipmentBonusInterface(attacker);
						CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										if (victim != null)
										{
												Combat.createHitsplatOnPlayer(attacker, victim, damage1, ServerConstants.DRAGONFIRE_ATTACK, ServerConstants.NO_ICON);
												victim.currentCombatSkillLevel[ServerConstants.HITPOINTS] -= damage1;
												Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.HITPOINTS);
										}
										container.stop();
								}

								@Override
								public void stop()
								{
								}
						}, 2);


				}
				else
				{
						attacker.playerAssistant.sendMessage("My shield hasn't finished recharging yet.");
				}
		}

		public static void addCharge(Player player)
		{
				if (player.playerEquipment[ServerConstants.SHIELD_SLOT] != 11284)
				{
						return;
				}
				if (player.dragonFireShieldCharges == 50)
				{
						return;
				}

				player.dragonFireShieldCharges++;
				player.startAnimation(6695);
				player.gfx0(1164);
				player.playerAssistant.sendMessage("You absord the fire breath and charge your Dragonfire shield.");
				ItemAssistant.calculateEquipmentBonuses(player);
				ItemAssistant.updateEquipmentBonusInterface(player);
		}

		public static void handleGraniteMaulPlayer(Player attacker)
		{
				if (attacker.getWieldedWeapon() != 4153 && attacker.getWieldedWeapon() != 18662)
				{
						return;
				}

				attacker.setUsingRanged(false);
				attacker.setUsingMagic(false);
				attacker.setLastCastedMagic(false);
				ItemAssistant.calculateEquipmentBonuses(attacker);
				ItemAssistant.updateEquipmentBonusInterface(attacker);
				if (attacker.getPlayerIdAttacking() > 0)
				{
						BotContent.addBotDebug(attacker, "Here24.6");
						Player victim = PlayerHandler.players[attacker.getPlayerIdAttacking()];
						if (victim == null)
						{
								return;
						}
						if (attacker.getX() != victim.getX() && attacker.getY() != victim.getY())
						{
								return;
						}
						if (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), getRequiredDistance(attacker)))
						{
								BotContent.addBotDebug(attacker, "Here24.7");
								if (AttackPlayer.hasSubAttackRequirements(attacker, victim))
								{
										BotContent.addBotDebug(attacker, "Here24.8");
										if (checkSpecAmount(attacker, 4153))
										{
												attacker.setMeleeFollow(true);
												attacker.isGraniteMaulSpecial = true;
												BotContent.addBotDebug(attacker, "Here24.9");
												MeleeFormula.calculateMeleeDamage(attacker, victim, 1);
												Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, attacker.graniteMaulSpecialDamage);
												attacker.startAnimation(1667);
												attacker.gfx100(340);
												Combat.createHitsplatOnPlayer(attacker, victim, attacker.graniteMaulSpecialDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON);
												attacker.isGraniteMaulSpecial = false;
										}
								}
						}
				}
				else if (attacker.getNpcIdAttacking() > 0)
				{
						Npc npc = NpcHandler.npcs[attacker.getNpcIdAttacking()];
						if (npc.isDead)
						{
								return;
						}
						if (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npc.getX(), npc.getY(), getRequiredDistance(attacker) + NpcDefinition.getDefinitions()[npc.npcType].size))
						{
								if (checkSpecAmount(attacker, 4153))
								{
										attacker.isGraniteMaulSpecial = true;
										CombatNpc.calculateMeleeDamageOnNpc(attacker, npc, 1, false);
										attacker.startAnimation(1667);
										attacker.gfx100(340);
										npc.underAttack = true;
										CombatNpc.applyHitSplatOnNpc(attacker, npc, attacker.graniteMaulSpecialDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON, 1);
										attacker.npcDamageMaskTime = System.currentTimeMillis();
										Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, attacker.graniteMaulSpecialDamage);
										attacker.isGraniteMaulSpecial = false;
								}
						}
				}
				attacker.setUsingSpecial(false);
		}

		/**
		 *
		 * @return true, if player is using a Chaotic weapon.
		 */
		public static boolean usingChaotic(Player player)
		{
				int ID = player.getWieldedWeapon();

				if (ID >= 18349 && ID <= 18363)
				{
						return true;
				}

				return false;


		}

		public static boolean stopMovement(Player player, Player target, boolean follow)
		{
				if (player.playerAssistant.isOnTopOfTarget(target))
				{
						return false;
				}

				boolean unblocked = Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), target.getX(), target.getY(), player.getHeight(), 1, 1, true);
				// Outside getPlayerIdAttack() > 0 because that field gets reset instantly after magic is casted.
				if (player.hasLastCastedMagic() && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.MAGIC_FOLLOW_DISTANCE) && unblocked)
				{
						Movement.stopMovement(player);
						return true;
				}
				if (player.getPlayerIdAttacking() > 0 && unblocked)
				{
						if (player.isUsingMediumRangeRangedWeapon() || player.isUsingShortRangeRangedWeapon())
						{
								if (player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.getRangedWeaponDistance(player)))
								{
										Movement.stopMovement(player);
										return true;
								}
						}
						if (MeleeData.usingHalberd(player) && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.HALBERD_DISTANCE) && (player.getX() == target.getX() || player.getY() == target.getY()))
						{
								Movement.stopMovement(player);
								return true;
						}
				}

				// Snowball.
				if (player.getPlayerIdAttacking() > 0 && ItemAssistant.hasItemEquippedSlot(player, 10501, ServerConstants.WEAPON_SLOT))
				{
						if (player.playerAssistant.withinDistanceOfTargetPlayer(target, 8))
						{
								return true;
						}
				}
				return false;

		}

		/**
		 * This is to fix the issue where you can stand on a player and attack, then walk on the player again and the
		 * player movement won't update.
		 * @param player
		 * 			The associated player.
		 */
		public static void preMovementCombatFix(Player player)
		{
				if ((player.getPlayerIdAttacking() > 0) && player.getAttackTimer() <= 1)
				{
						Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
						if (victim == null)
						{
								return;
						}
						if (player.playerAssistant.isOnTopOfTarget(victim))
						{
								Movement.movePlayerFromUnderEntity(player);
						}
				}

		}
}