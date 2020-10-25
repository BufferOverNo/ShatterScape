package game.content.dialogueold.options;

import game.content.donator.DonationsNeeded;
import game.content.godbook.BookPreaching;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.PvpTask;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Runecrafting;
import game.player.Player;
import utility.Misc;

/**
 * Handle actions on a four option dialogue.
 * @author MGT Madness.
 */
public class FourOptions
{

		public static void firstOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						case 41:
								BookPreaching.handlePreach(player, 3842, 0);
								break;

						case 40:
								BookPreaching.handlePreach(player, 3840, 0);
								break;

						case 42:
								BookPreaching.handlePreach(player, 3844, 0);
								break;

						// Martin the Master Gardener, Money making via skilling
						case 30:
								player.getDH().sendDialogues(33);
								break;

						// Wise Old Man, Can i change my password?
						case 32:
								player.getDH().sendDialogues(28);
								break;

						// Wilderness bosses, Chaos Elemental (50 Wilderness).
						case 82:
								Teleport.spellTeleport(player, 3307, 3916, 0, false);
								break;


						// Air altar
						case 105:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.AIR))
								{
										return;
								}
								break;

						// Mage of Zamorak, Nature altar.
						case 106:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.NATURE))
								{
										return;
								}
								break;

						case 202:
								InterfaceAssistant.whatToDoInterface(player);
								break;

						// Horvik.
						case 216:
								player.getShops().openShop(3);
								break;

						// Guide, Rare shop
						case 257:
								player.getShops().openShop(74);
								break;

						// Sir Pysin, Iron Man, Skilling.
						case 258:
								player.getShops().openShop(67);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(7);
								break;

						// Pvp task.
						case 265:
								PvpTask.killsLeft(player);
								break;
				}

		}

		public static void secondOption(Player player)
		{


				switch (player.getDialogueAction())
				{
						case 41:
								BookPreaching.handlePreach(player, 3842, 2);
								break;

						case 40:
								BookPreaching.handlePreach(player, 3840, 2);
								break;

						case 42:
								BookPreaching.handlePreach(player, 3844, 2);
								break;

						//Martin the Master Gardener, Skilling equipment shop 1
						case 30:
								player.getShops().openShop(5);
								break;

						// Wilderness bosses, King Black Dragon (44 Wilderness) & Ice strykwyrm.
						case 82:
								Teleport.spellTeleport(player, 2980 + Misc.random(2), 3866 + Misc.random(2), 0, false);
								break;

						// Fire altar
						case 105:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.FIRE))
								{
										return;
								}
								break;

						// Mage of Zamorak, Law altar.
						case 106:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.LAW))
								{
										return;
								}
								break;

						// Martin the Master Gardener, Skilling equipment.
						case 141:
								player.getShops().openShop(5);
								break;

						// Teleporter, Agility.
						case 142:
								player.getDH().sendDialogues(143);
								break;

						case 208:
								player.getShops().openShop(39);
								break;


						// Horvik.
						case 216:
								player.getShops().openShop(20);
								break;

						// Guide, Donator shop.
						case 257:
								player.getShops().openShop(71);
								break;

						// Sir Pysin, Iron Man, Equipment #1.
						case 258:
								player.getShops().openShop(68);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(5);
								break;

						// Pvp task, obtain task.
						case 265:
								PvpTask.obtainTask(player);
								break;
				}

		}

		public static void thirdOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						case 41:
								BookPreaching.handlePreach(player, 3842, 1);
								break;

						case 40:
								BookPreaching.handlePreach(player, 3840, 1);
								break;

						case 42:
								BookPreaching.handlePreach(player, 3844, 1);
								break;

						// Martin the Master Gardener, I want to sell resources
						case 30:
								player.getShops().openShop(16);
								break;


						// Teleporter, Next.
						case 82:
								player.getDH().sendDialogues(69);
								break;

						// Cosmic altar
						case 105:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.COSMIC))
								{
										return;
								}
								break;

						// Mage of Zamorak, Death altar.
						case 106:
								if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.DEATH))
								{
										return;
								}
								break;

						// Teleporter, Mining & Smithing.
						case 142:
								Teleport.spellTeleport(player, 3023, 9739, 0, true);
								break;

						case 208:
								player.getShops().openShop(40);
								break;

						// Horvik.
						case 216:
								player.getShops().openShop(19);
								break;


						// Guide, Legendary Donator Throne.
						case 257:
								if (!player.isLegendaryDonator())
								{
										DonationsNeeded.getLegendaryDonatorMessage(player);
										return;
								}
								player.getDH().sendDialogues(259);
								break;

						// Iron Man shop.
						case 258:
								player.getShops().openShop(41);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(41);
								break;

						// Pvp task, claim reward.
						case 265:
								PvpTask.claimReward(player);
								break;
				}

		}

		public static void fourthOption(Player player)
		{
				switch (player.getDialogueAction())
				{

						case 41:
								BookPreaching.handlePreach(player, 3842, 3);
								break;

						case 40:
								BookPreaching.handlePreach(player, 3840, 3);
								break;

						case 42:
								BookPreaching.handlePreach(player, 3844, 3);
								break;

						// Wise Old Man, Previous.
						case 32:
								player.getDH().sendDialogues(27);
								break;

						// Teleporter, Previous.
						case 82:
								player.getDH().sendDialogues(103);
								break;

						// Next, on the Runecrafting teleport options
						case 105:
								player.getDH().sendDialogues(106);
								break;

						// Mage of Zamorak, Previous.
						case 106:
								player.getDH().sendDialogues(105);
								break;

						// Teleporter, Previous.
						case 142:
								player.getDH().sendDialogues(161);
								break;

						case 202:
								player.getPA().closeInterfaces();
								break;

						case 208:
								player.getPA().closeInterfaces();
								break;

						// Horvik.
						case 216:
								player.getShops().openShop(4);
								break;

						// King Arthur.
						case 257:
								player.getPA().sendMessage(":packet:website www.dawntained.com/donate");
								player.getPA().closeInterfaces();
								break;

						// Iron Man shop.
						case 258:
								player.getShops().openShop(52);
								break;

						// Shopkeeper at Edgeville.
						case 261:
								player.getShops().openShop(52);
								break;

						// Pvp task, ask about rewards.
						case 265:
								PvpTask.whatAreRewards(player);
								break;
				}
		}




}
