package game.content.skilling;

import core.ServerConstants;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Hit points regeneration. This is seperate to RegenerateSkill because this one has speeding up through prayer health renewal.
 * @author MGT Madness, created on 04-10-2015.
 */
public class HitPointsRegeneration
{

		/**
		 * Begin the Hit points regeneration.
		 * @param player
		 * 			The associated player.
		 */
		public static void startHitPointsRegeneration(final Player player)
		{
				if (player.isInZombiesMinigame())
				{
						return;
				}
				if (player.hitPointsRegenerationEvent)
				{
						return;
				}
				if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) >= player.getBaseHitPointsLevel())
				{
						return;
				}
				player.hitPointsRegenerationEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.isInZombiesMinigame())
								{
										container.stop();
										return;
								}
								if (player.getTank())
								{
										return;
								}
								if (player.dead)
								{
										container.stop();
										return;
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) == 0)
								{
										container.stop();
										return;
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < player.getBaseHitPointsLevel())
								{
										player.hitPointsRegenerationCount++;
										if (player.hitPointsRegenerationCount == (player.prayerActive[ServerConstants.RAPID_HEAL] ? 1 : 2))
										{
												player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += 1;
												Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
										}
										if (player.hitPointsRegenerationCount == (player.prayerActive[ServerConstants.RAPID_HEAL] ? 1 : 2))
										{
												player.hitPointsRegenerationCount = 0;
										}
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.hitPointsRegenerationEvent = false;
								player.hitPointsRegenerationCount = 0;
						}
				}, 50);
		}

}
