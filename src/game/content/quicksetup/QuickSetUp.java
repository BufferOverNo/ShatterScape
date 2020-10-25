package game.content.quicksetup;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.BankButtons;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.SpellBook;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Quick set-up interface actions.
 * @author MGT Madness, created on 16-03-2015.
 */
public class QuickSetUp
{
		/**
		 * Display the quick set up interface.
		 */
		public static void displayInterface(Player player)
		{

				for (int index = 1; index < 10; index++)
				{
						player.presetIndex = index;
						if (!Presets.presetNameExists(player))
						{
								continue;
						}
						int frameId = 22867 + ((player.presetIndex - 1) * 4);
						player.getPA().sendFrame126(Presets.getCurrentPresetName(player), frameId);
				}
				player.presetIndex = 0;
				player.getPA().displayInterface(24280);

		}

		/**
		 * True, if the button clicked is a quick set-up button.
		 * @param player
		 * 			The associated player.
		 * @param buttonId
		 * 			The button identity clicked.
		 */
		public static boolean isQuickSetUpButton(Player player, int buttonId)
		{
				if (buttonId >= 94226 && buttonId <= 95038 || buttonId >= 89080 && buttonId <= 89112)
				{
						if (player.getHeight() == 20)
						{
								return true;
						}
						if (player.getDuelStatus() >= 1)
						{
								return true;
						}
						if (System.currentTimeMillis() - player.timeSkilled < 90000)
						{
								int secondsLeft = (int) (90 - ((System.currentTimeMillis() - player.timeSkilled) / 1000));
								player.getPA().sendMessage("You cannot use presets while you just skilled, wait " + secondsLeft + " seconds.");
								return true;
						}
						if (!player.isAdministratorRank())
						{
								if (!Bank.hasBankingRequirements(player, true))
								{
										return true;
								}
								if (System.currentTimeMillis() - player.lastQuickSetUpClicked <= 600 && (buttonId >= 94226 && buttonId <= 94254 || buttonId == 95002))
								{
										return true;
								}
								InterfaceAssistant.closeDialogueOnly(player);
						}
						else
						{
								InterfaceAssistant.closeDialogueOnly(player);
						}
				}
				player.lastQuickSetUpClicked = System.currentTimeMillis();

				if (buttonId >= 89080 && buttonId <= 89112)
				{
						int index = 0;
						index = buttonId - 89080;
						index /= 4;
						Presets.presetKit(player, index + 1);
						return true;
				}

				switch (buttonId)
				{

						case 95006:
								vengeanceRunes(player);
								return true;

						case 95010:
								barrageRunes(player);
								return true;

						case 95014:
								teleBlockRunes(player);
								return true;

						case 94226:
								mainMelee(player);
								return true;
						case 94230:
								mainHybrid(player);
								return true;

						case 94234:
								berserkerMelee(player);
								return true;

						case 94238:
								berserkerHybrid(player);
								return true;

						case 94242:
								pure(player);
								return true;

						case 94246:
								pureTribrid(player);
								return true;

						case 94250:
								rangedTank(player);
								return true;

						case 94254:
								f2pMelee(player);
								return true;

						case 95002:
								f2pRanged(player);
								return true;
				}
				return false;
		}

		//@formatter:off
		private static int[] randomTeamCape = {4315, 4317, 4319, 4321, 4323, 4325, 4327, 4329, 4331, 4333, 4335, 4337, 4339, 4341, 4343, 4345, 4347, 4349, 4351, 4353, 4355, 4357, 4359, 4361, 4363, 4365, 4367, 4369, 4371, 4373, 4375, 4377, 4379, 4381, 4383, 4385, 4387, 4389, 4391, 4393, 4395, 4397, 4399, 4401, 4403, 4405, 4407, 4409, 4411, 4413};

		private static int[] randomGodCape = {2412, 2413, 2414};

		private static int[] randomMysticTop = {4091, 4101, 4111};

		private static int[] randomMysticBottom = {4093, 4103, 4113};
		//@formatter:on

		public static int getRandomMysticBottom()
		{
				return randomMysticBottom[Misc.random(randomMysticBottom.length - 1)];
		}

