package game.content.consumable;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.minigame.zombie.Zombie;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.item.Item;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * @author Sanity
 */

public class Potions
{

		public static void handlePotion(Player player, int itemId, int slot)
		{
				if (player.duelRule[5])
				{
						player.playerAssistant.sendMessage("You may not drink potions in this duel.");
						return;
				}
				if (player.getDead())
				{
						player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
						return;
				}
				if (player.getTransformed() != 0)
				{
						return;
				}
				if (player.getHeight() == 20 && player.tournamentTarget == -1)
				{
						player.getPA().sendMessage("Use the box of health instead.");
						return;
				}
				long delay = 1700;
				if (System.currentTimeMillis() - player.potDelay < delay)
				{
						return;
				}
				player.showPotionMessage = true;
				String item = game.item.Item.getItemName(itemId);
				switch (itemId)
				{

						// Zamorak brew.
						case 2450:
						case 189:
						case 191:
						case 193:
								return;

						case 18799:
								drinkStatPotion(player, itemId, 18801, slot, 0, true);
								drinkStatPotion(player, itemId, 0, slot, 1, true);
								drinkStatPotion(player, itemId, 0, slot, 2, true);
								break;

						case 18801:
								drinkStatPotion(player, itemId, 18803, slot, 0, true);
								drinkStatPotion(player, itemId, 0, slot, 1, true);
								drinkStatPotion(player, itemId, 0, slot, 2, true);
								break;

						case 18803:
								drinkStatPotion(player, itemId, 18805, slot, 0, true);
								drinkStatPotion(player, itemId, 0, slot, 1, true);
								drinkStatPotion(player, itemId, 0, slot, 2, true);
								break;

						case 18805:
								drinkStatPotion(player, itemId, 229, slot, 0, true);
								drinkStatPotion(player, itemId, 0, slot, 1, true);
								drinkStatPotion(player, itemId, 0, slot, 2, true);
								break;

						case 6685:
								//brews
								doTheBrew(player, itemId, 6687, slot);
								break;
						case 6687:
								doTheBrew(player, itemId, 6689, slot);
								break;
						case 6689:
								doTheBrew(player, itemId, 6691, slot);
								break;
						case 6691:
								doTheBrew(player, itemId, 229, slot);
								break;

						case 3008:
								energyPotion(player, itemId, 3010, slot, 10);
								break;
						case 3010:
								energyPotion(player, itemId, 3012, slot, 10);
								break;
						case 3012:
								energyPotion(player, itemId, 3014, slot, 10);
								break;
						case 3014:
								energyPotion(player, itemId, 229, slot, 10);
								break;


						case 3016:
								energyPotion(player, itemId, 3018, slot, 50);
								break;
						case 3018:
								energyPotion(player, itemId, 3020, slot, 50);
								break;
						case 3020:
								energyPotion(player, itemId, 3022, slot, 50);
								break;
						case 3022:
								energyPotion(player, itemId, 229, slot, 50);
								break;

						case 2436:
								drinkStatPotion(player, itemId, 145, slot, 0, true); // Super attack.
								break;
						case 145:
								drinkStatPotion(player, itemId, 147, slot, 0, true);
								break;
						case 147:
								drinkStatPotion(player, itemId, 149, slot, 0, true);
								break;
						case 149:
								drinkStatPotion(player, itemId, 229, slot, 0, true);
								break;
						case 2440:
								drinkStatPotion(player, itemId, 157, slot, 2, true); //sup str
								break;
						case 157:
								drinkStatPotion(player, itemId, 159, slot, 2, true);
								break;
						case 159:
								drinkStatPotion(player, itemId, 161, slot, 2, true);
								break;
						case 161:
								drinkStatPotion(player, itemId, 229, slot, 2, true);
								break;
						case 2444:
								drinkStatPotion(player, itemId, 169, slot, 4, false); //range pot
								break;
						case 169:
								drinkStatPotion(player, itemId, 171, slot, 4, false);
								break;
						case 171:
								drinkStatPotion(player, itemId, 173, slot, 4, false);
								break;
						case 173:
								drinkStatPotion(player, itemId, 229, slot, 4, false);
								break;
						case 2432:
								drinkStatPotion(player, itemId, 133, slot, 1, false); //def pot
								break;
						case 133:
								drinkStatPotion(player, itemId, 135, slot, 1, false);
								break;
						case 135:
								drinkStatPotion(player, itemId, 137, slot, 1, false);
								break;
						case 137:
								drinkStatPotion(player, itemId, 229, slot, 1, false);
								break;
						case 113:
								drinkStatPotion(player, itemId, 115, slot, 2, false); //str pot
								break;
						case 115:
								drinkStatPotion(player, itemId, 117, slot, 2, false);
								break;
						case 117:
								drinkStatPotion(player, itemId, 119, slot, 2, false);
								break;
						case 119:
								drinkStatPotion(player, itemId, 229, slot, 2, false);
								break;
						case 2428:
								drinkStatPotion(player, itemId, 121, slot, 0, false); //attack pot
								break;
						case 121:
								drinkStatPotion(player, itemId, 123, slot, 0, false);
								break;
						case 123:
								drinkStatPotion(player, itemId, 125, slot, 0, false);
								break;
						case 125:
								drinkStatPotion(player, itemId, 229, slot, 0, false);
								break;
						case 2442:
								drinkStatPotion(player, itemId, 163, slot, 1, true); //super def pot
								break;
						case 163:
								drinkStatPotion(player, itemId, 165, slot, 1, true);
								break;
						case 165:
								drinkStatPotion(player, itemId, 167, slot, 1, true);
								break;
						case 167:
								drinkStatPotion(player, itemId, 229, slot, 1, true);
								break;
						case 10925:
								drinkSuperRestore(player, itemId, 10927, slot);
								curePoison(player, 360000);
								break;
						case 10927:
								drinkSuperRestore(player, itemId, 10929, slot);
								curePoison(player, 360000);
								break;
						case 10929:
								drinkSuperRestore(player, itemId, 10931, slot);
								curePoison(player, 360000);
								break;
						case 10931:
								drinkSuperRestore(player, itemId, 229, slot);
								curePoison(player, 360000);
								break;
						case 3024:
								drinkSuperRestore(player, itemId, 3026, slot);
								break;
						case 3026:
								drinkSuperRestore(player, itemId, 3028, slot);
								break;
						case 3028:
								drinkSuperRestore(player, itemId, 3030, slot);
								break;
						case 3030:
								drinkSuperRestore(player, itemId, 229, slot);
								break;
						case 2434:
								drinkPrayerPotion(player, itemId, 139, slot, false);
								break;
						case 139:
								drinkPrayerPotion(player, itemId, 141, slot, false);
								break;
						case 141:
								drinkPrayerPotion(player, itemId, 143, slot, false);
								break;
						case 143:
								drinkPrayerPotion(player, itemId, 229, slot, false);
								break;
						case 2446:
								drinkAntiPoison(player, itemId, 175, slot, 30000); //anti poisons
								break;
						case 175:
								drinkAntiPoison(player, itemId, 177, slot, 30000);
								break;
						case 177:
								drinkAntiPoison(player, itemId, 179, slot, 30000);
								break;
						case 179:
								drinkAntiPoison(player, itemId, 229, slot, 30000);
								break;
						case 2448:
								drinkAntiPoison(player, itemId, 181, slot, 300000); //anti poisons
								break;
						case 181:
								drinkAntiPoison(player, itemId, 183, slot, 300000);
								break;
						case 183:
								drinkAntiPoison(player, itemId, 185, slot, 300000);
								break;
						case 185:
								drinkAntiPoison(player, itemId, 229, slot, 300000);
								break;
						case 2452:
						case 2454:
						case 2456:
								drinkAntiFirePotion(player, itemId + 2, slot, 6);
								break;
						case 2458:
								drinkAntiFirePotion(player, 229, slot, 6);
								break;

						case 15304:
						case 15305:
						case 15306:
								drinkAntiFirePotion(player, itemId + 1, slot, 12);
								break;
						case 15307:
								drinkAntiFirePotion(player, 229, slot, 12);
								break;
						case 15320:
						case 15321:
						case 15322:
								player.extremeMagic = true;
								drinkExtremePotion(player, itemId, itemId + 1, slot, ServerConstants.MAGIC, false);
								break;
						case 15323:
								player.extremeMagic = true;
								drinkExtremePotion(player, itemId, 229, slot, ServerConstants.MAGIC, false);
								break;
						case 15312:
								// Extreme Strength
								drinkExtremePotion(player, itemId, 15313, slot, ServerConstants.STRENGTH, false);
								break;
						case 15313:
								// Extreme Strength
								drinkExtremePotion(player, itemId, 15314, slot, ServerConstants.STRENGTH, false);
								break;
						case 15314:
								// Extreme Strength
								drinkExtremePotion(player, itemId, 15315, slot, ServerConstants.STRENGTH, false);
								break;
						case 15315:
								// Extreme Strength
								drinkExtremePotion(player, itemId, 229, slot, ServerConstants.STRENGTH, false);
								break;
						case 15308:
								// Extreme Attack
								drinkExtremePotion(player, itemId, 15309, slot, ServerConstants.ATTACK, false);
								break;
						case 15309:
								// Extreme Attack
								drinkExtremePotion(player, itemId, 15310, slot, ServerConstants.ATTACK, false);
								break;
						case 15310:
								// Extreme Attack
								drinkExtremePotion(player, itemId, 15311, slot, ServerConstants.ATTACK, false);
								break;
						case 15311:
								// Extreme Attack
								drinkExtremePotion(player, itemId, 229, slot, ServerConstants.ATTACK, false);
								break;
						case 15316:
								// Extreme Defence
								drinkExtremePotion(player, itemId, 15317, slot, ServerConstants.DEFENCE, false);
								break;
						case 15317:
								// Extreme Defence
								drinkExtremePotion(player, itemId, 15318, slot, ServerConstants.DEFENCE, false);
								break;
						case 15318:
								// Extreme Defence
								drinkExtremePotion(player, itemId, 15319, slot, ServerConstants.DEFENCE, false);
								break;
						case 15319:
								// Extreme Defence
								drinkExtremePotion(player, itemId, 229, slot, ServerConstants.DEFENCE, false);
								break;
						case 15324:
								// Extreme Ranging
								drinkExtremePotion(player, itemId, 15325, slot, ServerConstants.RANGED, false);
								break;
						case 15325:
								// Extreme Ranging
								drinkExtremePotion(player, itemId, 15326, slot, ServerConstants.RANGED, false);
								break;
						case 15326:
								// Extreme Ranging
								drinkExtremePotion(player, itemId, 15327, slot, ServerConstants.RANGED, false);
								break;
						case 15327:
								// Extreme Ranging
								drinkExtremePotion(player, itemId, 229, slot, ServerConstants.RANGED, false);
								break;
						//Magic pots
						case 3040:
								drinkMagicPotion(player, itemId, 3042, slot, ServerConstants.MAGIC, false);
								break;
						case 3042:
								drinkMagicPotion(player, itemId, 3044, slot, ServerConstants.MAGIC, false);
								break;
						case 3044:
								drinkMagicPotion(player, itemId, 3046, slot, ServerConstants.MAGIC, false);
								break;
						case 3046:
								drinkMagicPotion(player, itemId, 229, slot, ServerConstants.MAGIC, false);
								break;

						case 15334:
						case 15333:
						case 15332:
								drinkOverload(player, itemId, itemId + 1, slot);
								break;
						case 15335:
								drinkOverload(player, itemId, 229, slot);
								break;

						case 15300:
						case 15301:
						case 15302:
								recoverSpecial(player, itemId, itemId + 1, slot);
								break;
						case 15303:
								recoverSpecial(player, itemId, 229, slot);
								break;

						// Super prayer.
						case 15328:
								drinkPrayerPotion(player, itemId, 15329, slot, true);
								break;
						case 15329:
								drinkPrayerPotion(player, itemId, 15330, slot, true);
								break;
						case 15330:
								drinkPrayerPotion(player, itemId, 15331, slot, true);
								break;
						case 15331:
								drinkPrayerPotion(player, itemId, 229, slot, true);
								break;

				}
				if (player.showPotionMessage)
				{
						player.soundToSend = 334;
						player.soundDelayToSend = 400;
						player.potDelay = System.currentTimeMillis();
						Combat.resetPlayerAttack(player);
						player.playerAssistant.stopAllActions();
						if (System.currentTimeMillis() - player.lastPotionSip <= 1300)
						{
								player.setAttackTimer(player.getAttackTimer() + 1);
								player.cannotEatDelay = System.currentTimeMillis();
						}
						player.lastPotionSip = System.currentTimeMillis();
						player.potionDrank++;
						String filteredName = Item.getItemName(itemId);
						int index = filteredName.indexOf("(");
						if (index == -1)
						{
								return;
						}
						filteredName = filteredName.substring(0, index).toLowerCase();
						player.playerAssistant.sendFilterableMessage("You drink some of your " + filteredName + " potion.");
						player.setInventoryUpdate(true);
						if (item.endsWith("(4)"))
						{
								player.playerAssistant.sendFilterableMessage("You have 3 doses of potion left.");
						}
						else if (item.endsWith("(3)"))
						{
								player.playerAssistant.sendFilterableMessage("You have 2 doses of potion left.");
						}
						else if (item.endsWith("(2)"))
						{
								player.playerAssistant.sendFilterableMessage("You have 1 dose of potion left.");
						}
						else if (item.endsWith("(1)"))
						{
								player.playerAssistant.sendFilterableMessage("You have finished your potion.");
						}
				}
		}

