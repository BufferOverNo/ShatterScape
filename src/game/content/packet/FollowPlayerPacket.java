package game.content.packet;

import game.player.Player;
import game.player.PlayerHandler;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Follow Player
 **/
public class FollowPlayerPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int followPlayer = player.getInStream().readUnsignedWordBigEndian();
				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "followPlayer: " + followPlayer);
				}
				if (PlayerHandler.players[followPlayer] == null)
				{
						return;
				}
				if (player.doingAnAction())
				{
						return;
				}
				player.getPA().stopAllActions();
				player.getDH().dialogueWalkingReset();
				player.setUsingRanged(false);
				player.setMeleeFollow(false);
				player.resetPlayerIdAttacking();
				player.resetNpcIdentityAttacking();
				player.setLastCastedMagic(false);
				player.setUsingMediumRangeRangedWeapon(false);
				player.setUsingShortRangeRangedWeapon(false);
				player.setPlayerIdToFollow(followPlayer);
				player.followLeader = false;
				if (PlayerHandler.players[followPlayer].getPlayerIdToFollow() == player.getPlayerId())
				{
						player.followLeader = true;
				}
		}
}
