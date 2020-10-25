package game.npc.clicknpc;


import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.miscellaneous.TeleportInterface;
import game.content.skilling.Fishing;
import game.content.skilling.Skilling;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * First click on NPC interactions.
 * @author MGT Madness, created on 18-01-2013.
 */
public class FirstClickNpc
{
		/**
		 * First click on NPC.
		 * @param player
		 * 			The associated player.
		 * @param npcId
		 * 			The NPC identity.
		 */
		public static void firstClickNpc(Player player, int npcId)
		{
				player.resetNpcIdToFollow();
				player.setClickNpcType(0);
				player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
				NpcHandler.facePlayer(player, player.getNpcClickIndex());
				Pet.pickUpPetRequirements(player, npcId);

				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("First click Npc: " + player.getNpcType());
				}
				int count = 0;
				for (Skilling.SkillCapeMasterData master : Skilling.SkillCapeMasterData.values())
				{
						if (npcId == master.getNpcId() && npcId != 3299)
						{
								player.skillCapeMasterSkill = count;
								player.getDH().sendDialogues(228);
								if (count == ServerConstants.HERBLORE)
								{
										player.skillCapeMasterExpression = 9760;
								}
								else
								{
										player.skillCapeMasterExpression = 9850;
								}
								return;
						}
						count++;
				}


