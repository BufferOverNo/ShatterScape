package network.packet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import core.ServerConfiguration;
import game.content.packet.AttackPlayerPacket;
import game.content.packet.Bank10Packet;
import game.content.packet.Bank5Packet;
import game.content.packet.BankAllPacket;
import game.content.packet.BankX1Packet;
import game.content.packet.BankXPacket;
import game.content.packet.ChallengePlayerPacket;
import game.content.packet.ChangeAppearancePacket;
import game.content.packet.ChangeRegionPacket;
import game.content.packet.ChatPacket;
import game.content.packet.ClanChatPacket;
import game.content.packet.ClickNpcPacket;
import game.content.packet.ClickObjectPacket;
import game.content.packet.ClickingButtonPacket;
import game.content.packet.ClickingInGamePacket;
import game.content.packet.ClickingOtherPacket;
import game.content.packet.CommandPacket;
import game.content.packet.DialoguePacket;
import game.content.packet.DropItemPacket;
import game.content.packet.FirstClickItemPacket;
import game.content.packet.FollowPlayerPacket;
import game.content.packet.IdleLogoutPacket;
import game.content.packet.ItemOnGroundItemPacket;
import game.content.packet.ItemOnItemPacket;
import game.content.packet.ItemOnNpcPacket;
import game.content.packet.ItemOnObjectPacket;
import game.content.packet.MagicOnFloorItemPacket;
import game.content.packet.MagicOnItemsPacket;
import game.content.packet.MoveItemsPacket;
import game.content.packet.PickupItemPacket;
import game.content.packet.PrivateMessagingPacket;
import game.content.packet.RemoveItemPacket;
import game.content.packet.SecondClickItemPacket;
import game.content.packet.SilentPacket;
import game.content.packet.ThirdClickItemPacket;
import game.content.packet.TradePacket;
import game.content.packet.WalkingPacket;
import game.content.packet.WearItemPacket;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;


public class PacketHandler
{

		private static PacketType packetId[] = new PacketType[256];

		static
		{
				SilentPacket u = new SilentPacket();
				packetId[3] = u;
				packetId[202] = u;
				packetId[77] = u;
				packetId[86] = u;
				packetId[74] = u;
				packetId[78] = u;
				packetId[36] = u;
				packetId[226] = u;
				packetId[234] = u;
				packetId[246] = u;
				packetId[148] = u;
				packetId[228] = u;
				packetId[183] = u;
				packetId[18] = u;
				packetId[230] = u;
				packetId[136] = u;
				packetId[189] = u;
				packetId[152] = u;
				packetId[200] = u;
				packetId[85] = u;
				packetId[165] = u;
				packetId[238] = u;
				packetId[150] = u;
				packetId[253] = u;
				packetId[40] = new DialoguePacket();
				ClickObjectPacket co = new ClickObjectPacket();
				packetId[132] = co;
				packetId[252] = co;
				packetId[70] = co;
				packetId[234] = co;
				packetId[57] = new ItemOnNpcPacket();
				ClickNpcPacket cn = new ClickNpcPacket();
				packetId[18] = cn;
				packetId[72] = cn;
				packetId[131] = cn;
				packetId[155] = cn;
				packetId[17] = cn;
				packetId[21] = cn;
				packetId[16] = new SecondClickItemPacket();
				packetId[75] = new ThirdClickItemPacket();
				packetId[122] = new FirstClickItemPacket();
				packetId[241] = new ClickingInGamePacket();
				packetId[4] = new ChatPacket();
				packetId[236] = new PickupItemPacket();
				packetId[87] = new DropItemPacket();
				packetId[185] = new ClickingButtonPacket();
				packetId[130] = new ClickingOtherPacket();
				packetId[103] = new CommandPacket();
				packetId[214] = new MoveItemsPacket();
				packetId[237] = new MagicOnItemsPacket();
				packetId[181] = new MagicOnFloorItemPacket();
				packetId[202] = new IdleLogoutPacket();
				AttackPlayerPacket ap = new AttackPlayerPacket();
				packetId[73] = ap;
				packetId[249] = ap;
				packetId[128] = new ChallengePlayerPacket();
				packetId[39] = new TradePacket();
				packetId[139] = new FollowPlayerPacket();
				packetId[41] = new WearItemPacket();
				packetId[145] = new RemoveItemPacket();
				packetId[117] = new Bank5Packet();
				packetId[43] = new Bank10Packet();
				packetId[129] = new BankAllPacket();
				packetId[101] = new ChangeAppearancePacket();
				packetId[14] = u;
				PrivateMessagingPacket pm = new PrivateMessagingPacket();
				packetId[188] = pm;
				packetId[126] = pm;
				packetId[215] = pm;
				packetId[74] = pm;
				packetId[95] = pm;
				packetId[133] = pm;
				packetId[135] = new BankX1Packet();
				packetId[208] = new BankXPacket();
				WalkingPacket w = new WalkingPacket();
				packetId[98] = w;
				packetId[164] = w;
				packetId[248] = w;
				packetId[53] = new ItemOnItemPacket();
				packetId[192] = new ItemOnObjectPacket();
				packetId[25] = new ItemOnGroundItemPacket();
				ChangeRegionPacket cr = new ChangeRegionPacket();
				packetId[121] = cr;
				packetId[210] = cr;
				packetId[60] = new ClanChatPacket();
		}

