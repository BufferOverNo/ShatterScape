package game.player;

import java.util.HashMap;
import java.util.Map;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.interfaces.AreaInterface;
import game.content.minigame.TargetSystem;
import game.content.minigame.barrows.Barrows;
import game.content.miscellaneous.HackLog;
import game.content.miscellaneous.PlayerGameTime;
import game.content.miscellaneous.PmLog;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.object.clip.Region;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import game.player.punishment.RagBan;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import utility.Misc;

public class PlayerAssistant
{
		private Player player;

		public PlayerAssistant(Player Player)
		{
				this.player = Player;
		}


		public boolean objectIsAt(int x, int y)
		{
				return player.getObjectX() == x && player.getObjectY() == y;
		}


		public boolean objectIsAt(int x, int y, int height)
		{
				return player.getObjectX() == x && player.getObjectY() == y && player.getHeight() == height;
		}

		public void peakIntoResourceWildArea()
		{
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++)
				{
						Player loop = PlayerHandler.players[index];
						if (loop == null)
						{
								continue;
						}
						if (Area.inResourceWilderness(loop))
						{
								player.getPA().sendMessage("Someone is in there..");
								return;
						}
				}
				player.getPA().sendMessage("No one is in here.");
		}


		/**
		 * Reset stats to base values, such as 99 strength instead of 118.
		 */
		public void resetStats()
		{
				for (int index = 0; index < 7; index++)
				{
						if (player.currentCombatSkillLevel[index] > player.baseSkillLevel[index])
						{
								player.currentCombatSkillLevel[index] = player.baseSkillLevel[index];
								Skilling.updateSkillTabFrontTextMain(player, index);
						}
				}
		}

		public boolean playerOnNpc(Player player, Npc npc)
		{
				if (npc == null)
				{
						return false;
				}
				return player.getX() == npc.getX() && player.getY() == npc.getY();
		}

		public void toggleBots(boolean dialogue)
		{
				player.displayBots = !player.displayBots;
				updateDisplayBots();
				String option = player.displayBots ? "on" : "off";
				if (dialogue)
				{
						player.getDH().sendStatement("Bots have been turned: " + option + ".");
				}
				else
				{
						player.getPA().sendMessage("Bots have been turned: " + option + ".");
				}
		}

		public void updateDisplayBots()
		{
				String option = player.displayBots ? "on" : "off";
				player.getPA().sendMessage(":packet:displaybots:" + option);
		}

		public void setInterfaceClicked(int interfaceId, boolean clicked)
		{
				player.getPA().sendMessage(":packet:setclicked " + interfaceId + " " + clicked);
		}

		public void setTextClicked(int interfaceId, boolean clicked)
		{
				player.getPA().sendMessage(":packet:settextclicked " + interfaceId + " " + clicked);
		}

		public void alertNotSameIp()
		{
				if (!player.isTutorialComplete())
				{
						return;
				}
				if (!player.addressIp.equals(player.lastSavedIpAddress))
				{
						player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Warning:");
						player.playerAssistant.sendMessage("Last ip address connected to this account is: " + player.lastSavedIpAddress);
						player.playerAssistant.sendMessage("Last logged in (hours): " + PlayerGameTime.calculateHoursFromLastVisit(player));
						player.playerAssistant.sendMessage("Your current ip address is: " + player.addressIp);
						HackLog.addNewHackEntry(player);
				}
		}

		/**
		 * Cancel specific animations from creating this "drag" effect while moving.
		 */
		public void animationDragCancel()
		{
				for (int value = 0; value < ServerConstants.animationCancel.length; value++)
				{

						if (player.getLastAnimation() == ServerConstants.animationCancel[value])
						{
								player.startAnimation(65535);
								return;
						}
				}
		}

		public void donatorGFX()
		{
				if (!player.isNormalRank())
				{
						player.gfx0(2009);
						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										container.stop();
								}

								@Override
								public void stop()
								{
										player.gfx0(2009);
								}
						}, 1);
				}
		}

		/**
		 * This event will save the player after the player has been online for 5 mins.
		 */
		public void saveGameEvent()
		{
				if (ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				if (player.isCombatBot())
				{
						return;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{

								VoteManager.voteAlert(player);
								PlayerSave.saveGame(player);
						}

						@Override
						public void stop()
						{

						}
				}, 2000); // 20 minutes..

		}

		/**
		 * This event is called per game tick to save the game.
		 */
		public void loopedSave()
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				if (ServerConfiguration.STABILITY_TEST)
				{
						return;
				}
				if (player.isCombatBot())
				{
						return;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								PlayerSave.saveGame(player);
						}

						@Override
						public void stop()
						{

						}
				}, 1);

		}

		public void sendClan(String name, String message, String clan, int rights)
		{
				if (player.isBot)
				{
						return;
				}
				player.getOutStream().createFrameVarSizeWord(217);
				player.getOutStream().writeString(name);
				player.getOutStream().writeString(message);
				player.getOutStream().writeString(clan);
				player.getOutStream().writeWord(rights);
				player.getOutStream().endFrameVarSize();
		}

		public void sendFrame34a(int frame, int item, int slot, int amount)
		{
				if (player.isBot)
				{
						return;
				}
				if (item <= 0)
				{
						item = -1;
				}
				player.getOutStream().createFrameVarSizeWord(34);
				player.getOutStream().writeWord(frame);
				player.getOutStream().writeByte(slot);
				player.getOutStream().writeWord(item + 1);
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(amount);
				player.getOutStream().endFrameVarSizeWord();
		}

		public void multiWay(int i1)
		{
				if (player.isBot)
				{
						return;
				}
				player.getOutStream().createFrame(61);
				player.getOutStream().writeByte(i1);
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}

		public Map<Integer, interfaceColourStore> interfaceColour = new HashMap<Integer, interfaceColourStore>();

		public class interfaceColourStore
		{
				public int colour;

				public int id;

				public interfaceColourStore(int colour, int id)
				{
						this.colour = colour;
						this.id = id;
				}

		}

		/**
		* @return
		* 			True, if the player already has the text in the interface id.
		*/
		public boolean alreadyHasColourInFrame(int colour, int id)
		{
				if (interfaceColour.containsKey(id))
				{
						interfaceColourStore t = interfaceColour.get(id);
						if (colour == t.colour)
						{
								return true;
						}
						t.colour = colour;
				}
				else
				{
						if (colour == ServerConstants.RED_HEX)
						{
								return true;
						}
						interfaceColour.put(id, new interfaceColourStore(colour, id));
				}
				return false;
		}

		public Map<Integer, InterfaceStore> interfaceText = new HashMap<Integer, InterfaceStore>();

		public class InterfaceStore
		{
				public int id;

				public String text;

				public InterfaceStore(String s, int id)
				{
						this.text = s;
						this.id = id;
				}

		}

		/**
		* @return
		* 			True, if the player already has the text in the interface id.
		*/
		public boolean alreadyHasTextInFrame(String text, int id)
		{
				for (int i = 0; i < ServerConstants.interfaceFramesIgnoreRepeat.length; i++)
				{
						if (id == ServerConstants.interfaceFramesIgnoreRepeat[i])
						{
								return false;
						}
				}

				// Dialogue option ids. It changes to Please wait client sided, so must update it here all the time.
				if (id >= 2461 && id <= 2498)
				{
						return false;
				}

				// Quest interface.
				if (id >= 25008 && id <= 25098)
				{
						return false;
				}

				if (id == 24396 || id == 24391)
				{
						return false;
				}

				// Pvp blacklist.
				if (id >= 22707 && id <= 22756)
				{
						return false;
				}

				// Guide interface.
				if (id >= 22556 && id <= 22569)
				{
						return false;
				}

				// Npc kills frames.
				if (id >= 25755 && id <= 25800)
				{
						return false;
				}

				// Rare drop frames.
				if (id >= 25855 && id <= 25955)
				{
						return false;
				}

				// Achievement titles on the scroll tab.
				if (id >= 19384 && id <= 19424)
				{
						return false;
				}

				// Titles on the scroll tab of titles interface.
				if (id >= 22283 && id <= 22382)
				{
						return false;
				}

				// Clan chat interfaces.
				if (id >= 24600 && id <= 24699)
				{
						return false;
				}

				// Clan chat banned list.
				if (id >= 19607 && id <= 19656)
				{
						return false;
				}

				// Clan chat moderator list.
				if (id >= 19596 && id <= 19605)
				{
						return false;
				}

				// Teleport interface.
				if (id >= 19738 && id <= 19794)
				{
						return false;
				}
				if (interfaceText.containsKey(id))
				{
						InterfaceStore t = interfaceText.get(id);
						if (text.equals(t.text))
						{
								return true;
						}
						t.text = text;
				}
				else
				{
						interfaceText.put(id, new InterfaceStore(text, id));
				}
				return false;
		}

		public void sendFrame126(String text, int id)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						if (alreadyHasTextInFrame(text, id))
						{
								return;
						}
						player.getOutStream().createFrameVarSizeWord(126);
						player.getOutStream().writeString(text);
						player.getOutStream().writeWordA(id);
						player.getOutStream().endFrameVarSizeWord();
						player.flushOutStream();
				}
		}

		public void setSkillLevel(int skillNum, int currentLevel, int XP)
		{
				if (player.isBot)
				{
						return;
				}

				// So it updates correctly on log-in.
				if (skillNum == ServerConstants.MAGIC)
				{
						currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(134);
						player.getOutStream().writeByte(skillNum);
						player.getOutStream().writeDWord_v1(XP);
						player.getOutStream().writeByte(currentLevel);
						player.flushOutStream();
				}
		}

		public void sendFrame106(int sideIcon)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(106);
						player.getOutStream().writeByteC(sideIcon);
						player.flushOutStream();
						requestUpdates();
				}
		}

		public void sendFrame107()
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(107);
						player.flushOutStream();
				}
		}

		public void sendFrame36(int id, int state)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(36);
						player.getOutStream().writeWordBigEndian(id);
						player.getOutStream().writeByte(state);
						player.flushOutStream();
				}
		}

		public void sendFrame185(int Frame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(185);
						player.getOutStream().writeWordBigEndianA(Frame);
				}
		}

		public void displayInterface(int interfaceId)
		{
				if (player.isBot)
				{
						return;
				}
				player.isUsingDeathInterface = false;
				if (player.getOutStream() != null && player != null)
				{
						player.interfaceDisplayed = interfaceId;
						player.getOutStream().createFrame(97);
						player.getOutStream().writeWord(interfaceId);
						player.flushOutStream();
				}
		}

		public void sendFrame248(int MainFrame, int SubFrame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(248);
						player.getOutStream().writeWordA(MainFrame);
						player.getOutStream().writeWord(SubFrame);
						player.flushOutStream();
				}
		}

		public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(246);
						player.getOutStream().writeWordBigEndian(MainFrame);
						player.getOutStream().writeWord(SubFrame);
						player.getOutStream().writeWord(SubFrame2);
						player.flushOutStream();
				}
		}

		public void sendFrame171(int MainFrame, int SubFrame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(171);
						player.getOutStream().writeByte(MainFrame);
						player.getOutStream().writeWord(SubFrame);
						player.flushOutStream();
				}
		}

		public void sendFrame200(int MainFrame, int SubFrame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.playerAssistant.sendMessage(":npctype" + player.getNpcType());
						player.getOutStream().createFrame(200);
						player.getOutStream().writeWord(MainFrame);
						player.getOutStream().writeWord(SubFrame);
						player.flushOutStream();
				}
		}

		public void sendFrame70(int i, int o, int id)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(70);
						player.getOutStream().writeWord(i);
						player.getOutStream().writeWordBigEndian(o);
						player.getOutStream().writeWordBigEndian(id);
						player.flushOutStream();
				}
		}

		public void sendFrame75(int MainFrame, int SubFrame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(75);
						player.getOutStream().writeWordBigEndianA(MainFrame);
						player.getOutStream().writeWordBigEndianA(SubFrame);
						player.flushOutStream();
				}
		}

		public void sendFrame164(int Frame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(164);
						player.getOutStream().writeWordBigEndian_dup(Frame);
						player.flushOutStream();
				}
		}

		public void sendFrame214()
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrameVarSizeWord(214);

						for (long ignore : player.ignores)
						{
								if (ignore > 0)
								{
										player.getOutStream().writeQWord(ignore);
								}
						}

						player.getOutStream().endFrameVarSizeWord();
						player.flushOutStream();
				}
		}

		public void setPrivateMessaging(int i)
		{ // friends and ignore list status
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(221);
						player.getOutStream().writeByte(i);
						player.flushOutStream();
				}
		}

		public void setChatOptions(int publicChat, int privateChat, int tradeBlock)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(206);
						player.getOutStream().writeByte(publicChat);
						player.getOutStream().writeByte(privateChat);
						player.getOutStream().writeByte(tradeBlock);
						player.flushOutStream();
				}
		}

		public void sendFrame87(int id, int state)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(87);
						player.getOutStream().writeWordBigEndian_dup(id);
						player.getOutStream().writeDWord_v1(state);
						player.flushOutStream();
				}
		}

		public void sendPM(String pmSender, long name, int rights, byte[] chatmessage, int messagesize, boolean trackPlayer)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrameVarSize(196);
						player.getOutStream().writeQWord(name);
						player.getOutStream().writeDWord(player.lastChatId++);
						player.getOutStream().writeByte(rights);
						player.getOutStream().writeBytes(chatmessage, messagesize, 0);
						player.getOutStream().endFrameVarSize();
						player.flushOutStream();
						PmLog.addPmLog(pmSender, player.getPlayerName(), Misc.textUnpack(chatmessage, messagesize));
						if (trackPlayer)
						{
								PacketHandler.chatAndPmLog.add("At " + Misc.getDate());
								PacketHandler.chatAndPmLog.add("From: " + pmSender + ", to: " + player.getPlayerName() + ", " + Misc.textUnpack(chatmessage, messagesize));
						}
				}
		}

		public void createPlayerHints(int type, int id)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						if (id >= 0)
						{
								Player target = PlayerHandler.players[id];
								if (target != null)
								{
										player.getPA().sendMessage(":packet:targethint:" + target.getX() + ":" + target.getY() + ":" + id);
								}

						}
						player.getOutStream().createFrame(254);
						player.getOutStream().writeByte(type);
						player.getOutStream().writeWord(id);
						player.getOutStream().write3Byte(0);
						player.flushOutStream();
				}
		}

		public void loadPM(long playerName, int world)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						if (world != 0)
						{
								world += 9;
						}
						player.getOutStream().createFrame(50);
						player.getOutStream().writeQWord(playerName);
						player.getOutStream().writeByte(world);
						player.flushOutStream();
				}
		}

		public void removeAllItems()
		{
				for (int i = 0; i < player.playerItems.length; i++)
				{
						player.playerItems[i] = 0;
				}
				for (int i = 0; i < player.playerItemsN.length; i++)
				{
						player.playerItemsN[i] = 0;
				}
				ItemAssistant.resetItems(player, 3214);
		}

		public void closeInterfaces()
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.interfaceDisplayed = 0;
						player.resetActionIdUsed();
						player.hasDialogueOptionOpened = false;
						player.getOutStream().createFrame(219);
						player.flushOutStream();
						player.getTradeAndDuel().declineTrade1(true);
						player.usingShop = false;
						player.setUsingBankInterface(false);
						player.canUseGameModeInterface = false;
						player.usingEquipmentBankInterface = false;
						player.shopId = 0;
						player.setDialogueAction(0);
						if (!player.doNotClosePmInterface)
						{
								player.getPA().sendMessage(":packet:closepminterface");
						}
						player.doNotClosePmInterface = false;
				}
		}

		public void sendFrame34(int itemId, int itemSlot, int interfaceId, int amount)
		{
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrameVarSizeWord(34); // init item to smith screen
						player.getOutStream().writeWord(interfaceId); // Column Across Smith Screen
						player.getOutStream().writeByte(126); // It was 4, which bugged out itemSlot 4.
						player.getOutStream().writeDWord(itemSlot); // Row Down The Smith Screen
						player.getOutStream().writeWord(itemId + 1); // item
						player.getOutStream().writeByte(amount); // how many there are?
						player.getOutStream().endFrameVarSizeWord();
				}
		}

		public void walkableInterface(int id)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(208);
						player.getOutStream().writeWordBigEndian_dup(id);
						player.flushOutStream();
				}
		}

		public int mapStatus = 0;

		public void sendFrame99(int state)
		{ // used for disabling map
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						if (mapStatus != state)
						{
								mapStatus = state;
								player.getOutStream().createFrame(99);
								player.getOutStream().writeByte(state);
								player.flushOutStream();
						}
				}
		}

		/**
		 * Creating projectile
		 **/
		public void createProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(85);
						player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
						player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
						player.getOutStream().createFrame(117);
						player.getOutStream().writeByte(angle);
						player.getOutStream().writeByte(offY);
						player.getOutStream().writeByte(offX);
						player.getOutStream().writeWord(lockon);
						player.getOutStream().writeWord(gfxMoving);
						player.getOutStream().writeByte(startHeight);
						player.getOutStream().writeByte(endHeight);
						player.getOutStream().writeWord(time);
						player.getOutStream().writeWord(speed);
						player.getOutStream().writeByte(slope);
						player.getOutStream().writeByte(64);
						player.flushOutStream();
				}
		}

		public void createProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(85);
						player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
						player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
						player.getOutStream().createFrame(117);
						player.getOutStream().writeByte(angle);
						player.getOutStream().writeByte(offY);
						player.getOutStream().writeByte(offX);
						player.getOutStream().writeWord(lockon);
						player.getOutStream().writeWord(gfxMoving);
						player.getOutStream().writeByte(startHeight);
						player.getOutStream().writeByte(endHeight);
						player.getOutStream().writeWord(time);
						player.getOutStream().writeWord(speed);
						player.getOutStream().writeByte(slope);
						player.getOutStream().writeByte(64);
						player.flushOutStream();
				}
		}

		// projectiles for everyone within 25 squares
		public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player p = PlayerHandler.players[i];
						if (p != null)
						{
								Player person = p;
								if (person != null)
								{
										if (person.getOutStream() != null)
										{
												if (person.playerAssistant.distanceToPoint(x, y) <= 25)
												{
														if (p.getHeight() == player.getHeight())
																person.getPA().createProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
												}
										}
								}
						}
				}
		}

		public void createPlayersProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player p = PlayerHandler.players[i];
						if (p != null)
						{
								Player person = p;
								if (person != null)
								{
										if (person.getOutStream() != null)
										{
												if (person.playerAssistant.distanceToPoint(x, y) <= 25)
												{
														person.getPA().createProjectile2(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
												}
										}
								}
						}
				}
		}

		/**
		 ** GFX
		 **/
		public void stillGfx(int id, int x, int y, int height, int time)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(85);
						player.getOutStream().writeByteC(y - (player.getMapRegionY() * 8));
						player.getOutStream().writeByteC(x - (player.getMapRegionX() * 8));
						player.getOutStream().createFrame(4);
						player.getOutStream().writeByte(0);
						player.getOutStream().writeWord(id);
						player.getOutStream().writeByte(height);
						player.getOutStream().writeWord(time);
						player.flushOutStream();
				}
		}

		// creates gfx for everyone
		public void createPlayersStillGfx(int id, int x, int y, int height, int time)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						Player p = PlayerHandler.players[i];
						if (p != null)
						{
								Player person = p;
								if (person != null)
								{
										if (person.getOutStream() != null)
										{
												if (person.playerAssistant.distanceToPoint(x, y) <= 25)
												{
														person.getPA().stillGfx(id, x, y, height, time);
												}
										}
								}
						}
				}
		}

		/**
		 * Objects, add and remove
		 **/
		public void spawnClientObject(int objectId, int objectX, int objectY, int face, int objectType)
		{
				if (player.isBot)
				{
						return;
				}

				// Packet 151 on the client handleSecondaryPacket method.
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(85);
						player.getOutStream().writeByteC(objectY - (player.getMapRegionY() * 8));
						player.getOutStream().writeByteC(objectX - (player.getMapRegionX() * 8));
						player.getOutStream().createFrame(101);
						player.getOutStream().writeByteC((objectType << 2) + (face & 3));
						player.getOutStream().writeByte(0);
						if (objectId != -1)
						{ // removing
								player.getOutStream().createFrame(151);
								player.getOutStream().writeByteS(0);
								player.getOutStream().writeWordBigEndian(objectId);
								player.getOutStream().writeByteS((objectType << 2) + (face & 3));
						}
						player.flushOutStream();
				}
		}

		public void objectNoClipping(int objectId, int objectX, int objectY, int face, int objectType)
		{
				if (player.isBot)
				{
						return;
				}
				// Packet 151 on the client handleSecondaryPacket method.
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrame(85);
						player.getOutStream().writeByteC(objectY - (player.getMapRegionY() * 8));
						player.getOutStream().writeByteC(objectX - (player.getMapRegionX() * 8));
						player.getOutStream().createFrame(101);
						player.getOutStream().writeByteC((objectType << 2) + (face & 3));
						player.getOutStream().writeByte(0);
						if (objectId != -1)
						{ // removing
								player.getOutStream().createFrame(151);
								player.getOutStream().writeByteS(0);
								player.getOutStream().writeWordBigEndian(objectId);
								player.getOutStream().writeByteS((objectType << 2) + (face & 3));
						}
						player.flushOutStream();
				}
		}

		public void showOption(int i, int l, String s, int a)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						if (!player.optionType.equalsIgnoreCase(s))
						{
								player.optionType = s;
								player.getOutStream().createFrameVarSize(104);
								player.getOutStream().writeByteC(i);
								player.getOutStream().writeByteA(l);
								player.getOutStream().writeString(s);
								player.getOutStream().endFrameVarSize();
								player.flushOutStream();
						}
				}
		}

		/**
		 * Private Messaging
		 **/
		public void logIntoPM()
		{
				setPrivateMessaging(2);
				for (int i1 = 0; i1 < ServerConstants.MAXIMUM_PLAYERS; i1++)
				{
						Player p = PlayerHandler.players[i1];
						if (p != null && p.isActive())
						{
								Player o = p;
								if (o != null)
								{
										o.getPA().updatePM(player.getPlayerId(), 1, true);
								}
						}
				}
				boolean pmLoaded = false;
				for (int i = 0; i < player.friends.length; i++)
				{
						if (player.friends[i] != 0)
						{
								for (int i2 = 1; i2 < ServerConstants.MAXIMUM_PLAYERS; i2++)
								{
										Player p = PlayerHandler.players[i2];
										if (p != null && p.isActive() && Misc.playerNameToInt64(p.getPlayerName()) == player.friends[i])
										{
												Player o = p;
												if (o != null)
												{
														if (player.playerRights >= 2 || p.privateChat == 0 || (p.privateChat == 1 && o.getPA().isInPM(Misc.playerNameToInt64(player.getPlayerName()))))
														{
																loadPM(player.friends[i], 1);
																pmLoaded = true;
														}
														break;
												}
										}
								}
								if (!pmLoaded)
								{
										loadPM(player.friends[i], 0);
								}
								pmLoaded = false;
						}
						for (int i1 = 1; i1 < ServerConstants.MAXIMUM_PLAYERS; i1++)
						{
								Player p = PlayerHandler.players[i1];
								if (p != null && p.isActive())
								{
										Player o = p;
										if (o != null)
										{
												o.getPA().updatePM(player.getPlayerId(), 1, true);
										}
								}
						}
				}
		}

		public void updatePM(int pID, int world, boolean logUpdate)
		{ // used for private chat updates
				Player p = PlayerHandler.players[pID];
				if (p == null || p.getPlayerName() == null || p.getPlayerName().equals("null"))
				{
						return;
				}
				Player o = p;
				long l = Misc.playerNameToInt64(PlayerHandler.players[pID].getPlayerName());
				if (p.privateChat == 0)
				{
						for (int i = 0; i < player.friends.length; i++)
						{
								if (player.friends[i] != 0)
								{
										if (l == player.friends[i])
										{
												if (logUpdate)
														loadPM(l, world);
												return;
										}
								}
						}
				}
				else if (p.privateChat == 1)
				{
						for (int i = 0; i < player.friends.length; i++)
						{
								if (player.friends[i] != 0)
								{
										if (l == player.friends[i])
										{
												if (o.getPA().isInPM(Misc.playerNameToInt64(player.getPlayerName())))
												{
														loadPM(l, world);
														return;
												}
												else
												{
														loadPM(l, 0);
														return;
												}
										}
								}
						}
				}
				else if (p.privateChat == 2)
				{
						for (int i = 0; i < player.friends.length; i++)
						{
								if (player.friends[i] != 0)
								{
										if (l == player.friends[i] && player.playerRights < 2)
										{
												loadPM(l, 0);
												return;
										}
								}
						}
				}
		}

		public boolean isInPM(long l)
		{
				for (int i = 0; i < player.friends.length; i++)
				{
						if (player.friends[i] != 0)
						{
								if (l == player.friends[i])
								{
										return true;
								}
						}
				}
				return false;
		}

		public void resetFollowers()
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								if (PlayerHandler.players[j].getPlayerIdToFollow() == player.getPlayerId())
								{
										Follow.resetFollow(player);
								}
						}
				}
		}

		public void processTeleport()
		{
				player.teleportToX = player.teleX;
				player.teleportToY = player.teleY;
				player.setHeight(player.teleHeight);

				if (!Area.inWilderness(player.teleportToX, player.teleportToY))
				{
						TargetSystem.leftWild(player);
						if (Area.inWilderness(player))
						{
								RagBan.removeFromWilderness(player.addressIp);
						}
				}
				if (player.getHeight() != 20)
				{
						Tournament.removeFromTournamentLobby(player.getPlayerId());
				}
				if (player.teleEndAnimation > 0)
				{
						player.startAnimation(player.teleEndAnimation);
				}
				if (player.teleEndGfx > 0)
				{
						player.gfx0(player.teleEndGfx);
				}
				player.teleEndAnimation = 0;
				player.teleEndGfx = 0;
				AreaInterface.updateWalkableInterfaces(player);
		}

		public void movePlayer(int x, int y, int h)
		{

				if (player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("Previous location: " + player.getX() + ", " + player.getY());
				}
				Movement.resetWalkingQueue(player);
				if (h != player.getHeight())
				{
						// Change region packet is not called when teleporting to the same height in a closer area or different height and close area.
						Server.objectManager.changeRegionPacketClientObjectUpdate(player, true);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										container.stop();
								}

								@Override
								public void stop()
								{
										if ((System.currentTimeMillis() - player.timeReloadedItems) <= 1300)
										{
												return;
										}
										Server.itemHandler.reloadItems(player);
								}
						}, 2);
				}
				player.teleportToX = x;
				player.teleportToY = y;
				player.setHeight(h);
				if (h != 20)
				{
						Tournament.removeFromTournamentLobby(player.getPlayerId());
				}
				requestUpdates();
				AreaInterface.updateWalkableInterfaces(player);
				Barrows.resetCoffinStatus(player);
		}

		/**
		 * reseting animation
		 **/
		public void resetAnimation()
		{
				Combat.updatePlayerStance(player);
				player.startAnimation(player.playerStandIndex);
				requestUpdates();
		}

		/**
		 * Update player appearance.
		 */
		public void requestUpdates()
		{
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}

		/**
		 * Show an arrow icon on the selected player.
		 *
		 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
		 * @Param j - The player/Npc that the arrow will be displayed above.
		 * @Param k - Keep this set as 0
		 * @Param l - Keep this set as 0
		 */

		public void drawHeadicon(int i, int j, int k, int l)
		{
				player.getOutStream().createFrame(254);
				player.getOutStream().writeByte(i);
				if (i == 1 || i == 10)
				{
						player.getOutStream().writeWord(j);
						player.getOutStream().writeWord(k);
						player.getOutStream().writeByte(l);
				}
				else
				{
						player.getOutStream().writeWord(k);
						player.getOutStream().writeWord(l);
						player.getOutStream().writeByte(j);
				}
		}

		public void flashSelectedSidebar(int i1)
		{
				player.getOutStream().createFrame(24);
				player.getOutStream().writeByteA(i1);
		}

		/**
		 * Select the tab.
		 *
		 * @param tab
		 *        The tab identity.
		 */
		public void changeToSidebar(int tab)
		{
				player.getOutStream().createFrame(106);
				player.getOutStream().writeByteC(tab);
		}

		public boolean confirmMessage;

		public void getSpeared(int otherX, int otherY)
		{
				int x = player.getX() - otherX;
				int y = player.getY() - otherY;
				//player.forceNoClip = true;
				if (x > 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST"))
				{
						//player.setForceMovement(player.playerWalkIndex, 1, 0, 0, 40, 1, 1);
						Movement.travelTo(player, 1, 0);
				}
				else if (x < 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST"))
				{
						//player.setForceMovement(player.playerWalkIndex, -1, 0, 0, 40, 3, 1);
						Movement.travelTo(player, -1, 0);
				}
				else if (y > 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH"))
				{
						//player.setForceMovement(player.playerWalkIndex, 0, 1, 0, 40, 0, 1);
						Movement.travelTo(player, 0, 1);
				}
				else if (y < 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH"))
				{
						//player.setForceMovement(player.playerWalkIndex, 0, -1, 0, 40, 2, 1);
						Movement.travelTo(player, 0, -1);
				}
				player.dragonSpearTicksLeft += 5;
				dragonSpearEvent(player);
		}

		private void dragonSpearEvent(Player player2)
		{
				if (player.dragonSpearEvent)
				{
						return;
				}
				player.dragonSpearEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.dragonSpearTicksLeft == 0)
								{
										container.stop();
										return;
								}
								player.dragonSpearTicksLeft--;
						}

						@Override
						public void stop()
						{
								player.dragonSpearEvent = false;
								for (int index = 0; index < player.dragonSpearEffectStack.size(); index++)
								{
										String string = player.dragonSpearEffectStack.get(index);
										String parse[];
										if (string.startsWith("Damage:"))
										{
												string = string.replace("Damage:", "");
												parse = string.split(" ");
												int damage = Integer.parseInt(parse[0]);
												int hitSplatColour = Integer.parseInt(parse[1]);
												int icon = Integer.parseInt(parse[2]);
												boolean maxHit = parse[3].equals("trues");
												player.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
												player.dealDamage(damage);
												Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
										}
										else if (string.startsWith("Prayer:"))
										{
												string = string.replace("Prayer:", "");
												int damage = Integer.parseInt(string);
												player.currentCombatSkillLevel[ServerConstants.PRAYER] -= damage / 4;
												if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) <= 0)
												{
														player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
														Combat.resetPrayers(player);
												}
												Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
										}
								}
								player.dragonSpearEffectStack.clear();
						}
				}, 1);

		}


		/**
		* Reset time attacked by another player, time attacked another player, time engaged npc.
		*/
		public void resetCombatTimer()
		{
				player.setTimeAttackedAnotherPlayer(0);
				player.setTimeUnderAttackByAnotherPlayer(0);
				player.setTimeNpcAttackedPlayer(0);
				player.timeNpcAttackedPlayerLogOutTimer = 0;
		}

		/**
		 * Check if player is within distance of the player.
		 * @param target
		 *        The player to check the distance with.
		 * @param distance
		 *        The maximum distance for this method to return true.
		 * 
		 * @return True, if the player is within the distance of the target.
		 */
		public boolean withinDistanceOfTargetPlayer(Player target, int distance)
		{
				for (int i = 0; i <= distance; i++)
				{
						for (int j = 0; j <= distance; j++)
						{
								if ((target.getX() + i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
								else if ((target.getX() - i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
								else if (target.getX() == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
						}
				}
				return false;
		}

		public boolean withInDistance(int objectX, int objectY, int playerX, int playerY, int distance)
		{
				for (int i = 0; i <= distance; i++)
				{
						for (int j = 0; j <= distance; j++)
						{
								if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
								else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
								else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
						}
				}
				return false;
		}

		/**
		 * Check if player is within distance of the npc.
		 * @param target
		 *        The player to check the distance with.
		 * @param distance
		 *        The maximum distance for this method to return true.
		 * 
		 * @return True, if the player is within the distance of the target.
		 */
		public boolean withinDistanceOfTargetNpc(Npc target, int distance)
		{
				for (int i = 0; i <= distance; i++)
				{
						for (int j = 0; j <= distance; j++)
						{
								if ((target.getX() + i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
								else if ((target.getX() - i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
								else if (target.getX() == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY()))
								{
										return true;
								}
						}
				}
				return false;
		}

		public boolean withinDistance(Npc npc)
		{
				if (npc == null || npc.needRespawn || player.getHeight() != npc.getHeight())
				{
						return false;
				}
				int deltaX = npc.getX() - player.getX(), deltaY = npc.getY() - player.getY();
				return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
		}

		public int distanceToPoint(int pointX, int pointY)
		{
				return (int) Math.sqrt(Math.pow(player.getX() - pointX, 2) + Math.pow(player.getY() - pointY, 2));
		}

		public int distanceToPoint(int firstX, int firstY, int pointX, int pointY)
		{
				return (int) Math.sqrt(Math.pow(firstX - pointX, 2) + Math.pow(firstY - pointY, 2));
		}


		public boolean withinDistance(Player otherPlr)
		{
				if (player.getHeight() != otherPlr.getHeight())
				{
						return false;
				}
				int deltaX = otherPlr.getX() - player.getX(), deltaY = otherPlr.getY() - player.getY();
				return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
		}

		public void stopAllActions()
		{
				player.flowerX = 0;
				player.flowerY = 0;
				player.flowerHeight = 0;
				player.flower = 0;
				player.itemOnNpcEvent = false;
				player.itemDestroyedId = -1;
				player.itemDestroyedSlot = -1;
				player.resetActionIdUsed();
				player.canUseTeleportInterface = false;
				player.yewLog = false;
				player.rawBeef = false;
				Skilling.stopAllSkilling(player);
				AgilityAssistant.stopResting(player);
				player.walkingToItem = false;
				//Do not add player.setMeleeFollow(false); or if you spam click a player with melee, it would cancel the melee follow
				// Remember the 4 min 3 seconds vid.
				player.setSpellId(-1);
				player.itemOnObjectEvent = false;
		}

		/**
		 * Change the interface of a certain tab.
		 * @param menuId
		 *        The tab identity.
		 * @param form
		 *        The interface to spawn in the tab.
		 */
		public void setSidebarInterface(int menuId, int form)
		{
				if (player.isBot)
				{
						return;
				}
				if (menuId == 0 && player.autoCasting)
				{
						// form = 328;
				}
				if (player.getOutStream() != null)
				{
						player.getOutStream().createFrame(71);
						player.getOutStream().writeWord(form);
						player.getOutStream().writeByteA(menuId);
				}
		}

		/**
		 * Announce a message to all players.
		 *
		 * @param message
		 *        The message to announce to all players.
		 */
		public void announce(String message)
		{
				String text = message;
				boolean extend = false;
				if (message.contains("NEWLINE"))
				{
						String[] split = message.split("-NEWLINE-");
						message = split[0];
						extend = true;
						text = split[1];
				}
				if (extend)
				{
						for (int j = 0; j < PlayerHandler.players.length; j++)
						{
								if (PlayerHandler.players[j] != null)
								{
										Player c3 = PlayerHandler.players[j];
										c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + message);
										c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + text);
								}
						}
				}
				else
				{
						for (int j = 0; j < PlayerHandler.players.length; j++)
						{
								if (PlayerHandler.players[j] != null)
								{
										Player c3 = PlayerHandler.players[j];
										c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + message);
								}
						}
				}
		}

		/**
		 * Send a message to the chatbox.
		 * @param message
		 *        The message to send.
		 */
		public void sendMessage(String message)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.doNotSendMessage)
				{
						player.doNotSendMessage = false;
						return;
				}
				if (player.getOutStream() != null)
				{
						player.getOutStream().createFrameVarSize(253);
						player.getOutStream().writeString(message);
						player.getOutStream().endFrameVarSize();
				}
		}

		public void sendFilterableMessage(String message)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.doNotSendMessage)
				{
						player.doNotSendMessage = false;
						return;
				}
				if (player.messageFiltered)
				{
						return;
				}
				if (player.getOutStream() != null)
				{
						player.getOutStream().createFrameVarSize(253);
						player.getOutStream().writeString(message);
						player.getOutStream().endFrameVarSize();
				}
		}

		public boolean isOnTopOfTarget(Player target)
		{
				if (player.getX() == target.getX() && player.getY() == target.getY())
				{
						return true;
				}
				return false;
		}

		public boolean isDiagonalFromTarget(Player target)
		{
				if (player.getX() != target.getX() && player.getY() != target.getY())
				{
						return true;
				}
				return false;
		}

		public void calculateCombatLevel()
		{
				int magic = (int) (player.baseSkillLevel[ServerConstants.MAGIC] * 1.5);
				int ranged = (int) (player.baseSkillLevel[ServerConstants.RANGED] * 1.5);
				int attstr = (int) ((double) (player.baseSkillLevel[ServerConstants.ATTACK]) + (double) (player.baseSkillLevel[ServerConstants.STRENGTH]));
				player.setCombatLevel(3);
				double defence = (player.baseSkillLevel[ServerConstants.DEFENCE] * 0.25);
				double hp = (player.baseSkillLevel[ServerConstants.HITPOINTS] * 0.25);
				double prayer = (player.baseSkillLevel[ServerConstants.PRAYER] * 0.125);
				if (ranged > attstr && magic < ranged)
				{
						player.setCombatLevel((int) (defence + hp + prayer + ((double) player.baseSkillLevel[ServerConstants.RANGED] * 0.4875)));
				}
				else if (magic > attstr)
				{
						player.setCombatLevel((int) (defence + hp + prayer + ((double) player.baseSkillLevel[ServerConstants.MAGIC] * 0.4875)));
				}
				else
				{
						player.setCombatLevel((int) (defence + hp + prayer + ((double) player.baseSkillLevel[ServerConstants.ATTACK] * 0.325) + ((double) player.baseSkillLevel[ServerConstants.STRENGTH] * 0.325)));
				}
		}

		/**
		 * Change colour of the text of an interface id.
		 * @param colour
		 * 		Hex colour code.
		 */
		public void changeTextColour(int id, int colour)
		{
				if (colour == -1)
				{
						return;
				}
				if (alreadyHasColourInFrame(colour, id))
				{
						return;
				}
				player.getPA().sendMessage(":packet:textcolour " + id + " " + colour);
		}

		public void sendScreenshot(final String screenshotName, int tickDelay)
		{
				if (tickDelay == 0)
				{
						String screenshotName1 = screenshotName.toLowerCase();
						screenshotName1 = screenshotName1.replaceAll(" ", "_");
						player.getPA().sendMessage(":packet:screenshot: " + screenshotName1);
						return;
				}
				// Has to be on an event or when i get a rare drop, it won't show the drop for some reason.
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								String screenshotName1 = screenshotName.toLowerCase();
								screenshotName1 = screenshotName1.replaceAll(" ", "_");
								player.getPA().sendMessage(":packet:screenshot: " + screenshotName1);
						}
				}, tickDelay);
		}

		/**
		 * True if the player can walk under the npc.
		 * Use for interacting with npcs that are used to talk to, fishing spots etc..
		 */
		public boolean canMoveToNpc(Npc npc)
		{
				switch (npc.npcType)
				{
						// Fishing spots.
						case 334:
						case 316:
						case 324:
						case 326:
						case 325:
								return true;
				}
				return Region.isStraightPathUnblocked(player.getX(), player.getY(), npc.getVisualX(), npc.getVisualY(), player.getHeight(), 1, 1);
		}


		public void quickChat(String string)
		{
				player.diceResultSaved = "<img=8> " + string;
				player.playerAssistant.sendMessage(":quickchat " + player.diceResultSaved);

		}
}