		private static void drinkOverload(final Player player, int itemID, int result, int slot)
		{
				if (Area.inWilderness(player))
				{
						player.showPotionMessage = false;
						player.playerAssistant.sendMessage("You may not use this in the Wilderness.");
						return;
				}
				if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < 51)
				{
						player.playerAssistant.sendMessage("Your hitpoints are too low.");
						player.showPotionMessage = false;
						return;
				}
				if (player.overloadEvent)
				{
						player.showPotionMessage = false;
						return;
				}
				player.overloadEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.getDead())
								{
										container.stop();
										return;
								}
								if (player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
								{
										container.stop();
										return;
								}
								if (Area.inPVPArea(player) || Area.inDuelArenaRing(player))
								{
										container.stop();
										return;
								}
								player.overloadTicks++;
								overloadBoost(player);
								player.startAnimation(3170);
								Combat.appendHitFromNpcOrVengEtc(player, 10, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
								if (player.overloadTicks >= 5)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.overloadEvent = false;
								player.overloadTicks = 0;
						}
				}, 3);

				player.overloadReboostTicks = 1;
				overloadReBoostEvent(player);
				player.startAnimation(829);
				player.playerItems[slot] = result + 1;
		}

		public static void overloadReBoostEvent(final Player player)
		{
				if (player.overloadReboostTicks == 0)
				{
						return;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{

								if (player.overloadReboostTicks == 0)
								{
										container.stop();
										return;
								}
								if (Zombie.inZombieMiniGameArea(player, player.getX(), player.getY()))
								{
										container.stop();
										return;
								}

								if (Area.inPVPArea(player) || Area.inDuelArenaRing(player))
								{
										player.overloadReboostTicks++;
										return;
								}

								if (player.getDead())
								{
										container.stop();
										return;
								}
								if (player.duelRule[5])
								{
										container.stop();
										return;
								}
								if (player.overloadReboostTicks >= 21)
								{
										for (int index = 0; index < 7; index++)
										{
												if (index == ServerConstants.HITPOINTS || index == ServerConstants.PRAYER)
												{
														continue;
												}
												player.currentCombatSkillLevel[index] = 99;
												Skilling.updateSkillTabFrontTextMain(player, index);
										}
										player.addToHitPoints(50);
										player.getPA().sendMessage("Your overload effects have run out.");
										container.stop();
										return;
								}
								else
								{
										overloadBoost(player);
								}
								player.overloadReboostTicks++;
						}

						@Override
						public void stop()
						{
								player.overloadReboostTicks = 0;
						}
				}, 25);

		}

		private static void overloadBoost(Player player)
		{
				player.currentCombatSkillLevel[ServerConstants.STRENGTH] = (int) (player.getBaseStrengthLevel() * 1.27);
				player.currentCombatSkillLevel[ServerConstants.ATTACK] = (int) (player.getBaseAttackLevel() * 1.27);
				player.currentCombatSkillLevel[ServerConstants.DEFENCE] = (int) (player.getBaseDefenceLevel() * 1.27);
				player.currentCombatSkillLevel[ServerConstants.RANGED] = (int) (player.getBaseRangedLevel() * 1.27);
				player.currentCombatSkillLevel[ServerConstants.MAGIC] = (int) (player.getBaseMagicLevel() * 1.07) + 1;
				RegenerateSkill.storeBoostedTime(player, ServerConstants.STRENGTH);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.ATTACK);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.DEFENCE);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.RANGED);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);

				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.STRENGTH);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.ATTACK);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.DEFENCE);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.RANGED);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
		}

		private static void energyPotion(Player player, int itemId, int i, int slot, int percentage)
		{
				player.startAnimation(829);
				player.playerItems[slot] = i + 1;
				player.runEnergy += percentage;
				if (player.runEnergy > 100)
				{
						player.runEnergy = 100.0;
				}
				AgilityAssistant.updateRunEnergyInterface(player);

		}

		public static void drinkMagicPotion(Player player, int itemId, int replaceItem, int slot, int stat, boolean sup)
		{
				player.startAnimation(829);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);
				player.playerItems[slot] = replaceItem + 1;
				enchanceMagic(player, stat, sup);

		}

		public static void enchanceMagic(Player player, int skillID, boolean sup)
		{
				player.currentCombatSkillLevel[skillID] += 4;
				if (player.currentCombatSkillLevel[skillID] > player.getBaseMagicLevel() + 4)
				{
						player.currentCombatSkillLevel[skillID] = player.getBaseMagicLevel() + 4;
				}
				player.skillTabMainToUpdate.add(skillID);
		}

		public static void recoverSpecial(Player player, int itemId, int replaceItem, int slot)
		{
				if (Area.isWithInArea(player, 3072, 3108, 3481, 3519))
				{
						player.playerAssistant.sendMessage("You may only use this in Edgeville.");
						player.showPotionMessage = false;
						return;
				}
				if (player.getSpecialAttackAmount() == 10)
				{
						player.playerAssistant.sendMessage("Your special attack is already full.");
						player.showPotionMessage = false;
						return;
				}
				if ((player.getSpecialAttackAmount() + 2.5) > 10)
				{
						player.setSpecialAttackAmount(10, false);
				}
				else
				{
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() + 2.5, false);
				}
				player.startAnimation(829);
				player.playerAssistant.sendFilterableMessage("As you drink drink the potion, you feel your special attack slightly regenerate.");
				player.playerItems[slot] = replaceItem + 1;
				CombatInterface.updateSpecialBar(player);
		}

		public static void enchanceStat2(Player player, int skillID, boolean sup)
		{
				RegenerateSkill.storeBoostedTime(player, skillID);
				player.currentCombatSkillLevel[skillID] += getExtremeStat(player, skillID, sup);
				player.skillTabMainToUpdate.add(skillID);
		}

		public static int getExtremeStat(Player player, int skill, boolean sup)
		{
				int increaseBy = 0;
				increaseBy = (int) (player.baseSkillLevel[skill] * (player.extremeMagic ? .07 : .26)) + 1;
				if (player.currentCombatSkillLevel[skill] + increaseBy > player.baseSkillLevel[skill] + increaseBy + 1)
				{
						return player.baseSkillLevel[skill] + increaseBy - player.currentCombatSkillLevel[skill];
				}
				return increaseBy;
		}

		public static void drinkAntiPoison(Player player, int itemId, int replaceItem, int slot, long immuneLength)
		{
				player.startAnimation(829);
				player.playerItems[slot] = replaceItem + 1;
				curePoison(player, immuneLength);
		}

		public static void curePoison(Player player, long immuneLength)
		{
				player.poisonImmune = immuneLength;
				player.lastPoisonSip = System.currentTimeMillis();
		}

		public static void drinkStatPotion(Player player, int itemId, int replaceItem, int slot, int stat, boolean sup)
		{
				player.startAnimation(829);
				RegenerateSkill.storeBoostedTime(player, stat);
				if (replaceItem != 0)
				{
						player.playerItems[slot] = replaceItem + 1;
				}
				enchanceStat(player, stat, sup);
		}

		private static void ringOfTheGodsImbuedEffect(Player player)
		{
				if (player.getBasePrayerLevel() < 86)
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] += 1;
				}
				else
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] += 2;
				}
		}

		public static void drinkSuperRestore(Player player, int itemId, int replaceItem, int slot)
		{
				player.startAnimation(829);
				player.playerItems[slot] = replaceItem + 1;
				player.currentCombatSkillLevel[ServerConstants.PRAYER] += (player.getBasePrayerLevel() * .33);
				ringOfTheGodsImbuedEffect(player);
				if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > player.getBasePrayerLevel())
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
				}
				player.skillTabMainToUpdate.add(ServerConstants.PRAYER);
				restoreStats(player);
		}

		public static void drinkPrayerPotion(Player player, int itemId, int replaceItem, int slot, boolean superPrayer)
		{
				player.startAnimation(829);
				player.playerItems[slot] = replaceItem + 1;
				player.currentCombatSkillLevel[ServerConstants.PRAYER] += (player.getBasePrayerLevel() * (superPrayer ? .40 : .30));
				ringOfTheGodsImbuedEffect(player);
				if (player.currentCombatSkillLevel[ServerConstants.PRAYER] > player.getBasePrayerLevel())
				{
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
				}
				player.skillTabMainToUpdate.add(ServerConstants.PRAYER);
		}

		public static void restoreStats(Player player)
		{
				for (int j = 0; j <= 6; j++)
				{
						if (j == 5 || j == 3)
						{
								continue;
						}
						if (player.currentCombatSkillLevel[j] < player.baseSkillLevel[j])
						{
								double boost = 8.0 + (player.baseSkillLevel[j] * .25);
								boost = Math.ceil(boost);
								player.currentCombatSkillLevel[j] += (int) boost;
								if (player.currentCombatSkillLevel[j] > player.baseSkillLevel[j])
								{
										player.currentCombatSkillLevel[j] = player.baseSkillLevel[j];
								}
								player.skillTabMainToUpdate.add(j);
						}
				}
				player.getPA().setSkillLevel(ServerConstants.MAGIC, player.getCurrentCombatSkillLevel(ServerConstants.MAGIC), player.skillExperience[ServerConstants.MAGIC]);
		}

		public static void consumeAnchovyPizza(Player player, int itemId, int slot)
		{
				if (player.getDead())
				{
						player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
						return;
				}
				if (System.currentTimeMillis() - player.potDelay < 1150)
				{
						return;
				}
				if (player.duelRule[6])
				{
						player.playerAssistant.sendMessage("You may not eat in this duel.");
						player.showPotionMessage = false;
						return;
				}
				if (System.currentTimeMillis() - player.cannotEatDelay < 1700)
				{
						return;
				}

				RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				player.startAnimation(829);
				if (itemId == 2297)
				{
						player.playerItems[slot] = 2300;
				}
				else
				{
						player.playerItems[slot] = 0;
				}
				player.playerAssistant.stopAllActions();
				Combat.resetPlayerAttack(player);
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < player.getBaseHitPointsLevel())
				{
						player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += 9;
						if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > player.getBaseHitPointsLevel())
						{
								player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = player.getBaseHitPointsLevel();
						}
						player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
						RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				}
				player.playerAssistant.sendFilterableMessage("You eat the " + ItemAssistant.getItemName(itemId) + ".");
				player.soundToSend = 317;
				player.soundDelayToSend = 400;
				player.foodAte++;
				player.setInventoryUpdate(true);
				player.potDelay = System.currentTimeMillis();
				if (System.currentTimeMillis() - player.lastPotionSip <= 1300)
				{
						player.setAttackTimer(player.getAttackTimer() + 1);
						player.foodDelay = player.potDelay;
				}
				player.lastPotionSip = System.currentTimeMillis();

		}

		public static void doTheBrew(Player player, int itemId, int replaceItem, int slot)
		{
				if (player.duelRule[6])
				{
						player.playerAssistant.sendMessage("You may not eat in this duel.");
						player.showPotionMessage = false;
						return;
				}

				RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				RegenerateSkill.storeBoostedTime(player, ServerConstants.DEFENCE);
				player.startAnimation(829);
				player.playerItems[slot] = replaceItem + 1;
				int[] toDecrease = {ServerConstants.ATTACK, ServerConstants.STRENGTH, ServerConstants.RANGED, ServerConstants.MAGIC};
				for (int skillsToDecrease : toDecrease)
				{
						player.currentCombatSkillLevel[skillsToDecrease] -= getBrewStat(player, skillsToDecrease, .10);
						if (player.currentCombatSkillLevel[skillsToDecrease] <= 0)
						{
								player.currentCombatSkillLevel[skillsToDecrease] = 1;
						}
						if (player.currentCombatSkillLevel[skillsToDecrease] < player.baseSkillLevel[skillsToDecrease])
						{
								RegenerateSkill.storeBoostedTime(player, skillsToDecrease);
						}
						player.skillTabMainToUpdate.add(skillsToDecrease);
				}
				player.currentCombatSkillLevel[ServerConstants.DEFENCE] += getBrewStat(player, 1, .20);
				if (player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE) > (player.getBaseDefenceLevel() * 1.2 + 1))
				{
						player.currentCombatSkillLevel[ServerConstants.DEFENCE] = (int) (player.getBaseDefenceLevel() * 1.2);
				}
				player.skillTabMainToUpdate.add(ServerConstants.DEFENCE);

				player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += getBrewStat(player, 3, .15);
				if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > (player.getBaseHitPointsLevel() * 1.17 + 1))
				{
						player.setHitPoints((int) (player.getBaseHitPointsLevel() * 1.17));
				}

				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > 115)
				{
						player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = 115;
				}
				player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
				player.getPA().setSkillLevel(ServerConstants.MAGIC, player.getCurrentCombatSkillLevel(ServerConstants.MAGIC), player.skillExperience[ServerConstants.MAGIC]);
		}

		public static void enchanceStat(Player player, int skillID, boolean sup)
		{
				player.currentCombatSkillLevel[skillID] += getBoostedStat(player, skillID, sup);
				if (player.currentCombatSkillLevel[skillID] > 118)
				{
						player.currentCombatSkillLevel[skillID] = 118;
				}
				player.skillTabMainToUpdate.add(skillID);
		}

		public static int getBrewStat(Player player, int skill, double amount)
		{
				return (int) (player.baseSkillLevel[skill] * amount) + 2;
		}

		public static int getBoostedStat(Player player, int skill, boolean sup)
		{
				int increaseBy = 0;
				if (sup)
						increaseBy = (int) ((skill == ServerConstants.HITPOINTS ? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill]) * .20);
				else
						increaseBy = (int) ((skill == ServerConstants.HITPOINTS ? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill]) * .13) + 1;
				if (player.currentCombatSkillLevel[skill] + increaseBy > (skill == ServerConstants.HITPOINTS ? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill]) + increaseBy + 1)
				{
						return (skill == 3 ? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill]) + increaseBy - player.currentCombatSkillLevel[skill];
				}
				return increaseBy;
		}

		public static boolean isPotion(Player player, int itemId)
		{
				String name = ItemAssistant.getItemName(itemId);
				if (name.contains("glory"))
				{
						return false;
				}
				return name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)");
		}

		public static void drinkExtremePotion(Player player, int itemId, int replaceItem, int slot, int stat, boolean sup)
		{
				if (Area.inWilderness(player))
				{
						player.showPotionMessage = false;
						player.playerAssistant.sendMessage("You may not use this in the Wilderness.");
						return;
				}
				player.startAnimation(829);
				player.playerItems[slot] = replaceItem + 1;
				enchanceStat2(player, stat, sup);
				player.extremeMagic = false;
		}

		/**
		 * Drink the anti-fire potion.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity used.
		 * @param slot
		 * 			The slot of the item identity used.
		 */
		public static void drinkAntiFirePotion(Player player, int itemId, int slot, int minutes)
		{
				player.antiFirePotion = true;
				player.setAntiFirePotionTimer(minutes * 2);
				player.playerItems[slot] = itemId + 1;
				antiFirePotionEvent(player);
				player.startAnimation(829);

		}

		/**
		 * The anti-fire potion event.
		 * @param player
		 * 			The associated player.
		 */
		public static void antiFirePotionEvent(final Player player)
		{
				if (player.getAntiFirePotionTimer() == 0)
				{
						return;
				}
				if (player.antiFireEvent)
				{
						return;
				}
				if (player.getAntiFirePotionTimer() == 1)
				{
						player.playerAssistant.sendMessage(ServerConstants.PURPLE_COL + "Your anti-fire potion is about to expire.");
				}
				player.antiFireEvent = true;
				player.antiFirePotion = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								player.setAntiFirePotionTimer(player.getAntiFirePotionTimer() - 1);
								if (player.getAntiFirePotionTimer() == 1)
								{
										player.playerAssistant.sendMessage(ServerConstants.PURPLE_COL + "Your anti-fire potion is about to expire.");
								}
								if (player.getAntiFirePotionTimer() == 0)
								{
										container.stop();
								}

						}

						@Override
						public void stop()
						{
								player.antiFirePotion = false;
								player.antiFireEvent = false;
								player.playerAssistant.sendMessage(ServerConstants.PURPLE_COL + "Your anti-fire potion has expired.");
						}
				}, 50);
		}

		public static void eatKarambwan(Player player, int itemId, int slot)
		{
				if (player.getDead())
				{
						player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
						return;
				}
				if (System.currentTimeMillis() - player.karambwanDelay < 1700)
				{
						return;
				}
				if (player.duelRule[6])
				{
						player.playerAssistant.sendMessage("You may not eat in this duel.");
						player.showPotionMessage = false;
						return;
				}
				if (System.currentTimeMillis() - player.cannotEatDelay < 1700)
				{
						return;
				}

				RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				player.startAnimation(829);
				player.playerItems[slot] = 0;
				player.playerAssistant.stopAllActions();
				Combat.resetPlayerAttack(player);
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < player.getBaseHitPointsLevel())
				{
						int healAmount = 18;
						if ((player.currentCombatSkillLevel[ServerConstants.HITPOINTS] + healAmount) > player.getBaseHitPointsLevel())
						{
								healAmount = player.getBaseHitPointsLevel() - player.currentCombatSkillLevel[ServerConstants.HITPOINTS];
						}
						player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += healAmount;
						player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
						RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				}
				player.playerAssistant.sendFilterableMessage("You eat the " + ItemAssistant.getItemName(itemId) + ".");
				player.soundToSend = 317;
				player.soundDelayToSend = 400;
				player.foodAte++;
				player.setInventoryUpdate(true);
				player.karambwanDelay = System.currentTimeMillis();
				if (System.currentTimeMillis() - player.lastPotionSip <= 1300 || System.currentTimeMillis() - player.foodDelay < 1700)
				{
				}
				else
				{
						player.setAttackTimer(player.getAttackTimer() + 2);
				}

		}

}