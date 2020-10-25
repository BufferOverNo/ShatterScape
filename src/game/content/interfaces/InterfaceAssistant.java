package game.content.interfaces;


import java.util.ArrayList;

import game.content.miscellaneous.PlayerMiscContent;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;

/**
 * Interface updating methods go here.
 * @author MGT Madness, created on 12-01-2014.
 */
public class InterfaceAssistant
{
		public static void vengeanceTimer(Player player, boolean start)
		{
				if (start)
				{
						player.getPA().sendMessage(":packet:vengstart");
				}
				else
				{

						player.getPA().sendMessage(":packet:vengend");
				}
		}

		public static void setFixedScrollMax(Player player, int interfaceId, int scrollMax)
		{
				player.getPA().sendMessage(":packet:scrollmax " + interfaceId + " " + scrollMax);
		}

		public static void scrollUp(Player player)
		{
				player.getPA().sendMessage(":packet:scrollup");
		}

		public static void summoningOrbOn(Player player)
		{
				player.getPA().sendMessage(":packet:summoningorbon");
		}

		public static void summoningOrbOff(Player player)
		{
				player.getPA().sendMessage(":packet:summoningorboff");
		}

		public static void closeDialogueOnly(Player player)
		{
				player.hasDialogueOptionOpened = false;
				player.getPA().sendMessage(":packet:closedialogue");
		}

