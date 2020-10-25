package game.content.miscellaneous;

import java.util.ArrayList;

import game.content.combat.EdgeAndWestsRule;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * Teleport interface.
 * @author MGT Madness, created on 05-09-2016.
 */
public class TeleportInterface
{
		private static ArrayList<String> titles = new ArrayList<String>();

		private static ArrayList<String> monster = new ArrayList<String>();

		private static ArrayList<String> skilling = new ArrayList<String>();

		private static ArrayList<String> wilderness = new ArrayList<String>();

		private static ArrayList<String> boss = new ArrayList<String>();

		private static ArrayList<String> minigame = new ArrayList<String>();

		private static ArrayList<String> city = new ArrayList<String>();

		public static void teleportStartUp()
		{
				titles.add("Wilderness");
				titles.add("Minigames");
				titles.add("Skilling");
				titles.add("Cities");
				titles.add("Monsters");
				titles.add("Safe bosses");
				/*
					titles.add("Wilderness");
					titles.add("Bosses");
					titles.add("Minigames");
					titles.add("Cities");
					*/



				monster.add("Rock crabs ; 2676 3715 0");
				monster.add("Slayer tower ; 3428 3537 0");
				monster.add("Brimhaven dungeon ; 2713 9564 0");
				monster.add("Bandit camp ; 3169 2990 0");
				monster.add("Taverley dungeon ; 2884 9798 0");
				monster.add("Edgeville dungeon ; 3096 9867 0");
				monster.add("Fremennik dungeon ; 2808 10002 0");
				monster.add("Tzhaar ; 2452 5167 0");



				skilling.add("Entrana ; 2834 3335 0");
				skilling.add("Dwarven mine ; 3023 9739 0");
				skilling.add("Gnome course ; 2469 3436 0");
				skilling.add("Barbarian course ; 2552 3563 0");
				skilling.add("Wilderness course @red@(Wilderness 49) ; 2998 3906 0");



				wilderness.add("West dragons @red@(Wilderness 10) ; 2976 3593 0");
				wilderness.add("East dragons @red@(Wilderness 17) ; 3348 3647 0");
				wilderness.add("Graveyard @red@(Wilderness 19) ; 3146 3671 0");
				wilderness.add("Tormented demons @red@(Wilderness 24) ; 3260 3705 0");
				wilderness.add("Revenants @red@(Wilderness 27) ; 2978 3735 0");
				wilderness.add("Venenatis @red@(Wilderness 28) ; 3308 3737 0");
				wilderness.add("Lava dragons @red@(Wilderness 31) ; 3070 3760 0");
				wilderness.add("Callisto @red@(Wilderness 44) ; 3202 3865 0");
				wilderness.add("Ice Strykewyrms @red@(Wilderness 45) ; 2977 3873 0");
				wilderness.add("Demonic ruins @red@(Wilderness 46) ; 3288 3886 0");
				wilderness.add("Chaos Elemental @red@(Wilderness 50) ; 3307 3916 0");
				wilderness.add("Mage arena @red@(Wilderness 52) ; 3105 3934 0");
				wilderness.add("Magebank @gr3@(Bank area) ; 2537 4714 0");

				boss.add("K'ril Tsutsaroth ; 2925 5331 2");
				boss.add("Commander Zilyana ; 2907 5265 0");
				boss.add("Kree'arra ; 2839 5296 2");
				boss.add("General Graardor ; 2864 5354 2");
				boss.add("Dagannoth Kings ; 1904 4366 0");
				boss.add("King Black Dragon @red@(Wilderness 43) ; 2980 3860 0");
				/*
				boss.add("Corporeal beast ; 2964 4383 2");
				boss.add("TzTok-Jad ; 2452 5167 0");
				boss.add("Cerberus ; 1240 1226 0");
				*/

				minigame.add("Duel arena ; 3366 3266 0");
				minigame.add("Clan wars ; 3327 4758 0");
				/*
				minigame.add("Zombie survival ; 3659 3516 0");
				minigame.add("Duel arena ; 3366 3266 0");
				minigame.add("Barrows ; 3565 3315 0");
				minigame.add("Fight caves ; 2452 5167 0");
				minigame.add("Warrior's guild ; 2882 3546 0");
				minigame.add("Recipe for disaster ; 1900 5346 0");
				*/



				city.add("Varrock ; 3213 3424 0");
				city.add("Falador ; 2965 3378 0");
				city.add("Lumbridge ; 3222 3218 0");
				city.add("Al-kharid ; 3276 3167 0");
				city.add("Karamja ; 2947 3147 0");
				city.add("Draynor ; 3093 3248 0");
				city.add("Ardougne ; 2661 3306 0");
				city.add("Taverly ; 2934 3450 0");
				city.add("Yanille ; 2606 3092 0");
				city.add("Catherby ; 2827 3437 0");
				city.add("Nardah ; 3420 2916 0");
				city.add("Pollvineach ; 3357 2967 0");
				city.add("Canifis ; 3494 3483 0");
				city.add("Camelot ; 2756 3477 0");
		}

