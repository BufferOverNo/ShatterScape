package game.content.combat.vsplayer.range;

import core.Server;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Drop the Ranged ammo on the floor.
 * @author MGT Madness, created on 28-05-2015.
 */
public class RangedAmmoUsed
{

		/**
		 * Drop the used ammo on the floor if requirements are met.
		 * @param attacker
		 * 			The player using the ammo.
		 * @param victim
		 * 			The player being attacked.
		 */
		public static void dropAmmo(Player attacker, Player victim)
		{
				if (!attacker.getAmmoDropped())
				{
						return;
				}

				if (RangedData.hasAvaRelatedItem(attacker))
				{
						if (Misc.hasPercentageChance(70))
						{
								return;
						}
				}
				else
				{
						if (Misc.hasPercentageChance(15))
						{
								return;
						}
				}
				if (Server.itemHandler.itemAmount(attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY()) == 0)
				{
						Server.itemHandler.createGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY(), 1, false, 0, false, "");
				}
				else if (Server.itemHandler.itemAmount(attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY()) != 0)
				{
						int amount = Server.itemHandler.itemAmount(attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY());
						Server.itemHandler.removeGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY(), false);
						Server.itemHandler.createGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), victim.getX(), victim.getY(), amount + 1, false, 0, false, "");
				}
		}

		/**
		 * Delete the used ammo if requirements are met.
		 * @param player
		 * 			The associated player.
		 */
		public static void deleteAmmo(Player player)
		{

				// Toxic blowpipe.
				if (player.getWieldedWeapon() == 18779)
				{
						if (player.blowpipeDartItemAmount > 0)
						{
								player.blowpipeDartItemAmount--;
								if (player.blowpipeDartItemAmount == 200)
								{
										player.getPA().sendMessage(ServerConstants.PURPLE_COL + "You have 200 darts left.");
								}
								else if (player.blowpipeDartItemAmount == 75)
								{
										player.getPA().sendMessage(ServerConstants.PURPLE_COL + "You have 75 darts left.");
								}
						}
						if (player.blowpipeDartItemAmount == 0)
						{
								player.blowpipeDartItemId = 0;
						}
						return;
				}
				// Bolt racks and hand cannon shots are an exception and Dragon javelin
				if (player.playerEquipment[ServerConstants.ARROW_SLOT] == 4740 || player.playerEquipment[ServerConstants.ARROW_SLOT] == 15243 || player.playerEquipment[ServerConstants.ARROW_SLOT] == 18819)
				{

				}
				else
				{
						if (RangedData.hasAvaRelatedItem(player) && Misc.hasPercentageChance(85))
						{
								return;
						}
						player.setAmmoDropped(true);
				}

				if (RangedData.isWieldingShortRangeRangedWeapon(player))
				{
						if (player.playerEquipmentN[ServerConstants.WEAPON_SLOT] == 1)
						{
								ItemAssistant.deleteEquipment(player, player.getWieldedWeapon(), ServerConstants.WEAPON_SLOT);
						}
						if (player.playerEquipmentN[ServerConstants.WEAPON_SLOT] != 0)
						{
								player.getOutStream().createFrameVarSizeWord(34);
								player.getOutStream().writeWord(1688);
								player.getOutStream().writeByte(ServerConstants.WEAPON_SLOT);
								player.getOutStream().writeWord(player.getWieldedWeapon() + 1);
								if (player.playerEquipmentN[ServerConstants.WEAPON_SLOT] - 1 > 254)
								{
										player.getOutStream().writeByte(255);
										player.getOutStream().writeDWord(player.playerEquipmentN[ServerConstants.WEAPON_SLOT] - 1);
								}
								else
								{
										player.getOutStream().writeByte(player.playerEquipmentN[ServerConstants.WEAPON_SLOT] - 1);
								}
								player.getOutStream().endFrameVarSizeWord();
								player.flushOutStream();
								player.playerEquipmentN[ServerConstants.WEAPON_SLOT] -= 1;
						}
						player.setUpdateRequired(true);
						player.setAppearanceUpdateRequired(true);
						return;
				}
				if (player.playerEquipmentN[ServerConstants.ARROW_SLOT] == 1)
				{
						ItemAssistant.deleteEquipment(player, player.playerEquipment[ServerConstants.ARROW_SLOT], ServerConstants.ARROW_SLOT);
				}
				if (player.playerEquipmentN[ServerConstants.ARROW_SLOT] != 0)
				{
						if (!player.isBot)
						{
								player.getOutStream().createFrameVarSizeWord(34);
								player.getOutStream().writeWord(1688);
								player.getOutStream().writeByte(ServerConstants.ARROW_SLOT);
								player.getOutStream().writeWord(player.playerEquipment[ServerConstants.ARROW_SLOT] + 1);
								if (player.playerEquipmentN[ServerConstants.ARROW_SLOT] - 1 > 254)
								{
										player.getOutStream().writeByte(255);
										player.getOutStream().writeDWord(player.playerEquipmentN[ServerConstants.ARROW_SLOT] - 1);
								}
								else
								{
										player.getOutStream().writeByte(player.playerEquipmentN[ServerConstants.ARROW_SLOT] - 1);
								}
								player.getOutStream().endFrameVarSizeWord();
								player.flushOutStream();
						}
						player.playerEquipmentN[ServerConstants.ARROW_SLOT] -= 1;
				}
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
		}
}
