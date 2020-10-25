package game.content.miscellaneous;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.minigame.RecipeForDisaster;
import game.content.minigame.barrows.Barrows;
import game.content.music.SoundSystem;
import game.content.skilling.agility.AgilityAssistant;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Spellbook teleports for the Modern spellbook and Ancient magicks spellbook.
 * @author MGT Madness, created on 29-11-2013.
 */

public class Teleport
{

		/**
		* Teleport a player and perform the correct emote and gfx depending on their spellbook type.
		*
		* @param x 
		* 			x-axis coordinate of the player.
		* @param y 
		* 			y-axis coordinate of the player.
		* @param height 
		* 			height level of the player.
		* @param teleporter
		* 			True, if the player is being teleported by the 'Teleporter' NPC.
		*/
		public static void spellTeleport(Player player, int x, int y, int height, boolean teleporter)
		{

				if (teleporter)
				{
						startTeleport(player, x, y, height, "TELEPORTER");
				}
				else
				{
						startTeleport(player, x, y, height, player.spellBook);
				}
		}

		/**
		 * Initiate the spellbook teleport.
		 */
		public static void startTeleport(final Player player, int x, int y, int height, final String teleportType)
		{
				if (player.getDuelStatus() == 5)
				{
						player.getPA().sendMessage("You cannot teleport in a duel!");
						return;
				}
				player.getTradeAndDuel().claimStakedItems();
				Tournament.logOutUpdate(player, false);
				player.clickObjectType = 0;
				player.setClickNpcType(0);
				player.resetNpcIdentityAttacking();
				player.resetPlayerIdAttacking();
				Player o = player.getTradeAndDuel().getPartner();
				if (o != null)
				{
						o.getTradeAndDuel().declineDuel(false);
				}
				player.getTradeAndDuel().declineDuel(true);
				player.requestDuoName = "";
				player.getTradeAndDuel().declineTrade1(true);
				player.resetFaceUpdate();
				Follow.resetFollow(player);
				GodWarsDungeonInterface.resetGwdData(player);
				player.playerAssistant.stopAllActions();
				player.getPA().closeInterfaces();
				if (!canTeleport(player, teleportType))
				{
						return;
				}
				if (teleportType.contains("GLORY"))
				{
						String[] split = teleportType.split(" ");
						int itemId = Integer.parseInt(split[1]);
						if (teleportType.contains("EQUIPMENT"))
						{
								ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, itemId - 2, 1, true, true);
						}
						else
						{
								ItemAssistant.deleteItemFromInventory(player, itemId, 1);
								ItemAssistant.addItemToInventoryOrDrop(player, itemId - 2, 1);
						}
				}
				player.teleportsUsed++;
				AgilityAssistant.stopResting(player);
				if (player.getPlayerIdAttacking() > 0 || player.getNpcIdAttacking() > 0)
				{
						Combat.resetPlayerAttack(player);
				}
				player.setTeleporting(true);
				Movement.stopMovement(player);
				player.teleX = x;
				player.teleY = y;
				player.resetNpcIdentityAttacking();
				player.resetPlayerIdAttacking();
				player.resetFaceUpdate();
				player.teleHeight = height;

				if (teleportType.equalsIgnoreCase("MODERN"))
				{
						player.startAnimation(8939);
						player.teleportCycle = 3;
						player.gfx0(1576);
						player.teleEndGfx = 1577;
						player.teleEndAnimation = 8941;
				}
				else if (teleportType.equalsIgnoreCase("ANCIENT"))
				{
						player.startAnimation(9599);
						player.teleportCycle = 5;
						player.teleEndAnimation = 65535;
						player.gfx0(1681);
				}
				else if (teleportType.equalsIgnoreCase("LUNAR"))
				{
						player.startAnimation(9606);
						player.teleportCycle = 6;
						player.teleEndAnimation = 65535;
						player.gfx0(1685);
				}
				else if (teleportType.equalsIgnoreCase("TAB"))
				{
						player.startAnimation(9597);
						player.teleportCycle = 4;
						player.gfx0(1680);
						ItemAssistant.deleteItemFromInventory(player, 8013, ItemAssistant.getItemSlot(player, 8013), 1);
						player.playerAssistant.sendMessage("You break the tablet.");
						EdgeAndWestsRule.teleTab(player);
				}
				else if (teleportType.contains("GLORY"))
				{
						player.startAnimation(9603);
						player.teleportCycle = 6;
						player.gfx0(1684);
				}
				else if (teleportType.contains("LEVER"))
				{
						if (!teleportType.contains("NO ANIMATION"))
						{
								player.startAnimation(2140);
						}
						player.teleportCycle = 5;
						player.teleEndGfx = 1577;
						player.teleEndAnimation = 8941;
				}

