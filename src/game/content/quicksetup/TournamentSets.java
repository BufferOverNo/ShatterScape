package game.content.quicksetup;

import game.player.Player;
import utility.Misc;

/**
 * Tournament quick set up layouts
 * @author MGT Madness, created on 29-03-2017.
 */
public class TournamentSets
{

		public static int[][] pureTribridInventory = {
				{9185, 1},
				{10499, 1},
				{18820, 1},
				{2440, 1},
				{2497, 1},
				{1215, 1},
				{18637, 1},
				{2436, 1},
				{4587, 1},
				{3024, 1},
				{18637, 1},
				{2444, 1},
				{6685, 1},
				{3024, 1},
				{6685, 1}, // Saradomin brew(4)
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1},
				{18637, 1}};

		public static int[][] tournamentInventory(Player player, String type)
		{
				//{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows"};

				if (type.contains("Nh"))
				{

						int[][] inventory = {
								{18799, 1}, // Super combat potion(4)
								{10499, 1}, // Ava's accumulator
								{2497, 1}, // Black d'hide chaps
								{18767, 1}, // Abyssal tentacle
								{2444, 1}, // Ranging potion(4)
								{9185, 1}, // Rune crossbow
								{2503, 1}, // Black d'hide body
								{18759, 1}, // Dragon defender
								{18820, 1}, // Rune pouch
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{Misc.hasPercentageChance(50) ? 14484 : 11694, 1}, // Armadyl godsword
								{3024, 1}, // Super restore(4)
								{3024, 1}, // Super restore(4)
								{3024, 1}, // Super restore(4)
								{3024, 1}, // Super restore(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
						};
						return inventory;
				}
				else
				{
						int[][] inventory = {
								{18799, 1}, // Super combat potion(4)
								{type.contains("barrows") ? 18637 : 2497, 1}, // Black d'hide chaps
								{type.startsWith("Berserker") ? 4587 : type.equals("Main hybrid barrows") ? 18767 : 4151, 1}, // Abyssal whip
								{type.contains("barrows") ? getRandomBarrowsMeleeBottom() : 1079, 1}, // Rune platelegs
								{3040, 1}, // Magic potion(4)
								{type.contains("barrows") ? 4736 : 2503, 1}, // Black d'hide body
								{type.startsWith("Berserker") ? 8850 : 18759, 1}, // Dragon defender
								{type.contains("barrows") ? getRandomBarrowsMeleeTop() : 10551, 1}, // Fighter torso
								{3024, 1}, // Super restore(4)
								{3024, 1}, // Super restore(4)
								{1215, 1}, // Dragon dagger
								{18820, 1}, // Rune pouch
								{6685, 1}, // Saradomin brew(4)
								{6685, 1}, // Saradomin brew(4)
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
								{18637, 1}, // Dark crab
						};
						return inventory;
				}
		}

		public static int[][] tournamentInventoryDharok(Player player, String type)
		{
				int[][] inventory = {
						{18799, 1}, // Super combat potion(4)
						{3024, 1}, // Super restore(4)
						{18820, 1}, // Rune pouch
						{18637, 1}, // Dark crab
						{6685, 1}, // Saradomin brew(4)
						{3024, 1}, // Super restore(4)
						{6685, 1}, // Saradomin brew(4)
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{4718, 1}, // Dharok's greataxe
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{1187, 1}, // Dragon sq shield
						{5698, 1}, // Dragon dagger (p++)
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
				};


				return inventory;
		}

		public static int[][] tournamentEquipment(Player player, String type)
		{
				if (type.contains("Nh"))
				{
						int[][] equipment = {
								{10828, 1}, // Helm of neitiznot
								{QuickSetUp.getRandomGodCape(), 1}, // Saradomin cape
								{1712, 1}, // Amulet of glory(4)
								{4675, 1}, // Ancient staff
								{QuickSetUp.getRandomMysticTop(), 1}, // Mystic robe top
								{13736, 1}, // Blessed spirit shield
								{-1, 1}, // Unarmed
								{QuickSetUp.getRandomMysticBottom(), 1}, // Mystic robe bottom
								{-1, 1}, // Unarmed
								{7462, 1}, // Gloves
								{3105, 1}, // Climbing boots
								{-1, 1}, // Unarmed
								{-1, 1}, // Unarmed
								{9244, 69}, // Dragon bolts (e)
						};
						return equipment;
				}
				else
				{
						int[][] equipment = {
								{type.startsWith("Berserker") ? 3751 : 10828, 1}, // Helm of neitiznot
								{QuickSetUp.getRandomGodCape(), 1},
								{1712, 1}, // Amulet of glory(4)
								{4675, 1}, // Ancient staff
								{QuickSetUp.getRandomMysticTop(), 1},
								{type.equals("Main hybrid barrows") ? 18790 : 3842, 1}, // Unholy book
								{-1, 0}, // Unarmed
								{QuickSetUp.getRandomMysticBottom(), 1},
								{-1, 0}, // Unarmed
								{7462, 1}, // Gloves
								{3105, 1}, // Climbing boots
								{-1, 0}, // Unarmed
								{-1, 0}, // Unarmed
								{-1, 0}, // Unarmed
						};
						return equipment;
				}
		}

		public static int[][] tournamentEquipmentDharok(Player player, String type)
		{
				int[][] equipment = {
						{4716, 1}, // Dharok's helm
						{6570, 1}, // Fire cape
						{1712, 1}, // Amulet of glory(4)
						{4587, 1}, // Dragon scimitar
						{4720, 1}, // Dharok's platebody
						{18759, 1}, // Dragon defender
						{-1, 0}, // Unarmed
						{4722, 1}, // Dharok's platelegs
						{-1, 0}, // Unarmed
						{7462, 1}, // Gloves
						{11732, 1}, // Dragon boots
						{-1, 0}, // Unarmed
						{-1, 0}, // Unarmed
						{-1, 0}, // Unarmed
				};
				return equipment;
		}



		public static int getRandomBarrowsMeleeTop()
		{
				return barrowsMeleeTop[Misc.random(barrowsMeleeTop.length - 1)];
		}

		public static int getRandomBarrowsMeleeBottom()
		{
				return barrowsMeleeBottom[Misc.random(barrowsMeleeBottom.length - 1)];
		}

		public static int getRandomSerpHelm()
		{
				return serpHelm[Misc.random(serpHelm.length - 1)];
		}

		private final static int[] serpHelm = {18773, 18775, 18777,};

		private final static int[] barrowsMeleeTop = {
				4720, // Dharok's platebody
				4728, // Guthan's platebody
				4749, // Torag's platebody
		};

		private final static int[] barrowsMeleeBottom = {
				4722, // Dharok's platelegs
				4730, // Guthan's chainskirt
				4751, // Torag's platelegs
				4759, // Verac's plateskirt
		};


		public static int[][] tournamentInventoryMaxBrid(Player player, String type)
		{
				int[][] inventory = {
						{18799, 1}, // Super combat potion(4)
						{18809, 1}, // Amulet of torture
						{18767, 1}, // Abyssal tentacle
						{11726, 1}, // Bandos tassets
						{3040, 1}, // Magic potion(4)
						{18755, 1}, // Primordial boots
						{18759, 1}, // Dragon defender
						{11724, 1}, // Bandos chestplate
						{3024, 1}, // Super restore(4)
						{3024, 1}, // Super restore(4)
						{Misc.hasOneOutOf(2) ? 11694 : 14484, 1}, // Armadyl godsword
						{4736, 1}, // Karil's leathertop
						{6685, 1}, // Saradomin brew(4)
						{6685, 1}, // Saradomin brew(4)
						{6685, 1}, // Saradomin brew(4)
						{3024, 1}, // Super restore(4)
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18637, 1}, // Dark crab
						{18820, 1}, // Rune pouch
				};


				return inventory;
		}

		public static int[][] tournamentEquipmentMaxBrid(Player player, String type)
		{
				int[][] equipment = {
						{getRandomSerpHelm(), 1}, // Serp helm
						{QuickSetUp.getRandomGodCape(), 1}, // Saradomin cape
						{18765, 1}, // Occult necklace
						{18783, 1}, // Toxic staff of the dead
						{4712, 1}, // Ahrim's robetop
						{13738, 1}, // Arcane spirit shield
						{-1, 1}, // Unarmed
						{4714, 1}, // Ahrim's robeskirt
						{-1, 1}, // Unarmed
						{7462, 1}, // Gloves
						{18751, 1}, // Eternal boots
						{-1, 1}, // Unarmed
						{15220, 1}, // Berserker ring (i)
						{-1, 1}, // Unarmed
				};
				return equipment;
		}



}
