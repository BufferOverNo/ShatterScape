package game.item;

import core.ServerConfiguration;
import game.content.minigame.WarriorsGuild;
import game.content.miscellaneous.CrystalChest;
import game.content.miscellaneous.WateringCan;
import game.content.miscellaneous.Web;
import game.content.skilling.Cooking;
import game.content.skilling.Farming;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.SpinningWheel;
import game.content.skilling.prayer.BoneOnAltar;
import game.content.skilling.smithing.Smithing;
import game.content.skilling.smithing.SmithingInterface;
import game.content.worldevent.BloodKey;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * @author Ryan / Lmctruck30 & Eliminate & MGT Madness.
 */

public class ItemOn
{

		public static void itemOnObject(final Player player, final int objectId, int objectX, int objectY, final int itemId)
		{
				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("[Item: " + itemId + "] on [Object: " + objectId + "][Object X: " + objectX + "][Object Y: " + objectY + "]");
				}
				if (player.farmingXCoordinate > 0 && (objectId == Farming.PATCH_CLEAN || objectId == Farming.PATCH_HERBS))
				{
						objectX = player.farmingXCoordinate;
						objectY = player.farmingYCoordinate;
						Movement.setNewPath(player, objectX, objectY);
				}
				player.turnPlayerTo(objectX, objectY);
				player.setObjectX(objectX);
				player.setObjectY(objectY);
				player.setObjectId(objectId);
				player.itemOnObjectEvent = true;
				final int objectX1 = objectX;
				final int objectY1 = objectY;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.itemOnObjectEvent)
								{
										int distance = 2;
										// Warrior's guild summoning platform.
										if (objectId == 15621 && objectX1 == 2857)
										{
												Movement.setNewPath(player, 2857, 3537);
										}
										if (!player.playerAssistant.withInDistance(objectX1, objectY1, player.getX(), player.getY(), distance))
										{
												return;
										}
										else
										{
												player.itemOnObjectEvent = false;
												container.stop();
										}

										if (Farming.checkItemOnObject(player, itemId, objectId))
										{
												return;
										}

										if (WarriorsGuild.spawnAnimatorAction(player, objectId, itemId))
										{
												return;
										}

										if (Smithing.isFurnace(player, objectId))
										{
												if (itemId != 2357)
												{
														return;
												}
												player.setActionIdUsed(7);
												JewelryCrafting.jewelryInterface(player);
												return;
										}

										if (objectId == 27277)
										{
												BloodKey.openChest(player, itemId);
												return;
										}


										switch (objectId)
										{

												case 172:
														CrystalChest.giveLoot(player, itemId);
														break;

												case 2644:
														SpinningWheel.spinningWheel(player, itemId);
														break;

												// Anvil.
												case 2031:
												case 2097:
														SmithingInterface.showSmithInterface(player, itemId);
														break;

												case 733:
														Web.slash(player, itemId);
														break;
												case 4259:
												case 3038:
												case 2732:
												case 5249: // Fire.
												case 26181:
												case 26185: // Fire
														if (itemId == 2132)
														{
																player.rawBeef = true;
																Cooking.rawBeef(player);
																return;
														}
														player.fireX = objectX1;
														player.fireY = objectY1;
														Cooking.cookThisFood(player, itemId, objectId);
														break;

												// Altar.
												case 2640:
														BoneOnAltar.useBoneOnAltar(player, itemId);
														break;
												case 874:
														WateringCan.refill(player);
														break;
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
						}
				}, 1);
		}

		public static boolean hasTwoItems(Player player, int itemUsed, int usedWith, int item1, int item2)
		{
				if (item1 == itemUsed && item2 == usedWith || item1 == usedWith && item2 == itemUsed)
				{
						return true;
				}
				return false;
		}

		public static void itemOnNpc(Player player, int itemId, int npcId, int slot)
		{
				// Cleaner at Resource wilderness area.
				Npc npc = NpcHandler.npcs[npcId];
				player.turnPlayerTo(npc.getX(), npc.getY());
				if (npc.npcType == 5916)
				{
						if (ItemDefinition.getDefinitions()[itemId].note)
						{
								player.getDH().sendStatement("You can only use unnoted items.");
								return;
						}
						int amountToNote = ItemAssistant.getItemAmount(player, itemId);
						int notedId = ItemAssistant.getNotedItem(itemId);
						if (ItemDefinition.getDefinitions()[notedId].note)
						{
								ItemAssistant.deleteItemFromInventory(player, itemId, amountToNote);
								ItemAssistant.addItemToInventory(player, notedId, amountToNote, slot, true);
								ItemAssistant.resetItems(player, 3214); // Update inventory.

								int offset = 25;
								if (ItemAssistant.getItemName(itemId).contains("bar"))
								{
										offset = 14;
								}
								player.getDH().sendItemChat1("", "The cleaner has noted for you x" + amountToNote + " " + ItemAssistant.getItemName(itemId) + ".", itemId, 200, offset, 0);
						}
						return;
				}
				switch (itemId)
				{
				}

		}


}