		public static int getRandomMysticTop()
		{
				return randomMysticTop[Misc.random(randomMysticTop.length - 1)];
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			Get a random team cape.
		 */
		public static int getTeamCape(boolean fireCape)
		{
				int random = 5;
				if (fireCape)
				{
						random = 6;
				}
				random = Misc.random(1, random);
				if (random <= 5)
				{
						return randomTeamCape[Misc.random(randomTeamCape.length - 1)];
				}
				else
				{
						// Fire cape.
						return 6570;
				}
		}

		public static int getMainMeleeHelmetBot(boolean berserker)
		{
				if (Misc.hasPercentageChance(80))
				{
						return berserker ? 3751 : 10828; // Helm of neitiznot.
				}
				else
				{
						return 10548; // Fighter hat.
				}
		}

		public static int getMeleeAmuletBot()
		{
				if (Misc.hasPercentageChance(30))
				{
						return 1725; // Amulet of strength.
				}
				else
				{
						return 1712; // Amulet of glory(4).
				}
		}

		public static int getMeleePlatebodyBot()
		{
				int random = Misc.random(1, 2);
				switch (random)
				{
						case 1:
								return 1127; // Rune platebody.
						case 2:
								return 10551; // Fighter torso.
				}
				return -1;
		}

		/**
		    * Bank inventory and equipment.
		    * @param player
		    * 			The associated player.
		    */
		public static void bankInventoryAndEquipment(Player player)
		{
				BankButtons.depositInventoryItems(player, false);
				BankButtons.depositWornItems(player, false, false);
		}



		/**
		 * 
		 * @param player
		 * 			The associated player.
		 * @param array
		 * 			The array of items to spawn into the inventory.
		 */
		public static void spawnInventory(Player player, int[][] array)
		{
				for (int i = 0; i < array.length; i++)
				{
						if (array[i][0] <= 1)
						{
								continue;
						}
						//player.botDebug.add("Added: " + array[i][0]);
						ItemAssistant.addItemNoMessage(player, array[i][0], array[i][1]);
				}
				player.botDebug.add("Sharks spawned: " + ItemAssistant.getFreeInventorySlots(player));
				ItemAssistant.addItemNoMessage(player, 385, ItemAssistant.getFreeInventorySlots(player));
		}

		/**
		 * Wear the given equipment.
		 * @param player
		 * 			The associated player.
		 * @param array
		 * 			The equipment array to equip.
		 */
		public static void spawnEquipment(Player player, int[][] array, boolean spawnInInventory)
		{
				for (int i = 0; i < array.length; i++)
				{
						if (spawnInInventory)
						{
								if (array[i][0] > 1)
								{
										ItemAssistant.addItem(player, array[i][0], array[i][1]);
								}
						}
						else
						{
								ItemAssistant.replaceEquipmentSlot(player, i, array[i][0], array[i][1], false, false);
						}
				}
		}

		/**
		 * Change spellbook and prayer book.
		 * @param player
		 * 			The associated player.
		 * @param prayer
		 * 			The prayer book to change into.
		 * @param magic
		 * 			The spellbook to change into.
		 */
		public static void setPrayerAndMagicBook(Player player, String magic)
		{
				if (magic.equals("ANCIENT"))
				{
						SpellBook.ancientMagicksSpellBook(player);
				}
				else if (magic.equals("LUNAR"))
				{
						SpellBook.lunarSpellBook(player);
				}
				else if (magic.equals("MODERN"))
				{
						SpellBook.modernSpellBook(player);
				}
		}

		/**
		 * Change combat skills.
		 * @param player
		 * 			The associated player.
		 * @param accountType
				 * 			The account type to change into.
		 */
		public static void setCombatSkills(Player player, String accountType, boolean custom, int[] customSkills)
		{
				int[] skills = new int[7];
				switch (accountType)
				{
						case "MAIN":
								for (int i = 0; i < 7; i++)
								{
										int[] skill = {99, 99, 99, 99, 99, 99, 99};
										skills = skill;
								}
								break;
						case "BERSERKER":
								for (int i = 0; i < 7; i++)
								{
										int[] skill = {75, 45, 99, 99, 99, 52, 99};
										skills = skill;
								}
								break;
						case "RANGED TANK":
								for (int i = 0; i < 7; i++)
								{
										int[] skill = {50, 85, 50, 99, 99, 52, 99};
										skills = skill;
								}
								break;
						case "PURE":
						case "INITIATE":
								for (int i = 0; i < 7; i++)
								{
										int[] skill = {75, accountType.equals("INITIATE") ? 20 : 1, 99, 99, 99, 52, 99};
										skills = skill;
								}
								break;
				}
				if (customSkills != null)
				{
						skills = customSkills;
				}
				for (int i = 0; i < 7; i++)
				{
						if (i != 3)
						{
								player.skillExperience[i] = Skilling.getExperienceForLevel(skills[i]);
								player.currentCombatSkillLevel[i] = skills[i];
								player.baseSkillLevel[i] = skills[i];
						}
				}
				Combat.resetPrayers(player);
				EditCombatSkill.calculateHitPoints(player);
				for (int i = 0; i < 7; i++) // This for loop has to be after calculateHitPoints, because giving extra
				//experience in attack skill, will give additional hitpoint experience.
				{
						if (i != 3)
						{
								if (player.baseSkillLevel[i] == 99)
								{
										player.skillExperience[i] = (player.skillExperience[i] + player.combatExperienceGainedAfterMaxed[i]);
								}
						}
				}

				for (int i = 0; i < 7; i++)
				{
						player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
				}
				Combat.refreshCombatSkills(player);
		}

		/**
		   * Wear the given equipment.
		   * @param player
		   * 			The associated player.
		   * @param array
		   * 			The equipment array to equip.
		   */
		private static void spawnEquipment(Player player, int[][] array)
		{
				for (int i = 0; i < array.length; i++)
				{
						ItemAssistant.replaceEquipmentSlot(player, i, array[i][0], array[i][1], false, false);
				}
		}


		/**
		 * Update equipment visuals to the player's client and other information relateing to an equipment change.
		 * @param player
		 * 			The associated player.
		 */
		public static void updateEquipment(Player player)
		{
				Combat.updatePlayerStance(player);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				player.setInventoryUpdate(true);
		}

		/**
		  * Restore hitpoints and prayer points.
		  * @param player
		  * 			The associated player.
		  */
		public static void heal(Player player)
		{
				player.setHitPoints(player.getBaseHitPointsLevel());
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
				player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
				player.setSpecialAttackAmount(10.0, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				player.setVengeance(false);
				player.runEnergy = 100.0;
				Poison.removePoison(player);
				AgilityAssistant.updateRunEnergyInterface(player);
				Combat.resetPrayers(player);
				player.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
				for (int index = 0; index < 7; index++)
				{
						player.currentCombatSkillLevel[index] = player.baseSkillLevel[index];
				}
				Combat.refreshCombatSkills(player);
		}

		public static int getRandomGodCape()
		{
				return randomGodCape[Misc.random(randomGodCape.length - 1)];
		}

		//@formatter:off
		private static int[][] inventoryTankTest = {
			{18799, 1},
			{11694, 1},
			{18759, 1},
			{4749, 1},
			{3040, 1},
			{11732, 1},
			{18767, 1},
			{4751, 1},
			{3024, 1},
			{18637, 1},
			{565, 800},
			{4712, 1},
			{3024, 1},
			{3024, 1},
			{3024, 1},
			{560, 1600},
			{6685, 1},
			{6685, 1},
			{3016, 1},
			{555, 2400},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
		};

		private static int[][] equipmentTankTest = {
			{10828, 1},
			{2414, 1},
			{6585, 1},
			{18783, 1},
			{4736, 1},
			{6889, 1},
			{-1, 1},
			{4714, 1},
			{-1, 1},
			{7462, 1},
			{6920, 1},
			{-1, 1},
			{-1, 1},
			{-1, 1},
		};
		//@formatter:on

		public static void tankTestBot(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, inventoryTankTest);
				spawnEquipment(player, equipmentTankTest);
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "MAIN", false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		   * Apply the main hybrid button action.
		   * @param player
		   * 			The associated player.
		   */
		public static void mainHybrid(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, HybridMain.inventorySet(player));
				spawnEquipment(player, HybridMain.equipmentSet(player));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "MAIN", false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}



		/**
		 * Apply the main hybrid button action.
		 * @param player
		 * 			The associated player.
		 */
		public static void mainHybridTournament(Player player, String type, String stats)
		{
				//{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows"};
				if (type.contains("hybrid") || type.contains("Nh"))
				{
						spawnInventory(player, TournamentSets.tournamentInventory(player, type));
						spawnEquipment(player, TournamentSets.tournamentEquipment(player, type));
				}
				else
				{
						spawnInventory(player, TournamentSets.pureTribridInventory);
						spawnEquipment(player, PureTribrid.getEquipment());
				}
				player.runePouchItemId[0] = 555;
				player.runePouchItemAmount[0] = 3000;
				player.runePouchItemId[1] = 560;
				player.runePouchItemAmount[1] = 2000;
				player.runePouchItemId[2] = 565;
				player.runePouchItemAmount[2] = 1000;
				RunePouch.updateRunePouchMainStorage(player, false);
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, stats, false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		 * Apply the main hybrid button action.
		 * @param player
		 * 			The associated player.
		 */
		public static void mainDharokTournament(Player player, String type, String stats)
		{
				spawnInventory(player, TournamentSets.tournamentInventoryDharok(player, type));
				spawnEquipment(player, TournamentSets.tournamentEquipmentDharok(player, type));
				player.runePouchItemId[0] = 557;
				player.runePouchItemAmount[0] = 1000;
				player.runePouchItemId[1] = 9075;
				player.runePouchItemAmount[1] = 400;
				player.runePouchItemId[2] = 560;
				player.runePouchItemAmount[2] = 200;
				RunePouch.updateRunePouchMainStorage(player, false);
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, stats, false, null);
				setPrayerAndMagicBook(player, "LUNAR");
		}

		public static void maxBridTournament(Player player, String type, String stats)
		{
				spawnInventory(player, TournamentSets.tournamentInventoryMaxBrid(player, type));
				spawnEquipment(player, TournamentSets.tournamentEquipmentMaxBrid(player, type));
				player.runePouchItemId[0] = 555;
				player.runePouchItemAmount[0] = 3000;
				player.runePouchItemId[1] = 560;
				player.runePouchItemAmount[1] = 2000;
				player.runePouchItemId[2] = 565;
				player.runePouchItemAmount[2] = 1000;
				RunePouch.updateRunePouchMainStorage(player, false);
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, stats, false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		 * Apply the main melee button action.
		 * @param player
		 * 			The associated player.
		 */
		public static void mainMelee(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, MeleeMain.inventory);
				spawnEquipment(player, MeleeMain.equipmentSet(player));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "MAIN", false, null);
				setPrayerAndMagicBook(player, "LUNAR");
		}

