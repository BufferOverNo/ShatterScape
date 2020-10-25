package game.content.miscellaneous;

import core.ServerConstants;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Notify the player about the loot.
 * @author MGT Madness, created on 13-02-2017.
 */
public class LootNotification
{

		public static void loot(Player player, int itemId, int amount)
		{
				int worth = BloodMoneyPrice.getBloodMoneyPrice(itemId) * amount;
				if (worth >= player.valuableLoot)
				{
						if (System.currentTimeMillis() - player.timeValuableLootNotified <= 1000)
						{

								if (System.currentTimeMillis() - player.timeValuableLootNotifiedAgain <= 1000)
								{
										return;
								}
								player.getPA().sendMessage(ServerConstants.BLUE_COL + "More valuable drops!");
								player.timeValuableLootNotifiedAgain = System.currentTimeMillis();
								return;
						}
						player.getPA().sendMessage(ServerConstants.BLUE_COL + "Valuable drop: " + ItemAssistant.getItemName(itemId) + " worth " + Misc.formatNumber(worth) + " blood money.");
						player.timeValuableLootNotified = System.currentTimeMillis();
				}
		}

}