		/**
		 * Players that have been added via command to be tracked and the default players logged from the moparscape thread.
		 */
		public static ArrayList<String> packetLogPlayerList = new ArrayList<String>();

		public static ArrayList<String> packetLogData = new ArrayList<String>();

		public static ArrayList<String> appearanceLog = new ArrayList<String>();

		public static ArrayList<String> shoppingLog = new ArrayList<String>();

		public static ArrayList<String> bankLog = new ArrayList<String>();

		public static ArrayList<String> invalidPacketLog = new ArrayList<String>();

		public static ArrayList<String> itemLog = new ArrayList<String>();

		public static ArrayList<String> fakeObjectsLog = new ArrayList<String>();

		public static ArrayList<String> spellbookLog = new ArrayList<String>();

		public static ArrayList<String> tradeAndDuelLog = new ArrayList<String>();

		public static ArrayList<String> chatAndPmLog = new ArrayList<String>();

		public static ArrayList<String> diceLog = new ArrayList<String>();

		public static ArrayList<String> stringAbuseLog = new ArrayList<String>();

		public static ArrayList<String> allKindsOfAbuse = new ArrayList<String>();

		public static ArrayList<String> unUsedObject = new ArrayList<String>();

		/**
		 * All the important packet abuse exploits, if they are modified, then add to here, so i can print all the edits to a file to know which exploits were used.
		 */
		public static ArrayList<String> packetAbuseAlert = new ArrayList<String>();

		public static boolean showIndividualPackets;

		public static void saveCurrentFlaggedPlayers()
		{
				FileUtility.deleteAllLines("backup/logs/packet abuse/flagged players.txt");
				FileUtility.saveArrayContents("backup/logs/packet abuse/flagged players.txt", packetLogPlayerList);
		}

		public static void saveData(String name, String data)
		{
				if (data.contains("Sending"))
				{
						packetLogData.add("-------------------------------------------------------------");
				}
				packetLogData.add(name + " at " + Misc.getDate() + ": " + data);

		}

