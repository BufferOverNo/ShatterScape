package game.content.packet;

import game.content.minigame.zombie.Zombie;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Challenge Player
 **/
public class ChallengePlayerPacket implements PacketType
{

		@Override
		public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				switch (packetType)
				{

						case 128:
								int answerPlayer = player.getInStream().readUnsignedWord();
								if (answerPlayer <= 0)
								{
										return;
								}
								if (player.getDuelStatus() == 5)
								{
										return;
								}
								final Player answerPlayerInstance = PlayerHandler.players[answerPlayer];
								if (answerPlayerInstance == null)
								{
										return;
								}
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "answerPlayer: " + answerPlayerInstance.getPlayerName());
								}

								if (answerPlayerInstance.isInTrade() || answerPlayerInstance.isUsingBankInterface() || answerPlayerInstance.getDuelStatus() != 0)
								{
										player.getPA().sendMessage(answerPlayerInstance.getPlayerName() + " is busy.");
										return;
								}

								if (Area.inZombieWaitingRoom(player))
								{
										if (player.findOtherPlayerId > 0)
										{
												return;
										}
										player.findOtherPlayerId = 20;
										player.setPlayerIdToFollow(answerPlayerInstance.getPlayerId());
										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														if (player.findOtherPlayerId > 0)
														{
																player.findOtherPlayerId--;
																if (player.getPA().withinDistanceOfTargetPlayer(answerPlayerInstance, 1))
																{
																		Zombie.requestDuo(player, answerPlayerInstance);
																		container.stop();
																}
														}
														else
														{
																container.stop();
														}
												}

												@Override
												public void stop()
												{
														player.findOtherPlayerId = 0;
												}
										}, 1);
								}
								else
								{
										if (!Area.inDuelArena(player))
										{
												return;
										}

										if (Area.inDuelArenaRing(player))
										{
												return;
										}

										if (player.findOtherPlayerId > 0)
										{
												return;
										}
										player.setPlayerIdToFollow(answerPlayerInstance.getPlayerId());
										player.setMeleeFollow(true);
										player.findOtherPlayerId = 20;
										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														if (player.findOtherPlayerId > 0)
														{
																player.findOtherPlayerId--;
																if (player.getPA().withinDistanceOfTargetPlayer(answerPlayerInstance, 1))
																{
																		player.getTradeAndDuel().requestDuel(answerPlayerInstance.getPlayerId());
																		container.stop();
																}
														}
														else
														{
																container.stop();
														}
												}

												@Override
												public void stop()
												{
														player.findOtherPlayerId = 0;
												}
										}, 1);
								}
								break;
				}
		}
}
