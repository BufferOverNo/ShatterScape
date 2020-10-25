package game.content.miscellaneous;

import core.Server;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Toxic blowpipe features.
 * @author MGT Madness, created on 25-02-2017.
 */
public class Blowpipe
{
		//18779

		public static void unload(Player player)
		{
				if (ItemAssistant.addItem(player, player.blowpipeDartItemId, player.blowpipeDartItemAmount))
				{
						player.getPA().sendMessage("You have unloaded x" + player.blowpipeDartItemAmount + " " + ItemAssistant.getItemName(player.blowpipeDartItemId) + "s.");
						player.blowpipeDartItemId = 0;
						player.blowpipeDartItemAmount = 0;
				}
		}

		public static void check(Player player)
		{
				if (player.blowpipeDartItemId == 0)
				{
						player.getPA().sendMessage("You Toxic blowpipe is empty.");
						return;
				}
				player.getPA().sendMessage("You Toxic blowpipe has x" + player.blowpipeDartItemAmount + " " + ItemAssistant.getItemName(player.blowpipeDartItemId) + "s.");
		}

		public static boolean useWithBlowpipe(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot)
		{
				boolean hasBlowpipe = false;
				if (itemUsed == 18779 || usedWith == 18779)
				{
						hasBlowpipe = true;
				}
				if (!hasBlowpipe)
				{
						return false;
				}

				int storeItem = 0;
				int storeItemAmount = 0;
				int storeItemSlot = 0;
				if (itemUsed != 18779)
				{
						storeItem = itemUsed;
						storeItemSlot = itemUsedSlot;
						storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
				}
				if (usedWith != 18779)
				{
						storeItem = usedWith;
						storeItemSlot = usedWithSlot;
						storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
				}
				if (storeItem != 11230 && storeItem != 811)
				{
						return false;
				}
				if (player.blowpipeDartItemId != storeItem && player.blowpipeDartItemId != 0)
				{
						player.getPA().sendMessage("You need to unload the darts before adding a different one.");
						return true;
				}
				player.blowpipeDartItemId = storeItem;
				if (player.blowpipeDartItemId == storeItem)
				{
						player.blowpipeDartItemAmount += storeItemAmount;
				}
				else
				{
						player.blowpipeDartItemAmount = storeItemAmount;
				}
				player.getPA().sendMessage("You have loaded x" + storeItemAmount + " " + ItemAssistant.getItemName(storeItem) + "s.");
				ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
				return true;
		}

		public static void diedWithBlowpipe(Player victim, Player killer, int itemId)
		{
				if (itemId != 18779)
				{
						return;
				}
				Server.itemHandler.createGroundItem(killer, victim.blowpipeDartItemId, victim.getX(), victim.getY(), victim.blowpipeDartItemAmount, false, 0, true, victim.getPlayerName());
				victim.blowpipeDartItemId = 0;
				victim.blowpipeDartItemAmount = 0;

		}
}
