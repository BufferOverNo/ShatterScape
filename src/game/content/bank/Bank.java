package game.content.bank;

import java.util.Arrays;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.starter.GameMode;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;

public class Bank
{

		public static void updateClientLastXAmount(Player player, int xAmount)
		{
				player.lastXAmount = xAmount;
				player.getPA().sendMessage(":packet:lastxamount " + player.lastXAmount);
		}

		public static void withdrawAllButOneAndLastX(Player player, String string)
		{
				if (!Bank.hasBankingRequirements(player, false))
				{
						return;
				}
				String split[] = string.split(" ");
				int slot = Integer.parseInt(split[2]);
				int itemId = Integer.parseInt(split[3]);
				if (string.contains("lastx"))
				{
						Bank.withdrawFromBank(player, itemId, slot, player.lastXAmount);
				}
				else if (string.contains("allbutone"))
				{
						player.withdrawAllButOne = true;
						Bank.withdrawFromBank(player, itemId, slot, player.bankingItemsN[slot] - 1);
						player.withdrawAllButOne = false;
				}
		}

		public static int getBankSizeAmount(Player player)
		{
				int extra = 0;
				if (GameMode.getGameMode(player, "IRON MAN"))
				{
						extra = 750;
				}
				else if (GameMode.getGameMode(player, "GLADIATOR"))
				{
						extra = 50;
				}

				if (player.isAdministratorRank())
				{
						extra += 650;
				}

				if (player.isLegendaryDonator())
				{
						extra += 400;
				}
				else if (player.isExtremeDonator())
				{
						extra += 300;
				}
				else if (player.isSuperDonator())
				{
						extra += 200;
				}
				else if (player.isDonator())
				{
						extra += 100;
				}
				return 300 + extra;
		}

