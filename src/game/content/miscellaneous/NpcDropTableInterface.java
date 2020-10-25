package game.content.miscellaneous;

import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.TargetSystem;
import game.content.miscellaneous.Artefacts.ArtefactsData;
import game.item.ItemAssistant;
import game.npc.NpcDrops;
import game.npc.Revenant;
import game.npc.data.NpcDefinition;
import game.npc.pet.BossPetDrops;
import game.player.Player;
import network.connection.VoteManager;

/**
 * Npc drop table interface, viewable by examining an npc or manually opening it.
 * @author MGT Madness, created on 03-11-2016.
 */
public class NpcDropTableInterface
{


		public static void displayInterface(Player player)
		{
				for (int index = 0; index < npcList.length; index++)
				{
						String name = "";
						int npcId = npcList[index];
						if (npcId == 1)
						{
								name = "Targets";
						}
						else if (npcId == 0)
						{
								name = "Player Guide";
						}
						else if (npcId == 2)
						{
								name = "Lava dragon";
						}
						else if (npcId == 3)
						{
								name = "Revenants";
						}
						else if (npcId == 5)
						{
								name = "Boss pets";
						}
						else if (npcId == 6)
						{
								name = "Voting";
						}
						else
						{
								name = NpcDefinition.getDefinitions()[npcId].name;
						}
						name = name.replace("Dagannoth Supreme", "Dagannoth Supr");
						name = name.replace("Commander Zilyana", "Commander Zilya");
						player.getPA().sendFrame126(name, 22472 + index);
				}
				InterfaceAssistant.setFixedScrollMax(player, 22471, (int) (npcList.length * 15.2));
				player.getPA().displayInterface(22450);

		}

		private final static int[] npcList = {0, 11260, 4043, 9463, 912, 8349, 3200, 4040, 3, 6203, 6247, 6260, 6222, 2881, 2882, 2883, 50, 1613, 1615, 2783, 1624, 1591, 1592, 54, 1, 5, 6};

