package game.content.skilling;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.music.SoundSystem;
import game.content.starter.GameMode;
import game.content.wildernessbonus.WildernessRisk;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.pet.Pet;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Fishing.
 * @author MGT Madness, re-written on 22-07-2014.
 **/

public class Fishing
{

		public static long timeFishingSpotChanged1;

		public static long timeFishingSpotChanged2;

		public static final int RAW_SHRIMP = 317;

		public static final int RAW_TROUT = 335;

		public static final int RAW_SALMON = 331;

		public static final int RAW_LOBSTER = 377;

		public static final int RAW_TUNA = 359;

		public static final int RAW_SWORDFISH = 371;

		public static final int RAW_MONKFISH = 7944;

		public static final int RAW_SHARK = 383;

		public static final int RAW_DARK_CRAB = 18635;

		private static final int[] REQUIREMENTS = {1, 20, 30, 35, 40, 50, 60, 70, 85};

		private static final int[] FISH_TYPES = {RAW_SHRIMP, RAW_TROUT, RAW_SALMON, RAW_TUNA, RAW_LOBSTER, RAW_SWORDFISH, RAW_MONKFISH, RAW_SHARK, RAW_DARK_CRAB};

		private static final int[] EXPERIENCE = {10, 50, 70, 80, 90, 100, 120, 140, 190};


		public static ArrayList<String> currentFishingSpots = new ArrayList<String>();

		public static ArrayList<String> darkCrabCurrentFishingSpots = new ArrayList<String>();

		public static void fillCurrentFishingSpots()
		{
				currentFishingSpots.add("2876 3342");
				currentFishingSpots.add("2879 3339");
				currentFishingSpots.add("2879 3335");
				currentFishingSpots.add("2876 3331");
				darkCrabCurrentFishingSpots.add("3045 3702");
		}

		private static void moveFishingSpot(Player player, Npc npc)
		{
				ArrayList<String> newArray1 = new ArrayList<String>();

				// Dak crab.
				if (npc.npcType == 325)
				{
						newArray1.add("3050 3704");
						newArray1.add("3052 3705");
						newArray1.add("3044 3700");
						newArray1.add("3047 3699");
						newArray1.add("3052 3697");
				}
				else
				{
						newArray1.add("2875 3342");
						newArray1.add("2876 3342");
						newArray1.add("2877 3342");
						newArray1.add("2879 3339");
						newArray1.add("2879 3338");
						newArray1.add("2879 3335");
						newArray1.add("2879 3334");
						newArray1.add("2877 3331");
						newArray1.add("2876 3331");
						newArray1.add("2875 3331");
				}

				String currentCoordinates = Integer.toString(npc.getX()) + " " + Integer.toString(npc.getY());
				String newCoordinate = "";
				if (npc.npcType == 325)
				{
						for (int i = 0; i < darkCrabCurrentFishingSpots.size(); i++)
						{
								if (newArray1.contains(darkCrabCurrentFishingSpots.get(i)))
								{
										newArray1.remove(darkCrabCurrentFishingSpots.get(i));
								}
						}
						darkCrabCurrentFishingSpots.remove(currentCoordinates);
						newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
						darkCrabCurrentFishingSpots.add(newCoordinate);
				}
				else
				{
						for (int i = 0; i < currentFishingSpots.size(); i++)
						{
								if (newArray1.contains(currentFishingSpots.get(i)))
								{
										newArray1.remove(currentFishingSpots.get(i));
								}
						}
						currentFishingSpots.remove(currentCoordinates);
						newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
						currentFishingSpots.add(newCoordinate);
				}

				int newX = Integer.parseInt(newCoordinate.substring(0, 4));
				int newY = Integer.parseInt(newCoordinate.substring(5, 9));
				int npcType = npc.npcType;
				Pet.deletePet(npc);
				Pet.summonNpc(player, npcType, newX, newY, 0, false);
		}

		private static int getAnimation(int fish)
		{
				switch (fish)
				{

						case RAW_SHARK:
						case RAW_TUNA:
						case RAW_SWORDFISH:
								return 618;

						case RAW_SHRIMP:
								return 621;

						case RAW_TROUT:
						case RAW_SALMON:
								return 7261;

						case RAW_LOBSTER:
						case RAW_DARK_CRAB:
								return 619;

						case RAW_MONKFISH:
								return 620;
				}

				return 0;

		}

