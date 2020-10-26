package core;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import core.maintick.Task;
import core.maintick.TaskScheduler;
import game.bot.BotManager;
import game.content.achievement.AchievementDefinitions;
import game.content.clanchat.ClanChatHandler;
import game.content.clanchat.ClanChatStartUp;
import game.content.donator.DonationsNeeded;
import game.content.highscores.Highscores;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.minigame.zombie.ZombieWaveInstance;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.TeleportInterface;
import game.content.miscellaneous.WelcomeMessage;
import game.content.shop.ShopHandler;
import game.content.skilling.Firemaking;
import game.content.skilling.Fishing;
import game.content.title.TitleDefinitions;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.item.BloodMoneyPrice;
import game.item.GlobalItemSpawn;
import game.item.ItemDefinition;
import game.item.ItemHandler;
import game.log.CoinEconomyTracker;
import game.log.GameTickLog;
import game.log.NewPlayerIpTracker;
import game.npc.NpcDrops;
import game.npc.NpcHandler;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.object.custom.ObjectManagerServer;
import game.player.PlayerHandler;
import game.player.event.CycleEventHandler;
import game.player.punishment.Ban;
import game.player.punishment.Blacklist;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import network.connection.ConnectionHandler;
import network.connection.ConnectionThrottleFilter;
import network.connection.InvalidAttempt;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import tools.CharacterBackup;
import tools.ErrorManager;
import utility.FileUtility;
import utility.LoggerDate;
import utility.Misc;
import utility.SimpleTimer;

/**
 * Main launch of the Server.
 *
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30
 * @author Sponjebubu
 * @author MGT Madness
 */
public class Server
{

		private static IoAcceptor acceptor;

		private static ConnectionHandler connectionHandler;

		private static ConnectionThrottleFilter throttleFilter;

		public static ItemHandler itemHandler = new ItemHandler();

		public static PlayerHandler playerHandler = new PlayerHandler();

		public static NpcHandler npcHandler = new NpcHandler();

		public static ShopHandler shopHandler = new ShopHandler();

		public static ObjectManagerServer objectManager = new ObjectManagerServer();

		public static NpcDrops npcDrops = new NpcDrops();

		public static ClanChatHandler clanChat = new ClanChatHandler();

		public static final TaskScheduler scheduler = new TaskScheduler();


		/**
		 * True, if a server update countdown timer is active.
		 */
		public static boolean UpdateServer;

		/**
		 * How long the server has been online for.
		 */
		public static long timeServerOnline;

		/**
		 * Launch the server.
		 */
		public static void main(java.lang.String args[]) throws NullPointerException, IOException
		{
				new SimpleTimer();
				System.setOut(new LoggerDate(System.out));
				System.setErr(new LoggerDate(System.err));
				ErrorManager.loadErrorFile();
				shutDownButtons();
				long start = System.currentTimeMillis();
				timeServerOnline = start;
				Misc.print("Loading latest version of ShatterScape");
				loadSystems();
				initiateConnections();
				gameTick();

				long timeTaken = (System.currentTimeMillis() - start);
				timeTaken /= 100;
				String time = Long.toString(timeTaken);
				if (time.length() == 2)
				{
						time = time.substring(0, 1) + "." + time.substring(1);
				}
				else if (time.length() == 3)
				{
						time = time.substring(0, 2) + "." + time.substring(2);
				}
				Misc.print("ShatterScape has finished loading in: " + time + " seconds");




		}

