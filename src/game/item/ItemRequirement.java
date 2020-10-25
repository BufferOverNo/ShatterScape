package game.item;

import core.ServerConstants;
import game.player.Player;

/**
 * Item requirements.
 * @author MGT Madness, created on 12-10-2015.
 */
public class ItemRequirement
{

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemEquipmentSlot
		 * 			The equipment slot used by the item.
		 * @return
		 * 			True, if the player has the level requirements to wear the item.
		 */
		public static boolean hasItemRequirement(Player player, int itemEquipmentSlot)
		{
				for (int i = 0; i < player.itemRequirement.length; i++)
				{
						if (player.baseSkillLevel[i] < player.itemRequirement[i])
						{
								String verb = itemEquipmentSlot == ServerConstants.WEAPON_SLOT ? "wield" : "wear";
								player.playerAssistant.sendMessage("You need a " + ServerConstants.SKILL_NAME[i] + " level of " + player.itemRequirement[i] + " to " + verb + " this item.");
								return false;
						}
				}

				return true;
		}

		/**
		* Set the item requirement variables.
		* @param player
		* 			The associated player.
		* @param itemName
		* 			The name of the item that will have it's item requirement variables set.
		* @param itemId
		* 			The identity of the item that will have it's item requirement variables set.
		**/
		public static void setItemRequirements(Player player, String itemName, int itemId)
		{
				for (int i = 0; i < player.itemRequirement.length; i++)
				{
						player.itemRequirement[i] = 1;
				}

				if (itemId >= 2653 && itemId <= 2676 || itemName.contains("gilded") || itemId >= 3476 && itemId <= 3489) // All God rune armour such as Zamorak platebody.
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}
				if (itemId >= 10368 && itemId <= 10391)
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
						player.itemRequirement[ServerConstants.RANGED] = 70;
				}
				if (itemId >= 10547 && itemId <= 10555) // All penance queen minigame 40 defence items.
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}
				else if (itemName.equals("ava's accumulator"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 50;
				}
				else if (itemName.equals("heavy ballista"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 75;
				}
				else if (itemName.equals("dragon warhammer"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 60;
				}
				else if (itemName.contains("granite") && !itemName.contains("maul"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 50;
						player.itemRequirement[ServerConstants.STRENGTH] = 50;
				}
				else if (itemName.contains("snakeskin"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 30;
						player.itemRequirement[ServerConstants.RANGED] = 30;
				}
				else if (itemName.equals("elder maul"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 75;
						player.itemRequirement[ServerConstants.STRENGTH] = 75;
				}
				else if (itemName.equals("eternal boots"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 75;
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
				}
				else if (itemName.equals("primordial boots"))
				{
						player.itemRequirement[ServerConstants.STRENGTH] = 75;
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
				}
				else if (itemName.equals("pegasian boots"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 75;
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
				}
				else if (itemName.equals("dragon dart"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 60;
				}
				else if (itemName.equals("trident of the swamp"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 75;
				}
				else if (itemName.equals("dragon hunter crossbow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 65;
				}
				else if (itemName.equals("hunters' crossbow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 50;
				}
				else if (itemName.contains("abyssal dagger"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 70;
				}
				else if (itemName.equals("twisted buckler"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 75;
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
				}
				else if (itemName.equals("abyssal bludgeon"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 70;
						player.itemRequirement[ServerConstants.STRENGTH] = 70;
				}
				else if (itemName.equals("necklace of anguish") || itemName.equals("amulet of torture"))
				{
						player.itemRequirement[ServerConstants.HITPOINTS] = 75;
				}
				else if (itemName.equals("occult necklace"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 70;
				}
				else if (itemName.equals("trident of the swamp"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 75;
				}
				else if (itemName.equals("toxic blowpipe"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 75;
				}
				else if (itemName.equals("dragon javelin"))
				{
				}
				else if (itemName.equals("serpentine helm") || itemName.equals("magma helm") || itemName.equals("tanzanite helm"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
				}
				else if (itemName.contains("staff of the dead"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 75;
						player.itemRequirement[ServerConstants.ATTACK] = 75;
				}
				else if (itemName.startsWith("elite black"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}
				else if (itemName.startsWith("dragonstone"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 1;
				}
				else if (itemName.startsWith("proselyte"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 30;
						player.itemRequirement[ServerConstants.PRAYER] = 20;
				}
				else if (itemName.startsWith("toktz-xil-ek"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 60;
				}
				else if (itemName.startsWith("rock-shell"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}
				else if (itemName.startsWith("spined"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 50;
				}
				else if (itemName.startsWith("rune berserker shield"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 45;
				}
				else if (itemName.startsWith("slayer helmet"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 20;
				}

				else if (itemName.contains("mystic") || itemName.contains("enchanted"))
				{
						if (itemName.contains("staff"))
						{
								player.itemRequirement[ServerConstants.MAGIC] = 20;
								player.itemRequirement[ServerConstants.ATTACK] = 40;
						}
						else
						{
								player.itemRequirement[ServerConstants.MAGIC] = 20;
								player.itemRequirement[ServerConstants.DEFENCE] = 20;
						}
				}

				else if (itemName.contains("infinity"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 50;
						player.itemRequirement[ServerConstants.DEFENCE] = 25;
				}

				else if (itemName.contains("rune crossbow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 61;
				}

				else if (itemName.contains("oak") && itemName.contains("bow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 5;
				}

				else if (itemName.contains("willow") && itemName.contains("bow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 20;
				}

				else if (itemName.contains("maple") && itemName.contains("bow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 30;
				}

				else if (itemName.contains("yew") && itemName.contains("bow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 40;
				}

				else if (itemName.contains("magic") && itemName.contains("bow"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 50;
				}

				else if (itemName.contains("steel knife"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 5;
				}

				else if (itemName.contains("black knife"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 10;
				}

				else if (itemName.contains("mithril knife"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 20;
				}

				else if (itemName.contains("adamant knife"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 30;
				}

				else if (itemName.contains("rune knife"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 40;
				}

				else if (itemName.contains("splitbark"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 40;
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

				else if (itemName.contains("green"))
				{
						if (itemName.contains("hide"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 40;
								if (itemName.contains("body"))
								{
										player.itemRequirement[ServerConstants.DEFENCE] = 40;
								}

						}
				}

				else if (itemName.contains("blue"))
				{
						if (itemName.contains("hide"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 50;
								if (itemName.contains("body"))
								{
										player.itemRequirement[ServerConstants.DEFENCE] = 40;
								}

						}
				}

				else if (itemName.contains("red"))
				{
						if (itemName.contains("hide"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 60;
								if (itemName.contains("body"))
								{
										player.itemRequirement[ServerConstants.DEFENCE] = 40;
								}

						}
				}

				else if (itemName.contains("black partyhat"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 1;
				}

				else if (itemName.contains("black") && !itemName.contains("robe") && !itemName.contains("shirt"))
				{
						if (itemName.contains("scimitar") || itemName.contains("dagger"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 10;
						}
						else if (itemName.contains("hide"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 70;
								if (itemName.contains("body"))
								{
										player.itemRequirement[ServerConstants.DEFENCE] = 40;
								}

						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 10;
						}
				}

				else if (itemName.contains("bronze"))
				{
						if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = player.itemRequirement[ServerConstants.DEFENCE] = 1;
						}

				}

				else if (itemName.contains("iron"))
				{
						if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = player.itemRequirement[ServerConstants.DEFENCE] = 1;
						}

				}

				else if (itemName.contains("steel") && !itemName.contains("bolt"))
				{
						if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe") || itemName.contains("hatchet"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 5;
						}
						else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow") && !itemName.contains("arrow"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 5;
						}

				}

				else if (itemName.contains("mithril") && !itemName.contains("bolt"))
				{
						if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe") || itemName.contains("hatchet"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 20;
						}
						else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow") && !itemName.contains("arrow"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 20;
						}

				}

				// Adam because the trimmed adamant items is called "adam".
				else if (itemName.contains("adam") && !itemName.contains("bolt"))
				{
						if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe") || itemName.contains("hatchet"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 30;
						}
						else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow") && !itemName.contains("arrow"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 30;
						}

				}

				else if (itemName.contains("rune") && !itemName.contains("bolt") && !itemName.contains("runecraf"))
				{
						if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe") || itemName.contains("hatchet"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 40;
						}
						else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow") && !itemName.contains("arrow"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 40;
						}

				}

				else if (itemName.contains("dragon") && !itemName.contains("bolt") && !itemName.contains("arrow"))
				{
						if (itemName.contains("sword") || itemName.contains("scimitar") || itemName.contains("dagger") || itemName.contains("mace") || itemName.contains("axe") || itemName.contains("halberd") || itemName.contains("claw") || itemName.contains("spear") || itemName.contains("hatchet"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 60;

						}
						else if (!itemName.contains("nti-") && !itemName.contains("fire"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 60;

						}
						else if (itemName.contains("dragonfire shield"))
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 75;
						}
				}

				else if (itemName.contains("ahrim"))
				{
						if (itemName.contains("staff"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 70;
						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 70;
						}
						player.itemRequirement[ServerConstants.MAGIC] = 70;
				}

				else if (itemName.contains("dagon"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 40;
						player.itemRequirement[ServerConstants.DEFENCE] = 20;
				}

				else if (itemName.contains("arcane stream necklace"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 70;
				}

				else if (itemName.contains("initiate"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 20;
						player.itemRequirement[ServerConstants.PRAYER] = 10;
				}
				else if (itemName.contains("armadyl") && !itemName.contains("godsword"))
				{
						if (itemName.contains("plate") || itemName.contains("helmet") || itemName.contains("plate"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 70;
								player.itemRequirement[ServerConstants.DEFENCE] = 70;
						}
						else if (itemName.contains("crossbow"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 70;
						}
				}

				else if (itemName.contains("vesta") && !itemName.contains("corrupt"))
				{
						if (itemName.contains("longsword") || itemName.contains("spear"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 78;
						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 78;
						}
				}

				else if (itemName.contains("statius") && !itemName.contains("corrupt"))
				{
						if (itemName.contains("warhammer"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 78;
						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 78;
						}
				}

				else if (itemName.contains("zuriel") && !itemName.contains("corrupt"))
				{
						if (itemName.contains("staff"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 78;
						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 78;
						}
						player.itemRequirement[ServerConstants.MAGIC] = 78;
				}

				else if (itemName.contains("morrigan") && !itemName.contains("corrupt"))
				{
						if (itemName.contains("javelin") || itemName.contains("axe"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 78;
						}
						else
						{
								player.itemRequirement[ServerConstants.RANGED] = 78;
								player.itemRequirement[ServerConstants.DEFENCE] = 78;
						}
				}

				else if (itemName.contains("karil"))
				{
						if (itemName.contains("crossbow"))
						{
								player.itemRequirement[ServerConstants.RANGED] = 70;
						}
						else
						{
								player.itemRequirement[ServerConstants.RANGED] = 70;
								player.itemRequirement[ServerConstants.DEFENCE] = 70;
						}
				}

				else if (itemName.contains("elite"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

				else if (itemName.contains("torva"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 80;
				}

				else if (itemName.contains("pernix"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 80;
						player.itemRequirement[ServerConstants.RANGED] = 80;
				}

				else if (itemName.contains("virtus"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 80;
						player.itemRequirement[ServerConstants.MAGIC] = 80;
				}

				else if (itemName.contains("godsword"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 75;
				}

				else if (itemName.contains("3rd age mage hat") || itemName.contains("3rd age robe top") || itemName.contains("3rd age robe bottom") || itemName.contains("3rd age amulet"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 30;
						player.itemRequirement[ServerConstants.MAGIC] = 65;

				}

				else if (itemName.contains("3rd age range top") || itemName.contains("3rd age range legs") || itemName.contains("3rd age range coif") || itemName.contains("3rd age vambraces"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 45;
						player.itemRequirement[ServerConstants.RANGED] = 65;

				}
				else if (itemName.contains("3rd age"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 65;
				}

				else if (itemName.contains("Initiate"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 20;
				}

				else if (itemName.contains("verac") || itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag"))
				{
						if (itemName.contains("hammers"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 70;
								player.itemRequirement[ServerConstants.STRENGTH] = 70;
						}
						else if (itemName.contains("axe"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 70;
								player.itemRequirement[ServerConstants.STRENGTH] = 70;
						}
						else if (itemName.contains("warspear"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 70;
								player.itemRequirement[ServerConstants.STRENGTH] = 70;
						}
						else if (itemName.contains("flail"))
						{
								player.itemRequirement[ServerConstants.ATTACK] = 70;
								player.itemRequirement[ServerConstants.STRENGTH] = 70;
						}
						else
						{
								player.itemRequirement[ServerConstants.DEFENCE] = 70;
						}
				}

				else if (itemName.contains("void"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 42;
						player.itemRequirement[ServerConstants.RANGED] = 42;
						player.itemRequirement[ServerConstants.STRENGTH] = 42;
						player.itemRequirement[ServerConstants.MAGIC] = 42;
						player.itemRequirement[ServerConstants.DEFENCE] = 42;
				}

				else if (itemName.contains("ancient staff"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 50;
						player.itemRequirement[ServerConstants.MAGIC] = 50;
				}

				else if (itemName.contains("staff of light"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 75;
						player.itemRequirement[ServerConstants.MAGIC] = 75;
				}

				else if (itemName.contains("saradomin sword") || itemName.contains("blessed sword") || itemName.contains("zamorakian spear"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 70;
				}

				else if (itemName.contains("divine spirit shield") || itemName.contains("elysian spirit shield") || itemName.contains("arcane spirit shield") || itemName.contains("spectral spirit shield"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 75;
						player.itemRequirement[ServerConstants.PRAYER] = 75;
				}
				else if (itemName.contains("blessed spirit shield"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 70;
						player.itemRequirement[ServerConstants.PRAYER] = 60;
				}
				else if (itemName.contains("spirit shield"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
						player.itemRequirement[ServerConstants.PRAYER] = 55;
				}

				else if (itemName.contains("fighter"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

				else if (itemName.contains("dark bow") || itemName.contains("toktz-xil-ul"))
				{
						player.itemRequirement[ServerConstants.RANGED] = 60;
				}

				else if (itemName.contains("toktz-ket-xil"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 60;
				}

				else if (itemName.contains("master wand") || itemName.contains("mages' book"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 60;
				}

				else if (itemName.contains("helm of neitiznot"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 55;
				}

				else if (itemName.contains("bandos chestplate") || itemName.contains("bandos tassets") || itemName.contains("bandos boots"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 65;
				}

				else if (itemName.contains("berserker helm") || itemName.contains("archer helm") || itemName.contains("farseer helm") || itemName.contains("warrior helm"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 45;
				}

				else if (itemName.contains("zamorak cape") || itemName.contains("saradomin cape") || itemName.contains("guthix cape"))
				{
						player.itemRequirement[ServerConstants.MAGIC] = 60;
				}

				else if (itemName.contains("steel defender"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 5;
				}

				else if (itemName.contains("black defender"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 10;
				}

				else if (itemName.contains("mithril defender"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 20;
				}

				else if (itemName.contains("adamant defender"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 30;
				}

				else if (itemName.contains("rune defender"))
				{
						player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

				else if (itemName.contains("barrelchest anchor"))
				{
						player.itemRequirement[ServerConstants.STRENGTH] = 40;
						player.itemRequirement[ServerConstants.ATTACK] = 60;
				}

				else if (itemName.contains("abyssal whip") || itemName.contains("abyssal tentacle"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 75;
				}

				else if (itemName.contains("granite maul"))
				{
						player.itemRequirement[ServerConstants.ATTACK] = 50;
						player.itemRequirement[ServerConstants.STRENGTH] = 50;
				}
				else if (itemName.contains("tzhaar-ket-om"))
				{
						player.itemRequirement[ServerConstants.STRENGTH] = 60;
				}
				switch (itemId)
				{

						case 7462:
								// Barrows gloves.
						case 7461:
								// Dragon gloves.
								player.itemRequirement[ServerConstants.DEFENCE] = 40;
								break;
						case 7460:
								// Rune gloves.
								player.itemRequirement[ServerConstants.DEFENCE] = 40;
								break;
				}
		}

}
