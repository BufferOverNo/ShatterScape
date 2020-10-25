package game.content.packet;

import core.ServerConfiguration;
import game.content.combat.Combat;
import game.content.combat.vsnpc.MagicOnNpcPacket;
import game.content.combat.vsplayer.range.RangedData;
import game.npc.Npc;
import game.npc.NpcEvent;
import game.npc.NpcHandler;
import game.npc.clicknpc.FirstClickNpc;
import game.npc.clicknpc.FourthClickNpc;
import game.npc.clicknpc.SecondClickNpc;
import game.npc.clicknpc.ThirdClickNpc;
import game.player.Player;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Click NPC
 */
public class ClickNpcPacket implements PacketType
{
		public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155, SECOND_CLICK = 17, THIRD_CLICK = 21, FOURTH_CLICK = 18;

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.isTeleporting() || player.getDoingAgility() || !player.isTutorialComplete())
				{
						return;
				}
				if (player.getDead())
				{
						return;
				}
				player.resetNpcIdentityAttacking();
				player.setNpcClickIndex(0);
				player.resetPlayerIdAttacking();
				player.setClickNpcType(0);
				Follow.resetFollow(player);
				player.clickObjectType = 0;
				player.lastDialogueSelected = 0;

				switch (packetType)
				{

						/**
						 * Attack npc melee or range
						 **/
						case ATTACK_NPC:
								int npcId = player.getInStream().readUnsignedWordA();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcId: " + npcId);
								}
								Npc npc = NpcHandler.npcs[npcId];
								if (npc == null)
								{
										break;
								}
								if (!player.playerAssistant.withinDistance(npc))
								{
										break;
								}
								player.setNpcIdentityAttacking(npcId);
								if (ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("[Attack npc: " + npc.npcType + "]");
								}
								if (npc.maximumHitPoints == 0 && player.getNpcIdAttacking() == npc.npcIndex)
								{
										player.resetNpcIdentityAttacking();
										break;
								}
								player.setNpcIdToFollow(npc.npcIndex);
								player.faceUpdate(player.getNpcIdAttacking());
								player.setLastCastedMagic(false);

								// Reset movement, this is to cancel the walking packet, because big bosses like Bandos, Callisto, are
								// 2 tiles off x and y on the client, so the client walks the wrong way.

								if (Follow.isBigNpc(npc.npcType) > 0)
								{
										Movement.resetWalkingQueue(player);
										Movement.stopMovementDifferent(player);
								}
								boolean usingBow = false;
								boolean usingOtherRangeWeapons = false;
								boolean usingCross = Combat.getUsingCrossBow(player);
								if (player.getAutocastId() > -1)
								{
										player.setAutoCasting(true);
										player.setUsingMagic(true);
										player.setLastCastedMagic(true);
								}
								else
								{

										if (npc.npcType >= 912 && npc.npcType <= 914 && player.getWieldedWeapon() != 18769)
										{
												player.getPA().sendMessage("You can only use magic spells on this npc.");
												Movement.stopMovement(player);
												player.resetNpcIdentityAttacking();
												player.turnPlayerTo(npc.getX(), npc.getY());
												Combat.resetPlayerAttack(player);
												return;
										}
								}
								if (!player.getAutoCasting() && player.getSpellId() > 0)
								{
										player.setSpellId(-1);
								}
								if (player.getWieldedWeapon() >= 4214 && player.getWieldedWeapon() <= 4223)
								{
										usingBow = true;
								}
								if (RangedData.isWieldingMediumRangeRangedWeapon(player))
								{
										usingBow = true;
								}
								if (RangedData.isWieldingShortRangeRangedWeapon(player))
								{
										usingOtherRangeWeapons = true;
								}
								if ((usingBow || player.getAutoCasting()) && player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 8))
								{
										Movement.stopMovement(player);
								}

								if (usingOtherRangeWeapons && player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 4))
								{
										Movement.stopMovement(player);
								}
								if (!usingCross && !usingBow && !usingOtherRangeWeapons && !player.isUsingMagic())
								{
										player.setMeleeFollow(true);
								}
								else
								{
										player.setMeleeFollow(false);
								}

								break;

						/**
						 * Attack npc with magic
						 **/
						case MAGE_NPC:
								int npcId1 = player.getInStream().readSignedWordBigEndianA();
								player.setNpcIdentityAttacking(npcId1);
								int castingSpellId = player.getInStream().readSignedWordA();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcId1: " + npcId1);
										PacketHandler.saveData(player.getPlayerName(), "castingSpellId: " + castingSpellId);
								}

								MagicOnNpcPacket.magicOnNpcPacket(player, castingSpellId);
								break;

						case FIRST_CLICK:
								int npcId2 = player.inStream.readSignedWordBigEndian();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcId2: " + npcId2);
								}
								player.setNpcClickIndex(npcId2);
								if (NpcHandler.npcs[player.getNpcClickIndex()] == null)
								{
										break;
								}
								if (!player.playerAssistant.withinDistance(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										break;
								}
								player.setNpcIdToFollow(npcId2);
								player.setMeleeFollow(true);
								player.setNpcType(NpcHandler.npcs[player.getNpcClickIndex()].npcType);
								player.faceUpdate(NpcHandler.npcs[player.getNpcClickIndex()].npcIndex);
								if (player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										Movement.movePlayerFromUnderEntity(player);
								}
								if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && player.playerAssistant.withInDistance(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY(), player.getX(), player.getY(), 1) && player.getPA().canMoveToNpc(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										FirstClickNpc.firstClickNpc(player, player.getNpcType());
								}
								else
								{
										player.setClickNpcType(1);
										NpcEvent.clickNpcType1Event(player);
								}
								break;

						case SECOND_CLICK:
								int npc3 = player.inStream.readUnsignedWordBigEndianA();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcid3: " + npc3);
								}

								player.setNpcClickIndex(npc3);
								if (NpcHandler.npcs[player.getNpcClickIndex()] == null)
								{
										break;
								}
								if (!player.playerAssistant.withinDistance(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										break;
								}
								player.setNpcIdToFollow(npc3);
								player.setMeleeFollow(true);
								player.setNpcType(NpcHandler.npcs[player.getNpcClickIndex()].npcType);
								player.faceUpdate(NpcHandler.npcs[player.getNpcClickIndex()].npcIndex);
								if (player.getX() == NpcHandler.npcs[player.getNpcClickIndex()].getX() && player.getY() == NpcHandler.npcs[player.getNpcClickIndex()].getY())
								{
										Movement.movePlayerFromUnderEntity(player);
								}
								// player.setNpcIdToFollow(NPCHandler.npcs[player.npcClickIndex].npcId);
								if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && player.playerAssistant.withInDistance(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY(), player.getX(), player.getY(), 1) && player.getPA().canMoveToNpc(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										SecondClickNpc.secondClickNpc(player, player.getNpcType());
								}
								else
								{
										player.setClickNpcType(2);
										NpcEvent.clickNpcType2Event(player);
								}
								break;

						case THIRD_CLICK:
								int npcId4 = player.inStream.readSignedWord();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcId4: " + npcId4);
								}
								player.setNpcClickIndex(npcId4);
								if (NpcHandler.npcs[player.getNpcClickIndex()] == null)
								{
										break;
								}
								if (!player.playerAssistant.withinDistance(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										break;
								}
								player.setNpcIdToFollow(npcId4);
								player.setMeleeFollow(true);
								player.setNpcType(NpcHandler.npcs[player.getNpcClickIndex()].npcType);
								player.faceUpdate(NpcHandler.npcs[player.getNpcClickIndex()].npcIndex);
								if (player.getX() == NpcHandler.npcs[player.getNpcClickIndex()].getX() && player.getY() == NpcHandler.npcs[player.getNpcClickIndex()].getY())
								{
										Movement.movePlayerFromUnderEntity(player);
								}
								if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && player.playerAssistant.withInDistance(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY(), player.getX(), player.getY(), 1) && player.getPA().canMoveToNpc(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
										ThirdClickNpc.thirdClickNpc(player, player.getNpcType());
								}
								else
								{
										player.setClickNpcType(3);
										NpcEvent.clickNpcType3Event(player);
								}
								break;

						case FOURTH_CLICK:
								int npcId5 = player.inStream.readSignedWordBigEndian();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "npcId5: " + npcId5);
								}
								player.setNpcClickIndex(npcId5);
								if (NpcHandler.npcs[player.getNpcClickIndex()] == null)
								{
										break;
								}
								if (!player.playerAssistant.withinDistance(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										break;
								}
								player.setNpcIdToFollow(npcId5);
								player.setMeleeFollow(true);
								player.setNpcType(NpcHandler.npcs[player.getNpcClickIndex()].npcType);
								player.faceUpdate(NpcHandler.npcs[player.getNpcClickIndex()].npcIndex);
								if (player.getX() == NpcHandler.npcs[player.getNpcClickIndex()].getX() && player.getY() == NpcHandler.npcs[player.getNpcClickIndex()].getY())
								{
										Movement.movePlayerFromUnderEntity(player);
								}
								if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && player.playerAssistant.withInDistance(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY(), player.getX(), player.getY(), 1) && player.getPA().canMoveToNpc(NpcHandler.npcs[player.getNpcClickIndex()]))
								{
										player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
										FourthClickNpc.fourthClickNpc(player, player.getNpcType());
								}
								else
								{
										player.setClickNpcType(4);
										NpcEvent.clickNpcType4Event(player);
								}
								break;
				}
		}
}