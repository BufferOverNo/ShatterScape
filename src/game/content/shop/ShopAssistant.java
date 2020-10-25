package game.content.shop;

import core.ServerConstants;
import game.content.achievement.AchievementShop;
import game.content.achievement.Achievements;
import game.content.combat.EdgeAndWestsRule;
import game.content.donator.DonationsNeeded;
import game.content.donator.DonatorTokenUse;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.TransformedOnDeathItems;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.PlayerHandler;
import network.packet.PacketHandler;
import utility.Misc;

public class ShopAssistant
{

		private Player player;

		public ShopAssistant(Player player)
		{
				this.player = player;
		}

		private static int[] SHOPS_CANNOT_BUY_FROM = {54};

		public final static double SELL_TO_SHOP_PRICE_MODIFIER = 0.7;

		private boolean buyOtherShopCurrency(int itemId, int amount)
		{
				for (ShopSpecialCurrency.Points value : ShopSpecialCurrency.Points.values())
				{
						if (player.shopId == value.getShopId())
						{
								switch (player.shopId)
								{
										case 48:
												player.setMeritPoints(getAmount(itemId, amount, player.getMeritPoints(), value.getCurrency(), 0));
												break;
										case 49:
												player.setAgilityPoints(getAmount(itemId, amount, player.getAgilityPoints(), value.getCurrency(), 0));
												break;
										case 33:

												int voteTicket = ItemAssistant.getItemAmount(player, 4067);
												int originalVoteTicket = voteTicket;
												voteTicket = getAmount(itemId, amount, originalVoteTicket, value.getCurrency(), 4067);
												if (voteTicket < originalVoteTicket)
												{
														ItemAssistant.deleteItemFromInventory(player, 4067, originalVoteTicket - voteTicket);
														ItemAssistant.resetItems(player, 3823);
												}
												break;

										case 11:
												for (int index = 0; index < player.itemsToShop.size(); index++)
												{
														String[] args = player.itemsToShop.get(index).split(" ");
														int itemBuying = Integer.parseInt(args[0]);
														int amount1 = Integer.parseInt(args[1]);
														if (itemId == itemBuying)
														{
																amount = amount1;
																int bloodMoneyRemaining = ItemAssistant.getItemAmount(player, 18644);
																int originalBloodMoney = bloodMoneyRemaining;
																bloodMoneyRemaining = getAmount(itemId, amount, originalBloodMoney, value.getCurrency(), 18644);
																if (bloodMoneyRemaining < originalBloodMoney)
																{
																		player.itemsToShop.remove(index);
																		player.getShops().openShop(11);
																		player.bloodMoneySpent = originalBloodMoney - bloodMoneyRemaining;
																		Achievements.checkCompletionMultiple(player, "1060 1124");
																		ItemAssistant.deleteItemFromInventory(player, 18644, originalBloodMoney - bloodMoneyRemaining);
																		ItemAssistant.resetItems(player, 3823);
																}
																break;
														}
												}
												break;
										case 60:
										case 46:
										case 74:
												int bloodMoney = ItemAssistant.getItemAmount(player, 18644);
												int originalBloodMoney = bloodMoney;
												bloodMoney = getAmount(itemId, amount, originalBloodMoney, value.getCurrency(), 18644);
												if (bloodMoney < originalBloodMoney)
												{
														player.bloodMoneySpent += originalBloodMoney - bloodMoney;
														Achievements.checkCompletionMultiple(player, "1060 1124");
														ItemAssistant.deleteItemFromInventory(player, 18644, originalBloodMoney - bloodMoney);
														ItemAssistant.resetItems(player, 3823);
												}
												break;
										case 71:
												int donatorTokens = 0;
												int originalDonatorTokens = ItemAssistant.getItemAmount(player, 7478);
												donatorTokens = getAmount(itemId, amount, originalDonatorTokens, value.getCurrency(), 7478);
												if (donatorTokens < originalDonatorTokens)
												{
														int spent = originalDonatorTokens - donatorTokens;
														ItemAssistant.deleteItemFromInventory(player, 7478, spent);
														ItemAssistant.resetItems(player, 3823);
														DonatorTokenUse.upgradeToNextRank(player, spent);
												}
												break;
										case 66:
												int tokkul = ItemAssistant.getItemAmount(player, 6529);
												int originalTokkul = tokkul;
												tokkul = getAmount(itemId, amount, originalTokkul, value.getCurrency(), 6529);
												if (tokkul < originalTokkul)
												{
														ItemAssistant.deleteItemFromInventory(player, 6529, originalTokkul - tokkul);
														ItemAssistant.resetItems(player, 3823);
												}
												break;
										case 73:
												player.communityEventPoints = getAmount(itemId, amount, player.communityEventPoints, value.getCurrency(), 0);
												break;
								}
								ShopSpecialCurrency.updateShopPointsTitle(player);
								return true;
						}
				}
				return false;
		}

