package game.content.miscellaneous;

import java.util.ArrayList;

import core.Server;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.commands.NormalCommand;
import game.content.interfaces.InterfaceAssistant;
import game.content.profile.ProfileRank;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcDrops;
import game.player.Player;
import utility.Misc;

/**
 * Clue scroll system.
 * @author MGT Madness, created on 03-01-2015.
 */
public class ClueScroll
{

		private static enum ClueScrollData
		{
		// @formatter:off.
		DRAYNOR(3085, 3256, "Aggie I see", "Lonely and southern I feel", "I am neither inside nor outside", "the house, yet no house would", "be complete without me.", "Your treasure waits beneath me.", " ", " "),
		PORT_SARIM(2656, 3161, "After trawling for bars, go to", "the nearest place to smith them and", "dig by the door.", " ", " ", " ", " ", " "),
		VARROCK_DUNGEON(3192, 9825, "I have no beginning or end.", "I am a token of the greatest love.", "My eye is red, I can fit like a glove.", "Go to the place where money they lend.", "And dig by the gate to be my friend.", " ", " ", " "),
		WILDERNESS_LEVER(3153, 3923, "If you didn't want to be here and", "in danger, you should lever things", "well enough alone.", " ", " ", " ", " ", " "),
		CHURCH(2993, 3177, "While a sea view is nice,", "it seems this church has", "not seen visitors in a while.", "Dig outside the rim of the", "window for a reward.", " ", " ", " "),
		NARDAH(3397, 2915, "As you desert this town,", "keep an eye out for a set of spines", "that could ruin nearby rugs:", "dig carefully around the greenery.", " ", " ", " ", " "),
		QUARRY(3178, 2917, "Brush off the sand and then dig in the", "quarry.", "There is a wheely handy barrow to the east.", "Don't worry, its coal to dig there - in fact,", "it's all oclay.", " ", " ", " "),
		PHASMATY(3647, 3497, "By the town of the dead, walk south down a", "rickety bridge, then dig near the slime-", "covered tree.", " ", " ", " ", " ", " "),
		EDGEVILLE(3088, 3469, "Come to the evil ledge.", "Yew know yew want to.", "Try not to get stung.", " ", " ", " ", " ", " "),
		MORTTON(3489, 3288, "Covered in shadows, the centre of the circle", "is where you will find the answer.", " ", " ", " ", " ", " ", " "),
		POLLVINEACH(3359, 2972, "Dig here if you are not feeling too well after", "travelling through the desert. Ali heartily", "recommends it.", " ", " ", " ", " ", " "),
		GNOME_VILLAGE(2460, 3505, "Dig near some giant mushrooms behind the", "Grand Tree.", " ", " ", " ", " ", " ", " "),
		WILDNERESS_GRAVEYARD(3174, 3664, "I lie lonely and forgotten in mid wilderness,", "where the dead rise from their beds.", "Feel free to quarrel and wind me up,", "and dig while you shoot their heads.", " ", " ", " ", " "),
		KARAMJA(2832, 9586, "Mine was the strangest birth under the sun.", "I left the crimson sack, Yet life had not", "begun.", "Entered the world, and yet was seen by", "none.", " ", " ", " "),
		VARROCK_SEWERS(3161, 9904, "My giant guardians below", "the market streets would", "be fans of rock and roll,", "if only they could grab hold of it.", "Dig near my purple smoke!", " ", " ", " ");
		// @formatter:on.
				private int digX;

				private int digY;

				private String line1;

				private String line2;

				private String line3;

				private String line4;

				private String line5;

				private String line6;

				private String line7;

				private String line8;


				private ClueScrollData(int digX, int digY, String line1, String line2, String line3, String line4, String line5, String line6, String line7, String line8)
				{
						this.digX = digX;
						this.digY = digY;
						this.line1 = line1;
						this.line2 = line2;
						this.line3 = line3;
						this.line4 = line4;
						this.line5 = line5;
						this.line6 = line6;
						this.line7 = line7;
						this.line8 = line8;
				}

				public int getDigX()
				{
						return digX;
				}

				public int getDigY()
				{
						return digY;
				}

				public String getLine1()
				{
						return line1;
				}

				public String getLine2()
				{
						return line2;
				}

				public String getLine3()
				{
						return line3;
				}

				public String getLine4()
				{
						return line4;
				}

				public String getLine5()
				{
						return line5;
				}

				public String getLine6()
				{
						return line6;
				}

				public String getLine7()
				{
						return line7;
				}

				public String getLine8()
				{
						return line8;
				}

		}

