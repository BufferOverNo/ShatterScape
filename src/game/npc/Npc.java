package game.npc;


import game.content.minigame.barrows.Barrows;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Player;
import game.player.movement.Follow;
import network.packet.Stream;
import utility.Misc;

public class Npc
{

		public long tormentedDemonTimeWeakened;

		public boolean tormentedDemonShield = true;

		public long tormentedDemonTimeChangedPrayer;

		public int tormentedDemonPrayerChangeRandom;

		/**
		 * The last damage style to damage the npc.
		 */
		public int attackStyleDamagedBy = -1;

		/**
		 * True, if the NPC has been summoned by a player.
		 */
		public boolean summoned;

		/**
		 * The identity of the player that summoned this NPC.
		 */
		public int summonedBy;

		public long timeFoundNewTarget;

		public long timeTurnedByPlayer;

		public boolean doNotRespawn;

		public String name;

		/**
		 * The npc index in the npc array.
		 */
		public int npcIndex;

		/**
		 * The npc identity in the client.
		 */
		public int npcType;

		private int x;

		private int y;

		private int height;

		private int spawnPositionX;

		private int spawnPositionY;

		/**
		 * True if the npc has moved during this tick.
		 */
		private boolean moved;

		private int moveX;

		private int moveY;

		public int direction;

		public int spawnX, spawnY;

		public int viewX, viewY;

		/**
		 * NPC attack type, Melee = 0, Ranged = 1, Magic = 2, Dragonfire = 3.
		 */
		public int attackType;

		public int projectileId, endGfx;

		private int spawnedBy;

		public int hitDelayTimer;

		public int currentHitPoints;

		public int maximumHitPoints;

		public int animNumber;

		public int respawnTimer;

		public int enemyX;

		public int enemyY;

		public boolean bottomGfx;

		public boolean applyDead, isDead, needRespawn;

		private boolean walkingHome;

		public boolean underAttack;

		public long timeAttackedAPlayer;

		public String faceAction = "";

		public int attackTimer;

		/**
		 * The time the npc was frozen.
		 */
		public long timeFrozen = 0;

		/**
		 * The amount of time the npc won't be able to move for.
		 * 8000 means 8 seconds.
		 */
		private long frozenLength = 0;

		public boolean isFrozen()
		{
				if (System.currentTimeMillis() - timeFrozen >= getFrozenLength())
				{
						return false;
				}
				return true;
		}

		/**
		 * True if the npc can be frozen.
		 */
		public boolean canBeFrozen()
		{
				if (System.currentTimeMillis() - timeFrozen >= (getFrozenLength() + 3500))
				{
						return true;
				}
				return false;
		}

		private int killerId;

		public int killedBy;

		public int oldIndex;

		public int underAttackBy;

		public long lastDamageTaken;

		public boolean randomWalk;

		public boolean dirUpdateRequired;

		public boolean animUpdateRequired;

		public boolean hitUpdateRequired;

		public boolean updateRequired;

		public boolean forcedChatRequired;

		public String forcedText;

		public Npc(int _npcId, int _npcType)
		{
				npcIndex = _npcId;
				npcType = _npcType;
				direction = -1;
				isDead = false;
				applyDead = false;
				respawnTimer = 0;
				randomWalk = true;
		}

		public Player projectile = null;

		public int transformId;

		public boolean transformUpdateRequired;

		public void appendTransformUpdate(Stream str)
		{
				str.writeWordBigEndianA(transformId);
		}

		public void transform(int Id)
		{
				transformId = Id;
				transformUpdateRequired = true;
				updateRequired = true;
		}

		public void updateNpcMovement(Stream str)
		{
				if (direction == -1)
				{
						if (updateRequired)
						{
								str.writeBits(1, 1);
								str.writeBits(2, 0);
						}
						else
						{
								str.writeBits(1, 0);
						}
				}
				else
				{
						str.writeBits(1, 1);
						str.writeBits(2, 1);
						str.writeBits(3, Misc.xlateDirectionToClient[direction]);
						if (updateRequired)
						{
								str.writeBits(1, 1);
						}
						else
						{
								str.writeBits(1, 0);
						}
				}
		}

		/**
		 * Text update
		 **/

		public void forceChat(String text)
		{
				forcedText = text;
				forcedChatRequired = true;
				updateRequired = true;
		}

		/**
		 * Graphics
		 **/

		public int mask80var1 = 0;

		public int mask80var2 = 0;

		protected boolean mask80update = false;

		public void appendMask80Update(Stream str)
		{
				str.writeWord(mask80var1);
				str.writeDWord(mask80var2);
		}

		public void gfx100(int gfx)
		{
				mask80var1 = gfx;
				mask80var2 = 6553600;
				mask80update = true;
				updateRequired = true;
		}

		public int teleportDelay = -1, teleX, teleY, teleHeight;

		public void npcTeleport(int x, int y, int h)
		{
				needRespawn = true;
				teleX = x;
				teleY = y;
				teleHeight = h;
				updateRequired = true;
				teleportDelay = 0;
		}