		private final static int[] shopsIronManCanUse = {54, 67, 68, 69, 42, 43, 41, 33, 26, 12, 66, 60, 52, 53, 55, 49, 48, 70, 42, 43, 74, 11, 46, 27, 30, 61, 62, 63, 64, 65, 71};

		public void openShop(int shopId)
		{
				if (GameMode.getGameMode(player, "IRON MAN"))
				{
						boolean found = false;
						for (int index = 0; index < shopsIronManCanUse.length; index++)
						{
								if (shopId == shopsIronManCanUse[index])
								{
										found = true;
										break;
								}
						}
						if (shopId == 8)
						{
								found = true;
						}
						if (!found)
						{
								player.getPA().closeInterfaces();
								player.getPA().sendMessage("You cannot use this shop.");
								return;
						}
				}
				player.usingShop = true;
				ItemAssistant.resetItems(player, 3823);
				player.shopId = shopId;
				if (!player.doNotOpenShopInterface)
				{
						player.getPA().sendFrame248(3824, 3822);
				}
				if (!ShopSpecialCurrency.updateShopPointsTitle(player))
				{
						player.getPA().sendFrame126(ShopHandler.ShopName[shopId], 19301);
				}
				configureNormalShop(shopId);
				configureUntradeablesShop(shopId);
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
				player.doNotOpenShopInterface = false;
		}


		private void configureNormalShop(int shopId)
		{
				if (shopId == 11)
				{
						return;
				}
				int totalItems = 0;
				for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
				{
						if (ShopHandler.ShopItems[shopId][i] > 0)
						{
								totalItems++;
						}
				}
				if (totalItems > ShopHandler.MAX_ITEM_IN_SHOP)
				{
						totalItems = ShopHandler.MAX_ITEM_IN_SHOP;
				}
				if (!player.shopSearchString.isEmpty())
				{
						totalItems = 0;
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								int itemId = ShopHandler.ShopItems[shopId][i] - 1;
								if (itemId > 0)
								{
										if (ItemAssistant.getItemName(itemId).toLowerCase().contains(player.shopSearchString))
										{
												totalItems++;
												continue;
										}
								}
						}
				}

				double scrollAmount = ((double) totalItems) / 10.0;

				// Round up the double into the highest integer.
				double dAbs = Math.abs(scrollAmount);

				int a = (int) dAbs;

				double result = dAbs - (double) a;
				if (result == 0.0)
				{
				}
				else

				{
						scrollAmount = scrollAmount < 0 ? -(a + 1) : a + 1;
				}