		/**
		 * The low value reward list that comes with the Casket reward.
		 */
		private static int[][] randomRewards = {
				{995, 50000},
				{995, 40000},
				{995, 30000},
				{995, 20000},
				{554, 100},
				{555, 100},
				{556, 100},
				{557, 100},
				{558, 100},
				{559, 100},
				{560, 100},
				{561, 100},
				{562, 100},
				{563, 100},
				{564, 100},
				{565, 100},
				{566, 100},
				{1731, 1
						// Amulet of power.
						}, {1729, 1
						// Amulet of defence.
						}, {1727, 1
						// Amulet of magic.
						}, {1725, 1
						// Amulet of strength.
						}, {1704, 1
						// Amulet of glory.
						}};

	//@formatter:off
	public static int[] easyClueScrollItems =
	{
		2587, 2589, 2585, 2583,// Full black (t)
		2595, 2597, 2593, 2591, // Full black (g)
		2633, 2635, 2637, // Beret
                7392, 7396, 7388, //Full wizard (t)
                7386, 7390, 7394, //Full wizard (g)
		7364, 7368, // Full studded (t)
		7366, 7362, // Full studded (g)
		10404, 10406, // Red elegant full
		10408, 10410, // Blue elegant full
		10412,10414, // Green elegant full
		10738, // Amulet of magic (t)
		2631, // Highwayman mask.
		10392, // A powdered wig.
		10394, // Flared trousers.
		10396, // Pantaloons.
		10398, // Sleeping cap.
		
		//Vestment robes full (saradomin/zamorak/guthix robe)
                10458, 10460, 10462, 10464, 10466, 10468, 10470, 10472, 10474, 10440, 10442,
                10444, 10446, 10448, 10450, 10452, 10454, 10456,
                
                // God pages.
                3831, 3832, 3833, 3834, 3827, 3828, 3829, 3830, 3835, 3836, 3837, 3838,
	};
	
	public static int[] mediumClueScrollItems =
	{
		
		2599, 2601, 2603, 2605, // Full adamant (t)
		2607, 2609, 2611, 2613, // Full adamant (g)
		2645, 2647, 2649, // Headbands
		
		// Elegant sets (Black, Red, Blue, Purple, Green)
		10400, 10402, 10404, 10406, 10408,
		10410, 10412, 10414, 10416, 10418,
		10420, 10422, 10424, 10426, 10428,
		10430, 10432, 10434, 10436, 10438,

		13109, // Penguin mask.
		10364, // Amulet of strength (t)
		2579, // Wizard boots.
		7319, 7321, 7323, 7325, 7327, // Boater set.
	};
	
	public static int[] hardClueScrollNormalItems =
	{
                2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, // Full rune (g) and (t)
                2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673, 2675, // Saradomin/Zamorak/Guthix full rune armour
                10368, 10370, 10372, 10374, 10376, 10378, 10380, 10382, 10384, 10386, 10388, 10390, // Full Saradomin/Zamorak and Guthix d'hide sets.
		8950, // Pirate hat.
		13101, // Top hat.
		13103, // Pith helmet.
		2639, 2641, 2643, // Cavalier set.
		10362, // Amulet of glory (t).
                3481, 3483, 3485, 3486, 3488, // Full gilded armour.
		2581, // Robin hood hat
		2577 // Ranger boots.
	};

	public static int[] thirdAgeSetItems =
	{
		10330, 10332, 10334, 10336, 10338, 10340, 10342, 10344, 10346, 10348, 10350, 10352 // Third age set.
	};
	//@formatter:on

		/**
		 * Open the clue scroll and show the interface riddle.
		 * @param player
		 * 			The associated player.
		 */
		public static void openClueScroll(Player player)
		{
				if (player.clueScrollType == -1)
				{
						player.clueScrollType = Misc.random((ClueScrollData.values().length) - 1);
				}
				showAppropriateClueScroll(player);
				player.getPA().displayInterface(6965);
		}

