package game.npc.clicknpc;

import core.ServerConfiguration;
import game.content.miscellaneous.PvpTask;
import game.content.skilling.Slayer;
import game.item.PotionCombining;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Fourth option click on NPC interactions.
 * @author MGT Madness, created on  25-11-2016.
 */
public class FourthClickNpc
{

		/**
		 * Fourth option click on NPC.
		 * @param player
		 * 			The associated player.
		 * @param npcType
		 * 			The NPC identity.
		 */
		public static void fourthClickNpc(Player player, int npcType)
		{
				player.resetNpcIdToFollow();
				player.setClickNpcType(0);
				if (!NpcHandler.npcs[player.getNpcClickIndex()].faceAction.equals("ROAM"))
				{
						NpcHandler.npcs[player.getNpcClickIndex()].facePlayer(player.getPlayerId());
						NpcHandler.npcs[player.getNpcClickIndex()].timeTurnedByPlayer = System.currentTimeMillis();
				}
				player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("Fourth Click Npc: " + player.getNpcType());
				}


				// Has to be on a delayed tick or else it will so awkward rotations when talking to an npc when running to it.
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
								player.resetFaceUpdate();
						}
				}, 2);
				switch (npcType)
				{
						// Pvp task master.
						case 11259:
								PvpTask.killsLeft(player);
								break;
						// Void Knight, Buy-back untradeables.
						case 3788:
								player.getShops().openShop(11);
								break;
						// Horvik, magic.
						case 549:
								player.getShops().openShop(19);
								break;

						// Vannaka, reset-task.
						case 1597:
								Slayer.resetTask(player);
								break;

						// Shop keeper at Edgeville.
						case 526:
								PotionCombining.decantAllPotions(player);
								break;

				}

				player.setNpcClickIndex(0);
		}

}
