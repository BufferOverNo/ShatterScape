package game.content.bank;

import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.miscellaneous.Transform;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;

/**
 * Handle all bank buttons.
 * @author MGT Madness, created on 24-07-2014.
 */
public class BankButtons
{

		private static int WITHDRAW_BUTTON = 85248;

		private static int BANK_PIN_BUTTON = 20174;

		private static int SEARCH_BUTTON = 85244;

		private static int DEPOSIT_WORN_ITEMS_BUTTON = 86000;

		private static int DEPOSIT_INVENTORY_ITEMS_BUTTON = 85252;

		private static int SHOW_EQUIPMENT_STATS_BUTTON = 86004;

		public static boolean isBankButtons(Player player, int buttonID)
		{
				if (buttonID == WITHDRAW_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						player.takeAsNote = !player.takeAsNote;
						return true;
				}
				else if (buttonID == BANK_PIN_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						player.getDH().sendDialogues(116);
						return true;
				}
				else if (buttonID == SEARCH_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						return true;
				}
				else if (buttonID == DEPOSIT_WORN_ITEMS_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						player.originalTab = player.bankingTab;
						depositWornItems(player, true, true);
						player.bankUpdated = true;
						return true;
				}
				else if (buttonID == DEPOSIT_INVENTORY_ITEMS_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						player.originalTab = player.bankingTab;
						depositInventoryItems(player, true);
						player.bankUpdated = true;
						return true;
				}
				else if (buttonID == SHOW_EQUIPMENT_STATS_BUTTON)
				{
						if (!Bank.hasBankingRequirements(player, false))
						{
								return true;
						}
						player.usingEquipmentBankInterface = true;
						player.getPA().displayInterface(15150);
						player.getPA().changeToSidebar(3);
						return true;
				}
				switch (buttonID)
				{
						case 86008:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 0, true, false);
								player.originalTab = 0;
								return true;
						case 86009:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 1, true, false);
								player.originalTab = 1;
								return true;
						case 86010:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 2, true, false);
								player.originalTab = 2;
								return true;
						case 86011:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 3, true, false);
								player.originalTab = 3;
								return true;
						case 86012:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 4, true, false);
								player.originalTab = 4;
								player.setUsingBankSearch(false);
								return true;
						case 86013:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 5, true, false);
								player.originalTab = 5;
								return true;
						case 86014:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 6, true, false);
								player.originalTab = 6;
								return true;
						case 86015:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 7, true, false);
								player.originalTab = 7;
								return true;
						case 86016:
								if (!Bank.hasBankingRequirements(player, false))
								{
										return true;
								}
								player.setUsingBankSearch(false);
								Bank.shouldRearrangePreviousTab(player);
								Bank.openUpBank(player, 8, true, false);
								player.originalTab = 8;
								return true;
				}
				return false;
		}

		/**
		 * Transfer all items from the inventory to the bank.
		 * @param player
		 * 			The associated player.
		 */
		public static void depositInventoryItems(Player player, boolean message)
		{
				player.setUsingBankInterface(true);
				boolean depositItems = false;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] > 0)
						{
								depositItems = true;
						}
				}
				if (!depositItems && message)
				{
						player.playerAssistant.sendMessage("You don't have any items to deposit.");
						return;
				}

				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] > 0 && player.playerItemsN[i] > 0)
						{
								Bank.bankItem(player, player.playerItems[i], i, player.playerItemsN[i], false);
						}
				}

				Bank.openUpBank(player, player.originalTab, false, false);
		}

		/**
		 * Transfer worn items to the bank.
		 * @param player
		 * 			The associated player.
		 */
		public static void depositWornItems(Player player, boolean message, boolean deleteEquipment)
		{
				boolean deposit = false;
				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						if (player.playerEquipment[i] > 0)
						{
								deposit = true;
						}
				}
				if (!deposit && message)
				{
						player.playerAssistant.sendMessage("You don't have any worn items to deposit.");
						return;
				}
				if (!message)
				{
						for (int i = 0; i < player.playerEquipment.length; i++)
						{
								if (player.playerEquipment[i] > 0 && player.playerEquipmentN[i] > 0)
								{
										if (!ItemDefinition.getDefinitions()[player.playerEquipment[i]].random)
										{
												if (Bank.addItemToBank(player, player.playerEquipment[i], player.playerEquipmentN[i], false))
												{
														ItemAssistant.replaceEquipmentSlot(player, i, -1, 1, true, false);
												}
										}
										else
										{
												ItemAssistant.replaceEquipmentSlot(player, i, -1, 1, true, false);
										}
								}
						}
				}
				else
				{

						for (int i = 0; i < player.playerEquipment.length; i++)
						{
								if (player.playerEquipment[i] > 0 && player.playerEquipmentN[i] > 0)
								{
										if (Bank.addItemToBank(player, player.playerEquipment[i], player.playerEquipmentN[i], false))
										{
												if (deleteEquipment)
												{
														ItemAssistant.replaceEquipmentSlot(player, i, -1, 1, true, false);
												}
										}
								}
						}
				}
				player.setInventoryUpdate(true);
				Bank.openUpBank(player, player.getLastBankTabOpened(), false, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				Combat.updatePlayerStance(player);
				Transform.unTransform(player);
		}

}