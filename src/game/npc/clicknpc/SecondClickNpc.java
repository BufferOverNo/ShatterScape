package game.npc.clicknpc;

import core.ServerConfiguration;
import game.content.achievement.Achievements;
import game.content.bank.Bank;
import game.content.combat.EdgeAndWestsRule;
import game.content.miscellaneous.PvpTask;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Fishing;
import game.content.skilling.Slayer;
import game.content.skilling.thieving.PickPocket;
import game.item.ItemAssistant;
import game.item.PotionCombining;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Second click on NPC interactions.
 * @author MGT Madness, created on 18-01-2013.
 */
public class SecondClickNpc
{

		/**
		 * Second click on NPC.
		 * @param player
		 * 			The associated player.
		 * @param npcType
		 * 			The NPC identity.
		 */
		public static void secondClickNpc(Player player, int npcType)
		{

				player.resetNpcIdToFollow();
				player.setClickNpcType(0);
				if (!NpcHandler.npcs[player.getNpcClickIndex()].faceAction.equals("ROAM"))
				{
						NpcHandler.npcs[player.getNpcClickIndex()].facePlayer(player.getPlayerId());
						NpcHandler.npcs[player.getNpcClickIndex()].timeTurnedByPlayer = System.currentTimeMillis();
				}
				player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("Second Click Npc: " + player.getNpcType());
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
				switch (npcType)
				{

						// Cleaner at wilderness resource area.
						case 5916:
								int free = ItemAssistant.getFreeInventorySlots(player);
								if (free > 10)
								{
										free = 10;
								}
								ItemAssistant.addItem(player, 453, free);
								break;
						// Pvp task master.
						case 11259:
								PvpTask.obtainTask(player);
								break;
						// Horvik, melee.
						case 549:
								player.getShops().openShop(3);
								break;
						// Vannaka, normal-task.
						case 1597:
								Slayer.giveTask(player, false);
								break;
						// Shopkeeper at Edgeville, Cape shop.
						case 526:
								player.getShops().openShop(52);
								break;
						// Ajjat.
						case 4288:
								player.getShops().openShop(72);
								break;

						case 1:
						case 2:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.MAN);
								break;

						case 7:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.FARMER);
								break;

						case 18:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.KHARID_WARRIOR);
								break;

						case 20:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.PALADIN);
								break;

						case 21:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.HERO);
								break;

						case 23:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.KNIGHT_OF_ARDOUGNE);
								break;
						case 2234:
								PickPocket.pickPocket(player, PickPocket.PickPocketData.MASTER_FARMER);
								break;

						// Aubury.
						case 553:
								Teleport.startTeleport(player, 2909 + Misc.random(3), 4830 + Misc.random(4), 0, "MODERN");
								break;

						case 316:
								Fishing.startFishing(player, Fishing.RAW_TROUT);
								break;

						case 324:
								Fishing.startFishing(player, Fishing.RAW_TUNA);
								break;

						// Shop keeper at Entrana, decant.
						case 528:
								PotionCombining.decantAllPotions(player);
								break;

						// Void Knight, Blod money.
						case 3788:
								Achievements.checkCompletionSingle(player, 1017);
								player.getShops().openShop(60);
								break;


						// Sigmund the Merchant.
						case 1282:
								player.getShops().openShop(54);
								break;

						// Mage of Zamorak.
						case 2258:
								player.getDH().sendDialogues(225);
								break;

						// Teleporter
						case 5196:
								if (player.lastTeleport.isEmpty())
								{
										return;
								}
								String[] teleport = player.lastTeleport.split(" ");
								if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, Integer.parseInt(teleport[0]), Integer.parseInt(teleport[1])))
								{
										return;
								}
								Teleport.spellTeleport(player, Integer.parseInt(teleport[0]), Integer.parseInt(teleport[1]), Integer.parseInt(teleport[2]), true);
								break;

						// Thessalia.
						case 599:
								if (ItemAssistant.hasEquipment(player))
								{

										player.playerAssistant.sendMessage("Please remove all your equipment before using this.");
										return;
								}
								player.getPA().displayInterface(3559);
								player.canChangeAppearance = true;
								break;

						case 494: // Banker.
						case 902: // Gundai.
						case 2619: // TzHaar-Ket-Zuh
								player.setUsingBankSearch(false);
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
								break;

						//TzHaar-Hur-Tel, Tzhaar shop.	
						case 2620:
								player.getShops().openShop(66);
								break;
				}
				player.setNpcClickIndex(0);
		}

}