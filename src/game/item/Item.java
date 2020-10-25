package game.item;

public class Item
{

		/**
		 * Items that cover the player's arms.
		 */
		private static String[] fullBody = {
				"snakeskin body",
				"top",
				"chestplate",
				"shirt",
				"platebody",
				"ahrim's robetop",
				"karils leathertop",
				"brassard",
				"Robe top",
				"robetop",
				"spined body",
				"platebody (t)",
				"platebody (g)",
				"chestplate",
				"prince tunic",
				"torso",
				"hauberk",
				"blouse",
				"jacket",
				"prince tunic",
				"d'hide Body",
				"pernix body",
				"dragon chainbody",
				"morrigan's leather body",
				"runecrafter robe",
				"moonclan armour",
				"vesta's chainbody",
				"gown",
				"granite body",
				"chicken wings"};

		private static String[] fullBodyException = {"pirate shirt",};

		/**
		 * Items that cover the player's head but not the beard.
		 */
		private static String[] normalHelm = {
				"med helm",
				"berserker helm",
				"coif",
				"dharok's helm",
				"hood",
				"spiny helmet",
				"coif",
				"archer helm",
				"farseer helm",
				"warrior helm",
				"Void",
				"lumberjack hat",
				"reindeer hat",
				"snakeskin na",
				"larupia hat",
				"kyatt hat",
				"bomber cap",
				"dwarven helmet",
				"sagittarian body",
				"helm of neitiznot",
				"profound decorative helm",
				"mitre",
				"coif",
				"helm",
				"leather cowl",
				"desert disguise",
				"bearhead",
				"menap",
				"camel mask",
				"pirate bandana",
				"chicken head"

		};

		/**
		 * Items that cover the player's entire head.
		 */
		private static String[] fullmask = {
				"snakeskin bandana",
				"full helm",
				"Initiate helm",
				"gorgonite full helm (b)",
				"primal full helm (b)",
				"zephyrium full helm (b)",
				"argonite full helm (b)",
				"verac's helm",
				"guthan's helm",
				"karil's coif",
				"mask",
				"torag's helm",
				"sallet",
				"saradomin helm",
				"lunar helm",
				"black full helm (t)",
				"rune heraldic helm",
				"armadyl helmet",
				"adamant full helm (t)",
				"adamant full helm (g)",
				"black full helm (g)",
				"black full helm (t)",
				"rune full helm (g)",
				"rune full helm (t)",
				"basic decorative helm",
				"detailed decorative helm",
				"slayer helmet",
				"intricate decorative helm",};

		/**
		 * @param itemId
		 * 			The item ID to check.
		 * @return
		 * 			True, if the item ID is an item that covers the player's arms.
		 */
		public static boolean isFullBody(int itemId)
		{
				String itemName = getItemName(itemId);
				if (itemName == null)
				{
						return false;
				}
				itemName = itemName.toLowerCase();
				if (itemId == 426 || itemId == 577 || itemId == 6129 || itemId == 544 || itemId == 1035) // Wizard robes, rock-shell plate, Monk's robe.
				{
						return true;
				}
				for (int i = 0; i < fullBodyException.length; i++)
				{
						if (itemName.contains(fullBodyException[i]))
						{
								return false;
						}
				}
				for (int i = 0; i < fullBody.length; i++)
				{
						if (itemName.contains(fullBody[i]))
						{
								return true;
						}
				}
				return false;
		}

		private static String[] normalHelmExceptions = {"robin hood hat", "ram skull helm", "grim reaper hood", "splitbark helm"};

		/**
		 * @param itemId
		 * 			The item ID to check.
		 * @return
		 * 			True, if the item ID is an item that covers the player's head but not the beard.
		 */
		public static boolean isNormalHelm(int itemId)
		{
				String itemName = getItemName(itemId);
				if (itemName == null)
				{
						return false;
				}
				itemName = itemName.toLowerCase();
				for (int a = 0; a < normalHelmExceptions.length; a++)
				{
						if (normalHelmExceptions[a].equals(itemName))
						{
								return false;
						}
				}
				for (int i = 0; i < normalHelm.length; i++)
				{
						if (itemName.contains(normalHelm[i]))
						{
								return true;
						}
				}
				return false;
		}

	//@formatter:off.
	private static String[] fullMaskExceptions =
	{
		"robin hood hat", 
		"sheep mask", 
		"penguin mask", 
		"wolf mask", 
		"bat mask", 
		"cat mask", 
		"highwayman mask", 
		"camel mask", 
		"cavalier and mask"
	};
	//@formatter:on.

		/**
		 * @param itemId
		 * 			The item ID to check.
		 * @return
		 * 			True, if the item ID is an item that covers the player's entire head.
		 */
		public static boolean isFullMask(int itemId)
		{
				String itemName = getItemName(itemId);
				if (itemName == null)
				{
						return false;
				}

				itemName = itemName.toLowerCase();
				for (int a = 0; a < fullMaskExceptions.length; a++)
				{
						if (fullMaskExceptions[a].equalsIgnoreCase(itemName))
						{
								return false;
						}
				}
				for (int i = 0; i < fullmask.length; i++)
				{
						if (itemName.contains(fullmask[i]))
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * 
		 * @param itemId
		 * 			The item identity.
		 * @return
		 * 			The name of the item identity.
		 */
		public static String getItemName(int itemId)
		{
				if (itemId <= 0)
				{
						return "Unarmed";
				}
				if (ItemDefinition.getDefinitions()[itemId] != null)
				{
						return ItemDefinition.getDefinitions()[itemId].name;
				}
				return "Unarmed";
		}

		static
		{
		}

}