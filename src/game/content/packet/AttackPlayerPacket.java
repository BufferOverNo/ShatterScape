package game.content.packet;

import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.magic.MagicAttack;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Attack player packet.
 **/
public class AttackPlayerPacket implements PacketType
{

		public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.isTeleporting() || player.getDoingAgility() || player.isAnEgg)
				{
						return;
				}
				if (player.getDead())
				{
						return;
				}
				player.resetNpcIdToFollow();
				player.resetPlayerIdAttacking();
				player.resetNpcIdentityAttacking();
				switch (packetType)
				{
						case ATTACK_PLAYER:
								int victimId = player.getInStream().readSignedWordBigEndian();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "victimId: " + victimId);
								}
								AttackPlayer.normalAttackPacket(player, victimId);
								break;
						case MAGE_PLAYER:
								int magicVictimId = player.getInStream().readSignedWordA();
								int castingSpellId = player.getInStream().readSignedWordBigEndian();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "magicVictimId: " + magicVictimId);
										PacketHandler.saveData(player.getPlayerName(), "castingSpellId: " + castingSpellId);
								}
								MagicAttack.magicOnPlayerPacket(player, magicVictimId, castingSpellId);
								break;
				}
		}
}