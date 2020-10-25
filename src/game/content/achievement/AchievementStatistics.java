package game.content.achievement;


import core.ServerConstants;
import game.content.highscores.HighscoresBerserker;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.highscores.HighscoresHybrid;
import game.content.highscores.HighscoresMelee;
import game.content.highscores.HighscoresPker;
import game.content.highscores.HighscoresPure;
import game.content.highscores.HighscoresRangedTank;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.PvpTask;
import game.content.miscellaneous.QuestTab;
import game.content.worldevent.WorldEvent;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Handle how the achievement statistics are given.
 * @author MGT Madness, created on 06-07-2015.
 */
public class AchievementStatistics
{

		public final static int MELEE_MAIN = 0;

		public final static int HYBRID = 1;

		public final static int BERSERKER = 2;

		public final static int PURE = 3;

		public final static int RANGED_TANK = 4;

		public static void startNewFight(Player attacker, Player victim, String combatType)
		{
				if (!attacker.lastPlayerAttackedName.equals(victim.getPlayerName()))
				{
						attacker.timeFightStarted = System.currentTimeMillis();
						attacker.lastPlayerAttackedName = victim.getPlayerName();
				}
				else if (System.currentTimeMillis() - victim.timeUnderAttackByAnotherPlayerAchievement > 10000 && System.currentTimeMillis() - attacker.timeAttackedAnotherPlayerAchievement > 10000)
				{
						attacker.timeFightStarted = System.currentTimeMillis();
				}
				victim.timeUnderAttackByAnotherPlayerAchievement = System.currentTimeMillis();
				attacker.timeAttackedAnotherPlayerAchievement = System.currentTimeMillis();
				switch (combatType)
				{
						case "RANGED":
								attacker.timeRangedUsed = System.currentTimeMillis();
								break;
						case "MELEE":
								attacker.timeMeleeUsed = System.currentTimeMillis();
								break;

						case "MAGIC":
								attacker.timeMagicUsed = System.currentTimeMillis();
								break;
				}
		}


		private static boolean timeIsClose(long time1)
		{
				if (System.currentTimeMillis() - time1 <= 25000)
				{
						return true;
				}
				return false;
		}

