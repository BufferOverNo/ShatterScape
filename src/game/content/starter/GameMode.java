package game.content.starter;


import java.util.ArrayList;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.combat.Combat;
import game.content.highscores.HighscoresAdventurer;
import game.content.highscores.HighscoresHallOfFame;
import game.content.highscores.HighscoresTotalLevel;
import game.content.highscores.HighscoresTotalLevelIronMan;
import game.content.highscores.HighscoresTotalLevelVeteran;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.QuestTab;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Game modes.
 * @author MGT Madness, created on 05-07-2015.
 */
public class GameMode
{


		/**
		 * 0.002% means 2% rare drop increase, so a 100 drop rate because 98.
		 */
		public static double VETERAN_DROP_CHANCE_INCREASE = 0.05;

		public static double GLADIATOR_DROP_CHANCE_INCREASE = 0.10;

		public static double IRON_MAN_DROP_CHANCE_INCREASE = 0.15;

		// Immortal will be 20% and HC Iron Man 25 and Ultimate Iron Man 30
		// Immortal + Legendary Donator is 32% Droprate, which is decent, because Immortal takes a long time to max out for bossing.

		public static int getDropRate(Player player, int chance)
		{
				double modifier = 1.0;
				double chanceNew = (double) chance;
				/*
				if (GameMode.getGameMode(player, "VETERAN"))
				{
						modifier -= GameMode.VETERAN_DROP_CHANCE_INCREASE;
				}
				else if (GameMode.getGameMode(player, "GLADIATOR"))
				{
						modifier -= GameMode.GLADIATOR_DROP_CHANCE_INCREASE;
				}
				else if (GameMode.getGameMode(player, "IRON MAN"))
				{
						modifier -= GameMode.IRON_MAN_DROP_CHANCE_INCREASE;
				}
				
				if (player.isLegendaryDonator())
				{
						modifier -= 0.12;
				}
				else if (player.isExtremeDonator())
				{
						modifier -= 0.09;
				}
				else if (player.isSuperDonator())
				{
						modifier -= 0.06;
				}
				else if (player.isDonator())
				{
						modifier -= 0.03;
				}
				*/
				chanceNew *= modifier;
				return (int) chanceNew;
		}



		private final static int[] UNTRADEABLE_ITEMS = {
		//@formatter:off
		3840, // Holy book.
		3842, // Unholy book.
		3844, // Book of balance.
		8844, 8845, 8846, 8847, 8848, 8849, 8850, 18759, // All defenders.
		7454, 7455, 7456, 7457, 7458, 7459, 7460, 7461, 7462, // All gloves. Such as barrows gloves etc..
		2528, // Prayer lamp.
		//@formatter:on
		};

		public static void sendDescription(Player player, String starterType)
		{
				ArrayList<String> text = new ArrayList<String>();
				if (starterType.equals("DEFENDER"))
				{
						text.add("- x150 experience rate.");
						text.add("- Defender tag when players");
						text.add("right click your character.");
						text.add("- Ability to set combat levels after");
						text.add("maxed combat.");
						text.add("- Start with 99 Strength, Ranged, Magic");
						text.add("and Hitpoints.");
						text.add("- Prayer XP lamp worth 740k xp for");
						text.add("70 prayer.");
						text.add("- Instant access to combat untradeables.");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
				}
				else if (starterType.equals("VETERAN"))
				{
						text.add("- x150 experience rate.");
						text.add("- Veteran tag when players");
						text.add("right click your character.");
						text.add("- Ability to set combat levels after");
						text.add("maxed combat.");
						text.add("- Access to Skiller highscores.");
						text.add("- Access to Adventurer highscores.");
						text.add("- " + (int) (VETERAN_DROP_CHANCE_INCREASE * 100) + "% increased rare drop chance.");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
						text.add("");
				}
				else if (starterType.equals("GLADIATOR"))
				{
						text.add("- x30 experience rate.");
						text.add("- Gladiator tag when players");
						text.add("right click your character.");
						text.add("- Ability to set combat levels after");
						text.add("maxed combat.");
						text.add("- Access to Skiller highscores.");
						text.add("- Access to Adventurer highscores.");
						text.add("- " + (int) (GLADIATOR_DROP_CHANCE_INCREASE * 100) + "% increased rare drop chance.");
						text.add("- 50 Extra bank slots");
						text.add("- Bonus Agility points");
						text.add("- Bonus Vote points");
						text.add("- This a challenging game mode.");
						text.add("");
						text.add("");
						text.add("");
				}
				else if (starterType.equals("IRON MAN"))
				{
						text.add("- x30 experience rate.");
						text.add("- Iron Man tag when players");
						text.add("right click your character.");
						text.add("- Ability to set combat levels after");
						text.add("maxed combat.");
						text.add("- Access to Skiller highscores.");
						text.add("- Access to Adventurer highscores.");
						text.add("- " + (int) (IRON_MAN_DROP_CHANCE_INCREASE * 100) + "% increased rare drop chance.");
						text.add("- 800 Extra bank slots");
						text.add("- Bonus Agility points");
						text.add("- Bonus Vote points");
						text.add("- This an extremely challenging game mode.");
						text.add("- <img=9> Iron Man crown next to your name.");
						text.add("");
						text.add("");
				}
				for (int index = 0; index < text.size(); index++)
				{
						player.getPA().sendFrame126(text.get(index), 25129 + index);
				}
		}

