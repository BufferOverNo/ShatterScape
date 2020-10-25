package game.item;



public class GroundItem
{

		public int itemId;

		public int itemX;

		public int itemY;

		public int itemHeight;

		public int itemAmount;

		public int hideTicks;

		public int removeTicks;

		public String ownerName;

		public String ownerGameMode;

		/**
		 * If Mgt drops an item on the floor and A picks it up, then the owner is Mgt.
		 * If mgt dies to A, and a picks up loot, the owner name is Mgt.
		 * So it traces the item back to its root.
		 */
		public String originalOwnerName;


		public GroundItem(int id, int x, int y, int amount, int hideTicks, String name, String ownerGameMode, int itemHeight, int removeTicks, String originalOwnerName)
		{
				this.itemId = id;
				this.itemX = x;
				this.itemY = y;
				this.itemAmount = amount;
				this.hideTicks = hideTicks;
				this.ownerName = name;
				this.ownerGameMode = ownerGameMode;
				this.itemHeight = itemHeight;
				this.removeTicks = removeTicks;
				this.originalOwnerName = originalOwnerName;
		}

		public String getOriginalOwnerName()
		{
				return originalOwnerName;
		}

		public int getItemId()
		{
				return this.itemId;
		}

		public int getItemX()
		{
				return this.itemX;
		}

		public int getItemY()
		{
				return this.itemY;
		}

		public int getItemAmount()
		{
				return this.itemAmount;
		}

		public String getOwnerName()
		{
				return this.ownerName;
		}

		public String getGameMode()
		{
				return this.ownerGameMode;
		}

		public int getItemHeight()
		{
				return this.itemHeight;
		}






}