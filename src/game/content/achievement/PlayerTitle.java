package game.content.achievement;

import core.ServerConstants;
import game.content.highscores.HighscoresHallOfFame;
import game.content.interfaces.InterfaceAssistant;
import game.content.profile.ProfileRank;
import game.content.starter.GameMode;
import game.content.title.TitleDefinitions;
import game.player.Player;
import utility.Misc;

/**
 *  Title interface actions and updating.
 * @author MGT Madness, created on 09-07-2015.
 */
public class PlayerTitle
{

		/**
		 * Update the current title interface frame.
		 * @param player
		 * 			The associated player.
		 */
		private static void updateCurrentTitle(Player player)
		{
				player.getPA().sendFrame126(player.playerTitle.isEmpty() ? "No title set" : player.playerTitle, 19382);
		}

		public static boolean containsTitle(String title, String contains)
		{
				if (title.toLowerCase().contains(contains.toLowerCase()))
				{
						return true;
				}
				return false;
		}

		public static void updateLatestTitleForPking(Player player, String titleType)
		{
				int currentProgress = 0;
				if (titleType.contains("Berserker"))
				{
						currentProgress = player.getBerserkerPureKills();
				}
				else if (titleType.contains("Pure"))
				{
						currentProgress = player.getPureKills();
				}
				else if (titleType.contains("Melee"))
				{
						currentProgress = player.getMeleeMainKills();
				}
				else if (titleType.contains("Hybrid"))
				{
						currentProgress = player.getHybridKills();
				}
				else if (titleType.contains("Ranged"))
				{
						currentProgress = player.getRangedTankKills();
				}
				boolean hasHitMilestone = false;
				for (int i = Achievements.ACHIEVEMENT_KILLS_LIST.length - 1; i >= 0; i--)
				{
						if (currentProgress == Achievements.ACHIEVEMENT_KILLS_LIST[i])
						{
								hasHitMilestone = true;
								break;
						}
				}
				if (!hasHitMilestone)
				{
						return;
				}
				int highestTitleRankIndex = 0;
				for (int i = Achievements.ACHIEVEMENT_KILLS_LIST.length - 1; i > 0; i--)
				{
						if (currentProgress >= Achievements.ACHIEVEMENT_KILLS_LIST[i])
						{
								highestTitleRankIndex = i;
								break;
						}
				}

				ProfileRank.rankPopUp(player, "PKER");
				for (int index = 0; index < TitleDefinitions.getDefinitions().length; index++)
				{
						if (TitleDefinitions.getDefinitions()[index] == null)
						{
								continue;
						}
						if (TitleDefinitions.getDefinitions()[index].title.equals(titleType + " " + titles1[highestTitleRankIndex]))
						{
								PlayerTitle.checkCompletionSingle(player, TitleDefinitions.getDefinitions()[index].titleId);
						}
				}
				PlayerTitle.setTitle(player, titleType + " " + titles1[highestTitleRankIndex], false);
		}

		/**
		 * Change the player's title.
		 * @param player
		 * 			The associated player.
		 * @param title
		 * 			The title identity to change to.
		 */
		public static void setTitle(Player player, String title, boolean afterName)
		{
				if (afterName)
				{
						player.titleSwap = 1;
				}
				else
				{
						player.titleSwap = 0;
				}
				player.playerTitle = title;
				PlayerTitle.updateCurrentTitle(player);
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				if (!title.isEmpty())
				{
						player.getPA().sendMessage("Title has been set to " + title + ".");
				}

				if (title.equals("Gladiator") || title.equals("Commander") || title.equals("War-chief") || title.equals("Immortal"))
				{
						player.titleColour = "<col=bb44aa>";
				}
				else
				{
						player.titleColour = "<col=ED700E>";
				}
		}

		/**
		 * 
		 * @param player
		 * @param buttonId
		 */
		public static boolean playerTitleInterfaceAction(Player player, int buttonId)
		{

				if (buttonId == 75162)
				{
						player.getPA().sendMessage(ServerConstants.RED_COL + "Skilling tab is disabled.");
						return true;
				}
				if (buttonId >= 75162 && buttonId <= 75174)
				{
						tabClicked(player, buttonId);
						return true;
				}
				if (buttonId >= 75184 && buttonId <= 75223)
				{
						titleClicked(player, buttonId);
						return true;
				}
				switch (buttonId)
				{
						case 87093:
								if (player.titleTab == -1)
								{
										tabClicked(player, 75166);
								}
								displayInterface(player);
								return true;

						case 75232:
								equipTitle(player);
								break;

						case 75236:
								clearTitle(player);
								break;
				}

				return false;
		}

