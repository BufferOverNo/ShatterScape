package game.item;

import java.util.ArrayList;

import game.player.Player;

/**
 * @author Sanity
 */

public class PotionCombining
{

		public static boolean hasPotionsToCombine(Player player)
		{
				boolean hasTwoSameTypePotion = false;
				ArrayList<String> potionNames = new ArrayList<String>();
				ArrayList<String> potionNamesDetailed = new ArrayList<String>();
				for (int index = 0; index < 28; index++)
				{
						int item = player.playerItems[index] - 1;
						int slot = index;
						if (item <= 0)
						{
								continue;
						}
						String itemName = ItemDefinition.getDefinitions()[item].name.toLowerCase();

						if (!itemName.contains("("))
						{
								continue;
						}
						if (itemName.contains("watering") || itemName.contains("glory"))
						{
								continue;
						}
						if (!itemName.contains("1") && !itemName.contains("2") && !itemName.contains("3"))
						{
								continue;
						}
						if (itemName.contains("4"))
						{
								continue;
						}

						String name = itemName.substring(0, itemName.indexOf("("));
						for (int index1 = 0; index1 < potionNames.size(); index1++)
						{
								if (potionNames.get(index1).equals(name))
								{
										hasTwoSameTypePotion = true;
										player.potions.add(item + " " + slot);
										player.potions.add(potionNamesDetailed.get(index1));
										break;
								}
						}
						potionNames.add(name);
						potionNamesDetailed.add(item + " " + slot);
				}
				if (player.potions.isEmpty() || !hasTwoSameTypePotion)
				{
						return false;
				}
				return true;
		}

		public static void combineAllPotions(Player player)
		{
				if (!hasPotionsToCombine(player))
				{
						return;
				}

				String potionName1 = "";
				ArrayList<String> potionList1 = new ArrayList<String>();

				for (int index = 0; index < player.potions.size(); index++)
				{
						String[] args = player.potions.get(index).split(" ");
						String fullName = ItemDefinition.getDefinitions()[Integer.parseInt(args[0])].name;
						String name = fullName.substring(0, fullName.indexOf("("));
						if (potionName1.isEmpty() || potionName1.equals(name) && potionList1.isEmpty())
						{
								potionName1 = name;
								potionList1.add(player.potions.get(index));
								continue;
						}

						if (name.equals(potionName1))
						{
								String[] args1 = potionList1.get(0).split(" ");
								PotionCombining.combinePotion(player, false, Integer.parseInt(args[0]), Integer.parseInt(args1[0]), Integer.parseInt(args[1]), Integer.parseInt(args1[1]), true);
								potionList1.clear();
								continue;
						}
				}

				player.potions.clear();
				player.potionCombineLoops++;
				if (player.potionCombineLoops > 20)
				{
						player.potionCombineLoops = 0;
						return;
				}
				combineAllPotions(player);
		}

		public static void combinePotion(Player player, boolean message, int id, int id2, int slot1, int slot2, boolean automatic)
		{
				if (player.getHeight() == 20 && ItemAssistant.getItemName(id).contains("brew"))
				{
						player.getPA().sendMessage("You cannot combine brews around here.");
						return;
				}
				if (automatic)
				{
						if (!ItemAssistant.playerHasItem(player, id, 1, slot1))
						{
								return;
						}
						if (!ItemAssistant.playerHasItem(player, id2, 1, slot2))
						{
								return;
						}
				}
				if (ItemDefinition.getDefinitions()[id].note)
				{
						return;
				}
				if (ItemDefinition.getDefinitions()[id2].note)
				{
						return;
				}
				String id11 = ItemAssistant.getItemName(id);
				String id22 = ItemAssistant.getItemName(id2);
				if (!id11.contains("(1)") && !id11.contains("(2)") && !id11.contains("(3)") && !id11.contains("(4)"))
				{
						return;
				}
				if (!id22.contains("(1)") && !id22.contains("(2)") && !id22.contains("(3)") && !id22.contains("(4)"))
				{
						return;
				}
				if (id11.substring(0, id11.indexOf("(")).equalsIgnoreCase(id22.substring(0, id22.indexOf("("))))
				{
						try
						{
								int amount1 = Integer.parseInt(id11.substring(id11.indexOf("(") + 1, id11.indexOf("(") + 2));
								int amount2 = Integer.parseInt(id22.substring(id22.indexOf("(") + 1, id22.indexOf("(") + 2));
								int totalAmount = amount1 + amount2;
								if (totalAmount > 4)
								{
										amount1 = 4;
										amount2 = totalAmount - 4;
										String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
										String item2 = id11.substring(0, id11.indexOf("(") + 1) + amount2 + ")";
										player.potionDecanted = true;
										ItemAssistant.deleteItemFromInventory(player, id, slot1, 1);
										ItemAssistant.deleteItemFromInventory(player, id2, slot2, 1);
										ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item1), 1, slot1, false);
										ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item2), 1, slot2, true);
										if (message)
										{
												player.playerAssistant.sendFilterableMessage("You combine the potion.");
										}
								}
								else
								{
										amount1 = totalAmount;
										String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
										player.potionDecanted = true;
										ItemAssistant.deleteItemFromInventory(player, id, slot1, 1);
										ItemAssistant.deleteItemFromInventory(player, id2, slot2, 1);
										ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item1), 1, slot1, false);
										ItemAssistant.addItemToInventory(player, 229, 1, slot2, true);
										if (message)
										{
												player.playerAssistant.sendFilterableMessage("You combine the potion.");
										}
								}
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}
		}

		public static void decantAllPotions(Player player)
		{
				player.potionCombineLoops = 0;
				PotionCombining.combineAllPotions(player);
				if (player.potionDecanted)
				{
						player.getPA().sendMessage("The shopkeeper decants all the potions for you.");
				}
				else
				{
						player.getPA().sendMessage("No potions available to decant.");
				}
				player.potionDecanted = false;
		}

}