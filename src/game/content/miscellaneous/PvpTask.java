package game.content.miscellaneous;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import utility.Misc;

/**
 * Pvp task system.
 * @author MGT Madness, created on 27-02-2017.
 */
public class PvpTask
{
		/**
		 * Maximum kills to be give per specific task type.
		 */
		private final static int MAXIMUM_KILLS = 4;

		private final static int BLOOD_MONEY_PER_KILL = 800;

		private final static String[] PVP_TYPE_NAME = {"Pure", "Berserker", "Ranged tank", "Maxed main"};

		public static void obtainTask(Player player)
		{
				if (hasPvpTask(player))
				{
						player.getDH().sendStatement("You already have a Pvp task.");
						return;
				}
				if (player.canClaimPvpTaskReward)
				{
						player.getDH().sendStatement("Claim your reward first.");
						return;
				}
				int maximumTaskCancels = 2;
				int totalKills = 0;
				for (int index = 0; index < player.pvpTask.length; index++)
				{
						if (maximumTaskCancels > 0)
						{
								if (Misc.hasPercentageChance(50))
								{
										maximumTaskCancels--;
										continue;
								}
						}
						int random = Misc.random(1, MAXIMUM_KILLS);
						totalKills += random;
						player.pvpTask[index] = random;
				}
				player.pvpTaskSize = totalKills;
				killsLeft(player);

		}

		public static void claimReward(Player player)
		{
				if (hasPvpTask(player))
				{
						player.getDH().sendStatement("You need to complete your Pvp task first.");
						return;
				}
				if (!player.canClaimPvpTaskReward)
				{
						player.getDH().sendStatement("You need to obtain a Pvp task first.");
						return;
				}
				int amount = BLOOD_MONEY_PER_KILL * player.pvpTaskSize;
				if (ItemAssistant.addItem(player, 18644, amount))
				{
						CoinEconomyTracker.incomeList.add("PVP-TASK " + amount);
						player.canClaimPvpTaskReward = false;
						player.getDH().sendItemChat1("Pvp task", "You are awarded x" + amount + " blood money!", 18644, 200, 10, 0);
						player.pvpTasksCompleted++;
				}
		}

		public static void whatAreRewards(Player player)
		{
				player.getDH().sendItemChat2("Pvp task", "After your task is completed, you will", "be awarded " + BLOOD_MONEY_PER_KILL + " blood money per kill.", 18644, 200, 10, 0);
		}

		public static void pvpKill(Player player, int pvpTypeIndex)
		{
				if (!hasPvpTask(player))
				{
						return;
				}
				if (player.pvpTask[pvpTypeIndex] > 0)
				{
						player.pvpTask[pvpTypeIndex]--;
						if (!hasPvpTask(player))
						{
								player.getPA().sendMessage("You have completed: " + ServerConstants.RED_COL + (player.pvpTasksCompleted + 1) + ServerConstants.BLACK_COL + " Pvp tasks.");
								player.canClaimPvpTaskReward = true;
						}
				}
		}

		public static void killsLeft(Player player)
		{
				if (!hasPvpTask(player))
				{
						player.getDH().sendStatement("You do not have a Pvp task.");
						return;
				}
				String text = "Kill players as a: ";
				for (int index = 0; index < player.pvpTask.length; index++)
				{
						if (player.pvpTask[index] > 0)
						{
								String type = "x" + player.pvpTask[index] + " " + PVP_TYPE_NAME[index];
								if (player.pvpTask[index] > 1)
								{
										//type = type + "s";
								}
								if (index == 3)
								{
										type = type + ".";
								}
								else
								{
										type = type + ", ";
								}

								text = text + type;
						}
				}
				if (text.endsWith(", "))
				{
						text = text.substring(0, text.length() - 2);
						text = text + ".";
				}
				player.getDH().sendStatement(text);
		}

		public static boolean hasPvpTask(Player player)
		{
				for (int index = 0; index < player.pvpTask.length; index++)
				{
						if (player.pvpTask[index] > 0)
						{
								return true;
						}
				}
				return false;
		}

}