		private static void clearTitle(Player player)
		{
				setTitle(player, "", false);
				player.getPA().sendMessage("Title has been cleared.");
		}


		static String[] titles = {"Melee", "Hybrid", "Berserker", "Pure", "Ranged"};

		static String[] titles1 = {"Cadet", "Soldier", "Sergeant", "General", "Legend"};

		private static void equipTitle(Player player)
		{
				String title = TitleDefinitions.getDefinitions()[player.titleIndexClicked].title;
				if (!isTitleUnlocked(player, TitleDefinitions.getDefinitions()[player.titleIndexClicked].titleId))
				{
						player.getPA().sendMessage(title + " is locked.");
						return;
				}
				if (!title.contains("Legend"))
				{
						for (int index = 0; index < titles.length; index++)
						{
								if (title.contains(titles[index]))
								{
										int currentProgress = 0;
										if (title.contains("Berserker"))
										{
												currentProgress = player.getBerserkerPureKills();
										}
										else if (title.contains("Pure"))
										{
												currentProgress = player.getPureKills();
										}
										else if (title.contains("Melee"))
										{
												currentProgress = player.getMeleeMainKills();
										}
										else if (title.contains("Hybrid"))
										{
												currentProgress = player.getHybridKills();
										}
										else if (title.contains("Ranged"))
										{
												currentProgress = player.getRangedTankKills();
										}
										int highestTitleRankIndex = 0;
										for (int i = Achievements.ACHIEVEMENT_KILLS_LIST.length - 1; i > 0; i--)
										{
												if (currentProgress >= Achievements.ACHIEVEMENT_KILLS_LIST[i])
												{
														highestTitleRankIndex = i;
														break;
												}
										}
										if (!title.contains(titles1[highestTitleRankIndex]))
										{
												player.getPA().sendMessage("You cannot use a lower title rank.");
												return;
										}
										break;
								}
						}
				}
				setTitle(player, title, TitleDefinitions.getDefinitions()[player.titleIndexClicked].titleAfterName);
		}

		public static void displayInterface(Player player)
		{
				tabClicked(player, 75166);
				updateCurrentTitle(player);
				updateCompletedAmount(player);
				showTitleScroll(player);
				player.getPA().displayInterface(19360);

		}

		private static void updateCompletedAmount(Player player)
		{
				// Unlocked tab.
				if (player.titleTab == 3)
				{
						player.getPA().sendFrame126("", 19446);
						return;
				}
				int amount = 0;
				if (player.titleTab == 0)
				{
						amount = TitleDefinitions.skillingTitlesIndex[1] + 1;
				}
				else if (player.titleTab == 1)
				{
						amount = (TitleDefinitions.pkingTitlesIndex[1] - TitleDefinitions.pkingTitlesIndex[0]) + 1;
				}
				else if (player.titleTab == 2)
				{
						amount = (TitleDefinitions.miscTitlesIndex[1] - TitleDefinitions.miscTitlesIndex[0]) + 1;
				}

				player.getPA().sendFrame126("Unlocked: " + player.titleTotal[player.titleTab] + "/" + amount, 19446);
		}

		public static boolean isTitleUnlocked(Player player, int titleId)
		{
				for (int index = 0; index < player.titlesUnlocked.size(); index++)
				{
						if (Integer.toString(titleId).equals(player.titlesUnlocked.get(index)))
						{
								return true;
						}
				}
				return false;
		}

		private static void titleReward(Player player, int titleId, int titleIndex)
		{
				player.titlesUnlocked.add(Integer.toString(titleId));
				player.gfx100(199);
				AchievementStatistics.showAchievementPopup(player, TitleDefinitions.getDefinitions()[titleIndex].title + " unlocked!", "Congratulations!", TitleDefinitions.getDefinitions()[titleIndex].title);
				player.playerAssistant.announce(GameMode.getGameModeName(player) + " has unlocked " + TitleDefinitions.getDefinitions()[titleIndex].title + " title.");
				player.getPA().sendScreenshot(TitleDefinitions.getDefinitions()[titleIndex].title, 2);
				if (titleIndex <= TitleDefinitions.skillingTitlesIndex[1])
				{
						player.titleTotal[0]++;
				}
				else if (titleIndex <= TitleDefinitions.pkingTitlesIndex[1])
				{
						player.titleTotal[1]++;
				}
				else if (titleIndex <= TitleDefinitions.miscTitlesIndex[1])
				{
						player.titleTotal[2]++;
				}

				// Check if all titles completed.


				int amount = 0;

				amount = TitleDefinitions.skillingTitlesIndex[1] + 1;
				if (player.titleTotal[0] < amount)
				{
						return;
				}

				amount = (TitleDefinitions.pkingTitlesIndex[1] - TitleDefinitions.pkingTitlesIndex[0]) + 1;
				if (player.titleTotal[1] < amount)
				{
						return;
				}
				amount = (TitleDefinitions.miscTitlesIndex[1] - TitleDefinitions.miscTitlesIndex[0]) + 1;
				if (player.titleTotal[2] < amount)
				{
						return;
				}

				HighscoresHallOfFame.enterToHallOfFame(player, "All Titles");
		}

