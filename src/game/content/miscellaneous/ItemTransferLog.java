package game.content.miscellaneous;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import game.content.clanchat.ClanChatHandler;
import game.content.worldevent.WorldEvent;
import game.item.BloodMoneyPrice;
import game.item.GameItem;
import game.log.StakingLog;
import game.player.Player;
import network.connection.InvalidAttempt;
import network.connection.VoteManager;
import utility.FileUtility;
import utility.Misc;

/**
 * Log when a player transfers items through trading, dropping, staking.
 * @author MGT Madness, created on 07-04-2017.
 */
public class ItemTransferLog
{

		/**
		 * name-data to add on seperate line
		 */
		public static ArrayList<String> data = new ArrayList<String>();

		public static void saveTransferItemLog()
		{
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				FileUtility.saveArrayContents("backup/logs/trade/" + dateFormat.format(cal.getTime()) + ".txt", data);
				data.clear();
				PmLog.savePmLog();
				HackLog.saveHackLog();
				FileUtility.saveArrayContents(PmLog.FILE_LOCATION + "alert.txt", PmLog.alertLog);
				PmLog.alertLog.clear();
				FileUtility.saveArrayContents("backup/logs/dice/" + dateFormat.format(cal.getTime()) + ".txt", ClanChatHandler.diceLog);
				ClanChatHandler.diceLog.clear();
				FileUtility.saveArrayContents("backup/logs/eventdebug.txt", WorldEvent.debug);
				WorldEvent.debug.clear();
				FileUtility.saveArrayContents("backup/logs/vote/" + dateFormat.format(cal.getTime()) + ".txt", VoteManager.voteUid);
				VoteManager.voteUid.clear();
				FileUtility.saveArrayContents("backup/logs/trade/rigged stake.txt", StakingLog.data);
				StakingLog.data.clear();
				if (InvalidAttempt.autBlacklistUpdated)
				{
						Misc.printDontSave("Updated: backup/logs/bruteforce/autoblacklisted.txt");
						InvalidAttempt.autBlacklistUpdated = false;
						FileUtility.deleteAllLines("backup/logs/bruteforce/autoblacklisted.txt");
						FileUtility.saveArrayContentsSilent("backup/logs/bruteforce/autoblacklisted.txt", InvalidAttempt.autoBlacklist);
				}
				InvalidAttempt.whitelistedMacs.clear();
				InvalidAttempt.autoBlacklist.clear();
				InvalidAttempt.loadAutoBlacklist();
				FileUtility.deleteAllLines("backup/logs/bruteforce/whitelisted.txt");
				FileUtility.saveArrayContents("backup/logs/bruteforce/whitelisted.txt", InvalidAttempt.whitelistedMacs);
				FileUtility.saveArrayContents("backup/logs/bruteforce/autoblacklisted reason.txt", InvalidAttempt.autoBlacklistReason);
				InvalidAttempt.autoBlacklistReason.clear();
				InvalidAttempt.saveInvalidAttemptLog();
		}

		public static void tradeCompleted(Player one, Player two)
		{
				int playerOneTotal = 0;
				int playerTwoTotal = 0;
				if (one != null && two != null)
				{
						for (GameItem item : one.getTradeAndDuel().offeredItems)
						{
								if (item.id > 0 && item.amount > 0)
								{
										playerOneTotal += BloodMoneyPrice.getBloodMoneyPrice(item.id) * item.amount;
								}
						}
						for (GameItem item1 : two.getTradeAndDuel().offeredItems)
						{
								if (item1.id > 0 && item1.amount > 0)
								{
										playerTwoTotal += BloodMoneyPrice.getBloodMoneyPrice(item1.id) * item1.amount;
								}
						}
						if (ClanChatHandler.inDiceCc(one, false) && ClanChatHandler.inDiceCc(two, false))
						{
								ClanChatHandler.sendDiceClanMessage("[Manager]", one.getClanId(), one.getPlayerName() + " traded with " + two.getPlayerName() + ", " + Misc.formatRunescapeStyle(playerOneTotal) + " and " + Misc.formatRunescapeStyle(playerTwoTotal));
						}
						if ((playerOneTotal + playerTwoTotal) >= 5000)
						{
								data.add(Misc.getDate() + " TRADE: [" + one.getPlayerName() + "] trading with [" + two.getPlayerName() + "], " + Misc.formatRunescapeStyle(playerOneTotal) + " and " + Misc.formatRunescapeStyle(playerTwoTotal));
						}
				}
		}

