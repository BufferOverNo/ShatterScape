package game.content.skilling;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Woodcutting skill.
 * @Author MGT Madness, re-written on 22-07-2014.
 */

public class Woodcutting
{
		/**
		 * x seconds have to pass before the tree can be deleted.
		 */
		private final static int TREE_IMMUNE_TIME = 60;

		public static enum Hatchet
		{
				BRONZE(1351, 1, 879, 13),
				IRON(1349, 1, 877, 12),
				STEEL(1353, 6, 875, 11),
				BLACK(1361, 6, 873, 10),
				MITHRIL(1355, 21, 871, 8),
				ADAMANT(1357, 31, 869, 6),
				RUNE(1359, 41, 867, 4),
				DRAGON(6739, 61, 2846, 3),
				INFERNAL(18757, 61, 13708, 2);

				private int id, req, anim, timer;

				private Hatchet(int id, int level, int animation, int timer)
				{
						this.id = id;
						this.req = level;
						this.anim = animation;
						this.timer = timer;
				}

				public int getItemId()
				{
						return id;
				}

				public int getRequiredLevel()
				{
						return req;
				}

				public int getAnim()
				{
						return anim;
				}

				public int getTimer()
				{
						return timer;
				}
		}

		public static enum TreeData
		{
				//@formatter:off
				NORMAL_TREE(1, 25, 1511, 1342, 1, new int[] {1276, 1278, 1279}),
				DYING_TREE(1, 25, 1511, 3649, 1, new int[] {3648}),
				DEAD_TREE(1, 25, 1511, 1341, 1, new int[] {1284}),
				DEAD1_TREE(1, 25, 1511, 1347, 1, new int[] {1282, 1283}),
				DEAD2_TREE(1, 25, 1511, 1351, 1, new int[] {1286}),
				DEAD3_TREE(1, 25, 1511, 1352, 1, new int[] {1365}),
				DEAD4_TREE(1, 25, 1511, 1353, 1, new int[] {1289}),
				DEAD5_TREE(1, 25, 1511, 1349, 1, new int[] {1285}),
				ACHEY_TREE(1, 25, 2862, 3371, 1, new int[] {2023}),
				OAK_TREE(15, 38, 1521, 1356, 2, new int[] {1751}),
				WILLOW_TREE(30, 68, 1519, 9471, 3, new int[] {1756, 1758, 1760}),
				WILLOW_TREE1(30, 68, 1519, 9711, 3, new int[] {1750}),
				TEAK_TREE(35, 85, 6333, 9037, 3, new int[] {9036}),
				MAPLE_TREE(45, 100, 1517, 9712, 4, new int[] {1759}),
				HOLLOW_TREE(45, 82, 3239, 4061, 4, new int[] {1752}),
				MAHOGANY_TREE(50, 85, 6332, 9035, 4, new int[] {9034}),
				YEW_TREE(60, 175, 1515, 9714, 5, new int[] {1753, 1754}),
				MAGIC_TREE(75, 250, 1513, 9713, 6, new int[] {1761}),
				REDWOOD_TREE(90, 380, 19669, 28860, 7, new int[] {28859});
				//@formatter:on
				public int woodcutLevelRequirement;

				public int experienceReward;

				public int logItemId;

				public int treeStumpObjectId;

				public int treeTimer;

				public int[] treeObjectId;

				private TreeData(int woodcutLevelRequirement, int experienceReward, int logItemId, int treeStumpObjectId, int treeTimer, int[] treeObjectId)
				{
						this.woodcutLevelRequirement = woodcutLevelRequirement;
						this.experienceReward = experienceReward;
						this.logItemId = logItemId;
						this.treeStumpObjectId = treeStumpObjectId;
						this.treeObjectId = treeObjectId;
						this.treeTimer = treeTimer;

				}
		}

		/**
		 * Store tree object id, coords and time it was removed.
		 */
		public static ArrayList<String> treeRemovedList = new ArrayList<String>();

		public static boolean isWoodcuttingObject(final Player player, int objectId)
		{
				for (TreeData treeData : TreeData.values())
				{
						for (int index = 0; index < treeData.treeObjectId.length; index++)
						{
								if (treeData.treeObjectId[index] == objectId)
								{
										startWoodcutting(player, treeData.woodcutLevelRequirement, treeData.experienceReward, treeData.logItemId, treeData.treeStumpObjectId, treeData.treeTimer);
										return true;
								}
						}
				}
				return false;
		}

