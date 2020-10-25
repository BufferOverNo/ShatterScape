package game.player.movement;

import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.interfaces.AreaInterface;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.ZombieGameInstance;
import game.content.minigame.zombie.ZombieWaveInstance;
import game.content.skilling.agility.AgilityAssistant;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.object.ObjectEvent;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.RagBan;
import utility.Misc;

public class Movement
{

		public static void setNewPath(Player player, int x, int y)
		{
				Movement.stopMovementDifferent(player);
				Movement.playerWalk(player, x, y);
		}

		public static void playerWalk(Player player, int x, int y)
		{
				PathFinder.getPathFinder().findRoute(player, x, y, true, 1, 1);
		}

		/**
		 * @param xCoord
		 *        the amount to walk to from the player's X axis co-ordination
		 * @param yCoord
		 *        the amount to walk to from the player's Y axis co-ordination
		 */
		public static void travelTo(Player player, int xCoord, int yCoord)
		{
				// Must use player.dragonSpearTicksLeft and not dragonSpearEvent.
				if (player.dragonSpearTicksLeft > 0)
				{
						return;
				}
				player.setNewWalkCmdSteps(0);
				if (player.setNewWalkCmdSteps(player.getNewWalkCmdSteps() + 1) > 50)
				{
						player.setNewWalkCmdSteps(0);
				}
				int k = player.getX() + xCoord;
				k -= player.mapRegionX * 8;
				player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;
				int l = player.getY() + yCoord;
				l -= player.mapRegionY * 8;
				for (int n = 0; n < player.getNewWalkCmdSteps(); n++)
				{
						player.getNewWalkCmdX()[n] += k;
						player.getNewWalkCmdY()[n] += l;
				}
				Movement.postProcessing(player);
		}

		/**
		 * Stops the player for a game tick.
		 * 
		 * @param player
		 */
		public static void stopMovement(Player player)
		{
				player.setNewWalkCmdSteps(0);
				player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = player.travelBackX[0] = player.travelBackY[0] = 0;
				resetWalkingQueue(player);
		}

		/**
		 * Stops the player until a new movement is issued.
		 * 
		 * @param player
		 */
		public static void stopMovementDifferent(Player player)
		{
				player.setNewWalkCmdSteps(0);
				player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = player.travelBackX[0] = player.travelBackY[0] = 0;
		}

		public static void resetWalkingQueue(Player player)
		{
				player.wQueueReadPtr = player.wQueueWritePtr = 0;
				for (int i = 0; i < player.walkingQueueSize; i++)
				{
						player.walkingQueueX[i] = player.currentX;
						player.walkingQueueY[i] = player.currentY;
				}
		}

		public static void getNextPlayerMovement(Player player)
		{
				player.mapRegionDidChange = false;
				player.didTeleport = false;
				player.dir1 = player.dir2 = -1;
				if (player.teleportToX != -1 && player.teleportToY != -1)
				{
						player.mapRegionDidChange = true;
						if (player.mapRegionX != -1 && player.mapRegionY != -1)
						{
								int relX = player.teleportToX - player.mapRegionX * 8, relY = player.teleportToY - player.mapRegionY * 8;
								if (relX >= 2 * 8 && relX < 11 * 8 && relY >= 2 * 8 && relY < 11 * 8)
								{
										player.mapRegionDidChange = false;
								}
						}
						if (player.mapRegionDidChange)
						{
								player.mapRegionX = (player.teleportToX >> 3) - 6;
								player.mapRegionY = (player.teleportToY >> 3) - 6;
						}
						player.currentX = player.teleportToX - 8 * player.mapRegionX;
						player.currentY = player.teleportToY - 8 * player.mapRegionY;
						player.setX(player.teleportToX);
						player.setY(player.teleportToY);
						resetWalkingQueue(player);
						player.setTeleporting(false);
						player.setDead(false);
						player.teleportToX = player.teleportToY = -1;
						player.didTeleport = true;
						EdgeAndWestsRule.edgeOrWestRuleCoordinateUpdate(player, player.logInTeleportCompleted);
						AreaInterface.updateWalkableInterfaces(player);
						AgilityAssistant.agilityActionCompleted(player);
						player.logInTeleportCompleted = true;
				}
				else
				{
						player.dir1 = getNextWalkingDirection(player, true);
						if (player.dir1 == -1)
						{
								ObjectEvent.stoppedMovingObjectAction(player);
								return;
						}

						if (player.isRunning)
						{
								player.dir2 = getNextWalkingDirection(player, false);
								if (player.dir2 > 0)
								{
										AgilityAssistant.agilityDrain(player);
								}
						}

						int deltaX = 0, deltaY = 0;

						if (player.currentX < 2 * 8)
						{
								deltaX = 4 * 8;
								player.mapRegionX -= 4;
								player.mapRegionDidChange = true;
						}
						else if (player.currentX >= 11 * 8)
						{
								deltaX = -4 * 8;
								player.mapRegionX += 4;
								player.mapRegionDidChange = true;
						}
						if (player.currentY < 2 * 8)
						{
								deltaY = 4 * 8;
								player.mapRegionY -= 4;
								player.mapRegionDidChange = true;
						}
						else if (player.currentY >= 11 * 8)
						{
								deltaY = -4 * 8;
								player.mapRegionY += 4;
								player.mapRegionDidChange = true;
						}
						if (player.mapRegionDidChange)
						{
								player.currentX += deltaX;
								player.currentY += deltaY;
								for (int i = 0; i < player.walkingQueueSize; i++)
								{
										player.walkingQueueX[i] += deltaX;
										player.walkingQueueY[i] += deltaY;
								}
						}
				}
		}

