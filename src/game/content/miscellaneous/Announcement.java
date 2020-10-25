package game.content.miscellaneous;

import core.ServerConstants;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Announcements.
 * 
 * @author MGT Madness, created on 08-01-2015.
 */
public class Announcement
{

		private final static int MINUTES = 10;

		private final static String[] message = {
				"Check the ::rules so you stay safe. Be friendly to everyone!",
				"Wilderness bosses is the fastest way to make money. Up to 21k/h.",
				"Donate to help fund advertisments for your favourite server ::donate",
				"::vote for 3,000 bm and a chance of receiving an Armadyl godsword/Dragon claws.",
				"Never use a password from another website/rsps or you might get hacked!",
				"You can purchase skill capes from Sir Prysin.",
				"Kill revenants for 20k+ an hour! One of the top money making methods.",
				"Check the guide button on your quest tab to learn alot.",
				"Chaos elemental gives 20k + an hour!",
				"Ragging other players will get you ip banned.",
				"Pets are always kept upon death.",
				"Deep wild kills can give up to 2x more artefacts!",
				"Tell your friends and family about ShatterScape!",
				"Obtain Pvp tasks to receive 7,000+ blood money after completion.",
				"The higher your victim's defence level, the more artefacts you will receive!",
				"Killing targets drop 4x artefacts and a chance for a rare spirit shield!",
				"You need to be in combat or walk in the wild for 14 minutes to gain a target.",
				"Items harvested and created through skilling can be sold to shop for profit.",
				"Join ::discord to make new friends",
				"Purchase blood money from ::donate in order to help the server grow.",
				"ShatterScape is a community driven Rsps, be sure to voice your opinion on the forums!",
				"::vote for 3,000 bm and a chance of receiving an Armadyl godsword/Dragon claws.",
				"Forced wilderness rules only apply at Edgeville.",
				"Use the wilderness resource area to fish Dark crabs for 12k/h.",
				"Talk to Hans at lumbridge to view account history.",
				"Magic hybrids and tribrids receive 2x artefacts from Pking!",
				"ShatterScape makes it rain.",
				"Mage arena can reward you with 21k+ an hour!",
				"Wild bosses reward up to 20k+ blood money per hour!",
				"Items in rare shop is sold back for the same price except for Partyhats.",
				"Never use a password from another website/rsps or you might get hacked!",
				"::bots to hide bots.",
				"Help ShatterScape by donating ::donate",
				"::discord to communicate with fellow Dawntainers.",
				"Killing targets drop 4x artefacts and a chance for a rare spirit shield!",
				"Pk vid makers will be awarded up to 250k bm depending on the quality! ::yt",
				"::vote for 3,000 bm and a chance of receiving an Armadyl godsword/Dragon claws.",
				"Chaos elemental gives 20k + an hour!",
				"Max cape is only those worthy to wear it at maxed total.",
				"Only the most dedicated players may reach the Hall of Fame highscores.",
				"Open the shop of Sir Prysin to use achievement rewards that you've earned.",
				"Press F12 to save a screenshot.",
				"::risk to quick chat your risk with protect item forced on.",
				"Use blood keys on the chest at home for 15k bm and a chance for spirit shield.",
				"Mage arena can reward you with 21k+ an hour!",
				"Obtain Pvp tasks to receive 7,000+ blood money after completion.",
				"Donate to help fund advertisments for your favourite server ::donate",
				"Teletabs can be used to teleport at 30 wilderness.",
				"Never use a password from another website/rsps or you might get hacked!",
				"The higher your victim's defence level, the more artefacts you will receive!",
				"Magic hybrids and tribrids receive 2x artefacts from Pking!",
				"Use the resource wilderness area for 20% bonus experience.",
				"::blacklist players to avoid being attacked by raggers in Edgeville.",
				"::vote for 3,000 bm and a chance of receiving an Armadyl godsword/Dragon claws.",
				"Wilderness bosses is the fastest way to make money. 15-21k+ an hour.",
				"The #1 daily highscores spot will receive 25k blood money!",
				"Ice strykewyrm is weaker to magic.",
				"Wearing full runecrafting robes will award you with 10% bonus runes.",
				"Donate for blood money ::donate",
				"Kill revenants for 20k an hour! One of the top money making methods.",
				"All types of dragon bones and hides will be converted to blood money on death.",
				"Join the 'ShatterScape' clan chat to meet other players.",
				"Certain untradeable items are bought back from the void knight.",
				"Join Discord at ::discord",
				"Kill Revenants for 20k blood money an hour!",
				"Dyed whips, dark bows & imbued rings are replaced with normal version in Pvp deaths.",
				"Not in a clan yet? Join or create a clan and dominate the Wilderness at ::clans",
				"Mage arena Npcs can reward you with 21k+ an hour!",
				"Obtain Pvp tasks to receive 7,000+ blood money after completion.",
				"Pk vid makers will be awarded up to 250k bm depending on the quality! ::yt",

		};

		private static int value = Misc.random(message.length - 1);

		private static int mainTick;

		public static void announce(String string, String colour)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (PlayerHandler.players[i] != null)
						{
								PlayerHandler.players[i].playerAssistant.sendMessage(colour + string);
						}
				}
		}

		public static void announce(String string)
		{
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++)
				{
						if (PlayerHandler.players[i] != null)
						{
								PlayerHandler.players[i].playerAssistant.sendMessage(string);
						}
				}
		}

		/**
		 * Announcements that are called every specified minutes.
		 */
		public static void announcementGameTick()
		{
				mainTick++;
				if (mainTick == MINUTES * 100)
				{
						mainTick = 0;
						/*
						if (Misc.hasOneOutOf(message.length / 3))
						{
								announce("Dawntained needs " + DonationsNeeded.currentDonationAmount + "/" + DonationsNeeded.DONATIONS_NEEDED + "$ for this month to maintain a growing playerbase.");
						}
						else
						{
						*/
						announce(message[value], "<img=20><col=0000ff>");
						value++;
						if (value > message.length - 1)
						{
								value = 0;
						}
						//}
				}
		}

}
