package game.content.packet;

import network.packet.PacketType;
import game.player.Player;

public class ItemOnPlayerPacket implements PacketType
{
	@Override
	public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer)
	{
	}

}