		public static void checkCompletionSingle(Player player, int titleId)
		{
				if (isTitleUnlocked(player, titleId))
				{
						return;
				}
				int titleIndex = TitleDefinitions.titleIdAndDefinitionIndex.get(titleId);
				if (TitleDefinitions.titleIdAndDefinitionIndex.get(titleId) == null)
				{
						return;
				}
				titleReward(player, titleId, titleIndex);
		}

		public static void checkCompletionMultiple(Player player, String titleIdList, String titletSaveName)
		{
				String string[] = titleIdList.split(" ");
				for (int index = 0; index < string.length; index++)
				{
						int titleId = Integer.parseInt(string[index]);
						int progress = getCurrentTitleProgress(player, titleId, titletSaveName);
						if (isTitleUnlocked(player, titleId))
						{
								continue;
						}
						int titleIndex = TitleDefinitions.titleIdAndDefinitionIndex.get(titleId);
						if (TitleDefinitions.titleIdAndDefinitionIndex.get(titleId) == null)
						{
								return;
						}
						if (progress >= TitleDefinitions.getDefinitions()[titleIndex].completeAmount)
						{
								titleReward(player, titleId, titleIndex);
						}
				}
		}

		public static int getCurrentTitleProgress(Player player, int titleId, String titleSaveName)
		{

				if (titleId >= 32 && titleId <= 52)
				{
						return player.skillExperience[titleId - 32];
				}
				switch (titleId)
				{

						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
								return player.getMeleeMainKills();
						case 12:
						case 13:
						case 14:
						case 15:
						case 16:
								return player.getHybridKills();
						case 17:
						case 18:
						case 19:
						case 20:
						case 21:
								return player.getBerserkerPureKills();

						case 22:
						case 23:
						case 24:
						case 25:
						case 26:
								return player.getPureKills();

						case 27:
						case 28:
						case 29:
						case 30:
						case 31:
								return player.getRangedTankKills();

						case 53:
								return player.getTotalLevel();

						case 54:
						case 55:
						case 56:
						case 57:
						case 58:
						case 59:
								return player.getBarrowsRunCompleted();

						case 66:
						case 67:
						case 68:
						case 69:
						case 70:
						case 71:
								return player.bossScoreUnCapped;

						case 72:
						case 73:
						case 74:
						case 75:
								return player.playerBotKills;

						case 76:
						case 77:
						case 78:
						case 79:
								return player.getWildernessKills();

						case 60:
						case 61:
						case 62:
						case 63:
						case 64:
						case 65:
								return Achievements.getArraylistCount("rareDrops", player.achievementProgress);
				}
				return Achievements.getArraylistCount(titleSaveName, player.achievementProgress);
		}

		public static void tabClicked(Player player, int buttonId)
		{
				int tabIndex = (buttonId - 75162);
				player.titleTab = tabIndex / 4;
				player.getPA().setInterfaceClicked(19362 + tabIndex, true);
				showTitleScroll(player);
				player.getPA().sendFrame126("", 19447);
				player.getPA().sendMessage(":packet:titlepercentage 0");

		}

