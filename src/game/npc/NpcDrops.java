package game.npc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import core.Server;
import core.ServerConstants;
import game.content.minigame.RecipeForDisaster;
import game.content.minigame.WarriorsGuild;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.npc.data.NpcDefinition;
import game.npc.pet.BossPetDrops;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * @author Sanity
 */

public class NpcDrops
{

		public NpcDrops()
		{
		}

		/**
		 * The data of the npc rare drops.
		 */
		public static ArrayList<String> npcRareDropsData = new ArrayList<String>();

		/**
		 * List of npc ids that have rare drops.
		 */
		public static ArrayList<Integer> npcRareDropsList = new ArrayList<Integer>();

		public static HashMap<Integer, int[]> constantDrops = new HashMap<Integer, int[]>();

		private final static int[] NPC_BIG_BONE_DROPS = {4040, 6222, 6260, 1583, 112, 117, 2783, 3072, 4291, 4292};

		private final static int[] NPC_ASHES_DROPS = {6203, 82, 84, 1613, 1615, 1633, 4045};

		private final static int[] NPC_DROPS_NOTHING = {
				2025,
				2026,
				2027,
				2028,
				2029,
				2030, // Barrows brothers.
				2745, // Jad.

		};

		/**
		 * Revenants will not drop bones & normal loot.
		 */
		public final static int[] REVENANT_LIST = {
		//@formatter:off
			6604, // Revenant imp
			6605, // Revenant goblin
			6606, // Revenant icefiend
			6607, // Revenant werewolf
			6608, // Revenant hobgoblin
			6610, // Revenant ork
			6611, // Revenant knight
			6613, // Revenant vampire
			6622, // Revenant pyrefiend
			6645, // Revenant cyclops
			6646, // Revenant hellhound
			6647, // Revenant demon
			6649, // Revenant dark beast
			//@formatter:on
		};

		private final static int[] NPC_NO_BONE_DROPS = {
		//@formatter:off
		8133, 3200, 9463, 103, 104, 107, 127, 1610, 1612, 2607, 2892, 2894, 1600, 1632, 1640, 
		//@formatter:on
		};

		private final static int[] STACKABLE_DROPS = {
				882, // Bronze arrow
				884, // Iron arrow
				886, // Steel arrow
				877, // Bronze bolts
				9140, // Iron bolts
				554, // Fire rune
				555, // Water rune
				556, // Air rune
				557, // Earth rune
				558, // Mind rune
				559, // Body rune
				995, // Coins
				888, // Mithril arrow
				890, // Adamant arrow
				9142, // Mithril bolts
				9143, // Adamant bolts
				562, // Chaos rune
				564, // Cosmic rune
				563, // Law rune
				995, // Coins
				892, // Rune arrow
				9144, // Runite bolts
				561, // Nature rune
				565, // Blood rune
				9075, // Astral rune
				560, // Death rune
				566, // Soul rune
				995, // Coins
		};


		private final static int[] LOW_NPC_DROPS = {
				1155, // Bronze full helm
				1117, // Bronze platebody
				1075, // Bronze platelegs
				1189, // Bronze kiteshield
				1191, // Iron kiteshield
				1067, // Iron platelegs
				1101, // Iron chainbody
				1175, // Iron sq shield
				1119, // Steel platebody
				1157, // Steel full helm
				1323, // Iron scimitar
				1325, // Steel scimitar
				1327, // Black scimitar
				1623, // Uncut sapphire
				199, // Grimy guam
				201, // Grimy marrentil
				203, // Grimy tarromin
				205, // Grimy harralander
				2359, // Mithril bar
				225, // Limpwurt root
				4119, // Bronze boots
				4123, // Steel boots
				1159, // Mithril full helm
				1121, // Mithril platebody
				1071, // Mithril platelegs
				1197, // Mithril kiteshield
				1143, // Mithril med helm
				1109, // Mithril chainbody
				1085, // Mithril plateskirt
				1161, // Adamant full helm
				1183, // Adamant sq shield
				4127, // Mithril boots
				1329, // Mithril scimitar
				1331, // Adamant scimitar
				1619, // Uncut ruby
				1621, // Uncut emerald
				209, // Grimy irit
				215, // Grimy cadantine
				1211, // Adamant dagger
				4125, // Black boots
				4127, // Mithril boots
				1271, // Adamant pickaxe
				1371, // Adamant battleaxe
				1369, // Mithril battleaxe
				4121, // Iron boots
				1267, // Iron pickaxe.
				1269, // Steel pickaxe

		};

