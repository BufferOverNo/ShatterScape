package game.content.miscellaneous;

import game.content.achievement.Achievements;
import game.item.ItemAssistant;
import game.item.ItemOn;
import game.player.Player;

/**
 * Abyssal whip colouring.
 * @author MGT Madness, created on 08-04-2014.
 */
public class ItemColouring
{

		public static int ABYSSAL_WHIP = 4151;

		public static int DARK_BOW = 11235;

		public static int CLEANING_CLOTH = 3188;

		public static enum ItemColouringData
		{
				WHITE(1773, 15443, 15703),
				BLUE(1767, 15442, 15702),
				YELLOW(1765, 15441, 15701),
				GREEN(1771, 15444, 15704);

				private int dyeId;

				private int whipId;

				private int bowId;


				private ItemColouringData(int dyeId, int whipId, int bowId)
				{
						this.dyeId = dyeId;
						this.whipId = whipId;
						this.bowId = bowId;
				}

				public int getDyeId()
				{
						return dyeId;
				}

				public int getWhipId()
				{
						return whipId;
				}

				public int getBowId()
				{
						return bowId;
				}

		}


		/**
		 * Abyssal whip dying.
		 * @param player
		 * 			The associated player.
		 * @param itemUsed
		 * 			The item being used.
		 * @param usedWith
		 * 			The item being used on.
		 */
		public static boolean combine(Player player, int itemUsed, int usedWith)
		{
				for (ItemColouringData data : ItemColouringData.values())
				{
						if (ItemOn.hasTwoItems(player, itemUsed, usedWith, data.getDyeId(), DARK_BOW))
						{
								ItemAssistant.deleteItemFromInventory(player, DARK_BOW, 1);
								ItemAssistant.deleteItemFromInventory(player, data.getDyeId(), 1);
								ItemAssistant.addItem(player, data.getBowId(), 1);
								player.getDH().sendItemChat1("", "You use the dye to colour the Dark bow.", data.getBowId(), 200, 0, -18);
								Achievements.checkCompletionSingle(player, 1069);
								return true;
						}
						if (ItemOn.hasTwoItems(player, itemUsed, usedWith, data.getDyeId(), ABYSSAL_WHIP))
						{
								ItemAssistant.deleteItemFromInventory(player, ABYSSAL_WHIP, 1);
								ItemAssistant.deleteItemFromInventory(player, data.getDyeId(), 1);
								ItemAssistant.addItem(player, data.getWhipId(), 1);
								player.getDH().sendItemChat1("", "You use the dye to colour the Abyssal whip.", data.getWhipId(), 200, 10, 0);
								Achievements.checkCompletionSingle(player, 1069);
								return true;
						}
						if (ItemOn.hasTwoItems(player, itemUsed, usedWith, data.getBowId(), CLEANING_CLOTH))
						{
								ItemAssistant.deleteItemFromInventory(player, data.getBowId(), 1);
								ItemAssistant.addItem(player, DARK_BOW, 1);
								player.getPA().sendMessage("You clean the Dark bow.");
								return true;
						}
						if (ItemOn.hasTwoItems(player, itemUsed, usedWith, data.getWhipId(), CLEANING_CLOTH))
						{
								ItemAssistant.deleteItemFromInventory(player, data.getWhipId(), 1);
								ItemAssistant.addItem(player, ABYSSAL_WHIP, 1);
								player.getPA().sendMessage("You clean the Abyssal whip.");
								return true;
						}
				}

				return false;
		}

}
