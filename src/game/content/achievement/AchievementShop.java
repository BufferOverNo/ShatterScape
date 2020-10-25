package game.content.achievement;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Achievement shop requirements.
 * @author MGT Madness, created on 09-07-2015.
 */
public class AchievementShop
{

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity being purchased in the shop.
		 * @return
		 * 			True, if the player has the requirements to buy the item.
		 */
		public static boolean hasAchievementItemRequirements(Player player, int itemId, boolean shopping, boolean message)
		{

				// When updating this method, check the achievement item popup method.
				if (shopping && player.shopId != 42)
				{
						return true;
				}
				boolean entered = false;
				if (!message)
				{
						player.doNotSendMessage = true;
				}
				// Rune heraldic helm set.
				if (itemId >= 8464 && itemId <= 8494 || itemId >= 8714 && itemId <= 8744)
				{
						entered = true;
						if (player.getBerserkerPureKills() >= 100 || player.getRangedTankKills() >= 30)
						{
								return true;
						}
						player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after 100 berserker pure kills or 30 ranged tank kills.");
						return false;
				}
				int amountOfMatches = 0;
				for (int index = 0; index < AchievementDefinitions.easyAchievementsIndex[1] + 1; index++)
				{
						String[] items = AchievementDefinitions.getDefinitions()[index].itemRewards.split(" ");
						for (int i = 0; i < items.length; i++)
						{
								if (Integer.parseInt(items[i]) == itemId)
								{
										amountOfMatches++;
								}
						}
				}
				int matchesFound = 0;
				String requirements = "";
				for (int index = 0; index < AchievementDefinitions.easyAchievementsIndex[1] + 1; index++)
				{
						String[] items = AchievementDefinitions.getDefinitions()[index].itemRewards.split(" ");
						for (int i = 0; i < items.length; i++)
						{
								if (Integer.parseInt(items[i]) == itemId)
								{
										matchesFound++;
										entered = true;
										if (Achievements.getCurrentAchievementProgress(player, AchievementDefinitions.getDefinitions()[index].achievementId, false, false) >= AchievementDefinitions.getDefinitions()[index].completeAmount)
										{
												return true;
										}
										requirements = requirements + (requirements.isEmpty() ? "" : " or ") + AchievementDefinitions.getDefinitions()[index].achievementTitle;
										if (matchesFound == amountOfMatches)
										{
												player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " requires " + requirements + ".");
												return false;
										}
								}
						}
				}

				//@formatter:on
				player.doNotSendMessage = false;
				if (entered && !message)
				{
						return true;
				}
				if (!shopping && !message)
				{
						return false;
				}
				return true;
		}

}