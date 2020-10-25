package game.content.packet;

import game.content.bank.Bank;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Move Items
 **/
public class MoveItemsPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
				byte insert = player.getInStream().readSignedByteC();
				int itemFrom = player.getInStream().readUnsignedWordBigEndianA();
				int itemTo = player.getInStream().readUnsignedWordBigEndian();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
						PacketHandler.saveData(player.getPlayerName(), "insert: " + insert);
						PacketHandler.saveData(player.getPlayerName(), "itemFrom: " + itemFrom);
						PacketHandler.saveData(player.getPlayerName(), "itemTo: " + itemTo);
				}
				if (itemFrom < 0 || itemTo < 0)
				{
						return;
				}

				if (interfaceId == 5)
				{
						Bank.toTab(player, 0, itemFrom);
						return;
				}
				else if (interfaceId == 13)
				{
						Bank.toTab(player, 1, itemFrom);
						return;
				}
				else if (interfaceId == 26)
				{
						Bank.toTab(player, 2, itemFrom);
						return;
				}
				else if (interfaceId == 39)
				{
						Bank.toTab(player, 3, itemFrom);
						return;
				}
				else if (interfaceId == 52)
				{
						Bank.toTab(player, 4, itemFrom);
						return;
				}
				else if (interfaceId == 65)
				{
						Bank.toTab(player, 5, itemFrom);
						return;
				}
				else if (interfaceId == 78)
				{
						Bank.toTab(player, 6, itemFrom);
						return;
				}
				else if (interfaceId == 91)
				{
						Bank.toTab(player, 7, itemFrom);
						return;
				}
				else if (interfaceId == 104)
				{
						Bank.toTab(player, 8, itemFrom);
						return;
				}
				if (player.isInTrade())
				{
						player.getTradeAndDuel().declineTrade1(true);
						return;
				}
				if (player.getTradeStatus() == 1)
				{
						player.getTradeAndDuel().declineTrade1(true);
						return;
				}
				if (player.getDuelStatus() == 1)
				{
						player.getTradeAndDuel().declineDuel(false);
						return;
				}
				ItemAssistant.moveItems(player, itemFrom, itemTo, interfaceId, insert);
		}
}