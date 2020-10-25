package game.item;

import game.content.combat.Combat;
import game.content.combat.vsnpc.CombatNpc;
import game.content.donator.DonationsNeeded;
import game.content.godbook.BookPreaching;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.CompletionistCape;
import game.content.miscellaneous.Teleport;
import game.player.Player;
import utility.Misc;

/**
 * Operate an item.
 * @author MGT Madness, created on 22-10-2013.
 *
 */
public class OperateItem
{

		public static void applyOperate(Player player, int itemId)
		{
				if (!ItemAssistant.hasItemEquipped(player, itemId))
				{
						return;
				}
				if (player.doingAnAction())
				{
						return;
				}
				if (itemId == 11284)
				{
						if (player.getPlayerIdAttacking() > 0)
						{
								Combat.handleDfs(player);
						}
						else if (player.getNpcIdAttacking() > 0)
						{
								CombatNpc.handleDfsNPC(player);
						}
						return;
				}

				// Amulet of glory.
				if (itemId >= 1706 && itemId <= 1712)
				{
						if (Combat.inCombatAlert(player))
						{
								return;
						}
						Teleport.startTeleport(player, 3085 + Misc.random(3), 3491 + Misc.random(5), 0, "GLORY " + itemId + " " + "EQUIPMENT");
						return;
				}
				else if (itemId == 1704)
				{
						player.playerAssistant.sendMessage("Your amulet of glory has run out of charges.");
				}


				if (itemId == 18779)
				{
						Blowpipe.check(player);
						return;
				}
				if (Combat.inPVPAreaOrCombat(player))
				{
						return;
				}
				if (player.getWieldedWeapon() == itemId && !player.isDonator())
				{
						DonationsNeeded.getDonatorMessage(player);
						return;
				}
				if (BookPreaching.sendPreachOptions(player, itemId))
				{
						return;
				}
				switch (itemId)
				{

						// Completionist cape.
						case 14011:
								CompletionistCape.displayInterface(player);
								break;
						case 11696:
								player.startAnimation(7073);
								player.gfx0(1223);
								break;

						case 11698:
								player.startAnimation(7071);
								player.gfx0(1220);
								break;
						case 18771:
								player.startAnimation(10505);
								player.gfx0(1840);
								break;

						case 11694:
								player.startAnimation(7074);
								player.gfx0(1222);
								break;

						case 11700:
								player.startAnimation(7070);
								player.gfx0(1221);
								break;

						case 1305:
								player.gfx100(248);
								player.startAnimation(1058);
								break;

						case 1249:
								player.startAnimation(406);
								player.gfx100(253);
								break;

						case 3204:
								player.gfx100(282);
								player.startAnimation(1203);
								break;

						case 4587:
								player.gfx100(347);
								player.startAnimation(1872);
								break;

						case 1434:
								player.startAnimation(1060);
								player.gfx100(251);
								break;

						case 10887:
								player.gfx100(1027);
								player.startAnimation(5870);
								break;

						case 1215:
						case 1231:
						case 5680:
						case 5698:
								player.gfx100(252);
								player.startAnimation(1062);
								break;

						case 18785: // Abyssal dagger.
								player.gfx0(2623);
								player.startAnimation(1062);
								break;

						case 11730:
								player.startAnimation(7072);
								player.gfx100(1224);
								break;

						case 18660: // Sara's blessed sword.
								player.startAnimation(13719);
								player.gfx100(1224);
								break;

						case 4151:
						case 15445:
						case 15444:
						case 15443:
						case 15442:
						case 18767: // Abyssal tentacle.
						case 15441:
								player.startAnimation(11956);
								break;

						case 14484:
								player.startAnimation(10961);
								player.gfx0(1950);
								break;

						case 13905:
						case 13907:
								player.startAnimation(10499);
								player.gfx0(1835);
								break;

						case 13899:
						case 13901:
								player.startAnimation(10502);
								break;

						// Granite maul
						case 4153:
						case 18662:
								player.startAnimation(1667);
								player.gfx100(340);
								break;

						// Dragon battleaxe
						case 1377:
								player.gfx0(246);
								player.forcedChat("Raarrrrrgggggghhhhhhh!");
								player.startAnimation(1056);
								break;
				}
		}

}