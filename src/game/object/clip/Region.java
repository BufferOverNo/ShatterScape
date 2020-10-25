package game.object.clip;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import game.content.minigame.zombie.Zombie;
import game.content.minigame.zombie.ZombieGameInstance;
import game.content.minigame.zombie.ZombieWaveInstance;
import game.content.miscellaneous.Web;
import game.content.skilling.Firemaking;
import game.object.custom.CustomClippedTiles;
import game.object.custom.Door;
import game.object.custom.Objects;
import game.player.Player;
import utility.Misc;

public class Region
{
		public ArrayList<Objects> verifiedObjects = new ArrayList<Objects>();

		public static ArrayList<String> removedObjectCoordinates = new ArrayList<String>();

		/**
		 * Store list of tiles that can be passed through or blocked, these are always changing depending on the state of the web/door.
		 * format is: "X Y HEIGHT DIRECTION BLOCK/PASS
		 */
		public static ArrayList<String> dynamicTileClipping = new ArrayList<String>();

		public static void removeFromDynamicTileClipping(String data)
		{
				for (int index = 0; index < dynamicTileClipping.size(); index++)
				{
						String string = dynamicTileClipping.get(index);
						if (data.equals(string))
						{
								dynamicTileClipping.remove(index);
								return;
						}
				}
		}

		public static Region[] regionIdTable;

		public static void buildTable(int count)
		{
				regionIdTable = new Region[count];
				for (Region r : regions)
				{
						regionIdTable[r.id()] = r;
				}
		}

		public static Region getRegion(int x, int y)
		{
				int regionX = x >> 3;
				int regionY = y >> 3;
				int regionId = (regionX / 8 << 8) + regionY / 8;
				/*
				for (Region region : regions)
				{
						if (region.id() == regionId)
						{
								return region;
						}
				}
				*/

				if (regionId < 0)
				{
						return null;
				}
				if (regionIdTable[regionId] != null)
				{
						return regionIdTable[regionId];
				}
				return null;
		}

		public static boolean objectExists(Player player, int objectId, int x, int y, int height)
		{

				if (objectId == 451) // Empty ore id.
				{
						return true;
				}

				// Jad cave exit.
				if (objectId == 11834)
				{
						if (x != 2412 || y != 5118)
						{
								return false;
						}
						return true;
				}
				if (height == 20)
				{
						// Box of health at tournament.
						if (objectId == 29300 && x == 3328 && y == 4753)
						{
								return true;
						}
				}
				// Fire made from logs.
				if (objectId == Firemaking.FIRE_OBJECT_ID)
				{
						if (Firemaking.fireExists(x, y, height))
						{
								return true;
						}
				}

				// Chest and anvil at Zombie minigame.
				if (objectId == 76 || objectId == 2031)
				{
						int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
						if (minigameInstanceIndex >= 0 && Zombie.inZombieMiniGameArea(player, player.getX(), player.getY()) && player.getHeight() > 0)
						{
								if (objectId == 76)
								{
										if (x == ZombieGameInstance.instance.get(minigameInstanceIndex).getChestXCoordinate() && y == ZombieGameInstance.instance.get(minigameInstanceIndex).getChestYCoordinate())
										{
												return true;
										}
								}
								else if (objectId == 2031)
								{
										String[] parse = ZombieWaveInstance.getWaveData(ZombieGameInstance.instance.get(minigameInstanceIndex).wave).anvilCoordinates.split(" ");
										if (x == Integer.parseInt(parse[0]) && y == Integer.parseInt(parse[1]))
										{
												return true;
										}
								}
						}
				}

				if (Door.isModifiedDoor(objectId, x, y, height))
				{
						return true;
				}


				Region r = getRegion(x, y);
				if (r == null)
				{
						return false;
				}
				for (Objects o : r.verifiedObjects)
				{
						if (o.objectId == objectId)
						{
								if (o.objectX == x && o.objectY == y && o.objectHeight == height)
								{
										return true;
								}
						}
				}
				return false;
		}

