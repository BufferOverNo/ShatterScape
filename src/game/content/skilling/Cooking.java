package game.content.skilling;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class Cooking
{

		public static void cookThisFood(Player p, int i, int object)
		{
				switch (i)
				{

						// Raw shrimp
						case 317:
								cookFish(p, i, 30, 1, 323, 315, object);
								break;

						// Raw chicken.
						case 2138:
								cookFish(p, i, 30, 1, 2144, 2140, object);
								break;

						// Raw tuna
						case 359:
								cookFish(p, i, 100, 30, 367, 361, object);
								break;

						// Raw swordfish
						case 371:
								cookFish(p, i, 140, 45, 375, 373, object);
								break;

						// Raw trout
						case 335:
								cookFish(p, i, 70, 15, 343, 333, object);
								break;

						// Raw Salmon
						case 331:
								cookFish(p, i, 90, 25, 343, 329, object);
								break;

						// Raw lobster
						case 377:
								cookFish(p, i, 120, 40, i + 4, i + 2, object);
								break;

						// Raw monkfish
						case 7944:
								cookFish(p, i, 150, 62, i + 4, i + 2, object);
								break;

						// Raw shark
						case 383:
								cookFish(p, i, 210, 80, i + 4, i + 2, object);
								break;

						// Raw dark crab.
						case 18635:
								cookFish(p, i, 250, 90, i + 4, i + 2, object);
								break;
						default:
								p.playerAssistant.sendMessage("You cannot cook this.");
								break;
				}
		}

		public static void cookFish(Player player, int itemID, int xpRecieved, int levelRequired, int burntFish, int cookedFish, int object)
		{
				if (!Skilling.hasRequiredLevel(player, 7, levelRequired, "cooking", "cook this"))
				{
						return;
				}
				player.playerSkillProp[7][0] = itemID;
				player.playerSkillProp[7][1] = xpRecieved;
				player.playerSkillProp[7][2] = levelRequired;
				player.playerSkillProp[7][3] = burntFish;
				player.playerSkillProp[7][4] = cookedFish;
				player.playerSkillProp[7][5] = object;
				int item = ItemAssistant.getItemAmount(player, player.playerSkillProp[7][0]);
				if (item == 1)
				{
						player.doAmount = 1;
						cookTheFish(player);
						return;
				}
				viewCookInterface(player, itemID);
		}

		public static void getAmount(Player player, int amount)
		{
				int item = ItemAssistant.getItemAmount(player, player.playerSkillProp[7][0]);
				if (amount > item)
				{
						amount = item;
				}
				player.doAmount = amount;
				cookTheFish(player);
		}

		public static void resetCooking(Player c)
		{
				c.isCookingEvent = false;
				for (int i = 0; i < 6; i++)
				{
						c.playerSkillProp[7][i] = -1;
				}
		}

		private static void viewCookInterface(Player player, int item)
		{
				player.getPA().sendFrame164(1743);
				player.getPA().sendFrame246(13716, 190, item);
				player.getPA().sendFrame126("\\n\\n\\n\\n\\n" + ItemAssistant.getItemName(item) + "", 13717);
		}

		private static void cookTheFish(final Player player)
		{
				if (player.isCookingEvent)
				{
						return;
				}
				player.isCookingEvent = true;
				player.getPA().closeInterfaces();
				if (player.playerSkillProp[7][5] > 0)
				{
						player.startAnimation(player.getObjectId() == Firemaking.FIRE_OBJECT_ID ? 897 : 896);
						SoundSystem.sendSound(player, 357, 0);
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.isCookingEvent)
								{
										if (player.getObjectId() == Firemaking.FIRE_OBJECT_ID)
										{
												if (!Firemaking.fireExists(player.fireX, player.fireY, player.getHeight()))
												{
														player.isCookingEvent = false;
														player.playerAssistant.sendMessage("The fire has run out.");
														container.stop();
														return;
												}
										}
										if (player.playerSkillProp[7][0] == Fishing.RAW_SHARK)
										{
												Achievements.checkCompletionMultiple(player, "1037");
										}
										ItemAssistant.deleteItemFromInventory(player, player.playerSkillProp[7][0], ItemAssistant.getItemSlot(player, player.playerSkillProp[7][0]), 1);
										double burnPercentage = (double) player.baseSkillLevel[ServerConstants.COOKING] - (double) player.playerSkillProp[7][2];
										burnPercentage = 30.0 - burnPercentage;
										if (burnPercentage > 20.0 && player.baseSkillLevel[ServerConstants.COOKING] >= 80)
										{
												burnPercentage = 20.0;
										}
										// Cooking gauntlets.
										if (ItemAssistant.hasItemEquipped(player, 775))
										{
												burnPercentage *= 0.5;
										}
										if (burnPercentage <= 0)
										{
												burnPercentage = 0;
										}
										if (Skilling.hasMasterCapeWorn(player, 9801))
										{
												burnPercentage = 0;
										}
										if (!Misc.hasPercentageChance((int) burnPercentage))
										{
												// Shrimp.
												if (player.playerSkillProp[7][4] == 315)
												{
														Achievements.checkCompletionSingle(player, 1022);
												}
												player.playerAssistant.sendFilterableMessage("You successfully cook the " + ItemAssistant.getItemName(player.playerSkillProp[7][0]).toLowerCase() + ".");
												Skilling.addSkillExperience(player, (int) (player.playerSkillProp[7][1] * 0.95), 7);
												ItemAssistant.addItem(player, player.playerSkillProp[7][4], 1);
												Skilling.addHarvestedResource(player, player.playerSkillProp[7][4], 1);
												player.skillingStatistics[SkillingStatistics.FISH_COOKED]++;
										}
										else
										{
												player.playerAssistant.sendFilterableMessage("You burn the " + ItemAssistant.getItemName(player.playerSkillProp[7][0]).toLowerCase() + ".");
												ItemAssistant.addItem(player, player.playerSkillProp[7][3], 1);
										}
										Skilling.deleteTime(player);
								}
								if (!ItemAssistant.hasItemAmountInInventory(player, player.playerSkillProp[7][0], 1) || player.doAmount <= 0)
								{
										container.stop();
								}
								if (!player.isCookingEvent)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								resetCooking(player);
						}
				}, 2);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{ //

								if (player.playerSkillProp[7][5] > 0 && player.isCookingEvent)
								{
										player.startAnimation(player.getObjectId() == Firemaking.FIRE_OBJECT_ID ? 897 : 896);
										SoundSystem.sendSound(player, 357, 0);
								}
								else if (!player.isCookingEvent)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{

						}
				}, 4);
		}

		public static void rawBeef(Player player)
		{
				player.getPA().sendMessage(":packet:senditemchat 8870 0 -12");
				player.getPA().sendMessage(":packet:senditemchat 8869 0 -10");
				player.getPA().sendFrame246(8869, 190, 2142);
				player.getPA().sendFrame246(8870, 210, 9436);
				player.getPA().sendFrame126("" + ItemAssistant.getItemName(2142) + "", 8874);
				player.getPA().sendFrame126("" + ItemAssistant.getItemName(9436) + "", 8878);
				player.getPA().sendFrame164(8866);

		}

		public static void cookSinew(final Player player)
		{
				if (Skilling.canActivateNewSkillingEvent(player, "SINEW"))
				{
						return;
				}
				player.getPA().closeInterfaces();
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{

								if (player.getObjectId() == Firemaking.FIRE_OBJECT_ID)
								{
										if (!Firemaking.fireExists(player.fireX, player.fireY, player.getHeight()))
										{
												player.isCookingEvent = false;
												player.playerAssistant.sendMessage("The fire has run out.");
												container.stop();
												return;
										}
								}
								if (!Skilling.isSkillingEventActive(player, "SINEW"))
								{
										container.stop();
										return;
								}
								if (!ItemAssistant.hasItemAmountInInventory(player, 2132, 1))
								{
										player.getDH().sendStatement("You have run out of " + ItemAssistant.getItemName(2132) + ".");
										container.stop();
										return;
								}
								ItemAssistant.deleteItemFromInventory(player, 2132, 1);
								ItemAssistant.addItem(player, 9436, 1);
								Skilling.addSkillExperience(player, 3, ServerConstants.COOKING);
								SoundSystem.sendSound(player, 357, 0);
								player.startAnimation(player.getObjectId() == Firemaking.FIRE_OBJECT_ID ? 897 : 896);
						}

						@Override
						public void stop()
						{
								player.getPA().stopAllActions();
						}
				}, 2);
		}
}