		public void gfx0(int gfx)
		{
				mask80var1 = gfx;
				mask80var2 = 65536;
				mask80update = true;
				updateRequired = true;
		}

		public void appendAnimUpdate(Stream str)
		{
				str.writeWordBigEndian(animNumber);
				str.writeByte(1);
		}

		public void requestAnimation(int animId)
		{
				animNumber = animId;
				animUpdateRequired = true;
				updateRequired = true;
		}

		/**
		 * 
		 Face
		 * 
		 **/

		public int FocusPointX = -1, FocusPointY = -1;

		public int face = 0;

		public int hitDiff;

		/**
		 * 1.0 means the npc damage will completely go through to the player. it's damage * hitThroughPrayerAmount.
		 * So 0.5 means only 50% of Npc damage will occur on prayer.
		 */
		public double hitThroughPrayerAmount = 0.0;

		private void appendSetFocusDestination(Stream str)
		{
				str.writeWordBigEndian(FocusPointX);
				str.writeWordBigEndian(FocusPointY);
		}

		public void turnNpc(int i, int j)
		{
				if (System.currentTimeMillis() - timeTurnedByPlayer < 5000)
				{
						return;
				}
				FocusPointX = 2 * i + 1;
				FocusPointY = 2 * j + 1;
				updateRequired = true;

		}

		public void appendFaceEntity(Stream str)
		{
				str.writeWord(face);
		}

		public void facePlayer(int player)
		{
				face = player + 32768;
				dirUpdateRequired = true;
				updateRequired = true;
		}

		public void appendFaceToUpdate(Stream str)
		{
				str.writeWordBigEndian(viewX);
				str.writeWordBigEndian(viewY);
		}

		public void appendNpcUpdateBlock(Stream str, Player c)
		{
				if (!updateRequired)
				{
						return;
				}
				int updateMask = 0;
				if (animUpdateRequired)
						updateMask |= 0x10;
				if (hitUpdateRequired2)
						updateMask |= 8;
				if (mask80update)
						updateMask |= 0x80;
				if (dirUpdateRequired)
						updateMask |= 0x20;
				if (forcedChatRequired)
						updateMask |= 1;
				if (hitUpdateRequired)
						updateMask |= 0x40;
				if (transformUpdateRequired)
						updateMask |= 2;
				if (FocusPointX != -1)
						updateMask |= 4;

				str.writeByte(updateMask);

				if (animUpdateRequired)
						appendAnimUpdate(str);
				if (hitUpdateRequired2)
						appendHitUpdate2(str, c);
				if (mask80update)
						appendMask80Update(str);
				if (dirUpdateRequired)
						appendFaceEntity(str);
				if (forcedChatRequired)
				{
						str.writeString(forcedText);
				}
				if (hitUpdateRequired)
				{
						appendHitUpdate(str, c);
				}
				if (transformUpdateRequired)
				{
						appendTransformUpdate(str);
				}
				if (FocusPointX != -1)
				{
						appendSetFocusDestination(str);
				}

		}

		public void clearUpdateFlags()
		{
				updateRequired = false;
				forcedChatRequired = false;
				hitUpdateRequired = false;
				hitUpdateRequired2 = false;
				animUpdateRequired = false;
				dirUpdateRequired = false;
				mask80update = false;
				forcedText = null;
				setMoveX(0);
				setMoveY(0);
				direction = -1;
				FocusPointX = -1;
				FocusPointY = -1;
				transformUpdateRequired = false;
		}

		public int getNextNpcWalkingDirection()
		{
				int dir;
				int nextXtile = getX() + getMoveX();
				int nextYtile = getY() + getMoveY();
				dir = Misc.direction(getX(), getY(), (nextXtile), (nextYtile));
				int size = 1;
				if (NpcDefinition.getDefinitions()[this.npcType] != null)
				{
						size = NpcDefinition.getDefinitions()[this.npcType].size;
				}

				// Do not let Npc move onto a tile that has another npc on it.
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						Npc npc = NpcHandler.npcs[i];
						if (npc != null && !npc.isDead)
						{

								if (nextXtile == npc.getX() && nextYtile == npc.getY())
								{
										return -1;
								}
						}
				}


				if (dir == -1)
				{
						return -1;
				}

