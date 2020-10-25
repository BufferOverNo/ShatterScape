package game.content.skilling;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.skilling.crafting.GemCrafting;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Mining.
 * @Author MGT Madness.
 */
public class Mining
{
		// Enum for pickaxe requirements and base mining timer and animation
		// enum for ores, ore base timer, ore id, xp, level requirement

		public static final int PURE_ESSENCE_ITEM = 7936;

		public static final int RUNE_ESSENCE_ITEM = 1436;

		public static final int ESSENCE_ORE = 1436;

		private static final int COPPER_ORE = 7484;

		private static final int COPPER_ORE_OTHER = 7453;

		private static final int TIN_ORE = 7485;

		private static final int TIN_ORE_OTHER = 7486;

		private static final int IRON_ORE = 7455;

		private static final int COAL = 7489;

		private static final int COAL_OTHER = 7488;

		private static final int MITHRIL_ORE = 7492;

		private static final int MITHRIL_ORE_OTHER = 7459;

		private static final int ADAMANT_ORE = 7493;

		private static final int RUNITE_ORE = 7494;

		private static final int GOLD_ORE = 7491;

		/**
		 * Store the ore id and time removed, if 60 seconds between last remove, do not, if 7 seconds between last remove, then ore doesn't exist.
		 */
		public static ArrayList<String> oreRemovedList = new ArrayList<String>();

		/**
		 * Mining object identities.
		 */
		private static int[] miningObject = {ESSENCE_ORE, COPPER_ORE, TIN_ORE, COPPER_ORE_OTHER, TIN_ORE_OTHER, IRON_ORE, COAL, COAL_OTHER, MITHRIL_ORE, MITHRIL_ORE_OTHER, ADAMANT_ORE, RUNITE_ORE, GOLD_ORE};

		/**
		 * Start the mining procedure.
		 * @param player
		 * 			The player mining.
		 */
		public static void startMining(Player player)
		{
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				if (player.isUsingMiningTimerEvent)
				{
						return;
				}
				player.pickAxeUsed = getHighestPickaxeId(player);
				if (!hasUseAblePickaxe(player))
				{
						return;
				}
				if (player.baseSkillLevel[ServerConstants.MINING] >= player.oreInformation[1])
				{
						player.playerAssistant.sendFilterableMessage("You swing your pickaxe at the rock.");
						player.miningTimer = setMiningTimer(player);
						startMiningTimerEvent(player);
						player.startAnimation(pickAxeAnimation(player));
				}
				else
				{
						player.getDH().sendStatement("You need a mining level of " + player.oreInformation[1] + " to mine this rock.");
						player.startAnimation(65535);
				}
		}

		public enum Pickaxes
		{
				BRONZE(1, 625, 1265, 9),
				IRON(1, 626, 1267, 8),
				STEEL(6, 627, 1269, 7),
				MITHRIL(21, 629, 1273, 6),
				ADAMANT(31, 628, 1271, 5),
				RUNE(41, 624, 1275, 4),
				DRAGON(61, 12188, 15259, 2),
				INFERNAL(61, 13707, 18758, 1);
				//dragon

				/**
				 * 
				 * Ints for enum
				 */
				private int levelRequired;

				private int animation;

				private int itemId;

				private int timer;

				/**
				 * 
				 * Constructor for enum
				 * @param levelRequired
				 * @param animation
				 * @param itemId
				 */
				Pickaxes(final int levelRequired, final int animation, final int itemId, final int timer)
				{
						this.levelRequired = levelRequired;
						this.animation = animation;
						this.itemId = itemId;
						this.timer = timer;
				}

				/**
				 * 
				 * Getter for level
				 * @return
				 */
				public int getLevelRequired()
				{
						return levelRequired;
				}

				/**
				 * 
				 * Getter for animation
				 * @return
				 */
				public int getAnimation()
				{
						return animation;
				}

				/**
				 * 
				 * Getter for itemId
				 * @return
				 */
				public int getItemId()
				{
						return itemId;
				}

				public int getTimer()
				{
						return timer;
				}
		}

