package game.content.worldevent;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.highscores.HighscoresDaily;
import game.content.miscellaneous.Announcement;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Automatic world event system.
 * @author MGT Madness, created on 28-02-2018.
 */
public class WorldEvent
{
		public static ArrayList<String> debug = new ArrayList<String>();

		/**
		 * Event length in minutes.
		 */
		private static int EVENT_LENGTH = 20;

		/**
		 * Amount of players required for an event to start.
		 */
		private static int PLAYERS_REQUIRED = 1;

		/**
		 * Amount of minutes between each event, from the moment an event ends to the moment another event starts.
		 */
		private static int LENGTH_BETWEEN_EVENTS = 100;

		/**
		 * Time the event has ended or is scheduled to end.
		 */
		public static long timeEventEnd;

		/**
		 * Current active event.
		 */
		public static String currentEvent = "";

		/**
		 * Store the upcoming event.
		 */
		public static String nextEvent = "";

		public static boolean eventStartedAnnounced;

		/**
		 * World event game tick.
		 */
		public static void worldEventTick()
		{
				Tournament.tournamenTick();
				long currentTime = System.currentTimeMillis();
				if (currentTime >= HighscoresDaily.getInstance().announceTime && HighscoresDaily.getInstance().announceTime != 0)
				{
						HighscoresDaily.getInstance().dateChanged();
				}
				if (!currentEvent.isEmpty())
				{
						if ((currentTime > timeEventEnd) && timeEventEnd != 0)
						{
								debug.add(Misc.getDate() + ": Here2: " + currentEvent + ", " + nextEvent);
								eventEnded();
								return;
						}
				}

				// Must be under event ended.
				if (PlayerHandler.getPlayerCount() < PLAYERS_REQUIRED)
				{
						return;
				}

				if (timeEventEnd > currentTime)
				{
						return;
				}
				if (currentTime < timeEventEnd + (LENGTH_BETWEEN_EVENTS * 60000) && timeEventEnd != 0)
				{
						return;
				}
				// Event will start 10 minutes from now
				timeEventEnd = currentTime + (EVENT_LENGTH * 60000);
				getRandomEvent();
				debug.add(Misc.getDate() + ": Here9: " + currentTime + ", " + timeEventEnd + ", " + currentEvent + ", " + nextEvent);
				eventStartedAnnounced = true;
				if (!WorldEvent.getActiveEvent("BLOOD KEY"))
				{
						Announcement.announce("World event has started! It will last for 20 minutes.", ServerConstants.DARK_BLUE);
						Announcement.announce(nextEvent, ServerConstants.DARK_BLUE);
				}
				else
				{
						Announcement.announce("Pickup the blood key for 15k blood money.", ServerConstants.DARK_BLUE);
						Announcement.announce("Location will be announced in 1 minute.", ServerConstants.DARK_BLUE);
				}
				BloodKey.spawnBloodKey();
		}

		public static void logInUpdate(Player player)
		{
				if (!eventStartedAnnounced)
				{
						return;
				}
				long minutesLeft = ((timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
				if (!WorldEvent.getActiveEvent("BLOOD KEY"))
				{
						player.getPA().sendMessage(ServerConstants.BLUE_COL + "Event active: " + nextEvent + ", " + minutesLeft + " minutes left.");
				}
		}

		/**
		 * Set a random event.
		 */
		public static void getRandomEvent()
		{
				int random = Misc.random(1, 3);
				if (random == 1)
				{
						String[] pkTypes = {"berserker", "ranged tank", "f2p"};
						random = Misc.random(0, pkTypes.length - 1);
						String finalBuild = pkTypes[random];
						nextEvent = "Pk as a " + finalBuild + " for 4x artefacts!";
						currentEvent = finalBuild.toUpperCase() + " PK";
				}
				else if (random == 2)
				{
						String[] wildTypes = {"Ice Strykewyrm", "Venenatis", "Callisto", "Lava dragons", "Chaos elemental", "Revenants", "Tormented demons"};
						int amountOfNpcsToKill = 3;
						int currentNpcsAdded = 0;
						int maximumCancels = wildTypes.length - amountOfNpcsToKill;
						int currentCancels = 0;
						String npcsToKill = "";
						for (int index = 0; index < wildTypes.length; index++)
						{
								if (currentNpcsAdded == amountOfNpcsToKill)
								{
										break;
								}
								if (currentCancels == maximumCancels)
								{
										if (npcsToKill.isEmpty())
										{
												npcsToKill = wildTypes[index];
										}
										else
										{
												npcsToKill = npcsToKill + ", " + wildTypes[index];
										}
										currentNpcsAdded++;
										continue;
								}
								double chance = (double) wildTypes.length / (double) amountOfNpcsToKill;
								chance *= 10;
								if (Misc.random(1, (int) chance) > 10)
								{
										currentCancels++;
										continue;
								}
								if (npcsToKill.isEmpty())
								{
										npcsToKill = wildTypes[index];
								}
								else
								{
										npcsToKill = npcsToKill + ", " + wildTypes[index];
								}
								currentNpcsAdded++;
						}
						nextEvent = "Kill " + npcsToKill + " for 3x bonus chance!";
						currentEvent = npcsToKill;

				}
				else if (random == 3)
				{
						nextEvent = "Find the blood key and make it out alive!";
						currentEvent = "BLOOD KEY";
				}
		}

		/**
		 * Event has ended.
		 */
		public static void eventEnded()
		{
				if (!WorldEvent.getActiveEvent("BLOOD KEY"))
				{
						Announcement.announce("World event has ended!", ServerConstants.DARK_BLUE);
				}
				debug.add(Misc.getDate() + ": Here10: " + System.currentTimeMillis() + ", " + timeEventEnd + ", " + currentEvent + ", " + nextEvent + ", " + eventStartedAnnounced);
				currentEvent = "";
				eventStartedAnnounced = false;
		}

		/**
		* @return
		* 	True if the eventName matches the current active event.
		*/
		public static boolean getActiveEvent(String eventName)
		{
				return currentEvent.equals(eventName);
		}

}
