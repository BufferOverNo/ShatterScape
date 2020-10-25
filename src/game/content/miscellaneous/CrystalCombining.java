package game.content.miscellaneous;

import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Crystal combining for Primordial boots etc..
 * @author MGT Madness, created on 23-11-2016.
 */
public class CrystalCombining
{

		public static enum CrystalBoots
		{
				ETERNAL_BOOTS(18751, 6920, 18743),
				PEGASIAN_BOOTS(18753, 2577, 18745),
				PRIMORDIAL_BOOTS(18755, 11732, 18747),
				INFERNAL_AXE(18757, 6739, 18749),
				INFERNAL_PICKAXE(18758, 15259, 18749);

				private int crystalBootsResult;

				private int bootsIngredient;

				private int crystalIngredient;


				private CrystalBoots(int crystalBootsResult, int bootsIngredient, int crystalIngredient)
				{
						this.crystalBootsResult = crystalBootsResult;
						this.bootsIngredient = bootsIngredient;
						this.crystalIngredient = crystalIngredient;
				}

				public int getCrystalBoots()
				{
						return crystalBootsResult;
				}

				public int getIngredient()
				{
						return bootsIngredient;
				}

				public int getCrystalIngredient()
				{
						return crystalIngredient;
				}

		}


		public static boolean isCrystalBootsParts(Player player, int itemUsedId, int itemUsedWithId)
		{

				int index = -1;
				for (CrystalBoots data : CrystalBoots.values())
				{
						if (itemUsedId == data.getCrystalIngredient() && itemUsedWithId == data.getIngredient() || itemUsedWithId == data.getCrystalIngredient() && itemUsedId == data.getIngredient())
						{
								index = data.ordinal();
								break;
						}
				}

				if (index == -1)
				{
						return false;
				}
				/*
				if (CrystalBoots.INFERNAL_AXE.ordinal() == index)
				{
						if (player.baseSkillLevel[ServerConstants.FIREMAKING] < 85)
						{
								player.getDH().sendStatement("You need 85 Firemaking to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
								return false;
						}
				}
				else if (CrystalBoots.INFERNAL_PICKAXE.ordinal() == index)
				{
						if (player.baseSkillLevel[ServerConstants.SMITHING] < 85)
						{
								player.getDH().sendStatement("You need 85 Runecrafting to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
								return false;
						}
				}
				else
				{
						if (player.baseSkillLevel[ServerConstants.RUNECRAFTING] < 60)
						{
								player.getDH().sendStatement("You need 60 Runecrafting to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
								return false;
						}
				
						if (player.baseSkillLevel[ServerConstants.MAGIC] < 60)
						{
								player.getDH().sendStatement("You need 60 Magic to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
								return false;
						}
				}
				*/
				player.getDH().sendItemChat1("", "You create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".", CrystalBoots.values()[index].getCrystalBoots(), 200, 24, 0);

				player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
				ItemAssistant.deleteItemFromInventory(player, CrystalBoots.values()[index].getCrystalIngredient(), 1);
				ItemAssistant.deleteItemFromInventory(player, CrystalBoots.values()[index].getIngredient(), 1);
				ItemAssistant.addItem(player, CrystalBoots.values()[index].getCrystalBoots(), 1);
				////Skilling.addSkillExperience(player, 200, ServerConstants.MAGIC);
				//Skilling.addSkillExperience(player, 200, ServerConstants.SMITHING);
				return true;
		}
}
