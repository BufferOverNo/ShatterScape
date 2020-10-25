package game.npc.clicknpc;

import core.ServerConfiguration;
import game.content.miscellaneous.PvpTask;
import game.content.skilling.Slayer;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Third option click on NPC interactions.
 * @author MGT Madness, created on 18-01-2013.
 */
public class ThirdClickNpc
{

		/**
		 * Third option click on NPC.
		 * @param player
		 * 			The associated player.
		 * @param npcType
		 * 			The NPC identity.
		 */
		public static void thirdClickNpc(Player player, int npcType)
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
						Misc.print("Third Click Npc: " + player.getNpcType());
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
								PvpTask.claimReward(player);
								break;

						// Void Knight, Untradeables.
						case 3788:
								player.getShops().openShop(46);
								break;
						// Horvik, ranged.
						case 549:
								player.getShops().openShop(20);
								break;

						// Vannaka, boss-task.
						case 1597:
								Slayer.giveBossTask(player);
								break;

						// Shop keeper at Edgeville.
						case 526:
								player.getShops().openShop(7);
								break;

						// Shop keeper at Entrana.
						case 528:
								/*
									PotionCombining.combineAllPotions(player);
									if (player.potionDecanted)
									{
											player.getPA().sendMessage("The shopkeeper decants all the potions for you.");
									}
									else
									{
											player.getPA().sendMessage("No potions available to decant.");
									}
									player.potionDecanted = false;
									*/
								break;

				}

				player.setNpcClickIndex(0);
		}

}
