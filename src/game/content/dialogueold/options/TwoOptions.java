package game.content.dialogueold.options;

import core.ServerConstants;
import game.content.combat.EdgeAndWestsRule;
import game.content.commands.AdministratorCommand;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.Teleport;
//import game.content.quicksetup.Presets;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.starter.GameMode;
import game.content.starter.NewPlayerContent;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import utility.Misc;

/**
 * Handle actions on a two option dialogue.
 * 
 * @author MGT Madness.
 */
public class TwoOptions
{

		public static void firstOption(Player player)
		{

				switch (player.getDialogueAction())
				{

						// Vannaka, Yes option to upgrade slayer helmet.
						case 21:
								if (!ItemAssistant.hasItemAmountInInventory(player, 18644, 10000))
								{

										player.playerAssistant.sendMessage("You need 10k blood money to buy this.");
										player.getPA().closeInterfaces();
										return;
								}
								if (!ItemAssistant.hasItemInInventory(player, 13263))
								{
										player.playerAssistant.sendMessage("You need the Slayer helmet in your inventory in order to upgrade.");
										player.getPA().closeInterfaces();
										return;
								}

								ItemAssistant.deleteItemFromInventory(player, 18644, 10000);
								ItemAssistant.deleteItemFromInventory(player, 13263, 1);
								ItemAssistant.addItem(player, 14637, 1);
								player.getPA().closeInterfaces();
								break;

						// Bob.
						case 2:
								BarrowsRepair.repair(player);
								break;

						// Thessalia, Yes please.
						case 25:
								if (ItemAssistant.hasEquipment(player))
								{
										player.getDH().sendDialogues(5);
										return;
								}
								player.getPA().displayInterface(3559);
								player.canChangeAppearance = true;
								break;
						case 85:
								// Lever at Edgeville, yes option.

								if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 3153, 3923))
								{
										return;
								}
								Teleport.startTeleport(player, 3153, 3923, 0, "LEVER");
								break;
						case 88:
								// I don't know.
								player.forcedChat("I don't know.");
								player.getPA().closeInterfaces();
								break;

						// Teleporter, Callisto.
						case 101:
								Teleport.spellTeleport(player, 3202, 3865, 0, false);
								break;

						// Teleporter, Previous.
						case 112:
								Teleport.spellTeleport(player, 1900, 5346, player.getPlayerId() * 4 + 2, true);
								break;

						// Delete bank pin.
						case 116:
								player.getPA().closeInterfaces();
								if (player.hasEnteredPin)
								{
										player.setPin = false;
										player.bankPin = "";
										player.playerAssistant.sendMessage("Your bank pin has been deleted.");
								}
								else if (!player.hasEnteredPin)
								{
										player.playerAssistant.sendMessage("You need to enter your bank pin first.");
								}
								break;


						// Martin the Master Gardener, Ask about Farming rewards.
						case 141:
								player.skillCapeMasterSkill = ServerConstants.FARMING;
								player.getDH().sendDialogues(229);
								player.skillCapeMasterExpression = 9850;
								break;

						// Barrows, You have found a hidden tunnel, Yes i'm fearless!
						case 164:
								player.getPA().movePlayer(3551, 9689, 0);
								player.getPA().closeInterfaces();
								break;

						// Christmas cracker, That's okay; I might get a party hat!
						case 183:
								player.getPA().closeInterfaces();
								if (!ItemAssistant.hasItemInInventory(player, 962))
								{
										return;
								}
								player.gfx0(1637);
								ItemAssistant.deleteItemFromInventory(player, 962, 1);
								int random = Misc.random(95);
								int itemId = 0;
								if (random < 20)
								{
										itemId = 1038;
								}
								else if (random < 40)
								{
										itemId = 1040;
								}
								else if (random < 58)
								{
										itemId = 1048;
								}
								else if (random < 75)
								{
										itemId = 1044;
								}
								else if (random < 85)
								{
										itemId = 1042;
								}
								else if (random < 90)
								{
										itemId = 1046;
								}
								else if (random < 95)
								{
										itemId = 18739;
								}
								ItemAssistant.addItem(player, itemId, 1);
								player.playerAssistant.announce(GameMode.getGameModeName(player) + " has received a " + ItemAssistant.getItemName(itemId) + "!");
								player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
								break;

