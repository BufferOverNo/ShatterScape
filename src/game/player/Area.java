package game.player;

/**
 * Area constants.
 * @author MGT Madness, created on 02-03-2015.
 */
public class Area
{

		public static boolean isWithInArea(Player player, int X1, int X2, int Y1, int Y2)
		{
				return player.getX() >= X1 && player.getX() <= X2 && player.getY() >= Y1 && player.getY() <= Y2;
		}

		public static boolean inResourceWilderness(Player player)
		{
				return isWithInArea(player, 3174, 3196, 3924, 3944);
		}

		public static boolean inZombieWaitingRoom(Player player)
		{
				return isWithInArea(player, 3651, 3668, 3512, 3528);
		}

		public static boolean isWithInArea(int currentX, int currentY, int X1, int X2, int Y1, int Y2)
		{
				return currentX >= X1 && currentX <= X2 && currentY >= Y1 && currentY <= Y2;
		}

		public static boolean inRfdArea(int x, int y)
		{
				if (isWithInArea(x, y, 1889, 1911, 5345, 5367))
				{
						return true;
				}
				return false;
		}

		public static boolean isInBarrowsChestArea(Player player)
		{
				return player.getX() >= 3545 && player.getX() <= 3558 && player.getY() >= 9689 && player.getY() <= 9701;
		}


		public static boolean inWildernessAgilityCourse(Player player)
		{
				if (isWithInArea(player, 2988, 3007, 3931, 3968))
				{
						return true;
				}

				return false;
		}

