package game.content.skilling;


import java.util.ArrayList;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.combat.Combat;
import game.content.donator.DonatorFeatures;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.Teleport;
import game.content.profile.ProfileRank;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Skilling related.
 * @author MGT Madness.
 */
public class Skilling
{

		private final static int[][] randomDirection = {
		//@formatter:off
			{1, 1},
			{-1, 0},
			{0, -1},
			{1, 0},
			{0, 1},
			{-1, -1},
			{-1, 1},
			{ 1, -1}
	//@formatter:on
		};


		public static void randomEvent(Player player, int chance)
		{
				if (!Misc.hasOneOutOf(chance))
				{
						return;
				}
				if (System.currentTimeMillis() - player.lastRandomEvent <= 3600000)
				{
						return;
				}
				player.lastRandomEvent = System.currentTimeMillis();
				ArrayList<String> walkableDirectionsIndex = new ArrayList<String>();
				//8 possible directions
				for (int index = 0; index < randomDirection.length; index++)
				{
						int nextX = player.getX() + randomDirection[index][0];
						int nextY = player.getY() + randomDirection[index][1];
						if (Region.isStraightPathUnblocked(player.getX(), player.getY(), nextX, nextY, player.getHeight(), 1, 1))
						{
								walkableDirectionsIndex.add(index + "");
						}
				}
				int indexChosen = 0;
				for (int index = 0; index < walkableDirectionsIndex.size(); index++)
				{
						if (index == walkableDirectionsIndex.size() - 1)
						{
								indexChosen = Integer.parseInt(walkableDirectionsIndex.get(index));
								break;
						}
						if (Misc.hasOneOutOf(walkableDirectionsIndex.size()))
						{
								indexChosen = Integer.parseInt(walkableDirectionsIndex.get(index));
								break;
						}
				}
				int xExtra = 0;
				int yExtra = 0;
				int random = Misc.random(1, 3);
				if (randomDirection[indexChosen][0] > 0 && randomDirection[indexChosen][1] == 0)
				{
						if (Region.isStraightPathUnblocked(player.getX(), player.getY(), player.getX() + randomDirection[indexChosen][0] + random, player.getY() + randomDirection[indexChosen][1], player.getHeight(), 1, 1))
						{
								xExtra += random;
						}
				}
				if (randomDirection[indexChosen][0] < 0 && randomDirection[indexChosen][1] == 0)
				{
						if (Region.isStraightPathUnblocked(player.getX(), player.getY(), player.getX() + randomDirection[indexChosen][0] - random, player.getY() + randomDirection[indexChosen][1], player.getHeight(), 1, 1))
						{
								xExtra -= random;
						}
				}
				if (randomDirection[indexChosen][1] > 0 && randomDirection[indexChosen][0] == 0)
				{
						if (Region.isStraightPathUnblocked(player.getX(), player.getY(), player.getX() + randomDirection[indexChosen][0], player.getY() + randomDirection[indexChosen][1] + random, player.getHeight(), 1, 1))
						{
								yExtra += random;
						}
				}
				if (randomDirection[indexChosen][1] < 0 && randomDirection[indexChosen][0] == 0)
				{
						if (Region.isStraightPathUnblocked(player.getX(), player.getY(), player.getX() + randomDirection[indexChosen][0], player.getY() + randomDirection[indexChosen][1] - random, player.getHeight(), 1, 1))
						{
								yExtra -= random;
						}
				}
				if (Area.inWilderness(player.getX() + randomDirection[indexChosen][0] + xExtra, player.getY() + randomDirection[indexChosen][1] + yExtra) && !Area.inWilderness(player))
				{
						return;
				}
				Teleport.spellTeleport(player, player.getX() + randomDirection[indexChosen][0] + xExtra, player.getY() + randomDirection[indexChosen][1] + yExtra, player.getHeight(), false);
		}

