package game.npc.pet;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Boss pet drops after getting a boss kill.
 * @author MGT Madness, created on 16-01-2016.
 */
public class BossPetDrops
{

		public final static int DROP_RATE_NORMAL_BOSS_PET = 600;

		public final static int DROP_RATE_JAD_PET = 200;

		/**
		 * Boss npc id, boss pet inventory id. Does not include Jad, jad is in FightCaves class
		 * Also NpcKillTracker.BOSS_LIST which counts the Boss kills amount in profile.
		 */
		public final static int[][] NORMAL_BOSS_DATA = {
		//@formatter:off
		{
			2881, 12643
		},
		{
			2882, 12644
		},
		{
			2883, 12646
		},
		{
			4040, 18641
		},
		{
			6203, 12652
		},
		{
			6247, 12651
		},
		{
			6222, 12649
		},
		{
			6260, 12650
		},
		{
			8133, 14006
		},
		{
			3200, 11995
		},
		{
			50, 12653
		},
		{
			4043, 18761 // Venenatis.
		},
		{
			8349, 14010 // Tormented demon.
		},
		{
			4045, 18762 // Cerberus.
		},
		{
			9463, 14012 // Ice Strykewyrm.
		}
		//@formatter:on
		};

		public static void bossPetDrop(Player player, Npc npc)
		{
				int chance = 0;
				int item = 0;
				int npcId = npc.npcType;
				if (npcId == 8350 || npcId == 8351)
				{
						npcId = 8349;
				}

				for (int i = 0; i < NORMAL_BOSS_DATA.length; i++)
				{
						if (npcId == NORMAL_BOSS_DATA[i][0])
						{
								chance = DROP_RATE_NORMAL_BOSS_PET;
								item = NORMAL_BOSS_DATA[i][1];
						}
				}
				if (item == 0)
				{
						return;
				}

				if (ItemAssistant.hasSingularUntradeableItem(player, item))
				{
						return;
				}
				if (player.getPetSummoned())
				{
						for (int i = 0; i < PetData.petData.length; i++)
						{
								if (PetData.petData[i][0] == player.getPetId())
								{
										if (PetData.petData[i][1] == item)
										{
												return;
										}
								}
						}
				}
				if (Misc.hasOneOutOf(GameMode.getDropRate(player, chance)))
				{
						awardBoss(player, item, npc.npcType);
				}
		}

		public static void awardBoss(Player player, int item, int npcType)
		{
				boolean received = false;

				if (ItemAssistant.hasSingularUntradeableItem(player, item))
				{
						return;
				}
				if (!received)
				{
						if (player.getPetSummoned() && ItemAssistant.addItem(player, item, 1))
						{
								received = true;
								player.getPA().sendMessage("You feel something weird sneaking into your backpack.");
						}
				}
				if (!received && !player.getPetSummoned())
				{
						for (int i = 0; i < PetData.petData.length; i++)
						{
								if (PetData.petData[i][1] == item)
								{
										Pet.summonNpcOnValidTile(player, PetData.petData[i][0]);
										player.getPA().sendMessage("You have a funny feeling like you're being followed.");
										received = true;
								}
						}
				}
				if (!received)
				{
						Bank.addItemToBank(player, item, 1, false);
						player.getPA().sendMessage("Your pet has been added to your bank.");
						received = true;
				}
				if (received)
				{
						RareDropLog.appendRareDrop(player, NpcDefinition.getDefinitions()[npcType].name + ": " + ItemAssistant.getItemName(item));
						player.singularUntradeableItemsOwned.add(Integer.toString(item));
						Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(item) + " from " + NpcDefinition.getDefinitions()[npcType].name + ".");
						player.getPA().sendScreenshot(ItemAssistant.getItemName(item), 2);
				}
		}

}