package game.content.miscellaneous;

import game.player.Player;
import utility.Misc;

/**
 * Transform into monkey/egg etc..
 * @author MGT Madness, created on 02-03-2015.
 */
public class Transform
{

		/**
		  * Transform into a random Easter egg.
		  * @param player
		  * 			The associated player.
		  */
		public static void easterEggTransform(Player player)
		{
				player.npcId2 = 3689 + Misc.random(5);
				player.isNpc = true;
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				player.isAnEgg = true;
				player.setTransformed(5);
		}

		/**
		 * Transform the player back to normal.
		 * @param player
		 * 			The associated player.
		 */
		public static void unTransform(Player player)
		{
				player.npcId2 = -1;
				player.isNpc = false;
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				player.isAnEgg = false;
				player.setTransformed(0);
		}

}
