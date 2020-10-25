package game.object.custom;

import core.Server;
import game.player.Player;

public class Object
{

		public int objectId;

		public int objectX;

		public int objectY;

		public int height;

		public int face;

		public int type;

		public int newId;

		public int tick;

		// Only used for doors.
		public int doorUniqueId;

		/**
		 * 
		 * @param id
		 * 			Object identity to spawn.
		 * @param x
		 * @param y
		 * @param height
		 * @param face
		 * @param type
		 * @param newId
		 * 			Object of identity to spawn after ticks are finished.
		 * @param ticks
		 * 			Amount of game ticks the object will last for.
		 */
		public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks)
		{
				this.objectId = id;
				this.objectX = x;
				this.objectY = y;
				this.height = height;
				this.face = face;
				this.type = type;
				this.newId = newId;
				this.tick = ticks;
				Server.objectManager.addObject(this);
		}

		public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks, int uniqueId)
		{
				this.objectId = id;
				this.objectX = x;
				this.objectY = y;
				this.height = height;
				this.face = face;
				this.type = type;
				this.newId = newId;
				this.tick = ticks;
				this.doorUniqueId = uniqueId;
				Server.objectManager.addObject(this);
		}

		public Object(Player player, int id, int x, int y, int height, int face, int type, int newId, int ticks)
		{
				this.objectId = id;
				this.objectX = x;
				this.objectY = y;
				this.height = height;
				this.face = face;
				this.type = type;
				this.newId = newId;
				this.tick = ticks;
				Server.objectManager.addObject1(player, this);
		}


}