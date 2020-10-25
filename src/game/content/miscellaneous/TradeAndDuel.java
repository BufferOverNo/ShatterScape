package game.content.miscellaneous;

import java.util.concurrent.CopyOnWriteArrayList;

import core.Server;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Skilling;
import game.item.BloodMoneyPrice;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.pet.PetData;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import network.packet.PacketHandler;
import utility.Misc;


public class TradeAndDuel
{
		private Player player;

		public TradeAndDuel(Player Player)
		{
				this.player = Player;
		}

		public CopyOnWriteArrayList<GameItem> offeredItems = new CopyOnWriteArrayList<GameItem>();

		/**
		 * Will reset to 0 once the claim interface has been closed.
		 */
		public final static int THE_INSTANT_ENEMY_DIED_IN_DUEL = 6;

		public Player getPartner()
		{
				Player partner = PlayerHandler.players[player.getDuelingWith()];
				if (partner != null)
				{
						if (!partner.getPlayerName().equals(player.lastDueledWithName))
						{
								partner = null;
						}
				}
				return partner;
		}

		public void tradeRequestChatbox(int id)
		{
				if (id <= 0)
				{
						return;
				}
				final Player other = PlayerHandler.players[id];
				if (other == null)
				{
						return;
				}


				if (other.getDuelStatus() != 0)
				{
						player.getPA().sendMessage(other.getPlayerName() + " is busy.");
						return;
				}

				if (player.findOtherPlayerId > 0)
				{
						return;
				}
				player.setPlayerIdToFollow(id);
				player.setMeleeFollow(true);
				player.findOtherPlayerId = 20;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.findOtherPlayerId > 0)
								{
										player.findOtherPlayerId--;
										if (player.getPA().withinDistanceOfTargetPlayer(other, 1))
										{
												requestTrade(other.getPlayerId());
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
								player.findOtherPlayerId = 0;
						}
				}, 1);
		}

		public void requestTrade(int id)
		{
				Player partner = PlayerHandler.players[id];
				if (partner == null)
				{
						return;
				}
				if (id == player.getPlayerId())
				{
						return;
				}
				if (!player.playerAssistant.withinDistance(partner))
				{
						return;
				}
				if (partner.getDuelStatus() != 0)
				{
						player.getPA().sendMessage(partner.getPlayerName() + " is busy.");
						return;
				}
				if (partner.getHeight() != player.getHeight())
				{
						return;
				}
				if (player.getHeight() == 20)
				{
						return;
				}
				if (!Region.isStraightPathUnblocked(player.getX(), player.getY(), partner.getX(), partner.getY(), player.getHeight(), 1, 1))
				{
						return;
				}
				if (ClanChatHandler.inDiceCc(player, false) && ClanChatHandler.inDiceCc(partner, false))
				{
						int wealth = 0;
						for (int index = 0; index < partner.playerItems.length; index++)
						{
								int itemId = partner.playerItems[index] - 1;
								if (itemId <= 0)
								{
										continue;
								}
								wealth += BloodMoneyPrice.getBloodMoneyPrice(itemId) * partner.playerItemsN[index];
						}
						ClanChatHandler.sendDiceClanMessage(partner.getPlayerName(), partner.getClanId(), "I am currently holding " + Misc.formatRunescapeStyle(wealth) + " blood money.");
				}
				player.resetPlayerIdToFollow();
				player.turnPlayerTo(partner.getX(), partner.getY());
				player.setTradeWith(id);
				if (!player.isInTrade() && partner.tradeRequested && partner.getTradeWith() == player.getPlayerId())
				{
						player.getTradeAndDuel().openTrade();
						partner.getTradeAndDuel().openTrade();
				}
				else if (!player.isInTrade())
				{

						player.tradeRequested = true;
						player.playerAssistant.sendMessage("Sending trade request to " + partner.getCapitalizedName() + ".");
						player.faceUpdate(partner.getPlayerId() + 32768);
						player.setFaceResetAtEndOfTick(true);
						partner.playerAssistant.sendMessage(player.getCapitalizedName() + ":tradereq:");
				}
		}

		public void openTrade()
		{
				Player o = PlayerHandler.players[player.getTradeWith()];

				if (o == null)
				{
						return;
				}
				player.setInTrade(true);
				player.canOffer = true;
				player.setTradeStatus(1);
				player.tradeRequested = false;
				ItemAssistant.resetItems(player, 3322);
				resetTItems(3415);
				resetOTItems(3416);
				String out = o.getPlayerName();
				if (o.isAdministratorRank())
				{
						out = "@cr2@" + out;
				}
				else if (o.isModeratorRank())
				{
						out = "@cr1@" + out;
				}
				player.getPA().sendFrame126(o.getPlayerName(), 21350);
				player.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(o) + " free", 21351);
				player.getPA().sendFrame126("Trading with: " + o.getCapitalizedName(), 3417);
				player.getPA().sendFrame126("", 3431);
				player.getPA().sendFrame126("Are you sure you want to make this trade with " + o.getCapitalizedName() + "?", 3535);
				player.getPA().sendFrame248(3323, 3321);
				player.getPA().sendFrame126("0 blood money.", 21353);
				player.getPA().sendFrame126("0 blood money.", 21354);
		}



		public void resetTItems(int WriteFrame)
		{
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(WriteFrame);
				int len = offeredItems.toArray().length;
				int current = 0;
				player.getOutStream().writeWord(len);
				for (GameItem item : offeredItems)
				{
						if (item.amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(item.amount);
						}
						else
						{
								player.getOutStream().writeByte(item.amount);
						}
						player.getOutStream().writeWordBigEndianA(item.id + 1);
						current++;
				}
				if (current < 27)
				{
						for (int i = current; i < 28; i++)
						{
								player.getOutStream().writeByte(1);
								player.getOutStream().writeWordBigEndianA(-1);
						}
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
		}

		public boolean fromTrade(int itemID, int fromSlot, int amount)
		{
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o == null)
				{
						return false;
				}
				try
				{
						if (!player.isInTrade() || !player.canOffer)
						{
								PacketHandler.tradeAndDuelLog.add(player.getPlayerName() + " at " + Misc.getDate());
								PacketHandler.tradeAndDuelLog.add("Trade, not in trade or force offering.");
								PacketHandler.tradeAndDuelLog.add("Partner: " + o.getPlayerName() + ", Item: " + itemID + ", inTrade: " + player.isInTrade() + ", canOffer: " + player.canOffer);
								declineTrade1(true);
								return false;
						}
						if (amount <= 0)
						{
								PacketHandler.tradeAndDuelLog.add(player.getPlayerName() + " at " + Misc.getDate());
								PacketHandler.tradeAndDuelLog.add("Trade, amount 0 or less");
								PacketHandler.tradeAndDuelLog.add("Amount: " + amount + ", Partner: " + o.getPlayerName() + ", Item: " + itemID + ", inTrade: " + player.isInTrade() + ", canOffer: " + player.canOffer);
								return false;
						}
						player.tradeConfirmed = false;
						o.tradeConfirmed = false;
						if (!ItemDefinition.getDefinitions()[itemID].stackable)
						{
								if (amount > 28)
								{
										amount = 28;
								}
								for (int a = 0; a < amount; a++)
								{
										for (GameItem item : offeredItems)
										{
												if (item.id == itemID)
												{
														if (!item.stackable)
														{
																offeredItems.remove(item);
																ItemAssistant.addItem(player, itemID, 1);
														}
														else if (item.amount > amount)
														{
																item.amount -= amount;
																ItemAssistant.addItem(player, itemID, amount);
														}
														else
														{
																amount = item.amount;
																offeredItems.remove(item);
																ItemAssistant.addItem(player, itemID, amount);
														}
														break;
												}
										}
								}
						}
						for (GameItem item : offeredItems)
						{
								if (item.id == itemID)
								{
										if (!item.stackable)
										{
										}
										else if (item.amount > amount)
										{
												item.amount -= amount;
												ItemAssistant.addItem(player, itemID, amount);
										}
										else
										{
												amount = item.amount;
												offeredItems.remove(item);
												ItemAssistant.addItem(player, itemID, amount);

										}
										break;
								}
						}

						bloodMoneyWorth(player, o);

						player.tradeConfirmed = false;
						o.tradeConfirmed = false;
						ItemAssistant.resetItems(player, 3322);
						resetTItems(3415);
						o.getTradeAndDuel().resetOTItems(3416);
						player.getPA().sendFrame126("", 3431);
						o.getPA().sendFrame126("", 3431);

						o.getPA().sendFrame126(player.getPlayerName(), 21350);
						o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
				}
				catch (Exception e)
				{

				}
				return true;
		}

		private void bloodMoneyWorth(Player player, Player o)
		{
				int totalBloodMoneyWorth = 0;
				for (GameItem item : offeredItems)
				{
						totalBloodMoneyWorth += BloodMoneyPrice.getBloodMoneyPrice(item.id) * item.amount;
				}
				player.getPA().sendFrame126(Misc.formatNumber(totalBloodMoneyWorth) + " blood money.", 21353); // my amount
				o.getPA().sendFrame126(Misc.formatNumber(totalBloodMoneyWorth) + " blood money.", 21354); // send my amount to other player

		}

		public boolean tradeItem(int itemId, int fromSlot, int amount)
		{
				if (!ItemAssistant.playerHasItem(player, itemId, 1, fromSlot))
				{
						return false;
				}
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o == null)
				{
						return false;
				}
				int baseAmount = ItemDefinition.getDefinitions()[itemId].stackable ? ItemAssistant.getItemAmount(player, itemId, fromSlot) : ItemAssistant.getItemAmount(player, itemId);
				if (amount > baseAmount)
				{
						amount = baseAmount;
				}
				if (amount <= 0)
				{
						return false;
				}


				// Hand cannon and hand cannon noted.
				if (itemId == 15241 || itemId == 15242)
				{
						player.getPA().sendMessage("This item is useless.");
						return false;
				}
				// Blood key.
				if (itemId == 18825)
				{
						if (Area.inWilderness(player))
						{
								player.getPA().sendMessage("You are unable to trade this key in the wilderness.");
								return false;
						}
				}


				for (int j = 0; j < ServerConstants.UNTRADEABLE_ITEMS.length; j++)
				{
						if (itemId == ServerConstants.UNTRADEABLE_ITEMS[j])
						{
								player.getPA().sendMessage("This item is untradeable.");
								return false;
						}
				}

				for (int a = 0; a < PetData.petData.length; a++)
				{
						if (PetData.petData[a][1] == itemId)
						{
								player.getPA().sendMessage("This item is untradeable.");
								return false;
						}
				}

				player.tradeConfirmed = false;
				o.tradeConfirmed = false;
				if (!ItemDefinition.getDefinitions()[itemId].stackable && !ItemDefinition.getDefinitions()[itemId].note)
				{
						for (int a = 0; a < amount; a++)
						{
								if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1))
								{
										offeredItems.add(new GameItem(itemId, 1));
										ItemAssistant.deleteItemFromInventory(player, itemId, ItemAssistant.getItemSlot(player, itemId), 1);
										o.getPA().sendFrame126(player.getPlayerName(), 21350);
										o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
								}
						}


						bloodMoneyWorth(player, o);

						o.getPA().sendFrame126(player.getPlayerName(), 21350);
						o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
						ItemAssistant.resetItems(player, 3322);
						resetTItems(3415);
						o.getTradeAndDuel().resetOTItems(3416);
						player.getPA().sendFrame126("", 3431);
						o.getPA().sendFrame126("", 3431);
				}
				if (ItemAssistant.getInventoryItemAmount1(player, itemId) < amount)
				{
						amount = ItemAssistant.getInventoryItemAmount1(player, itemId);
						if (amount == 0)
						{
								return false;
						}
				}
				if (!player.isInTrade() || !player.canOffer)
				{
						declineTrade1(true);
						return false;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, itemId, amount))
				{
						return false;
				}

				if (ItemDefinition.getDefinitions()[itemId].stackable || ItemDefinition.getDefinitions()[itemId].note)
				{
						boolean inTrade = false;
						for (GameItem item : offeredItems)
						{
								if (item.id == itemId)
								{
										inTrade = true;
										item.amount += amount;
										ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
										o.getPA().sendFrame126(player.getPlayerName(), 21350);
										o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
										break;
								}
						}

						if (!inTrade)
						{
								offeredItems.add(new GameItem(itemId, amount));
								ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
								o.getPA().sendFrame126(player.getPlayerName(), 21350);
								o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
						}
				}


				bloodMoneyWorth(player, o);

				o.getPA().sendFrame126(player.getPlayerName(), 21350);
				o.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
				ItemAssistant.resetItems(player, 3322);
				resetTItems(3415);
				o.getTradeAndDuel().resetOTItems(3416);
				player.getPA().sendFrame126("", 3431);
				o.getPA().sendFrame126("", 3431);
				return true;
		}

		public void tradeResetRequired()
		{
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o != null)
				{
						if (o.tradeResetNeeded)
						{
								player.getTradeAndDuel().resetTrade();
								o.getTradeAndDuel().resetTrade();
						}
				}
		}

		public void resetTrade()
		{
				offeredItems.clear();
				player.setInTrade(false);
				player.setTradeStatus(0);
				player.setTradeWith(0);
				player.canOffer = true;
				player.tradeConfirmed = false;
				player.tradeConfirmed2 = false;
				player.acceptedTrade = false;
				player.getPA().closeInterfaces();
				player.tradeResetNeeded = false;
				player.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
		}

		public void declineTrade1(boolean alert)
		{
				if (!player.isInTrade())
				{
						return;
				}
				player.setTradeStatus(0);
				declineTrade(alert);
		}


		public void declineTrade(boolean tellOther)
		{
				// Close interface. Cannot use player.getPA().closeInterfaces.
				if (!player.isBot)
				{
						if (player.getOutStream() != null && player != null)
						{
								player.hasDialogueOptionOpened = false;
								player.getOutStream().createFrame(219);
								player.flushOutStream();
						}
				}
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o == null)
				{
						return;
				}

				o.resetFaceUpdate();
				player.resetFaceUpdate();

				if (tellOther)
				{
						if (!o.ignoreTradeMessage && !player.ignoreTradeMessage)
						{
								if (o.isInTrade())
								{
										o.getPA().sendMessage(player.getPlayerName() + " has declined the trade.");
										player.getPA().sendMessage("You have declined the trade.");
								}
						}
						o.getTradeAndDuel().declineTrade(false);
						o.getTradeAndDuel().player.getPA().closeInterfaces();
				}

				for (GameItem item : offeredItems)
				{
						if (item.amount < 1)
						{
								continue;
						}
						if (item.stackable)
						{
								ItemAssistant.addItem(player, item.id, item.amount);
						}
						else
						{
								for (int i = 0; i < item.amount; i++)
								{
										ItemAssistant.addItem(player, item.id, 1);
								}
						}
				}
				player.canOffer = true;
				player.tradeConfirmed = false;
				player.tradeConfirmed2 = false;
				offeredItems.clear();
				player.setTradeStatus(0);
				player.setInTrade(false);
				player.setTradeWith(0);
		}


		public void resetOTItems(int WriteFrame)
		{
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o == null)
				{
						return;
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(WriteFrame);
				int len = o.getTradeAndDuel().offeredItems.toArray().length;
				int current = 0;
				player.getOutStream().writeWord(len);
				for (GameItem item : o.getTradeAndDuel().offeredItems)
				{
						if (item.amount > 254)
						{
								player.getOutStream().writeByte(255); // item's stack count. if over 254, write byte 255
								player.getOutStream().writeDWord_v2(item.amount);
						}
						else
						{
								player.getOutStream().writeByte(item.amount);
						}
						player.getOutStream().writeWordBigEndianA(item.id + 1); // item id
						current++;
				}
				if (current < 27)
				{
						for (int i = current; i < 28; i++)
						{
								player.getOutStream().writeByte(1);
								player.getOutStream().writeWordBigEndianA(-1);
						}
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
		}


		public void confirmScreen()
		{
				Player o = PlayerHandler.players[player.getTradeWith()];
				if (o == null)
				{
						return;
				}
				player.canOffer = false;
				player.setInventoryUpdate(true);
				String SendTrade = "Absolutely nothing!";
				String SendAmount = "";
				int Count = 0;
				for (GameItem item : offeredItems)
				{
						if (item.id > 0)
						{
								if (item.amount >= 1000 && item.amount < 1000000)
								{
										SendAmount = "@cya@" + (item.amount / 1000) + "K @whi@(" + Misc.formatNumber(item.amount) + ")";
								}
								else if (item.amount >= 1000000)
								{
										SendAmount = "@gre@" + (item.amount / 1000000) + " million @whi@(" + Misc.formatNumber(item.amount) + ")";
								}
								else
								{
										SendAmount = "" + Misc.formatNumber(item.amount);
								}

								if (Count == 0)
								{
										SendTrade = ItemAssistant.getItemName(item.id);
								}
								else
								{
										SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.id);
								}

								if (item.stackable)
								{
										SendTrade = SendTrade + " x " + SendAmount;
								}
								Count++;
						}
				}

				player.getPA().sendFrame126(SendTrade, 3557);
				SendTrade = "Absolutely nothing!";
				SendAmount = "";
				Count = 0;

				for (GameItem item : o.getTradeAndDuel().offeredItems)
				{
						if (item.id > 0)
						{
								if (item.amount >= 1000 && item.amount < 1000000)
								{
										SendAmount = "@cya@" + (item.amount / 1000) + "K @whi@(" + Misc.formatNumber(item.amount) + ")";
								}
								else if (item.amount >= 1000000)
								{
										SendAmount = "@gre@" + (item.amount / 1000000) + " million @whi@(" + Misc.formatNumber(item.amount) + ")";
								}
								else
								{
										SendAmount = "" + Misc.formatNumber(item.amount);
								}
								if (Count == 0)
								{
										SendTrade = ItemAssistant.getItemName(item.id);
								}
								else
								{
										SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.id);
								}
								if (item.stackable)
								{
										SendTrade = SendTrade + " x " + SendAmount;
								}
								Count++;
						}
				}
				player.getPA().sendFrame126(SendTrade, 3558);
				player.getPA().sendFrame248(3443, 197);
				o.getPA().sendFrame126(player.getPlayerName(), 3451);
				player.getPA().sendFrame126(o.getPlayerName(), 3451);
		}


		public void giveItems(Player other)
		{
				if (System.currentTimeMillis() - player.diceDelay < 6000)
				{
						return;
				}
				player.diceDelay = System.currentTimeMillis();
				if (other == null)
				{
						return;
				}
				try
				{
						for (GameItem item : other.getTradeAndDuel().offeredItems)
						{
								if (item.id > 0)
								{
										ItemAssistant.addItem(player, item.id, item.amount);
								}
						}
						player.tradesCompleted++;
				}
				catch (Exception e)
				{
				}
		}

		public CopyOnWriteArrayList<GameItem> otherStakedItems = new CopyOnWriteArrayList<GameItem>();

		public CopyOnWriteArrayList<GameItem> myStakedItems = new CopyOnWriteArrayList<GameItem>();

		public void requestDuel(int id)
		{
				try
				{
						if (id == player.getPlayerId())
						{
								return;
						}
						if (player.getDuelStatus() >= 1)
						{
								return;
						}
						player.resetPlayerIdToFollow();
						resetDuel();
						resetDuelItems();
						Player o = PlayerHandler.players[id];
						if (o == null)
						{
								return;
						}

						if (o.getHeight() != player.getHeight())
						{
								return;
						}
						if (player.getHeight() == 20)
						{
								return;
						}
						if (!player.playerAssistant.withinDistance(o))
						{
								return;
						}
						if (o.isInTrade() || o.isUsingBankInterface() || o.getDuelStatus() != 0)
						{
								player.turnPlayerTo(o.getX(), o.getY());
								player.getPA().sendMessage(o.getPlayerName() + " is busy.");
								Combat.resetPlayerAttack(player);
								return;
						}
						if (!Region.isStraightPathUnblocked(player.getX(), player.getY(), o.getX(), o.getY(), player.getHeight(), 1, 1))
						{
								return;
						}
						player.setDuelingWith(id);
						player.lastDueledWithName = o.getPlayerName();
						player.duelRequested = true;
						if (player.getDuelStatus() == 0 && o.getDuelStatus() == 0 && player.duelRequested && o.duelRequested && player.getDuelingWith() == o.getPlayerId() && o.getDuelingWith() == player.getPlayerId())
						{
								if (player.playerAssistant.withInDistance(player.getX(), player.getY(), o.getX(), o.getY(), 1))
								{
										player.getTradeAndDuel().openDuel();
										o.getTradeAndDuel().openDuel();
								}
								else
								{
										player.playerAssistant.sendMessage("You need to get closer to your opponent to start the duel.");
								}

						}
						else
						{
								player.playerAssistant.sendMessage("Sending duel request...");
								player.turnPlayerTo(o.getX(), o.getY());
								o.playerAssistant.sendMessage(player.getCapitalizedName() + ":duelreq:");
						}
				}
				catch (Exception e)
				{
						Misc.print("Error requesting duel.");
				}
		}

		public void openDuel()
		{
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						return;
				}
				player.setDuelStatus(1);
				refreshduelRules();
				refreshDuelScreen();
				player.canOffer = true;
				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						sendDuelEquipment(player.playerEquipment[i], player.playerEquipmentN[i], i);
				}
				player.getPA().sendFrame126("Dueling with: " + o.getCapitalizedName() + " (level-" + o.getCombatLevel() + ")", 6671);
				player.getPA().sendFrame126("", 6684);
				player.getPA().sendFrame248(6575, 3321);
				ItemAssistant.resetItems(player, 3322);
		}

		public void sendDuelEquipment(int itemId, int amount, int slot)
		{
				if (itemId != 0)
				{
						player.getOutStream().createFrameVarSizeWord(34);
						player.getOutStream().writeWord(13824);
						player.getOutStream().writeByte(slot);
						player.getOutStream().writeWord(itemId + 1);

						if (amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord(amount);
						}
						else
						{
								player.getOutStream().writeByte(amount);
						}
						player.getOutStream().endFrameVarSizeWord();
						player.flushOutStream();
				}
		}

		public void refreshduelRules()
		{
				for (int i = 0; i < player.duelRule.length; i++)
				{
						player.duelRule[i] = false;
				}
				player.getPA().sendFrame87(286, 0);
				player.duelOption = 0;
		}

		public void refreshDuelScreen()
		{
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						return;
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(6669);
				player.getOutStream().writeWord(myStakedItems.toArray().length);
				int current = 0;
				for (GameItem item : myStakedItems)
				{
						if (item.amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(item.amount);
						}
						else
						{
								player.getOutStream().writeByte(item.amount);
						}
						if (item.id > ServerConstants.MAX_ITEM_ID || item.id < 0)
						{
								item.id = ServerConstants.MAX_ITEM_ID;
						}
						player.getOutStream().writeWordBigEndianA(item.id + 1);

						current++;
				}

				if (current < 27)
				{
						for (int i = current; i < 28; i++)
						{
								player.getOutStream().writeByte(1);
								player.getOutStream().writeWordBigEndianA(-1);
						}
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();

				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(6670);
				player.getOutStream().writeWord(o.getTradeAndDuel().myStakedItems.toArray().length);
				current = 0;
				for (GameItem item : o.getTradeAndDuel().myStakedItems)
				{
						if (item.amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(item.amount);
						}
						else
						{
								player.getOutStream().writeByte(item.amount);
						}
						if (item.id > ServerConstants.MAX_ITEM_ID || item.id < 0)
						{
								item.id = ServerConstants.MAX_ITEM_ID;
						}
						player.getOutStream().writeWordBigEndianA(item.id + 1);
						current++;
				}

				if (current < 27)
				{
						for (int i = current; i < 28; i++)
						{
								player.getOutStream().writeByte(1);
								player.getOutStream().writeWordBigEndianA(-1);
						}
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
		}


		public boolean stakeItem(int itemId, int fromSlot, int amount)
		{
				int amountStock = ItemDefinition.getDefinitions()[itemId].stackable ? ItemAssistant.getItemAmount(player, itemId, fromSlot) : ItemAssistant.getItemAmount(player, itemId);

				if (amountStock < amount)
				{
						amount = amountStock;
				}
				if (amount <= 0)
				{
						return false;
				}

				if (!ItemAssistant.playerHasItem(player, itemId, amount, fromSlot))
				{
						return false;
				}
				if (amount <= 0)
				{
						return false;
				}
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						declineDuel(false);
						return false;
				}
				if (o.getDuelStatus() <= 0 || player.getDuelStatus() <= 0)
				{
						declineDuel(false);
						o.getTradeAndDuel().declineDuel(false);
						return false;
				}
				if (!player.canOffer)
				{
						return false;
				}
				if (ItemAssistant.getItemName(itemId).contains("bolts"))
				{
						player.getPA().sendMessage("You cannot stake bolts.");
						return false;
				}


				for (int j = 0; j < ServerConstants.UNTRADEABLE_ITEMS.length; j++)
				{
						if (itemId == ServerConstants.UNTRADEABLE_ITEMS[j])
						{
								player.getPA().sendMessage("This item is unstakeable.");
								return false;
						}
				}

				for (int a = 0; a < PetData.petData.length; a++)
				{
						if (PetData.petData[a][1] == itemId)
						{
								player.getPA().sendMessage("This item is unstakeable.");
								return false;
						}
				}

				o.timeDuelRuleChanged = System.currentTimeMillis();
				player.timeDuelRuleChanged = System.currentTimeMillis();
				changeDuelStuff();
				if (!ItemDefinition.getDefinitions()[itemId].stackable)
				{
						for (int a = 0; a < amount; a++)
						{
								if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1))
								{
										myStakedItems.add(new GameItem(itemId, 1));
										ItemAssistant.deleteItemFromInventory(player, itemId, ItemAssistant.getItemSlot(player, itemId), 1);
								}
						}
						player.setInventoryUpdate(true);
						ItemAssistant.resetItems(player, 3322);
						o.setInventoryUpdate(true);
						ItemAssistant.resetItems(o, 3322);
						refreshDuelScreen();
						o.getTradeAndDuel().refreshDuelScreen();
						player.getPA().sendFrame126("", 6684);
						o.getPA().sendFrame126("", 6684);
				}

				if (!ItemAssistant.hasItemAmountInInventory(player, itemId, amount))
				{
						return false;
				}
				if (ItemDefinition.getDefinitions()[itemId].stackable || ItemDefinition.getDefinitions()[itemId].note)
				{
						boolean found = false;
						for (GameItem item : myStakedItems)
						{
								if (item.id == itemId)
								{
										found = true;
										item.amount += amount;
										ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
										break;
								}
						}
						if (!found)
						{
								ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
								myStakedItems.add(new GameItem(itemId, amount));
						}
				}

				player.setInventoryUpdate(true);
				ItemAssistant.resetItems(player, 3322);
				o.setInventoryUpdate(true);
				ItemAssistant.resetItems(o, 3322);
				refreshDuelScreen();
				o.getTradeAndDuel().refreshDuelScreen();
				player.getPA().sendFrame126("", 6684);
				o.getPA().sendFrame126("", 6684);
				return true;
		}


		public boolean fromDuel(int itemID, int fromSlot, int amount)
		{
				if (player.getDuelStatus() >= 3)
				{
						return false;
				}
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						declineDuel(false);
						return false;
				}
				if (o.getDuelStatus() <= 0 || player.getDuelStatus() <= 0)
				{
						declineDuel(false);
						o.getTradeAndDuel().declineDuel(false);
						return false;
				}
				if (!player.canOffer)
				{
						PacketHandler.tradeAndDuelLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.tradeAndDuelLog.add("Duel arena, force offering.");
						PacketHandler.tradeAndDuelLog.add("Partner: " + o.getPlayerName() + ", Item: " + itemID);
						return false;
				}
				if (amount <= 0)
				{
						PacketHandler.tradeAndDuelLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.tradeAndDuelLog.add("Duel arena, amount 0 or less");
						PacketHandler.tradeAndDuelLog.add("amount: " + amount + ", Item: " + itemID + ", Partner: " + o.getPlayerName());
						return false;
				}
				if (ItemDefinition.getDefinitions()[itemID].stackable)
				{
						if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq))
						{
								player.playerAssistant.sendMessage("You have too many rules set to remove that item.");
								return false;
						}
				}

				o.timeDuelRuleChanged = System.currentTimeMillis();
				player.timeDuelRuleChanged = System.currentTimeMillis();
				changeDuelStuff();
				boolean goodSpace = true;
				if (!ItemDefinition.getDefinitions()[itemID].stackable)
				{
						for (int a = 0; a < amount; a++)
						{
								for (GameItem item : myStakedItems)
								{
										if (item.id == itemID)
										{
												if (!item.stackable)
												{
														if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq))
														{
																goodSpace = false;
																break;
														}
														myStakedItems.remove(item);
														ItemAssistant.addItem(player, itemID, 1);
												}
												else
												{
														if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq))
														{
																goodSpace = false;
																break;
														}
														if (item.amount > amount)
														{
																item.amount -= amount;
																ItemAssistant.addItem(player, itemID, amount);
														}
														else
														{
																if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq))
																{
																		goodSpace = false;
																		break;
																}
																amount = item.amount;
																myStakedItems.remove(item);
																ItemAssistant.addItem(player, itemID, amount);
														}
												}
												break;
										}
										o.setDuelStatus(1);
										player.setDuelStatus(1);
										player.setInventoryUpdate(true);
										ItemAssistant.resetItems(player, 3322);
										o.setInventoryUpdate(true);
										ItemAssistant.resetItems(o, 3322);
										player.getTradeAndDuel().refreshDuelScreen();
										o.getTradeAndDuel().refreshDuelScreen();
										o.getPA().sendFrame126("", 6684);
								}
						}
				}

				for (GameItem item : myStakedItems)
				{
						if (item.id == itemID)
						{
								if (!item.stackable)
								{
								}
								else
								{
										if (item.amount > amount)
										{
												item.amount -= amount;
												ItemAssistant.addItem(player, itemID, amount);
										}
										else
										{
												amount = item.amount;
												myStakedItems.remove(item);
												ItemAssistant.addItem(player, itemID, amount);
										}
								}
								break;
						}
				}
				o.setDuelStatus(1);
				player.setDuelStatus(1);
				player.setInventoryUpdate(true);
				ItemAssistant.resetItems(player, 3322);
				o.setInventoryUpdate(true);
				ItemAssistant.resetItems(o, 3322);
				player.getTradeAndDuel().refreshDuelScreen();
				o.getTradeAndDuel().refreshDuelScreen();
				o.getPA().sendFrame126("", 6684);
				if (!goodSpace)
				{
						player.playerAssistant.sendMessage("You have too many rules set to remove that item.");
						return true;
				}
				return true;
		}

		public void confirmDuel()
		{
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						declineDuel(false);
						return;
				}
				String itemId = "";
				for (GameItem item : myStakedItems)
				{
						if (ItemDefinition.getDefinitions()[item.id].stackable || ItemDefinition.getDefinitions()[item.id].note)
						{
								itemId += ItemAssistant.getItemName(item.id) + " x " + Misc.formatNumber(item.amount) + "\\n";
						}
						else
						{
								itemId += ItemAssistant.getItemName(item.id) + "\\n";
						}
				}
				player.getPA().sendFrame126(itemId, 6516);
				itemId = "";
				for (GameItem item : o.getTradeAndDuel().myStakedItems)
				{
						if (ItemDefinition.getDefinitions()[item.id].stackable || ItemDefinition.getDefinitions()[item.id].note)
						{
								itemId += ItemAssistant.getItemName(item.id) + " x " + Misc.formatNumber(item.amount) + "\\n";
						}
						else
						{
								itemId += ItemAssistant.getItemName(item.id) + "\\n";
						}
				}
				player.getPA().sendFrame126(itemId, 6517);
				player.getPA().sendFrame126("", 8242);
				for (int i = 8238; i <= 8253; i++)
				{
						player.getPA().sendFrame126("", i);
				}
				player.getPA().sendFrame126("Hitpoints will be restored.", 8250);
				player.getPA().sendFrame126("Boosted stats will be restored.", 8238);
				if (player.duelRule[8])
				{
						player.getPA().sendFrame126("There will be obstacles in the arena.", 8239);
				}
				player.getPA().sendFrame126("", 8240);
				player.getPA().sendFrame126("", 8241);

				String[] rulesOption = {
						"Players cannot forfeit!",
						"Players cannot move.",
						"Players cannot use range.",
						"Players cannot use melee.",
						"Players cannot use magic.",
						"Players cannot drink pots.",
						"Players cannot eat food.",
						"Players cannot use prayer."};

				int lineNumber = 8242;
				for (int i = 0; i < 8; i++)
				{
						if (player.duelRule[i])
						{
								player.getPA().sendFrame126("" + rulesOption[i], lineNumber);
								lineNumber++;
						}
				}
				player.getPA().sendFrame126("", 6571);
				player.getPA().sendFrame248(6412, 197);
				//c.getPA().showInterface(6412);
		}


		public void startDuel()
		{
				player.stakeAttacks = 0;
				player.stakeSpecialAttacks = 0;
				player.getPA().resetStats();
				Combat.resetPrayers(player);
				Follow.resetFollow(player);
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						duelVictory();
				}
				if (o.isDisconnected())
				{
						duelVictory();
				}
				QuickSetUp.heal(player);
				player.headIconHints = 2;
				player.setVengeance(false);

				if (player.duelRule[7])
				{
						Combat.resetPrayers(player);
				}
				if (player.duelRule[11])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[0], 0);
				}
				if (player.duelRule[12])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[1], 1);
				}
				if (player.duelRule[13])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[2], 2);
				}
				if (player.duelRule[14])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[3], 3);
				}
				if (player.duelRule[15])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[4], 4);
				}
				if (player.duelRule[16])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[5], 5);
				}
				if (player.duelRule[17])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[7], 7);
				}
				if (player.duelRule[18])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[9], 9);
				}
				if (player.duelRule[19])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[10], 10);
				}
				if (player.duelRule[20])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[12], 12);
				}
				if (player.duelRule[21])
				{
						ItemAssistant.removeItem(player, player.playerEquipment[13], 13);
				}

				//14 means weapon rule is on
				if (!player.duelRule[14] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT))
				{
						// If 2h weapon is wielded and shields is ticked off, then wapon must be removed. 16 means shield rule is on.
						if (ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT) && player.duelRule[16])
						{
								if (ItemAssistant.is2handed(ItemAssistant.getItemName(player.getWieldedWeapon()), player.getWieldedWeapon()))
								{
										ItemAssistant.removeItem(player, player.playerEquipment[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT);
								}
						}
				}
				player.setDuelStatus(5);
				player.getPA().closeInterfaces();
				player.setSpecialAttackAmount(10, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());

				if (player.duelRule[8])
				{
						if (player.duelRule[1])
						{
								player.getPA().movePlayer(player.duelTeleX, player.duelTeleY, 0);
						}
						else
						{
								player.getPA().movePlayer(3366 + Misc.random(12), 3246 + Misc.random(6), 0);
						}
				}
				else
				{
						if (player.duelRule[1])
						{
								player.getPA().movePlayer(player.duelTeleX, player.duelTeleY, 0);
						}
						else
						{
								player.getPA().movePlayer(3335 + Misc.random(12), 3246 + Misc.random(6), 0);
						}
				}

				player.getPA().createPlayerHints(10, o.getPlayerId());
				player.getPA().showOption(3, 0, "Attack", 1);
				Skilling.updateAllSkillTabFrontText(player);
				boolean staked = false;
				for (GameItem item : o.getTradeAndDuel().myStakedItems)
				{
						otherStakedItems.add(new GameItem(item.id, item.amount));
						staked = true;
				}
				if (staked)
				{
						o.duelArenaStakes++;
				}
				player.getPA().requestUpdates();
		}


		public void duelVictory()
		{
				Player o = player.getTradeAndDuel().getPartner();
				if (o != null)
				{
						player.getPA().sendFrame126("" + o.getCombatLevel(), 6839);
						player.getPA().sendFrame126(o.getPlayerName(), 6840);
						o.setDuelStatus(0);
						o.setFrozenLength(0);
						o.getTradeAndDuel().resetDuel();
				}
				else
				{
						player.getPA().sendFrame126("", 6839);
						player.getPA().sendFrame126("", 6840);
				}
				player.setDuelStatus(6);
				Combat.resetPrayers(player);
				Skilling.updateAllSkillTabFrontText(player);
				player.setSpecialAttackAmount(10, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				duelRewardInterface();
				player.getPA().displayInterface(6733);
				player.getPA().movePlayer(3362, 3263, 0);
				player.getPA().requestUpdates();
				player.getPA().showOption(3, 0, "Challenge", 3);
				player.getPA().resetStats();
				player.setSpecialAttackAmount(10.0, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				player.setHitPoints(player.getBaseHitPointsLevel());
				player.runEnergy = 100;
				Poison.removePoison(player);
				player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
				player.getPA().createPlayerHints(10, -1);
				player.canOffer = true;
				player.duelSpaceReq = 0;
				player.setDuelingWith(0);
				player.setFrozenLength(0);
				Follow.resetFollow(player);
				Combat.resetPlayerAttack(player);
				player.duelRequested = false;
				for (int i = 0; i < player.duelRule.length; i++)
				{
						player.duelRule[i] = false;
				}
		}


		public void duelRewardInterface()
		{
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(6822);
				player.getOutStream().writeWord(otherStakedItems.toArray().length);
				for (GameItem item : otherStakedItems)
				{
						if (item.amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(item.amount);
						}
						else
						{
								player.getOutStream().writeByte(item.amount);
						}
						if (item.id > ServerConstants.MAX_ITEM_ID || item.id < 0)
						{
								item.id = ServerConstants.MAX_ITEM_ID;
						}
						player.getOutStream().writeWordBigEndianA(item.id + 1);
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
		}

		public void claimStakedItems()
		{
				if (player.getDuelStatus() != 6)
				{
						return;
				}
				if (Area.inDuelArenaRing(player))
				{
						return;
				}

				for (GameItem item : otherStakedItems)
				{

						if (item.id > 0 && item.amount > 0)
						{
								if (ItemDefinition.getDefinitions()[item.id].stackable)
								{
										if (!ItemAssistant.addItem(player, item.id, item.amount))
										{
												player.getPA().sendMessage("The item has been dropped on the ground!");
												Server.itemHandler.createGroundItem(player, item.id, player.getX(), player.getY(), item.amount, false, 0, true, "Duel: " + player.lastDueledWithName);
										}
								}
								else
								{
										int amount = item.amount;
										for (int a = 1; a <= amount; a++)
										{
												if (!ItemAssistant.addItem(player, item.id, 1))
												{
														player.getPA().sendMessage("The item has been dropped on the ground!");
														Server.itemHandler.createGroundItem(player, item.id, player.getX(), player.getY(), 1, false, 0, true, "Duel: " + player.lastDueledWithName);
												}
										}
								}
						}
				}
				for (GameItem item : myStakedItems)
				{
						if (item.id > 0 && item.amount > 0)
						{
								if (ItemDefinition.getDefinitions()[item.id].stackable)
								{
										if (!ItemAssistant.addItem(player, item.id, item.amount))
										{
												player.getPA().sendMessage("The item has been dropped on the ground!");
												Server.itemHandler.createGroundItem(player, item.id, player.getX(), player.getY(), item.amount, false, 0, true, "Duel: " + player.lastDueledWithName);
										}
								}
								else
								{
										int amount = item.amount;
										for (int a = 1; a <= amount; a++)
										{
												if (!ItemAssistant.addItem(player, item.id, 1))
												{
														player.getPA().sendMessage("The item has been dropped on the ground!");
														Server.itemHandler.createGroundItem(player, item.id, player.getX(), player.getY(), 1, false, 0, true, "Duel: " + player.lastDueledWithName);
												}
										}
								}
						}
				}
				resetDuelItems();
				player.setDuelStatus(0);
		}

		public void declineDuel(boolean tellOther)
		{
				if (tellOther)
				{

						if (player.getDuelingWith() > 0)
						{
								player.getPA().sendMessage("You have declined the duel.");
						}
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								o.getPA().sendMessage(player.getPlayerName() + " has declined the duel.");
						}
				}
				player.getPA().closeInterfaces();
				player.canOffer = true;
				player.setDuelStatus(0);
				Player o = player.getTradeAndDuel().getPartner();
				if (o != null)
				{
						o.getTradeAndDuel().otherStakedItems.clear();
				}
				player.setDuelingWith(0);
				player.duelSpaceReq = 0;
				player.duelRequested = false;
				for (GameItem item : myStakedItems)
				{
						if (item.amount < 1)
								continue;
						if (ItemDefinition.getDefinitions()[item.id].stackable || ItemDefinition.getDefinitions()[item.id].note)
						{
								ItemAssistant.addItem(player, item.id, item.amount);
						}
						else
						{
								ItemAssistant.addItem(player, item.id, 1);
						}
				}
				myStakedItems.clear();
				for (int i = 0; i < player.duelRule.length; i++)
				{
						player.duelRule[i] = false;
				}
		}

		public void resetDuel()
		{
				player.getPA().showOption(3, 0, "Challenge", 3);
				player.headIconHints = 0;
				for (int i = 0; i < player.duelRule.length; i++)
				{
						player.duelRule[i] = false;
				}
				player.getPA().createPlayerHints(10, -1);
				player.setDuelStatus(0);
				player.canOffer = true;
				player.duelSpaceReq = 0;
				player.setDuelingWith(0);
				player.setFrozenLength(0);
				Follow.resetFollow(player);
				player.getPA().requestUpdates();
				Combat.resetPlayerAttack(player);
				player.duelRequested = false;
		}

		public void resetDuelItems()
		{
				myStakedItems.clear();
				otherStakedItems.clear();
		}

		public void changeDuelStuff()
		{
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						return;
				}
				o.setDuelStatus(1);
				player.setDuelStatus(1);
				o.getPA().sendFrame126("", 6684);
				player.getPA().sendFrame126("", 6684);
		}

		/**
		 * Remove player from the arena
		 */
		public void removeFromArena()
		{
				if (Area.inDuelArenaRing(player) && player.getDuelStatus() != 5)
				{
						player.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)), ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
				}
		}

		public final static int NO_FF = 0;

		public final static int NO_MOVEMENT = 1;

		public final static int NO_RANGED = 2;

		public final static int NO_MELEE = 3;

		public final static int NO_MAGIC = 4;

		public final static int NO_DRINK = 5;

		public final static int NO_FOOD = 6;

		public final static int NO_PRAYER = 7;

		public final static int OBSTACLES = 8;

		public final static int NO_FUN_WEP = 9;

		public final static int NO_SP_ATTACK = 10;

	//@formatter:off
	private final static String[] ruleNames =
	{
		"NO FF", "NO MOVEMENT", "NO RANGED", "NO MELEE", "NO MAGIC", "NO DRINK", "NO FOOD", "NO PRAYER", 
		"OBSTACLES", "NO FUN WEAPON", "NO SPECIAL ATTACK",
		"HELM", "CAPE", "AMULET", "WEAPON", "BODY", "LEGS", "GLOVES", "BOOTS", "RING", "ARROWS"
	};
	//@formatter:on



		public void selectRule(int ruleId)
		{ // rules
				Player o = player.getTradeAndDuel().getPartner();
				if (o == null)
				{
						return;
				}
				if (!player.canOffer)
				{
						PacketHandler.tradeAndDuelLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.tradeAndDuelLog.add("Rule bending");
						PacketHandler.tradeAndDuelLog.add("Partner: " + o.getPlayerName() + ", Rule bended: " + ruleNames[ruleId]);
						return;
				}
				o.timeDuelRuleChanged = System.currentTimeMillis();
				player.timeDuelRuleChanged = System.currentTimeMillis();
				changeDuelStuff();
				o.duelSlot = player.duelSlot;
				if (ruleId >= 11 && player.duelSlot > -1)
				{
						if (player.playerEquipment[player.duelSlot] > 0)
						{
								if (!player.duelRule[ruleId])
								{
										player.duelSpaceReq++;
								}
								else
								{
										player.duelSpaceReq--;
								}
						}
						if (o.playerEquipment[o.duelSlot] > 0)
						{
								if (!o.duelRule[ruleId])
								{
										o.duelSpaceReq++;
								}
								else
								{
										o.duelSpaceReq--;
								}
						}
				}

				if (ruleId >= 11)
				{
						if (ItemAssistant.getFreeInventorySlots(player) < (player.duelSpaceReq) || ItemAssistant.getFreeInventorySlots(o) < (o.duelSpaceReq))
						{
								player.playerAssistant.sendMessage("You or your opponent don't have the required space to set this rule.");
								if (player.playerEquipment[player.duelSlot] > 0)
								{
										player.duelSpaceReq--;
								}
								if (o.playerEquipment[o.duelSlot] > 0)
								{
										o.duelSpaceReq--;
								}
								return;
						}
				}

				if (!player.duelRule[ruleId])
				{
						player.duelRule[ruleId] = true;
						player.duelOption += ServerConstants.DUEL_RULE_ID[ruleId];
				}
				else
				{
						player.duelRule[ruleId] = false;
						player.duelOption -= ServerConstants.DUEL_RULE_ID[ruleId];
				}

				player.getPA().sendFrame87(286, player.duelOption);
				o.duelOption = player.duelOption;
				o.duelRule[ruleId] = player.duelRule[ruleId];
				o.getPA().sendFrame87(286, o.duelOption);

				if (player.duelRule[8])
				{
						if (player.duelRule[1])
						{
								player.duelTeleX = 3366 + Misc.random(12);
								o.duelTeleX = player.duelTeleX - 1;
								player.duelTeleY = 3246 + Misc.random(6);
								o.duelTeleY = player.duelTeleY;
						}
				}
				else
				{
						if (player.duelRule[1])
						{
								player.duelTeleX = 3335 + Misc.random(12);
								o.duelTeleX = player.duelTeleX - 1;
								player.duelTeleY = 3246 + Misc.random(6);
								o.duelTeleY = player.duelTeleY;
						}
				}

		}

		public boolean hasRequiredSpaceForDuel()
		{
				int amountOfItemsToRemove = 0;
				if (player.duelRule[11] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.HEAD_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[12] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.CAPE_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[13] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.AMULET_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[14] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT))
				{
						amountOfItemsToRemove++;
				}
				else
				{
						// If 2h weapon is wielded and shields is ticked off, then wapon must be removed.
						if (ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT) && player.duelRule[16])
						{
								if (ItemAssistant.is2handed(ItemAssistant.getItemName(player.getWieldedWeapon()), player.getWieldedWeapon()))
								{
										amountOfItemsToRemove++;
								}
						}
				}
				if (player.duelRule[15] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.BODY_SLOT))
				{
						amountOfItemsToRemove++;
				}

				// Shield slot.
				if (player.duelRule[16] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.SHIELD_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[17] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.LEG_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[18] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.HAND_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[19] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.FEET_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[20] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.RING_SLOT))
				{
						amountOfItemsToRemove++;
				}
				if (player.duelRule[21] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.ARROW_SLOT))
				{
						amountOfItemsToRemove++;
				}
				int slotsLeft = ItemAssistant.getFreeInventorySlots(player);
				if (slotsLeft < amountOfItemsToRemove)
				{
						player.getPA().sendMessage("You need " + (amountOfItemsToRemove - slotsLeft) + " more inventory spaces.");
						return false;
				}
				return true;
		}
}