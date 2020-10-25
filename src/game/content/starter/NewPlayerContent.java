package game.content.starter;

import game.content.miscellaneous.SpellBook;
import game.content.quicksetup.Pure;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Skilling;
import game.log.NewPlayerIpTracker;
import game.player.Player;
import game.player.PlayerSave;
import utility.Misc;

/**
 * Starter package.
 * @author MGT Madness, created on 11-01-2014.
 */
public class NewPlayerContent
{

		/**
		 * Buttons use-able while tutorial is not finished.
		 */
		public static int[] tutorialButtonExceptionList = {9167, 9168, 9169, 98017, 98021, 98025, 98029, 98033, 98037, 14067, 9154, 9158, 9157};

		/**
		 * Set the date of when the account is created on.
		 */
		private static void setDateCreated(Player player)
		{
				player.accountDateCreated = Misc.getDate();
				player.timeOfAccountCreation = System.currentTimeMillis();
		}

		/**
		 * New player logged in.
		 */
		public static void logIn(Player player)
		{
				if (!player.isTutorialComplete())
				{
						player.setNpcType(2566);
						player.getDH().sendDialogues(270);
						player.showTutorialArrows = true;
				}
		}

		/**
		 * Starter package given to a new player.
		 * @param player
		 * 			The associated player.
		 */
		public static void giveStarterPackage(Player player)
		{
				if (player.isTutorialComplete())
				{
						return;
				}
				player.getPA().requestUpdates();
				setDateCreated(player);
				NewPlayerIpTracker.saveIp(player.addressIp, player.getPlayerName());
				PlayerSave.saveGame(player); // Left here for a reason.
		}

		public final static int[][] IRON_MAN_KIT = {{1205, 1}, {1171, 1}, {1265, 1}, {1351, 1}, {2347, 1}, {590, 1}, {1755, 1}, {946, 1}, {303, 1}, {995, 2000},};

		public final static int[][] PLAYER_KILLER_STARTER_KIT = {
				{995, 100000},
				{2528, 1},
				{386, 100},
				{4587, 1},
				{2441, 5},
				{2437, 5},
				{2435, 8},
				{3105, 1},
				{3105, 1},
				{3105, 1},
				{1725, 1},
				{1725, 1},
				{1725, 1},
				{1215, 1},
				{1215, 1},
				{1215, 1},
				{7459, 1},
				{7459, 1},
				{7459, 1},
				{3842, 1},
				{3842, 1},
				{3842, 1},};

		public final static int[][] TRAIN_COMBAT_ITEMS = {
				{995, 200000},
				{1381, 1},
				{554, 100},
				{555, 100},
				{556, 100},
				{557, 100},
				{558, 100},
				{562, 100},
				{1731, 1},
				{1323, 1},
				{1327, 1},
				{1329, 1},
				{1331, 1},
				{1333, 1},
				{882, 100},
				{841, 1},
				{853, 1},
				{380, 30},
				{1153, 1},
				{1115, 1},
				{1067, 1},
				{1191, 1},
				{1061, 1}};



		// Second bank tab.
		private final static int[] secondTab = {
				// Super strength(3).
				157,
				// Super attack(3).
				145,
				// Ranging potion(3).
				169,
				// Magic potion(3).
				3042,
				// Super defence(3).
				163,
				// Saradomin brew(3).
				6687,
				// Super restore(3).
				3026,
				// Prayer potion(3).
				139,
				// Strength potion(3).
				115,
				// Antipoison(3).
				175,
				// Super strength(2).
				159,
				// Super attack(2).
				147,
				// Ranging potion(2).
				171,
				// Magic potion(2).
				3044,
				// Super defence(2).
				165,
				// Saradomin brew(2).
				6689,
				// Super restore(2).
				3028,
				// Prayer potion(2).
				141,
				// Strength potion(2).
				117,
				// Antipoison(2).
				177,
				// Super strength(1).
				161,
				// Super attack(1).
				149,
				// Ranging potion(1).
				173,
				// Magic potion(1).
				3046,
				// Super defence(1).
				167,
				// Saradomin brew(1).
				6691,
				// Super restore(1).
				3030,
				// Prayer potion(1).
				143,
				// Strength potion(1).
				119,
				// Antipoison(1).
				179,
				// Vial.
				229,
				// 1/2 anchovy pizza.
				2299,};

