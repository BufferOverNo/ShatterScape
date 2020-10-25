package game.content.skilling.herblore;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Herblore skill.
 * @author MGT Madness, created on 06-10-2015.
 */
public class Herblore
{

		public final static int VIAL_OF_WATER = 227;

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemUsed
		 * 			The item used.
		 * @param usedWith
		 * 			The item used with.
		 * @return
		 * 			True, if the player has the required items to start herblore.
		 */
		public static boolean isHerbloreItemOnItem(Player player, int itemUsed, int usedWith)
		{
				if (Herblore.createOverload(player, usedWith, itemUsed))
				{
						return true;
				}
				if (hasWeaponPoisonUnfinishedIngredients(player, usedWith, itemUsed))
				{
						return true;
				}
				if (hasWeaponPoisonFinishedIngredients(player, usedWith, itemUsed))
				{
						return true;
				}
				if (hasUnfinishedPotionIngredients(player, usedWith, itemUsed))
				{
						return true;
				}
				if (hasFinishedPotionIngredients(player, usedWith, itemUsed))
				{
						return true;
				}
				return false;
		}

		private static boolean hasWeaponPoisonFinishedIngredients(Player player, int usedWith, int itemUsed)
		{
				if (player.usingHerbloreEvent)
				{
						return false;
				}

				int POISON_IVY_BERRIES = 6018;
				int WEAPON_POISON_COMPLETED = 5940;
				int WEAPON_POISON_UNF = 5939;

				if (WEAPON_POISON_UNF != itemUsed && WEAPON_POISON_UNF != usedWith)
				{
						return false;
				}
				if (itemUsed == POISON_IVY_BERRIES || usedWith == POISON_IVY_BERRIES)
				{
						if (player.baseSkillLevel[ServerConstants.HERBLORE] >= 82)
						{
								player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.HERBLORE];
								InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 200, WEAPON_POISON_COMPLETED, 20, 0);
								player.skillingData[0] = WEAPON_POISON_COMPLETED;
								player.skillingData[1] = WEAPON_POISON_UNF;
								player.skillingData[2] = POISON_IVY_BERRIES;
								player.skillingData[3] = 190;
								return true;
						}
						else
						{
								player.playerAssistant.sendMessage("You need a herblore level of 82 to make this potion.");
								return false;
						}
				}
				return false;
		}

		private static boolean hasWeaponPoisonUnfinishedIngredients(Player player, int usedWith, int itemUsed)
		{
				if (player.usingHerbloreEvent)
				{
						return false;
				}

				int COCONUT_MILK = 5935;
				int CAVE_NIGHTSHADE = 2398;
				int WEAPON_POISON_UNF = 5939;
				if (COCONUT_MILK != itemUsed && COCONUT_MILK != usedWith)
				{
						return false;
				}
				if (itemUsed == CAVE_NIGHTSHADE || usedWith == CAVE_NIGHTSHADE)
				{
						if (player.baseSkillLevel[ServerConstants.HERBLORE] >= 82)
						{
								player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.HERBLORE];
								InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 200, WEAPON_POISON_UNF, 20, 0);
								player.skillingData[0] = WEAPON_POISON_UNF;
								player.skillingData[1] = COCONUT_MILK;
								player.skillingData[2] = CAVE_NIGHTSHADE;
								player.skillingData[3] = 0;
								return true;
						}
						else
						{
								player.playerAssistant.sendMessage("You need a herblore level of 82 to make this potion.");
								return false;
						}
				}
				return false;
		}

		/**
		* @param player
		* 			The associated player.
		* @param itemUsed
		* 			The item used.
		* @param usedWith
		* 			The item used with.
		* @return
		* 			True, if the player has the ingredients to complete a potion.
		*/
		public static boolean hasFinishedPotionIngredients(Player player, int itemUsed, int usedWith)
		{
				if (player.usingHerbloreEvent)
				{
						return false;
				}
				for (HerbloreFinishedPotionData herb : HerbloreFinishedPotionData.values())
				{
						if ((itemUsed == herb.getIngredientId() || usedWith == herb.getIngredientId()) && (itemUsed == herb.getUnfinishedPotionId() || usedWith == herb.getUnfinishedPotionId()))
						{
								if (player.baseSkillLevel[ServerConstants.HERBLORE] >= herb.getRequiredLevel())
								{
										player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.HERBLORE];
										InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 150, herb.getFinishedProduct(), 20, 0);
										player.skillingData[0] = herb.getFinishedProduct();
										player.skillingData[1] = herb.getIngredientId();
										player.skillingData[2] = herb.getUnfinishedPotionId();
										player.skillingData[3] = herb.getExperience();
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("You need a herblore level of " + herb.getRequiredLevel() + " to make this potion.");
										return false;
								}
						}
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param itemUsed
		 * 			The item used.
		 * @param usedWith
		 * 			The item used with.
		 * @return
		 * 			True, if the player has the ingredients to create an (unf) potion.
		 */
		public static boolean hasUnfinishedPotionIngredients(Player player, int itemUsed, int usedWith)
		{

				if (player.usingHerbloreEvent)
				{
						return false;
				}
				if (VIAL_OF_WATER != itemUsed && VIAL_OF_WATER != usedWith)
				{
						return false;
				}
				for (HerbloreUnfinishedPotionData herb : HerbloreUnfinishedPotionData.values())
				{
						if (itemUsed == herb.getCleanedId() || usedWith == herb.getCleanedId())
						{
								if (player.baseSkillLevel[ServerConstants.HERBLORE] >= herb.getRequiredLevel())
								{
										player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.HERBLORE];
										InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 200, herb.getUnfinishedId(), 20, 0);
										player.skillingData[0] = herb.getUnfinishedId();
										player.skillingData[1] = VIAL_OF_WATER;
										player.skillingData[2] = herb.getCleanedId();
										player.skillingData[3] = 0;
										return true;
								}
								else
								{
										player.playerAssistant.sendMessage("You need a herblore level of " + herb.getRequiredLevel() + " to make this potion.");
										return false;
								}
						}
				}
				return false;
		}

		/**
		 * Create the potion.
		 * @param player
		 * 			The associated player.
		 * @param herbIdResult
		 * 			The potion product.
		 * @param firstIngredient
		 * 			The first ingredient.
		 * @param secondIngredient
		 * 			The second ingredient.
		 * @param experience
		 * 			The experience received.
		 * @param amount
		 * 			The amount of potions to create.
		 */
		private static void createPotionEvent(final Player player, final int herbIdResult, final int firstIngredient, final int secondIngredient, final int experience)
		{
				player.usingHerbloreEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (ItemAssistant.hasItemInInventory(player, firstIngredient) && (secondIngredient == HerbloreFinishedPotionData.EXTREME_RANGING.getUnfinishedPotionId() ? ItemAssistant.hasItemAmountInInventory(player, HerbloreFinishedPotionData.EXTREME_RANGING.getIngredientId(), 5) : ItemAssistant.hasItemInInventory(player, secondIngredient)) && player.usingHerbloreEvent && player.skillingData[4] > 0)
								{
										if (secondIngredient == HerbloreFinishedPotionData.EXTREME_RANGING.getUnfinishedPotionId())
										{
												if (!ItemAssistant.hasItemInInventory(player, 169))
												{
														container.stop();
														return;
												}
										}
										ItemAssistant.deleteItemFromInventory(player, firstIngredient, 1);
										if (secondIngredient == HerbloreFinishedPotionData.EXTREME_RANGING.getUnfinishedPotionId())
										{
												ItemAssistant.deleteItemFromInventory(player, HerbloreFinishedPotionData.EXTREME_RANGING.getIngredientId(), 4);
										}
										ItemAssistant.deleteItemFromInventory(player, secondIngredient, 1);
										ItemAssistant.addItem(player, herbIdResult, 1);
										Skilling.addHarvestedResource(player, herbIdResult, 1);
										player.playerAssistant.sendFilterableMessage("You make a " + ItemAssistant.getItemName(herbIdResult) + ".");
										if (!ItemAssistant.getItemName(herbIdResult).contains("unf"))
										{
												player.skillingStatistics[SkillingStatistics.POTIONS_MADE]++;
										}
										player.startAnimation(363);
										if (experience > 0)
										{
												Skilling.addSkillExperience(player, experience, ServerConstants.HERBLORE);
										}
										player.skillingData[4] -= 1;
										if (herbIdResult == HerbloreFinishedPotionData.RANGING.getFinishedProduct())
										{
												Achievements.checkCompletionMultiple(player, "1043");
										}
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.usingHerbloreEvent = false;
						}
				}, 1);
		}

		/**
		 * True, if the itemClicked is a grimy herb.
		 * @param player
		 * 			The associated player.
		 * @param itemClicked
		 * 			The item clicked.
		 * @param itemSlot
		 * 			The slot the item is in.
		 * @return
		 * 			True, if the itemClicked is a grimy herb.
		 */
		public static boolean isGrimyHerb(Player player, int itemClicked, int itemSlot)
		{
				for (HerbloreCleaningData herb : HerbloreCleaningData.values())
				{
						if (itemClicked == herb.getGrimyId())
						{
								cleanHerb(player, herb, itemSlot);
								return true;
						}
				}
				return false;
		}

		/**
		 * Clean the grimy herb.
		 * @param player
		 * 			The associated player.
		 * @param herb
		 * 			The herb used.
		 * @param itemSlot
		 * 			The slot the herb is in.
		 */
		private static void cleanHerb(Player player, HerbloreCleaningData herb, int itemSlot)
		{
				if (ItemAssistant.hasItemInInventory(player, herb.getGrimyId()))
				{
						if (player.baseSkillLevel[ServerConstants.HERBLORE] >= herb.getRequiredLevel())
						{
								ItemAssistant.deleteItemFromInventory(player, herb.getGrimyId(), itemSlot, 1);
								ItemAssistant.addItemToInventory(player, herb.getCleanedId(), 1, itemSlot, true);
								Skilling.addSkillExperience(player, (int) herb.getExperience(), ServerConstants.HERBLORE);
								player.playerAssistant.sendFilterableMessage("You clean the dirt from the " + ItemAssistant.getItemName(herb.getGrimyId()) + ".");
						}
						else
						{
								player.playerAssistant.sendMessage("You need a herblore level of " + herb.getRequiredLevel() + " to clean this herb.");
						}
				}
		}

		public static boolean createOverload(final Player player, int usedWith, int itemUsed)
		{
				if (usedWith != 269 && itemUsed != 269)
				{
						return false;
				}
				if (player.baseSkillLevel[ServerConstants.HERBLORE] < 96)
				{
						player.getDH().sendStatement("You need 96 herblore to create an overload.");
						return false;
				}
				final int[] extremeList = {15325, 15321, 15317, 15309, 15313, 269};
				if (player.usingHerbloreEvent)
				{
						return false;
				}
				boolean noItems = false;
				for (int index = 0; index < extremeList.length; index++)
				{
						if (!ItemAssistant.hasItemInInventory(player, extremeList[index]))
						{
								noItems = true;
								break;
						}
				}
				if (noItems)
				{
						player.playerAssistant.sendMessage("You need a full set of extreme potions (3) and a clean torstol to create an overload.");
						return false;
				}
				player.usingHerbloreEvent = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								boolean carryOn = true;
								boolean noItems = false;
								if (!player.usingHerbloreEvent)
								{
										carryOn = false;
								}
								for (int index = 0; index < extremeList.length; index++)
								{
										if (!ItemAssistant.hasItemInInventory(player, extremeList[index]))
										{
												carryOn = false;
												noItems = true;
												break;
										}
								}
								if (noItems)
								{
										player.playerAssistant.sendMessage("You need a full set of extreme potions (3) and a clean torstol to create an overload.");
										container.stop();
								}
								if (carryOn)
								{
										for (int index = 0; index < extremeList.length; index++)
										{
												ItemAssistant.deleteItemFromInventory(player, extremeList[index], 1);
										}
										Achievements.checkCompletionMultiple(player, "1073 1131");
										int chance = 0;

										if (player.isInZombiesMinigame())
										{
												chance = 0;
										}

										ItemAssistant.addItem(player, 15333, 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
										player.playerAssistant.sendFilterableMessage("You make an overload (3).");
										player.startAnimation(363);
										Skilling.addSkillExperience(player, 1000, ServerConstants.HERBLORE);
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.usingHerbloreEvent = false;
						}
				}, 2);
				return true;
		}

		public static void herbloreInterfaceAction(Player player, int amount)
		{
				player.getPA().closeInterfaces();
				player.skillingData[4] = amount;
				if (amount == 100)
				{
						player.setAmountInterface(ServerConstants.SKILL_NAME[ServerConstants.HERBLORE]);
						player.getOutStream().createFrame(27);
						return;
				}
				createPotionEvent(player, player.skillingData[0], player.skillingData[1], player.skillingData[2], player.skillingData[3]);
		}

		public static void xAmountHerbloreAction(Player player, int amount)
		{
				player.skillingData[4] = amount;
				createPotionEvent(player, player.skillingData[0], player.skillingData[1], player.skillingData[2], player.skillingData[3]);
		}


}