		public static void changeGameMode(Player player)
		{
				player.selectedGameMode = player.gameMode;
				player.canUseGameModeInterface = true;
				player.getPA().sendMessage(":gamemode" + player.gameMode);
				sendDescription(player, "VETERAN");
				player.getPA().displayInterface(25100);
		}

		/**
		 * Example: The 'Gladiator' MGT Madness
		 * @return
		 * 			The player's game mode.
		 */
		public static String getGameModeName(Player player)
		{
				String gameMode = player.gameMode;
				if (gameMode.isEmpty())
				{
						return player.getCapitalizedName();
				}
				else
				{
						return "The '" + Misc.capitalize(gameMode) + "' " + player.getCapitalizedName();
				}
		}

		/**
		 * @return
		 * 			True, if the gameMode String given, matches the player's game mode.
		 */
		public static boolean getGameMode(Player player, String gameMode)
		{
				if (player.gameMode.equals(gameMode))
				{
						return true;
				}
				return false;
		}

		/**
		 * Append action of the game mode interface button.
		 * @param player
		 * 			The associated player.
		 * @param buttonId
		 * 			The button identity used.
		 * @return
		 * 			True, if the button belongs to the game mode interface.
		 */
		public static boolean isGameModeButton(Player player, int buttonId)
		{
				switch (buttonId)
				{
						case 98021:
								if (player.isTutorialComplete() && !player.canUseGameModeInterface)
								{
										return false;
								}
								if (!player.canUseGameModeInterface)
								{
										player.gameMode = "DEFENDER";
										player.gameModeTitle = "[Defender]";
								}
								else
								{
										player.selectedGameMode = "DEFENDER";
								}
								sendDescription(player, "DEFENDER");
								return true;
						case 98025:
								if (player.isTutorialComplete() && !player.canUseGameModeInterface)
								{
										Misc.print(player.getPlayerName() + " has force opened the game mode interface.");
										return false;
								}
								if (!player.canUseGameModeInterface)
								{
										player.gameMode = "VETERAN";
										player.gameModeTitle = "[Veteran]";
								}
								else
								{
										player.selectedGameMode = "VETERAN";
								}
								sendDescription(player, "VETERAN");
								return true;
						case 98029:
								if (player.isTutorialComplete() && !player.canUseGameModeInterface)
								{
										return false;
								}
								if (!player.canUseGameModeInterface)
								{
										player.gameMode = "GLADIATOR";
										player.gameModeTitle = "[Gladiator]";
								}
								else
								{
										player.selectedGameMode = "GLADIATOR";
								}
								sendDescription(player, "GLADIATOR");
								return true;
						case 98033:
								if (player.isTutorialComplete() && !player.canUseGameModeInterface)
								{
										return false;
								}
								if (!player.canUseGameModeInterface)
								{
										player.gameMode = "IRON MAN";
										player.gameModeTitle = "[Iron Man]";
								}
								else
								{
										player.selectedGameMode = "IRON MAN";
								}
								sendDescription(player, "IRON MAN");
								return true;

						case 98037:
								if (player.isTutorialComplete() && !player.canUseGameModeInterface)
								{
										return false;
								}
								if (!player.canUseGameModeInterface)
								{
										player.setTutorialComplete(true);
										NewPlayerContent.endTutorial(player);
										QuestTab.updateQuestTab(player);
										if (GameMode.getGameMode(player, "IRON MAN"))
										{
												player.playerRights = 9;
										}
								}
								else
								{
										if (player.gameMode.equals(player.selectedGameMode))
										{
												player.getDH().sendStatement("Your game mode has not been changed.");
										}
										else
										{
												sendGameModeChangeNotice(player);
										}
								}
								return true;
				}

				return false;
		}