		public static String sellHarvestedResource(Player player, int itemId, int amount)
		{
				itemId = ItemAssistant.getUnNotedItem(itemId);
				String edited = "false";
				// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
				for (int index = 0; index < player.resourcesHarvested.size(); index++)
				{
						String currentString = player.resourcesHarvested.get(index);
						String parse[] = currentString.split(" ");
						if (parse[0].equals("" + itemId))
						{
								int originalAmount = Integer.parseInt(parse[1]);
								if (originalAmount < amount)
								{
										amount = originalAmount;
										originalAmount = 0;
								}
								else
								{
										originalAmount -= amount;
								}
								edited = "true";
								player.resourcesHarvested.remove(index);
								if (originalAmount > 0)
								{
										player.resourcesHarvested.add(itemId + " " + originalAmount);
								}
								break;
						}
				}
				return amount + " " + edited;
		}

		public static void addHarvestedResource(Player player, int itemId, int amount)
		{
				player.resourcesHarvested.add(itemId + " " + amount);
				// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
				ArrayList<String> finalIncomeList = new ArrayList<String>();
				for (int index = 0; index < player.resourcesHarvested.size(); index++)
				{
						String currentString = player.resourcesHarvested.get(index);
						int lastIndex = currentString.lastIndexOf(" ");
						String matchToFind = currentString.substring(0, lastIndex);
						boolean finalIncomeListHas = false;
						for (int i = 0; i < finalIncomeList.size(); i++)
						{
								int lastIndex1 = finalIncomeList.get(i).lastIndexOf(" ");
								String matchToFind1 = finalIncomeList.get(i).substring(0, lastIndex1);
								if (matchToFind1.equals(matchToFind))
								{
										int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
										int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).substring(lastIndex + 1));
										int finalValueAdded = (finalNumberValue + numberValue);
										finalIncomeList.remove(i);
										finalIncomeList.add(i, matchToFind + " " + finalValueAdded);
										finalIncomeListHas = true;
								}
						}

