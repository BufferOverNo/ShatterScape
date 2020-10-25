package game.content.packet;

import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.minigame.zombie.ZombieGameInstance;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.item.BloodMoneyPrice;
import game.item.DestroyItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.pet.DropPet;
import game.player.Area;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Drop Item
 **/

public class DropItemPacket implements PacketType
{

		@Override
		public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.getDoingAgility() || !player.isTutorialComplete() || player.isTeleporting() || player.getDead() || player.isInTrade() || System.currentTimeMillis() - player.alchDelay < 1800)
				{
						return;
				}

				int itemId = player.getInStream().readUnsignedWordA();
				int value1 = player.getInStream().readUnsignedByte();
				int value2 = player.getInStream().readUnsignedByte();
				int slot = player.getInStream().readUnsignedWordA();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
						PacketHandler.saveData(player.getPlayerName(), "value1: " + value1);
						PacketHandler.saveData(player.getPlayerName(), "value2: " + value2);
						PacketHandler.saveData(player.getPlayerName(), "slot: " + slot);
				}

				if (ItemAssistant.nulledItem(itemId))
				{
						if (player.isAdministratorRank())
						{
								ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
						}
						return;
				}

				if (!ItemAssistant.playerHasItem(player, itemId, 1, slot))
				{
						return;
				}

				if (!EdgeAndWestsRule.canPickUpItemEdgeOrWestsRule(player, itemId, false))
				{
						return;
				}


				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				boolean droppable = true;
				player.playerAssistant.stopAllActions();
				player.itemDestroyedSlot = slot;
				Combat.resetPlayerAttack(player);
				player.itemDestroyedId = itemId;

				if (itemId == 18658 && Combat.inCombat(player))
				{
						return;
				}
				for (int j = 0; j < ServerConstants.UNTRADEABLE_ITEMS.length; j++)
				{
						if (itemId == ServerConstants.UNTRADEABLE_ITEMS[j])
						{// Looting bag.
								DestroyItem.displayDestroyItemInterface(player, itemId, slot);
								return;
						}
				}

				if (DropPet.dropPetRequirements(player, itemId, slot))
				{
						return;
				}

				if (itemId == 4045)
				{
						int amount = 15;
						if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - amount < 1)
						{
								amount = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - 1;
						}
						player.startAnimation(827);
						ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
						if (amount > 0)
						{
								Combat.appendHitFromNpcOrVengEtc(player, amount, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						}
						player.forcedText = "Ow! That really hurt!";
						player.forcedChatUpdateRequired = true;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
						droppable = false;

				}

				// Blood key.
				if (itemId == 18825)
				{
						if (Area.inWilderness(player))
						{
								player.getPA().sendMessage("There's no turning back.");
								return;
						}
				}
				if (droppable)
				{
						if (ItemDefinition.getDefinitions()[itemId] != null)
						{
								int itemValue = (BloodMoneyPrice.getBloodMoneyPrice(itemId) * ItemAssistant.getItemAmount(player, itemId, slot));
								if (Area.inWilderness(player) && itemValue > 100 && Combat.inCombat(player))
								{
										player.getPA().sendMessage("You cannot drop an item more than 100 blood money in combat.");
										return;
								}
								if (System.currentTimeMillis() - player.timeUsedRiskCommand < 45000 && itemValue > 100)
								{
										player.getPA().quickChat("I have dropped an item on the floor.");
								}
								if (player.getHeight() == 20 && itemId == 10499)
								{
										player.getPA().sendMessage("Wasteman.");
										return;
								}
								Server.itemHandler.createGroundItem(player, itemId, player.getX(), player.getY(), player.playerItemsN[slot], ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName()) >= 0 ? true : false, 0, true, player.getPlayerName());
						}
						ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
						SoundSystem.sendSound(player, 376, 0);
				}
				ItemsKeptOnDeath.updateInterface(player);
		}
}