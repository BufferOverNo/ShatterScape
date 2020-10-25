package game.content.miscellaneous;


import core.Server;
import core.ServerConstants;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Artefacts.
 * @author MGT Madness, created on 18-01-2014.
 */
public class Artefacts
{

		public final static int BLOOD_MONEY_ID = 18644;

		public static enum ArtefactsData
		{
				AncientStatuette(14876),
				SerenStatuette(14877),
				ArmadylStatuette(14878),
				ZamorakStatuette(14879),
				SaradominStatuette(14880),
				BandosStatuette(14881),
				RubyChalice(14882),
				GuthixianBrazier(14883),
				ArmadylTotem(14884),
				ZamorakMedalion(14885),
				SaradominCarving(14886),
				BandosScrimshaw(14887),
				SaradominAmphora(14888),
				AncientPsaltaryBridge(14889),
				BronzedDragonClaw(14890),
				ThirdAgeCarafe(14891),
				BrokenStatueHeaddress(14892);

				private int id;

				private ArtefactsData(int id)
				{
						this.id = id;
				}

				public int getId()
				{
						return id;
				}
		}


		/**
		 * Drop the artefact as a loot for the player.
		 * @param victim
		 * 			The associated player
		 */
		public static void dropArtefacts(Player killer, Player victim)
		{
				if (killer == null)
				{
						return;
				}
				if (GameMode.getGameMode(killer, "IRON MAN"))
				{
						return;
				}

				if (victim != null)
				{
						if (victim.isBot && Misc.hasPercentageChance(75))
						{
								return;
						}
				}

				int chance = 0;
				int artefactsDropped = 0;

				if (killer.isLegendaryDonator())
				{
						chance = 100;
				}
				else if (killer.isExtremeDonator())
				{
						chance = 80;
				}
				else if (killer.isSuperDonator())
				{
						chance = 65;
				}
				else if (killer.isDonator())
				{
						chance = 50;
				}
				// If player receives 1 drop everytime, the average is 500 blood money worth.
				if (Misc.hasPercentageChance(chance))
				{
						artefactsDropped++;
				}

				int artefactInitialChance = 70;

				artefactInitialChance += (0.6 * (double) victim.getBaseDefenceLevel());
				if (Misc.hasPercentageChance(artefactInitialChance))
				{
						artefactsDropped++;
				}
				if (artefactInitialChance > 100)
				{
						artefactInitialChance -= 100;
						if (Misc.hasPercentageChance(artefactInitialChance))
						{
								artefactsDropped++;
						}
				}

				// Drop another artefact depending on Wilderness level.
				if (killer.wildernessLevel >= 10)
				{
						artefactsDropped++;
				}
				else if (killer.wildernessLevel >= 30)
				{
						artefactsDropped++;
				}

				// if player is a magic hybrid or a tribrid.
				if (killer.combatStylesUsed == 2 && killer.hasMagicEquipment || killer.combatStylesUsed == 3)
				{
						artefactsDropped++;
				}

				for (int index = 0; index < artefactsDropped; index++)
				{
						int artefact = artefactDrop();
						if (victim.isBot)
						{
								CoinEconomyTracker.incomeList.add("BOTS " + BloodMoneyPrice.getBloodMoneyPrice(artefact));
						}
						else
						{
								CoinEconomyTracker.incomeList.add("PKING " + BloodMoneyPrice.getBloodMoneyPrice(artefact));
						}
						Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), 1, false, 0, true, victim.getPlayerName());
						if (artefact == ArtefactsData.AncientStatuette.getId())
						{
								if (!killer.profilePrivacyOn)
								{
										Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(killer) + " received an Ancient statuette from Pking!");
								}
								RareDropLog.appendRareDrop(killer, "Pking: Ancient statuette");
								killer.getPA().sendScreenshot(ItemAssistant.getItemName(artefact), 2);
						}
				}
		}

		/**
		 * Used only for dropping 2 extra artefacts if world event is active for pking.
		 */
		public static void dropArtefactsAmount(Player killer, Player victim, int amount)
		{
				for (int index = 0; index < amount; index++)
				{
						int artefact = artefactDrop();
						Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), 1, false, 0, true, victim.getPlayerName());
						if (artefact == ArtefactsData.AncientStatuette.getId())
						{
								if (!killer.profilePrivacyOn)
								{
										Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(killer) + " received an Ancient statuette from Pking!");
								}
								RareDropLog.appendRareDrop(killer, "Pking: Ancient statuette");
								killer.getPA().sendScreenshot(ItemAssistant.getItemName(artefact), 2);
						}
				}
		}

		/**
		 * Randomly choose one of the artefactDrop[].
		 * @return
		 * 			One of the artefactDrop[].
		 */
		public static int artefactDrop()
		{
				int random = Misc.random(1, 50);
				if (random >= 50)
				{
						return ArtefactsData.AncientStatuette.getId();
				}
				else if (random >= 43)
				{
						int misc = Misc.random(1, 4);
						if (misc == 1)
						{
								return ArtefactsData.SerenStatuette.getId();
						}
						else if (misc == 2)
						{
								return ArtefactsData.ArmadylStatuette.getId();
						}
						else if (misc == 3)
						{
								return ArtefactsData.ZamorakStatuette.getId();
						}
						else if (misc == 4)
						{
								return ArtefactsData.SaradominStatuette.getId();
						}
				}
				else if (random >= 38)
				{
						int misc = Misc.random(1, 4);
						if (misc == 1)
						{
								return ArtefactsData.BandosStatuette.getId();
						}
						else if (misc == 2)
						{
								return ArtefactsData.RubyChalice.getId();
						}
						else if (misc == 3)
						{
								return ArtefactsData.GuthixianBrazier.getId();
						}
						else if (misc == 4)
						{
								return ArtefactsData.ArmadylTotem.getId();
						}
				}
				else if (random >= 31)
				{
						int misc = Misc.random(1, 4);
						if (misc == 1)
						{
								return ArtefactsData.ZamorakMedalion.getId();
						}
						else if (misc == 2)
						{
								return ArtefactsData.SaradominCarving.getId();
						}
						else if (misc == 3)
						{
								return ArtefactsData.BandosScrimshaw.getId();
						}
						else if (misc == 4)
						{
								return ArtefactsData.SaradominAmphora.getId();
						}
				}
				else
				{
						int misc = Misc.random(1, 4);
						if (misc == 1)
						{
								return ArtefactsData.AncientPsaltaryBridge.getId();
						}
						else if (misc == 2)
						{
								return ArtefactsData.BronzedDragonClaw.getId();
						}
						else if (misc == 3)
						{
								return ArtefactsData.ThirdAgeCarafe.getId();
						}
						else if (misc == 4)
						{
								return ArtefactsData.BrokenStatueHeaddress.getId();
						}
				}
				return 995;
		}

		public static boolean isArtefact(Player player, int itemId)
		{
				for (ArtefactsData data : ArtefactsData.values())
				{
						if (itemId == data.getId())
						{
								if (Area.inWilderness(player))
								{
										player.getPA().sendMessage("You cannot use it in the wilderness.");
										return true;
								}
								int bloodMoney = BloodMoneyPrice.getBloodMoneyPrice(data.getId());
								ItemAssistant.deleteItemFromInventory(player, itemId, 1);
								ItemAssistant.addItem(player, BLOOD_MONEY_ID, bloodMoney);
								player.playerAssistant.sendMessage("You break the artefact for " + Misc.formatNumber(bloodMoney) + " blood money.");
								return true;
						}
				}
				return false;
		}

		public static void exchangeArtefacts(Player player)
		{
				boolean hasExchanged = false;
				int coinAmount = 0;
				for (ArtefactsData data : ArtefactsData.values())
				{
						if (ItemAssistant.hasItemInInventory(player, data.getId()))
						{
								coinAmount += ItemAssistant.getInventoryItemAmount1(player, data.getId()) * (BloodMoneyPrice.getBloodMoneyPrice(data.id));
								ItemAssistant.deleteItemFromInventory(player, data.getId(), ItemAssistant.getInventoryItemAmount1(player, data.getId()));
								hasExchanged = true;
						}
				}
				if (hasExchanged)
				{
						ItemAssistant.addItem(player, BLOOD_MONEY_ID, coinAmount);
						player.playerAssistant.sendMessage("The artefacts are exchanged for: " + Misc.formatNumber(coinAmount) + " blood money.");
				}
				else
				{
						player.playerAssistant.sendMessage("You do not have any artefacts.");
				}
				player.getPA().closeInterfaces();
		}
}