		/**
		 * @param player
		 * @return
		 * 			True, if the player is at the traditional Edgeville wilderness spots.
		 */
		public static boolean inEdgevilleWilderness(Player player)
		{
				if (isWithInArea(player, 3025, 3137, 3520, 3559) && inWilderness(player))
				{
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 * @return
		 * 			True, if the player is at the traditional Edgeville wilderness spots.
		 */
		public static boolean inWestDragons(Player player)
		{
				if (isWithInArea(player, 2962, 2995, 3573, 3605))
				{
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player is in the safe PK zone where players can attack each other.
		 */
		public static boolean inSafePkFightZone(Player player)
		{
				if (Area.inSafePKArea(player))
				{
						return true;
				}
				return false;
		}

		/**
		 * Counting the part behind the line too, as in the whole clan wars map region
		 */
		public static boolean inSafePkFightZoneAll(Player player)
		{
				if (isWithInArea(player, 3250, 3400, 4740, 4860))
				{
						return true;
				}
				return false;
		}

		public static boolean inSafePKArea(Player player)
		{
				if (isWithInArea(player, 3250, 3400, 4760, 4860))
				{
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player is in the Wilderness.
		 */
		public static boolean inWilderness(Player player)
		{

				// when updating this, also update the method below it called nextStepInWilderness.
				if (isWithInArea(player.getX(), player.getY(), 2995, 3025, 3524, 3534))
				{
						return false;
				}
				if (isWithInArea(player.getX(), player.getY(), 3001, 3025, 3535, 3538))
				{
						return false;
				}
				if (isWithInArea(player.getX(), player.getY(), 3005, 3025, 3539, 3543))
				{
						return false;
				}
				if (isWithInArea(player.getX(), player.getY(), 3026, 3029, 3524, 3532))
				{
						return false;
				}
				if (isWithInArea(player.getX(), player.getY(), 3030, 3035, 3522, 3527))
				{
						return false;
				}
				if (isWithInArea(player.getX(), player.getY(), 3066, 3123, 3520, 3523))
				{
						return true;
				}
				if (isWithInArea(player.getX(), player.getY(), 2942, 3391, 3524, 3965) || isWithInArea(player.getX(), player.getY(), 2942, 3391, 9919, 10365))
				{
						return true;
				}
				return false;
		}

		public static boolean inWilderness(int x, int y)
		{
				if (isWithInArea(x, y, 2995, 3025, 3524, 3534))
				{
						return false;
				}
				if (isWithInArea(x, y, 3001, 3025, 3535, 3538))
				{
						return false;
				}
				if (isWithInArea(x, y, 3005, 3025, 3539, 3543))
				{
						return false;
				}
				if (isWithInArea(x, y, 3026, 3029, 3524, 3532))
				{
						return false;
				}
				if (isWithInArea(x, y, 3030, 3035, 3522, 3527))
				{
						return false;
				}
				if (isWithInArea(x, y, 3066, 3123, 3520, 3523))
				{
						return true;
				}
				if (isWithInArea(x, y, 2942, 3391, 3524, 3965) || isWithInArea(x, y, 2942, 3391, 9919, 10365))
				{
						return true;
				}
				return false;
		}


		/**
		 * Used to check if the player is in a PVP area.
		 *
		 * @return The current state.
		 */
		public static boolean inPVPArea(Player player)
		{
				if (inSafePkFightZone(player))
				{
						return true;
				}
				if (inWilderness(player))
				{
						return true;
				}
				return false;
		}

		public static boolean inDuelArenaRing(Player player)
		{
				if (isWithInArea(player, 3332, 3390, 3243, 3259))
				{
						return true;
				}
				return false;
		}

		public static boolean inDuelArena(Player player)
		{
				if (isWithInArea(player, 3323, 3393, 3196, 3290) || isWithInArea(player, 3312, 3322, 3224, 3247))
				{
						return true;
				}
				return false;
		}

		public static boolean inGodWarsDungeon(Player player)
		{
				if (isWithInArea(player, 2823, 2941, 5255, 5374))
				{
						return true;
				}
				return false;
		}

		public static boolean inMulti(int x, int y)
		{

				// Do not add Zombie areas to this, make a different one where if the npc is spawned in a zombie instance, then it is multi, and player is in zombie instance, then multi.
				if (isWithInArea(x, y, 2863, 2877, 5350, 5374) || // Bandos Boss Chamber
				isWithInArea(x, y, 2889, 2908, 5255, 5276) || // Saradomin Boss Chamber
				isWithInArea(x, y, 2915, 2941, 5316, 5332) || // Zamorak Boss Chamber
				isWithInArea(x, y, 2823, 2843, 5295, 5309) || // Armadyl Boss Chamber
				isWithInArea(x, y, 2624, 2690, 2550, 2619) || // Pest control
				isWithInArea(x, y, 2896, 2927, 3595, 3630) || // Troll map(used on 317 servers for GWD)
				isWithInArea(x, y, 2892, 2932, 4435, 4464) || // Dagannoth kings.
				isWithInArea(x, y, 2975, 2999, 9625, 9659) || // Barrelchest
				isWithInArea(x, y, 3305, 3324, 9362, 9392) || // Corporeal Beast
				isWithInArea(x, y, 2365, 2500, 5057, 5186) || // Tzhaar.
				// King black dragon.
				isWithInArea(x, y, 2256, 2287, 4680, 4711) ||
				// Wilderness multi zones.
				isWithInArea(x, y, 3009, 3071, 3599, 3712)
				//
				|| isWithInArea(x, y, 2945, 2957, 3816, 3827)
				//
				|| isWithInArea(x, y, 3138, 3327, 3523, 3651)
				//
				|| isWithInArea(x, y, 3189, 3327, 3651, 3751)
				//
				|| isWithInArea(x, y, 3151, 3327, 3751, 3903)
				//
				|| isWithInArea(x, y, 3199, 3393, 3841, 3968)
				//
				|| isWithInArea(x, y, 3133, 3151, 3839, 3903)
				//
				|| isWithInArea(x, y, 3111, 3133, 3871, 3903)
				//
				|| isWithInArea(x, y, 3071, 3119, 3877, 3903)
				//
				|| isWithInArea(x, y, 3049, 3071, 3897, 3903)
				//
				|| isWithInArea(x, y, 3007, 3049, 3856, 3903)
				//
				|| isWithInArea(x, y, 3049, 3053, 3863, 3870)
				//
				|| isWithInArea(x, y, 2983, 3009, 3912, 3967)

				// End of Wilderness multi zones.
				)
				{
						return true;
				}
				return false;
		}

		public static boolean inCylopsRoom(Player player)
		{
				if (isWithInArea(player.getX(), player.getY(), 2847, 2877, 3532, 3557))
				{
						return true;
				}
				if (isWithInArea(player.getX(), player.getY(), 2838, 2846, 3543, 3555))
				{
						return true;
				}
				return false;
		}

}