package game.content.packet;

import utility.Misc;
import network.packet.PacketHandler;
import network.packet.PacketType;
import game.player.Player;


/**
 * Change appearance
 **/
public class ChangeAppearancePacket implements PacketType
{

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
	{
		int gender = player.getInStream().readSignedByte();
		int hair = player.getInStream().readSignedByte();
		int beard = player.getInStream().readSignedByte();
		int torso = player.getInStream().readSignedByte();
		int arms = player.getInStream().readSignedByte();
		int hands = player.getInStream().readSignedByte();
		int legs = player.getInStream().readSignedByte();
		int feet = player.getInStream().readSignedByte();
		int hairColour = player.getInStream().readSignedByte();
		int torsoColour = player.getInStream().readSignedByte();
		int legsColour = player.getInStream().readSignedByte();
		int feetColour = player.getInStream().readSignedByte();
		int skinColour = player.getInStream().readSignedByte();

		boolean flagged = false;
		if (player.canChangeAppearance)
		{
			if (gender != 0 && gender != 1)
			{
				flagged = true;
			}

			// Male.
			if (gender == 0)
			{
				if (hair < 0 || hair > 8)
				{
					flagged = true;
				}
				if (torso < 18 || torso > 25)
				{
					flagged = true;
				}
				if (arms < 26 || arms > 31)
				{
					flagged = true;
				}
				if (hands < 33 || hands > 34)
				{
					flagged = true;
				}
				if (legs < 36 || legs > 40)
				{
					flagged = true;
				}
				if (feet < 42 || feet > 43)
				{
					flagged = true;
				}
				if (beard < 10 || beard > 17)
				{
					flagged = true;
				}
			}

			// Female.
			if (gender == 1)
			{
				if (hair < 45 || hair > 54)
				{
					flagged = true;
				}
				if (torso < 56 || torso > 60)
				{
					flagged = true;
				}
				if (arms < 61 || arms > 65)
				{
					flagged = true;
				}
				if (hands < 67 || hands > 68)
				{
					flagged = true;
				}
				if (legs < 70 || legs > 77)
				{
					flagged = true;
				}
				if (feet < 79 || feet > 80)
				{
					flagged = true;
				}
				if (beard != -1)
				{
					flagged = true;
				}
			}

			// Both genders.
			if (hairColour < 0 || hairColour > 14)
			{
				flagged = true;
			}
			if (torsoColour < 0 || torsoColour > 21)
			{
				flagged = true;
			}
			if (legsColour < 0 || legsColour > 21)
			{
				flagged = true;
			}
			if (feetColour < 0 || feetColour > 7)
			{
				flagged = true;
			}
			if (skinColour < 0 || skinColour > 8)
			{
				flagged = true;
			}
			if (!flagged)
			{
				player.playerAppearance[0] = gender; // gender
				player.playerAppearance[1] = hair; // head
				player.playerAppearance[2] = torso;// Torso
				player.playerAppearance[3] = arms; // arms
				player.playerAppearance[4] = hands; // hands
				player.playerAppearance[5] = legs; // legs
				player.playerAppearance[6] = feet; // feet
				player.playerAppearance[7] = beard; // beard
				player.playerAppearance[8] = hairColour; // hair colour
				player.playerAppearance[9] = torsoColour; // torso colour
				player.playerAppearance[10] = legsColour; // legs colour
				player.playerAppearance[11] = feetColour; // feet colour
				player.playerAppearance[12] = skinColour; // skin colour
				player.getPA().requestUpdates();
			}
			else
			{
				PacketHandler.appearanceLog.add("Player name: " + player.getPlayerName());
				PacketHandler.appearanceLog.add("Date: " + Misc.getDate());
				PacketHandler.appearanceLog.add("gender: " + gender);
				PacketHandler.appearanceLog.add("hair: " + hair);
				PacketHandler.appearanceLog.add("torso: " + torso);
				PacketHandler.appearanceLog.add("arms: " + arms);
				PacketHandler.appearanceLog.add("hands: " + hands);
				PacketHandler.appearanceLog.add("legs: " + legs);
				PacketHandler.appearanceLog.add("feet: " + feet);
				PacketHandler.appearanceLog.add("beard: " + beard);
				PacketHandler.appearanceLog.add("hairColour: " + hairColour);
				PacketHandler.appearanceLog.add("torsoColour: " + torsoColour);
				PacketHandler.appearanceLog.add("legsColour: " + legsColour);
				PacketHandler.appearanceLog.add("feetColour: " + feetColour);
				PacketHandler.appearanceLog.add("skinColour: " + skinColour);
			}
			player.getPA().closeInterfaces();
			player.canChangeAppearance = false;
		}
	}
}
