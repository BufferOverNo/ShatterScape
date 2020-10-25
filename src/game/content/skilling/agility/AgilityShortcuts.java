package game.content.skilling.agility;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Agility shortcuts in other areas.
 * @author MGT Madness, created on 29-09-2015.
 */
public class AgilityShortcuts
{

		public static boolean isMiscAgilityShortcut(Player player, int objectId)
		{
				switch (objectId)
				{

						// Crumbling wall at Falador.
						case 11844:
								if (player.baseSkillLevel[ServerConstants.AGILITY] < 5)
								{
										player.playerAssistant.sendMessage("You need 5 agility to use this shortcut.");
										return true;
								}
								Achievements.checkCompletionSingle(player, 1026);
								player.resetPlayerTurn();
								if (player.getX() == 2934)
								{
										performAgilityShortcut(player, 839, false, 2, 0, 2936, 3355);
								}
								else
								{
										performAgilityShortcut(player, 839, false, -2, 0, 2934, 3355);
								}
								return true;

						// Monkeybars, Edgeville dungeon.
						case 23566:
								if (player.baseSkillLevel[ServerConstants.AGILITY] < 15)
								{
										player.playerAssistant.sendMessage("You need 15 agility to use this shortcut.");
										return true;
								}
								player.resetPlayerTurn();
								if (player.getY() == 9963)
								{
										performAgilityShortcut(player, 744, false, 0, 7, 3120, 9970);
								}
								else
								{
										performAgilityShortcut(player, 744, false, 0, -7, 3120, 9963);
								}
								return true;

						// Ardougne, Log balance.
						case 9330:
						case 9328:
								if (player.baseSkillLevel[ServerConstants.AGILITY] < 33)
								{
										player.playerAssistant.sendMessage("You need 33 agility to use this shortcut.");
										return true;
								}
								player.resetPlayerTurn();
								if (player.getX() == 2602)
								{
										performAgilityShortcut(player, 762, false, -4, 0, 2598, 3336);
								}
								else
								{
										performAgilityShortcut(player, 762, false, 4, 0, 2602, 3336);
								}
								return true;
				}
				return false;
		}

		public static void performAgilityShortcut(Player player, int animation, boolean run, int xTravel, int yTravel, int xEnd, int yEnd)
		{

				AgilityAssistant.agilityAnimation(player, animation, run, xTravel, yTravel);
				atPositionEvent(player, xEnd, yEnd);
		}

		public static void atPositionEvent(final Player player, final int x, final int y)
		{
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.getX() == x && player.getY() == y)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								AgilityAssistant.resetAgilityWalk(player);
								player.setDoingAgility(false);
						}
				}, 1);
		}


}
