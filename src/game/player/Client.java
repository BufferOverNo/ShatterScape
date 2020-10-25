package game.player;

import org.apache.mina.common.IoSession;

import core.ServerConstants;
import network.packet.Stream;

public class Client extends Player
{
		public Client(IoSession s, int _playerId, boolean isBot)
		{

				super(_playerId, isBot);
				this.session = s;
				setOutStream(new Stream(new byte[ServerConstants.BUFFER_SIZE]));
				getOutStream().currentOffset = 0;
				inStream = new Stream(new byte[ServerConstants.BUFFER_SIZE]);
				inStream.currentOffset = 0;
				buffer = new byte[ServerConstants.BUFFER_SIZE];

		}
}