		public static void sendGameModeChangeNotice(Player player)
		{
				String notice = "SAME GAMEMODE";
				switch (player.gameMode)
				{
						case "DEFENDER":
								if (player.selectedGameMode.equals("VETERAN"))
								{
										notice = "Combat skills reset, God books, Defenders & Gloves will be removed.";
								}
								else if (player.selectedGameMode.equals("GLADIATOR"))
								{
										notice = "All skills reset, God books, Defenders & Gloves will be removed.";
								}
								break;
						case "VETERAN":
								if (player.selectedGameMode.equals("DEFENDER"))
								{
										notice = "";
								}
								else if (player.selectedGameMode.equals("GLADIATOR"))
								{
										notice = "All skills will be reset.";
								}
								break;
						case "GLADIATOR":
								if (player.selectedGameMode.equals("DEFENDER"))
								{
										notice = "";
								}
								else if (player.selectedGameMode.equals("VETERAN"))
								{
										notice = "";
								}
								break;

						case "IRON MAN":
								if (!player.selectedGameMode.equals("IRON MAN"))
								{
										notice = "All your items and skills will be wiped.";
								}
								break;
				}
				if (player.selectedGameMode.equals("IRON MAN") && !player.gameMode.equals("IRON MAN"))
				{
						notice = "All your items and skills will be wiped.";
				}
				if (!notice.isEmpty())
				{
						player.getDH().sendStatement(notice);
						player.nextDialogue = 252;
				}
				else
				{
						player.getDH().sendDialogues(252);
				}
		}

		/**
		 * @param skillIndexMaximum
		 * 			Reset all skills until this skill index.
		 */
		private static void resetSkills(Player player, int skillIndexMaximum)
		{
				skillIndexMaximum++;
				for (int index = 0; index < skillIndexMaximum; index++)
				{
						int level = index == ServerConstants.HITPOINTS ? 10 : 1;
						if (index < 7)
						{
								player.currentCombatSkillLevel[index] = level;
								player.combatExperienceGainedAfterMaxed[index] = 0;
						}
						player.baseSkillLevel[index] = level;
						player.skillExperience[index] = Skilling.getExperienceForLevel(level);
				}
				Combat.resetPrayers(player);
				player.setHitPoints(player.getBaseHitPointsLevel());
				Skilling.updateSkillTabExperienceHover(player, -1, true);
				Skilling.updateAllSkillTabFrontText(player);
				Skilling.updateTotalLevel(player);
				player.playerAssistant.calculateCombatLevel();
				InterfaceAssistant.updateCombatLevel(player);
				for (int i = 0; i < skillIndexMaximum; i++)
				{
						player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
				}
				Combat.refreshCombatSkills(player);
		}

