package game.item;

import core.ServerConstants;

/**
 * Item equipment slot data.
 * @author MGT Madness, created on 12-10-2015.
 */
public class ItemSlot
{

		/**
		 * 
		 * @param item
		 * @param itemId
		 * @return
		 * 			The equipment slot which the item belongs to.
		 */
		public static int getItemEquipmentSlot(String item, int itemId)
		{
				item = item.toLowerCase();

				switch (itemId)
				{

						case 15673: // Squirrel ears.
						case 4166: // Earmuffs.
						case 18663: // Hunter's honour.
						case 18664: // Rogue's revenge.
						case 9472: // Gnome goggles.
						case 1025: // Eye patch.
						case 14824: // Octopus.
						case 13353: // Double eyepatches.
						case 13355: // Left eyepatch.
						case 15490: // Focus sight.
						case 18689: // Sagacious spectacles.
								return ServerConstants.HEAD_SLOT;

						case 7918: // Bonesack.
						case 7535: // Diving apparatus.
						case 5607: // Grain.
						case 18695: // Explorer backpack.
								return ServerConstants.CAPE_SLOT;

						case 4566: // Rubber chicken.
						case 4565: // Basket of eggs.
						case 7053: // Lit bug lantern
						case 7671: // Boxing gloves.
						case 7673: // Boxing gloves.
						case 14728: // Easter carrot.
						case 15426: // Candy cane.
						case 12844: // Toy kite.
						case 10840: // A jester stick.
						case 4084: // Sled.
						case 4024: // Monkey greegree.
						case 6525: // Toktz-xil-ek.
						case 10501: // Snowball.
						case 6522: // Toktz-xil-ul.
						case 2963: // Silver sickle.
						case 15507: // Royal sceptre.
						case 6526: // Toktz-mej-tal.
						case 18691: // Briefcase.
						case 4037: // Saradomin banner.
						case 18767: // Abyssal tentacle.
						case 18769: // Trident of the swamp.
						case 18807: // Heavy ballista.
						case 18779: // Toxic blowpipe.
						case 18787: // Abyssal buldgeon.
						case 18826: // Elder maul.
								return ServerConstants.WEAPON_SLOT;

						case 1035: // Zamorak robe.
						case 9070: // Moonclan armour.
						case 6129: // Rock-shell plate.
						case 426: // Priest gown.
						case 577: // Wizard robe.
						case 544: // Monk's robe.
						case 430: // Doctor's gown.
						case 11020: // Chicken wings.
								return ServerConstants.BODY_SLOT;


						case 18790: // Book of darkness
						case 18789: // Book of law
						case 18832: // Twisted buckler
						case 18843: // Malediction ward
						case 18845: // Odium ward
						case 4225: // Crystal shield
								return ServerConstants.SHIELD_SLOT;

						case 1033: // Zamorak robe.
						case 10838: // Silly jester tights.
						case 542: // Monk's robe.
						case 4300: // H.a.m. robe.
						case 6396: // Menaphite robe.
						case 6404: // Menaphite robe.
						case 6752: // Black desert robe.
								return ServerConstants.LEG_SLOT;

						case 11019: // Chicken feet.
						case 18753: // Pegasian boots.
								return ServerConstants.FEET_SLOT;
				}

				for (int i = 0; i < ARROWS.length; i++)
				{
						if (item.contains(ARROWS[i]))
						{
								return ServerConstants.ARROW_SLOT;
						}
				}
				for (int i = 0; i < CAPES.length; i++)
				{
						if (item.contains(CAPES[i]))
						{
								return ServerConstants.CAPE_SLOT;
						}
				}
				for (int i = 0; i < HATS.length; i++)
				{
						if (item.contains(HATS[i]) && !item.contains("hatchet"))
						{
								return ServerConstants.HEAD_SLOT;
						}
				}

				for (int i = 0; i < AMULETS.length; i++)
				{
						if (item.contains(AMULETS[i]))
						{
								return ServerConstants.AMULET_SLOT;
						}
				}
				for (int i = 0; i < WEAPONS.length; i++)
				{
						if (item.contains(WEAPONS[i]) && !item.contains("arcane"))
						{
								return ServerConstants.WEAPON_SLOT;
						}
				}
				for (int i = 0; i < BODY.length; i++)
				{
						if (item.contains(BODY[i]))
						{
								return ServerConstants.BODY_SLOT;
						}
				}
				for (int i = 0; i < SHIELDS.length; i++)
				{
						if (item.contains(SHIELDS[i]))
						{
								return ServerConstants.SHIELD_SLOT;
						}
				}
				for (int i = 0; i < LEGS.length; i++)
				{
						if (item.contains(LEGS[i]))
						{
								return ServerConstants.LEG_SLOT;
						}
				}
				for (int i = 0; i < GLOVES.length; i++)
				{
						if (item.contains(GLOVES[i]))
						{
								return ServerConstants.HAND_SLOT;
						}
				}
				for (int i = 0; i < BOOTS.length; i++)
				{
						if (item.contains(BOOTS[i]))
						{
								return ServerConstants.FEET_SLOT;
						}
				}
				for (int i = 0; i < RINGS.length; i++)
				{
						if (item.contains(RINGS[i]))
						{
								return ServerConstants.RING_SLOT;
						}
				}
				return -1;
		}

