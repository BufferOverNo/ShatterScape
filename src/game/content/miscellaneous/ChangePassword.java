package game.content.miscellaneous;

import core.ServerConstants;
import game.player.Player;
import network.packet.PacketHandler;
import utility.Misc;

/**
 * Change password function.
 * @author MGT Madness, created on 22-02-2015.
 */
public class ChangePassword
{
		/**
		* Inform the client to make the change password interface appear.
		*/
		public static void sendChangePassword(Player player)
		{
				player.playerAssistant.sendMessage(":7uj8f5ghjdr5tl:");
		}

		/**
		 * Receive the new player's password from the client.
		 */
		public static void receiveChangePassword(Player player, String newPassword)
		{
				player.getPA().closeInterfaces();
				String newestPassword = newPassword.substring(13);
				if (newestPassword.trim().length() == 0)
				{
						player.getDH().sendStatement("Cancelled. your password is: @blu@" + player.playerPass);
						return;
				}
				if (!newestPassword.matches("[A-Za-z0-9 ]+"))
				{
						PacketHandler.stringAbuseLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.stringAbuseLog.add("Change password abuse:");
						if (newestPassword.contains("\r") || newestPassword.contains("\n"))
						{
								PacketHandler.stringAbuseLog.add("Contains backwards slash r or n");
						}
						else
						{
								PacketHandler.stringAbuseLog.add("Contains: " + newestPassword);
						}
						return;
				}
				if (newestPassword.length() > 0 && newestPassword.length() < 21)
				{
						HackLog.hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDate());
						HackLog.hackLog.add("Old pass: " + player.playerPass);
						HackLog.hackLog.add("New pass: " + newestPassword);
						HackLog.hackLog.add("------");
						player.playerPass = newestPassword;
						player.getDH().sendStatement("Your new password is: @blu@" + player.playerPass);
				}
				else
				{
						player.playerAssistant.sendMessage("Wrong entry. your password is: " + ServerConstants.BLUE_COL + player.playerPass);
						player.playerAssistant.sendMessage("Maximium of 20 characters allowed for your password.");
				}
		}
}
