package game.object.click;

import game.content.bank.Bank;
import game.content.miscellaneous.CabbagePicking;
import game.content.miscellaneous.FlaxPicking;
import game.content.miscellaneous.SpellBook;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.smithing.Smithing;
import game.content.skilling.thieving.Stalls;
import game.object.ObjectEvent;
import game.object.custom.DoorEvent;
import game.player.Player;

public class SecondClickObject
{

		public static void secondClickObject(final Player player, int objectId, final int objectX, final int objectY)
		{
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				player.clickObjectType = 0;

				if (Stalls.isStallObject(player, objectId))
				{
						return;
				}

				if (Smithing.isFurnace(player, objectId))
				{
						player.setActionIdUsed(5);
						Smithing.sendSmelting(player);
				}

				switch (objectId)
				{

						case 26760:
								player.getPA().peakIntoResourceWildArea();
								break;
						// Door/gate at Pirate hut in wild and Magic axe hut in Wild.
						case 11726:
						case 11727:
								DoorEvent.openAutomaticDoor(player);
								break;

						// Stair case at Warrior's guild.
						case 16672:
								if (player.getPA().objectIsAt(2839, 3537, 1))
								{
										player.getPA().movePlayer(2840, 3539, 2);
								}
								break;

						// Staircase climb up.
						case 11792:
								// Church in East varrock.
								if (player.getPA().objectIsAt(3258, 3487))
								{
										if (player.getHeight() == 1)
										{
												player.getPA().movePlayer(3258, 3486, 2);
										}
										else if (player.getHeight() == 2)
										{
												player.getPA().movePlayer(3258, 3486, 3);
										}
								}
								break;

						// Ladder climb up.
						case 16684:
						case 12965:
						case 14747:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
								break;
						// Altar of the occult.
						case 29150:
								SpellBook.switchToModern(player);
								break;
						// Zombies minigame chest.
						case 76:
								JewelryCrafting.jewelryInterface(player);
								break;

						// Lumbridge staircase.
						case 1739:
								player.getPA().movePlayer(player.getX(), player.getY(), 2);
								break;
						// Flax.
						case 14896:
								FlaxPicking.flaxExists(player);
								break;

						// Cabbage.
						case 1161:
								CabbagePicking.cabbageExists(player);
								break;

						// Spinning wheel.
						case 2644:
								player.getPA().sendMessage("Use a material on the spinning wheel to begin.");
								break;
						// Bank
						case 6943:
						case 24101:
								player.setUsingBankSearch(false);
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
								break;

						// Ladder at Gnome tree.
						case 2884:
						case 1748:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
								break;
						default:
								player.getPA().sendMessage("Nothing interesting happens.");

				}
		}

}
