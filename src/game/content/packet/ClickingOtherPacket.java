package game.content.packet;

import game.player.Player;
import game.player.PlayerHandler;
import network.packet.PacketType;


/**
 * Clicking interfaces.
 **/
public class ClickingOtherPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				player.isUsingDeathInterface = false;
				player.usingShop = false;
				player.interfaceDisplayed = 0;
				player.setUsingBankInterface(false);
				player.canUseGameModeInterface = false;
				player.getTradeAndDuel().claimStakedItems();
				if (player.isInTrade())
				{
						if (!player.acceptedTrade)
						{
								Player o = PlayerHandler.players[player.getTradeWith()];
								o.tradeAccepted = false;
								player.tradeAccepted = false;
								o.setTradeStatus(0);
								player.setTradeStatus(0);
								player.tradeConfirmed = false;
								player.tradeConfirmed2 = false;
								player.getTradeAndDuel().declineTrade1(true);
						}
				}

				Player o = player.getTradeAndDuel().getPartner();
				if (player.getDuelStatus() == 5)
				{
						return;
				}
				if (o != null)
				{
						if (player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
						{
								player.getTradeAndDuel().declineDuel(true);
								o.getTradeAndDuel().declineDuel(false);
						}
				}


		}

}