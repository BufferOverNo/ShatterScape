package game.content.miscellaneous;

import game.content.combat.vsplayer.magic.AutoCast;
import game.content.music.SoundSystem;
import game.player.Player;

/**
 * Change prayer book/magic book.
 * @author MGT Madness, created on 16-03-2015.
 */
public class SpellBook
{

		public static void switchToLunar(Player player)
		{
				player.getPA().closeInterfaces();
				SpellBook.lunarSpellBook(player);
				player.playerAssistant.sendMessage("You have switched to the lunar spellbook.");
				player.startAnimation(645);
				SoundSystem.sendSound(player, 442, 200);
		}

		public static void switchToAncients(Player player)
		{
				player.getPA().closeInterfaces();
				SpellBook.ancientMagicksSpellBook(player);
				player.playerAssistant.sendMessage("You have switched to the ancient magicks spellbook.");
				player.startAnimation(645);
				SoundSystem.sendSound(player, 442, 200);
		}

		public static void switchToModern(Player player)
		{
				player.getPA().closeInterfaces();
				SpellBook.modernSpellBook(player);
				player.playerAssistant.sendMessage("You have switched to the modern spellbook.");
				player.startAnimation(645);
				SoundSystem.sendSound(player, 442, 200);
		}

		public static void lunarSpellBook(Player player)
		{
				player.spellBook = "LUNAR";
				player.playerAssistant.setSidebarInterface(6, 29999);
				AutoCast.resetAutocast(player);
		}

		public static void modernSpellBook(Player player)
		{
				player.playerAssistant.setSidebarInterface(6, 1151);
				AutoCast.resetAutocast(player);
				player.spellBook = "MODERN";
		}

		public static void ancientMagicksSpellBook(Player player)
		{
				player.playerAssistant.setSidebarInterface(6, PlayerMiscContent.getAncientMagicksInterface(player));
				AutoCast.resetAutocast(player);
				player.spellBook = "ANCIENT";
		}

}
