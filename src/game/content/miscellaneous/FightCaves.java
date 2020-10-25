package game.content.miscellaneous;

import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.NpcHandler;
import game.npc.pet.BossPetDrops;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * TzHaar fight caves minigame.
 * @author MGT Madness, created on 02-01-2015.
 */
public class FightCaves
{

		public static void exitFightCaves(Player player)
		{
				if (player.getY() != 5117)
				{
						return;
				}
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
						{
								continue;
						}
						if (NpcHandler.npcs[i].npcType != 2745)
						{
								continue;
						}
						if (NpcHandler.npcs[i].getSpawnedBy() != player.getPlayerId())
						{
								continue;
						}
						NpcHandler.npcs[i] = null;
						break;
				}
				player.getPA().movePlayer(2438, 5168, 0);
				player.setUsingFightCaves(false);
		}

		/**
		 * Start the fight caves minigame.
		 * @param player
		 * 			The associated player.
		 */
		public static void startFightCaves(final Player player)
		{
				if (player.isUsingFightCaves())
				{
						return;
				}
				player.setUsingFightCaves(true);
				player.getPA().movePlayer(2413, 5117, player.getPlayerId() * 4);

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
								player.setNpcType(2617);
								player.getDH().sendDialogues(172);
						}
				}, 1);

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
								NpcHandler.spawnNpc(player, 2745, 2408, 5101, player.getHeight(), true, true);
						}
				}, 5);
		}


		/**
		 * Reward the player for completing the fight caves minigame.
		 * @param playerID
		 * 			The associated player.
		 * @param npcKilled
		 * 			The npc killed.
		 */
		public static void fightCavesReward(int playerID, int npcKilled)
		{
				if (npcKilled != 2745)
				{
						return;
				}
				final Player player = PlayerHandler.players[playerID];

				if (player == null)
				{
						return;
				}
				player.startAnimation(862);
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
								player.getPA().movePlayer(2438, 5168, 0);
								player.setNpcType(2617);
								player.getDH().sendDialogues(171);
								ItemAssistant.addItemToInventoryOrDrop(player, FIRE_CAPE, 1);
								ItemAssistant.addItemToInventoryOrDrop(player, 6529, 250);
								player.setUsingFightCaves(false);

								// Has to be kept here.
								if (Misc.hasOneOutOf(GameMode.getDropRate(player, BossPetDrops.DROP_RATE_JAD_PET)))
								{

										if (ItemAssistant.hasSingularUntradeableItem(player, 7041))
										{
												return;
										}
										BossPetDrops.awardBoss(player, 7041, 2745);
								}
						}
				}, 5);

		}

		private static int FIRE_CAPE = 6570;

}