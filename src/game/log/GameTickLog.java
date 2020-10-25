package game.log;

import java.util.ArrayList;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotManager;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Track the game tick.
 * @author MGT Madness, created on 12-12-2013.
 */
public class GameTickLog
{

		private static int loop;

		public static int totalTickTimeTaken;

		private static long loopDuration;

		public static long cycleEventTickDuration;

		public static long packetTickDuration;

		public static long itemTickDuration;

		public static long playerTickDuration;

		public static long npcTickDuration;

		public static long objectTickDuration;

		public static long characterBackupTickDuration;

		public static ArrayList<String> saveTicks = new ArrayList<String>();

		public static int saveTicksTimer;

		public static ArrayList<String> lagDebugList = new ArrayList<String>();

		/**
		* Stage 1 of the loop debug.
		*/
		public static void loopDebugPart1()
		{
				loopDuration = System.currentTimeMillis();
				loop++;
		}

		private final static int MINUTES_AGO_USED_WILDERNESS = 5;

		/***
		 * Last loop duration.
		 */
		public static int durationAmount;

		/**
		 * Stage 2 of the loop debug.
		 */
		public static void loopDebugPart2()
		{
				if ((System.currentTimeMillis() - Server.timeServerOnline) <= 10000)
				{
						return;
				}
				loopDuration = System.currentTimeMillis() - loopDuration;
				durationAmount = (int) loopDuration;
				saveTicksTimer++;
				if (saveTicksTimer == 10)
				{
						saveTicksTimer = 0;
						saveTicks.add(durationAmount + "");
						if (saveTicks.size() > 90)
						{
								saveTicks.remove(0);
						}
				}
				totalTickTimeTaken += loopDuration;
				if (ServerConfiguration.STABILITY_TEST)
				{
						Misc.print("Main loop took: " + BotManager.BOTS_AMOUNT + " bots, " + loopDuration + " ms.");
						Misc.print("Cycle event: " + cycleEventTickDuration);
						Misc.print("Item: " + itemTickDuration);
						Misc.print("Player: " + playerTickDuration);
						Misc.print("NPC: " + npcTickDuration);
						Misc.print("Object: " + objectTickDuration);
						Misc.print("Packet: " + packetTickDuration);
				}
				else if (loop == 3000 && !ServerConfiguration.DEBUG_MODE) // 30 minutes.
				{
						int playersUsedWilderness = 0;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null && System.currentTimeMillis() - PlayerHandler.players[i].wildernessEnteredTime <= (MINUTES_AGO_USED_WILDERNESS * 60000))
								{
										playersUsedWilderness++;
								}
						}
						Misc.print("[Main loop took: " + totalTickTimeTaken / loop + " ms] [Players online: " + PlayerHandler.getPlayerCount() + "] [Players online used Wilderness in past " + MINUTES_AGO_USED_WILDERNESS + " mins: " + playersUsedWilderness + "]");
						totalTickTimeTaken = 0;
						loop = 0;
				}
				if (!ServerConfiguration.DEBUG_MODE && loopDuration >= 100)
				{
						Misc.print("Lag spike has been logged: " + loopDuration + "ms, with " + PlayerHandler.getPlayerCount() + " players.");
						appendLagLog(loopDuration);
				}
		}

		/**
		 * Create the lag log.
		 */
		private static void appendLagLog(long loopDuration)
		{
				lagDebugList.add("[" + Misc.getDate() + "] " + "Main tick took: " + loopDuration + "ms with " + PlayerHandler.getPlayerCount() + " players.");
				lagDebugList.add("Cycle event: " + cycleEventTickDuration);
				lagDebugList.add("Item: " + itemTickDuration);
				lagDebugList.add("Player: " + playerTickDuration);
				lagDebugList.add("NPC: " + npcTickDuration);
				lagDebugList.add("Object: " + objectTickDuration);
				lagDebugList.add("Packet: " + packetTickDuration);
				lagDebugList.add("Character backup: " + characterBackupTickDuration);
				lagDebugList.add("--------------");
		}

		/**
		 * Save the lag log arraylist to the file upon server restart.
		 */
		public static void saveLagLogFile()
		{
				FileUtility.saveArrayContents("backup/logs/system log/lag spike.txt", lagDebugList);
		}

}
