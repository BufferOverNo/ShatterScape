package game.content.packet;

import game.content.godbook.PageCombining;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.CombineGodsword;
import game.content.miscellaneous.CrystalCombining;
import game.content.miscellaneous.ItemColouring;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.RainbowPartyhat;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.SpiritShieldCrafting;
import game.content.skilling.Firemaking;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.smithing.SmithingOtherItem;
import game.item.ItemAssistant;
import game.item.PotionCombining;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

public class ItemOnItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{

				int itemUsedWithSlot = player.getInStream().readUnsignedWord();
				int itemUsedSlot = player.getInStream().readUnsignedWordA();
				int itemUsedWithId = player.playerItems[itemUsedWithSlot] - 1;
				int itemUsedId = player.playerItems[itemUsedSlot] - 1;

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "usedWithSlot: " + itemUsedWithSlot);
						PacketHandler.saveData(player.getPlayerName(), "itemUsedSlot: " + itemUsedSlot);
						PacketHandler.saveData(player.getPlayerName(), "useWith: " + itemUsedWithId);
						PacketHandler.saveData(player.getPlayerName(), "itemUsed: " + itemUsedId);
				}


				if (ItemAssistant.nulledItem(itemUsedWithId))
				{
						return;
				}

				if (ItemAssistant.nulledItem(itemUsedId))
				{
						return;
				}

				if (!ItemAssistant.playerHasItem(player, itemUsedWithId, 1, itemUsedWithSlot) || !ItemAssistant.playerHasItem(player, itemUsedId, 1, itemUsedSlot))
				{
						return;
				}


				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				player.playerAssistant.stopAllActions();
				if (Fletching.isBoltFletchingRelated(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (GemCrafting.useGemOnChisel(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				if (LeatherCrafting.useNeedleOnLeather(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				if (RunePouch.useWithRunePouch(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot))
				{
						return;
				}

				if (Blowpipe.useWithBlowpipe(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot))
				{
						return;
				}

				if (LootingBag.useWithLootingBag(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot))
				{
						return;
				}

				if (Fletching.isArrowCombining(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				if (BowStringFletching.useBowStringOnLeather(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 2299, 2299, 2297, "You combine the anchovy pizza halves to create a full pizza.", false, 0, 0))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 985, 987, 989, "You combine the Tooth half of a key and Loop half of a key.", false, 0, 0))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 9431, 9452, 9465, "You combine the Runite limb with the Yew stock.", false, 0, 0))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 9465, 9438, 9185, "You combine the Runite c'bow (u) with the crossbow string.", false, 0, 0))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 18674, 6570, 18675, "You combine the Fire cape with the Max cape.", true, 0, -30))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 18674, 2412, 18676, "You combine the Saradomin cape with the Max cape.", true, 0, -30))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 18674, 2414, 18677, "You combine the Zamorak cape with the Max cape.", true, 0, -30))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 18674, 2413, 18678, "You combine the Guthix cape with the Max cape.", true, 0, -30))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 18674, 10499, 18679, "You combine the Ava's accumulator with the Max cape.", true, 0, -30))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 2572, 18654, 18656, "The ring is imbued by the scroll.", true, 0, -18))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 861, 18655, 18659, "The Magic shortbow is imbued by the scroll.", true, 0, -18))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 4153, 18661, 18662, "The Granite maul is combined with the Granite clamp.", true, 0, -50))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11730, 18657, 18660, "The Saradomin sword has been blessed.", true, 0, -18))
				{
						return;
				}

				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 5940, 1215, 5698, "The dragon dagger has been poisoned.", false, 0, 0))
				{
						return;
				}

				if (PageCombining.isPage(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (SpiritShieldCrafting.createSpiritShield(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (ItemColouring.combine(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (CombineGodsword.createGodSwordBlade(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (CombineGodsword.createGodSword(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (Herblore.isHerbloreItemOnItem(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (Firemaking.grabData(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (Fletching.normal(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (Fletching.others(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (SmithingOtherItem.smithVariousItems(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (RainbowPartyhat.partyHatCombine(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				if (JewelryCrafting.stringAmulet(player, itemUsedId, itemUsedWithId))
				{
						return;
				}
				if (CrystalCombining.isCrystalBootsParts(player, itemUsedId, itemUsedWithId))
				{
						return;
				}

				PotionCombining.combinePotion(player, true, itemUsedId, itemUsedWithId, itemUsedWithSlot, itemUsedSlot, false);

		}

}