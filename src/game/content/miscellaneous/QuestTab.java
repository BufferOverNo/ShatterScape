package game.content.miscellaneous;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import core.ServerConstants;
import game.content.worldevent.Tournament;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Quest tab interface updating.
 * @author MGT Madness, created on 22-02-2015.
 */
public class QuestTab
{

		private static int START_FRAME_ID = 28026;

		public static void updateQuestTab(Player player)
		{
				if (player == null)
				{
						return;
				}
				String[] content = {
						"<img=18>@yel@ Server",
						updateTime(player),
						updateDate(player),
						"",
						"<img=19>@yel@ Players",
						"Players online: @gre@" + PlayerHandler.getPlayerCount(),
						"Edgeville wild count: @gre@" + playersInEdgeville,
						"Under 30 wild count: @gre@" + playersUnder30Wild,
						"Deep wild count: @gre@" + playersOver30Wild,
						"",
						"<img=17>@yel@ Pk statistics",
						"Kills: @gr3@" + player.getWildernessKills(),
						"Deaths: @gr3@" + player.getWildernessDeaths(),
						"Kdr: @gr3@" + QuestTab.getKDR(player.getWildernessKills(), player.getWildernessDeaths()),
						"Melee main kills: @gr3@" + player.getMeleeMainKills(),
						"Hybrid kills: @gr3@" + player.getHybridKills(),
						"Berserker pure kills: @gr3@" + player.getBerserkerPureKills(),
						"Pure kills: @gr3@" + player.getPureKills(),
						"Ranged tank kills: @gr3@" + player.getRangedTankKills(),
						"Current killstreak: @gr3@" + player.currentKillStreak,
						"Highest killstreak: @gr3@" + player.killStreaksRecord,
						"Safe kills: @gr3@" + player.safeKills,
						"Safe deaths: @gr3@" + player.safeDeaths,
						"Bot kills: @gr3@" + player.playerBotKills,
						"Bot deaths: @gr3@" + player.playerBotDeaths,
						"Current bot killstreak: @gr3@" + player.playerBotCurrentKillstreak,
						"Highest bot killstreak: @gr3@" + player.playerBotHighestKillstreak,};
				for (int i = 0; i < content.length; i++)
				{
						player.getPA().sendFrame126(content[i], START_FRAME_ID + i);
				}
		}


		public static String updateTime(Player player)
		{
				return "Server time: @gr3@" + PlayerHandler.currentTime;

		}

		public static String updateDate(Player player)
		{
				return "Server date: @gr3@" + PlayerHandler.currentDate;
		}


		public static int playersInEdgeville;

		public static int playersUnder30Wild;

		public static int playersOver30Wild;

		/**
		 * Update amount of players in Wilderness every 90 seconds.
		 */
		public static boolean updatePlayersInWilderness()
		{
				playersInEdgeville = 0;
				playersUnder30Wild = 0;
				playersOver30Wild = 0;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player player = PlayerHandler.players[i];
						if (player == null)
						{
								continue;
						}
						if (player.isBot)
						{
								continue;
						}
						if (!Area.inWilderness(player))
						{
								continue;
						}
						if (Area.inEdgevilleWilderness(player))
						{
								playersInEdgeville++;
						}
						else if (player.wildernessLevel <= 30)
						{
								playersUnder30Wild++;
						}
						else if (player.wildernessLevel > 30)
						{
								playersOver30Wild++;
						}
				}
				return true;
		}

		public static long lastUpdatedTimeAndDate;

		/**
		 * Update date and time for player every 60 seconds.
		 */
		public static boolean updateTimeAndDate()
		{
				//59 because 60 can actually skip a minute by chance.
				if (System.currentTimeMillis() - lastUpdatedTimeAndDate < 59000)
				{
						return false;
				}
				DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				Calendar cal = Calendar.getInstance();
				PlayerHandler.currentTime = dateFormat.format(cal.getTime());

				DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
				Calendar cal1 = Calendar.getInstance();
				PlayerHandler.currentDate = dateFormat1.format(cal1.getTime());

				lastUpdatedTimeAndDate = System.currentTimeMillis();
				updatePlayersInWilderness();
				Tournament.currentTime();
				return true;
		}

		/**
		 * Get the KDR.
		 * @param kills
		 * 			The player's kills.
		 * @param deaths
		 * 			The player's death.s
		 * @return
		 * 			The KDR.
		 */
		public static double getKDR(int kills, int deaths)
		{
				double kdr = (double) kills / (double) deaths;
				if (kdr != kdr)
				{
						kdr = 0;
				}
				if (deaths == 0)
				{
						kdr = kills;
				}
				DecimalFormat df = new DecimalFormat("#.##");
				kdr = Double.parseDouble(df.format(kdr));
				return kdr;
		}

}
