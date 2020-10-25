package game.object.click;

import game.content.bank.Bank;
import game.content.miscellaneous.SpellBook;
import game.object.ObjectEvent;
import game.player.Player;

public class ThirdClickObject
{

		public static void thirdClickObject(Player player, int objectType, int objectX, int objectY)
		{
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				player.clickObjectType = 0;
				switch (objectType)
				{




						// Stair case at Warrior's guild.
						case 16672:
								if (player.getPA().objectIsAt(2839, 3537, 1))
								{
										player.getPA().movePlayer(2840, 3539, 0);
								}
								break;
						// Staircase climb down.
						case 11792:
								// Church in East varrock.
								if (player.getPA().objectIsAt(3258, 3487) && player.getHeight() == 1)
								{
										player.getPA().movePlayer(3257, 3487, 0);
								}
								else if (player.getPA().objectIsAt(3258, 3487) && player.getHeight() == 2)
								{
										player.getPA().movePlayer(3258, 3486, 1);
								}
								break;
						// Ladder climb down.
						case 16684:
						case 12965:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
								break;

						// Altar of the occult.
						case 29150:
								SpellBook.switchToAncients(player);
								break;
						// Lumbridge staircase.
						case 1739:
								player.getPA().movePlayer(player.getX(), player.getY(), 0);
								break;

						case 14747:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), 0);
								break;
						case 10177:
								// Dagganoth ladder 1st level
								player.getPA().movePlayer(1798, 4407, 3);
								break;

						// Bank.
						case 6943:
								player.setUsingBankSearch(false);
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
								break;

						// Ladder at Gnome tree.
						case 2884:
						case 1748:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
								break;
						default:
								player.getPA().sendMessage("Nothing interesting happens.");
				}
		}

}