		public static void readRemovedObjectCoordinates()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("./data/world osrs/object/removed_objects.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty() && !line.contains("//"))
								{
										removedObjectCoordinates.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		/**
		 * 
		 * @param startX
		 * 	For example playerX
		 * @param startY
		 * 	For example playerY
		 * @param endX
		 * 	For example npcX
		 * @param endY
		 * 	For example npcY
		 * @param height
		 * 	For example playerHeight
		 * @param xLength
		 * 	Size of Npc
		 * @param yLength
		 * 	Size of Npc
		 * @return
		 * 	True if the player can walk directly to the npc.
		 */
		public static boolean isStraightPathUnblocked(int startX, int startY, int endX, int endY, int height, int xLength, int yLength)
		{
				int diffX = endX - startX;
				int diffY = endY - startY;
				int max = Math.max(Math.abs(diffX), Math.abs(diffY));
				for (int ii = 0; ii < max; ii++)
				{
						int currentX = endX - diffX;
						int currentY = endY - diffY;

						for (int i = 0; i < xLength; i++)
						{
								for (int i2 = 0; i2 < yLength; i2++)
								{
										if (diffX < 0 && diffY < 0)
										{
												if (blockedSouthWest((currentX + i), (currentY + i2), height) || blockedWest((currentX + i), currentY + i2, height) || blockedSouth(currentX + i, (currentY + i2), height))
												{
														return false;
												}
										}
										else if (diffX > 0 && diffY > 0)
										{
												if (blockedNorthEast(currentX + i, currentY + i2, height) || blockedEast(currentX + i, currentY + i2, height) || blockedNorth(currentX + i, currentY + i2, height))
												{
														return false;
												}
										}
										else if (diffX < 0 && diffY > 0)
										{
												if (blockedNorthWest((currentX + i), currentY + i2, height) || blockedWest((currentX + i), currentY + i2, height) || blockedNorth(currentX + i, currentY + i2, height))
												{
														return false;
												}
										}
										else if (diffX > 0 && diffY < 0)
										{
												if (blockedSouthEast(currentX + i, (currentY + i2), height) || blockedEast(currentX + i, currentY + i2, height) || blockedSouth(currentX + i, (currentY + i2), height))
												{
														return false;
												}
										}
										else if (diffX > 0 && diffY == 0)
										{
												if (blockedEast(currentX + i, currentY + i2, height))
												{
														return false;
												}
										}
										else if (diffX < 0 && diffY == 0)
										{
												if (blockedWest((currentX + i), currentY + i2, height))
												{
														return false;
												}
										}
										else if (diffX == 0 && diffY > 0)
										{
												if (blockedNorth(currentX + i, currentY + i2, height))
												{
														return false;
												}
										}
										else if (diffX == 0 && diffY < 0 && blockedSouth(currentX + i, (currentY + i2), height))
										{
												return false;
										}
								}

						}

						if (diffX < 0)
								diffX++;
						else if (diffX > 0)
								diffX--;
						if (diffY < 0)
								diffY++;
						else if (diffY > 0)
								diffY--;
				}

				return true;
		}

		/**
		 * This has specific exceptions to certain object coordinates that can be shot through with an arrow etc. Like tree stumps, fences etc.
		 * @param startX
		 * 	For example playerX
		 * @param startY
				 * 	For example playerY
		 * @param endX
				 * 	For example npcX
		 * @param endY
				 * 	For example npcY
		 * @param height
				 * 	For example playerHeight
		 * @param xLength
				 * 	Size of Npc
		 * @param yLength
				 * 	Size of Npc
		 * @param mainScan TODO
		 * @return
		 * 	True if the player can walk directly to the npc.
		 */
		public static boolean isStraightPathUnblockedProjectiles(int startX, int startY, int endX, int endY, int height, int xLength, int yLength, boolean mainScan)
		{
				if (mainScan)
				{
						if (!isStraightPathUnblockedProjectiles(endX, endY, startX, startY, height, xLength, yLength, false))
						{
								return false;
						}
				}
				int diffX = endX - startX;
				int diffY = endY - startY;
				int distance = Math.max(Math.abs(diffX), Math.abs(diffY));
				for (int index = 0; index < distance + 1; index++)
				{
						int currentX = endX - diffX;
						int currentY = endY - diffY;
						for (int xIndex = 0; xIndex < xLength; xIndex++)
						{
								for (int yIndex = 0; yIndex < yLength; yIndex++)
								{
										int x = currentX + xIndex;
										int y = currentY + yIndex;

										if (diffX < 0 && diffY < 0)
										{
												if (!Region.projectileCanPassThrough(x - 1, y - 1, height))
												{
														if (blockedSouthWest(x, y, height))
														{
																return false;
														}
												}

												if (!Region.projectileCanPassThrough(x - 1, y, height))
												{
														if (blockedWest(x, y, height))
														{
																return false;
														}
												}

												if (!Region.projectileCanPassThrough(x, y - 1, height))
												{
														if (blockedSouth(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX > 0 && diffY > 0)
										{
												if (!Region.projectileCanPassThrough(x + 1, y + 1, height))
												{
														if (blockedNorthEast(x, y, height))
														{
																return false;
														}
												}


												if (!Region.projectileCanPassThrough(x + 1, y, height))
												{
														if (blockedEast(x, y, height))
														{
																return false;
														}
												}

												if (!Region.projectileCanPassThrough(x, y + 1, height))
												{
														if (blockedNorth(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX < 0 && diffY > 0)
										{
												if (!Region.projectileCanPassThrough(x - 1, y + 1, height))
												{
														if (blockedNorthWest(x, y, height))
														{
																return false;
														}
												}

												if (!Region.projectileCanPassThrough(x, y + 1, height))
												{
														if (blockedNorth(x, y, height))
														{
																return false;
														}
												}

												if (!Region.projectileCanPassThrough(x - 1, y, height))
												{
														if (blockedWest(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX > 0 && diffY < 0)
										{
												if (!Region.projectileCanPassThrough(x + 1, y - 1, height))
												{
														if (blockedSouthEast(x, y, height))
														{
																return false;
														}
												}


												if (!Region.projectileCanPassThrough(x + 1, y, height))
												{
														if (blockedEast(x, y, height))
														{
																return false;
														}
												}


												if (!Region.projectileCanPassThrough(x, y - 1, height))
												{
														if (blockedSouth(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX > 0 && diffY == 0)
										{
												if (!Region.projectileCanPassThrough(x + 1, y, height))
												{
														if (blockedEast(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX < 0 && diffY == 0)
										{
												if (!Region.projectileCanPassThrough(x - 1, y, height))
												{
														if (blockedWest(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX == 0 && diffY > 0)
										{
												if (!Region.projectileCanPassThrough(x, y + 1, height))
												{
														if (blockedNorth(x, y, height))
														{
																return false;
														}
												}
										}
										else if (diffX == 0 && diffY < 0)
										{

												if (!Region.projectileCanPassThrough(x, y - 1, height))
												{
														if (blockedSouth(x, y, height))
														{
																return false;
														}
												}
										}


								}


						}

						if (diffX < 0)
								diffX++;
						else if (diffX > 0)
								diffX--;
						if (diffY < 0)
								diffY++;
						else if (diffY > 0)
								diffY--;
				}

				return true;
		}

		private void removeClip(int x, int y, int height, int shift)
		{
				int regionAbsX = (id >> 8) * 64;
				int regionAbsY = (id & 0xff) * 64;
				if (clips[height] == null)
				{
						clips[height] = new int[64][64];
				}
				clips[height][x - regionAbsX][y - regionAbsY] = 0;
		}

		private void addClip(int x, int y, int height, int shift, int type, int direction, String debug)
		{
				int regionAbsX = (id >> 8) * 64;
				int regionAbsY = (id & 0xff) * 64;
				if (clips[height] == null)
				{
						clips[height] = new int[64][64];
				}

				clips[height][x - regionAbsX][y - regionAbsY] |= shift;
		}

		private int getClip(int x, int y, int height)
		{
				int regionAbsX = (id >> 8) * 64;
				int regionAbsY = (id & 0xff) * 64;
				if (height < 0)
				{
						height = 1;
				}
				if (clips[height] == null)
				{
						return 0;
				}
				return clips[height][x - regionAbsX][y - regionAbsY];
		}

		public static void addClipping(int x, int y, int height, int shift, String debug, int type, int direction)
		{
				int regionX = x >> 3;
				int regionY = y >> 3;
				int regionId = ((regionX / 8) << 8) + (regionY / 8);
				if (regionId < 0)
				{
						return;
				}
				if (regionIdTable[regionId] != null)
				{
						regionIdTable[regionId].addClip(x, y, height, shift, type, direction, debug);
				}
		}

		public static void removeClipping(int x, int y, int height)
		{
				int regionX = x >> 3;
				int regionY = y >> 3;
				int regionId = ((regionX / 8) << 8) + (regionY / 8);
				/*
				for (Region r : regions)
				{
						if (r.id() == regionId)
						{
								r.removeClip(x, y, height, 0);
								break;
						}
				}
				*/
				if (regionId < 0)
				{
						return;
				}
				regionIdTable[regionId].removeClip(x, y, height, 0);
		}

		private static Region[] regions;

		private int id;

		private int[][][] clips = new int[4][][];

		public Region(int id, boolean members)
		{
				this.id = id;
		}

		public int id()
		{
				return id;
		}

		private static void addClippingForVariableObject(int x, int y, int height, int type, int direction, boolean flag)
		{
				if (type == 0)
				{
						if (direction == 0)
						{
								addClipping(x, y, height, 128, "Here2", type, direction);
								addClipping(x - 1, y, height, 8, "Here3", type, direction);
						}
						else if (direction == 1)
						{
								addClipping(x, y, height, 2, "Here4", type, direction);
								addClipping(x, y + 1, height, 32, "Here5", type, direction);
						}
						else if (direction == 2)
						{
								addClipping(x, y, height, 8, "Here6", type, direction);
								addClipping(x + 1, y, height, 128, "Here7", type, direction);
						}
						else if (direction == 3)
						{
								addClipping(x, y, height, 32, "Here8", type, direction);
								addClipping(x, y - 1, height, 2, "Here9", type, direction);
						}
				}
				else if (type == 1 || type == 3)
				{
						if (direction == 0)
						{
								addClipping(x, y, height, 1, "Here10", type, direction);
								addClipping(x - 1, y, height, 16, "Here11", type, direction);
						}
						else if (direction == 1)
						{
								addClipping(x, y, height, 4, "Here12", type, direction);
								addClipping(x + 1, y + 1, height, 64, "Here13", type, direction);
						}
						else if (direction == 2)
						{
								addClipping(x, y, height, 16, "Here14", type, direction);
								addClipping(x + 1, y - 1, height, 1, "Here15", type, direction);
						}
						else if (direction == 3)
						{
								addClipping(x, y, height, 64, "Here16", type, direction);
								addClipping(x - 1, y - 1, height, 4, "Here17", type, direction);
						}
				}
				else if (type == 2)
				{
						if (direction == 0)
						{
								addClipping(x, y, height, 130, "Here18", type, direction);
								addClipping(x - 1, y, height, 8, "Here19", type, direction);
								addClipping(x, y + 1, height, 32, "Here20", type, direction);
						}
						else if (direction == 1)
						{
								addClipping(x, y, height, 10, "Here21", type, direction);
								addClipping(x, y + 1, height, 32, "Here22", type, direction);
								addClipping(x + 1, y, height, 128, "Here23", type, direction);
						}
						else if (direction == 2)
						{
								addClipping(x, y, height, 40, "Here24", type, direction);
								addClipping(x + 1, y, height, 128, "Here25", type, direction);
								addClipping(x, y - 1, height, 2, "Here26", type, direction);
						}
						else if (direction == 3)
						{
								addClipping(x, y, height, 160, "Here27", type, direction);
								addClipping(x, y - 1, height, 2, "Here28", type, direction);
								addClipping(x - 1, y, height, 8, "Here29", type, direction);
						}
				}
				if (flag)
				{
						if (type == 0)
						{
								if (direction == 0)
								{
										addClipping(x, y, height, 65536, "Here30", type, direction);
										addClipping(x - 1, y, height, 4096, "Here31", type, direction);
								}
								else if (direction == 1)
								{
										addClipping(x, y, height, 1024, "Here32", type, direction);
										addClipping(x, y + 1, height, 16384, "Here33", type, direction);
								}
								else if (direction == 2)
								{
										addClipping(x, y, height, 4096, "Here34", type, direction);
										addClipping(x + 1, y, height, 65536, "Here35", type, direction);
								}
								else if (direction == 3)
								{
										addClipping(x, y, height, 16384, "Here36", type, direction);
										addClipping(x, y - 1, height, 1024, "Here37", type, direction);
								}
						}
						if (type == 1 || type == 3)
						{
								if (direction == 0)
								{
										addClipping(x, y, height, 512, "Here38", type, direction);
										addClipping(x - 1, y + 1, height, 8192, "Here39", type, direction);
								}
								else if (direction == 1)
								{
										addClipping(x, y, height, 2048, "Here40", type, direction);
										addClipping(x + 1, y + 1, height, 32768, "Here41", type, direction);
								}
								else if (direction == 2)
								{
										addClipping(x, y, height, 8192, "Here42", type, direction);
										addClipping(x + 1, y + 1, height, 512, "Here43", type, direction);
								}
								else if (direction == 3)
								{
										addClipping(x, y, height, 32768, "Here44", type, direction);
										addClipping(x - 1, y - 1, height, 2048, "Here45", type, direction);
								}
						}
						else if (type == 2)
						{
								if (direction == 0)
								{
										addClipping(x, y, height, 66560, "Here46", type, direction);
										addClipping(x - 1, y, height, 4096, "Here47", type, direction);
										addClipping(x, y + 1, height, 16384, "Here48", type, direction);
								}
								else if (direction == 1)
								{
										addClipping(x, y, height, 5120, "Here49", type, direction);
										addClipping(x, y + 1, height, 16384, "Here50", type, direction);
										addClipping(x + 1, y, height, 65536, "Here51", type, direction);
								}
								else if (direction == 2)
								{
										addClipping(x, y, height, 20480, "Here52", type, direction);
										addClipping(x + 1, y, height, 65536, "Here53", type, direction);
										addClipping(x, y - 1, height, 1024, "Here54", type, direction);
								}
								else if (direction == 3)
								{
										addClipping(x, y, height, 81920, "Here55", type, direction);
										addClipping(x, y - 1, height, 1024, "Here56", type, direction);
										addClipping(x - 1, y, height, 4096, "Here57", type, direction);
								}
						}
				}
		}

		private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag, int type, int direction)
		{
				int clipping = 256;
				for (int i = x; i < x + xLength; i++)
				{
						for (int i2 = y; i2 < y + yLength; i2++)
						{
								addClipping(i, i2, height, clipping, "Here1", type, direction);
						}
				}
		}

		public static ArrayList<Objects> testObjects = new ArrayList<Objects>();

		public static void addObject(int objectId, int x, int y, int height, int type, int direction, boolean ignoreGate)
		{

				ObjectDefinitionServer definition = ObjectDefinitionServer.getObjectDef(objectId);
				if (definition == null)
				{
						return;
				}
				int xLength;
				int yLength;
				if (direction != 1 && direction != 3)
				{
						xLength = definition.xLength();
						yLength = definition.yLength();
				}
				else
				{
						xLength = definition.yLength();
						yLength = definition.xLength();
				}
				if (objectId == -1)
				{
						return;
				}
				// Invisible rocks at edgeville wilderness, i did not want to update the client cache, so i just removed the clipping from server.
				if ((objectId == 444 || objectId == 445 || objectId == 446) && type == 10)
				{
						return;
				}

				// Tree invisible there.
				if (x == 3081 && y == 3527)
				{
						return;
				}
				if (x >= 3094 && x <= 3100 && y >= 3521 && y <= 3530)
				{
						//Misc.printDontSave(objectId + ", " + x + ", " + y + ", " + type);
				}


				if (!ignoreGate)
				{
						if (definition.name != null)
						{
								if (definition.name.toLowerCase().contains("door") && !definition.name.toLowerCase().contains("trapdoor"))
								{
										if (definition.actions != null)
										{
												// 9 means diagonal door.
												if (type != 9)
												{
														if (definition.actions[0].contains("Open"))
														{
																// Check if the door has a closed version.
																ObjectDefinitionServer definitionOther = ObjectDefinitionServer.getObjectDef(objectId - 1);
																if (objectId == 1535 || objectId == 24050 || objectId == 24057 || objectId == 24318 || objectId == 14749 || objectId == 2100 || objectId == 2337 || objectId == 1540)
																{
																		Door.doorsList.add(objectId + " " + x + " " + y + " " + height + " " + direction + " " + type + " " + definition.actions[0] + " ORIGINAL");
																}
																if (definitionOther != null)
																{
																		if (definitionOther.name != null)
																		{
																				if (definitionOther.name.toLowerCase().contains("door") && !definitionOther.name.toLowerCase().contains("trapdoor"))
																				{
																						if (definitionOther.actions != null)
																						{
																								if (definitionOther.actions[0].contains("Close"))
																								{
																										Door.doorsList.add(objectId + " " + x + " " + y + " " + height + " " + direction + " " + type + " " + definition.actions[0] + " ORIGINAL");
																								}
																						}
																				}
																		}
																}
														}
														else if (definition.actions[0].contains("Close"))
														{
																ObjectDefinitionServer definitionOther = ObjectDefinitionServer.getObjectDef(objectId + 1);
																if (definitionOther != null)
																{
																		if (definitionOther.name != null)
																		{
																				if (definitionOther.name.toLowerCase().contains("door") && !definitionOther.name.toLowerCase().contains("trapdoor"))
																				{
																						if (definitionOther.actions != null)
																						{
																								if (definitionOther.actions[0].contains("Open"))
																								{
																										Door.doorsList.add(objectId + " " + x + " " + y + " " + height + " " + direction + " " + type + " " + definition.actions[0] + " ORIGINAL");
																								}
																						}
																				}
																		}
																}
														}
												}
										}
								}
								if (definition.name.toLowerCase().contains("gate") || objectId == 1524 || objectId == 1521 || objectId == 14752 || objectId == 14751)
								{
										if (definition.actions != null)
										{
												if (definition.actions[0].contains("Open"))
												{
														if (x == 2997 && y == 3931 || x == 2998 && y == 3931)
														{
																// Skip these gates at Wilderness course.
														}
														else
														{
																return;
														}
												}
										}
								}
						}
				}
				// Web object.
				if (objectId == 733)
				{
						type = 0;
						Web.webList.add(x + " " + y + " " + height + " " + direction + " UNCUT");
						Region r = getRegion(x, y);
						if (r != null)
						{
								r.verifiedObjects.add(new Objects(objectId, x, y, height, 0, 10, 0));
						}
						String directionString = "EAST";
						if (direction == 1)
						{
								directionString = "SOUTH";
						}
						else if (direction == 3)
						{
								directionString = "NORTH";
						}
						else if (direction == 2)
						{
								directionString = "WEST";
						}
						if (directionString.equals("WEST"))
						{
								Region.dynamicTileClipping.add(x + " " + y + " " + height + " EAST BLOCK");
								Region.dynamicTileClipping.add((x + 1) + " " + y + " " + height + " WEST BLOCK");
						}
						else if (directionString.equals("EAST"))
						{
								Region.dynamicTileClipping.add(x + " " + y + " " + height + " WEST BLOCK");
								Region.dynamicTileClipping.add((x - 1) + " " + y + " " + height + " EAST BLOCK");
						}
						else if (directionString.equals("NORTH"))
						{
								Region.dynamicTileClipping.add(x + " " + y + " " + height + " SOUTH BLOCK");
								Region.dynamicTileClipping.add(x + " " + (y - 1) + " " + height + " NORTH BLOCK");
						}
						else if (directionString.equals("SOUTH"))
						{
								Region.dynamicTileClipping.add(x + " " + y + " " + height + " NORTH BLOCK");
								Region.dynamicTileClipping.add(x + " " + (y + 1) + " " + height + " SOUTH BLOCK");
						}
						return;
				}

				// Objects that projectiles can pass through.
				if (!definition.blocksProjectiles)
				{
						int clipping = 0x20000;
						for (int i = x; i < x + xLength; i++)
						{
								for (int i2 = y; i2 < y + yLength; i2++)
								{
										addClipping(i, i2, height, clipping, "Special1", type, direction);
								}
						}
				}
				if (type == 22)
				{
						if (definition.hasActions() && definition.blocksWalk())
						{
								addClipping(x, y, height, 0x200000, "Here58", type, direction);
						}
				}
				else if (type >= 9)
				{
						if (definition.blocksWalk())
						{
								addClippingForSolidObject(x, y, height, xLength, yLength, definition.solid(), type, direction);
						}
				}
				else if (type >= 0 && type <= 3)
				{
						if (definition.blocksWalk())
						{
								addClippingForVariableObject(x, y, height, type, direction, definition.solid());
						}
				}
				if (definition.hasActions)
				{
						Region r = getRegion(x, y);
						if (r != null)
						{
								r.verifiedObjects.add(new Objects(objectId, x, y, height, direction, type, 0));
						}
				}
		}

		public static int getClipping(int x, int y, int height)
		{
				if (height > 3)
				{
						height = 0;
				}
				int regionX = x >> 3;
				int regionY = y >> 3;
				int regionId = ((regionX / 8) << 8) + (regionY / 8);
				/*
				for (Region r : regions)
				{
						if (r.id() == regionId)
						{
								return r.getClip(x, y, height);
						}
				}
				*/
				if (regionId < 0)
				{
						return 0;
				}
				if (regionIdTable[regionId] != null)
				{
						return regionIdTable[regionId].getClip(x, y, height);
				}
				return 0;
		}

		public static boolean pathUnblocked(int x, int y, int height, String moveTo)
		{
				if (height > 3)
				{
						height = 0;
				}
				switch (moveTo)
				{
						case "WEST":
								return !blockedWest(x, y, height);
						case "EAST":
								return !blockedEast(x, y, height);
						case "SOUTH":
								return !blockedSouth(x, y, height);
						case "NORTH":
								return !blockedNorth(x, y, height);
						case "SOUTH WEST":
								return !blockedSouthWest(x, y, height);
						case "SOUTH EAST":
								return !blockedSouthEast(x, y, height);
						case "NORTH WEST":
								return !blockedNorthWest(x, y, height);
						case "NORTH EAST":
								return !blockedNorthEast(x, y, height);
				}
				return false;
		}

		public static void load()
		{
				try
				{
						readRemovedObjectCoordinates();
						File f = new File("./data/world osrs/map_index");
						byte[] buffer = new byte[(int) f.length()];
						DataInputStream dis = new DataInputStream(new FileInputStream(f));
						dis.readFully(buffer);
						dis.close();
						ByteStream in = new ByteStream(buffer);
						int size = in.length() / 6;
						in.readUnsignedWord();
						regions = new Region[size];
						int[] regionIds = new int[size];
						int[] mapGroundFileIds = new int[size];
						int[] mapObjectsFileIds = new int[size];
						for (int i = 0; i < size; i++)
						{
								regionIds[i] = in.getUShort();
								mapGroundFileIds[i] = in.getUShort();
								mapObjectsFileIds[i] = in.getUShort();
						}
						/*
						for (int i = 0; i < size; i++)
						{
								regions[i] = new Region(regionIds[i], false);
						}
						*/

						int highest = 0;
						for (int i = 0; i < size; i++)
						{
								regions[i] = new Region(regionIds[i], false);
								if (regionIds[i] > highest)
								{
										highest = regionIds[i];
								}
						}
						buildTable(highest + 1);

						for (int i = 0; i < size; i++)
						{
								byte[] file1 = getBuffer(new File("./data/world osrs/map/" + mapObjectsFileIds[i] + ".gz"));
								byte[] file2 = getBuffer(new File("./data/world osrs/map/" + mapGroundFileIds[i] + ".gz"));
								if (file1 == null || file2 == null)
								{
										continue;
								}
								try
								{
										loadMaps(regionIds[i], new ByteStream(file1), new ByteStream(file2));
								}
								catch (Exception e)
								{
										e.printStackTrace();
								}
						}

				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		private static void loadMaps(int regionId, ByteStream str1, ByteStream str2)
		{
				int absX = (regionId >> 8) * 64;
				int absY = (regionId & 0xff) * 64;
				int[][][] someArray = new int[4][64][64];
				for (int i = 0; i < 4; i++)
				{
						for (int i2 = 0; i2 < 64; i2++)
						{
								for (int i3 = 0; i3 < 64; i3++)
								{
										while (true)
										{
												int v = str2.getUByte();
												if (v == 0)
												{
														break;
												}
												else if (v == 1)
												{
														str2.skip(1);
														break;
												}
												else if (v <= 49)
												{
														str2.skip(1);
												}
												else if (v <= 81)
												{
														someArray[i][i2][i3] = v - 49;
												}
										}
								}
						}
				}
				for (int i = 0; i < 4; i++)
				{
						for (int i2 = 0; i2 < 64; i2++)
						{
								for (int i3 = 0; i3 < 64; i3++)
								{
										if ((someArray[i][i2][i3] & 1) == 1)
										{
												int height = i;
												if ((someArray[1][i2][i3] & 2) == 2)
												{
														height--;
												}
												if (height >= 0 && height <= 3)
												{
														addClipping(absX + i2, absY + i3, height, 0x200000, "Here59", -1, -1);
												}
										}
								}
						}
				}
				int objectId = -1;
				int incr;
				while ((incr = str1.getUSmart()) != 0)
				{
						objectId += incr;
						int location = 0;
						int incr2;
						while ((incr2 = str1.getUSmart()) != 0)
						{
								location += incr2 - 1;
								int localX = (location >> 6 & 0x3f);
								int localY = (location & 0x3f);
								int height = location >> 12;
								int objectData = str1.getUByte();
								int type = objectData >> 2;
								int direction = objectData & 0x3;
								if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
								{
										continue;
								}
								if ((someArray[1][localX][localY] & 2) == 2)
								{
										height--;
								}
								if (height >= 0 && height <= 3)
								{
										addObject(objectId, absX + localX, absY + localY, height, type, direction, false);
								}
						}
				}

				CustomClippedTiles.removeClippedTileCompleted();

		}

		public static void addObjectActionTile(int objectId, int x, int y, int height, int xLength, int yLength)
		{
				Region r = getRegion(x, y);
				if (r != null)
				{
						r.verifiedObjects.add(new Objects(objectId, x, y, height, 0, 10, 0));
						addClippingForSolidObject(x, y, height, xLength, yLength, true, 0, 0);
				}
		}

		public static byte[] getBuffer(File f) throws Exception
		{
				if (!f.exists())
						return null;
				byte[] buffer = new byte[(int) f.length()];
				DataInputStream dis = new DataInputStream(new FileInputStream(f));
				dis.readFully(buffer);
				dis.close();
				byte[] gzipInputBuffer = new byte[999999];
				int bufferlength = 0;
				GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
				do
				{
						if (bufferlength == gzipInputBuffer.length)
						{
								Misc.print("Error inflating data.\nGZIP buffer overflow.");
								break;
						}
						int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
						if (readByte == -1)
								break;
						bufferlength += readByte;
				}
				while (true);
				byte[] inflated = new byte[bufferlength];
				System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
				buffer = inflated;
				if (buffer.length < 10)
						return null;
				return buffer;
		}

		public static boolean projectileCanPassThrough(int x, int y, int z)
		{
				return (getClipping(x, y, z) & 0x20000) != 0;
		}

		public static boolean blockedNorth(int x, int y, int z)
		{
				return (getClipping(x, y + 1, z) & 0x1280120) != 0;
		}

		public static boolean blockedEast(int x, int y, int z)
		{
				return (getClipping(x + 1, y, z) & 0x1280180) != 0;
		}

		public static boolean blockedSouth(int x, int y, int z)
		{
				return (getClipping(x, y - 1, z) & 0x1280102) != 0;
		}
		// != 0 means blocked
		// == 0 means not blocked

		public static boolean blockedWest(int x, int y, int height)
		{
				return (getClipping(x - 1, y, height) & 0x1280108) != 0;
		}

		public static boolean blockedNorthEast(int x, int y, int z)
		{
				return (getClipping(x + 1, y + 1, z) & 0x12801e0) != 0;
		}

		public static boolean blockedNorthWest(int x, int y, int z)
		{
				return (getClipping(x - 1, y + 1, z) & 0x1280138) != 0;
		}

		public static boolean blockedSouthEast(int x, int y, int z)
		{
				return (getClipping(x + 1, y - 1, z) & 0x1280183) != 0;
		}

		public static boolean blockedSouthWest(int x, int y, int z)
		{
				return (getClipping(x - 1, y - 1, z) & 0x128010e) != 0;
		}

		public static String foundDynamicClipping1(String string, int x, int y, int z)
		{
				for (int index = 0; index < dynamicTileClipping.size(); index++)
				{
						String parse[] = dynamicTileClipping.get(index).split(" ");
						if (parse[3].equals(string))
						{
								if (x == Integer.parseInt(parse[0]) && y == Integer.parseInt(parse[1]) && z == Integer.parseInt(parse[2]))
								{
										return parse[4];
								}
						}
				}
				return "EMPTY";
		}
}