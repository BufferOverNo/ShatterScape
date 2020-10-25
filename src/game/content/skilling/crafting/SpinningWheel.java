package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Spinning wheel.
 * @author MGT Madness, created on 04-08-2016.
 */
public class SpinningWheel
{
		private static enum SpinningData
		{
				WOOL(1737, 1759, 1, 3),
				FLAX(1779, 1777, 10, 15),
				SINEW(9436, 9438, 10, 15);

				private int used;

				private int result;

				private int level;

				private int experience;

				private SpinningData(int used, int result, int level, int experience)
				{
						this.used = used;
						this.result = result;
						this.level = level;
						this.experience = experience;
				}

				public int getRawMaterial()
				{
						return used;
				}

				public int getSpinnedProduct()
				{
						return result;
				}

				public int getLevelRequired()
				{
						return level;
				}

				public int getExperience()
				{
						return experience;
				}
		}

		public static void spinningWheel(final Player player, int itemUsed)
		{
				for (SpinningData data : SpinningData.values())
				{
						if (itemUsed == data.getRawMaterial())
						{
								spinningWheelAction(player, data);
								break;
						}
				}
		}

		private static void spinningWheelAction(final Player player, final SpinningData data)
		{

				if (player.baseSkillLevel[ServerConstants.CRAFTING] < data.getLevelRequired())
				{
						player.getDH().sendStatement("You need a crafting level of " + data.getLevelRequired() + " to spin " + ItemAssistant.getItemName(data.getRawMaterial()) + ".");
						return;
				}
				if (Skilling.canActivateNewSkillingEvent(player, "SPINNING WHEEL"))
				{
						return;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (!Skilling.isSkillingEventActive(player, "SPINNING WHEEL"))
								{
										container.stop();
										return;
								}
								if (!ItemAssistant.hasItemAmountInInventory(player, data.getRawMaterial(), 1))
								{
										container.stop();
										return;
								}
								ItemAssistant.deleteItemFromInventory(player, data.getRawMaterial(), 1);
								ItemAssistant.addItem(player, data.getSpinnedProduct(), 1);
								Skilling.addSkillExperience(player, data.getExperience(), ServerConstants.CRAFTING);
								player.getPA().sendFilterableMessage("You spin some " + ItemAssistant.getItemName(data.getRawMaterial()) + ".");
								player.startAnimation(894);
						}

						@Override
						public void stop()
						{
								player.getPA().stopAllActions();
						}
				}, 3);
		}
}
