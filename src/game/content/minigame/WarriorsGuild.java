package game.content.minigame;

import core.Server;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Warrior's Guild minigame.
 * @author MGT Madness, created on 18-02-2016.
 */
public class WarriorsGuild
{

		/**
		 * Helm, Body, Leg, Npc.
		 */
		public static final int[][] ARMOUR_DATA = {
				// Bronze.
				{1075, 1117, 1155, 4278},
				// Iron.
				{1067, 1115, 1153, 4279},
				// Steel.
				{1069, 1119, 1157, 4280},
				// Black.
				{1077, 1125, 1165, 4281},
				// Mithril.
				{1071, 1121, 1159, 4282},
				// Adamant.
				{1073, 1123, 1161, 4283},
				// Rune.
				{1079, 1127, 1163, 4284}};

		private static final int[] TOKEN_REWARDS = {5, 10, 15, 20, 25, 30, 40};

		private static final int[] DEFENDERS = new int[] {8844, 8845, 8846, 8847, 8848, 8849, 8850, 18759};

		/**
		 * Drop the tokens depending on the npc.
		 */
		public static void dropAnimatedTokens(Player player, Npc npc)
		{
				for (int index = 0; index < ARMOUR_DATA.length; index++)
				{
						if (ARMOUR_DATA[index][3] == npc.npcType)
						{
								// Remove head icon from npc killed.
								player.getPA().drawHeadicon(0, npc.npcIndex, 0, 0);
								player.summonedAnimator = false;

								int chance = 0;

								Server.itemHandler.createGroundItem(player, 8851, npc.getX(), npc.getY(), TOKEN_REWARDS[index] * (Misc.hasPercentageChance(chance) ? 2 : 1), false, 0, true, "");
								Server.itemHandler.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][0], npc.getX(), npc.getY(), 1, false, 0, true, "");
								Server.itemHandler.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][1], npc.getX(), npc.getY(), 1, false, 0, true, "");
								Server.itemHandler.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][2], npc.getX(), npc.getY(), 1, false, 0, true, "");
						}
				}
		}

		public static void dropDefender(Player player, Npc npc)
		{
				if (npc.npcType != 4292)
				{
						return;
				}
				if (!Misc.hasOneOutOf(10))
				{
						return;
				}
				int defenderIndex = -1;

				for (int index = 0; index < DEFENDERS.length; index++)
				{
						if (ItemAssistant.hasItemEquipped(player, DEFENDERS[index]))
						{
								defenderIndex = index;
						}
				}

				// If already has dragon defender, set it to rune defender, so the player can receive a dragon defender.
				if (defenderIndex == 7)
				{
						defenderIndex = 6;
				}

				defenderIndex++;

				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				Server.itemHandler.createGroundItem(player, DEFENDERS[defenderIndex], npcX, npcY, 1, false, 0, true, "");

				// If item to receive is a dragon defender.
				if (defenderIndex == 7)
				{
						player.playerAssistant.sendMessage("<col=005f00>You have received a " + ItemAssistant.getItemName(DEFENDERS[defenderIndex]) + "!");
				}
				else
				{
						player.playerAssistant.sendMessage("<col=005f00>You have received a " + ItemAssistant.getItemName(DEFENDERS[defenderIndex]) + ", equip it to receive the next one!");
				}
		}

		/**
		 * The action when the player uses a piece of armour with the animator object.
		 */
		public static boolean spawnAnimatorAction(final Player player, int objectId, int itemId)
		{
				if (objectId != 15621)
				{
						return false;
				}

				if (player.summonedAnimator)
				{
						player.playerAssistant.sendMessage("You already summoned an animator.");
						return true;
				}

				// If player has already spawned an animator, return.
				for (int i = 0; i < ARMOUR_DATA.length; i++)
				{
						for (int f = 0; f < ARMOUR_DATA[0].length; f++)
						{
								if (itemId == ARMOUR_DATA[i][f])
								{
										if (ItemAssistant.hasItemInInventory(player, ARMOUR_DATA[i][0]) && ItemAssistant.hasItemInInventory(player, ARMOUR_DATA[i][1]) && ItemAssistant.hasItemInInventory(player, ARMOUR_DATA[i][2]))
										{
												createAnimatorEvent(player, ARMOUR_DATA[i][3], ARMOUR_DATA[i][0], ARMOUR_DATA[i][1], ARMOUR_DATA[i][2]);
												player.warriorsGuildArmourIndex = i;
												return true;
										}
								}
						}
				}

				return false;
		}

		/**
		 * Spawning the animator event.
		 */
		private static void createAnimatorEvent(final Player player, final int npcId, final int item1, final int item2, final int item3)
		{
				player.summonedAnimator = true;
				player.doingActionTimer = 1;
				player.warriorsGuildEventTimer++;
				player.startAnimation(827);
				player.getDH().sendStartInfo("", "You place your armour on the platform where it", "", "dissapears....", "");
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								player.warriorsGuildEventTimer++;
								switch (player.warriorsGuildEventTimer)
								{
										case 4:
												ItemAssistant.deleteItemFromInventory(player, item1, 1);
												ItemAssistant.deleteItemFromInventory(player, item2, 1);
												ItemAssistant.deleteItemFromInventory(player, item3, 1);
												break;

										case 5:
												player.getDH().sendStartInfo("", "The animator hums, something appears to be working.", "", "You stand back...", "");
												break;

										case 6:
												Movement.travelTo(player, 0, 3);
												player.turnPlayerTo(player.getX(), 3536);
												break;

										case 7:
												NpcHandler.spawnNpc(player, npcId, player.getX(), 3536, 0, true, true);
												container.stop();
												break;

								}
						}

						@Override
						public void stop()
						{
								player.warriorsGuildEventTimer = 0;
								player.doingActionTimer = 0;
						}
				}, 1);
		}

		public static boolean canAttackCyclops(Player player, int npcId)
		{
				if (npcId != 4292)
				{
						return true;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, 8851, 10))
				{
						player.playerAssistant.sendMessage("You have run out of tokens.");
						return false;
				}
				if (!Area.inCylopsRoom(player))
				{
						return false;
				}

				if (player.usingCyclopsEvent)
				{
						return true;
				}
				startTokenDrainingEvent(player);
				return true;
		}

		public static void startTokenDrainingEvent(final Player player)
		{


				player.usingCyclopsEvent = true;
				player.warriorsGuildCyclopsTimer = 10;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								player.warriorsGuildCyclopsTimer--;

								if (Area.inCylopsRoom(player))
								{
										if (player.warriorsGuildCyclopsTimer == 0)
										{
												if (ItemAssistant.hasItemAmountInInventory(player, 8851, 10))
												{
														ItemAssistant.deleteItemFromInventory(player, 8851, 10);
														player.playerAssistant.sendMessage("10 tokens have vanished.");
														player.warriorsGuildCyclopsTimer = 10;
												}
												else
												{
														container.stop();
												}
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
								player.usingCyclopsEvent = false;
						}
				}, 10);

		}
}