						if (!finalIncomeListHas)
						{
								finalIncomeList.add(currentString);
						}
				}
				player.resourcesHarvested = finalIncomeList;
		}

		/**
		 * True if the player has the given itemId worn in the cape slot or has Max cape worn.
		 * Use the untrimmed itemId version, because this will check for the trimmed version too which is +1
		 */
		public static boolean hasMasterCapeWorn(Player player, int itemId)
		{
				int[] maxCapes = {
						18674, // Max cape.
						18675, // Fire max cape.
						18676, // Saradomin max cape.
						18677, // Zamorak max cape.
						18678, // Guthix max cape.
						18679, // Ava's max cape.
				};

				// Untrimmed version.
				if (player.playerEquipment[ServerConstants.CAPE_SLOT] == itemId)
				{
						return true;
				}

				// Trimmed version.
				if (player.playerEquipment[ServerConstants.CAPE_SLOT] == itemId + 1)
				{
						return true;
				}

				for (int index = 0; index < maxCapes.length; index++)
				{
						if (player.playerEquipment[ServerConstants.CAPE_SLOT] == maxCapes[index])
						{
								return true;
						}
				}

				return false;
		}

		public static void sendXpDropAmount(Player player)
		{
				if (player.xpDropAmount > 0)
				{
						player.playerAssistant.sendMessage(":xpdisplay " + player.xpDropAmount + " " + player.xpDropSkills);
				}
				player.xpDropAmount = 0;
				player.xpDropSkills = "";
		}

		public static boolean canActivateNewSkillingEvent(Player player, String eventName)
		{
				if (!player.skillingEvent.isEmpty())
				{
						return true;
				}
				player.skillingEvent = eventName;
				return false;
		}

		public static boolean isSkillingEventActive(Player player, String eventName)
		{
				if (!player.skillingEvent.equals(eventName))
				{
						return false;
				}
				return true;
		}

		/**
		 * Set the client xp bar total to my current total experience.
		 */
		public static void sendXpToDisplay(Player player)
		{
				int amount = 0;
				if (player.xpBarShowType.equals("TOTAL"))
				{
						amount = (int) player.getXpTotal();
						player.playerAssistant.sendMessage(":xpshowtotal");
				}
				else if (player.xpBarShowType.equals("SESSION"))
				{
						amount = player.currentSessionExperience;
						player.playerAssistant.sendMessage(":xpshowsession");
				}
				else if (player.xpBarShowType.equals("COMBAT"))
				{
						for (int index = 0; index < 7; index++)
						{
								amount += player.combatExperienceGainedAfterMaxed[index];
						}
						player.playerAssistant.sendMessage(":xpshowcombat");
				}
				player.playerAssistant.sendMessage(":xptotal" + amount);
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			Get the total level, ignoring the decreased combat stats from being editted.
		 */
		public static int getOriginalTotalLevel(Player player)
		{
				int total = Skilling.getTotalLevel(player, true);
				if (player.getAbleToEditCombat())
				{
						total += 693;
				}
				else
				{
						total += player.getBaseAttackLevel();
						total += player.getBaseStrengthLevel();
						total += player.getBaseDefenceLevel();
						total += player.getBaseHitPointsLevel();
						total += player.getBaseRangedLevel();
						total += player.getBasePrayerLevel();
						total += player.getBaseMagicLevel();
				}
				return total;
		}

		private static void updateSkillTabFrontTextMethod(Player player, int skill, int first, int second)
		{
				if (skill <= 6)
				{
						player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(skill) + "", first);
				}
				else
				{
						player.getPA().sendFrame126("" + player.baseSkillLevel[skill] + "", first);
				}

				player.getPA().sendFrame126("" + player.baseSkillLevel[skill] + "", second);
		}

		public static void resetCombatSkills(Player player)
		{
				for (int i = 0; i <= 6; i++)
				{
						player.currentCombatSkillLevel[i] = player.baseSkillLevel[i];
						Skilling.updateSkillTabFrontTextMain(player, i);
				}
		}

		private final static int[][] skillInterfaceData = {
				{4004, 4005},
				{4008, 4009},
				{4006, 4007},
				{4016, 4017},
				{4010, 4011},
				{4012, 4013},
				{4014, 4015},
				{4034, 4035},
				{4038, 4039},
				{4026, 4027},
				{4032, 4033},
				{4036, 4037},
				{4024, 4025},
				{4030, 4031},
				{4028, 4029},
				{4020, 4021},
				{4018, 4019},
				{4022, 4023},
				{12166, 12167},
				{13926, 13927},
				{4152, 4153},};

		public static void updateSkillTabFrontTextMain(Player player, int skill)
		{
				if (skill == 3)
				{
						player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS), 19001); // Hitpoints orb.
				}
				if (skill == 5)
				{
						player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) + " / " + player.baseSkillLevel[ServerConstants.PRAYER] + "", 687); // Prayer spellbook text.
				}
				updateSkillTabExperienceHover(player, skill, false);
				updateSkillTabFrontTextMethod(player, skill, skillInterfaceData[skill][0], skillInterfaceData[skill][1]);
		}

		private static int experienceArray[] = {
				0,
				83,
				174,
				276,
				388,
				512,
				650,
				801,
				969,
				1154,
				1358,
				1584,
				1833,
				2107,
				2411,
				2746,
				3115,
				3523,
				3973,
				4470,
				5018,
				5624,
				6291,
				7028,
				7842,
				8740,
				9730,
				10824,
				12031,
				13363,
				14833,
				16456,
				18247,
				20224,
				22406,
				24815,
				27473,
				30408,
				33648,
				37224,
				41171,
				45529,
				50339,
				55649,
				61512,
				67983,
				75127,
				83014,
				91721,
				101333,
				111945,
				123660,
				136594,
				150872,
				166636,
				184040,
				203254,
				224466,
				247886,
				273742,
				302288,
				333804,
				368599,
				407015,
				449428,
				496254,
				547953,
				605032,
				668051,
				737627,
				814445,
				899257,
				992895,
				1096278,
				1210421,
				1336443,
				1475581,
				1629200,
				1798808,
				1986068,
				2192818,
				2421087,
				2673114,
				2951373,
				3258594,
				3597792,
				3972294,
				4385776,
				4842295,
				5346332,
				5902831,
				6517253,
				7195629,
				7944614,
				8771558,
				9684577,
				10692629,
				11805606,
				13034431,};

		public static int getExperienceForLevel(int level)
		{
				return experienceArray[--level > 98 ? 98 : level];
		}

		public static int getExprienceForLevelDifferent(int level)
		{
				if (level > 99)
				{
						return 0;
				}
				return experienceArray[--level > 98 ? 98 : level];
		}

		public static int getLevelForExperience(int exp)
		{
				for (int j = 98; j != -1; j--)
				{
						if (experienceArray[j] <= exp)
						{
								return j + 1;
						}
				}
				return 0;
		}

		public static void updateTotalSkillExperience(Player player, long amount)
		{
				player.setXpTotal(amount);
				player.getPA().sendFrame126("Total Xp: " + (player.getXpTotal() / 1000000) + " million", 24363);
		}

		public static void experienceGainedAfterMaxed(Player player, int skill, int amount)
		{
				if (skill > 6)
				{
						return;
				}
				player.combatExperienceGainedAfterMaxed[skill] += amount;
		}

		public static boolean addSkillExperience(Player player, int amount, int skill)
		{
				// Only multiply experience if it is not a combat skill.
				if (skill > 6)
				{
						amount *= ServerConstants.SKILLING_XP_MULTIPLIER;
						// Xp lamp.
						if (!player.xpLampUsed)
						{
								if (skill == ServerConstants.AGILITY)
								{

										amount *= 1.6;
								}
								else if (skill == ServerConstants.MINING || skill == ServerConstants.SMITHING)
								{
										amount *= 1.2;
								}
								else if (skill == ServerConstants.FARMING)
								{
										amount *= 1.6;
								}
						}

						if (Area.inResourceWilderness(player))
						{
								amount *= 1.2;
						}
						if (skill != ServerConstants.SLAYER)
						{
								player.timeSkilled = System.currentTimeMillis();
						}
						randomEvent(player, 200);
				}
				else if (skill == ServerConstants.PRAYER)
				{
						if (!Combat.inCombat(player))
						{
								randomEvent(player, 200);
						}
				}
				player.xpLampUsed = false;
				player.getPA().sendMessage(":xpdisplaybar" + amount);
				if (skill >= 7)
				{
						GameTimeSpent.increaseGameTime(player, GameTimeSpent.SKILLING);
				}
				if (amount + player.skillExperience[skill] < 0)
				{
						return false;
				}
				player.xpDropAmount += amount;
				player.xpDropSkills = player.xpDropSkills + (player.xpDropSkills.isEmpty() ? "" : " ") + skill;
				if (player.xpLock)
				{
						return false;
				}
				int maximumExperienceCap = 100000000;
				player.currentSessionExperience += amount;
				experienceGainedAfterMaxed(player, skill, amount);
				int oldLevel = player.baseSkillLevel[skill];
				updateTotalSkillExperience(player, player.getXpTotal() + amount);

				// Only add experience to skill level if not a combat skill or is a combat skill and the combat skill level is 99.
				// So 1 defence pures do not mess up if they train defence by accident.
				if (skill > 6 || skill <= 6 && player.baseSkillLevel[skill] == 99)
				{
						player.skillExperience[skill] += amount;
				}
				if (player.skillExperience[skill] > maximumExperienceCap)
				{
						player.skillExperience[skill] = maximumExperienceCap;
				}
				// 14.1m incase a veteran takes an xp lamp at 13.02m experience.
				if (player.skillExperience[skill] < 14200000)
				{
						int newLevel = getLevelForExperience(player.skillExperience[skill]); // No need to go through this big looping method.
						if (newLevel == 250 || newLevel == 500 || newLevel == 750 || newLevel == 1000 || newLevel == 1250 || newLevel == 1500 || newLevel == 1750 || newLevel == 2000)
						{
								player.getPA().sendScreenshot(newLevel + " total level", 2);
						}
						if (oldLevel < newLevel)
						{
								if (skill <= 6)
								{
										if (player.getCurrentCombatSkillLevel(skill) < newLevel && skill != 3 && skill != 5)
										{
												player.currentCombatSkillLevel[skill] = newLevel;
										}
								}
								player.baseSkillLevel[skill] = newLevel;
								levelUp(player, skill);
								player.getPA().requestUpdates();
						}
				}
				player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
				updateSkillTabFrontTextMain(player, skill);
				announceMaxExperienceInASkill(player, skill);
				return true;
		}

		public static int getTotalLevel(Player player, boolean excludeCombat)
		{
				int total = 0;
				for (int i = excludeCombat ? 7 : 0; i <= 20; i++)
				{
						total += player.baseSkillLevel[i];
				}
				return total;
		}

		public static long getExperienceTotal(Player player)
		{
				boolean reAdd = false;
				long xp = 0;
				if (player.getAbleToEditCombat())
				{
						for (int i = 0; i <= 6; i++)
						{
								xp += (13034431 + player.combatExperienceGainedAfterMaxed[i]);
						}
						reAdd = true;
				}
				for (int i = reAdd ? 7 : 0; i <= 20; i++)
				{
						xp += player.skillExperience[i];
				}
				return xp;
		}

		private static void levelUpMessage(Player player, int skill, int first, int second, int third)
		{
				player.getPA().sendFrame126("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", first);
				player.getPA().sendFrame126("You have now reached level " + player.baseSkillLevel[skill] + "!", second);
				player.playerAssistant.sendFilterableMessage("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level.");
				player.getPA().sendFrame164(third);
		}

		private final static int[][] levelUpInterfaceData = {
				{4268, 4269, 6247},

				{4268, 4269, 6253},

				{4268, 4269, 6206},

				{4268, 4269, 6216},

				{4268, 4269, 4443},

				{4268, 4269, 6242},

				{4268, 4269, 6211},

				{4268, 4269, 6226},

				{4268, 4269, 4272},

				{4268, 4269, 6231},

				{4268, 4269, 6258},

				{4268, 4269, 4282},

				{4268, 4269, 6263},

				{4268, 4269, 6221},

				{4268, 4269, 4416},

				{4268, 4269, 6237},

				{4268, 4269, 4277}, // This one doesnt exist in old version.

				{4268, 4269, 4261},

				{4268, 4269, 12122},

				{4268, 4269, 5267},

				{4268, 4269, 4267},};

		private final static int[][] levelUpIds2007 = {
				{6248, 6249},
				{6254, 6255},
				{6207, 6208},
				{6217, 6218},
				{5453, 6114},
				{6243, 6244},
				{6212, 6213},
				{6227, 6228},
				{4273, 4274},
				{6232, 6233},
				{6259, 6260},
				{4283, 4284},
				{6264, 6265},
				{6222, 6223},
				{4417, 4438},
				{6238, 6239},
				{4278, 4279}, // Agility is missing
				{4263, 4264},
				{12123, 12124},
				{12123, 12124},
				{4268, 4269},};

		public static void levelUp(Player player, int skill)
		{
				Skilling.updateTotalLevel(player);
				if (skill == ServerConstants.FARMING)
				{

						player.playerAssistant.sendFilterableMessage("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level.");
						player.getDH().sendItemChat4("", "Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "", "You have now reached level " + player.baseSkillLevel[skill] + "!", "", 5340, 250, 30, 0);
				}
				else
				{
						if (skill == ServerConstants.RUNECRAFTING && player.graphicsType.equals("2007"))
						{
								player.playerAssistant.sendFilterableMessage("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level.");
								player.getDH().sendItemChat4("", "Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "", "You have now reached level " + player.baseSkillLevel[skill] + "!", "", 556, 250, 0, -5);
						}
						else if (skill == ServerConstants.RANGED && player.graphicsType.equals("2007"))
						{
								player.playerAssistant.sendFilterableMessage("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level.");
								player.getDH().sendItemChat4("", "Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "", "You have now reached level " + player.baseSkillLevel[skill] + "!", "", 841, 250, 0, -10);
						}
						else if (skill == ServerConstants.PRAYER && player.graphicsType.equals("2007"))
						{
								player.playerAssistant.sendFilterableMessage("Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level.");
								player.getDH().sendItemChat4("", "Congratulations! You've just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "", "You have now reached level " + player.baseSkillLevel[skill] + "!", "", 526, 250, 0, -17);
						}
						else
						{
								levelUpMessage(player, skill, player.graphicsType.equals("2007") ? levelUpIds2007[skill][0] : levelUpInterfaceData[skill][0], player.graphicsType.equals("2007") ? levelUpIds2007[skill][1] : levelUpInterfaceData[skill][1], levelUpInterfaceData[skill][2]);
						}
				}

				player.setDialogueAction(0);
				player.nextDialogue = 0;
				player.playerAssistant.calculateCombatLevel();
				InterfaceAssistant.updateCombatLevel(player);
				GameMode.announceMaxedCombatOrMaxedTotal(player);
				announce99Achieved(player, skill);
				ProfileRank.rankPopUp(player, "ADVENTURER");
				player.gfx100(199);

		}

		public static enum SkillCapeMasterData
		{
				ATTACK("Ajjat", "Warriors' Guild", 9747, 4288, 19113),
				DEFENCE("Melee combat tutor", "Lumbridge", 9753, 705, 19116),
				STRENGTH("Sloane", "Warriors' Guild", 9750, 4297, 19130),
				HITPOINTS("Surgeon General Tafani", "Duel Arena", 9768, 961, 19122),
				RANGED("Armour salesman", "Ranging Guild", 9756, 682, 70069),
				PRAYER("Brother Jered", "Monastery", 9759, 802, 19125),
				MAGIC("Robe Store Owner", "Wizards' Guild", 9762, 1658, 19123),
				COOKING("Head chef", "Cooks' Guild", 9801, 847, 19114),
				WOODCUTTING("Wilfred", "Lumbridge", 9807, 4906, 19132),
				FLETCHING("Hickton", "Catherby", 9783, 575, 19120),
				FISHING("Master Fisher", "Fishing Guild", 9798, 308, 19119),
				FIREMAKING("Ignatius Vulcan", "South of Seers' Village", 9804, 4946, 19118),
				CRAFTING("Master Crafter", "Crafting Guild", 9780, 805, 19115),
				SMITHING("Thurgo", "Mudskipper Point", 9795, 604, 19129),
				MINING("Dwarf", "Mining Guild", 9792, 3295, 19124),
				HERBLORE("Kaqemeex", "Taverley", 9774, 455, 19121),
				AGILITY("Cap'n Izzy No-Beard", "Brimhaven Agility Arena", 9771, 437, 19112),
				THIEVING("Martin Thwait", "Rogues' Den", 9777, 2270, 19131),
				SLAYER("Duradel", "Shilo Village", 9786, 8275, 19128),
				FARMING("Martin the Master Gardener", "Entrana", 9810, 3299, 19117),
				RUNECRAFTING("Aubury", "South-East Varrock", 9765, 553, 19127);

				private String npc;

				private String location;

				private int skillCapeId;

				private int npcId;

				private int masterCape;


				private SkillCapeMasterData(String npc, String location, int skillCapeId, int npcId, int masterCape)
				{
						this.npc = npc;
						this.location = location;
						this.skillCapeId = skillCapeId;
						this.npcId = npcId;
						this.masterCape = masterCape;
				}

				public String getNpc()
				{
						return npc;
				}

				public String getLocation()
				{
						return location;
				}

				public int getUntrimmedSkillCapeId()
				{
						return skillCapeId;
				}

				public int getNpcId()
				{
						return npcId;
				}

				public int getLegendCapeId()
				{
						return masterCape;
				}

		}

		/**
		 * Announce the player for reaching 99 in a skill.
		 * @param skill
		 * 			The skill type.
		 * @param skillName
		 * 			The name of the skill.
		 */
		public static void announce99Achieved(Player player, int skill)
		{
				if (player.baseSkillLevel[skill] < 99)
				{
						return;
				}
				if (skill <= 6 && player.combatSkillsAnnounced[skill])
				{
						return;
				}
				if (skill <= 6)
				{
						player.combatSkillsAnnounced[skill] = true;
				}
				player.playerAssistant.announce(GameMode.getGameModeName(player) + " has just achieved 99 " + ServerConstants.SKILL_NAME[skill] + ".");
				player.getDH().sendItemChat4("", "Congratulations! You are now an expert of " + ServerConstants.SKILL_NAME[skill] + ".", "Why not visit " + SkillCapeMasterData.values()[skill].getNpc() + " at " + SkillCapeMasterData.values()[skill].getLocation() + "? He has something", "special that is only available to true experts of the", ServerConstants.SKILL_NAME[skill] + " skill!", SkillCapeMasterData.values()[skill].getUntrimmedSkillCapeId(), 200, 0, -16);
				player.getPA().sendScreenshot("99 " + ServerConstants.SKILL_NAME[skill], 2);
		}

		/**
		 * Update all the text in the skill tab.
		 * @param player
		 * 			The associated player.
		 */
		public static void updateAllSkillTabFrontText(Player player)
		{
				for (int i = 0; i <= 20; i++)
				{
						player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
						updateSkillTabFrontTextMain(player, i);
				}
		}

		/**
		 * Stop all on-going skilling.
		 * @param player
		 * 			The associated player.
		 */
		public static void stopAllSkilling(Player player)
		{
				player.miningTimer = 0;
				player.enchantEvent = false;
				DonatorFeatures.resetAfk(player, false);
				player.isFarming = false;
				player.bowStringEvent = false;
				Cooking.resetCooking(player);
				player.usingBoneOnAltar = false;
				player.smeltInterface = false;
				player.setSmithingEvent(false);
				player.playerFletch = false;
				player.getCrafting = false;
				player.skillingEvent = "";
				Fishing.stopFishing(player);
				Woodcutting.stopWoodcutting(player);
				player.oreInformation[0] = player.oreInformation[1] = player.oreInformation[2] = 0;
				player.usingHerbloreEvent = false;
				player.isCookingEvent = false;
				player.playerSkillProp = new int[20][15];
				player.skillingData = new int[10];
		}

		/**
		 * Announce 100 million experience in a skill.
		 * @param player
		 * 			The associated player.
		 * @param skill
		 * 			The skill that has reached 100 million experience.
		 */
		public static void announceMaxExperienceInASkill(Player player, int skill)
		{
				if (player.skillExperience[skill] == 100000000 && !player.skillMilestone100mAnnounced[skill])
				{
						String text[] = {GameMode.getGameModeName(player) + " has achieved 100 million experience in " + ServerConstants.SKILL_NAME[skill] + "."};
						player.playerAssistant.announce(text[0]);
						player.skillMilestone100mAnnounced[skill] = true;
						PlayerTitle.checkCompletionSingle(player, 32 + skill);
						Achievements.checkCompletionSingle(player, 1117);
						Achievements.checkCompletionSingle(player, 1095 + skill);
						player.getDH().sendItemChat4("", "Congratulations! You are now a Legend of " + ServerConstants.SKILL_NAME[skill] + ".", "Why not visit " + SkillCapeMasterData.values()[skill].getNpc() + " at " + SkillCapeMasterData.values()[skill].getLocation() + "?", "He has something special that is only available", "to true Legends of the " + ServerConstants.SKILL_NAME[skill] + " skill!", SkillCapeMasterData.values()[skill].getUntrimmedSkillCapeId() + 1, 200, 0, 0);
						player.gfx100(199);
						player.getPA().sendScreenshot("100m in " + ServerConstants.SKILL_NAME[skill], 2);
				}
		}

		private static String getHoverText(Player player, int skillId)
		{
				int maximumExperienceCap = 100000000;
				int currentExp = player.skillExperience[skillId];
				String currentExperience = "Current Xp: " + Misc.formatNumber(currentExp) + "\\n";
				String[] message = new String[4];
				if (skillId <= 6)
				{
						message[0] = ServerConstants.SKILL_NAME[skillId] + ": " + player.currentCombatSkillLevel[skillId] + "/" + player.baseSkillLevel[skillId] + "\\n";
				}
				else
				{
						message[0] = ServerConstants.SKILL_NAME[skillId] + ": " + player.baseSkillLevel[skillId] + "/" + player.baseSkillLevel[skillId] + "\\n";
				}
				message[1] = currentExperience;
				message[2] = player.baseSkillLevel[skillId] >= 99 ? "Next lvl at: " + Misc.formatNumber(maximumExperienceCap) + "\\n" : "Next lvl at: " + Misc.formatNumber(Skilling.getExperienceForLevel(player.baseSkillLevel[skillId] + 1)) + "\\n";
				if (currentExp == maximumExperienceCap)
				{
						message[3] = "Remainder: 0\\n";
				}
				else
				{
						message[3] = player.baseSkillLevel[skillId] >= 99 ? "Remainder: " + Misc.formatNumber((maximumExperienceCap - player.skillExperience[skillId])) + "\\n" : "Remainder: " + Misc.formatNumber(Skilling.getExprienceForLevelDifferent(player.baseSkillLevel[skillId] + 1) - player.skillExperience[skillId]) + "\\n";
				}
				return message[0] + message[1] + message[2] + message[3];
		}

		private static int[] skillTabHoverFrames = {

				4040,
				4052,
				4046,
				4076,
				4058,
				4064,
				4070,
				4130,
				4142,
				4106,
				4124,
				4136,
				4100,
				4118,
				4112,
				4088,
				4082,
				4094,
				2832,
				13917,
				4160};

		/**
		 * Update the "Current Xp:" in the skill tab hovers.
		 * @param player
		 * 			The associated player.
		 * @param skill
		 * 			The skill being used.
		 * @param updateAll
		 * 			True, to update all the skills.
		 */
		public static void updateSkillTabExperienceHover(Player player, int skill, boolean updateAll)
		{
				if (updateAll)
				{
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.ATTACK), 4040);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.HITPOINTS), 4076);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.STRENGTH), 4046);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.DEFENCE), 4052);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.RANGED), 4058);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.PRAYER), 4064);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.MAGIC), 4070);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.MINING), 4112);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.AGILITY), 4082);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.SMITHING), 4118);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.HERBLORE), 4088);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.FISHING), 4124);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.THIEVING), 4094);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.COOKING), 4130);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.CRAFTING), 4100);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.FIREMAKING), 4136);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.FLETCHING), 4106);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.WOODCUTTING), 4142);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.RUNECRAFTING), 4160);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.SLAYER), 2832);
						player.getPA().sendFrame126(getHoverText(player, ServerConstants.FARMING), 13917);
						return;
				}
				String text = getHoverText(player, skill);
				player.getPA().sendFrame126(text, skillTabHoverFrames[skill]);
		}

		public static final boolean view190 = true;

		public static boolean hasRequiredLevel(final Player c, int id, int lvlReq, String skill, String event)
		{
				if (c.baseSkillLevel[id] < lvlReq)
				{
						c.playerAssistant.sendMessage("You dont't have a high enough " + skill + " level to " + event + "");
						c.playerAssistant.sendMessage("You at least need the " + skill + " level of " + lvlReq + ".");
						c.getDH().sendStatement("You need a " + skill + " level of " + lvlReq + " to " + event + ".");
						return false;
				}
				return true;
		}

		public static void deleteTime(Player c)
		{
				c.doAmount--;
		}

		/**
		 * Update the total level text on the skill tab interface.
		 * @param player
		 * 			The associated player.
		 */
		public static void updateTotalLevel(Player player)
		{
				player.setTotalLevel(getOriginalTotalLevel(player));
				player.getPA().sendFrame126("Total level: " + player.getTotalLevel(), 3984);
				//player.getPA().sendFrame126(" ", 3985); // QP: text on the 474 skill tab.
		}

}