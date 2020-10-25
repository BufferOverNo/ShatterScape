package game.content.miscellaneous;

import game.player.Area;
import game.player.Player;

/**
 * Track what kind of gameplay the player does the most. Such as 30% skilling, 10% Pking, 60% Pvm.
 * @author MGT Madness, created on 03-01-2015.
 */
public class GameTimeSpent
{

		public final static String PKING = "PKING";

		public final static String SKILLING = "SKILLING";

		public final static String PVM = "PVM";

		/**
		 * @param player
		 * 			The associated player.
		 * @param type
		 * 			The type of gameplay.
		 * @return
		 * 			The percentage of the given type of gameplay.
		 */
		public static int getPercentagePlayed(int[] timeSpent, String type)
		{
				double percentage = 0;
				double totalSpent = timeSpent[0] + timeSpent[1] + timeSpent[2];

				switch (type)
				{
						case PKING:
								percentage = timeSpent[0] / totalSpent;
								break;

						case SKILLING:
								percentage = timeSpent[1] / totalSpent;
								break;

						case PVM:
								percentage = timeSpent[2] / totalSpent;
								break;
				}
				percentage *= 100;
				return (int) percentage;
		}

		/**
		 * Increase the game spent time spent.
		 * @param player
		 * 			The associated player.
		 * @param type
		 * 			The activity type.
		 */
		public static void increaseGameTime(Player player, String type)
		{
				if (System.currentTimeMillis() - player.lastTimeSpentUsed < 5000)
				{
						return;
				}
				player.lastTimeSpentUsed = System.currentTimeMillis();
				switch (type)
				{
						case PKING:
								player.timeSpent[0]++;
								if (Area.inWilderness(player))
								{
										player.lastActivity = "PVP WILD";
								}
								else
								{
										player.lastActivity = "PVP SAFE";
								}
								player.lastActivityTime = System.currentTimeMillis();
								break;
						case SKILLING:
								player.timeSpent[1]++;
								if (Area.inWilderness(player))
								{
										player.lastActivity = "SKILL WILD";
								}
								else
								{
										player.lastActivity = "SKILL SAFE";
								}
								player.lastActivityTime = System.currentTimeMillis();
								break;
						case PVM:
								player.timeSpent[2]++;
								if (Area.inWilderness(player))
								{
										player.lastActivity = "PVM WILD";
								}
								else
								{
										player.lastActivity = "PVM SAFE";
								}
								player.lastActivityTime = System.currentTimeMillis();
								break;
				}
		}

}
