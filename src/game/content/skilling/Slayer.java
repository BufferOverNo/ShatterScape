package game.content.skilling;

import core.ServerConstants;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Slayer skill.
 * @author MGT Madness, created on 02-01-2015.
 */
public class Slayer
{

		/**
		 * Slayer tasks. NPC, Least slayer level to access task, Highest slayer level to access task, 1 if required slayer lvl to attack.
		 */
		public static int[][] slayerTasks = {{103, 1, 20, 0
						// Ghost.
						}, {117, 1, 30, 0
						// Hill giant.
						}, {92, 1, 20, 0
						// Skeleton.
						}, {78, 1, 20, 0
						// Giant bat.
						}, {1265, 1, 35, 0
						// Rock crab.
						}, {76, 1, 20, 0
						// Zombie.
						}, {1648, 5, 15, 1
						// Crawling hand.
						}, {1600, 10, 20, 1
						// Cave crawler.
						}, {1612, 15, 30, 1
						// Banshee.
						}, {1632, 20, 30, 1
						// Rockslug.
						}, {127, 25, 35, 0
						// Magic axe.
						}, {1621, 25, 35, 1
						//Cockatrice.
						}, {134, 30, 40, 0
						// Poison spider.
						}, {1593, 30, 40, 0
						// Wild dog.
						}, {1633, 30, 40, 1
						// Pyrefriend.
						}, {112, 35, 50, 0
						// Moss giant.
						}, {1583, 40, 65, 0
						// Fire giant.
						}, {82, 40, 70, 0
						// Lesser demon.
						}, {941, 40, 70, 0
						// Green dragon.
						}, {1616, 40, 55, 1
						// Basilik.
						}, {1643, 45, 65, 1
						// Inferal mage.
						}, {1618, 50, 99, 1
						// Bloodveld.
						}, {2455, 50, 70, 0
						// Dagannoth.
						}, {55, 50, 80, 0
						// Blue dragon.
						}, {1640, 52, 65, 1
						// Jelly.
						}, {1627, 55, 70, 1
						// Turoth.
						}, {84, 60, 99, 0
						// Black demon.
						}, {49, 60, 99, 0
						// Hellhound.
						}, {1624, 65, 90, 1
						// Dust devil.
						}, {2607, 70, 99, 0
						// TzHaar-Xil
						}, {1609, 70, 85, 1
						// Kurask.
						}, {1610, 75, 90, 1
						// Gargoyle.
						}, {1590, 80, 99, 0
						// Bronze dragon.
						}, {1591, 80, 99, 0
						// Iron Dragon.
						}, {1592, 80, 99, 0
						// Steel dragon.
						}, {53, 80, 99, 0
						// Red dragon.
						}, {1613, 80, 99, 1
						// Nechryael.
						}, {1615, 85, 99, 1
						// Abyssal demon.
						}, {2783, 90, 99, 1
						// Dark beast.
						},};


		/**
		 * Give the player a Slayer task.
		 * @param player
		 * 			The associated player.
		 * @param forceGiveTask
		 * 			True, to give player another task, ignoring if the player already has a task.
		 */
		public static void giveTask(Player player, boolean forceGiveTask)
		{
				if (player.slayerTaskNpcAmount != 0 && !forceGiveTask)
				{
						player.setNpcType(1597);
						player.getDH().sendDialogues(184);
						return;
				}
				int random = Misc.random((slayerTasks.length - 1));
				if (player.baseSkillLevel[ServerConstants.SLAYER] >= slayerTasks[random][1] && player.baseSkillLevel[ServerConstants.SLAYER] <= slayerTasks[random][2])
				{
						player.slayerTaskNpcType = slayerTasks[random][0];
						player.slayerTaskNpcAmount = Misc.random(10) + (player.baseSkillLevel[ServerConstants.SLAYER] / 3) + ((GameMode.getGameMode(player, "GLADIATOR") || GameMode.getGameMode(player, "IRON MAN")) ? 30 : 10);
						player.setNpcType(1597);
						player.getDH().sendDialogues(12);
				}
				else
				{
						giveTask(player, forceGiveTask);
				}
		}

