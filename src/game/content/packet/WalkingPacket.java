package game.content.packet;

import game.content.donator.DonatorFeatures;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.Transform;
import game.content.music.SoundSystem;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketType;

public class WalkingPacket implements PacketType
{
		private final static int MINIMAP_CLICK_PACKET = 248;

		private final static int TILE_CLICK_PACKET = 164;

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (!player.isTutorialComplete())
				{
						player.getDH().sendDialogues(270);
						return;
				}
				if (player.getTransformed() == 5)
				{
						Transform.unTransform(player);
				}
				if (player.doingAnAction())
				{
						return;
				}

				if (player.dragonSpearEvent)
				{
						return;
				}
				if (player.cannotIssueMovement)
				{
						return;
				}
				player.doNotClosePmInterface = true;
				player.getPA().closeInterfaces();
				player.setUsingBankInterface(false);
				MithrilSeeds.resetPlayerPlantData(player);
				// When clicking on Npc/object, packet 98 is sent. Which is a walking packet.
				if (packetType == MINIMAP_CLICK_PACKET || packetType == TILE_CLICK_PACKET)
				{
						player.clickObjectType = 0;
						player.setClickNpcType(0);
						player.resetNpcIdentityAttacking();
						player.resetPlayerIdAttacking();
						player.resetPlayerTurn();
						player.resetFaceUpdate();
						Follow.resetFollow(player);
				}
				player.getDH().dialogueWalkingReset();

				DonatorFeatures.resetAfk(player, false);
				player.playerAssistant.stopAllActions();
				player.isUsingDeathInterface = false;

				if (player.isFrozen())
				{
						if (PlayerHandler.players[player.getPlayerIdAttacking()] != null)
						{
								if (player.playerAssistant.withInDistance(player.getX(), player.getY(), PlayerHandler.players[player.getPlayerIdAttacking()].getX(), PlayerHandler.players[player.getPlayerIdAttacking()].getY(), 1) && packetType != 98)
								{
										player.resetPlayerIdAttacking();
										return;
								}
						}
						if (packetType != 98)
						{
								player.resetPlayerIdAttacking();
						}
						SoundSystem.sendSound(player, 221, 0);
						player.getPA().sendFilterableMessage("A magical force stops you from moving.");
						return;
				}

				// Player in duel screen.
				if ((player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4))
				{
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								o.getTradeAndDuel().declineDuel(false);
						}
						player.getTradeAndDuel().declineDuel(true);
				}
				player.getTradeAndDuel().claimStakedItems();
				if (player.getDead())
				{
						return;
				}
				if (player.isInTrade())
				{
						player.getTradeAndDuel().declineTrade1(true);
				}
				player.setTradeStatus(0);
				player.tradeRequested = false;
				player.setTradeWith(0);
				player.duelRequested = false;
				if (player.getDuelStatus() == 0)
				{
						player.setDuelingWith(0);
				}
				if (packetType == 248)
				{
						packetSize -= 14;
				}

				player.setNewWalkCmdSteps((packetSize - 5) / 2);
				if (player.setNewWalkCmdSteps(player.getNewWalkCmdSteps() + 1) > player.walkingQueueSize)
				{
						player.setNewWalkCmdSteps(0);
						return;
				}

				player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;

				int firstStepX = player.getInStream().readSignedWordBigEndianA() - player.getMapRegionX() * 8;
				for (int i = 1; i < player.getNewWalkCmdSteps(); i++)
				{
						player.getNewWalkCmdX()[i] = player.getInStream().readSignedByte();
						player.getNewWalkCmdY()[i] = player.getInStream().readSignedByte();
				}

				int firstStepY = player.getInStream().readSignedWordBigEndian() - player.getMapRegionY() * 8;
				player.setNewWalkCmdIsRunning(player.getInStream().readSignedByteC() == 1);
				for (int i1 = 0; i1 < player.getNewWalkCmdSteps(); i1++)
				{
						player.getNewWalkCmdX()[i1] += firstStepX;
						player.getNewWalkCmdY()[i1] += firstStepY;
				}
				int pathX = player.getNewWalkCmdX()[player.getNewWalkCmdSteps() - 1] + player.getMapRegionX() * 8;
				int pathY = player.getNewWalkCmdY()[player.getNewWalkCmdSteps() - 1] + player.getMapRegionY() * 8;
				boolean enableNoClip = true;
				if (enableNoClip && player.noClip)
				{
						return;
				}
				Movement.stopMovementDifferent(player); // This reset has to be here for the playerWalk to work.
				Movement.playerWalk(player, pathX, pathY);
				player.tempDir1 = Movement.tempGetNextWalkingDirection(player);
				player.tempRunning = player.runModeOn;
				player.tempRunning = player.isNewWalkCmdIsRunning() || player.runModeOn;
				if (player.isRunning)
				{
						player.tempDir2 = Movement.tempGetNextWalkingDirection(player);
				}
				if (player.tempDir1 == -1)
				{
						player.tempMoving = false;
				}
				else
				{
						player.tempMoving = true;
				}



		}
}