package game.content.miscellaneous;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;



/**
 * General methods related to the player. Unfinished with PlayerOther class...
 * @author MGT Madness, created on 01-01-2015.
 */
public class PlayerMiscContent
{

		private final static int[] npcsWhoCanBow = {1, 2, 3, 4, 5, 6, 7, 8, 9, 6970, 548, 519, 883, 7949, 549, 6540, 5196, 494, 495, 496, 497, 902, 6538, 176, 3073, 196, 195, 202, 172, 174, 7110, 187, 124, 922};

		private final static int[] hailEmoteList = {858, 862, 863, 2109, 861, 866, 2106, 2107, 2108, 865, 3543, 6111, 7531, 1331};

		/**
		 * All players to perform an emote and text.
		 * @param text
		 * 			The text for the players to shout.
		 */
		public static void allPlayersHail(String text[])
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						Player player = PlayerHandler.players[j];
						if (player != null)
						{
								Player p = player;
								p.forcedChat(text[Misc.random(text.length - 1)]);
								if (!Combat.inCombat(player))
								{
										int index = Misc.random(hailEmoteList.length - 1);
										p.startAnimation(hailEmoteList[index]);
								}
						}
				}

				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
						{
								continue;
						}
						NpcHandler.npcs[i].forceChat(text[Misc.random(text.length - 1)]);
						for (int a = 0; a < npcsWhoCanBow.length; a++)
						{

								if (NpcHandler.npcs[i].npcType == npcsWhoCanBow[a])
								{
										int index = Misc.random(hailEmoteList.length - 1);
										NpcHandler.npcs[i].requestAnimation(hailEmoteList[index]);
								}
						}
				}
		}

		/**
		 * Pray at the altar.
		 * @param player
		 * 			The associated player.
		 */
		public static void prayAtAltar(Player player)
		{
				player.getPA().closeInterfaces();
				if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) >= player.getBasePrayerLevel())
				{
						player.playerAssistant.sendMessage("Your prayer points is already full.");
						return;
				}
				player.playerAssistant.sendMessage("Your recharge your prayer points.");
				player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
				player.startAnimation(645);
				SoundSystem.sendSound(player, 442, 200);
		}

		/**
		 * @return
		 * 			The ancient magick interface.
		 */
		public static int getAncientMagicksInterface(Player player)
		{
				if (player.ancientsInterfaceType == 1)
				{
						return 24836;
				}
				else if (player.ancientsInterfaceType == 2)
				{
						return 24818;
				}
				else
				{
						return 24800;
				}
		}

		/**
		 * Calculate and change wildernessLevel integer.
		 * @param player
		 * 			The associated player.
		 */
		public static void calculateWildernessLevel(Player player)
		{
				int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
				int level = (((modY - 3520) / 8) + 1);
				player.wildernessLevel = level;
		}

}
