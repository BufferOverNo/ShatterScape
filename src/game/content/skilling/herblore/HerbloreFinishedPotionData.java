package game.content.skilling.herblore;


/**
 * Finished potion data, unfinished potion + ingredient = attack potion (3) etc..
 * @author Joshua Barry <Ares>, (MGT Madness created on 06-10-2015).
 */
public enum HerbloreFinishedPotionData
{

		/**
		 * Eye of Newt - Attack Potion - Guam (unf).
		 */
		ATTACK(221, 91, 121, 3, 25),
		/**
		 * Unicorn Horn Dust - Antipoison - Marrentil (unf).
		 */
		ANTIPOISON(235, 93, 175, 5, 37),
		/**
		 * Limpwurt Root - Strength Potion - Tarromin (unf).
		 */
		STRENGTH(225, 95, 115, 12, 50),
		/**
		 * Red Spider's Eggs - Restore Potion - Harralander (unf).
		 */
		RESTORE(223, 97, 127, 22, 62),
		/**
		 * Chocolate Dust - Energy Potion - Harralander (unf).
		 */
		ENERGY(1975, 97, 3010, 26, 67),
		/**
		 * White Berries - Defence Potion - Ranarr (unf).
		 */
		DEFENCE(239, 99, 133, 30, 75),
		/**
		 * Snape Grass - Prayer Potion - Ranarr (unf).
		 */
		PRAYER(231, 99, 139, 38, 87),
		/**
		 * Eye of Newt - Super Attack Potion - Irit (unf).
		 */
		SUPER_ATTACK(221, 101, 145, 45, 100),
		/**
		 * Mort Myre Fungi - Super Energy Potion - Avantoe (unf).
		 */
		SUPER_ENERGY(2970, 103, 3018, 52, 117),
		/**
		 * Limpwurt Root - Super Strength Potion - Kwuarm (unf).
		 */
		SUPER_STRENGTH(225, 105, 157, 55, 125),
		/**
		 * Red Spider's Eggs - Super Restore Potion - Snapdragon (unf).
		 */
		SUPER_RESTORE(223, 3004, 3026, 63, 142),
		/**
		 * White Berries - Super Defence Potion - Cadantine (unf).
		 */
		SUPER_DEFENCE(239, 107, 163, 66, 150),

		/**
		 *  Dragonscale dust - Antifire potion - Lantadyme (unf).
		 */
		ANTIFIRE(241, 2483, 2454, 69, 158),
		/**
		 * Wine of Zamorak - Ranging Potion - Dwarf weed (unf).
		 */
		RANGING(245, 109, 169, 72, 162),

		/**
		 *  Potato cactus - Magic Potion - Lantadyme (unf).
		 */
		MAGIC(3138, 2483, 3042, 76, 172),

		/**
		 * Jangerberries - Zamorak brew - Torstol potion (unf).
		 */
		ZAMORAK_BREW(247, 111, 2450, 78, 175),

		/** 
		 * Crushed nest - Saradomin brew - Toadflax (unf).
		 */
		SARADOMIN_BREW(6693, 3002, 6687, 81, 180),

		/** 
		 * Papaya fruit - Recovery potion - Super energy(3).
		 */

		RECOVER_SPECIAL(5972, 3018, 15300, 84, 200),

		/** 
		 * Phoenix feather - Super antifire - Antfire potion(3).
		 */
		SUPER_ANTIFIRE(4621, 2454, 15305, 85, 210),

		/** 
		 * Clean avantoe - Extreme attack potion - Super attack(3).
		 */
		EXTREME_ATTACK(261, 145, 15309, 88, 220),

		/** 
		 * Clean Dwarf weed - Extreme strength potion - Super strength(3).
		 */
		EXTREME_STRENGTH(267, 157, 15313, 89, 230),

		/** 
		 * Clean lantadyme - Extreme defence potion - Super defence(3).
		 */
		EXTREME_DEFENCE(2481, 163, 15317, 90, 240),

		/** 
		 * Ground mud rune - Extreme magic potion - Magic potion(3).
		 */
		EXTREME_MAGIC(9594, 3042, 15321, 91, 250),

		/** 
		 * Grenwall spikes - Extreme ranging potion - Ranging potion(3).
		 */
		EXTREME_RANGING(12539, 169, 15325, 92, 260),

		/** 
		 * Bonemeal - Super prayer potion - Prayer potion(3).
		 */
		SUPER_PRAYER(6810, 139, 15328, 94, 270);

		/**
		 * The id.
		 */
		private int ingredient;

		/**
		 * The index.
		 */
		private int unfinishedPotionId;

		/**
		 * The reward.
		 */
		private int finishedProduct;

		/**
		 * The level.
		 */
		private int level;

		/**
		 * The experience.
		 */
		private int exp;

		private HerbloreFinishedPotionData(int ingredient, int unfinishedPotionId, int finishedProduct, int level, int exp)
		{
				this.ingredient = ingredient;
				this.unfinishedPotionId = unfinishedPotionId;
				this.level = level;
				this.finishedProduct = finishedProduct;
				this.exp = exp;
		}

		public int getIngredientId()
		{
				return ingredient;
		}

		public int getUnfinishedPotionId()
		{
				return unfinishedPotionId;
		}

		/**
		 * Gets the exp.
		 * 
		 * @return The exp.
		 */
		public int getExperience()
		{
				return exp;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel()
		{
				return level;
		}

		/**
		 * @return
		 *			The item identity of the finished product.
		 */
		public int getFinishedProduct()
		{
				return finishedProduct;
		}
}
