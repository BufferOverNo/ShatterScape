package game.content.combat.vsnpc;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.CombatInterface;
import game.content.combat.SpecialAttack;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.magic.MagicData;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.melee.MeleeAttack;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.content.combat.vsplayer.range.RangedData;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.minigame.TargetSystem;
import game.content.minigame.WarriorsGuild;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Player vs Npc combat.
 * @author MGT Madness, Edited heavily on 02-11-2015 to make the Xp drops appear before hitsplat.
 */
public class CombatNpc
{

		public static void applyPoisonOnNpc(Player player, Npc npc, int poisonDamage)
		{

				if (poisonDamage > npc.poisonDamage)
				{
						npc.poisonDamage = poisonDamage;
						npc.poisonHitsplatsLeft = 4;
				}
				if (npc.poisonEvent)
				{
						return;
				}
				npc.poisonDamage = poisonDamage;
				npc.poisonHitsplatsLeft = 3;
				npc.poisonTicksUntillDamage = 100;
				CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								if (player == null)
								{
										CombatNpc.applyHitSplatOnNpcNoPlayerRelation(npc, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
								}
								else
								{
										npc.underAttack = true;
										CombatNpc.applyHitSplatOnNpc(player, npc, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
								}
						}
				}, 1);
				npc.poisonEvent = true;

				CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (npc.isDead)
								{
										container.stop();
										return;
								}
								if (npc.poisonDamage == 0)
								{
										container.stop();
										return;
								}
								int damage = npc.poisonDamage;

