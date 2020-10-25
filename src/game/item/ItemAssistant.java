package game.item;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.achievement.AchievementShop;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.range.RangedData;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.commands.NormalCommand;
import game.content.donator.DonatorFeatures;
import game.content.miscellaneous.ItemColouring;
import game.content.miscellaneous.ItemColouring.ItemColouringData;
import game.content.miscellaneous.Transform;
import game.content.miscellaneous.TransformedOnDeathItems;
import game.content.skilling.Skilling;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.npc.pet.PetData;
import game.player.Player;
import game.player.PlayerHandler;
import network.packet.PacketHandler;
import utility.Misc;

public class ItemAssistant
{

		/**
		 * Combine two items together.
		 */
		public static boolean combineTwoItems(Player player, int itemUsedId, int itemUsedWithId, int item1, int item2, int result, String text, boolean statement, int offset1, int offset2)
		{
				if (ItemOn.hasTwoItems(player, itemUsedId, itemUsedWithId, item1, item2))
				{
						ItemAssistant.deleteItemFromInventory(player, item1, 1);
						ItemAssistant.deleteItemFromInventory(player, item2, 1);
						ItemAssistant.addItem(player, result, 1);
						if (statement)
						{
								player.getDH().sendItemChat1("", text, result, 200, offset1, offset2);
						}
						else
						{
								player.getPA().sendMessage(text);
						}
						return true;
				}
				return false;
		}

		public static boolean nulledItem(int itemId)
		{
				if (itemId <= 0)
				{
						return true;
				}
				if (itemId > ItemDefinition.getDefinitions().length - 1)
				{
						return true;
				}
				if (ItemDefinition.getDefinitions()[itemId] == null)
				{
						if (ServerConfiguration.DEBUG_MODE)
						{
								Misc.print("Item is not defined: " + itemId);
						}
						return true;
				}
				return false;
		}

		/**
		* True, to update equipment. To avoid unnecessary update duo to withdrawing items from bank etc..
		*/
		public static boolean updateEquipment(Player player)
		{
				boolean updated = false;
				for (int equipmentSlot = 0; equipmentSlot < player.playerEquipment.length; equipmentSlot++)
				{
						if (player.playerEquipmentAfterLastTick[equipmentSlot] != player.playerEquipment[equipmentSlot])
						{
								updated = true;
								if (equipmentSlot == ServerConstants.WEAPON_SLOT)
								{
										BotContent.adjustWeaponAttackStyle(player);
										CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
										if (player.getAutoCasting() && player.usingOldAutocast)
										{
												AutoCast.resetAutocast(player);
										}
								}
								if (!player.isBot)
								{
										updateSlot(player, equipmentSlot);
								}
						}
				}
				return updated;
		}

