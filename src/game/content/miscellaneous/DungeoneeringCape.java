package game.content.miscellaneous;

import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Dungeoneering cape emote.
 * @author MGT Madness, created on 02-03-2015.
 */
public class DungeoneeringCape
{

		/**
		* Perform the Dungeoneering cape emote.
		*/
		public static void performAnimation(final Player player)
		{

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.dungTime == 16)
								{
										player.gfx0(2442);
										player.startAnimation(13190);
								}
								else if (player.dungTime == 15)
								{
										player.npcId2 = 11228;
										player.isNpc = true;
										player.setUpdateRequired(true);
										player.setAppearanceUpdateRequired(true);
										player.startAnimation(13192);
								}
								else if (player.dungTime == 10)
								{
										player.npcId2 = 11227;
										player.isNpc = true;
										player.setUpdateRequired(true);
										player.setAppearanceUpdateRequired(true);
										player.startAnimation(13193);
								}
								else if (player.dungTime == 6)
								{
										player.gfx0(2442);
								}
								else if (player.dungTime == 5)
								{
										player.npcId2 = 11229;
										player.setUpdateRequired(true);
										player.setAppearanceUpdateRequired(true);
										player.startAnimation(13194);
								}
								if (player.dungTime == 0)
								{
										player.npcId2 = -1;
										player.setUpdateRequired(true);
										player.setAppearanceUpdateRequired(true);
										container.stop();
								}
								if (player.dungTime > 0)
								{
										player.dungTime--;
								}
						}

						@Override
						public void stop()
						{
								player.dungTime = 16;
						}
				}, 1);

		}

}
