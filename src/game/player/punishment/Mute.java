package game.player.punishment;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;

/**
 * Mute & Un-mute feature.
 * @author MGT Madness, created on 01-02-2016.
 */
public class Mute
{

		/**
		 * Mute a player.
		 * @param player
		 * 			The associated player using the command.
		 * @param playerCommand
		 * 			The command.
		 */
		public static void muteCommand(Player player, String playerCommand)
		{
				try
				{
						String[] split = playerCommand.split(" ");
						int hours = Integer.parseInt(split[1]);
						int nameIndex = split[1].length() + 6;
						if (hours <= 0)
						{
								player.playerAssistant.sendMessage("Invalid hours amount.");
								return;
						}
						String name = playerCommand.substring(nameIndex);
						if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
						{
								player.playerAssistant.sendMessage("Account does not exist.");
								return;
						}

						boolean online = false;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
										{
												PlayerHandler.players[i].timeUnMuted = System.currentTimeMillis() + (hours * 3600000);
												online = true;
												break;
										}
								}
						}

						if (!online)
						{
								try
								{
										BufferedReader file = new BufferedReader(new FileReader(ServerConstants.CHARACTER_LOCATION + name + ".txt"));
										String line;
										String input = "";
										while ((line = file.readLine()) != null)
										{
												if (line.contains("timeUnMuted ="))
												{
														line = "";
														long value = System.currentTimeMillis() + (hours * 3600000);
														line = "timeUnMuted = " + value;
												}
												input += line + '\n';
										}
										FileOutputStream File = new FileOutputStream(ServerConstants.CHARACTER_LOCATION + name + ".txt");
										File.write(input.getBytes());
										file.close();
										File.close();
								}
								catch (Exception e)
								{
								}
						}
						player.playerAssistant.sendMessage("Muted " + name + " for " + hours + " hours, online: " + online);
				}
				catch (NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException a)
				{
						player.playerAssistant.sendMessage("Invalid. Correct use is ::mute 24 mgt madness");
				}
		}

		/**
		 * Un-mute the given player.
		 */
		public static void unMuteCommand(Player player, String playerCommand)
		{
				try
				{
						String name = playerCommand.substring(7);
						if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + name + ".txt"))
						{
								player.playerAssistant.sendMessage("Account does not exist.");
								return;
						}

						boolean online = false;
						for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
						{
								if (PlayerHandler.players[i] != null)
								{
										if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name))
										{
												PlayerHandler.players[i].timeUnMuted = System.currentTimeMillis();
												online = true;
												break;
										}
								}
						}

						if (!online)
						{
								try
								{
										BufferedReader file = new BufferedReader(new FileReader(ServerConstants.CHARACTER_LOCATION + name + ".txt"));
										String line;
										String input = "";
										while ((line = file.readLine()) != null)
										{
												if (line.contains("timeUnMuted ="))
												{
														line = "";
														long value = System.currentTimeMillis();
														line = "timeUnMuted = " + value;
												}
												input += line + '\n';
										}
										FileOutputStream File = new FileOutputStream(ServerConstants.CHARACTER_LOCATION + name + ".txt");
										File.write(input.getBytes());
										file.close();
										File.close();
								}
								catch (Exception e)
								{
								}
						}
						player.playerAssistant.sendMessage("Un-muted " + name + ", online: " + online);
				}
				catch (NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException a)
				{
				}
		}

		/**
		 * Calculate the time untill un-mute and notify player.
		 */
		public static void calculateTimeTillUnmute(Player player)
		{
				long totalSeconds = 0;
				double decimalHours = 0;
				int totalMinutes = 0;
				int integerHours = 0;
				double minutesExtra = 0;
				double lastOne = 0;
				totalSeconds = (player.timeUnMuted - System.currentTimeMillis()) / 1000;
				totalMinutes = (int) totalSeconds / 60;
				decimalHours = totalMinutes / 60.0;
				if (decimalHours < 1)
				{
						player.playerAssistant.sendMessage("Message not sent, unmuted in " + totalMinutes + " minutes.");
						return;
				}
				integerHours = (int) decimalHours;
				lastOne = (decimalHours - integerHours) * 100;
				minutesExtra = (60.0 / 100.0) * lastOne;
				player.playerAssistant.sendMessage("Message not sent, unmuted in " + integerHours + " hours and " + (int) minutesExtra + " minutes.");
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player is Account muted or Player muted.
		 */
		public static boolean isMuted(Player player)
		{
				if (player.ipMuted)
				{
						player.getPA().sendMessage("You are permanently muted.");
						return true;
				}
				if (player.timeUnMuted - System.currentTimeMillis() > 0)
				{
						calculateTimeTillUnmute(player);
						return true;
				}
				return false;
		}
}
