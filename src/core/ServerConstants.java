package core;

import game.bot.BotManager;

/**
 * Constants regarding whole server.
 */
public class ServerConstants
{

		/**
		 * Items cannot stake or trade.
		 * Must also include in ITEMS_TO_INVENTORY_ON_DEATH
		 */
		public static final int[] UNTRADEABLE_ITEMS = {
				18674, // Max cape.
				18675, // Fire max cape.
				18676, // Saradomin max cape.
				18677, // Zamorak max cape.
				18678, // Guthix max cape.
				18679, // Ava's max cape.
				299, // Mithril seeds
				4024, // Monkey greegree
				15098, // Dice.
				15332,
				15333,
				15334,
				15335, // Overloads.
				15300,
				15301,
				15302,
				15303, // Recover special.
				15304,
				15305,
				15306,
				15307, // Super antifire.
				15308,
				15309,
				15310,
				15311, // Extreme attack.
				15312,
				15313,
				15314,
				15315, // Extreme strength.
				15316,
				15317,
				15318,
				15319, // Extreme defence.
				15320,
				15321,
				15322,
				15323, // Extreme magic.
				15324,
				15325,
				15326,
				15327, // Extreme ranged.
				15328,
				15329,
				15330,
				15331, // Super prayer.
		};

		/**
		 * Cannot appear on ground at all.
		 * Goes to inventory after death
		 * Cannot drop item, only can destroy
		 */
		public static final int[] ITEMS_TO_INVENTORY_ON_DEATH = {
				// Items with (i) are automatically untradeable.
				18659, // Magic shortbow (i)
				18663, // Hunter's honour.
				18664, // Rogue's revenge.
				2412,
				2413,
				2414, // God capes.
				10548,
				10547,
				10550,
				10549, // Barbarian assault helms .
				15349, // Ardougne cape.
				7454,
				7455,
				7456,
				7457,
				7458,
				7459,
				7460,
				7461,
				7462, // All gloves. Such as barrows gloves etc..
				3840, // Holy book.
				3842, // Unholy book.
				3844, // Book of balance.
				8844,
				8845,
				8846,
				8847,
				8848,
				8849,
				8850,
				18759, // All defenders.
				10498, // Ava's attractor.
				10499, // Ava's accumulator.
				7927, // Easter ring.
				4084, // Sled.
				12844, // Toy kite.
				15673, // Squirrel ears.
				9470, // Gnome scarf.
				9472, // Gnome goggles.
				4079, // Yo-yo.
				7671, // Boxing gloves.
				1037, // Bunny ears.
				7673, // Boxing gloves.
				4565, // Basket of eggs.
				7053, // Lit bug lantern.
				6858, // Jester hat.
				9005, // Fancy boots.
				9920, // Jack lantern mask.
				1419, // Scythe.
				10507, // Reindeer hat.
				14728, // Easter carrot.
				4566, // Rubber chicken
				6666, // Flippers
				2460, // Flowers.
				2462, // Flowers.
				2464, // Flowers.
				2466, // Flowers.
				2468, // Flowers.
				2470, // Flowers.
				2472, // Flowers.
				2474, // Flowers.
				2476, // Flowers.
				18508, // Dungoneering cape.
				18509, // Dungoneering cape (t). 

				6529, // Tokkul.

				13616,
				13614,
				13617,
				13618,
				13613,
				13619,
				13622,
				13623,
				13626,
				13624,
				13627,
				13628, // Runecrafter robes sets.

				2677, // Clue scroll.
				2740, // Casket.
				15332,
				15333,
				15334,
				15335, // Overloads.
				15300,
				15301,
				15302,
				15303, // Recover special.
				15304,
				15305,
				15306,
				15307, // Super antifire.
				15308,
				15309,
				15310,
				15311, // Extreme attack.
				15312,
				15313,
				15314,
				15315, // Extreme strength.
				15316,
				15317,
				15318,
				15319, // Extreme defence.
				15320,
				15321,
				15322,
				15323, // Extreme magic.
				15324,
				15325,
				15326,
				15327, // Extreme ranged.
				15328,
				15329,
				15330,
				15331, // Super prayer.
				13263, // Slayer helmet.
				14637, // Slayer helmet (charged).
				15098, // Dice.
				299, // Mithril seeds.
				2528, // Prayer lamp
				18741, // Prayer lamp (100k)
				18742, // Prayer lamp (500k)

				18680, // Achievement cape (t).
				1949, // Chef's hat.
				6343, // Villager robe.
				6345, // Villager hat.
				6347, // Villager armband.
				6349, // Villager sandals.
				6353, // Villager robe.
				6355, // Villager hat.
				6357, // Villager sandals.
				6359, // Villager armband.
				6363, // Villager robe.
				6365, // Villager hat.
				6367, // Villager sandals.
				6369, // Villager armband.
				6373, // Villager robe.
				6375, // Villager hat.
				6377, // Villager sandals.
				6379, // Villager armband.
				5042, // Shorts.
				5044, // Shorts.
				5046, // Shorts.
				5048, // Skirt.
				5050, // Skirt.
				5052, // Skirt.
				6392, // Menap headgear.
				6394, // Menaphite top.
				6396, // Menaphite robe.
				6402, // Menaphite top.
				6404, // Menaphite robe.
				7917, // Ram skull helm.
				7003, // Camel mask.
				14078, // Witch top.
				14079, // Witch skirt.
				14080, // Witch cloak.
				14076, // Warlock top.
				14077, // Warlock legs.
				14081, // Warlock cloak.
				1506, // Gas mask.
				4611, // Desert disguise hat.
				4502, // Bearhead.
				5607, // Grain.
				7918, // Bonesack.
				2978, // Chompy bird hat.
				2979, // Chompy bird hat.
				2980, // Chompy bird hat.
				2981, // Chompy bird hat.
				2982, // Chompy bird hat.
				2983, // Chompy bird hat.
				2984, // Chompy bird hat.
				2985, // Chompy bird hat.
				2986, // Chompy bird hat.
				2987, // Chompy bird hat.
				2988, // Chompy bird hat.
				2989, // Chompy bird hat.
				2990, // Chompy bird hat.
				2991, // Chompy bird hat.
				2992, // Chompy bird hat.
				2993, // Chompy bird hat.
				2994, // Chompy bird hat.
				2995, // Chompy bird hat.
				2890, // Elemental shield.
				9729, // Elemental helmet.
				9731, // Mind shield.
				9733, // Mind helmet.
				6235, // Broodoo shield.
				6257, // Broodoo shield.
				6279, // Broodoo shield.
				6335, // Tribal hat.
				6337, // Tribal hat.
				6339, // Tribal hat.
				6341, // Tribal top.
				6351, // Tribal top.
				6361, // Tribal top.
				6371, // Tribal top.
				3107, // Spiked boots.
				10863, // Builder's shirt.
				10864, // Builder's trousers.
				10865, // Builder's boots.
				10862, // Hard hat.
				6548, // Nurse hat.
				430, // Doctors' gown.
				6547, // Doctors hat.
				6665, // Mudskipper hat.
				5554, // Rogue mask.
				5553, // Rogue top.
				5555, // Rogue trousers.
				5556, // Rogue gloves.
				5557, // Rogue boots.
				11280, // Cavalier and mask.
				7534, // Fishbowl helmet.
				7535, // Diving apparatus.
				13105, // Spiked helmet.
				9068, // Moonclan helm.
				9069, // Moonclan hat.
				9070, // Moonclan armour.
				9071, // Moonclan skirt.
				9072, // Moonclan gloves.
				9073, // Moonclan boots.
				9074, // Moonclan cape.
				8959, // Blue tricorn hat.
				8952, // Blue naval shirt.
				8991, // Blue navy slacks.
				8960, // Green tricorn hat.
				8953, // Green naval shirt.
				8992, // Green navy slacks.
				8961, // Red tricorn hat.
				8954, // Red naval shirt.
				8993, // Red navy slacks.
				8965, // Grey tricorn hat.
				8958, // Grey naval shirt.
				8997, // Grey navy slacks.
				8963, // Black tricorn hat.
				8956, // Black naval shirt.
				8995, // Black navy slacks.
				8964, // Purple tricorn hat.
				8957, // Purple naval shirt.
				8996, // Purple navy slacks.
				8962, // Brown tricorn hat.
				8955, // Brown naval shirt.
				8994, // Brown navy slacks.
				8966, // Cutthroat flag.
				8967, // Guilded smile flag.
				8968, // Bronze fist flag.
				8969, // Lucky shot flag.
				8970, // Treasure flag.
				8971, // Phasmatys flag.
				8924, // Bandana and eyepatch.
				8927, // Bandana and eyepatch.
				8949, // Pirate bandana.
				2651, // Pirate's hat.
				2997, // Pirate's hook.
				7112, // Pirate bandana.
				7110, // Stripy pirate shirt.
				7116, // Pirate leggings.
				7114, // Pirate boots.
				8925, // Bandana and eyepatch.
				7122, // Stripy pirate shirt.
				7126, // Pirate leggings.
				8926, // Bandana and eyepatch.
				7128, // Stripy pirate shirt.
				7132, // Pirate leggings.
				7134, // Stripy pirate shirt.
				7138, // Pirate leggings.
				1025, // Eye patch.
				8928, // Hat and eyepatch.
				13107, // Sheep mask.
				13109, // Penguin mask.
				13111, // Bat mask.
				7159, // Insulated boots.
				13115, // Wolf mask.
				15490, // Focus sight.
				13113, // Cat mask.
				12645, // Chocatrice cape.
				11756, // Varrock armour 1.
				11757, // Varrock armour 2.
				11758, // Varrock armour 3.
				14577, // Falador shield 1.
				14578, // Falador shield 1.
				14579, // Falador shield 1.
				13353, // Double eyepatches.
				13355, // Left eyepatch.
				11278, // Beret mask.
				10501, // Snowball.
				// Achievement shop.
				14600,
				14602,
				14603,
				14605,
				15422,
				15423,
				15425,
				15426,
				11019,
				11020,
				11021,
				11022,
				7053,
				6858,
				6860,
				10836,
				10837,
				10838,
				10839,
				10840,
				6862,
				6863,
				6856,
				6857,
				6746, // Darklight.
				4024, // Monkey greegree.
				6109, // Ghostly hood.
				6107, // Ghostly robetop.
				6108, // Ghostly bottoms.
				6111, // Ghostly cloak.
				6110, // Ghostly gloves.
				6106, // Ghostly boots.
		};