						// Max cape.
						case 199:
								if (ItemAssistant.getFreeInventorySlots(player) >= 1)
								{
										if (ItemAssistant.checkAndDeleteStackableFromInventory(player, 18644, 10000))
										{
												ItemAssistant.addItem(player, 18674, 1);
										}
										else
										{
												player.playerAssistant.sendMessage("You do not have enough coins.");
										}
								}
								else
								{
										player.playerAssistant.sendMessage("Not enough coins.");
								}
								player.getPA().closeInterfaces();
								break;

						// Vannaka, Yes.
						case 203:
								Slayer.resetTask(player);
								break;

						// Gandai, Yes.
						case 223:
								if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 500000))
								{
										player.getDH().sendDialogues(224);
										return;
								}
								player.wildernessAgilityCourseImmunity = System.currentTimeMillis();
								player.getPA().closeInterfaces();
								break;

						// Mage of Zamorak, Equipment shop.
						case 225:
								player.getShops().openShop(GameMode.getGameMode(player, "IRON MAN") ? 70 : 47);
								break;

						// Gnome, Let me check your store.
						case 192:
								player.getShops().openShop(49);
								break;

						// Skill cape master, May i buy a normal Skill cape.
						case 229:
								if (player.baseSkillLevel[player.skillCapeMasterSkill] == 99)
								{
										player.getDH().sendDialogues(230);
								}
								else
								{
										player.getDH().sendDialogues(233);
								}
								break;

						// I'm afraid that's too much money for me.
						case 231:
								player.getPA().closeInterfaces();
								break;

						// I'm afraid that's too much money for me.
						case 236:
								player.getPA().closeInterfaces();
								break;

						// Yes, i am very nimble and agile!
						case 239:
								player.getPA().movePlayer(player.getX() >= 2878 ? 2876 : 2880, 2952, 0);
								player.getPA().closeInterfaces();
								break;

						// Prayer xp lamp.
						case 249:
								ItemAssistant.deleteItemFromInventory(player, 2528, 1);
								Skilling.addSkillExperience(player, 4109, ServerConstants.PRAYER);
								player.playerAssistant.sendMessage("You rub the prayer lamp and gain experience.");
								break;

						// Twiggy O'Korn, Achievement shop #1
						case 256:
								player.getShops().openShop(61);
								break;

						// Custom presets yes/no dialogue.
