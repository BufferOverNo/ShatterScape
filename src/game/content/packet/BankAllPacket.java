package game.content.packet;

import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Bank All Items
 **/
public class BankAllPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int removeSlot = player.getInStream().readUnsignedWordA();
				int interfaceId = player.getInStream().readUnsignedWord();
				int removeId = player.getInStream().readUnsignedWordA();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
						PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
						PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
				}


				if (ItemAssistant.nulledItem(removeId))
				{
						return;
				}

				switch (interfaceId)
				{

						case 7423:
								DepositBox.depositItemAmount(player, removeSlot, 0);
								break;
						case 3900:
								player.getShops().buyItem(removeId, removeSlot, 10);
								break;

						case 3823:
								player.getShops().sellItemToShop(player, removeId, removeSlot, 10);
								break;

						case 5064:
								if (player.isInTrade())
								{
										return;
								}
								if (ItemDefinition.getDefinitions()[removeId].stackable)
								{
										Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, player.playerItemsN[removeSlot], true);
								}
								else
								{
										Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, ItemAssistant.itemAmount(player, player.playerItems[removeSlot]), true);
								}
								break;

						case 5382: // Withdraw all
								Bank.withdrawFromBank(player, player.bankingItems[removeSlot], removeSlot, player.bankingItemsN[removeSlot]);
								break;

						case 3322:
								if (player.getDuelStatus() <= 0 && player.isInTrade())
								{
										if (ItemDefinition.getDefinitions()[removeId].stackable)
										{
												player.getTradeAndDuel().tradeItem(removeId, removeSlot, player.playerItemsN[removeSlot]);
										}
										else
										{
												player.getTradeAndDuel().tradeItem(removeId, removeSlot, 28);
										}
								}
								else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2)
								{
										if (ItemDefinition.getDefinitions()[removeId].stackable || ItemDefinition.getDefinitions()[removeId].note)
										{
												player.getTradeAndDuel().stakeItem(removeId, removeSlot, player.playerItemsN[removeSlot]);
										}
										else
										{
												player.getTradeAndDuel().stakeItem(removeId, removeSlot, 28);
										}
								}
								break;

						case 3415:
								if (player.getDuelStatus() <= 0)
								{
										if (ItemDefinition.getDefinitions()[removeId].stackable)
										{
												if (removeSlot > player.getTradeAndDuel().offeredItems.size() - 1)
												{
														return;
												}
												for (GameItem item : player.getTradeAndDuel().offeredItems)
												{
														if (item.id == removeId)
														{
																player.getTradeAndDuel().fromTrade(removeId, removeSlot, player.getTradeAndDuel().offeredItems.get(removeSlot).amount);
														}
												}
										}
										else
										{
												for (GameItem item : player.getTradeAndDuel().offeredItems)
												{
														if (item.id == removeId)
														{
																player.getTradeAndDuel().fromTrade(removeId, removeSlot, 28);
														}
												}
										}
								}
								break;

						case 6669:
								if (ItemDefinition.getDefinitions()[removeId].stackable || ItemDefinition.getDefinitions()[removeId].note)
								{
										if (removeSlot > player.getTradeAndDuel().myStakedItems.size() - 1)
										{
												return;
										}
										for (GameItem item : player.getTradeAndDuel().myStakedItems)
										{
												if (item.id == removeId)
												{
														player.getTradeAndDuel().fromDuel(removeId, removeSlot, player.getTradeAndDuel().myStakedItems.get(removeSlot).amount);
												}
										}

								}
								else
								{
										player.getTradeAndDuel().fromDuel(removeId, removeSlot, 28);
								}
								break;

				}
		}

}