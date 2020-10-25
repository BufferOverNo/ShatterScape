package game.content.consumable;

import java.util.HashMap;

import core.ServerConstants;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * @author Sanity
 */

public class Food
{

		public static enum FoodToEat
		{
				BANANA(1963, 2, "Banana"),
				COOKED_CHICKEN(2140, 3, "Cooked chicken"),
				PURPLE_SWEETS(10476, 3, "Purple sweets"),
				COOKED_MEAT(2142, 3, "Cooked meat"),
				CAKE(1891, 4, "Cake"),
				CAKE_TWO_THIRDS(1893, 4, "2/3 cake"),
				SLICE_OF_CAKE(1895, 4, "Slice of cake"),
				CHOCOLATE_CAKE(1897, 4, "Cake"),
				CHOCOLATE_CAKE_TWO_THIRDS(1899, 4, "2/3 chocolate cake"),
				SLICE_OF_CHOCOLATE_CAKE(1901, 4, "Slice of chocolate cake"),
				SHRIMP(315, 3, "Shrimp"),
				CABBAGE(1965, 1, "Cabbage"),
				TROUT(333, 7, "Trout"),
				PEACH(6883, 8, "Peach"),
				SALMON(329, 9, "Salmon"),
				TUNA(361, 10, "Tuna"),
				LOBSTER(379, 12, "Lobster"),
				BASS(365, 13, "Bass"),
				SWORDFISH(373, 14, "Swordfish"),
				MONKFISH(7946, 16, "Monkfish"),
				SHARK(385, 20, "Shark"),
				DARK_CAB(18637, 22, "Dark crab");

				private int id;

				private int heal;

				private String name;

				private FoodToEat(int id, int heal, String name)
				{
						this.id = id;
						this.heal = heal;
						this.name = name;
				}

				public int getId()
				{
						return id;
				}

				public int getHeal()
				{
						return heal;
				}

				public String getName()
				{
						return name;
				}

				public static HashMap<Integer, FoodToEat> food = new HashMap<Integer, FoodToEat>();

				public static FoodToEat forId(int id)
				{
						return food.get(id);
				}

				static
				{
						for (FoodToEat f : FoodToEat.values())
								food.put(f.getId(), f);
				}
		}

		public static void eat(Player player, int id, int slot)
		{

				BotContent.addBotDebug(player, "Here31.5");
				if (player.duelRule[6])
				{
						player.playerAssistant.sendMessage("You may not eat in this duel.");
						return;
				}
				BotContent.addBotDebug(player, "Here31.6");
				if (player.getDead())
				{
						player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
						return;
				}
				if (System.currentTimeMillis() - player.cannotEatDelay < 1700)
				{
						return;
				}
				if (System.currentTimeMillis() - player.foodDelay < 1700)
				{
						return;
				}
				BotContent.addBotDebug(player, "Here31.7");
				if (player.doingAnAction())
				{
						return;
				}
				BotContent.addBotDebug(player, "Here31.8: " + (System.currentTimeMillis() - player.foodDelay));
				BotContent.addBotDebug(player, "Here31.9");
				BotContent.handleSafing(player);
				player.playerAssistant.stopAllActions();
				Combat.resetPlayerAttack(player);
				player.setAttackTimer(player.getAttackTimer() + 2);
				player.startAnimation(829);
				ItemAssistant.deleteItemFromInventory(player, id, slot, 1);
				FoodToEat food = FoodToEat.food.get(id);
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < (food.getId() == 15272 ? (player.getBaseHitPointsLevel() * 1.1) : player.getBaseHitPointsLevel()))
				{
						player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += food.getHeal();
						if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > (food.getId() == 15272 ? (player.getBaseHitPointsLevel() * 1.1) : player.getBaseHitPointsLevel()))
						{
								player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = (int) (food.getId() == 15272 ? (player.getBaseHitPointsLevel() * 1.1) : player.getBaseHitPointsLevel());
						}
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
						RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
				}
				player.foodDelay = System.currentTimeMillis();
				if (food.getId() == FoodToEat.CABBAGE.getId())
				{
						player.playerAssistant.sendFilterableMessage("Yuck!");
				}
				else
				{
						player.playerAssistant.sendFilterableMessage("You eat the " + food.getName() + ".");
						player.playerAssistant.sendFilterableMessage("It heals some health.");
				}
				player.soundToSend = 317;
				player.soundDelayToSend = 400;
				player.foodAte++;
				if (food.getName().toLowerCase().contains("cake") && food.getId() != 1895 && food.getId() != 1901)
				{
						ItemAssistant.addItemToInventory(player, food.getId() + 2, 1, slot, false);
				}
				player.setInventoryUpdate(true);
		}


		public static boolean isFood(int id)
		{
				return FoodToEat.food.containsKey(id);
		}


}