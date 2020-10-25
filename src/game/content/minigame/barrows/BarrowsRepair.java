package game.content.minigame.barrows;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Repair Barrows equipment.
 * @author MGT Madness, created on 22-02-2015.
 */
public class BarrowsRepair
{
		/**
		 * Cost of repairing each Barrows piece.
		 */
		private final static int COST = 100000;

		/**
		 * List of barrows pieces followed by the repaired version.
		 */
		public static int[][] brokenBarrows = {
				{4708, 4860},
				{4710, 4866},
				{4712, 4872},
				{4714, 4878},
				{4716, 4884},
				{4720, 4896},
				{4718, 4890},
				{4720, 4896},
				{4722, 4902},
				{4732, 4932},
				{4734, 4938},
				{4736, 4944},
				{4738, 4950},
				{4724, 4908},
				{4726, 4914},
				{4728, 4920},
				{4730, 4926},
				{4745, 4956},
				{4747, 4962},
				{4749, 4968},
				{4751, 4974},
				{4753, 4980},
				{4755, 4986},
				{4757, 4992},
				{4759, 4998}};

		/**
		 * Repair the Barrows equipment.
		 * @param player
		 * 			The associated player.
		 */
		public static void repair(Player player)
		{
				int totalCost = 0;
				int costPerPiece = COST;



				if (player.isLegendaryDonator())
				{
						costPerPiece -= 100;
				}
				else if (player.isExtremeDonator())
				{
						costPerPiece -= 75;
				}
				else if (player.isSuperDonator())
				{
						costPerPiece -= 50;
				}
				else if (player.isDonator())
				{
						costPerPiece -= 25;
				}
				boolean hasBarrowsItem = false;
				for (int j = 0; j < player.playerItems.length; j++)
				{
						if (player.playerItems[j] <= 1)
						{
								continue;
						}
						for (int i = 0; i < brokenBarrows.length; i++)
						{
								if (player.playerItems[j] - 1 == brokenBarrows[i][1])
								{
										int cashAmount = ItemAssistant.getItemAmount(player, 995);
										if (costPerPiece > cashAmount)
										{
												player.getDH().sendDialogues(3); // You do not have enough coins dialogue.
												return;
										}
										ItemAssistant.deleteItemFromInventory(player, 995, costPerPiece);
										hasBarrowsItem = true;
										totalCost += costPerPiece;
										player.playerItems[j] = brokenBarrows[i][0] + 1;
								}
						}
				}
				if (!hasBarrowsItem)
				{
						player.getDH().sendDialogues(214); // You do not have any barrows equipment.
						return;
				}
				if (totalCost > 0)
				{
						String plural = (totalCost > costPerPiece ? "s." : ".");
						player.playerAssistant.sendMessage("Bob repairs the barrows piece" + plural);
						player.getPA().closeInterfaces();
				}
		}

}