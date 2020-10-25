package game.object.areas;

import game.content.combat.EdgeAndWestsRule;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Web;
import game.object.ObjectEvent;
import game.object.custom.DoorEvent;
import game.player.Area;
import game.player.Player;

/**
 * Handles objects of Wilderness
 * @author MGT Madness 27-10-2013
 */
public class WildernessObjects
{

		/**
		 * @return true if object in the Wilderness.
		 */
		public static boolean isWildernessObject(final Player player, final int objectType)
		{
				if (!Area.inWilderness(player))
				{
						return false;
				}
				for (int i = 0; i < wildernessObject.length; i++)
				{
						if (objectType == wildernessObject[i])
						{
								return true;
						}
				}

				return false;
		}

		/**
		 * Wilderness object identities.
		 */
		private static int[] wildernessObject = {
				1597, // Gate.
				1596, // Gate.
				9707, // Lever.
				5959, // Lever.
				9706, // Lever.
				9707, // Lever.
				1765, // Ladder.
				1766, // Ladder.
				1815, // Lever.
				1816, // Lever.
				16664, // Staircase.
				16665, // Staircase.
				733, // Web.
				2558, // Door.
				18987,
				18988,
				2557,
				1519, // Large door.
				1516, // Large door.
				411, // Chaos altar.
				272, // Ladder.
				273, // Ladder.
				245, // Ship's ladder.
				246, // Ship's ladder.
				246, // Ship's ladder.
				11726,
				11727,};

		/**
		 * Perform actions of the objects in Wilderness.
		 */
		public static void doWildernessObject(final Player player, int objectType)
		{

				int playerX = player.getX();
				int playerY = player.getY();

				switch (objectType)
				{

						// Ship's ladder, first floor.
						case 245:
								player.getPA().movePlayer(playerX, playerY + 2, 2);
								break;

						// Ship's ladder, second floor.
						case 246:
								player.getPA().movePlayer(playerX, playerY - 2, 1);
								break;


						// Ladder.
						case 272:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), 1);
								break;

						// Ladder.
						case 273:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), 0);
								break;

						case 5959:
								if (player.getX() != 3090)
								{
										return;
								}
								player.turnPlayerTo(player.getObjectX() - 1, player.getObjectY());

								if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 2539, 4712))
								{
										return;
								}
								Teleport.startTeleport(player, 2539, 4712, 0, "LEVER");
								break;

						case 9706:
								if (player.getX() == 3105 && player.getY() == 3956)
								{

										if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 3105, 3951))
										{
												return;
										}
										Teleport.startTeleport(player, 3105, 3951, 0, "LEVER");
								}
								break;

						case 9707:
								if (player.getX() == 3105 && player.getY() == 3951)
								{
										if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 3105, 3956))
										{
												return;
										}
										Teleport.startTeleport(player, 3105, 3956, 0, "LEVER");
								}
								break;

						//Ladder at Lesser demon area outside Kbd
						case 18987:
								if (player.getX() == 3016 && player.getY() == 3849 || player.getX() == 3017 && player.getY() == 3848 || player.getX() == 3017 && player.getY() == 3850)
								{
										ObjectEvent.climbDownLadder(player, 3069, 10255, 0);
								}
								break;

						//Ladder, leads to lesser demon area outside Kbd area.
						case 18988:
								ObjectEvent.climbUpLadder(player, 3016, 3849, 0);
								break;

						// Lever located at 42 wilderness, beside the spiders, This is the lever to enter KBD area.
						case 1816:
								player.turnPlayerTo(3067, 10252);
								if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 2271, 4680))
								{
										return;
								}
								Teleport.startTeleport(player, 2271, 4680, 0, "LEVER");
								break;

						// Lever located at 50 wild
						case 1815:
								Teleport.startTeleport(player, 2561, 3311, 0, "LEVER");
								break;

						// Staircase, west of outside of Magebank.
						case 16664:
								if (System.currentTimeMillis() - player.agility1 < 1700)
								{
										return;
								}
								player.agility1 = System.currentTimeMillis();
								if (player.getObjectX() == 3044 && player.getObjectY() == 3924)
								{
										player.getPA().movePlayer(player.getX(), 10323, 0);
								}
								break;

						// Staircase
						case 16665:
								if (System.currentTimeMillis() - player.agility1 < 1700)
								{
										return;
								}
								player.agility1 = System.currentTimeMillis();
								player.getPA().movePlayer(player.getX(), 3927, 0);
								break;

						// Web
						case 733:
								Web.slash(player, 946);
								break;

						case 11726:
						case 11727:
								DoorEvent.openAutomaticDoor(player);
								break;

						case 411:
								PlayerMiscContent.prayAtAltar(player);
								break;

				}
		}

}