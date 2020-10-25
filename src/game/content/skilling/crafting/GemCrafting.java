package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Crafting skill.
 * @author MGT Madness, created on 29-01-2015.
 */
public class GemCrafting
{

		/**
		 * UN-CUT ID, CUT-ID, ANIMATION, LEVEL REQUIRED, EXPERIENCE
		 */
		public static int[][] gemData = {
				{1623, 1607, 888, 1, 5},
				{1621, 1605, 889, 27, 10},
				{1619, 1603, 887, 34, 15},
				{1617, 1601, 890, 43, 20},
				// Uncut dragonstone.
				{1631, 1615, 892, 55, 25},
				// Uncut onyx.
				{6571, 6573, 890, 67, 168}};

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemUsedID
		 * 			The item identity used on the chisel
		 * @return
		 * 			True, if the item identity used on the chisel is a gem.
		 */
		public static boolean isAGem(Player player, int itemUsedID)
		{
				for (int i = 0; i < gemData.length; i++)
				{
						if (itemUsedID == gemData[i][0])
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
		 * 			The item identityy used on the chisel.
		 * @return
		 * 			True, if the player has a gem in his inventory.
		 */
		private static boolean hasGem(Player player, int itemUsedID)
		{
				return ItemAssistant.hasItemInInventory(player, itemUsedID) ? true : false;
		}

		/**
		 * Start the crafting system.
		 * @param player
		 * 			The associated player.
		 * @param itemUsedId
		 * 			The item identity used on the chisel.
		 */
		public static boolean useGemOnChisel(Player player, int itemUsedId, int itemUsedWith)
		{
				if (itemUsedId != 1755 && itemUsedWith != 1755 && itemUsedId != 4051 && itemUsedWith != 4051)
				{
						return false;
				}
				if (itemUsedId == 1755 || itemUsedId == 4051)
				{
						itemUsedId = itemUsedWith;
				}
				if (!isAGem(player, itemUsedId))
				{
						return false;
				}
				for (int i = 0; i < gemData.length; i++)
				{
						if (gemData[i][0] == itemUsedId && player.baseSkillLevel[ServerConstants.CRAFTING] < gemData[i][3])
						{
								player.getDH().sendStatement("You need " + gemData[i][3] + " crafting to cut this gem.");
								return false;
						}
				}
				int index = 0;
				for (int i = 0; i < gemData.length; i++)
				{
						if (itemUsedId == gemData[i][0])
						{
								index = i;
						}
				}
				player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.CRAFTING];
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 150, itemUsedId, 20, 0);
				player.skillingData[0] = gemData[index][0];
				return true;
		}

		public static void craftingInterfaceAction(Player player, int amount)
		{
				player.getPA().closeInterfaces();
				player.skillingData[3] = amount;
				if (amount == 100)
				{
						player.setAmountInterface(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING]);
						player.getOutStream().createFrame(27);
						return;
				}
				createUncutGemEvent(player, player.skillingData[0]);
		}

		public static void xAmountCraftingAction(Player player, int amount)
		{
				player.skillingData[3] = amount;
				createUncutGemEvent(player, player.skillingData[0]);
		}

		/**
		 * Cycle event of crafting.
		 * @param player
		 * 			The associated player.
		 * @param itemUsedId
		 * 			The item identity used on the chisel.
		 */
		private static void createUncutGemEvent(final Player player, final int itemUsedId)
		{
				if (player.getCrafting)
				{
						return;
				}
				player.getCrafting = true;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.getCrafting)
								{
										if (hasGem(player, itemUsedId) && player.skillingData[3] > 0)
										{
												for (int i = 0; i < gemData.length; i++)
												{
														if (itemUsedId == gemData[i][0])
														{
																successfulCraft(player, itemUsedId, i);
														}
												}
										}
										else
										{
												container.stop();
										}
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.playerAssistant.stopAllActions();
						}
				}, 1);
		}

		/**
		 * Reward the player for a successful Crafting action..
		 * @param player
		 * 			The associated player.
		 * @param itemUsedID
		 * 			The item identity used on the chisel.
		 */
		private static void successfulCraft(Player player, int itemUsedID, int i)
		{
				Skilling.addSkillExperience(player, (int) (gemData[i][4] * (GameMode.getGameMode(player, "IRON MAN") ? 2.5 : 1.30)), ServerConstants.CRAFTING);
				player.startAnimation(gemData[i][2]);
				ItemAssistant.deleteItemFromInventory(player, itemUsedID, 1);
				ItemAssistant.addItem(player, gemData[i][1], 1);
				Skilling.addHarvestedResource(player, gemData[i][1], 1);
				player.skillingData[3]--;
				player.skillingStatistics[SkillingStatistics.GEMS_CRAFTED]++;
				SoundSystem.sendSound(player, 464, 0);
		}
}