				if (barrowsNpcClipping(nextXtile, nextYtile))
				{
						return -1;
				}
				if (!Region.isStraightPathUnblocked(getX(), getY(), nextXtile, nextYtile, getHeight(), size, size))
				{
						return -1;
				}
				dir >>= 1;
				setX(nextXtile);
				setY(nextYtile);
				return dir;
		}

		private final static int[][] barrowsClipping = {

				{3554, 9714},
				{3555, 9714},
				{3556, 9714},
				{3554, 9715},
				{3555, 9715},
				{3556, 9715},

				{3538, 9703},
				{3539, 9703},
				{3540, 9703},
				{3538, 9704},
				{3539, 9704},
				{3540, 9704},

				{3550, 9682},
				{3550, 9683},
				{3550, 9684},
				{3551, 9682},
				{3551, 9683},
				{3551, 9684},

				{3569, 9685},
				{3569, 9686},
				{3569, 9687},
				{3570, 9685},
				{3570, 9686},
				{3570, 9687},

				{3573, 9705},
				{3573, 9706},
				{3573, 9707},
				{3574, 9705},
				{3574, 9706},
				{3574, 9707},};

		private boolean barrowsNpcClipping(int x, int y)
		{

				for (int index = 0; index < Barrows.COFFIN_AND_BROTHERS.length; index++)
				{
						if (npcType == Barrows.COFFIN_AND_BROTHERS[index][1])
						{
								for (int a = 0; a < barrowsClipping.length; a++)
								{
										if (x == barrowsClipping[a][0] && y == barrowsClipping[a][1])
										{
												return true;
										}
								}
								break;
						}
				}
				return false;
		}

		public void getNextNPCMovement(int i)
		{
				direction = -1;
				if (!NpcHandler.npcs[i].isFrozen())
				{
						direction = getNextNpcWalkingDirection();
				}
		}

		public void appendHitUpdate(Stream str, Player c)
		{
				if (currentHitPoints <= 0)
				{
						isDead = true;
				}
				str.writeWordA(hitDiff);
				str.writeByteS(hitMask);
				str.writeByte(hitIcon);
				str.writeWordA(currentHitPoints);
				str.writeWordA(maximumHitPoints);
		}

		public int hitDiff2 = 0;

		public boolean hitUpdateRequired2 = false;

		public int hitIcon, hitMask, hitIcon2, hitMask2;

		public int transformTimer;

		public int transformIntoId;

		public void appendHitUpdate2(Stream str, Player c)
		{
				if (currentHitPoints <= 0)
				{
						isDead = true;
				}
				str.writeWordA(hitDiff2);
				str.writeByteC(hitMask2);
				str.writeByte(hitIcon2);
				str.writeWordA(currentHitPoints);
				str.writeWordA(maximumHitPoints);
		}

		public String zombieOwner = "";

		public int poisonDamage;

		public int poisonHitsplatsLeft;

		public boolean poisonEvent;

		public int poisonTicksUntillDamage;

		public int getX()
		{
				return x;
		}

		public int getY()
		{
				return y;
		}

		/**
		 * Npcs with size of 2 or more have a different x and y visually and another x and y on the server.
		 * This visual x and y is used for dropping items on the correct spot, following the npc etc..
		 */
		public int getVisualX()
		{
				return Follow.isBigNpc(this.npcType) + this.getX();
		}

		/**
		 * Npcs with size of 2 or more have a different x and y visually and another x and y on the server.
		 * This visual x and y is used for dropping items on the correct spot, following the npc etc..
		 */
		public int getVisualY()
		{
				return Follow.isBigNpc(this.npcType) + this.getY();
		}

		public boolean inBarbDef()
		{
				return (coordsCheck(3147, 3193, 9737, 9778));
		}

		public boolean coordsCheck(int X1, int X2, int Y1, int Y2)
		{
				return getX() >= X1 && getX() <= X2 && getY() >= Y1 && getY() <= Y2;
		}

		public boolean inWild()
		{
				if (getX() > 2941 && getX() < 3392 && getY() > 3518 && getY() < 3966 || getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366)
				{
						return true;
				}
				return false;
		}

		public int getSpawnPositionX()
		{
				return spawnPositionX;
		}

		public void setSpawnPositionX(int makeX)
		{
				this.spawnPositionX = makeX;
		}

		public int getKillerId()
		{
				return killerId;
		}

		public void setKillerId(int killerId)
		{
				if (killerId == 0)
				{
						this.timeAttackedAPlayer = 0;
				}
				this.killerId = killerId;
		}

		public int getSpawnPositionY()
		{
				return spawnPositionY;
		}

		public void setSpawnPositionY(int spawnPositionY)
		{
				this.spawnPositionY = spawnPositionY;
		}

		public void setX(int x)
		{
				this.x = x;
		}

		public void setY(int y)
		{
				this.y = y;
		}

		public boolean isWalkingHome()
		{
				return walkingHome;
		}

		public void setWalkingHome(boolean walkingHome)
		{
				this.walkingHome = walkingHome;
		}

		public int getMoveX()
		{
				return moveX;
		}

		public void setMoveX(int moveX)
		{
				this.moveX = moveX;
		}

		public int getMoveY()
		{
				return moveY;
		}

		public void setMoveY(int moveY)
		{
				this.moveY = moveY;
		}

		public boolean isMoved()
		{
				return moved;
		}

		public void setMoved(boolean moved)
		{
				this.moved = moved;
		}

		public int getSpawnedBy()
		{
				return spawnedBy;
		}

		public void setSpawnedBy(int spawnedBy)
		{
				this.spawnedBy = spawnedBy;
		}

		public int getHeight()
		{
				return height;
		}

		public void setHeight(int heightLevel)
		{
				this.height = heightLevel;
		}

		public long getFrozenLength()
		{
				return frozenLength;
		}

		public void setFrozenLength(long frozenLength)
		{
				if (frozenLength > 0)
				{
						timeFrozen = System.currentTimeMillis();
				}
				this.frozenLength = frozenLength;
		}
}