		private static String getPacketName(int packetType)
		{
				switch (packetType)
				{
						case 40:
								return "Dialogue";
						case 132:
								return "ClickObjectFirst";
						case 252:
								return "ClickObjectSecond";
						case 70:
								return "ClickObjectThird";
						case 57:
								return "ItemOnNpc";
						case 72:
								return "ClickNpcAttack";
						case 131:
								return "ClickNpcMage";
						case 155:
								return "ClickNpcFirst";
						case 17:
								return "ClickNpcSecond";
						case 21:
								return "ClickNpcThird";
						case 16:
								return "ItemClick2";
						case 75:
								return "ItemClick3";
						case 122:
								return "ClickItem";
						case 241:
								return "ClickingInGame";
						case 4:
								return "Chat";
						case 236:
								return "PickUpItem";
						case 87:
								return "DropItem";
						case 185:
								return "ClickingButton";
						case 130:
								return "ClickingOther";
						case 103:
								return "Command";
						case 214:
								return "MoveItems";
						case 237:
								return "MagicOnItems";
						case 181:
								return "MagicOnFloorItems";
						case 202:
								return "IdleLogout";
						case 73:
								return "AttackPlayerNormal";
						case 249:
								return "AttackPlayerMage";
						case 128:
								return "ChallengePlayer";
						case 39:
								return "Trade";
						case 139:
								return "FollowPlayer";
						case 41:
								return "WearItem";
						case 145:
								return "RemoveItem";
						case 117:
								return "Bank5";
						case 43:
								return "Bank10";
						case 129:
								return "BankAll";
						case 101:
								return "ChangeAppearance";
						case 188:
								return "PrivateMessagingAddFriend";
						case 126:
								return "PrivateMessagingSendPm";
						case 215:
								return "PrivateMessagingRemoveFriend";
						case 95:
								return "PrivateMessagingChangePmStatus";
						case 74:
								return "PrivateMessagingRemoveIgnore";
						case 133:
								return "PrivateMessagingAddIgnore";
						case 135:
								return "BankX1";
						case 208:
								return "BankX";
						case 98:
								return "WalkingNpcOrObjectClick";
						case 164:
								return "WalkingTileClick";
						case 248:
								return "WalkingMinimapClick";
						case 53:
								return "ItemOnItem";
						case 192:
								return "ItemOnObject";
						case 25:
								return "ItemOnGroundItem";
						case 121:
								return "ChangeRegion1";
						case 210:
								return "ChangeRegion2";
						case 60:
								return "ClanChat";
				}
				return "Invalid/Unknown packet!";
		}

		/**
		 * Default flagged players on server start-up.
		 */
		public static void defaultFlaggedPlayers()
		{
				String line = "";
				boolean EndOfFile = false;
				BufferedReader fileLocation = null;
				String fileLocationText = "backup/logs/packet abuse/flagged players.txt";
				try
				{
						fileLocation = new BufferedReader(new FileReader(fileLocationText));
				}
				catch (FileNotFoundException fileex)
				{
						Misc.print(fileLocationText + ": file not found.");
						return;
				}
				try
				{
						line = fileLocation.readLine();
				}
				catch (IOException ioexception)
				{
						Misc.print(fileLocationText + ": error loading file.");
				}
				while (!EndOfFile && line != null)
				{
						if (line.isEmpty())
						{
								continue;
						}
						PacketHandler.packetLogPlayerList.add(line);

						try
						{
								line = fileLocation.readLine();
						}
						catch (IOException ioexception1)
						{
								EndOfFile = true;
						}
				}
				try
				{
						fileLocation.close();
				}
				catch (IOException ioexception)
				{
				}
		}

		public static void processPacket(Player player, int packetType, int packetSize)
		{
				PacketType p = packetId[packetType];
				if (p != null && packetType > 0 && packetType < 257 && packetType == player.packetType && packetSize == player.packetSize)
				{
						player.packetsSentThisTick++;
						if (player.packetsSentThisTick > ServerConfiguration.MAXIMUM_PACKETS_PER_TICK)
						{
								//Misc.print("Too many packets sent by: " + player.getPlayerName() + ", amount:" + player.packetsSentThisTick + ", last packet type: " + getPacketName(packetType));
								return;
						}
						if (ServerConfiguration.SHOW_PACKETS)
						{
								player.playerAssistant.sendMessage("PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
						}
						// Uncomment to show packets sent.
						//Misc.print("Name: " + player.getPlayerName() + ", PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
						if (showIndividualPackets)
						{
								Misc.print("Name: " + player.getPlayerName() + ", PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
						}

						boolean trackPlayer = false;
						for (int i = 0; i < packetLogPlayerList.size(); i++)
						{
								if (player.getPlayerName().toLowerCase().equals(packetLogPlayerList.get(i).toLowerCase()))
								{
										trackPlayer = true;
										saveData(player.getPlayerName(), "Sending " + getPacketName(packetType) + ", Packet type: " + packetType + ". Packet size: " + packetSize);
										break;
								}
						}

						try
						{
								p.processPacket(player, packetType, packetSize, trackPlayer);
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}
				else
				{
						player.setDisconnected(true);
						if (ServerConfiguration.DEBUG_MODE)
						{
								Misc.print(player.getPlayerName() + " has been disconnected at " + Misc.getDate() + " for sending invalid Packet type: " + packetType + ". Packet size: " + packetSize);
						}
						invalidPacketLog.add(player.getPlayerName() + " has been disconnected at " + Misc.getDate() + " for sending invalid Packet type: " + packetType + ". Packet size: " + packetSize);
				}
		}

}