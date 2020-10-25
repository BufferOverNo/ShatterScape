package game.content.commands;

import game.content.donator.DonatorFeatures;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.PlayerRank;
import game.content.miscellaneous.Teleport;
import game.player.Player;
import game.player.punishment.Mute;
import utility.Misc;

/**
 * Member commands.
 * @author MGT Madness, created on 12-12-2013.
 */
public class DonatorCommand
{

		/**
		 * Commands eligible for Members only.
		 * @param player
		 * 			The player using the command.
		 * @param playerCommand
		 * 			The command used.
		 *
		 */
		public static boolean donatorCommands(Player player, String playerCommand)
		{
				if (playerCommand.equals("don") || playerCommand.equals("dz") || playerCommand.equals("donatorzone") || playerCommand.equals("dzone"))
				{
						player.getPA().sendMessage("You have been teleported to the Donator zone.");
						Teleport.spellTeleport(player, 2192, 3251, 0, false);
						return true;
				}
				else if (playerCommand.startsWith("afk"))
				{
						DonatorFeatures.afk(player);
						return true;
				}
				return false;
		}

		/**
		* chat command.
		* @param player
		* 			The associated player.
		* @param playerCommand
		* 			The command used by the player.
		*/
		public static void yell(Player player, String playerCommand)
		{
				if (Mute.isMuted(player))
				{
						return;
				}
				try
				{
						String message = playerCommand.substring(5);

						/* Check for empty message. */
						if (message.isEmpty())
						{
								return;
						}

						if (message.toLowerCase().contains("<col=") || message.toLowerCase().contains("<img="))
						{
								return;
						}

						/* Convert message to lowercase and capitalize first letter. */
						String convertMessage = playerCommand.substring(5);
						convertMessage = convertMessage.toLowerCase();

						if (Misc.checkForOffensive(message))
						{
								player.playerAssistant.sendMessage("Do not use offensive language or you will be muted.");
								return;
						}
						if (player.isJailed())
						{
								player.getPA().sendMessage("Cannot yell while jailed.");
								return;
						}

						String name = player.getCapitalizedName();
						Announcement.announce("<col=0>[<col=255>Yell<col=0>] " + PlayerRank.getIconText("", player.playerRights, false) + name + ":<col=800000> " + Misc.optimize(message) + "", "");
				}
				catch (Exception e)
				{

				}
		}

}
