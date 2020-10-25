package game.npc;

import core.Server;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.Artefacts.ArtefactsData;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.data.NpcDefinition;
import game.player.Player;
import utility.Misc;

public class Revenant
{


		public final static int REVENANT_CHANCE_DIVIDE = 325;

		public static void revanantLoot(Player player, Npc npc)
		{
				if (NpcDefinition.getDefinitions()[npc.npcType].name.contains("Revenant"))
				{
						//178 hp
						int npcX = npc.getVisualX();
						int npcY = npc.getVisualY();
						int chance = REVENANT_CHANCE_DIVIDE / npc.maximumHitPoints;
						if (WorldEvent.currentEvent.toLowerCase().contains("revenant"))
						{
								chance /= 3;
						}
						if (Misc.hasOneOutOf(GameMode.getDropRate(player, chance)))
						{
								int artefact = Artefacts.artefactDrop();
								CoinEconomyTracker.incomeList.add("WILD-PVM " + BloodMoneyPrice.getBloodMoneyPrice(artefact));
								Server.itemHandler.createGroundItem(player, artefact, npcX, npcY, 1, false, 0, true, "");
								if (artefact == ArtefactsData.AncientStatuette.getId())
								{
										if (!player.profilePrivacyOn)
										{
												Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received an Ancient statuette from Revenants!");
										}
										RareDropLog.appendRareDrop(player, "Revenants: Ancient statuette");
										player.getPA().sendScreenshot(ItemAssistant.getItemName(artefact), 2);
								}
						}
				}
		}
}
