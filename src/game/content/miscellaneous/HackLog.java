package game.content.miscellaneous;

import java.util.ArrayList;

import game.player.Player;
import utility.FileUtility;
import utility.Misc;

/**
 * Hack log.
 * @author MGT Madness, created on 15-04-2017
 */
public class HackLog
{
		/**
		 * Store data of new ip logging into an account.
		 */
		public static ArrayList<String> hackLog = new ArrayList<String>();


		public final static String FILE_LOCATION = "backup/logs/bruteforce/hacklog.txt";

		public static void saveHackLog()
		{
				FileUtility.saveArrayContents(FILE_LOCATION, hackLog);
				hackLog.clear();
		}

		public static void addNewHackEntry(Player player)
		{
				if (player.addressMac.equalsIgnoreCase(player.lastMacAddress) || player.addressUid.equalsIgnoreCase(player.lastUidAddress) || player.addressIp.equals(player.lastSavedIpAddress))
				{
						return;
				}
				hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDate());
				hackLog.add("New Ip: " + player.addressIp);
				hackLog.add("New Mac: " + player.addressMac);
				hackLog.add("New Uid: " + player.addressUid);
				hackLog.add("Last logged in (hours): " + PlayerGameTime.calculateHoursFromLastVisit(player));
				hackLog.add("Old Ip: " + player.lastSavedIpAddress);
				hackLog.add("Old Mac: " + player.lastMacAddress);
				hackLog.add("Old Uid: " + player.lastUidAddress);
				hackLog.add("------");
		}
}
