package game.content.combat.vsplayer.magic;

import core.ServerConstants;
import game.content.combat.CombatConstants;
import game.content.miscellaneous.RunePouch;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

public class MagicData
{

		/**
		 * If the spell used is a combat spell, append the spell to the player spell variable and set the player to usingMagic.
		 * @param attacker
		 * 			The player using the spell.
		 * @param castingSpellId
		 * 			The spell casted by the player.
		 */
		public static void setCombatSpell(Player attacker, int castingSpellId)
		{
				for (int i = 0; i < CombatConstants.MAGIC_SPELLS.length; i++)
				{
						if (castingSpellId == CombatConstants.MAGIC_SPELLS[i][0])
						{
								attacker.setSpellId(i);
								attacker.setLastCastedMagic(true);
								break;
						}
				}
		}

		/**
		 * Get the must-use staff for a specific spell.
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			The staff needed.
		 */
		public static int getStaffNeeded(Player player, int spellUsed)
		{
				switch (CombatConstants.MAGIC_SPELLS[spellUsed][0])
				{
						case 1539:
								return 1409;
						case 12037:
								return 4170;
						case 1190:
								return 2415;
						case 1191:
								return 2416;
						case 1192:
								return 2417;
						default:
								return 0;
				}
		}

		/**
		 * Check if player is using a staff.
		 * <p>
		 * Example, if player is wearing fire staff, no need to delete fire rune.
		 * @param player
		 * 			The associated player.
		 * @param runeId
		 * 			The rune item ID required.
		 * @return
		 */
		public static boolean wearingStaff(Player player, int runeId)
		{
				int wieldedWeapon = player.getWieldedWeapon();
				switch (runeId)
				{
						case 554:
								if (wieldedWeapon == 1387)
								{
										return true;
								}
								break;
						case 555:
								if (wieldedWeapon == 1383)
								{
										return true;
								}
								break;
						case 556:
								if (wieldedWeapon == 1381)
								{
										return true;
								}
								break;
						case 557:
								if (wieldedWeapon == 1385)
								{
										return true;
								}
								break;
				}
				return false;
		}

