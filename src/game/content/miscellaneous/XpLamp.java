package game.content.miscellaneous;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Xp lamp.
 * @author Mgt Madness, created on 26-08-2016.
 */
public class XpLamp
{

		private final static int[] xpLampButtons = {10252, 10253, 10254, 10255, 11000, 11001, 11002, 11003, 11004, 11005, 11006, 11007, 47002, 54090, 11008, 11009, 11010, 11011, 11012, 11013, 11014};

		private final static int[] skillOrder = {0, 2, 4, 6, 1, 3, 5, 16, 15, 17, 12, 20, 18, 19, 14, 13, 10, 7, 11, 8, 9};

		public static boolean xpLampButton(Player player, int button)
		{
				for (int index = 0; index < xpLampButtons.length; index++)
				{
						if (xpLampButtons[index] == button)
						{
								boolean hasItem = ItemAssistant.hasItemInInventory(player, 18742) ? true : false;
								if (!hasItem)
								{
										return true;
								}
								if (player.getDuelStatus() >= 1)
								{
										return true;
								}
								if (System.currentTimeMillis() - player.xpLampUsedTime <= 43200000)
								{
										int minutes = (int) ((43200000 - (System.currentTimeMillis() - player.xpLampUsedTime)) / 60000);
										String string = "minutes";
										if (minutes > 59)
										{
												minutes /= 60;
												string = "hours";
												if (minutes <= 1)
												{
														string = "hour";
												}
										}
										player.getPA().sendMessage("You may use the Xp lamp in " + minutes + " " + string + ".");
										return true;
								}
								int itemId = 18742;
								String amount = "500k";
								int amount1 = 25000;
								player.getPA().closeInterfaces();
								ItemAssistant.deleteItemFromInventory(player, itemId, 1);
								player.xpLampUsed = true;
								player.xpLampUsedTime = System.currentTimeMillis();
								Skilling.addSkillExperience(player, amount1, skillOrder[index]);
								player.getPA().sendMessage("You rub the lamp and receive " + amount + " experience in " + ServerConstants.SKILL_NAME[skillOrder[index]] + ".");
								player.getPA().sendMessage("You may rub another lamp in 12 hours time.");
								return true;
						}
				}
				return false;
		}

}
