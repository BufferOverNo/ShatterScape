package game.content.packet;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.miscellaneous.EditCombatSkill;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.prayer.BoneOnAltar;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Bank X Items
 **/

public class BankXPacket implements PacketType
{
		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int xAmount = player.getInStream().readDWord();
				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "xAmount: " + xAmount);
				}
				if (xAmount <= 0)
				{
						return;
				}
				if (player.xInterfaceId == 5064)
				{
						Bank.bankItem(player, player.playerItems[player.xRemoveSlot], player.xRemoveSlot, xAmount, true);

				}
				else if (player.xInterfaceId == 5382)
				{
						Bank.withdrawFromBank(player, player.bankingItems[player.xRemoveSlot], player.xRemoveSlot, xAmount);
						Bank.updateClientLastXAmount(player, xAmount);

				}
				else if (player.xInterfaceId == 3322)
				{
						if (player.getDuelStatus() <= 0 && player.isInTrade())
						{
								player.getTradeAndDuel().tradeItem(player.xRemoveId, player.xRemoveSlot, xAmount);
						}
						else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2)
						{
								player.getTradeAndDuel().stakeItem(player.xRemoveId, player.xRemoveSlot, xAmount);
						}

				}
				else if (player.xInterfaceId == 3415)
				{
						if (player.getDuelStatus() <= 0)
						{
								player.getTradeAndDuel().fromTrade(player.xRemoveId, player.xRemoveSlot, xAmount);
						}

				}

				else if (player.xInterfaceId == 6669)
				{
						player.getTradeAndDuel().fromDuel(player.xRemoveId, player.xRemoveSlot, xAmount);

				}

				else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.HERBLORE]))
				{
						Herblore.xAmountHerbloreAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING] + " LEATHER"))
				{
						Herblore.xAmountHerbloreAction(player, xAmount);
						LeatherCrafting.xAmountLeatherCraftingAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING]))
				{
						GemCrafting.xAmountCraftingAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.FLETCHING]))
				{
						BowStringFletching.xAmountFletchingAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals("HARD LEATHER BODY"))
				{
						LeatherCrafting.xAmountHardLeatherBodyAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.PRAYER]))
				{
						BoneOnAltar.xAmountPrayerAction(player, xAmount);
				}

				else if (player.getAmountInterface().equals("TANNING"))
				{
						LeatherCrafting.tan(player, player.skillingData[0], player.skillingData[1], xAmount, player.skillingData[2]);
				}

				else if (player.getAmountInterface().equals("STRINGING AMULET"))
				{
						JewelryCrafting.stringAmuletAmount(player, xAmount);
				}

				else if (player.getAmountInterface().equals("COMBINE ARROWS"))
				{
						Fletching.xAmountCombineArrowParts(player, xAmount);
				}

				else if (player.getAmountInterface().equals("CUT GEM INTO BOLT TIPS"))
				{
						Fletching.xAmountCutGem(player, xAmount);
				}

				else if (player.getAmountInterface().equals("ATTACH TIPS TO BOLT"))
				{
						Fletching.xAmountAttachTipToBolt(player, xAmount);
				}

				else if (player.getAmountInterface().equals("LOOT NOTIFICATION"))
				{
						player.valuableLoot = xAmount;
						player.getPA().sendMessage("Loot notification will appear for items worth " + ServerConstants.RED_COL + Misc.formatNumber(player.valuableLoot) + ServerConstants.BLACK_COL + " blood money and above.");
				}
				else if (player.getAmountInterface().equals("SHOP BUY X"))
				{
						player.getShops().buyItem(player.xRemoveId, player.xRemoveSlot, xAmount);
				}
				else if (player.getAmountInterface().equals("SHOP SELL X"))
				{
						player.getShops().sellItemToShop(player, player.xRemoveId, player.xRemoveSlot, xAmount);
				}
				else if (player.xInterfaceId == 7423)
				{
						DepositBox.depositItemAmount(player, player.xRemoveSlot, xAmount);
				}

				else
				{
						EditCombatSkill.editCombatSkill(player, xAmount);
				}
		}
}