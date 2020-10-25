package game.content.combat.vsplayer.melee;

import core.ServerConstants;
import game.content.music.SoundSystem;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Handle Melee animations etc..
 * 
 * @author MGT Madness, created on 20-11-2013.
 */
public class MeleeData
{

		/**
		 * Check if the player is wielding a halberd.
		 * 
		 * @param player
		 *        The associated player.
		 * @return True, if the player is wielding a halberd.
		 */
		public static boolean usingHalberd(Player player)
		{
				switch (player.getWieldedWeapon())
				{
						case 3190:
						case 3192:
						case 3194:
						case 3196:
						case 3198:
						case 3200:
						case 3202:
						case 3204:
								return true;
						default:
								return false;
				}
		}

		public static int getWeaponAnimation(Player player, String weaponName)
		{
				weaponName = weaponName.toLowerCase();
				Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
				switch (weaponName)
				{
						case "unarmed":
								if (player.getCombatStyle(ServerConstants.ACCURATE) || player.getCombatStyle(ServerConstants.DEFENSIVE))
								{
										SoundSystem.sendSound(player, victim, 417, 300);
										return 422;
								}
								if (player.getCombatStyle(ServerConstants.AGGRESSIVE))
								{
										SoundSystem.sendSound(player, victim, 417, 300);
										return 423;
								}
								break;
				}
				if (weaponName.equalsIgnoreCase("staff of light"))
				{
						return 13048;
				}
				if (weaponName.contains("halberd"))
				{
						if (player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								return 2080;
						}
						else
						{
								return 440;
						}
				}
				if (weaponName.startsWith("dragon dagger") || weaponName.startsWith("abyssal dagger"))
				{
						SoundSystem.sendSound(player, victim, 793, 270);
						return 402;
				}
				if (weaponName.contains("dagger"))
				{
						SoundSystem.sendSound(player, victim, 793, 270);
						return 386;
				}
				if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("saradomin sword") || weaponName.contains("blessed sword"))
				{
						if (player.getCombatStyle(ServerConstants.AGGRESSIVE) || player.getCombatStyle(ServerConstants.ACCURATE))
						{
								return 7041;
						}
						if (player.getCombatStyle(ServerConstants.CONTROLLED))
						{
								return 7048;
						}
						if (player.getCombatStyle(ServerConstants.DEFENSIVE))
						{
								return 7049;
						}
				}
				if (weaponName.equalsIgnoreCase("dragon sword") || weaponName.equalsIgnoreCase("rune sword"))
				{
						switch (player.getCombatStyle())
						{
								case ServerConstants.DEFENSIVE:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 390;
								case ServerConstants.CONTROLLED:
								case ServerConstants.AGGRESSIVE:
								case ServerConstants.ACCURATE:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 386;
						}
				}
				if (weaponName.contains("sword"))
				{
						return 390;
				}
				if (weaponName.contains("battleaxe"))
				{
						return 395;
				}
				if (weaponName.contains("scimitar") || weaponName.contains("longsword") || weaponName.contains("darklight"))
				{
						switch (player.getCombatStyle())
						{
								case ServerConstants.ACCURATE:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 390;
								case ServerConstants.AGGRESSIVE:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 390;
								case ServerConstants.DEFENSIVE:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 390;
								case ServerConstants.CONTROLLED:
										SoundSystem.sendSound(player, victim, 396, 400);
										return 386;
						}
				}
				if (weaponName.contains("pickaxe"))
				{
						return 13035;
				}
				if (weaponName.contains("rapier"))
				{
						return 12028;
				}
				switch (player.getWieldedWeapon())
				{
						// Toktz-xil-ek.
						case 6525:
								SoundSystem.sendSound(player, victim, 793, 270);
								return 386;
						case 1434:
								// Dragon mace
								return 13035;

						case 18826: // Elder maul
								return 13730;
						case 4153: // Granite maul.
						case 18662: // Granite maul (or).
								SoundSystem.sendSound(player, victim, 1079, 350);
								return 1665;
						case 4726:
								// Guthan's spear 
								return 2080;
						case 4747:
								// Torags hammers
								return 0x814;
						case 13905:
						case 13907:
						case 11716:
						case 1249:
								return 2080;
						case 4718:

								player.specialAttackWeaponUsed[30] = 1;
								player.setWeaponAmountUsed(30);
								// Dharok's greataxe
								if (player.getCombatStyle(ServerConstants.AGGRESSIVE))
										return 2066;
								if (player.getCombatStyle(ServerConstants.ACCURATE))
										return 2066;
								return 2067;
						case 4710:
								// Ahrim's staff
								return 406;
						// Boxing gloves
						case 7671:
						case 7673:
								return 3678;
						case 14484:
								// Dragon claws
								return 393;
						case 4755:
								// Verac's flail
								return 2062;
						case 18771:
								// Dragon warhammer
								return 13035;
						case 10887: // Barrelchest anchor
								return 5865;
						case 4151:
						case 18767: // Abyssal tentacle.
						case 15445:
						case 15444:
						case 15443:
						case 15442:
						case 15441:
								SoundSystem.sendSound(player, victim, 1080, 300);
								return player.getWieldedWeapon() == 18767 ? 13721 : 1658;
						case 1215:
						case 5698:
						case 18785: // Abyssal dagger.
								return 402;
						case 6528: // Obby maul
						case 18353:
						case 18354:
								return 2661;
						default:
								SoundSystem.sendSound(player, victim, 417, 300);
								return 451;
				}
		}

}
