package game.content.miscellaneous;

import game.content.combat.Death;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;

/**
 * Looting bag feature.
 * @author MGT Madness, created on 20-04-2016.
 */
public class LootingBag
{

		public static void displayLootingBagInterface(Player player)
		{
				for (int index = 0; index < player.lootingBagStorageItemId.length; index++)
				{
						int item = player.lootingBagStorageItemId[index] == 0 ? -1 : player.lootingBagStorageItemId[index];
						player.getPA().sendFrame34a(22251, item, index, player.lootingBagStorageItemAmount[index]);
				}
				player.getPA().setSidebarInterface(3, 22245);
		}

		public static void closeLootingBagInterface(Player player)
		{
				player.getPA().setSidebarInterface(3, 3213);
		}

		public static void lootingBagDeath(Player victim, Player attacker, boolean killerExists)
		{
				for (int index = 0; index < victim.lootingBagStorageItemId.length; index++)
				{
						int item = victim.lootingBagStorageItemId[index];
						int amount = victim.lootingBagStorageItemAmount[index];
						if (item <= 0)
						{
								continue;
						}

						victim.lootingBagStorageItemId[index] = 0;
						victim.lootingBagStorageItemAmount[index] = 0;
						Death.victimLoot(victim, attacker, item, amount, false, killerExists);
				}
		}

		public static void withdrawLootingBag(Player player)
		{
				if (Area.inPVPArea(player))
				{
						player.getPA().sendMessage("You may only withdraw items outside Pvp areas.");
						return;
				}
				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1)
				{
						return;
				}
				for (int index = 0; index < player.lootingBagStorageItemId.length; index++)
				{
						int item = player.lootingBagStorageItemId[index];
						int amount = player.lootingBagStorageItemAmount[index];
						boolean give = false;
						if (ItemDefinition.getDefinitions()[item].stackable && ItemAssistant.hasItemInInventory(player, item))
						{
								give = true;
						}
						else if (ItemAssistant.getFreeInventorySlots(player) > 0)
						{
								give = true;
						}
						if (give)
						{
								if (ItemAssistant.addItem(player, item, amount))
								{
										player.lootingBagStorageItemId[index] = 0;
										player.lootingBagStorageItemAmount[index] = 0;
								}
						}
				}
		}

		public static boolean useWithLootingBag(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot)
		{
				boolean hasBag = false;
				if (itemUsed == 18658 || usedWith == 18658)
				{
						hasBag = true;
				}
				if (!hasBag)
				{
						return false;
				}

				if (!Area.inWilderness(player))
				{
						player.getPA().sendMessage("You can only use this in the Wilderness.");
						return true;
				}

				int storeItem = 0;
				int storeItemAmount = 0;
				int storeItemSlot = 0;
				if (itemUsed != 18658)
				{
						storeItem = itemUsed;
						storeItemSlot = itemUsedSlot;
						storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
				}
				if (usedWith != 18658)
				{
						storeItem = usedWith;
						storeItemSlot = usedWithSlot;
						storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
				}
				if (storeItem == 18658)
				{
						player.getPA().sendMessage("Bag'ception is not permitted.");
						return true;
				}
				boolean itemStoredCompleted = false;
				if (ItemDefinition.getDefinitions()[storeItem].stackable)
				{
						for (int index = 0; index < player.lootingBagStorageItemId.length; index++)
						{
								if (player.lootingBagStorageItemId[index] == storeItem)
								{
										int maximumAmount = Integer.MAX_VALUE - player.lootingBagStorageItemAmount[index];
										if (storeItemAmount > maximumAmount)
										{
												player.getPA().sendMessage("Your looting bag cannot hold anymore!");
												return false;
										}
										player.lootingBagStorageItemAmount[index] += storeItemAmount;
										ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
										itemStoredCompleted = true;
										break;
								}
						}
				}
				if (!itemStoredCompleted)
				{

						for (int index = 0; index < player.lootingBagStorageItemId.length; index++)
						{
								if (player.lootingBagStorageItemId[index] == 0)
								{
										player.lootingBagStorageItemId[index] = storeItem;
										player.lootingBagStorageItemAmount[index] = storeItemAmount;
										ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
										break;
								}
						}
				}

				return true;
		}
}
