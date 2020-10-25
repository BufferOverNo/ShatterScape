package game.content.packet;

import core.ServerConfiguration;
import game.content.combat.Combat;
import game.content.consumable.Potions;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Wolpertinger;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 * Proper Streams
 */

public class ThirdClickItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{

				int itemId11 = player.getInStream().readSignedWordBigEndianA();
				int itemSlot = player.getInStream().readUnsignedWordA(); // Slot = 128 + (slotNumber * 256) // Slot starts at 0 to 27.
				int itemId = player.getInStream().readSignedWordA();
				itemSlot = (itemSlot - 128) / 256;

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "itemId11: " + itemId11);
						PacketHandler.saveData(player.getPlayerName(), "itemSlot: " + itemSlot);
						PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
				}


				if (ItemAssistant.nulledItem(itemId))
				{
						return;
				}

				if (!ItemAssistant.playerHasItem(player, itemId, 1, itemSlot))
				{
						return;
				}

				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("[Third click item: " + itemId + "]");
				}
				if (player.doingAnAction())
				{
						return;
				}

				if (player.getDead())
				{
						return;
				}
				if (Potions.isPotion(player, itemId) || ItemAssistant.getItemName(itemId).toLowerCase().contains("potion"))
				{
						player.playerAssistant.sendMessage("You empty the " + ItemAssistant.getItemName(itemId) + ".");
						ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
						ItemAssistant.addItemToInventory(player, 229, 1, itemSlot, true);
						return;
				}
				// Amulet of glory.
				if (itemId >= 1706 && itemId <= 1712)
				{
						if (Combat.inCombatAlert(player))
						{
								return;
						}
						Teleport.startTeleport(player, 3085 + Misc.random(3), 3491 + Misc.random(5), 0, "GLORY " + itemId + " INVENTORY");
						return;
				}
				else if (itemId == 1704)
				{
						player.playerAssistant.sendMessage("Your amulet of glory has run out of charges.");
				}

				switch (itemId)
				{

						case 18779:
								Blowpipe.unload(player);
								break;
						// Rune pouch.
						case 18820:
								player.setActionIdUsed(8);
								RunePouch.runePouchItemClick(player, "EMPTY");
								break;

						// Yo-yo.
						case 4079:
								player.startAnimation(1460);
								break;

						// Wolpertinger pouch.
						case 12089:
								Wolpertinger.summonWolpertinger(player, true);
								break;
				}

		}

}
