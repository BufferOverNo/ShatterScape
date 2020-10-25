package game.item;

import game.player.Player;

/**
 * Destroy item interface functions.
 * @author MGT Madness, created on 06-07-2015.
 */
public class DestroyItem
{

		/**
		 * True, if the button belongs to the destroy interface.
		 * @param player
		 * @param buttonId
		 * @return
		 */
		public static boolean isDestroyInterfaceButton(Player player, int buttonId)
		{
				switch (buttonId)
				{
						case 55096:
								player.getPA().closeInterfaces();
								player.itemDestroyedId = -1;
								return true;
						case 55095:
								destroyItemAction(player, player.itemDestroyedId);
								return true;
				}
				return false;
		}

		public static void displayDestroyItemInterface(Player player, int itemId, int slot)
		{
				String itemName = ItemAssistant.getItemName(itemId);
				String[][] info = {{"@bla@Are you sure you want to drop this item?", "14174"}, {"Yes.", "14175"}, {"No.", "14176"}, {" ", "14177"}, {"@red@This item will dissapear if you accept.", "14182"}, {" ", "14183"}, {itemName, "14184"}};
				player.getPA().sendFrame34(itemId, 0, 14171, ItemAssistant.getItemAmount(player, itemId, slot));
				for (int i = 0; i < info.length; i++)
				{
						player.getPA().sendFrame126(info[i][0], Integer.parseInt(info[i][1]));
				}
				player.getPA().sendFrame164(14170);
		}

		public static void destroyItemAction(Player player, int itemId)
		{
				itemId = player.itemDestroyedId;
				if (itemId == -1 || player.itemDestroyedSlot == -1)
				{
						return;
				}
				String itemName = ItemAssistant.getItemName(itemId);
				ItemAssistant.deleteItemFromInventory(player, itemId, player.itemDestroyedSlot, player.playerItemsN[ItemAssistant.getItemSlot(player, itemId)]);
				player.playerAssistant.sendMessage("Your " + itemName + " vanishes as you drop it on the ground.");
				player.getPA().closeInterfaces();
				player.itemDestroyedId = -1;
				if (ItemAssistant.hasSingularUntradeableItem(player, itemId))
				{
						player.singularUntradeableItemsOwned.remove(Integer.toString(itemId));
						if (itemId == 2677)
						{
								player.clueScrollType = -1;
						}
				}
		}
}