		/**
		 * Start the woodcutting procedure.
		 */
		public static void startWoodcutting(Player player, final int woodcutLevelRequirement, final int experienceReward, final int logItemId, final int treeStumpObjectId, final int treeTimer)
		{

				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				if (player.baseSkillLevel[ServerConstants.WOODCUTTING] < woodcutLevelRequirement)
				{
						stopWoodcutting(player);
						player.getDH().sendStatement("You need a woodcutting level of " + woodcutLevelRequirement + " to cut this tree.");
						return;
				}
				if (!hasCorrectHatchet(player))
				{
						return;
				}
				performAnimation(player);
				startWcTimerEvent(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
		}

		/**
		 * Force stop woodcutting.
		 * @param player
		 * 			The associated player.
		 */
		public static void stopWoodcutting(Player player)
		{
				if (player.woodCuttingEventTimer > 0)
				{
						player.startAnimation(65535);
				}
				player.woodCuttingEventTimer = -1;
		}

		/**
		 * Perform the woodcutting hatchet animation.
		 * @param player
		 * 			The associated player.
		 */
		public static void performAnimation(Player player)
		{
				for (Hatchet data : Hatchet.values())
				{
						if (data.getItemId() == player.hatchetUsed)
						{
								player.startAnimation(data.getAnim());
						}
				}
				SoundSystem.sendSound(player, 473, 400);
				treeExists(player);
		}

		private static void treeExists(Player player)
		{
				long timeValue = 0;
				for (int index = 0; index < treeRemovedList.size(); index++)
				{
						String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
						if (treeRemovedList.get(index).contains(match))
						{
								// Time tree removed.
								String time = treeRemovedList.get(index).replace(match + " ", "");
								timeValue = Long.parseLong(time);
								break;
						}
				}
				if (System.currentTimeMillis() - timeValue <= 7000)
				{
						player.playerAssistant.stopAllActions();
				}
		}

		/**
		* @param player
		* 			The associated player.
		* @return
		* 			True, if the player has the correct hatchet.
		*/
		public static boolean hasCorrectHatchet(Player player)
		{
				player.hatchetUsed = 0;
				for (Hatchet data : Hatchet.values())
				{
						if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) && player.baseSkillLevel[ServerConstants.WOODCUTTING] >= data.getRequiredLevel())
						{
								player.hatchetUsed = data.getItemId();
						}
				}
				if (player.hatchetUsed == 0)
				{
						for (Hatchet data : Hatchet.values())
						{
								if (ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
								{
										if (player.baseSkillLevel[ServerConstants.WOODCUTTING] < data.getRequiredLevel())
										{
												player.getPA().sendMessage("You need a woodcutting level of " + data.getRequiredLevel() + " to use this hatchet.");
												return false;
										}
								}
						}
						player.getPA().sendMessage("You do not have a hatchet.");
						return false;
				}
				return true;
		}

		/**
		 * Receive the logs.
		 * @param player
		 * 			The associated player.
		 * @param logType
		 * 			The log identity.
		 * @param experience
		 * 			Experience to gain from the log.
		 */
		public static void receiveLogFromTree(Player player, int experienceReward, final int logItemId, final int treeStumpObjectId, final int treeTimer)
		{
				// Oak tree.
				if (logItemId == TreeData.OAK_TREE.logItemId)
				{
						Achievements.checkCompletionSingle(player, 1009);
				}
				// Yew log.
				else if (logItemId == TreeData.YEW_TREE.logItemId)
				{
						Achievements.checkCompletionMultiple(player, "1040");
				}

				int chance = 0;

				if (player.isInZombiesMinigame())
				{
						chance = 0;
				}

				int amount = 1 * (Misc.hasPercentageChance(chance) ? 2 : 1);
				if (!ItemAssistant.addItem(player, logItemId, amount))
				{
						player.playerAssistant.sendMessage("You have run out of inventory space.");
						return;
				}
				Skilling.addHarvestedResource(player, logItemId, amount);
				performAnimation(player);
				player.skillingStatistics[SkillingStatistics.LOGS_GAINED]++;
				player.playerAssistant.sendFilterableMessage("You get some logs.");
				Skilling.addSkillExperience(player, experienceReward, ServerConstants.WOODCUTTING);
				startWcTimerEvent(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
				createTreeStump(player, treeStumpObjectId);
		}


		private static int setWoodcuttingTimer(Player player, int treeTimer)
		{
				int timer = 0;

				for (Hatchet data : Hatchet.values())
				{
						if (player.hatchetUsed == data.getItemId())
						{
								timer = data.getTimer();
						}
				}
				timer += treeTimer;

				int baseMinimum = 1;
				timer = Misc.random(baseMinimum, (int) ((17 + baseMinimum) - (player.baseSkillLevel[ServerConstants.WOODCUTTING] * 0.17)) + timer);
				return timer;
		}

		/**
		 * Decrease the wcTimer variable untill it reaches 0.
		 */
		private static void startWcTimerEvent(final Player player, final int experienceReward, final int logItemId, final int treeStumpObjectId, final int treeTimer)
		{

				/* Check if this event is being used, if it is, then stop */
				if (player.isUsingWcTimerEvent)
				{
						return;
				}
				player.isUsingWcTimerEvent = true;
				player.woodCuttingEventTimer = setWoodcuttingTimer(player, treeTimer);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.woodCuttingEventTimer > 0)
								{
										player.woodCuttingEventTimer--;
										performAnimation(player);
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.isUsingWcTimerEvent = false;
								player.startAnimation(65535);
								if (player.woodCuttingEventTimer == 0)
								{
										receiveLogFromTree(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
								}
						}
				}, 1);

		}

		private static void createTreeStump(Player player, final int treeStumpObjectId)
		{
				boolean normalTree = false;
				for (int index = 0; index < TreeData.NORMAL_TREE.treeObjectId.length; index++)
				{
						if (TreeData.NORMAL_TREE.treeObjectId[index] == player.getObjectId())
						{
								normalTree = true;
								break;
						}
				}
				if (!Misc.hasOneOutOf(20) && !normalTree)
				{
						return;
				}

				long timeValue = 0;
				int listIndex = -1;

				for (int index = 0; index < treeRemovedList.size(); index++)
				{
						String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
						if (treeRemovedList.get(index).contains(match))
						{
								// Time tree removed.
								String time = treeRemovedList.get(index).replace(match + " ", "");
								timeValue = Long.parseLong(time);
								listIndex = index;
								break;
						}
				}

				if (System.currentTimeMillis() - timeValue <= (TREE_IMMUNE_TIME * 1000) && !normalTree)
				{
						return;
				}

				if (listIndex >= 0)
				{
						treeRemovedList.remove(listIndex);
				}
				SoundSystem.sendSound(player, 1312, 0);
				treeRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
				new Object(treeStumpObjectId, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), 12);
				player.playerAssistant.stopAllActions();
		}
}