		public static boolean isTeleportInterfaceButton(Player player, int buttonId)
		{
				if (buttonId >= 77023 && buttonId <= 77079)
				{
						teleportAction(player, buttonId);
						return true;
				}
				switch (buttonId)
				{
						case 76247:
						case 76251:
						case 76255:
						case 77003:
						case 77007:
						case 77011:
								if (!player.canUseTeleportInterface)
								{
										return true;
								}
								int index = buttonId - (buttonId > 77000 ? 76999 : 76243);
								if (buttonId > 77000)
								{
										index += 12;
								}
								InterfaceAssistant.scrollUp(player);
								player.teleportInterfaceIndex = index;
								player.getPA().setInterfaceClicked(19699 + index, true);
								displayTeleports(player, index);
								return true;
				}

				return false;

		}

		private static void teleportAction(Player player, int buttonId)
		{
				if (!player.canUseTeleportInterface)
				{
						return;
				}
				if (player.currentTeleports.isEmpty())
				{
						return;
				}
				if (player.isInZombiesMinigame())
				{
						return;
				}
				int index = buttonId - 77023;
				index /= 4;
				if (index > player.currentTeleports.size() - 1)
				{
						return;
				}
				String coordinate = player.currentTeleports.get(index).substring(player.currentTeleports.get(index).indexOf(";") + 2);
				String parse[] = coordinate.split(" ");
				int height = Integer.parseInt(parse[2]);
				if (Integer.parseInt(parse[0]) == 1900)
				{
						height = player.getPlayerId() * 4 + 2;
				}
				if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, Integer.parseInt(parse[0]), Integer.parseInt(parse[01])))
				{
						return;
				}
				player.lastTeleport = parse[0] + " " + parse[1] + " " + height;
				Teleport.spellTeleport(player, Integer.parseInt(parse[0]), Integer.parseInt(parse[1]), height, player.getActionIdUsed() == 5196 ? true : false);
				player.resetActionIdUsed();
				player.canUseTeleportInterface = false;

		}

		private static void displayTeleports(Player player, int index)
		{
				if (!player.canUseTeleportInterface)
				{
						return;
				}
				ArrayList<String> list = null;
				if (index == 4)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (wilderness.size() * 25.2));
						list = wilderness;
				}
				else if (index == 8)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
						list = minigame;
				}
				else if (index == 12)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
						list = skilling;
				}
				else if (index == 16)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (city.size() * 25.2));
						list = city;
				}
				else if (index == 20)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (monster.size() * 25.2));
						list = monster;
				}
				else if (index == 24)
				{
						InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
						list = boss;
				}
				player.currentTeleports = list;
				int lastUsed = 0;
				for (int index1 = 0; index1 < list.size(); index1++)
				{
						String text = list.get(index1);
						int a = text.lastIndexOf(";") - 1;
						text = text.substring(0, a);
						player.getPA().sendFrame126(text, 19738 + (index1 * 4));
						lastUsed = 19738 + (index1 * 4);
				}
				player.getPA().sendMessage(":packet:clearteleport " + (lastUsed + 4));
		}

		public static void displayInterface(Player player)
		{
				if (player.isInZombiesMinigame())
				{
						return;
				}
				if (!player.canUseTeleportInterface)
				{
						return;
				}
				for (int index = 0; index < titles.size(); index++)
				{
						player.getPA().sendFrame126(titles.get(index), 19706 + (index * 4));
				}
				if (player.teleportInterfaceIndex == 0)
				{
						player.teleportInterfaceIndex = 4;
				}
				player.getPA().setInterfaceClicked(19699 + player.teleportInterfaceIndex, true);
				displayTeleports(player, player.teleportInterfaceIndex);
				player.getPA().displayInterface(19700);
		}

		public static void teleportTo(Player player, int x, int y)
		{
				if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, x, y))
				{
						return;
				}
				Teleport.spellTeleport(player, x, y, 0, false);

		}
}
