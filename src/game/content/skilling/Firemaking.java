package game.content.skilling;

import java.util.ArrayList;

import core.Server;
import game.content.achievement.Achievements;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;

public class Firemaking
{


		public static final int FIRE_OBJECT_ID = 5249;

		public static ArrayList<String> fireMakingSpots = new ArrayList<String>();

		private static int[][] data = {
				{1511, 1, 40}, //	LOG
				{1521, 15, 60}, //	OAK
				{1519, 30, 90},
				{1517, 45, 135},
				{1515, 60, 203}, //	YEW
				{1513, 75, 304}, //	MAGIC
		};

		/**
		 * @return
		 * 	True, if the fire exists at the given coordinates.
		 */
		public static boolean fireExists(int fireX, int fireY, int fireHeight)
		{
				for (int i = 0; i < Firemaking.fireMakingSpots.size(); i++)
				{
						if (Firemaking.fireMakingSpots.get(i).equals(fireX + " " + fireY + " " + fireHeight))
						{
								return true;
						}
				}

				return false;
		}

		public static boolean playerLogs(Player player, int i, int l)
		{
				boolean flag = false;
				for (int kl = 0; kl < data.length; kl++)
				{
						if ((i == data[kl][0] && requiredItem(player, l)) || (requiredItem(player, i) && l == data[kl][0]))
						{
								flag = true;
						}
				}
				return flag;
		}

		private static boolean requiredItem(Player player, int i)
		{
				if (i == 590)
				{
						return true;
				}
				return false;
		}

		/**
		 * Initiate default firemaking spots that are spawned by the map.
		 */
		public static void defaultFiremakingSpots()
		{
				fireMakingSpots.add("3188 3930 0");
		}

		public static boolean grabData(final Player player, final int itemUsed, final int usedWith)
		{
				if (!Firemaking.playerLogs(player, itemUsed, usedWith))
				{
						return false;
				}
				final int[] coords = new int[2];
				coords[0] = player.getX();
				coords[1] = player.getY();
				player.playerAssistant.stopAllActions();
				for (int i = 0; i < data.length; i++)
				{
						if ((requiredItem(player, itemUsed) && usedWith == data[i][0] || itemUsed == data[i][0] && requiredItem(player, usedWith)))
						{
								if (player.baseSkillLevel[11] < data[i][1])
								{
										player.getDH().sendStatement("You need the Firemaking level of at least " + data[i][1] + ".");
										return false;
								}

								if (fireExists(player.getX(), player.getY(), player.getHeight()))
								{
										player.playerAssistant.sendMessage("You may not light a fire here.");
										return false;
								}
								if (System.currentTimeMillis() - player.lastFire > 1200)
								{

										if (player.playerIsFiremaking)
										{
												return false;
										}

										final int[] time = new int[3];
										final int log = data[i][0];

										// Yew log.
										if (log == 1515)
										{
												Achievements.checkCompletionMultiple(player, "1041");
										}
										// Magic log.
										else if (log == 1513)
										{
												Achievements.checkCompletionMultiple(player, "1064");
										}
										if (System.currentTimeMillis() - player.lastFire > 3000)
										{
												player.startAnimation(733);
												SoundSystem.sendSound(player, 811, 400);
												time[0] = 4;
												time[1] = 3;
										}
										else
										{
												time[0] = 1;
												time[1] = 2;
										}

										player.playerIsFiremaking = true;
										player.woodCuttingEventTimer = -1;
										Server.itemHandler.createGroundItem(player, log, coords[0], coords[1], 1, false, 0, true, "");

										int FIRE_TIMER = 100;
										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														new Object(FIRE_OBJECT_ID, coords[0], coords[1], player.getHeight(), 0, 10, -1, FIRE_TIMER);
														Server.itemHandler.removeGroundItem(player, log, coords[0], coords[1], false);
														player.playerIsFiremaking = false;
														fireMakingSpots.add(coords[0] + " " + coords[1] + " " + player.getHeight());
														container.stop();
												}

												@Override
												public void stop()
												{

												}
										}, time[0]);

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
														Server.itemHandler.createGroundItem(player, 592, coords[0], coords[1], 1, true, 20, true, "");
												}
										}, FIRE_TIMER + time[0]);

										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														player.startAnimation(65535);
														container.stop();
												}

												@Override
												public void stop()
												{

												}
										}, time[1]);


										Movement.movePlayerFromUnderEntity(player);
										player.getPA().sendFilterableMessage("The fire catches and the logs begin to burn.");
										Skilling.addSkillExperience(player, data[i][2], 11);
										player.skillingStatistics[SkillingStatistics.LOGS_BURNT]++;
										player.turnPlayerTo(player.getX() + 1, player.getY());
										ItemAssistant.deleteItemFromInventory(player, data[i][0], ItemAssistant.getItemSlot(player, data[i][0]), 1);
										player.lastFire = System.currentTimeMillis();
										return true;
								}
						}
				}
				return false;
		}

		public static void deleteFire(Object object)
		{
				if (object.objectId != Firemaking.FIRE_OBJECT_ID)
				{
						return;
				}
				for (int i = 0; i < Firemaking.fireMakingSpots.size(); i++)
				{
						if (Firemaking.fireMakingSpots.get(i).equals(object.objectX + " " + object.objectY + " " + object.height))
						{
								Firemaking.fireMakingSpots.remove(Firemaking.fireMakingSpots.get(i));
								break;
						}
				}

		}
}