		/**
		 *Items sent to shop on death and drop blood money for killer.
		 */
		public static final int[] ITEMS_DROP_BLOOD_MONEY = {
				18820, // Rune pouch.
				18658, // Looting bag.
				11665, // Void items.
				11664, // Void items.
				11663, // Void items.
				8839, // Void items.
				8840, // Void items.
				8842, // Void items.
				10551, // Fighter torso
				14011, // Completionist cape.
				6570, // Fire cape.
				18674, // Max cape.
				18675, // Fire max cape.
				18676, // Saradomin max cape.
				18677, // Zamorak max cape.
				18678, // Guthix max cape.
				18679, // Ava's max cape.
				18789, // Book of law
				18790, // Book of darkness

		};
		//@formatter:on

		public final static int NOT_DUELING = 0;

		public final static int IN_DUEL_INTERFACE = 1;

		public final static int ON_FIRST_SCREEN_ACCEPTED = 2;

		public final static int ON_SECOND_SCREEN = 3;

		public final static int ON_SECOND_SCREEN_ACCEPTED = 4;

		public final static int DUEL_STARTED = 5;

		public final static int HIGHSCORES_PLAYERS_AMOUNT = 30;

		public final static int DUELING = 5;

		public final static int[] animationCancel = {12565, 4410, 12567, 12589, 2763, 2756, 2761, 2764};

