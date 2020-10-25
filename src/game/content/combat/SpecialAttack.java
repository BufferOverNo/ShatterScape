package game.content.combat;

import core.ServerConstants;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.content.music.SoundSystem;
import game.content.skilling.agility.AgilityAssistant;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Special attacks.
 * 
 * @author MGT Madness, created on 21-11-2013.
 */
public class SpecialAttack
{

		public static void activateSpecial(final Player attacker, int weapon, int theTarget)
		{
				Player victim = null;
				if (attacker.getNpcIdAttacking() <= 0)
				{
						victim = PlayerHandler.players[theTarget];
				}
				Npc targetNPC = NpcHandler.npcs[theTarget];

				int Delay = Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase());

				if (targetNPC == null && attacker.getNpcIdAttacking() > 0)
				{
						return;
				}
				attacker.doubleHit = false;
				attacker.setSpecEffect(0);
				attacker.setUsingDarkBowSpecialAttack(false);
				attacker.setProjectileStage(0);

				if (attacker.getNpcIdAttacking() > 0)
				{
						attacker.setOldNpcIndex(targetNPC.npcIndex);
				}
				else if (attacker.getPlayerIdAttacking() > 0)
				{
						if (victim != null)
						{
								attacker.setOldPlayerIndex(victim.getPlayerId());
								victim.setUnderAttackBy(attacker.getPlayerId());
								victim.setLastAttackedBy(attacker.getPlayerId());
						}
				}
				switch (weapon)
				{

						/* Melee weapons */

						case 18785: // Abyssal dagger.
								attacker.startAnimation(1062);
								attacker.gfx0(2623);
								attacker.specDamage = 0.85;
								attacker.setSpecialAttackAccuracyMultiplier(1.25);
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[33] = 2;
								attacker.setWeaponAmountUsed(33);
								SoundSystem.sendSound(attacker, victim, 385, 0);
								attacker.doubleHit = true;
								attacker.setMultipleDamageSpecialAttack(true);
								break;

						// Bandos godsword
						case 11696:
								attacker.setSpecEffect(3);
								attacker.startAnimation(7073);
								attacker.gfx0(1223);
								attacker.specDamage = 1.1;
								attacker.setSpecialAttackAccuracyMultiplier(1.35);
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[0] = 1;
								attacker.setWeaponAmountUsed(0);
								break;

						// Saradomin godsword
						case 11698:
								attacker.setSpecEffect(4);
								attacker.startAnimation(7071);
								attacker.gfx0(1220);
								attacker.specDamage = 1.15;
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[1] = 1;
								attacker.setWeaponAmountUsed(1);
								attacker.setSpecialAttackAccuracyMultiplier(1.35);
								break;

						// Dragon warhammer
						case 18771:
								attacker.startAnimation(10505);
								attacker.gfx0(1840);
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase() + 1));
								attacker.specDamage = 1.50;
								attacker.specialAttackWeaponUsed[2] = 1;
								attacker.setWeaponAmountUsed(2);
								attacker.setSpecEffect(6);
								break;

						// Armadyl godsword
						case 11694:
								attacker.startAnimation(7074);
								attacker.gfx0(1222);
								attacker.specDamage = 1.4;
								attacker.setSpecialAttackAccuracyMultiplier(2.0);
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[3] = 1;
								attacker.setWeaponAmountUsed(3);
								break;

						// Zamorak godsword
						case 11700:
								attacker.setSpecEffect(2);
								attacker.startAnimation(7070);
								attacker.gfx0(1221);
								attacker.setSpecialAttackAccuracyMultiplier(1.35);
								attacker.setHitDelay(Delay);
								attacker.specDamage = 1.15;
								attacker.specialAttackWeaponUsed[4] = 1;
								attacker.setWeaponAmountUsed(4);
								break;

						// Dragon longsword
						case 1305:
								attacker.gfx100(248);
								attacker.startAnimation(1058);
								attacker.setHitDelay(Delay);
								attacker.specDamage = 1.3;
								attacker.specialAttackWeaponUsed[5] = 1;
								attacker.setWeaponAmountUsed(5);
								attacker.setSpecialAttackAccuracyMultiplier(1.2);
								SoundSystem.sendSound(attacker, victim, 390, 0);
								break;

						// Dragon spear
						case 1249:
								attacker.startAnimation(1064);
								attacker.gfx100(253);
								if (attacker.getPlayerIdAttacking() > 0)
								{
										if (victim != null)
										{
												victim.getPA().getSpeared(attacker.getX(), attacker.getY());
												victim.gfx100(254);
												if (!victim.soundSent)
												{
														SoundSystem.sendSound(attacker, victim, 511, 450);
												}
												Combat.resetPlayerAttack(victim);
												attacker.turnPlayerTo(victim.getX(), victim.getY());
										}
								}
								attacker.setHitDelay(0);
								Combat.resetPlayerAttack(attacker);
								break;

						// Dragon Halberd
						case 3204:
								attacker.gfx100(282);
								attacker.startAnimation(1203);
								attacker.setHitDelay(Delay);
								attacker.setSpecialAttackAccuracyMultiplier(1.2);
								if (targetNPC != null && attacker.getNpcIdAttacking() > 0)
								{
										if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), targetNPC.getVisualX(), targetNPC.getVisualY(), 1))
										{
												attacker.doubleHit = true;
												attacker.setMultipleDamageSpecialAttack(true);
										}
								}
								if (victim != null && attacker.getPlayerIdAttacking() > 0)
								{
										if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), 1))
										{
												attacker.doubleHit = true;
												attacker.setMultipleDamageSpecialAttack(true);
										}
								}
								attacker.specialAttackWeaponUsed[7] = 2;
								attacker.setWeaponAmountUsed(7);
								break;

						// Dragon scimitar
						case 4587:
								attacker.setSpecEffect(1);
								attacker.gfx100(347);
								attacker.startAnimation(1872);
								attacker.setSpecialAttackAccuracyMultiplier(1.2);
								attacker.setHitDelay(Delay);
								break;

						// Dragon mace
						case 1434:
								attacker.startAnimation(1060);
								attacker.gfx100(251);
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()) + 1);
								attacker.specDamage = 1.5;
								attacker.specialAttackWeaponUsed[9] = 1;
								attacker.setWeaponAmountUsed(9);
								attacker.setSpecialAttackAccuracyMultiplier(1.2);
								break;

						// Barrelchest Anchor
						case 10887:
								attacker.gfx100(1027);
								attacker.startAnimation(5870);
								attacker.setSpecialAttackAccuracyMultiplier(1.25);
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[10] = 1;
								attacker.setWeaponAmountUsed(10);
								attacker.setSpecEffect(5);
								break;

						// Dragon dagger
						case 1215:
						case 1231:
						case 5680:
						case 5698:
								attacker.gfx100(252);
								attacker.startAnimation(1062);
								attacker.setHitDelay(Delay);
								attacker.setSpecialAttackAccuracyMultiplier(1.35);
								attacker.specDamage = 1.15;
								attacker.doubleHit = true;
								attacker.setMultipleDamageSpecialAttack(true);
								attacker.specialAttackWeaponUsed[11] = 2;
								attacker.setWeaponAmountUsed(11);
								SoundSystem.sendSound(attacker, victim, 385, 0);
								break;

						// Saradomin sword
						case 11730:
						case 18660: // Sara's blessed sword.
								if (targetNPC != null && attacker.getNpcIdAttacking() > 0)
								{
										targetNPC.gfx100(1194);
								}
								if (victim != null && attacker.getPlayerIdAttacking() > 0)
								{
										victim.gfx100(1194);
								}
								attacker.startAnimation(7072);
								attacker.gfx100(1224);
								attacker.setHitDelay(Delay);
								attacker.saradominSwordSpecialAttack = true;
								attacker.setMultipleDamageSpecialAttack(true);
								attacker.setSpecialAttackAccuracyMultiplier(weapon == 18660 ? 1.5 : 1.2);
								if (weapon == 18660)
								{
										attacker.specDamage = 1.35;
								}
								attacker.specialAttackWeaponUsed[13] = 2;
								attacker.setWeaponAmountUsed(13);
								break;

						// Abyssal whip
						case 4151:
						case 15445:
						case 15444:
						case 15443:
						case 15442:
						case 15441:
								if (targetNPC != null && attacker.getNpcIdAttacking() > 0)
								{
										targetNPC.gfx100(341);
								}
								if (victim != null && attacker.getPlayerIdAttacking() > 0)
								{
										victim.gfx100(341);
										int energyStolen = (int) (victim.runEnergy / 10);
										victim.runEnergy -= energyStolen;
										AgilityAssistant.updateRunEnergyInterface(victim);
										attacker.runEnergy += energyStolen;
										if (attacker.runEnergy > 100)
										{
												attacker.runEnergy = 100;
										}
										AgilityAssistant.updateRunEnergyInterface(attacker);
								}
								attacker.startAnimation(11956);
								attacker.setHitDelay(Delay);
								attacker.setSpecialAttackAccuracyMultiplier(1.25);
								SoundSystem.sendSound(attacker, victim, 1081, 300);
								break;


						case 18767: // Abyssal tentacle.
								if (targetNPC != null && attacker.getNpcIdAttacking() > 0)
								{
										targetNPC.gfx100(341);
										targetNPC.setFrozenLength(5000);
										if (Misc.hasPercentageChance(25))
										{
												CombatNpc.applyPoisonOnNpc(attacker, targetNPC, 4);
										}
								}
								if (victim != null && attacker.getPlayerIdAttacking() > 0)
								{
										victim.gfx100(341);
										victim.setFrozenLength(5000);
										victim.frozenBy = attacker.getPlayerId();
										if (Misc.hasPercentageChance(25))
										{
												Poison.appendPoison(attacker, victim, false, 4);
										}
								}
								attacker.startAnimation(11956);
								attacker.setHitDelay(Delay);
								SoundSystem.sendSound(attacker, victim, 1081, 300);
								break;

						// Dragon claws.
						case 14484:
								attacker.startAnimation(10961);
								attacker.gfx0(1950);
								attacker.setHitDelay(Delay);
								attacker.setDragonClawsSpecialAttack(true);
								attacker.setMultipleDamageSpecialAttack(true);
								attacker.setSpecEffect(10);
								attacker.setSpecialAttackAccuracyMultiplier(1.45);
								attacker.setWeaponAmountUsed(15);
								attacker.specDamage = 1.25;
								break;

						// Vesta's spear
						case 13905:
						case 13907:
								attacker.startAnimation(10499);
								attacker.gfx0(1835);
								attacker.setSpecialAttackAccuracyMultiplier(1.25);
								attacker.setHitDelay(Delay);
								attacker.specialAttackWeaponUsed[19] = 1;
								attacker.setWeaponAmountUsed(19);
								break;

						// Vesta's Longsword
						case 13899:
						case 13901:
								attacker.startAnimation(10502);
								attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase() + 1));
								attacker.specDamage = 1.05;
								attacker.setSpecialAttackAccuracyMultiplier(1.5);
								attacker.specialAttackWeaponUsed[20] = 1;
								attacker.setWeaponAmountUsed(20);
								break;

						/* End of Melee weapons */

						/* Ranged weapons */

						// Hand cannon
						case 15241:
								attacker.gfx0(2138);
								attacker.handCannonSpecialAttack = true;
								attacker.setUsingShortRangeRangedWeapon(true);
								attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(12153);
								attacker.setProjectileStage(1);
								//attacker.setSpecialAttackAccuracyMultiplier(1.1);
								attacker.setHitDelay(Delay);
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.setAttackTimer(attacker.getAttackTimer() + 1);
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.specialAttackWeaponUsed[21] = 2;
								attacker.setWeaponAmountUsed(21);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						// Morrigan Throwing Axe
						case 13883:
								ItemAssistant.deleteEquipment(attacker);
								attacker.setUsingShortRangeRangedWeapon(true);
								attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(10504);
								attacker.gfx0(1838);
								attacker.setSpecialAttackAccuracyMultiplier(1.4);
								attacker.specDamage = 1.20;
								attacker.setProjectileStage(1);
								attacker.setHitDelay(Delay);
								if (attacker.getCombatStyle(ServerConstants.RAPID))
								{
										attacker.setAttackTimer(attacker.getAttackTimer() - 1);
								}
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.specialAttackWeaponUsed[23] = 1;
								attacker.setWeaponAmountUsed(23);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						// Toxic blowpipe.
						case 18779:
								attacker.setUsingShortRangeRangedWeapon(true);
								attacker.startAnimation(13729);
								attacker.setHitDelay(Delay);
								attacker.specDamage = 1.50;
								attacker.setProjectileStage(1);
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.blowpipeSpecialAttack = true;
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						// Heavy ballista.
						case 18807:
								attacker.setUsingMediumRangeRangedWeapon(true);
								attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(13727);
								attacker.setSpecialAttackAccuracyMultiplier(1.50);
								attacker.specDamage = 1.25;
								attacker.setProjectileStage(1);
								attacker.setHitDelay(Delay);
								if (attacker.getCombatStyle(ServerConstants.RAPID))
								{
										attacker.setAttackTimer(attacker.getAttackTimer() - 1);
								}
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.specialAttackWeaponUsed[32] = 1;
								attacker.setWeaponAmountUsed(32);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						// Morrigan Javelin
						case 13879:
								ItemAssistant.deleteEquipment(attacker);
								attacker.morrigansJavelinSpecialAttack = true;
								attacker.setUsingShortRangeRangedWeapon(true);
								attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(10501);
								attacker.gfx0(1836);
								attacker.setSpecialAttackAccuracyMultiplier(1.4);
								attacker.setProjectileStage(1);
								attacker.setHitDelay(Delay);
								if (attacker.getCombatStyle(ServerConstants.RAPID))
								{
										attacker.setAttackTimer(attacker.getAttackTimer() - 1);
								}
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.specialAttackWeaponUsed[24] = 1;
								attacker.setWeaponAmountUsed(24);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						case 861: // Magic shortbow.
						case 18659: // Magic shortbow (i).
								attacker.setMagicBowSpecialAttack(true);
								attacker.setUsingMediumRangeRangedWeapon(true);
								attacker.bowSpecShot = 1;
								attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
								RangedAmmoUsed.deleteAmmo(attacker);
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(1074);
								attacker.setSpecialAttackAccuracyMultiplier(1.2);
								attacker.setProjectileStage(1);
								attacker.setHitDelay(Delay);
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.specialAttackWeaponUsed[25] = 2;
								attacker.setWeaponAmountUsed(25);
								attacker.rangedSpecialAttackOnNpc = true;
								SoundSystem.sendSound(attacker, victim, 386, 180);
								break;

						case 11235:
						case 15701:
						case 15702:
						case 15703:
						case 15704:
								// Dark bow
								attacker.setUsingMediumRangeRangedWeapon(true);
								attacker.setUsingDarkBowSpecialAttack(true);
								attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
								RangedAmmoUsed.deleteAmmo(attacker);
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.startAnimation(426);
								attacker.setProjectileStage(1);
								attacker.gfx100(Combat.getRangeStartGFX(attacker));
								attacker.setHitDelay(Delay);
								if (attacker.getCombatStyle(ServerConstants.RAPID))
								{
										attacker.setAttackTimer(attacker.getAttackTimer() - 1);
								}
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.setSpecialAttackAccuracyMultiplier(1.20);
								attacker.specDamage = 1.5;
								attacker.specialAttackWeaponUsed[27] = 2;
								attacker.setWeaponAmountUsed(27);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						case 18642: // Armadyl crossbow.
								attacker.armadylCrossBowSpecial = true;
								attacker.setUsingMediumRangeRangedWeapon(true);
								attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
								RangedAmmoUsed.deleteAmmo(attacker);
								attacker.setLastRangedWeaponUsed(weapon);
								attacker.setProjectileStage(1);
								attacker.startAnimation(4230);
								attacker.setHitDelay(Delay);
								if (attacker.getCombatStyle(ServerConstants.RAPID))
								{
										attacker.setAttackTimer(attacker.getAttackTimer() - 1);
								}
								if (attacker.getPlayerIdAttacking() > 0)
								{
										Combat.fireProjectilePlayer(attacker);
								}
								else if (attacker.getNpcIdAttacking() > 0)
								{
										CombatNpc.fireProjectileNpc(attacker);
								}
								attacker.setSpecialAttackAccuracyMultiplier(1.40);
								attacker.rangedSpecialAttackOnNpc = true;
								break;

						/* End of Ranged weapons */

				}
				attacker.setUsingSpecial(false);
				attacker.botUsedSpecialAttack = false;
				CombatInterface.updateSpecialBar(attacker);
		}

}