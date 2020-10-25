package game.content.donator;

import core.ServerConstants;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Donator scrolls.
 * @author MGT Madness, created on 19-01-2016.
 */
public class DonatorTokenUse
{
		public static enum DonatorRankSpentData
		{
				DONATOR(150),
				SUPER_DONATOR(400),
				EXTREME_DONATOR(1000),
				LEGENDARY_DONATOR(3000);

				private int spentRequired;


				private DonatorRankSpentData(int spentRequired)
				{
						this.spentRequired = spentRequired;
				}

				public int getSpentRequired()
				{
						return spentRequired;
				}

		}

		public static void upgradeToNextRank(Player player, int amountUsed)
		{
				player.donatorTokensRankUsed += amountUsed;
				player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have spent a total of " + player.donatorTokensRankUsed + " donator tokens!");
				int highestRankIndex = -1;
				// Search for the next rank, then see if player has the amount for next rank, then add to claimed.
				for (DonatorRankSpentData data : DonatorRankSpentData.values())
				{
						if (player.donatorTokensRankUsed >= data.getSpentRequired())
						{
								highestRankIndex = data.ordinal();
						}
				}
				if (highestRankIndex == -1)
				{
						return;
				}
				if (highestRankIndex + 3 == player.playerRights)
				{
						return;
				}
				if (highestRankIndex + 1 <= DonatorRankSpentData.values().length - 1)
				{
						String nextName = DonatorRankSpentData.values()[highestRankIndex + 1].name();
						nextName = Misc.capitalize(nextName).replace("_", " ");
						player.getPA().sendMessage(ServerConstants.BLUE_COL + "You need " + (DonatorRankSpentData.values()[highestRankIndex + 1].getSpentRequired() - player.donatorTokensRankUsed) + " Donator tokens spent to become " + nextName + ".");
				}
				highestRankIndex += 3;
				String rankName = DonatorRankSpentData.values()[highestRankIndex - 3].toString();
				rankName = rankName.replace("_", " ");
				rankName = Misc.capitalize(rankName);
				// Give reward.
				if (rankName.equals("Donator"))
				{
						ItemAssistant.addItemToInventoryOrDrop(player, 4024, 1);
				}
				player.gfx0(1637);
				if (!GameMode.getGameMode(player, "IRON MAN"))
				{
						player.playerRights = highestRankIndex;
				}
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				player.getPA().announce(GameMode.getGameModeName(player) + " has been promoted to " + rankName + "!");
				player.getPA().sendScreenshot(rankName, 2);
		}

}
