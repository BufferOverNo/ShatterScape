package game.content.worldevent;

import java.util.ArrayList;

import core.Server;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Skull;
import game.content.packet.PickupItemPacket;
import game.item.GroundItem;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Blood key world event.
 * @author MGT Madness, created on 28-02-2017.
 */
public class BloodKey
{
		// Key id 18825

		public static ArrayList<String> keyData = new ArrayList<String>();

		public static void bloodKeyInitialize()
		{
				// The blood key has spawned....
				// Use unused places.
				//keyData.add("3241 3671 = south west Tormented demons");
				//keyData.add("3241 3843 = south of Callisto");
				keyData.add("3259 3779 = south east of Chaos dwarfs");
				keyData.add("3366 3936 = at the lava bridge");
				keyData.add("3282 3929 = at Rogue's castle");
				keyData.add("3039 3706 = at the Bandit camp");
				keyData.add("3241 3614 = at the Chaos temple");
		}

		public static void spawnBloodKey()
		{
				if (!WorldEvent.getActiveEvent("BLOOD KEY"))
				{
						return;
				}
				Object object = new Object();
				CycleEventHandler.getSingleton().addEvent(object, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								int random = Misc.random(0, keyData.size() - 1);
								String parseCoords[] = keyData.get(random).split(" ");
								String parseDescription[] = keyData.get(random).split("= ");
								String description = "The blood key has spawned " + parseDescription[1] + "!";
								Announcement.announce("Pick it up before it dissapears!", ServerConstants.DARK_BLUE);
								Announcement.announce("Reward is 15k blood money and a chance for a rare spirit shield!", ServerConstants.DARK_BLUE);
								Announcement.announce(description, ServerConstants.DARK_BLUE);
								GroundItem item = new GroundItem(18825, Integer.parseInt(parseCoords[0]), Integer.parseInt(parseCoords[1]), 1, 2, "", "", 0, 0, "OTHER: Blood key spawn");
								Server.itemHandler.addItem(item);
						}
				}, 100);

		}

		public static void pickUpBloodKey(Player player)
		{
				if (player.getCombatLevel() != 126)
				{
						player.getPA().sendMessage("Sneaky sneaky, 126 or no can do.");
						return;
				}
				player.getDH().sendDialogues(266);
		}

		public static void confirmPickUpBloodKey(Player player)
		{
				boolean hasKey = ItemAssistant.hasItemInInventory(player, 18825);
				if (!hasKey)
				{
						if (PickupItemPacket.pickUpItem(player))
						{
								if (ItemAssistant.hasItemInInventory(player, 18825))
								{
										Announcement.announce(player.getPlayerName() + " has picked up the blood key!", ServerConstants.DARK_BLUE);
										Skull.goldenSkull(player);
										player.teleBlockEndTime = System.currentTimeMillis() + 300000;
								}
						}
				}
				player.getPA().closeInterfaces();
		}

		public static void leftWild(Player player)
		{
				// Golden skull.
				if (player.headIconPk == 2 && player.getHeight() != 20)
				{
						Announcement.announce(player.getPlayerName() + " has made it out alive!", ServerConstants.DARK_BLUE);
						Skull.clearSkull(player);
				}

		}

		public static void openChest(Player player, int itemId)
		{
				if (itemId != 18825)
				{
						player.getPA().sendMessage("You do not have a blood key.");
						return;
				}
				ItemAssistant.deleteItemFromInventory(player, 18825, 1);
				int amount = 15000;
				ItemAssistant.addItem(player, 18644, amount);
				CoinEconomyTracker.incomeList.add("BLOOD-KEY " + amount);
				player.startAnimation(6387);
				int reward = 0;
				if (Misc.hasOneOutOf(30))
				{
						reward = 13742;
				}
				else if (Misc.hasOneOutOf(10))
				{
						reward = 13738;
				}
				else if (Misc.hasOneOutOf(10))
				{
						reward = 13744;
				}
				else
				{
						reward = Misc.hasPercentageChance(65) ? 13734 : 13736;
				}

				ItemAssistant.addItemToInventoryOrDrop(player, reward, 1);
				Announcement.announce(player.getPlayerName() + " has received 15k bm and a " + ItemAssistant.getItemName(reward) + " from the blood key.", ServerConstants.DARK_BLUE);

		}

}
