package game.content.dialogueold.options;

import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.MonkeyGreeGree;
import game.content.miscellaneous.SpellBook;
import game.content.miscellaneous.Teleport;
//import game.content.quicksetup.Presets;
import game.content.starter.GameMode;
import game.player.Player;

/**
 * Handle actions on a three option dialogue.
 * 
 * @author MGT Madness.
 */
public class ThreeOptions
{

		public static void firstOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Glove shop.
						case 13:
								player.getShops().openShop(17);
								break;

						// Supplies Shop.
						case 17:
								player.getShops().openShop(1);
								break;

						// Range Supplies.
						case 19:
								player.getShops().openShop(2);
								break;

						// Altar, Modern spellbook.
						case 22:
								SpellBook.switchToModern(player);
								break;

						// Martin the Master Gardener, yes to open the resources shop
						case 35:
								player.getShops().openShop(16);
								break;

						// Teleporter, Gnome course.
						case 143:
								Teleport.spellTeleport(player, 2469, 3436, 0, true);
								break;

						// Wise Old Man, What is a Train combat account?
						case 147:
								player.getDH().sendDialogues(148);
								break;


						// TzHaar-Mej-Jal, Information about the Fire cape.
						case 166:
								player.getDH().sendDialogues(167);
								break;

						// Item Specialist Merchant, Ask about Nomad.
						case 177:
								player.getDH().sendDialogues(117);
								break;

						// Monkey greegree, Large monkey.
						case 182:
								MonkeyGreeGree.chooseTransformation(player, 1, 1486, 1386, 1382, 1381);
								break;

						// Gnome.
						case 192:
								player.getDH().sendDialogues(193);
								break;

						// Guide, open tier 3 pet shop.
						case 218:
								player.getShops().openShop(30);
								break;

						// Change game mode.
						case 252:
								GameMode.confirmChangeGameMode(player);
								break;

						// Guide, Throne.
						case 259:
								player.throneId = 1097;
								player.getPA().closeInterfaces();
								break;

						case 260:
								Achievements.claimReward(player);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(7);
								break;

						// Custom presets dialogue.
//						case 262:
//								Presets.equip(player);
//								break;

						case 264:
								player.bossKillCountMessage = !player.bossKillCountMessage;
								player.getPA().closeInterfaces();
								break;
				}

		}

		public static void secondOption(Player player)
		{

				switch (player.getDialogueAction())
				{
						// Hat shop 2
						case 13:
								player.getShops().openShop(26);
								break;

						// Magician Armour Shop.
						case 17:
								player.getShops().openShop(19);
								break;

						// Range Armour Shop.
						case 19:
								player.getShops().openShop(20);
								break;

						// Altar, Ancient magicks.
						case 22:
								SpellBook.switchToAncients(player);
								break;

						// Martin the Master Gardener, No option to open the resources shop
						case 35:
								player.getPA().closeInterfaces();
								break;

						// Boss teleports
						case 101:
								player.getDH().sendDialogues(103);
								break;

						// Teleporter, Barbarian course.
						case 143:
								Teleport.spellTeleport(player, 2552, 3563, 0, true);
								break;

						// Wise Old Man, What is a Set Combat account?
						case 147:
								player.getDH().sendDialogues(150);
								break;

						// Item Specialist Merchant, Vote points shop.
						case 177:
								player.getShops().openShop(33);
								break;

						// Monkey greegree, Karamja monkey.
						case 182:
								MonkeyGreeGree.chooseTransformation(player, 1, 1487, 222, 219, 220);
								break;

						// Gnome, Open agility point shop.
						case 192:
								player.getShops().openShop(35);
								break;

						// Guide, open tier 2 pet shop.
						case 218:
								player.getShops().openShop(27);
								break;

						// Do not change game mode.
						case 252:
								player.getPA().closeInterfaces();
								break;

						// Guide, Throne.
						case 259:
								player.throneId = 1098;
								player.getPA().closeInterfaces();
								break;

						// Twiggy, Community event shop.
						case 260:
								player.getShops().openShop(73);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(41);
								break;

						// Custom presets dialogue.
//						case 262:
//								Presets.update(player, true);
//								break;

						case 264:
								InterfaceAssistant.closeDialogueOnly(player);
								player.setAmountInterface("LOOT NOTIFICATION");
								player.getOutStream().createFrame(27);
								break;

				}

		}

		public static void thirdOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Boot shop
						case 13:
								player.getShops().openShop(15);
								break;

						case 17:
								player.getDH().sendDialogues(216);
								break;

						case 19:
								player.getDH().sendDialogues(216);
								break;

						// Altar, Lunar spellbook.
						case 22:
								SpellBook.switchToLunar(player);
								break;

						// Martin the Master Gardener, Ask about something else option
						case 35:
								player.getDH().sendDialogues(30);
								break;

						// Bartering
						case 48:
								player.getDH().sendDialogues(51);
								break;

						// Can i buy it for a higher price?
						case 49:
								player.forcedChat("Can i buy it for a higher price?");
								player.getPA().closeInterfaces();
								break;

						// How can i donate?
						case 112:
								player.getDH().sendDialogues(113);
								break;

						// Teleporter, Previous.
						case 143:
								player.getDH().sendDialogues(142);
								break;

						// Wise Old Man, Proceed to account selection.
						case 147:
								player.getDH().sendDialogues(151);
								break;

						// Wise Old Man, Go back.
						case 151:
								player.getDH().sendDialogues(147);
								break;

						// TzHaar-Mej-Jal, Nothing.
						case 166:
								player.getPA().closeInterfaces();
								break;

						// Item Specialist Merchant, Previous.
						case 177:
								player.getDH().sendDialogues(15);
								break;

						// Monkey greegree, Previous.
						case 182:
								player.getDH().sendDialogues(181);
								break;

						// Gnome, I'll get back to training.
						case 192:
								player.getPA().closeInterfaces();
								break;

						// I'll be back.
						case 199:
								player.getPA().closeInterfaces();
								break;

						// Guide, close.
						case 218:
								player.getPA().closeInterfaces();
								break;

						// Previous
						case 242:
								player.getDH().sendDialogues(226);
								break;

						// Game mode changing, back.
						case 252:
								GameMode.sendGameModeChangeNotice(player);
								break;

						// Guide, Throne.
						case 259:
								player.throneId = 1099;
								player.getPA().closeInterfaces();
								break;

						// Twiggy, Open www.dawntained.com/event
						case 260:
								player.getPA().sendMessage(":packet:website www.dawntained.com/event");
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(52);
								break;

						// Custom presets dialogue.
//						case 262:
//								Presets.Rename(player);
//								break;

						case 264:
								player.profilePrivacyOn = !player.profilePrivacyOn;
								player.getPA().closeInterfaces();
								break;

				}

		}

}