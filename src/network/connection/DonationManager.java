/*Change the package if this isn't yours!*/
package network.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import core.ServerConstants;
import game.content.donator.DonationsNeeded;
import game.item.ItemAssistant;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;


public class DonationManager implements Runnable
{

		/* If you have a client.java change this!*/
		private Player player = null;

		private boolean isConnected = false;

		private Connection con;

		/*Edit these variables to your database details*/
		private String host = "192.254.184.132";

		private String user = "mgtmadne_don";

		private String password = "Lxm*Mbsa^BX2";

		private String db = "mgtmadne_donate";

		public DonationManager(Player p)
		{
				this.player = p;
		}

		private final static int[] donatingAmount = {5, 10, 20, 35, 50, 100};

		private final static int[] TOKENS_AMOUNT = {50, 100, 210, 385, 575, 1200};

		@Override
		public void run()
		{
				if (player == null)
				{
						return;
				}
				player.getPA().sendMessage("Retrieving your donation...");
				if (!this.isConnected())
				{
						connect();
				}

				/*Change this variable to the users username!*/
				String username = player.getPlayerName();
				String query = "SELECT * FROM `donation` WHERE `username` = ?";
				//String query = "SELECT * FROM `donation` WHERE `username` = ? AND status=`Completed`";
				//String query = "SELECT * FROM `donation` WHERE status=`Completed` AND `username` = ?";
				try
				{
						ResultSet rs = query(query, new char[] {'s'}, new Object[] {username});
						if (rs.next())
						{
								int pid = rs.getInt("productid");
								int id = rs.getInt("id");
								String state = rs.getString("status");
								if (!state.equals("Completed"))
								{
										player.getPA().sendMessage("Your donation status is: " + state + ".");
										player.getPA().sendMessage("It must be 'Completed' before you receive your reward.");
										FileUtility.addLineOnTxt("backup/logs/donations/donation history.txt", "Pending " + donatingAmount[pid - 1] + "$ donation from: " + player.getPlayerName() + ", " + Misc.getDate() + ", pid: " + pid + ", id: " + id);
										return;
								}
								Misc.printDontSave("Donation from " + player.getPlayerName() + " of " + donatingAmount[pid - 1] + "$");
								ItemAssistant.addItemToInventoryOrDrop(player, 7478, TOKENS_AMOUNT[pid - 1]);
								player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have donated for " + TOKENS_AMOUNT[pid - 1] + " Donator Tokens!");
								DonationsNeeded.currentDonationAmount += donatingAmount[pid - 1];
								FileUtility.addLineOnTxt("backup/logs/donations/donation history.txt", donatingAmount[pid - 1] + "$, " + player.getPlayerName() + ", " + Misc.getDate() + ", pid: " + pid + ", id: " + id);
								query("DELETE FROM `donation` WHERE `id`= ?", new char[] {'s'}, new Object[] {id});
								player.getPA().sendMessage(ServerConstants.BLUE_COL + "Thank you so much for donating which will keep ShatterScape growing!");
								player.donatorTokensReceived += TOKENS_AMOUNT[pid - 1];
								return;
						}
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}

				player.getPA().sendMessage("No donation found, please try again in a minute.");
		}

		public ResultSet query(String query, char[] type, Object[] value) throws Exception
		{
				PreparedStatement ps = con.prepareStatement(query);
				if (type == null || value == null)
				{
						ps.executeQuery();
						return null;
				}

				for (int i = 0; i < type.length; i++)
				{
						if (type[i] == 's')
						{
								ps.setString((i + 1), value[i].toString());
						}
						else if (type[i] == 'i')
						{
								ps.setInt((i + 1), Integer.parseInt(value[i].toString()));
						}
						else if (type[i] == 'l')
						{
								ps.setLong((i + 1), Long.parseLong(value[i].toString()));
						}
						else if (type[i] == 'd')
						{
								ps.setDouble((i + 1), Double.parseDouble(value[i].toString()));
						}
				}

				if (query.toLowerCase().startsWith("select"))
				{
						ResultSet rs = ps.executeQuery();
						return rs;
				}
				ps.executeUpdate();
				return null;

		}

		public void connect()
		{
				try
				{
						Class.forName("com.mysql.jdbc.Driver").newInstance();
						con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db, user, password);
						isConnected = true;
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		private boolean isConnected()
		{
				return isConnected;
		}

}