		public static void slayerTaskNPCKilled(int playerID, int npcType, int npcHP)
		{
				Player player = PlayerHandler.players[playerID];
				if (player == null)
				{
						return;
				}

				if (npcType >= 8349 && npcType <= 8351)
				{
						npcType = 8349;
				}

				// If killing Cerberus and slayer task is hellhounds.
				if (npcType == 4045 && player.slayerTaskNpcType == 49)
				{
						npcType = 49;
				}

				// Skeletons.
				if (npcType == 90 || npcType == 91)
				{
						npcType = 92;
				}

				// Ghosts.
				else if (npcType == 104)
				{
						npcType = 103;
				}

				// Baby red dragon to red dragon.
				else if (npcType == 1589)
				{
						npcType = 53;
				}

				// Baby blue dragon to blue dragon.
				else if (npcType == 52)
				{
						npcType = 55;
				}
				if (player.slayerTaskNpcType == npcType)
				{
						player.slayerTaskNpcAmount--;
						Skilling.addSkillExperience(player, npcHP, ServerConstants.SLAYER);
						if (player.slayerTaskNpcAmount <= 0)
						{
								player.slayerTaskNpcAmount = 0;
								player.slayerTaskNpcType = -1;
								player.skillingStatistics[SkillingStatistics.SLAYER_TASKS]++;
								int points = player.baseSkillLevel[ServerConstants.SLAYER] * 20;
								player.playerAssistant.sendMessage("You have completed your slayer assignment and receive x" + points + " blood money.");
								ItemAssistant.addItemToInventoryOrDrop(player, 18644, points);
								CoinEconomyTracker.incomeList.add("SKILLING " + points);
						}
				}
		}

		public final static int[][] CURRENT_BOSS_USED_DATA = {
		//@formatter:off
		{
			4040, 18641
		},
		{
			6203, 12652
		},
		{
			6247, 12651
		},
		{
			6222, 12649
		},
		{
			6260, 12650
		},
		{
			3200, 11995
		},
		{
			4043, 18761 // Venenatis.
		},
		{
			8349, 14010 // Tormented demon.
		},
		{
			9463, 14012 // Ice Strykewyrm.
		},
		{
			2881, 12643 // Dagannoths.
		},
		{
			2882, 12644
		},
		{
			2883, 12646
		},
		//@formatter:on
		};

		public static void giveBossTask(Player player)
		{
				player.getPA().closeInterfaces();
				if (player.baseSkillLevel[ServerConstants.SLAYER] < 90)
				{
						player.getPA().sendMessage("You need 90 slayer to be assigned a boss task.");
						return;
				}

				// You already have a task.
				if (player.slayerTaskNpcAmount != 0)
				{
						player.setNpcType(1597);
						player.getDH().sendDialogues(184);
						return;
				}
				int random = Misc.random((CURRENT_BOSS_USED_DATA.length - 1));
				player.slayerTaskNpcType = CURRENT_BOSS_USED_DATA[random][0];
				player.slayerTaskNpcAmount = Misc.random(14, 22);
				player.setNpcType(1597);
				player.getDH().sendDialogues(12);
		}

		public static void resetTask(Player player)
		{
				if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 18644, 1000))
				{
						player.getPA().sendMessage("You need 1,000 blood money to reset a task.");
						return;
				}

				if (player.slayerTaskNpcType <= 0)
				{
						player.getPA().sendMessage("You do not have any assignment.");
						return;
				}
				player.getPA().closeInterfaces();
				player.getPA().sendMessage("Your slayer assignment has been reset for 1,000 blood money.");
				player.slayerTaskNpcType = 0;
				player.slayerTaskNpcAmount = 0;

		}

}