		/**
		 * Announce to all players.
		 */
		public static final String DARK_RED_COL = "<img=13><col=800000>";

		/**
		 * Announce rare drops.
		 */
		public static final String GREEN_COL = "<img=11><col=005f00>";

		/**
		 * Used for World events.
		 */
		public static final String DARK_BLUE = "<img=12><col=d51212>";

		/**
		 * Alert player not same ip, preset missing from bank.
		 */
		public static final String RED_COL = "<col=ef1020>";

		/**
		 * Player personal alerts, eligible to vote, password changed.
		 */
		public static final String BLUE_COL = "<col=3f3fff>";

		public static final String BLACK_COL = "<col=000000>";

		/**
		 * Special attack tracker.
		 */
		public static final String PALE_DARK_BLUE_COL = "<col=186098>";

		/**
		 * Used for anti-fire potion, teleblock.
		 */
		public static final String PURPLE_COL = "<col=804080>";

		public static final int NORMAL_HITSPLAT_COLOUR = 0;

		public static final int CRITICAL_HITSPLAT_COLOUR = 1;

		public static final int POISON_HITSPLAT_COLOUR = 2;

		public static final int YELLOW_HITSPLAT_COLOUR = 3;

		public static final int PURPLE_HITSPLAT_COLOUR = 4;

