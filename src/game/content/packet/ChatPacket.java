package game.content.packet;

import game.bot.BotCommunication;
import game.player.Player;
import game.player.punishment.Mute;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Chat
 **/
public class ChatPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int value1 = player.getInStream().readUnsignedByteS();
				int value2 = player.getInStream().readUnsignedByteS();
				byte byte1 = (byte) (player.packetSize - 2); // Text size.
				player.setChatTextEffects(value1);
				player.setChatTextColor(value2);
				player.setChatTextSize(byte1);
				player.inStream.readBytes_reverseA(player.getChatText(), player.getChatTextSize(), 0);
				String text = Misc.textUnpack(player.getChatText(), packetSize - 2);
				if (text.toLowerCase().contains("img=8"))
				{
						if (!text.equals(player.diceResultSaved.toLowerCase()))
						{
								PacketHandler.diceLog.add(player.getPlayerName() + " at " + Misc.getDate());
								PacketHandler.diceLog.add("Rigged dice: " + text.toLowerCase());
								PacketHandler.diceLog.add("Original dice: " + player.diceResultSaved.toLowerCase());
								return;
						}
				}
				if (trackPlayer)
				{
						PacketHandler.chatAndPmLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.chatAndPmLog.add("Typed in chat: " + text);
				}

				if (Mute.isMuted(player))
				{
						return;
				}
				//DiscordBot.announce(player.getPlayerName() + ": " + text);
				BotCommunication.playerToBot(player, text);
				player.setChatTextUpdateRequired(true);

		}
}