		/**
		 * Add the items associated with the starter package to the bank.
		 * <p>It will read the data from the starter_package.txt file and add the items to the bank.
		 * @param player
		 * 			The associated player.
		 */
		public static void starterPackageBank(Player player)
		{
				int randomOutFitSet = Misc.random(Pure.robeAndHatSet.length - 1);
				QuickSetUp.setCombatSkills(player, "MAIN", false, null);
				SpellBook.lunarSpellBook(player);
				Skilling.updateSkillTabExperienceHover(player, 0, true);
				Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
				player.getPA().sendMessage("Check the guide button on your quest tab!");
				player.getPA().sendMessage("You have received 10m, which has been placed in your bank.");

				int[][] bank = {
						// Coins.
						{995, Misc.random(50, 500) * 1000000},
//						{18644, 2}, // Blood money
//						{385, 2000}, // Shark
//						{373, 1000}, // Swordfish
//						{2297, 150}, // Anchovy pizza
//						{2440, 200}, // Super strength(4)
//						{2436, 200}, // Super attack(4)
//						{2444, 200}, // Ranging potion(4)
//						{3040, 200}, // Magic potion(4)
//						{2442, 200}, // Super defence(4)
//						{1127, 20}, // Rune platebody
//						{8850, 2}, // Rune defender
//						{2503, 20}, // Black d'hide body
//						 Mystic robe top.
//						{QuickSetUp.getRandomMysticTop(), 20},
//						{4675, 20}, // Ancient staff
//						{6685, 200}, // Saradomin brew(4)
//						{3024, 200}, // Super restore(4)
//						{2434, 200}, // Prayer potion(4)
//						{113, 200}, // Strength potion(4)
//						{2446, 200}, // Antipoison(4)
//						{1079, 20}, // Rune platelegs
//						{4587, 20}, // Dragon scimitar
//						{2497, 20}, // Black d'hide chaps
						// Mystic robe bottom.
//						{QuickSetUp.getRandomMysticBottom(), 20},
//						{3842, 2}, // Unholy book
						// Team cape.
//						{QuickSetUp.getTeamCape(false), 100},
//						{1434, 20}, // Dragon mace
//						{3204, 20}, // Dragon halberd
//						{9075, 4000}, // Astral rune
//						{565, 5200}, // Blood rune
//						{3105, 20}, // Rock climbing boots
//						{1712, 20}, // Amulet of glory(4)
//						{10828, 20}, // Helm of neitiznot
//						{7461, 2}, // Gloves
						// Random god cape.
//						{QuickSetUp.getRandomGodCape(), 20},
//						{9244, 500}, // Dragon bolts (e)
//						{1215, 20}, // Dragon dagger
//						{1305, 20}, // Dragon longsword
//						{557, 10000}, // Earth rune
//						{560, 13400}, // Death rune
//						{4131, 20}, // Rune boots
//						{1725, 20}, // Amulet of strength
//						{3751, 20}, // Berserker helm
//						{1201, 20}, // Rune kiteshield
//						{10499, 2}, // Ava's accumulator
//						{9185, 20}, // Rune crossbow
//						{861, 20}, // Magic shortbow
//						{562, 1000}, // Chaos rune
//						{563, 1000}, // Law rune
//						{555, 15600}, // Water rune
						// Hat.
//						{Pure.robeAndHatSet[randomOutFitSet][0], 20},
						// Robe top.
//						{Pure.robeAndHatSet[randomOutFitSet][1], 20},
						// Robe bottoms.
//						{Pure.robeAndHatSet[randomOutFitSet][2], 20},
//						{6528, 20}, // Tzhaar-ket-om
//						{7459, 2}, // Gloves
//						{868, 1000}, // Rune knife
//						{892, 1000}, // Rune arrow
//						{6328, 20}, // Snakeskin boots
//						{952, 20}, // Spade
//						{1523, 20}, // Lockpick
//						{6568, 20}, // Obsidian cape
//						{544, 20}, // Monk's robe
//						{542, 20}, // Monk's robe
//						{1333, 20}, // Rune scimitar
//						{1319, 20}, // Rune 2h sword
//						{853, 20}, // Maple shortbow
//						{890, 1000}, // Adamant arrow
//						{1065, 20}, // Green d'hide vamb
//						{1059, 20}, // Leather gloves
//						{1061, 20}, // Leather boots
				};

				for (int index = 0; index < bank.length; index++)
				{
						player.bankItems[index] = bank[index][0] + 1;
						player.bankItemsN[index] = bank[index][1];
				}




				for (int index = 0; index < secondTab.length; index++)
				{
						player.bankItems1[index] = secondTab[index] + 1;
						player.bankItems1N[index] = 1;
				}
		}

		/**
		 * End the tutorial.
		 * @param player
		 * 			The associated player.
		 */
		public static void endTutorial(Player player)
		{
				player.getPA().requestUpdates();
				player.getPA().displayInterface(3559);
				player.canChangeAppearance = true;
				player.showTutorialArrows = false;
				player.setDialogueAction(126);
				NewPlayerContent.starterPackageBank(player);
				player.playerAssistant.sendMessage(":tutorial:");
		}

}