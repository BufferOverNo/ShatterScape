package game.content.packet;

import core.ServerConfiguration;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.RainbowPartyhat;
import game.content.miscellaneous.RunecrafterHat;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Item Click 2 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 * Proper Streams
 */

public class SecondClickItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int itemId = player.getInStream().readSignedWordA();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
				}
				if (ItemAssistant.nulledItem(itemId))
				{
						return;
				}

				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("[Second click item: " + itemId + "]");
				}
				if (itemId >= 20000)
				{
						return;
				}

				if (player.doingAnAction())
				{
						return;
				}

				if (player.getDead())
				{
						return;
				}

				if (!ItemAssistant.hasItemAmountInInventory(player, itemId, 1))
				{
						return;
				}

				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				if (RunecrafterHat.isRunecrafterHat(player, itemId))
				{
						return;
				}

				switch (itemId)
				{

						case 15098:
								ItemAssistant.addItem(player, 299, Misc.random(20, 60));
								player.getPA().sendMessage("You find some seeds.");
								break;

						// Toxic blowpipe.
						case 18779:
								Blowpipe.check(player);
								break;
						// Yo-yo.
						case 4079:
								player.startAnimation(1459);
								break;

						case 7011:
								RainbowPartyhat.dismantleRainbow(player);
								break;

						case 18658:
								LootingBag.withdrawLootingBag(player);
								break;

						case 11284:
								player.playerAssistant.sendMessage("Your shield has " + player.dragonFireShieldCharges + " charges");
								break;
				}

		}

}