				InterfaceAssistant.setFixedScrollMax(player, 19683, (int) (scrollAmount * 51.5));
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(3900);
				player.getOutStream().writeWord(totalItems);
				int totalCount = 0;
				// Blood money shops, with price as the amount.
				if (player.shopId == 60 || player.shopId == 46 || player.shopId == 74)
				{
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								int itemId = ShopHandler.ShopItems[shopId][i];
								if (itemId > 0)
								{
										boolean skip = false;
										if (!player.shopSearchString.isEmpty())
										{
												if (!ItemAssistant.getItemName(itemId - 1).toLowerCase().contains(player.shopSearchString))
												{
														skip = true;
												}
										}
										if (!skip)
										{
												int price = BloodMoneyPrice.getBloodMoneyPrice(itemId - 1);
												if (price > 254)
												{
														player.getOutStream().writeByte(255);
														player.getOutStream().writeDWord_v2(price);
												}
												else
												{
														player.getOutStream().writeByte(price);
												}
												if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0)
												{
														itemId = ServerConstants.MAX_ITEM_ID;
												}
												player.getOutStream().writeWordBigEndianA(itemId);
												totalCount++;
										}
								}
								if (totalCount > totalItems)
								{
										break;
								}
						}
				}
				else
				{
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								int itemId = ShopHandler.ShopItems[shopId][i];
								if (itemId > 0)
								{

										if (!player.shopSearchString.isEmpty())
										{
												if (!ItemAssistant.getItemName(itemId - 1).toLowerCase().contains(player.shopSearchString))
												{
														continue;
												}
										}
										if (ShopHandler.ShopItemsN[shopId][i] > 254)
										{
												player.getOutStream().writeByte(255);
												player.getOutStream().writeDWord_v2(ShopHandler.ShopItemsN[shopId][i]);
										}
										else
										{
												player.getOutStream().writeByte(ShopHandler.ShopItemsN[shopId][i]);
										}
										if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0)
										{
												itemId = ServerConstants.MAX_ITEM_ID;
										}
										player.getOutStream().writeWordBigEndianA(itemId);
										totalCount++;
								}
								if (totalCount > totalItems)
								{
										break;
								}
						}
				}

		}

		/**
		 * Verify if the item bought is in the shop list. Packet abuse precaution.
		 */
		public boolean itemInShop(int itemId, boolean buy)
		{
				// Untradeables shop.
				if (player.shopId == 11)
				{
						for (int index = 0; index < player.itemsToShop.size(); index++)
						{
								String[] args = player.itemsToShop.get(index).split(" ");
								int itemBuying = Integer.parseInt(args[0]);
								if (itemId == itemBuying)
								{
										return true;
								}
						}
				}
				else
				{
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								if (itemId == (ShopHandler.ShopItems[player.shopId][i] - 1))
								{
										return true;
								}
								if (ItemDefinition.getDefinitions()[itemId + 1] != null)
								{
										if (player.shopId == 54 && ItemDefinition.getDefinitions()[itemId + 1].note && itemId == (ShopHandler.ShopItems[player.shopId][i] - 2))
										{
												return true;
										}
								}
						}
				}

				// Ignore General store and buy-back untradeables shop.
				if (buy && player.shopId != 8 && player.shopId != 11)
				{
						PacketHandler.shoppingLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.shoppingLog.add("Current shop: " + player.shopId + ", item tried to buy: " + itemId);
				}
				return false;
		}

		public static int getItemShopValue(int itemId)
		{
				if (itemId <= 0)
				{
						return 0;
				}
				itemId = ItemAssistant.getUnNotedItem(itemId);
				if (ItemDefinition.getDefinitions()[itemId] == null)
				{
						return 1;
				}
				return ItemDefinition.getDefinitions()[itemId].price;
		}

		public static int getSellToShopPrice(int itemId)
		{
				if (itemId <= 0)
				{
						return 0;
				}
				if (ItemDefinition.getDefinitions()[itemId] == null)
				{
						return 1;
				}
				itemId = ItemAssistant.getUnNotedItem(itemId);
				int value = 0;
				int bloodMoneyPrice = BloodMoneyPrice.getBloodMoneyPrice(itemId);
				if (bloodMoneyPrice > 0)
				{
						value = (int) (bloodMoneyPrice * SELL_TO_SHOP_PRICE_MODIFIER);
						for (int index = 0; index < ShopHandler.standardPriceItems.length; index++)
						{
								if (ShopHandler.standardPriceItems[index] == 0)
								{
										continue;
								}
								if (ShopHandler.standardPriceItems[index] == itemId)
								{
										value = bloodMoneyPrice;
										break;
								}
						}
				}
				return value;
		}

		/**
		 * Price of non-coin shops
		 * 
		 * @param itemId
		 *        The item identity the player is buying.
		 */
		private void getOtherCurrencyPrice(int itemId)
		{
				String pointsName = "";
				for (ShopSpecialCurrency.Points data : ShopSpecialCurrency.Points.values())
				{
						if (player.shopId == data.getShopId())
						{
								pointsName = data.getCurrency();
								break;
						}
				}
				int price = 0;

				for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
				{
						if (ShopHandler.ShopItems[player.shopId][i] - 1 == itemId)
						{
								price = ShopHandler.shopPrice[player.shopId][i];
								break;
						}
				}
				if (handleImbuedRing(itemId, BloodMoneyPrice.getBloodMoneyPrice(itemId), "PRICE"))
				{
						return;
				}

				if (itemId == 18741 || itemId == 18742)
				{
						// Community event shop.
						if (player.shopId == 73)
						{
								price = 2;
						}
				}
				// Communtiy event casket.
				else if (itemId == 3849)
				{
						player.getPA().sendMessage("You might receive a rare item from this casket: H'ween masks, santa hat & 3rd age.");
				}
				if (price == 0)
				{
						price = BloodMoneyPrice.getBloodMoneyPrice(itemId);
				}
				if (player.shopId == 11)
				{
						price /= 10;
						price += 50;
				}
				if (price == 0)
				{
						player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is free.");
				}
				else
				{
						player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + ": currently costs " + Misc.formatNumber(price) + " " + pointsName + ".");
				}
				return;
		}


		public void checkShopPrice(int itemId, int removeSlot)
		{
				//Misc.print("{" + itemId + ", 50}, // " + ItemAssistant.getItemName(itemId) + ".");
				if (ItemDefinition.getDefinitions()[itemId].note)
				{
						itemId--;
				}
				for (ShopSpecialCurrency.Points value : ShopSpecialCurrency.Points.values())
				{
						if (player.shopId == value.getShopId())
						{
								getOtherCurrencyPrice(itemId);
								return;
						}
				}

				// Buy back untradeables shop
				if (player.shopId == 11)
				{
						if (BloodMoneyPrice.getBloodMoneyPrice(itemId) == 0)
						{

								player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is free.");
						}
						else
						{
								player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + ": currently costs " + (Misc.formatNumber(BloodMoneyPrice.getBloodMoneyPrice(itemId) / 10) + 50) + " blood money.");
						}
						return;
				}
				if (!canPurchasingSkillcape(itemId, removeSlot))
				{
						return;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is free.");
				AchievementShop.hasAchievementItemRequirements(player, itemId, true, true);
		}

		private int getAmount(int itemId, int amount, int points, String pointsName, int currencyItemId)
		{
				int result = 0;

				int price = 0;

				for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
				{
						if (ShopHandler.ShopItems[player.shopId][i] - 1 == itemId)
						{
								price = ShopHandler.shopPrice[player.shopId][i];
						}
				}

				if (price == 0)
				{
						price = BloodMoneyPrice.getBloodMoneyPrice(itemId);
				}
				if (player.shopId == 11)
				{
						price /= 10;
						price += 50;
				}
				if (price == 0)
				{
						ItemAssistant.addItem(player, itemId, amount);
						ItemAssistant.resetItems(player, 3823);
				}
				else
				{
						if (points >= price)
						{
								if (handleImbuedRing(itemId, price, "BUY"))
								{
										return points;
								}
								int maxAmount = points / price;
								if (amount > maxAmount)
								{
										amount = maxAmount;
								}
								if (!ItemDefinition.getDefinitions()[itemId].stackable && ItemAssistant.getFreeInventorySlots(player) < amount)
								{
										amount = ItemAssistant.getFreeInventorySlots(player);
								}
								else if (ItemDefinition.getDefinitions()[itemId].stackable && !ItemAssistant.hasItemInInventory(player, itemId) && ItemAssistant.getFreeInventorySlots(player) == 0)
								{
										amount = 0;
								}
								if (amount == 0)
								{
										if (price == ItemAssistant.getItemAmount(player, currencyItemId))
										{
												ItemAssistant.deleteItemFromInventory(player, currencyItemId, price);
												ItemAssistant.addItem(player, itemId, 1);
												ItemAssistant.resetItems(player, 3823);
												return 0;
										}
										player.playerAssistant.sendMessage("Not enough inventory space.");
										return points;
								}

								int oldAmount = amount;
								// Blood money 15k.
								if (itemId == 18840 || itemId == 18842)
								{
										int toGive = (itemId == 18842 ? 1500 : 7000);
										itemId = 18644;
										amount = (oldAmount * toGive);
										if (toGive == 7000)
										{
												CoinEconomyTracker.incomeList.add("DONATING " + amount);
										}
										else
										{
												CoinEconomyTracker.incomeList.add("VOTING " + amount);
										}
								}
								ItemAssistant.addItem(player, itemId, amount);
								amount = oldAmount;
								if (itemId == 13263)
								{
										Achievements.checkCompletionSingle(player, 1059);
								}
								ItemAssistant.resetItems(player, 3823);
								result = price * amount;
						}
						else
						{
								player.playerAssistant.sendMessage("You do not have enough to buy: " + ItemAssistant.getItemName(itemId) + ".");
						}
				}
				return points - result;
		}

		private boolean canPurchasingSkillcape(int itemId, int slot)
		{
				if (player.shopId == 9)
				{
						if (slot <= 20)
						{
								if (player.baseSkillLevel[slot] < 99)
								{
										player.getPA().sendMessage("You need 99 " + ServerConstants.SKILL_NAME[slot] + " to purchase this cape.");
										return false;
								}
						}
						else
						{
								slot -= 21;
								int total99s = 0;
								for (int index = 7; index < player.baseSkillLevel.length; index++)
								{
										if (player.baseSkillLevel[index] == 99)
										{
												total99s++;
										}
										if (total99s == 1)
										{
												break;
										}
								}
								if (total99s != 1)
								{
										player.getPA().sendMessage("You need a single 99 to buy a trimmed cape.");
										return false;
								}
						}
						return true;
				}
				else
				{
						return true;
				}
		}

		public void buyItem(int itemId, int fromSlot, int amount)
		{
				if (itemId >= 20000)
				{
						return;
				}
				if (player.shopId == 8 && GameMode.getGameMode(player, "IRON MAN"))
				{
						player.getPA().sendMessage("Iron Man accounts cannot use this.");
						return;
				}
				if (!itemInShop(itemId, true))
				{
						return;
				}
				if (buyOtherShopCurrency(itemId, amount))
				{
						return;
				}
				if (!AchievementShop.hasAchievementItemRequirements(player, itemId, false, true))
				{
						return;
				}
				if ((player.shopId >= 61 && player.shopId <= 62 || player.shopId == 27 || player.shopId == 30) && !player.isDonator())
				{
						DonationsNeeded.getDonatorMessage(player);
						return;
				}
				if (!canPurchasingSkillcape(itemId, fromSlot))
				{
						return;
				}
				for (int i = 0; i < SHOPS_CANNOT_BUY_FROM.length; i++)
				{
						if (player.shopId == SHOPS_CANNOT_BUY_FROM[i])
						{
								player.playerAssistant.sendMessage("This shop does sell any item.");
								return;
						}
				}
				if (!ItemDefinition.getDefinitions()[itemId].stackable && ItemAssistant.getFreeInventorySlots(player) < (amount))
				{
						amount = ItemAssistant.getFreeInventorySlots(player);
				}
				else if (ItemDefinition.getDefinitions()[itemId].stackable && !ItemAssistant.hasItemInInventory(player, itemId) && ItemAssistant.getFreeInventorySlots(player) == 0)
				{
						amount = 0;
				}
				if (amount == 0)
				{
						player.playerAssistant.sendMessage("Not enough inventory space.");
						return;
				}
				for (int index = 0; index < Tournament.eventShopIds.length; index++)
				{
						if (player.shopId == Tournament.eventShopIds[index])
						{
								if (ItemDefinition.getDefinitions()[itemId].name.startsWith("Saradomin brew"))
								{
										amount = 1;
										EdgeAndWestsRule.hasExcessBrews(player, 100);
										int doses = Integer.parseInt(ItemDefinition.getDefinitions()[itemId].name.replace("Saradomin brew(", "").replace(")", "")) - 2;
										if (doses < 0)
										{
												doses = 0;
										}
										if (player.brewCount + doses > (player.shopId == 82 ? 16 : player.shopId == 81 ? 6 : 4))
										{
												player.getPA().sendMessage("No more brews cheater!");
												return;
										}
								}
								break;
						}
				}
				ItemAssistant.addItem(player, itemId, amount);
				ItemAssistant.resetItems(player, 3823);

		}

		private void configureUntradeablesShop(int shopId)
		{
				if (shopId != 11)
				{
						return;
				}
				int totalItems = 0;
				for (int i = 0; i < player.itemsToShop.size(); i++)
				{
						totalItems++;
				}
				if (totalItems > ShopHandler.MAX_ITEM_IN_SHOP)
				{
						totalItems = ShopHandler.MAX_ITEM_IN_SHOP;
						player.playerAssistant.sendMessage("Too many items to display, buy some items to view the rest.");
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(3900);
				player.getOutStream().writeWord(totalItems);
				int TotalCount = 0;
				for (int i = 0; i < player.itemsToShop.size(); i++)
				{
						String[] args = player.itemsToShop.get(i).split(" ");
						int itemId = Integer.parseInt(args[0]);
						itemId++;
						int itemAmount = Integer.parseInt(args[1]);

						if (itemAmount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(itemAmount);
						}
						else
						{
								player.getOutStream().writeByte(itemAmount);
						}
						if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0)
						{
								itemId = ServerConstants.MAX_ITEM_ID;
						}
						player.getOutStream().writeWordBigEndianA(itemId);
						TotalCount++;
						if (TotalCount > totalItems)
						{
								break;
						}

				}
		}

		public boolean canSellItemToShop(Player player, int itemId)
		{
				if (itemId == 995)
				{
						return false;
				}
				itemId = ItemAssistant.getUnNotedItem(itemId);
				if (GameMode.getGameMode(player, "IRON MAN"))
				{
						player.getPA().sendMessage("You cannot sell as an Iron man.");
						return false;
				}
				for (int index = 0; index < ShopHandler.cannotSellToShopItems.length; index++)
				{
						if (ShopHandler.cannotSellToShopItems[index] == 0)
						{
								continue;
						}
						if (ShopHandler.cannotSellToShopItems[index] == itemId)
						{
								player.getPA().sendMessage("This item cannot be sold to the shop.");
								return false;
						}
				}

				return true;

		}

		public void priceCheckItemToSell(Player player, int itemId)
		{
				itemId = ItemAssistant.getUnNotedItem(itemId);
				if (!canSellItemToShop(player, itemId))
				{
						return;
				}
				int value = getSellToShopPrice(itemId);
				for (int index = 0; index < player.resourcesHarvested.size(); index++)
				{
						String[] parse = player.resourcesHarvested.get(index).split(" ");
						if (Integer.parseInt(parse[0]) == itemId)
						{
								if (itemId == 556 || itemId == 558 || itemId == 555 || itemId == 557 || itemId == 554 || itemId == 559 || itemId == 564 || itemId == 562 || itemId == 9075 || itemId == 561)
								{
										player.playerAssistant.sendMessage("The only runes you may sell are Law runes and above.");
										return;
								}
								if (BloodMoneyPrice.getDefinitions()[itemId] == null)
								{
										break;
								}
								value = BloodMoneyPrice.getDefinitions()[itemId].harvestedPrice;
								break;
						}
				}
				if (value == 0)
				{
						player.playerAssistant.sendMessage("This item cannot be sold.");
						return;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + ": can be sold for " + Misc.formatNumber(value) + " blood money.");
		}

		public void sellItemToShop(Player player, int itemId, int itemSlot, int amount)
		{
				if (!canSellItemToShop(player, itemId))
				{
						return;
				}
				if (!ItemAssistant.playerHasItem(player, itemId, 1, itemSlot))
				{
						return;
				}

				if (amount > ItemAssistant.getInventoryItemAmount1(player, itemId))
				{
						amount = ItemAssistant.getInventoryItemAmount1(player, itemId);
				}
				int bloodMoneyPrice = getSellToShopPrice(itemId);

				if (ItemDefinition.getDefinitions()[itemId].stackable && ItemAssistant.getFreeInventorySlots(player) == 0 && !ItemAssistant.hasItemInInventory(player, 18644))
				{
						player.playerAssistant.sendMessage("Not enough inventory space.");
						return;
				}
				if (itemId == 556 || itemId == 558 || itemId == 555 || itemId == 557 || itemId == 554 || itemId == 559 || itemId == 564 || itemId == 562 || itemId == 9075 || itemId == 561)
				{
						player.playerAssistant.sendMessage("The only runes you may sell are Law runes and above.");
						return;
				}
				String parse[] = Skilling.sellHarvestedResource(player, itemId, amount).split(" ");
				if (parse[1].equals("true"))
				{
						amount = Integer.parseInt(parse[0]);
						if (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)] != null)
						{
								bloodMoneyPrice = BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].harvestedPrice;
								CoinEconomyTracker.incomeList.add("SKILLING " + (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].harvestedPrice * amount));
						}
				}
				if (bloodMoneyPrice == 0)
				{
						player.getPA().sendMessage("This item cannot be sold.");
						return;
				}
				ItemAssistant.deleteItemFromInventory(player, itemId, amount);
				if (player.getHeight() != 20)
				{
						ItemAssistant.addItemToInventoryOrDrop(player, 18644, bloodMoneyPrice * amount);
				}
				//addQuantityToShop(itemId, amount);
				ItemAssistant.resetItems(player, 3823);
		}

		public static void updateShopForActiveShoppers()
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player player = PlayerHandler.players[i];
						if (player == null)
						{
								continue;
						}
						if (player.usingShop)
						{
								if (player.shopId == 8)
								{
										player.getShops().openShop(8);
								}
								if (player.shopId == 54)
								{
										player.getShops().openShop(54);
								}
						}
				}
		}

		public void addQuantityToShop(int itemId, int amount)
		{
				boolean itemInShop = false;

				int getUnnotedId = ItemAssistant.getUnNotedItem(itemId) + 1; // Get unnoted id of the item i'm selling.
				int getNotedId = ItemAssistant.getNotedItem(itemId) + 1; // Get noted id of the item i'm selling.

				// Selling Merchant shop item to the general store.
				if (player.shopId == 8)
				{
						// Add amount to existing item.
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								if (ShopHandler.ShopItems[54][i] == getUnnotedId || ShopHandler.ShopItems[54][i] == getNotedId)
								{
										itemInShop = true;
										ShopHandler.ShopItemsN[54][i] += amount;
										player.playerAssistant.sendMessage("The General store owner immediately resells the " + ItemAssistant.getItemName(getUnnotedId) + " to the merchant.");
										updateShopForActiveShoppers();
								}
						}
				}

				// Add amount to existing item.
				if (!itemInShop)
				{
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								if (ShopHandler.ShopItems[8][i] == getUnnotedId || ShopHandler.ShopItems[8][i] == getNotedId)
								{
										itemInShop = true;
										ShopHandler.ShopItemsN[8][i] += amount;
										updateShopForActiveShoppers();
								}
						}
				}


				// Add new item to general store.
				if (!itemInShop)
				{
						for (int i = 0; i < ShopHandler.MAX_ITEM_IN_SHOP; i++)
						{
								if (ShopHandler.ShopItems[8][i] == 0)
								{
										itemInShop = true;
										ShopHandler.ShopItems[8][i] = itemId + 1;
										ShopHandler.ShopItemsN[8][i] += amount;
										updateShopForActiveShoppers();
										break;
								}
						}
				}
				if (player.shopId == 8 || player.shopId == 54)
				{
						player.getShops().openShop(player.shopId);
				}
		}

		public boolean handleImbuedRing(int itemIdToBuy, int price, String action)
		{

				int normalRing = 0;
				for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values())
				{
						if (itemIdToBuy == data.getSpecialId())
						{
								normalRing = data.getNormalId();
						}
				}
				if (normalRing == 0)
				{
						return false;
				}
				String ringName = ItemAssistant.getItemName(normalRing);
				boolean hasRequiredRing = ItemAssistant.hasItemInInventory(player, normalRing);
				if (action.equals("PRICE"))
				{
						player.playerAssistant.sendMessage("It costs " + Misc.formatNumber(price) + " Blood money & one " + ringName + " to upgrade to " + ItemAssistant.getItemName(itemIdToBuy) + ".");
						return true;
				}
				if (hasRequiredRing)
				{
						ItemAssistant.deleteItemFromInventory(player, normalRing, 1);
						ItemAssistant.deleteItemFromInventory(player, 18644, price);
						ItemAssistant.addItem(player, itemIdToBuy, 1);
						ItemAssistant.resetItems(player, 3823);
						player.playerAssistant.sendMessage("You upgrade the " + ringName + " to an imbued version.");
						return true;
				}
				else
				{
						player.playerAssistant.sendMessage("You need a " + ringName + " to upgrade.");
						return true;
				}
		}
}