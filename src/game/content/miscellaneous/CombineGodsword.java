package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Combine Godsword pieces to create a Godsword.
 * @author MGT Madness, created on 13-02-2015.
 */
public class CombineGodsword
{

		/**
		 * Combine Godsword shard pieces to create a Godsword blade.
		 * @param player
		 * 			The associated player.
		 */
		public static boolean createGodSwordBlade(Player player, int itemUsed, int itemUsedOn)
		{
				if (itemUsed != 11710 && itemUsed != 11712 && itemUsed != 11714)
				{
						return false;
				}
				if (!ItemAssistant.hasItemInInventory(player, 11710) || !ItemAssistant.hasItemInInventory(player, 11712) || !ItemAssistant.hasItemInInventory(player, 11714))
				{
						return false;
				}
				ItemAssistant.deleteItemFromInventory(player, 11710, 1);
				ItemAssistant.deleteItemFromInventory(player, 11712, 1);
				ItemAssistant.deleteItemFromInventory(player, 11714, 1);
				ItemAssistant.addItem(player, 11690, 1);
				player.getDH().sendItemChat1("", "You combine the shards and create a Godsword blade.", 11690, 200, 15, 0);
				return true;
		}

		/**
		 * Create the Godsword by using the appropriate hilt.
		 * @param player
		 * 			The associated player.
		 */
		public static boolean createGodSword(Player player, int itemUsed, int itemUsedOn)
		{
				if (itemUsed != 11690 && itemUsed != 11702 && itemUsed != 11704 && itemUsed != 11706 && itemUsed != 11708)
				{
						return false;
				}
				if (ItemAssistant.hasItemInInventory(player, 11690) && ItemAssistant.hasItemInInventory(player, 11702))
				{
						ItemAssistant.deleteItemFromInventory(player, 11690, 1);
						ItemAssistant.deleteItemFromInventory(player, 11702, 1);
						ItemAssistant.addItem(player, 11694, 1);
						successfulGodSwordMessage(player, 11694);
						return true;
				}
				if (ItemAssistant.hasItemInInventory(player, 11690) && ItemAssistant.hasItemInInventory(player, 11704))
				{
						ItemAssistant.deleteItemFromInventory(player, 11690, 1);
						ItemAssistant.deleteItemFromInventory(player, 11704, 1);
						ItemAssistant.addItem(player, 11696, 1);
						successfulGodSwordMessage(player, 11696);
						return true;
				}
				if (ItemAssistant.hasItemInInventory(player, 11690) && ItemAssistant.hasItemInInventory(player, 11706))
				{
						ItemAssistant.deleteItemFromInventory(player, 11690, 1);
						ItemAssistant.deleteItemFromInventory(player, 11706, 1);
						ItemAssistant.addItem(player, 11698, 1);
						successfulGodSwordMessage(player, 11698);
						return true;
				}
				if (ItemAssistant.hasItemInInventory(player, 11690) && ItemAssistant.hasItemInInventory(player, 11708))
				{
						ItemAssistant.deleteItemFromInventory(player, 11690, 1);
						ItemAssistant.deleteItemFromInventory(player, 11708, 1);
						ItemAssistant.addItem(player, 11700, 1);
						successfulGodSwordMessage(player, 11700);
						return true;
				}
				return false;
		}

		public static void successfulGodSwordMessage(Player player, int itemId)
		{
				player.getDH().sendItemChat1("", "You combine the Godsword blade with the hilt.", itemId, 200, 0, -20);
		}

}