		private static int getItemRequirement(int fish)
		{
				switch (fish)
				{

						case RAW_SHARK:
						case RAW_TUNA:
						case RAW_SWORDFISH:
								return 311;

						case RAW_TROUT:
						case RAW_SALMON:
								return 309;

						case RAW_SHRIMP:
								return 303;

						case RAW_LOBSTER:
						case RAW_DARK_CRAB:
								return 301;

						case RAW_MONKFISH:
								return 305;
				}

				return 0;

		}

		/**
		 * Start fishing.
		 * @param player
		 * 			The associated player.
		 * @param fishType
		 * 			The type of fish being fished.
		 */
		public static void startFishing(Player player, int fishType)
		{
				int fishOrder = 0;
				for (int b = 0; b < FISH_TYPES.length; b++)
				{
						if (FISH_TYPES[b] == fishType)
						{
								fishOrder = b;
								break;
						}
				}
				setInitialFishingSpotCoordinates(player);
				if (!fishingRequirements(player, fishOrder))
				{
						stopFishing(player);
						player.startAnimation(65535);
						return;
				}
				SoundSystem.sendSound(player, 289, 500);
				startFishTimerEvent(player, fishOrder);

		}

		private static void setInitialFishingSpotCoordinates(Player player)
		{
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
						{
								continue;
						}
						if (NpcHandler.npcs[i].npcType == player.getNpcType() && !Area.inWilderness(NpcHandler.npcs[i].getX(), NpcHandler.npcs[i].getY()))
						{
								player.lastFishingSpotX = NpcHandler.npcs[i].getX();
								player.lastFishingSpotY = NpcHandler.npcs[i].getY();
						}
				}
		}

		private static void findNewFishingSpot(Player player)
		{
				if (Area.inResourceWilderness(player))
				{
						return;
				}
				if (Misc.hasPercentageChance(95))
				{
						return;
				}
				if (System.currentTimeMillis() - (player.getNpcType() == 325 ? timeFishingSpotChanged2 : timeFishingSpotChanged1) < 40000)
				{
						return;
				}

				if (player.getNpcType() == 325)
				{
						timeFishingSpotChanged2 = System.currentTimeMillis();
				}
				else
				{
						timeFishingSpotChanged1 = System.currentTimeMillis();
				}
				for (int j = 0; j < NpcHandler.npcs.length; j++)
				{
						if (NpcHandler.npcs[j] != null)
						{
								if (NpcHandler.npcs[j].npcType == player.getNpcType() && NpcHandler.npcs[j].getX() == player.lastFishingSpotX && NpcHandler.npcs[j].getY() == player.lastFishingSpotY)
								{
										moveFishingSpot(player, NpcHandler.npcs[j]);
										break;
								}
						}
				}
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @param fishOrder
		 * 			The fish being fished.
		 * @return
		 * 			True, if the player has all the requirements to start fishing.
		 */
		public static boolean fishingRequirements(Player player, int fishOrder)
		{
				boolean needFeather = FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON ? true : false;
				if (!ItemAssistant.hasItemInInventory(player, getItemRequirement(FISH_TYPES[fishOrder])))
				{
						if (needFeather)
						{
								player.getDH().sendStatement("You need a Fishing rod to fish Trout/Salmon.");
								return false;
						}
						player.getDH().sendStatement("You need a " + ItemAssistant.getItemName(getItemRequirement(FISH_TYPES[fishOrder])) + " to fish " + NpcDefinition.getDefinitions()[player.getNpcType()].name + ".");
						return false;
				}
				if (needFeather && !ItemAssistant.hasItemInInventory(player, 314))
				{
						player.getDH().sendStatement("You have run out of feathers.");
						return false;
				}
				if (ItemAssistant.getFreeInventorySlots(player) == 0)
				{
						player.playerAssistant.sendMessage("You do not have free inventory space.");
						return false;
				}
				if (player.baseSkillLevel[ServerConstants.FISHING] < REQUIREMENTS[fishOrder])
				{
						player.getDH().sendStatement("You need a fishing level of " + REQUIREMENTS[fishOrder] + " to catch this fish.");
						return false;
				}
				if (FISH_TYPES[fishOrder] == RAW_DARK_CRAB && !WildernessRisk.hasWildernessActivityRisk(player, 200))
				{
						return false;
				}
				if (!Area.inResourceWilderness(player))
				{
						for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
						{
								if (NpcHandler.npcs[i] == null)
								{
										continue;
								}
								if (NpcHandler.npcs[i].npcType == player.getNpcType() && !Area.inWilderness(NpcHandler.npcs[i].getX(), NpcHandler.npcs[i].getY()))
								{
										if (NpcHandler.npcs[i].getX() != player.lastFishingSpotX || NpcHandler.npcs[i].getY() != player.lastFishingSpotY)
										{
												return false;
										}
								}
						}
				}
				return true;
		}

		private static int setFishingTimer(Player player)
		{
				int timer = 0;

				switch (player.getNpcType())
				{

						// Shrimp.
						case 316:
								timer += 3;
								break;

						// Lobster.
						case 324:
								timer += 3;
								break;

						// Monk fish.
						case 326:
								timer += 4;
								break;

						//Sharks
						case 334:
								timer += 5;
								break;

						// Dark crab.
						case 325:
								timer += 8;
								break;
				}

				int value = 30;
				int maximum = (int) (value - (player.baseSkillLevel[ServerConstants.FISHING] * (value / 99.0))) + timer;
				int baseMinimum = maximum / 2;
				timer = Misc.random(baseMinimum, maximum);
				return timer;
		}

		/**
		 * Start the fishing cycle event.
		 * @param player
		 * 			The associated player.
		 * @param fishOrder
		 * 			The order of the fish in the array being fished.
		 */
		private static void startFishTimerEvent(final Player player, final int fishOrder)
		{

				if (player.isUsingFishTimerEvent)
				{
						return;
				}
				player.isUsingFishTimerEvent = true;
				player.fishTimerAmount = setFishingTimer(player);
				player.startAnimation(getAnimation(FISH_TYPES[fishOrder]));
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (player.fishTimerAmount > 0)
								{
										player.fishTimerAmount--;
										player.startAnimation(getAnimation(FISH_TYPES[fishOrder]));
								}
								else
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								player.isUsingFishTimerEvent = false;
								if (player.fishTimerAmount == 0)
								{
										catchFish(player, fishOrder);
								}
						}
				}, 1);

		}

		/**
		 * Force stop fishing.
		 * @param player
		 * 			The associated player.
		 */
		public static void stopFishing(Player player)
		{
				player.fishTimerAmount = -1;
		}

		/**
		 * Successfully catch the fish.
		 * @param player
		 * 			The associated player.
		 * @param fishOrder
		 * 			The order of the fish used in the fish array.
		 */
		public static void catchFish(Player player, int fishOrder)
		{
				if (!fishingRequirements(player, fishOrder))
				{
						stopFishing(player);
						player.startAnimation(65535);
						return;
				}
				if (FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON)
				{
						int chanceForSalmon = 0;
						int level = player.baseSkillLevel[ServerConstants.FISHING];
						if (level >= 30)
						{
								chanceForSalmon = 99 - level;
								chanceForSalmon += 15;
								chanceForSalmon = 100 - chanceForSalmon;
								if (Misc.hasPercentageChance(chanceForSalmon))
								{
										fishOrder = 2; // Changed to Salmon.
								}
								else
								{
										fishOrder = 1;
								}
						}
				}
				if (FISH_TYPES[fishOrder] == RAW_TUNA || FISH_TYPES[fishOrder] == RAW_SWORDFISH)
				{
						int chanceForSwordy = 0;
						int level = player.baseSkillLevel[ServerConstants.FISHING];
						if (level >= 50)
						{
								chanceForSwordy = 99 - level;
								chanceForSwordy += 15;
								chanceForSwordy = 100 - chanceForSwordy;
								if (Misc.hasPercentageChance(chanceForSwordy))
								{
										fishOrder = 5; // Changed to Swordfish.
								}
								else
								{
										fishOrder = 3;
								}
						}
				}
				if (FISH_TYPES[fishOrder] == RAW_SHARK)
				{
						Achievements.checkCompletionMultiple(player, "1036");
						if (GameMode.getGameMode(player, "IRON MAN"))
						{
								Achievements.checkCompletionSingle(player, 1031);
						}
				}
				else if (FISH_TYPES[fishOrder] == RAW_DARK_CRAB)
				{
						Achievements.checkCompletionMultiple(player, "1062 1128");
				}

				boolean needFeather = FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON ? true : false;
				if (needFeather)
				{
						ItemAssistant.deleteItemFromInventory(player, 314, 1);
				}
				player.skillingStatistics[SkillingStatistics.FISH_CAUGHT]++;
				int chance = 0;


				SoundSystem.sendSound(player, 378, 500);
				ItemAssistant.addItem(player, FISH_TYPES[fishOrder], 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
				Skilling.addHarvestedResource(player, FISH_TYPES[fishOrder], 1);
				Skilling.addSkillExperience(player, (int) (EXPERIENCE[fishOrder] * 1.05), ServerConstants.FISHING);
				player.playerAssistant.sendFilterableMessage("You catch a fish.");
				findNewFishingSpot(player);
				startFishing(player, FISH_TYPES[fishOrder]);
		}
}