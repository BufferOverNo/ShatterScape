package game.content.combat;

import core.Server;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.achievement.Achievements;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.AutoBuyBack;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.ItemsToInventoryDeath;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.QuestTab;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.Transform;
import game.content.prayer.QuickPrayers;
import game.content.shop.ShopAssistant;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.wildernessbonus.KillReward;
import game.content.worldevent.Tournament;
import game.item.BloodMoneyPrice;
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
 * Death stages.
 * 
 * @author MGT Madness, created on 25-11-2013.
 */
public class Death
{

		public static int findPlayerWhoDidMostDamage(Player victim)
		{
				int killer = victim.getPlayerId();
				if (System.currentTimeMillis() - victim.getTimeUnderAttackByAnotherPlayer() >= 60000)
				{
						return killer;
				}
				int damage = 0;
				for (int j = 0; j < ServerConstants.MAXIMUM_PLAYERS; j++)
				{
						if (PlayerHandler.players[j] == null)
						{
								continue;
						}
						if (j == victim.getPlayerId())
						{
								continue;
						}
						if (victim.damageTaken[j] > damage)
						{
								damage = victim.damageTaken[j];
								killer = j;
						}
				}
				return killer;
		}

		/**
		 * Apply death, once the player reaches 0 hitpoints.
		 * 
		 * @param victim
		 *        The player that died.
		 */
		public static void deathStage(final Player victim)
		{
				Player killer = null;
				if (System.currentTimeMillis() - victim.getTimeUnderAttackByAnotherPlayer() >= 5000 && !Area.inWilderness(victim))
				{
						victim.setLastAttackedBy(0);
				}
				killer = PlayerHandler.players[findPlayerWhoDidMostDamage(victim)];
				if (killer != null)
				{
						if (killer.getPlayerName().equals(victim.getPlayerName()))
						{
								killer = null;
						}
				}

				final Player killer1 = killer;

				applyDeath(victim, killer1);
				CycleEventHandler.getSingleton().addEvent(victim, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								victim.startAnimation(836);
								deathEvent(victim, killer1);
						}
				}, 2);

		}

		private static void applyDeath(Player victim, Player killer)
		{
				victim.timePrayerActivated = new long[victim.prayerActive.length];
				victim.setDead(true);
				victim.resetFaceUpdate();
				victim.resetPlayerTurn();
				victim.setUsingSpecial(false);
				victim.timeExitedWilderness = 0;
				victim.timeVictimExitedWilderness = 0;
				victim.playerIdCanAttackInSafe = 0;
				victim.playerIdAttackingMeInSafe = 0;
				InterfaceAssistant.offCityTimer(victim);
				victim.timeInPlayerCombat = 0;
				BotContent.death(victim);
				BotContent.diedToBot(victim, killer);
				boolean killerExists = killer != null;
				if (killerExists)
				{
						killer.timeExitedWilderness = 0;
						killer.timeVictimExitedWilderness = 0;
						killer.playerIdCanAttackInSafe = 0;
						killer.playerIdAttackingMeInSafe = 0;
						InterfaceAssistant.offCityTimer(killer);
						if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(killer))
						{
								killer.killedPlayerImmuneTime = System.currentTimeMillis();
						}
						killer.getPA().resetCombatTimer();
						killer.timeInPlayerCombat = 0;
						boolean faceVictim = killer.getFace() == victim.getPlayerId() + 32768;
						Combat.resetPlayerAttack(killer); // Resets the face, so we have to do the face update again.
						if (faceVictim)
						{
								killer.setFace(victim.getPlayerId() + 32768);
						}
						if (victim.getX() >= 2950 && victim.getX() <= 3390 && victim.getY() >= 3345 && victim.getY() <= 3445)
						{
								killer.setSpecialAttackAmount(10.0, false);
								CombatInterface.addSpecialBar(killer, killer.getWieldedWeapon());
						}
						if (victim.getDuelStatus() != 6 && killer.getDuelStatus() == 5)
						{
								killer.setDuelStatus(killer.getDuelStatus() + 1);
						}
				}
				Transform.unTransform(victim);
				Movement.stopMovement(victim);
				if (victim.getDuelStatus() <= 4)
				{
						if (killerExists)
						{
								if (Area.inWilderness(victim) || Area.inSafePkFightZoneAll(victim))
								{
										if (killer.getPlayerId() != victim.getPlayerId())
										{
												killer.playerAssistant.sendMessage(deathMessage(GameMode.getGameModeName(victim)));
										}
								}
						}
						victim.playerAssistant.sendMessage("Oh dear, you died.");
						if (!Area.inWilderness(victim))
						{
								victim.safeDeaths++;
								if (killerExists)
								{
										safeKillReward(killer);
								}
						}
				}
				CombatInterface.addSpecialBar(victim, victim.getWieldedWeapon());
				victim.getPA().resetFollowers();
				if (killerExists)
				{
						killer.setUnderAttackBy(-1);
						killer.attackedPlayers.clear();
				}
		}

		private static void safeKillReward(Player killer)
		{
				killer.safeKills++;
				killer.setSpecialAttackAmount(10, false);
				killer.getPA().resetCombatTimer();
				CombatInterface.addSpecialBar(killer, killer.getWieldedWeapon());
				Achievements.checkCompletionMultiple(killer, "1048 1068");
		}

		/**
		 * Give life to a dead player
		 */
		public static void respawnPlayer(Player killer, Player victim)
		{
				InterfaceAssistant.vengeanceTimer(victim, false);
				victim.lastVeng = 0;
				victim.timeExitedWildFromTarget = System.currentTimeMillis();
				TargetSystem.leftWild(victim);
				victim.setVengeance(false);
				victim.resetFaceUpdate();
				victim.runEnergy = 100;
				victim.redemptionOrWrathActivated = false;
				victim.setFrozenLength(0);
				victim.frozenBy = 0;
				victim.resetNpcIdentityAttacking();
				victim.resetPlayerIdAttacking();
				victim.playerBotCurrentKillstreak = 0;
				victim.itemsKeptOnDeathList.clear();
				if (killer != null)
				{
						//65535 means the killer did not issue a new face and is still facing the victim.
						boolean faceVictim = killer.getFace() == 65535;
						if (faceVictim)
						{
								killer.resetFaceUpdate();
						}
						killer.itemsKeptOnDeathList.clear();
				}
				diedInNonSafeArea(killer, victim);
				victim.rfdWave = 0;
				victim.overloadReboostTicks = 0;
				Combat.resetPrayers(victim);
				victim.setUnderAttackBy(0);
				victim.setNpcIndexAttackingPlayer(0);
				victim.setHitPoints(victim.getBaseHitPointsLevel());
				Combat.resetPlayerAttack(victim);
				Skull.clearSkull(victim);
				victim.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
				victim.setSpecialAttackAmount(10, false);
				CombatInterface.addSpecialBar(victim, victim.getWieldedWeapon());
				victim.getPA().requestUpdates();
				victim.timeDied = System.currentTimeMillis();
				Poison.removePoison(victim);
				Skilling.resetCombatSkills(victim);
				Combat.updatePlayerStance(victim);
				deathRespawnArea(victim, killer);
				if (victim.getDuelStatus() != 6)
				{
						victim.getTradeAndDuel().myStakedItems.clear();
				}
				AutoCast.resetAutocast(victim);
				victim.getPA().resetCombatTimer();
				victim.setUsingFightCaves(false);
				QuestTab.updateQuestTab(killer);
				QuestTab.updateQuestTab(victim);
				victim.setLastAttackedBy(0);
				if (victim.quickPray)
				{
						QuickPrayers.turnOffQuicks(victim);
				}
				ItemsKeptOnDeath.addItemsKeptOnDeath(victim);
				ItemsToInventoryDeath.addItemsAfterDeath(victim);
				victim.startAnimation(65535);
				victim.dragonSpearEffectStack.clear();
				victim.dragonSpearTicksLeft = 0;
				victim.dragonSpearEvent = false;
				AutoBuyBack.autoBuyBack(victim);
		}

		/**
		 * The area to teleport the player to.
		 * 
		 * @param victim
		 *        The associated player.
		 */
		private static void deathRespawnArea(Player victim, Player killer)
		{
				if (victim.getHeight() == 20)
				{
						Tournament.playerDied(killer, victim);
				}
				else if (Zombie.inZombieMiniGameArea(victim, victim.getX(), victim.getY()))
				{
						victim.getPA().movePlayer(3657, 3519, 0);
				}
				else if (victim.isUsingFightCaves())
				{
						victim.getPA().movePlayer(2439, 5169, 0);
				}
				else if (victim.getDuelStatus() >= 5)
				{
						duelArenaDeath(victim);
				}
				else if (Area.inSafePkFightZoneAll(victim))
				{
						victim.getPA().movePlayer(3324 + Misc.random(7), 4756 + Misc.random(3), 0);
				}
				else
				{
						if (System.currentTimeMillis() - victim.timeMovedFromDoubleDuelDeath <= 1200)
						{
								return;
						}
						victim.getPA().movePlayer(3086 + Misc.random(3), 3508 + Misc.random(4), 0);
				}
		}

		/**
		 * The death stages done through Cycle Event.
		 */
		private static void deathEvent(final Player victim, final Player killer)
		{

				CycleEventHandler.getSingleton().addEvent(victim, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								Death.respawnPlayer(killer, victim);
						}
				}, 4);

		}

		public static String deathMessage(String name)
		{
				int deathMessage = Misc.random(12);
				switch (deathMessage)
				{
						case 0:
								return "With a crushing blow, you defeat " + name + ".";
						case 1:
								return "It's a humiliating defeat for " + name + ".";
						case 2:
								return "" + name + " didn't stand a chance against you.";
						case 3:
								return "You've defeated " + name + ".";
						case 4:
								return "" + name + " regrets the day they met you in combat.";
						case 5:
								return "It's all over for " + name + ".";
						case 6:
								return "" + name + " falls before your might.";
						case 7:
								return "Can anyone defeat you? Certainly not " + name + ".";
						case 8:
								return "You were clearly a better fighter than " + name + ".";
						case 9:
								return "You pwned " + name + ".";
						case 10:
								return "You have sent " + name + " to Edgeville.";
						case 11:
								return "You owned " + name + ".";
						case 12:
								return "You demolished " + name + ".";
				}
				return null;
		}

		/**
		 * Append specific updates to the player that died in the Wilderness.
		 * 
		 * @victim The player who died.
		 */
		private static void diedInNonSafeArea(Player killer, Player victim)
		{
				if (victim.isAdministratorRank() || victim.isUsingFightCaves() || victim.getDuelStatus() == 5)
				{
						return;
				}
				if (killer != null)
				{
						if (killer.isAdministratorRank() || killer.getDuelStatus() == 6)
						{
								return;
						}
				}

				// Must be kept here or if both players die at same time, one of them will lose everything in inventory.
				if (Area.inDuelArena(victim))
				{
						return;
				}
				if (victim.getHeight() == 20)
				{
						return;
				}
				if (victim.isInZombiesMinigame())
				{
						Zombie.playerDeath(victim, false);
						return;
				}

				if (Area.inSafePkFightZoneAll(victim))
				{
						return;
				}

				if (Area.isWithInArea(victim, 1889, 1910, 5345, 5366))
				{
						return;
				}
				ItemsKeptOnDeath.getItemsKeptOnDeath(victim, false, false);
				if (killer != null)
				{
						ItemsKeptOnDeath.getItemsKeptOnDeath(killer, false, false); // Used to check if attacker has risk, used if attacker has more than 20k risk.
				}
				TargetSystem.death(victim, killer);
				ItemsKeptOnDeath.deleteItemsKeptOnDeath(victim);
				dropItemsForKiller(killer, victim);
				ItemAssistant.deleteAllItems(victim);
		}

		/**
		 * Append updates for when the player died in Duel arena.
		 * 
		 * @param victim
		 *        The associated player.
		 */
		private static void duelArenaDeath(Player victim)
		{
				victim.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)), ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
				Player killer = victim.getTradeAndDuel().getPartner();

				if (killer != null)
				{
						if (killer.getDead())
						{
								killer.safeKills++;
								killer.getTradeAndDuel().declineDuel(false);
								victim.getTradeAndDuel().declineDuel(false);
								killer.timeMovedFromDoubleDuelDeath = System.currentTimeMillis();
								killer.getPA().sendMessage("You have both lost the duel.");
								victim.getPA().sendMessage("You have both lost the duel.");
								killer.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)), ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
								victim.getPA().createPlayerHints(10, -1);
								killer.getPA().createPlayerHints(10, -1);

						}
						else
						{
								ItemTransferLog.duelWon(killer, victim);
								killer.getTradeAndDuel().duelVictory();
								killer.safeKills++;
								victim.getPA().sendMessage("You have lost the duel.");
								killer.getPA().sendMessage("You have won the duel!");
								//log here
								killer.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)), ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
								killer.getPA().createPlayerHints(10, -1);
								victim.getPA().createPlayerHints(10, -1);
						}
				}
				else
				{
						ItemTransferLog.duelWon(null, victim);
						victim.getPA().sendMessage("You have won the duel!");
						// log here
						victim.getTradeAndDuel().duelVictory();
						victim.getPA().createPlayerHints(10, -1);
				}
				victim.safeDeaths++;
		}

		public static void diedToBot(Player killer, Player victim)
		{
				Server.itemHandler.createGroundItem(killer, 526, victim.getX(), victim.getY(), 1, false, 0, true, "");
				for (int e = 0; e < victim.playerEquipment.length; e++)
				{
						int equipmentItemId = victim.playerEquipment[e];
						if (equipmentItemId <= 0)
						{
								continue;
						}
						if (ItemAssistant.isUntradeableItem(victim, equipmentItemId, "ITEM TO SHOP"))
						{
								victim.itemsToShop.add(equipmentItemId + " " + victim.playerEquipmentN[e]);
						}
						else if (ItemAssistant.isUntradeableItem(victim, equipmentItemId, "ITEM TO INVENTORY"))
						{
								victim.itemsToInventory.add(equipmentItemId + " " + victim.playerEquipmentN[e]);
						}
				}

				for (int i = 0; i < victim.playerItems.length; i++)
				{
						int item = victim.playerItems[i] - 1;
						if (item <= 0)
						{
								continue;
						}
						if (ItemAssistant.isUntradeableItem(victim, item, "ITEM TO SHOP"))
						{
								victim.itemsToShop.add(item + " " + victim.playerItemsN[i]);
						}
						else if (ItemAssistant.isUntradeableItem(victim, item, "ITEM TO INVENTORY"))
						{
								victim.itemsToInventory.add(item + " " + victim.playerItemsN[i]);
						}
				}
		}

		/**
		 * Victim drops items for the killer.
		 **/
		public static void dropItemsForKiller(Player killer, Player victim)
		{
				if (killer == null)
				{
						killer = victim;
				}
				victim.itemsToInventory.clear();
				boolean killerExists = killer.getPlayerName() != victim.getPlayerName();
				boolean victimBot = victim.isCombatBot();
				if (killer.isCombatBot())
				{
						diedToBot(killer, victim);
						return;
				}
				victim.victimBotWealth = 0;
				if (killerExists)
				{
						if (!Area.inEdgevilleWilderness(victim))
						{
								victim.timeDiedInWilderness = System.currentTimeMillis();
						}
						KillReward.giveLoot(killer, victim);
						killer.coinsPile = 0;
				}
				for (int e = 0; e < victim.playerEquipment.length; e++)
				{
						int equipmentItemId = victim.playerEquipment[e];
						if (equipmentItemId <= 0)
						{
								continue;
						}
						int equipmentItemAmount = victim.playerEquipmentN[e];

						if (victim.isBot && BloodMoneyPrice.getBloodMoneyPrice(equipmentItemId) > 0 && !victim.getPlayerName().equals("Remy E"))
						{
								continue;
						}
						killer.coinsPile += getUntradeableItemBloodMoneyDropPrice(equipmentItemId);
						victimLoot(victim, killer, equipmentItemId, equipmentItemAmount, victimBot, killerExists);
				}
				for (int i = 0; i < victim.playerItems.length; i++)
				{
						int itemId = victim.playerItems[i] - 1;
						if (itemId <= 0)
						{
								continue;
						}
						if (itemId == 18820)
						{
								RunePouch.dropRunePouchLoot(victim, killer);
						}
						int itemAmount = victim.playerItemsN[i];
						if (victim.isBot && BloodMoneyPrice.getBloodMoneyPrice(itemId) > 0 && !victim.getPlayerName().equals("Remy E"))
						{
								continue;
						}
						killer.coinsPile += getUntradeableItemBloodMoneyDropPrice(itemId);
						victimLoot(victim, killer, itemId, itemAmount, victimBot, killerExists);
				}
				if (killerExists)
				{
						if (killer.coinsPile > 0)
						{
								Server.itemHandler.createGroundItem(killer, 18644, victim.getX(), victim.getY(), killer.coinsPile, false, 0, true, victim.getPlayerName());
						}
						Server.itemHandler.createGroundItem(killer, 526, victim.getX(), victim.getY(), 1, false, 0, true, victim.getPlayerName());
				}
				LootingBag.lootingBagDeath(victim, killer, killerExists);
		}

		/**
		 * Get the blood money amount that will be dropped for the player when the victim risks an untradeable item that drops blood money.
		 * @param itemId
		 * @return
		 */
		private static int getUntradeableItemBloodMoneyDropPrice(int itemId)
		{
				if (ItemAssistant.isUntradeableItem(null, itemId, "ITEM TO SHOP"))
				{
						return BloodMoneyPrice.getBloodMoneyPrice(itemId) / 10;
				}
				return 0;
		}

		/**
		* Used for when a player dies and i have to figure out what to do with the player's inventory items, equipment & looting bag contents.
		* This sorts out the coinPile, items to shop, create item on ground, bone on ground & create normal version of item on ground.
		*/
		public static void victimLoot(Player victim, Player killer, int itemId, int amount, boolean victimBot, boolean killerExists)
		{
				if (GameMode.getGameMode(killer, "IRON MAN") && !killer.getPlayerName().equals(victim.getPlayerName()))
				{
						return;
				}
				if (GameMode.getGameMode(victim, "IRON MAN") && !killer.getPlayerName().equals(victim.getPlayerName()))
				{
						return;
				}
				/* Untradeables turn to coins for the killer.
				if (getItemCoinAmount(victim, itemId) > 0)
				{
						killer.coinsPile += getItemCoinAmount(victim, itemId);
				}
				*/
				if (killerExists)
				{
						if (ItemAssistant.isUntradeableItem(victim, itemId, "ITEM TO SHOP"))
						{
								victim.itemsToShop.add(itemId + " " + amount);
						}
						else if (ItemAssistant.isUntradeableItem(victim, itemId, "ITEM TO INVENTORY"))
						{
								victim.itemsToInventory.add(itemId + " " + amount);
						}
						else
						{
								// Lava dragon constant drops and green dragons.
								if (itemId == 1753 || itemId == 536 || itemId == 18821 || itemId == 18823 || itemId == 1747)
								{
										killer.coinsPile += ShopAssistant.getSellToShopPrice(itemId);
										return;
								}
						}

				}

				if (!ItemAssistant.isUntradeableItem(victim, itemId, "PLAYER KILLED LOOT"))
				{
						Blowpipe.diedWithBlowpipe(victim, killer, itemId);
						Server.itemHandler.createGroundItem(killer, itemId, victim.getX(), victim.getY(), amount, false, 0, true, victim.getPlayerName());
				}
				else if (!killerExists) // If item is untradeable and the killer does not exist.
				{
						if (ItemAssistant.isUntradeableItem(victim, itemId, "ITEM TO SHOP"))
						{
								victim.itemsToShop.add(itemId + " " + amount);
						}
						else if (ItemAssistant.isUntradeableItem(victim, itemId, "ITEM TO INVENTORY"))
						{
								victim.itemsToInventory.add(itemId + " " + amount);
						}
						else
						{
								Server.itemHandler.createGroundItem(killer, itemId, victim.getX(), victim.getY(), amount, false, 0, true, victim.getPlayerName());
						}
				}

				ItemAssistant.createNormalItemVersion(killer, victim, itemId);
		}

}