//						case 263:
//								Presets.update(player, false);
//								break;

						case 267:
								BloodKey.confirmPickUpBloodKey(player);
								break;

						case 268:
								Tournament.openShop(player);
								break;

						// Achievements shop, Sir pysin.
						case 269:
								player.getShops().openShop(42);
								break;

						// Tutorial, Sure thing!
						case 272:
								player.getDH().sendDialogues(273);
								break;

						case 285:
								MithrilSeeds.pickUpPlant(player);
								break;

						// Yes delete inventory
						case 286:
								AdministratorCommand.empty(player);
								player.getPA().closeInterfaces();
								break;
				}

		}

		public static void secondOption(Player player)
		{
				switch (player.getDialogueAction())
				{

						// Bob, no
						case 2:
								player.getPA().closeInterfaces();
								break;

						// Barrow's repair-man, No
						case 10:
								player.getPA().closeInterfaces();
								break;

						// Dawntain Guide, No option
						case 20:
								player.getPA().closeInterfaces();
								break;

						// Thessalia, no option
						case 25:
								for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++)
								{
										Npc npc = NpcHandler.npcs[index];
										if (npc == null)
										{
												continue;
										}
										if (npc.name.equals("Thessalia"))
										{
												npc.forceChat("No! You're the ugly one!");
												break;
										}
								}
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No thank you.
						case 29:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No. To not buy a casket.
						case 35:
								player.getPA().closeInterfaces();
								break;
						// Wise Old Man, No
						case 71:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No thank you
						case 72:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No
						case 73:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No
						case 76:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No
						case 78:
								player.getPA().closeInterfaces();
								break;

						// Wise Old Man, No
						case 81:
								player.getPA().closeInterfaces();
								break;

						// Lever at Edgeville, no option
						case 85:
								player.getPA().closeInterfaces();
								break;

						// Follow me
						case 88:
								player.forcedChat("Follow me.");
								player.getPA().closeInterfaces();
								break;

						// Teleporter, Next.
						case 101:
								player.getDH().sendDialogues(82);
								break;

						// Teleporter, Previous.
						case 112:
								player.getDH().sendDialogues(161);
								break;

						// Nothing
						case 116:
								player.getPA().closeInterfaces();
								break;

						// Martin the Master Gardener, Open Farming shop.
						case 141:
								player.getShops().openShop(6);
								break;

						// Christmas cracker, Stop, i want to keep my cracker.
						case 183:
								player.getPA().closeInterfaces();
								break;

						case 188:
								player.getPA().closeInterfaces();
								break;

						case 189:
								player.getPA().closeInterfaces();
								break;

						// Completionist cape.
						case 199:
								player.getPA().closeInterfaces();
								break;

						// Vannaka, No, nevermind.
						case 203:
								player.getPA().closeInterfaces();
								break;

						// Gandai, No.
						case 223:
								player.getPA().closeInterfaces();
								break;

						// Mage of Zamorak, Merit shop.
						case 225:
								player.getShops().openShop(48);
								break;

						// Gnome, Okay i will!
						case 192:
								player.getPA().closeInterfaces();
								break;

						// Skill cape master, May i buy a trimmed Skill cape.
						case 229:
								if (player.skillExperience[player.skillCapeMasterSkill] >= 100000000)
								{
										player.getDH().sendDialogues(235);
								}
								else
								{
										player.getDH().sendDialogues(234);
								}
								break;

						// Skill cape master, Fair enough
						case 231:
								if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 99000))
								{
										player.getDH().sendDialogues(237);
										return;
								}
								ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId(), 1);
								ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId() + 2, 1);
								player.getDH().sendDialogues(232);
								break;

						// Skill cape master, Fair enough
						case 236:
								if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 2000000))
								{
										player.getDH().sendDialogues(237);
										return;
								}
								ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId() + 1, 1);
								player.getDH().sendDialogues(232);
								break;

						// No, i am happy where i am thanks!
						case 239:
								player.getPA().closeInterfaces();
								break;

						// Donator scroll, keep the scroll.
						case 248:
								player.getPA().closeInterfaces();
								break;

						// Prayer xp lamp.
						case 249:
								player.getPA().closeInterfaces();
								break;


						// Twiggy O'Korn, Achievement shop #2.
						case 256:
								player.getShops().openShop(62);
								break;

						// Custom presets yes/no dialogue.
//						case 263:
//								InterfaceAssistant.closeDialogueOnly(player);
//								break;

						case 267:
								player.getPA().closeInterfaces();
								break;

						case 268:
								player.getShops().openShop(42);
								break;

						// Skill cape shop, Sir pysin.
						case 269:
								player.getShops().openShop(9);
								break;

						// Tutorial,  No thanks, i know my way around Dawntained
						case 272:
								player.getPA().movePlayer(3092, 3515, 0);
								player.getPA().sendMessage(":packet:facecompass");
								player.setTutorialComplete(true);
								NewPlayerContent.endTutorial(player);
								break;

						case 285:
								player.getPA().closeInterfaces();
								MithrilSeeds.resetPlayerPlantData(player);
								break;
						// No, do not delete inventory.
						case 286:
								player.getPA().closeInterfaces();
								break;
				}
		}

}