		public static void addToWalkingQueue(Player player, int x, int y)
		{
				int next = (player.wQueueWritePtr + 1) % player.walkingQueueSize;
				if (next == player.wQueueWritePtr)
						return;
				player.walkingQueueX[player.wQueueWritePtr] = x;
				player.walkingQueueY[player.wQueueWritePtr] = y;
				player.wQueueWritePtr = next;
		}

		public static void postProcessing(Player player)
		{
				if (player.doingAction() && player.warriorsGuildEventTimer == 0)
				{
						resetWalkingQueue(player);
						return;
				}
				// Must use player.dragonSpearTicksLeft and not dragonSpearEvent.
				if (player.isFrozen() && player.dragonSpearTicksLeft == 0)
				{
						Player attacker = PlayerHandler.players[player.frozenBy];
						if (attacker != null)
						{
								if (!player.getPA().withinDistanceOfTargetPlayer(attacker, 12))
								{
										player.setFrozenLength(0);
								}
								else if (!player.forceNoClip)
								{
										resetWalkingQueue(player);
										return;
								}
						}
						else
						{
								player.setFrozenLength(0);
						}
				}

				player.isRunning = player.runModeOn;

				if (player.getNewWalkCmdSteps() > 0)
				{
						int firstX = player.getNewWalkCmdX()[0], firstY = player.getNewWalkCmdY()[0];
						int lastDir = 0;
						boolean found = false;
						player.numTravelBackSteps = 0;
						int ptr = player.wQueueReadPtr;
						int dir = Misc.direction(player.currentX, player.currentY, firstX, firstY);
						if (dir != -1 && (dir & 1) != 0)
						{
								do
								{
										lastDir = dir;
										if (--ptr < 0)
										{
												ptr = player.walkingQueueSize - 1;
										}
										player.travelBackX[player.numTravelBackSteps] = player.walkingQueueX[ptr];
										player.travelBackY[player.numTravelBackSteps++] = player.walkingQueueY[ptr];
										dir = Misc.direction(player.walkingQueueX[ptr], player.walkingQueueY[ptr], firstX, firstY);
										if (lastDir != dir)
										{
												found = true;
												break;
										}
								}
								while (ptr != player.wQueueWritePtr);
						}
						else
						{
								found = true;
						}
						if (found)
						{
								player.wQueueWritePtr = player.wQueueReadPtr;
								addToWalkingQueue(player, player.currentX, player.currentY);
								if (dir != -1 && (dir & 1) != 0)
								{
										for (int i = 0; i < player.numTravelBackSteps - 1; i++)
										{
												addToWalkingQueue(player, player.travelBackX[i], player.travelBackY[i]);
										}
										int wayPointX2 = player.travelBackX[player.numTravelBackSteps - 1], wayPointY2 = player.travelBackY[player.numTravelBackSteps - 1];
										int wayPointX1, wayPointY1;
										if (player.numTravelBackSteps == 1)
										{
												wayPointX1 = player.currentX;
												wayPointY1 = player.currentY;
										}
										else
										{
												wayPointX1 = player.travelBackX[player.numTravelBackSteps - 2];
												wayPointY1 = player.travelBackY[player.numTravelBackSteps - 2];
										}
										dir = Misc.direction(wayPointX1, wayPointY1, wayPointX2, wayPointY2);
										if (dir == -1 || (dir & 1) != 0)
										{
										}
										else
										{
												dir >>= 1;
												found = false;
												int x = wayPointX1, y = wayPointY1;
												while (x != wayPointX2 || y != wayPointY2)
												{
														x += Misc.directionDeltaX[dir];
														y += Misc.directionDeltaY[dir];
														if ((Misc.direction(x, y, firstX, firstY) & 1) == 0)
														{
																found = true;
																break;
														}
												}
												if (!found)
												{
														Misc.print("Error3: " + player.getPlayerName());
												}
												else
												{
														addToWalkingQueue(player, wayPointX1, wayPointY1);
												}
										}
								}
								else
								{
										for (int i = 0; i < player.numTravelBackSteps; i++)
										{
												addToWalkingQueue(player, player.travelBackX[i], player.travelBackY[i]);
										}
								}
								for (int i = 0; i < player.getNewWalkCmdSteps(); i++)
								{
										addToWalkingQueue(player, player.getNewWalkCmdX()[i], player.getNewWalkCmdY()[i]);
								}
						}
						player.isRunning = player.isNewWalkCmdIsRunning() || player.runModeOn;
				}
				player.didTeleport = false;
		}