		/**
		 * @param player
		 * @param ignoreUsingBankCheck TODO
		 * @return
		 * 			True, if the player has all the requirements to use the bank system.
		 */
		public static boolean hasBankingRequirements(Player player, boolean ignoreUsingBankCheck)
		{
				if (player.isAdministratorRank())
				{
						return true;
				}
				if (player.isInTrade() || player.getTradeStatus() == 1)
				{
						return false;
				}
				if (player.getDuelStatus() != 0)
				{
						return false;
				}
				if (!player.isUsingBankInterface() && !ignoreUsingBankCheck)
				{
						return false;
				}
				if (player.isTeleporting())
				{
						return false;
				}
				if (Area.inPVPArea(player))
				{
						return false;
				}
				if (Combat.inCombat(player))
				{
						if (ignoreUsingBankCheck)
						{
								Combat.inCombatAlert(player);
						}
						return false;
				}
				if (ignoreUsingBankCheck)
				{
						if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase(""))
						{
								player.getPA().sendMessage("You need to enter your pin first.");
								return false;
						}
				}
				return true;
		}

		public static boolean hasItemInBankAndDelete(Player player, int itemId, int amount)
		{
				boolean free = false;
				if (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)] != null)
				{
						free = BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].spawnFree;
				}
				if (BloodMoneyPrice.getBloodMoneyPrice(ItemAssistant.getUnNotedItem(itemId)) <= 0 && free)
				{
						return true;
				}
				itemId += 1;
				for (int i = 0; i < player.bankItems.length; i++)
				{
						if (player.bankItems[i] == itemId && player.bankItemsN[i] >= amount)
						{
								player.bankItemsN[i] -= amount;
								if (player.bankItemsN[i] == 0)
								{
										player.bankItems[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems1.length; i++)
				{

						if (player.bankItems1[i] == itemId && player.bankItems1N[i] >= amount)
						{
								player.bankItems1N[i] -= amount;
								if (player.bankItems1N[i] == 0)
								{
										player.bankItems1[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems2.length; i++)
				{

						if (player.bankItems2[i] == itemId && player.bankItems2N[i] >= amount)
						{
								player.bankItems2N[i] -= amount;
								if (player.bankItems2N[i] == 0)
								{
										player.bankItems2[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems3.length; i++)
				{
						if (player.bankItems3[i] == itemId && player.bankItems3N[i] >= amount)
						{
								player.bankItems3N[i] -= amount;
								if (player.bankItems3N[i] == 0)
								{
										player.bankItems3[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems4.length; i++)
				{
						if (player.bankItems4[i] == itemId && player.bankItems4N[i] >= amount)
						{
								player.bankItems4N[i] -= amount;
								if (player.bankItems4N[i] == 0)
								{
										player.bankItems4[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems5.length; i++)
				{
						if (player.bankItems5[i] == itemId && player.bankItems5N[i] >= amount)
						{
								player.bankItems5N[i] -= amount;
								if (player.bankItems5N[i] == 0)
								{
										player.bankItems5[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems6.length; i++)
				{
						if (player.bankItems6[i] == itemId && player.bankItems6N[i] >= amount)
						{
								player.bankItems6N[i] -= amount;
								if (player.bankItems6N[i] == 0)
								{
										player.bankItems6[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems7.length; i++)
				{
						if (player.bankItems7[i] == itemId && player.bankItems7N[i] >= amount)
						{
								player.bankItems7N[i] -= amount;
								if (player.bankItems7N[i] == 0)
								{
										player.bankItems7[i] = 0;
								}
								return true;
						}
				}
				for (int i = 0; i < player.bankItems8.length; i++)
				{
						if (player.bankItems8[i] == itemId && player.bankItems8N[i] >= amount)
						{
								player.bankItems8N[i] -= amount;
								if (player.bankItems8N[i] == 0)
								{
										player.bankItems8[i] = 0;
								}
								return true;
						}
				}
				return false;
		}

		/**
		 * True, if the player has the item in the bank.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to check for.
		 */
		public static boolean hasItemInBank(Player player, int itemId)
		{

				//Item id has to be +1
				for (int i = 0; i < player.bankItems.length; i++)
				{
						if (player.bankItems[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems1.length; i++)
				{
						if (player.bankItems1[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems2.length; i++)
				{
						if (player.bankItems2[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems3.length; i++)
				{
						if (player.bankItems3[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems4.length; i++)
				{
						if (player.bankItems4[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems5.length; i++)
				{
						if (player.bankItems5[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems6.length; i++)
				{
						if (player.bankItems6[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems7.length; i++)
				{
						if (player.bankItems7[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				for (int i = 0; i < player.bankItems8.length; i++)
				{
						if (player.bankItems8[i] == itemId)
						{
								player.itemInBankSlot = i;
								return true;
						}
				}
				return false;
		}

		public static int getItemAmountInBank(Player player, int itemId)
		{
				itemId++;
				for (int i = 0; i < player.bankItems.length; i++)
				{
						if (player.bankItems[i] == itemId)
						{
								return player.bankItemsN[i];
						}
				}
				for (int i = 0; i < player.bankItems1.length; i++)
				{
						if (player.bankItems1[i] == itemId)
						{
								return player.bankItems1N[i];
						}
				}
				for (int i = 0; i < player.bankItems2.length; i++)
				{
						if (player.bankItems2[i] == itemId)
						{
								return player.bankItems2N[i];
						}
				}
				for (int i = 0; i < player.bankItems3.length; i++)
				{
						if (player.bankItems3[i] == itemId)
						{
								return player.bankItems3N[i];
						}
				}
				for (int i = 0; i < player.bankItems4.length; i++)
				{
						if (player.bankItems4[i] == itemId)
						{
								return player.bankItems4N[i];
						}
				}
				for (int i = 0; i < player.bankItems5.length; i++)
				{
						if (player.bankItems5[i] == itemId)
						{
								return player.bankItems5N[i];
						}
				}
				for (int i = 0; i < player.bankItems6.length; i++)
				{
						if (player.bankItems6[i] == itemId)
						{
								return player.bankItems6N[i];
						}
				}
				for (int i = 0; i < player.bankItems7.length; i++)
				{
						if (player.bankItems7[i] == itemId)
						{
								return player.bankItems7N[i];
						}
				}
				for (int i = 0; i < player.bankItems8.length; i++)
				{
						if (player.bankItems8[i] == itemId)
						{
								return player.bankItems8N[i];
						}
				}
				return 0;
		}


		public static int getTabCount(Player player)
		{
				// count tabs
				int tabs = 0;
				if (!checkEmpty(player.bankItems1))
						tabs++;
				if (!checkEmpty(player.bankItems2))
						tabs++;
				if (!checkEmpty(player.bankItems3))
						tabs++;
				if (!checkEmpty(player.bankItems4))
						tabs++;
				if (!checkEmpty(player.bankItems5))
						tabs++;
				if (!checkEmpty(player.bankItems6))
						tabs++;
				if (!checkEmpty(player.bankItems7))
						tabs++;
				if (!checkEmpty(player.bankItems8))
						tabs++;
				return tabs;
		}

		public static boolean checkEmpty(int[] array)
		{
				for (int i = 0; i < array.length; i++)
				{
						if (array[i] != 0)
								return false;
				}
				return true;
		}

		public static int getBankItems(Player player, int tab)
		{
				int ta = 0, tb = 0, tc = 0, td = 0, te = 0, tf = 0, tg = 0, th = 0, ti = 0;
				for (int i = 0; i < player.bankItems.length; i++)
						if (player.bankItems[i] > 0)
								ta++;
				for (int i = 0; i < player.bankItems1.length; i++)
						if (player.bankItems1[i] > 0)
								tb++;
				for (int i = 0; i < player.bankItems2.length; i++)
						if (player.bankItems2[i] > 0)
								tc++;
				for (int i = 0; i < player.bankItems3.length; i++)
						if (player.bankItems3[i] > 0)
								td++;
				for (int i = 0; i < player.bankItems4.length; i++)
						if (player.bankItems4[i] > 0)
								te++;
				for (int i = 0; i < player.bankItems5.length; i++)
						if (player.bankItems5[i] > 0)
								tf++;
				for (int i = 0; i < player.bankItems6.length; i++)
						if (player.bankItems6[i] > 0)
								tg++;
				for (int i = 0; i < player.bankItems7.length; i++)
						if (player.bankItems7[i] > 0)
								th++;
				for (int i = 0; i < player.bankItems8.length; i++)
						if (player.bankItems8[i] > 0)
								ti++;
				if (tab == 0)
						return ta;
				if (tab == 1)
						return tb;
				if (tab == 2)
						return tc;
				if (tab == 3)
						return td;
				if (tab == 4)
						return te;
				if (tab == 5)
						return tf;
				if (tab == 6)
						return tg;
				if (tab == 7)
						return th;
				if (tab == 8)
						return ti;
				return ta + tb + tc + td + te + tf + tg + th + ti; // return total
		}

		/**
		 * Open bank
		 **/
		public static void openUpBank(Player player, int tab, boolean openInterface, boolean reArrangeBank)
		{
				if (player.isTeleporting())
				{
						return;
				}
				if (Area.inPVPArea(player) && !player.isAdministratorRank())
				{
						return;
				}
				if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase(""))
				{
						BankPin.open(player);
						return;
				}
				if (player.getObjectY() == 3338 && player.getObjectX() >= 2860 && player.getObjectX() <= 2862)
				{
						Achievements.checkCompletionSingle(player, 1005);
				}
				player.setUsingBankInterface(true);

				player.setLastBankTabOpened((byte) tab);
				player.getPA().sendFrame36(116, 0);
				if (player.takeAsNote)
				{
						player.getPA().sendFrame36(115, 1);
				}
				else
				{
						player.getPA().sendFrame36(115, 0);
				}
				if (player.isInTrade() || player.getTradeStatus() == 1)
				{
						Player o = PlayerHandler.players[player.getTradeWith()];
						if (o != null)
						{
								o.getTradeAndDuel().declineTrade1(true);
						}
				}
				if (player.getDuelStatus() == 1)
				{
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								o.getTradeAndDuel().resetDuel();
						}
				}
				if (player.getOutStream() != null && player != null)
				{
						player.bankingTab = tab;
						sendTabs(player);
						if (player.bankingTab == 0)
						{
								player.bankingItems = player.bankItems;
								player.bankingItemsN = player.bankItemsN;
						}
						if (player.bankingTab == 1)
						{
								player.bankingItems = player.bankItems1;
								player.bankingItemsN = player.bankItems1N;
						}
						if (player.bankingTab == 2)
						{
								player.bankingItems = player.bankItems2;
								player.bankingItemsN = player.bankItems2N;
						}
						if (player.bankingTab == 3)
						{
								player.bankingItems = player.bankItems3;
								player.bankingItemsN = player.bankItems3N;
						}
						if (player.bankingTab == 4)
						{
								player.bankingItems = player.bankItems4;
								player.bankingItemsN = player.bankItems4N;
						}
						if (player.bankingTab == 5)
						{
								player.bankingItems = player.bankItems5;
								player.bankingItemsN = player.bankItems5N;
						}
						if (player.bankingTab == 6)
						{
								player.bankingItems = player.bankItems6;
								player.bankingItemsN = player.bankItems6N;
						}
						if (player.bankingTab == 7)
						{
								player.bankingItems = player.bankItems7;
								player.bankingItemsN = player.bankItems7N;
						}
						if (player.bankingTab == 8)
						{
								player.bankingItems = player.bankItems8;
								player.bankingItemsN = player.bankItems8N;
						}
						if (reArrangeBank)
						{
								rearrangeBank(player);
						}
						if (openInterface)
						{

								ItemAssistant.resetItems(player, 5064);
								Bank.resetBank(player, false);
								if (player.isBot)
								{
										return;
								}
								player.getOutStream().createFrame(248);
								player.getOutStream().writeWordA(24959);
								player.getOutStream().writeWord(5063);
								player.flushOutStream();
						}
				}
		}

		public static void openCorrectTab(Player player, int tab, boolean updateBank)
		{
				if (!hasBankingRequirements(player, false))
				{
						return;
				}
				if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase(""))
				{
						BankPin.open(player);
						return;
				}

				player.setLastBankTabOpened((byte) tab);
				if (player.isInTrade() || player.getTradeStatus() == 1)
				{
						Player o = PlayerHandler.players[player.getTradeWith()];
						if (o != null)
						{
								if (!o.getPlayerName().equals(player.lastDueledWithName))
								{
										o = null;
								}
						}
						if (o != null)
						{
								o.getTradeAndDuel().declineTrade1(true);
						}
				}
				if (player.getDuelStatus() == 1)
				{
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								o.getTradeAndDuel().resetDuel();
						}
				}
				if (player.getOutStream() != null && player != null)
				{
						player.bankingTab = tab;
						if (player.bankingTab == 0)
						{
								player.bankingItems = player.bankItems;
								player.bankingItemsN = player.bankItemsN;
						}
						if (player.bankingTab == 1)
						{
								player.bankingItems = player.bankItems1;
								player.bankingItemsN = player.bankItems1N;
						}
						if (player.bankingTab == 2)
						{
								player.bankingItems = player.bankItems2;
								player.bankingItemsN = player.bankItems2N;
						}
						if (player.bankingTab == 3)
						{
								player.bankingItems = player.bankItems3;
								player.bankingItemsN = player.bankItems3N;
						}
						if (player.bankingTab == 4)
						{
								player.bankingItems = player.bankItems4;
								player.bankingItemsN = player.bankItems4N;
						}
						if (player.bankingTab == 5)
						{
								player.bankingItems = player.bankItems5;
								player.bankingItemsN = player.bankItems5N;
						}
						if (player.bankingTab == 6)
						{
								player.bankingItems = player.bankItems6;
								player.bankingItemsN = player.bankItems6N;
						}
						if (player.bankingTab == 7)
						{
								player.bankingItems = player.bankItems7;
								player.bankingItemsN = player.bankItems7N;
						}
						if (player.bankingTab == 8)
						{
								player.bankingItems = player.bankItems8;
								player.bankingItemsN = player.bankItems8N;
						}
						if (updateBank)
						{
								player.bankUpdated = true;
						}
				}
		}

		public static void sendTabs(Player player)
		{

				if (!hasBankingRequirements(player, false))
				{
						return;
				}
				if (player.doNotSendTabs)
				{
						player.doNotSendTabs = false;
						return;
				}

				// remove empty tab
				boolean moveRest = false;
				if (checkEmpty(player.bankItems1))
				{ // tab 1 empty
						player.bankItems1 = Arrays.copyOf(player.bankItems2, player.bankingItems.length);
						player.bankItems1N = Arrays.copyOf(player.bankItems2N, player.bankingItems.length);
						player.bankItems2 = new int[ServerConstants.BANK_SIZE];
						player.bankItems2N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems2) || moveRest)
				{
						player.bankItems2 = Arrays.copyOf(player.bankItems3, player.bankingItems.length);
						player.bankItems2N = Arrays.copyOf(player.bankItems3N, player.bankingItems.length);
						player.bankItems3 = new int[ServerConstants.BANK_SIZE];
						player.bankItems3N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems3) || moveRest)
				{
						player.bankItems3 = Arrays.copyOf(player.bankItems4, player.bankingItems.length);
						player.bankItems3N = Arrays.copyOf(player.bankItems4N, player.bankingItems.length);
						player.bankItems4 = new int[ServerConstants.BANK_SIZE];
						player.bankItems4N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems4) || moveRest)
				{
						player.bankItems4 = Arrays.copyOf(player.bankItems5, player.bankingItems.length);
						player.bankItems4N = Arrays.copyOf(player.bankItems5N, player.bankingItems.length);
						player.bankItems5 = new int[ServerConstants.BANK_SIZE];
						player.bankItems5N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems5) || moveRest)
				{
						player.bankItems5 = Arrays.copyOf(player.bankItems6, player.bankingItems.length);
						player.bankItems5N = Arrays.copyOf(player.bankItems6N, player.bankingItems.length);
						player.bankItems6 = new int[ServerConstants.BANK_SIZE];
						player.bankItems6N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems6) || moveRest)
				{
						player.bankItems6 = Arrays.copyOf(player.bankItems7, player.bankingItems.length);
						player.bankItems6N = Arrays.copyOf(player.bankItems7N, player.bankingItems.length);
						player.bankItems7 = new int[ServerConstants.BANK_SIZE];
						player.bankItems7N = new int[ServerConstants.BANK_SIZE];
						moveRest = true;
				}
				if (checkEmpty(player.bankItems7) || moveRest)
				{
						player.bankItems7 = Arrays.copyOf(player.bankItems8, player.bankingItems.length);
						player.bankItems7N = Arrays.copyOf(player.bankItems8N, player.bankingItems.length);
						player.bankItems8 = new int[ServerConstants.BANK_SIZE];
						player.bankItems8N = new int[ServerConstants.BANK_SIZE];
				}
				if (player.bankingTab > getTabCount(player))
				{
						player.bankingTab = getTabCount(player);
				}

				if (player.isUsingBankSearch())
				{
						player.bankingTab = player.originalTab;
				}
				if (moveRest)
				{
						player.doNotSendTabs = true;
						Bank.openUpBank(player, player.bankingTab, false, false);
				}
				player.getPA().sendFrame126(Integer.toString(getTabCount(player)), 27001);
				player.getPA().sendFrame126(Integer.toString(player.bankingTab), 27002);

				// Item amount on the tab item.

				itemOnInterface(player, 22035, 0, getInterfaceModel(0, player.bankItems1, player.bankItems1N), getAmount(player.bankItems1[0], player.bankItems1N[0]));
				itemOnInterface(player, 22036, 0, getInterfaceModel(0, player.bankItems2, player.bankItems2N), getAmount(player.bankItems2[0], player.bankItems2N[0]));
				itemOnInterface(player, 22037, 0, getInterfaceModel(0, player.bankItems3, player.bankItems3N), getAmount(player.bankItems3[0], player.bankItems3N[0]));
				itemOnInterface(player, 22038, 0, getInterfaceModel(0, player.bankItems4, player.bankItems4N), getAmount(player.bankItems4[0], player.bankItems4N[0]));
				itemOnInterface(player, 22039, 0, getInterfaceModel(0, player.bankItems5, player.bankItems5N), getAmount(player.bankItems5[0], player.bankItems5N[0]));
				itemOnInterface(player, 22040, 0, getInterfaceModel(0, player.bankItems6, player.bankItems6N), getAmount(player.bankItems6[0], player.bankItems6N[0]));
				itemOnInterface(player, 22041, 0, getInterfaceModel(0, player.bankItems7, player.bankItems7N), getAmount(player.bankItems7[0], player.bankItems7N[0]));
				itemOnInterface(player, 22042, 0, getInterfaceModel(0, player.bankItems8, player.bankItems8N), getAmount(player.bankItems8[0], player.bankItems8N[0]));
				/*
				itemOnInterface(player, 22035, 0, getInterfaceModel(0, player.bankItems1, player.bankItems1N), 1);
				itemOnInterface(player, 22036, 0, getInterfaceModel(0, player.bankItems2, player.bankItems2N), 1);
				itemOnInterface(player, 22037, 0, getInterfaceModel(0, player.bankItems3, player.bankItems3N), 1);
				itemOnInterface(player, 22038, 0, getInterfaceModel(0, player.bankItems4, player.bankItems4N), 1);
				itemOnInterface(player, 22039, 0, getInterfaceModel(0, player.bankItems5, player.bankItems5N), 1);
				itemOnInterface(player, 22040, 0, getInterfaceModel(0, player.bankItems6, player.bankItems6N), 1);
				itemOnInterface(player, 22041, 0, getInterfaceModel(0, player.bankItems7, player.bankItems7N), 1);
				itemOnInterface(player, 22042, 0, getInterfaceModel(0, player.bankItems8, player.bankItems8N), 1);
				*/
				player.getPA().sendFrame126("1", 27000);
		}

		public static void itemOnInterface(Player player, int frame, int slot, int id, int amount)
		{
				if (player.isBot)
				{
						return;
				}
				player.getOutStream().createFrameVarSizeWord(34);
				player.getOutStream().writeWord(frame);
				player.getOutStream().writeByte(slot);
				player.getOutStream().writeWord(id + 1);
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(amount);
				player.getOutStream().endFrameVarSizeWord();
		}

		public static int getInterfaceModel(int slot, int[] array, int[] arrayN)
		{
				int model = array[slot] - 1;
				if (model == 995)
				{
						if (arrayN[slot] > 9999)
						{
								model = 1004;
						}
						else if (arrayN[slot] > 999)
						{
								model = 1003;
						}
						else if (arrayN[slot] > 249)
						{
								model = 1002;
						}
						else if (arrayN[slot] > 99)
						{
								model = 1001;
						}
						else if (arrayN[slot] > 24)
						{
								model = 1000;
						}
						else if (arrayN[slot] > 4)
						{
								model = 999;
						}
						else if (arrayN[slot] > 3)
						{
								model = 998;
						}
						else if (arrayN[slot] > 2)
						{
								model = 997;
						}
						else if (arrayN[slot] > 1)
						{
								model = 996;
						}
				}
				return model;
		}

		public static int getAmount(int itemId, int amount)
		{
				itemId--;
				if (itemId <= 0)
				{
						return 1;
				}
				return amount;
		}

		public static boolean bankItem(Player player, int itemId, int fromSlot, int amount, int[] array, int[] arrayN)
		{
				if (!hasBankingRequirements(player, false))
				{
						return false;
				}
				if (player.playerItems[fromSlot] <= 0 || player.playerItemsN[fromSlot] <= 0)
				{
						return false;
				}
				if (!ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].note)
				{
						if (player.playerItems[fromSlot] <= 0)
						{
								return false;
						}
						if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].stackable || player.playerItemsN[fromSlot] > 1)
						{
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (array[i] == player.playerItems[fromSlot])
										{
												if (player.playerItemsN[fromSlot] < amount)
														amount = player.playerItemsN[fromSlot];
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}

								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (array[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										array[toBankSlot] = player.playerItems[fromSlot];
										if (player.playerItemsN[fromSlot] < amount)
										{
												amount = player.playerItemsN[fromSlot];
										}
										if ((arrayN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (arrayN[toBankSlot] + amount) > -1)
										{
												arrayN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Bank full!");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										player.bankUpdated = true;
										return true;
								}
								else if (alreadyInBank)
								{
										if ((arrayN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (arrayN[toBankSlot] + amount) > -1)
										{
												arrayN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Bank full!");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										player.bankUpdated = true;
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
						else
						{
								itemId = player.playerItems[fromSlot];
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (array[i] == player.playerItems[fromSlot])
										{
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}
								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (array[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														array[toBankSlot] = player.playerItems[firstPossibleSlot];
														arrayN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										player.bankUpdated = true;
										return true;
								}
								else if (alreadyInBank)
								{
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														arrayN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										player.bankUpdated = true;
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
				}
				else if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].note && !ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 2].note)
				{
						if (player.playerItems[fromSlot] <= 0)
						{
								return false;
						}
						if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].stackable || player.playerItemsN[fromSlot] > 1)
						{
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (array[i] == (player.playerItems[fromSlot] - 1))
										{
												if (player.playerItemsN[fromSlot] < amount)
														amount = player.playerItemsN[fromSlot];
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}

								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (array[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										array[toBankSlot] = (player.playerItems[fromSlot] - 1);
										if (player.playerItemsN[fromSlot] < amount)
										{
												amount = player.playerItemsN[fromSlot];
										}
										if ((arrayN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (arrayN[toBankSlot] + amount) > -1)
										{
												arrayN[toBankSlot] += amount;
										}
										else
										{
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										player.bankUpdated = true;
										return true;
								}
								else if (alreadyInBank)
								{
										if ((arrayN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (arrayN[toBankSlot] + amount) > -1)
										{
												arrayN[toBankSlot] += amount;
										}
										else
										{
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										player.bankUpdated = true;
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
						else
						{
								itemId = player.playerItems[fromSlot];
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (array[i] == (player.playerItems[fromSlot] - 1))
										{
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}
								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (array[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														array[toBankSlot] = (player.playerItems[firstPossibleSlot] - 1);
														arrayN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										player.bankUpdated = true;
										return true;
								}
								else if (alreadyInBank)
								{
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														arrayN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										player.bankUpdated = true;
										player.setInventoryUpdate(true);
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
				}
				else
				{
						player.playerAssistant.sendMessage("Item not supported1 " + (player.playerItems[fromSlot] - 1));
						return false;
				}
		}

		public static void shouldRearrangePreviousTab(Player player)
		{
				switch (player.bankingTab)
				{
						case 1:
								if (player.bankItems1[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 2:
								if (player.bankItems2[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 3:
								if (player.bankItems3[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 4:
								if (player.bankItems4[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 5:
								if (player.bankItems5[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 6:
								if (player.bankItems6[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 7:
								if (player.bankItems7[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
						case 8:
								if (player.bankItems8[0] == 0)
								{
										Bank.rearrangeBank(player);
								}
								break;
				}
		}

		public static int getTabforItem(Player player, int itemId)
		{
				itemId = ItemAssistant.getUnNotedItem(itemId);
				itemId = itemId + 1;
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems[i] == itemId)
						{
								return 0;
						}
						else if (player.bankItems1[i] == itemId)
						{
								return 1;
						}
						else if (player.bankItems2[i] == itemId)
						{
								return 2;
						}
						else if (player.bankItems3[i] == itemId)
						{
								return 3;
						}
						else if (player.bankItems4[i] == itemId)
						{
								return 4;
						}
						else if (player.bankItems5[i] == itemId)
						{
								return 5;
						}
						else if (player.bankItems6[i] == itemId)
						{
								return 6;
						}
						else if (player.bankItems7[i] == itemId)
						{
								return 7;
						}
						else if (player.bankItems8[i] == itemId)
						{
								return 8;
						}
				}

				return player.originalTab; // if not in bank add to current tab
		}

		public static boolean bankItem(Player player, int itemId, int fromSlot, int amount, boolean updateBank)
		{
				if (amount <= 0)
				{
						return false;
				}
				if (ItemAssistant.nulledItem(itemId - 1))
				{
						return false;
				}
				if (!hasBankingRequirements(player, false))
				{
						return false;
				}
				int unnoted = ItemAssistant.getUnNotedItem(itemId - 1) + 1;
				if (getBankAmount(player, "REMAINING") == 0)
				{
						if (!hasItemInBank(player, unnoted))
						{
								player.getPA().sendMessage("Your bank is full.");
								return false;
						}
				}
				if (player.playerItems[fromSlot] <= 0 || player.playerItemsN[fromSlot] <= 0)
				{
						return false;
				}
				int initialTab = player.bankingTab;
				openCorrectTab(player, getTabforItem(player, itemId - 1), updateBank); // Move to tab item is in before adding
				if (!ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].note)
				{
						if (player.playerItems[fromSlot] <= 0)
						{
								return false;
						}
						if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].stackable || player.playerItemsN[fromSlot] > 1)
						{
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (player.bankingItems[i] == player.playerItems[fromSlot])
										{
												if (player.playerItemsN[fromSlot] < amount)
												{
														amount = player.playerItemsN[fromSlot];
												}
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}

								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (player.bankingItems[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										if (player.playerItemsN[fromSlot] < amount)
										{
												amount = player.playerItemsN[fromSlot];
										}
										if ((player.bankingItemsN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[toBankSlot] + amount) > -1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (amount > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItems[toBankSlot] = player.playerItems[fromSlot];
												player.bankingItemsN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Bank full!");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else if (alreadyInBank)
								{
										if ((player.bankingItemsN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[toBankSlot] + amount) > -1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (amount > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItemsN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Bank full!");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
						else
						{
								itemId = player.playerItems[fromSlot];
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (player.bankingItems[i] == player.playerItems[fromSlot])
										{
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}
								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (player.bankingItems[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										int firstPossibleSlot = 0;
										boolean itemExists = false;

										if (amount == 1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (1 > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItems[toBankSlot] = player.playerItems[fromSlot];
												player.bankingItemsN[toBankSlot] += 1;
												ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, 1);
										}
										else
										{
												while (amount > 0)
												{
														itemExists = false;
														for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
														{
																if ((player.playerItems[i]) == itemId)
																{
																		firstPossibleSlot = i;
																		itemExists = true;
																		break;
																}
														}
														if (itemExists)
														{
																int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
																if (1 > maximumAmount)
																{
																		player.getPA().sendMessage("Your item stack cannot hold anymore!");
																		return false;
																}
																player.bankingItems[toBankSlot] = player.playerItems[firstPossibleSlot];
																player.bankingItemsN[toBankSlot] += 1;
																ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
																amount--;
														}
														else
														{
																amount = 0;
														}
												}
										}
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else if (alreadyInBank)
								{
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										if (amount == 1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (1 > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItemsN[toBankSlot] += 1;
												ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, 1);
										}
										else
										{
												while (amount > 0)
												{
														itemExists = false;
														for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
														{
																if ((player.playerItems[i]) == itemId)
																{
																		firstPossibleSlot = i;
																		itemExists = true;
																		break;
																}
														}
														if (itemExists)
														{
																int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
																if (1 > maximumAmount)
																{
																		player.getPA().sendMessage("Your item stack cannot hold anymore!");
																		return false;
																}
																player.bankingItemsN[toBankSlot] += 1;
																ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
																amount--;
														}
														else
														{
																amount = 0;
														}
												}
										}
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
				}
				else if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].note && !ItemDefinition.getDefinitions()[isNotedEnchantedEquipment(player.playerItems[fromSlot] - 2) ? player.playerItems[fromSlot] - 5 : player.playerItems[fromSlot] - 2].note)
				{
						if (player.playerItems[fromSlot] <= 0)
						{
								return false;
						}
						if (ItemDefinition.getDefinitions()[player.playerItems[fromSlot] - 1].stackable || player.playerItemsN[fromSlot] > 1)
						{
								int toBankSlot = 0;
								boolean itemExistsInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (player.bankingItems[i] == ((isNotedEnchantedEquipment(player.playerItems[fromSlot] - 1) ? player.playerItems[fromSlot] - 3 : player.playerItems[fromSlot] - 1)))
										{
												if (player.playerItemsN[fromSlot] < amount)
												{
														amount = player.playerItemsN[fromSlot];
												}
												itemExistsInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}

								if (!itemExistsInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (player.bankingItems[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										if (player.playerItemsN[fromSlot] < amount)
										{
												amount = player.playerItemsN[fromSlot];
										}
										if ((player.bankingItemsN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[toBankSlot] + amount) > -1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (amount > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItems[toBankSlot] = isNotedEnchantedEquipment(player.playerItems[fromSlot] - 1) ? (player.playerItems[fromSlot] - 3) : (player.playerItems[fromSlot] - 1);
												player.bankingItemsN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Full slot.");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else if (itemExistsInBank)
								{
										if ((player.bankingItemsN[toBankSlot] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[toBankSlot] + amount) > -1)
										{
												int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
												if (amount > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return false;
												}
												player.bankingItemsN[toBankSlot] += amount;
										}
										else
										{
												player.playerAssistant.sendMessage("Full slot.");
												return false;
										}
										ItemAssistant.deleteItemFromInventory(player, (player.playerItems[fromSlot] - 1), fromSlot, amount);
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
						else
						{
								itemId = player.playerItems[fromSlot];
								int toBankSlot = 0;
								boolean alreadyInBank = false;
								for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
								{
										if (player.bankingItems[i] == (player.playerItems[fromSlot] - 1))
										{
												alreadyInBank = true;
												toBankSlot = i;
												i = ServerConstants.BANK_SIZE + 1;
										}
								}
								if (!alreadyInBank && freeBankSlots(player) > 0)
								{
										for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
										{
												if (player.bankingItems[i] <= 0)
												{
														toBankSlot = i;
														i = ServerConstants.BANK_SIZE + 1;
												}
										}
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
														if (1 > maximumAmount)
														{
																player.getPA().sendMessage("Your item stack cannot hold anymore!");
																return false;
														}
														player.bankingItems[toBankSlot] = (player.playerItems[firstPossibleSlot] - 1);
														player.bankingItemsN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else if (alreadyInBank)
								{
										int firstPossibleSlot = 0;
										boolean itemExists = false;
										while (amount > 0)
										{
												itemExists = false;
												for (int i = firstPossibleSlot; i < player.playerItems.length; i++)
												{
														if ((player.playerItems[i]) == itemId)
														{
																firstPossibleSlot = i;
																itemExists = true;
																i = 30;
														}
												}
												if (itemExists)
												{
														int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[toBankSlot];
														if (1 > maximumAmount)
														{
																player.getPA().sendMessage("Your item stack cannot hold anymore!");
																return false;
														}
														player.bankingItemsN[toBankSlot] += 1;
														ItemAssistant.deleteItemFromInventory(player, (player.playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
														amount--;
												}
												else
												{
														amount = 0;
												}
										}
										if (updateBank)
										{
												player.bankUpdated = true;
												openUpBank(player, initialTab, false, false);
										}
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("Bank full!");
										return false;
								}
						}
				}
				else
				{
						return false;
				}
		}

		public static int freeBankSlots(Player player)
		{
				int freeS = 0;
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankingItems[i] <= 0)
						{
								freeS++;
						}
				}
				return freeS;
		}

		public static void toTab(Player player, int tab, int fromSlot)
		{
				if (!hasBankingRequirements(player, false))
				{
						return;
				}
				if (tab == player.bankingTab)
				{
						return;
				}
				if (tab > getTabCount(player) + 1)
				{
						return;
				}
				if (getBankItems(player, tab) >= 352)
				{
						player.playerAssistant.sendMessage("You can't store any more items in this tab!");
						return;
				}
				int id = player.bankingItems[fromSlot];

				int amount = player.bankingItemsN[fromSlot];
				int[] invItems = new int[28];
				int[] invItemsN = new int[28];
				for (int i = 0; i < player.playerItems.length; i++)
				{
						invItems[i] = player.playerItems[i];
						invItemsN[i] = player.playerItemsN[i];
						player.playerItems[i] = 0;
						player.playerItemsN[i] = 0;
				}
				player.playerItems[0] = id;
				player.playerItemsN[0] = amount;
				player.bankingItems[fromSlot] = -1;
				player.bankingItemsN[fromSlot] = 0;
				if (tab == 0)
						bankItem(player, id, 0, amount, player.bankItems, player.bankItemsN);
				else if (tab == 1)
						bankItem(player, id, 0, amount, player.bankItems1, player.bankItems1N);
				else if (tab == 2)
						bankItem(player, id, 0, amount, player.bankItems2, player.bankItems2N);
				else if (tab == 3)
						bankItem(player, id, 0, amount, player.bankItems3, player.bankItems3N);
				else if (tab == 4)
						bankItem(player, id, 0, amount, player.bankItems4, player.bankItems4N);
				else if (tab == 5)
						bankItem(player, id, 0, amount, player.bankItems5, player.bankItems5N);
				else if (tab == 6)
						bankItem(player, id, 0, amount, player.bankItems6, player.bankItems6N);
				else if (tab == 7)
						bankItem(player, id, 0, amount, player.bankItems7, player.bankItems7N);
				else if (tab == 8)
						bankItem(player, id, 0, amount, player.bankItems8, player.bankItems8N);
				for (int i = 0; i < invItems.length; i++)
				{
						player.playerItems[i] = invItems[i];
						player.playerItemsN[i] = invItemsN[i];
				}
				openUpBank(player, player.bankingTab, true, false); // refresh
				openUpBank(player, player.bankingTab, true, false); // refresh twice to ensure update
				player.setInventoryUpdate(true);

		}

		public static void withdrawFromBank(Player player, int itemId, int fromSlot, int amount)
		{
				if (!hasBankingRequirements(player, false))
				{
						return;
				}
				if (player.isUsingBankSearch())
				{
						if (fromSlot > player.bankSearchedItems.size() - 1)
						{
								return;
						}
						String[] args = player.bankSearchedItems.get(fromSlot).split(" ");
						itemId = Integer.parseInt(args[0]) - 1;
						player.bankingTab = getTabforItem(player, itemId);
						hasItemInBank(player, itemId + 1);

						// fromSlot is currently the slot of where the item is on the player's search screen.
						// Now we are changing it to the slot of where the item is in the tab the item resides in.
						fromSlot = player.itemInBankSlot;
						if (player.bankingTab == 0)
						{
								player.bankingItems = player.bankItems;
								player.bankingItemsN = player.bankItemsN;
						}
						if (player.bankingTab == 1)
						{
								player.bankingItems = player.bankItems1;
								player.bankingItemsN = player.bankItems1N;
						}
						if (player.bankingTab == 2)
						{
								player.bankingItems = player.bankItems2;
								player.bankingItemsN = player.bankItems2N;
						}
						if (player.bankingTab == 3)
						{
								player.bankingItems = player.bankItems3;
								player.bankingItemsN = player.bankItems3N;
						}
						if (player.bankingTab == 4)
						{
								player.bankingItems = player.bankItems4;
								player.bankingItemsN = player.bankItems4N;
						}
						if (player.bankingTab == 5)
						{
								player.bankingItems = player.bankItems5;
								player.bankingItemsN = player.bankItems5N;
						}
						if (player.bankingTab == 6)
						{
								player.bankingItems = player.bankItems6;
								player.bankingItemsN = player.bankItems6N;
						}
						if (player.bankingTab == 7)
						{
								player.bankingItems = player.bankItems7;
								player.bankingItemsN = player.bankItems7N;
						}
						if (player.bankingTab == 8)
						{
								player.bankingItems = player.bankItems8;
								player.bankingItemsN = player.bankItems8N;
						}
						if (player.withdrawAllButOne)
						{
								amount = player.bankingItemsN[fromSlot] - 1;
						}
				}
				int tempT = player.bankingTab;
				int collect = amount;
				for (int i = 0; i < player.tempItems.length; i++)
				{
						if (player.tempItems[i] == itemId + 1 || player.tempItems[i] == itemId)
						{
								int count = Math.min(player.tempItemsN[i], collect);
								if (collect == -1)
								{
										count = player.tempItemsN[i];
								}
								player.bankingTab = (player.tempItemsT[i]);
								if (player.bankingTab == 0)
								{
										player.bankingItems = player.bankItems;
										player.bankingItemsN = player.bankItemsN;
								}
								if (player.bankingTab == 1)
								{
										player.bankingItems = player.bankItems1;
										player.bankingItemsN = player.bankItems1N;
								}
								if (player.bankingTab == 2)
								{
										player.bankingItems = player.bankItems2;
										player.bankingItemsN = player.bankItems2N;
								}
								if (player.bankingTab == 3)
								{
										player.bankingItems = player.bankItems3;
										player.bankingItemsN = player.bankItems3N;
								}
								if (player.bankingTab == 4)
								{
										player.bankingItems = player.bankItems4;
										player.bankingItemsN = player.bankItems4N;
								}
								if (player.bankingTab == 5)
								{
										player.bankingItems = player.bankItems5;
										player.bankingItemsN = player.bankItems5N;
								}
								if (player.bankingTab == 6)
								{
										player.bankingItems = player.bankItems6;
										player.bankingItemsN = player.bankItems6N;
								}
								if (player.bankingTab == 7)
								{
										player.bankingItems = player.bankItems7;
										player.bankingItemsN = player.bankItems7N;
								}
								if (player.bankingTab == 8)
								{
										player.bankingItems = player.bankItems8;
										player.bankingItemsN = player.bankItems8N;
								}
								withdrawFromBank(player, itemId + 1, player.tempItemsS[i], count);
								collect -= count;
						}
				}

				player.bankingTab = tempT;
				if (amount > 0)
				{
						if (player.bankingItems[fromSlot] > 0)
						{
								boolean nulledNoted = ItemDefinition.getDefinitions()[player.bankingItems[fromSlot]] == null ? true : false;
								if (!player.takeAsNote)
								{
										if (ItemDefinition.getDefinitions()[player.bankingItems[fromSlot] - 1].stackable)
										{
												if (player.bankingItemsN[fromSlot] > amount)
												{
														if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amount))
														{
																player.bankingItemsN[fromSlot] -= amount;
																player.bankUpdated = true;
														}
												}
												else
												{
														if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), player.bankingItemsN[fromSlot]))
														{
																player.bankingItems[fromSlot] = 0;
																player.bankingItemsN[fromSlot] = 0;
																player.bankUpdated = true;
														}
												}
										}
										else
										{
												while (amount > 0)
												{
														if (player.bankingItemsN[fromSlot] > 0)
														{
																if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), 1))
																{
																		player.bankingItemsN[fromSlot] += -1;
																		amount--;
																}
																else
																{
																		amount = 0;
																}
														}
														else
														{
																amount = 0;
														}
												}
												player.bankUpdated = true;
										}
								}
								else if (!nulledNoted && player.takeAsNote && ItemDefinition.getDefinitions()[isNotedEnchantedEquipment(player.bankingItems[fromSlot] + 2) ? player.bankingItems[fromSlot] + 2 : player.bankingItems[fromSlot]].note)
								{
										if (player.bankingItemsN[fromSlot] > amount)
										{
												if (ItemAssistant.addItem(player, isNotedEnchantedEquipment(player.bankingItems[fromSlot] + 2) ? player.bankingItems[fromSlot] + 2 : player.bankingItems[fromSlot], amount))
												{
														player.bankingItemsN[fromSlot] -= amount;
														player.bankUpdated = true;
												}
										}
										else
										{
												if (ItemAssistant.addItem(player, isNotedEnchantedEquipment(player.bankingItems[fromSlot] + 2) ? player.bankingItems[fromSlot] + 2 : player.bankingItems[fromSlot], player.bankingItemsN[fromSlot]))
												{
														player.bankingItems[fromSlot] = 0;
														player.bankingItemsN[fromSlot] = 0;
														player.bankUpdated = true;
												}
										}
								}
								else
								{
										player.playerAssistant.sendMessage("This item can't be withdrawn as a note.");
										if (ItemDefinition.getDefinitions()[player.bankingItems[fromSlot] - 1].stackable)
										{
												if (player.bankingItemsN[fromSlot] > amount)
												{
														if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amount))
														{
																player.bankingItemsN[fromSlot] -= amount;
																player.bankUpdated = true;
														}
												}
												else
												{
														if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), player.bankingItemsN[fromSlot]))
														{
																player.bankingItems[fromSlot] = 0;
																player.bankingItemsN[fromSlot] = 0;
																player.bankUpdated = true;
														}
												}
										}
										else
										{
												while (amount > 0)
												{
														if (player.bankingItemsN[fromSlot] > 0)
														{
																if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), 1))
																{
																		player.bankingItemsN[fromSlot] += -1;
																		amount--;
																}
																else
																{
																		amount = 0;
																}
														}
														else
														{
																amount = 0;
														}
												}
												player.bankUpdated = true;
										}
								}
						}
						sendTabs(player);
				}
		}

		static long start;

		public static void rearrangeBank(Player player)
		{
				int highestSlot = 0;
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankingItems[i] != 0)
						{
								if (highestSlot <= i)
								{
										highestSlot = i;
								}
						}
				}
				for (int i = 0; i <= highestSlot; i++)
				{
						if (player.bankingItems[i] == 0)
						{
								boolean stop = false;

								for (int k = i; k <= highestSlot; k++)
								{
										if (player.bankingItems[k] != 0 && !stop)
										{
												int spots = k - i;
												for (int j = k; j <= highestSlot; j++)
												{
														player.bankingItems[j - spots] = player.bankingItems[j];
														player.bankingItemsN[j - spots] = player.bankingItemsN[j];
														stop = true;
														player.bankingItems[j] = 0;
														player.bankingItemsN[j] = 0;
												}
										}
								}
						}
				}
		}

		public static void resetBank(Player player, boolean ignoreSearchCheck)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.isUsingBankSearch() && !ignoreSearchCheck)
				{
						search(player, player.bankSearchString, true);
						return;
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(5382);
				player.getOutStream().writeWord(ServerConstants.BANK_SIZE);
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankingItemsN[i] > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(player.bankingItemsN[i]);
						}
						else
						{
								player.getOutStream().writeByte(player.bankingItemsN[i]);
						}
						if (player.bankingItemsN[i] < 1)
						{
								player.bankingItems[i] = 0;
						}
						if (player.bankingItems[i] > ServerConstants.MAX_ITEM_ID || player.bankingItems[i] < 0)
						{
								player.bankingItems[i] = ServerConstants.MAX_ITEM_ID;
						}
						player.getOutStream().writeWordBigEndianA(player.bankingItems[i]);
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
				player.getPA().sendFrame126(Integer.toString(getBankAmount(player, "AMOUNT")), 22033);
				player.getPA().sendFrame126(Integer.toString(getBankSizeAmount(player)), 22034);
		}

		public static int getBankAmount(Player player, String type)
		{
				int tab0 = 0;
				int tab1 = 0;
				int tab2 = 0;
				int tab3 = 0;
				int tab4 = 0;
				int tab5 = 0;
				int tab6 = 0;
				int tab7 = 0;
				int tab8 = 0;

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems[i] > 0)
						{
								tab0++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems1[i] > 0)
						{
								tab1++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems2[i] > 0)
						{
								tab2++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems3[i] > 0)
						{
								tab3++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems4[i] > 0)
						{
								tab4++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems5[i] > 0)
						{
								tab5++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems6[i] > 0)
						{
								tab6++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems7[i] > 0)
						{
								tab7++;
						}
				}

				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankItems8[i] > 0)
						{
								tab8++;
						}
				}

				int total = tab0 + tab1 + tab2 + tab3 + tab4 + tab5 + tab6 + tab7 + tab8;
				if (type.equals("AMOUNT"))
				{
						return total;
				}
				else if (type.equals("REMAINING"))
				{
						return getBankSizeAmount(player) - total;
				}

				return 0;
		}

		public static void swapBankItem(Player player, int from, int to)
		{
				if (player.bankingTab == 0)
				{
						int tempI = player.bankItems[from];
						int tempN = player.bankItemsN[from];
						player.bankItems[from] = player.bankItems[to];
						player.bankItemsN[from] = player.bankItemsN[to];
						player.bankItems[to] = tempI;
						player.bankItemsN[to] = tempN;
				}
				else if (player.bankingTab == 1)
				{
						int tempI = player.bankItems1[from];
						int tempN = player.bankItems1N[from];
						player.bankItems1[from] = player.bankItems1[to];
						player.bankItems1N[from] = player.bankItems1N[to];
						player.bankItems1[to] = tempI;
						player.bankItems1N[to] = tempN;
				}
				else if (player.bankingTab == 2)
				{
						int tempI = player.bankItems2[from];
						int tempN = player.bankItems2N[from];
						player.bankItems2[from] = player.bankItems2[to];
						player.bankItems2N[from] = player.bankItems2N[to];
						player.bankItems2[to] = tempI;
						player.bankItems2N[to] = tempN;
				}
				else if (player.bankingTab == 3)
				{
						int tempI = player.bankItems3[from];
						int tempN = player.bankItems3N[from];
						player.bankItems3[from] = player.bankItems3[to];
						player.bankItems3N[from] = player.bankItems3N[to];
						player.bankItems3[to] = tempI;
						player.bankItems3N[to] = tempN;
				}
				else if (player.bankingTab == 4)
				{
						int tempI = player.bankItems4[from];
						int tempN = player.bankItems4N[from];
						player.bankItems4[from] = player.bankItems4[to];
						player.bankItems4N[from] = player.bankItems4N[to];
						player.bankItems4[to] = tempI;
						player.bankItems4N[to] = tempN;
				}
				else if (player.bankingTab == 5)
				{
						int tempI = player.bankItems5[from];
						int tempN = player.bankItems5N[from];
						player.bankItems5[from] = player.bankItems5[to];
						player.bankItems5N[from] = player.bankItems5N[to];
						player.bankItems5[to] = tempI;
						player.bankItems5N[to] = tempN;
				}
				else if (player.bankingTab == 6)
				{
						int tempI = player.bankItems6[from];
						int tempN = player.bankItems6N[from];
						player.bankItems6[from] = player.bankItems6[to];
						player.bankItems6N[from] = player.bankItems6N[to];
						player.bankItems6[to] = tempI;
						player.bankItems6N[to] = tempN;
				}
				else if (player.bankingTab == 7)
				{
						int tempI = player.bankItems7[from];
						int tempN = player.bankItems7N[from];
						player.bankItems7[from] = player.bankItems7[to];
						player.bankItems7N[from] = player.bankItems7N[to];
						player.bankItems7[to] = tempI;
						player.bankItems7N[to] = tempN;
				}
				else if (player.bankingTab == 8)
				{
						int tempI = player.bankItems8[from];
						int tempN = player.bankItems8N[from];
						player.bankItems8[from] = player.bankItems8[to];
						player.bankItems8N[from] = player.bankItems8N[to];
						player.bankItems8[to] = tempI;
						player.bankItems8N[to] = tempN;
				}
		}

		/**
		 * Add item/s to the player's bank.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to add to the player's bank.
		 * @param amount
		 * 			The amount of the itemID.
		 */
		public static boolean addItemToBank(Player player, int itemId, int amount, boolean updateBankVisual)
		{
				if (amount <= 0)
				{
						return false;
				}
				itemId++;
				// Items saved in bank are +1, so coins is 996 instead.
				itemId = ItemAssistant.getUnNotedItem(itemId - 1);
				if (getBankAmount(player, "REMAINING") == 0)
				{
						if (!hasItemInBank(player, itemId + 1))
						{
								player.getPA().sendMessage("Your bank is full.");
								return false;
						}
				}
				switch (getTabforItem(player, itemId))
				{
						case 0:
								player.bankingItems = player.bankItems;
								player.bankingItemsN = player.bankItemsN;
								break;
						case 1:
								player.bankingItems = player.bankItems1;
								player.bankingItemsN = player.bankItems1N;
								break;
						case 2:
								player.bankingItems = player.bankItems2;
								player.bankingItemsN = player.bankItems2N;
								break;
						case 3:
								player.bankingItems = player.bankItems3;
								player.bankingItemsN = player.bankItems3N;
								break;
						case 4:
								player.bankingItems = player.bankItems4;
								player.bankingItemsN = player.bankItems4N;
								break;
						case 5:
								player.bankingItems = player.bankItems5;
								player.bankingItemsN = player.bankItems5N;
								break;
						case 6:
								player.bankingItems = player.bankItems6;
								player.bankingItemsN = player.bankItems6N;
								break;
						case 7:
								player.bankingItems = player.bankItems7;
								player.bankingItemsN = player.bankItems7N;
								break;
						case 8:
								player.bankingItems = player.bankItems8;
								player.bankingItemsN = player.bankItems8N;
								break;
				}
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (player.bankingItems[i] <= 0 || player.bankingItems[i] == itemId + 1 && player.bankingItemsN[i] + amount < Integer.MAX_VALUE)
						{
								int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[i];
								if (amount > maximumAmount)
								{
										player.getPA().sendMessage("Your item stack cannot hold anymore!");
										return false;
								}
								player.bankingItems[i] = itemId + 1;
								player.bankingItemsN[i] += amount;
								if (updateBankVisual)
								{
										resetBank(player, false);
								}
								return true;
						}
				}
				openUpBank(player, getTabforItem(player, itemId), false, false);
				return false;
		}

		public static boolean isNotedEnchantedEquipment(int itemId)
		{
				if (itemId == 7401 || itemId == 7402 || itemId == 7403)
				{
						return true;
				}
				return false;
		}

		public static void search(Player player, String string, boolean ignoreSameString)
		{
				if (string.isEmpty())
				{
						Bank.resetBank(player, true);
						return;
				}
				if (string.equals(player.bankSearchString) && !ignoreSameString)
				{
						return;
				}
				String search = string;
				player.bankSearchString = string;
				player.bankSearchedItems.clear();
				player.setUsingBankSearch(true);
				for (int i = 0; i < player.bankItems.length; i++)
				{
						if (player.bankItems[i] > 0 && player.bankItemsN[i] < 1)
						{
								player.bankItems[i] = 0;
						}
						if (player.bankItems[i] != 0 && ItemAssistant.getItemName(player.bankItems[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems[i] + " " + player.bankItemsN[i]);
						}
				}
				for (int i = 0; i < player.bankItems1.length; i++)
				{
						if (player.bankItems1[i] > 0 && player.bankItems1N[i] < 1)
						{
								player.bankItems1[i] = 0;
						}
						if (player.bankItems1[i] != 0 && ItemAssistant.getItemName(player.bankItems1[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems1[i] + " " + player.bankItems1N[i]);
						}
				}
				for (int i = 0; i < player.bankItems2.length; i++)
				{
						if (player.bankItems2[i] > 0 && player.bankItems2N[i] < 1)
						{
								player.bankItems2[i] = 0;
						}
						if (player.bankItems2[i] != 0 && ItemAssistant.getItemName(player.bankItems2[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems2[i] + " " + player.bankItems2N[i]);
						}
				}
				for (int i = 0; i < player.bankItems3.length; i++)
				{
						if (player.bankItems3[i] > 0 && player.bankItems3N[i] < 1)
						{
								player.bankItems3[i] = 0;
						}
						if (player.bankItems3[i] != 0 && ItemAssistant.getItemName(player.bankItems3[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems3[i] + " " + player.bankItems3N[i]);
						}
				}
				for (int i = 0; i < player.bankItems4.length; i++)
				{
						if (player.bankItems4[i] > 0 && player.bankItems4N[i] < 1)
						{
								player.bankItems4[i] = 0;
						}
						if (player.bankItems4[i] != 0 && ItemAssistant.getItemName(player.bankItems4[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems4[i] + " " + player.bankItems4N[i]);
						}
				}
				for (int i = 0; i < player.bankItems5.length; i++)
				{
						if (player.bankItems5[i] > 0 && player.bankItems5N[i] < 1)
						{
								player.bankItems5[i] = 0;
						}
						if (player.bankItems5[i] != 0 && ItemAssistant.getItemName(player.bankItems5[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems5[i] + " " + player.bankItems5N[i]);
						}
				}
				for (int i = 0; i < player.bankItems6.length; i++)
				{
						if (player.bankItems6[i] > 0 && player.bankItems6N[i] < 1)
						{
								player.bankItems6[i] = 0;
						}
						if (player.bankItems6[i] != 0 && ItemAssistant.getItemName(player.bankItems6[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems6[i] + " " + player.bankItems6N[i]);
						}
				}
				for (int i = 0; i < player.bankItems7.length; i++)
				{
						if (player.bankItems7[i] > 0 && player.bankItems7N[i] < 1)
						{
								player.bankItems7[i] = 0;
						}
						if (player.bankItems7[i] != 0 && ItemAssistant.getItemName(player.bankItems7[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems7[i] + " " + player.bankItems7N[i]);
						}
				}
				for (int i = 0; i < player.bankItems8.length; i++)
				{
						if (player.bankItems8[i] > 0 && player.bankItems8N[i] < 1)
						{
								player.bankItems8[i] = 0;
						}
						if (player.bankItems8[i] != 0 && ItemAssistant.getItemName(player.bankItems8[i] - 1).toLowerCase().contains(search))
						{
								player.bankSearchedItems.add(player.bankItems8[i] + " " + player.bankItems8N[i]);
						}
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(5382);
				player.getOutStream().writeWord(ServerConstants.BANK_SIZE);
				int amount = 0;
				int itemId = 0;
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						if (i < player.bankSearchedItems.size())
						{
								String[] args = player.bankSearchedItems.get(i).split(" ");
								itemId = Integer.parseInt(args[0]);
								amount = Integer.parseInt(args[1]);
						}

						// When withdrawing the last stack of an item, the quantity will go to 0, but not the bankItem number.
						// So we are required to set it to 0, maybe because of potential dupes.
						if (amount < 1)
						{
								itemId = 0;
								amount = 0;
						}
						if (amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(amount);
						}
						else
						{
								player.getOutStream().writeByte(amount);
						}
						if (i < player.bankSearchedItems.size())
						{
								player.getOutStream().writeWordBigEndianA(itemId);
						}
						else
						{
								player.getOutStream().writeWordBigEndianA(0);
						}
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();

		}

}