		/**
		 * Show the correct Clue scroll interface text, depending on the player's Clue scroll.
		 * @param player
		 * 			The associated player.
		 */
		private static void showAppropriateClueScroll(Player player)
		{
				int clueScrollType = player.clueScrollType;
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine1(), 6968);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine2(), 6969);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine3(), 6970);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine4(), 6971);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine5(), 6972);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine6(), 6973);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine7(), 6974);
				player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine8(), 6975);
		}

		/**
		 * Dig for a casket.
		 * @param player
		 * 			The associated player.
		 */
		public static void dig(Player player)
		{
				if (player.clueScrollType <= -1)
				{
						return;
				}
				if (!ItemAssistant.hasItemInInventory(player, 2677))
				{
						return;
				}
				if (player.getPA().withInDistance(player.getX(), player.getY(), ClueScrollData.values()[player.clueScrollType].getDigX(), ClueScrollData.values()[player.clueScrollType].getDigY(), 6))
				{
						player.clueScrollType = -1;
						ItemAssistant.addItemToInventoryOrDrop(player, 2740, 1);
						ItemAssistant.deleteItemFromInventory(player, 2677, 1);
						if (ItemAssistant.hasSingularUntradeableItem(player, 2677))
						{
								player.singularUntradeableItemsOwned.remove(Integer.toString(2677));
						}
						Achievements.checkCompletionMultiple(player, "1044 1067 1118");
						player.getPA().sendMessage("You have found a casket!");
				}
		}

		/**
		 * Open the casket.
		 * @param player
		 * 			The associated player.
		 */
		public static void openCasket(Player player)
		{
				if (System.currentTimeMillis() - player.casketTime <= 500)
				{
						return;
				}
				player.casketTime = System.currentTimeMillis();
				int item2 = 0;
				int amount2 = 0;
				int item3 = 0;
				int amount3 = 1;
				int item2Random = Misc.random(Misc.random(randomRewards.length - 1));
				item2 = randomRewards[item2Random][0];
				amount2 = randomRewards[item2Random][1];
				if (amount2 > 1)
				{
						amount2 = Misc.random(amount2 + 1);
				}
				if (Misc.hasOneOutOf(110))
				{
						item3 = thirdAgeSetItems[Misc.random(thirdAgeSetItems.length - 1)];
						RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
						if (!player.profilePrivacyOn)
						{
								Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue scroll.");
						}
						player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
				}
				else
				{
						int random = Misc.random(1, 11);
						if (random >= 8)
						{
								item3 = hardClueScrollNormalItems[Misc.random(hardClueScrollNormalItems.length - 1)];

								// Ranger boots.
								if (item3 == 2577)
								{
										RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
										if (!player.profilePrivacyOn)
										{
												Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue scroll.");
										}
										player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
								}
						}
						else
						{
								if (Misc.hasPercentageChance(65))
								{
										item3 = easyClueScrollItems[Misc.random(easyClueScrollItems.length - 1)];
								}
								else
								{
										item3 = mediumClueScrollItems[Misc.random(mediumClueScrollItems.length - 1)];
								}
						}
				}
				displayReward(player, item2, amount2, item3, amount3);
		}

		/**
		 * Display the casket reward on an interface.
		 * @param player
		 * 			The associated player.
		 * @param item2
		 * 			The item identity of the first reward.
		 * @param amount2
		 * 			The item amount of the first reward.
		 * @param item3
		 * 			The item identity of the second reward.
		 * @param amount3
		 * 			The item amount of the second reward.
		 */
		private static void displayReward(Player player, int item2, int amount2, int item3, int amount3)
		{
				player.setClueScrollsCompleted(player.getClueScrollsCompleted() + 1);
				player.getPA().sendFilterableMessage("Your clue scroll count is: " + ServerConstants.RED_COL + player.getClueScrollsCompleted() + ".");
				ProfileRank.rankPopUp(player, "ADVENTURER");
				ItemAssistant.deleteItemFromInventory(player, 2740, 1);
				ItemAssistant.addItemToInventoryOrDrop(player, item2, amount2);
				ItemAssistant.addItemToInventoryOrDrop(player, item3, amount3);
				ArrayList<String> list = new ArrayList<String>();
				list.add(item2 + " " + amount2);
				list.add(item3 + " " + amount3);
				InterfaceAssistant.displayReward(player, list);
		}

		public static void dropClueScroll(Npc npc, Player player)
		{
				if (player == null)
				{
						return;
				}

				if (NpcDrops.isDropNothingNpc(npc.npcType))
				{
						return;
				}
				if (ItemAssistant.hasSingularUntradeableItem(player, 2677))
				{
						return;
				}
				if (player.getPlayerName().equals("Arab Unity"))
				{
						NormalCommand.clueScrollDebug.add("Give clue scroll1.");

						for (int index = 0; index < player.singularUntradeableItemsOwned.size(); index++)
						{
								int itemList = Integer.parseInt(player.singularUntradeableItemsOwned.get(index));
								NormalCommand.clueScrollDebug.add("'" + itemList + "'");
						}
						NormalCommand.clueScrollDebug.add("List end.");
				}
				int chance = 0;
				int npcHp = npc.maximumHitPoints;
				if (npcHp > 250)
				{
						npcHp = 250;
				}
				chance = 270 - npcHp;
				chance /= 2.6;


				boolean ring = ItemAssistant.hasItemEquippedSlot(player, 2572, ServerConstants.RING_SLOT);
				boolean imbuedRing = ItemAssistant.hasItemEquippedSlot(player, 18656, ServerConstants.RING_SLOT);
				chance *= (ring ? 0.96 : imbuedRing ? 0.90 : 1.0);
				if (chance <= 20)
				{
						chance = 20;
				}
				if (Misc.hasOneOutOf(chance))
				{
						int npcX = npc.getVisualX();
						int npcY = npc.getVisualY();
						Server.itemHandler.createGroundItem(player, 2677, npcX, npcY, 1, false, 0, true, "");
						player.playerAssistant.sendMessage(ServerConstants.GREEN_COL + "You have received a clue scroll drop.");
				}
		}

}