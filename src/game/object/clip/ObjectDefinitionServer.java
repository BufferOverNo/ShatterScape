package game.object.clip;

public final class ObjectDefinitionServer
{
		private static ByteStreamExt stream;

		public static int[] streamIndices;

		public static ObjectDefinitionServer class46;

		private static int objects = 0;

		public static int getObjects()
		{
				return objects;
		}

		public static ObjectDefinitionServer getObjectDef(int i)
		{
				if (i > streamIndices.length)
				{
						i = streamIndices.length - 1;
				}

				for (int j = 0; j < 20; j++)
				{
						if (cache[j].type == i)
						{
								return cache[j];
						}
				}

				cacheIndex = (cacheIndex + 1) % 20;
				class46 = cache[cacheIndex];

				if (i > streamIndices.length - 1 || i < 0)
				{
						return null;
				}

				stream.currentOffset = streamIndices[i];

				class46.type = i;
				class46.setDefaults();
				class46.readValues(stream);

				return class46;
		}

		private void setDefaults()
		{
				anIntArray773 = null;
				anIntArray776 = null;
				name = null;
				description = null;
				modifiedModelColors = null;
				originalModelColors = null;
				objectSizeX = 1;
				objectSizeY = 1;
				blocksWalk = true;
				blocksProjectiles = true;
				hasActions = false;
				aBoolean762 = false;
				aBoolean764 = false;
				anInt781 = -1;
				anInt775 = 16;
				actions = null;
				anInt746 = -1;
				anInt758 = -1;
				aBoolean779 = true;
				anInt768 = 0;
				aBoolean736 = false;
				aBoolean766 = false;
				anInt760 = -1;
				anInt774 = -1;
				anInt749 = -1;
				childrenIDs = null;
		}

		public static void loadConfig()
		{
				stream = new ByteStreamExt(getBuffer("loc.dat"));
				ByteStreamExt stream = new ByteStreamExt(getBuffer("loc.idx"));
				objects = stream.readUnsignedWord();
				streamIndices = new int[objects];
				int i = 2;
				for (int j = 0; j < objects; j++)
				{
						streamIndices[j] = i;
						i += stream.readUnsignedWord();
				}
				cache = new ObjectDefinitionServer[20];
				for (int k = 0; k < 20; k++)
				{
						cache[k] = new ObjectDefinitionServer();
				}
		}

		public static byte[] getBuffer(String s)
		{
				try
				{
						java.io.File f = new java.io.File("./data/world osrs/object/" + s);
						if (!f.exists())
						{
								return null;
						}
						byte[] buffer = new byte[(int) f.length()];
						java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
						dis.readFully(buffer);
						dis.close();
						return buffer;
				}
				catch (Exception e)
				{
				}
				return null;
		}