				// Has to be on a delayed tick or else it will so awkward rotations when talking to an npc when running to it.
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								player.resetFaceUpdate();
						}
				}, 2);
				switch (npcId)
				{

						// Fairy.
						case 534:
								PlayerTitle.displayInterface(player);
								break;
						// Cleaner at wilderness resource area.
						case 5916:
								player.getDH().sendNpcChat("Give me the item you want to note.", 9850);
								break;
						// Mandrith
						case 11261:
								player.getPA().sendMessage("Mandrith does not feel like talking.");
								break;

						// Cow31337Killer.
						case 5210:
								Tournament.talkToCowKiller(player);
								break;
						// Pvp task master.
						case 11259:
								player.getDH().sendDialogues(265);
								break;

						// Adam.
						case 11257:
								player.getDH().sendDialogues(258);
								break;

						case 526:
								if (GameMode.getGameMode(player, "IRON MAN"))
								{
										player.getPA().sendMessage("Iron Man accounts cannot use this.");
										return;
								}
								player.getDH().sendDialogues(261);
								break;
						case 3246:
								player.getShops().openShop(49);
								break;

						// Hatius Cosaintus.
						case 11258:
								player.getDH().sendDialogues(257);
								break;

						case 43:
								if (System.currentTimeMillis() - player.flaxDelay <= 4000)
								{
										return;
								}
								player.flaxDelay = System.currentTimeMillis();
								final Player finalPlayer = player;
								if (!ItemAssistant.hasItemInInventory(finalPlayer, 1735))
								{
										finalPlayer.getPA().sendMessage("You need a pair of shears to shave the sheep.");
										return;
								}
								player.startAnimation(893);

								final Npc npc = NpcHandler.npcs[finalPlayer.getNpcClickIndex()];
								CycleEventHandler.getSingleton().addEvent(finalPlayer, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												container.stop();
										}

										@Override
										public void stop()
										{
												if (npc == null)
												{
														return;
												}
												int oldX = npc.getX();
												int oldY = npc.getY();
												Pet.deletePet(npc);
												ItemAssistant.addItem(finalPlayer, 1737, 1);
												finalPlayer.getPA().sendFilterableMessage("You shear the sheep.");
												NpcHandler.spawnDefaultNpcSheep(42, "Sheep", oldX, oldY, 0, "ROAM", 30, 43);
										}
								}, 3);
								break;
						// Twiggy O'Korn.
						case 4042:
								player.getDH().sendDialogues(260);
								break;
						// Ellis.
						case 2824:
								LeatherCrafting.displayTanningInterface(player);
								break;

						// Abbot Langley.
						case 801:
								player.getDH().sendDialogues(253);
								break;

						// Mage of Zamorak.
						case 2258:
								player.getDH().sendDialogues(225);
								break;

						// Jatix.
						case 587:
								player.getShops().openShop(50);
								break;

						// Shopkeeper.
						case 528:
								player.getShops().openShop(5);
								break;

						// Drogo dwarf.
						case 579:
								player.getShops().openShop(59);
								break;

						// Kamfreena.
						case 4289:
								if (ItemAssistant.hasItemAmountInInventory(player, 8851, 100))
								{
										player.getDH().sendDialogues(251);
								}
								else
								{
										player.getDH().sendDialogues(250);
								}
								break;

						// Hans.
						case 0:
								Achievements.checkCompletionSingle(player, 1079);
								if (GameMode.getGameMode(player, "IRON MAN"))
								{
										Achievements.checkCompletionSingle(player, 1014);
								}
								player.getDH().sendDialogues(245);
								break;

						// Vannaka.
						case 1597:
								player.getDH().sendDialogues(10);
								break;

						//Gnome Glider.
						case 2650:

								player.setActionIdUsed(2650);
								player.getPA().displayInterface(802);
								player.getPA().sendFrame36(153, 0);
								break;


						// Gundai.
						case 902:
								Achievements.checkCompletionSingle(player, 1010);
								player.getDH().sendDialogues(222);
								break;

						// Martin the Master Gardener.
						case 3299:
								player.getDH().sendDialogues(140);
								break;

						// Gnome
						case 2649:
								player.getDH().sendDialogues(191);
								break;

						//Sharks
						case 334:
								Fishing.startFishing(player, Fishing.RAW_SHARK);
								break;

						// Shrimp.
						case 316:
								Fishing.startFishing(player, Fishing.RAW_SHRIMP);
								break;

						// Lobster.
						case 324:
								Fishing.startFishing(player, Fishing.RAW_LOBSTER);
								break;

						// Monk fish.
						case 326:
								Fishing.startFishing(player, Fishing.RAW_MONKFISH);
								break;

						// Dark crab.
						case 325:
								Fishing.startFishing(player, Fishing.RAW_DARK_CRAB);
								break;

						// Horvik.
						case 549:
								if (GameMode.getGameMode(player, "IRON MAN"))
								{
										player.getPA().sendMessage("Iron Man accounts cannot use this.");
										return;
								}
								player.getDH().sendDialogues(216);
								break;

						// Guide
						case 7949:
								Achievements.checkCompletionSingle(player, 1001);
								player.getDH().sendDialogues(217);
								break;

						// Void Knight.
						case 3788:
								player.getDH().sendDialogues(209);
								break;

						// Bob.
						case 519:
								player.getDH().sendDialogues(1);
								break;

						// Teleporter.
						case 5196:
								player.setActionIdUsed(5196);
								player.canUseTeleportInterface = true;
								TeleportInterface.displayInterface(player);
								break;

						// Thessalia.
						case 548:
								player.getDH().sendDialogues(24);
								break;

						// Member Merchant.
						case 2328:
								player.getDH().sendDialogues(112);
								break;

						// Item Specialist Merchant.
						case 521:
								player.getDH().sendDialogues(114);
								break;

						// Melee Merchant Shop.
						case 705:
								player.getDH().sendDialogues(16);
								break;

						case 946:
								// Magician Merchant.
								player.getDH().sendDialogues(17);
								break;

						// Range Merchant.
						case 1861:
								player.getDH().sendDialogues(19);
								break;

						//Cosmetic Merchant.
						case 659:
								player.getDH().sendDialogues(14);
								break;

						// Bankers.
						case 494:
						case 6538:
						case 2619: // TzHaar-Ket-Zuh
								player.setUsingBankSearch(false);
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
								break;

						// Merchant.
						case 596:
								player.getShops().openShop(7);
								break;

						// Sir Prysin.
						case 883:
								player.getDH().sendDialogues(269);
								break;

						//TzHaar-Hur-Tel, Tzhaar shop.	
						case 2620:
								player.getShops().openShop(66);
								break;
				}
				player.setNpcClickIndex(0);
		}

}