		/**
		 * Delete the runes according to the spell used.
		 * @param player
		 * 			The associated player.
		 * @param usedSpell
		 * 			The spell used.
		 */
		public static boolean requiredRunes(Player player, int usedSpell, String action)
		{
				// Trident of the swamp.
				if (usedSpell == 52)
				{
						return true;
				}
				if (action.equals("DELETE RUNES"))
				{
						// Toxic staff of the dead.
						if ((player.getWieldedWeapon() == 18783 || player.getWieldedWeapon() == 18781) && Misc.hasPercentageChance(13))
						{
								return true;
						}
				}
				boolean hasRunePouch = ItemAssistant.hasItemInInventory(player, 18820);

				// Total types of runes needed.
				int requiredRunesTypesTotal = 0;
				if (CombatConstants.MAGIC_SPELLS[usedSpell][8] > 0)
				{
						requiredRunesTypesTotal++;
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][10] > 0)
				{
						requiredRunesTypesTotal++;
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][12] > 0)
				{
						requiredRunesTypesTotal++;
				}

				// Amount of rune types confirmed in possession.
				int currentRunesTypesAmount = 0;
				if (CombatConstants.MAGIC_SPELLS[usedSpell][8] > 0)
				{
						if (!MagicData.wearingStaff(player, CombatConstants.MAGIC_SPELLS[usedSpell][8]))
						{
								if (action.equals("DELETE RUNES"))
								{
										if (hasRunePouch && RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][8], CombatConstants.MAGIC_SPELLS[usedSpell][9]))
										{
												RunePouch.specificRuneInsideRunePouch(player, "DELETE", CombatConstants.MAGIC_SPELLS[usedSpell][8], CombatConstants.MAGIC_SPELLS[usedSpell][9]);
										}
										else
										{
												ItemAssistant.deleteItemFromInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][8], ItemAssistant.getItemSlot(player, CombatConstants.MAGIC_SPELLS[usedSpell][8]), CombatConstants.MAGIC_SPELLS[usedSpell][9]);
										}
								}
								else if (action.equals("CHECK REQUIREMENT"))
								{
										if (ItemAssistant.hasItemAmountInInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][8], CombatConstants.MAGIC_SPELLS[usedSpell][9]))
										{
												currentRunesTypesAmount++;
										}
										if (hasRunePouch)
										{
												if (RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][8], CombatConstants.MAGIC_SPELLS[usedSpell][9]))
												{
														currentRunesTypesAmount++;
												}
										}
								}
						}
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][10] > 0)
				{
						if (!MagicData.wearingStaff(player, CombatConstants.MAGIC_SPELLS[usedSpell][10]))
						{
								if (action.equals("DELETE RUNES"))
								{
										if (hasRunePouch && RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][10], CombatConstants.MAGIC_SPELLS[usedSpell][11]))
										{
												RunePouch.specificRuneInsideRunePouch(player, "DELETE", CombatConstants.MAGIC_SPELLS[usedSpell][10], CombatConstants.MAGIC_SPELLS[usedSpell][11]);
										}
										else
										{
												ItemAssistant.deleteItemFromInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][10], ItemAssistant.getItemSlot(player, CombatConstants.MAGIC_SPELLS[usedSpell][10]), CombatConstants.MAGIC_SPELLS[usedSpell][11]);
										}
								}
								else if (action.equals("CHECK REQUIREMENT"))
								{
										if (ItemAssistant.hasItemAmountInInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][10], CombatConstants.MAGIC_SPELLS[usedSpell][11]))
										{
												currentRunesTypesAmount++;
										}
										if (hasRunePouch)
										{
												if (RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][10], CombatConstants.MAGIC_SPELLS[usedSpell][11]))
												{
														currentRunesTypesAmount++;
												}
										}
								}
						}
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][12] > 0)
				{
						if (!MagicData.wearingStaff(player, CombatConstants.MAGIC_SPELLS[usedSpell][12]))
						{
								if (action.equals("DELETE RUNES"))
								{
										if (hasRunePouch && RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][12], CombatConstants.MAGIC_SPELLS[usedSpell][13]))
										{
												RunePouch.specificRuneInsideRunePouch(player, "DELETE", CombatConstants.MAGIC_SPELLS[usedSpell][12], CombatConstants.MAGIC_SPELLS[usedSpell][13]);
										}
										else
										{
												ItemAssistant.deleteItemFromInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][12], ItemAssistant.getItemSlot(player, CombatConstants.MAGIC_SPELLS[usedSpell][12]), CombatConstants.MAGIC_SPELLS[usedSpell][13]);
										}
								}
								else if (action.equals("CHECK REQUIREMENT"))
								{
										if (ItemAssistant.hasItemAmountInInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][12], CombatConstants.MAGIC_SPELLS[usedSpell][13]))
										{
												currentRunesTypesAmount++;
										}
										if (hasRunePouch)
										{
												if (RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][12], CombatConstants.MAGIC_SPELLS[usedSpell][13]))
												{
														currentRunesTypesAmount++;
												}
										}
								}
						}
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][14] > 0)
				{
						if (!MagicData.wearingStaff(player, CombatConstants.MAGIC_SPELLS[usedSpell][14]))
						{
								if (action.equals("DELETE RUNES"))
								{
										if (hasRunePouch && RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][14], CombatConstants.MAGIC_SPELLS[usedSpell][15]))
										{
												RunePouch.specificRuneInsideRunePouch(player, "DELETE", CombatConstants.MAGIC_SPELLS[usedSpell][14], CombatConstants.MAGIC_SPELLS[usedSpell][15]);
										}
										else
										{
												ItemAssistant.deleteItemFromInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][14], ItemAssistant.getItemSlot(player, CombatConstants.MAGIC_SPELLS[usedSpell][14]), CombatConstants.MAGIC_SPELLS[usedSpell][15]);
										}
								}
								else if (action.equals("CHECK REQUIREMENT"))
								{
										if (ItemAssistant.hasItemAmountInInventory(player, CombatConstants.MAGIC_SPELLS[usedSpell][14], CombatConstants.MAGIC_SPELLS[usedSpell][15]))
										{
												currentRunesTypesAmount++;
										}
										if (hasRunePouch)
										{
												if (RunePouch.specificRuneInsideRunePouch(player, "CHECK", CombatConstants.MAGIC_SPELLS[usedSpell][14], CombatConstants.MAGIC_SPELLS[usedSpell][15]))
												{
														currentRunesTypesAmount++;
												}
										}
								}
						}
				}
				//>= because if player has death runes in inv and in pouch, it will pick them both up.
				if (currentRunesTypesAmount >= requiredRunesTypesTotal)
				{
						return true;
				}
				return false;
		}


		/**
		 * @return
		 * 			True, if the player is wearing full void magic.
		 */
		public static boolean wearingFullVoidMagic(Player player)
		{
				return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11663 && player.playerEquipment[ServerConstants.LEG_SLOT] == 8840 && player.playerEquipment[ServerConstants.BODY_SLOT] == 8839 && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
		}

}