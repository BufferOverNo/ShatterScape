package game.content.miscellaneous;

import game.content.achievement.Achievements;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Crystal key on chest in Taverly.
 * @author MGT Madness, created on 15-08-2016.
 */
public class CrystalChest
{

		// @formatter:off
		private final static int[][][] LOOT = {
			{{1631, 1}, {1079, 1}, {995, 2000}}, 
			{{1631, 1}, {371, 5}, {995, 1000}}, 
			{{1631, 1}, {554, 50}, {555, 50}, {556, 50}, {557, 50}, {558, 50}, {559, 50}, {560, 10}, {561, 10}, {562, 10}, {563, 10}, {564, 10}}, 
			{{1631, 1}, {454, 100}}, 
			{{1631, 1}, {1603, 2}, {1601, 2}}, 
			{{1631, 1}, {985, 1}, {995, 750}}, 
			{{1631, 1}, {2363, 3}}, 
			{{1631, 1}, {987, 1}, {995, 750}}, 
			{{1631, 1}, {441, 150}}, 
			{{1631, 1}, {1183, 1}}, 
			{{1631, 1}, {1079, 1}} // if female, put 1093 instead of 1079.
			};
		// @formatter:on

		private final static int[] chance = {100, 59, 42, 32, 24, 17, 13, 9, 6, 3, 1};

		public static void giveLoot(Player player, int itemId)
		{
				if (itemId != 989)
				{
						return;
				}
				int random = Misc.random(1, 100);
				for (int index = chance.length - 1; index >= 0; index--)
				{
						if (random <= chance[index])
						{
								player.getPA().sendMessage("You use the Crystal key on the chest.");
								Achievements.checkCompletionMultiple(player, "1089 1090 1133");
								ItemAssistant.deleteItemFromInventory(player, 989, 1);
								for (int i = 0; i < LOOT[index].length; i++)
								{
										int amount = LOOT[index][i][1];
										int itemId1 = LOOT[index][i][0];
										if (ItemDefinition.getDefinitions()[itemId1].stackable)
										{
												amount *= 5;
										}
										if (amount == 1 && !ItemDefinition.getDefinitions()[itemId1].stackable && itemId != 985 && itemId != 987)
										{
												itemId1++;
												amount = Misc.random(3, 10);
										}
										ItemAssistant.addItemToInventoryOrDrop(player, itemId1, amount);
								}
								int chance = 0;
								if (Misc.hasPercentageChance(chance))
								{
										ItemAssistant.addItemToInventoryOrDrop(player, 1632, 3);
								}
								break;
						}
				}
		}

}