		public static void displayReward(Player player, ArrayList<?> list)
		{
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(6963);
				player.getOutStream().writeWord(list.size());
				for (int i = 0; i < list.size(); i++)
				{
						String[] args = ((String) list.get(i)).split(" ");
						int item = Integer.parseInt(args[0]);
						int amount = Integer.parseInt(args[1]);
						if (amount > 254)
						{
								player.getOutStream().writeByte(255);
								player.getOutStream().writeDWord_v2(amount);
						}
						else
						{
								player.getOutStream().writeByte(amount);
						}
						player.getOutStream().writeWordBigEndianA(item + 1);
				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
				player.getPA().displayInterface(6960);
		}

		public static void showSkillingInterface(Player player, String instruction, int zoom, int itemId, int offset1, int offset2)
		{
				player.getPA().sendMessage(":packet:senditemchat 1746 " + offset1 + " " + offset2);
				player.getPA().sendFrame126(instruction, 8879);
				player.getPA().sendFrame126(ItemAssistant.getItemName(itemId), 2799);
				player.getPA().sendFrame246(1746, zoom, itemId);
				player.getPA().sendFrame164(4429);
		}


		/**
		 * Show Wilderness interface.
		 * @param player
		 * 			The associated player.
		 */
		public static void wildernessInterface(Player player)
		{
				if (!Area.inPVPArea(player))
				{
						return;
				}
				player.getPA().sendFrame126(Area.inSafePkFightZone(player) ? "@gr3@Safe" : "Level: " + player.wildernessLevel, player.useBottomRightWildInterface ? 24396 : 24391);
				player.getPA().walkableInterface(player.useBottomRightWildInterface ? 24395 : 24390);
		}

		public static void whatToDoInterface(Player player)
		{
				player.getPA().sendFrame126("@dre@What to do on ShatterScape:", 8144);
				player.getPA().sendFrame126("@bla@", 8145);
				player.getPA().sendFrame126("@bla@Teleporting can be done through 'Teleporter' NPC inside Edgeville bank.", 8146);
				player.getPA().sendFrame126("@bla@Highscores and slayer task master are also found in the bank.", 8147);
				player.getPA().sendFrame126("@bla@Altar is located at the starter area and so are the shops.", 8148);
				player.getPA().sendFrame126("@bla@", 8149);
				player.getPA().sendFrame126("@bla@Completionist cape is claimed inside Edgevile bank, you must however", 8150);
				player.getPA().sendFrame126("@bla@be a 'Train combat' account and achieve 99 in all skills, including", 8151);
				player.getPA().sendFrame126("@bla@combat.", 8152);
				player.getPA().sendFrame126("@bla@Completionist cape is fully customizable into hundreds of colours.", 8153);
				player.getPA().sendFrame126("@bla@", 8154);
				player.getPA().sendFrame126("@bla@All boss drops are the same as Runescape, teleport to Bosses", 8155);
				player.getPA().sendFrame126("@bla@at 'Teleporter' NPC in Edgeville bank.", 8156);
				player.getPA().sendFrame126("@bla@", 8157);
				player.getPA().sendFrame126("@bla@Talk to NPC outside Tzhaar fight caves to claim Tokhaar-kal.", 8158);
				player.getPA().sendFrame126("@bla@", 8159);
				player.getPA().sendFrame126("@bla@Win Party hats and H'ween masks by participating in the community", 8160);
				player.getPA().sendFrame126("@bla@event at 'ShatterScape.com/event'", 8161);
				player.getPA().sendFrame126("@bla@", 8162);
				player.getPA().sendFrame126("@bla@Talk to all NPCs at starter area to find out more about the game.", 8163);
				player.getPA().sendFrame126("@bla@", 8164);
				player.getPA().sendFrame126("@bla@You may set your player title at the Wise old man at starter area.", 8165);
				player.getPA().sendFrame126("@bla@", 8166);
				player.getPA().sendFrame126("@dre@Money making:", 8167);
				player.getPA().sendFrame126("@bla@Thieve from 'Man', north west of Edgeville bank.", 8168);
				player.getPA().sendFrame126("@bla@Loot casket coins from monsters.", 8169);
				player.getPA().sendFrame126("@bla@Earn rare drops and sell to players.", 8170);
				player.getPA().sendFrame126("@bla@", 8171);
				player.getPA().sendFrame126("@bla@Loot clue scrolls from Slayer tasks, solve them and earn special", 8172);
				player.getPA().sendFrame126("@bla@items including Third age equipment!", 8173);
				player.getPA().displayInterface(8134);
		}

		/**
		 * Sign post at Entrana.
		 * @param player
		 * 			The associated player.
		 */
		public static void signPost(Player player)
		{
				player.getPA().displayInterface(13585);
				player.getPA().sendFrame126("@dre@Directions", 13589);
				player.getPA().sendFrame126("West: farming.", 13591);
				player.getPA().sendFrame126("North: mining and smithing.", 13592);
				player.getPA().sendFrame126("North east: runecrafting.", 13593);
				player.getPA().sendFrame126("South east: woodcutting, fishing, cooking", 13594);
				player.getPA().sendFrame126("and runecrafting.", 13595);
				player.getPA().sendFrame126("", 13596);
				player.getPA().sendFrame126("", 13597);
				player.getPA().sendFrame126("", 13598);
				player.getPA().sendFrame126("", 13599);
				player.getPA().sendFrame126("", 13600);
				player.getPA().sendFrame126("", 13601);
				player.getPA().sendFrame126("", 13602);
				player.getPA().sendFrame126("", 13603);
				player.getPA().sendFrame126("", 13604);
				player.getPA().sendFrame126("", 13605);
				player.getPA().sendFrame126("", 13606);
				player.getPA().sendFrame126("", 13607);
				player.getPA().sendFrame126("", 13608);
				player.getPA().sendFrame126("", 13609);
		}

		/**
		 * Inform the client to turn off quick prayer orb glow.
		 * @param player
		 * 			The associated player.
		 */
		public static void quickPrayersOff(Player player)
		{
				player.playerAssistant.sendMessage(":quickprayeroff:");
		}

		/**
		 * Inform the client to turn on quick prayer orb glow.
		 * @param player
		 * 			The associated player.
		 */
		public static void quickPrayersOn(Player player)
		{
				player.playerAssistant.sendMessage(":quickprayeron:");
		}

		/**
		 * Inform the client about the resting state.
		 * @param player
		 * 			The associated player.
		 * @param state
		 * 			The state of the resting.
		 */
		public static void informClientRestingState(Player player, String state)
		{
				if (state.equals("on"))
				{
						player.playerAssistant.sendMessage(":restingon:");
				}
				else if (state.equals("off"))
				{
						player.playerAssistant.sendMessage(":restingoff:");
				}
		}

		/**
		 * Update the combat level text on the combat tab interface.
		 * @param player
		 * 			The associated player.
		 */
		public static void updateCombatLevel(Player player)
		{
				player.getPA().sendFrame126("Combat Level: " + player.getCombatLevel(), 19000);
				player.getPA().sendFrame126("Combat Lvl: " + player.getCombatLevel(), 20246);
		}

		/**
		 * Show the tabs.
		 * @param player
		 * 			The associated player.
		 */
		public static void showTabs(Player player)
		{
				player.playerAssistant.sendMessage(":updatetabs:"); // Send information to the client to shown tabs.
				switch (player.spellBook)
				{
						case "ANCIENT":
								player.playerAssistant.setSidebarInterface(6, PlayerMiscContent.getAncientMagicksInterface(player));
								break;

						case "LUNAR":
								player.playerAssistant.setSidebarInterface(6, 29999);
								break;

						case "MODERN":
								player.playerAssistant.setSidebarInterface(6, 1151);
								break;
				}

				player.playerAssistant.setSidebarInterface(5, 5608); //Normal prayer book
		}

		/**
		 * Update the split private chat interface.
		 * @param player
		 * 			The associated interface.
		 */
		public static void splitPrivateChat(Player player)
		{
				player.getPA().sendFrame36(502, player.splitChat ? 1 : 0);
				player.getPA().sendFrame36(287, player.splitChat ? 1 : 0);
				if (player.splitChat)
				{
						player.getPA().sendFrame126("Split Private Chat\\n(currently on)", 22185);
				}
				else
				{
						player.getPA().sendFrame126("Split Private Chat\\n(currently off)", 22185);
				}

		}

		/**
		 * Will clear these frames, starting from firstId to lastId
		 */
		public static void clearFrames(Player player, int firstId, int lastId)
		{
				player.getPA().sendMessage(":packet:clearframes " + firstId + " " + lastId);

		}

		public static void offCityTimer(Player player)
		{
				player.getPA().sendMessage(":packet:offcitytimer");

		}
}