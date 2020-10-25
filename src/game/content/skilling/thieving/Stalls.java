package game.content.skilling.thieving;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Thieving stalls.
 * @author MGT Madness, created on 29-11-2015.
 */
public class Stalls
{


		public static enum StallData
		{
				BANANA_STALL(1, 16, 1963, 4875),
				GENERAL_STALL(40, 36, 1731, 4876),
				RUNES_STALL(65, 81, 1391, 4877),
				SCIMITAR_STALL(85, 160, 1331, 4878); // 1333

				private int levelRequirement;

				private int experience;

				private int itemStolen;

				private int objectId;


				private StallData(int levelRequirement, int experience, int itemStolen, int objectId)
				{
						this.levelRequirement = levelRequirement;
						this.experience = experience;
						this.itemStolen = itemStolen;
						this.objectId = objectId;
				}

				public int getLevelRequirement()
				{
						return levelRequirement;
				}

				public int getExperience()
				{
						return experience;
				}

				public int getItemStolen()
				{
						return itemStolen;
				}

				public int getObjectId()
				{
						return objectId;
				}
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param objectId
		 * 			The objectId to find a match for.
		 * @return
		 * 			True if the object is a stall object.
		 */
		public static boolean isStallObject(Player player, int objectId)
		{
				for (StallData data : StallData.values())
				{
						if (objectId == data.getObjectId())
						{
								stealFromStall(player, data);
								return true;
						}
				}
				return false;
		}

		/**
		 * Steal from the stall.
		 * @param player
		 * 			The associated player.
		 * @param data
		 * 			The Stall enum data.
		 */
		private static void stealFromStall(Player player, StallData data)
		{
				if (!hasStallRequirements(player, data))
				{
						return;
				}

				player.stoleFromStallTime = System.currentTimeMillis();
				player.startAnimation(832);
				giveItemFromStallEvent(player, data);
				player.skillingStatistics[SkillingStatistics.STALLS_THIEVED]++;

		}

		/**
		* True if the player has the requirements to use the stall.
		* @param player
		* 			The associated player.
		* @param data
		* 			The Stall enum data.
		*/
		private static boolean hasStallRequirements(Player player, StallData data)
		{
				if (player.baseSkillLevel[ServerConstants.THIEVING] < data.getLevelRequirement())
				{
						player.getDH().sendStatement("You need a thieving level of " + data.getLevelRequirement() + " to steal from this stall.");
						return false;
				}

				if (System.currentTimeMillis() - player.stoleFromStallTime < 1750)
				{
						return false;
				}

				if (ItemAssistant.getFreeInventorySlots(player) == 0)
				{
						player.playerAssistant.sendMessage("You need at least 1 inventory space.");
						return false;
				}

				return true;

		}

		/**
		* Give the item stolen from the stall.
		* @param player
		* 			The associated player.
		* @param data
		* 			The Stall enum data.
		*/
		private static void giveItemFromStallEvent(final Player player, final StallData data)
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
								giveItemFromStallAction(player, data);
						}
				}, 1);
		}

		public static void giveItemFromStallAction(Player player, StallData data)
		{
				int amount = 1;
				int itemId = data.getItemStolen();
				ItemAssistant.addItemToInventoryOrDrop(player, itemId, amount);
				player.playerAssistant.sendFilterableMessage("You steal a " + ItemAssistant.getItemName(itemId) + " from the stall.");
				Skilling.addSkillExperience(player, data.getExperience(), ServerConstants.THIEVING);
		}

}
