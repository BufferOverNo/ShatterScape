package game.content.packet;

import game.content.combat.Combat;
import game.content.skilling.Runecrafting;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import game.player.movement.Follow;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;


/**
 * Wear Item
 **/

public class WearItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting() || player.getDead() || player.isAnEgg || player.getTransformed() != 0)
				{
						return;
				}
				if (player.canUseGameModeInterface)
				{
						return;
				}
				int wearId = 0;
				int wearSlot = 0;
				int interfaceId = 0;
				wearId = player.getInStream().readUnsignedWord();
				wearSlot = player.getInStream().readUnsignedWordA();
				interfaceId = player.getInStream().readUnsignedWordA();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "wearId: " + wearId);
						PacketHandler.saveData(player.getPlayerName(), "wearSlot: " + wearSlot);
						PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
				}

				if (ItemAssistant.nulledItem(wearId))
				{
						return;
				}
				if (ItemDefinition.getDefinitions()[wearId].note)
				{
						PacketHandler.itemLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.itemLog.add("Noted item worn: " + ItemAssistant.getItemName(wearId) + ", slot: " + wearSlot + ", interfaceId: " + interfaceId);
						return;
				}

				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				Follow.resetFollow(player);
				if (!ItemAssistant.playerHasItem(player, wearId, 1, wearSlot))
				{
						return;
				}
				if (wearId >= 5509 && wearId <= 5515)
				{
						int pouch = -1;
						int pouchUsed = wearId;
						switch (pouchUsed)
						{
								case 5509:
										pouch = 0;
										break;
								case 5510:
										pouch = 1;
										break;
								case 5512:
										pouch = 2;
										break;
								case 5514:
										pouch = 3;
										break;
						}

						Runecrafting.emptyPouch(player, pouch);
						player.playerAssistant.sendMessage("You have emptied your pouch.");
						return;
				}

				if ((player.getPlayerIdAttacking() > 0 || player.getNpcIdAttacking() > 0))
				{
						// Granite mauls.
						if (wearId != 4153 && wearId != 18662)
						{
								Combat.resetPlayerAttack(player);
								player.playerAssistant.stopAllActions();
						}
						else
						{
								player.setUsingRanged(false);
								player.setUsingMagic(false);
								player.setLastCastedMagic(false);
								player.setSpellId(-1);
								player.setMeleeFollow(true);
								player.usedGmaul = true;
								player.setUsingMediumRangeRangedWeapon(false);
								player.setUsingShortRangeRangedWeapon(false);
								if (player.getPlayerIdAttacking() > 0)
								{
										player.setPlayerIdToFollow(player.getPlayerIdAttacking());
								}
						}
				}
				ItemAssistant.wearItem(player, wearId, wearSlot);
		}

}