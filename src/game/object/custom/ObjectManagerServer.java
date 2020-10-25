package game.object.custom;

import java.util.ArrayList;

import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Web;
import game.content.skilling.Firemaking;
import game.log.GameTickLog;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * @author Sanity
 */

public class ObjectManagerServer
{

		public ArrayList<Object> objects = new ArrayList<Object>();

		public ArrayList<Object> toRemove = new ArrayList<Object>();

		public void objectGameTick()
		{
				long time = System.currentTimeMillis();
				for (Object o : objects)
				{
						if (o.tick > 0)
						{
								o.tick--;
						}
						else if (o.tick == 0)
						{
								toRemove.add(o);
						}
				}
				for (Object o : toRemove)
				{
						if (isObelisk(o.newId))
						{
								int index = getObeliskIndex(o.newId);
								if (activated[index])
								{
										activated[index] = false;
										teleportObelisk(index);
								}
						}
						spawnNewObjectAfterTimer(o);
						objects.remove(o);
				}
				toRemove.clear();
				GameTickLog.objectTickDuration = System.currentTimeMillis() - time;
		}

		public void spawnNewObjectAfterTimer(Object o)
		{

				Firemaking.deleteFire(o);
				MithrilSeeds.deletePlant(o);
				Web.webRespawning(o);

				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player loop = PlayerHandler.players[j];
								if (loadForPlayer(o, loop))
								{
										loop.getPA().spawnClientObject(o.newId, o.objectX, o.objectY, o.face, o.type);
								}
						}
				}
		}

		// When an object is spawned straight away, such as empty ore, tree stump, throne, obelisk etc.
		public void spawnGlobalObject(Object o)
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player loop = PlayerHandler.players[j];
								if (loadForPlayer(o, loop))
								{
										loop.getPA().spawnClientObject(o.objectId, o.objectX, o.objectY, o.face, o.type);
								}
						}
				}
		}

		public void spawnGlobalObject(int objectId, int x, int y, int height, int face, int type)
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player loop = PlayerHandler.players[j];
								if (loop.playerAssistant.distanceToPoint(x, y) <= 60 && height == loop.getHeight())
								{
										loop.getPA().spawnClientObject(objectId, x, y, face, type);
								}
						}
				}
		}

		public Object getObject(int x, int y, int height)
		{
				for (Object o : objects)
				{
						if (o.objectX == x && o.objectY == y && o.height == height)
						{
								return o;
						}
				}
				return null;
		}

		// Called upon ChangeRegionPacket.
		public void changeRegionPacketClientObjectUpdate(Player player, boolean delay)
		{

				if (player == null)
				{
						return;
				}
				if (System.currentTimeMillis() - player.doorAntiSpam < 1300)
				{
						return;
				}
				player.doorAntiSpam = System.currentTimeMillis();
				if (!delay)
				{
						for (Object o : objects)
						{
								if (loadForPlayer(o, player))
								{
										player.getPA().spawnClientObject(o.objectId, o.objectX, o.objectY, o.face, o.type);
								}
						}
				}
				else
				{
						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										container.stop();
								}

								@Override
								public void stop()
								{
										for (Object o : objects)
										{
												if (loadForPlayer(o, player))
												{
														player.getPA().spawnClientObject(o.objectId, o.objectX, o.objectY, o.face, o.type);
												}
										}
								}
						}, 2);
				}
		}

		public boolean isObelisk(int id)
		{
				for (int j = 0; j < obeliskIds.length; j++)
				{
						if (obeliskIds[j] == id)
								return true;
				}
				return false;
		}

		public int[] obeliskIds = {14829, 14830, 14827, 14828, 14826, 14831};

		public int[][] obeliskCoords = {{3154, 3618}, {3225, 3665}, {3033, 3730}, {3104, 3792}, {2978, 3864}, {3305, 3914}};

		public boolean[] activated = {false, false, false, false, false, false};

		public void startObelisk(int obeliskId)
		{
				int index = getObeliskIndex(obeliskId);
				if (index >= 0)
				{
						if (!activated[index])
						{
								activated[index] = true;
								new Object(14825, obeliskCoords[index][0], obeliskCoords[index][1], 0, -1, 10, obeliskId, 16);
								new Object(14825, obeliskCoords[index][0] + 4, obeliskCoords[index][1], 0, -1, 10, obeliskId, 16);
								new Object(14825, obeliskCoords[index][0], obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16);
								new Object(14825, obeliskCoords[index][0] + 4, obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16);
						}
				}
		}

		public int getObeliskIndex(int id)
		{
				for (int j = 0; j < obeliskIds.length; j++)
				{
						if (obeliskIds[j] == id)
						{
								return j;
						}
				}
				return -1;
		}

		public void teleportObelisk(int port)
		{
				int random = Misc.random(5);
				while (random == port)
				{
						random = Misc.random(5);
				}
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player c = PlayerHandler.players[j];
								int xOffset = c.getX() - obeliskCoords[port][0];
								int yOffset = c.getY() - obeliskCoords[port][1];
								if (c.playerAssistant.withInDistance(c.getX(), c.getY(), obeliskCoords[port][0] + 2, obeliskCoords[port][1] + 2, 1))
								{
										Teleport.startTeleport(c, obeliskCoords[random][0] + xOffset, obeliskCoords[random][1] + yOffset, 0, "LEVER NO ANIMATION");
								}
						}
				}
		}

		public boolean loadForPlayer(Object o, Player c)
		{
				if (o == null || c == null)
						return false;
				return c.playerAssistant.distanceToPoint(o.objectX, o.objectY) <= 60 && c.getHeight() == o.height;
		}

		public void addObject(Object o)
		{
				if (getObject(o.objectX, o.objectY, o.height) == null)
				{
						objects.add(o);
						spawnGlobalObject(o);
				}
		}

		public void loadCustomObjects()
		{
				// Lever at level 51, leads to Ardougne
				new Object(1817, 3153, 3923, 0, 0, 4, -1, -1, 0);
		}

		/**
		 * Remove an object that is in the global objects list.
		 */
		public void removeGlobalObject(Object o)
		{
				for (Object list : objects)
				{
						if (o.objectX == list.objectX && o.objectY == list.objectY && o.objectId == list.objectId && o.height == list.height)
						{
								toRemove.add(o);
								spawnGlobalObject(o);
						}
				}
		}

		/**
		* Remove an object that is in the global objects list.
		*/
		public void removeGlobalObject(int uniqueId)
		{
				for (int index = 0; index < objects.size(); index++)
				{
						if (objects.get(index).doorUniqueId == uniqueId)
						{
								objects.remove(index);
								index--;
						}
				}
		}

		public void addObject1(Player player, Object o)
		{
				if (getObject(o.objectX, o.objectY, o.height) == null)
				{
						player.toRemove.add(o);
						objects.add(o);
						spawnGlobalObject(o);
				}
		}




}