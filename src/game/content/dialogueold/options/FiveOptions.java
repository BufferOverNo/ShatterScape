package game.content.dialogueold.options;

import game.content.achievement.Achievements;
import game.content.miscellaneous.AutoBuyBack;
import game.content.miscellaneous.ChangePassword;
import game.content.miscellaneous.MonkeyGreeGree;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Slayer;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Handle actions on a five option dialogue.
 * @author MGT Madness.
 */
public class FiveOptions
{

		public static void firstOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, I need another assignment.
						case 11:
								Slayer.giveTask(player, false);
								break;

						// Horvik, Cape shop #1.
						case 13:
								player.getShops().openShop(52);
								break;

						// Cosmetic shop 1
						case 14:
								player.getShops().openShop(12);
								break;

						// Item Specialist Merchant, Item points shop.
						case 15:
								player.getShops().openShop(28);
								break;

						// Armour shop 1
						case 16:
								player.getShops().openShop(3);
								break;

						// Completionist cape, top.
						case 133:
								player.partOfCape = "TOP";
								player.getPA().displayInterface(14000);
								break;


						// Pikkupstix, Open pet shop.
						case 135:
								player.getShops().openShop(30);
								break;

						// Monkey greegree, Small ninja.
						case 180:
								MonkeyGreeGree.chooseTransformation(player, 1, 1480, 1386, 1380, 1381);
								break;

						// Monkey greegree, Bearded monkey guard.
						case 181:
								MonkeyGreeGree.chooseTransformation(player, 1, 1483, 1401, 1399, 1400);
								break;

						// Void knight, Blood money shop.
						case 209:
								Achievements.checkCompletionSingle(player, 1017);
								player.getShops().openShop(60);
								break;

						// Guide.
						case 217:
								ChangePassword.sendChangePassword(player);
								break;

						// Sir Prysin, Achievement shop #1.
						case 219:
								player.getShops().openShop(42);
								break;
						// Ice strykewyrm.
						case 220:
								Teleport.spellTeleport(player, 2980, 3866, 0, false);
								break;

						// Dark crabs.
						case 221:
								Teleport.spellTeleport(player, 3035, 3683, 0, false);
								break;