		/**
		 * Apply the main hybrid button action.
		 * @param player
		 * 			The associated player.
		 */
		private static void berserkerHybrid(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, BerserkerHybrid.inventorySet(player));
				spawnEquipment(player, BerserkerHybrid.getEquipment());
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "BERSERKER", false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		 * Apply the main hybrid button action.
		 * @param player
		 * 			The associated player.
		 */
		private static void berserkerMelee(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, BerserkerMelee.inventory);
				spawnEquipment(player, BerserkerMelee.equipmentSet(player));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "BERSERKER", false, null);
				setPrayerAndMagicBook(player, "LUNAR");
		}

		/**
		 * Apply the pure button action.
		 * @param player
		 * 			The associated player.
		 */
		private static void pure(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, Pure.inventory);
				Pure.random(player);
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "PURE", false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		 * Apply the pure tribrid button action.
		 * @param player
		 * 			The associated player.
		 */
		private static void pureTribrid(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, PureTribrid.inventory);
				spawnEquipment(player, PureTribrid.getEquipment());
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "PURE", false, null);
				setPrayerAndMagicBook(player, "ANCIENT");
		}

		/**
		 * Apply the ranged button action.
		 * @param player
		 * 			The associated player.
		 */
		private static void rangedTank(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, RangedTank.inventory);
				spawnEquipment(player, RangedTank.equipmentSet(player));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "RANGED TANK", false, null);
				setPrayerAndMagicBook(player, "LUNAR");
		}

		private static void f2pMelee(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, F2pSet.getInventory(player, false));
				spawnEquipment(player, F2pSet.getEquipment(false));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "MAIN", false, null);
				setPrayerAndMagicBook(player, "MODERN");
		}

		private static void f2pRanged(Player player)
		{
				bankInventoryAndEquipment(player);
				spawnInventory(player, F2pSet.getInventory(player, true));
				spawnEquipment(player, F2pSet.getEquipment(true));
				updateEquipment(player);
				heal(player);
				setCombatSkills(player, "MAIN", false, null);
				setPrayerAndMagicBook(player, "MODERN");
		}

		/**
		 * True if the player is using f2p only gear.
		 * All equipment must be f2p.
		 * Must not have any stat above the base level, except for strength which can be up to 112.
			And must be on modern spellbook.
		 */
		public static boolean isUsingF2pOnly(Player player, boolean message, boolean prayerCheck)
		{
				if (!message && prayerCheck)
				{
						if (!ItemAssistant.hasEquipment(player))
						{
								return false;
						}
				}
				if (!player.spellBook.equals("MODERN"))
				{
						if (message)
						{
								player.getPA().sendMessage("You must be using f2p only to attack this player.");
								player.getPA().sendMessage("Please switch to the modern spellbook.");
						}
						return false;
				}
				for (int index = 0; index < 7; index++)
				{
						if (index == ServerConstants.STRENGTH)
						{
								if (player.currentCombatSkillLevel[ServerConstants.STRENGTH] > 112)
								{
										if (message)
										{
												player.getPA().sendMessage("You must be using f2p only to attack this player.");
												player.getPA().sendMessage("Please lower your strength level to 112.");
										}
										return false;
								}
								continue;
						}
						if (player.currentCombatSkillLevel[index] > player.baseSkillLevel[index])
						{
								if (message)
								{
										player.getPA().sendMessage("You must be using f2p only to attack this player.");
										player.getPA().sendMessage("Please lower your " + ServerConstants.SKILL_NAME[index] + " to 99.");
								}
								return false;
						}
				}

				int item = 0;
				for (int index = 0; index < player.playerEquipment.length; index++)
				{
						item = player.playerEquipment[index];
						if (item <= 0)
						{
								continue;
						}
						if (!ItemDefinition.getDefinitions()[item].f2p)
						{
								if (message)
								{
										player.getPA().sendMessage("You must be using f2p only to attack this player.");
										player.getPA().sendMessage("Please bank your " + ItemAssistant.getItemName(item) + ".");
								}
								return false;
						}
				}

				for (int index = 0; index < player.playerItems.length; index++)
				{

						item = player.playerItems[index] - 1;
						if (item <= 0)
						{
								continue;
						}
						if (!ItemDefinition.getDefinitions()[item].f2p)
						{
								if (message)
								{
										player.getPA().sendMessage("You must be using f2p only to attack this player.");
										player.getPA().sendMessage("Please bank your " + ItemAssistant.getItemName(item) + ".");
								}
								return false;
						}
				}

				if (prayerCheck)
				{

						for (int index = 0; index < 8; index++)
						{
								if (player.prayerActive[21 + index])
								{
										if (message)
										{
												player.getPA().sendMessage("You must be using f2p only to attack this player.");
												player.getPA().sendMessage("Please turn off all non-f2p prayers.");
										}
										return false;
								}
						}
				}
				return true;
		}

		/**
		 * Spawn vengeance runes.
		 * @param player
		 * 			The associated player.
		 */
		private static void vengeanceRunes(Player player)
		{
				int[] runes = {563, 562, 555, 565};
				for (int i = 0; i < runes.length; i++)
				{
						if (ItemAssistant.hasItemInInventory(player, runes[i]))
						{
								Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
								ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
						}
				}
				if (ItemAssistant.addItem(player, 560, 40) && ItemAssistant.addItem(player, 9075, 80) && ItemAssistant.addItem(player, 557, 200))
				{
						player.playerAssistant.sendMessage("Vengeance runes have been added to your inventory.");
				}

				SpellBook.lunarSpellBook(player);
		}

		/**
		 * Spawn barrage runes.
		 * @param player
		 * 			The associated player.
		 */
		private static void barrageRunes(Player player)
		{
				int[] runes = {563, 562, 9075, 557};
				for (int i = 0; i < runes.length; i++)
				{
						if (ItemAssistant.hasItemInInventory(player, runes[i]))
						{
								Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
								ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
						}
				}
				if (ItemAssistant.addItem(player, 565, 200) && ItemAssistant.addItem(player, 560, 400) && ItemAssistant.addItem(player, 555, 600))
				{
						player.playerAssistant.sendMessage("Barrage runes have been added to your inventory.");
				}
				SpellBook.ancientMagicksSpellBook(player);

		}

		/**
		 * Spawn tele block runes.
		 * @param player
		 * 			The associated player.
		 */
		private static void teleBlockRunes(Player player)
		{
				int[] runes = {9075, 557, 555, 565};
				for (int i = 0; i < runes.length; i++)
				{
						if (ItemAssistant.hasItemInInventory(player, runes[i]))
						{
								Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
								ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
						}
				}
				if (ItemAssistant.addItem(player, 563, 30) && ItemAssistant.addItem(player, 562, 30) && ItemAssistant.addItem(player, 560, 30))
				{
						player.playerAssistant.sendMessage("Tele block runes have been added to your inventory.");
				}
				SpellBook.modernSpellBook(player);

		}

}