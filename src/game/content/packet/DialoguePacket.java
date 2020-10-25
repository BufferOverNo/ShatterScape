package game.content.packet;

import network.packet.PacketType;
import game.player.Player;


/**
 * Dialogue
 **/
public class DialoguePacket implements PacketType
{

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
	{
		player.getDH().dialoguePacketAction();
	}

}