		private static int getHighestPickaxeId(Player player)
		{
				int highest = 0;
				for (Pickaxes data : Pickaxes.values())
				{
						if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) && player.baseSkillLevel[ServerConstants.MINING] >= data.getLevelRequired())
						{
								highest = data.getItemId();
						}
				}
				return highest;
		}

		private static int setMiningTimer(Player player)
		{
				int base = 0;
				for (Pickaxes data : Pickaxes.values())
				{
						if (player.pickAxeUsed == data.getItemId())
						{
								base = data.getTimer();
						}
				}

				switch (player.getObjectId())
				{
						// Rune/pure essence.
						case ESSENCE_ORE:
								base -= 4;
								break;
						// Copper.
						case COPPER_ORE:
						case COPPER_ORE_OTHER:
						case TIN_ORE:
						case TIN_ORE_OTHER:
								base += 2;
								break;

						// Iron.
						case IRON_ORE:
								base += 3;
								break;

						// Coal.
						case COAL:
								base += 3;
								break;

						case GOLD_ORE:
								base += 3;
								break;

						// Mithril.
						case MITHRIL_ORE:
						case MITHRIL_ORE_OTHER:
								base += 4;
								break;

						// Adamant.
						case ADAMANT_ORE:
								base += 5;
								break;

						// Runite.
						case RUNITE_ORE:
								base += 6;
								break;
				}
				int randomMaximum = (int) (18 - (player.baseSkillLevel[ServerConstants.MINING] * 0.17));
				int random = Misc.random(0, randomMaximum);
				int finalTimer = base + random;
				if (finalTimer < 1)
				{
						finalTimer = 1;
				}
				return finalTimer;
		}

		/**
		 * Find the correct pick axe animation.
		 * @param player
		 * 			The player mining.
		 * @return
		 * 			The pick axe animation.
		 */
		public static int pickAxeAnimation(Player player)
		{
				int animation = 0;
				for (Pickaxes data : Pickaxes.values())
				{
						if (player.pickAxeUsed == data.getItemId())
						{
								return data.getAnimation();
						}
				}

				return animation;
		}

		private static void createEmptyOre(Player player)
		{
				// Rune essence object.
				if (player.getObjectId() == ESSENCE_ORE)
				{
						return;
				}
				boolean tinOrCopper = player.getObjectId() == TIN_ORE || player.getObjectId() == COPPER_ORE || player.getObjectId() == COPPER_ORE_OTHER || player.getObjectId() == TIN_ORE_OTHER;
				if (!tinOrCopper && !Misc.hasOneOutOf(20))
				{
						return;
				}
				long timeValue = 0;
				int listIndex = -1;
				for (int index = 0; index < oreRemovedList.size(); index++)
				{
						String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
						if (oreRemovedList.get(index).contains(match))
						{
								// Time ore removed.
								String time = oreRemovedList.get(index).replace(match + " ", "");
								timeValue = Long.parseLong(time);
								listIndex = index;
								break;
						}
				}

				if (System.currentTimeMillis() - timeValue <= 60000 && !tinOrCopper)
				{
						return;
				}

				if (listIndex >= 0)
				{
						oreRemovedList.remove(listIndex);
				}
				new Object(10081, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), 12);
				oreRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
				player.playerAssistant.stopAllActions();
		}

		public static boolean oreExists(Player player)
		{
				long timeValue = 0;
				for (int index = 0; index < oreRemovedList.size(); index++)
				{
						String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
						if (oreRemovedList.get(index).contains(match))
						{
								// Time ore removed.
								String time = oreRemovedList.get(index).replace(match + " ", "");
								timeValue = Long.parseLong(time);
								break;
						}
				}
				if (System.currentTimeMillis() - timeValue <= 7000)
				{
						player.playerAssistant.stopAllActions();
						return false;
				}
				return true;
		}

		/**
		 * Mine the ore and resume mining.
		 * @param player
		 * 			The player mining.
		 */
		public static void mineOre(Player player)
		{
				if (player.oreInformation[0] == RUNE_ESSENCE_ITEM && player.baseSkillLevel[ServerConstants.MINING] >= 30)
				{
						player.oreInformation[0] = PURE_ESSENCE_ITEM;
				}


				if (ItemAssistant.addItem(player, player.oreInformation[0], 1))
				{
						// Adamant ore.
						if (player.oreInformation[0] == 449)
						{
								Achievements.checkCompletionMultiple(player, "1039");
						}
						player.startAnimation(pickAxeAnimation(player));
						if (player.getObjectId() == 2491)
						{
								player.playerAssistant.sendFilterableMessage("You manage to mine some essence.");
						}
						else
						{
								player.playerAssistant.sendFilterableMessage("You manage to mine some ore.");
						}
						Skilling.addHarvestedResource(player, player.oreInformation[0], 1);
						int experience = player.oreInformation[2];

						Skilling.addSkillExperience(player, experience, ServerConstants.MINING);
						player.miningTimer = setMiningTimer(player);
						startMiningTimerEvent(player);
						player.skillingStatistics[SkillingStatistics.ORES_MINED]++;
						createEmptyOre(player);
						giveGem(player);
				}
				else
				{
						player.playerAssistant.stopAllActions();
						player.startAnimation(65535);
				}
		}

		private static void giveGem(Player player)
		{
				if (!Misc.hasPercentageChance(player.getObjectId() == 2491 ? 1 : 3))
				{
						return;
				}
				int random = Misc.random(1, 100);
				int item = 0;
				if (random < 10)
				{
						item = GemCrafting.gemData[3][0];
				}
				else if (random < 30)
				{
						item = GemCrafting.gemData[2][0];
				}
				else if (random < 60)
				{
						item = GemCrafting.gemData[1][0];
				}
				else
				{
						item = GemCrafting.gemData[0][0];
				}
				ItemAssistant.addItem(player, item, 1);
		}

		public static boolean hasUseAblePickaxe(Player player)
		{
				if (player.pickAxeUsed == 0)
				{
						player.getPA().sendMessage("You do not have a pickaxe.");
						return false;
				}
				int highest = 0;
				for (Pickaxes data : Pickaxes.values())
				{
						if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) && player.baseSkillLevel[ServerConstants.MINING] >= data.getLevelRequired())
						{
								highest = data.getItemId();
						}
				}
				if (highest == 0)
				{
						for (Pickaxes data : Pickaxes.values())
						{
								if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) && player.baseSkillLevel[ServerConstants.MINING] < data.getLevelRequired())
								{
										player.getDH().sendStatement("You need a mining level of " + data.getLevelRequired() + " to use this pickaxe.");
										return false;
								}
						}
				}

				return true;
		}

		/**
		 * @return true, if the object is an ore.
		 */
		public static boolean isMiningObject(final int objectType)
		{
				for (int i = 0; i < miningObject.length; i++)
				{
						if (objectType == miningObject[i])
						{
								return true;
						}
				}

				return false;
		}

		/**
		 * Perform actions of mining related objects.
		 */
		public static void doMiningObject(final Player player, int objectType)
		{
				if (player.getTransformed() > 0)
				{
						return;
				}

				if (objectType == 2491)
				{
						player.oreInformation[0] = RUNE_ESSENCE_ITEM; //Ore id
						player.oreInformation[1] = 1; //Level required
						player.oreInformation[2] = 5; //Xp
						startMining(player);
				}
				else if (objectType == COPPER_ORE || objectType == COPPER_ORE_OTHER)
				{
						player.oreInformation[0] = 436; //Ore id
						player.oreInformation[1] = 1; //Level required
						player.oreInformation[2] = 18; //Xp
						startMining(player);
				}
				else if (objectType == TIN_ORE || objectType == TIN_ORE_OTHER)
				{
						player.oreInformation[0] = 438;
						player.oreInformation[1] = 1;
						player.oreInformation[2] = 18;
						startMining(player);
				}
				else if (objectType == IRON_ORE)
				{
						player.oreInformation[0] = 440;
						player.oreInformation[1] = 15;
						player.oreInformation[2] = 35;
						startMining(player);
				}
				else if (objectType == COAL || objectType == COAL_OTHER)
				{
						player.oreInformation[0] = 453;
						player.oreInformation[1] = 30;
						player.oreInformation[2] = 50;
						startMining(player);
				}

				else if (objectType == MITHRIL_ORE || objectType == MITHRIL_ORE_OTHER)
				{
						player.oreInformation[0] = 447;
						player.oreInformation[1] = 50;
						player.oreInformation[2] = 80;
						startMining(player);
				}

				else if (objectType == ADAMANT_ORE)
				{
						player.oreInformation[0] = 449;
						player.oreInformation[1] = 70;
						player.oreInformation[2] = 95;
						startMining(player);
				}

				else if (objectType == RUNITE_ORE)
				{
						player.oreInformation[0] = 451;
						player.oreInformation[1] = 85;
						player.oreInformation[2] = 125;
						startMining(player);
				}

				else if (objectType == GOLD_ORE)
				{
						player.oreInformation[0] = 444;
						player.oreInformation[1] = 40;
						player.oreInformation[2] = 65;
						startMining(player);
				}
		}

		/**
		 * Decrease the miningTimer variable untill it reaches 0.
		 */
		private static void startMiningTimerEvent(final Player player)
		{

				/* Check if this event is being used, if it is, then stop */
				if (player.isUsingMiningTimerEvent)
				{
						return;
				}
				player.isUsingMiningTimerEvent = true;
				/* The event is continious untill wcTimer reaches 0. */
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (!player.isUsingMiningTimerEvent)
								{
										container.stop();
										return;
								}
								if (player.miningTimer > 0 && player.oreInformation[0] > 0 && oreExists(player))
								{
										player.miningTimer--;
										if (player.miningTimer == 0)
										{
												mineOre(player);
										}
										player.startAnimation(pickAxeAnimation(player));
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.isUsingMiningTimerEvent = false;
								player.startAnimation(65535);
						}
				}, 1);

		}



}