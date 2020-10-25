package network.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;

public class VoteManager implements Runnable
{

		// This has been already edited properly

		public static final String HOST = "192.254.184.132";

		public static final String USER = "mgtmadne_vote";

		public static final String PASS = "3spiral7hhaggs]";

		public static final String DATABASE = "mgtmadne_vote";

		public static void main(String[] args)
		{
				new Thread(new VoteManager(null)).start();
		}

		private Player player;

		private Connection conn;

		private Statement stmt;

		public VoteManager(Player player)
		{
				this.player = player;
		}

		public static ArrayList<String> voteUid = new ArrayList<String>();

		public static ArrayList<String> voteBan = new ArrayList<String>();

		@SuppressWarnings("unused")
		@Override
		public void run()
		{
				player.getPA().sendMessage("Retrieving your vote...");
				try
				{
						if (!connect(HOST, DATABASE, USER, PASS))
						{
								return;
						}
						boolean claimed = false;
						String name = player.getPlayerName().replace(" ", "_");
						ResultSet rs = executeQuery("SELECT * FROM fx_votes WHERE username='" + name + "' AND claimed=0 AND callback_date IS NOT NULL");
						while (rs.next())
						{
								String timestamp = rs.getTimestamp("callback_date").toString();
								String ipAddress = rs.getString("ip_address");
								int siteId = rs.getInt("site_id");

								int tickets = 5;
								if (player != null)
								{

										if (VoteManager.voteLimitReached(player))
										{
												return;
										}
										if (player.isLegendaryDonator())
										{
												tickets += 10;
										}
										else if (player.isExtremeDonator())
										{
												tickets += 6;
										}
										else if (player.isSuperDonator())
										{
												tickets += 4;
										}
										else if (player.isDonator())
										{
												tickets += 2;
										}

										rareReward(player);
										player.timeVoted = System.currentTimeMillis();
										player.votesClaimed++;

										// If player.votesClaimed is a multiple of 6.
										if (player.votesClaimed % 6 == 0)
										{
												voteUid.add(Misc.getDate() + "[" + player.addressUid + "] [" + player.getPlayerName() + "]");
										}
										player.voteAlerted = false;
										ItemAssistant.addItemToInventoryOrDrop(player, 4067, tickets);
										player.voteTotalPoints += tickets;
										player.getPA().sendMessage("Thank you for voting, you have been awarded " + tickets + " vote tickets!");
										player.getPA().sendMessage("Talk to the Void knight to spend your tickets.");
										if (!lastPlayerVoted.equals(player.getPlayerName()))
										{
												currentVotes++;
												lastPlayerVoted = player.getPlayerName();
										}
										if (currentVotes == 4)
										{
												Announcement.announce("4 players have voted, ::vote now for 3,000 bm and a chance for Ags/Claws!", "<img=10><col=0000ff>");
												currentVotes = 0;
										}
								}
								rs.updateInt("claimed", 1);
								rs.updateRow();
								claimed = true;
						}
						if (!claimed)
						{
								player.getPA().sendMessage("You have not voted, try again at ::vote");
						}
						destroy();
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		public static ArrayList<String> voteRareItems = new ArrayList<String>();

		public static int currentVotes;

		/**
		 * To avoid spam from Vpn voters.
		 */
		public static String lastPlayerVoted = "";


		public static final int LOOT_CHANCE = 600;

		public static void rareReward(Player player)
		{

				// If they vote 4 times a day, they will get a loot every 5 days.
				// The loot is most likely a barrows piece.
				if (Misc.hasOneOutOf(LOOT_CHANCE))
				{
						int itemId = Misc.hasPercentageChance(50) ? 11694 : 14484;

						if (!player.profilePrivacyOn)
						{
								Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(itemId) + " from Voting.");
						}
						voteRareItems.add(Misc.getDate() + ", " + player.getPlayerName() + ", " + ItemAssistant.getItemName(itemId));
						RareDropLog.appendRareDrop(player, "Voting: " + ItemAssistant.getItemName(itemId));
						player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
						ItemAssistant.addItemToInventoryOrDrop(player, itemId, 1);
				}
		}


		public boolean connect(String host, String database, String user, String pass)
		{
				try
				{
						this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, pass);
						return true;
				}
				catch (SQLException e)
				{
						Misc.print("Failing connecting to database!");
						return false;
				}
		}

		public void destroy()
		{
				try
				{
						conn.close();
						conn = null;
						if (stmt != null)
						{
								stmt.close();
								stmt = null;
						}
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		public int executeUpdate(String query)
		{
				try
				{
						this.stmt = this.conn.createStatement(1005, 1008);
						int results = stmt.executeUpdate(query);
						return results;
				}
				catch (SQLException ex)
				{
						ex.printStackTrace();
				}
				return -1;
		}

		public ResultSet executeQuery(String query)
		{
				try
				{
						this.stmt = this.conn.createStatement(1005, 1008);
						ResultSet results = stmt.executeQuery(query);
						return results;
				}
				catch (SQLException ex)
				{
						ex.printStackTrace();
				}
				return null;
		}

		public static void voteAlert(Player player)
		{
				// If it has been less than 12 hours since last time claimed vote, then return.
				if (System.currentTimeMillis() - player.timeVoted < 43200000)
				{
						return;
				}
				if (player.voteAlerted)
				{
						return;
				}

				player.getPA().sendMessage(ServerConstants.BLUE_COL + "You are eligible to vote.");
				player.voteAlerted = true;
		}

		public static boolean voteLimitReached(Player player)
		{
				if (player.votesClaimed >= 2 && System.currentTimeMillis() - player.timeVoted < 43200000)
				{
						long hoursLeft = 12 - (System.currentTimeMillis() - player.timeVoted) / 3600000;
						if (hoursLeft == 0)
						{
								long minutes = 60 - (System.currentTimeMillis() - player.timeVoted) / 60000;
								player.getPA().sendMessage("You have recently voted, you may vote again in " + minutes + " minute.");
						}
						else
						{
								String s = "";
								if (hoursLeft > 1)
								{
										s = "s";
								}
								player.getPA().sendMessage("You have recently voted, you may vote again in " + hoursLeft + " hour" + s + ".");
						}
						return true;
				}
				else if (player.votesClaimed >= 2)
				{
						player.votesClaimed = 0;
				}
				return false;

		}

		private final static String FILE_LOCATION = "./backup/logs/vote/bans.txt";

		public static void readVoteBans()
		{

				try
				{
						BufferedReader file = new BufferedReader(new FileReader(FILE_LOCATION));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										voteBan.add(line);
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public static void updateVoteBans()
		{
				FileUtility.deleteAllLines(FILE_LOCATION);

				try
				{
						BufferedWriter bw = null;
						bw = new BufferedWriter(new FileWriter(FILE_LOCATION, true));
						for (int i = 0; i < voteBan.size(); i++)
						{
								bw.write(voteBan.get(i));
								bw.newLine();
						}
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
		}

}