		private final static int[] MEDIUM_NPC_DROPS = {
				1125, // Black platebody
				1077, // Black platelegs
				1195, // Black kiteshield
				1123, // Adamant platebody
				1073, // Adamant platelegs
				1199, // Adamant kiteshield
				1147, // Rune med helm
				4129, // Adamant boots
				1289, // Rune sword
				1333, // Rune scimitar
				1617, // Uncut diamond
				211, // Grimy avantoe
				213, // Grimy kwuarm
				2363, // Runite bar
				207, // Grimy ranarr
				217, // Grimy dwarf weed
				2485, // Grimy lantadyme
				3049, // Grimy toadflax
				3051, // Grimy snapdragon
				1213, // Rune dagger
				4129, // Adamant boots
				1271, // Adamant pickaxe
		};

		private final static int[] HIGH_NPC_DROPS = {
				1163, // Rune full helm
				1185, // Rune sq shield
				1303, // Rune longsword
				1127, // Rune platebody
				1079, // Rune platelegs
				1201, // Rune kiteshield
				1113, // Rune chainbody
				1093, // Rune plateskirt
				4131, // Rune boots
				1319, // Rune 2h sword
				1615, // Dragonstone
				219, // Grimy torstol
				4131, // Rune boots
				1275, // Rune pickaxe
				1373, // Rune battleaxe
				1275, // Rune pickaxe
		};


