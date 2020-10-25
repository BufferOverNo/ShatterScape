package game.content.packet;

import core.ServerConfiguration;
import game.content.skilling.Farming;
import game.object.ObjectEvent;
import game.object.ObjectRePathing;
import game.object.click.FirstClickObject;
import game.object.click.FourthClickObject;
import game.object.click.SecondClickObject;
import game.object.click.ThirdClickObject;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.player.Player;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Click Object
 */

public class ClickObjectPacket implements PacketType
{

		public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234;

		@Override
		public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.getDoingAgility() || !player.isTutorialComplete() || player.isTeleporting() || player.getTransformed() > 0)
				{
						return;
				}

				player.clickObjectType = 0;
				player.setObjectId(0);
				player.setObjectX(0);
				player.setObjectY(0);
				player.objectYOffset = 0;
				player.objectXOffset = 0;
				player.objectDistance = 1;
				player.resetFaceUpdate();
				player.setUsingMagic(false);
				player.resetNpcIdentityAttacking();
				player.resetPlayerIdAttacking();
				player.playerAssistant.stopAllActions();
				player.setClickNpcType(0);
				Follow.resetFollow(player);
				int objectXLength = 0;
				int objectYLength = 0;
				ObjectDefinitionServer objectDefinition = null;

				switch (packetType)
				{

						case FIRST_CLICK:
								int objectX = player.getInStream().readSignedWordBigEndianA();
								int objectId = player.getInStream().readUnsignedWord();
								int objectY = player.getInStream().readUnsignedWordA();

								if (player.farmingXCoordinate > 0 && objectId == Farming.PATCH_HERBS)
								{
										objectX = player.farmingXCoordinate;
										objectY = player.farmingYCoordinate;
										Movement.setNewPath(player, objectX, objectY);
								}
								player.setObjectX(objectX);
								player.setObjectId(objectId);
								player.setObjectY(objectY);

								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "objectX: " + objectX);
										PacketHandler.saveData(player.getPlayerName(), "objectId: " + objectId);
										PacketHandler.saveData(player.getPlayerName(), "objectY: " + objectY);
								}
								if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight()))
								{
										if (ServerConfiguration.DEBUG_MODE)
										{
												Misc.print("Object does not exist, first click [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
										}
										return;
								}

								// Rune essence mine/
								if (player.getObjectId() == 2491)
								{
										if (player.getObjectX() == 2927 && player.getObjectY() == 4814)
										{
												player.setObjectX(2929);
												player.setObjectY(4816);
										}
										else if (player.getObjectX() == 2893 && player.getObjectY() == 4812)
										{
												player.setObjectX(2895);
												player.setObjectY(4814);
										}
										else if (player.getObjectX() == 2925 && player.getObjectY() == 4848)
										{
												player.setObjectX(2927);
												player.setObjectY(4850);
										}
										else if (player.getObjectX() == 2891 && player.getObjectY() == 4847)
										{
												player.setObjectX(2893);
												player.setObjectY(4849);
										}
								}
								// Staircase inside the Varrock west bank dungeon.
								else if (player.getObjectId() == 11805 && player.getObjectX() == 3187 && player.getObjectY() == 9833)
								{
										player.setObjectX(3189);
										player.setObjectY(9833);
								}

								if (ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("FIRST CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
								}

								if (Math.abs(player.getX() - player.getObjectX()) > 25 || Math.abs(player.getY() - player.getObjectY()) > 25)
								{
										Movement.resetWalkingQueue(player);
										break;
								}

								objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
								if (objectDefinition == null)
								{
										return;
								}
								objectXLength = objectDefinition.xLength();
								objectYLength = objectDefinition.yLength();
								if (objectXLength >= 2 || objectYLength >= 2)
								{
										player.objectDistance = 2;
								}

								if (objectXLength == 3 || objectXLength == 4)
								{
										player.objectXOffset = 1;
								}
								if (objectYLength == 3 || objectYLength == 4)
								{
										player.objectYOffset = 1;
								}
								if (objectYLength == 4 || objectXLength == 4)
								{
										player.objectDistance += 1;
								}
								if (player.getObjectId() == 19040)
								{
										player.objectDistance = 2;
								}
								if (player.getObjectId() == 2114)
								{
										player.objectDistance = 4;
								}
								ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving)
								{
										FirstClickObject.firstClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								}
								else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving)
								{
										FirstClickObject.firstClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								}
								else
								{
										player.clickObjectType = 1;
										ObjectEvent.clickObjectType1Event(player);

								}
								break;

						case SECOND_CLICK:
								int objectId1 = player.getInStream().readUnsignedWordBigEndianA();
								int objectY1 = player.getInStream().readSignedWordBigEndian();
								int objectX1 = player.getInStream().readUnsignedWordA();
								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "objectId1: " + objectId1);
										PacketHandler.saveData(player.getPlayerName(), "objectY1: " + objectY1);
										PacketHandler.saveData(player.getPlayerName(), "objectX1: " + objectX1);
								}
								player.setObjectId(objectId1);
								player.setObjectY(objectY1);
								player.setObjectX(objectX1);
								if (!Region.objectExists(player, objectId1, objectX1, objectY1, player.getHeight()))
								{
										if (ServerConfiguration.DEBUG_MODE)
										{
												Misc.print("Object does not exist, second click [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
										}
										return;
								}

								if (ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("SECOND CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
								}


								objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
								if (objectDefinition == null)
								{
										return;
								}
								objectXLength = objectDefinition.xLength();
								objectYLength = objectDefinition.yLength();
								if (objectXLength >= 2 || objectYLength >= 2)
								{
										player.objectDistance = 2;
								}

								if (objectXLength == 3 || objectXLength == 4)
								{
										player.objectXOffset = 1;
								}
								if (objectYLength == 3 || objectYLength == 4)
								{
										player.objectYOffset = 1;
								}
								if (objectYLength == 4 || objectXLength == 4)
								{
										player.objectDistance += 1;
								}

								ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving)
								{
										SecondClickObject.secondClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								}
								else
								{
										player.clickObjectType = 2;
										ObjectEvent.clickObjectType2Event(player);
								}
								break;

						case THIRD_CLICK:
								int objectX2 = player.getInStream().readSignedWordBigEndian();
								int objectY2 = player.getInStream().readUnsignedWord();
								int objectId2 = player.getInStream().readUnsignedWordBigEndianA();

								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "objectX2: " + objectX2);
										PacketHandler.saveData(player.getPlayerName(), "objectY2: " + objectY2);
										PacketHandler.saveData(player.getPlayerName(), "objectId2: " + objectId2);
								}
								player.setObjectX(objectX2);
								player.setObjectY(objectY2);
								player.setObjectId(objectId2);
								if (!Region.objectExists(player, objectId2, objectX2, objectY2, player.getHeight()))
								{
										if (ServerConfiguration.DEBUG_MODE)
										{
												Misc.print("Object does not exist, third click [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
										}
										return;
								}

								if (ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("THIRD CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
								}


								objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
								if (objectDefinition == null)
								{
										return;
								}
								objectXLength = objectDefinition.xLength();
								objectYLength = objectDefinition.yLength();
								if (objectXLength >= 2 || objectYLength >= 2)
								{
										player.objectDistance = 2;
								}

								if (objectXLength == 3 || objectXLength == 4)
								{
										player.objectXOffset = 1;
								}
								if (objectYLength == 3 || objectYLength == 4)
								{
										player.objectYOffset = 1;
								}
								if (objectYLength == 4 || objectXLength == 4)
								{
										player.objectDistance += 1;
								}
								if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving)
								{
										ThirdClickObject.thirdClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								}
								else
								{
										player.clickObjectType = 3;
										ObjectEvent.clickObjectType3Event(player);
								}
								break;

						case FOURTH_CLICK:
								int objectX3 = player.getInStream().readSignedWordBigEndian();
								int objectId3 = player.getInStream().readUnsignedWord();
								int objectY3 = player.getInStream().readUnsignedWordBigEndianA();
								objectId3 += 128;
								objectX3 -= 128;

								if (trackPlayer)
								{
										PacketHandler.saveData(player.getPlayerName(), "objectX2: " + objectX3);
										PacketHandler.saveData(player.getPlayerName(), "objectY2: " + objectY3);
										PacketHandler.saveData(player.getPlayerName(), "objectId2: " + objectId3);
								}

								player.setObjectX(objectX3);
								player.setObjectY(objectY3);
								player.setObjectId(objectId3);
								if (!Region.objectExists(player, objectId3, objectX3, objectY3, player.getHeight()))
								{
										if (ServerConfiguration.DEBUG_MODE)
										{
												Misc.print("Object does not exist, fourth click [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
										}
										return;
								}

								if (ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("FOURTH CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY() + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
								}


								objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
								if (objectDefinition == null)
								{
										return;
								}
								objectXLength = objectDefinition.xLength();
								objectYLength = objectDefinition.yLength();
								if (objectXLength >= 2 || objectYLength >= 2)
								{
										player.objectDistance = 2;
								}

								if (objectXLength == 3 || objectXLength == 4)
								{
										player.objectXOffset = 1;
								}
								if (objectYLength == 3 || objectYLength == 4)
								{
										player.objectYOffset = 1;
								}
								if (objectYLength == 4 || objectXLength == 4)
								{
										player.objectDistance += 1;
								}
								if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving)
								{
										FourthClickObject.fourthClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
								}
								else
								{
										player.clickObjectType = 4;
										ObjectEvent.clickObjectType4Event(player);
								}
								break;
				}

		}

}