				// Echtopial teleport.
				else if (teleportType.equalsIgnoreCase("ZOMBIE"))
				{
						player.startAnimation(8939);
						player.teleportCycle = 2;
						player.gfx0(1587);
						player.teleEndAnimation = 8941;
				}
				else if (teleportType.equalsIgnoreCase("TELEPORTER"))
				{
						player.teleportCycle = 4;
						player.teleEndAnimation = 715;
						Server.npcHandler.teleporterInAction = true;
						SoundSystem.sendSound(player, 202, 600);
				}

				teleportEvent(player, teleportType);
		}

		/**
		 * The teleport cycle event.
		 */
		private static void teleportEvent(final Player player, final String teleportType)
		{
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								player.teleportCycle--;
								if (teleportType.equalsIgnoreCase("tab") && player.teleportCycle == 2)
								{
										player.startAnimation(4731);
								}
								else if (teleportType.contains("LEVER") && player.teleportCycle == 3)
								{
										player.startAnimation(8939);
										player.gfx0(1576);
								}
								else if (teleportType.equalsIgnoreCase("teleporter") && player.teleportCycle == 3)
								{
										player.startAnimation(714);
										player.gfx0(342);
								}

								if (player.teleportCycle == 0)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								if (player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE)
								{
										Misc.print("Previous location: " + player.getX() + ", " + player.getY());
								}
								player.getPA().processTeleport();
								if (teleportType.equalsIgnoreCase("tab") && player.teleportCycle == 0)
								{
										player.startAnimation(9598);
										player.gfx0(678);
										tabletReturn(player);
								}
								Barrows.resetCoffinStatus(player);
								if (player.teleportToX == 1900)
								{
										player.getPA().sendMessage("If you die, you will have to start the waves from scratch.");
										player.getPA().sendMessage("Your items are safe on death.");
										RecipeForDisaster.spawnNextWave(player, true, RecipeForDisaster.NPC_WAVE_LIST[0]);
								}
								else
								{
										player.rfdWave = 0;
								}

								if (player.teleportToX == 3659 && player.teleportToY == 3516)
								{
										player.getPA().sendMessage("Right click duo a player to start the zombie survival.");
								}
						}
				}, 1);
		}

		/**
		 * Reset player's animation so the player can move after doing the last animation of tablet.
		 * @param player
		 * 			The associated player.
		 */
		private static void tabletReturn(final Player player)
		{
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
								player.startAnimation(65535);
						}
				}, 2);
		}

		/**
		 * True, if the player is allowed to teleport.
		 * @param player
		 * 			The player.
		 * @param teleportType
		 * 			The type of teleport the player is using.
		 */
		private static boolean canTeleport(Player player, String teleportType)
		{
				if (player.isJailed())
				{
						player.playerAssistant.sendMessage("You can't teleport out of jail.");
						return false;
				}

				if (player.getDuelStatus() == 5)
				{
						player.playerAssistant.sendMessage("You can't teleport during a duel!");
						return false;
				}

				if (teleportType.equals("TAB") || teleportType.contains("GLORY"))
				{
						if (Area.inWilderness(player) && player.wildernessLevel > 30 && !player.isAdministratorRank() && !player.isInZombiesMinigame())
						{
								player.playerAssistant.sendMessage("You can't teleport above level 30 in the wilderness.");
								return false;
						}
				}
				else
				{
						if (Area.inWilderness(player) && player.wildernessLevel > 20 && !teleportType.contains("LEVER") && !player.isAdministratorRank() && !player.isInZombiesMinigame())
						{
								player.playerAssistant.sendMessage("You can't teleport above level 20 in the wilderness.");
								return false;
						}
				}

				if ((Area.inWilderness(player) || player.getHeight() == 20) && System.currentTimeMillis() < player.teleBlockEndTime)
				{
						player.playerAssistant.sendMessage("You are teleblocked and can't teleport.");
						return false;
				}

				if (player.getDead() || player.isTeleporting() || player.isAnEgg)
				{
						return false;
				}

				if (!player.isAdministratorRank() && (Combat.wasAttackingAnotherPlayer(player, 9600) || Combat.wasUnderAttackByAnotherPlayer(player, 9600)) && !teleportType.contains("LEVER") && !teleportType.equalsIgnoreCase("tab"))
				{
						Combat.inCombatAlert(player);
						return false;
				}

				if (!player.isAdministratorRank() && System.currentTimeMillis() - player.timeNpcAttackedPlayer < 9600 && Area.inWilderness(player) && !teleportType.contains("LEVER") && !teleportType.equalsIgnoreCase("tab"))
				{
						Combat.inCombatAlert(player);
						return false;
				}
				if (player.isUsingFightCaves())
				{
						player.playerAssistant.sendMessage("A strong force keeps you from teleporting.");
						return false;
				}
				return true;
		}

}