		private static void showTitleScroll(Player player)
		{
				int minimumIndex = 0;
				int maximumIndex = 0;
				if (player.titleTab == 0)
				{
						minimumIndex = TitleDefinitions.skillingTitlesIndex[0];
						maximumIndex = TitleDefinitions.skillingTitlesIndex[1] + 1;
				}
				else if (player.titleTab == 1)
				{
						minimumIndex = TitleDefinitions.pkingTitlesIndex[0];
						maximumIndex = TitleDefinitions.pkingTitlesIndex[1] + 1;
				}
				else if (player.titleTab == 2)
				{
						minimumIndex = TitleDefinitions.miscTitlesIndex[0];
						maximumIndex = TitleDefinitions.miscTitlesIndex[1] + 1;
				}
				double amount = 0;
				int indexOrder = 0;
				if (player.titleTab < 3)
				{
						for (int index = minimumIndex; index < maximumIndex; index++)
						{
								player.getPA().sendFrame126(TitleDefinitions.getDefinitions()[index].title, 19384 + indexOrder);
								if (isTitleUnlocked(player, TitleDefinitions.getDefinitions()[index].titleId))
								{
										player.getPA().changeTextColour(19384 + indexOrder, ServerConstants.GREEN_HEX);
								}
								else
								{
										player.getPA().changeTextColour(19384 + indexOrder, ServerConstants.RED_HEX);
								}
								indexOrder++;
						}
						amount = maximumIndex - minimumIndex;
				}
				else
				{

						for (int index = 0; index < player.titlesUnlocked.size(); index++)
						{
								player.getPA().sendFrame126(TitleDefinitions.getDefinitions()[TitleDefinitions.titleIdAndDefinitionIndex.get(Integer.parseInt(player.titlesUnlocked.get(index)))].title, 19384 + indexOrder);
								player.getPA().changeTextColour(19384 + indexOrder, ServerConstants.GREEN_HEX);
								indexOrder++;
						}
						amount = indexOrder;

				}
				amount *= 15.2;
				InterfaceAssistant.setFixedScrollMax(player, 19383, (int) amount);
				player.playerAssistant.sendMessage(":packet:cleartextclicked");
				updateCompletedAmount(player);
				clearTitleRequirements(player);
				InterfaceAssistant.clearFrames(player, 19384 + indexOrder, 19423);
		}

		private static void titleClicked(Player player, int indexButton)
		{
				indexButton = (indexButton - 75183);
				int titleIndex = 0;
				if (player.titleTab == 0)
				{
						if (indexButton > TitleDefinitions.skillingTitlesIndex[1] + 1)
						{
								return;
						}
				}
				else if (player.titleTab == 1)
				{
						titleIndex += TitleDefinitions.pkingTitlesIndex[0];
						if (indexButton > TitleDefinitions.pkingTitlesIndex[1] + 1)
						{
								return;
						}
				}
				else if (player.titleTab == 2)
				{
						titleIndex += TitleDefinitions.miscTitlesIndex[0];
						if (indexButton > TitleDefinitions.miscTitlesIndex[1] + 1)
						{
								return;
						}
				}
				titleIndex += indexButton - 1;
				if (player.titleTab == 3)
				{
						if ((indexButton - 1) >= player.titlesUnlocked.size())
						{
								return;
						}
						titleIndex = TitleDefinitions.titleIdAndDefinitionIndex.get(Integer.parseInt(player.titlesUnlocked.get(indexButton - 1)));
				}
				player.getPA().setTextClicked(19383 + indexButton, true);
				player.titleIndexClicked = titleIndex;
				boolean gladiatorOrIron = GameMode.getGameMode(player, "GLADIATOR") || GameMode.getGameMode(player, "IRON MAN");
				String description = TitleDefinitions.getDefinitions()[titleIndex].requirementsSubText1;
				int amount = TitleDefinitions.getDefinitions()[titleIndex].completeAmount;
				if (gladiatorOrIron)
				{
						description = description.replaceAll("250m", "50m");
						if (amount == 250000000)
						{
								amount = 50000000;
						}
				}
				player.getPA().sendFrame126(description, 19424);
				player.getPA().sendFrame126(TitleDefinitions.getDefinitions()[titleIndex].requirementsSubText2, 19425);
				String afterName = TitleDefinitions.getDefinitions()[titleIndex].titleAfterName ? "Yes" : "No";
				player.getPA().sendFrame126("Title after name: " + afterName, 19426);
				int progress = getCurrentTitleProgress(player, TitleDefinitions.getDefinitions()[titleIndex].titleId, "");
				player.getPA().sendFrame126("Progress: " + Misc.formatRunescapeStyle(progress) + "/" + Misc.formatRunescapeStyle(amount), 19447);
				player.getPA().sendMessage(":packet:titlepercentage " + Achievements.calculatePercentage(progress, amount));
		}

		private static void clearTitleRequirements(Player player)
		{
				player.getPA().sendFrame126("", 19424);
				player.getPA().sendFrame126("", 19425);
				player.getPA().sendFrame126("", 19426);
				player.getPA().sendFrame126("", 19427);
				player.getPA().sendFrame126("", 19428);
		}

}