		public static final int DARK_RED_HITSPLAT_COLOUR = 5;


		public static final int NO_ICON = -1;

		public static final int MELEE_ICON = 0;

		public static final int RANGED_ICON = 1;

		public static final int MAGIC_ICON = 2;

		public static final int DEFLECT_ICON = 3;

		public static final int DRAGONFIRE_ATTACK = 3;

		public static final int ACCURATE = 0;

		public static final int AGGRESSIVE = 1;

		public static final int DEFENSIVE = 2;

		public static final int CONTROLLED = 3;


		public static final int RAPID = 1;

		public static final int LONG_RANGED = 3;

		public static final String CHARACTER_LOCATION = "./backup/characters/players/";

		public static final String flaggedNames[] = {"nigg",};

		/**
		 * Offensive words.
		 */
		public static final String offensiveLanguage[] = {
				"sex",
				"fuck",
				"kanker",
				"cancer",
				"faggot",
				"fk u",
				"bitch",
				"cunt",
				"asshole",
				"nigger",
				"gay",
				"lesbian",
				"bastard",
				"blowjob",
				"blow job",
				"bukakke",
				"bukake",
				"cum",
				"homosexual",
				"vagina",
				"penis",
				"dick",
				"shit",
				"tits",
				"cock",
				"nigga",
				"retard",
				"stfu",
				"fag",
				"fgt",
				"cnt"};

		/**
		 * Maximum amount of invalid password log-in attempts allowed.
		 */
		public static final int MAXIMUM_INVALID_ATTEMPTS = ServerConfiguration.DEBUG_MODE ? 100 : 3;

		/**
		 * The delay in milliseconds between continious connections from the same IP.
		 */
		public static final int CONNECTION_DELAY = 100;

		/**
		 * The amount of simultaneous connections from the same IP.
		 */
		public static final int IPS_ALLOWED = 2;

		/**
		 * Equivelant to 40 seconds.
		 * <p>
		 * This is used to force disconnect the player if in combat.
		 */
		public static final int TIMEOUT = 67;

		/**
		 * The maximum item identity.
		 */
		public static final int MAX_ITEM_ID = 20000;

		/**
		 * The maximum amount of a single item.
		 */
		public static final int MAX_ITEM_AMOUNT = Integer.MAX_VALUE;

		/**
		 * Remove player from Duel arena to this X coordinate.
		 */
		public static final int DUEL_ARENA_X = 3362;

		/**
		 * Remove player from Duel arena to this Y coordinate.
		 */
		public static final int DUEL_ARENA_Y = 3263;

		/**
		 * The random distance from the Duel arena X and Y coordinate spawn.
		 */
		public static final int RANDOM_DISTANCE = 5;

		/**
		 * Undead NPCs identities.
		 */
		public static final int[] UNDEAD_NPCS = {90, 91, 92, 93, 94, 103, 104, 73, 74, 75, 76, 77};

		/**
		 * The slot identity of the head in the equipment tab.
		 */
		public static final int HEAD_SLOT = 0;

		/**
		 * The slot identity of the cape in the equipment tab.
		 */
		public static final int CAPE_SLOT = 1;

		/**
		 * The slot identity of the amulet in the equipment tab.
		 */
		public static final int AMULET_SLOT = 2;

		/**
		 * The slot identity of the weapon in the equipment tab.
		 */
		public static final int WEAPON_SLOT = 3;

		/**
		 * The slot identity of the torso in the equipment tab.
		 */
		public static final int BODY_SLOT = 4;

		/**
		 * The slot identity of the shield in the equipment tab.
		 */
		public static final int SHIELD_SLOT = 5;

