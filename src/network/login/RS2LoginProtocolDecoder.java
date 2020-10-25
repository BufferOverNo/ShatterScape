package network.login;

import java.math.BigInteger;
import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotManager;
import game.player.Client;
import game.player.PlayerHandler;
import game.player.PlayerSave;
import game.player.punishment.Blacklist;
import network.connection.InvalidAttempt;
import network.packet.Packet;
import network.packet.PacketHandler;
import network.packet.StaticPacketBuilder;
import utility.ISAACRandomGen;
import utility.Misc;

/**
 * Login protocol decoder.
 * @author Graham
 * @author Ryan / Lmctruck30 <- login Protocol fixes
 *
 */
public class RS2LoginProtocolDecoder extends CumulativeProtocolDecoder
{

		private static final BigInteger RSA_MODULUS = new BigInteger("103771197001696760138645861627115008410568786686191199923231106922942513606142806886255378073442269194090481672900726647008689024021665314562821603892145874108423775851359774351388368787111786115161850243538649852438662514768067535849602163954168354349344645187212610364430565715287893022490289911734587367863");

		private static final BigInteger RSA_EXPONENT = new BigInteger("62534746088240411445682281140444026155713622249789206856708261544084892997253521143259073525375421205600034838176011996230544033366062681131941901889259629391782463492939120757809090575236033030036801642254649649914738743846185533344167169539608044929154521623137684068925096538028921765607836647792456760529");

		public static boolean printOutAddress;

