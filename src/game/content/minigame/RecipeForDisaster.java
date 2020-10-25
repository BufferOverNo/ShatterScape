package game.content.minigame;

import game.content.combat.Combat;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class RecipeForDisaster
{
		public final static int[] NPC_WAVE_LIST = {3493, 3494, 3495, 3496, 3498, 3491};

		public static void isRfdNpc(Player player, Npc npc)
		{
				/*
					boolean isNpc = false;
					for (int index = 0; index < NPC_WAVE_LIST.length; index++)
					{
							if (npc.npcType == NPC_WAVE_LIST[index])
							{
									isNpc = true;
							}
					}
				
					if (!isNpc)
					{
							return;
					}
					// Remove head icon from npc killed.
					player.getPA().drawHeadicon(0, npc.npcIndex, 0, 0);
					player.rfdWave++;
					if (player.rfdWave == 6)
					{
							// What if player is tabed and cannot teleport.
							Teleport.startTeleport(player, 3086 + Misc.random(3), 3508 + Misc.random(4), 0, "MODERN");
					}
					if (player.rfdWave > player.highestRfdWave)
					{
							player.highestRfdWave = player.rfdWave - 1;
					}
					*/
		}

		public static void spawnNextWave(final Player player, boolean firstSpawn, int npcType)
		{
				boolean isRfdNpc = false;
				for (int i = 0; i < NPC_WAVE_LIST.length; i++)
				{
						if (npcType == NPC_WAVE_LIST[i])
						{
								isRfdNpc = true;
						}
				}
				if (!isRfdNpc)
				{
						return;
				}
				if (player.rfdWave >= NPC_WAVE_LIST.length)
				{
						player.rfdWave = 0;
						player.playerAssistant.sendMessage("Congratulations, you have completed the Recipe for Disaster minigame!");
						return;
				}
				final int npcId = NPC_WAVE_LIST[player.rfdWave];
				Combat.resetPrayers(player);
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
								NpcHandler.spawnNpc(player, npcId, 1897 + Misc.random(5), 5349 + Misc.random(4), player.getPlayerId() * 4 + 2, true, true);
						}
				}, 3);
		}

		public static boolean hasGlovesRequirements(Player player, int itemId, boolean shopping, boolean message)
		{
				if (GameMode.getGameMode(player, "DEFENDER"))
				{
						return true;
				}
				if (shopping && player.shopId != 57)
				{
						return true;
				}
				boolean entered = false;
				if (!message)
				{
						player.doNotSendMessage = true;
				}
				int wave = 5;

		//@formatter:off
		switch (itemId)
		{
	
			case 7462:
				entered = true;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
			case 7461:
				entered = true;
				wave = 4;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
			case 7460:
				entered = true;
			        wave = 3;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
			case 7459:
				entered = true;
				wave = 2;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
			case 7458:
				entered = true;
				wave = 1;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
			case 7457:
			case 7456:
			case 7455:
				entered = true;
				wave = 0;
				if (player.highestRfdWave >= wave)
				{
					return true;
				}
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
		}
		//@formatter:on

				player.doNotSendMessage = false;
				if (entered && !message)
				{
						return true;
				}
				if (!shopping && !message)
				{
						return false;
				}
				return true;
		}
}