		public static int tempGetNextWalkingDirection(Player player)
		{
				if (player.wQueueReadPtr == player.wQueueWritePtr)
				{
						return -1;
				}
				int dir;
				dir = Misc.direction(player.currentX, player.currentY, player.walkingQueueX[player.wQueueReadPtr], player.walkingQueueY[player.wQueueReadPtr]);
				if (dir == -1)
				{
				}
				else if ((dir & 1) != 0)
				{
						return -1;
				}
				if (dir == -1)
				{
						return -1;
				}

				dir >>= 1;

				if (player.isInZombiesMinigame())
				{
						String[] parse = ZombieWaveInstance.getWaveData(ZombieGameInstance.getCurrentInstance(player.getPlayerName()).wave).teleportCoordinatesSaved.split(" ");
						if (player.getPA().distanceToPoint(player.getX() + Misc.directionDeltaX[dir], player.getY() + Misc.directionDeltaY[dir], Integer.parseInt(parse[0]), Integer.parseInt(parse[1])) >= 16)
						{
								return -1;
						}
				}

				if (player.dead)
				{
						return -1;
				}
				return dir;
		}

		/**
		 * If i modify the return statement results, update the tempGetNextWalkingDirection too.
		 */
		public static int getNextWalkingDirection(Player player, boolean drainAgility)
		{
				if (player.wQueueReadPtr == player.wQueueWritePtr)
				{
						return -1;
				}
				int dir;
				do
				{
						dir = Misc.direction(player.currentX, player.currentY, player.walkingQueueX[player.wQueueReadPtr], player.walkingQueueY[player.wQueueReadPtr]);
						if (dir != -1 && player.directionFacingPath != dir)
						{
								player.directionFacingPath = dir;
						}
						if (dir == -1)
						{
								player.wQueueReadPtr = (player.wQueueReadPtr + 1) % player.walkingQueueSize;
						}
						else if ((dir & 1) != 0)
						{
								resetWalkingQueue(player);
								return -1;
						}
				}
				while ((dir == -1) && (player.wQueueReadPtr != player.wQueueWritePtr));

				if (dir == -1)
				{
						return -1;
				}

				switch (dir)
				{
						// North.
						case 0:
								if (Region.foundDynamicClipping1("NORTH", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
						// North West.
						case 14:
								if (Region.foundDynamicClipping1("NORTH-WEST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
						// North East.
						case 2:
								if (Region.foundDynamicClipping1("NORTH-EAST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
						// West.
						case 12:
								if (Region.foundDynamicClipping1("WEST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;

						// East.
						case 4:
								if (Region.foundDynamicClipping1("EAST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;

						// South.
						case 8:
								if (Region.foundDynamicClipping1("SOUTH", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
						// South West.
						case 10:
								if (Region.foundDynamicClipping1("SOUTH-WEST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
						// South East.
						case 6:
								if (Region.foundDynamicClipping1("SOUTH-EAST", player.getX(), player.getY(), player.getHeight()).equals("BLOCK"))
								{
										return -1;
								}
								break;
				}

				dir >>= 1;
				boolean nextStepWild = Area.inWilderness(player.getX() + Misc.directionDeltaX[dir], player.getY() + Misc.directionDeltaY[dir]);
				boolean ragBanned = RagBan.isRagBanned(player);
				boolean inWild = Area.inWilderness(player);
				if (!nextStepWild)
				{
						TargetSystem.leftWild(player);
						BloodKey.leftWild(player);
				}
				else
				{
						player.timeExitedWildFromTarget = 0;
						if (!inWild)
						{
								if (player.getPlayerIdToFollow() > 0 && System.currentTimeMillis() - player.timeWildernessFollowWarned > 10000)
								{
										player.getPA().sendMessage("You have been stopped from following someone into the wild.");
										player.resetPlayerIdToFollow();
										player.resetFaceUpdate();
										player.timeWildernessFollowWarned = System.currentTimeMillis();
										resetWalkingQueue(player);
										return -1;
								}
						}
				}

				if (player.isInZombiesMinigame())
				{
						String[] parse = ZombieWaveInstance.getWaveData(ZombieGameInstance.getCurrentInstance(player.getPlayerName()).wave).teleportCoordinatesSaved.split(" ");
						if (player.getPA().distanceToPoint(player.getX() + Misc.directionDeltaX[dir], player.getY() + Misc.directionDeltaY[dir], Integer.parseInt(parse[0]), Integer.parseInt(parse[1])) >= 16)
						{
								player.getPA().sendMessage("You cannot walk too far.");
								resetWalkingQueue(player);
								return -1;
						}
				}

				if (player.getHeight() == 20)
				{
						if (player.getPA().distanceToPoint(player.getX() + Misc.directionDeltaX[dir], player.getY() + Misc.directionDeltaY[dir], Tournament.TOURNAMENT_ARENA_X, Tournament.TOURNAMENT_ARENA_Y) >= 25)
						{
								player.getPA().sendMessage("You cannot walk too far.");
								resetWalkingQueue(player);
								return -1;
						}
						if (player.getY() >= 4760 && player.getY() + Misc.directionDeltaY[dir] < 4760)
						{
								player.getPA().sendMessage("You cannot walk to the safe zone.");
								resetWalkingQueue(player);
								return -1;
						}
						if (player.getY() < 4760 && player.getY() + Misc.directionDeltaY[dir] >= 4760)
						{
								player.getPA().sendMessage("You cannot walk to the arena.");
								resetWalkingQueue(player);
								return -1;
						}
				}

				if (player.dead)
				{
						return -1;
				}


				if (player.duelRule[1] && player.getDuelStatus() == 5)
				{
						Player o = player.getTradeAndDuel().getPartner();
						if (o != null)
						{
								player.playerAssistant.sendMessage("Walking has been disabled in this duel!");
								resetWalkingQueue(player);
								return -1;
						}
				}

				Player target = PlayerHandler.players[player.getPlayerIdToFollow()];
				if (target != null)
				{
						if (Combat.stopMovement(player, target, false))
						{
								return -1;
						}
				}
				if (ragBanned)
				{
						resetWalkingQueue(player);
						return -1;
				}
				//If i modify the return statement results, update the tempGetNextWalkingDirection too.
				if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, player.getX() + Misc.directionDeltaX[dir], player.getY() + Misc.directionDeltaY[dir]))
				{
						resetWalkingQueue(player);
						return -1;
				}
				if (!nextStepWild)
				{
						if (inWild)
						{
								RagBan.removeFromWilderness(player.addressIp);
						}
				}
				if (Area.inPVPArea(player) || Area.inDuelArenaRing(player))
				{
						Combat.lowerBoosterCombatLevels(player);
				}
				int x = Misc.directionDeltaX[dir];
				int y = Misc.directionDeltaY[dir];
				player.currentX += x;
				player.currentY += y;
				player.setX(player.getX() + x);
				player.setY(player.getY() + y);

				// Follow methods added here, because if the player is running, it moves 2 tiles per tick.
				// If i don't add it here, the follow method will be called once every tick which results in:
				// Player is at x 1, follow method. is called. 1 tick later, player is at x 3, follow method is called.
				// The issue is, at x 2 which hasn't been checked on, that specific tile can lead to other results.
				if (player.getPlayerIdToFollow() > 0)
				{
						Follow.followPlayer(player, true);
				}
				else if (player.getNpcIdToFollow() > 0)
				{
						Follow.followNpc(player);
				}

				if (inWild && !player.isBot)
				{
						player.wildernessEnteredTime = System.currentTimeMillis();
				}
				AgilityAssistant.agilityActionCompleted(player);
				player.getPA().animationDragCancel();
				player.tilesWalked++;
				AreaInterface.updateWalkableInterfaces(player);
				AgilityAssistant.agilityGain(player);
				EdgeAndWestsRule.edgeOrWestRuleCoordinateUpdate(player, true);
				TargetSystem.doingWildActivity(player);
				player.forceNoClip = false;
				return dir;
		}

		/**
		* Move the player from under the Clicked NPC.
		* 
		* @param player
		*        The associated player.
		*/
		public static void movePlayerFromUnderEntity(Player player)
		{

				if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST"))
				{
						travelTo(player, -1, 0);
				}
				else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST"))
				{
						travelTo(player, 1, 0);
				}
				else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH"))
				{
						travelTo(player, 0, -1);
				}
				else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH"))
				{
						travelTo(player, 0, 1);
				}
		}

}