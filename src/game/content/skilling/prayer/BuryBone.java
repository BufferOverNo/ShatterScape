package game.content.skilling.prayer;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.movement.Follow;

public class BuryBone
{

		public static void buryBone(Player player, int id, int slot)
		{
				if (System.currentTimeMillis() - player.buryDelay > 900)
				{
						if (System.currentTimeMillis() - player.timeAttackedAnotherPlayer <= 8000)
						{
								player.getPA().sendMessage("You cannot bury bones while under attack by another player.");
								return;
						}
						// Dagannoth bone.
						if (id == 6729)
						{
								Achievements.checkCompletionSingle(player, 1025);
						}
						ItemAssistant.deleteItemFromInventory(player, id, slot, 1);
						player.playerAssistant.sendFilterableMessage("You bury the bones.");
						player.skillingStatistics[SkillingStatistics.BONES_BURIED]++;
						for (int i = 0; i < bonesExp.length; i++)
						{
								if (bonesExp[i][0] == id)
								{
										Skilling.addSkillExperience(player, bonesExp[i][1], ServerConstants.PRAYER);
								}
						}

						// Normal bone in Monastery.
						if (id == 526 && Area.isWithInArea(player, 3044, 3059, 3481, 3500))
						{
								Achievements.checkCompletionSingle(player, 1013);
						}
						else // Dagannoth bone in Monastery.
						if (id == 6729 && Area.isWithInArea(player, 3044, 3059, 3481, 3500))
						{
								Achievements.checkCompletionMultiple(player, "1056");
						}
						Combat.resetPlayerAttack(player);
						Follow.resetFollow(player);
						player.buryDelay = System.currentTimeMillis();
						player.startAnimation(827);
						SoundSystem.sendSound(player, 380, 700);
						player.getPA().requestUpdates();
						player.getPA().stopAllActions();
				}
		}

		public static int[][] bonesExp = {
				{526, 5},
				{530, 6},
				{532, 15},
				{532, 15},

				// Longbone.
				{10976, 15},

				// Curved bone.
				{10977, 15},
				{534, 30},
				{536, 72},

				// Lava dragon bone
				{18821, 85},
				{6729, 125}};

		public static boolean isBone(int id)
		{
				for (int j = 0; j < bonesExp.length; j++)
				{
						if (bonesExp[j][0] == id)
						{
								return true;
						}
				}
				return false;
		}

}