		public static void loadConstants()
		{
				for (int index = 0; index < NPC_BIG_BONE_DROPS.length; index++)
				{
						int[] BIG_BONE = new int[1];
						BIG_BONE[0] = 532;
						constantDrops.put(NPC_BIG_BONE_DROPS[index], BIG_BONE);
				}

				for (int index = 0; index < NPC_ASHES_DROPS.length; index++)
				{
						int[] ASHES = new int[1];
						ASHES[0] = 592;
						constantDrops.put(NPC_ASHES_DROPS[index], ASHES);
				}

				try
				{
						File f = new File("./data/npc/npc constant drops.txt");
						Scanner s = new Scanner(f);
						while (s.hasNextLine())
						{
								String line = s.nextLine();
								if (line.startsWith("#"))
										continue;
								StringTokenizer constantTok = new StringTokenizer(line, "\t");
								int npcId = Integer.parseInt(constantTok.nextToken());
								int count = 0;
								int[] temp = new int[constantTok.countTokens()];
								while (constantTok.hasMoreTokens())
								{
										temp[count] = Integer.parseInt(constantTok.nextToken());
										count++;
								}
								constantDrops.put(npcId, temp);
						}
						s.close();
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		public static boolean isDropNothingNpc(int npcId)
		{
				for (int index = 0; index < NPC_DROPS_NOTHING.length; index++)
				{
						if (npcId == NPC_DROPS_NOTHING[index])
						{
								return true;
						}
				}
				for (int index = 0; index < WarriorsGuild.ARMOUR_DATA.length; index++)
				{
						if (npcId == WarriorsGuild.ARMOUR_DATA[index][3])
						{
								return true;
						}
				}
				for (int index = 0; index < RecipeForDisaster.NPC_WAVE_LIST.length; index++)
				{
						if (npcId == RecipeForDisaster.NPC_WAVE_LIST[index])
						{
								return true;
						}
				}
				return false;
		}

		public static void getNormalDrop(Npc npc, Player player, boolean skipExtraDrop, boolean forceRuneLoot)
		{
				if (isDropNothingNpc(npc.npcType))
				{
						return;
				}
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();

				// Cow, Chicken.
				if (npc.npcType == 81 || npc.npcType == 41)
				{
						if (npc.npcType == 41)
						{
								Server.itemHandler.createGroundItem(player, 314, npcX, npcY, Misc.random(10, 25), false, 0, true, "");
						}
						return;
				}
				boolean ring = ItemAssistant.hasItemEquipped(player, 2572);
				boolean imbuedRing = ItemAssistant.hasItemEquipped(player, 18656);
				// This is balanced, the rings cannot be any more because berserker ring should be more important.
				int percentageChance = imbuedRing ? 45 : ring ? 35 : 30;
				if (forceRuneLoot)
				{
						percentageChance = 100;
				}
				int[] number = new int[2];
				number[1] = 1;
				if (npc.maximumHitPoints <= 50)
				{
						number = getLoot("EASY", LOW_NPC_DROPS[Misc.random(LOW_NPC_DROPS.length - 1)], 2000);
				}
				else if (npc.maximumHitPoints <= 100)
				{
						if (Misc.hasPercentageChance(percentageChance))
						{
								number = getLoot("MEDIUM", MEDIUM_NPC_DROPS[Misc.random(MEDIUM_NPC_DROPS.length - 1)], 7000);
						}
						else
						{
								number = getLoot("EASY", LOW_NPC_DROPS[Misc.random(LOW_NPC_DROPS.length - 1)], 4000);
						}
				}
				else
				{
						percentageChance += (npc.maximumHitPoints * 0.17);
						if (Misc.hasPercentageChance(percentageChance))
						{
								number = getLoot("HARD", HIGH_NPC_DROPS[Misc.random(HIGH_NPC_DROPS.length - 1)], 15000);
						}
						else
						{
								number = getLoot("MEDIUM", MEDIUM_NPC_DROPS[Misc.random(MEDIUM_NPC_DROPS.length - 1)], 9000);
						}
				}
				if (ItemDefinition.getDefinitions()[number[0] + 1] != null)
				{
						if (ItemDefinition.getDefinitions()[number[0] + 1].note)
						{
								int chance = 0;

								if (Misc.hasPercentageChance(chance))
								{
										number[0] = ItemDefinition.getDefinitions()[number[0] + 1].itemId;
								}
						}
				}
				Server.itemHandler.createGroundItem(player, number[0], npcX, npcY, number[1], false, 0, true, "");
				if (skipExtraDrop)
				{
						return;
				}

				// This excludes the Ice Strykewyrm which is 170, too easy.
				if (npc.maximumHitPoints < 240)
				{
						return;
				}
				for (int index = 0; index < BossPetDrops.NORMAL_BOSS_DATA.length; index++)
				{
						if (npc.npcType == BossPetDrops.NORMAL_BOSS_DATA[index][0] || npc.npcType == 2745)
						{
								if (Misc.hasPercentageChance(40))
								{
										getNormalDrop(npc, player, true, false);
								}
								if (Area.inWilderness(npc.getX(), npc.getY()))
								{
										getNormalDrop(npc, player, true, true);
										getNormalDrop(npc, player, true, true);
								}
								break;
						}
				}
		}

		public static int[] getLoot(String difficulty, int mainLoot, int stackableWorth)
		{
				stackableWorth = Misc.random(stackableWorth - (stackableWorth / 6), stackableWorth);
				int[] number = new int[2];
				if (Misc.hasPercentageChance(80))
				{
						number[0] = mainLoot;
						number[1] = 1;
				}
				else
				{
						number[0] = STACKABLE_DROPS[Misc.random(STACKABLE_DROPS.length - 1)];
						int stackableTotalWorth = stackableWorth;
						number[1] = stackableTotalWorth / ItemDefinition.getDefinitions()[number[0]].price;
						if (number[1] > 150)
						{
								if (number[0] == 995)
								{
										if (number[1] > stackableTotalWorth)
										{
												number[1] = Misc.random(stackableTotalWorth - (stackableTotalWorth / 6), stackableTotalWorth);
										}
								}
								else
								{
										number[1] = Misc.random(130, 150);
								}
						}
				}
				return number;
		}

		public static void otherDrops(Npc npc, Player player)
		{
				int item = 0;
				int amount = 1;
				switch (npc.npcType)
				{

						// Chaos druid.
						case 181:
								if (Misc.hasPercentageChance(30))
								{
										return;
								}
								int random = Misc.random(1, 100);
								for (int index = herbChance.length - 1; index >= 0; index--)
								{
										if (random <= herbChance[index])
										{
												item = herbs[index];
												break;
										}
								}
								break;
						// Tzhaar-xil
						case 2607:
								item = 6529;
								amount = Misc.random(40, 160);

								if (Misc.random(24) == 1)
								{
										Server.itemHandler.createGroundItem(player, 6522, npc.getVisualX(), npc.getVisualY(), Misc.random(3, 7), false, 0, true, "");
								}
								break;


						case 1643: // Infernal Mage.
						case 1616: // Basilisk.
								if (Misc.random(40) == 1)
								{
										item = mysticHats[Misc.random(mysticHats.length - 1)];
								}
								break;

						case 1610: // Gargoyle.
						case 1609: // Kurask.
								if (Misc.random(40) == 1)
								{
										item = mysticTops[Misc.random(mysticTops.length - 1)];
								}
								break;

						case 1627: // Turoth.
								if (Misc.random(40) == 1)
								{
										item = mysticBottoms[Misc.random(mysticBottoms.length - 1)];
								}
								break;

						case 1612: // Banshee.
						case 1632: // Rockslug.
						case 1648: // Crawling Hand.
								if (Misc.random(40) == 1)
								{
										item = mysticGloves[Misc.random(mysticGloves.length - 1)];
								}
								break;

						case 1621: // Cockatrice.
								if (Misc.random(40) == 1)
								{
										item = mysticBoots[Misc.random(mysticBoots.length - 1)];
								}
								break;

						case 53: // Red dragon
						case 54: // Black dragon
						case 55: // Blue dragon
						case 941: // Green dragon
						case 1590: // Bronze dragon
						case 11260: // Lava dragon.
								if (Misc.random(90) == 1)
								{
										item = 2366;
								}
								break;

						// God wars dungeon minions in the boss room.
						case 6206:
						case 6204:
						case 6208:
						case 6263:
						case 6265:
						case 6261:
						case 6223:
						case 6225:
						case 6227:
						case 6252:
						case 6250:
						case 6248:
								if (Misc.hasOneOutOf(4))
								{
										item = 18644;
										amount = Misc.random(160, 300);
								}
								break;

						// Godwars dungeon bosses.
						case 6203:
						case 6247:
						case 6222:
						case 6260:
								if (Misc.random(25) == 1)
								{
										item = godswordShards[Misc.random(godswordShards.length - 1)];
								}
								else if (Misc.random(75) == 1)
								{
										item = 11690;
								}
								break;
				}

				if (item != 0)
				{
						int npcX = npc.getVisualX();
						int npcY = npc.getVisualY();
						Server.itemHandler.createGroundItem(player, item, npcX, npcY, amount, false, 0, true, "");
				}
		}

		/**
		 * Drop the 100% drop of the npc, such as bones, d hide etc.
		 * @param npc
		 * @param player
		 */
		public static void getConstantDrop(Npc npc, Player player)
		{

				for (int index = 0; index < NPC_NO_BONE_DROPS.length; index++)
				{
						if (npc.npcType == NPC_NO_BONE_DROPS[index])
						{
								return;
						}
				}
				for (int index = 0; index < REVENANT_LIST.length; index++)
				{
						if (npc.npcType == REVENANT_LIST[index])
						{
								return;
						}
				}
				if (isDropNothingNpc(npc.npcType))
				{
						return;
				}
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				boolean received = false;
				if (constantDrops.get(npc.npcType) != null)
				{
						for (int item : constantDrops.get(npc.npcType))
						{
								received = true;
								if (BloodMoneyPrice.getBloodMoneyPrice(item) > 0)
								{
										if (Area.inWilderness(player))
										{
												CoinEconomyTracker.incomeList.add("WILD-PVM " + BloodMoneyPrice.getBloodMoneyPrice(item));
										}
										else
										{
												CoinEconomyTracker.incomeList.add("SAFE-PVM " + BloodMoneyPrice.getBloodMoneyPrice(item));
										}
								}
								Server.itemHandler.createGroundItem(player, item, npcX, npcY, 1, false, 0, true, "");
						}
				}
				if (!received)
				{
						Server.itemHandler.createGroundItem(player, 526, npcX, npcY, 1, false, 0, true, "");
				}

		}


		private final static int[] herbChance = {100, 80, 65, 55, 45, 35, 25, 15, 10, 5};

		private final static int[] herbs = {199, 201, 203, 205, 207, 209, 211, 213, 215, 217};

		public final static int[] mysticHats = {
				4089, // Mystic hat.
				4109, // Mystic hat.
				4099, // Mystic hat.
		};

		public final static int[] mysticTops = {
				4091, // Mystic robe top.
				4111, // Mystic robe top.
				4101, // Mystic robe top.
		};

		public final static int[] mysticBottoms = {
				4093, // Mystic robe bottom.
				4113, // Mystic robe bottom.
				4103, // Mystic robe bottom.
		};

		public final static int[] mysticGloves = {
				4095, // Mystic gloves.
				4115, // Mystic gloves.
				4105, // Mystic gloves.
		};

		public final static int[] mysticBoots = {
				4097, // Mystic boots.
				4117, // Mystic boots.
				4107, // Mystic boots.
		};


		public static void loadRareDrops()
		{
				npcRareDropsData.clear();
				npcRareDropsList.clear();
				String data = "";
				boolean lastLineWasDropRate = false;
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("data/npc/drops.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty() && !line.contains("// End"))
								{
										String[] parse = line.split(" ");
										if (line.contains("Npc:"))
										{
												int npcId = Integer.parseInt(parse[1]);
												npcRareDropsList.add(npcId);

										}
										else if (!line.startsWith("//"))
										{
												String announce = "true";
												if (line.contains("false"))
												{
														announce = "false";
												}
												String seperator = "-";
												if (data.isEmpty())
												{
														seperator = "";
												}
												int dropRate = Integer.parseInt(parse[0]);
												parse[2] = parse[2].replace("-", ",");
												data = data + seperator + dropRate + " " + parse[1] + " " + parse[2] + " " + announce;
												lastLineWasDropRate = true;
										}
								}
								else if (lastLineWasDropRate || line.contains("// End"))
								{
										npcRareDropsData.add(data);
										data = "";
										lastLineWasDropRate = false;
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public static void giveRareDrop(Player player, Npc npc)
		{
				int npcToKill = npc.npcType;
				int npcIndex = -1;
				int chance = 0;
				int itemId = 0;
				int itemAmount = 0;
				boolean announce = false;

				for (int i = 0; i < NpcDrops.npcRareDropsList.size(); i++)
				{
						if (NpcDrops.npcRareDropsList.get(i) == npcToKill)
						{
								npcIndex = i;
								break;
						}
				}
				if (npcIndex == -1)
				{
						return;
				}
				String[] drops = NpcDrops.npcRareDropsData.get(npcIndex).split("-");
				int dropsLength = drops.length;
				for (int a = 0; a < dropsLength; a++)
				{
						String[] currentLoot = drops[a].split(" ");

						chance = Integer.parseInt(currentLoot[0]);
						chance = GameMode.getDropRate(player, chance);
						if (WorldEvent.currentEvent.contains(NpcDefinition.getDefinitions()[npc.npcType].name))
						{
								chance /= 3;
						}

						if (currentLoot[2].contains(","))
						{
								String[] split = currentLoot[2].split(",");
								itemAmount = Misc.random(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
						}
						else
						{
								itemAmount = Integer.parseInt(currentLoot[2]);
						}
						if (Misc.hasOneOutOf(chance))
						{
								itemId = Integer.parseInt(currentLoot[1]);
								announce = currentLoot[3].equals("true") ? true : false;
								break;
						}
				}
				if (itemId == 0)
				{
						return;
				}
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				if (BloodMoneyPrice.getBloodMoneyPrice(itemId) > 0)
				{
						if (Area.inWilderness(player))
						{
								CoinEconomyTracker.incomeList.add("WILD-PVM " + BloodMoneyPrice.getBloodMoneyPrice(itemId));
						}
						else
						{
								CoinEconomyTracker.incomeList.add("SAFE-PVM " + BloodMoneyPrice.getBloodMoneyPrice(itemId));
						}
				}
				Server.itemHandler.createGroundItem(player, itemId, npcX, npcY, itemAmount, false, 0, true, "");

				if (announce)
				{
						String itemName = ItemAssistant.getItemName(itemId);
						if (!player.profilePrivacyOn)
						{
								Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + itemName + " from " + NpcDefinition.getDefinitions()[npc.npcType].name + "!");
						}
						RareDropLog.appendRareDrop(player, NpcDefinition.getDefinitions()[npc.npcType].name + ": " + ItemAssistant.getItemName(itemId));
						player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
				}

		}

		private final static int[] godswordShards = {11710, 11712, 11714};


}