		/**
		 * Delete God books, Defenders, Gloves from the account.
		 * @param player
		 * 	 	The associated player.
		 */
		private static void removeUntradeables(Player player, boolean deleteAll)
		{
				// Lag test on a filled account takes 0ms.

				// Scan inventory, equipment & bank.

				for (int index = 0; index < player.playerItems.length; index++)
				{
						if (deleteAll)
						{
								player.playerItems[index] = 0;
								continue;
						}
						for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
						{
								if ((player.playerItems[index] - 1) == UNTRADEABLE_ITEMS[i])
								{
										player.playerItems[index] = 0;
								}
						}
				}

				for (int index = 0; index < player.playerEquipment.length; index++)
				{
						if (deleteAll)
						{
								ItemAssistant.deleteEquipment(player, player.playerEquipment[index], index);
								continue;
						}
						for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
						{
								if ((player.playerEquipment[index]) == UNTRADEABLE_ITEMS[i])
								{
										ItemAssistant.deleteEquipment(player, player.playerEquipment[index], index);
								}
						}
				}

				for (int index = 0; index < player.bankItems.length; index++)
				{
						if (player.bankItems[index] > 0)
						{
								if (deleteAll)
								{
										player.bankItems[index] = 0;
										continue;
								}
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems1.length; index++)
				{
						if (player.bankItems1[index] > 0)
						{
								if (deleteAll)
								{
										player.bankItems1[index] = 0;
										continue;
								}
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems1[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems1[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems2.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems2[index] = 0;
								continue;
						}
						if (player.bankItems2[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems2[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems2[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems3.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems3[index] = 0;
								continue;
						}
						if (player.bankItems3[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems3[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems3[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems4.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems4[index] = 0;
								continue;
						}
						if (player.bankItems4[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems4[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems4[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems5.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems5[index] = 0;
								continue;
						}
						if (player.bankItems5[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems5[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems5[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems6.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems6[index] = 0;
								continue;
						}
						if (player.bankItems6[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems6[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems6[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems7.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems7[index] = 0;
								continue;
						}
						if (player.bankItems7[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems7[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems7[index] = 0;
										}
								}
						}
				}
				for (int index = 0; index < player.bankItems8.length; index++)
				{
						if (deleteAll)
						{
								player.bankItems8[index] = 0;
								continue;
						}
						if (player.bankItems8[index] > 0)
						{
								for (int i = 0; i < UNTRADEABLE_ITEMS.length; i++)
								{
										if ((player.bankItems8[index] - 1) == UNTRADEABLE_ITEMS[i])
										{
												player.bankItems8[index] = 0;
										}
								}
						}
				}
				player.getPA().requestUpdates();
				ItemAssistant.updateSlot(player, ServerConstants.HAND_SLOT);
				ItemAssistant.updateSlot(player, ServerConstants.SHIELD_SLOT);
				player.setInventoryUpdate(true);
		}

		public static void confirmChangeGameMode(Player player)
		{
				switch (player.gameMode)
				{
						case "DEFENDER":
								if (player.selectedGameMode.equals("VETERAN"))
								{
										resetSkills(player, 6);
										player.setAbleToEditCombat(false);
										removeUntradeables(player, false);
								}
								else if (player.selectedGameMode.equals("GLADIATOR"))
								{
										resetSkills(player, 20);
										for (int index = 0; index < player.combatExperienceGainedAfterMaxed.length; index++)
										{
												player.combatExperienceGainedAfterMaxed[index] = 0;
										}
										player.setAbleToEditCombat(false);
										removeUntradeables(player, false);
								}
								break;
						case "VETERAN":
								if (player.selectedGameMode.equals("GLADIATOR"))
								{
										resetSkills(player, 20);
										for (int index = 0; index < player.combatExperienceGainedAfterMaxed.length; index++)
										{
												player.combatExperienceGainedAfterMaxed[index] = 0;
										}
										player.setAbleToEditCombat(false);
								}
								break;
				}
				if (player.selectedGameMode.equals("IRON MAN"))
				{
						removeUntradeables(player, true);
						resetSkills(player, 20);
						for (int a = 0; a < NewPlayerContent.IRON_MAN_KIT.length; a++)
						{
								ItemAssistant.addItemToInventoryOrDrop(player, NewPlayerContent.IRON_MAN_KIT[a][0], NewPlayerContent.IRON_MAN_KIT[a][1]);
						}
						for (int index = 0; index < player.combatExperienceGainedAfterMaxed.length; index++)
						{
								player.combatExperienceGainedAfterMaxed[index] = 0;
						}
						player.setAbleToEditCombat(false);
						player.playerRights = 9;
				}
				if (player.gameMode.equals("GLADIATOR") && !player.selectedGameMode.equals("GLADIATOR"))
				{
						HighscoresTotalLevel.getInstance().removePlayer(player.getPlayerName());
				}
				if (player.gameMode.equals("IRON MAN") && !player.selectedGameMode.equals("IRON MAN"))
				{
						HighscoresTotalLevelIronMan.getInstance().removePlayer(player.getPlayerName());
						player.playerRights = 0;
						removeUntradeables(player, true);
						resetSkills(player, 20);
						player.setAbleToEditCombat(false);
						for (int index = 0; index < player.combatExperienceGainedAfterMaxed.length; index++)
						{
								player.combatExperienceGainedAfterMaxed[index] = 0;
						}
						// Do not give starter kit because they can just get starter kit, transfer items through wild with a partner killing them, then turn to iron man and repeat.
				}

				if (!player.gameMode.equals("DEFENDER") && player.selectedGameMode.equals("DEFENDER"))
				{
						HighscoresTotalLevelVeteran.getInstance().removePlayer(player.getPlayerName());
						HighscoresTotalLevel.getInstance().removePlayer(player.getPlayerName());
						HighscoresTotalLevelIronMan.getInstance().removePlayer(player.getPlayerName());
						HighscoresAdventurer.getInstance().removePlayer(player.getPlayerName());

						int level = 75;
						int skill = ServerConstants.ATTACK;
						if (player.getBaseAttackLevel() < level)
						{
								player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
								player.currentCombatSkillLevel[skill] = level;
								player.baseSkillLevel[skill] = level;
						}

						level = 99;
						skill = ServerConstants.STRENGTH;
						if (player.getBaseStrengthLevel() < level)
						{
								player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
								player.currentCombatSkillLevel[skill] = level;
								player.baseSkillLevel[skill] = level;
						}

						level = 99;
						skill = ServerConstants.RANGED;
						if (player.getBaseRangedLevel() < level)
						{
								player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
								player.currentCombatSkillLevel[skill] = level;
								player.baseSkillLevel[skill] = level;
						}

						level = 99;
						skill = ServerConstants.MAGIC;
						if (player.getBaseMagicLevel() < level)
						{
								player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
								player.currentCombatSkillLevel[skill] = level;
								player.baseSkillLevel[skill] = level;
						}

						level = 52;
						skill = ServerConstants.PRAYER;
						if (player.getBasePrayerLevel() < level)
						{
								player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
								player.currentCombatSkillLevel[skill] = level;
								player.baseSkillLevel[skill] = level;
						}
						Combat.refreshCombatSkills(player);


						EditCombatSkill.calculateHitPoints(player);
				}
				ItemAssistant.addItem(player, 1856, 1);
				player.gameMode = player.selectedGameMode;
				player.gameModeTitle = "[" + Misc.capitalize(player.gameMode) + "]";
				player.getDH().sendStatement("You are now a " + Misc.capitalize(player.gameMode) + ".");
				QuestTab.updateQuestTab(player);
				Combat.resetPrayers(player);
		}

		/**
		 * Announce to everyone that the player has achieved maxed combat.
		 * @param player
		 * 			The associated player.
		 */
		public static void announceMaxedCombatOrMaxedTotal(Player player)
		{
				if (player.getCombatLevel() == 126 && player.baseSkillLevel[ServerConstants.PRAYER] == 99 && player.loggingInFinished && !player.getAbleToEditCombat())
				{
						player.setAbleToEditCombat(true);
						player.playerAssistant.announce(GameMode.getGameModeName(player) + " has finally achieved maxed combat.");
						player.getPA().sendScreenshot("maxed combat", 2);
				}

				if (Skilling.getOriginalTotalLevel(player) == 2079 && !player.announceMaxLevel)
				{
						HighscoresHallOfFame.enterToHallOfFame(player, "2079 Total");
						PlayerTitle.checkCompletionSingle(player, 53);
						Achievements.checkCompletionSingle(player, 1152);
						player.playerAssistant.announce(GameMode.getGameModeName(player) + " has achieved maximum total level.");
						player.getPA().sendScreenshot("maxed total level", 2);
						player.announceMaxLevel = true;
				}

		}

}