		/**
		 * Save state of equipment after last update.
		 * @param player
		 */
		public static void saveEquipment(Player player)
		{
				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						player.playerEquipmentAfterLastTick[i] = player.playerEquipment[i];
				}
		}


		/**
		 * @param itemId
		 * 			The itemId to check if noted.
		 * @return
		 * 			The un-noted form of the itemId given.
		 */
		public static int getUnNotedItem(int itemId)
		{
				if (itemId >= 20000)
				{
						Misc.print("BUG ALERT CHECK!!!---------------------------Unnoted item: " + itemId);
						return 1;
				}
				if (ItemDefinition.getDefinitions()[itemId] == null)
				{
						return itemId;
				}
				if (Bank.isNotedEnchantedEquipment(itemId))
				{
						itemId -= 3;
				}
				if (ItemDefinition.getDefinitions()[itemId].note)
				{
						itemId -= 1;
				}

				return itemId;
		}

		public static int getNotedItem(int itemId)
		{
				if (itemId == 7398 || itemId == 7399 || itemId == 7400)
				{
						return itemId += 3;
				}
				if (ItemDefinition.getDefinitions()[itemId + 1] == null)
				{
						return itemId;
				}
				if (ItemDefinition.getDefinitions()[itemId + 1].note)
				{
						return itemId += 1;
				}
				return itemId;
		}

		/**
		 * True, if the player is wearing equipment.
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player is wearing equipment.
		 */
		public static boolean hasEquipment(Player player)
		{
				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						if (player.playerEquipment[i] > 0)
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Check if the player has the item in the inventory, if so, then delete the specified amount.
		 * <p>
		 * Only use for stackable items, if not stackable item, it will delete all in inventory.
		 * @param player
		 * @param itemId
		 * @param itemAmount
		 */
		public static boolean checkAndDeleteStackableFromInventory(Player player, int itemId, int itemAmount)
		{
				boolean found = false;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId + 1 && player.playerItemsN[i] >= itemAmount)
						{
								player.playerItemsN[i] -= itemAmount;
								if (player.playerItemsN[i] <= 0)
								{
										player.playerItemsN[i] = 0;
										player.playerItems[i] = 0;
								}
								found = true;
						}
				}
				if (found)
				{
						player.setInventoryUpdate(true);
				}
				return found;
		}

		/**
		 * Give the specified itemId on the ground, under the player.
		 * <p>
		 * Note: the item will not appear instantly to other players.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to show on ground.
		 * @param itemAmount
		 * 			Amount of the item identity to show on ground.
		 */
		public static void giveItemUnderPlayer(Player player, int itemId, int itemAmount)
		{
				Server.itemHandler.createGroundItem(player, itemId, player.getX(), player.getY(), itemAmount, false, 0, true, "");
		}

		/**
		 * @return
		 * 			True, if the player has an untradeable item that can only have 1 quantity on the account.
		 * Such as Max capes, Clue scroll, God capes, Boss pets etc.
		 */
		public static boolean hasSingularUntradeableItem(Player player, int itemId)
		{
				for (int index = 0; index < player.singularUntradeableItemsOwned.size(); index++)
				{
						int itemList = Integer.parseInt(player.singularUntradeableItemsOwned.get(index));
						if (itemId == itemList)
						{
								return true;
						}
				}
				return false;
		}


		public static void pickUpSingularUntradeableItem(Player player, int itemId)
		{
				if (itemId != 2677)
				{
						return;
				}

				if (player.getPlayerName().equals("Arab Unity"))
				{
						NormalCommand.clueScrollDebug.add("Pickup1.");
				}

				if (hasSingularUntradeableItem(player, itemId))
				{
						return;
				}

				if (player.getPlayerName().equals("Arab Unity"))
				{
						NormalCommand.clueScrollDebug.add("Pickup2.");
				}
				player.singularUntradeableItemsOwned.add(Integer.toString(itemId));
		}

		/**
		 * When using this and it drops to ground, Iron men can pick it up.
		 * Give the player an item in the inventory, if the inventory is full, drop the item under the player.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The identity of the item.
		 * @param amount
		 * 			The amount of the item.
		 */
		public static void addItemToInventoryOrDrop(Player player, int itemId, int amount)
		{
				if (ItemAssistant.getFreeInventorySlots(player) > 0)
				{
						ItemAssistant.addItem(player, itemId, amount);
				}
				else if (ItemAssistant.hasItemInInventory(player, itemId) && ItemDefinition.getDefinitions()[itemId].stackable)
				{
						ItemAssistant.addItem(player, itemId, amount);
				}
				else
				{
						giveItemUnderPlayer(player, itemId, amount);
				}
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to search for.
		 * @param equipmentSlot
		 * 			The equipment slot to search in.
		 * @return
		 * 			True, if the player has the itemId equipped in the required equipmentSlot
		 */
		public static boolean hasItemEquippedSlot(Player player, int itemId, int equipmentSlot)
		{
				return player.playerEquipment[equipmentSlot] == itemId;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The itemId to check for in the player's equipped items.
		 * @return
		 * 			True, if the player has the itemId equipped.
		 */
		public static boolean hasItemEquipped(Player player, int itemId)
		{
				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						if (player.playerEquipment[i] == itemId)
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Replace the player's chosen equipment slot with an item.
		 * @param player
		 * 			The associated player.
		 * @param equipmentSlot
		 * 			The equipment slot ID to put the itemId in.
		 * @param itemId
		 * 			The item ID to put in the equipmentSlot
		 */
		public static void replaceEquipmentSlot(Player player, int equipmentSlot, int itemId, int amount, boolean checkIfSlotEmpty, boolean update)
		{
				if (player.playerEquipment[equipmentSlot] <= 0 && checkIfSlotEmpty)
				{
						return;
				}
				player.playerEquipment[equipmentSlot] = itemId;
				player.playerEquipmentN[equipmentSlot] = amount;
				if (itemId <= 0)
				{
						player.playerEquipmentN[equipmentSlot] = 0;
						player.setUpdateRequired(true);
						player.getPA().requestUpdates();
						player.setAppearanceUpdateRequired(true);
				}
				updateSlot(player, equipmentSlot);
				if (update)
				{
						player.setInventoryUpdate(true);
				}
		}

		/**
		 * @param Player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to check.
		 * @return
		 * 			The amount of itemId the player has equipped.
		 */
		public static int getWornItemAmount(Player player, int itemId)
		{
				for (int i = 0; i < 12; i++)
				{
						if (player.playerEquipment[i] == itemId)
						{
								return player.playerEquipmentN[i];
						}
				}
				return 0;
		}

		/**
		 * Update the writeFrame.
		 * <p> Also used for updating the inventory visuals.
		 * @param player
		 * 			The associated player.
		 * @param writeFrame
		 * 			The ID to update, 3214 is inventory.
		 */
		public static void resetItems(Player player, int writeFrame)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrameVarSizeWord(53);
						player.getOutStream().writeWord(writeFrame);
						player.getOutStream().writeWord(player.playerItems.length);
						for (int i = 0; i < player.playerItems.length; i++)
						{
								if (player.playerItemsN[i] > 254)
								{
										player.getOutStream().writeByte(255);
										player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
								}
								else
								{
										player.getOutStream().writeByte(player.playerItemsN[i]);
								}
								player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
						}
						player.getOutStream().endFrameVarSizeWord();
						player.flushOutStream();
						if (writeFrame == 3214)
						{
								player.playerAssistant.sendMessage(":vengeancerunes:"); // Inform client to update vengeance runes interface.
						}
				}
		}

		public static int getInventoryItemAmount1(Player player, int itemId)
		{
				int count = 0;
				itemId++;
				for (int j = 0; j < player.playerItems.length; j++)
				{
						if (player.playerItems[j] == itemId)
						{
								count += player.playerItemsN[j];
						}
				}
				return count;
		}

		/**
		 * Update equipment interface.
		 */
		public static void updateEquipmentBonusInterface(Player player)
		{
				int offset = 0;
				String send = "";
				for (int i = 0; i < player.playerBonus.length; i++)
				{
						send = (player.playerBonus[i] >= 0) ? ServerConstants.EQUIPMENT_BONUS[i] + ": +" + player.playerBonus[i] : ServerConstants.EQUIPMENT_BONUS[i] + ": -" + java.lang.Math.abs(player.playerBonus[i]);
						if (i == 10)
						{
								offset = 1;
						}
						player.getPA().sendFrame126(send, (1675 + i + offset));
				}
				player.getPA().sendFrame126("Ranged strength: +" + RangedFormula.getRangedStrength(player), 15115);
				player.getPA().sendFrame126("Magic damage: +" + (int) (MagicFormula.getMagicPercentageDamageBonus(player) * 100) + "%", 15116);
		}

		/**
		 * Delete all worn items and items in inventory.
		 **/
		public static void deleteAllItems(Player player)
		{
				for (int i1 = 0; i1 < player.playerEquipment.length; i1++)
				{
						deleteEquipment(player, player.playerEquipment[i1], i1);
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						deleteItemFromInventory(player, player.playerItems[i] - 1, getItemSlot(player, player.playerItems[i] - 1), player.playerItemsN[i]);
				}
		}

		/**
		 * Replace the coloured item/imbued ring with a normal version.
		 * @param killer
		 * 			The player who got the kill.
		 * @param victim
		 * 			The player who died.
		 * @param itemId
		 * 			The item identity being dropped by the victim.
		 */
		public static void createNormalItemVersion(Player killer, Player victim, int itemId)
		{

				if (killer.getPlayerName().equals(victim.getPlayerName()))
				{
						return;
				}
				int itemProduced = 0;

				for (ItemColouringData data : ItemColouringData.values())
				{
						if (itemId == data.getBowId())
						{
								itemProduced = ItemColouring.DARK_BOW;
								break;
						}
						if (itemId == data.getWhipId())
						{
								itemProduced = ItemColouring.ABYSSAL_WHIP;
								break;
						}
				}
				if (itemProduced == 0)
				{
						for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values())
						{
								if (itemId == data.getSpecialId())
								{
										itemProduced = data.getNormalId();
										break;
								}
						}
				}

				if (itemProduced > 0)
				{
						Server.itemHandler.createGroundItem(killer, itemProduced, victim.getX(), victim.getY(), 1, killer.getPlayerName() == victim.getPlayerName(), 0, true, victim.getPlayerName());
				}
		}

		/**
		 * @param itemId
		 * 			The item identity to dissapear on death.
		 * @return
		 * 			True if the item will dissapear on death.
		 */
		public static boolean isUntradeableItem(Player player, int itemId, String exception)
		{
				// Exclude imbued rings and ava's.
				if (exception.equals("PLAYER KILLED LOOT"))
				{
						for (ItemColouring.ItemColouringData data : ItemColouring.ItemColouringData.values())
						{
								if (itemId == data.getBowId() || itemId == data.getWhipId())
								{
										return true;
								}
						}
						for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values())
						{
								if (itemId == data.getSpecialId())
								{
										return true;
								}
						}
				}
				if (exception.equals("ITEM TO INVENTORY"))
				{
						for (int j = 0; j < ServerConstants.UNTRADEABLE_ITEMS.length; j++)
						{
								if (itemId == ServerConstants.UNTRADEABLE_ITEMS[j])
								{
										return true;
								}
						}
				}
				if (exception.equals("ITEM TO SHOP"))
				{
						for (int j = 0; j < ServerConstants.ITEMS_DROP_BLOOD_MONEY.length; j++)
						{
								if (itemId == ServerConstants.ITEMS_DROP_BLOOD_MONEY[j])
								{
										return true;
								}
						}
						return false;
				}
				if (exception.equals("PLAYER KILLED LOOT"))
				{
						for (int j = 0; j < ServerConstants.ITEMS_DROP_BLOOD_MONEY.length; j++)
						{
								if (itemId == ServerConstants.ITEMS_DROP_BLOOD_MONEY[j])
								{
										return true;
								}
						}
				}
				if (!exception.equals("DROP ITEM"))
				{
						for (int j = 0; j < ServerConstants.ITEMS_TO_INVENTORY_ON_DEATH.length; j++)
						{
								if (itemId == ServerConstants.ITEMS_TO_INVENTORY_ON_DEATH[j])
								{
										return true;
								}
						}

						for (int index = 0; index < PetData.petData.length; index++)
						{
								if (itemId == PetData.petData[index][1])
								{
										return true;
								}
						}
				}

				for (Skilling.SkillCapeMasterData data : Skilling.SkillCapeMasterData.values())
				{
						if (data.getLegendCapeId() == itemId)
						{
								return true;
						}
						if (data.getUntrimmedSkillCapeId() == itemId)
						{
								return true;
						}
						// Trimmed cape.
						if (data.getUntrimmedSkillCapeId() + 1 == itemId)
						{
								return true;
						}
						// Hood.
						if (data.getUntrimmedSkillCapeId() + 2 == itemId)
						{
								return true;
						}
						if (data.getLegendCapeId() == itemId)
						{
								return true;
						}
				}
				if (!exception.equals("ITEM TO INVENTORY") && Pet.petItem(itemId) && !exception.equals("DROP ITEM"))
				{
						return true;
				}
				return false;
		}

		public static void addItemToInventory(Player player, int itemId, int amount, int slot, boolean update)
		{
				if (itemId <= 0)
				{
						return;
				}
				if (amount <= 0)
				{
						return;
				}
				// Incase item is stackable.
				if (ItemAssistant.hasItemInInventory(player, itemId) && ItemDefinition.getDefinitions()[itemId].stackable)
				{
						ItemAssistant.addItem(player, itemId, amount);
				}
				else
				{
						player.playerItems[slot] = (itemId + 1);
						player.playerItemsN[slot] = amount;
				}

				if (update)
				{
						player.setInventoryUpdate(true);
				}
		}


		public static boolean addItemNoMessage(Player player, int itemId, int amount)
		{
				if (amount < 1)
				{
						amount = 1;
				}
				if (itemId <= 0)
				{
						return false;
				}
				if (getFreeInventorySlots(player) == 0 && !ItemDefinition.getDefinitions()[itemId].stackable)
				{
						return false;
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId + 1 && ItemDefinition.getDefinitions()[itemId].stackable && player.playerItems[i] > 0)
						{
								player.playerItems[i] = (itemId + 1);
								if (player.playerItemsN[i] + amount < ServerConstants.MAX_ITEM_AMOUNT && player.playerItemsN[i] + amount > -1)
								{
										player.playerItemsN[i] += amount;
								}
								else
								{
										return false;
								}
								if (player.getOutStream() != null && player != null && !player.isBot)
								{
										player.setInventoryUpdate(true);
										player.playerAssistant.sendMessage(":vengeancerunes:");
								}
								i = 30;
								return true;
						}
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] <= 0)
						{
								player.playerItems[i] = itemId + 1;
								if (amount < ServerConstants.MAX_ITEM_AMOUNT && amount > -1)
								{
										player.playerItemsN[i] = 1;
										if (amount > 1)
										{
												addItem(player, itemId, amount - 1);
												return true;
										}
								}
								else
										player.playerItemsN[i] = ServerConstants.MAX_ITEM_AMOUNT;
								player.setInventoryUpdate(true);
								i = 30;
								return true;
						}
				}
				return false;
		}

		/**
		 * Add item/s to the player's inventory.
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to add to the inventory.
		 * @param amount
		 * 			The amount of the itemId.
		 * @return
		 * 			True if the player has enough free slots to receive the items.
		 */
		public static boolean addItem(Player player, int itemId, int amount)
		{
				if (amount < 1)
				{
						amount = 1;
				}
				if (itemId <= 0)
				{
						return false;
				}
				if (getFreeInventorySlots(player) == 0 && !ItemDefinition.getDefinitions()[itemId].stackable)
				{
						player.playerAssistant.sendMessage("Not enough inventory space.");
						return false;
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId + 1 && ItemDefinition.getDefinitions()[itemId].stackable && player.playerItems[i] > 0)
						{
								player.playerItems[i] = (itemId + 1);
								if (player.playerItemsN[i] + amount < ServerConstants.MAX_ITEM_AMOUNT && player.playerItemsN[i] + amount > -1)
								{
										player.playerItemsN[i] += amount;
								}
								else
								{
										player.playerAssistant.sendMessage("You have reached the maximum amount of a single item. Cannot add anymore.");
										return false;
								}
								if (player.getOutStream() != null && player != null && !player.isBot)
								{
										player.setInventoryUpdate(true);
										player.playerAssistant.sendMessage(":vengeancerunes:");
								}
								return true;
						}
				}
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] <= 0)
						{
								player.playerItems[i] = itemId + 1;
								if (amount < ServerConstants.MAX_ITEM_AMOUNT && amount > -1)
								{
										player.playerItemsN[i] = 1;
										if (amount > 1)
										{
												addItem(player, itemId, amount - 1);
												return true;
										}
								}
								else
								{
										player.playerItemsN[i] = ServerConstants.MAX_ITEM_AMOUNT;
								}
								player.setInventoryUpdate(true);
								return true;
						}
				}
				player.playerAssistant.sendMessage("Not enough inventory space.");
				return false;
		}


		/**
		 * Calculate the equipment bonuses.
		 */
		public static void calculateEquipmentBonuses(Player player)
		{
				for (int i = 0; i < player.playerBonus.length; i++)
				{
						player.playerBonus[i] = 0;
				}

				for (int i = 0; i < player.playerEquipment.length; i++)
				{
						if (player.playerEquipment[i] > -1)
						{
								int itemId = player.playerEquipment[i];
								if (ItemDefinition.getDefinitions()[itemId] != null)
								{
										for (int k = 0; k < player.playerBonus.length; k++)
										{
												player.playerBonus[k] += ItemDefinition.getDefinitions()[itemId].bonuses[k];
										}
								}
						}
				}
				// Dragonfire shield.
				if (ItemAssistant.hasItemEquippedSlot(player, 11284, ServerConstants.SHIELD_SLOT))
				{
						player.playerBonus[7] += player.dragonFireShieldCharges;
						player.playerBonus[6] += player.dragonFireShieldCharges;
						player.playerBonus[5] += player.dragonFireShieldCharges;
				}
		}

		/**
		 * @param itemName
		 * 			The name of the item to check if it's 2-handed.
		 * @param itemId
		 * 			The ID of the item to check if it's 2-handed.
		 * @return
		 * 			True, if the itemId is a 2-handed weapon.
		 */
		public static boolean is2handed(String itemName, int itemId)
		{
				itemName = itemName.toLowerCase();
				String[] names = {
						"toxic blowpipe",
						"twisted bow",
						"ballista",
						"ahrim",
						"halberd",
						"karil",
						"verac",
						"guthan",
						"dharok",
						"torag",
						"longbow",
						"shortbow",
						"dark bow",
						"godsword",
						"saradomin sword",
						"blessed sword",
						"2h",
						"spear",
						"maul",
						"tzhaar-ket-om",
						"dragon claws",
						"barrelchest anchor",
						"boxing gloves",
						"hand cannon",
						"dragon axe",
						"toktz-mej-tal"};
				for (int i = 0; i < names.length; i++)
				{
						if (itemName.contains(names[i]))
						{
								return true;
						}
				}
				return false;
		}

		public static boolean canWearSpecialItem(Player player, int itemId)
		{
				switch (itemId)
				{
						case 7927:
								Transform.easterEggTransform(player);
								return true;
						// Yo-yo.
						case 4079:
								player.startAnimation(1458);
								return false;

						// Bonemeal, so players cannot wear it.
						case 6810:
								return false;
				}
				return true;
		}

		public static void wearItem(Player player, int itemId, int slot)
		{
				int itemEquipmentSlot = 0;
				if (!canWearSpecialItem(player, itemId))
				{
						return;
				}
				if (player.playerItems[slot] != (itemId + 1))
				{
						return;
				}
				String itemName = getItemName(itemId).toLowerCase();
				ItemRequirement.setItemRequirements(player, itemName, itemId);
				itemEquipmentSlot = ItemSlot.getItemEquipmentSlot(itemName, itemId);

				if (ServerConfiguration.DEBUG_MODE && itemEquipmentSlot == -1)
				{
						Misc.print("Item is not configured: " + itemId);
				}
				// Flag for wearing invalid items like coins from packet abuse.
				if (itemEquipmentSlot == -1)
				{
						PacketHandler.itemLog.add(player.getPlayerName() + " at " + Misc.getDate());
						PacketHandler.itemLog.add("Tried to wear item: " + ItemAssistant.getItemName(itemId) + ", " + itemId);
						return;
				}

				int wearAmount = player.playerItemsN[slot];
				if (wearAmount < 1)
				{
						return;
				}
				if (player.duelRule[11] && itemEquipmentSlot == ServerConstants.HEAD_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing hats has been disabled in this duel!");
						return;
				}
				if (player.duelRule[12] && itemEquipmentSlot == ServerConstants.CAPE_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing capes has been disabled in this duel!");
						return;
				}
				if (player.duelRule[13] && itemEquipmentSlot == ServerConstants.AMULET_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing amulets has been disabled in this duel!");
						return;
				}
				if (player.duelRule[14] && itemEquipmentSlot == ServerConstants.WEAPON_SLOT)
				{
						player.playerAssistant.sendMessage("Wielding weapons has been disabled in this duel!");
						return;
				}
				if (player.duelRule[15] && itemEquipmentSlot == ServerConstants.BODY_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing bodies has been disabled in this duel!");
						return;
				}
				if ((player.duelRule[16] && itemEquipmentSlot == ServerConstants.SHIELD_SLOT) || (player.duelRule[16] && is2handed(itemName, itemId)))
				{
						player.playerAssistant.sendMessage("Wearing shield has been disabled in this duel!");
						return;
				}
				if (player.duelRule[17] && itemEquipmentSlot == ServerConstants.LEG_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing legs has been disabled in this duel!");
						return;
				}
				if (player.duelRule[18] && itemEquipmentSlot == ServerConstants.HAND_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing gloves has been disabled in this duel!");
						return;
				}
				if (player.duelRule[19] && itemEquipmentSlot == ServerConstants.FEET_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing boots has been disabled in this duel!");
						return;
				}
				if (player.duelRule[20] && itemEquipmentSlot == ServerConstants.RING_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing rings has been disabled in this duel!");
						return;
				}
				if (player.duelRule[21] && itemEquipmentSlot == ServerConstants.ARROW_SLOT)
				{
						player.playerAssistant.sendMessage("Wearing arrows has been disabled in this duel!");
						return;
				}

				// Aby whip and dds only.
				if (player.duelRule[9] && itemEquipmentSlot == ServerConstants.WEAPON_SLOT)
				{
						if (itemId != 4151 && itemId != 1215 && itemId != 5698)
						{
								player.playerAssistant.sendMessage("You can only wear Abyssal whip and Dragon daggers in this duel!");
								return;
						}
				}
				if (!ItemRequirement.hasItemRequirement(player, itemEquipmentSlot))
				{
						return;
				}

				if (!AchievementShop.hasAchievementItemRequirements(player, itemId, false, true))
				{
						return;
				}
				int extraItem = 0;
				int extraItemAmount = 0;
				if (slot >= 0 && itemId >= 0)
				{
						int toEquipN = player.playerItemsN[slot];
						if ((player.getWieldedWeapon() == 4151 || player.getWieldedWeapon() == 18767) && player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								player.wasWearingAggressiveWhip = true;
						}
						// If player is weilding trident and is switching to not trident.
						else if (player.getWieldedWeapon() == 18769 && itemId != 18769)
						{
								player.setAutocastId(-1);
								player.setAutoCasting(false);
						}
						player.setInventoryUpdate(true);
						int toEquip = player.playerItems[slot];
						int toRemove = player.playerEquipment[itemEquipmentSlot];
						int toRemoveN = player.playerEquipmentN[itemEquipmentSlot];
						if (toEquip == toRemove + 1 && ItemDefinition.getDefinitions()[toRemove].stackable)
						{
								int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
								if (toEquipN > maximumAmount)
								{
										player.getPA().sendMessage("Your item stack cannot hold anymore!");
										return;
								}
								deleteItemFromInventory(player, toRemove, getItemSlot(player, toRemove), toEquipN);
								player.playerEquipmentN[itemEquipmentSlot] += toEquipN;
						}
						else if (itemEquipmentSlot != ServerConstants.SHIELD_SLOT && itemEquipmentSlot != ServerConstants.WEAPON_SLOT)
						{
								int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
								if (toEquipN > maximumAmount)
								{
										player.getPA().sendMessage("Your item stack cannot hold anymore!");
										return;
								}
								boolean added = false;
								if (!ItemAssistant.nulledItem(toRemove))
								{
										if (ItemDefinition.getDefinitions()[toRemove].stackable)
										{
												if (ItemAssistant.hasItemInInventory(player, toRemove))
												{
														player.playerItems[slot] = 0;
														player.playerItemsN[slot] = 0;
														ItemAssistant.addItem(player, toRemove, toRemoveN);
														added = true;
												}
										}
								}
								if (!added)
								{
										player.playerItems[slot] = toRemove + 1;
										player.playerItemsN[slot] = toRemoveN;
								}
								player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
								player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
						}
						else if (itemEquipmentSlot == ServerConstants.SHIELD_SLOT)
						{
								int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
								if (toEquipN > maximumAmount)
								{
										player.getPA().sendMessage("Your item stack cannot hold anymore!");
										return;
								}
								boolean wearing2h = is2handed(ItemAssistant.getItemName(player.getWieldedWeapon()), player.getWieldedWeapon());
								if (wearing2h)
								{
										toRemove = player.getWieldedWeapon();
										toRemoveN = player.playerEquipmentN[ServerConstants.WEAPON_SLOT];
										player.playerEquipment[ServerConstants.WEAPON_SLOT] = -1;
										player.playerEquipmentN[ServerConstants.WEAPON_SLOT] = 0;
								}
								player.playerItems[slot] = toRemove + 1;
								player.playerItemsN[slot] = toRemoveN;
								player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
								player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
						}
						else if (itemEquipmentSlot == ServerConstants.WEAPON_SLOT)
						{
								boolean is2h = is2handed(itemName, itemId);
								boolean wearingShield = player.playerEquipment[ServerConstants.SHIELD_SLOT] > 0;
								boolean wearingWeapon = player.getWieldedWeapon() > 0;
								if (is2h)
								{
										if (wearingShield && wearingWeapon)
										{
												if (getFreeInventorySlots(player) > 0)
												{
														int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
														if (toEquipN > maximumAmount)
														{
																player.getPA().sendMessage("Your item stack cannot hold anymore!");
																return;
														}
														boolean added = false;
														//Prevent 2 stacks of an item, to prevent dupes.
														if (!ItemAssistant.nulledItem(toRemove))
														{
																if (ItemDefinition.getDefinitions()[toRemove].stackable)
																{
																		if (ItemAssistant.hasItemInInventory(player, toRemove))
																		{
																				player.playerItems[slot] = 0;
																				player.playerItemsN[slot] = 0;
																				ItemAssistant.addItem(player, toRemove, toRemoveN);
																				added = true;
																		}
																}
														}
														if (!added)
														{
																player.playerItems[slot] = toRemove + 1;
																player.playerItemsN[slot] = toRemoveN;
														}
														player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
														player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
														removeItem(player, player.playerEquipment[ServerConstants.SHIELD_SLOT], ServerConstants.SHIELD_SLOT);
												}
										}
										else if (wearingShield && !wearingWeapon)
										{
												int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
												if (toEquipN > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return;
												}
												player.playerItems[slot] = player.playerEquipment[ServerConstants.SHIELD_SLOT] + 1;
												player.playerItemsN[slot] = player.playerEquipmentN[ServerConstants.SHIELD_SLOT];
												player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
												player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
												player.playerEquipment[ServerConstants.SHIELD_SLOT] = -1;
												player.playerEquipmentN[ServerConstants.SHIELD_SLOT] = 0;
										}
										else
										{
												int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
												if (toEquipN > maximumAmount)
												{
														player.getPA().sendMessage("Your item stack cannot hold anymore!");
														return;
												}
												boolean added = false;
												//Prevent 2 stacks of an item, to prevent dupes.
												if (!ItemAssistant.nulledItem(toRemove))
												{
														if (ItemDefinition.getDefinitions()[toRemove].stackable)
														{
																if (ItemAssistant.hasItemInInventory(player, toRemove))
																{
																		player.playerItems[slot] = 0;
																		player.playerItemsN[slot] = 0;
																		ItemAssistant.addItem(player, toRemove, toRemoveN);
																		added = true;
																}
														}
												}
												if (!added)
												{
														player.playerItems[slot] = toRemove + 1;
														player.playerItemsN[slot] = toRemoveN;
												}
												player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
												player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
										}
								}
								else
								{
										int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
										if (toEquipN > maximumAmount)
										{
												player.getPA().sendMessage("Your item stack cannot hold anymore!");
												return;
										}
										boolean added = false;
										//Prevent 2 stacks of an item, to prevent dupes.
										if (!ItemAssistant.nulledItem(toRemove))
										{
												if (ItemDefinition.getDefinitions()[toRemove].stackable)
												{
														if (ItemAssistant.hasItemInInventory(player, toRemove))
														{
																player.playerItems[slot] = 0;
																player.playerItemsN[slot] = 0;
																ItemAssistant.addItem(player, toRemove, toRemoveN);
																added = true;
														}
												}
										}
										if (!added)
										{
												player.playerItems[slot] = toRemove + 1;
												player.playerItemsN[slot] = toRemoveN;
										}
										player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
										player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
										added = false;
								}
						}
				}

				if (itemEquipmentSlot == ServerConstants.WEAPON_SLOT)
				{
						player.setUsingSpecial(false);
						player.botUsedSpecialAttack = false;
				}
				if (extraItem > 0)
				{
						ItemAssistant.addItem(player, extraItem, extraItemAmount);
				}
				DonatorFeatures.resetAfk(player, false);
				Combat.updatePlayerStance(player);
				player.switches++;
				player.itemWorn = true;
		}

		public static void updateSlot(Player player, int slot)
		{
				if (player.isBot)
				{
						return;
				}
				if (player.getOutStream() != null && player != null)
				{
						player.getOutStream().createFrameVarSizeWord(34);
						player.getOutStream().writeWord(1688);
						player.getOutStream().writeByte(slot);
						player.getOutStream().writeWord(player.playerEquipment[slot] + 1);
						if (player.playerEquipmentN[slot] > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord(player.playerEquipmentN[slot]);
						}
						else
						{
								player.getOutStream().writeByte(player.playerEquipmentN[slot]);
						}
						player.getOutStream().endFrameVarSizeWord();
						player.flushOutStream();
				}
		}

		/**
		 * Remove Item from equipment tab
		 **/
		public static void removeItem(Player player, int wearID, int slot)
		{
				if (wearID == 7927 || wearID == 4024 || wearID == 4084)
				{
						Transform.unTransform(player);
				}
				if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting())
				{
						return;
				}
				if (!ItemAssistant.hasItemEquipped(player, wearID))
				{
						return;
				}
				if (player.usedGmaul)
				{
						player.usedGmaul = false;
				}
				if (player.playerEquipment[slot] > -1)
				{
						if (addItem(player, player.playerEquipment[slot], player.playerEquipmentN[slot]))
						{
								if (player.getAutoCasting() && player.usingOldAutocast && slot == ServerConstants.WEAPON_SLOT)
								{
										AutoCast.resetAutocast(player);
								}
								player.switches++;
								player.playerEquipment[slot] = -1;
								player.playerEquipmentN[slot] = 0;
								player.setInventoryUpdate(true);
								Combat.updatePlayerStance(player);
								player.setUpdateRequired(true);
								player.setAppearanceUpdateRequired(true);
								player.itemWorn = true;
						}
				}
		}

		public static void sendFrame34(Player player, int item, int amount, int slot, int frame)
		{
				if (player.getOutStream() == null)
				{
						return;
				}
				player.getOutStream().createFrameVarSizeWord(34);
				player.getOutStream().writeWord(frame);
				player.getOutStream().writeByte(slot);
				player.getOutStream().writeWord(item + 1);
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(amount);
				player.getOutStream().endFrameVarSizeWord();
		}

		public static int itemAmount(Player player, int itemId)
		{
				int tempAmount = 0;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId)
						{
								tempAmount += player.playerItemsN[i];
						}
				}
				return tempAmount;
		}

		public static boolean isStackable(int itemId)
		{
				return ItemDefinition.getDefinitions()[itemId].stackable;
		}

		/**
		 * Update the equipment interface to show the items in the slots.
		 */
		public static void updateEquipmentInterface(Player player)
		{
				setEquipment(player, player.playerEquipment[ServerConstants.HEAD_SLOT], 1, ServerConstants.HEAD_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.CAPE_SLOT], 1, ServerConstants.CAPE_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.AMULET_SLOT], 1, ServerConstants.AMULET_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.ARROW_SLOT], player.playerEquipmentN[ServerConstants.ARROW_SLOT], ServerConstants.ARROW_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.BODY_SLOT], 1, ServerConstants.BODY_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.SHIELD_SLOT], 1, ServerConstants.SHIELD_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.LEG_SLOT], 1, ServerConstants.LEG_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.HAND_SLOT], 1, ServerConstants.HAND_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.FEET_SLOT], 1, ServerConstants.FEET_SLOT);
				setEquipment(player, player.playerEquipment[ServerConstants.RING_SLOT], 1, ServerConstants.RING_SLOT);
				setEquipment(player, player.getWieldedWeapon(), player.playerEquipmentN[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT);
		}

		/**
		 * Update Equip tab
		 **/
		public static void setEquipment(Player player, int wearID, int amount, int targetSlot)
		{
				player.playerEquipment[targetSlot] = wearID;
				player.playerEquipmentN[targetSlot] = amount;
				if (player.isBot)
				{
						return;
				}
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}

		/**
		 * Move Items
		 **/
		public static void moveItems(Player player, int from, int to, int moveWindow, byte insert)
		{
				if (moveWindow == 5382 && from >= 0 && to >= 0 && from < ServerConstants.BANK_SIZE && to < ServerConstants.BANK_SIZE && to < ServerConstants.BANK_SIZE)
				{
						if (Bank.hasBankingRequirements(player, false))
						{
								if (insert == 0)
								{
										int tempI;
										int tempN;
										tempI = player.bankingItems[from];
										tempN = player.bankingItemsN[from];
										player.bankingItems[from] = player.bankingItems[to];
										player.bankingItemsN[from] = player.bankingItemsN[to];
										player.bankingItems[to] = tempI;
										player.bankingItemsN[to] = tempN;
										Bank.openUpBank(player, player.bankingTab, true, false);
								}
								else if (insert == 1)
								{
										int tempFrom = from;
										for (int tempTo = to; tempFrom != tempTo;)
												if (tempFrom > tempTo)
												{
														Bank.swapBankItem(player, tempFrom, tempFrom - 1);
														tempFrom--;
												}
												else if (tempFrom < tempTo)
												{
														Bank.swapBankItem(player, tempFrom, tempFrom + 1);
														tempFrom++;
												}
										Bank.openUpBank(player, player.bankingTab, true, false);
								}
								if (moveWindow == 5382)
								{
										Bank.resetBank(player, false);
								}
								player.bankUpdated = true;
						}
				}
				if (moveWindow == 3214)
				{
						int tempI;
						int tempN;
						tempI = player.playerItems[from];
						tempN = player.playerItemsN[from];
						player.playerItems[from] = player.playerItems[to];
						player.playerItemsN[from] = player.playerItemsN[to];
						player.playerItems[to] = tempI;
						player.playerItemsN[to] = tempN;
						player.setInventoryUpdate(true);
				}
				if (moveWindow == 18579 || moveWindow == 5064)
				{
						int tempI;
						int tempN;
						tempI = player.playerItems[from];
						tempN = player.playerItemsN[from];
						player.playerItems[from] = player.playerItems[to];
						player.playerItemsN[from] = player.playerItemsN[to];
						player.playerItems[to] = tempI;
						player.playerItemsN[to] = tempN;
						player.setInventoryUpdate(true);
						ItemAssistant.resetItems(player, 5064); // Spawning items while banking.
				}
		}

		/**
		 * delete Item
		 **/
		public static void deleteEquipment(Player player, int itemId, int itemSlot)
		{
				if (PlayerHandler.players[player.getPlayerId()] == null)
				{
						return;
				}
				if (itemId < 0)
				{
						return;
				}
				player.playerEquipment[itemSlot] = -1;
				player.playerEquipmentN[itemSlot] = player.playerEquipmentN[itemSlot] - 1;
				player.setInventoryUpdate(true);
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}

		/**
		 * Search all equipment slots and delete the item id.
		 */
		public static void deleteEquipment(Player player, int itemId)
		{
				if (PlayerHandler.players[player.getPlayerId()] == null)
				{
						return;
				}
				if (itemId < 0)
				{
						return;
				}
				for (int index = 0; index < player.playerEquipment.length; index++)
				{
						if (player.playerEquipment[index] != itemId)
						{
								continue;
						}
						player.playerEquipment[index] = -1;
						player.playerEquipmentN[index] = 0;
						if (player.isBot)
						{
								return;
						}
						player.setInventoryUpdate(true);
						player.setUpdateRequired(true);
						player.setAppearanceUpdateRequired(true);
						break;
				}
		}

		public static void deleteItemFromInventory(Player player, int itemId, int amount)
		{
				if (itemId <= 0)
				{
						return;
				}
				if (ItemDefinition.getDefinitions()[itemId].stackable)
				{
						deleteStackableItemFromInventory(player, itemId, amount);
						return;
				}
				for (int j = 0; j < player.playerItems.length; j++)
				{
						if (player.playerItems[j] == itemId + 1)
						{
								player.playerItems[j] = 0;
								player.playerItemsN[j] = 0;
								amount--;
								if (amount == 0)
								{
										break;
								}
						}
				}
				player.setInventoryUpdate(true);
		}

		/**
		 * Find the item in the inventory and delete it.
		 */
		public static void deleteStackableItemFromInventory(Player player, int id, int amount)
		{
				deleteItemFromInventory(player, id, getItemSlot(player, id), amount);
		}

		public static void deleteItemFromInventory(Player player, int id, int slot, int amount)
		{
				if (id <= 0 || slot < 0)
				{
						return;
				}
				if (player.playerItems[slot] == (id + 1))
				{
						if (player.playerItemsN[slot] > amount)
						{
								player.playerItemsN[slot] -= amount;
						}
						else
						{
								player.playerItemsN[slot] = 0;
								player.playerItems[slot] = 0;
						}
						player.setInventoryUpdate(true);
				}
		}

		public static void deleteEquipment(Player player)
		{

				if (RangedData.hasAvaRelatedItem(player) && Misc.hasPercentageChance(80) && player.playerEquipment[ServerConstants.ARROW_SLOT] != 4740 && player.playerEquipment[ServerConstants.ARROW_SLOT] != 15243)
				{
						return;
				}

				if (player.playerEquipmentN[ServerConstants.WEAPON_SLOT] == 1)
				{
						deleteEquipment(player, player.getWieldedWeapon(), ServerConstants.WEAPON_SLOT);
				}
				if (player.playerEquipmentN[ServerConstants.WEAPON_SLOT] != 0)
				{
						player.playerEquipmentN[ServerConstants.WEAPON_SLOT] -= 1;
				}
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}

		/**
		 * Dropping Arrows
		 **/
		public static void dropArrowNpc(Player player)
		{
				// Toxic blowpipe.
				if (player.getWieldedWeapon() == 18779)
				{
						return;
				}
				if (!player.getAmmoDropped())
				{
						return;
				}



				if (RangedData.hasAvaRelatedItem(player))
				{
						if (Misc.hasPercentageChance(70))
						{
								return;
						}
				}
				else
				{
						if (Misc.hasPercentageChance(15))
						{
								return;
						}
				}
				int enemyX = NpcHandler.npcs[player.getOldNpcIndex()].getVisualX();
				int enemyY = NpcHandler.npcs[player.getOldNpcIndex()].getVisualY();
				if (Server.itemHandler.itemAmount(player.getDroppedRangedWeaponUsed(), enemyX, enemyY) == 0)
				{
						Server.itemHandler.createGroundItem(player, player.getDroppedRangedWeaponUsed(), enemyX, enemyY, 1, false, 0, false, "");
				}
				else if (Server.itemHandler.itemAmount(player.getDroppedRangedWeaponUsed(), enemyX, enemyY) != 0)
				{
						int amount = Server.itemHandler.itemAmount(player.getDroppedRangedWeaponUsed(), enemyX, enemyY);
						Server.itemHandler.removeGroundItem(player, player.getDroppedRangedWeaponUsed(), enemyX, enemyY, false);
						Server.itemHandler.createGroundItem(player, player.getDroppedRangedWeaponUsed(), enemyX, enemyY, amount + 1, false, 0, false, "");
				}
		}

		public static void removeAllItems(Player player)
		{
				for (int i = 0; i < player.playerItems.length; i++)
				{
						player.playerItems[i] = 0;
				}
				for (int i = 0; i < player.playerItemsN.length; i++)
				{
						player.playerItemsN[i] = 0;
				}
				player.setInventoryUpdate(true);
		}

		public static int getFreeInventorySlots(Player player)
		{
				int freeS = 0;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if ((player.playerItems[i] - 1) <= 0)
						{
								freeS++;
						}
				}
				return freeS;
		}

		/**
		 * @return
		 * 		True if the player has enough inventory slots. If false, it will send a message to the player.
		 */
		public static boolean hasInventorySlotsAlert(Player player, int amount)
		{
				if (getFreeInventorySlots(player) >= amount)
				{
						return true;
				}
				player.getPA().sendMessage("You need at least " + amount + " inventory slots to continue.");
				return false;
		}

		public static String getItemName(int itemId)
		{
				if (itemId <= 0)
				{
						return "Unarmed";
				}
				if (ItemDefinition.getDefinitions()[itemId] != null)
				{
						return ItemDefinition.getDefinitions()[itemId].name;
				}
				return "Unarmed";
		}

		public static int getItemId(String itemName)
		{
				for (int i = 0; i < ServerConstants.MAX_ITEM_ID; i++)
				{
						if (ItemDefinition.getDefinitions()[i] != null)
						{
								if (ItemDefinition.getDefinitions()[i].name.equalsIgnoreCase(itemName))
								{
										return i;
								}
						}
				}
				return -1;
		}

		public static int getItemSlot(Player player, int itemId)
		{
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if ((player.playerItems[i] - 1) == itemId)
						{
								return i;
						}
				}
				return -1;
		}

		public static int getItemAmount(Player player, int itemId)
		{
				int itemCount = 0;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if ((player.playerItems[i] - 1) == itemId)
						{
								itemCount += player.playerItemsN[i];
						}
				}
				return itemCount;
		}

		public static int getItemAmount(Player player, int itemId, int slot)
		{
				int itemCount = 0;
				if ((player.playerItems[slot] - 1) == itemId)
				{
						itemCount += player.playerItemsN[slot];
				}
				return itemCount;
		}

		public static boolean playerHasItem(Player player, int itemId, int amt, int slot)
		{
				itemId++;
				int found = 0;
				if (player.playerItems[slot] == (itemId))
				{
						for (int i = 0; i < player.playerItems.length; i++)
						{
								if (player.playerItems[i] == itemId)
								{
										if (player.playerItemsN[i] >= amt)
										{
												return true;
										}
										else
										{
												found++;
										}
								}
						}
						if (found >= amt)
						{
								return true;
						}
						return false;
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemId
		 * 			The item identity to search for.
		 * @return
		 * 			True, if the player has the item in inventory.
		 */
		public static boolean hasItemInInventory(Player player, int itemId)
		{
				itemId++;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId)
						{
								return true;
						}
				}
				return false;
		}

		public static boolean hasItemAmountInInventory(Player player, int itemId, int amount)
		{
				itemId++;
				int found = 0;
				for (int i = 0; i < player.playerItems.length; i++)
				{
						if (player.playerItems[i] == itemId)
						{
								if (player.playerItemsN[i] >= amount)
								{
										return true;
								}
								else
								{
										found++;
								}
						}
				}
				if (found >= amount)
				{
						return true;
				}
				return false;
		}

		/**
		 * Drop Item
		 **/
		public static void createGroundItem(Player player, int itemId, int itemX, int itemY, int itemAmount)
		{
				if (player == null)
				{
						return;
				}
				if (player.isBot)
				{
						return;
				}
				itemY = (itemY - 8 * player.mapRegionY);
				itemX = (itemX - 8 * player.mapRegionX);
				player.sendGroundItemPacket.add(itemY + " " + itemX + " " + itemId + " " + itemAmount);
				if (player.getOutStream() == null)
				{
						return;
				}
				/*
				player.getOutStream().createFrame(85);
				player.getOutStream().writeByteC(itemY);
				player.getOutStream().writeByteC(itemX);
				player.getOutStream().createFrame(44);
				player.getOutStream().writeWordBigEndianA(itemId);
				player.getOutStream().writeWord(itemAmount);
				player.getOutStream().writeByte(0);
				player.flushOutStream();
				*/

		}

		public static void sendGroundItemCustompacket(Player player)
		{
				int count = 0;
				String data = "";
				if (player.sendGroundItemPacket.isEmpty())
				{
						return;
				}
				for (int index = 0; index < player.sendGroundItemPacket.size(); index++)
				{
						count++;
						data = data + player.sendGroundItemPacket.get(index) + "-";
						if (count == 7)
						{
								player.getPA().sendMessage(":packet:showgrounditem:" + data);
								count = 0;
								data = "";
						}
				}
				if (!data.isEmpty())
				{
						player.getPA().sendMessage(":packet:showgrounditem:" + data);
				}
				player.sendGroundItemPacket.clear();
		}

		public static void sendGroundItemRemoveCustompacket(Player player)
		{
				int count = 0;
				String data = "";
				if (player.sendGroundItemPacketRemove.isEmpty())
				{
						return;
				}
				for (int index = 0; index < player.sendGroundItemPacketRemove.size(); index++)
				{
						count++;
						data = data + player.sendGroundItemPacketRemove.get(index) + "-";
						if (count == 7)
						{
								player.getPA().sendMessage(":packet:removegrounditem:" + data);
								count = 0;
								data = "";
						}
				}
				if (!data.isEmpty())
				{
						player.getPA().sendMessage(":packet:removegrounditem:" + data);
				}
				player.sendGroundItemPacketRemove.clear();
		}

		/**
		 * Pickup Item
		 **/
		public static void removeGroundItem(Player player, int itemId, int itemX, int itemY, int Amount)
		{
				if (player == null)
				{
						return;
				}
				if (player.isBot)
				{
						return;
				}
				itemY = (itemY - 8 * player.mapRegionY);
				itemX = (itemX - 8 * player.mapRegionX);
				player.sendGroundItemPacketRemove.add(itemY + " " + itemX + " " + itemId + " " + Amount);
				if (player.getOutStream() == null)
				{
						return;
				}
				/*
				player.getOutStream().createFrame(85);
				player.getOutStream().writeByteC((itemY - 8 * player.mapRegionY));
				player.getOutStream().writeByteC((itemX - 8 * player.mapRegionX));
				player.getOutStream().createFrame(156);
				player.getOutStream().writeByteS(0);
				player.getOutStream().writeWord(itemId);
				player.flushOutStream();
				*/
		}

		/**
		 * True if the player has an item equipped in the given slot.
		 */
		public static boolean hasItemInEquipmentSlot(Player player, int slot)
		{
				if (player.playerEquipment[slot] > 0 && player.playerEquipmentN[slot] > 0)
				{
						return true;
				}
				return false;
		}

		public static void updateEquipmentLogIn(Player player)
		{
				for (int equipmentSlot = 0; equipmentSlot < player.playerEquipment.length; equipmentSlot++)
				{
						updateSlot(player, equipmentSlot);
				}

		}
}