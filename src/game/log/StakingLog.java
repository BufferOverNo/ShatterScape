package game.log;

import java.util.ArrayList;

import game.player.Player;
import utility.Misc;

/**
 * Used to take down RWT.
 * @author MGT Madness, created on 08-05-2017
 */
public class StakingLog
{
		public static ArrayList<String> data = new ArrayList<String>();

		public static void saveStakeLog(Player stakeWinner, Player stakeLoser, int stakeWinnerLoot, int stakeLoserLoot)
		{
				data.add(Misc.getDate() + " STAKE: [" + stakeWinner.getPlayerName() + "] won vs [" + stakeLoser.getPlayerName() + "], " + Misc.formatRunescapeStyle(stakeWinnerLoot) + " vs " + Misc.formatRunescapeStyle(stakeLoserLoot));
				data.add(stakeWinner.stakeAttacks + ", " + stakeWinner.stakeSpecialAttacks + " vs " + stakeLoser.stakeAttacks + ", " + stakeLoser.stakeSpecialAttacks);
		}
}
