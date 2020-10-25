package game.object.click;

import game.content.miscellaneous.SpellBook;
import game.player.Player;

public class FourthClickObject
{

		public static void fourthClickObject(Player player, int objectType, int objectX, int objectY)
		{
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				player.clickObjectType = 0;
				switch (objectType)
				{
						// Altar of the occult.
						case 29150:
								SpellBook.switchToLunar(player);
								break;
						default:
								player.getPA().sendMessage("Nothing interesting happens.");
				}
		}

}