								npc.poisonTicksUntillDamage--;
								if (npc.poisonTicksUntillDamage == 0)
								{
										if (player == null)
										{
												CombatNpc.applyHitSplatOnNpcNoPlayerRelation(npc, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
										}
										else
										{
												CombatNpc.applyHitSplatOnNpc(player, npc, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
										}
										npc.poisonHitsplatsLeft--;
										npc.poisonTicksUntillDamage = 100;

										if (npc.poisonHitsplatsLeft == 0)
										{
												if (npc.poisonDamage == 1)
												{
														container.stop();
														return;
												}
												else
												{

														npc.poisonDamage--;
														npc.poisonHitsplatsLeft = 4;
												}
										}
								}



						}

						@Override
						public void stop()
						{
								npc.poisonEvent = false;
								npc.poisonDamage = 0;
						}
				}, 1);

		}

		public static void calculateMagicDamageOnNpc(Player attacker, Npc npc)
		{
				// Ice Blitz on Battle mage.
				if (NpcHandler.isBattleMageNpc(npc.npcType) && CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12871)
				{
						Achievements.checkCompletionMultiple(attacker, "1027");
				}
				int maximumDamage = MagicFormula.getMagicMaximumDamage(attacker);
				boolean task = false;
				for (int i = 0; i < Slayer.slayerTasks.length; i++)
				{
						if (npc.npcType == Slayer.slayerTasks[i][0])
						{
								task = true;
								break;
						}
				}
				if (task)
				{
						if (ItemAssistant.hasItemEquipped(attacker, 14637))
						{
								maximumDamage *= 1.15;
						}
				}
				int damage = Misc.random(maximumDamage);
				attacker.setMaximumDamageMagic(maximumDamage);
				if (Combat.godSpells(attacker))
				{
						damage += Misc.random(10);
				}
				boolean magicFailed = false;
				int bonusAttack = CombatNpc.getBonusAttack(attacker, npc);
				if (Misc.random(npc.npcType == 2883 ? (NpcDefinition.getDefinitions()[npc.npcType].meleeDefence / 2) : NpcDefinition.getMagicDefence(npc.npcType)) > 10 + Misc.random(MagicFormula.getMagicAttackAdvantage(attacker)) + bonusAttack && npc.npcType != 9463)
				{
						damage = 0;
						magicFailed = true;
				}
				else if (npc.npcType == 2881 || npc.npcType == 2882)
				{
						damage = 0;
						magicFailed = true;
				}
				if (Misc.random((NpcDefinition.getDefinitions()[npc.npcType].meleeDefence / 6)) > 10 + Misc.random(MagicFormula.getMagicAttackAdvantage(attacker)) + bonusAttack && npc.npcType == 9463)
				{
						damage = 0;
						magicFailed = true;
				}
				attacker.setOldSpellId(attacker.getSpellId());
				damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);


				if (npc.npcType == 8350)
				{
						damage *= 0.4;
				}
				if (!magicFailed)
				{
						long freezeDelay = Combat.getFreezeTime(attacker); // freeze
						if (freezeDelay > 0 && npc.canBeFrozen())
						{
								npc.setFrozenLength(freezeDelay);
						}
						switch (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0])
						{
								case 12901:
								case 12919:
										// blood spells
								case 12911:
								case 12929:
										int heal = Misc.random(damage / 2);
										attacker.addToHitPoints(heal);
										break;
						}
				}

				attacker.setMagicDamage(damage);
				attacker.setMagicSplash(magicFailed);


				Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damage);
		}

		private static void appendMultiBarrageNPC(Player attacker, int npcId, boolean splashed)
		{
				if (NpcHandler.npcs[npcId] != null)
				{
						Npc n = NpcHandler.npcs[npcId];
						if (n.isDead || n.currentHitPoints <= 0)
						{
								return;
						}
						if (checkMultiBarrageReqsNPC(npcId))
						{
								attacker.barrageCount++;
								if (Misc.random(NpcDefinition.getMagicDefence(NpcHandler.npcs[npcId].npcType)) < (10 + Misc.random(MagicFormula.getMagicAttackAdvantage(attacker))))
								{
										if (Combat.getEndGfxHeight(attacker) == 100)
										{
												n.gfx100(CombatConstants.MAGIC_SPELLS[attacker.oldSpellId][5]);
										}
										else
										{
												n.gfx0(CombatConstants.MAGIC_SPELLS[attacker.oldSpellId][5]);
										}
										int damage = Misc.random(attacker.getMagicDamage());
										if (n.currentHitPoints - damage < 0)
										{
												damage = n.currentHitPoints;
										}
										CombatNpc.applyHitSplatOnNpc(attacker, n, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON, 1);
										attacker.setSpellId(attacker.oldSpellId);
										Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damage);
										n.underAttackBy = attacker.getPlayerId();
										n.underAttack = true;
										multiSpellEffectNPC(attacker, npcId, damage);
								}
								else
										n.gfx100(85);
						}
				}

		}

		public static boolean multiMagicSpell(Player player)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.oldSpellId][0])
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

		public static void calculateRangedDamageOnNpc(Player attacker, Npc npc, int damageType)
		{
				int rangedAttack = RangedFormula.getInvisibleRangedAttackAdvantage(attacker);
				int maximum = RangedFormula.getRangedMaximumDamage(attacker);

				boolean task = false;
				for (int i = 0; i < Slayer.slayerTasks.length; i++)
				{
						if (npc.npcType == Slayer.slayerTasks[i][0])
						{
								task = true;
								break;
						}
				}
				if (task)
				{
						if (ItemAssistant.hasItemEquipped(attacker, 14637))
						{
								maximum *= 1.15;
						}
				}

				// Dragon hunter crossbow.
				if (attacker.getWieldedWeapon() == 18836 && NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase().contains("dragon"))
				{
						rangedAttack *= 1.1;
						maximum *= 1.1;
				}

				// Twisted bow.
				if (attacker.getWieldedWeapon() == 18830)
				{
						double damageMultiplier = 0.0;
						double attackMultiplier = 0.0;
						switch (NpcDefinition.getDefinitions()[npc.npcType].name)
						{
								case "Cerberus":
										damageMultiplier = 2.0;
										break;
								case "Infernal Mage":
										damageMultiplier = 1.2;
										break;
								case "Balfrug Kreeyath":
										damageMultiplier = 1.6;
										break;
								case "Tormented demon":
										damageMultiplier = 1.7;
										break;
								case "General Graardor":
										damageMultiplier = 1.4;
										break;
								case "Corporeal Beast":
										damageMultiplier = 2.4;
										break;
								case "Venenatis":
										damageMultiplier = 1.6;
										break;
								case "Kree'arra":
										damageMultiplier = 1.8;
										break;
								case "Wingman Skree":
										damageMultiplier = 1.6;
										break;
								case "Commander Zilyana":
										damageMultiplier = 2.3;
										break;
								case "Growler":
										damageMultiplier = 1.6;
										break;
								case "Sergeant Steelwill":
										damageMultiplier = 1.6;
										break;
								case "K'ril Tsutsaroth":
										damageMultiplier = 1.9;
										break;
								case "Ahrim":
										damageMultiplier = 1.3;
										break;
								case "Dagannoth Prime":
										damageMultiplier = 2.1;
										break;
								case "Chaos elemental":
										damageMultiplier = 2.2;
										break;
								case "TzTok-Jad":
										damageMultiplier = 2.5;
										break;
						}
						if (damageMultiplier > 1.0)
						{
								damageMultiplier += 0.2;
								maximum *= damageMultiplier;
								attackMultiplier = damageMultiplier;
								if (maximum > 250)
								{
										maximum = 250;
								}
						}
						if (attackMultiplier > 1.0)
						{
								rangedAttack *= (attackMultiplier + 0.50);
						}
				}
				int damage = Misc.random(1, maximum);
				attacker.maximumDamageRanged = maximum;
				boolean ignoreDef = false;

				if (Misc.random(NpcDefinition.getRangedDefence(npc.npcType)) > Misc.random(rangedAttack) && !ignoreDef)
				{
						damage = 0;
				}
				else if (npc.npcType == 2881 || npc.npcType == 2883 && !ignoreDef)
				{
						damage = 0;
				}
				if (npc.npcType == 8133)
				{
						damage /= 2;
				}
				if (attacker.hit1)
				{
						damage = 999;
				}
				damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);
				if (npc.npcType == 8351)
				{
						damage *= 0.4;
				}


				if (Misc.random(9) == 1 && attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9243)
				{

						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										ignoreDef = true;
										npc.gfx0(758);
								}
						}
				}
				if (Misc.random(8) == 1 && attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9242 && damage > 0 && npc.npcType != 8133)
				{
						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										if (attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > (attacker.getBaseHitPointsLevel() / 10))
										{
												npc.gfx0(754);
												damage = npc.currentHitPoints / 5;
												attacker.dealDamage(attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 10);
												attacker.gfx0(754);
												attacker.subtractFromHitPoints(attacker.getBaseHitPointsLevel() / 10);
												Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.HITPOINTS);
										}
								}
						}
				}
				if (attacker.isUsingDarkBowSpecialAttack())
				{
						if (damage < 8)
						{
								damage = 8;
						}
				}
				if (damage > 0 && Misc.random(9) == 1 && attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9244)
				{


						if (RangedData.hasCrossBowEquipped(attacker))
						{
								if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon()))
								{
										damage *= 1.55;
										npc.gfx0(756);
										attacker.specialAttackWeaponUsed[29] = 1;
										attacker.setWeaponAmountUsed(29);
								}
						}
				}
				if (attacker.blowpipeSpecialAttack)
				{
						attacker.addToHitPoints(damage / 2);
						attacker.blowpipeSpecialAttack = false;
				}
				if (damageType == 1)
				{
						attacker.rangedFirstDamage = damage;
				}
				else
				{
						attacker.rangedSecondDamage = damage;
				}
				Combat.addCombatExperience(attacker, ServerConstants.RANGED_ICON, damage);
		}

		private static void applyDragonClawsDamageOnNpc(final Player attacker, final Npc npc)
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
								if (npc.currentHitPoints - attacker.meleeThirdDamage < 0)
								{
										attacker.meleeThirdDamage = npc.currentHitPoints;
								}

								CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 3, attacker.meleeThirdDamage);

								if (npc.currentHitPoints - attacker.meleeFourthDamage < 0)
								{
										attacker.meleeFourthDamage = npc.currentHitPoints;
								}
								CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 4, attacker.meleeFourthDamage);
								attacker.setUsingDragonClawsSpecialAttackEvent(false);
						}
				}, 1);
		}

		public static void calculateMeleeDamageOnNpc(Player attacker, Npc npc, int damageType, boolean clawsFormula)
		{
				int damage = 0;
				MeleeAttack.saveCriticalDamage(attacker);
				int maximumDamage = MeleeFormula.getMaximumMeleeDamage(attacker);
				boolean task = false;
				for (int i = 0; i < Slayer.slayerTasks.length; i++)
				{
						if (npc.npcType == Slayer.slayerTasks[i][0])
						{
								task = true;
								break;
						}
				}
				if (task)
				{
						if (ItemAssistant.hasItemEquipped(attacker, 13263))
						{
								maximumDamage *= 1.15;
						}
						else if (ItemAssistant.hasItemEquipped(attacker, 14637))
						{
								maximumDamage *= 1.15;
						}
				}
				damage = Misc.random(1, maximumDamage);

				int attackAdvantage = MeleeFormula.getInvisibleMeleeAttackAdvantage(attacker);
				if (task)
				{
						if (ItemAssistant.hasItemEquipped(attacker, 13263))
						{
								attackAdvantage *= 1.15;
						}
						else if (ItemAssistant.hasItemEquipped(attacker, 14637))
						{
								attackAdvantage *= 1.15;
						}
				}
				if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].meleeDefence) > Misc.random(attackAdvantage))
				{
						damage = 0;
				}

				if (npc.npcType == 2882 || npc.npcType == 2883)
				{
						damage = 0;
				}

				if (npc.npcType == 8133)
				{
						if (!Combat.usingSpear(attacker))
						{
								damage /= 3;
						}
				}

				if (attacker.hit1)
				{
						damage = 999;
				}

				damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);
				if (npc.npcType == 8349 && !Combat.wearingFullVerac(attacker))
				{
						damage *= 0.4;
				}
				if (npc.tormentedDemonShield && (npc.npcType == 8349 || npc.npcType == 8350 || npc.npcType == 8351))
				{
						if (ItemAssistant.hasItemEquipped(attacker, 6746))
						{
								npc.tormentedDemonShield = false;
								npc.tormentedDemonTimeWeakened = System.currentTimeMillis();
								attacker.getPA().sendMessage("The shield of the demon has been demolished.");
						}
				}
				if (damageType == 2)
				{
						if (attacker.getMultipleDamageSpecialAttack())
						{
								if (attacker.saradominSwordSpecialAttack)
								{
										damage = Misc.random(damage);
										if (damage > 18)
										{
												damage = 18;
										}
								}
								attacker.meleeSecondDamage = damage;
								if (!clawsFormula)
								{
										Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
								}
						}
				}
				else if (damageType == 1)
				{
						if (attacker.isGraniteMaulSpecial)
						{
								attacker.graniteMaulSpecialDamage = damage;
								attacker.isGraniteMaulSpecial = false;
						}
						else
						{
								attacker.meleeFirstDamage = damage;
						}
						if (!clawsFormula)
						{
								Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
						}
				}
				else if (damageType == 3)
				{
						attacker.meleeThirdDamage = damage;
						if (!clawsFormula)
						{
								Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
						}
				}
		}

		private static int tormentedDemonShield(Player attacker, int damage, int npcType, boolean shield)
		{
				if (npcType != 8349 && npcType != 8350 && npcType != 8351)
				{
						return damage;
				}
				/*
				if (shield)
				{
						attacker.getPA().sendMessage("Your damage has been reduced due to the Tormented Demon's shield.");
						damage *= 0.7;
				}
				*/
				return damage;
		}

		private static void calculateDragonClawsOnNpc(Player attacker, Npc npc)
		{
				if (!attacker.getDragonClawsSpecialAttack())
				{
						return;
				}
				calculateMeleeDamageOnNpc(attacker, npc, 1, true);
				int damage1 = attacker.meleeFirstDamage;
				int damage2 = 0;
				int damage3 = 0;
				int damage4 = 0;

				damage1 = tormentedDemonShield(attacker, damage1, npc.npcType, npc.tormentedDemonShield);
				if (npc.npcType == 8349)
				{
						damage1 *= 0.4;
				}

				/* Start of First result. */
				if (damage1 > 0)
				{
						damage2 = damage1 / 2;
						damage3 = damage2 / 2;
						damage4 = damage3;
				}
				else
				{
						calculateMeleeDamageOnNpc(attacker, npc, 1, true);
						damage1 = attacker.meleeFirstDamage;
						damage2 = damage1 / 2;
						damage3 = damage2 / 2;
						damage4 = damage3;
				} /* End of First result. */
				if (damage1 == 0)
				{
						calculateMeleeDamageOnNpc(attacker, npc, 3, true);
						damage3 = attacker.meleeThirdDamage;
						damage4 = damage3;
				}
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, (damage1 + damage2 + damage3 + damage4));
				attacker.meleeSecondDamage = damage2;
				attacker.meleeThirdDamage = damage3;
				attacker.meleeFourthDamage = damage4;
		}

		public static void attackNpc(Player attacker, Npc npc)
		{
				if (npc == null)
				{
						Combat.resetPlayerAttack(attacker);
						return;
				}
				if (attacker.doingAnAction())
				{
						Combat.resetPlayerAttack(attacker);
						return;
				}

				if (attacker.getHeight() != npc.getHeight())
				{
						Combat.resetPlayerAttack(attacker);
						return;
				}
				Combat.resetSpecialAttackData(attacker);
				AttackPlayer.resetAttackData(attacker);

				if (npc.isDead || npc.maximumHitPoints <= 0)
				{
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						Combat.resetPlayerAttack(attacker);
						return;
				}
				if (attacker.getDead())
				{
						Combat.resetPlayerAttack(attacker);
						return;
				}

				if (!WarriorsGuild.canAttackCyclops(attacker, npc.npcType))
				{
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						Combat.resetPlayerAttack(attacker);
						return;
				}

				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();

				if (npc.underAttackBy > 0 && npc.underAttackBy != attacker.getPlayerId() && !Area.inMulti(npcX, npcY))
				{
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						attacker.playerAssistant.sendMessage("This monster is already in combat.");
						Combat.resetPlayerAttack(attacker);
						return;
				}
				if (!Area.inMulti(attacker.getX(), attacker.getY()) && Combat.wasUnderAttackByAnotherPlayer(attacker, 5000))
				{
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						attacker.playerAssistant.sendMessage("I am already under attack.");
						Combat.resetPlayerAttack(attacker);
						return;
				}

				if ((attacker.getUnderAttackBy() > 0 || attacker.getNpcIndexAttackingPlayer() > 0 && Combat.wasAttackedByNpc(attacker)) && attacker.getNpcIndexAttackingPlayer() != npc.npcIndex && !Area.inMulti(attacker.getX(), attacker.getY()))
				{
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						attacker.playerAssistant.sendMessage("I am already under attack.");
						Combat.resetPlayerAttack(attacker);
						return;
				}
				for (int i = 0; i < Slayer.slayerTasks.length; i++)
				{
						if (npc.npcType == Slayer.slayerTasks[i][0] && Slayer.slayerTasks[i][3] == 1)
						{
								if (attacker.baseSkillLevel[ServerConstants.SLAYER] < Slayer.slayerTasks[i][1])
								{
										attacker.playerAssistant.sendMessage("You need " + Slayer.slayerTasks[i][1] + " slayer to attack this monster.");
										Movement.stopMovement(attacker);
										attacker.turnPlayerTo(npc.getX(), npc.getY());
										Combat.resetPlayerAttack(attacker);
										return;
								}
						}
				}
				if (npc.npcType == 4045 && (attacker.slayerTaskNpcType != 49 && attacker.slayerTaskNpcType != 4045) || npc.npcType == 4045 && attacker.slayerTaskNpcAmount <= 0)
				{
						attacker.playerAssistant.sendMessage("You need a Cerberus or Hellhound slayer assignment to kill Cerberus.");
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						Combat.resetPlayerAttack(attacker);
						return;
				}
				if (npc.npcType == 1 && npcX >= 2985 && npcX <= 3000 && npcY >= 3360 && npcY <= 3380)
				{
						attacker.playerAssistant.sendMessage("You cannot attack this npc.");
						Combat.resetPlayerAttack(attacker);
						Movement.stopMovement(attacker);
						attacker.turnPlayerTo(npc.getX(), npc.getY());
						return;
				}
				attacker.doubleHit = false;
				attacker.resetPlayerIdToFollow();
				attacker.setNpcIdToFollow(npc.npcIndex);
				if (attacker.getAttackTimer() <= 0)
				{
						boolean usingBow = false;
						boolean usingOtherRangeWeapons = false;
						boolean usingCross = false;
						boolean usingMagic = attacker.hasLastCastedMagic();
						if (!usingMagic)
						{
								usingCross = Combat.getUsingCrossBow(attacker);
						}
						GameTimeSpent.increaseGameTime(attacker, GameTimeSpent.PVM);
						if (attacker.isInZombiesMinigame())
						{
								attacker.lastActivity = "ZOMBIES";
								attacker.lastActivityTime = System.currentTimeMillis();
						}
						attacker.bonusAttack = 0;
						attacker.setDroppedRangedItemUsed(0);
						attacker.setProjectileStage(0);

						if (attacker.getAutoCasting())
						{
								if (attacker.getAutocastId() != 52)
								{
										if (Combat.spellbookPacketAbuse(attacker, attacker.getAutocastId()))
										{
												Combat.resetPlayerAttack(attacker);
												return;
										}
								}
								attacker.setSpellId(attacker.getAutocastId());
								attacker.setUsingMagic(true);
								attacker.setLastCastedMagic(true);
								usingMagic = true;
						}
						else if (attacker.getWieldedWeapon() == 18769 && attacker.getSpellId() == -1)
						{
								attacker.setSpellId(52);
								attacker.setAutocastId(52);
								attacker.setAutoCasting(true);
								attacker.setUsingMagic(true);
								attacker.setLastCastedMagic(true);
								attacker.getPA().sendFrame36(43, -1);
								attacker.getPA().sendFrame36(108, 1); // Autocast on combat interface.
								usingMagic = true;
						}
						attacker.setSpecialAttackAccuracyMultiplier(1.0);
						attacker.specDamage = 1.0;
						if (!usingMagic)
						{
								if (RangedData.isWieldingShortRangeRangedWeapon(attacker))
								{
										usingOtherRangeWeapons = true;
										attacker.setMeleeFollow(false);
								}
						}
						else
						{
								attacker.setMeleeFollow(false);
						}
						attacker.setUsingMediumRangeRangedWeapon(false);
						RangedData.isWieldingMediumRangeRangedWeapon(attacker);
						if (!usingMagic)
						{
								if (attacker.isUsingMediumRangeRangedWeapon())
								{
										usingBow = true;
								}
						}
						if (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && (usingBow || usingMagic))
						{
								Movement.stopMovement(attacker);
						}
						if (CombatNpc.isArmadylNpc(npc.npcIndex) && !usingCross && !usingBow && !usingMagic && !Combat.usingCrystalBow(attacker) && !usingOtherRangeWeapons)
						{
								attacker.playerAssistant.sendMessage("You can only use range against this.");
								Combat.resetPlayerAttack(attacker);
								return;
						}
						if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, 2) && MeleeData.usingHalberd(attacker) && !usingOtherRangeWeapons && !usingBow && !usingMagic || !attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && usingOtherRangeWeapons && !usingBow && !usingMagic || !attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && usingBow)
						{
								return;
						}
						if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.MAGIC_FOLLOW_DISTANCE) && usingMagic)
						{
								return;
						}
						if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, NpcDefinition.getDefinitions()[npc.npcType].size) && (!usingOtherRangeWeapons && !MeleeData.usingHalberd(attacker) && !usingBow && !usingMagic))
						{
								return;
						}

						boolean hasArrowEquipped = attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= 0 ? false : true;
						if (usingBow || usingOtherRangeWeapons)
						{
								if (!RangedData.hasRequiredAmmo(attacker, hasArrowEquipped))
								{
										Movement.stopMovement(attacker);
										Combat.resetPlayerAttack(attacker);
										attacker.turnPlayerTo(npc.getX(), npc.getY());
										return;
								}
								if (npc.npcType >= 912 && npc.npcType <= 914)
								{
										attacker.getPA().sendMessage("You can only use magic spells on this npc.");
										Movement.stopMovement(attacker);
										attacker.resetNpcIdentityAttacking();
										attacker.turnPlayerTo(npc.getX(), npc.getY());
										Combat.resetPlayerAttack(attacker);
										return;
								}
						}
						else if (!usingMagic)
						{
								if (npc.npcType >= 912 && npc.npcType <= 914)
								{
										attacker.getPA().sendMessage("You can only use magic spells on this npc.");
										Movement.stopMovement(attacker);
										attacker.resetNpcIdentityAttacking();
										attacker.turnPlayerTo(npc.getX(), npc.getY());
										Combat.resetPlayerAttack(attacker);
										return;
								}
						}

						if (usingBow || usingMagic || usingOtherRangeWeapons || (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, 2) && MeleeData.usingHalberd(attacker)))
						{
								Movement.stopMovement(attacker);
						}
						if (attacker.getSpellId() >= 0)
						{
								if (!Combat.checkMagicRequirementsForNpcCombatAndMagicOnFloorItemPacket(attacker, attacker.getSpellId()))
								{
										Movement.stopMovement(attacker);
										attacker.resetNpcIdentityAttacking();
										attacker.turnPlayerTo(npc.getX(), npc.getY());
										return;
								}
						}
						if (attacker.isMeleeFollow() && attacker.getX() != npc.getX() && attacker.getY() != npc.getY() && Follow.isBigNpc(npc.npcType) == 0)
						{
								if (attacker.isFrozen())
								{
										Combat.resetPlayerAttack(attacker);
										attacker.turnPlayerTo(npc.getX(), npc.getY());
								}
								return;
						}
						if (attacker.isMeleeFollow())
						{
								if (!Region.isStraightPathUnblocked(attacker.getX(), attacker.getY(), npc.getVisualX(), npc.getVisualY(), attacker.getHeight(), 1, 1))
								{
										return;
								}
						}
						else
						{
								if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), npc.getVisualX(), npc.getVisualY(), attacker.getHeight(), 1, 1, true))
								{
										return;
								}
						}



						TargetSystem.doingWildActivity(attacker);
						attacker.againstPlayer = false;
						attacker.rangedSpecialAttackOnNpc = false;
						SpecialAttackTracker.resetSpecialAttackWeaponUsed(attacker);
						// Attacking begins.
						attacker.setAttackTimer(Combat.getAttackTimerCount(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
						attacker.playerInNpcCombat = System.currentTimeMillis();
						Barrows.startBarrowsTimer(attacker, npc);
						attacker.faceUpdate(npc.npcIndex);
						npc.underAttackBy = attacker.getPlayerId();
						npc.lastDamageTaken = System.currentTimeMillis();
						if (attacker.isUsingSpecial() && !usingMagic)
						{
								if (Combat.checkSpecAmount(attacker, attacker.getWieldedWeapon()))
								{
										attacker.setLastRangedWeaponUsed(attacker.getWieldedWeapon());
										SpecialAttack.activateSpecial(attacker, attacker.getWieldedWeapon(), npc.npcIndex);
										if (attacker.rangedSpecialAttackOnNpc)
										{
												calculateRangedDamageOnNpc(attacker, npc, 1);
												calculateRangedDamageOnNpc(attacker, npc, 2);
										}
										else
										{
												if (attacker.getDragonClawsSpecialAttack())
												{
														calculateDragonClawsOnNpc(attacker, npc);
												}
												else
												{
														calculateMeleeDamageOnNpc(attacker, npc, 1, false);
														calculateMeleeDamageOnNpc(attacker, npc, 2, false);
												}
										}
										return;
								}
								else
								{
										attacker.playerAssistant.sendMessage("You don't have the required special energy to use this attack.");
										attacker.setUsingSpecial(false);
										CombatInterface.updateSpecialBar(attacker);
										attacker.resetNpcIdentityAttacking();
										return;
								}
						}
						if (usingMagic && attacker.getSpellId() >= 0)
						{
								attacker.startAnimation(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][2]);
								Combat.addBarragesCasted(attacker);
								attacker.setUsingMagic(true);
								calculateMagicDamageOnNpc(attacker, npc);
								MagicData.requiredRunes(attacker, attacker.getSpellId(), "DELETE RUNES");
						}

						attacker.setLastRangedWeaponUsed(attacker.getWieldedWeapon());
						if (!usingBow && !usingMagic && !usingOtherRangeWeapons)
						{
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
								attacker.setProjectileStage(0);
								attacker.setOldNpcIndex(npc.npcIndex);
								calculateMeleeDamageOnNpc(attacker, npc, 1, false);

								attacker.startAnimation(MeleeData.getWeaponAnimation(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
						}
						if (usingBow && !usingOtherRangeWeapons && !usingMagic || usingCross)
						{ // range hit delay
								if (usingCross)
								{
										attacker.setUsingMediumRangeRangedWeapon(true);
								}
								attacker.setLastRangedWeaponUsed(attacker.getWieldedWeapon());
								attacker.gfx100(Combat.getRangeStartGFX(attacker));
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
								attacker.setProjectileStage(1);
								attacker.setOldNpcIndex(npc.npcIndex);
								if (attacker.getWieldedWeapon() >= 4212 && attacker.getWieldedWeapon() <= 4223)
								{
										attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
								}
								else
								{
										attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
										RangedAmmoUsed.deleteAmmo(attacker);
										if (attacker.getWieldedWeapon() == 11235)
										{
												RangedAmmoUsed.deleteAmmo(attacker);
										}
								}
								CombatNpc.fireProjectileNpc(attacker);
								calculateRangedDamageOnNpc(attacker, npc, 1);
								if (Combat.usingDbow(attacker))
								{
										attacker.setUsingDarkBowNormalAttack(true);
										calculateRangedDamageOnNpc(attacker, npc, 2);
								}
								attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
						}
						else if (usingOtherRangeWeapons && !usingMagic && !usingBow)
						{ // knives, darts, etc hit delay
								attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.gfx100(Combat.getRangeStartGFX(attacker));
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
								attacker.setProjectileStage(1);
								attacker.setOldNpcIndex(npc.npcIndex);
								CombatNpc.fireProjectileNpc(attacker);
								calculateRangedDamageOnNpc(attacker, npc, 1);
								attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
						}
						if (usingMagic && attacker.getSpellId() >= 0)
						{ // magic hit delay
								int pX = attacker.getX();
								int pY = attacker.getY();
								int nX = npc.getVisualX();
								int nY = npc.getVisualY();
								int offX = (pY - nY) * -1;
								int offY = (pX - nX) * -1;
								attacker.setLastCastedMagic(true);
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
										attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78, CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4], Combat.getStartHeight(attacker), Combat.getEndHeight(attacker), npc.npcIndex + 1, 50, Combat.getProjectileSlope(attacker));
								}
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
								attacker.setOldNpcIndex(npc.npcIndex);
								attacker.setOldSpellId(attacker.getSpellId());
								attacker.setSpellId(-1);
								if (!attacker.getAutoCasting())
								{
										attacker.resetNpcIdentityAttacking();
								}
						}
				}
		}

		public static void applyHitSplatOnNPC(final Player attacker, final Npc npc)
		{ // npc hit delay
				if (npc != null)
				{
						if (npc.isDead)
						{
								if (npc.npcIndex == attacker.getNpcIdAttacking())
								{
										attacker.resetNpcIdentityAttacking();
								}
								attacker.setUsingDarkBowSpecialAttack(false);
								return;
						}
						npc.facePlayer(attacker.getPlayerId());
						if (npc.underAttackBy > 0)
						{
								npc.setKillerId(attacker.getPlayerId());
						}
						attacker.lastNpcAttackedIndex = npc.npcIndex;

						if (attacker.getProjectileStage() == 0)
						{ // melee hit damage

								// Dragon dagger p++ and Abyssal dagger p++.
								if ((attacker.getWieldedWeapon() == 5698 || attacker.getWieldedWeapon() == 18785) && Misc.hasPercentageChance(30))
								{
										CombatNpc.applyPoisonOnNpc(attacker, npc, 6);
								}

								// Abyssal tentacle.
								if (attacker.getWieldedWeapon() == 18767 && Misc.hasPercentageChance(25))
								{
										CombatNpc.applyPoisonOnNpc(attacker, npc, 4);
								}
								CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 1, attacker.meleeFirstDamage);

								if (attacker.getMultipleDamageSpecialAttack())
								{
										CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 2, attacker.meleeSecondDamage);
										applyDragonClawsDamageOnNpc(attacker, npc);
								}
						}
						if (!attacker.isUsingMagic() && attacker.getProjectileStage() > 0)
						{ // range hit damage
								int damage = attacker.rangedFirstDamage;
								int damage2 = attacker.rangedSecondDamage;
								if (npc.currentHitPoints - damage < 0)
								{
										damage = npc.currentHitPoints;
								}
								if (npc.currentHitPoints - damage <= 0 && damage2 > 0)
								{
										damage2 = 0;
								}


								// Blowpipe
								if (attacker.getWieldedWeapon() == 18779 && Misc.hasOneOutOf(4))
								{
										CombatNpc.applyPoisonOnNpc(attacker, npc, 10);
								}
								ItemAssistant.dropArrowNpc(attacker);
								npc.underAttack = true;
								CombatNpc.applyHitSplatOnNpc(attacker, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 1);
								if (attacker.isMagicBowSpecialAttack() || attacker.isUsingDarkBowSpecialAttack() || attacker.handCannonSpecialAttack || attacker.isUsingDarkBowNormalAttack())
								{
										CombatNpc.applyHitSplatOnNpc(attacker, npc, damage2, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 2);
								}
								if (attacker.killingNpcIndex != attacker.getOldNpcIndex())
								{
										attacker.setTotalDamageDealt(0);
								}
								attacker.killingNpcIndex = attacker.getOldNpcIndex();
						}
						else if (attacker.getProjectileStage() > 0)
						{ // magic hit damage

								// Toxic staff of the dead.
								if (attacker.getWieldedWeapon() == 18783 && Misc.hasPercentageChance(25))
								{
										CombatNpc.applyPoisonOnNpc(attacker, npc, 10);
								}
								int damage = attacker.getMagicDamage();
								if (Area.inMulti(npc.getX(), npc.getY()) && multiMagicSpell(attacker) && damage > 0)
								{
										attacker.barrageCount = 0;
										for (int j = 0; j < NpcHandler.npcs.length; j++)
										{
												if (NpcHandler.npcs[j] != null)
												{
														if (attacker.barrageCount >= 9)
														{
																break;
														}
														int nX = NpcHandler.npcs[j].getX(), nY = NpcHandler.npcs[j].getY(), pX = npc.getX(), pY = npc.getY();
														if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1) && npc.npcIndex != j)
														{
																appendMultiBarrageNPC(attacker, j, false);
														}
												}
										}
								}
								if (npc.currentHitPoints - damage < 0)
								{
										damage = npc.currentHitPoints;
								}
								boolean magicFailed = attacker.isMagicSplash();
								if (Combat.getEndGfxHeight(attacker) == 100 && !magicFailed)
								{ // end GFX
										npc.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
								}
								else if (!magicFailed)
								{
										npc.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
								}
								if (magicFailed)
								{
										npc.gfx100(85);
								}
								npc.underAttack = true;
								if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][6] != 0)
								{
										CombatNpc.applyHitSplatOnNpc(attacker, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON, 1);
								}
								else
								{
										Combat.addCombatExperience(attacker, ServerConstants.RANGED_ICON, 0);
								}
								attacker.killingNpcIndex = attacker.getOldNpcIndex();
								npc.updateRequired = true;
								if (!attacker.magicOnFloor)
								{
										attacker.setUsingMagic(false);
								}
								attacker.setOldSpellId(0);
						}
						NpcHandler.blockAnimation(npc);

						/*
						if ((npc.npcType == 8349 || npc.npcType == 8350 || npc.npcType == 8351) && System.currentTimeMillis() - npc.tormentedDemonTimeChangedPrayer >= (npc.tormentedDemonPrayerChangeRandom * 1000) && npc.currentHitPoints > 50 && !npc.isDead)
						{
								npc.tormentedDemonTimeChangedPrayer = System.currentTimeMillis();
								final int oldX = npc.getX();
								final int oldY = npc.getY();
								final int old2 = npc.attackType;
								final boolean old3 = npc.tormentedDemonShield;
								final long old4 = npc.tormentedDemonTimeChangedPrayer;
								final long old5 = npc.tormentedDemonTimeWeakened;
								final int oldHp = npc.currentHitPoints;
								int npcId = 0;
								if (npc.attackStyleDamagedBy == ServerConstants.MELEE_ICON)
								{
										npcId = 8349;
								}
								else if (npc.attackStyleDamagedBy == ServerConstants.MAGIC_ICON)
								{
										npcId = 8350;
								}
								else if (npc.attackStyleDamagedBy == ServerConstants.RANGED_ICON)
								{
										npcId = 8351;
								}
								final int npcId1 = npcId;
						
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
												Pet.deletePet(npc);
												NpcHandler.spawnDifferentTormentedDemon(attacker, npcId1, oldHp, oldX, oldY, old2, old3, old4, old5);
										}
								}, 2);
						
						}
						*/
				}
				if (attacker.bowSpecShot <= 0)
				{
						attacker.setOldNpcIndex(0);
						attacker.setProjectileStage(0);
						attacker.setLastRangedWeaponUsed(0);
						attacker.bowSpecShot = 0;
				}
				if (attacker.bowSpecShot >= 2)
				{
						attacker.bowSpecShot = 0;
				}
				if (attacker.bowSpecShot == 1)
				{
						attacker.bowSpecShot = 0;
				}
		}


		public static void applyMeleeDamageOnNpc(Player player, Npc npc, int damageMask, int damage)
		{
				if (npc.currentHitPoints - damage < 0)
				{
						damage = npc.currentHitPoints;
				}
				boolean guthansEffect = false;
				if (Combat.wearingFullGuthan(player))
				{
						if (Misc.random(3) == 1)
						{
								guthansEffect = true;
						}
				}
				if (damage > 0 && guthansEffect)
				{
						player.addToHitPoints(damage);
						npc.gfx0(398);
				}
				npc.underAttack = true;
				player.killingNpcIndex = player.getNpcIdAttacking();
				player.lastNpcAttackedIndex = npc.npcIndex;
				switch (player.getSpecEffect())
				{

						// Zamorak Godsword.
						case 2:
								if (damage > 0 && npc.canBeFrozen())
								{
										npc.setFrozenLength(20000);
										npc.gfx0(369);
										player.playerAssistant.sendMessage("You have frozen your target.");
								}
								break;


						// Saradomin godsword special.
						case 4:
								if (damage > 20)
								{
										player.addToHitPoints(damage / 2);
										player.currentCombatSkillLevel[ServerConstants.PRAYER] += damage / 4;
										Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
								}
								break;
				}
				CombatNpc.applyHitSplatOnNpc(player, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON, damageMask);
				player.setSpecEffect(0);
		}

		public static void fireProjectileNpc(Player player)
		{
				if (player.getOldNpcIndex() > 0)
				{
						if (NpcHandler.npcs[player.getOldNpcIndex()] != null)
						{
								player.setProjectileStage(2);
								int pX = player.getX();
								int pY = player.getY();
								int nX = NpcHandler.npcs[player.getOldNpcIndex()].getVisualX();
								int nY = NpcHandler.npcs[player.getOldNpcIndex()].getVisualY();
								int offX = (pY - nY) * -1;
								int offY = (pX - nX) * -1;
								player.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, Combat.getProjectileSpeed(player), Combat.getRangeProjectileGFX(player), Combat.getProjectileStartHeight(player), 31, player.getOldNpcIndex() + 1, Combat.getStartDelay(player), Combat.getProjectileSlope(player));
								if (Combat.usingDbow(player))
								{
										player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, Combat.getProjectileSpeed(player) - 20, Combat.getRangeProjectileGFX(player), 43, 31, player.getOldNpcIndex() + 1, Combat.getStartDelay(player), Combat.getProjectileSlope(player));
								}
								else if (player.handCannonSpecialAttack)
								{
										player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 95, Combat.getRangeProjectileGFX(player), Combat.getProjectileStartHeight(player), Combat.getProjectileEndHeight(player), -player.getOldNpcIndex() - 1, 65, Combat.getProjectileSlope(player));
								}
						}
				}
		}

		/**
		 * Create hitsplat on NPC.
		 */
		public static void applyHitSplatOnNpc(Player player, Npc npc, int damage, int mask, int icon, int hitsplatIndex)
		{
				if (npc.currentHitPoints == 0)
				{
						return;
				}
				npc.attackStyleDamagedBy = icon;
				if (damage > npc.currentHitPoints)
				{
						damage = npc.currentHitPoints;
				}
				player.setTotalDamageDealt(player.getTotalDamageDealt() + damage);

				npc.currentHitPoints -= damage;
				if (player.getDragonClawsSpecialAttack())
				{
						SpecialAttackTracker.storeDragonClawsDamage(player, hitsplatIndex == 1 ? damage : -1, hitsplatIndex == 2 ? damage : -1, hitsplatIndex == 3 ? damage : -1, hitsplatIndex == 4 ? damage : -1);
						if (hitsplatIndex == 4)
						{
								SpecialAttackTracker.saveDragonClawsMaximumDamage(player, true);
						}
				}
				else
				{
						if (hitsplatIndex == 1)
						{
								SpecialAttackTracker.saveMaximumDamage(player, damage, "FIRST", true);
						}
						else
						{
								SpecialAttackTracker.saveMaximumDamage(player, damage, "SECOND", true);
						}
				}

				Zombie.zombieDamaged(player, npc, damage);
				boolean maxHit = false;
				if (player.maximumDamageMelee < 4)
				{
						player.maximumDamageMelee = 4;
				}
				if (player.maximumDamageRanged < 4)
				{
						player.maximumDamageRanged = 4;
				}
				if (player.getMaximumDamageMagic() < 4)
				{
						player.setMaximumDamageMagic(4);
				}
				switch (icon)
				{
						case 0:
								int damageMelee = player.maximumDamageMelee;
								if (player.isGraniteMaulSpecial)
								{
										damageMelee = player.graniteMaulSpecialCriticalDamage;
								}
								maxHit = damage >= damageMelee * 0.96;
								break;
						case 1:
								maxHit = damage >= player.maximumDamageRanged * 0.96;
								break;
						case 2:
								maxHit = damage >= player.getMaximumDamageMagic() * 0.96;
								break;
				}
				if (maxHit)
				{
						mask = 1;
				}
				if (npc.currentHitPoints == 0)
				{
						npc.isDead = true;
				}
				if (!npc.hitUpdateRequired)
				{
						npc.hitDiff = damage;
						npc.hitUpdateRequired = true;
						npc.updateRequired = true;
						npc.hitIcon = icon;
						npc.hitMask = mask;
				}
				else
				{
						npc.hitDiff2 = damage;
						npc.hitUpdateRequired2 = true;
						npc.updateRequired = true;
						player.doubleHit = false;
						npc.hitIcon2 = icon;
						npc.hitMask2 = mask;
				}
		}

		public static void applyHitSplatOnNpcNoPlayerRelation(Npc npc, int damage, int mask, int icon)
		{
				if (npc.currentHitPoints == 0)
				{
						return;
				}
				npc.attackStyleDamagedBy = icon;
				if (damage > npc.currentHitPoints)
				{
						damage = npc.currentHitPoints;
				}

				npc.currentHitPoints -= damage;
				mask = 0;
				if (npc.currentHitPoints == 0)
				{
						npc.isDead = true;
				}

				if (!npc.hitUpdateRequired)
				{
						npc.hitDiff = damage;
						npc.hitUpdateRequired = true;
						npc.updateRequired = true;
						npc.hitIcon = icon;
						npc.hitMask = mask;
				}
				else
				{
						npc.hitDiff2 = damage;
						npc.hitUpdateRequired2 = true;
						npc.updateRequired = true;
						npc.hitIcon2 = icon;
						npc.hitMask2 = mask;
				}
		}

		public static void multiSpellEffectNPC(Player player, int npcId, int damage)
		{
				switch (CombatConstants.MAGIC_SPELLS[player.getOldSpellId()][0])
				{
						case 12919:
								// blood spells
						case 12929:
								int heal = damage / 4;
								player.addToHitPoints(heal);
								break;
						case 12891:
						case 12881:
								if (NpcHandler.npcs[npcId].canBeFrozen())
								{
										NpcHandler.npcs[npcId].setFrozenLength(Combat.getFreezeTime(player));
								}
								break;
				}
		}

		public static boolean checkMultiBarrageReqsNPC(int i)
		{
				if (NpcHandler.npcs[i] == null)
						return false;
				return true;
		}

		public static void handleDfsNPC(Player player)
		{
				if (player.getNpcIdAttacking() > 0)
				{
						if (player.dragonFireShieldCharges == 0)
						{
								player.playerAssistant.sendMessage("Your shield has no charges left.");
								return;
						}
						if (NpcHandler.npcs[player.getNpcIdAttacking()] == null && NpcHandler.npcs[player.getNpcIdAttacking()].isDead)
						{
								return;
						}
						player.setProjectileStage(2);
						if (System.currentTimeMillis() - player.dfsDelay > 120000)
						{
								if (player.getNpcIdAttacking() > 0 && NpcHandler.npcs[player.getNpcIdAttacking()] != null)
								{
										final int damage = Misc.random(25);
										player.startAnimation(6696);
										player.gfx0(1165);
										player.dragonFireShieldCharges -= 1;
										applyHitSplatOnNpc(player, NpcHandler.npcs[player.getNpcIdAttacking()], damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
										final int pX = player.getX();
										final int pY = player.getY();
										int oX = NpcHandler.npcs[player.getNpcIdAttacking()].getX();
										int oY = NpcHandler.npcs[player.getNpcIdAttacking()].getY();
										final int offX = (pY - oY) * -1;
										final int offY = (pX - oX) * -1;
										player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 75, 1166, 37, 37, -player.getOldNpcIndex() - 1, 53, 16);
										if (NpcHandler.npcs[player.getNpcIdAttacking()].isDead == true)
										{
												player.playerAssistant.sendMessage("This NPC is already dead!");
												return;
										}
										player.dfsDelay = System.currentTimeMillis();
								}
								else
								{
										player.playerAssistant.sendMessage("I should be in combat before using this.");
								}
						}
						else
						{
								player.playerAssistant.sendMessage("My shield hasn't finished recharging yet.");
						}
				}
		}

		public static int getBonusAttack(Player player, Npc npc)
		{
				switch (npc.npcType)
				{
						case 2883:
								return Misc.random(50) + 30;
						case 2026:
						case 2027:
						case 2029:
						case 2030:
								return Misc.random(50) + 30;
				}
				return 0;
		}

		public static boolean isArmadylNpc(int i)
		{
				switch (NpcHandler.npcs[i].npcType)
				{
						case 6222:
						case 6223:
						case 6229:
						case 6225:
						case 6230:
						case 6227:
						case 6232:
						case 6239:
						case 6233:
						case 6231:
								return true;
				}
				return false;
		}
}