		private static String[] HATS = {
				"boater",
				"cowl",
				"head",
				"peg",
				"coif",
				"helm",
				"mask",
				"hat",
				"headband",
				"hood",
				"disguise",
				"cavalier",
				"full",
				"tiara",
				"helmet",
				"ears",
				"crown",
				"partyhat",
				"helm(t)",
				"helm(g)",
				"beret",
				"facemask",
				"sallet",
				"hat(g)",
				"hat(t)",
				"bandana",
				"mitre",
				"afro",
				"Afro",
				"Lord marshal cap",
				"cap",};

		private static String[] CAPES = {"cape", "accumulator", "attractor", "cloak", "alerter", "kal", "master cape"};

		private static String[] AMULETS = {"amulet", "scarf", "necklace", "pendant", "symbol", "stole", "ammy"};

		private static String[] WEAPONS = {
				"hand",
				"mace",
				"dart",
				"knife",
				"javelin",
				"scythe",
				"claws",
				"bow",
				"crossbow",
				"c' bow",
				"adze",
				"axe",
				"hatchet",
				"sword",
				"rapier",
				"scimitar",
				"spear",
				"dagger",
				"staff",
				"wand",
				"blade",
				"whip",
				"silverlight",
				"darklight",
				"maul",
				"halberd",
				"anchor",
				"tzhaar-ket-om",
				"hammer",
				"hand cannon",
				"flail",
				"crozier",
				"cane",
				"flower",
				"flag",
				"hook"};

		private static String[] BODY = {
				"varrock armour",
				"body",
				"top",
				"Priest gown",
				"apron",
				"shirt",
				"platebody",
				"robetop",
				"body(g)",
				"body(t)",
				"wizard robe (g)",
				"wizard robe (t)",
				"body",
				"brassard",
				"blouse",
				"tunic",
				"leathertop",
				"chainbody",
				"hauberk",
				"shirt",
				"torso",
				"chestplate",
				"jacket",
				"runecrafter robe",};

		private static String[] SHIELDS = {"kiteshield", "book", "Kiteshield", "toktz-ket-xil", "Toktz-ket-xil", "shield", "kite", "defender", "tome"};

		private static String[] LEGS = {
				"tassets",
				"chaps",
				"bottoms",
				"gown",
				"trousers",
				"platelegs",
				"robebottoms",
				"plateskirt",
				"legs",
				"leggings",
				"shorts",
				"skirt",
				"cuisse",
				"trousers",
				"pantaloons",
				"tasset",
				"robe bottom",
				"enchanted robe",
				"villager robe",
				"navy slacks"};

		private static String[] GLOVES = {"glove", "vamb", "gauntlets", "bracers", "vambraces", "bracelet", "villager armband",};

		private static String[] BOOTS = {"boots", "shoes", "flipper", "sandal"};

		private static String[] RINGS = {"ring"};

		private static String[] ARROWS = {"bolts", "arrow", "bolt rack", "hand cannon shot", "dragon javelin"};

}
