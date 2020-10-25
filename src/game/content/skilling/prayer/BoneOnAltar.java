package game.content.skilling.prayer;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Bones on altar.
 * @author MGT Madness, created on 29-01-2015.
 */
public class BoneOnAltar
{

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemUsedID
		 * 			The item identity used on the altar.
		 * @return
		 * 			True, if the item identity used on the altar is a bone.
		 */
		public static boolean isABone(Player player, int itemUsedID)
		{
				for (int i = 0; i < BuryBone.bonesExp.length; i++)
				{
						if (itemUsedID == BuryBone.bonesExp[i][0])
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemUsedID
		 * 			The item identity used on the altar.
		 * @return
		 * 			True, if the player has a bone in his inventory.
		 */
		private static boolean hasBone(Player player, int itemUsedID)
		{
				return ItemAssistant.hasItemInInventory(player, itemUsedID) ? true : false;
		}

		/**
		 * Start the bone on altar system.
		 * @param player
		 * 			The associated player.
		 * @param itemUsedId
		 * 			The item identity used on the altar.
		 */
		public static void useBoneOnAltar(Player player, int itemUsedId)
		{
				if (!isABone(player, itemUsedId))
				{
						return;
				}
				if (player.usingBoneOnAltarEvent)
				{
						return;
				}
				player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.PRAYER];
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 150, itemUsedId, 35, 0);
				player.skillingData[0] = itemUsedId;

		}

		/**
		 * Cycle event of bones on altar.
		 * @param player
		 * 			The associated player.
		 * @param itemUsedId
		 * 			The item identity used on the altar.
		 */
		private static void boneOnAltarCycleEvent(final Player player, final int itemUsedId)
		{
				player.usingBoneOnAltarEvent = true;
				player.usingBoneOnAltar = true;
				canUseBoneOnAltarAction(player, itemUsedId);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{

								if (!canUseBoneOnAltarAction(player, itemUsedId))
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.usingBoneOnAltarEvent = false;
								player.usingBoneOnAltar = false;
						}
				}, 2);
		}

		private static boolean canUseBoneOnAltarAction(Player player, int itemUsedId)
		{
				if (hasBone(player, itemUsedId) && player.skillingData[1] > 0 && player.usingBoneOnAltar)
				{
						for (int i = 0; i < BuryBone.bonesExp.length; i++)
						{
								if (itemUsedId == BuryBone.bonesExp[i][0])
								{
										successfulBoneOnAltar(player, itemUsedId, i);
										player.skillingData[1]--;
										return true;
								}
						}
				}
				return false;
		}

		/**
		 * Reward the player for a successful bone on altar action.
		 * @param player
		 * 			The associated player.
		 * @param itemUsedId
		 * 			The item identity used on the altar.
		 */
		private static void successfulBoneOnAltar(Player player, int itemUsedId, int i)
		{
				Skilling.addSkillExperience(player, (int) (BuryBone.bonesExp[i][1] * 3.5), ServerConstants.PRAYER);
				if (System.currentTimeMillis() - player.boneOnAltarAnimation > 2000)
				{
						player.startAnimation(896);
						player.boneOnAltarAnimation = System.currentTimeMillis();
				}
				ItemAssistant.deleteItemFromInventory(player, itemUsedId, 1);
				player.skillingStatistics[SkillingStatistics.BONES_ON_ALTAR]++;
				SoundSystem.sendSound(player, 442, 650);
		}

		public static void prayerInterfaceAction(Player player, int amount)
		{
				player.getPA().closeInterfaces();
				player.skillingData[1] = amount;
				if (amount == 100)
				{
						player.setAmountInterface(ServerConstants.SKILL_NAME[ServerConstants.PRAYER]);
						player.getOutStream().createFrame(27);
						return;
				}
				boneOnAltarCycleEvent(player, player.skillingData[0]);
		}

		public static void xAmountPrayerAction(Player player, int amount)
		{
				player.skillingData[1] = amount;
				boneOnAltarCycleEvent(player, player.skillingData[0]);
		}

}