package game.content.combat.vsnpc;

import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.movement.Movement;

/**
 * Magic on NPC packet action.
 * @author MGT Madness, created on 26-03-2015.
 */
public class MagicOnNpcPacket
{
		public static void magicOnNpcPacket(Player player, int castingSpellID)
		{
				Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
				if (npc == null)
				{
						return;
				}
				if (!player.playerAssistant.withinDistance(npc))
				{
						return;
				}
				player.setUsingMagic(false);
				player.setLastCastedMagic(true);
				if (npc.maximumHitPoints == 0 || npc.npcType == 944)
				{
						player.playerAssistant.sendMessage("You can't attack this npc.");
						return;
				}

				for (int i = 0; i < CombatConstants.MAGIC_SPELLS.length; i++)
				{
						if (castingSpellID == CombatConstants.MAGIC_SPELLS[i][0])
						{
								player.setSpellId(i);
								player.setUsingMagic(true);
								break;
						}
				}
				if (Combat.spellbookPacketAbuse(player, player.getSpellId()))
				{
						Combat.resetPlayerAttack(player);
						return;
				}
				player.setAutoCasting(false);
				if (player.isUsingMagic())
				{
						if (player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), CombatConstants.MAGIC_FOLLOW_DISTANCE))
						{
								Movement.stopMovement(player);
						}
				}
		}

}