		public static void updateAchievementStatistics(Player player, boolean isAKill, Player other)
		{
				// Player is killer and other is victim if isAKill is true.
				// other becomes null if isAKill is false.
				int combatStyles = 0;
				if (System.currentTimeMillis() - player.timeMagicUsed <= 25000)
				{
						combatStyles++;
				}
				if (System.currentTimeMillis() - player.timeMeleeUsed <= 25000)
				{
						combatStyles++;
				}
				if (System.currentTimeMillis() - player.timeRangedUsed <= 25000)
				{
						combatStyles++;
				}
				if (combatStyles >= 2 && System.currentTimeMillis() - player.timeMagicUsed <= 25000)
				{
						if (isAKill)
						{
								player.setHybridKills(player.getHybridKills() + 1);
								HighscoresHybrid.getInstance().sortHighscores(player);
								PlayerTitle.updateLatestTitleForPking(player, "Hybrid");
								Achievements.checkCompletionMultiple(player, "1 2 3 4 5");
								if (player.getHybridKills() == 100)
								{
										HighscoresHallOfFame.enterToHallOfFame(player, "Kill 100 Hybrids");
								}
								if (HighscoresDaily.getInstance().currentDailyHighscores.equals("Hybrid kills"))
								{
										HighscoresDaily.getInstance().sortHighscores(player, 1, player.getHybridKills(), QuestTab.getKDR(player.getHybridKills(), player.deathTypes[1]) + "");
								}
						}
						else
						{
								player.deathTypes[HYBRID]++;
								HighscoresHybrid.getInstance().sortHighscores(player);
						}
				}

				if (player.getCombatLevel() >= 122)
				{
						PvpTask.pvpKill(player, 3);
				}

				if (player.getCombatLevel() >= 122 && timeIsClose(player.timeMeleeUsed) && System.currentTimeMillis() - player.timeRangedUsed >= 40000 && System.currentTimeMillis() - player.timeMagicUsed >= 40000)
				{
						if (isAKill)
						{
								player.setMeleeMainKills(player.getMeleeMainKills() + 1);
								HighscoresMelee.getInstance().sortHighscores(player);
								PlayerTitle.updateLatestTitleForPking(player, "Melee");
								Achievements.checkCompletionMultiple(player, "21 22 23 24 25");
								if (player.getMeleeMainKills() == 100)
								{
										HighscoresHallOfFame.enterToHallOfFame(player, "Kill 100 Melee Mains");
								}
						}
						else
						{
								player.deathTypes[MELEE_MAIN]++;
								HighscoresMelee.getInstance().sortHighscores(player);
						}
						return;
				}

				else if (player.getBaseDefenceLevel() >= 40 && player.getBaseDefenceLevel() <= 50)
				{
						if (isAKill)
						{
								player.setBerserkerPureKills(player.getBerserkerPureKills() + 1);
								HighscoresBerserker.getInstance().sortHighscores(player);
								PlayerTitle.updateLatestTitleForPking(player, "Berserker");
								Achievements.checkCompletionMultiple(player, "6 7 8 9 10");
								PvpTask.pvpKill(player, 1);
								if (WorldEvent.getActiveEvent("BERSERKER PK"))
								{
										Artefacts.dropArtefactsAmount(player, other, 3);
								}
								if (player.getBerserkerPureKills() == 100)
								{
										HighscoresHallOfFame.enterToHallOfFame(player, "Kill 100 Zerks");
								}
						}
						else
						{
								player.deathTypes[BERSERKER]++;
								HighscoresBerserker.getInstance().sortHighscores(player);
						}
				}

				else if (player.getBaseDefenceLevel() >= 70 && timeIsClose(player.timeRangedUsed) && (player.getBaseStrengthLevel() + player.getBaseAttackLevel()) <= 140)
				{
						if (isAKill)
						{
								player.setRangedTankKills(player.getRangedTankKills() + 1);
								HighscoresRangedTank.getInstance().sortHighscores(player);
								PlayerTitle.updateLatestTitleForPking(player, "Ranged");
								Achievements.checkCompletionMultiple(player, "11 12 13 14 15");
								PvpTask.pvpKill(player, 2);
								if (player.getRangedTankKills() == 100)
								{
										HighscoresHallOfFame.enterToHallOfFame(player, "Kill 100 Ranged Tanks");
								}
								if (WorldEvent.getActiveEvent("RANGED TANK PK"))
								{
										Artefacts.dropArtefactsAmount(player, other, 3);
								}
						}
						else
						{
								player.deathTypes[RANGED_TANK]++;
								HighscoresRangedTank.getInstance().sortHighscores(player);
						}
				}

				else if (player.getBaseDefenceLevel() <= 20)
				{
						if (isAKill)
						{
								player.setPureKills(player.getPureKills() + 1);
								HighscoresPure.getInstance().sortHighscores(player);
								PlayerTitle.updateLatestTitleForPking(player, "Pure");
								Achievements.checkCompletionMultiple(player, "16 17 18 19 20");
								PvpTask.pvpKill(player, 0);
								if (player.getPureKills() == 100)
								{
										HighscoresHallOfFame.enterToHallOfFame(player, "Kill 100 Pures");
								}
								if (WorldEvent.getActiveEvent("PURE PK"))
								{
										Artefacts.dropArtefactsAmount(player, other, 3);
								}
						}
						else
						{
								player.deathTypes[PURE]++;
								HighscoresPure.getInstance().sortHighscores(player);
						}
				}
				HighscoresPker.getInstance().sortHighscores(player);
		}

		/**
		 * Inform client to show acheivement popup.
		 * @param player
		 * 			The associated player.
		 * @param difficulty
		 * 			The difficulty of the achievement.
		 * @param achievementName
		 * 			The achievement name.
		 */
		public static void showAchievementPopup(final Player player, final String title, final String text1, final String text2)
		{
				player.pendingAchievementPopUps++;
				int delay = 1;
				if (player.pendingAchievementPopUps > 1)
				{
						delay = player.pendingAchievementPopUps * 5;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								informClientAchievementPopup(player, title, text1, text2);
						}
				}, delay);

		}

		private static void informClientAchievementPopup(Player player, String title, String text1, String text2)
		{
				player.getPA().sendMessage(ServerConstants.BLUE_COL + "Achievement completed: " + title + ".");
				player.playerAssistant.sendMessage(":achievement:popup0:" + title);
				player.playerAssistant.sendMessage(":achievement:popup1:" + text1);
				player.playerAssistant.sendMessage(":achievement:popup2:" + text2);
				player.pendingAchievementPopUps--;
		}
}