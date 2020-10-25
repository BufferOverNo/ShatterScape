package game.content.shop;

import game.player.Player;

/**
 * Special currency shop prices.
 * @author MGT Madness, created on 29-09-2015.
 */
public class ShopSpecialCurrency
{

		public static enum Points
		{
				MERIT_SHOP(48, "Merit points"),
				VOTE_SHOP_1(33, "Vote tickets"),
				AGILITY_SHOP(49, "Agility points"),
				BLOOD_MONEY_SHOP(60, "Blood money"),
				UNTRADEABLES_SHOP(46, "Blood money"),
				BLOOD_MONEY_SHOP_RARES(74, "Blood money"),
				BUY_BACK_UNTRADEABLES_SHOP(11, "Blood money"),
				DONATOR_TOKEN_SHOP(71, "Donator tokens"),
				TOKKUL_SHOP(66, "Tokkul"),
				COMMUNITY_EVENT_SHOP(73, "Community event points");

				private int shopId;

				private String currency;

				private Points(int shopId, String currency)
				{
						this.shopId = shopId;
						this.currency = currency;
				}

				public int getShopId()
				{
						return shopId;
				}

				public String getCurrency()
				{
						return currency;
				}

		}

		public static boolean updateShopPointsTitle(Player player)
		{
				String title = "";
				switch (player.shopId)
				{
						case 48:
								title = "Merit points shop: " + player.getMeritPoints() + " points";
								break;
						case 49:
								title = "Agility points shop: " + player.getAgilityPoints() + " points";
								break;
						case 73:
								title = "Community event shop: " + player.communityEventPoints + " points";
								break;
				}

				if (!title.isEmpty())
				{
						player.getPA().sendFrame126(title, 19301);
						return true;
				}

				return false;
		}

}