package game.content.packet;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.skilling.smithing.Smithing;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;


/**
 * Remove Item
 **/
public class RemoveItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int interfaceId = player.getInStream().readUnsignedWordA();
				int removeSlot = player.getInStream().readUnsignedWordA();
				int removeId = player.getInStream().readUnsignedWordA();
				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
						PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
						PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
				}


				if (ItemAssistant.nulledItem(removeId))
				{
						return;
				}
				switch (interfaceId)
				{

						case 7423:
								DepositBox.depositItemAmount(player, removeSlot, 1);
								break;
						case 1688:

								if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
								{
										return;
								}
								ItemAssistant.removeItem(player, removeId, removeSlot);
								break;

						case 5064:
								Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, 1, true);
								break;

						case 5382:
								Bank.withdrawFromBank(player, removeId, removeSlot, 1);
								break;

						case 3900:
								player.getShops().checkShopPrice(removeId, removeSlot);
								break;

						case 3823:
								player.getShops().priceCheckItemToSell(player, removeId);
								break;

						case 3322:
								if (player.getDuelStatus() <= 0 && player.isInTrade())
								{
										player.getTradeAndDuel().tradeItem(removeId, removeSlot, 1);
								}
								else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2)
								{
										player.getTradeAndDuel().stakeItem(removeId, removeSlot, 1);
								}
								break;

						case 3415:
								if (player.getDuelStatus() <= 0)
								{
										player.getTradeAndDuel().fromTrade(removeId, removeSlot, 1);
								}
								break;

						case 6669:
								player.getTradeAndDuel().fromDuel(removeId, removeSlot, 1);
								break;

						case 1119:
						case 1120:
						case 1121:
						case 1122:
						case 1123:
								Smithing.readInput(player.baseSkillLevel[ServerConstants.SMITHING], Integer.toString(removeId), player, 1);
								break;
				}
		}

}
