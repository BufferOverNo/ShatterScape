package game.content.donator;

import java.io.File;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Area;
import game.player.Player;
import game.player.PlayerSave;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Refill Hp, Prayer, Special and cure poison.
 * @author MGT Madness, created on 16-09-2016, in the 12 hour flight from Malaysia to Egypt.
 */
public class DonatorFeatures
{


		public static void nameChange(Player player, String newName)
		{
				if (!player.isDonator())
				{
						return;
				}
				if (!ItemAssistant.hasItemInInventory(player, 18760))
				{
						return;
				}
				if (newName.isEmpty())
				{
						return;
				}
				newName = newName.trim();
				if (!newName.matches("[A-Za-z0-9 ]+"))
				{
						player.getPA().sendMessage("Invalid characters.");
						return;
				}
				if (newName.length() > 12)
				{
						player.getPA().sendMessage("Name cannot be longer than 12 characters.");
						return;
				}
				String oldName = player.getPlayerName();
				if (!FileUtility.accountExists(ServerConstants.CHARACTER_LOCATION + newName + ".txt"))
				{
						File file = new File(ServerConstants.CHARACTER_LOCATION + oldName + ".txt");

						if (!file.delete())
						{
								player.getPA().sendMessage("Name change failed, try again.");
								return;
						}
						ItemAssistant.deleteItemFromInventory(player, 18760, 1);
						player.getPA().sendMessage("Your name has been changed successfully!");
						player.setPlayerName(Misc.capitalize(newName));
						PlayerSave.saveGame(player);
						player.getPA().requestUpdates();
				}
				else
				{
						player.getPA().sendMessage(newName + " already exists.");
				}
				Server.clanChat.updateClanChat(player.getClanId());
		}

		public static void afk(final Player player)
		{
				if (ServerConfiguration.DEBUG_MODE)
				{
						//return;
				}
				if ((player.isLegendaryDonator()) && !player.idleEventUsed && player.getTransformed() == 0 && !Combat.inCombat(player) && !player.resting && !Area.inPVPArea(player))
				{
						player.idleEventUsed = true;
						player.turnPlayerTo(player.getX(), player.getY() - 1);
						player.playerStandIndex = 3363;
						new Object(player, player.throneId, player.getX(), player.getY(), player.getHeight(), 2, 10, -1, -1);
						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
						{
								@Override
								public void execute(CycleEventContainer container)
								{
										if (player.idleEventUsed)
										{
												player.gfx0(277);
												player.getPA().requestUpdates();
										}
										else
										{
												container.stop();
										}
								}

								@Override
								public void stop()
								{
										resetAfk(player, false);
								}
						}, 4);
				}
		}

		public static void resetAfk(Player player, boolean ignore)
		{
				if (player.idleEventUsed)
				{
						if (!ignore)
						{
								player.startAnimation(65535);
								Combat.updatePlayerStance(player);
						}
						player.getPA().requestUpdates();
						player.idleEventUsed = false;
						for (int index = 0; index < player.toRemove.size(); index++)
						{
								if (player.toRemove.get(index).objectId == player.throneId)
								{
										Server.objectManager.toRemove.add(player.toRemove.get(index));
										break;
								}
						}
						player.toRemove.clear();
				}

		}

}
