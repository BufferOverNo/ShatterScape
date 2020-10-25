package game.content.packet;

import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketType;

/**
 * Trading
 */
public class TradePacket implements PacketType
{

		@Override
		public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int tradeId = player.getInStream().readSignedWordBigEndian();
				Follow.resetFollow(player);
				if (player.isDisconnected())
				{
						player.setTradeStatus(0);
				}
				if (player.isInTrade() || Area.inDuelArenaRing(player) || tradeId == player.getPlayerId())
				{
						return;
				}
				if (tradeId < 1)
				{
						return;
				}
				final Player other = PlayerHandler.players[tradeId];
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
				if (player.getX() == other.getX() && player.getY() == other.getY())
				{
						Movement.movePlayerFromUnderEntity(player);
				}
				player.setPlayerIdToFollow(tradeId);
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
												player.getTradeAndDuel().requestTrade(other.getPlayerId());
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

}