		/**
		 * The slot identity of the leg in the equipment tab.
		 */
		public static final int LEG_SLOT = 7;

		/**
		 * The slot identity of the hand in the equipment tab.
		 */
		public static final int HAND_SLOT = 9;

		/**
		 * The slot identity of the feet in the equipment tab.
		 */
		public static final int FEET_SLOT = 10;

		/**
		 * The slot identity of the ring in the equipment tab.
		 */
		public static final int RING_SLOT = 12;

		/**
		 * The slot identity of the arrow in the equipment tab.
		 */
		public static final int ARROW_SLOT = 13;

		public final static int SARADOMIN_BLESSED_SWORD_CHARGES = 6000;

		public static final int ATTACK = 0;

		public static final int DEFENCE = 1;

		public static final int STRENGTH = 2;

		public static final int HITPOINTS = 3;

		public static final int RANGED = 4;

		public static final int PRAYER = 5;

		public static final int MAGIC = 6;

		public static final int COOKING = 7;

		public static final int WOODCUTTING = 8;

		public static final int FLETCHING = 9;

		public static final int FISHING = 10;

		public static final int FIREMAKING = 11;

		public static final int CRAFTING = 12;

		public static final int SMITHING = 13;

		public static final int MINING = 14;

		public static final int HERBLORE = 15;

		public static final int AGILITY = 16;

		public static final int THIEVING = 17;

		public static final int SLAYER = 18;

		public static final int FARMING = 19;

		public static final int RUNECRAFTING = 20;

		public static final int THICK_SKIN = 0;

		public static final int BURST_OF_STRENGTH = 1;

		public static final int CLARITY_OF_THOUGHT = 2;

		public static final int SHARP_EYE = 3;

		public static final int MYSTIC_WILL = 4;

		public static final int ROCK_SKIN = 5;

		public static final int SUPERHUMAN_STRENGTH = 6;

		public static final int IMPROVED_REFLEXES = 7;

		public static final int RAPID_RESTORE = 8;

		public static final int RAPID_HEAL = 9;

		public static final int PROTECT_ITEM = 10;

		public static final int HAWK_EYE = 11;

		public static final int MYSTIC_LORE = 12;

		public static final int STEEL_SKIN = 13;

		public static final int ULTIMATE_STRENGTH = 14;

		public static final int INCREDIBLE_REFLEXES = 15;

		public static final int PROTECT_FROM_MAGIC = 16;

		public static final int PROTECT_FROM_RANGED = 17;

		public static final int PROTECT_FROM_MELEE = 18;

		public static final int EAGLE_EYE = 19;

		public static final int MYSTIC_MIGHT = 20;

		public static final int RETRIBUTION = 21;

		public static final int REDEMPTION = 22;

		public static final int SMITE = 23;

		public static final int CHIVALRY = 24;

		public static final int PIETY = 25;

		public static final int PRESERVE = 26;

		public static final int RIGOUR = 27;

		public static final int AUGURY = 28;

		public final static String[] SKILL_NAME = {
				"Attack",
				"Defence",
				"Strength",
				"Hitpoints",
				"Ranged",
				"Prayer",
				"Magic",
				"Cooking",
				"Woodcutting",
				"Fletching",
				"Fishing",
				"Firemaking",
				"Crafting",
				"Smithing",
				"Mining",
				"Herblore",
				"Agility",
				"Thieving",
				"Slayer",
				"Farming",
				"Runecrafting"};

		/**
		 * The name of the equipment bonuses.
		 */
		public final static String[] EQUIPMENT_BONUS = {"Stab", "Slash", "Crush", "Magic", "Ranged", "Stab", "Slash", "Crush", "Magic", "Range", "Strength", "Prayer"};

		public final static int STAB_ATTACK_BONUS = 0;

		public final static int SLASH_ATTACK_BONUS = 1;

		public final static int CRUSH_ATTACK_BONUS = 2;

		public final static int MAGIC_ATTACK_BONUS = 3;

		public final static int RANGED_ATTACK_BONUS = 4;

		public final static int STAB_DEFENCE_BONUS = 5;

		public final static int SLASH_DEFENCE_BONUS = 6;

		public final static int CRUSH_DEFENCE_BONUS = 7;

