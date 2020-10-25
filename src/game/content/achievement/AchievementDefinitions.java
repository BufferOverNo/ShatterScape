package game.content.achievement;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utility.Misc;


public class AchievementDefinitions
{
		public static final AchievementDefinitions[] DEFINITIONS = new AchievementDefinitions[50];

		public AchievementDefinitions(String achievementTitle, String descriptionSubText1, String descriptionSubText2, String titleUnlocked, String requirementsSubText2, String achievementSaveName, int completeAmount, int achievementId)
		{
				this.achievementTitle = achievementTitle;
				this.descriptionSubText1 = descriptionSubText1;
				this.descriptionSubText2 = descriptionSubText2;
				this.titleUnlocked = titleUnlocked;
				this.itemRewards = requirementsSubText2;
				this.achievementSaveName = achievementSaveName;
				this.completeAmount = completeAmount;
				this.achievementId = achievementId;
		}

		public static AchievementDefinitions[] getDefinitions()
		{
				return DEFINITIONS;
		}

		public final String achievementTitle;

		public final String descriptionSubText1;

		public final String descriptionSubText2;

		public final String titleUnlocked;

		public final String itemRewards;

		public final String achievementSaveName;

		public final int completeAmount;

		public final int achievementId;

		public static void loadAllAchievements()
		{
				easyAchievementsIndex[0] = 0;
				loadAchievements("easy achievements");

				storeAchievementIdAndDefinitionIndex();
		}

		private final static String[] textData = {"Achievement title:", "Description Sub Text:", "Description Sub Text:", "Title Unlocked:", "Item Rewards:", "Achievement Save Name:", "Complete Amount:", "Achievement Id:"};

		private static int emptyDefinitionIndex;

		public static int[] easyAchievementsIndex = new int[2];

		public static void loadAchievements(String name)
		{
				String line = "";
				boolean EndOfFile = false;
				BufferedReader fileLocation = null;
				try
				{
						fileLocation = new BufferedReader(new FileReader("./data/content/achievements/" + name + ".txt"));
				}
				catch (FileNotFoundException fileex)
				{
						Misc.print(name + " file not found.");
						return;
				}
				try
				{
						line = fileLocation.readLine();
				}
				catch (IOException ioexception)
				{
				}
				int finishedReadingIndex = 0;
				String achievementTitle = "";
				String descriptionSubText1 = "";
				String descriptionSubText2 = "";
				String titleUnlocked = "";
				String itemRewards = "";
				String achievementSaveName = "";
				int completeAmount = -1;
				int achievementId = -1;
				while (!EndOfFile && line != null)
				{


						finishedReadingIndex++;
						if (finishedReadingIndex == 9)
						{
								finishedReadingIndex = 0;
								DEFINITIONS[emptyDefinitionIndex] = new AchievementDefinitions(achievementTitle, descriptionSubText1, descriptionSubText2, titleUnlocked, itemRewards, achievementSaveName, completeAmount, achievementId);
								emptyDefinitionIndex++;
						}
						else
						{
								for (int index = 0; index < textData.length; index++)
								{
										if (line.contains(textData[index]))
										{
												line = line.replaceAll(textData[finishedReadingIndex - 1] + " ", "");
												line = line.replaceAll(textData[finishedReadingIndex - 1], "");
												switch (finishedReadingIndex)
												{
														case 1:
																achievementTitle = line;
																break;
														case 2:
																descriptionSubText1 = line;
																break;
														case 3:
																descriptionSubText2 = line;
																break;
														case 4:
																titleUnlocked = line;
																break;
														case 5:
																itemRewards = line;
																break;
														case 6:
																achievementSaveName = line;
																break;
														case 7:
																completeAmount = Integer.parseInt(line);
																break;
														case 8:
																achievementId = Integer.parseInt(line);
																if (achievementSaveName.contains("NPC TASK"))
																{
																		String npc = achievementSaveName.substring(achievementSaveName.lastIndexOf(": ") + 2);
																		npcTasksData.add(achievementId + "-" + npc);
																}
																if (!achievementSaveName.isEmpty())
																{
																		idAndSaveName.add(achievementId + " " + achievementSaveName);
																}
																break;
												}
												break;
										}
								}
						}
						try
						{
								line = fileLocation.readLine();
						}
						catch (IOException ioexception1)
						{
								EndOfFile = true;
						}
				}
				easyAchievementsIndex[1] = emptyDefinitionIndex;
				DEFINITIONS[emptyDefinitionIndex] = new AchievementDefinitions(achievementTitle, descriptionSubText1, descriptionSubText2, titleUnlocked, itemRewards, achievementSaveName, completeAmount, achievementId);
				emptyDefinitionIndex++;
				try
				{
						fileLocation.close();
				}
				catch (IOException ioexception)
				{
				}
		}

		public static Map<Integer, Integer> achievementIdAndDefinitionIndex = new HashMap<Integer, Integer>();

		private static void storeAchievementIdAndDefinitionIndex()
		{
				for (int index = 0; index < AchievementDefinitions.getDefinitions().length; index++)
				{
						if (AchievementDefinitions.getDefinitions()[index] == null)
						{
								return;
						}
						achievementIdAndDefinitionIndex.put(AchievementDefinitions.getDefinitions()[index].achievementId, index);
				}
		}


		/**
		 * Store achievement id and Npc name. Used to complete npc kill tasks.
		 */
		public static ArrayList<String> npcTasksData = new ArrayList<String>();

		/**
		 * Store achievement id and achievementSaveName.
		 */
		public static ArrayList<String> idAndSaveName = new ArrayList<String>();
}