		public static void duelWon(Player stakeWinner, Player stakeLoser)
		{
				int myStakedWealth = 0;
				int otherStakedWealth = 0;
				// If winner is nulled, then the loser wins the stake rewards.
				if (stakeWinner == null)
				{
						for (GameItem item : stakeLoser.getTradeAndDuel().myStakedItems)
						{
								if (item.id > 0 && item.amount > 0)
								{
										myStakedWealth += BloodMoneyPrice.getBloodMoneyPrice(item.id) * item.amount;
								}
						}
						for (GameItem item1 : stakeLoser.getTradeAndDuel().otherStakedItems)
						{
								if (item1.id > 0 && item1.amount > 0)
								{
										otherStakedWealth += BloodMoneyPrice.getBloodMoneyPrice(item1.id) * item1.amount;
								}
						}
						if ((myStakedWealth + otherStakedWealth) >= 5000)
						{
								data.add(Misc.getDate() + " STAKE: [" + stakeLoser.getPlayerName() + "] won vs NULLED KILLER!, " + myStakedWealth + " vs " + otherStakedWealth);
						}
				}
				else
				{
						for (GameItem item : stakeWinner.getTradeAndDuel().myStakedItems)
						{
								if (item.id > 0 && item.amount > 0)
								{
										myStakedWealth += BloodMoneyPrice.getBloodMoneyPrice(item.id) * item.amount;
								}
						}
						for (GameItem item : stakeWinner.getTradeAndDuel().otherStakedItems)
						{
								if (item.id > 0 && item.amount > 0)
								{
										otherStakedWealth += BloodMoneyPrice.getBloodMoneyPrice(item.id) * item.amount;
								}
						}
						if ((myStakedWealth + otherStakedWealth) >= 5000)
						{
								data.add(Misc.getDate() + " STAKE: [" + stakeWinner.getPlayerName() + "] won vs [" + stakeLoser.getPlayerName() + "], " + myStakedWealth + " vs " + otherStakedWealth);
						}
						if ((myStakedWealth + otherStakedWealth) >= 10000)
						{
								if (stakeWinner.stakeAttacks != stakeLoser.stakeAttacks || stakeWinner.stakeSpecialAttacks != stakeLoser.stakeSpecialAttacks)
								{
										StakingLog.saveStakeLog(stakeWinner, stakeLoser, myStakedWealth, otherStakedWealth);
								}
						}
				}
		}

		//TODO owner name is wrong, gotta add a string to createGroundItem called sourceOfItemOwnerName
		public static void pickUpItem(Player player, String playerName, int itemId, int itemAmount, String ownerName)
		{
				if (playerName.equalsIgnoreCase(ownerName))
				{
						return;
				}
				int worth = BloodMoneyPrice.getBloodMoneyPrice(itemId) * itemAmount;
				if (worth < 1000)
				{
						return;
				}
				String string = "picked up an item that belongs";

				if (System.currentTimeMillis() - player.timeInCombat <= 60000)
				{
						long time = (System.currentTimeMillis() - player.timeInCombat) / 1000;
						string = "picked up an item while in combat " + time + " seconds ago that belongs";
				}
				data.add(Misc.getDate() + " PICKUP: [" + playerName + "] " + string + " to [" + ownerName + "] " + Misc.formatRunescapeStyle(worth));

		}
}
