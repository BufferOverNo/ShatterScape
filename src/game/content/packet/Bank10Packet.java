package game.content.packet;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.skilling.smithing.Smithing;
import game.item.ItemAssistant;
import game.item.OperateItem;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Bank 10 Items
 **/
public class Bank10Packet implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int interfaceId = player.getInStream().readUnsignedWordBigEndian();
				int removeId = player.getInStream().readUnsignedWordA();
				int removeSlot = player.getInStream().readUnsignedWordA();
				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
						PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
						PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
				}


				if (ItemAssistant.nulledItem(removeId))
				{
						return;
				}

				switch (interfaceId)
				{

						case 7423:
								DepositBox.depositItemAmount(player, removeSlot, 10);
								break;
						case 1688:
								OperateItem.applyOperate(player, removeId);
								break;
						case 3900:
								player.getShops().buyItem(removeId, removeSlot, 5);
								break;

						case 3823:
								player.getShops().sellItemToShop(player, removeId, removeSlot, 5);
								break;

						case 5064:
								Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, 10, true);
								break;

						case 5382:
								Bank.withdrawFromBank(player, removeId, removeSlot, 10);
								break;

						case 3322:
								if (player.getDuelStatus() <= 0 && player.isInTrade())
								{
										player.getTradeAndDuel().tradeItem(removeId, removeSlot, 10);
								}
								else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2)
								{
										player.getTradeAndDuel().stakeItem(removeId, removeSlot, 10);
								}
								break;

						case 3415:
								if (player.getDuelStatus() <= 0)
								{
										player.getTradeAndDuel().fromTrade(removeId, removeSlot, 10);
								}
								break;

						case 6669:
								player.getTradeAndDuel().fromDuel(removeId, removeSlot, 10);
								break;

						case 1119:
						case 1120:
						case 1121:
						case 1122:
						case 1123:
								Smithing.readInput(player.baseSkillLevel[ServerConstants.SMITHING], Integer.toString(removeId), player, 10);
								break;
				}
		}

}