		public static boolean isNpcDropTableButton(Player player, int buttonId)
		{

				// To get average amount of kills to get a drop, it is highest item chance divided by another item's chance. Then save this number. Do the same to the highest item chance
				//divided by the other item changes and keep adding the answer.

				if (buttonId >= 87200 && buttonId <= 87229)
				{
						int indexButton = (buttonId - 87200);
						if (indexButton > npcList.length - 1)
						{
								return true;
						}
						int interfaceId = 22457;
						int npcId = npcList[indexButton];
						String[] rareDropNames = new String[7];
						int[] rareDropItemId = new int[7];
						int[] rarityDrop = new int[7];

						double highestRareDropChance = 0;
						int average = 0;
						if (npcId <= 10)
						{
								if (npcId == 0)
								{
										GuideBook.displayGuideInterface(player);
										rareDropNames[0] = "";
										rarityDrop[0] = 0;
										rareDropItemId[0] = 0;

										rareDropNames[1] = "";
										rarityDrop[1] = 0;
										rareDropItemId[1] = 0;

										rareDropNames[2] = "";
										rarityDrop[2] = 0;
										rareDropItemId[2] = 0;

										rareDropNames[3] = "";
										rarityDrop[3] = 0;
										rareDropItemId[3] = 0;

										rareDropNames[4] = "";
										rarityDrop[4] = 0;
										rareDropItemId[4] = 0;

										rareDropNames[5] = "";
										rarityDrop[5] = 0;
										rareDropItemId[5] = 0;

										rareDropNames[6] = "";
										rarityDrop[6] = 0;
										rareDropItemId[6] = 0;
										average = 0;
								}
								else if (npcId == 3)
								{
										rareDropNames[0] = "Chance for artefact drop is";
										rarityDrop[0] = 0;
										rareDropItemId[0] = 0;

										rareDropNames[1] = Revenant.REVENANT_CHANCE_DIVIDE + " divided by revenant maximum hp";
										rarityDrop[1] = 0;
										rareDropItemId[1] = 0;

										rareDropNames[2] = ItemAssistant.getItemName(ArtefactsData.AncientStatuette.getId());
										rarityDrop[2] = 0;
										rareDropItemId[2] = ArtefactsData.AncientStatuette.getId();

										rareDropNames[3] = ItemAssistant.getItemName(ArtefactsData.SerenStatuette.getId());
										rarityDrop[3] = 0;
										rareDropItemId[3] = ArtefactsData.SerenStatuette.getId();

										rareDropNames[4] = ItemAssistant.getItemName(ArtefactsData.ArmadylStatuette.getId());
										rarityDrop[4] = 0;
										rareDropItemId[4] = ArtefactsData.ArmadylStatuette.getId();

										rareDropNames[5] = ItemAssistant.getItemName(ArtefactsData.ZamorakStatuette.getId());
										rarityDrop[5] = 0;
										rareDropItemId[5] = ArtefactsData.ZamorakStatuette.getId();

										rareDropNames[6] = ItemAssistant.getItemName(ArtefactsData.SaradominStatuette.getId());
										rarityDrop[6] = 0;
										rareDropItemId[6] = ArtefactsData.SaradominStatuette.getId();
										average = 0;
								}
								else if (npcId == 1)
								{


										rareDropNames[0] = "Killing targets";
										rarityDrop[0] = 0;
										rareDropItemId[0] = 0;

										for (int index = 0; index < TargetSystem.spiritShields.length; index++)
										{
												rareDropNames[index + 1] = ItemAssistant.getItemName(TargetSystem.spiritShields[index][0]);
												rarityDrop[index + 1] = TargetSystem.spiritShields[index][1];
												rareDropItemId[index + 1] = TargetSystem.spiritShields[index][0];
										}

										rareDropNames[6] = "";
										rarityDrop[6] = 0;
										rareDropItemId[6] = 0;

										average = 0;
								}
								else if (npcId == 5)
								{


										rareDropNames[0] = "Boss pet drop:";
										rarityDrop[0] = BossPetDrops.DROP_RATE_NORMAL_BOSS_PET;
										rareDropItemId[0] = 0;

										rareDropNames[1] = "Jad pet for Fcape:";
										rarityDrop[1] = BossPetDrops.DROP_RATE_JAD_PET;
										rareDropItemId[1] = 0;

										rareDropNames[2] = "";
										rarityDrop[2] = 0;
										rareDropItemId[2] = 0;

										rareDropNames[3] = "";
										rarityDrop[3] = 0;
										rareDropItemId[3] = 0;

										rareDropNames[4] = "";
										rarityDrop[4] = 0;
										rareDropItemId[4] = 0;

										rareDropNames[5] = "";
										rarityDrop[5] = 0;
										rareDropItemId[5] = 0;

										rareDropNames[6] = "";
										rarityDrop[6] = 0;
										rareDropItemId[6] = 0;

										average = 0;
								}
								// Voting.
								else if (npcId == 6)
								{


										rareDropNames[0] = "For every site you vote on = 1 chance";
										rarityDrop[0] = 0;
										rareDropItemId[0] = 0;

										rareDropNames[1] = "Ags/Dragon claws";
										rarityDrop[1] = VoteManager.LOOT_CHANCE;
										rareDropItemId[1] = 11694;

										rareDropNames[2] = "";
										rarityDrop[2] = 0;
										rareDropItemId[2] = 0;

										rareDropNames[3] = "";
										rarityDrop[3] = 0;
										rareDropItemId[3] = 0;

										rareDropNames[4] = "";
										rarityDrop[4] = 0;
										rareDropItemId[4] = 0;

										rareDropNames[5] = "";
										rarityDrop[5] = 0;
										rareDropItemId[5] = 0;

										rareDropNames[6] = "";
										rarityDrop[6] = 0;
										rareDropItemId[6] = 0;

										average = 0;
								}

								for (int index = 0; index < 7; index++)
								{
										if (rareDropNames[index].isEmpty())
										{
												player.getPA().sendFrame34(-1, index, 22456, 1);
												player.getPA().sendFrame126("", interfaceId);
												interfaceId++;
												player.getPA().sendFrame126("", interfaceId);
												interfaceId++;
										}
										else
										{
												player.getPA().sendFrame34(rareDropNames[index].contains("Barrows") ? -1 : rareDropItemId[index] == 0 ? -1 : rareDropItemId[index], index, 22456, 1);
												player.getPA().sendFrame126(rareDropNames[index], interfaceId);
												interfaceId++;
												if (highestRareDropChance < rarityDrop[index])
												{
														highestRareDropChance = rarityDrop[index];
												}
												player.getPA().sendFrame126(rarityDrop[index] == 0 ? "" : ("1/" + rarityDrop[index]), interfaceId);
												interfaceId++;
										}
								}
						}
						else
						{
								int npcIndex = -1;
								double rarity = 0;
								int itemId = 0;
								String itemAmount = "";
								for (int i = 0; i < NpcDrops.npcRareDropsList.size(); i++)
								{
										if (NpcDrops.npcRareDropsList.get(i) == npcId)
										{
												npcIndex = i;
												break;
										}
								}
								if (npcIndex == -1)
								{
										return true;
								}
								String[] drops = NpcDrops.npcRareDropsData.get(npcIndex).split("-");
								int dropsLength = drops.length;
								double averageKillsPerDrop = 1.0;
								boolean highestGrabbed = false;
								for (int index = 0; index < 7; index++)
								{

										if (index < dropsLength)
										{
												String[] currentLoot = drops[index].split(" ");
												rarity = Integer.parseInt(currentLoot[0]);
												itemId = Integer.parseInt(currentLoot[1]);
												itemAmount = currentLoot[2];
												if (itemAmount.contains(","))
												{
														String parseAmount[] = itemAmount.split(",");
														itemAmount = parseAmount[1];
												}
												player.getPA().sendFrame34a(22456, itemId, index, Integer.parseInt(itemAmount));
												player.getPA().sendFrame126(ItemAssistant.getItemName(itemId), interfaceId);
												interfaceId++;
												player.getPA().sendFrame126("1/" + (int) rarity, interfaceId);
												if (!highestGrabbed)
												{
														highestRareDropChance = rarity;
														highestGrabbed = true;
												}
												else
												{
														averageKillsPerDrop += (highestRareDropChance / rarity);
												}
												interfaceId++;
										}
										else
										{
												player.getPA().sendFrame34(-1, index, 22456, 1);
												player.getPA().sendFrame126("", interfaceId);
												interfaceId++;
												player.getPA().sendFrame126("", interfaceId);
												interfaceId++;
										}
								}
								double result = highestRareDropChance / averageKillsPerDrop;
								average = (int) (result);

						}
						player.getPA().sendFrame126("Avrg " + (npcId == 6 ? "votes per loot: " : "kills per drop: ") + (average == 0 ? "?" : average), 22502);



						player.getPA().setTextClicked(22472 + indexButton, true);
						return true;
				}
				return false;
		}
}