		/**
		 * Load all systems.
		 */
		public static void loadSystems() throws IOException
		{
				if (!ServerConfiguration.DEBUG_MODE)
				{
						FileUtility.addLineOnTxt("backup/logs/system log/save log.txt", Misc.getDate() + " Booted.");
				}
				ObjectDefinitionServer.loadConfig();
				Region.load();
				HighscoresDaily.getInstance().readDailyHighscoresType();
				CoinEconomyTracker.readCoinEconomyLog();
				Fishing.fillCurrentFishingSpots();
				Highscores.initiateHighscoresInstance();
				Blacklist.loadStartUpBlacklistedData();
				GlobalItemSpawn.initialize();
				WelcomeMessage.loadWelcomeMessage();
				ItemDefinition.loadItemDefinitions();
				BloodMoneyPrice.loadBloodMoneyPrice();
				NpcHandler.loadNpcData();
				ShopHandler.loadShops();
				PacketHandler.defaultFlaggedPlayers();
				AchievementDefinitions.loadAllAchievements();
				TitleDefinitions.loadAllTitles();
				BotManager.logInBots();
				ClanChatStartUp.loadClanChatStartUp();
				TeleportInterface.teleportStartUp();
				HighscoresHallOfFame.loadHallOfFameData();
				NewPlayerIpTracker.loadNewPlayerIpTrackerFiles();
				ZombieWaveInstance.loadZombieContent();
				DonationsNeeded.loadFile();
				CommunityEvent.loadFile();
				GuideBook.loadGuideDataFile();
				Firemaking.defaultFiremakingSpots();
				BloodKey.bloodKeyInitialize();
				Tournament.tournamentStartUp();
				IpMute.readIpMuteLog();
				Ban.readBanLog();
				VoteManager.readVoteBans();
				RagBan.readBanLog();
				InvalidAttempt.loadAutoBlacklist();
				//Web.loadBlockedWebsClipping();
				Server.objectManager.loadCustomObjects();
				/*
				try
				{
						Misc.print(AESencrp.decrypt("6EyRfyJMcM4XYDNSPXKgEg=="));
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
				*/
		}

		public static int index = 0;

		private static void shutDownButtons()
		{
				if (ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				JFrame frame = new JFrame("Control panel");
				frame.setResizable(false);
				frame.setSize(280, 77);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel panel = new JPanel();

				JButton button1 = new JButton("Save & Close");
				panel.add(button1);
				button1.addActionListener(new Action2());

				JButton button3 = new JButton("Save trade logs");
				panel.add(button3);
				button3.addActionListener(new Action4());

				frame.add(panel);
				frame.setVisible(true);

		}

		static class Action2 implements ActionListener
		{
				@Override
				public void actionPerformed(ActionEvent e)
				{
						playerHandler.serverRestartContentUpdate(false, true);
						Misc.print("Players online: " + PlayerHandler.getPlayerCount());
						Misc.print("JFrame used: Saved & Closed.");
				}
		}

		static class Action4 implements ActionListener
		{
				@Override
				public void actionPerformed(ActionEvent e)
				{
						ItemTransferLog.saveTransferItemLog();
						Misc.print("JFrame used: Save trade logs only.");
				}
		}

		/**
		 * Initiate all the connections.
		 */
		private static void initiateConnections()
		{
				acceptor = new SocketAcceptor();
				connectionHandler = new ConnectionHandler();
				SocketAcceptorConfig sac = new SocketAcceptorConfig();
				sac.getSessionConfig().setTcpNoDelay(false);
				sac.setReuseAddress(true);
				sac.setBacklog(100);
				throttleFilter = new ConnectionThrottleFilter(ServerConstants.CONNECTION_DELAY);
				sac.getFilterChain().addFirst("throttleFilter", throttleFilter);
				try
				{
						acceptor.bind(new InetSocketAddress(43594), connectionHandler, sac);
				}
				catch (IOException e)
				{
						e.printStackTrace();
				}
		}

		/**
		 * The main game tick.
		 */
		public static void gameTick()
		{
				scheduler.schedule(new Task()
				{
						@Override
						protected void execute()
						{
								GameTickLog.loopDebugPart1();
								npcHandler.clearNpcFlags();
								playerHandler.packetProcessing();
								CycleEventHandler.getSingleton().cycleEventGameTick();
								itemHandler.itemGameTick();
								npcHandler.npcGameTick();
								playerHandler.playerGameTick();
								objectManager.objectGameTick();
								Announcement.announcementGameTick();
								WorldEvent.worldEventTick();
								CharacterBackup.backUpSystem();
								GameTickLog.loopDebugPart2();
						}
				});
		}

		public static void restart()
		{
				File execute = new File("Server.bat");
				try
				{
						Desktop.getDesktop().open(execute);
				}
				catch (IOException e)
				{
						e.printStackTrace();
				}
		}

}