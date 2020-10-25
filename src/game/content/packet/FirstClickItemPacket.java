package game.content.packet;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.consumable.Food;
import game.content.consumable.Potions;
import game.content.minigame.barrows.Barrows;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.ClueScroll;
import game.content.miscellaneous.CoinCasket;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Teleport;
import game.content.music.SoundSystem;
import game.content.prayer.PrayerBook;
import game.content.skilling.Runecrafting;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.prayer.BuryBone;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Clicking an item, bury bone, eat food etc
 **/

public class FirstClickItemPacket implements PacketType
{
		@Override
		public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting() || !player.isTutorialComplete())
				{
						return;
				}

				if (player.getDead())
				{
						return;
				}
				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)
				{
						return;
				}
				int unknown1 = player.getInStream().readSignedWordBigEndianA();
				int itemSlot = player.getInStream().readUnsignedWordA();
				int itemId = player.getInStream().readUnsignedWordBigEndian();

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "unknown1: " + unknown1);
						PacketHandler.saveData(player.getPlayerName(), "itemSlot: " + itemSlot);
						PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
				}

				if (ItemAssistant.nulledItem(itemId))
				{
						return;
				}

				if (itemId != player.playerItems[itemSlot] - 1)
				{
						return;
				}

				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("[First click item: " + itemId + "]");
				}

				if (CoinCasket.isCoinCasketItemId(player, itemId, itemSlot))
				{
						return;
				}
				if (Food.isFood(itemId))
				{
						Food.eat(player, itemId, itemSlot);
						return;
				}

				if (Potions.isPotion(player, itemId))
				{
						Potions.handlePotion(player, itemId, itemSlot);
						return;
				}

				if (BuryBone.isBone(itemId))
				{
						BuryBone.buryBone(player, itemId, itemSlot);
						return;
				}

				if (Runecrafting.isPouch(player, itemId))
				{
						return;
				}

				if (Herblore.isGrimyHerb(player, itemId, itemSlot))
				{
						return;
				}

				if (Artefacts.isArtefact(player, itemId))
				{
						return;
				}

				switch (itemId)
				{

						case 299:
								MithrilSeeds.plantSeed(player);
								break;
						// Arcane prayer scroll.
						case 18849:
								if (player.auguryUnlocked)
								{
										player.getPA().sendMessage("You cannot absorb anymore knowledge.");
										return;
								}
								ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
								player.auguryUnlocked = true;
								player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have unlocked the augury prayer!");
								PrayerBook.updateRigourAndAugury(player);
								break;
						// Dexterous prayer scroll.
						case 18848:
								if (player.rigourUnlocked)
								{
										player.getPA().sendMessage("You cannot absorb anymore knowledge.");
										return;
								}
								ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
								player.rigourUnlocked = true;
								player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have unlocked the rigour prayer!");
								PrayerBook.updateRigourAndAugury(player);
								break;
						case 18820:
								RunePouch.runePouchItemClick(player, "OPEN");
								break;
						// Yo-yo.
						case 4079:
								player.startAnimation(1457);
								break;

						// Guide book.
						case 1856:
								GuideBook.displayGuideInterface(player);
								break;

						// Community event casket.
						case 3849:
								CommunityEvent.giveReward(player);
								break;

						// 1m coins casket.
						case 405:
								if (ItemAssistant.hasItemInInventory(player, 405))
								{
										ItemAssistant.deleteItemFromInventory(player, 405, 1);
										ItemAssistant.addItem(player, 995, 1000000);
										player.getPA().sendFilterableMessage("You receive 1m coins.");
								}
								break;
						// Name change scroll.
						case 18760:
								player.playerAssistant.sendMessage(":namechange:");
								break;
						// Christmas cracker.
						case 962:
								player.getDH().sendDialogues(183);
								break;

						case 7478:
								player.getDH().sendDialogues(248);
								break;

						case 18741:
						case 18742:
								player.getPA().displayInterface(2808);
								break;

						case 18658:
								LootingBag.displayLootingBagInterface(player);
								break;

						case 2297:
						case 2299:
								Potions.consumeAnchovyPizza(player, itemId, itemSlot);
								break;

						case 3144:
								Potions.eatKarambwan(player, itemId, itemSlot);
								break;

						// Prayer lamp.
						case 2528:
								player.getDH().sendDialogues(249);
								break;


						case 2740:
								ClueScroll.openCasket(player);
								break;

						// Toy kite.
						case 12844:
								player.startAnimation(8990);
								break;

						// Monkey greegree.
						case 4024:
								player.getDH().sendDialogues(180);
								break;

						// Spade.
						case 952:
								player.startAnimation(831);
								SoundSystem.sendSound(player, 380, 500);
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
												ClueScroll.dig(player);
												Barrows.startDigging(player);
												player.startAnimation(65535);
										}
								}, 2);
								break;

						// Teleport home.
						case 8013:
								Teleport.startTeleport(player, 3092 + Misc.random(4), 3501 + Misc.random(4), 0, "TAB");
								break;

						// Enchanted gem.
						case 4155:
								player.getDH().sendDialogues(39);
								break;

						// Clue scroll.
						case 2677:
								ClueScroll.openClueScroll(player);
								break;

						//Dice Bag
						case 15098:
								if (System.currentTimeMillis() - player.diceDelay < 2500)
								{
										return;
								}
								player.diceDelay = System.currentTimeMillis();
								if (!ClanChatHandler.inDiceCc(player, true))
								{
										return;
								}
								player.startAnimation(11900);
								player.gfx0(2075);
								player.doingActionEvent(4);
								String message = "I have rolled a " + Misc.random(1, 100) + " on the percentile dice.";
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
												ClanChatHandler.sendDiceClanMessage(player.getPlayerName(), player.getClanId(), message);
										}
								}, 2);
								break;
				}
		}
}