						case 226:
								player.getPA().closeInterfaces();
								break;

				}

		}

		public static void secondOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, May you spare me an Enchanted gem?
						case 11:
								ItemAssistant.addItem(player, 4155, 1);
								player.getPA().closeInterfaces();
								break;

						// Horvik, Cosmetic shop #2.
						case 13:
								player.getShops().openShop(53);
								break;

						// Cosmetic shop 2
						case 14:
								player.getShops().openShop(13);
								break;

						// Item Specialist Merchant, Coins shop. (items added to bank on death).
						case 15:
								player.getShops().openShop(32);
								break;

						// Armour shop 2
						case 16:
								player.getShops().openShop(21);
								break;

						// Completionist cape, top detail.
						case 133:
								player.partOfCape = "TOP DETAIL";
								player.getPA().displayInterface(14000);
								break;

						// Monkey greegree, Large ninja.
						case 180:
								MonkeyGreeGree.chooseTransformation(player, 1, 1481, 1386, 1380, 1381);
								break;

						// Monkey greegree, Blue face monkey guard.
						case 181:
								MonkeyGreeGree.chooseTransformation(player, 1, 1484, 1401, 1399, 1400);
								break;

						// Void knight untradeables.
						case 209:
								player.getShops().openShop(46);
								break;

						// Guide.
						case 217:
								player.profilePrivacyOn = !player.profilePrivacyOn;
								player.getDH().sendDialogues(242);
								break;

						// Sir Prysin, Achievement shop #2.
						case 219:
								player.getShops().openShop(43);
								break;
						// King black dragon.
						case 220:
								Teleport.spellTeleport(player, 3006, 3850, 0, false);
								break;


						// Revenants.
						case 221:
								Teleport.spellTeleport(player, 2978, 3735, 0, false);
								break;

						case 226:
								player.getPA().closeInterfaces();
								break;
				}
		}

		public static void thirdOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, Upgrade to Slayer helmet (charged).
						case 11:
								if (!ItemAssistant.hasItemAmountInInventory(player, 13263, 1))
								{
										player.getDH().sendDialogues(15);
										return;
								}
								player.getDH().sendDialogues(14);
								break;

						// Horvik, Hat shop.
						case 13:
								player.getShops().openShop(41);
								break;

						// Cape shop
						case 14:
								player.getShops().openShop(11);
								break;

						// Item Specialist Merchant, Coins shop.
						case 15:
								player.getShops().openShop(29);
								break;

						//Armour shop 3
						case 16:
								player.getShops().openShop(4);
								break;

						// Completionist cape, bottom.
						case 133:
								player.partOfCape = "BOTTOM";
								player.getPA().displayInterface(14000);
								break;

						// Monkey greegree, Monkey guard.
						case 180:
								MonkeyGreeGree.chooseTransformation(player, 1, 1482, 1401, 1399, 1400);
								break;

						// Monkey greegree, Small zombie.
						case 181:
								MonkeyGreeGree.chooseTransformation(player, 1, 1485, 1386, 1382, 1381);
								break;
						// Void Knight, Buy back untradeables.
						case 209:
								player.getShops().openShop(11);
								break;
						// Guide, Pet shops.
						case 217:
								player.getDH().sendDialogues(218);
								break;

						// Sir Prysin, God page shop or Iron Man shop when an Iron Man opens the dialogue.
						case 219:
								player.getDH().sendDialogues(258);
								break;
						// Chaos elemental.
						case 220:
								Teleport.spellTeleport(player, 3307, 3916, 0, false);
								break;

						// Venenatis.
						case 221:
								Teleport.spellTeleport(player, 3308, 3737, 0, false);
								break;

						// Guide, Change game mode.
						case 226:
								//GameMode.changeGameMode(player);
								player.getPA().closeInterfaces();
								break;

						// Twiggy O'Korn, Achievement shop #3.
						case 256:
								player.getShops().openShop(63);
								break;
				}

		}

		public static void fourthOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, How many slayer points do i have?
						case 11:
								Slayer.giveBossTask(player);
								break;

						// Horvik, Robe shop.
						case 13:
								player.getShops().openShop(12);
								break;

						// Hat shop 1
						case 14:
								player.getShops().openShop(10);
								break;

						// Item Specialist Merchant, Next.
						case 15:
								player.getDH().sendDialogues(177);
								break;

						// Weapon shop
						case 16:
								player.getShops().openShop(22);
								break;

						// Next.
						case 118:
								player.getDH().sendDialogues(119);
								break;

						// Next.
						case 119:
								player.getDH().sendDialogues(120);
								break;

						// Teleporter, Next.
						case 120:
								player.getDH().sendDialogues(121);
								break;

						// Completionist cape, bottom detail.
						case 133:
								player.partOfCape = "BOTTOM DETAIL";
								player.getPA().displayInterface(14000);
								break;

						// Monkey greegree, Next.
						case 180:
								player.getDH().sendDialogues(181);
								break;

						// Monkey greegree, Next.
						case 181:
								player.getDH().sendDialogues(182);
								break;

						// Void Knight, Vote shop.
						case 209:
								AutoBuyBack.toggleOption(player);
								break;

						case 217:

								player.getPA().toggleBots(true);
								break;

						// Sir Prysin, Gloves.
						case 219:
								player.getShops().openShop(GameMode.getGameMode(player, "IRON MAN") ? 74 : 57);
								break;

						// Mage arena.
						case 220:
								Teleport.spellTeleport(player, 3113, 3959, 0, false);
								break;

						// Callisto.
						case 221:
								Teleport.spellTeleport(player, 3202, 3865, 0, false);
								break;

						// Guide, Previous.
						case 226:
								player.xpLock = !player.xpLock;
								String text = player.xpLock ? "on" : "off";
								player.getDH().sendStatement("Experience lock has been turned " + text + ".");
								break;

						// Twiggy O'Korn, Achievement shop #4.
						case 256:
								player.getShops().openShop(64);
								break;

				}

		}

		public static void fifthOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, Er..nothing...
						case 11:
								player.getShops().openShop(46);
								break;

						// Horvik, Previous.
						case 13:
								player.getDH().sendDialogues(GameMode.getGameMode(player, "IRON MAN") ? 258 : 216);
								break;

						// Cosmetic Merchant, Next
						case 14:
								player.getDH().sendDialogues(13);
								break;

						// Item Specialist Merchant, Nevermind.
						case 15:
								player.getPA().closeInterfaces();
								break;

						// Previous.
						case 16:
								player.getDH().sendDialogues(216);
								break;

						case 22:
								player.getPA().closeInterfaces();
								break;

						// Teleporter, Previous.
						case 69:
								player.getDH().sendDialogues(161);
								break;

						// Teleporter, Previous.
						case 82:
								player.getDH().sendDialogues(69);
								break;

						// Teleporter, Next.
						case 110:
								player.getDH().sendDialogues(176);
								break;

						// Teleporter, Next.
						case 111:
								player.getDH().sendDialogues(112);
								break;

						// Teleporter, Previous.
						case 118:
								player.getDH().sendDialogues(161);
								break;

						// Previous.
						case 119:
								player.getDH().sendDialogues(118);
								break;

						// Previous.
						case 120:
								player.getDH().sendDialogues(119);
								break;

						// Previous.
						case 121:
								player.getDH().sendDialogues(120);
								break;

						// Completionist cape, cancel.
						case 133:
								player.getPA().closeInterfaces();
								break;

						// Item Specialist Merchant, Nevermind.
						case 177:
								player.getPA().closeInterfaces();
								break;

						// Monkey greegree, Nothing.
						case 180:
								player.getPA().closeInterfaces();
								break;

						// Monkey greegree, Previous.
						case 181:
								player.getDH().sendDialogues(180);
								break;

						// Void Knight, Vote shop.
						case 209:
								Achievements.checkCompletionSingle(player, 1008);
								player.getShops().openShop(33);
								break;

						// Guide, Next.
						case 217:
								player.getDH().sendDialogues(226);
								break;

						// Sir Prysin, Close.
						case 219:
								player.getPA().closeInterfaces();
								break;

						// Previous.
						case 220:
								player.getDH().sendDialogues(221);
								break;

						case 221:
								player.getDH().sendDialogues(220);
								break;

						// Guide, Previous.
						case 226:
								player.getDH().sendDialogues(217);
								break;

						// Wilderness activities, Previous.
						case 245:
								player.getDH().sendDialogues(221);
								break;

						// Twiggy O'Korn, Back.
						case 256:
								player.getShops().openShop(65);
								break;

				}

		}

}