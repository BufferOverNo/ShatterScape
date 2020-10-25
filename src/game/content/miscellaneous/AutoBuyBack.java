package game.content.miscellaneous;

import core.ServerConstants;
import game.content.bank.Bank;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Auto-buy back untradeables from the shop.
 * @author MGT Madness, created on 28-04-2017
 */
public class AutoBuyBack
{
		/**
		 * Toggle the feature on/off
		 */
		public static void toggleOption(Player player)
		{
				player.autoBuyBack = !player.autoBuyBack;
				player.getDH().sendItemChat1("", "Auto-buy back from untradeables shop has been turned " + (player.autoBuyBack ? "on" : "off") + ".", 10551, 200, 0, -10);
		}

		public static void autoBuyBack(Player player)
		{
				if (!player.autoBuyBack)
				{
						return;
				}

				// -1 so it does not finish their whole blood money stack and ruin their bank order.
				int bloodMoneyStock = Bank.getItemAmountInBank(player, 18644) - 1;
				if (bloodMoneyStock <= 0)
				{
						return;
				}
				int bloodMoneySpent = 0;
				for (int index = 0; index < player.itemsToShop.size(); index++)
				{
						String[] args = player.itemsToShop.get(index).split(" ");
						int itemId = Integer.parseInt(args[0]);
						int amount = Integer.parseInt(args[1]);
						int price = ((BloodMoneyPrice.getBloodMoneyPrice(itemId) / 10) + 50) * amount;
						if (price <= bloodMoneyStock - bloodMoneySpent)
						{
								player.itemsToShop.remove(index);
								index--;
								bloodMoneySpent += price;
								ItemAssistant.addItem(player, itemId, amount);
						}
						else
						{
								player.getPA().sendMessage(ServerConstants.RED_COL + "You do not have enough blood money to purchase all your untradeables.");
								break;
						}
				}
				if (bloodMoneySpent <= 0)
				{
						return;
				}
				Bank.hasItemInBankAndDelete(player, 18644, bloodMoneySpent);
				player.getPA().sendMessage("A total of x" + Misc.formatNumber(bloodMoneySpent) + " blood money has been deducted from your bank to purchase");
				player.getPA().sendMessage("back your untradeables.");
		}
}