		private void readValues(ByteStreamExt stream)
		{

				int flag = -1;
				do
				{
						int type = stream.readUnsignedByte();
						if (type == 0)
								break;
						if (type == 1)
						{
								int len = stream.readUnsignedByte();
								if (len > 0)
								{
										if (anIntArray773 == null || lowMem)
										{
												anIntArray776 = new int[len];
												anIntArray773 = new int[len];
												for (int k1 = 0; k1 < len; k1++)
												{
														anIntArray773[k1] = stream.readUnsignedWord();
														anIntArray776[k1] = stream.readUnsignedByte();
												}
										}
										else
										{
												stream.currentOffset += len * 3;
										}
								}
						}
						else if (type == 2)
								name = stream.readString();
						else if (type == 3)
								description = stream.readBytes();
						else if (type == 5)
						{
								int len = stream.readUnsignedByte();
								if (len > 0)
								{
										if (anIntArray773 == null || lowMem)
										{
												anIntArray776 = null;
												anIntArray773 = new int[len];
												for (int l1 = 0; l1 < len; l1++)
														anIntArray773[l1] = stream.readUnsignedWord();
										}
										else
										{
												stream.currentOffset += len * 2;
										}
								}
						}
						else if (type == 14)
								objectSizeX = stream.readUnsignedByte();
						else if (type == 15)
								objectSizeY = stream.readUnsignedByte();
						else if (type == 17)
								blocksWalk = false;
						else if (type == 18)
								blocksProjectiles = false;
						else if (type == 19)
								hasActions = (stream.readUnsignedByte() == 1);
						else if (type == 21)
								aBoolean762 = true;
						else if (type == 22)
						{
						}
						else if (type == 23)
								aBoolean764 = true;
						else if (type == 24)
						{
								anInt781 = stream.readUnsignedWord();
								if (anInt781 == 65535)
										anInt781 = -1;
						}
						else if (type == 28)
								anInt775 = stream.readUnsignedByte();
						else if (type == 29)
								stream.readSignedByte();
						else if (type == 39)
								stream.readSignedByte();
						else if (type >= 30 && type < 39)
						{
								if (actions == null)
										actions = new String[5];
								actions[type - 30] = stream.readString();
								if (actions[type - 30].equalsIgnoreCase("hidden"))
										actions[type - 30] = null;
						}
						else if (type == 40)
						{
								int i1 = stream.readUnsignedByte();
								modifiedModelColors = new int[i1];
								originalModelColors = new int[i1];
								for (int i2 = 0; i2 < i1; i2++)
								{
										modifiedModelColors[i2] = stream.readUnsignedWord();
										originalModelColors[i2] = stream.readUnsignedWord();
								}

						}
						else if (type == 60)
								anInt746 = stream.readUnsignedWord();
						else if (type == 62)
						{
						}
						else if (type == 64)
						{
						}
						else if (type == 65)
								stream.readUnsignedWord();
						else if (type == 66)
								stream.readUnsignedWord();
						else if (type == 67)
								stream.readUnsignedWord();
						else if (type == 68)
								anInt758 = stream.readUnsignedWord();
						else if (type == 69)
								anInt768 = stream.readUnsignedByte();
						else if (type == 70)
								stream.readSignedWord();
						else if (type == 71)
								stream.readSignedWord();
						else if (type == 72)
								stream.readSignedWord();
						else if (type == 73)
								aBoolean736 = true;
						else if (type == 74)
								aBoolean766 = true;
						else if (type == 75)
								anInt760 = stream.readUnsignedByte();
						else if (type == 77)
						{
								anInt774 = stream.readUnsignedWord();
								if (anInt774 == 65535)
										anInt774 = -1;
								anInt749 = stream.readUnsignedWord();
								if (anInt749 == 65535)
										anInt749 = -1;
								int j1 = stream.readUnsignedByte();
								childrenIDs = new int[j1 + 1];
								for (int j2 = 0; j2 <= j1; j2++)
								{
										childrenIDs[j2] = stream.readUnsignedWord();
										if (childrenIDs[j2] == 65535)
												childrenIDs[j2] = -1;
								}
						}
				}
				while (true);
				if (flag == -1 && name != "null" && name != null)
				{
						hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
						if (actions != null)
								hasActions = true;
				}
				if (aBoolean766)
				{
						blocksWalk = false;
						blocksProjectiles = false;
				}
				if (anInt760 == -1)
						anInt760 = blocksWalk ? 1 : 0;
		}

		private ObjectDefinitionServer()
		{
				type = -1;
		}

		public boolean hasActions()
		{
				return hasActions || actions != null;
		}

		public boolean hasName()
		{
				return name != null && name.length() > 1;
		}

		public int xLength()
		{
				return objectSizeX;
		}

		public int yLength()
		{
				return objectSizeY;
		}


		public boolean blocksWalk()
		{
				return blocksWalk;
		}

		public boolean solid()
		{
				return aBoolean779;
		}

		public boolean aBoolean736;

		public String name;

		public int objectSizeX;

		public int anInt746;

		int[] originalModelColors;

		public int anInt749;

		public static boolean lowMem;

		public int type;

		public boolean blocksProjectiles;

		public int anInt758;

		public int childrenIDs[];

		private int anInt760;

		public int objectSizeY;

		public boolean aBoolean762;

		public boolean aBoolean764;

		private boolean aBoolean766;

		public boolean blocksWalk;

		public int anInt768;

		private static int cacheIndex;

		int[] anIntArray773;

		public int anInt774;

		public int anInt775;

		int[] anIntArray776;

		public byte description[];

		public boolean hasActions;

		public boolean aBoolean779;

		public int anInt781;

		private static ObjectDefinitionServer[] cache;

		int[] modifiedModelColors;

		public String actions[];
}