		public final static int MAGIC_DEFENCE_BONUS = 8;

		public final static int RANGED_DEFENCE_BONUS = 9;

		public final static int STRENGTH_BONUS = 10;

		public final static int PRAYER_BONUS = 11;

		public static final int BUFFER_SIZE = 10000;

	//@formatter:off
	 public static final int PACKET_SIZES[] =
	 {
		                0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
		                0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
		                0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
		                0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
		                2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
		                0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
		                8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
		                6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
		                0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
		                0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
		                0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
		                0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
		                1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
		                0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
		                0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
		                0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
		                0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
		                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
		                0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
		                0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
		                2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
		                4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
		                0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
		                1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
		                0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
		                0, 0, 6, 6, 0, 0, 0 // 250
	 };
	//@formatter:on

		public final static int[] PRAYER_LEVEL_REQUIRED = {1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52, 60, 70, 55, 74, 77};

		public final static int[] PRAYER_GLOW = {83, 84, 85, 601, 602, 86, 87, 88, 89, 90, 91, 603, 604, 92, 93, 94, 95, 96, 97, 605, 606, 98, 99, 100, 607, 608, 609, 610, 611};

		public final static String[] PRAYER_NAME = {
				"Thick Skin",
				"Burst of Strength",
				"Clarity of Thought",
				"Sharp Eye",
				"Mystic Will",
				"Rock Skin",
				"Superhuman Strength",
				"Improved Reflexes",
				"Rapid Restore",
				"Rapid Heal",
				"Protect Item",
				"Hawk Eye",
				"Mystic Lore",
				"Steel Skin",
				"Ultimate Strength",
				"Incredible Reflexes",
				"Protect from Magic",
				"Protect from Missiles",
				"Protect from Melee",
				"Eagle Eye",
				"Mystic Might",
				"Retribution",
				"Redemption",
				"Smite",
				"Chivalry",
				"Piety",
				"Preserve",
				"Rigour",
				"Augury"};

		public final static int[] PRAYER_HEAD_ICONS = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0, -1, -1, 3, 5, 4, -1, -1, -1, -1, -1};

		public final static int[] DUEL_RULE_ID = {1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 2097152, 8388608, 16777216, 67108864, 134217728};

		/**
		 * Maximum amount of players online simultaneously.
		 */
		public final static int MAXIMUM_PLAYERS = ServerConfiguration.STABILITY_TEST ? BotManager.BOTS_AMOUNT + 5 : 175;

		public final static int[] interfaceFramesIgnoreRepeat = {
				27001,
				27002,
				27000,
				22033,
				22034, // Bank tab ids, which send to client which tab is being used and items in that tab.
				18139,
				18140, // Owner & Talking in clan chat interface.
				25359,
				25360,
				25361,
				25362,
				25373};

		public static final int BANK_SIZE = 352;

		public final static int YELLOW_HEX = 0xF6FF00;

		public final static int ORANGE_HEX = 0xffb000;

		public final static int GREEN_HEX = 0x09FF00;

		public final static int RED_HEX = 0xFF0000;

		public final static String DONATOR_ICON = "<img=3>";

		/**
		 * When i had 30 experience rates last lime with 15 players average, it took them nearly 2 weeks to max out.
		 * But back then they needed to train combat and prayer.
		 */
		public final static int SKILLING_XP_MULTIPLIER = 25;

		public final static String[] SPECIAL_ATTACK_SAVE_NAMES = {
				"Bandos godsword",
				"Saradomin godsword",
				"Dragon warhammer",
				"Armadyl godsword",
				"Zamorak godsword",
				"Dragon longsword",
				"",
				"Dragon halberd",
				"",
				"Dragon mace",
				"Barrelchest anchor",
				"Dragon dagger",
				"",
				"Saradomin sword",
				"",
				"",
				"",
				"",
				"",
				"Vesta's spear",
				"Vesta's longsword",
				"Hand cannon",
				"",
				"Morrigan's throwing axe",
				"Morrigan's javelin",
				"Magic shortbow",
				"",
				"Dark bow",
				"",
				"Dragon bolts (e)",
				"Dharok's axe",
				"Vengeance",
				"Heavy ballista",
				"Abyssal dagger (p++)",
				""};
}