		/**
		* Parses the data in the provided byte buffer and writes it to
		* <code>out</code> as a <code>Packet</code>.
		*
		* @param session The IoSession the data was read from
		* @param in	  The buffer
		* @param out	 The decoder output stream to which to write the <code>Packet</code>
		* @return Whether enough data was available to create a packet
		*/
		@Override
		public boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out)
		{
				//if you need any help, just ask, will do thanks
				synchronized (session)
				{
						Object loginStageObj = session.getAttribute("LOGIN_STAGE");
						int loginStage = 0;
						if (loginStageObj != null)
						{
								loginStage = (Integer) loginStageObj;
						}
						switch (loginStage)
						{
								case 0:
										if (2 <= in.remaining())
										{
												int protocol = in.get() & 0xff;
												@SuppressWarnings("unused")
												int nameHash = in.get() & 0xff;
												if (protocol == 14)
												{
														long serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32) + (long) (java.lang.Math.random() * 99999999D);
														StaticPacketBuilder s1Response = new StaticPacketBuilder();
														s1Response.setBare(true).addBytes(new byte[] {0, 0, 0, 0, 0, 0, 0, 0}).addByte((byte) 0).addLong(serverSessionKey);
														session.setAttribute("SERVER_SESSION_KEY", serverSessionKey);
														session.write(s1Response.toPacket());
														session.setAttribute("LOGIN_STAGE", 1);
												}
												return true;
										}
										else
										{
												in.rewind();
												return false;
										}
								case 1:
										@SuppressWarnings("unused")
										int loginType = -1, loginPacketSize = -1, loginEncryptPacketSize = -1;
										if (2 <= in.remaining())
										{
												loginType = in.get() & 0xff; //should be 16 or 18
												loginPacketSize = in.get() & 0xff;
												loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2);
												if (loginPacketSize <= 0 || loginEncryptPacketSize <= 0)
												{
														Misc.print("Zero or negative login size.");
														session.close();
														return false;
												}
										}
										else
										{
												in.rewind();
												return false;
										}
										if (loginPacketSize <= in.remaining())
										{
												int magic = in.get() & 0xff;
												int version = in.getUnsignedShort();
												if (magic != 255)
												{
														Misc.print("Wrong magic id.");
														session.close();
														return false;
												}
												@SuppressWarnings("unused")
												int lowMem = in.get() & 0xff;
												for (int i = 0; i < 9; i++)
												{
														in.getInt();
												}

												loginEncryptPacketSize--;
												if (loginEncryptPacketSize != (in.get() & 0xff))
												{
														session.close();
														return false;
												}
												byte[] encryptionBytes = new byte[loginEncryptPacketSize];
												in.get(encryptionBytes);
												ByteBuffer rsaBuffer = ByteBuffer.wrap(new BigInteger(encryptionBytes).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray());
												if ((rsaBuffer.get() & 0xff) != 10)
												{
														session.close();
														return false;
												}
												long clientSessionKey = rsaBuffer.getLong();
												long serverSessionKey = rsaBuffer.getLong();
												int clientIdVersion = rsaBuffer.getInt();
												String addressMac = readRS2String(rsaBuffer);
												String uidAddress = readRS2String(rsaBuffer);
												String name = readRS2String(rsaBuffer);
												if (ServerConfiguration.DEBUG_MODE)
												{
														if (name.toLowerCase().equals("m"))
														{
																name = "mgt madness";
														}
												}
												String pass = readRS2String(rsaBuffer);
												int sessionKey[] = new int[4];
												sessionKey[0] = (int) (clientSessionKey >> 32);
												sessionKey[1] = (int) clientSessionKey;
												sessionKey[2] = (int) (serverSessionKey >> 32);
												sessionKey[3] = (int) serverSessionKey;
												ISAACRandomGen inC = new ISAACRandomGen(sessionKey);
												for (int i = 0; i < 4; i++)
												{
														sessionKey[i] += 50;
												}
												ISAACRandomGen outC = new ISAACRandomGen(sessionKey);
												boolean outdated = false;
												if (clientIdVersion != ServerConfiguration.UID)
												{
														outdated = true;
												}
												load(session, clientIdVersion, name, pass, inC, outC, version, outdated, addressMac.toLowerCase(), uidAddress.toLowerCase());
												session.getFilterChain().remove("protocolFilter");
												session.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(new GameCodecFactory(inC)));

												return true;
										}
										else
										{
												in.rewind();
												return false;
										}
						}
				}
				return false;
		}

		private synchronized void load(final IoSession session, final int uid, String name, String pass, final ISAACRandomGen inC, ISAACRandomGen outC, int version, boolean uidOutdated, String macAddress, String uidAddress)
		{
				session.setAttribute("opcode", -1);
				session.setAttribute("size", -1);
				int loginDelay = 1;
				int returnCode = 2; // 255 is maximum, i've set all mine to null...

				name = name.trim();
				pass = pass.toLowerCase();

				if (!name.matches("[A-Za-z0-9 ]+"))
				{
						returnCode = 4;
				}

				if (name.length() > 12)
				{
						returnCode = 8;
				}
				if (uidOutdated)
				{
						returnCode = 24;
				}
				uidOutdated = false;
				Client player = new Client(session, -1, false);
				player.setPlayerName(name);
				player.playerPass = pass;
				player.setInStreamDecryption(inC);
				player.setOutStreamDecryption(outC);
				player.getOutStream().packetEncryption = outC;
				player.addressIp = ((InetSocketAddress) player.getSession().getRemoteAddress()).getAddress().getHostAddress();
				player.saveCharacter = false;
				if (PlayerHandler.isPlayerOn(name))
				{
						returnCode = 5;
				}

				if (PlayerHandler.playerCount > ServerConstants.MAXIMUM_PLAYERS)
				{
						returnCode = 7;
				}

				if (Server.UpdateServer)
				{
						returnCode = 14;
				}

				macAddress = scanMac(name, macAddress);
				uidAddress = scanUid(name, uidAddress);
				if (returnCode == 2)
				{
						if (Blacklist.isBlacklisted(name, player.addressIp, macAddress, player.playerPass, uidAddress))
						{
								returnCode = 10;
						}
				}
				if (returnCode == 2)
				{
						for (int index = 0; index < ServerConstants.flaggedNames.length; index++)
						{
								if (name.toLowerCase().contains(ServerConstants.flaggedNames[index]))
								{
										returnCode = 10;
								}
						}
				}
				if (returnCode == 2)
				{
						if (!InvalidAttempt.canConnect(player.addressIp, macAddress, uidAddress, name))
						{
								returnCode = 16;
						}
				}
				if (returnCode == 2)
				{
						if (InvalidAttempt.autoBlackListed(player.addressIp, macAddress, uidAddress))
						{
								if (printOutAddress)
								{
										Misc.printDontSave("Print: [" + player.addressIp + "] [" + macAddress + "] [" + uidAddress + "]");
								}
								returnCode = 10;
						}
				}



				if (returnCode == 2)
				{
						int load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, false);
						//load 1 == successfull.
						if (load == 0)
						{
								player.setPlayerName(Misc.capitalize(player.getPlayerName()));
						}
						boolean skip = false;
						if (InvalidAttempt.isFlaggedAccount(player, name, player.addressIp, macAddress, uidAddress, pass, load))
						{
								returnCode = 10;
								skip = true;
						}
						if (!skip)
						{
								if (load == 3) // 3 = wrong password.
								{
										returnCode = 3;
										player.saveFile = false;
										String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressUid", 3);
										String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "addressMac", 3);
										String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.CHARACTER_LOCATION + name + ".txt", "lastSavedIpAddress", 3);
										boolean notSamePerson = true;
										if (originalIp.equalsIgnoreCase(player.addressIp) || originalMac.equalsIgnoreCase(macAddress) || originalUid.equalsIgnoreCase(uidAddress))
										{
												notSamePerson = false;
										}
										if (notSamePerson)
										{
												InvalidAttempt.addToLog(name, player.addressIp, macAddress, uidAddress, pass, originalMac, originalUid);
										}
										InvalidAttempt.invalidAttempts.add(new InvalidAttempt(player.addressIp, macAddress, uidAddress, name, notSamePerson, pass));
										if (returnCode != 10)
										{
												if (InvalidAttempt.isBruteforceHacker(player.addressIp, macAddress, uidAddress, name))
												{
														returnCode = 10;
												}
										}
								}
								else
								{
										for (int i = 0; i < player.playerEquipment.length; i++)
										{
												if (player.playerEquipment[i] == 0)
												{
														player.playerEquipment[i] = -1;
														player.playerEquipmentN[i] = 0;
												}
										}
										if (!Server.playerHandler.newPlayerClient(player))
										{
												returnCode = 7;
												player.saveFile = false;
										}
										else
										{
												player.saveFile = true;
										}
								}
						}
				}
				player.lastMacAddress = player.addressMac;
				player.lastUidAddress = player.addressUid;
				player.addressMac = macAddress;
				player.addressUid = uidAddress;
				player.packetType = -1;
				player.packetSize = 0;

				StaticPacketBuilder bldr = new StaticPacketBuilder();
				bldr.setBare(true);
				bldr.addByte((byte) returnCode);
				if (returnCode == 2)
				{
						player.saveCharacter = true;
						bldr.addByte((byte) player.playerRights);
				}
				else if (returnCode == 21)
				{
						bldr.addByte((byte) loginDelay);
				}
				else
				{
						bldr.addByte((byte) 0);
				}
				player.setActive(true);
				bldr.addByte((byte) 0);
				Packet pkt = bldr.toPacket();
				session.setAttachment(player);
				session.write(pkt).addListener(new IoFutureListener()
				{
						@Override
						public void operationComplete(IoFuture arg0)
						{
								session.getFilterChain().remove("protocolFilter");
								session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new GameCodecFactory(inC)));
						}
				});
		}

		private String scanUid(String name, String uidAddress)
		{
				if (uidAddress.contains("\r") || uidAddress.contains("\n"))
				{
						PacketHandler.stringAbuseLog.add(name + " at " + Misc.getDate());
						PacketHandler.stringAbuseLog.add("Uid address abuse:");
						PacketHandler.stringAbuseLog.add("Contains backwards slash r or n");
						return "Exploited uid address";
				}
				return uidAddress;
		}

		private String scanMac(String name, String macAddress)
		{
				if (macAddress.contains("\r") || macAddress.contains("\n"))
				{
						PacketHandler.stringAbuseLog.add(name + " at " + Misc.getDate());
						PacketHandler.stringAbuseLog.add("Mac address abuse:");
						PacketHandler.stringAbuseLog.add("Contains backwards slash r or n");
						return "Exploited mac address";
				}
				return macAddress;
		}

		public static Client loadBot(String name, String pass)
		{
				BotManager.currentBotNumber++;
				int loginDelay = 1;
				int returnCode = 2; // 255 is maximum

				name = name.trim();
				pass = pass.toLowerCase();

				Client player = new Client(null, -1, true);


				if (ServerConfiguration.STABILITY_TEST)
				{

				}
				else
				{
						if (BotManager.currentBotNumber <= 1)
						{
								player.botPkType = "PURE";
						}
						else if (BotManager.currentBotNumber <= 2)
						{
								player.botPkType = "INITIATE";
						}
						else if (BotManager.currentBotNumber <= 3)
						{
								player.botPkType = "BERSERKER";
						}
						else if (BotManager.currentBotNumber <= 4)
						{
								player.botPkType = "RANGED TANK";
						}
						else if (BotManager.currentBotNumber <= 5)
						{
								player.botPkType = "MELEE";
						}
						else if (BotManager.currentBotNumber <= 10)
						{
								player.botPkType = "";
						}
						player.gameModeTitle = "[Bot]";
				}
				player.setPlayerName(name);
				player.playerPass = pass;
				player.getOutStream().packetEncryption = null;

				player.saveCharacter = false;

				if (PlayerHandler.isPlayerOn(name))
				{
						returnCode = 5;
				}

				if (returnCode == 2)
				{
						int load = 13;
						if (!ServerConfiguration.STABILITY_TEST)
						{
								load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, true);
						}
						//load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, true);

						if (load == 3) // 3 = wrong password.
						{
								returnCode = 3;
								player.saveFile = false;
						}
						else
						{
								for (int i = 0; i < player.playerEquipment.length; i++)
								{
										if (player.playerEquipment[i] == 0)
										{
												player.playerEquipment[i] = -1;
												player.playerEquipmentN[i] = 0;
										}
								}
								if (!Server.playerHandler.newPlayerClient(player))
								{
										returnCode = 7;
										player.saveFile = false;
								}
								else
								{
										player.saveFile = true;
								}
						}
				}

				player.packetType = -1;
				player.packetSize = 0;

				StaticPacketBuilder bldr = new StaticPacketBuilder();
				bldr.setBare(true);
				bldr.addByte((byte) returnCode);
				if (returnCode == 2)
				{
						player.saveCharacter = true;
						bldr.addByte((byte) player.playerRights);
				}
				else if (returnCode == 21)
				{
						bldr.addByte((byte) loginDelay);
				}
				else
				{
						bldr.addByte((byte) 0);
				}
				player.setActive(true);
				bldr.addByte((byte) 0);


				return player;
		}


		private synchronized String readRS2String(ByteBuffer in)
		{
				StringBuilder sb = new StringBuilder();
				byte b;
				while ((b = in.get()) != 10)
				{
						sb.append((char) b);
				}
				return sb.toString();
		}



		/**
		 * Releases the buffer used by the given session.
		 *
		 * @param session The session for which to release the buffer
		 * @throws Exception if failed to dispose all resources
		 */
		@Override
		public void dispose(IoSession session) throws Exception
		{
				super.dispose(session);
		}

}