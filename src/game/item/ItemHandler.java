package game.item;

import java.util.ArrayList;
import java.util.List;

import core.ServerConfiguration;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.LootNotification;
import game.content.starter.GameMode;
import game.log.GameTickLog;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Handles ground items
 **/

public class ItemHandler
{

		public List<GroundItem> items = new ArrayList<GroundItem>();

		public static final int APPEAR_FOR_MYSELF_ONLY_TICKS = 100; // 60 seconds.

		public static final int APPEAR_FOR_EVERYONE_TICKS = 200; // 120 seconds.

		public ItemHandler()
		{
		}

		/**
		 * Adds item to list
		 **/
		public void addItem(GroundItem item)
		{
				items.add(item);
		}

		/**
		 * Removes item from list
		 **/
		public void removeItem(GroundItem item)
		{
				items.remove(item);
		}

		/**
		 * Item amount
		 **/
		public int itemAmount(int itemId, int itemX, int itemY)
		{
				for (GroundItem i : items)
				{
						if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY)
						{
								return i.getItemAmount();
						}
				}
				return 0;
		}


		/**
		 * Item exists
		 **/
		public boolean itemExists(int itemId, int itemX, int itemY)
		{
				for (GroundItem i : items)
				{
						if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY)
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Reloads any items if you enter a new region
		 **/
		public void reloadItems(Player player)
		{
				if (player != null)
				{
						player.timeReloadedItems = System.currentTimeMillis();
				}
				// Remove items first, so it doesn't show 2 whips instead of 1 whip.
				// It happens when i walk out of the region, then walk back in.

				/*
				for (GroundItem i : items)
				{
						if (player != null)
						{
								if (player.playerAssistant.distanceToPoint(i.getItemX(), i.getItemY()) <= 60 && player.getHeight() == i.getItemHeight())
								{
										if (i.hideTicks > 0 && i.getOwnerName().equalsIgnoreCase(player.getPlayerName()))
										{
												ItemAssistant.removeGroundItem(player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										}
										else if (i.hideTicks == 0)
										{
												ItemAssistant.removeGroundItem(player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										}
								}
						}
				}
				*/
				player.getPA().sendMessage(":packet:grounditemsremove");

				// So after removing the items, add them.
				for (GroundItem i : items)
				{
						if (player != null)
						{

								if (player.playerAssistant.distanceToPoint(i.getItemX(), i.getItemY()) <= 60 && player.getHeight() == i.getItemHeight())
								{
										if (i.hideTicks > 0 && i.getOwnerName().equalsIgnoreCase(player.getPlayerName()))
										{
												ItemAssistant.createGroundItem(player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										}
										else if (i.hideTicks == 0)
										{
												ItemAssistant.createGroundItem(player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										}
								}
						}
				}
		}

		public void itemGameTick()
		{
				long time = System.currentTimeMillis();
				ArrayList<GroundItem> toRemove = new ArrayList<GroundItem>();
				for (int j = 0; j < items.size(); j++)
				{
						if (items.get(j) != null)
						{
								GroundItem i = items.get(j);
								if (i.hideTicks > 0)
								{
										i.hideTicks--;
								}
								if (i.hideTicks == 1)
								{ // item can now be seen by others
										i.hideTicks = 0;
										createGlobalItem(i);
										if (i.removeTicks == 0)
										{
												i.removeTicks = APPEAR_FOR_EVERYONE_TICKS;
										}
								}
								if (i.removeTicks > 0)
								{
										i.removeTicks--;
								}
								if (i.removeTicks == 1)
								{
										i.removeTicks = 0;
										toRemove.add(i);
								}

						}

				}

				for (int j = 0; j < toRemove.size(); j++)
				{
						GroundItem i = toRemove.get(j);
						removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
				}
				GameTickLog.itemTickDuration = System.currentTimeMillis() - time;
		}


		/**
		 * Create a ground item.
		 * @param appearInstantly
								 *		 	True, to make the item appear instantly to everyone.
		 * @param appearForEveryoneCustomTimer
								 * 		Used to make the Firemaking ash appear for only 20 ticks instead of 100.
		 * @param originalOwnerNameSource TODO
		 */
		public void createGroundItem(Player player, int itemId, int itemX, int itemY, int itemAmount, boolean appearInstantly, int appearForEveryoneCustomTimer, boolean alertPlayer, String originalOwnerNameSource)
		{
				if (player == null)
				{
						return;
				}
				if (itemId > 0)
				{
						if (alertPlayer)
						{
								LootNotification.loot(player, itemId, itemAmount);
						}
						if (itemId > 4705 && itemId < 4760)
						{
								for (int j = 0; j < BarrowsRepair.brokenBarrows.length; j++)
								{
										if (BarrowsRepair.brokenBarrows[j][0] == itemId)
										{
												itemId = BarrowsRepair.brokenBarrows[j][1];
												break;
										}
								}
						}

						if (!ItemDefinition.getDefinitions()[itemId].stackable && itemAmount > 0)
						{
								for (int j = 0; j < itemAmount; j++)
								{
										ItemAssistant.createGroundItem(player, itemId, itemX, itemY, 1);
										if (appearInstantly)
										{
												GroundItem item = new GroundItem(itemId, itemX, itemY, 1, 2, player.getPlayerName(), player.gameMode, player.getHeight(), appearForEveryoneCustomTimer, originalOwnerNameSource);
												addItem(item);
										}
										else
										{
												GroundItem item = new GroundItem(itemId, itemX, itemY, 1, player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE ? Integer.MAX_VALUE : APPEAR_FOR_MYSELF_ONLY_TICKS, player.getPlayerName(), player.gameMode, player.getHeight(), appearForEveryoneCustomTimer, originalOwnerNameSource);
												addItem(item); // If the above error happens again, have a Misc.print for player.getPlayerName and PlayerHandler.players[playerId].getPlayerName
										}
								}
						}
						else
						{
								ItemAssistant.createGroundItem(player, itemId, itemX, itemY, itemAmount);
								if (appearInstantly)
								{
										GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount, 2, player.getPlayerName(), player.gameMode, player.getHeight(), appearForEveryoneCustomTimer, originalOwnerNameSource);
										addItem(item);
								}
								else
								{
										GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount, player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE ? Integer.MAX_VALUE : APPEAR_FOR_MYSELF_ONLY_TICKS, player.getPlayerName(), player.gameMode, player.getHeight(), appearForEveryoneCustomTimer, originalOwnerNameSource);
										addItem(item);
								}
						}
				}
		}


		/**
		 * Shows items for everyone who is within 60 squares
		 **/
		public void createGlobalItem(GroundItem i)
		{
				for (Player p : PlayerHandler.players)
				{
						if (p != null)
						{
								Player person = p;
								if (person != null)
								{
										if (person.getPlayerName().equals(i.getOwnerName()))
										{
												continue;
										}
										if (person.playerAssistant.distanceToPoint(i.getItemX(), i.getItemY()) <= 60 && person.getHeight() == i.getItemHeight())
										{
												ItemAssistant.createGroundItem(person, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										}
								}
						}
				}
		}



		/**
		 * Removing the ground item
		 **/

		public void removeGroundItem(Player player, int itemId, int itemX, int itemY, boolean add)
		{
				for (GroundItem i : items)
				{
						if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY && player.getHeight() == i.getItemHeight())
						{
								if (i.getGameMode().equals("IRON MAN"))
								{
										if (!GameMode.getGameMode(player, "IRON MAN"))
										{
												player.getPA().sendMessage("This item belongs to an Iron Man.");
												continue;
										}
										else if (!player.getPlayerName().equals(i.getOwnerName()))
										{
												player.getPA().sendMessage("This item belongs to another Iron Man.");
												continue;
										}
								}
								if (GameMode.getGameMode(player, "IRON MAN") && !i.getOwnerName().equals(player.getPlayerName()))
								{
										player.getPA().sendMessage("This item belongs to another player.");
										continue;
								}
								if (i.getOwnerName().equalsIgnoreCase("mgt madness") || i.getOwnerName().equalsIgnoreCase("ronald") || i.getOwnerName().equalsIgnoreCase("connor"))
								{
										if (!player.isAdministratorRank() && !ServerConfiguration.DEBUG_MODE)
										{
												player.getPA().sendMessage("This item belongs to an Administrator.");
												continue;
										}
								}
								if (i.hideTicks > 0 && i.getOwnerName().equalsIgnoreCase(player.getPlayerName()))
								{
										if (add)
										{
												if (ItemAssistant.addItem(player, i.getItemId(), i.getItemAmount()))
												{
														ItemTransferLog.pickUpItem(player, player.getPlayerName(), i.getItemId(), i.getItemAmount(), i.getOriginalOwnerName());
														ItemAssistant.pickUpSingularUntradeableItem(player, i.getItemId());
														player.timePickedUpItem = System.currentTimeMillis();
														player.lastItemIdPickedUp = i.getItemId();
														removeControllersItem(i, player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
														break;
												}
										}
										else
										{
												removeControllersItem(i, player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
												break;
										}
										removeControllersItem(i, player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										break;
								}
								else if (i.hideTicks <= 0)
								{
										if (add)
										{
												if (ItemAssistant.addItem(player, i.getItemId(), i.getItemAmount()))
												{
														ItemTransferLog.pickUpItem(player, player.getPlayerName(), i.getItemId(), i.getItemAmount(), i.getOriginalOwnerName());
														ItemAssistant.pickUpSingularUntradeableItem(player, i.getItemId());
														player.timePickedUpItem = System.currentTimeMillis();
														player.lastItemIdPickedUp = i.getItemId();
														removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
														break;
												}
										}
										else
										{
												removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
												break;
										}
								}
						}
				}
		}

		/**
		 * Remove item for just the item controller (item not global yet)
		 **/

		public void removeControllersItem(GroundItem i, Player player, int itemId, int itemX, int itemY, int itemAmount)
		{
				ItemAssistant.removeGroundItem(player, itemId, itemX, itemY, itemAmount);
				removeItem(i);
		}

		/**
		 * Remove item for everyone within 60 squares
		 **/

		public void removeGlobalItem(GroundItem i, int itemId, int itemX, int itemY, int itemAmount)
		{
				for (Player p : PlayerHandler.players)
				{
						if (p != null)
						{
								Player person = p;
								if (person != null)
								{
										if (person.playerAssistant.distanceToPoint(itemX, itemY) <= 60)
										{
												ItemAssistant.removeGroundItem(person, itemId, itemX, itemY, itemAmount);
										}
								}
						}
				}
				removeItem(i);
		}

}