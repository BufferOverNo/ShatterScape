package game.content.combat.vsplayer.range;

import core.ServerConstants;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;

public class RangedData
{

		/**
		 * @param player
		 *        The associated player.
		 * @return True, if the player is wielding a short ranged weapon. Such as knifes, javelin, throwing axe.
		 */
		public static boolean isWieldingShortRangeRangedWeapon(Player player)
		{
				for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++)
				{
						if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON[value])
						{
								player.setUsingShortRangeRangedWeapon(true);
								return true;
						}
				}
				return false;
		}

		public static int getRangedAttackEmote(Player attacker)
		{
				String weaponName = ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase();
				Player victim = PlayerHandler.players[attacker.getPlayerIdAttacking()];
				attacker.lastAttackAnimationTimer = System.currentTimeMillis();
				if (weaponName.contains("knife") || weaponName.contains("dart") || weaponName.contains("javelin") || weaponName.contains("thrownaxe"))
				{
						SoundSystem.sendSound(attacker, victim, 365, 570);
						return 806;
				}
				if (weaponName.contains("karil"))
				{
						return 2075;
				}
				if (weaponName.contains("'bow") || weaponName.contains("crossbow"))
				{
						SoundSystem.sendSound(attacker, victim, 370, 700);
						return 4230;
				}
				if (weaponName.contains("bow"))
				{
						SoundSystem.sendSound(attacker, victim, 370, 300);
						return 426;
				}
				boolean soundApplied = false;
				switch (attacker.getWieldedWeapon())
				{

						case 18779: // Toxic blowpipe.
								return 13729;
						case 13879:
								// Morrigan's javelin.
								return 10501;

						case 13883:
								// Morrigan's throwing axe.
								return 10504;
						case 6522:
								// Toktz-xil-ul (tzhaar range weapon, looks like a hoolahoop)
								return 2614;
						case 4734:
								// Karil's crossbow
								return 2075;
						case 15241:
								// Hand cannon.
								attacker.gfx0(2138);
								return 12152;

						// Heavy ballista.
						case 18807:
								return 13726;
						default:
								if (!soundApplied)
								{
										SoundSystem.sendSound(attacker, victim, 417, 300);
								}
								return 451;
				}
		}

		/**
		 * @param player
		 * @param hasArrowEquipped
		 * 			True, if the player has an arrow equipped.
		 * @return
		 * 			True, if the player has the ammo required to use their Ranged weapon.
		 */
		public static boolean hasRequiredAmmo(Player player, boolean hasArrowEquipped)
		{

				if (player.getWieldedWeapon() == 18779 && player.blowpipeDartItemAmount == 0)
				{
						player.playerAssistant.sendMessage("You have run out of darts!");
						return false;
				}
				if (isWieldingShortRangeRangedWeapon(player))
				{
						player.setDroppedRangedItemUsed(player.getWieldedWeapon());
						return true;
				}

				if (!hasArrowEquipped)
				{
						player.playerAssistant.sendMessage("You have run out of ammo!");
						return false;
				}

				int weapon = player.getWieldedWeapon();
				int ammo = player.playerEquipment[ServerConstants.ARROW_SLOT];

				if (hasBowEquipped(player) && hasArrowEquipped(player))
				{
						if (ammo <= getHighestArrow(player, weapon))
						{
								if (ammo == 11212 && player.playerEquipmentN[ServerConstants.ARROW_SLOT] <= 1)
								{
										player.getPA().sendMessage("You need 2 dragon arrows.");
										return false;
								}
								player.setDroppedRangedItemUsed(ammo);
								return true;
						}
				}

				if (hasCrossBowEquipped(player) && hasBoltEquipped(player))
				{
						if (ammo <= getHighestBolt(player, weapon))
						{
								player.setDroppedRangedItemUsed(ammo);
								return true;
						}
				}

				for (int value = 0; value < SPECIAL_RANGED_WEAPON.length; value++)
				{
						if (weapon == SPECIAL_RANGED_WEAPON[value][0] && ammo == SPECIAL_RANGED_WEAPON[value][1])
						{
								// Hand cannon.
								if (weapon != 15241)
								{
										player.setDroppedRangedItemUsed(ammo);
								}
								return true;
						}
				}

				player.playerAssistant.sendMessage("You cannot use a " + ItemAssistant.getItemName(weapon) + " with a " + ItemAssistant.getItemName(ammo) + ".");
				return false;
		}

		/**
		 * Get the highest bolt use-able depending on the bow used.
		 * 
		 * @param player
		 *        The associated player.
		 * @param weapon
		 *        The weapon used by the player.
		 * @return The highest bolt use-able.
		 */
		public static int getHighestBolt(Player player, int weapon)
		{
				// Armadyl crossbow, Onyx bolts (e).
				for (int value = 0; value < CROSS_BOW.length; value++)
				{
						if (weapon == CROSS_BOW[value][0])
						{
								return CROSS_BOW[value][1];
						}
				}
				return 0;
		}

		/**
		 * Get the highest arrow use-able depending on the bow used.
		 * 
		 * @param player
		 *        The associated player.
		 * @param weapon
		 *        The weapon used by the player.
		 * @return The highest arrow use-able.
		 */
		private static int getHighestArrow(Player player, int weapon)
		{
				for (int value = 0; value < BOW.length; value++)
				{
						if (weapon == BOW[value][0])
						{
								return BOW[value][1];
						}
				}
				return 0;
		}

		/**
		 * Check if the player has arrow/s equipped.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player has arrow/s equipped.
		 */
		public static boolean hasArrowEquipped(Player player)
		{
				int amount = 1;
				for (int value : ARROW)
				{
						if (player.playerEquipment[ServerConstants.ARROW_SLOT] == value && player.playerEquipmentN[ServerConstants.ARROW_SLOT] >= amount)
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player has an Ava bag or Completionist cape equipped.
		 */
		public static boolean hasAvaRelatedItem(Player player)
		{
				if (hasAvaEquipped(player) || player.playerEquipment[ServerConstants.CAPE_SLOT] == 14011 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18679)
				{
						return true;
				}
				if (Skilling.hasMasterCapeWorn(player, 9756))
				{
						return true;
				}
				return false;
		}

		public static boolean hasAvaEquipped(Player player)
		{
				if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 10498 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 10499)
				{
						return true;
				}
				return false;
		}

		/**
		 * Check if the player has a bow equipped.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player has a bow equipped.
		 */
		public static boolean hasBowEquipped(Player player)
		{
				for (int value = 0; value < BOW.length; value++)
				{
						if (player.getWieldedWeapon() == BOW[value][0])
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * True if the player has a ranged weapon equipped or a ranged weapon  in inventory.
		 * @param player
		 * @return
		 */
		public static boolean hasRangedWeapon(Player player)
		{
				if (player.getWieldedWeapon() > 0)
				{
						if (ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase().contains("bow"))
						{
								return true;
						}
				}
				for (int index = 0; index < player.playerItems.length; index++)
				{
						int itemId = player.playerItems[index] - 1;
						if (itemId <= 0)
						{
								continue;
						}
						if (ItemAssistant.getItemName(itemId).toLowerCase().contains("bow"))
						{
								return true;
						}
				}


				for (int value = 0; value < SPECIAL_RANGED_WEAPON.length; value++)
				{
						if (player.getWieldedWeapon() == SPECIAL_RANGED_WEAPON[value][0])
						{
								return true;
						}
						for (int index = 0; index < player.playerItems.length; index++)
						{
								if (player.playerItems[index] - 1 == SPECIAL_RANGED_WEAPON[value][0])
								{
										return true;
								}
						}
				}
				for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++)
				{
						if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON[value])
						{
								return true;
						}
						for (int index = 0; index < player.playerItems.length; index++)
						{
								if (player.playerItems[index] - 1 == RangedData.NO_AMMO_RANGED_WEAPON[value])
								{
										return true;
								}
						}
				}
				return false;
		}

		public static boolean isRangedItem(Player player, int itemId)
		{
				if (ItemAssistant.getItemName(itemId).toLowerCase().contains("bow"))
				{
						return true;
				}
				for (int value = 0; value < SPECIAL_RANGED_WEAPON.length; value++)
				{
						if (itemId == SPECIAL_RANGED_WEAPON[value][0])
						{
								return true;
						}
				}
				for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++)
				{
						if (itemId == RangedData.NO_AMMO_RANGED_WEAPON[value])
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Check if the player has bolt/s equipped.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player has bolt/s equipped.
		 */
		public static boolean hasBoltEquipped(Player player)
		{
				int ammo = player.playerEquipment[ServerConstants.ARROW_SLOT];
				if (ammo >= 9140 && ammo <= 9245 || ammo >= 9337 && ammo <= 9342)
				{
						return true;
				}
				return false;
		}

		/**
		 * Check if the player has a crossbow equipped.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player has a crossbow equipped.
		 */
		public static boolean hasCrossBowEquipped(Player player)
		{
				for (int value = 0; value < CROSS_BOW.length; value++)
				{
						if (player.getWieldedWeapon() == CROSS_BOW[value][0])
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Check if the player has a crossbow equipped.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player has a crossbow equipped.
		 */
		public static boolean hasSpecialRangedWeaponEquipped(Player player)
		{
				for (int value = 0; value < SPECIAL_RANGED_WEAPON.length; value++)
				{
						if (player.getWieldedWeapon() == SPECIAL_RANGED_WEAPON[value][0])
						{
								return true;
						}
				}
				return false;
		}


		/**
		 * @param player
		 *        The associated player.
		 * @return True, if the player is wielding a medium ranged weapon.
		 */
		public static boolean isWieldingMediumRangeRangedWeapon(Player player)
		{
				if (hasBowEquipped(player) || hasCrossBowEquipped(player))
				{
						player.setUsingMediumRangeRangedWeapon(true);
						return true;
				}

				if (hasSpecialRangedWeaponEquipped(player))
				{
						player.setUsingMediumRangeRangedWeapon(true);
						return true;
				}

				return false;
		}

		/**
		 * @return True, if the player is wearing full void ranged.
		 */
		public static boolean wearingFullVoidRanged(Player player)
		{
				return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11664 && player.playerEquipment[ServerConstants.LEG_SLOT] == 8840 && player.playerEquipment[ServerConstants.BODY_SLOT] == 8839 && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
		}

		/**
		 * List of arrows.
		 */
		public final static int[] ARROW = {
				882, // Bronze arrow.
				884, // Iron arrow.
				886, // Steel arrow.
				888, // Mithril arrow.
				890, // Adamant arrow.
				892, // Rune arrow.
				11212, // Dragon arrow.
		};

		/**
		 * List of bows and their corresponding maximum arrow used with it.
		 */
		public final static int[][] BOW = {{841, 884
						// Shortbow, Iron arrow.
						}, {843, 886
						// Oak shortbow, Steel arrow.
						}, {849, 888
						// Willow shortbow, Mithril arrow.
						}, {853, 890
						// Maple shortbow, Adamant arrow.
						}, {857, 890
						// Yew shortbow, Adamant arrow.
						}, {861, 892
						// Magic shortbow, Rune arrow.
						}, {859, 892
						// Magic longbow, Rune arrow.
						}, {18659, 892
						// Magic shortbow (i), Rune arrow.
						}, {18830, 11212
						// Twisted bow, Dragon arrow.
						}, {11235, 11212
						// Dark bow, Dragon arrow.
						}, {15701, 11212
						// Dark bow, Dragon arrow.
						}, {15702, 11212
						// Dark bow, Dragon arrow.
						}, {15703, 11212
						// Dark bow, Dragon arrow.
						}, {15704, 11212
						// Dark bow, Dragon arrow.
						},};

		/**
		 * List of crossbows and their corresponding maximum bolt used with it.
		 */
		public final static int[][] CROSS_BOW = {{9174, 9140
						// Bronze crossbow, Iron bolts.
						}, {9177, 9140
						// Iron crossbow, Iron bolts.
						}, {9179, 9141
						// Steel crossbow, Steel bolts.
						}, {9181, 9142
						// Mith crossbow, Mithril bolts.
						}, {9183, 9143
						// Adamant crossbow, Adamant bolts.
						}, {9185, 9342
						// Rune crossbow, Onyx bolts (e).
						}, {18642, 9342
						// Armadyl crossbow, Onyx bolts (e).
						}, {18836, 9342
						// Armadyl crossbow, Onyx bolts (e).
						}};

		/**
		 * List of short ranged weapons.
		 */
		public final static int[] NO_AMMO_RANGED_WEAPON = {
				863, // Iron knife.
				864, // Bronze knife.
				865, // Steel_knife.
				869, // Black_knife.
				866, // Mithril_knife.
				867, // Adamant_knife.
				868, // Rune_knife.
				806, // Bronze dart.
				807, // Iron dart.
				808, // Steel dart.
				809, // Mithril dart.
				810, // Adamant dart.
				811, // Rune dart.
				11230, // Dragon dart.
				18779, // Toxic blowpipe.
				6522, // Toktz-xil-ul.
				13879, // Morrigan's javelin.
				13883 // Morrigan's throwing axe.
		};

		public final static int[][] SPECIAL_RANGED_WEAPON = {
				{4734, 4740}, // Karil's crossbow.
				{15241, 15243}, // Hand cannon.
				{18807, 18819}, // Heavy ballista.
				{10156, 10159},// Hunters' crossbow.

		};
}